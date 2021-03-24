package vn.thuephonghoc.service;

import org.springframework.scheduling.annotation.Async;

import vn.thuephonghoc.entity.EmailConfig;
import vn.thuephonghoc.vo.EmailVo;

public interface EmailService {

    /**
     * @param emailConfig
     * @param old
     * @return EmailConfig
     */
    EmailConfig update(EmailConfig emailConfig, EmailConfig old);

    /**
     * @return EmailConfig
     */
    EmailConfig find();

    /**
     * @param emailVo
     * @param emailConfig
     * @throws Exception /
     */
    @Async
    void send(EmailVo emailVo, EmailConfig emailConfig) throws Exception;
}

