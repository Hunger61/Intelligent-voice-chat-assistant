CREATE TABLE user
(
    id         VARCHAR(36) PRIMARY KEY COMMENT '用户ID（UUID）',
    name       VARCHAR(100) NOT NULL COMMENT '姓名',
    email      VARCHAR(100) UNIQUE COMMENT '邮箱',
    password   VARCHAR(255) NOT NULL COMMENT '密码',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_email (email)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='用户表';

CREATE TABLE dialogue (
                          id               VARCHAR(36) NOT NULL PRIMARY KEY COMMENT '对话ID（UUID）',
                          ai_assistant_id  VARCHAR(36) NOT NULL COMMENT 'AI 助手ID',
                          contexts         LONGTEXT NULL COMMENT '对话上下文，JSON 序列化后的字符串（建议使用 LONGTEXT）',
                          created_at       DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                          updated_at       DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                          INDEX idx_ai_assistant_id (ai_assistant_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci
    COMMENT = '对话表';
