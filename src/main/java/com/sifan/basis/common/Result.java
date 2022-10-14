package com.sifan.basis.common;

import lombok.Data;

import java.io.Serializable;

@Data
public class Result<T> implements Serializable {
    /**
     * 状态码
     */
    private Integer code;
    /**
     * 返回结果
     */
    private T data;
    /**
     * 返回消息
     */
    private String msg;

    public static <T> Result<T> success() {
        Result<T> result = new Result<>();
        result.setCode(1);
        return result;
    }

    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setData(data);
        result.setCode(1);
        return result;
    }

    public static <T> Result<T> success(T data,String msg) {
        Result<T> result = new Result<>();
        result.setData(data);
        result.setCode(1);
        return result;
    }

    public static <T> Result<T> fail() {
        Result<T> result = new Result<>();
        result.setCode(0);
        result.setMsg("操作失败");
        return result;
    }

    public static <T> Result<T> fail(Integer code, String msg) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMsg(msg);
        return result;
    }
}