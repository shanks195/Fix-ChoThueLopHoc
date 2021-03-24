package vn.thuephonghoc.service;

import org.springframework.data.domain.Pageable;

import vn.thuephonghoc.dto.DictDetailDto;
import vn.thuephonghoc.entity.DictDetail;
import vn.thuephonghoc.query.DictDetailQueryCriteria;

import java.util.Map;

public interface DictDetailService {

    /**
     * @param id /
     * @return /
     */
    DictDetailDto findById(Long id);

    /**
     * @param resources /
     * @return /
     */
    DictDetailDto create(DictDetail resources);

    /**
     * @param resources /
     */
    void update(DictDetail resources);

    /**
     * @param id /
     */
    void delete(Long id);

    /**
     * @param criteria
     * @param pageable
     * @return /
     */
    Map queryAll(DictDetailQueryCriteria criteria, Pageable pageable);
}
