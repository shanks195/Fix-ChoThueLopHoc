package vn.thuephonghoc.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import vn.thuephonghoc.dto.CaptchaDto;
import vn.thuephonghoc.entity.Captcha;
import vn.thuephonghoc.mapper.CaptchaMapper;
import vn.thuephonghoc.query.CaptchaQueryCriteria;
import vn.thuephonghoc.repository.CaptchaRepository;
import vn.thuephonghoc.service.CaptchaService;
import vn.thuephonghoc.utils.FileUtil;
import vn.thuephonghoc.utils.PageUtil;
import vn.thuephonghoc.utils.QueryHelp;
import vn.thuephonghoc.utils.ValidationUtil;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.LinkedHashMap;

@Service
@CacheConfig(cacheNames = "captcha")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class CaptchaServiceImpl implements CaptchaService {

    private final CaptchaRepository captchaRepository;

    private final CaptchaMapper captchaMapper;

    public CaptchaServiceImpl(CaptchaRepository captchaRepository, CaptchaMapper captchaMapper) {
        this.captchaRepository = captchaRepository;
        this.captchaMapper = captchaMapper;
    }

    @Override
    @Cacheable
    public Map<String,Object> queryAll(CaptchaQueryCriteria criteria, Pageable pageable){
        Page<Captcha> page = captchaRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        return PageUtil.toPage(page.map(captchaMapper::toDto));
    }

    @Override
    @Cacheable
    public List<CaptchaDto> queryAll(CaptchaQueryCriteria criteria){
        return captchaMapper.toDto(captchaRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
    }

    @Override
    @Cacheable(key = "#p0")
    public CaptchaDto findById(Long id) {
        Captcha captcha = captchaRepository.findById(id).orElseGet(Captcha::new);
        ValidationUtil.isNull(captcha.getId(),"Captcha","id",id);
        return captchaMapper.toDto(captcha);
    }

    @Override
    @CacheEvict(allEntries = true)
    @Transactional(rollbackFor = Exception.class)
    public CaptchaDto create(Captcha resources) {
        Snowflake snowflake = IdUtil.createSnowflake(1, 1);
        resources.setId(snowflake.nextId());
        return captchaMapper.toDto(captchaRepository.save(resources));
    }

    @Override
    @CacheEvict(allEntries = true)
    @Transactional(rollbackFor = Exception.class)
    public void update(Captcha resources) {
        Captcha captcha = captchaRepository.findById(resources.getId()).orElseGet(Captcha::new);
        ValidationUtil.isNull( captcha.getId(),"Captcha","id",resources.getId());
        captcha.copy(resources);
        captchaRepository.save(captcha);
    }

    @Override
    @CacheEvict(allEntries = true)
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        captchaRepository.deleteById(id);
    }

    @Override
    @CacheEvict(allEntries = true)
    public void deleteAll(Long[] ids) {
        for (Long id : ids) {
            captchaRepository.deleteById(id);
        }
    }

    @Override
    public void download(List<CaptchaDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (CaptchaDto captcha : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("Verification Code Type", captcha.getType());
            map.put("Font name", captcha.getFontName());
            map.put("Font Style", captcha.getFontStyle());
            map.put("Font size", captcha.getFontSize());
            map.put("Width", captcha.getWidth());
            map.put("Height", captcha.getHeight());
            map.put("Number of digits", captcha.getLen());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

 }
