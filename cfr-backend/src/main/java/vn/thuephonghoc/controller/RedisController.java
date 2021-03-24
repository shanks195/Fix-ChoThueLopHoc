package vn.thuephonghoc.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import vn.thuephonghoc.aop.log.Log;
import vn.thuephonghoc.service.RedisService;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;

@RestController
@RequestMapping("/api/redis")
@Api(tags = "System: Redis cache management")
public class RedisController {

    private final RedisService redisService;

    public RedisController(RedisService redisService) {
        this.redisService = redisService;
    }

    @Log("Query Redis cache")
    @GetMapping
    @ApiOperation("Query Redis Cache")
    @PreAuthorize("@ddt.check('redis:list')")
    public ResponseEntity getRedis(String key, Pageable pageable){
        return new ResponseEntity<>(redisService.findByKey(key,pageable), HttpStatus.OK);
    }

    @Log("Export data")
    @ApiOperation("Export data")
    @GetMapping(value = "/download")
    @PreAuthorize("@ddt.check('redis:download')")
    public void download(HttpServletResponse response, String key) throws IOException {
        redisService.download(redisService.findByKey(key), response);
    }

    @Log("Delete Redis cache")
    @DeleteMapping
    @ApiOperation("Delete Redis Cache")
    @PreAuthorize("@ddt.check('redis:del')")
    public ResponseEntity<Object> delete(@RequestBody Set<String> ids){
        redisService.delete(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}

