package com.sifan.basis.shiro.jwt;

import com.sifan.basis.shiro.jwt.JwtUtils;
import org.apache.shiro.authc.AuthenticationToken;

/**
 * 该类与UsernamePasswordToken差不多，都是AuthenticationToken接口的实现类
 * 目的是封装成UsernamePasswordToken让shiro进行登录、登出等操作
 */
public class JwtToken implements AuthenticationToken {

    private static final long serialVersionUID = 1L;

    //加密后的 JWT token
    private String token;

    private String username;

    public JwtToken(String token){
        this.token = token;
        this.username = JwtUtils.getClaimFiled(token,"username");
    }

    @Override
    public Object getPrincipal() {
        return this.username;
    }

    @Override
    public Object getCredentials() {
        return token;
    }
}