package com.xueyan.payment.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

/**
 * 订单服务 Feign 客户端 — payment → order
 * <p>
 * 支付服务调用订单服务获取订单信息
 */
@FeignClient(name = "xueyan-order", url = "http://localhost:8103", path = "/order")
public interface OrderFeignClient {

    /**
     * 查询订单信息
     */
    @GetMapping("/feign/{orderNo}")
    Map<String, Object> getOrderByNo(@PathVariable("orderNo") String orderNo);
}
