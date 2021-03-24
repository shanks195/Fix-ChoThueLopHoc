package vn.thuephonghoc.utils;

import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.quartz.QuartzJobBean;

import vn.thuephonghoc.entity.QuartzJob;
import vn.thuephonghoc.entity.QuartzLog;
import vn.thuephonghoc.repository.QuartzLogRepository;
import vn.thuephonghoc.service.QuartzJobService;
import vn.thuephonghoc.thread.ThreadPoolExecutorUtil;

import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

@Async
public class ExecutionJob extends QuartzJobBean {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	// This place is for reference only
	private final static ThreadPoolExecutor EXECUTOR = ThreadPoolExecutorUtil.getPoll();

	@Override
	@SuppressWarnings("unchecked")
	protected void executeInternal(JobExecutionContext context) {
		QuartzJob quartzJob = (QuartzJob) context.getMergedJobDataMap().get(QuartzJob.JOB_KEY);
		// Get spring bean
		QuartzLogRepository quartzLogRepository = SpringContextHolder.getBean(QuartzLogRepository.class);
		QuartzJobService quartzJobService = SpringContextHolder.getBean(QuartzJobService.class);

		QuartzLog log = new QuartzLog();
		log.setJobName(quartzJob.getJobName());
		log.setBeanName(quartzJob.getBeanName());
		log.setMethodName(quartzJob.getMethodName());
		log.setParams(quartzJob.getParams());
		long startTime = System.currentTimeMillis();
		log.setCronExpression(quartzJob.getCronExpression());
		try {
			// perform task
			logger.info("Task is ready to be executed, task name: {}", quartzJob.getJobName());
			QuartzRunnable task = new QuartzRunnable(quartzJob.getBeanName(), quartzJob.getMethodName(),
					quartzJob.getParams());
			Future<?> future = EXECUTOR.submit(task);
			future.get();
			long times = System.currentTimeMillis() - startTime;
			log.setTime(times);
			// task status
			log.setIsSuccess(true);
			logger.info("Task completed, task name: {} total time: {} milliseconds", quartzJob.getJobName(), times);
		} catch (Exception e) {
			logger.error("Task execution failed, task name: {}" + quartzJob.getJobName(), e);
			long times = System.currentTimeMillis() - startTime;
			log.setTime(times);
			// Task status 0: success 1: failure
			log.setIsSuccess(false);
			log.setExceptionDetail(ThrowableUtil.getStackTrace(e));
			quartzJob.setIsPause(false);
			// update status
			quartzJobService.updateIsPause(quartzJob);
		} finally {
			quartzLogRepository.save(log);
		}
	}
}
