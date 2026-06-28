package com.xueyan.order.listener;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.rabbitmq.client.Channel;
import com.xueyan.order.config.OrderRabbitMQConfig;
import com.xueyan.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 订单取消监听器 — 消费死信队列中的过期消息
 * <p>
 * 消息链路：
 * 下单时发送 → order.delay.queue（TTL=30s）
 * → 过期 → order.dlx.exchange → order.dlx.queue
 * → 此监听器消费 → 检查并取消订单
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OrderCancelListener {

    private final OrderService orderService;

    /**
     * 监听死信队列，处理超时未支付订单
     */
    @RabbitListener(queues = OrderRabbitMQConfig.ORDER_DLX_QUEUE)
    public void handleOrderCancel(String messageJson, Channel channel,
                                  @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {
        log.info("【延迟消息到达】死信队列收到消息 → {}", messageJson);

        try {
            JSONObject msg = JSON.parseObject(messageJson);
            Long orderId = msg.getLong("orderId");
            String orderNo = msg.getString("orderNo");

            orderService.cancelOrder(orderId, orderNo);

            // 手动 ACK
            channel.basicAck(deliveryTag, false);
            log.info("【取消订单消息已确认】orderNo={}", orderNo);

        } catch (Exception e) {
            log.error("【取消订单失败】消息处理异常", e);
            try {
                // 重新入队，稍后重试
                channel.basicNack(deliveryTag, false, true);
            } catch (IOException ex) {
                log.error("消息 NACK 失败", ex);
            }
        }
    }
}
