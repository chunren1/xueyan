package com.xueyan.order.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * RabbitMQ 延迟队列 + 死信交换机配置
 *
 * <pre>
 * 架构设计：
 *   下单时发送消息 → order.delay.queue（TTL=30s）
 *        ↓ TTL 过期
 *   自动投递到 → order.dlx.exchange → order.dlx.queue
 *        ↓
 *   OrderCancelListener 消费 → 检查订单状态，若仍为 WAIT_PAY 则取消
 *
 * 原型阶段 TTL 设为 30 秒方便验证，生产环境改为 30 分钟（1800000ms）
 * </pre>
 */
@Configuration
public class OrderRabbitMQConfig {

    // ==================== 交换机 ====================

    /** 订单业务交换机 */
    public static final String ORDER_EXCHANGE = "xueyan.order.exchange";

    /** 死信交换机（接收过期消息） */
    public static final String ORDER_DLX_EXCHANGE = "xueyan.order.dlx.exchange";

    // ==================== 队列 ====================

    /** 延迟队列（消息在此等待 TTL 过期） */
    public static final String ORDER_DELAY_QUEUE = "xueyan.order.delay.queue";

    /** 死信队列（TTL 过期后消息投递到此） */
    public static final String ORDER_DLX_QUEUE = "xueyan.order.dlx.queue";

    // ==================== 路由键 ====================

    /** 创建订单 → 延迟队列 */
    public static final String ROUTING_KEY_CREATE = "order.create";

    /** 死信队列消费路由键 */
    public static final String ROUTING_KEY_CANCEL = "order.cancel";

    // ==================== Bean 定义 ====================

    /**
     * 订单业务交换机（Topic 类型）
     */
    @Bean
    public TopicExchange orderExchange() {
        return new TopicExchange(ORDER_EXCHANGE, true, false);
    }

    /**
     * 死信交换机（Topic 类型）
     */
    @Bean
    public TopicExchange orderDlxExchange() {
        return new TopicExchange(ORDER_DLX_EXCHANGE, true, false);
    }

    /**
     * 延迟队列 — 设置 TTL + 死信交换机
     * <p>
     * 消息在此队列等待 TTL 过期，然后自动转发到死信交换机。
     */
    @Bean
    public Queue orderDelayQueue() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-dead-letter-exchange", ORDER_DLX_EXCHANGE);   // 死信交换机
        args.put("x-dead-letter-routing-key", ROUTING_KEY_CANCEL); // 死信路由键
        args.put("x-message-ttl", 30_000); // 原型阶段 30 秒，生产改为 1_800_000（30 分钟）
        return new Queue(ORDER_DELAY_QUEUE, true, false, false, args);
    }

    /**
     * 死信队列 — 接收 TTL 过期的消息，由 OrderCancelListener 消费
     */
    @Bean
    public Queue orderDlxQueue() {
        return new Queue(ORDER_DLX_QUEUE, true, false, false);
    }

    /**
     * 延迟队列 绑定 业务交换机（通过 order.create 路由键）
     */
    @Bean
    public Binding delayQueueBinding() {
        return BindingBuilder.bind(orderDelayQueue())
                .to(orderExchange())
                .with(ROUTING_KEY_CREATE);
    }

    /**
     * 死信队列 绑定 死信交换机（通过 order.cancel 路由键）
     */
    @Bean
    public Binding dlxQueueBinding() {
        return BindingBuilder.bind(orderDlxQueue())
                .to(orderDlxExchange())
                .with(ROUTING_KEY_CANCEL);
    }
}


