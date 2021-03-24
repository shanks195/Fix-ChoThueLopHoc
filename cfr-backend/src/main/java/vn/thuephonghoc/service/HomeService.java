package vn.thuephonghoc.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.data.domain.Pageable;

import vn.thuephonghoc.dto.HomeDto;
import vn.thuephonghoc.entity.Home;
import vn.thuephonghoc.query.HomeQueryCriteria;

public interface HomeService {

	/**
     * Query data paging
     * @param criteria conditions
     * @param pageable paging parameters
     * @return Map<String,Object>
     */
     Map<String,Object> queryAll(HomeQueryCriteria criteria, Pageable pageable);

     /**
     * Query all data without paging
     * @param criteria criteria parameter
     * @return List<HomeDto>
     */
     List<HomeDto> queryAll(HomeQueryCriteria criteria);

     /**
     * Query by ID
     * @param id ID
     * @return HomeDto
     */
     HomeDto findById(Long id);
     
     /**
     * Create
     * @param resources /
     * @return HomeDto
     */
     HomeDto create(Home resources);
     
     /**
     * Edit
     * @param resources /
     */
     void update(Home resources);
     
     /**
     * export data
     * @param all the data to be exported
     * @param response /
     * @throws IOException /
     */
     void download(List<HomeDto> all, HttpServletResponse response) throws IOException;

     /**
     * Multiple selection delete
     * @param ids /
     */
    void deleteAll(Long[] ids);
}
