package com.ghr.chat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ghr.chat.common.ResponseResult;
import com.ghr.chat.dao.UserMapper;
import com.ghr.chat.domain.entity.User;
import com.ghr.chat.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.Random;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Value("${chat.path}")
    private String path;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;


    @Override
    public ResponseResult editInfo(MultipartFile multipartFile, User user) {
        log.info(path);
        if(!Objects.isNull(multipartFile)){
            String prefix = multipartFile.getOriginalFilename();
            prefix = prefix.substring(prefix.indexOf("."));

            File file = new File(System.getProperty("user.dir") + "/chat/src/main/resources/static/avatars/" + user.getUserName() + prefix);
            try {
                multipartFile.transferTo(file);
                // 设置图片路径
                user.setAvatar(path + "/avatars/" + user.getUserName() + prefix);
            } catch (IOException e) {
                log.error(user.getUserName(), "Error saving file");
                return new ResponseResult<>(0, "Error saving file");
            }
        }

        // 保存信息
        userMapper.updateById(user);
        // 重新查询信息
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getId, user.getId());
        user = userMapper.selectOne(queryWrapper);
        user.setPassword("");
        return new ResponseResult<>(200, "success", user);
    }

    @Override
    public ResponseResult resetPassword(String oldPwd, String newPwd, String newPwdSuc, Long userId) {
        if(!StringUtils.hasText(oldPwd) || !StringUtils.hasText(newPwd) || !StringUtils.hasText(newPwdSuc)
            || oldPwd.length() < 6 || oldPwd.length() > 15 || newPwd.length() < 6 || newPwd.length() > 15 ||
            newPwdSuc.length() < 6 || newPwdSuc.length() > 15
        ){
            return new ResponseResult<>(0, "参数校验不通过");
        }
        if(!newPwd.equals(newPwdSuc)){
            return new ResponseResult<>(0, "新密码不一致");
        }
        // 判断原密码是否一致
        User user = userMapper.selectById(userId);
        boolean matches = passwordEncoder.matches(oldPwd, user.getPassword());
        if(!matches){
            return new ResponseResult<>(0, "原密码不正确");
        }
        // 修改密码
        newPwd = passwordEncoder.encode(newPwd);
        userMapper.resetPassword(userId, newPwd);
        return new ResponseResult<>(200, "修改成功");
    }

    @Override
    public ResponseResult register(User user) {
        // 判断用户名是否存在
        if(Objects.isNull(user)){
            return new ResponseResult<>(0, "参数校验错误");
        }
        if(!user.getPassword().equals(user.getCheckPassword())){
            return new ResponseResult<>(0, "两次密码不一致");
        }
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUserName, user.getUserName());
        User user1 = userMapper.selectOne(queryWrapper);
        if(!Objects.isNull(user1)){
            return new ResponseResult<>(0, "用户名已存在");
        }
        // 密码加密
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        // 设置头像
        // 两个头像随机选择
        int random = new Random().nextInt(1) + 1;
        user.setAvatar(path + "/avatars/default" + random + ".jpeg");
        // 保存信息
        userMapper.insert(user);

        return new ResponseResult<>(200, "注册成功");
    }
}
