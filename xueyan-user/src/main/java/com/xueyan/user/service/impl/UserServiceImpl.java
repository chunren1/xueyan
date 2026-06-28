package com.xueyan.user.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xueyan.common.exception.BizException;
import com.xueyan.common.result.ResultCode;
import com.xueyan.user.dto.LoginDTO;
import com.xueyan.user.dto.LoginVO;
import com.xueyan.user.dto.RegisterDTO;
import com.xueyan.user.dto.UserVO;
import com.xueyan.user.entity.User;
import com.xueyan.user.mapper.UserMapper;
import com.xueyan.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 用户服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final PasswordEncoder passwordEncoder;

    @Override
    public UserVO register(RegisterDTO dto) {
        // 1. 校验用户名是否已存在
        long count = count(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, dto.getUsername()));
        if (count > 0) {
            throw new BizException(ResultCode.USER_ALREADY_EXISTS);
        }

        // 2. 构建用户实体并加密密码
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setNickname(dto.getNickname() != null ? dto.getNickname() : dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setStatus(1); // 默认正常

        // 3. 保存到数据库
        save(user);
        log.info("用户注册成功 → username={}, id={}", user.getUsername(), user.getId());

        // 4. 返回用户信息（不含密码）
        return UserVO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .phone(user.getPhone())
                .status(user.getStatus())
                .createdAt(user.getCreatedAt())
                .build();
    }

    @Override
    public LoginVO login(LoginDTO dto) {
        // 1. 根据用户名查询用户
        User user = getOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, dto.getUsername()));
        if (user == null) {
            throw new BizException(ResultCode.USER_NOT_FOUND);
        }

        // 2. 校验密码
        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new BizException(ResultCode.PASSWORD_ERROR);
        }

        // 3. 校验用户状态
        if (user.getStatus() == 0) {
            throw new BizException(ResultCode.FORBIDDEN.getCode(), "账号已被禁用");
        }

        log.info("用户登录成功 → username={}", user.getUsername());

        return LoginVO.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .build();
    }

    @Override
    public UserVO getUserById(Long id) {
        User user = getById(id);
        if (user == null) {
            throw new BizException(ResultCode.USER_NOT_FOUND);
        }

        return UserVO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .phone(user.getPhone())
                .status(user.getStatus())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
