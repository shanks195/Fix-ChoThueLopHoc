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
import vn.thuephonghoc.entity.Price;
import vn.thuephonghoc.query.PriceQueryCriteria;
import vn.thuephonghoc.service.PriceService;



@Api(tags = "Client: Price Management")
@RestController
@RequestMapping("/api/price")
public class PriceController {

	private final PriceService priceService;
	
	public PriceController(PriceService priceService) {
		this.priceService = priceService;
	}
	
	@Log("Export data")
	@ApiOperation("Export data")
	@GetMapping(value = "/download")
	@PreAuthorize("@ddt.check('price:download')")
	public void download(HttpServletResponse response, PriceQueryCriteria criteria) throws IOException {
		priceService.download(priceService.queryAll(criteria), response);
	}

	@Log("Query Price")
	@ApiOperation("Query Price")
	@GetMapping()
	public ResponseEntity<Object> getIntroduce(PriceQueryCriteria criteria, Pageable pageable) {
		return new ResponseEntity<>(priceService.queryAll(criteria, pageable), HttpStatus.OK);
	}

	@Log("Add Price")
	@ApiOperation("Add Price")
	@PostMapping
	@PreAuthorize("@ddt.check('price:add')")
	public ResponseEntity<Object> create(@Validated @RequestBody Price resources) {
		return new ResponseEntity<>(priceService.create(resources), HttpStatus.CREATED);
	}

	@Log("Modify Price")
	@ApiOperation("Modify Price")
	@PutMapping
	@PreAuthorize("@ddt.check('price:edit')")
	public ResponseEntity<Object> update(@Validated @RequestBody Price resources) {
		priceService.update(resources);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	@Log("Delete Price")
	@ApiOperation("Delete Price")
	@PreAuthorize("@ddt.check('intro:del')")
	@DeleteMapping
	public ResponseEntity<Object> deleteAll(@RequestBody Long[] ids) {
		priceService.deleteAll(ids);
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
