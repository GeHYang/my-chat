package com.ghr.chat.handler;

import com.alibaba.fastjson.JSON;
import com.ghr.chat.common.ResponseResult;
import com.ghr.chat.utils.WebUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class AccessDeniedHandlerImpl implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        // 给前端返回错误信息
        ResponseResult responseResult = new ResponseResult<>(HttpStatus.FORBIDDEN.value(), "权限不足");
        String result = JSON.toJSONString(responseResult);
        WebUtils.renderString(response, result);
    }
}
