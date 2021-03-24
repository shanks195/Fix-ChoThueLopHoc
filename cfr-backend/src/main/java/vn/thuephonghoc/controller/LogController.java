package vn.thuephonghoc.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import vn.thuephonghoc.aop.log.Log;
import vn.thuephonghoc.query.LogQueryCriteria;
import vn.thuephonghoc.service.LogService;
import vn.thuephonghoc.utils.SecurityUtils;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequestMapping("/api/logs")
@Api(tags = "Monitoring: Log Management")
public class LogController {

    private final LogService logService;
    public LogController(LogService logService) {
        this.logService = logService;
    }

    @Log("Export data")
    @ApiOperation("Export data")
    @GetMapping(value = "/download")
    @PreAuthorize("@ddt.check()")
    public void download(HttpServletResponse response, LogQueryCriteria criteria) throws IOException {
        if ("ERROR".equals(criteria.getLogType())) {
            logService.downloadError(logService.queryAll(criteria), response);
        } else {
            logService.download(logService.queryAll(criteria), response);
        }
    }

    @GetMapping
    @ApiOperation("Log Query")
    @PreAuthorize("@ddt.check()")
    public ResponseEntity<Object> getLogs(LogQueryCriteria criteria, Pageable pageable){
        criteria.setLogType("INFO");
        return new ResponseEntity<>(logService.queryAll(criteria,pageable), HttpStatus.OK);
    }

    @GetMapping(value = "/user")
    @ApiOperation("User log query")
    public ResponseEntity<Object> getUserLogs(LogQueryCriteria criteria, Pageable pageable){
        criteria.setLogType("INFO");
        criteria.setBlurry(SecurityUtils.getCurrentUsername());
        return new ResponseEntity<>(logService.queryAllByUser(criteria,pageable), HttpStatus.OK);
    }

    @GetMapping(value = "/error")
    @ApiOperation("Error log query")
    @PreAuthorize("@ddt.check()")
    public ResponseEntity<Object> getErrorLogs(LogQueryCriteria criteria, Pageable pageable){
        criteria.setLogType("ERROR");
        return new ResponseEntity<>(logService.queryAll(criteria,pageable), HttpStatus.OK);
    }

    @GetMapping(value = "/error/{id}")
    @ApiOperation("Log exception details query")
    @PreAuthorize("@ddt.check()")
    public ResponseEntity<Object> getErrorLogs(@PathVariable Long id){
        return new ResponseEntity<>(logService.findByErrDetail(id), HttpStatus.OK);
    }


    @DeleteMapping(value = "/del/error")
    @Log("Delete all ERROR logs")
    @ApiOperation("Delete all ERROR logs")
    @PreAuthorize("@ddt.check()")
    public ResponseEntity<Object> delAllByError(){
        logService.delAllByError();
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping(value = "/del/info")
    @Log("Delete all INFO logs")
    @ApiOperation("Delete all INFO logs")
    @PreAuthorize("@ddt.check()")
    public ResponseEntity<Object> delAllByInfo(){
        logService.delAllByInfo();
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
