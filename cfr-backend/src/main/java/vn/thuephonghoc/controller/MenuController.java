package vn.thuephonghoc.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import vn.thuephonghoc.aop.log.Log;
import vn.thuephonghoc.dto.MenuDto;
import vn.thuephonghoc.dto.UserDto;
import vn.thuephonghoc.entity.Menu;
import vn.thuephonghoc.exception.BadRequestException;
import vn.thuephonghoc.query.MenuQueryCriteria;
import vn.thuephonghoc.service.MenuService;
import vn.thuephonghoc.service.RoleService;
import vn.thuephonghoc.service.UserService;
import vn.thuephonghoc.utils.SecurityUtils;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@Api(tags = "System: Menu Management")
@RequestMapping("/api/menus")
public class MenuController {

    private final MenuService menuService;

    private final UserService userService;

    private final RoleService roleService;

    public MenuController(MenuService menuService, UserService userService, RoleService roleService) {
        this.menuService = menuService;
        this.userService = userService;
        this.roleService = roleService;
    }

    private static final String ENTITY_NAME = "menu";

    @Log("Export menu data")
    @ApiOperation("Export menu data")
    @GetMapping(value = "/download")
    @PreAuthorize("@ddt.check('menu:list')")
    public void download(HttpServletResponse response, MenuQueryCriteria criteria) throws IOException {
        menuService.download(menuService.queryAll(criteria), response);
    }

    @ApiOperation("Get the front-end required menu")
    @GetMapping(value = "/build")
    public ResponseEntity<Object> buildMenus(){
        List<MenuDto> menuDtoList = menuService.findByRoles(roleService.findByUsersId(SecurityUtils.getCurrentUserId()));
        List<MenuDto> menuDtos = (List<MenuDto>) menuService.buildTree(menuDtoList).get("content");
        return new ResponseEntity<>(menuService.buildMenus(menuDtos),HttpStatus.OK);
    }

    @ApiOperation("Return to all menu")
    @GetMapping(value = "/tree")
    @PreAuthorize("@ddt.check('menu:list','roles:list')")
    public ResponseEntity<Object> getMenuTree(){
        return new ResponseEntity<>(menuService.getMenuTree(menuService.findByPid(0L)),HttpStatus.OK);
    }

    @Log("Query Menu")
    @ApiOperation("Query Menu")
    @GetMapping
    @PreAuthorize("@ddt.check('menu:list')")
    public ResponseEntity<Object> getMenus(MenuQueryCriteria criteria){
        List<MenuDto> menuDtoList = menuService.queryAll(criteria);
        return new ResponseEntity<>(menuService.buildTree(menuDtoList),HttpStatus.OK);
    }

    @Log("New menu")
    @ApiOperation("New menu")
    @PostMapping
    @PreAuthorize("@ddt.check('menu:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody Menu resources){
        if (resources.getId() != null) {
            throw new BadRequestException("A new "+ ENTITY_NAME +" cannot already have an ID");
        }
        return new ResponseEntity<>(menuService.create(resources),HttpStatus.CREATED);
    }

    @Log("Modify Menu")
    @ApiOperation("Modify Menu")
    @PutMapping
    @PreAuthorize("@ddt.check('menu:edit')")
    public ResponseEntity<Object> update(@Validated(Menu.Update.class) @RequestBody Menu resources){
        menuService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("Delete Menu")
    @ApiOperation("Delete Menu")
    @DeleteMapping
    @PreAuthorize("@ddt.check('menu:del')")
    public ResponseEntity<Object> delete(@RequestBody Set<Long> ids){
        Set<Menu> menuSet = new HashSet<>();
        for (Long id: ids) {
            List<Menu> menuList = menuService.findByPid(id);
            menuSet.add(menuService.findOne(id));
            menuSet = menuService.getDeleteMenus(menuList, menuSet);
        }
        menuService.delete(menuSet);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}

