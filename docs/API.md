# API 接口文档

所有接口统一前缀：`http://localhost:8080/api`（通过 Gateway）
或直接访问各服务：`http://localhost:{端口}`

---

## 用户服务 (xueyan-user :8101)

### 用户注册

```http
POST /user/register
Content-Type: application/json

{
  "username": "test123",
  "password": "123456",
  "nickname": "测试用户"   // 选填
}
```

**成功响应**
```json
{
  "code": 200,
  "message": "注册成功",
  "data": {
    "id": 1,
    "username": "test123",
    "nickname": "测试用户",
    "email": null,
    "phone": null,
    "status": 1,
    "createdAt": "2026-06-29T00:39:22"
  }
}
```

### 用户登录

```http
POST /user/login
Content-Type: application/json

{
  "username": "test123",
  "password": "123456"
}
```

**成功响应**
```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "userId": 1,
    "username": "test123",
    "nickname": "测试用户"
  }
}
```

### 查询用户信息

```http
GET /user/{id}
```

---

## 课程服务 (xueyan-course :8102)

### 课程列表

```http
GET /course/list?category=后端开发&keyword=Spring&page=1&size=10
```

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| category | string | 否 | 分类筛选 |
| keyword | string | 否 | 关键词搜索 |
| page | int | 否 | 页码（默认 1） |
| size | int | 否 | 每页条数（默认 10） |

**成功响应**
```json
{
  "code": 200,
  "data": [
    {
      "id": 1,
      "name": "Spring Cloud Alibaba 微服务实战",
      "description": "从零到一搭建微服务架构...",
      "price": 199.00,
      "stock": 100,
      "category": "后端开发",
      "teacherName": "张老师",
      "createdAt": "2026-06-29T00:00:00"
    }
  ]
}
```

### 课程详情

```http
GET /course/{id}
```

### 扣减库存（内部 Feign 调用）

```http
PUT /course/{id}/stock/deduct?count=1
```

---

## 订单服务 (xueyan-order :8103)

### 创建订单（Seata 分布式事务）

```http
POST /order/create
Content-Type: application/json

{
  "userId": 1,
  "courseId": 1,
  "quantity": 1
}
```

**流程说明**：
1. Feign 查询课程信息
2. 写入订单（WAIT_PAY）
3. Feign 扣减课程库存
4. RabbitMQ 发送 30s 延迟取消消息
5. 以上操作由 Seata 全局事务协调

**成功响应**
```json
{
  "code": 200,
  "message": "下单成功，请尽快支付",
  "data": {
    "id": 1,
    "orderNo": "XO1719600000000abc12345",
    "userId": 1,
    "courseId": 1,
    "courseName": "Spring Cloud Alibaba 微服务实战",
    "amount": 199.00,
    "status": "WAIT_PAY",
    "createdAt": "2026-06-29T01:00:00"
  }
}
```

### 查询订单

```http
GET /order/{orderNo}
```

---

## 支付服务 (xueyan-payment :8104)

### 发起支付

```http
POST /payment/pay
Content-Type: application/json

{
  "orderNo": "XO1719600000000abc12345",
  "userId": 1,
  "amount": 199.00
}
```

**流程说明**（本地消息表模式）：
1. 创建支付记录（SUCCESS）
2. 写入本地消息表（PENDING）
3. 发送 RabbitMQ 消息通知订单服务
4. 更新本地消息表（SENT）
5. 如 MQ 发送失败，@Scheduled 定时重试

### 模拟回调

```http
POST /payment/callback/{orderNo}
```

---

## 网关路由

通过 Gateway :8080 访问时，路径前缀 `/api` 会被 StripPrefix=1 去掉：

```
浏览器 → /api/user/register
         ↓ Gateway
       /user/register → xueyan-user:8101
```

---

## 错误码

| code | 说明 |
|------|------|
| 200 | 成功 |
| 400 | 请求参数错误 |
| 401 | 未授权 |
| 500 | 服务器内部错误 |
| 1001 | 用户不存在 |
| 1002 | 密码错误 |
| 1003 | 用户已存在 |
| 2001 | 课程不存在 |
| 2002 | 课程库存不足 |
| 3001 | 订单不存在 |
| 3002 | 订单状态异常 |
| 4001 | 支付失败 |
