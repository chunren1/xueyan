package com.xueyan.order.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xueyan.common.exception.BizException;
import com.xueyan.common.result.ResultCode;
import com.xueyan.order.config.OrderRabbitMQConfig;
import com.xueyan.order.dto.CreateOrderDTO;
import com.xueyan.order.dto.OrderVO;
import com.xueyan.order.entity.LocalMessage;
import com.xueyan.order.entity.OrderInfo;
import com.xueyan.order.feign.CourseFeignClient;
import com.xueyan.order.feign.dto.CourseFeignVO;
import com.xueyan.order.mapper.LocalMessageMapper;
import com.xueyan.order.mapper.OrderInfoMapper;
import com.xueyan.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单服务实现 — Seata 分布式事务 + RabbitMQ 异步消息
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderInfoMapper orderInfoMapper;
    private final LocalMessageMapper localMessageMapper;
    private final CourseFeignClient courseFeignClient;
    private final RabbitTemplate rabbitTemplate;

    /**
     * 创建订单 — Seata 全局事务
     * <p>
     * 事务边界：order_info 写入 + 课程服务扣库存（Feign）
     * 如果任一操作失败，Seata 自动回滚两端数据。
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderVO createOrder(CreateOrderDTO dto) {
        log.info("【下单开始】userId={}, courseId={}, quantity={}", dto.getUserId(), dto.getCourseId(), dto.getQuantity());

        // 1. 查询课程信息（Feign 调用课程服务）
        CourseFeignVO course = courseFeignClient.getCourseById(dto.getCourseId());

        // 2. 构建订单
        String orderNo = generateOrderNo();
        BigDecimal totalAmount = course.getPrice().multiply(BigDecimal.valueOf(dto.getQuantity()));

        OrderInfo order = new OrderInfo();
        order.setOrderNo(orderNo);
        order.setUserId(dto.getUserId());
        order.setCourseId(dto.getCourseId());
        order.setCourseName(course.getName());
        order.setAmount(totalAmount);
        order.setStatus("WAIT_PAY");

        // 3. 写入订单表（本地 DB — 由 Seata 管理）
        int inserted = orderInfoMapper.insert(order);
        if (inserted <= 0) {
            throw new BizException(500, "订单创建失败");
        }
        log.info("【下单】订单记录已写入 → orderNo={}", orderNo);

        // 4. 扣减课程库存（Feign 调用课程服务 —由 Seata 协调回滚）
        boolean deducted = courseFeignClient.deductStock(dto.getCourseId(), dto.getQuantity());
        if (!deducted) {
            log.error("【下单失败】库存不足 → courseId={}", dto.getCourseId());
            throw new BizException(ResultCode.COURSE_STOCK_INSUFFICIENT);
        }
        log.info("【下单】库存扣减成功 → courseId={}, count={}", dto.getCourseId(), dto.getQuantity());

        // 5. 发送延迟消息（30 秒后检查是否已支付，未支付则取消）
        sendDelayCancelMessage(order.getId(), orderNo);
        log.info("【下单】延迟取消消息已发送 → orderNo={}, TTL=30s", orderNo);

        log.info("【下单完成】orderNo={}, amount={}", orderNo, totalAmount);
        return toVO(order);
    }

    /**
     * 取消订单 — 由延迟队列消息触发
     * <p>
     * 此时的调用不在 Seata 全局事务中，是独立的本地事务 + 补偿操作
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelOrder(Long orderId, String orderNo) {
        log.info("【取消订单】消息到达 → orderId={}, orderNo={}", orderId, orderNo);

        OrderInfo order = orderInfoMapper.selectById(orderId);
        if (order == null) {
            log.warn("【取消订单】订单不存在 → orderId={}", orderId);
            return;
        }

        // 幂等校验：只有待支付状态的订单才能取消
        if (!"WAIT_PAY".equals(order.getStatus())) {
            log.info("【取消订单】订单已处理，跳过 → orderNo={}, status={}", orderNo, order.getStatus());
            return;
        }

        // 更新订单状态
        order.setStatus("CANCELLED");
        orderInfoMapper.updateById(order);

        // 补偿：恢复课程库存（Feign 调用课程服务 restoreStock 接口）
        try {
            boolean restored = courseFeignClient.restoreStock(order.getCourseId(), 1);
            log.info("【取消订单】库存恢复 → courseId={}, result={}", order.getCourseId(), restored);
        } catch (Exception e) {
            log.error("【取消订单】恢复库存失败（需人工介入） → orderNo={}", orderNo, e);
            // 记录到本地消息表，后续定时任务重试
            saveLocalMessage(orderNo, "ORDER_CANCEL_RESTORE",
                    "{\"courseId\":" + order.getCourseId() + ", \"orderId\":" + orderId + "}");
        }

        log.info("【取消订单完成】orderNo={}", orderNo);
    }

    /**
     * 处理支付成功回调
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void handlePaymentCallback(String orderNo) {
        log.info("【支付回调】orderNo={}", orderNo);

        OrderInfo order = getByOrderNo(orderNo);
        if (order == null) {
            log.warn("【支付回调】订单不存在 → orderNo={}", orderNo);
            return;
        }

        if (!"WAIT_PAY".equals(order.getStatus())) {
            log.info("【支付回调】订单已处理，跳过 → orderNo={}, status={}", orderNo, order.getStatus());
            return;
        }

        order.setStatus("PAID");
        orderInfoMapper.updateById(order);
        log.info("【支付回调完成】orderNo={} → PAID", orderNo);
    }

    @Override
    public OrderVO getOrderByNo(String orderNo) {
        OrderInfo order = getByOrderNo(orderNo);
        if (order == null) {
            throw new BizException(ResultCode.ORDER_NOT_FOUND);
        }
        return toVO(order);
    }

    // ==================== 内部工具方法 ====================

    /**
     * 发送延迟取消消息到 RabbitMQ
     */
    private void sendDelayCancelMessage(Long orderId, String orderNo) {
        String message = "{\"orderId\":" + orderId + ",\"orderNo\":\"" + orderNo + "\"}";
        rabbitTemplate.convertAndSend(
                OrderRabbitMQConfig.ORDER_EXCHANGE,
                OrderRabbitMQConfig.ROUTING_KEY_CREATE,
                message
        );
    }

    /**
     * 记录到本地消息表（用于后续重试）
     */
    private void saveLocalMessage(String orderNo, String messageType, String messageBody) {
        LocalMessage msg = new LocalMessage();
        msg.setOrderNo(orderNo);
        msg.setMessageType(messageType);
        msg.setMessageBody(messageBody);
        msg.setStatus("PENDING");
        msg.setRetryCount(0);
        msg.setMaxRetry(5);
        msg.setNextRetryAt(LocalDateTime.now().plusMinutes(1));
        localMessageMapper.insert(msg);
    }

    private OrderInfo getByOrderNo(String orderNo) {
        return orderInfoMapper.selectOne(new LambdaQueryWrapper<OrderInfo>()
                .eq(OrderInfo::getOrderNo, orderNo));
    }

    private String generateOrderNo() {
        return "XO" + System.currentTimeMillis() + IdUtil.fastSimpleUUID().substring(0, 8);
    }

    private OrderVO toVO(OrderInfo order) {
        return OrderVO.builder()
                .id(order.getId())
                .orderNo(order.getOrderNo())
                .userId(order.getUserId())
                .courseId(order.getCourseId())
                .courseName(order.getCourseName())
                .amount(order.getAmount())
                .status(order.getStatus())
                .createdAt(order.getCreatedAt())
                .build();
    }
}
