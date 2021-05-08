package com.spark.xposeddy.xposed.dy.api;

public class Result {
    private int code;
    private String message;
    private String data;

    public static Result ok() {
        return new Result().setCode(ResultCode.OK);
    }

    public static Result ok(String data) {
        return new Result().setCode(ResultCode.OK).setData(data);
    }

    public static Result error(int code, String message) {
        return new Result().setCode(code).setMessage(message);
    }

    public Result setCode(int code) {
        this.code = code;
        return this;
    }

    public Result setMessage(String message) {
        this.message = message;
        return this;
    }

    public Result setData(String data) {
        this.data = data;
        return this;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getData() {
        return data;
    }

    public interface ResultCode {
        int OK = 200;
        int PARSE_ERR = 201;
        int REQ_ERR = 202;
        int TIME_OUT = 203;
        int EXISTS = 204;
    }
}

