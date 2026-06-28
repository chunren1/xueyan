package com.xueyan.user.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户信息对外暴露字段（不含密码）
 */
@Data
@Builder
public class UserVO {

    private Long id;

    private String username;

    private String nickname;

    private String email;

    private String phone;

    private Integer status;

    private LocalDateTime createdAt;
}
