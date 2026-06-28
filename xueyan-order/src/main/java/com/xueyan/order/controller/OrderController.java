package com.xueyan.order.controller;

import com.xueyan.common.result.Result;
import com.xueyan.order.dto.CreateOrderDTO;
import com.xueyan.order.dto.OrderVO;
import com.xueyan.order.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 订单服务 Controller
 */
@Tag(name = "订单服务", description = "下单、查询订单")
@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @Operation(summary = "创建订单（Seata 分布式事务 + 延迟取消）")
    @PostMapping("/create")
    public Result<OrderVO> createOrder(@Valid @RequestBody CreateOrderDTO dto) {
        OrderVO vo = orderService.createOrder(dto);
        return Result.success("下单成功，请尽快支付", vo);
    }

    @Operation(summary = "查询订单")
    @GetMapping("/{orderNo}")
    public Result<OrderVO> getOrder(@PathVariable String orderNo) {
        OrderVO vo = orderService.getOrderByNo(orderNo);
        return Result.success(vo);
    }

    @Operation(summary = "查询订单（供 Feign 内部调用）")
    @GetMapping("/feign/{orderNo}")
    public Result<OrderVO> getOrderForFeign(@PathVariable String orderNo) {
        OrderVO vo = orderService.getOrderByNo(orderNo);
        return Result.success(vo);
    }
}
