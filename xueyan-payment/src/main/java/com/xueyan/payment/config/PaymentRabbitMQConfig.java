package com.xueyan.payment.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 支付服务 RabbitMQ 配置（生产者端）
 * <p>
 * 注意：交换机/队列的名称必须与消费者端（xueyan-order）保持一致
 */
@Configuration
public class PaymentRabbitMQConfig {

    /** 支付回调交换机 */
    public static final String PAYMENT_EXCHANGE = "xueyan.payment.exchange";

    /** 支付回调队列 */
    public static final String PAYMENT_CALLBACK_QUEUE = "xueyan.payment.callback.queue";

    /** 支付回调路由键 */
    public static final String ROUTING_KEY_PAY_CALLBACK = "payment.callback";

    @Bean
    public TopicExchange paymentExchange() {
        return new TopicExchange(PAYMENT_EXCHANGE, true, false);
    }

    @Bean
    public Queue paymentCallbackQueue() {
        return new Queue(PAYMENT_CALLBACK_QUEUE, true, false, false);
    }

    @Bean
    public Binding paymentCallbackBinding() {
        return BindingBuilder.bind(paymentCallbackQueue())
                .to(paymentExchange())
                .with(ROUTING_KEY_PAY_CALLBACK);
    }
}
