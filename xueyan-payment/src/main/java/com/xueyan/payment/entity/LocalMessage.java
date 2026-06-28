package com.xueyan.payment.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 本地消息表（支付服务侧） — 保障支付回调消息可靠投递
 */
@Data
@TableName("local_message")
public class LocalMessage {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 关联订单号 */
    private String orderNo;

    /** 消息类型 */
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
