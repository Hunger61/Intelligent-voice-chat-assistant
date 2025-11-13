-- Sample dialogues inserted for testing. Conforms to schema `dialogue(id, ai_assistant_id, contexts, created_at, updated_at)`.
-- Business rules applied:
-- 1) `id` is a UUID-like string (VARCHAR(36)).
-- 2) `ai_assistant_id` is not null.
-- 3) `contexts` is a JSON-serialized array of messages. Each message has `role` (user/assistant/system) and `content`.
-- 4) created_at/updated_at provided for deterministic test data.

INSERT INTO dialogue (id, ai_assistant_id, contexts, created_at, updated_at) VALUES
('11111111-1111-1111-1111-111111111111', 'ai-0001', '[{"role":"user","content":"你好，帮我查询订单 ORD-1001 的状态。"},{"role":"assistant","content":"好的，请稍等，我正在查询。"}]', '2025-11-13 09:00:00', '2025-11-13 09:00:00'),
('22222222-2222-2222-2222-222222222222', 'ai-0001', '[{"role":"user","content":"请推荐几本 Java 相关的书籍。"},{"role":"assistant","content":"推荐：1. Effective Java 2. Java 并发编程实战 3. 深入理解 Java 虚拟机"}]', '2025-11-13 10:15:00', '2025-11-13 10:15:00'),
('33333333-3333-3333-3333-333333333333', 'ai-0002', '[{"role":"system","content":"会话由系统发起，基于计划任务提醒用户。"},{"role":"user","content":"今天有什么待办？"},{"role":"assistant","content":"您今天有 3 项待办：A、B、C。"}]', '2025-11-13 11:00:00', '2025-11-13 11:00:00'),
('44444444-4444-4444-4444-444444444444', 'ai-0003', '[{"role":"user","content":"如何重置密码？"}]', '2025-11-13 12:30:00', '2025-11-13 12:30:00'),
('55555555-5555-5555-5555-555555555555', 'ai-0002', '[{"role":"user","content":"给我一段激励的话。"},{"role":"assistant","content":"不要害怕失败，失败是成功之母。继续努力！"}]', '2025-11-13 13:45:00', '2025-11-13 13:45:00');

-- End of sample data
