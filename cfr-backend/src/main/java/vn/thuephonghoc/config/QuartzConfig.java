package vn.thuephonghoc.config;

import org.quartz.Scheduler;
import org.quartz.spi.TriggerFiredBundle;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.AdaptableJobFactory;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Component;

/**
 * Timing task configuration
 */
@Configuration
public class QuartzConfig {

    /**
     * Solve the problem of null injection of Spring Bean in Job
     */
    @Component("quartzJobFactory")
    public static class QuartzJobFactory extends AdaptableJobFactory {

        private final AutowireCapableBeanFactory capableBeanFactory;

        public QuartzJobFactory(AutowireCapableBeanFactory capableBeanFactory) {
            this.capableBeanFactory = capableBeanFactory;
        }

        @Override
        protected Object createJobInstance(TriggerFiredBundle bundle) throws Exception {

            //Call the method of the parent class
            Object jobInstance = super.createJobInstance(bundle);
            capableBeanFactory.autowireBean(jobInstance);
            return jobInstance;
        }
    }

    /**
     * Inject scheduler into spring 
     * @param quartzJobFactory /
     * @return Scheduler
     * @throws Exception /
     */
    @Bean(name = "scheduler")
    public Scheduler scheduler(QuartzJobFactory quartzJobFactory) throws Exception {
        SchedulerFactoryBean factoryBean=new SchedulerFactoryBean();
        factoryBean.setJobFactory(quartzJobFactory);
        factoryBean.afterPropertiesSet();
        Scheduler scheduler=factoryBean.getScheduler();
        scheduler.start();
        return scheduler;
    }
}

