package com.atguigu.gulimall.member.exception;

public class PhoneException extends RuntimeException{

    public PhoneException(){
        super("该手机号已被注册");
    }

}
