package vn.thuephonghoc.controller;

import cn.hutool.core.lang.Dict;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import vn.thuephonghoc.aop.log.Log;
import vn.thuephonghoc.dto.RoleDto;
import vn.thuephonghoc.dto.RoleSmallDto;
import vn.thuephonghoc.entity.Role;
import vn.thuephonghoc.exception.BadRequestException;
import vn.thuephonghoc.query.RoleQueryCriteria;
import vn.thuephonghoc.service.RoleService;
import vn.thuephonghoc.service.UserService;
import vn.thuephonghoc.utils.SecurityUtils;
import vn.thuephonghoc.utils.ThrowableUtil;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Api(tags = "System: Role Management")
@RestController
@RequestMapping("/api/roles")
public class RoleController {

    private final RoleService roleService;

    private final UserService userService;

    public RoleController(RoleService roleService, UserService userService) {
        this.roleService = roleService;
        this.userService = userService;
    }

    private static final String ENTITY_NAME = "role";

    @Log("Export role data")
    @ApiOperation("Export role data")
    @GetMapping(value = "/download")
    @PreAuthorize("@ddt.check('role:list')")
    public void download(HttpServletResponse response, RoleQueryCriteria criteria) throws IOException {
        roleService.download(roleService.queryAll(criteria), response);
    }


    @ApiOperation("Get a single role")
    @GetMapping(value = "/{id}")
    @PreAuthorize("@ddt.check('roles:list')")
    public ResponseEntity<Object> getRoles(@PathVariable Long id){
        return new ResponseEntity<>(roleService.findById(id), HttpStatus.OK);
    }

    @ApiOperation("Return all roles")
    @GetMapping(value = "/all")
    @PreAuthorize("@ddt.check('roles:list','user:add','user:edit')")
    public ResponseEntity<Object> getAll(@PageableDefault(value = 2000, sort = {"level"}, direction = Sort.Direction.ASC) Pageable pageable){
        return new ResponseEntity<>(roleService.queryAll(pageable),HttpStatus.OK);
    }

    @Log("Query role")
    @ApiOperation("Query role")
    @GetMapping
    @PreAuthorize("@ddt.check('roles:list')")
    public ResponseEntity<Object> getRoles(RoleQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(roleService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @ApiOperation("Get user level")
    @GetMapping(value = "/level")
    public ResponseEntity<Object> getLevel(){
        return new ResponseEntity<>(Dict.create().set("level", getLevels(null)),HttpStatus.OK);
    }

    @Log("New Role")
    @ApiOperation("Add role")
    @PostMapping
    @PreAuthorize("@ddt.check('roles:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody Role resources){
        if (resources.getId() != null) {
            throw new BadRequestException("A new "+ ENTITY_NAME +" cannot already have an ID");
        }
        getLevels(resources.getLevel());
        return new ResponseEntity<>(roleService.create(resources),HttpStatus.CREATED);
    }

    @Log("Modify Role")
    @ApiOperation("Modify Role")
    @PutMapping
    @PreAuthorize("@ddt.check('roles:edit')")
    public ResponseEntity<Object> update(@Validated(Role.Update.class) @RequestBody Role resources) {
        getLevels(resources.getLevel());
        roleService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("Modify Role Menu")
    @ApiOperation("Modify Role Menu")
    @PutMapping(value = "/menu")
    @PreAuthorize("@ddt.check('roles:edit')")
    public ResponseEntity<Object> updateMenu(@RequestBody Role resources){
        RoleDto role = roleService.findById(resources.getId());
        getLevels(role.getLevel());
        roleService.updateMenu(resources,role);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("Delete role")
    @ApiOperation("Delete Role")
    @DeleteMapping
    @PreAuthorize("@ddt.check('roles:del')")
    public ResponseEntity<Object> delete(@RequestBody Set<Long> ids){
        for (Long id: ids) {
            RoleDto role = roleService.findById(id);
            getLevels(role.getLevel());
        }
        try {
            roleService.delete(ids);
        } catch (Throwable e){
            ThrowableUtil.throwForeignKeyException(e, "The selected role has a user association, please cancel the association and try again");
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Get the user's role level
     * @return /
     */
    private int getLevels(Integer level){
        List<Integer> levels = roleService.findByUsersId(SecurityUtils.getCurrentUserId()).stream().map(RoleSmallDto::getLevel).collect(Collectors.toList());
        int min = Collections.min(levels);
        if(level != null){
            if(level <min){
                throw new BadRequestException("Insufficient permissions, your role level: "+ min + ", lower than the role level of the operation:" + level);
            }
        }
        return min;
    }
}

