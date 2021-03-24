package vn.thuephonghoc.service;

import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;

import vn.thuephonghoc.entity.Log;
import vn.thuephonghoc.query.LogQueryCriteria;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;


public interface LogService {

    /**
     * @param criteria 
     * @param pageable 
     * @return /
     */
    Object queryAll(LogQueryCriteria criteria, Pageable pageable);

    /**
     * @param criteria
     * @return /
     */
    List<Log> queryAll(LogQueryCriteria criteria);

    /**
     * @param criteria 
     * @param pageable 
     * @return -
     */
    Object queryAllByUser(LogQueryCriteria criteria, Pageable pageable);

    /**
     * @param username 
     * @param browser 
     * @param ip 
     * @param joinPoint /
     * @param log 
     */
    @Async
    void save(String username, String browser, String ip, ProceedingJoinPoint joinPoint, Log log);

    /**
     *
     * @param id
     * @return Object
     */
    Object findByErrDetail(Long id);

    /**
     * @param logs
     * @param response /
     * @throws IOException /
     */
    void download(List<Log> logs, HttpServletResponse response) throws IOException;


    /**
     * @param queryAll 
     * @param response /
     * @throws IOException /
     */
    void downloadError(List<Log> queryAll, HttpServletResponse response) throws IOException;

    /**
     * Delete all error logs
     */
    void delAllByError();

    /**
     * Delete all INFO logs
     */
    void delAllByInfo();

}
