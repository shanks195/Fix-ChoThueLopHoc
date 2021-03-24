package ${package}.controller;

import vn.thuephonghoc.aop.log.Log;
import ${package}.entity.${className};
import ${package}.service.${className}Service;
import ${package}.query.${className}QueryCriteria;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@Api(tags = "${apiAlias}")
@RestController
@RequestMapping("/api/${changeClassName}")
public class ${className}Controller {

    private final ${className}Service ${changeClassName}Service;

    public ${className}Controller(${className}Service ${changeClassName}Service) {
        this.${changeClassName}Service = ${changeClassName}Service;
    }

	@Log("Export data")
     @ApiOperation("Export data")
    @GetMapping(value = "/download")
    @PreAuthorize("@dokit.check('${changeClassName}:list')")
    public void download(HttpServletResponse response, ${className}QueryCriteria criteria) throws IOException {
        ${changeClassName}Service.download(${changeClassName}Service.queryAll(criteria), response);
    }

	@Log("Query ${apiAlias}")
     @ApiOperation("Query ${apiAlias}")
    @GetMapping()
    @PreAuthorize("@dokit.check('${changeClassName}:list')")
    public ResponseEntity<Object> get${className}s(${className}QueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(${changeClassName}Service.queryAll(criteria,pageable),HttpStatus.OK);
    }

	@Log("Add ${apiAlias}")
     @ApiOperation("Add ${apiAlias}")
    @PostMapping
    @PreAuthorize("@dokit.check('${changeClassName}:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody ${className} resources){
        return new ResponseEntity<>(${changeClassName}Service.create(resources),HttpStatus.CREATED);
    }

	@Log("Modify ${apiAlias}")
     @ApiOperation("Modify ${apiAlias}")
    @PutMapping
    @PreAuthorize("@dokit.check('${changeClassName}:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody ${className} resources){
    ${changeClassName}Service.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

	@Log("Delete ${apiAlias}")
     @ApiOperation("Delete ${apiAlias}")
    @PreAuthorize("@dokit.check('${changeClassName}:del')")
    @DeleteMapping
    public ResponseEntity<Object> deleteAll(@RequestBody ${pkColumnType}[] ids) {
    ${changeClassName}Service.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
