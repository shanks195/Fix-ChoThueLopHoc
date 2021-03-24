package vn.thuephonghoc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import vn.thuephonghoc.entity.QuartzJob;

import java.util.List;

public interface QuartzJobRepository extends JpaRepository<QuartzJob,Long>, JpaSpecificationExecutor<QuartzJob> {

    /**
     * @return List
     */
    List<QuartzJob> findByIsPauseIsFalse();
}
