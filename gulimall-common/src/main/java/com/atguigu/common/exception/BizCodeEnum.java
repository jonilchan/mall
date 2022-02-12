package com.atguigu.common.exception;

public enum BizCodeEnum {
    UNKNOW_EXCEPTION(10000, "系统未知异常"),
    VALID_EXCEPTION(10001, "参数格式校验失败"),
    TOO_MANY_REQUEST(10002, "请求流量过大,请稍后再试"),
    SMS_CODE_EXCEPTION(10002, "验证码请求频率过高,请稍后再试"),
    PRODUCT_UP_EXCEPTION(11000, "商品上架异常"),
    USER_EXIST_EXCEPTION(15001, "存在相同的手机号"),
    PHONE_EXIST_EXCEPTION(21000, "商品库存不足"),
    LOGINACCT_PASSWORD_EXCEPTION(15003, "账号或密码错误");


    private int code;
    private String msg;

    BizCodeEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
