package vn.thuephonghoc.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import vn.thuephonghoc.annotation.AnonymousAccess;
import vn.thuephonghoc.annotation.Limit;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Interface current limit test
 */
@RestController
@RequestMapping("/api/limit")
@Api(tags = "System: Current Limit Test Management")
public class LimitController {
    private static final AtomicInteger ATOMIC_INTEGER = new AtomicInteger();

    /**
     * Test current limit annotation, the following configuration shows that the interface can only be accessed 10 times in 60 seconds, and the key saved to redis is limit_test,
     */
    @Limit(key = "test", period = 60, count = 10, name = "testLimit", prefix = "limit")
    @GetMapping
    @ApiOperation("Test")
    @AnonymousAccess
    public int testLimit() {
        return ATOMIC_INTEGER.incrementAndGet();
    }
}

