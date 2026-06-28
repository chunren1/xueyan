# xueyan-common — 公共模块

## 功能
- **统一返回体** `Result<T>`：所有服务 Controller 的标准返回格式
- **状态码枚举** `ResultCode`：统一错误码定义（200/4xx/5xx/1xxx 业务码）
- **业务异常** `BizException`：Service 层抛出，由全局拦截器统一处理
- **全局异常拦截** `GlobalExceptionHandler`：`@RestControllerAdvice` 拦截所有异常

## 被依赖关系
所有业务子模块（user / course / order / payment）均依赖此模块。
