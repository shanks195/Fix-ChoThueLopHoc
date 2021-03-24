package vn.thuephonghoc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import vn.thuephonghoc.entity.Picture;

public interface PictureRepository extends JpaRepository<Picture, Long>, JpaSpecificationExecutor<Picture> {

    /**
     * @param code
     * @return /
     */
    Picture findByMd5Code(String code);

    /**
     * @param url /
     * @return /
     */
    boolean existsByUrl(String url);
}
