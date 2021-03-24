package vn.thuephonghoc.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import vn.thuephonghoc.dto.ServerDeployDto;
import vn.thuephonghoc.entity.ServerDeploy;
import vn.thuephonghoc.mapper.ServerDeployMapper;
import vn.thuephonghoc.query.ServerDeployQueryCriteria;
import vn.thuephonghoc.repository.ServerDeployRepository;
import vn.thuephonghoc.service.ServerDeployService;
import vn.thuephonghoc.utils.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

@Service
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class ServerDeployServiceImpl implements ServerDeployService {

    private ServerDeployRepository serverDeployRepository;

    private ServerDeployMapper serverDeployMapper;

    public ServerDeployServiceImpl(ServerDeployRepository serverDeployRepository,ServerDeployMapper serverDeployMapper){
        this.serverDeployRepository = serverDeployRepository;
        this.serverDeployMapper = serverDeployMapper;
    }

    @Override
    public Object queryAll(ServerDeployQueryCriteria criteria, Pageable pageable){
        Page<ServerDeploy> page = serverDeployRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        return PageUtil.toPage(page.map(serverDeployMapper::toDto));
    }

    @Override
    public List<ServerDeployDto> queryAll(ServerDeployQueryCriteria criteria){
        return serverDeployMapper.toDto(serverDeployRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
    }

    @Override
    public ServerDeployDto findById(Long id) {
        ServerDeploy server = serverDeployRepository.findById(id).orElseGet(ServerDeploy::new);
        ValidationUtil.isNull(server.getId(),"ServerDeploy","id",id);
        return serverDeployMapper.toDto(server);
    }

    @Override
    public ServerDeployDto findByIp(String ip) {
        ServerDeploy deploy = serverDeployRepository.findByIp(ip);
        return serverDeployMapper.toDto(deploy);
    }


    @Override
    public Boolean testConnect(ServerDeploy resources) {
        ExecuteShellUtil executeShellUtil = null;
        try {
            executeShellUtil = new ExecuteShellUtil(resources.getIp(), resources.getAccount(), resources.getPassword(),resources.getPort());
            return executeShellUtil.execute("ls")==0;
        } catch (Exception e) {
            return false;
        }finally {
            if (executeShellUtil != null) {
                executeShellUtil.close();
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ServerDeployDto create(ServerDeploy resources) {
        return serverDeployMapper.toDto(serverDeployRepository.save(resources));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(ServerDeploy resources) {
        ServerDeploy serverDeploy = serverDeployRepository.findById(resources.getId()).orElseGet(ServerDeploy::new);
        ValidationUtil.isNull( serverDeploy.getId(),"ServerDeploy","id",resources.getId());
        serverDeploy.copy(resources);
        serverDeployRepository.save(serverDeploy);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Set<Long> ids) {
        for (Long id : ids) {
            serverDeployRepository.deleteById(id);
        }
    }

    @Override
    public void download(List<ServerDeployDto> queryAll, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (ServerDeployDto deployDto : queryAll) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("Server name", deployDto.getName());
            map.put("Server IP", deployDto.getIp());
            map.put("Port", deployDto.getPort());
            map.put("Account", deployDto.getAccount());
            map.put("Create Date", deployDto.getCreateTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }
}
