package vn.thuephonghoc.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.*;
import vn.thuephonghoc.aop.log.Log;
import vn.thuephonghoc.entity.Home;
import vn.thuephonghoc.query.HomeQueryCriteria;
import vn.thuephonghoc.service.HomeService;


@Api(tags = "Client: Home Management")
@RestController
@RequestMapping("/api/home")
public class HomeController {

	private final HomeService homeService;
	
	public HomeController( HomeService homeService) {
		this.homeService = homeService;
	}
	
	@Log("Export data")
	@ApiOperation("Export data")
	@GetMapping(value = "/download")
	@PreAuthorize("@ddt.check('home:download')")
	public void download(HttpServletResponse response, HomeQueryCriteria criteria) throws IOException {
		homeService.download(homeService.queryAll(criteria), response);
	}

	@Log("Query Home")
	@ApiOperation("Query Home")
	@GetMapping()
	public ResponseEntity<Object> getIntroduce(HomeQueryCriteria criteria, Pageable pageable) {
		return new ResponseEntity<>(homeService.queryAll(criteria, pageable), HttpStatus.OK);
	}

	@Log("Add Home")
	@ApiOperation("Add Home")
	@PostMapping
	@PreAuthorize("@ddt.check('home:add')")
	public ResponseEntity<Object> create(@Validated @RequestBody Home resources) {
		return new ResponseEntity<>(homeService.create(resources), HttpStatus.CREATED);
	}

	@Log("Modify Home")
	@ApiOperation("Modify Home")
	@PutMapping
	@PreAuthorize("@ddt.check('home:edit')")
	public ResponseEntity<Object> update(@Validated @RequestBody Home resources) {
		homeService.update(resources);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	@Log("Delete Home")
	@ApiOperation("Delete Home")
	@PreAuthorize("@ddt.check('home:del')")
	@DeleteMapping
	public ResponseEntity<Object> deleteAll(@RequestBody Long[] ids) {
		homeService.deleteAll(ids);
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
