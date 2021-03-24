package vn.thuephonghoc.service.impl;


import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import vn.thuephonghoc.dto.MenuDto;
import vn.thuephonghoc.dto.RoleSmallDto;
import vn.thuephonghoc.entity.Menu;
import vn.thuephonghoc.exception.BadRequestException;
import vn.thuephonghoc.exception.EntityExistException;
import vn.thuephonghoc.mapper.MenuMapper;
import vn.thuephonghoc.query.MenuQueryCriteria;
import vn.thuephonghoc.repository.MenuRepository;
import vn.thuephonghoc.service.MenuService;
import vn.thuephonghoc.service.RoleService;
import vn.thuephonghoc.utils.FileUtil;
import vn.thuephonghoc.utils.QueryHelp;
import vn.thuephonghoc.utils.StringUtils;
import vn.thuephonghoc.utils.ValidationUtil;
import vn.thuephonghoc.vo.MenuMetaVo;
import vn.thuephonghoc.vo.MenuVo;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@CacheConfig(cacheNames = "menu")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class MenuServiceImpl implements MenuService {

    private final MenuRepository menuRepository;

    private final MenuMapper menuMapper;

    private final RoleService roleService;

    public MenuServiceImpl(MenuRepository menuRepository, MenuMapper menuMapper, RoleService roleService) {
        this.menuRepository = menuRepository;
        this.menuMapper = menuMapper;
        this.roleService = roleService;
    }

    @Override
    @Cacheable
    public List<MenuDto> queryAll(MenuQueryCriteria criteria){
//        Sort sort = new Sort(Sort.Direction.DESC,"id");
        return menuMapper.toDto(menuRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
    }

    @Override
    @Cacheable(key = "#p0")
    public MenuDto findById(long id) {
        Menu menu = menuRepository.findById(id).orElseGet(Menu::new);
        ValidationUtil.isNull(menu.getId(),"Menu","id",id);
        return menuMapper.toDto(menu);
    }

    @Override
    public List<MenuDto> findByRoles(List<RoleSmallDto> roles) {
        Set<Long> roleIds = roles.stream().map(RoleSmallDto::getId).collect(Collectors.toSet());
        LinkedHashSet<Menu> menus = menuRepository.findByRoles_IdInAndTypeNotOrderBySortAsc(roleIds, 2);
        return menus.stream().map(menuMapper::toDto).collect(Collectors.toList());
    }

    @Override
    @CacheEvict(allEntries = true)
    public MenuDto create(Menu resources) {
        if(menuRepository.findByName(resources.getName()) != null){
            throw new EntityExistException(Menu.class,"name",resources.getName());
        }
        if(StringUtils.isNotBlank(resources.getComponentName())){
            if(menuRepository.findByComponentName(resources.getComponentName()) != null){
                throw new EntityExistException(Menu.class,"componentName",resources.getComponentName());
            }
        }
        if(resources.getIFrame()){
            if (!(resources.getPath().toLowerCase().startsWith("http://")||resources.getPath().toLowerCase().startsWith("https://"))) {
                throw new BadRequestException("The external link must start with http:// or https://");
            }
        }
        return menuMapper.toDto(menuRepository.save(resources));
    }

    @Override
    @CacheEvict(allEntries = true)
    public void update(Menu resources) {
        if(resources.getId().equals(resources.getPid())) {
            throw new BadRequestException("Can't update, Please contact admin");
        }
        Menu menu = menuRepository.findById(resources.getId()).orElseGet(Menu::new);
        ValidationUtil.isNull(menu.getId(),"Permission","id",resources.getId());

        if(resources.getIFrame()){
            if (!(resources.getPath().toLowerCase().startsWith("http://")||resources.getPath().toLowerCase().startsWith("https://"))) {
                throw new BadRequestException("The external link must start with http:// or https://");
            }
        }
        Menu menu1 = menuRepository.findByName(resources.getName());

        if(menu1 != null && !menu1.getId().equals(menu.getId())){
            throw new EntityExistException(Menu.class,"name",resources.getName());
        }

        if(StringUtils.isNotBlank(resources.getComponentName())){
            menu1 = menuRepository.findByComponentName(resources.getComponentName());
            if(menu1 != null && !menu1.getId().equals(menu.getId())){
                throw new EntityExistException(Menu.class,"componentName",resources.getComponentName());
            }
        }
        menu.setName(resources.getName());
        menu.setComponent(resources.getComponent());
        menu.setPath(resources.getPath());
        menu.setIcon(resources.getIcon());
        menu.setIFrame(resources.getIFrame());
        menu.setPid(resources.getPid());
        menu.setSort(resources.getSort());
        menu.setCache(resources.getCache());
        menu.setHidden(resources.getHidden());
        menu.setComponentName(resources.getComponentName());
        menu.setPermission(resources.getPermission());
        menu.setType(resources.getType());
        menuRepository.save(menu);
    }

    @Override
    @CacheEvict(allEntries = true)
    public Set<Menu> getDeleteMenus(List<Menu> menuList, Set<Menu> menuSet) {
        for (Menu menu1 : menuList) {
            menuSet.add(menu1);
            List<Menu> menus = menuRepository.findByPid(menu1.getId());
            if(menus!=null && menus.size()!=0){
                getDeleteMenus(menus, menuSet);
            }
        }
        return menuSet;
    }

    @Override
    @CacheEvict(allEntries = true)
    @Transactional(rollbackFor = Exception.class)
    public void delete(Set<Menu> menuSet) {
        for (Menu menu : menuSet) {
            roleService.untiedMenu(menu.getId());
            menuRepository.deleteById(menu.getId());
        }
    }

    @Override
    @Cacheable(key = "'tree'")
    public Object getMenuTree(List<Menu> menus) {
        List<Map<String,Object>> list = new LinkedList<>();
        menus.forEach(menu -> {
                    if (menu!=null){
                        List<Menu> menuList = menuRepository.findByPid(menu.getId());
                        Map<String,Object> map = new HashMap<>();
                        map.put("id",menu.getId());
                        map.put("label",menu.getName());
                        if(menuList!=null && menuList.size()!=0){
                            map.put("children",getMenuTree(menuList));
                        }
                        list.add(map);
                    }
                }
        );
        return list;
    }

    @Override
    @Cacheable(key = "'pid:'+#p0")
    public List<Menu> findByPid(long pid) {
        return menuRepository.findByPid(pid);
    }

    @Override
    public Map<String,Object> buildTree(List<MenuDto> menuDtos) {
        List<MenuDto> trees = new ArrayList<>();
        Set<Long> ids = new HashSet<>();
        for (MenuDto menuDTO : menuDtos) {
            if (menuDTO.getPid() == 0) {
                trees.add(menuDTO);
            }
            for (MenuDto it : menuDtos) {
                if (it.getPid().equals(menuDTO.getId())) {
                    if (menuDTO.getChildren() == null) {
                        menuDTO.setChildren(new ArrayList<>());
                    }
                    menuDTO.getChildren().add(it);
                    ids.add(it.getId());
                }
            }
        }
        Map<String,Object> map = new HashMap<>();
        if(trees.size() == 0){
            trees = menuDtos.stream().filter(s -> !ids.contains(s.getId())).collect(Collectors.toList());
        }
        map.put("content",trees);
        map.put("totalElements", menuDtos.size());
        return map;
    }

    @Override
    public List<MenuVo> buildMenus(List<MenuDto> menuDtos) {
        List<MenuVo> list = new LinkedList<>();
        menuDtos.forEach(menuDTO -> {
                    if (menuDTO!=null){
                        List<MenuDto> menuDtoList = menuDTO.getChildren();
                        MenuVo menuVo = new MenuVo();
                        menuVo.setName(ObjectUtil.isNotEmpty(menuDTO.getComponentName())  ? menuDTO.getComponentName() : menuDTO.getName());
                        // The first level directory needs to add a slash, otherwise it will report a warning
                        menuVo.setPath(menuDTO.getPid() == 0 ? "/" + menuDTO.getPath() :menuDTO.getPath());
                        menuVo.setHidden(menuDTO.getHidden());
                        if(!menuDTO.getIFrame()){
                            if(menuDTO.getPid() == 0){
                                menuVo.setComponent(StrUtil.isEmpty(menuDTO.getComponent())?"Layout":menuDTO.getComponent());
                            }else if(!StrUtil.isEmpty(menuDTO.getComponent())){
                                menuVo.setComponent(menuDTO.getComponent());
                            }
                        }
                        menuVo.setMeta(new MenuMetaVo(menuDTO.getName(),menuDTO.getIcon(),!menuDTO.getCache()));
                        if(menuDtoList !=null && menuDtoList.size()!=0){
                            menuVo.setAlwaysShow(true);
                            menuVo.setRedirect("noredirect");
                            menuVo.setChildren(buildMenus(menuDtoList));
                            // Deal with the situation where the first-level menu and no sub-menu
                        } else if(menuDTO.getPid() == 0){
                            MenuVo menuVo1 = new MenuVo();
                            menuVo1.setMeta(menuVo.getMeta());
                            if(!menuDTO.getIFrame()){
                                menuVo1.setPath("index");
                                menuVo1.setName(menuVo.getName());
                                menuVo1.setComponent(menuVo.getComponent());
                            } else {
                                menuVo1.setPath(menuDTO.getPath());
                            }
                            menuVo.setName(null);
                            menuVo.setMeta(null);
                            menuVo.setComponent("Layout");
                            List<MenuVo> list1 = new ArrayList<>();
                            list1.add(menuVo1);
                            menuVo.setChildren(list1);
                        }
                        list.add(menuVo);
                    }
                }
        );
        return list;
    }

    @Override
    public Menu findOne(Long id) {
        Menu menu = menuRepository.findById(id).orElseGet(Menu::new);
        ValidationUtil.isNull(menu.getId(),"Menu","id",id);
        return menu;
    }

    @Override
    public void download(List<MenuDto> menuDtos, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (MenuDto menuDTO : menuDtos) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("Menu name", menuDTO.getName());
            map.put("Menu Type", menuDTO.getType() == 0? "Catalogue": menuDTO.getType() == 1? "Menu": "Button");
            map.put("Permission Identification", menuDTO.getPermission());
            map.put("External link menu", menuDTO.getIFrame()? "Yes": "No");
            map.put("The menu is visible", menuDTO.getHidden()? "No": "Yes");
            map.put("Do you want to cache", menuDTO.getCache()? "Yes": "No");
            map.put("Create Date", menuDTO.getCreateTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }
}

