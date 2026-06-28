package com.xueyan.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xueyan.user.dto.LoginDTO;
import com.xueyan.user.dto.LoginVO;
import com.xueyan.user.dto.RegisterDTO;
import com.xueyan.user.dto.UserVO;
import com.xueyan.user.entity.User;

/**
 * 用户服务接口
 */
public interface UserService extends IService<User> {

    /**
     * 用户注册
     * @param dto 注册信息
     * @return 用户信息（不含密码）
     */
    UserVO register(RegisterDTO dto);

    /**
     * 用户登录
     * @param dto 登录信息
     * @return 登录用户信息
     */
    LoginVO login(LoginDTO dto);

    /**
     * 根据 ID 查询用户（供 Feign 调用）
     */
    UserVO getUserById(Long id);
}
