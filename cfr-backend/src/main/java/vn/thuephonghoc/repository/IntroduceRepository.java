package vn.thuephonghoc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import vn.thuephonghoc.entity.Introduce;

public interface IntroduceRepository extends JpaRepository<Introduce, Long>, JpaSpecificationExecutor<Introduce> {
}
