package vn.thuephonghoc.config;

import org.springframework.stereotype.Component;

import vn.thuephonghoc.dto.RoleSmallDto;
import vn.thuephonghoc.dto.UserDto;
import vn.thuephonghoc.entity.Dept;
import vn.thuephonghoc.service.DeptService;
import vn.thuephonghoc.service.RoleService;
import vn.thuephonghoc.service.UserService;
import vn.thuephonghoc.utils.SecurityUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Data permission configuration
 */
@Component
public class DataScope {
    private final String[] scopeType = {"All", "This level", "Customize"};

    private final UserService userService;

    private final RoleService roleService;

    private final DeptService deptService;

    public DataScope(UserService userService, RoleService roleService, DeptService deptService) {
        this.userService = userService;
        this.roleService = roleService;
        this.deptService = deptService;
    }

    public Set<Long> getDeptIds() {

        UserDto user = userService.findByName(SecurityUtils.getCurrentUsername());

        // Used to store department id
        Set<Long> deptIds = new HashSet<>();

        // Query user role
        List<RoleSmallDto> roleSet = roleService.findByUsersId(user.getId());

        for (RoleSmallDto role: roleSet) {

            if (scopeType[0].equals(role.getDataScope())) {
                return new HashSet<>();
            }

            // Store this level of data permissions
            if (scopeType[1].equals(role.getDataScope())) {
                deptIds.add(user.getDept().getId());
            }

            // Store custom data permissions
            if (scopeType[2].equals(role.getDataScope())) {
                Set<Dept> depts = deptService.findByRoleIds(role.getId());
                for (Dept dept : depts) {
                    deptIds.add(dept.getId());
                    List<Dept> deptChildren = deptService.findByPid(dept.getId());
                    if (deptChildren != null && deptChildren.size() != 0) {
                        deptIds.addAll(getDeptChildren(deptChildren));
                    }
                }
            }
        }
        return deptIds;
    }


    public List<Long> getDeptChildren(List<Dept> deptList) {
        List<Long> list = new ArrayList<>();
        deptList.forEach(dept -> {
                    if (dept!=null && dept.getEnabled()){
                        List<Dept> depts = deptService.findByPid(dept.getId());
                        if(deptList.size() != 0){
                            list.addAll(getDeptChildren(depts));
                        }
                        list.add(dept.getId());
                    }
                }
        );
        return list;
    }
}
