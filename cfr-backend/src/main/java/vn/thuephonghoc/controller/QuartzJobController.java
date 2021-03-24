package vn.thuephonghoc.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import vn.thuephonghoc.aop.log.Log;
import vn.thuephonghoc.entity.QuartzJob;
import vn.thuephonghoc.exception.BadRequestException;
import vn.thuephonghoc.query.JobQueryCriteria;
import vn.thuephonghoc.query.QuartzJobQueryCriteria;
import vn.thuephonghoc.service.QuartzJobService;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;

@Slf4j
@RestController
@Api(tags = "System: Timing Task Management")
@RequestMapping("/api/jobs")
public class QuartzJobController {

    private static final String ENTITY_NAME = "quartzJob";

    private final QuartzJobService quartzJobService;

    public QuartzJobController(QuartzJobService quartzJobService) {
        this.quartzJobService = quartzJobService;
    }

    @Log("Query Timing Task")
    @ApiOperation("Query Timing Task")
    @GetMapping
    @PreAuthorize("@ddt.check('timing:list')")
    public ResponseEntity<Object> getJobs(QuartzJobQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(quartzJobService.queryAll(criteria,pageable), HttpStatus.OK);
    }

    @Log("Export task data")
    @ApiOperation("Export task data")
    @GetMapping(value = "/download")
    @PreAuthorize("@ddt.check('timing:download')")
    public void download(HttpServletResponse response, JobQueryCriteria criteria) throws IOException {
        quartzJobService.download(quartzJobService.queryAll(criteria), response);
    }

    @Log("Export log data")
    @ApiOperation("Export log data")
    @GetMapping(value = "/logs/download")
    @PreAuthorize("@ddt.check('timing:list')")
    public void downloadLog(HttpServletResponse response, JobQueryCriteria criteria) throws IOException {
        quartzJobService.downloadLog(quartzJobService.queryAllLog(criteria), response);
    }

    @ApiOperation("Query task execution log")
    @GetMapping(value = "/logs")
    @PreAuthorize("@ddt.check('timing:list')")
    public ResponseEntity<Object> getJobLogs(QuartzJobQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(quartzJobService.queryAllLog(criteria,pageable), HttpStatus.OK);
    }

    @Log("Add Timed Task")
    @ApiOperation("Add Timed Task")
    @PostMapping
    @PreAuthorize("@ddt.check('timing:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody QuartzJob resources){
        if (resources.getId() != null) {
            throw new BadRequestException("A new "+ ENTITY_NAME +" cannot already have an ID");
        }
        return new ResponseEntity<>(quartzJobService.create(resources),HttpStatus.CREATED);
    }

    @Log("Modify Timing Task")
    @ApiOperation("Modify Timing Task")
    @PutMapping
    @PreAuthorize("@ddt.check('timing:edit')")
    public ResponseEntity<Object> update(@Validated(QuartzJob.Update.class) @RequestBody QuartzJob resources){
        quartzJobService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("Change timed task status")
    @ApiOperation("Change timing task status")
    @PutMapping(value = "/{id}")
    @PreAuthorize("@ddt.check('timing:edit')")
    public ResponseEntity<Object> updateIsPause(@PathVariable Long id){
        quartzJobService.updateIsPause(quartzJobService.findById(id));
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("Perform timing tasks")
    @ApiOperation("Perform timing tasks")
    @PutMapping(value = "/exec/{id}")
    @PreAuthorize("@ddt.check('timing:edit')")
    public ResponseEntity<Object> execution(@PathVariable Long id){
        quartzJobService.execution(quartzJobService.findById(id));
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("Delete Timed Task")
    @ApiOperation("Delete Timed Task")
    @DeleteMapping
    @PreAuthorize("@ddt.check('timing:del')")
    public ResponseEntity<Object> delete(@RequestBody Set<Long> ids){
        quartzJobService.delete(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}

