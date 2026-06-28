package com.xueyan.payment.service;

import com.xueyan.payment.dto.PayDTO;
import com.xueyan.payment.dto.PaymentVO;

/**
 * 支付服务接口
 */
public interface PaymentService {

    /**
     * 发起支付（原型阶段模拟支付成功）
     * <p>
     * 流程：创建支付记录 → 模拟支付成功 → 写本地消息表 → 发 MQ 通知订单服务
     */
    PaymentVO pay(PayDTO dto);

    /**
     * 模拟支付回调（第三方支付异步通知）
     */
    PaymentVO mockCallback(String orderNo);
}
