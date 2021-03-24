package vn.thuephonghoc.service;

import vn.thuephonghoc.entity.VerificationCode;
import vn.thuephonghoc.vo.EmailVo;

public interface VerificationCodeService {

	/**
     * Send email verification code
     * @param code verification code
     * @return EmailVo
     */
    EmailVo sendEmail(VerificationCode code);

    /**
     * Verification
     * @param code verification code
     */
    void validated(VerificationCode code);
}
