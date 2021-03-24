package vn.thuephonghoc.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import vn.thuephonghoc.entity.VerificationCode;
import vn.thuephonghoc.service.EmailService;
import vn.thuephonghoc.service.VerificationCodeService;
import vn.thuephonghoc.utils.CfrConstant;
import vn.thuephonghoc.vo.EmailVo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/code")
@Api(tags = "Tool: Verification Code Management")
public class VerificationCodeController {

    private final VerificationCodeService verificationCodeService;

    private final EmailService emailService;

    public VerificationCodeController(VerificationCodeService verificationCodeService,  EmailService emailService) {
        this.verificationCodeService = verificationCodeService;
        this.emailService = emailService;
    }

    @PostMapping(value = "/resetEmail")
    @ApiOperation("Reset email and send verification code")
    public ResponseEntity<Object> resetEmail(@RequestBody VerificationCode code) throws Exception {
        code.setScenes(CfrConstant.RESET_MAIL);
        EmailVo emailVo = verificationCodeService.sendEmail(code);
        emailService.send(emailVo,emailService.find());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(value = "/email/resetPass")
    @ApiOperation("Reset password, send verification code")
    public ResponseEntity<Object> resetPass(@RequestParam String email) throws Exception {
        VerificationCode code = new VerificationCode();
        code.setType("email");
        code.setValue(email);
        code.setScenes(CfrConstant.RESET_MAIL);
        EmailVo emailVo = verificationCodeService.sendEmail(code);
        emailService.send(emailVo,emailService.find());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping(value = "/validated")
    @ApiOperation("Verification Code Verification")
    public ResponseEntity<Object> validated(VerificationCode code){
        verificationCodeService.validated(code);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}

