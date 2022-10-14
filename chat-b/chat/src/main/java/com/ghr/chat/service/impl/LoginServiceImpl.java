package com.ghr.chat.service.impl;

import com.ghr.chat.domain.entity.LoginUser;
import com.ghr.chat.domain.entity.User;
import com.ghr.chat.common.ResponseResult;
import com.ghr.chat.service.LoginService;
import com.ghr.chat.utils.JwtUtil;
import com.ghr.chat.utils.RedisCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
public class LoginServiceImpl implements LoginService {

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private RedisCache redisCache;

    @Override
    public ResponseResult login(User user) {
        // 1、将用户输入的用户名密码与数据库信息进行校验
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                new UsernamePasswordAuthenticationToken(user.getUserName(), user.getPassword());
        Authentication authenticate = authenticationManager.authenticate(usernamePasswordAuthenticationToken);

        if (Objects.isNull(authenticate)){
            throw new RuntimeException("用户名或密码错误！");
        }

        // 2、获取数据库查询到的信息
        LoginUser loginUser = (LoginUser) authenticate.getPrincipal();
        loginUser.getUser().setPassword("");

        // 3、生成token
        String userId = loginUser.getUser().getId().toString();
        String token = JwtUtil.createJWT(userId);
        // 4、将用户信息存入redis
        redisCache.setCacheObject("login:" + userId, loginUser);

        // 5、将token返回给客户
        Map<String, Object> map = new HashMap<>();
        map.put("token", token);
        map.put("user_info", loginUser);

        return new ResponseResult(200, "登录成功", map);
    }

    @Override
    public ResponseResult logout(long userId) {
        redisCache.deleteObject("login:" + userId);
        return new ResponseResult<>(200, "退出登录成功");
    }
}
