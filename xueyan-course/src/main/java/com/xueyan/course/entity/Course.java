package com.xueyan.course.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 课程实体 — 对应 xueyan_course.course 表
 */
@Data
@TableName("course")
public class Course {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 课程名称 */
    private String name;

    /** 课程描述 */
    private String description;

    /** 封面图 URL */
    private String coverUrl;

    /** 价格 */
    private BigDecimal price;

    /** 剩余库存（名额） */
    private Integer stock;

    /** 课程分类 */
    private String category;

    /** 讲师姓名 */
    private String teacherName;

    /** 状态：0=下架, 1=上架 */
    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    @TableLogic
    private Integer deleted;
}
