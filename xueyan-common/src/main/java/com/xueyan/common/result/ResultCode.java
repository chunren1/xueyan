package com.xueyan.common.result;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 统一返回状态码枚举
 */
@Getter
@AllArgsConstructor
public enum ResultCode {

    // ========== 成功 ==========
    SUCCESS(200, "操作成功"),

    // ========== 客户端错误 4xx ==========
    BAD_REQUEST(400, "请求参数错误"),
    UNAUTHORIZED(401, "未授权，请先登录"),
    FORBIDDEN(403, "无权限访问"),
    NOT_FOUND(404, "资源不存在"),
    METHOD_NOT_ALLOWED(405, "请求方法不允许"),
    CONFLICT(409, "资源冲突"),

    // ========== 服务端错误 5xx ==========
    INTERNAL_ERROR(500, "服务器内部错误"),
    SERVICE_UNAVAILABLE(503, "服务暂不可用"),

    // ========== 业务错误 1xxx ==========
    USER_NOT_FOUND(1001, "用户不存在"),
    PASSWORD_ERROR(1002, "密码错误"),
    USER_ALREADY_EXISTS(1003, "用户已存在"),
    COURSE_NOT_FOUND(2001, "课程不存在"),
    COURSE_STOCK_INSUFFICIENT(2002, "课程库存不足"),
    ORDER_NOT_FOUND(3001, "订单不存在"),
    ORDER_STATUS_ERROR(3002, "订单状态异常"),
    PAYMENT_FAILED(4001, "支付失败"),
    PAYMENT_CALLBACK_ERROR(4002, "支付回调异常");

    /** 状态码 */
    private final int code;

    /** 提示信息 */
    private final String message;
}
