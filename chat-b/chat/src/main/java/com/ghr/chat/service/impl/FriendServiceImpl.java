package com.ghr.chat.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ghr.chat.dao.FriendMapper;
import com.ghr.chat.dao.UserMapper;
import com.ghr.chat.domain.entity.Friend;
import com.ghr.chat.domain.entity.User;
import com.ghr.chat.domain.vo.AddFriendNotify;
import com.ghr.chat.domain.vo.FriendsVO;
import com.ghr.chat.common.ResponseResult;
import com.ghr.chat.handler.WebSocket;
import com.ghr.chat.service.FriendService;
import com.ghr.chat.utils.RedisCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;

@Service
public class FriendServiceImpl implements FriendService {

    @Autowired
    private FriendMapper friendMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private WebSocket webSocket;
    @Autowired
    private RedisCache redisCache;

    @Override
    public ResponseResult findFriendByUserId(Long userId) {
        LambdaQueryWrapper<Friend> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Friend::getUserId, userId).or().eq(Friend::getFriendId, userId);
        List<Friend> friends = friendMapper.selectList(queryWrapper);
        return new ResponseResult<>(200, "请求成功", friends);
    }

    @Override
    public ResponseResult selectFriendAndFriendDetail(Long userId) {
        List<FriendsVO> friendsVOS = friendMapper.selectFriendAndFriendDetail(userId);
        return new ResponseResult<>(200, friendsVOS);
    }

    /**
     * 搜索好友
     * @param userName
     * @return
     */
    @Override
    public ResponseResult searchFriend(String userName, Long userId) {
        if (!StringUtils.hasText(userName)){
            Map map1 = new HashMap();
            map1.put("friends", new ArrayList<>());
            map1.put("other", new ArrayList<>());
            return new ResponseResult<>(200, map1);
        }
        Map map = new HashMap();
        map.put("userId", userId);
        map.put("userName", userName);
        List<User> users1 = friendMapper.selectFriendByUserIdAndLikeUserName(map);

        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.ne(User::getId, userId);
        queryWrapper.like(User::getUserName, userName);
        List<User> users = userMapper.selectList(queryWrapper);
        // 去重
        for (User user : users) {
            for (User user1 : users1) {
                if(user1.getId() == user.getId()){
                    users.remove(user);
                }
            }
            if(users.size() <= 0) break;
        }
        Map map1 = new HashMap();
        map1.put("friends", users1);
        map1.put("other", users);
        return new ResponseResult<>(200, map1);
    }

    @Override
    public ResponseResult addFriend(AddFriendNotify friendNotify) {

        if(Objects.isNull(friendNotify)){
            return new ResponseResult<>(0, "信息丢失了");
        }
        Boolean isOnline;
        ResponseResult result = new ResponseResult<>(200, "addFriendNotify", friendNotify);
        FriendsVO vo = null;
        boolean flag = false;
        if(friendNotify.getFlag() == 1){
            // 同意添加好友
            Friend friend = new Friend(friendNotify.getFromUser().getId(), friendNotify.getToUser().getId(), friendNotify.getToUser().getNikeName());
            Friend friend1 = new Friend(friendNotify.getToUser().getId(), friendNotify.getFromUser().getId(), friendNotify.getFromUser().getNikeName());
            friendMapper.insert(friend);
            friendMapper.insert(friend1);
            // 同意添加发送给对方
            isOnline = webSocket.sendOneMessage(friendNotify.getToUser().getId().toString(), JSON.toJSONString(result));
            // 查询好友信息
            vo = friendMapper.selectFriendAndFriendDetailByUserIdAndFriendId(friendNotify.getFromUser().getId(), friendNotify.getToUser().getId());
            flag = true;
        } else if(friendNotify.getFlag() == 2){// 拒绝添加好友
            // 同意添加发送给对方
            isOnline = webSocket.sendOneMessage(friendNotify.getToUser().getId().toString(), JSON.toJSONString(result));
        } else{
            // 发起添加请求
            isOnline = webSocket.sendOneMessage(friendNotify.getToUser().getId().toString(), JSON.toJSONString(result));
        }
        if(!isOnline){
            // 将信息存入缓存
            // 采用set集合自动去重
            redisCache.redisTemplate.opsForSet().add("unread-add-friend-notify:" + friendNotify.getToUser().getId(), result);
        }
        if(flag){
            return new ResponseResult<>(200, "addFriendNotifySuccess", vo);
        }
        return result;
    }

    @Override
    public ResponseResult setNikeName(FriendsVO friendsVO) {
        friendMapper.setNikeNameByFriendsVO(friendsVO);
        return new ResponseResult<>(200, "success");
    }

    @Override
    public ResponseResult delFriend(FriendsVO friendsVO) {
        if(Objects.isNull(friendsVO)){
            return new ResponseResult<>(0, "数据丢失了");
        }


        int i = friendMapper.deleteById(friendsVO.getId());

        LambdaQueryWrapper<Friend> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Friend::getFriendId, friendsVO.getUserId());
        queryWrapper.eq(Friend::getUserId, friendsVO.getFriendId());
        i = friendMapper.delete(queryWrapper);

        // 往对方发送删除好友信息
        ResponseResult<Object> objectResponseResult = new ResponseResult<>(200, "deleteFriend", friendsVO);
        boolean b = webSocket.sendOneMessage(friendsVO.getFriendId().toString(), JSON.toJSONString(objectResponseResult));
        if(!b){
            // 存储信息
            redisCache.redisTemplate.opsForList().rightPush("unread-delete-friend-messages:" + friendsVO.getFriendId(), objectResponseResult);
        }
        return new ResponseResult<>(200, "删除成功");
    }
}
