package com.xueyan.payment.message;

import com.xueyan.payment.config.PaymentRabbitMQConfig;
import com.xueyan.payment.entity.LocalMessage;
import com.xueyan.payment.mapper.LocalMessageMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 本地消息表定时重发任务
 * <p>
 * 每 10 秒扫描一次待重发的消息，重新投递到 RabbitMQ。
 * 超过最大重试次数后标记为失败，需要人工介入。
 */
@Slf4j
@Component
@EnableScheduling
@RequiredArgsConstructor
public class LocalMessageHandler {

    private final LocalMessageMapper localMessageMapper;
    private final RabbitTemplate rabbitTemplate;

    /**
     * 定时扫描并重发本地消息
     */
    @Scheduled(fixedDelay = 10_000) // 每 10 秒执行一次
    public void retryFailedMessages() {
        List<LocalMessage> pendingMessages = localMessageMapper.findPendingMessages(LocalDateTime.now());

        if (pendingMessages.isEmpty()) return;

        log.debug("【本地消息重试】找到 {} 条待处理消息", pendingMessages.size());

        for (LocalMessage msg : pendingMessages) {
            try {
                // 重新发送 MQ
                rabbitTemplate.convertAndSend(
                        PaymentRabbitMQConfig.PAYMENT_EXCHANGE,
                        PaymentRabbitMQConfig.ROUTING_KEY_PAY_CALLBACK,
                        msg.getMessageBody()
                );

                // 更新状态为 SENT
                msg.setStatus("SENT");
                msg.setRetryCount(msg.getRetryCount() + 1);
                localMessageMapper.updateById(msg);

                log.info("【本地消息重发成功】msgId={}, orderNo={}, retryCount={}",
                        msg.getId(), msg.getOrderNo(), msg.getRetryCount());

            } catch (Exception e) {
                log.error("【本地消息重发失败】msgId={}, orderNo={}", msg.getId(), msg.getOrderNo());

                msg.setRetryCount(msg.getRetryCount() + 1);
                if (msg.getRetryCount() >= msg.getMaxRetry()) {
                    msg.setStatus("FAILED");
                    log.error("【本地消息已达最大重试次数】msgId={}, 需人工介入", msg.getId());
                } else {
                    msg.setNextRetryAt(LocalDateTime.now().plusSeconds(30));
                }
                localMessageMapper.updateById(msg);
            }
        }
    }
}
