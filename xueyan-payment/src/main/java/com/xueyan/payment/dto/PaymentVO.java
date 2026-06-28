package com.xueyan.payment.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class PaymentVO {

    private Long id;

    private String paymentNo;

    private String orderNo;

    private BigDecimal amount;

    private String paymentMethod;

    private String status;

    private LocalDateTime createdAt;
}
