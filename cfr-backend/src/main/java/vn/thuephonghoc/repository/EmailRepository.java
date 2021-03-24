package vn.thuephonghoc.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import vn.thuephonghoc.entity.EmailConfig;

public interface EmailRepository extends JpaRepository<EmailConfig,Long> {
}
