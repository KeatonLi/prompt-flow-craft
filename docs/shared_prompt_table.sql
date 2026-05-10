-- ============================================
-- 提示词分享表 shared_prompt
-- ============================================
CREATE TABLE IF NOT EXISTS `shared_prompt` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `description` TEXT COMMENT '提示词描述',
  `prompt_content` TEXT COMMENT '提示词内容',
  `author_nickname` VARCHAR(100) COMMENT '作者昵称',
  `author_contact` VARCHAR(500) COMMENT '联系方式',
  `like_count` INT DEFAULT 0 COMMENT '点赞数',
  `view_count` INT DEFAULT 0 COMMENT '浏览数',
  `source_prompt_id` BIGINT COMMENT '来源历史记录ID',
  `delete_token` VARCHAR(32) COMMENT '删除令牌(UUID)',
  `last_like_time` DATETIME COMMENT '最后点赞时间',
  `created_at` DATETIME NOT NULL COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  INDEX `idx_delete_token` (`delete_token`),
  INDEX `idx_like_count` (`like_count`),
  INDEX `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='分享的提示词';
