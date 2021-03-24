package vn.thuephonghoc.service;

import org.springframework.data.domain.Pageable;

import vn.thuephonghoc.dto.DictDto;
import vn.thuephonghoc.entity.Dict;
import vn.thuephonghoc.query.DictQueryCriteria;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface DictService {

    /**
     * @param criteria
     * @param pageable
     * @return /
     */
    Map<String,Object> queryAll(DictQueryCriteria criteria, Pageable pageable);

    /**
     * @param dict /
     * @return /
     */
    List<DictDto> queryAll(DictQueryCriteria dict);

    /**
     * @param id /
     * @return /
     */
    DictDto findById(Long id);

    /**
     * @param resources /
     * @return /
     */
    DictDto create(Dict resources);

    /**
     * @param resources /
     */
    void update(Dict resources);

    /**
     * @param ids /
     */
    void delete(Set<Long> ids);

    /**
     * @param queryAll
     * @param response /
     * @throws IOException /
     */
    void download(List<DictDto> queryAll, HttpServletResponse response) throws IOException;

}
