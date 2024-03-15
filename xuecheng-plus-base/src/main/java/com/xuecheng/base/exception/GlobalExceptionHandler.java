package com.xuecheng.base.exception;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.ArrayList;
import java.util.List;

/**
 * 全局异常处理器
 */
@Slf4j
@ControllerAdvice //拦截所有的异常信息
public class GlobalExceptionHandler {

    /**
     * 捕获发出XueChengPlusException的异常 相应内部服务异常
     * @param e
     * @return
     */
    @ResponseBody
    @ExceptionHandler(XueChengPlusException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public RestErrorResponse customException(XueChengPlusException e) {
        log.error("【系统异常】{}",e.getErrMessage(),e);
        return new RestErrorResponse(e.getErrMessage());

    }

    @ResponseBody
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public RestErrorResponse exception(Exception e) {
        log.error("【系统异常】{}",e.getMessage(),e);
        return new RestErrorResponse(CommonError.UNKOWN_ERROR.getErrMessage());

    }

    /**
     * 当Spring发生异常时 会抛出MethodArgumentNotValidException 异常
     * @param e
     * @return
     */
    @ResponseBody
    @ExceptionHandler(MethodArgumentNotValidException .class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public RestErrorResponse methodArgumentNotValidException (MethodArgumentNotValidException e) {

        BindingResult bindingResult = e.getBindingResult();
        //存储异常信息
        List<String> errors = new ArrayList<>();
        //添加异常信息
        bindingResult.getFieldErrors().stream().forEach(item->{
            errors.add(item.getDefaultMessage());
        });
        //将异常信息 拼接起来
        String errorMessage = StringUtils.join(errors, ",");

        //记录异常
        log.error("系统异常！{}",e.getMessage(),errorMessage);

        log.error("【系统异常】{}",e.getMessage(),e);
        return new RestErrorResponse(errorMessage);

    }
}
