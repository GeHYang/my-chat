package com.ghr.chat.controller;

import com.ghr.chat.common.ResponseResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/websocket")
public class TestWebSocket {

    @PostMapping("/sendMsg")
    public ResponseResult testWebsocket(String msg){
        System.out.println(msg);
        return new ResponseResult(200, "----> 服务器收到");
    }
}
