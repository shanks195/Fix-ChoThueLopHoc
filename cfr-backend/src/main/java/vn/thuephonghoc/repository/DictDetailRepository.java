package vn.thuephonghoc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import vn.thuephonghoc.entity.DictDetail;

public interface DictDetailRepository extends JpaRepository<DictDetail, Long>, JpaSpecificationExecutor<DictDetail> {
}
