-- ============================================
-- Prompt Flow Craft 数据库初始化脚本
-- ============================================

CREATE DATABASE IF NOT EXISTS prompt DEFAULT CHARSET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE prompt;

-- ============================================
-- Agent 提示词表
-- ============================================
CREATE TABLE IF NOT EXISTS agent_prompts (
  id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(100) COMMENT 'Agent名称',
  role_description TEXT COMMENT '角色定位描述',
  capabilities TEXT COMMENT '核心能力',
  behaviors TEXT COMMENT '行为规范',
  communication_style VARCHAR(50) COMMENT '对话风格',
  generated_prompt TEXT COMMENT '生成的完整提示词',
  author_id BIGINT COMMENT '作者ID',
  author_nickname VARCHAR(100) COMMENT '作者昵称',
  like_count INT DEFAULT 0 COMMENT '点赞数',
  view_count INT DEFAULT 0 COMMENT '浏览数',
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  INDEX idx_author_id (author_id),
  INDEX idx_like_count (like_count),
  INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Agent提示词';

-- ============================================
-- Skill 提示词表
-- ============================================
CREATE TABLE IF NOT EXISTS skill_prompts (
  id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(100) COMMENT 'Skill名称',
  description TEXT COMMENT '功能描述',
  skill_type ENUM('api','function','webhook','data') DEFAULT 'api' COMMENT 'Skill类型',
  method VARCHAR(10) COMMENT 'HTTP方法',
  endpoint TEXT COMMENT 'API端点',
  parameters JSON COMMENT '输入参数定义',
  output_description TEXT COMMENT '输出描述',
  generated_prompt TEXT COMMENT '生成的完整提示词',
  author_id BIGINT COMMENT '作者ID',
  author_nickname VARCHAR(100) COMMENT '作者昵称',
  like_count INT DEFAULT 0 COMMENT '点赞数',
  view_count INT DEFAULT 0 COMMENT '浏览数',
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  INDEX idx_author_id (author_id),
  INDEX idx_like_count (like_count),
  INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Skill提示词';

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
