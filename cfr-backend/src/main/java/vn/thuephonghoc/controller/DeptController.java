package vn.thuephonghoc.controller;

import cn.hutool.core.collection.CollectionUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import vn.thuephonghoc.aop.log.Log;
import vn.thuephonghoc.config.DataScope;
import vn.thuephonghoc.dto.DeptDto;
import vn.thuephonghoc.entity.Dept;
import vn.thuephonghoc.exception.BadRequestException;
import vn.thuephonghoc.query.DeptQueryCriteria;
import vn.thuephonghoc.service.DeptService;
import vn.thuephonghoc.utils.ThrowableUtil;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@Api(tags = "System: Department Management")
@RequestMapping("/api/dept")
public class DeptController {

    private final DeptService deptService;

    private final DataScope dataScope;

    private static final String ENTITY_NAME = "dept";

    public DeptController(DeptService deptService, DataScope dataScope) {
        this.deptService = deptService;
        this.dataScope = dataScope;
    }

    @Log("Export department data")
    @ApiOperation("Export department data")
    @GetMapping(value = "/download")
    @PreAuthorize("@ddt.check('dept:download')")
    public void download(HttpServletResponse response, DeptQueryCriteria criteria) throws IOException {
        deptService.download(deptService.queryAll(criteria), response);
    }

    @Log("Query Department")
    @ApiOperation("Query Department")
    @GetMapping
    @PreAuthorize("@ddt.check('user:list','dept:list')")
    public ResponseEntity<Object> getDepts(DeptQueryCriteria criteria){
        // data permission
        criteria.setIds(dataScope.getDeptIds());
        List<DeptDto> deptDtos = deptService.queryAll(criteria);
        return new ResponseEntity<>(deptService.buildTree(deptDtos),HttpStatus.OK);
    }

    @Log("New Department")
    @ApiOperation("New Department")
    @PostMapping
    @PreAuthorize("@ddt.check('dept:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody Dept resources){
        if (resources.getId() != null) {
            throw new BadRequestException("A new "+ ENTITY_NAME +" cannot already have an ID");
        }
        return new ResponseEntity<>(deptService.create(resources),HttpStatus.CREATED);
    }

    @Log("Modify Department")
    @ApiOperation("Modify Department")
    @PutMapping
    @PreAuthorize("@ddt.check('dept:edit')")
    public ResponseEntity<Object> update(@Validated(Dept.Update.class) @RequestBody Dept resources){
        deptService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("Delete department")
    @ApiOperation("Delete Department")
    @DeleteMapping
    @PreAuthorize("@ddt.check('dept:del')")
    public ResponseEntity<Object> delete(@RequestBody Set<Long> ids){
        Set<DeptDto> deptDtos = new HashSet<>();
        for (Long id: ids) {
            List<Dept> deptList = deptService.findByPid(id);
            deptDtos.add(deptService.findById(id));
            if(CollectionUtil.isNotEmpty(deptList)){
                deptDtos = deptService.getDeleteDepts(deptList, deptDtos);
            }
        }
        try {
            deptService.delete(deptDtos);
        }catch (Throwable e){
            ThrowableUtil.throwForeignKeyException(e, "There is a position or role association in the selected department, please cancel the association and try again");
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
