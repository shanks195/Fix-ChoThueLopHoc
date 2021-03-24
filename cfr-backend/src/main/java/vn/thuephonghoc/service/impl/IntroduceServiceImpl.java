package vn.thuephonghoc.service.impl;
import vn.thuephonghoc.dto.IntroduceDto;
import vn.thuephonghoc.entity.Introduce;
import vn.thuephonghoc.mapper.IntroduceMapper;
import vn.thuephonghoc.query.IntroduceQueryCriteria;
import vn.thuephonghoc.repository.IntroduceRepository;
import vn.thuephonghoc.service.IntroduceService;
import vn.thuephonghoc.utils.*;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.servlet.http.HttpServletResponse;


@Service
@CacheConfig(cacheNames = "localStorage")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class IntroduceServiceImpl implements IntroduceService {

	private final IntroduceRepository introduceRepository;
	
	private final IntroduceMapper introduceMapper;

	
  
    public  IntroduceServiceImpl(IntroduceRepository introduceRepository, IntroduceMapper introduceMapper) {
    	this.introduceRepository = introduceRepository;
    	this.introduceMapper = introduceMapper;
    }

	@Override
	@Cacheable
	public Map<String, Object> queryAll(IntroduceQueryCriteria criteria, Pageable pageable) {
		Page<Introduce> page = introduceRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
		return PageUtil.toPage(page.map(introduceMapper::toDto));
	}

	@Override
	@Cacheable
	public List<IntroduceDto> queryAll(IntroduceQueryCriteria criteria) {
		List<Introduce> introduce = introduceRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder));
		return introduceMapper.toDto(introduce);
	}

	@Override
	@Cacheable(key = "#p0")
	public IntroduceDto findById(Long id) {
		Introduce introduce = introduceRepository.findById(id).orElseGet(Introduce::new);
		ValidationUtil.isNull(introduce.getId(), "Introduce", "id", id);
		return introduceMapper.toDto(introduce);
	}

	@Override
	@CacheEvict(allEntries = true)
	@Transactional(rollbackFor = Exception.class)
	public IntroduceDto create(Introduce resources) {
		return introduceMapper.toDto(introduceRepository.save(resources));
	}

	@Override
	@CacheEvict(allEntries = true)
    @Transactional(rollbackFor = Exception.class)
	public void update(Introduce resources) {
		Introduce introduce = introduceRepository.findById(resources.getId()).orElseGet(Introduce::new);
		ValidationUtil.isNull(introduce.getId(), "Introduce", "id", resources.getId());
		introduce.copy(resources);
		introduceRepository.save(introduce);
	}

	@Override
	public void download(List<IntroduceDto> all, HttpServletResponse response) throws IOException {
		List<Map<String, Object>> list = new ArrayList<>();
		for (IntroduceDto introduce : all) {
			Map<String,Object> map = new LinkedHashMap<>();
			
			map.put("Title", introduce.getTitle());
			map.put("Description", introduce.getDescription());
			map.put("Name", introduce.getName());
			map.put("Link", introduce.getLink());
			list.add(map);
		}
		FileUtil.downloadExcel(list, response);
	}

	@Override
	@CacheEvict(allEntries = true)
	public void deleteAll(Long[] ids) {
		for(Long id : ids) {
			introduceRepository.deleteById(id);
		}
	}


}
