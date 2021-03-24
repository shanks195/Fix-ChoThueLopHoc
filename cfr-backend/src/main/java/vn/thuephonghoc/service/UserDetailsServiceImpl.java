package vn.thuephonghoc.service;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import vn.thuephonghoc.dto.UserDto;
import vn.thuephonghoc.exception.BadRequestException;
import vn.thuephonghoc.vo.JwtUserDto;


@Service("userDetailsService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserService userService;

    private final RoleService roleService;

    public UserDetailsServiceImpl(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }
    @Override
    public JwtUserDto loadUserByUsername(String username){

        UserDto user = userService.findByName(username);
        if (user == null) {
        	throw new BadRequestException("Account does not exist");
        } else {
            if (!user.getEnabled()) {
                throw new BadRequestException("Account is not activated");
            }
            return new JwtUserDto(
                    user,
                    roleService.mapToGrantedAuthorities(user)
            );
        }
    }
}

