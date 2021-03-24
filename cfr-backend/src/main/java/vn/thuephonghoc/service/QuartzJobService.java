package vn.thuephonghoc.service;

import org.springframework.data.domain.Pageable;

import vn.thuephonghoc.entity.QuartzJob;
import vn.thuephonghoc.entity.QuartzLog;
import vn.thuephonghoc.query.JobQueryCriteria;
import vn.thuephonghoc.query.QuartzJobQueryCriteria;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Set;

public interface QuartzJobService {

    /**
     * @param criteria 
     * @param pageable 
     * @return /
     */
    Object queryAll(QuartzJobQueryCriteria criteria, Pageable pageable);

    /**
     * @param criteria 
     * @return /
     */
    List<QuartzJob> queryAll(JobQueryCriteria criteria);

    /**
     *
     * @param criteria 
     * @param pageable 
     * @return /
     */
    Object queryAllLog(QuartzJobQueryCriteria criteria, Pageable pageable);

    /**
     * @param criteria 
     * @return /
     */
    List<QuartzLog> queryAllLog(JobQueryCriteria criteria);

    /**
     * @param resources /
     * @return /
     */
    QuartzJob create(QuartzJob resources);

    /**
     * @param resources /
     */
    void update(QuartzJob resources);

    /**
     * @param ids /
     */
    void delete(Set<Long> ids);

    /**
     * @param id ID
     * @return /
     */
    QuartzJob findById(Long id);

    /**
     * @param quartzJob /
     */
    void updateIsPause(QuartzJob quartzJob);

    /**
     * @param quartzJob /
     */
    void execution(QuartzJob quartzJob);

    /**
     * @param queryAll
     * @param response /
     * @throws IOException /
     */
    void download(List<QuartzJob> queryAll, HttpServletResponse response) throws IOException;

    /**
     * @param queryAllLog 
     * @param response /
     * @throws IOException /
     */
    void downloadLog(List<QuartzLog> queryAllLog, HttpServletResponse response) throws IOException;
}

