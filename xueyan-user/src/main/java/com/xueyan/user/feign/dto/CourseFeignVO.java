package com.xueyan.user.feign.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 课程信息 VO — Feign 调用返回值
 * <p>
 * 注意：微服务中各服务维护自己的 DTO，不共享实体类。
 * 这避免了服务间耦合，允许各自独立演进。
 */
@Data
public class CourseFeignVO {

    private Long id;

    private String name;

    private BigDecimal price;

    private Integer stock;

    private String category;

    private String teacherName;
}
