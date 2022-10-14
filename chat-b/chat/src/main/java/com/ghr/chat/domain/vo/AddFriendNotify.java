package com.ghr.chat.domain.vo;

import com.ghr.chat.domain.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 添加好友信息实体
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddFriendNotify implements Serializable {

    private User fromUser;// 自己
    private User toUser;// 对方
    private Integer flag;// 添加成功标志
}
