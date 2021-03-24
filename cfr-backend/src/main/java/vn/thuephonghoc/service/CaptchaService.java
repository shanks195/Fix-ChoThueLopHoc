package vn.thuephonghoc.service;

import org.springframework.data.domain.Pageable;

import vn.thuephonghoc.dto.CaptchaDto;
import vn.thuephonghoc.entity.Captcha;
import vn.thuephonghoc.query.CaptchaQueryCriteria;

import java.util.Map;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


public interface CaptchaService {

    /**
    * @param criteria 
    * @param pageable
    * @return Map<String,Object>
    */
    Map<String,Object> queryAll(CaptchaQueryCriteria criteria, Pageable pageable);

    /**
    * @param criteria
    * @return List<CaptchaDto>
    */
    List<CaptchaDto> queryAll(CaptchaQueryCriteria criteria);

    /**
    * @param id ID
    * @return CaptchaDto
    */
    CaptchaDto findById(Long id);

    /**
    * @param resources /
    * @return CaptchaDto
    */
    CaptchaDto create(Captcha resources);

    /**
    * @param resources /
    */
    void update(Captcha resources);

    /**
    * @param id /
    */
    void delete(Long id);

    /**
    * @param all
    * @param response /
    * @throws IOException /
    */
    void download(List<CaptchaDto> all, HttpServletResponse response) throws IOException;

    /**
    * @param ids /
    */
    void deleteAll(Long[] ids);
}
