# 数据库设计文档

## 库划分

每个微服务独立数据库，服务间禁止跨库操作（通过 Feign 调用）。

| 数据库 | 服务 | 说明 |
|--------|------|------|
| `xueyan_user` | xueyan-user | 用户注册/登录/信息 |
| `xueyan_course` | xueyan-course | 课程管理/库存 |
| `xueyan_order` | xueyan-order | 订单/本地消息 |
| `xueyan_payment` | xueyan-payment | 支付记录/本地消息 |

---

## 用户服务 (xueyan_user)

### user 表

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT PK | 用户 ID，自增 |
| username | VARCHAR(50) UNIQUE | 用户名 |
| password | VARCHAR(255) | BCrypt 加密密码 |
| nickname | VARCHAR(50) | 昵称 |
| email | VARCHAR(100) | 邮箱 |
| phone | VARCHAR(20) | 手机号 |
| status | TINYINT | 0=禁用, 1=正常 |
| created_at | DATETIME | 创建时间 |
| updated_at | DATETIME | 更新时间 |
| deleted | TINYINT | 逻辑删除 (MyBatis-Plus @TableLogic) |

### 索引

| 索引 | 字段 | 类型 |
|------|------|------|
| PRIMARY | id | 主键 |
| uk_username | username | 唯一索引 |

---

## 课程服务 (xueyan_course)

### course 表

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT PK | 课程 ID，自增 |
| name | VARCHAR(100) | 课程名称 |
| description | TEXT | 课程描述 |
| cover_url | VARCHAR(255) | 封面图 URL |
| price | DECIMAL(10,2) | 价格（元） |
| stock | INT | 剩余名额 |
| category | VARCHAR(50) | 分类（后端开发/前端开发/数据库） |
| teacher_name | VARCHAR(50) | 讲师姓名 |
| status | TINYINT | 0=下架, 1=上架 |
| created_at | DATETIME | 创建时间 |
| updated_at | DATETIME | 更新时间 |
| deleted | TINYINT | 逻辑删除 |

### 索引

| 索引 | 字段 | 类型 |
|------|------|------|
| PRIMARY | id | 主键 |
| idx_category | category | 普通索引 |
| idx_status | status | 普通索引 |

### 测试数据

| 课程 | 价格 | 名额 |
|------|------|------|
| Spring Cloud Alibaba 微服务实战 | ¥199 | 100 |
| Java 17 新特性详解 | ¥99 | 200 |
| Vue3 + TypeScript 前端进阶 | ¥149 | 150 |
| Redis 深度解析与实战 | ¥129 | 80 |
| MySQL 性能优化指南 | ¥89 | 120 |

### 库存扣减 SQL（行级锁）

```sql
UPDATE course SET stock = stock - ? WHERE id = ? AND stock >= ?
```

---

## 订单服务 (xueyan_order)

### order_info 表

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT PK | 订单 ID，自增 |
| order_no | VARCHAR(64) UNIQUE | 订单编号（XO + 时间戳 + UUID） |
| user_id | BIGINT | 用户 ID |
| course_id | BIGINT | 课程 ID |
| course_name | VARCHAR(100) | 课程名称（冗余，避免跨服务查） |
| amount | DECIMAL(10,2) | 实付金额 |
| status | VARCHAR(20) | WAIT_PAY / PAID / COMPLETED / CANCELLED |
| created_at | DATETIME | 创建时间 |
| updated_at | DATETIME | 更新时间 |
| deleted | TINYINT | 逻辑删除 |

### local_message 表

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT PK | 消息 ID |
| order_no | VARCHAR(64) | 关联订单号 |
| message_type | VARCHAR(50) | PAY_CALLBACK / ORDER_CANCEL |
| message_body | TEXT | JSON 消息体 |
| status | VARCHAR(20) | PENDING → SENT → CONFIRMED / FAILED |
| retry_count | INT | 已重试次数 |
| max_retry | INT | 最大重试次数（默认 5） |
| next_retry_at | DATETIME | 下次重试时间 |
| created_at | DATETIME | 创建时间 |
| updated_at | DATETIME | 更新时间 |

