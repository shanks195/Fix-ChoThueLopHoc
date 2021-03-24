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
import vn.thuephonghoc.entity.Contact;
import vn.thuephonghoc.query.ContactQueryCriteria;
import vn.thuephonghoc.service.ContactService;

@Api(tags = "Client: Contact Management")
@RestController
@RequestMapping("/api/contact")
public class ContactController {

	private final ContactService contactService;

	public ContactController(ContactService contactService) {
		this.contactService = contactService;
	}

	@Log("Export data")
	@ApiOperation("Export data")
	@GetMapping(value = "/download")
	@PreAuthorize("@ddt.check('contact:download')")
	public void download(HttpServletResponse response, ContactQueryCriteria criteria) throws IOException {
		contactService.download(contactService.queryAll(criteria), response);
	}

	@Log("Query Contact")
	@ApiOperation("Query Contact")
	@GetMapping()
	public ResponseEntity<Object> getContact(ContactQueryCriteria criteria, Pageable pageable) {
		return new ResponseEntity<>(contactService.queryAll(criteria, pageable), HttpStatus.OK);
	}

	@Log("Add Contact")
	@ApiOperation("Add Contact")
	@PostMapping
	@PreAuthorize("@ddt.check('contact:add')")
	public ResponseEntity<Object> create(@Validated @RequestBody Contact resources) {
		return new ResponseEntity<>(contactService.create(resources), HttpStatus.CREATED);
	}
	
	@Log("Modify Contact")
	@ApiOperation("Modify Contact")
	@PutMapping
	@PreAuthorize("@ddt.check('contact:edit')")
	public ResponseEntity<Object> update(@Validated @RequestBody Contact resources) {
		contactService.update(resources);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	@Log("Delete Contact")
	@ApiOperation("Delete Contact")
	@PreAuthorize("@ddt.check('contact:del')")
	@DeleteMapping
	public ResponseEntity<Object> deleteAll(@RequestBody Long[] ids) {
		contactService.deleteAll(ids);
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
