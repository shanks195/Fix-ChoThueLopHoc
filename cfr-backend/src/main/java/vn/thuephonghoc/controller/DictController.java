package vn.thuephonghoc.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import vn.thuephonghoc.aop.log.Log;
import vn.thuephonghoc.entity.Dict;
import vn.thuephonghoc.exception.BadRequestException;
import vn.thuephonghoc.query.DictQueryCriteria;
import vn.thuephonghoc.service.DictService;

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
@Api(tags = "System: Dictionary Management")
@RequestMapping("/api/dict")
public class DictController {

    private final DictService dictService;

    public DictController(DictService dictService) {
        this.dictService = dictService;
    }

    private static final String ENTITY_NAME = "dict";

    @Log("Export dictionary data")
    @ApiOperation("Export dictionary data")
    @GetMapping(value = "/download")
    @PreAuthorize("@ddt.check('dict:download')")
    public void download(HttpServletResponse response, DictQueryCriteria criteria) throws IOException {
        dictService.download(dictService.queryAll(criteria), response);
    }

    @Log("Query Dictionary")
    @ApiOperation("Query Dictionary")
    @GetMapping(value = "/all")
    @PreAuthorize("@ddt.check('dict:list')")
    public ResponseEntity<Object> all(){
        return new ResponseEntity<>(dictService.queryAll(new DictQueryCriteria()),HttpStatus.OK);
    }

    @Log("Query Dictionary")
    @ApiOperation("Query Dictionary")
    @GetMapping
    @PreAuthorize("@ddt.check('dict:list')")
    public ResponseEntity<Object> getDicts(DictQueryCriteria resources, Pageable pageable){
        return new ResponseEntity<>(dictService.queryAll(resources,pageable),HttpStatus.OK);
    }

    @Log("New Dictionary")
    @ApiOperation("Add a new dictionary")
    @PostMapping
    @PreAuthorize("@ddt.check('dict:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody Dict resources){
        if (resources.getId() != null) {
            throw new BadRequestException("A new "+ ENTITY_NAME +" cannot already have an ID");
        }
        return new ResponseEntity<>(dictService.create(resources),HttpStatus.CREATED);
    }

    @Log("Modify Dictionary")
    @ApiOperation("Modify Dictionary")
    @PutMapping
    @PreAuthorize("@ddt.check('dict:edit')")
    public ResponseEntity<Object> update(@Validated(Dict.Update.class) @RequestBody Dict resources){
        dictService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("Delete dictionary")
    @ApiOperation("Delete dictionary")
    @DeleteMapping
    @PreAuthorize("@ddt.check('dict:del')")
    public ResponseEntity<Object> delete(@RequestBody Set<Long> ids){
        dictService.delete(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