### 索引

| 索引 | 字段 | 类型 |
|------|------|------|
| PRIMARY | id | 主键 |
| uk_order_no | order_no | 唯一索引 |
| idx_user_id | user_id | 普通索引 |
| idx_status | status | 普通索引 |
| idx_status_retry | (status, next_retry_at) | 联合索引 |

### 状态流转

```
WAIT_PAY ──支付──▶ PAID ──完成──▶ COMPLETED
    │
    └──超时──▶ CANCELLED ──恢复库存
```

---

## 支付服务 (xueyan_payment)

### payment_record 表

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT PK | 支付记录 ID |
| payment_no | VARCHAR(64) UNIQUE | 支付流水号（PAY + 时间戳 + UUID） |
| order_no | VARCHAR(64) | 订单编号 |
| user_id | BIGINT | 用户 ID |
| amount | DECIMAL(10,2) | 支付金额 |
| payment_method | VARCHAR(20) | WECHAT / ALIPAY（默认 WECHAT） |
| status | VARCHAR(20) | PENDING / SUCCESS / FAILED |
| callback_at | DATETIME | 回调时间 |
| created_at | DATETIME | 创建时间 |
| updated_at | DATETIME | 更新时间 |

### local_message 表

与订单服务同结构，独立维护，通过 `message_type = PAY_CALLBACK` 区分。

### 索引

| 索引 | 字段 | 类型 |
|------|------|------|
| PRIMARY | id | 主键 |
| uk_payment_no | payment_no | 唯一索引 |
| idx_order_no | order_no | 普通索引 |

---

## Seata undo_log 表

每个库都包含，结构相同：

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT PK | 自增 |
| branch_id | BIGINT | 分支事务 ID |
| xid | VARCHAR(128) | 全局事务 ID |
| rollback_info | LONGBLOB | 回滚信息 |
| log_status | INT | 日志状态 |
| log_created | DATETIME | 创建时间 |
| log_modified | DATETIME | 修改时间 |

UNIQUE KEY: `(xid, branch_id)`

---

## ER 关系图

```
xueyan_user            xueyan_course
┌──────────┐          ┌──────────────┐
│  user    │          │   course     │
│──────────│          │──────────────│
│ id       │──┐       │ id           │──┐
│ username │  │       │ name         │  │
│ password │  │       │ price        │  │
│ nickname │  │       │ stock        │  │
│ status   │  │       │ category     │  │
└──────────┘  │       │ teacher_name │  │
              │       └──────────────┘  │
              │                         │
     ┌────────▼─────────────────────────▼──┐
     │          xueyan_order               │
     │  ┌──────────────┐ ┌──────────────┐ │
     │  │  order_info  │ │local_message │ │
     │  │──────────────│ │──────────────│ │
     │  │ id           │ │ id           │ │
     │  │ order_no     │ │ order_no ────┼─┤
     │  │ user_id ─────┤ │ message_body │ │
     │  │ course_id ───┤ │ status       │ │
     │  │ amount       │ │ retry_count  │ │
     │  │ status       │ └──────────────┘ │
     │  └──────────────┘                  │
     └───────────────────┬────────────────┘
                         │
     ┌───────────────────▼──────────────────┐
     │         xueyan_payment               │
     │  ┌──────────────┐ ┌──────────────┐  │
     │  │payment_record│ │local_message │  │
     │  │──────────────│ │──────────────│  │
     │  │ id           │ │ id           │  │
     │  │ payment_no   │ │ order_no     │  │
     │  │ order_no ────┼─┤ message_body │  │
     │  │ amount       │ │ status       │  │
     │  │ status       │ │ retry_count  │  │
     │  └──────────────┘ └──────────────┘  │
     └─────────────────────────────────────┘
```

> **设计原则**: 服务间不共享数据库，通过 order_no 关联订单与支付。course_name 冗余在订单表避免跨服务查询。local_message 保障消息最终一致性。
