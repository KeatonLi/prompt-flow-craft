-- ============================================
-- Prompt Flow Craft 数据库初始化脚本
-- ============================================

CREATE DATABASE IF NOT EXISTS prompt DEFAULT CHARSET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE prompt;

-- ============================================
-- 统一提示词资源表（替代原来的 prompt_cache）
-- 支持 Agent 和 Skill 两种类型
-- ============================================
CREATE TABLE IF NOT EXISTS prompt_resource (
  id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,

  -- 提示词类型：agent 或 skill 或 generic
  prompt_type VARCHAR(20) COMMENT '提示词类型',

  -- 名称（Agent名称、Skill名称或泛化提示词标题）
  name VARCHAR(100) COMMENT '名称',

  -- ========== Agent 特有字段 ==========
  role_description TEXT COMMENT '角色定位描述',
  capabilities TEXT COMMENT '核心能力',
  behaviors TEXT COMMENT '行为规范',
  communication_style VARCHAR(50) COMMENT '对话风格',

  -- ========== Skill 特有字段 ==========
  description TEXT COMMENT '功能描述',
  skill_type VARCHAR(20) COMMENT 'Skill类型',
  method VARCHAR(10) COMMENT 'HTTP方法',
  endpoint TEXT COMMENT 'API端点',
  parameters JSON COMMENT '输入参数定义',
  output_description TEXT COMMENT '输出描述',

  -- ========== 旧泛化提示词字段 ==========
  task_description TEXT COMMENT '任务描述',
  target_audience VARCHAR(200) COMMENT '目标受众',
  output_format VARCHAR(100) COMMENT '输出格式',
  constraints TEXT COMMENT '约束条件',
  examples TEXT COMMENT '示例',
  tone VARCHAR(50) COMMENT '语气风格',
  length VARCHAR(50) COMMENT '长度要求',
  request_hash VARCHAR(64) COMMENT '请求哈希（唯一）',

  -- ========== 通用字段 ==========
  generated_prompt TEXT COMMENT '生成的完整提示词',
  prompt_summary VARCHAR(500) COMMENT '提示词摘要',

  -- ========== 作者与统计 ==========
  author_id BIGINT COMMENT '作者ID',
  author_nickname VARCHAR(100) COMMENT '作者昵称',
  like_count INT DEFAULT 0 COMMENT '点赞数',
  view_count INT DEFAULT 0 COMMENT '浏览数',
  hit_count INT DEFAULT 0 COMMENT '命中次数',

  -- ========== 分类与标签 ==========
  category_id BIGINT COMMENT '分类ID',
  is_auto_tagged BIT DEFAULT b'0' COMMENT '是否自动打标签',
  ai_tags JSON COMMENT 'AI生成的标签',

  -- ========== 评分与效果 ==========
  usage_scenario VARCHAR(200) COMMENT '使用场景',
  effectiveness_score INT COMMENT '效果评分',
  user_rating INT COMMENT '用户评分（1-5星）',
  rating_comment TEXT COMMENT '评分评论',
  rating_count INT DEFAULT 0 COMMENT '评分次数',
  average_rating DOUBLE COMMENT '平均评分',

  -- ========== Tokens 统计 ==========
  input_tokens BIGINT DEFAULT 0 COMMENT '输入tokens数',
  output_tokens BIGINT DEFAULT 0 COMMENT '输出tokens数',

  -- ========== 时间戳 ==========
  last_like_time DATETIME COMMENT '最后点赞时间',
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',

  -- 索引
  INDEX idx_prompt_type (prompt_type),
  INDEX idx_name (name),
  INDEX idx_author_id (author_id),
  INDEX idx_category_id (category_id),
  INDEX idx_like_count (like_count),
  INDEX idx_created_at (created_at),
  INDEX idx_request_hash (request_hash)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='统一提示词资源表';

-- ============================================
-- 分类表
-- ============================================
CREATE TABLE IF NOT EXISTS prompt_category (
  id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(100) NOT NULL COMMENT '分类名称',
  description VARCHAR(500) COMMENT '分类描述',
  icon VARCHAR(50) COMMENT '分类图标',
  color VARCHAR(20) COMMENT '主题颜色',
  sort_order INT COMMENT '排序顺序',
  is_system BIT DEFAULT b'0' COMMENT '是否系统预设',
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='提示词分类';

-- ============================================
-- 标签表
-- ============================================
CREATE TABLE IF NOT EXISTS prompt_tags (
  id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(50) NOT NULL UNIQUE COMMENT '标签名称',
  color VARCHAR(20) COMMENT '标签颜色',
  usage_count BIGINT DEFAULT 0 COMMENT '使用次数',
  is_system BIT DEFAULT b'0' COMMENT '是否系统预设',
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='提示词标签';

-- ============================================
-- 标签关联表
-- ============================================
CREATE TABLE IF NOT EXISTS prompt_tag_relation (
  prompt_id BIGINT NOT NULL,
  tag_id BIGINT NOT NULL,
  PRIMARY KEY (prompt_id, tag_id),
  INDEX idx_tag_id (tag_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='提示词标签关联表';

-- ============================================
-- 分享表
-- ============================================
CREATE TABLE IF NOT EXISTS shared_prompt (
  id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  description TEXT COMMENT '提示词描述',
  prompt_content TEXT COMMENT '提示词内容',
  author_nickname VARCHAR(100) COMMENT '作者昵称',
  author_contact VARCHAR(500) COMMENT '联系方式',
  like_count INT DEFAULT 0 COMMENT '点赞数',
  view_count INT DEFAULT 0 COMMENT '浏览数',
  source_prompt_id BIGINT COMMENT '来源历史记录ID',
  delete_token VARCHAR(32) COMMENT '删除令牌',
  last_like_time DATETIME COMMENT '最后点赞时间',
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  INDEX idx_delete_token (delete_token),
  INDEX idx_like_count (like_count),
  INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='分享的提示词';

-- ============================================
-- 初始化预设分类
-- ============================================
INSERT INTO prompt_category (name, description, icon, color, sort_order, is_system) VALUES
('办公效率', '提升工作效率的提示词', '💼', '#3b82f6', 1, b'1'),
('内容创作', '文章、文案等创作类提示词', '✍️', '#8b5cf6', 2, b'1'),
('代码开发', '编程、代码相关的提示词', '💻', '#10b981', 3, b'1'),
('教育培训', '教育，学习相关的提示词', '📚', '#f59e0b', 4, b'1'),
('视频媒体', '视频、音频媒体相关的提示词', '🎬', '#ef4444', 5, b'1'),
('数据分析', '数据处理，分析相关的提示词', '📊', '#06b6d4', 6, b'1'),
('设计创意', '设计、创意相关的提示词', '🎨', '#ec4899', 7, b'1'),
('智能对话', '对话、问答相关的提示词', '💬', '#6366f1', 8, b'1')
ON DUPLICATE KEY UPDATE name = VALUES(name);
