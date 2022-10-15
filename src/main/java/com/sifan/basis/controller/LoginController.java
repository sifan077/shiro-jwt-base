package com.sifan.basis.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sifan.basis.common.Result;
import com.sifan.basis.domain.Utoken;
import com.sifan.basis.service.UtokenService;
import com.sifan.basis.shiro.jwt.JwtUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.subject.Subject;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
public class LoginController {

    @Resource
    private UtokenService utokenService;

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
            utokenService.clearToken(userName);
            assert jwtToken != null;
            Utoken utoken = new Utoken(userName, jwtToken);
            utokenService.save(utoken);
            return Result.success(jwtToken);
        } else {
            return Result.fail(401, "msg");
        }

    }

    @PostMapping("/logout")
    public Object logout(ServletRequest request) {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        String header = httpServletRequest.getHeader(JwtUtils.AUTH_HEADER);
        LambdaQueryWrapper<Utoken> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Utoken::getToken, header);
        utokenService.remove(lqw);
        return Result.success("退出登陆成功");
    }

    @GetMapping("/")
    public Result<String> index() {
        return Result.success("你好，shiro jwt前后端分离");
    }

    @GetMapping("/index")
    @RequiresAuthentication
    public Result<String> iindex() {
        return Result.success("我是主页");
    }


    @GetMapping("/index2")
    public Result<String> iindex2() {
        return Result.success("我是主页");
    }
}