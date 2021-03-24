package vn.thuephonghoc.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import vn.thuephonghoc.annotation.AnonymousAccess;
import vn.thuephonghoc.aop.log.Log;
import vn.thuephonghoc.entity.server.Server;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Server monitoring
 *
 */
@RestController
@RequestMapping("/api/monitor/server")
@Api(tags = "Monitoring: Server Monitoring")
public class ServerController{

    @GetMapping
    @PreAuthorize("@ddt.check('server:list')")
    @ApiOperation("Query Server Monitoring")
    @Log("Query server monitoring")
    public ResponseEntity<Object> get() throws Exception {
        Server server = new Server();
        server.copyTo();
        return new ResponseEntity<>(server,HttpStatus.OK);
    }

}
