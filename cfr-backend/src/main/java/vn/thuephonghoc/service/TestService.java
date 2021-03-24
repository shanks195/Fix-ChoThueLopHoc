package vn.thuephonghoc.service;

import org.springframework.data.domain.Pageable;

import vn.thuephonghoc.dto.TestDto;
import vn.thuephonghoc.entity.Test;
import vn.thuephonghoc.query.TestQueryCriteria;

import java.util.Map;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


public interface TestService {

	/**
     * Query data paging
     * @param criteria conditions
     * @param pageable paging parameters
     * @return Map<String,Object>
     */
     Map<String,Object> queryAll(TestQueryCriteria criteria, Pageable pageable);

     /**
     * Query all data without paging
     * @param criteria criteria parameter
     * @return List<TestDto>
     */
     List<TestDto> queryAll(TestQueryCriteria criteria);

     /**
     * Query by ID
     * @param id ID
     * @return TestDto
     */
     TestDto findById(Integer id);

     /**
     * Create
     * @param resources /
     * @return TestDto
     */
     TestDto create(Test resources);

     /**
     * Edit
     * @param resources /
     */
     void update(Test resources);

     /**
     * export data
     * @param all the data to be exported
     * @param response /
     * @throws IOException /
     */
     void download(List<TestDto> all, HttpServletResponse response) throws IOException;

     /**
     * Multiple selection delete
     * @param ids /
     */
    void deleteAll(Integer[] ids);
}
