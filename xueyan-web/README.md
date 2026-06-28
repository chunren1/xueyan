# xueyan-web — 学研在线前端

基于 **Vue 3 + TypeScript + Vite** 的在线教育平台前端。

## 技术栈

| 分类 | 技术 | 说明 |
|------|------|------|
| 框架 | Vue 3.4 | Composition API + `<script setup>` |
| 语言 | TypeScript 5.4 | 完整类型定义 |
| 构建 | Vite 5.3 | 极速 HMR |
| UI 库 | Element Plus 2.7 | 企业级组件库 |
| 状态管理 | Pinia 2.1 | Vue 官方推荐 |
| 路由 | Vue Router 4.3 | 懒加载 + 导航守卫 |
| HTTP | Axios 1.7 | 拦截器 + 统一错误处理 |
| CSS | UnoCSS 0.61 | 原子化 CSS |

## 快速启动

```bash
cd xueyan-web
npm install
npm run dev
# → http://localhost:5173
```

## 项目结构

```
xueyan-web/
├── index.html                    # 入口 HTML
├── vite.config.ts                # Vite 配置（代理 /api → :8080）
├── tsconfig.json                 # TypeScript 配置
├── uno.config.ts                 # UnoCSS 配置
├── package.json
│
├── public/
│   └── vite.svg                  # 网站图标
│
├── .env.development              # 开发环境变量
│   └── VITE_API_BASE=http://localhost:8080/api
│
├── .env.production               # 生产环境变量
│   └── VITE_API_BASE=/api
│
└── src/
    ├── main.ts                   # 应用入口（注册 Pinia/Router/ElementPlus）
    ├── App.vue                   # 根组件（NavBar + RouterView）
    │
    ├── api/                      # API 接口层
    │   ├── request.ts            #   Axios 实例 + 响应拦截
    │   ├── user.ts               #   用户 API（注册/登录/查询）
    │   ├── course.ts             #   课程 API（列表/详情）
    │   └── order.ts              #   订单 API（创建/查询）
    │
    ├── router/
    │   └── index.ts              # 路由配置 + 登录守卫
    │
    ├── stores/
    │   └── user.ts               # 用户状态（登录/登出/持久化）
    │
    ├── components/
    │   └── NavBar.vue            # 导航栏（登录状态自适应）
    │
    └── views/                    # 页面视图
        ├── Home.vue              # 首页（课程搜索+分类+卡片列表）
        ├── Login.vue             # 登录页
        ├── Register.vue          # 注册页
        ├── CourseDetail.vue      # 课程详情页
        ├── Order.vue             # 下单/订单详情页
        └── UserCenter.vue        # 个人中心
```

## 页面路由

| 路径 | 组件 | 权限 | 说明 |
|------|------|------|------|
| `/` | Home.vue | 公开 | 课程首页，支持搜索和分类筛选 |
| `/login` | Login.vue | 公开 | 登录页 |
| `/register` | Register.vue | 公开 | 注册页 |
| `/course/:id` | CourseDetail.vue | 公开 | 课程详情 + 购买入口 |
| `/order` | Order.vue | 需登录 | 下单（带 query 参数）/ 查看订单 |
| `/user` | UserCenter.vue | 需登录 | 个人中心 + 退出登录 |

## 前端调用流程

```
Home 页面                     Login 页面
  │                              │
  ├─ GET /course/list ───────────┤
  │  (课程卡片列表)               │
  │                              │
  ├─ 点击「立即购买」             │
  │  → 未登录 → 跳转 /login      ├─ POST /user/login
  │                              │  → Pinia 存储 user
  │  → 已登录 → 跳转 /order      │  → localStorage 持久化
  │     ?courseId=1&courseName=  │
  │                              │
Order 页面                       │
  ├─ 用户确认订单                 │
  ├─ POST /order/create ─────────┤
  │  (Seata 全局事务)            │
  │                              │
  ├─ 显示订单详情                 │
  │  (orderNo + 状态)            │
```

## 与后端对接

| 前端 API 方法 | 请求 | 后端接口 |
|---------------|------|----------|
| `userApi.register()` | POST | `/user/register` |
| `userApi.login()` | POST | `/user/login` |
| `courseApi.list({category,keyword})` | GET | `/course/list?category=&keyword=` |
| `courseApi.detail(id)` | GET | `/course/{id}` |
| `orderApi.create({userId,...})` | POST | `/order/create` |
| `orderApi.getByNo(orderNo)` | GET | `/order/{orderNo}` |

## 构建部署

```bash
# 开发模式
npm run dev

# 生产构建
npm run build
# 输出到 dist/ 目录

# 预览生产构建
npm run preview
```

## 页面截图（设计稿）

```
┌─────────────────────────────────────────────────┐
│  🏫 学研在线             登录  |  注册            │  ← NavBar
├─────────────────────────────────────────────────┤
│  🔍 [  搜索课程...          ]  [搜索]            │
│  [全部] [后端开发] [前端开发] [数据库]            │
├────────────┬────────────┬────────────┤
│  课程卡片   │  课程卡片   │  课程卡片   │
│  名称 ¥价   │  名称 ¥价   │  名称 ¥价   │
│  描述...    │  描述...    │  描述...    │
│  [立即购买] │  [立即购买] │  [立即购买] │
└────────────┴────────────┴────────────┘
```
