package vn.thuephonghoc.aspect;

import lombok.extern.slf4j.Slf4j;
import vn.thuephonghoc.entity.Log;
import vn.thuephonghoc.service.LogService;
import vn.thuephonghoc.utils.RequestHolder;
import vn.thuephonghoc.utils.SecurityUtils;
import vn.thuephonghoc.utils.StringUtils;
import vn.thuephonghoc.utils.ThrowableUtil;
import vn.thuephonghoc.utils.ip.IpUtils;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;


/**
 * Log section
 */
@Component
@Aspect
@Slf4j
public class LogAspect {

    private final LogService logService;

    public LogAspect(LogService logService) {
        this.logService = logService;
    }

    ThreadLocal<Long> currentTime = new ThreadLocal<>();

    /**
     * Configuration entry point
     */
    @Pointcut("@annotation(vn.thuephonghoc.aop.log.Log)")
    public void logPointcut() {
        // This method has no method body, mainly to allow other methods in the same class to use this entry point
    }

    /**
     * Configure surround notification, use the entry point registered on method logPointcut()
     * @param proceedingJoinPoint
     * @return
     */
    @Around("logPointcut()")
    public Object logAround(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        Object result;
        currentTime.set(System.currentTimeMillis());
        result = proceedingJoinPoint.proceed();
        Log log = new Log("INFO", System.currentTimeMillis()-currentTime.get());
        currentTime.remove();
        HttpServletRequest request = RequestHolder.getHttpServletRequest();
        logService.save(getUsername(), StringUtils.getBrowser(request), IpUtils.getIpAddr(request),proceedingJoinPoint, log);
        return result;
    }

    /**
     * Configuration exception notification
     * @param joinPoint
     * @param e
     */
    @AfterThrowing(pointcut = "logPointcut()",throwing = "e")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable e) {
        Log log = new Log("ERROR", System.currentTimeMillis() - currentTime.get());
        log.setExceptionDetail(ThrowableUtil.getStackTrace(e).getBytes());
        HttpServletRequest request = RequestHolder.getHttpServletRequest();
        logService.save(getUsername(), StringUtils.getBrowser(request), IpUtils.getIpAddr(request), (ProceedingJoinPoint)joinPoint, log);
    }

    public String getUsername() {
        try {
            return SecurityUtils.getCurrentUsername();
        }catch (Exception e){
            return "";
        }
    }

}
