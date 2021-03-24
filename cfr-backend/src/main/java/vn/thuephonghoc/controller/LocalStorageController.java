package vn.thuephonghoc.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.*;
import vn.thuephonghoc.aop.log.Log;
import vn.thuephonghoc.entity.LocalStorage;
import vn.thuephonghoc.query.LocalStorageQueryCriteria;
import vn.thuephonghoc.service.LocalStorageService;

import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Api(tags = "Tool: Local Storage Management")
@RestController
@RequestMapping("/api/localStorage")
public class LocalStorageController {

    private final LocalStorageService localStorageService;

    public LocalStorageController(LocalStorageService localStorageService) {
        this.localStorageService = localStorageService;
    }
    @ApiOperation("Query File")
    @GetMapping()
    @PreAuthorize("@ddt.check('storage:list')")
    public ResponseEntity<Object> getLocalStorages(LocalStorageQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(localStorageService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @Log("Export data")
    @ApiOperation("Export data")
    @GetMapping(value = "/download")
    @PreAuthorize("@ddt.check('storage:download')")
    public void download(HttpServletResponse response, LocalStorageQueryCriteria criteria) throws IOException {
        localStorageService.download(localStorageService.queryAll(criteria), response);
    }

    @ApiOperation("Upload file")
    @PostMapping()
    @PreAuthorize("@ddt.check('storage:add')")
    public ResponseEntity<Object> create(@RequestParam String name, @RequestParam("file") MultipartFile file){
        return new ResponseEntity<>(localStorageService.create(name, file),HttpStatus.CREATED);
    }

    @ApiOperation("Modify file")
    @PutMapping()
    @PreAuthorize("@ddt.check('storage:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody LocalStorage resources){
        localStorageService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("Multiple selection delete")
    @DeleteMapping
    @ApiOperation("Multiple selection delete")
    @PreAuthorize("@ddt.check('storage:delete')")
    public ResponseEntity<Object> deleteAll(@RequestBody Long[] ids) {
        localStorageService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
