package com.xueyan.course.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 课程对外展示（不含内部字段）
 */
@Data
@Builder
public class CourseVO {

    private Long id;

    private String name;

    private String description;

    private String coverUrl;

    private BigDecimal price;

    private Integer stock;

    private String category;

    private String teacherName;

    private Integer status;

    private LocalDateTime createdAt;
}
