package vn.thuephonghoc.service;

import org.springframework.data.domain.Pageable;

import vn.thuephonghoc.dto.AppDto;
import vn.thuephonghoc.entity.App;
import vn.thuephonghoc.query.AppQueryCriteria;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Set;


public interface AppService {

    /**
     * @param criteria 
     * @param pageable
     * @return /
     */
    Object queryAll(AppQueryCriteria criteria, Pageable pageable);

    /**
     * @param criteria
     * @return /
     */
    List<AppDto> queryAll(AppQueryCriteria criteria);

    /**
     * @param id /
     * @return /
     */
    AppDto findById(Long id);

    /**
     * @param resources /
     * @return /
     */
    AppDto create(App resources);

    /**
     * @param resources /
     */
    void update(App resources);

    /**
     * @param ids /
     */
    void delete(Set<Long> ids);

    /**
     * @param queryAll /
     * @param response /
     */
    void download(List<AppDto> queryAll, HttpServletResponse response) throws IOException;
}
