package vn.thuephonghoc.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.data.domain.Pageable;

import vn.thuephonghoc.dto.ContactClientDto;
import vn.thuephonghoc.entity.ContactClient;
import vn.thuephonghoc.query.ContactClientQueryCriteria;

public interface ContactClientService {

	/**
     * Query data paging
     * @param criteria conditions
     * @param pageable paging parameters
     * @return Map<String,Object>
     */
     Map<String,Object> queryAll(ContactClientQueryCriteria criteria, Pageable pageable);

     /**
     * Query all data without paging
     * @param criteria criteria parameter
     * @return List<ContactClientDto>
     */
     List<ContactClientDto> queryAll(ContactClientQueryCriteria criteria);

     /**
     * Query by ID
     * @param id ID
     * @return ContactClientDto
     */
     ContactClientDto findById(Long id);
     
     /**
     * Create
     * @param resources /
     * @return ContactClientDto
     */
     ContactClientDto create(ContactClient resources);
     
     /**
     * Edit
     * @param resources /
     */
     void update(ContactClient resources);
     
     /**
     * export data
     * @param all the data to be exported
     * @param response /
     * @throws IOException /
     */
     void download(List<ContactClientDto> all, HttpServletResponse response) throws IOException;

     /**
     * Multiple selection delete
     * @param ids /
     */
    void deleteAll(Long[] ids);
    
}
