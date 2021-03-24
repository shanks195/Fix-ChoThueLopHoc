package vn.thuephonghoc.utils;

import org.hibernate.exception.ConstraintViolationException;

import vn.thuephonghoc.exception.BadRequestException;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Exception tool
 */
public class ThrowableUtil {

    /**
     * Get stack information
     */
    public static String getStackTrace(Throwable throwable){
        StringWriter sw = new StringWriter();
        try (PrintWriter pw = new PrintWriter(sw)) {
            throwable.printStackTrace(pw);
            return sw.toString();
        }
    }

    public static void throwForeignKeyException(Throwable e, String msg){
        Throwable t = e.getCause();
        while ((t != null) && !(t instanceof ConstraintViolationException)) {
            t = t.getCause();
        }
        if (t != null) {
            throw new BadRequestException(msg);
        }
        throw new BadRequestException("Failed to delete:" + t.getMessage());
    }
}
