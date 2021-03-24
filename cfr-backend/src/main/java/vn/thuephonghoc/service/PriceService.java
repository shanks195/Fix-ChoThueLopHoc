package vn.thuephonghoc.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.data.domain.Pageable;

import vn.thuephonghoc.dto.PriceDto;
import vn.thuephonghoc.entity.Price;
import vn.thuephonghoc.query.PriceQueryCriteria;

public interface PriceService {

	/**
     * Query data paging
     * @param criteria conditions
     * @param pageable paging parameters
     * @return Map<String,Object>
     */
     Map<String,Object> queryAll(PriceQueryCriteria criteria, Pageable pageable);

     /**
     * Query all data without paging
     * @param criteria criteria parameter
     * @return List<PriceDto>
     */
     List<PriceDto> queryAll(PriceQueryCriteria criteria);

     /**
     * Query by ID
     * @param id ID
     * @return PriceDto
     */
     PriceDto findById(Long id);
     
     /**
     * Create
     * @param resources /
     * @return PriceDto
     */
     PriceDto create(Price resources);
     
     /**
     * Edit
     * @param resources /
     */
     void update(Price resources);
     
     /**
     * export data
     * @param all the data to be exported
     * @param response /
     * @throws IOException /
     */
     void download(List<PriceDto> all, HttpServletResponse response) throws IOException;

     /**
     * Multiple selection delete
     * @param ids /
     */
    void deleteAll(Long[] ids);
}
