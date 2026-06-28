package com.xueyan.payment.service.impl;

import cn.hutool.core.util.IdUtil;
import com.xueyan.common.exception.BizException;
import com.xueyan.common.result.ResultCode;
import com.xueyan.payment.config.PaymentRabbitMQConfig;
import com.xueyan.payment.dto.PayDTO;
import com.xueyan.payment.dto.PaymentVO;
import com.xueyan.payment.entity.LocalMessage;
import com.xueyan.payment.entity.PaymentRecord;
import com.xueyan.payment.mapper.LocalMessageMapper;
import com.xueyan.payment.mapper.PaymentRecordMapper;
import com.xueyan.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 支付服务实现 — 本地消息表 + RabbitMQ 保障最终一致性
 *
 * <pre>
 * 支付流程（本地消息表模式）：
 *   1. 创建支付记录 → 状态 SUCCESS（原型模拟）
 *   2. 写入本地消息表（PENDING）
 *   3. 发送 RabbitMQ 消息
 *   4. 更新本地消息表状态 → SENT
 *
 * 异常处理：
 *   - 第 3 步发送失败 → 本地消息表定时任务重试
 *   - 订单服务消费失败 → MQ auto-retry + 手动 ACK
 * </pre>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRecordMapper paymentRecordMapper;
    private final LocalMessageMapper localMessageMapper;
    private final RabbitTemplate rabbitTemplate;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PaymentVO pay(PayDTO dto) {
        log.info("【支付开始】orderNo={}, amount={}", dto.getOrderNo(), dto.getAmount());

        // 1. 创建支付记录（模拟支付成功）
        String paymentNo = "PAY" + System.currentTimeMillis() + IdUtil.fastSimpleUUID().substring(0, 6);

        PaymentRecord record = new PaymentRecord();
        record.setPaymentNo(paymentNo);
        record.setOrderNo(dto.getOrderNo());
        record.setUserId(dto.getUserId());
        record.setAmount(dto.getAmount());
        record.setPaymentMethod(dto.getPaymentMethod());
        record.setStatus("SUCCESS");
        record.setCallbackAt(LocalDateTime.now());
        paymentRecordMapper.insert(record);
        log.info("【支付记录创建】paymentNo={}", paymentNo);

        // 2. 写入本地消息表（先写消息再发 MQ，保障不丢）
        String messageBody = "{\"orderNo\":\"" + dto.getOrderNo() + "\",\"paymentNo\":\"" + paymentNo + "\"}";
        LocalMessage localMsg = new LocalMessage();
        localMsg.setOrderNo(dto.getOrderNo());
        localMsg.setMessageType("PAY_CALLBACK");
        localMsg.setMessageBody(messageBody);
        localMsg.setStatus("PENDING");
        localMsg.setRetryCount(0);
        localMsg.setMaxRetry(5);
        localMsg.setNextRetryAt(LocalDateTime.now().plusSeconds(10));
        localMessageMapper.insert(localMsg);
        log.info("【本地消息写入】msgId={}, orderNo={}", localMsg.getId(), dto.getOrderNo());

        // 3. 发送 RabbitMQ 消息
        try {
            rabbitTemplate.convertAndSend(
                    PaymentRabbitMQConfig.PAYMENT_EXCHANGE,
                    PaymentRabbitMQConfig.ROUTING_KEY_PAY_CALLBACK,
                    messageBody
            );
            log.info("【MQ 消息已发送】orderNo={}", dto.getOrderNo());

            // 4. 更新本地消息表状态
            localMsg.setStatus("SENT");
            localMessageMapper.updateById(localMsg);

        } catch (Exception e) {
            log.error("【MQ 发送失败，本地消息表将重试】orderNo={}", dto.getOrderNo(), e);
            // 不抛异常，由定时任务重试
        }

        log.info("【支付完成】orderNo={}, paymentNo={}", dto.getOrderNo(), paymentNo);
        return toVO(record);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PaymentVO mockCallback(String orderNo) {
        log.info("【模拟支付回调】orderNo={}", orderNo);

        // 原型阶段直接模拟支付成功
        String paymentNo = "PAY" + System.currentTimeMillis() + IdUtil.fastSimpleUUID().substring(0, 6);

        PaymentRecord record = new PaymentRecord();
        record.setPaymentNo(paymentNo);
        record.setOrderNo(orderNo);
        record.setUserId(0L); // 原型简化
        record.setAmount(java.math.BigDecimal.ZERO);
        record.setPaymentMethod("WECHAT");
        record.setStatus("SUCCESS");
        record.setCallbackAt(LocalDateTime.now());
        paymentRecordMapper.insert(record);

        // 发送 MQ 通知订单服务
        String messageBody = "{\"orderNo\":\"" + orderNo + "\",\"paymentNo\":\"" + paymentNo + "\"}";
        rabbitTemplate.convertAndSend(
                PaymentRabbitMQConfig.PAYMENT_EXCHANGE,
                PaymentRabbitMQConfig.ROUTING_KEY_PAY_CALLBACK,
                messageBody
        );

        log.info("【回调处理完成】orderNo={}", orderNo);
        return toVO(record);
    }

    private PaymentVO toVO(PaymentRecord record) {
        return PaymentVO.builder()
                .id(record.getId())
                .paymentNo(record.getPaymentNo())
                .orderNo(record.getOrderNo())
                .amount(record.getAmount())
                .paymentMethod(record.getPaymentMethod())
                .status(record.getStatus())
                .createdAt(record.getCreatedAt())
                .build();
    }
}
