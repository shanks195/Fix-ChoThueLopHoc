package vn.thuephonghoc.service.impl;

import cn.hutool.extra.mail.Mail;
import cn.hutool.extra.mail.MailAccount;
import vn.thuephonghoc.entity.EmailConfig;
import vn.thuephonghoc.exception.BadRequestException;
import vn.thuephonghoc.repository.EmailRepository;
import vn.thuephonghoc.service.EmailService;
import vn.thuephonghoc.utils.EncryptUtils;
import vn.thuephonghoc.vo.EmailVo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@CacheConfig(cacheNames = "email")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class EmailServiceImpl implements EmailService {

    private final EmailRepository emailRepository;

    public EmailServiceImpl(EmailRepository emailRepository) {
        this.emailRepository = emailRepository;
    }

    @Override
    @CachePut(key = "'1'")
    @Transactional(rollbackFor = Exception.class)
    public EmailConfig update(EmailConfig emailConfig, EmailConfig old) {
        try {
            if(!emailConfig.getPass().equals(old.getPass())){
                // Symmetric encryption
                emailConfig.setPass(EncryptUtils.desEncrypt(emailConfig.getPass()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return emailRepository.save(emailConfig);
    }

    @Override
    @Cacheable(key = "'1'")
    public EmailConfig find() {
        Optional<EmailConfig> emailConfig = emailRepository.findById(1L);
        return emailConfig.orElseGet(EmailConfig::new);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void send(EmailVo emailVo, EmailConfig emailConfig){
        if(emailConfig == null){
            throw new BadRequestException("Please configure before operation");
        }
        // Mail
        MailAccount account = new MailAccount();
        account.setHost(emailConfig.getHost());
        account.setPort(Integer.parseInt(emailConfig.getPort()));
        account.setAuth(true);
        try {
            account.setPass(EncryptUtils.desDecrypt(emailConfig.getPass()));
        } catch (Exception e) {
            throw new BadRequestException(e.getMessage());
        }
        account.setFrom(emailConfig.getUser()+"<"+emailConfig.getFromUser()+">");
        // ssl send
        account.setSslEnable(true);
        String content = emailVo.getContent();
        try {
            int size = emailVo.getTos().size();
            Mail.create(account)
                    .setTos(emailVo.getTos().toArray(new String[size]))
                    .setTitle(emailVo.getSubject())
                    .setContent(content)
                    //Copy to yourself to prevent email sending
                    .setCcs(emailConfig.getFromUser())
                    .setHtml(true)
                    //session
                    .setUseGlobalSession(false)
                    .send();
        }catch (Exception e){
            throw new BadRequestException(e.getMessage());
        }
    }
}

