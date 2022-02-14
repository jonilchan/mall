package com.atguigu.cart.exception;

import com.atguigu.common.utils.R;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class RuntimeExceptionHandler {

    @ResponseBody
    @ExceptionHandler(RuntimeException.class)
    public R handler(RuntimeException exception){
        return R.error(exception.getMessage());
    }

    @ExceptionHandler(CartExceptionHandler.class)
    public R userHandle(CartExceptionHandler exception){
        return R.error("购物车无此商品");
    }

}
