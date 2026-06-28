<p align="center">
  <h1 align="center">🏫 学研在线 — 在线教育平台</h1>
  <p align="center">
    基于 Spring Cloud Alibaba 的微服务架构全栈项目
  </p>
</p>

---

## 📖 项目简介

「学研在线」是一个在线教育平台，采用**微服务架构**实现服务拆分、远程调用、分布式事务与异步消息等高并发场景下的核心技术能力。

### 核心特性
- 🏗 **4 个核心微服务** — 用户、课程、订单、支付独立部署
- 📨 **异步消息** — RabbitMQ 延迟队列 + 死信交换机处理订单超时取消
- 🔒 **分布式事务** — Seata 协调跨服务下单与库存扣减
- 🚦 **服务治理** — Nacos 注册/配置中心 + Sentinel 限流熔断 + Gateway 统一路由
- 🎨 **前后端分离** — Vue 3 + TypeScript 现代化前端

---

## 🛠 技术栈

### 后端
| 分类 | 技术 | 版本 |
|------|------|------|
| 语言 | Java | 17 |
| 框架 | Spring Boot | 3.2.0 |
| 微服务 | Spring Cloud | 2023.0.0 |
| 微服务 | Spring Cloud Alibaba | 2023.0.1.0 |
| ORM | MyBatis-Plus | 3.5.5 |
| 数据库 | MySQL | 8.0 |
| 缓存 | Redis | 7 |
| 消息队列 | RabbitMQ | 3.12 |
| 注册/配置中心 | Nacos | 2.3 |
| 分布式事务 | Seata | 1.8 |
| 网关 | Spring Cloud Gateway | — |
| 远程调用 | OpenFeign + LoadBalancer | — |
| API 文档 | Knife4j | 4.4 |
| 连接池 | Druid | 1.2 |

### 前端
| 分类 | 技术 | 版本 |
|------|------|------|
| 框架 | Vue 3 + Composition API | 3.4 |
| 语言 | TypeScript | 5.4 |
| 构建 | Vite | 5.3 |
| UI 库 | Element Plus | 2.7 |
| 状态管理 | Pinia | 2.1 |
| 路由 | Vue Router | 4.3 |
| HTTP | Axios | 1.7 |
| CSS | UnoCSS | 0.61 |

### DevOps
| 分类 | 技术 |
|------|------|
| 容器化 | Docker + Docker Compose |
| 构建 | Maven 3.8+ |
| 版本管理 | Git + GitHub |

---

## 📂 项目结构

```
xueyan-parent                     # 父模块（统一依赖版本管理）
│
├── sql/                          # 数据库脚本
│   ├── init.sql                  #   建库 + 建表 + 测试数据
│   └── seata.sql                 #   Seata undo_log 表
│
├── docker/                       # Docker 配置
│   └── seata/registry.conf       #   Seata 注册中心配置
│
├── docker-compose.yml            # 中间件编排（MySQL/Redis/RabbitMQ/Nacos/Seata）
├── package.json                  # npm 管理脚本
├── pom.xml                       # Maven 父 POM
│
├── xueyan-common                 # 公共模块（JAR 库）
│   ├── result/                   #   Result<T> 统一返回体
│   ├── exception/                #   BizException + GlobalExceptionHandler
│   └── config/                   #   AutoFillMetaObjectHandler
│
├── xueyan-gateway     :8080      # 网关服务
│   └── Spring Cloud Gateway + Sentinel 限流
│
├── xueyan-user        :8101      # 用户服务
│   ├── POST /user/register      注册（BCrypt 加密）
│   ├── POST /user/login         登录（密码校验）
│   └── GET  /user/{id}          查询用户信息
│
├── xueyan-course      :8102      # 课程服务
│   ├── GET  /course/list        课程列表（分页 + Redis 缓存）
│   ├── GET  /course/{id}        课程详情
│   └── PUT  /course/{id}/stock/deduct   扣减库存（行级锁）
│
├── xueyan-order       :8103      # 订单服务
│   ├── POST /order/create       下单（Seata 全局事务）
│   └── GET  /order/{orderNo}    查询订单
│   └── [内部] RabbitMQ 延迟取消 + 死信队列
│
├── xueyan-payment     :8104      # 支付服务
│   ├── POST /payment/pay        发起支付（本地消息表）
│   └── POST /payment/callback/{orderNo}  模拟回调
│   └── [内部] @Scheduled 本地消息重试
│
└── xueyan-web         :5173      # Vue 3 前端
    ├── src/api/                 接口封装层
    ├── src/views/               6 个业务页面
    ├── src/router/              路由 + 守卫
    └── src/stores/              Pinia 状态管理
```

