package vn.thuephonghoc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import vn.thuephonghoc.entity.ContactClient;

public interface ContactClientRepository extends JpaRepository<ContactClient, Long>, JpaSpecificationExecutor<ContactClient> {

}
