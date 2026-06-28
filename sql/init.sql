-- ========================================================
-- 学研在线 — 数据库初始化脚本
-- 每个微服务独立数据库（服务之间通过 Feign 通信，不跨库操作）
-- ========================================================

-- ==================== 用户服务数据库 ====================
CREATE DATABASE IF NOT EXISTS `xueyan_user`
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_general_ci;

USE `xueyan_user`;

DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `username`    VARCHAR(50)  NOT NULL                COMMENT '用户名',
    `password`    VARCHAR(255) NOT NULL                COMMENT '密码（BCrypt 加密）',
    `nickname`    VARCHAR(50)  DEFAULT NULL            COMMENT '昵称',
    `email`       VARCHAR(100) DEFAULT NULL            COMMENT '邮箱',
    `phone`       VARCHAR(20)  DEFAULT NULL            COMMENT '手机号',
    `status`      TINYINT      NOT NULL DEFAULT 1      COMMENT '状态：0=禁用, 1=正常',
    `created_at`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`     TINYINT      NOT NULL DEFAULT 0      COMMENT '逻辑删除：0=正常, 1=已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 插入一条测试用户（密码 123456 的 BCrypt 哈希，实际运行时会动态生成）
-- INSERT INTO `user` (username, password, nickname) VALUES ('test', '$2a$10$...', '测试用户');


-- ==================== 课程服务数据库 ====================
CREATE DATABASE IF NOT EXISTS `xueyan_course`
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_general_ci;

USE `xueyan_course`;

DROP TABLE IF EXISTS `course`;
CREATE TABLE `course` (
    `id`           BIGINT        NOT NULL AUTO_INCREMENT COMMENT '课程ID',
    `name`         VARCHAR(100)  NOT NULL               COMMENT '课程名称',
    `description`  TEXT          DEFAULT NULL           COMMENT '课程描述',
    `cover_url`    VARCHAR(255)  DEFAULT NULL           COMMENT '封面图 URL',
    `price`        DECIMAL(10,2) NOT NULL DEFAULT 0.00  COMMENT '价格（元）',
    `stock`        INT           NOT NULL DEFAULT 0     COMMENT '剩余库存（名额）',
    `category`     VARCHAR(50)   DEFAULT NULL           COMMENT '课程分类',
    `teacher_name` VARCHAR(50)   DEFAULT NULL           COMMENT '讲师姓名',
    `status`       TINYINT       NOT NULL DEFAULT 1     COMMENT '状态：0=下架, 1=上架',
    `created_at`   DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`   DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`      TINYINT       NOT NULL DEFAULT 0     COMMENT '逻辑删除：0=正常, 1=已删除',
    PRIMARY KEY (`id`),
    KEY `idx_category` (`category`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课程表';

-- 插入测试课程数据
INSERT INTO `course` (name, description, cover_url, price, stock, category, teacher_name, status) VALUES
('Spring Cloud Alibaba 微服务实战',  '从零到一搭建微服务架构，涵盖 Nacos/Sentinel/Seata/Gateway 等核心组件',  NULL, 199.00, 100, '后端开发', '张老师', 1),
('Java 17 新特性详解',             '全面解析 Java 17 LTS 版本的新增特性与最佳实践',                   NULL, 99.00,  200, '后端开发', '李老师', 1),
('Vue3 + TypeScript 前端进阶',     'Vue3 Composition API + TypeScript 实战项目驱动教学',            NULL, 149.00, 150, '前端开发', '王老师', 1),
('Redis 深度解析与实战',           'Redis 数据结构、持久化、集群、缓存穿透/击穿/雪崩解决方案',      NULL, 129.00, 80,  '后端开发', '赵老师', 1),
('MySQL 性能优化指南',             '索引优化、慢查询分析、分库分表实战',                             NULL, 89.00,  120, '数据库',   '刘老师', 1);


-- ==================== 订单服务数据库 ====================
CREATE DATABASE IF NOT EXISTS `xueyan_order`
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_general_ci;

USE `xueyan_order`;

DROP TABLE IF EXISTS `order_info`;
CREATE TABLE `order_info` (
    `id`          BIGINT        NOT NULL AUTO_INCREMENT COMMENT '订单ID',
    `order_no`    VARCHAR(64)   NOT NULL               COMMENT '订单编号（业务唯一）',
    `user_id`     BIGINT        NOT NULL               COMMENT '用户ID',
    `course_id`   BIGINT        NOT NULL               COMMENT '课程ID',
    `course_name` VARCHAR(100)  NOT NULL               COMMENT '课程名称（冗余）',
    `amount`      DECIMAL(10,2) NOT NULL               COMMENT '实付金额',
    `status`      VARCHAR(20)   NOT NULL DEFAULT 'WAIT_PAY' COMMENT '订单状态：WAIT_PAY/PAID/COMPLETED/CANCELLED',
    `created_at`  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`     TINYINT       NOT NULL DEFAULT 0     COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_order_no` (`order_no`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

-- 本地消息表（保障最终一致性）
DROP TABLE IF EXISTS `local_message`;
CREATE TABLE `local_message` (
    `id`            BIGINT       NOT NULL AUTO_INCREMENT COMMENT '消息ID',
    `order_no`      VARCHAR(64)  NOT NULL               COMMENT '关联订单号',
    `message_type`  VARCHAR(50)  NOT NULL               COMMENT '消息类型：PAY_CALLBACK/ORDER_CANCEL',
    `message_body`  TEXT         NOT NULL               COMMENT '消息体 JSON',
    `status`        VARCHAR(20)  NOT NULL DEFAULT 'PENDING' COMMENT '状态：PENDING/SENT/CONFIRMED',
    `retry_count`   INT          NOT NULL DEFAULT 0     COMMENT '已重试次数',
    `max_retry`     INT          NOT NULL DEFAULT 5     COMMENT '最大重试次数',
    `next_retry_at` DATETIME     DEFAULT NULL           COMMENT '下次重试时间',
    `created_at`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_order_no` (`order_no`),
    KEY `idx_status_retry` (`status`, `next_retry_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='本地消息表';


-- ==================== 支付服务数据库 ====================
CREATE DATABASE IF NOT EXISTS `xueyan_payment`
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_general_ci;

USE `xueyan_payment`;

DROP TABLE IF EXISTS `payment_record`;
CREATE TABLE `payment_record` (
    `id`             BIGINT        NOT NULL AUTO_INCREMENT COMMENT '支付记录ID',
    `payment_no`     VARCHAR(64)   NOT NULL               COMMENT '支付流水号',
    `order_no`       VARCHAR(64)   NOT NULL               COMMENT '订单编号',
    `user_id`        BIGINT        NOT NULL               COMMENT '用户ID',
    `amount`         DECIMAL(10,2) NOT NULL               COMMENT '支付金额',
    `payment_method` VARCHAR(20)   DEFAULT 'WECHAT'       COMMENT '支付方式',
    `status`         VARCHAR(20)   NOT NULL DEFAULT 'PENDING' COMMENT '状态：PENDING/SUCCESS/FAILED',
    `callback_at`    DATETIME      DEFAULT NULL           COMMENT '回调时间',
    `created_at`     DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`     DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_payment_no` (`payment_no`),
    KEY `idx_order_no` (`order_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='支付记录表';

-- 支付服务本地消息表（保障最终一致性）
DROP TABLE IF EXISTS `local_message`;
CREATE TABLE `local_message` (
    `id`            BIGINT       NOT NULL AUTO_INCREMENT COMMENT '消息ID',
    `order_no`      VARCHAR(64)  NOT NULL               COMMENT '关联订单号',
    `message_type`  VARCHAR(50)  NOT NULL               COMMENT '消息类型',
    `message_body`  TEXT         NOT NULL               COMMENT '消息体 JSON',
    `status`        VARCHAR(20)  NOT NULL DEFAULT 'PENDING' COMMENT '状态',
    `retry_count`   INT          NOT NULL DEFAULT 0     COMMENT '已重试次数',
    `max_retry`     INT          NOT NULL DEFAULT 5     COMMENT '最大重试次数',
    `next_retry_at` DATETIME     DEFAULT NULL           COMMENT '下次重试时间',
    `created_at`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_order_no` (`order_no`),
    KEY `idx_status_retry` (`status`, `next_retry_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='本地消息表';
