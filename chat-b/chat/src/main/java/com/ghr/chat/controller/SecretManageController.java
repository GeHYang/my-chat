package com.ghr.chat.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.ghr.chat.common.ResponseResult;
import com.ghr.chat.domain.entity.Secret;
import com.ghr.chat.service.SecretManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author: Yang
 * @create: 2022-12-02
 * @Description: 密钥管理服务
 */
@RestController
@RequestMapping("/secret")
public class SecretManageController {

    @Autowired
    private SecretManageService secretManageService;

    @PostMapping
    public ResponseResult save(@RequestBody Secret secret){
        LambdaQueryWrapper<Secret> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Secret::getUserId, secret.getUserId());
        Secret one = secretManageService.getOne(queryWrapper);
        if(one != null){
            LambdaUpdateWrapper<Secret> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(Secret::getUserId, secret.getUserId());
            secretManageService.update(secret, updateWrapper);
        } else {
            secretManageService.save(secret);
        }
        return new ResponseResult<>(200, "success");
    }

    @GetMapping("/{userId}")
    public ResponseResult get(@PathVariable("userId") Long userId){
        LambdaQueryWrapper<Secret> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Secret::getUserId, userId);
        Secret one = secretManageService.getOne(queryWrapper);
        return new ResponseResult<>(200, "success", one);
    }
}
