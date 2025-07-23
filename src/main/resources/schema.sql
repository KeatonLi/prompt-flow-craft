-- 创建数据库（如果不存在）
CREATE DATABASE IF NOT EXISTS prompt_flow_craft 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

-- 使用数据库
USE prompt_flow_craft;

-- 创建提示词缓存表
CREATE TABLE IF NOT EXISTS prompt_cache (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    task_description TEXT COMMENT '任务描述',
    target_audience VARCHAR(255) COMMENT '目标受众',
    output_format VARCHAR(255) COMMENT '输出格式',
    constraints TEXT COMMENT '约束条件',
    examples TEXT COMMENT '参考示例',
    tone VARCHAR(255) COMMENT '语调风格',
    length VARCHAR(255) COMMENT '内容长度',
    generated_prompt TEXT NOT NULL COMMENT '生成的提示词',
    request_hash VARCHAR(32) UNIQUE NOT NULL COMMENT '请求参数的MD5哈希值',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    hit_count INT DEFAULT 0 COMMENT '缓存命中次数'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='提示词缓存表';

-- 创建索引
CREATE INDEX idx_request_hash ON prompt_cache(request_hash);
CREATE INDEX idx_created_at ON prompt_cache(created_at);
CREATE INDEX idx_hit_count ON prompt_cache(hit_count);
CREATE INDEX idx_task_description ON prompt_cache(task_description(100));

-- 插入示例数据（可选）
-- INSERT INTO prompt_cache (
--     task_description, target_audience, output_format, constraints, 
--     examples, tone, length, generated_prompt, request_hash, hit_count
-- ) VALUES (
--     '写一篇关于人工智能的文章', '技术爱好者', 'Markdown格式', '不超过1000字', 
--     '参考最新的AI发展趋势', '专业严谨', '中等长度', 
--     '你是一位资深的AI技术专家，请写一篇关于人工智能发展趋势的专业文章...', 
--     'example_hash_123456789', 0
-- );