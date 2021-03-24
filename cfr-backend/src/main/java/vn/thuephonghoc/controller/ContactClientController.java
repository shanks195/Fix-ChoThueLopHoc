package vn.thuephonghoc.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import vn.thuephonghoc.aop.log.Log;
import vn.thuephonghoc.entity.ContactClient;
import vn.thuephonghoc.query.ContactClientQueryCriteria;
import vn.thuephonghoc.service.ContactClientService;

@Api(tags = "Client: ContactClient Management")
@RestController
@RequestMapping("/api/contactclient")
public class ContactClientController {

	public final ContactClientService contactClientService;
	
	public ContactClientController( ContactClientService contactClientService) {
		this.contactClientService = contactClientService;
		
	}
	
	@Log("Export data")
	@ApiOperation("Export data")
	@GetMapping(value = "/download")
	@PreAuthorize("@ddt.check('contactc:download')")
	public void download(HttpServletResponse response, ContactClientQueryCriteria criteria) throws IOException {
		contactClientService.download(contactClientService.queryAll(criteria), response);
	}

	@Log("Query Contact")
	@ApiOperation("Query Contact")
	@GetMapping()
	public ResponseEntity<Object> getContact(ContactClientQueryCriteria criteria, Pageable pageable) {
		return new ResponseEntity<>(contactClientService.queryAll(criteria, pageable), HttpStatus.OK);
	}

	@Log("Add Contact")
	@ApiOperation("Add Contact")
	@PostMapping
	public ResponseEntity<Object> create(@Validated @RequestBody ContactClient resources) {
		return new ResponseEntity<>(contactClientService.create(resources), HttpStatus.CREATED);
	}
	
	@Log("Modify Contact")
	@ApiOperation("Modify Contact")
	@PutMapping
	@PreAuthorize("@ddt.check('contactc:edit')")
	public ResponseEntity<Object> update(@Validated @RequestBody ContactClient resources) {
		contactClientService.update(resources);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	@Log("Delete Contact")
	@ApiOperation("Delete Contact")
	@PreAuthorize("@ddt.check('contactc:del')")
	@DeleteMapping
	public ResponseEntity<Object> deleteAll(@RequestBody Long[] ids) {
		contactClientService.deleteAll(ids);
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
