package com.ghr.chat.controller;

import com.ghr.chat.common.ResponseResult;
import com.ghr.chat.domain.vo.AddFriendNotify;
import com.ghr.chat.domain.vo.FriendsVO;
import com.ghr.chat.service.FriendService;
import com.ghr.chat.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/friend")
public class FriendController {

    @Autowired
    private FriendService friendService;

    @PostMapping("/findFriends")
    public ResponseResult findFriends(Long userId){
        return friendService.selectFriendAndFriendDetail(userId);
    }

    @PostMapping("/searchFriend")
    public ResponseResult searchFriend(@RequestParam("userName") String userName, HttpServletRequest request){
        String token = request.getHeader("token");
        Long userId;
        try {
            Claims claims = JwtUtil.parseJWT(token);
            userId = Long.parseLong(claims.getSubject());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return friendService.searchFriend(userName, userId);
    }

    @PostMapping("/addFriend")
    public ResponseResult addFriend(@RequestBody AddFriendNotify friendNotify){
        return friendService.addFriend(friendNotify);
    }

    @PostMapping("/setNikeName")
    public ResponseResult setNikeName(@RequestBody FriendsVO friendsVO){
        return friendService.setNikeName(friendsVO);
    }

    @DeleteMapping
    public ResponseResult delFriend(@RequestBody FriendsVO friendsVO){
        return friendService.delFriend(friendsVO);
    }
}
