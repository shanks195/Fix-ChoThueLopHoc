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

import vn.thuephonghoc.dto.ContactClientDto;
import vn.thuephonghoc.entity.ContactClient;
import vn.thuephonghoc.mapper.ContactClientMapper;
import vn.thuephonghoc.query.ContactClientQueryCriteria;
import vn.thuephonghoc.repository.ContactClientRepository;
import vn.thuephonghoc.service.ContactClientService;
import vn.thuephonghoc.utils.FileUtil;
import vn.thuephonghoc.utils.PageUtil;
import vn.thuephonghoc.utils.QueryHelp;
import vn.thuephonghoc.utils.ValidationUtil;

import org.springframework.transaction.annotation.Propagation;

@Service
@CacheConfig(cacheNames = "contact")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class ContactClientServiceImpl implements ContactClientService {
	
	private final ContactClientRepository contactClientRepository;
	
	private final ContactClientMapper contactClientMapper;
	
	public ContactClientServiceImpl(ContactClientRepository contactClientRepository, ContactClientMapper contactClientMapper) {
	 this.contactClientRepository = contactClientRepository;
	 this.contactClientMapper = contactClientMapper;
	}

	@Override
	@Cacheable
	public Map<String, Object> queryAll(ContactClientQueryCriteria criteria, Pageable pageable) {
		Page<ContactClient> page = contactClientRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
		return PageUtil.toPage(page.map(contactClientMapper::toDto));
	}

	@Override
	@Cacheable
	public List<ContactClientDto> queryAll(ContactClientQueryCriteria criteria) {
		return contactClientMapper.toDto(contactClientRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
	}

	@Override
	@Cacheable(key = "#p0")
	public ContactClientDto findById(Long id) {
		ContactClient contactClient = contactClientRepository.findById(id).orElseGet(ContactClient::new);
		ValidationUtil.isNull(contactClient.getId(), "ContactClient", "id", id);
		return contactClientMapper.toDto(contactClient) ;
	}

	@Override
	@CacheEvict(allEntries = true)
	@Transactional(rollbackFor = Exception.class)
	public ContactClientDto create(ContactClient resources) {
		return contactClientMapper.toDto(contactClientRepository.save(resources));
	}

	@Override
	@CacheEvict(allEntries = true)
    @Transactional(rollbackFor = Exception.class)
	public void update(ContactClient resources) {
		ContactClient contactClient = contactClientRepository.findById(resources.getId()).orElseGet(ContactClient::new);
		ValidationUtil.isNull(contactClient.getId(), "ContactClient", "id", resources.getId());
		contactClient.copy(resources);
		contactClientRepository.save(contactClient);
	}

	@Override
	public void download(List<ContactClientDto> all, HttpServletResponse response) throws IOException {
		List<Map<String, Object>> list = new ArrayList<>();
		for (ContactClientDto contact : all) {
			Map<String,Object> map = new LinkedHashMap<>();
			map.put("Fullname", contact.getFullname());
			map.put("Phone", contact.getPhone());
			map.put("Email", contact.getEmail());
			map.put("Schedule", contact.getSchedule());
			list.add(map);
		}
		FileUtil.downloadExcel(list, response);
	}

	@Override
	public void deleteAll(Long[] ids) {
		for(Long id : ids) {
			contactClientRepository.deleteById(id);
		}
		
	}

}
