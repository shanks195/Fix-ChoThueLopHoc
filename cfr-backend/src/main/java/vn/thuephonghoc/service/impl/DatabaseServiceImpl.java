package vn.thuephonghoc.service.impl;

import cn.hutool.core.util.IdUtil;
import lombok.extern.slf4j.Slf4j;
import vn.thuephonghoc.dto.DatabaseDto;
import vn.thuephonghoc.entity.Database;
import vn.thuephonghoc.mapper.DatabaseMapper;
import vn.thuephonghoc.query.DatabaseQueryCriteria;
import vn.thuephonghoc.repository.DatabaseRepository;
import vn.thuephonghoc.service.DatabaseService;
import vn.thuephonghoc.utils.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

@Service
@Slf4j
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class DatabaseServiceImpl implements DatabaseService {

    private DatabaseRepository databaseRepository;

    private DatabaseMapper databaseMapper;

    public DatabaseServiceImpl(DatabaseRepository databaseRepository,DatabaseMapper databaseMapper){
        this.databaseMapper = databaseMapper;
        this.databaseRepository = databaseRepository;
    }

    @Override
    public Object queryAll(DatabaseQueryCriteria criteria, Pageable pageable){
        Page<Database> page = databaseRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        return PageUtil.toPage(page.map(databaseMapper::toDto));
    }

    @Override
    public List<DatabaseDto> queryAll(DatabaseQueryCriteria criteria){
        return databaseMapper.toDto(databaseRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
    }

    @Override
    public DatabaseDto findById(String id) {
        Database database = databaseRepository.findById(id).orElseGet(Database::new);
        ValidationUtil.isNull(database.getId(),"Database","id",id);
        return databaseMapper.toDto(database);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DatabaseDto create(Database resources) {
        resources.setId(IdUtil.simpleUUID());
        return databaseMapper.toDto(databaseRepository.save(resources));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Database resources) {
        Database database = databaseRepository.findById(resources.getId()).orElseGet(Database::new);
        ValidationUtil.isNull(database.getId(),"Database","id",resources.getId());
        database.copy(resources);
        databaseRepository.save(database);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Set<String> ids) {
        for (String id : ids) {
            databaseRepository.deleteById(id);
        }
    }

    @Override
    public boolean testConnection(Database resources) {
        try {
            return SqlUtils.testConnection(resources.getJdbcUrl(), resources.getUserName(), resources.getPwd());
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }

    }


    @Override
    public void download(List<DatabaseDto> queryAll, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (DatabaseDto databaseDto : queryAll) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("Database name", databaseDto.getName());
            map.put("Database connection address", databaseDto.getJdbcUrl());
            map.put("User Name", databaseDto.getUserName());
            map.put("Create Date", databaseDto.getCreateTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }
}
