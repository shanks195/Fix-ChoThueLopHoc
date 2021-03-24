package vn.thuephonghoc.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import vn.thuephonghoc.dto.ContactDto;
import vn.thuephonghoc.entity.Contact;
import vn.thuephonghoc.mapper.ContactMapper;
import vn.thuephonghoc.query.ContactQueryCriteria;
import vn.thuephonghoc.repository.ContactRepository;
import vn.thuephonghoc.service.ContactService;
import vn.thuephonghoc.utils.FileUtil;
import vn.thuephonghoc.utils.PageUtil;
import vn.thuephonghoc.utils.QueryHelp;
import vn.thuephonghoc.utils.ValidationUtil;

import org.springframework.transaction.annotation.Propagation;

@Service
@CacheConfig(cacheNames = "contact")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class ContactServiceImpl implements ContactService {

	private final ContactRepository contactRepository;
	
	private final ContactMapper contactMapper;
	
	
	public ContactServiceImpl(ContactRepository contactRepository, ContactMapper contactMapper ) {
		this.contactRepository = contactRepository;
		this.contactMapper = contactMapper;
	}
	@Override
	@Cacheable
	public Map<String, Object> queryAll(ContactQueryCriteria criteria, Pageable pageable) {
		Page<Contact> page = contactRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
		return PageUtil.toPage(page.map(contactMapper::toDto));
	}

	@Override
	@Cacheable
	public List<ContactDto> queryAll(ContactQueryCriteria criteria) {
		return contactMapper.toDto(contactRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
	}

	@Override
	@Cacheable(key = "#p0")
	public ContactDto findById(Long id) {
		Contact contact = contactRepository.findById(id).orElseGet(Contact::new);
		ValidationUtil.isNull(contact.getId(), "Contact", "id", id);
		return contactMapper.toDto(contact);
	}

	@Override
	@CacheEvict(allEntries = true)
	@Transactional(rollbackFor = Exception.class)
	public ContactDto create(Contact resources) {
		return contactMapper.toDto(contactRepository.save(resources));
	}

	@Override
	@CacheEvict(allEntries = true)
    @Transactional(rollbackFor = Exception.class)
	public void update(Contact resources) {
		Contact contact = contactRepository.findById(resources.getId()).orElseGet(Contact::new);
		ValidationUtil.isNull(contact.getId(), "Contact", "id", resources.getId());
		contact.copy(resources);
		contactRepository.save(contact);
	}

	@Override
	public void download(List<ContactDto> all, HttpServletResponse response) throws IOException {
		
		List<Map<String, Object>> list = new ArrayList<>();
		for (ContactDto contact : all) {
			Map<String,Object> map = new LinkedHashMap<>();
			
			map.put("Title", contact.getTitle());
			map.put("Description", contact.getDescription());
			map.put("Phone", contact.getPhone());
			map.put("Email", contact.getEmail());
			list.add(map);
		}
		FileUtil.downloadExcel(list, response);
	}

	@Override
	@CacheEvict(allEntries = true)
	public void deleteAll(Long[] ids) {
		for(Long id : ids) {
			contactRepository.deleteById(id);
		}
	}

}
