package vn.thuephonghoc.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import vn.thuephonghoc.dto.TestDto;
import vn.thuephonghoc.entity.Test;
import vn.thuephonghoc.mapper.TestMapper;
import vn.thuephonghoc.query.TestQueryCriteria;
import vn.thuephonghoc.repository.TestRepository;
import vn.thuephonghoc.service.TestService;
import vn.thuephonghoc.utils.FileUtil;
import vn.thuephonghoc.utils.PageUtil;
import vn.thuephonghoc.utils.QueryHelp;
import vn.thuephonghoc.utils.ValidationUtil;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.LinkedHashMap;

@Service
//@CacheConfig(cacheNames = "test")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class TestServiceImpl implements TestService {

    private final TestRepository testRepository;

    private final TestMapper testMapper;

    public TestServiceImpl(TestRepository testRepository, TestMapper testMapper) {
        this.testRepository = testRepository;
        this.testMapper = testMapper;
    }

    @Override
    //@Cacheable
    public Map<String,Object> queryAll(TestQueryCriteria criteria, Pageable pageable){
        Page<Test> page = testRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        return PageUtil.toPage(page.map(testMapper::toDto));
    }

    @Override
    //@Cacheable
    public List<TestDto> queryAll(TestQueryCriteria criteria){
        return testMapper.toDto(testRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
    }

    @Override
    //@Cacheable(key = "#p0")
    public TestDto findById(Integer id) {
        Test test = testRepository.findById(id).orElseGet(Test::new);
        ValidationUtil.isNull(test.getId(),"Test","id",id);
        return testMapper.toDto(test);
    }

    @Override
    //@CacheEvict(allEntries = true)
    @Transactional(rollbackFor = Exception.class)
    public TestDto create(Test resources) {
        return testMapper.toDto(testRepository.save(resources));
    }

    @Override
    //@CacheEvict(allEntries = true)
    @Transactional(rollbackFor = Exception.class)
    public void update(Test resources) {
        Test test = testRepository.findById(resources.getId()).orElseGet(Test::new);
        ValidationUtil.isNull( test.getId(),"Test","id",resources.getId());
        test.copy(resources);
        testRepository.save(test);
    }

    @Override
    //@CacheEvict(allEntries = true)
    public void deleteAll(Integer[] ids) {
        for (Integer id : ids) {
            testRepository.deleteById(id);
        }
    }

    @Override
    public void download(List<TestDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (TestDto test : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("Email", test.getEmail());
            map.put("Username", test.getUsername());
            map.put("Create Time", test.getCreateTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

 }
