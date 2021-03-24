package vn.thuephonghoc.service;

import lombok.extern.slf4j.Slf4j;
import vn.thuephonghoc.config.SecurityProperties;
import vn.thuephonghoc.utils.*;
import vn.thuephonghoc.utils.ip.AddressUtils;
import vn.thuephonghoc.utils.ip.IpUtils;
import vn.thuephonghoc.vo.JwtUserDto;
import vn.thuephonghoc.vo.OnlineUserDto;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

@Service
@Slf4j
@SuppressWarnings({"unchecked","all"})
public class OnlineUserService {

    private final SecurityProperties properties;

    private RedisUtils redisUtils;

    public OnlineUserService(SecurityProperties properties,  RedisUtils redisUtils) {
        this.properties = properties;
        this.redisUtils = redisUtils;
    }

    /**
     * 保存在线用户信息
     * @param jwtUserDto
     * @param token
     * @param request
     */
    public void save(JwtUserDto jwtUserDto, String token, HttpServletRequest request){
        String job = jwtUserDto.getUser().getDept().getName() + "/" + jwtUserDto.getUser().getJob().getName();
        String ip = IpUtils.getIpAddr(request);
        String browser = StringUtils.getBrowser(request);
        String address = AddressUtils.getRealAddressByIP(ip);
        OnlineUserDto onlineUserDto = null;
        try {
            onlineUserDto = new OnlineUserDto(jwtUserDto.getUsername(), jwtUserDto.getUser().getNickName(), job, browser , ip, address, EncryptUtils.desEncrypt(token), new Date());
        } catch (Exception e) {
            e.printStackTrace();
        }
        redisUtils.set(properties.getOnlineKey() + token, onlineUserDto, properties.getTokenValidityInSeconds()/1000);

    }

    /**
     * @param filter
     * @param pageable
     * @return
     */
    public Map<String,Object> getAll(String filter, Pageable pageable){
        List<OnlineUserDto> onlineUserDtos = getAll(filter);
        return PageUtil.toPage(
                PageUtil.toPage(pageable.getPageNumber(), pageable.getPageSize(), onlineUserDtos),
                onlineUserDtos.size());
    }

    /**
     * @param filter
     * @return
     */
    public List<OnlineUserDto> getAll(String filter){
        List<String> keys = redisUtils.scan(properties.getOnlineKey() + "*");
        Collections.reverse(keys);
        List<OnlineUserDto> onlineUserDtos = new ArrayList<>();
        for (String key : keys) {
            OnlineUserDto onlineUserDto = (OnlineUserDto) redisUtils.get(key);
            if(StringUtils.isNotBlank(filter)){
                if(onlineUserDto.toString().contains(filter)){
                    onlineUserDtos.add(onlineUserDto);
                }
            } else {
                onlineUserDtos.add(onlineUserDto);
            }
        }
        onlineUserDtos.sort((o1, o2) -> o2.getLoginTime().compareTo(o1.getLoginTime()));
        return onlineUserDtos;
    }

    /**
     * @param val
     * @throws Exception
     */
    public void kickOut(String key) {
        key = properties.getOnlineKey() + key;
        redisUtils.del(key);
    }


    /**
     * @param token
     */
    public void logout(String token) {
        String key = properties.getOnlineKey() + token;
        redisUtils.del(key);
    }

    /**
     * Check whether the user has been logged in before, has logged in and kicked offline
     * @param userName
     */
    public void checkLoginOnUser(String userName, String igoreToken){
        List<OnlineUserDto> onlineUserDtos = getAll(userName);
        if(onlineUserDtos ==null || onlineUserDtos.isEmpty()){
            return;
        }
        for(OnlineUserDto onlineUserDto : onlineUserDtos){
            if(onlineUserDto.getUserName().equals(userName)){
                try {
                    String token =EncryptUtils.desDecrypt(onlineUserDto.getKey());
                    if(StringUtils.isNotBlank(igoreToken)&&!igoreToken.equals(token)){
                        this.kickOut(token);
                    }else if(StringUtils.isBlank(igoreToken)){
                        this.kickOut(token);
                    }
                } catch (Exception e) {
                    log.error("checkUser is error",e);
                }
            }
        }
    }

    /**
     * @param all
     * @param response
     * @throws IOException
     */
    public void download(List<OnlineUserDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (OnlineUserDto user : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("User Name", user.getUserName());
            map.put("Post", user.getJob());
            map.put("Login IP", user.getIp());
            map.put("Login Location", user.getAddress());
            map.put("Browser", user.getBrowser());
            map.put("Login Date", user.getLoginTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }


    /**
     * 查询用户
     * @param key
     * @return
     */
    public OnlineUserDto getOne(String key) {
        return (OnlineUserDto)redisUtils.get(key);
    }
}
