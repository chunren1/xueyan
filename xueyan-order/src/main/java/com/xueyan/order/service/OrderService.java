package com.xueyan.order.service;

import com.xueyan.order.dto.CreateOrderDTO;
import com.xueyan.order.dto.OrderVO;

/**
 * 订单服务接口
 */
public interface OrderService {

    /**
     * 创建订单（Seata 分布式事务）
     * <p>
     * 流程：查询课程 → 创建订单(WAIT_PAY) → 扣减库存(Feign) → 发送延迟消息
     */
    OrderVO createOrder(CreateOrderDTO dto);

    /**
     * 取消订单（由延迟队列触发）
     * <p>
     * 流程：校验状态 → 更新为 CANCELLED → 恢复库存(Feign)
     */
    void cancelOrder(Long orderId, String orderNo);

    /**
     * 支付成功回调
     * <p>
     * 流程：更新订单状态为 PAID
     */
    void handlePaymentCallback(String orderNo);

    /**
     * 查询订单详情
     */
    OrderVO getOrderByNo(String orderNo);
}
