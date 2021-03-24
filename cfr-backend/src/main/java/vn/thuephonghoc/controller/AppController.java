package vn.thuephonghoc.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import vn.thuephonghoc.aop.log.Log;
import vn.thuephonghoc.entity.App;
import vn.thuephonghoc.query.AppQueryCriteria;
import vn.thuephonghoc.service.AppService;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;


@Api(tags = "application management")
@RestController
@RequestMapping("/api/app")
public class AppController {

    private final AppService appService;

    public AppController(AppService appService){
        this.appService = appService;
    }

    @Log("Export application data")
    @ApiOperation("Export application data")
    @GetMapping(value = "/download")
    @PreAuthorize("@ddt.check('app:download')")
    public void download(HttpServletResponse response, AppQueryCriteria criteria) throws IOException {
        appService.download(appService.queryAll(criteria), response);
    }

    @Log("Query Application")
    @ApiOperation(value = "Query Application")
    @GetMapping
    @PreAuthorize("@ddt.check('app:list')")
    public ResponseEntity<Object> getApps(AppQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(appService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @Log("New Application")
    @ApiOperation(value = "New Application")
    @PostMapping
    @PreAuthorize("@ddt.check('app:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody App resources){
        return new ResponseEntity<>(appService.create(resources),HttpStatus.CREATED);
    }

    @Log("Modify Application")
    @ApiOperation(value = "Modify Application")
    @PutMapping
    @PreAuthorize("@ddt.check('app:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody App resources){
        appService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("Delete Application")
    @ApiOperation(value = "Delete application")
    @DeleteMapping
    @PreAuthorize("@ddt.check('app:del')")
    public ResponseEntity<Object> delete(@RequestBody Set<Long> ids){
        appService.delete(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
