package com.ghr.chat.controller;

import com.alibaba.fastjson.JSON;
import com.ghr.chat.common.ResponseResult;
import com.ghr.chat.domain.entity.FriendMessage;
import com.ghr.chat.domain.vo.CallFriend;
import com.ghr.chat.handler.WebSocket;
import com.ghr.chat.service.FriendMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController
@RequestMapping("/friendMessage")
public class FriendMessageController {

    @Autowired
    private FriendMessageService friendMessageService;
    @Autowired
    private WebSocket webSocket;

    @PostMapping
    public ResponseResult save(@RequestBody FriendMessage friendMessage){

        return friendMessageService.messageCache(friendMessage);
    }

    @PostMapping("/getAllUnreadMessage")
    public ResponseResult getAllUnreadMessage(@RequestBody FriendMessage friendMessage){
        return friendMessageService.getAllUnreadMessage(friendMessage);
    }

    @PostMapping("/call")
    public ResponseResult call(@RequestBody CallFriend callFriend){
        ResponseResult responseResult = new ResponseResult(200, "callFriend", callFriend);
        webSocket.sendOneMessage(callFriend.getToId().toString(), JSON.toJSONString(responseResult));
        return friendMessageService.call(callFriend);
    }
}
