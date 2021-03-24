package vn.thuephonghoc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import vn.thuephonghoc.entity.QuartzLog;

public interface QuartzLogRepository extends JpaRepository<QuartzLog,Long>, JpaSpecificationExecutor<QuartzLog> {

}
