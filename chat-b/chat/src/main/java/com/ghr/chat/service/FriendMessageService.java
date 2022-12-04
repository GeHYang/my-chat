package com.ghr.chat.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ghr.chat.common.ResponseResult;
import com.ghr.chat.domain.entity.FriendMessage;
import com.ghr.chat.domain.vo.CallFriend;

public interface FriendMessageService extends IService<FriendMessage> {

    ResponseResult messageCache(FriendMessage friendMessage);

    ResponseResult getAllUnreadMessage(FriendMessage friendMessage);

    ResponseResult call(CallFriend callFriend);

}
