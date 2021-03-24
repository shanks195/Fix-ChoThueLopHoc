package vn.thuephonghoc.controller;

import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.RSA;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import vn.thuephonghoc.aop.log.Log;
import vn.thuephonghoc.config.DataScope;
import vn.thuephonghoc.dto.RoleSmallDto;
import vn.thuephonghoc.dto.UserDto;
import vn.thuephonghoc.entity.User;
import vn.thuephonghoc.entity.VerificationCode;
import vn.thuephonghoc.exception.BadRequestException;
import vn.thuephonghoc.query.UserQueryCriteria;
import vn.thuephonghoc.service.*;
import vn.thuephonghoc.utils.CfrConstant;
import vn.thuephonghoc.utils.PageUtil;
import vn.thuephonghoc.utils.SecurityUtils;
import vn.thuephonghoc.vo.UserPassVo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Api(tags = "System: User Management")
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Value("${rsa.private_key}")
    private String privateKey;

    private final PasswordEncoder passwordEncoder;

    private final UserService userService;

    private final DataScope dataScope;

    private final DeptService deptService;

    private final RoleService roleService;

    private final VerificationCodeService verificationCodeService;

    public UserController(PasswordEncoder passwordEncoder, UserService userService, DataScope dataScope, DeptService deptService, RoleService roleService, VerificationCodeService verificationCodeService) {
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
        this.dataScope = dataScope;
        this.deptService = deptService;
        this.roleService = roleService;
        this.verificationCodeService = verificationCodeService;
    }


    @Log("Export user data")
    @ApiOperation("Export user data")
    @GetMapping(value = "/download")
    @PreAuthorize("@ddt.check('user:list')")
    public void download(HttpServletResponse response, UserQueryCriteria criteria) throws IOException {
        userService.download(userService.queryAll(criteria), response);
    }

    @Log("Query User")
    @ApiOperation("Query User")
    @GetMapping
    @PreAuthorize("@ddt.check('user:list')")
    public ResponseEntity<Object> getUsers(UserQueryCriteria criteria, Pageable pageable) {
        Set<Long> deptSet = new HashSet<>();
        Set<Long> result = new HashSet<>();

        if (!ObjectUtils.isEmpty(criteria.getDeptId())) {
            deptSet.add(criteria.getDeptId());
            deptSet.addAll(dataScope.getDeptChildren(deptService.findByPid(criteria.getDeptId())));
        }

        // data permission
        Set<Long> deptIds = dataScope.getDeptIds();

        // If the query condition is not empty and the data permission is not empty, take the intersection
        if (!CollectionUtils.isEmpty(deptIds) && !CollectionUtils.isEmpty(deptSet)) {

            result.addAll(deptSet);
            result.retainAll(deptIds);

            // If there is no intersection, it means there is no data permission
            criteria.setDeptIds(result);
            if (result.size() == 0) {
                return new ResponseEntity<>(PageUtil.toPage(null, 0), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(userService.queryAll(criteria, pageable), HttpStatus.OK);
            }
            // Otherwise take the union
        } else {
            result.addAll(deptSet);
            result.addAll(deptIds);
            criteria.setDeptIds(result);
            return new ResponseEntity<>(userService.queryAll(criteria, pageable), HttpStatus.OK);
        }
    }

    @Log("New User")
    @ApiOperation("Add User")
    @PostMapping
    @PreAuthorize("@ddt.check('user:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody User resources) {
        checkLevel(resources);
        // default pass 123456
        resources.setPassword(passwordEncoder.encode("123456"));
        return new ResponseEntity<>(userService.create(resources), HttpStatus.CREATED);
    }

    @Log("Modify User")
    @ApiOperation("Modify User")
    @PutMapping
    @PreAuthorize("@ddt.check('user:edit')")
    public ResponseEntity<Object> update(@Validated(User.Update.class) @RequestBody User resources) {
        checkLevel(resources);
        userService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("Modify User: Personal Center")
    @ApiOperation("Modify User: Personal Center")
    @PutMapping(value = "center")
    public ResponseEntity<Object> center(@Validated(User.Update.class) @RequestBody User resources) {
        if(!resources.getId().equals(SecurityUtils.getCurrentUserId())){
            throw new BadRequestException("Cannot modify other people's information");
        }
        userService.updateCenter(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("Delete User")
    @ApiOperation("Delete User")
    @DeleteMapping
    @PreAuthorize("@ddt.check('user:del')")
    public ResponseEntity<Object> delete(@RequestBody Set<Long> ids) {
        for (Long id : ids) {
            Integer currentLevel =  Collections.min(roleService.findByUsersId(SecurityUtils.getCurrentUserId()).stream().map(RoleSmallDto::getLevel).collect(Collectors.toList()));
            Integer optLevel = Collections.min(roleService.findByUsersId(id).stream().map(RoleSmallDto::getLevel).collect(Collectors.toList()));
            if (currentLevel > optLevel) {
                throw new BadRequestException("Role permissions are insufficient to delete:" + userService.findById(id).getUsername());
            }
        }
        userService.delete(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation("Modify Password")
    @PostMapping(value = "/updatePass")
    public ResponseEntity<Object> updatePass(@RequestBody UserPassVo passVo) {
        // Password decryption
        RSA rsa = new RSA(privateKey, null);
        String oldPass = new String(rsa.decrypt(passVo.getOldPass(), KeyType.PrivateKey));
        String newPass = new String(rsa.decrypt(passVo.getNewPass(), KeyType.PrivateKey));
        UserDto user = userService.findByName(SecurityUtils.getCurrentUsername());
        if (!passwordEncoder.matches(oldPass, user.getPassword())) {
            throw new BadRequestException("Modification failed, old password is wrong");
        }
        if (passwordEncoder.matches(newPass, user.getPassword())) {
            throw new BadRequestException("The new password cannot be the same as the old password");
        }
        userService.updatePass(user.getUsername(), passwordEncoder.encode(newPass));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation("Modify Avatar")
    @PostMapping(value = "/updateAvatar")
    public ResponseEntity<Object> updateAvatar(@RequestParam MultipartFile file) {
        userService.updateAvatar(file);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Log("Modify Email")
    @ApiOperation("Modify Email")
    @PostMapping(value = "/updateEmail/{code}")
    public ResponseEntity<Object> updateEmail(@PathVariable String code, @RequestBody User user) {
        // Password decryption
        RSA rsa = new RSA(privateKey, null);
        String password = new String(rsa.decrypt(user.getPassword(), KeyType.PrivateKey));
        UserDto userDto = userService.findByName(SecurityUtils.getCurrentUsername());
        if (!passwordEncoder.matches(password, userDto.getPassword())) {
            throw new BadRequestException("Password error");
        }
        VerificationCode verificationCode = new VerificationCode(code, CfrConstant.RESET_MAIL, "email", user.getEmail());
        verificationCodeService.validated(verificationCode);
        userService.updateEmail(userDto.getUsername(), user.getEmail());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * If the role level of the current user is lower than the role level of the created user, an insufficient authority error will be thrown
     *
     * @param resources /
     */
    private void checkLevel(User resources) {
        Integer currentLevel = Collections.min(roleService.findByUsersId(SecurityUtils.getCurrentUserId()).stream().map(RoleSmallDto::getLevel).collect(Collectors.toList()));
        Integer optLevel = roleService.findByRoles(resources.getRoles());
        if (currentLevel> optLevel) {
            throw new BadRequestException("Insufficient role permissions");
        }
    }
}

