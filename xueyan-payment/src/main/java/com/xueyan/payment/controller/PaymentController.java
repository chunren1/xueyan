package com.xueyan.payment.controller;

import com.xueyan.common.result.Result;
import com.xueyan.payment.dto.PayDTO;
import com.xueyan.payment.dto.PaymentVO;
import com.xueyan.payment.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 支付服务 Controller
 */
@Tag(name = "支付服务", description = "发起支付、模拟回调")
@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @Operation(summary = "发起支付（原型模拟支付成功）")
    @PostMapping("/pay")
    public Result<PaymentVO> pay(@Valid @RequestBody PayDTO dto) {
        PaymentVO vo = paymentService.pay(dto);
        return Result.success("支付成功（原型模拟）", vo);
    }

    @Operation(summary = "模拟第三方支付回调")
    @PostMapping("/callback/{orderNo}")
    public Result<PaymentVO> mockCallback(@PathVariable String orderNo) {
        PaymentVO vo = paymentService.mockCallback(orderNo);
        return Result.success("回调处理成功", vo);
    }
}
