package com.xueyan.course.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xueyan.common.exception.BizException;
import com.xueyan.common.result.ResultCode;
import com.xueyan.course.dto.CourseVO;
import com.xueyan.course.entity.Course;
import com.xueyan.course.mapper.CourseMapper;
import com.xueyan.course.service.CourseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 课程服务实现 — 集成 Redis 缓存
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CourseServiceImpl extends ServiceImpl<CourseMapper, Course> implements CourseService {

    /**
     * 查询课程列表 — 缓存 5 分钟
     * key: course:list::{category}:{keyword}:{page}:{size}
     */
    @Override
    @Cacheable(value = "courseList", key = "#category+':'+#keyword+':'+#page+':'+#size", unless = "#result == null || #result.isEmpty()")
    public List<CourseVO> listCourses(String category, String keyword, int page, int size) {
        log.info("查询课程列表（读库）→ category={}, keyword={}", category, keyword);

        LambdaQueryWrapper<Course> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Course::getStatus, 1); // 只查上架课程

        if (category != null && !category.isBlank()) {
            wrapper.eq(Course::getCategory, category);
        }
        if (keyword != null && !keyword.isBlank()) {
            wrapper.like(Course::getName, keyword);
        }
        wrapper.orderByDesc(Course::getId);

        Page<Course> pageResult = page(new Page<>(page, size), wrapper);
        return pageResult.getRecords().stream()
                .map(this::toVO)
                .toList();
    }

    /**
     * 查询课程详情 — 缓存 10 分钟
     * key: course:detail::{courseId}
     */
    @Override
    @Cacheable(value = "courseDetail", key = "#courseId", unless = "#result == null")
    public CourseVO getCourseDetail(Long courseId) {
        log.info("查询课程详情（读库）→ courseId={}", courseId);

        Course course = getById(courseId);
        if (course == null || course.getStatus() == 0) {
            throw new BizException(ResultCode.COURSE_NOT_FOUND);
        }
        return toVO(course);
    }

    /**
     * 扣减库存 — 清除相关缓存
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = {"courseList", "courseDetail"}, allEntries = true)
    public boolean deductStock(Long courseId, int count) {
        log.info("扣减库存 → courseId={}, count={}", courseId, count);

        // 使用行级锁确保并发安全
        Course course = getById(courseId);
        if (course == null) {
            throw new BizException(ResultCode.COURSE_NOT_FOUND);
        }
        if (course.getStock() < count) {
            log.warn("库存不足 → courseId={}, 剩余={}, 需要={}", courseId, course.getStock(), count);
            return false;
        }

        // UPDATE course SET stock = stock - #{count} WHERE id = #{courseId} AND stock >= #{count}
        boolean updated = lambdaUpdate()
                .setSql("stock = stock - " + count)
                .eq(Course::getId, courseId)
                .ge(Course::getStock, count)
                .update();
        if (updated) {
            log.info("库存扣减成功 → courseId={}, 扣减={}, 剩余={}", courseId, count, course.getStock() - count);
        }
        return updated;
    }

    /**
     * 恢复库存 — 订单取消时补偿（非事务性，由调用方控制）
     */
    @Override
    @CacheEvict(value = {"courseList", "courseDetail"}, allEntries = true)
    public boolean restoreStock(Long courseId, int count) {
        log.info("恢复库存 → courseId={}, count={}", courseId, count);
        boolean updated = lambdaUpdate()
                .setSql("stock = stock + " + count)
                .eq(Course::getId, courseId)
                .update();
        log.info("库存恢复{} → courseId={}", updated ? "成功" : "失败", courseId);
        return updated;
    }

    @Override
    public CourseVO getCourseById(Long courseId) {
        Course course = getById(courseId);
        if (course == null) {
            throw new BizException(ResultCode.COURSE_NOT_FOUND);
        }
        return toVO(course);
    }

    // ==================== 内部工具方法 ====================

    private CourseVO toVO(Course course) {
        return CourseVO.builder()
                .id(course.getId())
                .name(course.getName())
                .description(course.getDescription())
                .coverUrl(course.getCoverUrl())
                .price(course.getPrice())
                .stock(course.getStock())
                .category(course.getCategory())
                .teacherName(course.getTeacherName())
                .status(course.getStatus())
                .createdAt(course.getCreatedAt())
                .build();
    }
}
