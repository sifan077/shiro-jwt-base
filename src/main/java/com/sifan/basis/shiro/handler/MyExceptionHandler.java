package com.sifan.basis.shiro.handler;

import com.sifan.basis.common.Result;
import org.apache.shiro.ShiroException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class MyExceptionHandler {
    // 捕捉shiro的异常
    @ExceptionHandler(ShiroException.class)
    public Object handleShiroException(ShiroException e) {
        return Result.fail(401, "您权限不足或者token失效，请重新登陆");
    }

    // 捕捉其他所有异常
    @ExceptionHandler(Exception.class)
    public Object globalException(HttpServletRequest request, Throwable ex) {
        return Result.fail(401, ex.getMessage());
    }

}