package com.p2p.demo.pojo;

import java.io.Serializable;

/**
 * @program: p2pDemo
 * @description:
 * @author: JavaLitterBoy
 * @create: 2018-10-09 09:21
 **/
public class Result implements Serializable {
    private int code;
    private String message;
    private String data;

    public Result(int code, String message, String data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static Result ok(String msg,String data){
        return new Result(200,msg,data);
    }

    public static Result error(String msg,String data){
        return new Result(300,msg,data);
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
