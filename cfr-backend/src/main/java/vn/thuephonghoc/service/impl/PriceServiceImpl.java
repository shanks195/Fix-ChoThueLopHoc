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

import vn.thuephonghoc.dto.PriceDto;
import vn.thuephonghoc.entity.Price;
import vn.thuephonghoc.mapper.PriceMapper;
import vn.thuephonghoc.query.PriceQueryCriteria;
import vn.thuephonghoc.repository.PriceRepository;
import vn.thuephonghoc.service.PriceService;
import vn.thuephonghoc.utils.FileUtil;
import vn.thuephonghoc.utils.PageUtil;
import vn.thuephonghoc.utils.QueryHelp;
import vn.thuephonghoc.utils.ValidationUtil;

@Service
@CacheConfig(cacheNames = "price")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class PriceServiceImpl implements PriceService {

	private final PriceRepository priceRepository;
	
	private final PriceMapper priceMapper;
	
	public PriceServiceImpl(PriceRepository priceRepository, PriceMapper priceMapper) {
		this.priceRepository = priceRepository;
		this.priceMapper = priceMapper;
	}

	@Override
	@Cacheable
	public Map<String, Object> queryAll(PriceQueryCriteria criteria, Pageable pageable) {
		Page<Price> page = priceRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
		return PageUtil.toPage(page.map(priceMapper::toDto));
	}

	@Override
	@Cacheable
	public List<PriceDto> queryAll(PriceQueryCriteria criteria) {
		return priceMapper.toDto(priceRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
	}

	@Override
	@Cacheable(key = "#p0")
	public PriceDto findById(Long id) {
		Price price = priceRepository.findById(id).orElseGet(Price::new);
		ValidationUtil.isNull(price.getId(), "Price", "id", id);
		return priceMapper.toDto(price);
	}

	@Override
	@CacheEvict(allEntries = true)
	@Transactional(rollbackFor = Exception.class)
	public PriceDto create(Price resources) {
		return priceMapper.toDto(priceRepository.save(resources));
	}

	@Override
	@CacheEvict(allEntries = true)
    @Transactional(rollbackFor = Exception.class)
	public void update(Price resources) {
		Price price = priceRepository.findById(resources.getId()).orElseGet(Price::new);
		ValidationUtil.isNull(price.getId(), "Price", "id", resources.getId());
		price.copy(resources);
		priceRepository.save(price);
	}

	@Override
	public void download(List<PriceDto> all, HttpServletResponse response) throws IOException {
		List<Map<String, Object>> list = new ArrayList<>();
		for (PriceDto price : all) {
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
			priceRepository.deleteById(id);
		}
	}
	
	
}