---

## 🏗 架构图

```
                         ┌──────────────────────┐
                         │   Vue 3 前端 :5173    │
                         │   Element Plus +      │
                         │   Pinia + Axios       │
                         └──────────┬───────────┘
                                    │ /api/*
                         ┌──────────▼───────────┐
                         │   Gateway :8080       │
                         │   路由 + Sentinel     │
                         └──────┬───────┬───────┘
                    ┌───────────┘       └───────────┐
           ┌────────▼────────┐            ┌─────────▼────────┐
           │   User  :8101   │◄──Feign──►│  Course  :8102    │
           │   注册/登录      │            │  列表/详情/库存    │
           └─────────────────┘            └──────────────────┘
                                                    │
                                           ┌────────▼────────┐
                                           │  Order  :8103    │
                                           │  下单/取消        │
                                           │  RabbitMQ 延迟    │
                                           └────────┬────────┘
                                                    │
                                           ┌────────▼────────┐
                                           │ Payment :8104    │
                                           │  支付/回调        │
                                           │  本地消息表重试   │
                                           └─────────────────┘

                    ┌────────────────────────────────┐
                    │   Infrastructure (Docker)      │
                    │  Nacos :8848  注册/配置中心     │
                    │  MySQL :3306  4 库独立         │
                    │  Redis :6379  缓存             │
                    │  RabbitMQ :5672  延迟队列+DLX  │
                    │  Seata :8091  分布式事务       │
                    └────────────────────────────────┘
```

### 核心业务流程

```
用户注册 → 登录 → 浏览课程 → 下单 → 支付 → 完成
                        │               │
                   Seata 全局事务    本地消息表
                   订单写入 + 扣库存    → MQ → 订单→PAID
                        │               │
                  发送延迟取消消息    定时重试保障投递
                        │
                  30s 超时未付
                  → DLX → 取消订单 + 恢复库存
```

---

## 🚀 快速启动

### 前置条件

- JDK 17+
- Maven 3.8+
- Docker Desktop
- Node.js 18+（仅前端需要）

### 1. 启动中间件（Docker）

```bash
npm run infra:up
```

自动启动 MySQL、Redis、RabbitMQ、Nacos、Seata，首次运行自动建库建表。

### 2. 编译后端

```bash
npm run mvn:install
```

### 3. 启动后端微服务（5 个独立窗口）

```bash
npm run dev:all
```

### 4. 启动前端（可选）

```bash
cd xueyan-web
npm install
npm run dev
# → http://localhost:5173
```

或一键全栈启动：

```bash
npm run start:full
```

### 5. 验证

```bash
# 注册用户
curl -X POST http://localhost:8101/user/register \
  -H "Content-Type: application/json" \
  -d '{"username":"test","password":"123456"}'

# 登录
curl -X POST http://localhost:8101/user/login \
  -H "Content-Type: application/json" \
  -d '{"username":"test","password":"123456"}'

# 查询课程
curl http://localhost:8102/course/list

# 下单（Seata 分布式事务 + 30s 超时取消）
curl -X POST http://localhost:8103/order/create \
  -H "Content-Type: application/json" \
  -d '{"userId":1,"courseId":1,"quantity":1}'

# 支付
curl -X POST http://localhost:8104/payment/pay \
  -H "Content-Type: application/json" \
  -d '{"orderNo":"XO...","userId":1,"amount":199}'
```

