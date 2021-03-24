package vn.thuephonghoc.service;

import org.springframework.data.domain.Pageable;
import org.springframework.security.core.GrantedAuthority;

import vn.thuephonghoc.dto.RoleDto;
import vn.thuephonghoc.dto.RoleSmallDto;
import vn.thuephonghoc.dto.UserDto;
import vn.thuephonghoc.entity.Role;
import vn.thuephonghoc.query.RoleQueryCriteria;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Set;

public interface RoleService {

    /**
     * @param id /
     * @return /
     */
    RoleDto findById(long id);

    /**
     * @param resources /
     * @return /
     */
    RoleDto create(Role resources);

    /**
     * @param resources /
     */
    void update(Role resources);

    /**
     * @param ids /
     */
    void delete(Set<Long> ids);

    /**
     * @param id 
     * @return /
     */
    List<RoleSmallDto> findByUsersId(Long id);

    /**
     * @param roles /
     * @return /
     */
    Integer findByRoles(Set<Role> roles);

    /**
     * @param resources /
     * @param roleDTO /
     */
    void updateMenu(Role resources, RoleDto roleDTO);

    /**
     * @param id /
     */
    void untiedMenu(Long id);

    /**
     * @param pageable 
     * @return /
     */
    Object queryAll(Pageable pageable);

    /**
     * @param criteria 
     * @param pageable 
     * @return /
     */
    Object queryAll(RoleQueryCriteria criteria, Pageable pageable);

    /**
     * @param criteria 
     * @return /
     */
    List<RoleDto> queryAll(RoleQueryCriteria criteria);

    /**
     * @param queryAll 
     * @param response /
     * @throws IOException /
     */
    void download(List<RoleDto> queryAll, HttpServletResponse response) throws IOException;


    /**
     * @param user
     * @return permission information
     */
    List<GrantedAuthority> mapToGrantedAuthorities(UserDto user);

}

