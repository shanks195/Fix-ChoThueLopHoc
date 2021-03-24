package vn.thuephonghoc.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import vn.thuephonghoc.dto.AppDto;
import vn.thuephonghoc.entity.App;
import vn.thuephonghoc.exception.BadRequestException;
import vn.thuephonghoc.mapper.AppMapper;
import vn.thuephonghoc.query.AppQueryCriteria;
import vn.thuephonghoc.repository.AppRepository;
import vn.thuephonghoc.service.AppService;
import vn.thuephonghoc.utils.FileUtil;
import vn.thuephonghoc.utils.PageUtil;
import vn.thuephonghoc.utils.QueryHelp;
import vn.thuephonghoc.utils.ValidationUtil;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

@Service
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class AppServiceImpl implements AppService {

    private AppRepository appRepository;

    private AppMapper appMapper;

    public AppServiceImpl(AppRepository appRepository, AppMapper appMapper) {
        this.appMapper = appMapper;
        this.appRepository = appRepository;
    }

    @Override
    public Object queryAll(AppQueryCriteria criteria, Pageable pageable){
        Page<App> page = appRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        return PageUtil.toPage(page.map(appMapper::toDto));
    }

    @Override
    public List<AppDto> queryAll(AppQueryCriteria criteria){
        return appMapper.toDto(appRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
    }

    @Override
    public AppDto findById(Long id) {
        App app = appRepository.findById(id).orElseGet(App::new);
        ValidationUtil.isNull(app.getId(),"App","id",id);
        return appMapper.toDto(app);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AppDto create(App resources) {
        verification(resources);
        return appMapper.toDto(appRepository.save(resources));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(App resources) {
        verification(resources);
        App app = appRepository.findById(resources.getId()).orElseGet(App::new);
        ValidationUtil.isNull(app.getId(),"App","id",resources.getId());
        app.copy(resources);
        appRepository.save(app);
    }

    private void verification(App resources){
        String opt = "/opt";
        String home = "/home";
        if (!(resources.getUploadPath().startsWith(opt) || resources.getUploadPath().startsWith(home))) {
        	throw new BadRequestException("Files can only be uploaded in the opt directory or home directory");
        }
        if (!(resources.getDeployPath().startsWith(opt) || resources.getDeployPath().startsWith(home))) {
            throw new BadRequestException("Files can only be deployed in the opt directory or home directory");
        }
        if (!(resources.getBackupPath().startsWith(opt) || resources.getBackupPath().startsWith(home))) {
            throw new BadRequestException("Files can only be backed up in the opt directory or home directory");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Set<Long> ids) {
        for (Long id : ids) {
            appRepository.deleteById(id);
        }
    }

    @Override
    public void download(List<AppDto> queryAll, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (AppDto appDto : queryAll) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("Application name", appDto.getName());
            map.put("Port", appDto.getPort());
            map.put("Upload Directory", appDto.getUploadPath());
            map.put("Deployment Directory", appDto.getDeployPath());
            map.put("Backup Directory", appDto.getBackupPath());
            map.put("Start Script", appDto.getStartScript());
            map.put("Deployment script", appDto.getDeployScript());
            map.put("Creation Date", appDto.getCreateTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }
}
