package com.ghr.chat.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ghr.chat.domain.entity.Friend;
import com.ghr.chat.domain.entity.User;
import com.ghr.chat.domain.vo.FriendsVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface FriendMapper extends BaseMapper<Friend> {

    List<FriendsVO> selectFriendAndFriendDetail(Long userId);

    List selectFriendByUserIdAndLikeUserName(Map map);

    boolean setNikeNameByFriendsVO(FriendsVO friendsVO);

    FriendsVO selectFriendAndFriendDetailByUserIdAndFriendId(@Param("userId") Long userId, @Param("friendId") Long friendId);

}
