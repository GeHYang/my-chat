package com.ghr.chat.domain.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author: Yang
 * @create: 2022-12-02
 * @Description:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("chat_secret")
public class Secret implements Serializable {
    private Long id;
    private Long userId;
    private String p;
    private String g;
    private String publicKey;
    @TableField(fill = FieldFill.INSERT)
    @JSONField(serialize = false)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @JSONField(serialize = false)
    private LocalDateTime updateTime;
}
