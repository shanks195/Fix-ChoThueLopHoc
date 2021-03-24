package vn.thuephonghoc.service;

import javax.servlet.http.HttpServletResponse;

import vn.thuephonghoc.dto.MenuDto;
import vn.thuephonghoc.dto.RoleSmallDto;
import vn.thuephonghoc.entity.Menu;
import vn.thuephonghoc.query.MenuQueryCriteria;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface MenuService {

    /**
     * @param criteria 
     * @return /
     */
    List<MenuDto> queryAll(MenuQueryCriteria criteria);

    /**
     * @param id /
     * @return /
     */
    MenuDto findById(long id);

    /**
     * @param resources /
     * @return /
     */
    MenuDto create(Menu resources);

    /**
     * @param resources /
     */
    void update(Menu resources);

    /**
     * @param menuList /
     * @param menuSet /
     * @return /
     */
    Set<Menu> getDeleteMenus(List<Menu> menuList, Set<Menu> menuSet);

    /**
     * @param menus /
     * @return /
     */
    Object getMenuTree(List<Menu> menus);

    /**
     * @param pid /
     * @return /
     */
    List<Menu> findByPid(long pid);

    /**
     * @param menuDtos
     * @return /
     */
    Map<String,Object> buildTree(List<MenuDto> menuDtos);

    /**
     * @param roles /
     * @return /
     */
    List<MenuDto> findByRoles(List<RoleSmallDto> roles);

    /**
     * @param menuDtos /
     * @return /
     */
    Object buildMenus(List<MenuDto> menuDtos);

    /**
     * @param id /
     * @return /
     */
    Menu findOne(Long id);

    /**
     * @param menuSet /
     */
    void delete(Set<Menu> menuSet);

    /**
     * @param queryAll 
     * @param response /
     * @throws IOException /
     */
    void download(List<MenuDto> queryAll, HttpServletResponse response) throws IOException;
}

