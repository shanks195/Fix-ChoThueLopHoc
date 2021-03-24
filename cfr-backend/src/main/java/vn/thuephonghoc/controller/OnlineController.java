package vn.thuephonghoc.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import vn.thuephonghoc.aop.log.Log;
import vn.thuephonghoc.service.OnlineUserService;
import vn.thuephonghoc.utils.EncryptUtils;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;

@RestController
@RequestMapping("/auth/online")
@Api(tags = "System: Online User Management")
public class OnlineController {

    private final OnlineUserService onlineUserService;

    public OnlineController(OnlineUserService onlineUserService) {
        this.onlineUserService = onlineUserService;
    }

    @Log("Check online users")
    @ApiOperation("Query online users")
    @GetMapping
    @PreAuthorize("@ddt.check()")
    public ResponseEntity<Object> getAll(String filter, Pageable pageable){
        return new ResponseEntity<>(onlineUserService.getAll(filter, pageable), HttpStatus.OK);
    }

    @Log("Export data")
    @ApiOperation("Export data")
    @GetMapping(value = "/download")
    @PreAuthorize("@ddt.check()")
    public void download(HttpServletResponse response, String filter) throws IOException {
        onlineUserService.download(onlineUserService.getAll(filter), response);
    }

    @Log("Kick out the user")
    @ApiOperation("Kick out user")
    @DeleteMapping
    @PreAuthorize("@ddt.check()")
    public ResponseEntity<Object> delete(@RequestBody Set<String> keys) throws Exception {
        for (String key: keys) {
            // Decryption key
            key = EncryptUtils.desDecrypt(key);
            onlineUserService.kickOut(key);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
