package vn.thuephonghoc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import vn.thuephonghoc.entity.Database;

public interface DatabaseRepository extends JpaRepository<Database, String>, JpaSpecificationExecutor<Database> {
}
