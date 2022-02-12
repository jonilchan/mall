package com.atguigu.gulimall.member.exception;

public class UserNameException extends RuntimeException{

    public UserNameException(){
        super("该用户名已被注册");
    }

}
