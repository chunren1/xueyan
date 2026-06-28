package com.xueyan.common.result;

import lombok.Data;

/**
 * 统一返回体 —— 所有 Controller 返回此对象
 *
 * <pre>
 * 成功: Result.success(data)
 * 失败: Result.fail(ResultCode.xxx)
 *
 * JSON 格式:
 * {
 *   "code": 200,
 *   "message": "操作成功",
 *   "data": ...,
 *   "timestamp": 1234567890
 * }
 * </pre>
 *
 * @param <T> 数据类型
 * @author xueyan
 */
@Data
public class Result<T> {

    /** 状态码 */
    private int code;

    /** 提示信息 */
    private String message;

    /** 数据体 */
    private T data;

    /** 时间戳 */
    private long timestamp;

    private Result() {
        this.timestamp = System.currentTimeMillis();
    }

    // ==================== 静态工厂方法 ====================

    public static <T> Result<T> success() {
        return success(null);
    }

    public static <T> Result<T> success(T data) {
        Result<T> r = new Result<>();
        r.code = ResultCode.SUCCESS.getCode();
        r.message = ResultCode.SUCCESS.getMessage();
        r.data = data;
        return r;
    }

    public static <T> Result<T> success(String message, T data) {
        Result<T> r = new Result<>();
        r.code = ResultCode.SUCCESS.getCode();
        r.message = message;
        r.data = data;
        return r;
    }

    public static <T> Result<T> fail() {
        return fail(ResultCode.INTERNAL_ERROR);
    }

    public static <T> Result<T> fail(String message) {
        Result<T> r = new Result<>();
        r.code = ResultCode.INTERNAL_ERROR.getCode();
        r.message = message;
        return r;
    }

    public static <T> Result<T> fail(ResultCode resultCode) {
        Result<T> r = new Result<>();
        r.code = resultCode.getCode();
        r.message = resultCode.getMessage();
        return r;
    }

    public static <T> Result<T> fail(int code, String message) {
        Result<T> r = new Result<>();
        r.code = code;
        r.message = message;
        return r;
    }
}
