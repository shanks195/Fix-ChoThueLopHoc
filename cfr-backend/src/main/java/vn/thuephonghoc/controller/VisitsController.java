package vn.thuephonghoc.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import vn.thuephonghoc.service.VisitsService;
import vn.thuephonghoc.utils.RequestHolder;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/visits")
@Api(tags = "System: Access Record Management")
public class VisitsController {

    private final VisitsService visitsService;

    public VisitsController(VisitsService visitsService) {
        this.visitsService = visitsService;
    }

    @PostMapping
    @ApiOperation("Create Access Record")
    public ResponseEntity<Object> create(){
        visitsService.count(RequestHolder.getHttpServletRequest());
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping
    @ApiOperation("Query")
    public ResponseEntity<Object> get(){
        return new ResponseEntity<>(visitsService.get(),HttpStatus.OK);
    }

    @GetMapping(value = "/chartData")
    @ApiOperation("Query chart data")
    public ResponseEntity<Object> getChartData(){
        return new ResponseEntity<>(visitsService.getChartData(),HttpStatus.OK);
    }
}

