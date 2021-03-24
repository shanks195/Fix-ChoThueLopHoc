package vn.thuephonghoc.config;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import vn.thuephonghoc.entity.QuartzJob;
import vn.thuephonghoc.repository.QuartzJobRepository;
import vn.thuephonghoc.utils.QuartzManage;

import java.util.List;

@Component
public class JobRunner implements ApplicationRunner {

    private final QuartzJobRepository quartzJobRepository;

    private final QuartzManage quartzManage;

    public JobRunner(QuartzJobRepository quartzJobRepository, QuartzManage quartzManage) {
        this.quartzJobRepository = quartzJobRepository;
        this.quartzManage = quartzManage;
    }

    /**
     * Reactivate the enabled scheduled tasks when the project starts
     * @param applicationArguments /
     */
    @Override
    public void run(ApplicationArguments applicationArguments){
        System.out.println("--------------------Inject timing tasks-------------------- -");
        List<QuartzJob> quartzJobs = quartzJobRepository.findByIsPauseIsFalse();
        quartzJobs.forEach(quartzManage::addJob);
        System.out.println("--------------------Timed task injection completed------------------- --");
    }
}


