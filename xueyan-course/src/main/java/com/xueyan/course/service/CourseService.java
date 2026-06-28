package com.xueyan.course.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xueyan.course.dto.CourseVO;
import com.xueyan.course.entity.Course;

import java.util.List;

/**
 * 课程服务接口
 */
public interface CourseService extends IService<Course> {

    /**
     * 分页查询课程列表（Redis 缓存）
     * @param category 分类筛选（可选）
     * @param keyword  关键词搜索（可选）
     * @param page     页码
     * @param size     每页条数
     * @return 课程列表
     */
    List<CourseVO> listCourses(String category, String keyword, int page, int size);

    /**
     * 查询课程详情（Redis 缓存）
     */
    CourseVO getCourseDetail(Long courseId);

    /**
     * 扣减课程库存（供订单服务 Feign 调用，需要分布式事务协调）
     * @return true=扣减成功, false=库存不足
     */
    boolean deductStock(Long courseId, int count);

    /**
     * 恢复课程库存（订单取消时补偿）
     */
    boolean restoreStock(Long courseId, int count);

    /**
     * 根据 ID 查询课程信息（供 Feign 内部调用）
     */
    CourseVO getCourseById(Long courseId);
}
