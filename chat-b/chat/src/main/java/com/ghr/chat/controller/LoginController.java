package com.ghr.chat.controller;

import com.ghr.chat.domain.entity.User;
import com.ghr.chat.common.ResponseResult;
import com.ghr.chat.service.LoginService;
import com.ghr.chat.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/user")
public class LoginController {

    @Autowired
    private LoginService loginService;

    @PostMapping("/login")
    public ResponseResult login(@RequestBody User user){

        return loginService.login(user);
    }

    @PostMapping("/logout")
    public ResponseResult logout(HttpServletRequest request) throws Exception {
        String token = request.getHeader("token");
        String userId = JwtUtil.parseJWT(token).getSubject();
        return loginService.logout(Long.parseLong(userId));
    }

}
