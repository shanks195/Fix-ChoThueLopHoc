package vn.thuephonghoc.service;

import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import vn.thuephonghoc.dto.LocalStorageDto;
import vn.thuephonghoc.entity.LocalStorage;
import vn.thuephonghoc.query.LocalStorageQueryCriteria;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public interface LocalStorageService {

    Object queryAll(LocalStorageQueryCriteria criteria, Pageable pageable);

    List<LocalStorageDto> queryAll(LocalStorageQueryCriteria criteria);

    LocalStorageDto findById(Long id);

    LocalStorageDto create(String name, MultipartFile file);

    void update(LocalStorage resources);

    void deleteAll(Long[] ids);

    void download(List<LocalStorageDto> queryAll, HttpServletResponse response) throws IOException;

}
