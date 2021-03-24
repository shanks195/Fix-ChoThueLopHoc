package vn.thuephonghoc.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import vn.thuephonghoc.vo.RedisVo;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Set;

public interface RedisService {

    /**
     * @param key 
     * @return /
     */
    Page findByKey(String key, Pageable pageable);

    /**
     * findById
     * @param key 
     * @return /
     */
    List<RedisVo> findByKey(String key);

    /**
     * @param key 
     * @return /
     */
    String getCodeVal(String key);

    /**
     * @param key 
     * @param val 
     */
    void saveCode(String key, Object val);

    /**
     * delete
     * @param ids 
     */
    void delete(Set<String> ids);

    /**
     * @param redisVos /
     * @param response /
     */
    void download(List<RedisVo> redisVos, HttpServletResponse response) throws IOException;
}

