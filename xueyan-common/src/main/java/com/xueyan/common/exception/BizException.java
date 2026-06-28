package com.xueyan.common.exception;

import com.xueyan.common.result.ResultCode;
import lombok.Getter;

/**
 * 业务异常 —— Service 层抛出，由 GlobalExceptionHandler 统一拦截
 */
@Getter
public class BizException extends RuntimeException {

    /** 异常码 */
    private final int code;

    public BizException(ResultCode resultCode) {
        super(resultCode.getMessage());
        this.code = resultCode.getCode();
    }

    public BizException(int code, String message) {
        super(message);
        this.code = code;
    }

    public BizException(ResultCode resultCode, String detail) {
        super(resultCode.getMessage() + "：" + detail);
        this.code = resultCode.getCode();
    }
}
