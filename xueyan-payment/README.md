# xueyan-payment — 支付服务

## 功能
- 模拟支付接口（原型阶段）
- 支付回调处理
- 支付状态同步到订单服务（本地消息表 + RabbitMQ）
- 支付流水记录

## 技术组件
- Nacos 注册 & 配置
- MyBatis-Plus + MySQL
- OpenFeign 远程调用
- RabbitMQ
- Seata 分布式事务
- Knife4j 接口文档

## 端口
默认 `8104`，可通过 `PAYMENT_SERVER_PORT` 环境变量覆盖。
