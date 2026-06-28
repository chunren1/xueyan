package com.xueyan.order.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 本地消息表 — 保障最终一致性
 * <p>
 * 场景：支付服务收到回调后，先写本地消息表再发 MQ，
 * 发送失败则由定时任务重试，确保消息不丢。
 * <p>
 * 表：xueyan_order.local_message
 */
@Data
@TableName("local_message")
public class LocalMessage {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 关联订单号 */
    private String orderNo;

    /** 消息类型：PAY_CALLBACK / ORDER_CANCEL */
    private String messageType;

    /** 消息体 JSON */
    private String messageBody;

    /** 状态：PENDING→SENT→CONFIRMED */
    private String status;

    /** 已重试次数 */
    private Integer retryCount;

    /** 最大重试次数 */
    private Integer maxRetry;

    /** 下次重试时间 */
    private LocalDateTime nextRetryAt;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
