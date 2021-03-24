package vn.thuephonghoc.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import vn.thuephonghoc.aop.log.Log;
import vn.thuephonghoc.entity.Picture;
import vn.thuephonghoc.query.PictureQueryCriteria;
import vn.thuephonghoc.service.PictureService;
import vn.thuephonghoc.utils.SecurityUtils;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequestMapping("/api/pictures")
@Api(tags = "Tool: Free Image Bed Management")
public class PictureController {

    private final PictureService pictureService;

    public PictureController(PictureService pictureService) {
        this.pictureService = pictureService;
    }

    @Log("Query picture")
    @PreAuthorize("@ddt.check('pictures:list')")
    @GetMapping()
    @ApiOperation("Query Image")
    public ResponseEntity<Object> getRoles(PictureQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(pictureService.queryAll(criteria,pageable), HttpStatus.OK);
    }

    @Log("Export data")
    @ApiOperation("Export data")
    @GetMapping(value = "/download")
    @PreAuthorize("@ddt.check('pictures:download')")
    public void download(HttpServletResponse response, PictureQueryCriteria criteria) throws IOException {
        pictureService.download(pictureService.queryAll(criteria), response);
    }

    @Log("Upload picture")
    @PreAuthorize("@ddt.check('pictures:add')")
    @PostMapping
    @ApiOperation("Upload image")
    public ResponseEntity<Object> upload(@RequestParam MultipartFile file){
        String userName = SecurityUtils.getCurrentUsername();
        Picture picture = pictureService.upload(file,userName);
        return new ResponseEntity<>(picture,HttpStatus.OK);
    }

    @Log("Synchronize image bed data")
    @ApiOperation("Synchronize Image Bed Data")
    @PostMapping(value = "/synchronize")
    public ResponseEntity<Object> synchronize(){
        pictureService.synchronize();
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Log("Multiple selection to delete pictures")
    @ApiOperation("Multiple selection to delete pictures")
    @PreAuthorize("@ddt.check('pictures:del')")
    @DeleteMapping
    public ResponseEntity<Object> deleteAll(@RequestBody Long[] ids) {
        pictureService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}

