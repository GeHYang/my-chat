package com.ghr.chat.domain.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;


@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("chat_user")
public class User implements Serializable {

    @TableId
    private Long id;
    private String userName;
    @TableField(updateStrategy = FieldStrategy.NEVER)
    @JSONField(serialize = false)
    private String password;
    @TableField(exist = false)
    @JSONField(serialize = false)
    private String checkPassword;
    private String nikeName;
    private Integer sex;
    private String birthday;
    private String address;
    private String email;
    private Long phoneNumber;
    private String intro;
    private String avatar;
    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
    private Integer delFlag;
    private Integer status;

}
