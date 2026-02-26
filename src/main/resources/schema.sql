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

-- 扩展 prompt_cache 表，添加分类和标签相关字段
ALTER TABLE prompt_cache ADD COLUMN IF NOT EXISTS category_id BIGINT COMMENT '分类ID' AFTER hit_count;
ALTER TABLE prompt_cache ADD COLUMN IF NOT EXISTS is_favorite BOOLEAN DEFAULT FALSE COMMENT '是否收藏' AFTER category_id;
ALTER TABLE prompt_cache ADD COLUMN IF NOT EXISTS is_auto_tagged BOOLEAN DEFAULT FALSE COMMENT '是否自动打标签' AFTER is_favorite;
ALTER TABLE prompt_cache ADD COLUMN IF NOT EXISTS ai_tags JSON COMMENT 'AI识别的标签' AFTER is_auto_tagged;
ALTER TABLE prompt_cache ADD COLUMN IF NOT EXISTS usage_scenario VARCHAR(200) COMMENT '使用场景' AFTER ai_tags;
ALTER TABLE prompt_cache ADD COLUMN IF NOT EXISTS effectiveness_score INT COMMENT '效果评分(1-5)' AFTER usage_scenario;

-- 新增索引
CREATE INDEX IF NOT EXISTS idx_category_id ON prompt_cache(category_id);
CREATE INDEX IF NOT EXISTS idx_is_favorite ON prompt_cache(is_favorite);

-- 创建分类表
CREATE TABLE IF NOT EXISTS prompt_category (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    name VARCHAR(100) NOT NULL COMMENT '分类名称',
    description VARCHAR(500) COMMENT '分类描述',
    icon VARCHAR(50) COMMENT '图标标识',
    color VARCHAR(20) COMMENT '主题色',
    sort_order INT DEFAULT 0 COMMENT '排序',
    is_system BOOLEAN DEFAULT FALSE COMMENT '是否系统预设',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='提示词分类表';

-- 创建标签表
CREATE TABLE IF NOT EXISTS prompt_tag (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    name VARCHAR(50) NOT NULL COMMENT '标签名称',
    color VARCHAR(20) COMMENT '标签颜色',
    usage_count INT DEFAULT 0 COMMENT '使用次数',
    is_system BOOLEAN DEFAULT FALSE COMMENT '是否系统预设',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_tag_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='提示词标签表';

-- 创建提示词与标签关联表（多对多）
CREATE TABLE IF NOT EXISTS prompt_tag_relation (
    prompt_id BIGINT NOT NULL COMMENT '提示词ID',
    tag_id BIGINT NOT NULL COMMENT '标签ID',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (prompt_id, tag_id),
    FOREIGN KEY (prompt_id) REFERENCES prompt_cache(id) ON DELETE CASCADE,
    FOREIGN KEY (tag_id) REFERENCES prompt_tag(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='提示词标签关联表';

-- 插入默认分类数据
INSERT INTO prompt_category (id, name, description, icon, color, sort_order, is_system) VALUES
(1, '写作创作', '文章、文案、内容创作、博客、小说等写作相关', '✍️', '#3b82f6', 1, TRUE),
(2, '编程开发', '编程、代码、开发、算法、调试、API等技术相关', '💻', '#10b981', 2, TRUE),
(3, '数据分析', '数据处理、分析、可视化、报表等相关', '📊', '#f59e0b', 3, TRUE),
(4, '创意设计', '图像、设计、创意、艺术等相关', '🎨', '#ec4899', 4, TRUE),
(5, '商业办公', '商务、办公、邮件、报告、演示等相关', '💼', '#6366f1', 5, TRUE),
(6, '教育培训', '教学、学习、培训、考试、知识等相关', '📚', '#8b5cf6', 6, TRUE),
(7, '翻译语言', '翻译、语言学习、语法、写作等相关', '🌐', '#14b8a6', 7, TRUE),
(8, '对话聊天', '对话、聊天、问答、咨询、客服等相关', '💬', '#f97316', 8, TRUE),
(9, '其他', '不属于以上分类的其他类型', '📌', '#6b7280', 99, TRUE)
ON DUPLICATE KEY UPDATE name = VALUES(name), description = VALUES(description);

-- 插入常用系统标签
INSERT INTO prompt_tag (name, color, usage_count, is_system) VALUES
('AI', '#3b82f6', 0, TRUE),
('教程', '#10b981', 0, TRUE),
('模板', '#f59e0b', 0, TRUE),
('高效', '#ec4899', 0, TRUE),
('专业', '#6366f1', 0, TRUE),
('创意', '#8b5cf6', 0, TRUE),
('简洁', '#14b8a6', 0, TRUE),
('详细', '#f97316', 0, TRUE)
ON DUPLICATE KEY UPDATE name = VALUES(name);