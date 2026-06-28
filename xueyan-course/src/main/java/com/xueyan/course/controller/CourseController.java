package com.xueyan.course.controller;

import com.xueyan.common.result.Result;
import com.xueyan.course.dto.CourseVO;
import com.xueyan.course.service.CourseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 课程服务 Controller
 */
@Tag(name = "课程服务", description = "课程列表、详情、库存管理")
@RestController
@RequestMapping("/course")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    @Operation(summary = "课程列表（支持分类筛选和关键词搜索）")
    @GetMapping("/list")
    public Result<List<CourseVO>> list(
            @Parameter(description = "分类") @RequestParam(required = false) String category,
            @Parameter(description = "关键词") @RequestParam(required = false) String keyword,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "10") int size) {
        List<CourseVO> list = courseService.listCourses(category, keyword, page, size);
        return Result.success(list);
    }

    @Operation(summary = "课程详情")
    @GetMapping("/{id}")
    public Result<CourseVO> detail(@PathVariable Long id) {
        CourseVO vo = courseService.getCourseDetail(id);
        return Result.success(vo);
    }

    @Operation(summary = "课程信息查询（供 Feign 内部调用）")
    @GetMapping("/feign/{id}")
    public Result<CourseVO> getCourseForFeign(@PathVariable Long id) {
        CourseVO vo = courseService.getCourseById(id);
        return Result.success(vo);
    }

    @Operation(summary = "扣减库存（供订单服务 Feign 调用）")
    @PutMapping("/{id}/stock/deduct")
    public Result<Boolean> deductStock(
            @PathVariable Long id,
            @Parameter(description = "扣减数量") @RequestParam(defaultValue = "1") int count) {
        boolean success = courseService.deductStock(id, count);
        return Result.success(success);
    }

    @Operation(summary = "恢复库存（订单取消补偿）")
    @PutMapping("/{id}/stock/restore")
    public Result<Boolean> restoreStock(
            @PathVariable Long id,
            @Parameter(description = "恢复数量") @RequestParam(defaultValue = "1") int count) {
        boolean success = courseService.restoreStock(id, count);
        return Result.success(success);
    }
}
