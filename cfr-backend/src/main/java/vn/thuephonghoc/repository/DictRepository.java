package vn.thuephonghoc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import vn.thuephonghoc.entity.Dict;

public interface DictRepository extends JpaRepository<Dict, Long>, JpaSpecificationExecutor<Dict> {
}
