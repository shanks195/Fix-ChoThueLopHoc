package vn.thuephonghoc.service.impl;

import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.extra.template.Template;
import cn.hutool.extra.template.TemplateConfig;
import cn.hutool.extra.template.TemplateEngine;
import cn.hutool.extra.template.TemplateUtil;
import vn.thuephonghoc.entity.VerificationCode;
import vn.thuephonghoc.exception.BadRequestException;
import vn.thuephonghoc.repository.VerificationCodeRepository;
import vn.thuephonghoc.service.VerificationCodeService;
import vn.thuephonghoc.vo.EmailVo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class VerificationCodeServiceImpl implements VerificationCodeService {

    private final VerificationCodeRepository verificationCodeRepository;

    public VerificationCodeServiceImpl(VerificationCodeRepository verificationCodeRepository) {
        this.verificationCodeRepository = verificationCodeRepository;
    }

    @Value("${code.expiration}")
    private Integer expiration;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public EmailVo sendEmail(VerificationCode code) {
        EmailVo emailVo;
        String content;
        VerificationCode verificationCode = verificationCodeRepository.findByScenesAndTypeAndValueAndStatusIsTrue(code.getScenes(),code.getType(),code.getValue());
        // If there is no valid verification code, create a new one
        TemplateEngine engine = TemplateUtil.createEngine(new TemplateConfig("template", TemplateConfig.ResourceMode.CLASSPATH));
        Template template = engine.getTemplate("email/email.ftl");
        if(verificationCode == null){
            code.setCode(RandomUtil.randomNumbers (6));
            content = template.render(Dict.create().set("code",code.getCode()));
            emailVo = new EmailVo(Collections.singletonList(code.getValue()),"CFR-BACKEND",content);
            timedDestruction(verificationCodeRepository.save(code));
            // Send the original verification code again if it exists
        } else {
            content = template.render(Dict.create().set("code",verificationCode.getCode()));
            emailVo = new EmailVo(Collections.singletonList(verificationCode.getValue()),"CFR-BACKEND",content);
        }
        return emailVo;
    }

    @Override
    public void validated(VerificationCode code) {
        VerificationCode verificationCode = verificationCodeRepository.findByScenesAndTypeAndValueAndStatusIsTrue(code.getScenes(),code.getType(),code.getValue());
        if(verificationCode == null || !verificationCode.getCode().equals(code.getCode())){
            throw new BadRequestException("Invalid verification code");
        } else {
            verificationCode.setStatus(false);
            verificationCodeRepository.save(verificationCode);
        }
    }

    /**
     * Timed task, change the verification code status after specified minutes
     * @param verifyCode 
     */
    private void timedDestruction(VerificationCode verifyCode) {
        //The following example continues to run after the program call ends
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        try {
            executorService.schedule(() -> {
                verifyCode.setStatus(false);
                verificationCodeRepository.save(verifyCode);
            }, expiration * 60 * 1000L, TimeUnit.MILLISECONDS);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}

