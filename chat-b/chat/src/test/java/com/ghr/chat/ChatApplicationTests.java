package com.ghr.chat;

import com.ghr.chat.dao.FriendMapper;
import com.ghr.chat.domain.entity.Friend;
import com.ghr.chat.domain.entity.User;
import com.ghr.chat.domain.vo.FriendsVO;
import com.ghr.chat.service.FriendService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest
public class ChatApplicationTests {

    @Autowired
    PasswordEncoder passwordEncoder;

    @Test
    public void testBCryptPasswordEncoder(){
        String encode1 = new BCryptPasswordEncoder().encode("123456");
        String encode2 = passwordEncoder.encode("123456");
        System.out.println(encode2);
        System.out.println(encode1);
        System.out.println(passwordEncoder.matches("123456", encode2));
        System.out.println(passwordEncoder.matches("123456", encode1));
    }

    @Autowired
    private FriendMapper friendMapper;
    @Test
    public void testSelectFriendAndFriendDetail(){
        List<FriendsVO> friendsVOS = friendMapper.selectFriendAndFriendDetail(1L);
        for (FriendsVO friendsVO : friendsVOS) {
            System.out.println(friendsVO);
        }
    }
    @Test
    public void testSelectFriendByUserIdAndLikeUserName(){
        Map map = new HashMap<>();
        map.put("userId", 1);
        map.put("userName", "zhangsan");
        List<User> list = friendMapper.selectFriendByUserIdAndLikeUserName(map);
        for (User o : list) {
            System.out.println(o);
        }
    }
}
