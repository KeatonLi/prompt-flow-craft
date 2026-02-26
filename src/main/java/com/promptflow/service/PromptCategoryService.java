package com.promptflow.service;

import com.promptflow.entity.PromptCategory;
import com.promptflow.repository.PromptCacheRepository;
import com.promptflow.repository.PromptCategoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PromptCategoryService {

    private static final Logger logger = LoggerFactory.getLogger(PromptCategoryService.class);

    @Autowired
    private PromptCategoryRepository categoryRepository;

    @Autowired
    private PromptCacheRepository promptCacheRepository;

    /**
     * 初始化默认分类数据
     */
    @PostConstruct
    @Transactional
    public void initDefaultCategories() {
        logger.info("检查并初始化默认分类数据");

        // 如果没有任何分类，则创建默认分类
        if (categoryRepository.count() == 0) {
            logger.info("创建默认分类数据");

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

    /**
     * 获取所有分类及统计信息
     */
    public List<CategoryStats> getCategoriesWithStats() {
        List<PromptCategory> categories = categoryRepository.findAllByOrderBySortOrderAsc();

        // 获取各分类的提示词数量
        Map<Long, Long> countMap = promptCacheRepository.countByCategory().stream()
                .collect(Collectors.toMap(
                        arr -> (Long) arr[0],
                        arr -> (Long) arr[1]
                ));

        return categories.stream()
                .map(cat -> new CategoryStats(
                        cat,
                        countMap.getOrDefault(cat.getId(), 0L)
                ))
                .collect(Collectors.toList());
    }

    /**
     * 分类统计信息
     */
    public static class CategoryStats {
        private final PromptCategory category;
        private final long promptCount;

        public CategoryStats(PromptCategory category, long promptCount) {
            this.category = category;
            this.promptCount = promptCount;
        }

        public PromptCategory getCategory() {
            return category;
        }

        public long getPromptCount() {
            return promptCount;
        }
    }
}
