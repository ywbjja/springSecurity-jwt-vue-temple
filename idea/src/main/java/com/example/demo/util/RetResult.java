package com.example.demo.util;

/**
 * @Autoor:杨文彬
 * @Date:2019/1/2
 * @Description：
 */
public class RetResult {
    private  Integer code;
    private String msg;
    private Object data;

    public RetResult(){
        this.code = Integer.valueOf(0);
        this.msg = "";
        this.data = null;
    }
    /**
     *返回状态码、信息、以及数据
     */
    public RetResult(Integer code, String msg, Object data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }
    /**
     *只返回状态码，以及信息可以用于失败时候来使用
     */
    public RetResult(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
        this.data = null;
    }
    /**
     *只返回状态码和数据
     */
    public RetResult(Integer code, Object data) {
        this.code = code;
        this.msg = "";
        this.data = data;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
