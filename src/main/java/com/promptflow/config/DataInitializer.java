package com.promptflow.config;

import com.promptflow.entity.PromptCategory;
import com.promptflow.entity.PromptTag;
import com.promptflow.repository.PromptCategoryRepository;
import com.promptflow.repository.PromptTagRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 数据初始化组件
 * 应用启动时自动创建默认分类和标签
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    @Autowired
    private PromptCategoryRepository categoryRepository;

    @Autowired
    private PromptTagRepository tagRepository;

    @Override
    @Transactional
    public void run(String... args) {
        logger.info("开始初始化数据...");
        initDefaultCategories();
        initDefaultTags();
        logger.info("数据初始化完成");
    }

    /**
     * 初始化默认分类
     */
    private void initDefaultCategories() {
        if (categoryRepository.count() > 0) {
            logger.info("分类数据已存在，跳过初始化");
            return;
        }

        logger.info("创建默认分类数据...");

        List<PromptCategory> defaultCategories = List.of(
            createCategory(1L, "写作创作", "文章、文案、内容创作、博客、小说等写作相关", "✍️", "#3b82f6", 1),
            createCategory(2L, "编程开发", "编程、代码、开发、算法、调试、API等技术相关", "💻", "#10b981", 2),
            createCategory(3L, "数据分析", "数据处理、分析、可视化、报表等相关", "📊", "#f59e0b", 3),
            createCategory(4L, "创意设计", "图像、设计、创意、艺术等相关", "🎨", "#ec4899", 4),
            createCategory(5L, "商业办公", "商务、办公、邮件、报告、演示等相关", "💼", "#6366f1", 5),
            createCategory(6L, "教育培训", "教学、学习、培训、考试、知识等相关", "📚", "#8b5cf6", 6),
            createCategory(7L, "翻译语言", "翻译、语言学习、语法、写作等相关", "🌐", "#14b8a6", 7),
            createCategory(8L, "对话聊天", "对话、聊天、问答、咨询、客服等相关", "💬", "#f97316", 8),
            createCategory(9L, "其他", "不属于以上分类的其他类型", "📌", "#6b7280", 99)
        );

        categoryRepository.saveAll(defaultCategories);
        logger.info("默认分类数据创建完成，共 {} 个", defaultCategories.size());
    }

    /**
     * 初始化默认标签
     */
    private void initDefaultTags() {
        if (tagRepository.count() > 0) {
            logger.info("标签数据已存在，跳过初始化");
            return;
        }

        logger.info("创建默认标签数据...");

        List<PromptTag> defaultTags = List.of(
            createTag("AI", "#3b82f6"),
            createTag("教程", "#10b981"),
            createTag("模板", "#f59e0b"),
            createTag("高效", "#ec4899"),
            createTag("专业", "#6366f1"),
            createTag("创意", "#8b5cf6"),
            createTag("简洁", "#14b8a6"),
            createTag("详细", "#f97316"),
            createTag("实用", "#22c55e"),
            createTag("进阶", "#f59e0b")
        );

        tagRepository.saveAll(defaultTags);
        logger.info("默认标签数据创建完成，共 {} 个", defaultTags.size());
    }

    private PromptCategory createCategory(Long id, String name, String description, String icon, String color, int sortOrder) {
        PromptCategory category = new PromptCategory();
        category.setId(id);
        category.setName(name);
        category.setDescription(description);
        category.setIcon(icon);
        category.setColor(color);
        category.setSortOrder(sortOrder);
        category.setIsSystem(true);
        return category;
    }

    private PromptTag createTag(String name, String color) {
        PromptTag tag = new PromptTag();
        tag.setName(name);
        tag.setColor(color);
        tag.setUsageCount(0);
        tag.setIsSystem(true);
        return tag;
    }
}
