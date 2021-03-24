package vn.thuephonghoc.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.*;
import vn.thuephonghoc.aop.log.Log;
import vn.thuephonghoc.entity.Test;
import vn.thuephonghoc.query.TestQueryCriteria;
import vn.thuephonghoc.service.TestService;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@Api(tags = "test management")
@RestController
@RequestMapping("/api/test")
public class TestController {

    private final TestService testService;

    public TestController(TestService testService) {
        this.testService = testService;
    }

    @Log("Export data")
    @ApiOperation("Export data")
    @GetMapping(value = "/download")
    @PreAuthorize("@ddt.check('test:download')")
    public void download(HttpServletResponse response, TestQueryCriteria criteria) throws IOException {
        testService.download(testService.queryAll(criteria), response);
    }

    @Log("Query test")
    @ApiOperation("Query test")
    @GetMapping()
    @PreAuthorize("@ddt.check('test:list')")
    public ResponseEntity<Object> getTests(TestQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(testService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @Log("Add test")
    @ApiOperation("Add test")
    @PostMapping
    @PreAuthorize("@ddt.check('test:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody Test resources){
        return new ResponseEntity<>(testService.create(resources),HttpStatus.CREATED);
    }

    @Log("Modify test")
    @ApiOperation("Modify test")
    @PutMapping
    @PreAuthorize("@ddt.check('test:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody Test resources){
    testService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("Delete test")
    @ApiOperation("Delete test")
    @PreAuthorize("@ddt.check('test:del')")
    @DeleteMapping
    public ResponseEntity<Object> deleteAll(@RequestBody Integer[] ids) {
    testService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
