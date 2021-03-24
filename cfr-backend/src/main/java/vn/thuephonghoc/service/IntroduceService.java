package vn.thuephonghoc.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;
import vn.thuephonghoc.dto.IntroduceDto;
import vn.thuephonghoc.entity.Introduce;
import vn.thuephonghoc.query.IntroduceQueryCriteria;

public interface IntroduceService {

	/**
     * Query data paging
     * @param criteria conditions
     * @param pageable paging parameters
     * @return Map<String,Object>
     */
     Map<String,Object> queryAll(IntroduceQueryCriteria criteria, Pageable pageable);

     /**
     * Query all data without paging
     * @param criteria criteria parameter
     * @return List<ContactDto>
     */
     List<IntroduceDto> queryAll(IntroduceQueryCriteria criteria);

     /**
     * Query by ID
     * @param id ID
     * @return IntroduceDto
     */
     IntroduceDto findById(Long id);
     
     /**
     * Create
     * @param resources /
     * @return IntroduceDto
     */
     IntroduceDto create(Introduce resources);
     
     /**
     * Edit
     * @param resources /
     */
     void update(Introduce resources);
     
     /**
     * export data
     * @param all the data to be exported
     * @param response /
     * @throws IOException /
     */
     void download(List<IntroduceDto> all, HttpServletResponse response) throws IOException;

     
     /**
     * Multiple selection delete
     * @param ids /
     */
    void deleteAll(Long[] ids);

}
