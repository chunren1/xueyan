package com.xueyan.payment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 发起支付请求
 */
@Data
public class PayDTO {

    @NotBlank(message = "订单号不能为空")
    private String orderNo;

    @NotNull(message = "用户ID不能为空")
    private Long userId;

    @NotNull(message = "金额不能为空")
    private BigDecimal amount;

    /** 支付方式（默认微信） */
    private String paymentMethod = "WECHAT";
}
