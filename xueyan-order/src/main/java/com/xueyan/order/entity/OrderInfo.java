package com.xueyan.order.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单实体 — 对应 xueyan_order.order_info 表
 */
@Data
@TableName("order_info")
public class OrderInfo {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 订单编号（业务唯一，格式：XO + 时间戳 + 随机数） */
    private String orderNo;

    /** 用户ID */
    private Long userId;

    /** 课程ID */
    private Long courseId;

    /** 课程名称（冗余，避免跨服务查询） */
    private String courseName;

    /** 实付金额 */
    private BigDecimal amount;

    /** 
     * 订单状态
     * WAIT_PAY   — 待支付
     * PAID       — 已支付
     * COMPLETED  — 已完成
     * CANCELLED  — 已取消
     */
    private String status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    @TableLogic
    private Integer deleted;
}
