package com.ghr.chat.controller;

import com.alibaba.fastjson.JSON;
import com.ghr.chat.common.ResponseResult;
import com.ghr.chat.domain.entity.User;
import com.ghr.chat.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/editInfo")
    public ResponseResult editInfo(@RequestParam(value = "file", required = false) MultipartFile multipartFile, @RequestParam("user") String userStr) {
        User user = JSON.toJavaObject(JSON.parseObject(userStr), User.class);

        return userService.editInfo(multipartFile, user);
    }

    @PostMapping("/resetPassword")
    public ResponseResult resetPassword(@RequestParam("oldPwd") String oldPwd,
                                        @RequestParam("newPwd") String newPwd,
                                        @RequestParam("newPwdSuc") String newPwdSuc,
                                        @RequestParam("userId") Long userId){
        return userService.resetPassword(oldPwd, newPwd, newPwdSuc, userId);
    }

    @PostMapping("/register")
    public  ResponseResult register(@RequestBody User user){
        return userService.register(user);
    }
}
