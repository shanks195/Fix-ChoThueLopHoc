package vn.thuephonghoc.service;

import org.springframework.data.domain.Pageable;

import vn.thuephonghoc.dto.DatabaseDto;
import vn.thuephonghoc.entity.Database;
import vn.thuephonghoc.query.DatabaseQueryCriteria;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Set;

public interface DatabaseService {

    /**
     * @param criteria 
     * @param pageable
     * @return /
     */
    Object queryAll(DatabaseQueryCriteria criteria, Pageable pageable);

    /**
     * @param criteria
     * @return /
     */
    List<DatabaseDto> queryAll(DatabaseQueryCriteria criteria);

    /**
     * @param id /
     * @return /
     */
    DatabaseDto findById(String id);

    /**
     * @param resources /
     * @return /
     */
    DatabaseDto create(Database resources);

    /**
     * @param resources /
     */
    void update(Database resources);

    /**
     * @param ids /
     */
    void delete(Set<String> ids);


    /**
     * @param resources
     * @return
     */
    boolean testConnection(Database resources);

    /**
     * @param queryAll /
     * @param response /
     */
    void download(List<DatabaseDto> queryAll, HttpServletResponse response) throws IOException;
}
