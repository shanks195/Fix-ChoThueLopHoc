package vn.thuephonghoc.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import vn.thuephonghoc.entity.VerificationCode;

public interface VerificationCodeRepository extends JpaRepository<VerificationCode, Long> {

    /**
     * @param scenes Business scenarios, such as resetting password, resetting email, etc.
     * @param type 
     * @param value
     * @return VerificationCode
     */
    VerificationCode findByScenesAndTypeAndValueAndStatusIsTrue(String scenes, String type, String value);
}
