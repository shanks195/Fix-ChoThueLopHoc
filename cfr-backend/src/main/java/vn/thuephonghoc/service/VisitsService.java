package vn.thuephonghoc.service;

import org.springframework.scheduling.annotation.Async;

import javax.servlet.http.HttpServletRequest;

public interface VisitsService {

	/**
     * Provide timed tasks, executed at 0:00 every day
     */
    void save();

    /**
     * Add record
     * @param request /
     */
    @Async
    void count(HttpServletRequest request);

    /**
     * retrieve data
     * @return /
     */
    Object get();

    /**
     * getChartData
     * @return /
     */
    Object getChartData();
}
