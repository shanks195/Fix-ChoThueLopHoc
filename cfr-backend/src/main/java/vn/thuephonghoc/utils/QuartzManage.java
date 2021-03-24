package vn.thuephonghoc.utils;

import lombok.extern.slf4j.Slf4j;
import vn.thuephonghoc.entity.QuartzJob;
import vn.thuephonghoc.exception.BadRequestException;

import org.quartz.*;
import org.quartz.impl.triggers.CronTriggerImpl;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;
import java.util.Date;
import static org.quartz.TriggerBuilder.newTrigger;

@Slf4j
@Component
public class QuartzManage {

    private static final String JOB_NAME = "TASK_";

    @Resource(name = "scheduler")
    private Scheduler scheduler;

    public void addJob(QuartzJob quartzJob){
        try {
        	// Build job information
            JobDetail jobDetail = JobBuilder.newJob(ExecutionJob.class).
                    withIdentity(JOB_NAME + quartzJob.getId()).build();

            //Create Trigger by trigger name and cron expression
            Trigger cronTrigger = newTrigger()
                    .withIdentity(JOB_NAME + quartzJob.getId())
                    .startNow()
                    .withSchedule(CronScheduleBuilder.cronSchedule(quartzJob.getCronExpression()))
                    .build();

            cronTrigger.getJobDataMap().put(QuartzJob.JOB_KEY, quartzJob);

            //Reset start time
            ((CronTriggerImpl)cronTrigger).setStartTime(new Date());

            //Perform timing tasks
            scheduler.scheduleJob(jobDetail, cronTrigger);

            // Pause the task
            if (quartzJob.getIsPause()) {
                pauseJob(quartzJob);
            }
        } catch (Exception e){
            log.error("Failed to create timing task", e);
            throw new BadRequestException("Failed to create timed task");
        }
    }

    /**
     * Update job cron expression
     * @param quartzJob /
     */
    public void updateJobCron(QuartzJob quartzJob){
        try {
            TriggerKey triggerKey = TriggerKey.triggerKey(JOB_NAME + quartzJob.getId());
            CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);
            // Create a scheduled task if it does not exist
            if(trigger == null){
                addJob(quartzJob);
                trigger = (CronTrigger) scheduler.getTrigger(triggerKey);
            }
            CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(quartzJob.getCronExpression());
            trigger = trigger.getTriggerBuilder().withIdentity(triggerKey).withSchedule(scheduleBuilder).build();
            //Reset start time
            ((CronTriggerImpl)trigger).setStartTime(new Date());
            trigger.getJobDataMap().put(QuartzJob.JOB_KEY,quartzJob);

            scheduler.rescheduleJob(triggerKey, trigger);
            // Pause the task
            if (quartzJob.getIsPause()) {
                pauseJob(quartzJob);
            }
        } catch (Exception e){
            log.error("Failed to update timing task", e);
            throw new BadRequestException("Failed to update timing task");
        }

    }

    /**
     * Delete a job
     * @param quartzJob /
     */
    public void deleteJob(QuartzJob quartzJob){
        try {
            JobKey jobKey = JobKey.jobKey(JOB_NAME + quartzJob.getId());
            scheduler.pauseJob(jobKey);
            scheduler.deleteJob(jobKey);
        } catch (Exception e){
            log.error("Failed to delete the scheduled task", e);
            throw new BadRequestException("Failed to delete timing task");
        }
    }

    /**
     * Resuming a job
     * @param quartzJob /
     */
    public void resumeJob(QuartzJob quartzJob){
        try {
            TriggerKey triggerKey = TriggerKey.triggerKey(JOB_NAME + quartzJob.getId());
            CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);
            // Create a scheduled task if it does not exist
            if(trigger == null)
                addJob(quartzJob);
            JobKey jobKey = JobKey.jobKey(JOB_NAME + quartzJob.getId());
            scheduler.resumeJob(jobKey);
        } catch (Exception e){
            log.error("Failed to restore timing task", e);
            throw new BadRequestException("Failed to restore timing task");
        }
    }

    /**
     * Execute job immediately
     * @param quartzJob /
     */
    public void runJobNow(QuartzJob quartzJob){
        try {
            TriggerKey triggerKey = TriggerKey.triggerKey(JOB_NAME + quartzJob.getId());
            CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);
            // Create a scheduled task if it does not exist
            if(trigger == null)
                addJob(quartzJob);
            JobDataMap dataMap = new JobDataMap();
            dataMap.put(QuartzJob.JOB_KEY, quartzJob);
            JobKey jobKey = JobKey.jobKey(JOB_NAME + quartzJob.getId());
            scheduler.triggerJob(jobKey,dataMap);
        } catch (Exception e){
            log.error("Timed task execution failed", e);
            throw new BadRequestException("Timed task execution failed");
        }
    }

    /**
     * Pause a job
     * @param quartzJob /
     */
    public void pauseJob(QuartzJob quartzJob){
        try {
            JobKey jobKey = JobKey.jobKey(JOB_NAME + quartzJob.getId());
            scheduler.pauseJob(jobKey);
        } catch (Exception e){
            log.error("Timed task pause failed", e);
            throw new BadRequestException("Timed task pause failed");
        }
    }
}

