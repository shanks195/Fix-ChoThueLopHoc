package vn.thuephonghoc.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import vn.thuephonghoc.aop.log.Log;
import vn.thuephonghoc.entity.DictDetail;
import vn.thuephonghoc.exception.BadRequestException;
import vn.thuephonghoc.query.DictDetailQueryCriteria;
import vn.thuephonghoc.service.DictDetailService;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@Api(tags = "System: Dictionary details management")
@RequestMapping("/api/dictDetail")
public class DictDetailController {

    private final DictDetailService dictDetailService;

    public DictDetailController(DictDetailService dictDetailService) {
        this.dictDetailService = dictDetailService;
    }

    private static final String ENTITY_NAME = "dictDetail";

    @Log("Query dictionary details")
    @ApiOperation("Query dictionary details")
    @GetMapping
    public ResponseEntity<Object> getDictDetails(DictDetailQueryCriteria criteria,
                                         @PageableDefault(sort = {"sort"}, direction = Sort.Direction.ASC) Pageable pageable){
        return new ResponseEntity<>(dictDetailService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @Log("Query multiple dictionary details")
    @ApiOperation("Query multiple dictionary details")
    @GetMapping(value = "/map")
    public ResponseEntity<Object> getDictDetailMaps(DictDetailQueryCriteria criteria,
                                            @PageableDefault(sort = {"sort"}, direction = Sort.Direction.ASC) Pageable pageable){
        String[] names = criteria.getDictName().split(",");
        Map<String,Object> map = new HashMap<>(names.length);
        for (String name: names) {
            criteria.setDictName(name);
            map.put(name,dictDetailService.queryAll(criteria,pageable).get("content"));
        }
        return new ResponseEntity<>(map,HttpStatus.OK);
    }

    @Log("New dictionary details")
    @ApiOperation("Add dictionary details")
    @PostMapping
    @PreAuthorize("@ddt.check('dict:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody DictDetail resources){
        if (resources.getId() != null) {
            throw new BadRequestException("A new "+ ENTITY_NAME +" cannot already have an ID");
        }
        return new ResponseEntity<>(dictDetailService.create(resources),HttpStatus.CREATED);
    }

    @Log("Modify dictionary details")
    @ApiOperation("Modify the dictionary details")
    @PutMapping
    @PreAuthorize("@ddt.check('dict:edit')")
    public ResponseEntity<Object> update(@Validated(DictDetail.Update.class) @RequestBody DictDetail resources){
        dictDetailService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("Delete dictionary details")
    @ApiOperation("Delete dictionary details")
    @DeleteMapping(value = "/{id}")
    @PreAuthorize("@ddt.check('dict:del')")
    public ResponseEntity<Object> delete(@PathVariable Long id){
        dictDetailService.delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
