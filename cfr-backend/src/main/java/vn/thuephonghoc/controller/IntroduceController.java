package vn.thuephonghoc.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.*;
import vn.thuephonghoc.aop.log.Log;
import vn.thuephonghoc.entity.Introduce;
import vn.thuephonghoc.query.IntroduceQueryCriteria;
import vn.thuephonghoc.service.IntroduceService;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Api(tags = "Client: Introduce Management")
@RestController
@RequestMapping("/api/introduce")
public class IntroduceController {

	private final IntroduceService introduceService;
	
	public IntroduceController(IntroduceService introduceService) {
		this.introduceService = introduceService;
	}
	
	@Log("Export data")
	@ApiOperation("Export data")
	@GetMapping(value = "/download")
	@PreAuthorize("@ddt.check('intro:download')")
	public void download(HttpServletResponse response, IntroduceQueryCriteria criteria) throws IOException {
		introduceService.download(introduceService.queryAll(criteria), response);
	}

	@Log("Query Introduce")
	@ApiOperation("Query Introduce")
	@GetMapping()
	public ResponseEntity<Object> getIntroduce(IntroduceQueryCriteria criteria, Pageable pageable) {
		return new ResponseEntity<>(introduceService.queryAll(criteria, pageable), HttpStatus.OK);
	}

	@Log("Add Introduce")
	@ApiOperation("Add Introduce")
	@PostMapping
	@PreAuthorize("@ddt.check('intro:add')")
	public ResponseEntity<Object> create(@Validated @RequestBody Introduce resources) {
		return new ResponseEntity<>(introduceService.create(resources), HttpStatus.CREATED);
	}

	@Log("Modify Introduce")
	@ApiOperation("Modify Introduce")
	@PutMapping
	@PreAuthorize("@ddt.check('intro:edit')")
	public ResponseEntity<Object> update(@Validated @RequestBody Introduce resources) {
		introduceService.update(resources);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	@Log("Delete Introduce")
	@ApiOperation("Delete Introduce")
	@PreAuthorize("@ddt.check('intro:del')")
	@DeleteMapping
	public ResponseEntity<Object> deleteAll(@RequestBody Long[] ids) {
		introduceService.deleteAll(ids);
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
