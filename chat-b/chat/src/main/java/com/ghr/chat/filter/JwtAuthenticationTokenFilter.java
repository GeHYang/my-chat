package com.ghr.chat.filter;

import com.alibaba.fastjson.JSON;
import com.ghr.chat.domain.entity.LoginUser;
import com.ghr.chat.common.ResponseResult;
import com.ghr.chat.utils.JwtUtil;
import com.ghr.chat.utils.RedisCache;
import com.ghr.chat.utils.WebUtils;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

/**
 * jwt token校验过滤器
 */
@Component
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {
    @Autowired
    private RedisCache redisCache;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 1、通过header获取token
        String token = request.getHeader("token");
        if(!StringUtils.hasText(token)){// 如果不存在token，放行，让security自带错误拦截器拦截
            filterChain.doFilter(request, response);
            return;
        }
        // 2、通过token获取用户id
        String userId;
        try {
            Claims claims = JwtUtil.parseJWT(token);
            userId = claims.getSubject();
        } catch (Exception e) {
            String result = JSON.toJSONString(new ResponseResult<>(HttpStatus.UNAUTHORIZED.value(), "登陆认证失败，请重新登录"));
            WebUtils.renderString(response, result);
            return;
        }
        // 3、通过userId获取用户信息
        LoginUser loginUser = redisCache.getCacheObject("login:" + userId);
        if(Objects.isNull(loginUser)){
            String result = JSON.toJSONString(new ResponseResult<>(HttpStatus.UNAUTHORIZED.value(), "登陆认证失败，请重新登录"));
            WebUtils.renderString(response, result);
            return;
        }
        // 4、封装用户信息
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(loginUser, null, null);
        // 5、将用户信息存入SecurityContextHolder
        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
        // 6、放行
        filterChain.doFilter(request, response);
    }
}
