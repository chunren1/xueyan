package com.xueyan.payment.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 支付记录实体 — 对应 xueyan_payment.payment_record 表
 */
@Data
@TableName("payment_record")
public class PaymentRecord {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 支付流水号 */
    private String paymentNo;

    /** 关联订单号 */
    private String orderNo;

    /** 用户ID */
    private Long userId;

    /** 支付金额 */
    private BigDecimal amount;

    /** 支付方式：WECHAT / ALIPAY */
    private String paymentMethod;

    /** 状态：PENDING / SUCCESS / FAILED */
    private String status;

    /** 回调时间 */
    private LocalDateTime callbackAt;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