---

## 🎛 管理面板

| 面板 | 地址 | 账号 |
|------|------|------|
| 🖥 前端页面 | http://localhost:5173 | — |
| 📖 Knife4j 文档 | http://localhost:8101/doc.html | — |
| ⚙️ Nacos 控制台 | http://localhost:8848/nacos | nacos/nacos |
| 🐰 RabbitMQ 管理 | http://localhost:15672 | guest/guest |

---

## 📦 npm 脚本

```bash
# --- Docker 中间件 ---
npm run infra:up         # 启动全部中间件
npm run infra:down       # 停止
npm run infra:status     # 状态查询
npm run infra:clean      # 清除数据卷
npm run mysql:cli        # MySQL 命令行

# --- 后端 ---
npm run mvn:install      # 编译安装
npm run dev:all          # 启动全部 5 个微服务
npm run dev:gateway      # 单独启动网关
npm run dev:user         # 单独启动用户服务
npm run dev:course       # 单独启动课程服务
npm run dev:order        # 单独启动订单服务
npm run dev:payment      # 单独启动支付服务
npm run dev:stop         # 停止所有微服务

# --- 前端 ---
npm run web:install      # 安装前端依赖
npm run web:dev          # 启动前端开发服务器
npm run web:build        # 构建前端

# --- 全栈 ---
npm run start:full       # 中间件 + 后端 + 前端
```

---

## 🌐 环境变量

| 变量 | 默认值 | 说明 |
|------|--------|------|
| `NACOS_HOST` | 127.0.0.1 | Nacos 地址 |
| `NACOS_PORT` | 8848 | Nacos HTTP 端口 |
| `DB_HOST` | 127.0.0.1 | MySQL 地址 |
| `DB_PORT` | 3306 | MySQL 端口 |
| `DB_USER` | root | 数据库用户名 |
| `DB_PASS` | 123456 | 数据库密码 |
| `REDIS_HOST` | 127.0.0.1 | Redis 地址 |
| `RABBITMQ_HOST` | 127.0.0.1 | RabbitMQ 地址 |
| `VITE_API_BASE` | http://localhost:8080/api | 前端 API 地址 |

---

## 📋 项目规范

### 编码规范
- 所有服务统一包名：`com.xueyan.{服务名}`
- 禁止跨服务直接操作数据库（必须通过 Feign 调用）
- 禁止在 Controller 中写业务逻辑
- 禁止硬编码 IP / 端口 / 密码（统一使用环境变量）
- 统一使用 MyBatis-Plus，禁止 JPA

### Git 规范
- 提交信息格式：`feat:` / `fix:` / `refactor:` / `docs:` / `chore:`
- 禁止提交 `target/`、`node_modules/`、`*.log`

### 数据库规范
- 每个微服务独立数据库（4 个库）
- 表名使用下划线命名（`order_info`）
- 统一使用逻辑删除（`deleted` 字段）
- 使用 `undo_log` 表支持 Seata AT 模式

---

## 📡 API 接口

> 详细文档: [docs/API.md](docs/API.md) ｜ 数据库设计: [docs/DATABASE.md](docs/DATABASE.md)

| 方法 | 路径 | 服务 | 说明 |
|------|------|------|------|
| POST | `/user/register` | user | 用户注册 |
| POST | `/user/login` | user | 用户登录 |
| GET | `/user/{id}` | user | 查询用户 |
| GET | `/course/list` | course | 课程列表（分页+筛选） |
| GET | `/course/{id}` | course | 课程详情 |
| PUT | `/course/{id}/stock/deduct` | course | 扣减库存 |
| POST | `/order/create` | order | 创建订单 |
| GET | `/order/{orderNo}` | order | 查询订单 |
| POST | `/payment/pay` | payment | 发起支付 |
| POST | `/payment/callback/{orderNo}` | payment | 模拟回调 |

---

## 📄 License

MIT
