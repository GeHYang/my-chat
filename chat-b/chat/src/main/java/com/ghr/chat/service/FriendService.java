package com.ghr.chat.service;

import com.ghr.chat.common.ResponseResult;
import com.ghr.chat.domain.vo.AddFriendNotify;
import com.ghr.chat.domain.vo.FriendsVO;

public interface FriendService {
    ResponseResult findFriendByUserId(Long userId);

    ResponseResult selectFriendAndFriendDetail(Long userId);
    // 查询好友
    ResponseResult searchFriend(String userName, Long userId);

    // 添加好友
    ResponseResult addFriend(AddFriendNotify friendNotify);

    // 修改昵称
    ResponseResult setNikeName(FriendsVO friendsVO);

    // 删除好友
    ResponseResult delFriend(FriendsVO friendsVO);
}
