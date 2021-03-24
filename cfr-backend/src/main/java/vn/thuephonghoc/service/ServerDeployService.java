package vn.thuephonghoc.service;

import org.springframework.data.domain.Pageable;

import vn.thuephonghoc.dto.ServerDeployDto;
import vn.thuephonghoc.entity.ServerDeploy;
import vn.thuephonghoc.query.ServerDeployQueryCriteria;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Set;

public interface ServerDeployService {

    /**
     * @param criteria 
     * @param pageable
     * @return /
     */
    Object queryAll(ServerDeployQueryCriteria criteria, Pageable pageable);

    /**
     * @param criteria 
     * @return /
     */
    List<ServerDeployDto> queryAll(ServerDeployQueryCriteria criteria);

    /**
     * @param id /
     * @return /
     */
    ServerDeployDto findById(Long id);

    /**
     * @param resources /
     * @return /
     */
    ServerDeployDto create(ServerDeploy resources);

    /**
     * @param resources /
     */
    void update(ServerDeploy resources);

    /**
     * @param ids /
     */
    void delete(Set<Long> ids);

    /**
     * @param ip /
     * @return /
     */
    ServerDeployDto findByIp(String ip);

    /**
     * @param resources
     * @return
     */
    Boolean testConnect(ServerDeploy resources);

    /**
     * @param queryAll /
     * @param response /
     */
    void download(List<ServerDeployDto> queryAll, HttpServletResponse response) throws IOException;

}
