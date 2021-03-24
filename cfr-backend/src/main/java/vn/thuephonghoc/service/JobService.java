package vn.thuephonghoc.service;

import org.springframework.data.domain.Pageable;

import vn.thuephonghoc.dto.JobDTO;
import vn.thuephonghoc.entity.Job;
import vn.thuephonghoc.query.JobQueryCriteria;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface JobService {

    /**
     * @param id /
     * @return /
     */
    JobDTO findById(Long id);

    /**
     * @param resources /
     * @return /
     */
    JobDTO create(Job resources);

    /**
     * @param resources /
     */
    void update(Job resources);

    /**
     * @param ids /
     */
    void delete(Set<Long> ids);

    /**
     * @param criteria 
     * @param pageable 
     * @return /
     */
    Map<String,Object> queryAll(JobQueryCriteria criteria, Pageable pageable);

    /**
     * @param criteria /
     * @return /
     */
    List<JobDTO> queryAll(JobQueryCriteria criteria);

    /**
     * @param queryAll
     * @param response /
     * @throws IOException /
     */
    void download(List<JobDTO> queryAll, HttpServletResponse response) throws IOException;
}
