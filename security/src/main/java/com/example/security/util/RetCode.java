package com.example.security.util;

/**
 * @Autoor:杨文彬
 * @Date:2019/1/4
 * @Description：
 */
public enum  RetCode {

    //成功
    SUCCESS(200),

    //失败
    FAIL(400),

    //错误
    FALSE(404),

    //token过期
    EXPIRED(401),

    //无权限
    NODEFINED(403),

    //内部错误
    ERROR(500);

    private int code;

    private RetCode(Integer code){
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
