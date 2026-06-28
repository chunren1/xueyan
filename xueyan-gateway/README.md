# xueyan-gateway — 网关服务

## 功能
- 统一入口路由（基于 Nacos 服务发现自动路由）
- 统一鉴权（后续阶段实现 JWT 校验）
- Sentinel 网关层限流

## 路由规则
| 前缀 | 目标服务 |
|------|---------|
| `/api/user/**` | xueyan-user (8101) |
| `/api/course/**` | xueyan-course (8102) |
| `/api/order/**` | xueyan-order (8103) |
| `/api/payment/**` | xueyan-payment (8104) |

## 端口
默认 `8080`，可通过 `GATEWAY_SERVER_PORT` 环境变量覆盖。
