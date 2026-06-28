# xueyan-user — 用户服务

## 功能
- 用户注册 / 登录
- 用户信息管理
- 通过 Feign 对外暴露用户查询接口

## 技术组件
- Nacos 注册 & 配置
- MyBatis-Plus + MySQL
- Redis 缓存
- OpenFeign 远程调用
- Knife4j 接口文档

## 端口
默认 `8101`，可通过 `USER_SERVER_PORT` 环境变量覆盖。
