package vn.thuephonghoc.controller;

import cn.hutool.core.util.IdUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.RSA;

import com.wf.captcha.*;
import com.wf.captcha.base.Captcha;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import vn.thuephonghoc.annotation.AnonymousAccess;
import vn.thuephonghoc.aop.log.Log;
import vn.thuephonghoc.config.SecurityProperties;
import vn.thuephonghoc.dto.CaptchaDto;
import vn.thuephonghoc.exception.BadRequestException;
import vn.thuephonghoc.security.TokenProvider;
import vn.thuephonghoc.service.CaptchaService;
import vn.thuephonghoc.service.OnlineUserService;
import vn.thuephonghoc.utils.*;
import vn.thuephonghoc.vo.AuthUserDto;
import vn.thuephonghoc.vo.JwtUserDto;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.awt.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Authorize, get user details based on token
 */
@Slf4j
@RestController
@RequestMapping("/auth")
@Api(tags = "System: System Authorization Interface")
public class AuthorizationController {

    @Value("${loginCode.expiration}")
    private Long expiration;

    @Value("${rsa.private_key}")
    private String privateKey;

    @Value("${single.login:false}")
    private Boolean singleLogin;

    private final CaptchaService captchaService;

    private final SecurityProperties properties;

    private final RedisUtils redisUtils;

    private final UserDetailsService userDetailsService;

    private final OnlineUserService onlineUserService;

    private final TokenProvider tokenProvider;

    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    public AuthorizationController(SecurityProperties properties, RedisUtils redisUtils, UserDetailsService userDetailsService, OnlineUserService onlineUserService, TokenProvider tokenProvider, AuthenticationManagerBuilder authenticationManagerBuilder, CaptchaService captchaService) {
        this.properties = properties;
        this.redisUtils = redisUtils;
        this.userDetailsService = userDetailsService;
        this.onlineUserService = onlineUserService;
        this.tokenProvider = tokenProvider;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.captchaService = captchaService;
    }

    /**
     * Login authorization
     * @param authUserDto
     * @return
     */
    @Log("User Login")
    @ApiOperation("Login authorization")
    @AnonymousAccess
    @PostMapping(value = "/login")
    public ResponseEntity<Object> login(@Validated @RequestBody AuthUserDto authUserDto, HttpServletRequest request){
        // Password decryption
        RSA rsa = new RSA(privateKey, null);
        String password = new String(rsa.decrypt(authUserDto.getPassword(), KeyType.PrivateKey));
        // Query verification code
        String code = (String) redisUtils.get(authUserDto.getUuid());
        // Clear the verification code
        redisUtils.del(authUserDto.getUuid());
        if (StringUtils.isBlank(code)) {
            throw new BadRequestException("The verification code does not exist or has expired");
        }
        if (StringUtils.isBlank(authUserDto.getCode()) || !authUserDto.getCode().equalsIgnoreCase(code)) {
            throw new BadRequestException("Verification code error");
        }
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(authUserDto.getUsername(), password);

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        // Generate token
        String token = tokenProvider.createToken(authentication);
        final JwtUserDto jwtUserDto = (JwtUserDto) authentication.getPrincipal();

        // Save online information
        onlineUserService.save(jwtUserDto, token, request);

        if(singleLogin){
            //Kick the token that has been logged in before
            onlineUserService.checkLoginOnUser(authUserDto.getUsername(),token);
        }
        // return token and user information
        Map<String,Object> authInfo = new HashMap<String,Object>(2){{
            put("token", properties.getTokenStartWith() + token);
            put("user", jwtUserDto);
        }};
        return ResponseEntity.ok(authInfo);
    }

    @ApiOperation("Get user information")
    @GetMapping(value = "/info")
    public ResponseEntity<Object> getUserInfo(){
        return ResponseEntity.ok(SecurityUtils.getCurrentUser());
    }

    @ApiOperation("Get verification code")
    @AnonymousAccess
    @GetMapping(value = "/code")
    public ResponseEntity<Object> getCode() throws IOException, FontFormatException {
        // Type https://gitee.com/whvse/EasyCaptcha
    	// Obtain the verification code type according to the database configuration
        CaptchaDto captcha = captchaService.findById(1L);
        SpecCaptcha specCaptcha = new SpecCaptcha(captcha.getWidth(), captcha.getHeight(), captcha.getLen());
        specCaptcha.setFont(new Font(captcha.getFontName(), captcha.getFontStyle(), captcha.getFontSize()));
        specCaptcha.setCharType(captcha.getType());
        String result = specCaptcha.text();
        String uuid = properties.getCodeKey() + IdUtil.simpleUUID();
        // save
        redisUtils.set(uuid, result, expiration, TimeUnit.MINUTES);
        // Verification code information
        Map<String,Object> imgResult = new HashMap<String,Object>(2){{
            put("img", ((Captcha) specCaptcha).toBase64());
            put("uuid", uuid);
        }};
        return ResponseEntity.ok(imgResult);
    }

    @ApiOperation("Logout")
    @AnonymousAccess
    @DeleteMapping(value = "/logout")
    public ResponseEntity<Object> logout(HttpServletRequest request){
        onlineUserService.logout(tokenProvider.getToken(request));
        return new ResponseEntity<>(HttpStatus.OK);
    }
}

