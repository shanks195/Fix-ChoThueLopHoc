package vn.thuephonghoc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import vn.thuephonghoc.entity.Captcha;


public interface CaptchaRepository extends JpaRepository<Captcha, Long>, JpaSpecificationExecutor<Captcha> {
}
