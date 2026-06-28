# xueyan-course — 课程服务

## 功能
- 课程列表查询（支持分页、分类筛选）
- 课程详情查询
- 课程库存管理（扣减 / 回滚）
- Redis 缓存课程热点数据

## 技术组件
- Nacos 注册 & 配置
- MyBatis-Plus + MySQL
- Redis 缓存
- OpenFeign 远程调用
- Knife4j 接口文档

## 端口
默认 `8102`，可通过 `COURSE_SERVER_PORT` 环境变量覆盖。
