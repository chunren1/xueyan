package com.xueyan.order.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单返回信息
 */
@Data
@Builder
public class OrderVO {

    private Long id;

    private String orderNo;

    private Long userId;

    private Long courseId;

    private String courseName;

    private BigDecimal amount;

    private String status;

    private LocalDateTime createdAt;
}
