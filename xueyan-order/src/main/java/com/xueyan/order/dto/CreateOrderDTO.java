package com.xueyan.order.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 创建订单请求
 */
@Data
public class CreateOrderDTO {

    @NotNull(message = "用户ID不能为空")
    private Long userId;

    @NotNull(message = "课程ID不能为空")
    private Long courseId;

    @Min(value = 1, message = "购买数量至少为1")
    private Integer quantity = 1;

    /** 支付方式（暂不校验，阶段四扩展） */
    private String paymentMethod = "WECHAT";
}
