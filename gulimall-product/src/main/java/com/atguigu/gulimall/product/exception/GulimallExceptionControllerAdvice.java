package com.atguigu.gulimall.product.exception;


import com.atguigu.common.utils.R;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

//全局异常处理
@Log4j2
@RestControllerAdvice(basePackages = "com.atguigu.gulimall.product.controller")
public class GulimallExceptionControllerAdvice {

    @ExceptionHandler(value=Exception.class)
    public R handlerValidException(Exception e){
        log.error("出现问题{}，异常类型：{}", e.getMessage(), e.getClass());
        return R.error();
    }

}
