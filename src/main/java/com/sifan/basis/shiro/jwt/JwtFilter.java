package com.sifan.basis.shiro.jwt;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.BasicHttpAuthenticationFilter;
import org.apache.shiro.web.util.WebUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

//@Component
public class JwtFilter extends BasicHttpAuthenticationFilter {

    /**
     * 前置拦截处理
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @Override
    protected boolean preHandle(ServletRequest request, ServletResponse response) throws Exception {
        //servlet请求与响应的转换
        HttpServletRequest httpServletRequest = WebUtils.toHttp(request);
        HttpServletResponse httpServletResponse = WebUtils.toHttp(response);

        //跨域时会首先发送一个option请求，这里我们给option请求直接返回正常状态
        if (httpServletRequest.getMethod().equals(RequestMethod.OPTIONS.name())) {
            httpServletResponse.setStatus(HttpStatus.OK.value());
            return false;
        }
        return super.preHandle(request, response);
    }

    /**
     * 后置拦截处理
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @Override
    protected void postHandle(ServletRequest request, ServletResponse response) throws Exception {
        //添加跨域支持
        this.fillCorsHeader(WebUtils.toHttp(request), WebUtils.toHttp(response));
    }

    /**
     * 过滤器拦截请求的入口方法,所有请求都会进入该方法
     * 返回true则允许访问
     *
     * @param request
     * @param response
     * @param mappedValue
     * @return
     */
    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        // 原用来判断是否是登录请求，在本例中不会拦截登录请求，用来检测Header是否包含token字段
        if (this.isLoginRequest(request, response)) {//看看源码，调用了isLoginAttempt()方法
            return false; //返回false后进入onAccessDenied()方法，返回错误信息
        }

        boolean allowed = false;
        try {
            //检测header里的JWT Token内容是否正确，尝试使用token进行登录
            allowed = this.executeLogin(request, response);
        } catch (IllegalStateException e) { //未找到token
            e.printStackTrace();
            System.out.println("未找到token");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("token检验出错");
        }
        return allowed || super.isPermissive(mappedValue);
    }

    /**
     * 检测Header中是否包含token字段
     *
     * @param request
     * @param response
     * @return
     */
    @Override
    protected boolean isLoginAttempt(ServletRequest request, ServletResponse response) {
        return ((HttpServletRequest) request).getHeader(JwtUtils.AUTH_HEADER) == null;
    }

    /**
     * 身份验证，检查JWT token是否合法
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @Override
    protected boolean executeLogin(ServletRequest request, ServletResponse response) throws Exception {
        //从请求头里拿到token
        AuthenticationToken token = createToken(request, response);
        if (token == null) {
            String msg = "createToken method implementation returned null. A valid non-null AuthenticationToken must be created in order to execute a login attempt.";
            throw new IllegalStateException(msg);
        }


        try {
            Subject subject = getSubject(request, response);
            subject.login(token); //让shiro进行登录验证
            //没出错则验证成功
            return onLoginSuccess(token, subject, request, response);
        } catch (AuthenticationException e) {
            return onLoginFailure(token, e, request, response);
        }
    }

    /**
     * 从Header中提取 JWT token
     *
     * @param request
     * @param response
     * @return
     */
    @Override
    protected AuthenticationToken createToken(ServletRequest request, ServletResponse response) {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        String authorization = httpServletRequest.getHeader(JwtUtils.AUTH_HEADER);
        JwtToken token = new JwtToken(authorization);
        return token;
    }

    /**
     * isAccessAllowed()方法返回false,会进入该方法，表示拒绝访问
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        HttpServletResponse httpServletResponse = WebUtils.toHttp(response);
        httpServletResponse.setCharacterEncoding("UTF-8");
        httpServletResponse.setContentType("application/json;charset=UTF-8");
        httpServletResponse.setStatus(HttpStatus.UNAUTHORIZED.value());

        PrintWriter writer = httpServletResponse.getWriter();
        writer.write("{\"errorCode\":401,\"msg\":\"UNAUTHORIZED\"}");
        fillCorsHeader(WebUtils.toHttp(request), httpServletResponse);
        return false;
    }

    /**
     * shiro利用 JWT Token 登录成功后，进入该方法
     *
     * @param token
     * @param subject
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @Override
    protected boolean onLoginSuccess(AuthenticationToken token, Subject subject, ServletRequest request, ServletResponse response) throws Exception {
        HttpServletResponse httpResponse = WebUtils.toHttp(response);
        String newToken = null;

        //登录成功后刷新token
        if (token instanceof JwtToken) {
            newToken = JwtUtils.refreshTokenExpired(token.getCredentials().toString(), JwtUtils.SECRET);
        }
        if (newToken != null) {
            httpResponse.setHeader(JwtUtils.AUTH_HEADER, newToken);
        }
        return true;
    }

    /**
     * 利用 JWT token 登录失败，会进入该方法
     *
     * @param token
     * @param e
     * @param request
     * @param response
     * @return
     */
    @Override
    protected boolean onLoginFailure(AuthenticationToken token, AuthenticationException e, ServletRequest request, ServletResponse response) {
        return false;//直接返回false,交给后面的 onAccessDenied()方法处理
    }

    //跨域请求的解决方案之一
    protected void fillCorsHeader(HttpServletRequest request, HttpServletResponse response) {
        response.setHeader("Access-control-Allow-Origin", request.getHeader("Origin"));
        response.setHeader("Access-Control-Allow-Methods", "GET,POST,OPTIONS,HEAD");
        response.setHeader("Access-Control-Allow-Headers",
                request.getHeader("Access-Control-Request-Headers"));
    }
}