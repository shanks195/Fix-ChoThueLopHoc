package vn.thuephonghoc.utils;

import cn.hutool.json.JSONObject;
import lombok.extern.slf4j.Slf4j;
import vn.thuephonghoc.exception.BadRequestException;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * Get the currently logged-in user
 */
@Slf4j
public class SecurityUtils {


    /**
     * Get the currently logged-in user
     * @return
     */
    public static UserDetails getCurrentUser() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new BadRequestException(HttpStatus.UNAUTHORIZED, "The current login status is expired");
        }
        if (authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            UserDetailsService userDetailsService = SpringContextHolder.getBean(UserDetailsService.class);
            return userDetailsService.loadUserByUsername(userDetails.getUsername());
        }
        throw new BadRequestException(HttpStatus.UNAUTHORIZED, "Cannot find the current login information");
    }



    /**
     * Get system user name
     * @return system user name
     */
    public static String getCurrentUsername() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new BadRequestException(HttpStatus.UNAUTHORIZED, "The current login status is expired");
        }
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return userDetails.getUsername();
    }

    /**
     * Get system user ID
     *
     * @return system user ID
     */
    public static Long getCurrentUserId() {
        UserDetails userDetails = getCurrentUser();
        return new JSONObject(new JSONObject(userDetails).get("user")).get("id", Long.class);
    }



}
