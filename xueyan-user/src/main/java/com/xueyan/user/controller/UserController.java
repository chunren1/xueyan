package com.xueyan.user.controller;

import com.xueyan.common.result.Result;
import com.xueyan.user.dto.LoginDTO;
import com.xueyan.user.dto.LoginVO;
import com.xueyan.user.dto.RegisterDTO;
import com.xueyan.user.dto.UserVO;
import com.xueyan.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 用户服务 Controller
 */
@Tag(name = "用户服务", description = "注册、登录、用户信息查询")
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "用户注册")
    @PostMapping("/register")
    public Result<UserVO> register(@Valid @RequestBody RegisterDTO dto) {
        UserVO vo = userService.register(dto);
        return Result.success("注册成功", vo);
    }

    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public Result<LoginVO> login(@Valid @RequestBody LoginDTO dto) {
        LoginVO vo = userService.login(dto);
        return Result.success("登录成功", vo);
    }

    @Operation(summary = "根据 ID 查询用户（供 Feign 内部调用）")
    @GetMapping("/{id}")
    public Result<UserVO> getUserById(@PathVariable Long id) {
        UserVO vo = userService.getUserById(id);
        return Result.success(vo);
    }
}
