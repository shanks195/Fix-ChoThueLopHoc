package vn.thuephonghoc.utils;

import org.hibernate.validator.internal.constraintvalidators.bv.EmailValidator;

import cn.hutool.core.util.ObjectUtil;
import vn.thuephonghoc.exception.BadRequestException;

/**
 * Verification tool
 */
public class ValidationUtil{

    /**
     * Verify empty
     */
    public static void isNull(Object obj, String entity, String parameter, Object value){
        if(ObjectUtil.isNull(obj)){
            String msg = entity + "Does not exist: "+ parameter +" is "+ value;
            throw new BadRequestException(msg);
        }
    }

    /**
     * Verify whether it is a mailbox
     */
    public static boolean isEmail(String email) {
        return new EmailValidator().isValid(email, null);
    }
}

