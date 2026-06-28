package com.xueyan.order.feign.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 课程信息（Feign 返回值，独立 DTO）
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
