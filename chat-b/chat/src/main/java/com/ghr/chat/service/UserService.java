package com.ghr.chat.service;

import com.ghr.chat.common.ResponseResult;
import com.ghr.chat.domain.entity.User;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface UserService {
    ResponseResult editInfo(MultipartFile multipartFile, User user);

    ResponseResult resetPassword(String oldPwd, String newPwd, String newPwdSuc, Long userId);

    ResponseResult register(User user);
}
