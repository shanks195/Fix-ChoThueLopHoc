package vn.thuephonghoc.service;

import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import vn.thuephonghoc.entity.Picture;
import vn.thuephonghoc.query.PictureQueryCriteria;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public interface PictureService {

    /**
     * @param criteria 
     * @param pageable 
     * @return /
     */
    Object queryAll(PictureQueryCriteria criteria, Pageable pageable);

    /**
     * @param criteria 
     * @return /
     */
    List<Picture> queryAll(PictureQueryCriteria criteria);

    /**
     * @param file /
     * @param username /
     * @return /
     */
    Picture upload(MultipartFile file, String username);

    /**
     * @param id /
     * @return /
     */
    Picture findById(Long id);

    /**
     * @param ids /
     */
    void deleteAll(Long[] ids);

    /**
     * @param queryAll
     * @param response /
     * @throws IOException /
     */
    void download(List<Picture> queryAll, HttpServletResponse response) throws IOException;


    void synchronize();

}

