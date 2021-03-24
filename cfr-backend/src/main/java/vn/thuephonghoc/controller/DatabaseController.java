package vn.thuephonghoc.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import vn.thuephonghoc.annotation.AnonymousAccess;
import vn.thuephonghoc.aop.log.Log;
import vn.thuephonghoc.dto.DatabaseDto;
import vn.thuephonghoc.entity.Database;
import vn.thuephonghoc.exception.BadRequestException;
import vn.thuephonghoc.query.DatabaseQueryCriteria;
import vn.thuephonghoc.service.DatabaseService;
import vn.thuephonghoc.utils.FileUtil;
import vn.thuephonghoc.utils.SqlUtils;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.Set;

@Api(tags = "Database Management")
@RestController
@RequestMapping("/api/database")
public class DatabaseController {

    private String fileSavePath = System.getProperty("java.io.tmpdir");

    private final DatabaseService databaseService;

    public DatabaseController(DatabaseService databaseService) {
        this.databaseService = databaseService;
    }

    @Log("Export database data")
    @ApiOperation("Export database data")
    @GetMapping(value = "/download")
    @PreAuthorize("@ddt.check('database:download')")
    public void download(HttpServletResponse response, DatabaseQueryCriteria criteria) throws IOException {
        databaseService.download(databaseService.queryAll(criteria), response);
    }


    @Log("Query database")
    @ApiOperation(value = "Query database")
    @GetMapping
    @PreAuthorize("@ddt.check('database:list')")
    public ResponseEntity<Object> getDatabases(DatabaseQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(databaseService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @Log("New Database")
    @ApiOperation(value = "New database")
    @PostMapping
    @PreAuthorize("@ddt.check('database:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody Database resources){
        return new ResponseEntity<>(databaseService.create(resources),HttpStatus.CREATED);
    }

    @Log("Modify Database")
    @ApiOperation(value = "Modify database")
    @PutMapping
    @PreAuthorize("@ddt.check('database:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody Database resources){
        databaseService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("Delete database")
    @ApiOperation(value = "Delete database")
    @DeleteMapping
    @PreAuthorize("@ddt.check('database:del')")
    public ResponseEntity<Object> delete(@RequestBody Set<String> ids){
        databaseService.delete(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Log("Test database link")
    @ApiOperation(value = "Test database link")
    @PostMapping("/testConnect")
    @PreAuthorize("@ddt.check('database:testConnect')")
    public ResponseEntity<Object> testConnect(@Validated @RequestBody Database resources){
        return new ResponseEntity<>(databaseService.testConnection(resources),HttpStatus.CREATED);
    }

    @Log("Execute SQL script")
    @ApiOperation(value = "Execute SQL script")
    @PostMapping(value = "/upload")
// @PreAuthorize("@ddt.check('database:add')")
    @AnonymousAccess
    public ResponseEntity<Object> upload(@RequestBody MultipartFile file, HttpServletRequest request)throws Exception{
        String id = request.getParameter("id");
        DatabaseDto database = databaseService.findById(id);
        String fileName;
        if(database != null){
            fileName = file.getOriginalFilename();
            File executeFile = new File(fileSavePath+fileName);
            FileUtil.del(executeFile);
            file.transferTo(executeFile);
            String result = SqlUtils.executeFile(database.getJdbcUrl(), database.getUserName(), database.getPwd(), executeFile);
            return new ResponseEntity<>(result,HttpStatus.OK);
        }else{
            throw new BadRequestException("Database not exist");
        }
    }
}
