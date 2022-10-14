package com.ghr.chat.service;

import com.ghr.chat.domain.entity.User;
import com.ghr.chat.common.ResponseResult;

public interface LoginService {

    ResponseResult login(User user);

    ResponseResult logout(long userId);
}
