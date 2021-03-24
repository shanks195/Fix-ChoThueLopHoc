package vn.thuephonghoc.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.data.domain.Pageable;

import vn.thuephonghoc.dto.ContactDto;
import vn.thuephonghoc.entity.Contact;
import vn.thuephonghoc.query.ContactQueryCriteria;

public interface ContactService {

	/**
     * Query data paging
     * @param criteria conditions
     * @param pageable paging parameters
     * @return Map<String,Object>
     */
     Map<String,Object> queryAll(ContactQueryCriteria criteria, Pageable pageable);

     /**
     * Query all data without paging
     * @param criteria criteria parameter
     * @return List<ContactDto>
     */
     List<ContactDto> queryAll(ContactQueryCriteria criteria);

     /**
     * Query by ID
     * @param id ID
     * @return ContactDto
     */
     ContactDto findById(Long id);
     
     /**
     * Create
     * @param resources /
     * @return ContactDto
     */
     ContactDto create(Contact resources);
     
     /**
     * Edit
     * @param resources /
     */
     void update(Contact resources);
     
     /**
     * export data
     * @param all the data to be exported
     * @param response /
     * @throws IOException /
     */
     void download(List<ContactDto> all, HttpServletResponse response) throws IOException;

     /**
     * Multiple selection delete
     * @param ids /
     */
    void deleteAll(Long[] ids);
}
