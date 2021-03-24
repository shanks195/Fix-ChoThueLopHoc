package vn.thuephonghoc.handler;

import lombok.extern.slf4j.Slf4j;
import vn.thuephonghoc.exception.BadRequestException;
import vn.thuephonghoc.exception.EntityExistException;
import vn.thuephonghoc.utils.ThrowableUtil;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.persistence.EntityNotFoundException;

import java.util.Objects;

import static org.springframework.http.HttpStatus.*;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * @param apiError
     * @return
     */
    private ResponseEntity<ApiError> buildResponseEntity(ApiError apiError) {
        return new ResponseEntity<>(apiError, HttpStatus.valueOf(apiError.getStatus()));
    }

    /**
     * @param e
     * @return
     */
    @ExceptionHandler(Throwable.class)
    public ResponseEntity<ApiError> handleException(Throwable e){
        log.error(ThrowableUtil.getStackTrace(e));
        return buildResponseEntity(ApiError.error(e.getMessage()));
    }

    /**
     * BadCredentialsException
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiError> badCredentialsException(BadCredentialsException e){
        String message = "Bad credential".equals(e.getMessage())? "The username or password is incorrect" : e.getMessage();
        log.error(message);
        return buildResponseEntity(ApiError.error(message));
    }


    /**
     * @param e
     * @return
     */
    @ExceptionHandler(value = BadRequestException.class)
    public ResponseEntity<ApiError> badRequestException(BadRequestException e) {
        log.error(ThrowableUtil.getStackTrace(e));
        return buildResponseEntity(ApiError.error(e.getStatus(),e.getMessage()));
    }

    /**
     *  EntityExist
     * @param e
     * @return
     */
    @ExceptionHandler(value = EntityExistException.class)
    public ResponseEntity<ApiError> entityExistException(EntityExistException e) {
        log.error(ThrowableUtil.getStackTrace(e));
        return buildResponseEntity(ApiError.error(e.getMessage()));
    }

    /**
     *  EntityNotFound
     * @param e
     * @return
     */
    @ExceptionHandler(value = EntityNotFoundException.class)
    public ResponseEntity<ApiError> entityNotFoundException(EntityNotFoundException e) {
        log.error(ThrowableUtil.getStackTrace(e));
        return buildResponseEntity(ApiError.error(NOT_FOUND.value(),e.getMessage()));
    }

    /**
     * Handle all interface data verification exceptions
     * @param e
     * @returns
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleMethodArgumentNotValidException(MethodArgumentNotValidException e){
        log.error(ThrowableUtil.getStackTrace(e));
        String[] str = Objects.requireNonNull(e.getBindingResult().getAllErrors().get(0).getCodes())[1].split("\\.");
        String message = e.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        String msg = "Can not be empty";
        if(msg.equals(message)){
            message = str[1] + ":" + message;
        }        return buildResponseEntity(ApiError.error(message));
    }

}
