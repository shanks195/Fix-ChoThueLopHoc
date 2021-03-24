package vn.thuephonghoc.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import vn.thuephonghoc.config.SecurityProperties;
import vn.thuephonghoc.service.RedisService;
import vn.thuephonghoc.utils.FileUtil;
import vn.thuephonghoc.utils.PageUtil;
import vn.thuephonghoc.utils.StringUtils;
import vn.thuephonghoc.vo.RedisVo;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@SuppressWarnings({"unchecked","all"})
public class RedisServiceImpl implements RedisService {

    private final SecurityProperties properties;

    private final RedisTemplate redisTemplate;

    public RedisServiceImpl(RedisTemplate redisTemplate, SecurityProperties properties) {
        this.redisTemplate = redisTemplate;
        this.properties = properties;
    }

    @Value("${loginCode.expiration}")
    private Long expiration;

    @Override
    public Page<RedisVo> findByKey(String key, Pageable pageable) {
        // Default query all without passing value:*
        if (StringUtils.isEmpty(key)) {
            key = "*";
        }
        List<RedisVo> redisVos = findByKey(key);
        return new PageImpl<RedisVo>(
                PageUtil.toPage(pageable.getPageNumber(), pageable.getPageSize(), redisVos),
                pageable,
                redisVos.size());
    }

    @Override
    public List<RedisVo> findByKey(String key) {
        List<RedisVo> redisVos = new ArrayList<>();
        if(!"*".equals(key)){
            key = "*" + key + "*";
        }
        Set<String> keys = redisTemplate.keys(key);
        for (String s : keys) {
            // Filter out the permission cache
            if (s.contains("role::loadPermissionByUser") || s.contains("user::loadUserByUsername") || s.contains(properties.getOnlineKey()) || s.contains(properties.getCodeKey())) {
                continue;
            }
            RedisVo redisVo = new RedisVo(s, Objects.requireNonNull(redisTemplate.opsForValue().get(s)).toString());
            redisVos.add(redisVo);
        }
        return redisVos;
    }

    @Override
    public void delete(Set<String> keys) {
        for (String key : keys) {
            redisTemplate.delete(key);
        }
    }


    @Override
    public String getCodeVal(String key) {
        try {
            return Objects.requireNonNull(redisTemplate.opsForValue().get(key)).toString();
        }catch (Exception e){
            return "";
        }
    }

    @Override
    public void saveCode(String key, Object val) {
        redisTemplate.opsForValue().set(key,val);
        redisTemplate.expire(key,expiration, TimeUnit.MINUTES);
    }

    @Override
    public void download(List<RedisVo> redisVos, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (RedisVo redisVo : redisVos) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("key", redisVo.getKey());
            map.put("value", redisVo.getValue());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }
}

