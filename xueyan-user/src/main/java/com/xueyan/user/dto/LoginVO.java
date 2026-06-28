package com.xueyan.user.dto;

import lombok.Builder;
import lombok.Data;

/**
 * 登录返回值
 */
@Data
@Builder
public class LoginVO {

    private Long userId;

    private String username;

    private String nickname;
}
