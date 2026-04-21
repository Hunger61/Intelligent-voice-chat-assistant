-- 插入测试用户
-- 密码：password123 
INSERT IGNORE INTO `user` (`id`, `name`, `email`, `password`, `created_at`, `updated_at`) VALUES
('123e4567-e89b-12d3-a456-426614174000', '测试用户', '1@1.1', '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjPGga31lW', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);