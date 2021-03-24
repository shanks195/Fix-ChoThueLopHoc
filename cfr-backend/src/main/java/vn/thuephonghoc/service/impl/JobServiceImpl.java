package vn.thuephonghoc.service.impl;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import vn.thuephonghoc.dto.JobDTO;
import vn.thuephonghoc.entity.Job;
import vn.thuephonghoc.mapper.JobMapper;
import vn.thuephonghoc.query.JobQueryCriteria;
import vn.thuephonghoc.repository.DeptRepository;
import vn.thuephonghoc.repository.JobRepository;
import vn.thuephonghoc.service.JobService;
import vn.thuephonghoc.utils.FileUtil;
import vn.thuephonghoc.utils.PageUtil;
import vn.thuephonghoc.utils.QueryHelp;
import vn.thuephonghoc.utils.ValidationUtil;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

@Service
@CacheConfig(cacheNames = "job")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class JobServiceImpl implements JobService {

    private final JobRepository jobRepository;

    private final JobMapper jobMapper;

    private final DeptRepository deptRepository;

    public JobServiceImpl(JobRepository jobRepository, JobMapper jobMapper, DeptRepository deptRepository) {
        this.jobRepository = jobRepository;
        this.jobMapper = jobMapper;
        this.deptRepository = deptRepository;
    }

    @Override
    @Cacheable
    public Map<String,Object> queryAll(JobQueryCriteria criteria, Pageable pageable) {
        Page<Job> page = jobRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        List<JobDTO> jobs = new ArrayList<>();
        for (Job job : page.getContent()) {
            jobs.add(jobMapper.toDto(job,deptRepository.findNameById(job.getDept().getPid())));
        }
        return PageUtil.toPage(jobs,page.getTotalElements());
    }

    @Override
    @Cacheable
    public List<JobDTO> queryAll(JobQueryCriteria criteria) {
        List<Job> list = jobRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder));
        return jobMapper.toDto(list);
    }


    @Override
    @Cacheable(key = "#p0")
    public JobDTO findById(Long id) {
        Job job = jobRepository.findById(id).orElseGet(Job::new);
        ValidationUtil.isNull(job.getId(),"Job","id",id);
        return jobMapper.toDto(job);
    }

    @Override
    @CacheEvict(allEntries = true)
    @Transactional(rollbackFor = Exception.class)
    public JobDTO create(Job resources) {
        return jobMapper.toDto(jobRepository.save(resources));
    }

    @Override
    @CacheEvict(allEntries = true)
    @Transactional(rollbackFor = Exception.class)
    public void update(Job resources) {
        Job job = jobRepository.findById(resources.getId()).orElseGet(Job::new);
        ValidationUtil.isNull( job.getId(),"Job","id",resources.getId());
        resources.setId(job.getId());
        jobRepository.save(resources);
    }

    @Override
    @CacheEvict(allEntries = true)
    @Transactional(rollbackFor = Exception.class)
    public void delete(Set<Long> ids) {
        for (Long id : ids) {
            jobRepository.deleteById(id);
        }
    }

    @Override
    public void download(List<JobDTO> jobDTOs, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (JobDTO jobDTO : jobDTOs) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("Job name", jobDTO.getName());
            map.put("Department", jobDTO.getDept().getName());
            map.put("Job Status", jobDTO.getEnabled()? "Enable": "Disable");
            map.put("Creation Date", jobDTO.getCreateTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }
}
