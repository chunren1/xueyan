# xueyan-order — 订单服务

## 功能
- 创建订单（分布式事务：Seata 协调扣库存）
- 订单状态流转（待支付 → 已支付 → 已完成 / 已取消）
- 超时未支付自动取消（RabbitMQ 延迟队列 + 死信交换机）
- 本地消息表保障最终一致性

## 技术组件
- Nacos 注册 & 配置
- MyBatis-Plus + MySQL
- OpenFeign 远程调用
- RabbitMQ 延迟队列 + 死信交换机
- Seata 分布式事务
- Knife4j 接口文档

## 端口
默认 `8103`，可通过 `ORDER_SERVER_PORT` 环境变量覆盖。
