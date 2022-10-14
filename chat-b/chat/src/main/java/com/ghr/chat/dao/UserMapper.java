package com.ghr.chat.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ghr.chat.domain.entity.User;
import org.apache.ibatis.annotations.Update;

public interface UserMapper extends BaseMapper<User> {

    @Update("update chat_user set password = #{newPwd}, update_time = NOW() where id = #{userId}")
    int resetPassword(Long userId, String newPwd);
}
