-- 删除已存在的表
DROP TABLE IF EXISTS `dialogue`;
DROP TABLE IF EXISTS `ai_assistant`;
DROP TABLE IF EXISTS `user`;

-- 用户表
CREATE TABLE `user` (
                        `id` VARCHAR(36) NOT NULL COMMENT '主键，UUID',
                        `name` VARCHAR(100) NOT NULL COMMENT '用户名称',
                        `email` VARCHAR(255) NOT NULL COMMENT '邮箱',
                        `password` VARCHAR(255) NOT NULL COMMENT '密码（加密存储）',
                        `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                        `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                        PRIMARY KEY (`id`),
                        UNIQUE KEY `uk_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- AI 助手表（无外键约束）
CREATE TABLE `ai_assistant` (
                                `id` VARCHAR(36) NOT NULL COMMENT '主键，UUID',
                                `user_id` VARCHAR(36) NOT NULL COMMENT '所属用户ID',
                                `name` VARCHAR(100) NOT NULL COMMENT '助手名称',
                                `description` TEXT COMMENT '助手描述',
                                `assistant_character` TEXT COMMENT '助手角色设定/性格描述',
                                `knowledge_base_id` VARCHAR(36) COMMENT '知识库ID',
                                `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                PRIMARY KEY (`id`),
                                KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI助手表';

-- 对话表（无外键约束）
CREATE TABLE `dialogue` (
                            `id` VARCHAR(36) NOT NULL COMMENT '主键，UUID',
                            `ai_assistant_id` VARCHAR(36) NOT NULL COMMENT '关联的AI助手ID',
                            `contexts` TEXT NOT NULL COMMENT '对话上下文JSON字符串（例如List<DialogueContext>序列化结果）',
                            `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                            `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                            PRIMARY KEY (`id`),
                            KEY `idx_ai_assistant_id` (`ai_assistant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='对话表';