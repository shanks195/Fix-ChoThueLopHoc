package vn.thuephonghoc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import vn.thuephonghoc.entity.Price;

public interface PriceRepository extends JpaRepository<Price, Long>, JpaSpecificationExecutor<Price> {

}
