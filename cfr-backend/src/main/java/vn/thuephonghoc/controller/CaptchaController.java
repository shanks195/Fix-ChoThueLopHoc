package vn.thuephonghoc.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.*;
import vn.thuephonghoc.aop.log.Log;
import vn.thuephonghoc.entity.Captcha;
import vn.thuephonghoc.query.CaptchaQueryCriteria;
import vn.thuephonghoc.service.CaptchaService;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@Api(tags = "captcha management")
@RestController
@RequestMapping("/api/captcha")
public class CaptchaController {

    private final CaptchaService captchaService;

    public CaptchaController(CaptchaService captchaService) {
        this.captchaService = captchaService;
    }

    @Log("Export data")
    @ApiOperation("Export data")
    @GetMapping(value = "/download")
    @PreAuthorize("@ddt.check('captcha:download')")
    public void download(HttpServletResponse response, CaptchaQueryCriteria criteria) throws IOException {
        captchaService.download(captchaService.queryAll(criteria), response);
    }

    @Log("Query captcha")
    @ApiOperation("Query captcha")
    @GetMapping()
    @PreAuthorize("@ddt.check('captcha:list')")
    public ResponseEntity<Object> getCaptchas(CaptchaQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(captchaService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @Log("Add captcha")
    @ApiOperation("Add captcha")
    @PostMapping
    @PreAuthorize("@ddt.check('captcha:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody Captcha resources){
        return new ResponseEntity<>(captchaService.create(resources),HttpStatus.CREATED);
    }

    @Log("Modify captcha")
    @ApiOperation("Modify captcha")
    @PutMapping
    @PreAuthorize("@ddt.check('captcha:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody Captcha resources){
    captchaService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("Delete captcha")
    @ApiOperation("Delete captcha")
    @DeleteMapping(value = "/{id}")
    @PreAuthorize("@ddt.check('captcha:del')")
    public ResponseEntity<Object> delete(@PathVariable Long id){
    captchaService.delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @Log("Multiple selection to delete captcha")
    @ApiOperation("Multi-select delete captcha")
    @PreAuthorize("@ddt.check('captcha:del')")
    @DeleteMapping
    public ResponseEntity<Object> deleteAll(@RequestBody Long[] ids) {
    captchaService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
