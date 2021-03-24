package vn.thuephonghoc.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import vn.thuephonghoc.aop.log.Log;
import vn.thuephonghoc.config.DataScope;
import vn.thuephonghoc.entity.Job;
import vn.thuephonghoc.exception.BadRequestException;
import vn.thuephonghoc.query.JobQueryCriteria;
import vn.thuephonghoc.service.JobService;
import vn.thuephonghoc.utils.ThrowableUtil;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;

@RestController
@Api(tags = "System: Post Management")
@RequestMapping("/api/job")
public class JobController {

    private final JobService jobService;

    private final DataScope dataScope;

    private static final String ENTITY_NAME = "job";

    public JobController(JobService jobService, DataScope dataScope) {
        this.jobService = jobService;
        this.dataScope = dataScope;
    }

    @Log("Export job data")
    @ApiOperation("Export job data")
    @GetMapping(value = "/download")
    @PreAuthorize("@ddt.check('job:download')")
    public void download(HttpServletResponse response, JobQueryCriteria criteria) throws IOException {
        jobService.download(jobService.queryAll(criteria), response);
    }

    @Log("Query Position")
    @ApiOperation("Query Position")
    @GetMapping
    @PreAuthorize("@ddt.check('job:list','user:list')")
    public ResponseEntity<Object> getJobs(JobQueryCriteria criteria, Pageable pageable){
        // data permission
        criteria.setDeptIds(dataScope.getDeptIds());
        return new ResponseEntity<>(jobService.queryAll(criteria, pageable),HttpStatus.OK);
    }

    @Log("New Post")
    @ApiOperation("New Post")
    @PostMapping
    @PreAuthorize("@ddt.check('job:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody Job resources){
        if (resources.getId() != null) {
            throw new BadRequestException("A new "+ ENTITY_NAME +" cannot already have an ID");
        }
        return new ResponseEntity<>(jobService.create(resources),HttpStatus.CREATED);
    }

    @Log("Modify Post")
    @ApiOperation("Modify post")
    @PutMapping
    @PreAuthorize("@ddt.check('job:edit')")
    public ResponseEntity<Object> update(@Validated(Job.Update.class) @RequestBody Job resources){
        jobService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("Delete Post")
    @ApiOperation("Delete post")
    @DeleteMapping
    @PreAuthorize("@ddt.check('job:del')")
    public ResponseEntity<Object> delete(@RequestBody Set<Long> ids){
        try {
            jobService.delete(ids);
        }catch (Throwable e){
            ThrowableUtil.throwForeignKeyException(e, "The selected position has a user association, please cancel the association and try again");
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
