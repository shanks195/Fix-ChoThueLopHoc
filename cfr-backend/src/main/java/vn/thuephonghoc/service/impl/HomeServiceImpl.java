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
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import vn.thuephonghoc.utils.FileUtil;
import vn.thuephonghoc.utils.PageUtil;
import vn.thuephonghoc.utils.QueryHelp;
import vn.thuephonghoc.utils.ValidationUtil;
import vn.thuephonghoc.dto.HomeDto;
import vn.thuephonghoc.entity.Home;
import vn.thuephonghoc.mapper.HomeMapper;
import vn.thuephonghoc.query.HomeQueryCriteria;
import vn.thuephonghoc.repository.HomeRepository;
import vn.thuephonghoc.service.HomeService;

@Service
@CacheConfig(cacheNames = "home")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class HomeServiceImpl  implements HomeService{
	
	public final HomeRepository homeRepository;
	
	public final HomeMapper homeMapper;
	
	public HomeServiceImpl( HomeRepository homeRepository, HomeMapper homeMapper) {
		this.homeMapper = homeMapper;
		this.homeRepository = homeRepository;
	}

	@Override
	@Cacheable
	public Map<String, Object> queryAll(HomeQueryCriteria criteria, Pageable pageable) {
		Page<Home> page = homeRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
		return PageUtil.toPage(page.map(homeMapper::toDto));
	}

	@Override
	@Cacheable
	public List<HomeDto> queryAll(HomeQueryCriteria criteria) {
		return homeMapper.toDto(homeRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
	}

	@Override
	@Cacheable(key = "#p0")
	public HomeDto findById(Long id) {
		Home home = homeRepository.findById(id).orElseGet(Home::new);
		ValidationUtil.isNull(home.getId(), "Home", "id", id);
		return homeMapper.toDto(home);
	}

	@Override
	@CacheEvict(allEntries = true)
	@Transactional(rollbackFor = Exception.class)
	public HomeDto create(Home resources) {
		return homeMapper.toDto(homeRepository.save(resources));
	}

	@Override
	@CacheEvict(allEntries = true)
    @Transactional(rollbackFor = Exception.class)
	public void update(Home resources) {
		Home home = homeRepository.findById(resources.getId()).orElseGet(Home::new);
		ValidationUtil.isNull(home.getId(), "Home", "id", resources.getId());
		home.copy(resources);
		homeRepository.save(home);
		
	}

	@Override
	public void download(List<HomeDto> all, HttpServletResponse response) throws IOException {
		List<Map<String, Object>> list = new ArrayList<>();
		for (HomeDto price : all) {
			Map<String,Object> map = new LinkedHashMap<>();
			map.put("Title", price.getTitle());
			map.put("Description", price.getDescription());
			map.put("Name", price.getName());
			map.put("Link", price.getLink());
			list.add(map);
		}
		FileUtil.downloadExcel(list, response);
	}

	@Override
	@CacheEvict(allEntries = true)
	public void deleteAll(Long[] ids) {
		for(Long id : ids) {
			homeRepository.deleteById(id);
		}
		
	}

}
