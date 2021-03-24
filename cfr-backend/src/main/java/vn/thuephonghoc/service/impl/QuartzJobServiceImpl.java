package vn.thuephonghoc.service.impl;

import org.quartz.CronExpression;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import vn.thuephonghoc.entity.QuartzJob;
import vn.thuephonghoc.entity.QuartzLog;
import vn.thuephonghoc.exception.BadRequestException;
import vn.thuephonghoc.query.JobQueryCriteria;
import vn.thuephonghoc.query.QuartzJobQueryCriteria;
import vn.thuephonghoc.repository.QuartzJobRepository;
import vn.thuephonghoc.repository.QuartzLogRepository;
import vn.thuephonghoc.service.QuartzJobService;
import vn.thuephonghoc.utils.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

@Service(value = "quartzJobService")
@CacheConfig(cacheNames = "quartzJob")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class QuartzJobServiceImpl implements QuartzJobService {

    private final QuartzJobRepository quartzJobRepository;

    private final QuartzLogRepository quartzLogRepository;

    private final QuartzManage quartzManage;

    public QuartzJobServiceImpl(QuartzJobRepository quartzJobRepository, QuartzLogRepository quartzLogRepository, QuartzManage quartzManage) {
        this.quartzJobRepository = quartzJobRepository;
        this.quartzLogRepository = quartzLogRepository;
        this.quartzManage = quartzManage;
    }

    @Override
    @Cacheable
    public Object queryAll(QuartzJobQueryCriteria criteria, Pageable pageable){
        return PageUtil.toPage(quartzJobRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable));
    }

    @Override
    public Object queryAllLog(QuartzJobQueryCriteria criteria, Pageable pageable){
        return PageUtil.toPage(quartzLogRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable));
    }

    @Override
    public List<QuartzJob> queryAll(JobQueryCriteria criteria) {
        return quartzJobRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder));
    }

    @Override
    public List<QuartzLog> queryAllLog(JobQueryCriteria criteria) {
        return quartzLogRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder));
    }

    @Override
    @Cacheable(key = "#p0")
    public QuartzJob findById(Long id) {
        QuartzJob quartzJob = quartzJobRepository.findById(id).orElseGet(QuartzJob::new);
        ValidationUtil.isNull(quartzJob.getId(),"QuartzJob","id",id);
        return quartzJob;
    }

    @Override
    @CacheEvict(allEntries = true)
    @Transactional(rollbackFor = Exception.class)
    public QuartzJob create(QuartzJob resources) {
        if (!CronExpression.isValidExpression(resources.getCronExpression())){
            throw new BadRequestException("cron expression format error");
        }
        resources = quartzJobRepository.save(resources);
        quartzManage.addJob(resources);
        return resources;
    }

    @Override
    @CacheEvict(allEntries = true)
    @Transactional(rollbackFor = Exception.class)
    public void update(QuartzJob resources) {
        if(resources.getId().equals(1L)){
            throw new BadRequestException("The task is not operational");
        }
        if (!CronExpression.isValidExpression(resources.getCronExpression())){
            throw new BadRequestException("cron expression format error");
        }
        resources = quartzJobRepository.save(resources);
        quartzManage.updateJobCron(resources);
    }

    @Override
    @CacheEvict(allEntries = true)
    public void updateIsPause(QuartzJob quartzJob) {
        if(quartzJob.getId().equals(1L)){
            throw new BadRequestException("The task is not operational");
        }
        if (quartzJob.getIsPause()) {
            quartzManage.resumeJob(quartzJob);
            quartzJob.setIsPause(false);
        } else {
            quartzManage.pauseJob(quartzJob);
            quartzJob.setIsPause(true);
        }
        quartzJobRepository.save(quartzJob);
    }

    @Override
    public void execution(QuartzJob quartzJob) {
        if(quartzJob.getId().equals(1L)){
            throw new BadRequestException("The task is not operational");
        }
        quartzManage.runJobNow(quartzJob);
    }

    @Override
    @CacheEvict(allEntries = true)
    @Transactional(rollbackFor = Exception.class)
    public void delete(Set<Long> ids) {
        for (Long id : ids) {
            if(id.equals(1L)){
                throw new BadRequestException("Update visitor records cannot be deleted, you can cancel the restriction in the background code");
            }
            QuartzJob quartzJob = findById(id);
            quartzManage.deleteJob(quartzJob);
            quartzJobRepository.delete(quartzJob);
        }
    }

    @Override
    public void download(List<QuartzJob> quartzJobs, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (QuartzJob quartzJob : quartzJobs) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("Task name", quartzJob.getJobName());
            map.put("BeanName", quartzJob.getBeanName());
            map.put("Execution method", quartzJob.getMethodName());
            map.put("Parameter", quartzJob.getParams());
            map.put("Expression", quartzJob.getCronExpression());
            map.put("Status", quartzJob.getIsPause()? "Pausing": "Running");
            map.put("Description", quartzJob.getRemark());
            map.put("Creation Date", quartzJob.getCreateTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    public void downloadLog(List<QuartzLog> queryAllLog, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (QuartzLog quartzLog : queryAllLog) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("Task name", quartzLog.getJobName());
            map.put("BeanName", quartzLog.getBeanName());
            map.put("execution method", quartzLog.getMethodName());
            map.put("Parameter", quartzLog.getParams());
            map.put("Expression", quartzLog.getCronExpression());
            map.put("Exception Details", quartzLog.getExceptionDetail());
            map.put("Time-consuming/millisecond", quartzLog.getTime());
            map.put("Status", quartzLog.getIsSuccess()? "Success": "Failure");
            map.put("Creation Date", quartzLog.getCreateTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }
}

