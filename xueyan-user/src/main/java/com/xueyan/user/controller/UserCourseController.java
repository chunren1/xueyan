package com.xueyan.user.controller;

import com.xueyan.common.result.Result;
import com.xueyan.user.feign.CourseFeignClient;
import com.xueyan.user.feign.dto.CourseFeignVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户-课程关联 Controller
 * <p>
 * 演示 user → course 的 OpenFeign 远程调用
 */
@Slf4j
@Tag(name = "用户课程", description = "演示 Feign 远程调用 user → course")
@RestController
@RequestMapping("/user/course")
@RequiredArgsConstructor
public class UserCourseController {

    private final CourseFeignClient courseFeignClient;

    /**
     * 根据课程 ID 查询课程信息（通过 Feign 调课程服务）
     * <p>
     * 调用链路：浏览器 → Gateway → xueyan-user → (Feign) → xueyan-course
     */
    @Operation(summary = "通过 Feign 查询课程信息（演示 user → course 远程调用）")
    @GetMapping("/{courseId}")
    public Result<Map<String, Object>> getCourseViaFeign(@PathVariable Long courseId) {
        log.info("Feign 调用 → 查询课程 courseId={}", courseId);

        // 通过 Feign 远程调用课程服务
        Result<CourseFeignVO> courseResult = courseFeignClient.getCourseById(courseId);

        Map<String, Object> result = new HashMap<>();
        result.put("source", "xueyan-user 通过 Feign 调用 xueyan-course");
        result.put("course", courseResult.getData());
        return Result.success(result);
    }
}
