package vn.thuephonghoc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import vn.thuephonghoc.entity.Home;

public interface HomeRepository extends JpaRepository<Home, Long>, JpaSpecificationExecutor<Home> {

}
