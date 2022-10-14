package com.ghr.chat.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ghr.chat.common.ResponseResult;
import com.ghr.chat.dao.FriendMapper;
import com.ghr.chat.dao.FriendMessageMapper;
import com.ghr.chat.domain.entity.Friend;
import com.ghr.chat.domain.entity.FriendMessage;
import com.ghr.chat.domain.vo.CallFriend;
import com.ghr.chat.handler.WebSocket;
import com.ghr.chat.service.FriendMessageService;
import com.ghr.chat.utils.RedisCache;
import com.ghr.chat.utils.SnowFlakeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class FriendMessageServiceImpl extends ServiceImpl<FriendMessageMapper, FriendMessage> implements FriendMessageService {
    @Autowired
    private WebSocket webSocket;
    @Autowired
    private FriendMapper friendMapper;
    @Autowired
    private RedisCache redisCache;
    @Override
    public ResponseResult messageCache(FriendMessage friendMessage) {
//        this.save(friendMessage);
        // 判断对方是否为好友
        LambdaQueryWrapper<Friend> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Friend::getFriendId, friendMessage.getToId());
        lambdaQueryWrapper.eq(Friend::getUserId, friendMessage.getFromId());
        Friend friend = friendMapper.selectOne(lambdaQueryWrapper);
        if(Objects.isNull(friend)){
            // 不是好友
            return new ResponseResult(0, "对方不是你好友");
        }
        // 手动生成ID
        friendMessage.setId(new SnowFlakeUtil(10, 11).nextId());
        // 设置时间
        friendMessage.setCreateTime(LocalDateTime.now());
        friendMessage.setUpdateTime(LocalDateTime.now());

        ResponseResult oneMessageSuccess = new ResponseResult(200, "oneMessageSuccess", friendMessage);

        // 判断接收方是否在线
        boolean online = webSocket.sendOneMessage(friendMessage.getToId().toString(),
                JSON.toJSONStringWithDateFormat(oneMessageSuccess,
                        "yyyy-MM-dd HH:mm:ss",
                        SerializerFeature.WriteDateUseDateFormat
                )
        );
        if(!online){// 接收方不在线
            // 添加缓存信息
            redisCache.redisTemplate.opsForList().rightPush("unread-friend-messages:" + friendMessage.getToId(), friendMessage);
        }

        return oneMessageSuccess;
    }

    /**
     * 获取所有未读消息
     * @param friendMessage
     * @return
     */
    @Override
    public ResponseResult getAllUnreadMessage(FriendMessage friendMessage) {
        String userId = friendMessage.getFromId().toString();
        if(!StringUtils.hasText(userId)){
            return new ResponseResult<>(0, "error");
        }
        // 从redis中获取未读消息
        FriendMessage friendMessage1 = (FriendMessage) redisCache.redisTemplate.opsForList().leftPop("unread-friend-messages:" + userId);
        while(!Objects.isNull(friendMessage1)){
            ResponseResult oneMessageSuccess = new ResponseResult(200, "oneMessageSuccess", friendMessage1);
            webSocket.sendOneMessage(userId,
                    JSON.toJSONStringWithDateFormat(oneMessageSuccess,
                    "yyyy-MM-dd HH:mm:ss",
                    SerializerFeature.WriteDateUseDateFormat
                    )
            );
            friendMessage1 = (FriendMessage) redisCache.redisTemplate.opsForList().leftPop("unread-friend-messages:" + userId);
        }
        // 从redis中获取未读通知
        Set<ResponseResult> members = redisCache.redisTemplate.opsForSet().members("unread-add-friend-notify:" + userId);
        if(members.size() > 0) {
            for (ResponseResult member : members) {
                webSocket.sendOneMessage(userId, JSON.toJSONString(member));
            }
        }
        // 删除未读通知
        redisCache.deleteObject("unread-add-friend-notify:" + userId);
        // 从redis中获取未读删除好友消息
        ResponseResult result = (ResponseResult) redisCache.redisTemplate.opsForList().leftPop("unread-delete-friend-messages:" + userId);
        while(!Objects.isNull(friendMessage1)){
            webSocket.sendOneMessage(userId, JSON.toJSONString(result));
            result = (ResponseResult) redisCache.redisTemplate.opsForList().leftPop("unread-delete-friend-messages:" + userId);
        }

        return new ResponseResult(200, "success");
    }

    @Override
    public ResponseResult call(CallFriend callFriend) {
        // 判断对方是否为好友
        LambdaQueryWrapper<Friend> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Friend::getFriendId, callFriend.getToId());
        lambdaQueryWrapper.eq(Friend::getUserId, callFriend.getFromId());
        Friend friend = friendMapper.selectOne(lambdaQueryWrapper);
        if(Objects.isNull(friend)){
            // 不是好友
            return new ResponseResult(0, "对方不是你好友");
        }
        return new ResponseResult(200, "success");
    }
}
