package com.ghr.chat.domain.vo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.ghr.chat.domain.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FriendsVO implements Serializable {

    @TableId
    private Long id;
    private Long userId;
    private Long friendId;
    private String friendNikeName;
    private User friendDetail;
}
