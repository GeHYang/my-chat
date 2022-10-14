package com.ghr.chat.handler;

import com.alibaba.fastjson.JSON;
import com.ghr.chat.common.ResponseResult;
import com.ghr.chat.utils.WebUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class AuthenticationEntryPointImpl implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        // 给前端返回错误信息
        ResponseResult responseResult = new ResponseResult<>(HttpStatus.UNAUTHORIZED.value(), "用户名或密码错误");
        String result = JSON.toJSONString(responseResult);
        WebUtils.renderString(response, result);
    }
}
