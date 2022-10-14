package com.ghr.chat.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CallFriend {
    private Long fromId;
    private Long toId;
    private Object offer;
}
