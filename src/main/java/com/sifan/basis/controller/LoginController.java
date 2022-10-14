package com.sifan.basis.controller;

import com.sifan.basis.common.Result;
import com.sifan.basis.shiro.jwt.JwtUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.subject.Subject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

@RestController
public class LoginController {
    @PostMapping(value = "/login")
    public Object userLogin(@RequestParam(name = "username", required = true) String userName,
                            @RequestParam(name = "password", required = true) String password, ServletResponse response) {

        // 获取当前用户主体
        Subject subject = SecurityUtils.getSubject();
        String msg = null;
        boolean loginSuccess = false;
        // 将用户名和密码封装成 UsernamePasswordToken 对象
        UsernamePasswordToken token = new UsernamePasswordToken(userName, password);
        try {
            subject.login(token);
            msg = "登录成功。";
            loginSuccess = true;
        } catch (UnknownAccountException uae) { // 账号不存在
            msg = "未知账号，请检查后重新输入！";
        } catch (IncorrectCredentialsException ice) { // 账号与密码不匹配
            msg = "用户名与密码不匹配，请检查后重新输入！";
        } catch (LockedAccountException lae) { // 账号已被锁定
            msg = "该账户已被锁定，如需解锁请联系管理员！";
        } catch (AuthenticationException ae) { // 其他身份验证异常
            msg = "登录异常，请联系管理员！";
        }


        if (loginSuccess) {
            // 若登录成功，签发 JWT token
            String jwtToken = JwtUtils.sign(userName, JwtUtils.SECRET);
            // 将签发的 JWT token 设置到 HttpServletResponse 的 Header 中
            ((HttpServletResponse) response).setHeader(JwtUtils.AUTH_HEADER, jwtToken);
            // 把token和用户id存入数据库中

            return Result.success(jwtToken);
        } else {
            return Result.fail(401, "msg");
        }

    }

    @GetMapping("/logout")
    public Object logout() {
        return Result.success(null, "退出登录");
    }

    @GetMapping("/")
    public Result<String> index() {
        return Result.success("你好，shiro jwt前后端分离");
    }
}