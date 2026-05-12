package com.promptflow.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.promptflow.dto.ClassificationResult;
import com.promptflow.entity.PromptResource;
import com.promptflow.entity.PromptCategory;
import com.promptflow.entity.PromptTag;
import com.promptflow.repository.PromptResourceRepository;
import com.promptflow.repository.PromptCategoryRepository;
import com.promptflow.repository.PromptTagRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import jakarta.annotation.PostConstruct;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 提示词自动分类和标签服务
 * 使用基于规则 + LLM 的混合策略实现智能分类
 */
@Service
public class PromptClassificationService {

    private static final Logger logger = LoggerFactory.getLogger(PromptClassificationService.class);

    @Autowired
    private PromptCategoryRepository categoryRepository;

    @Autowired
    private PromptTagRepository tagRepository;

    @Autowired
    private PromptResourceRepository promptResourceRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${api.key}")
    private String apiKey;

    @Value("${api.base-url}")
    private String baseUrl;

    @Value("${api.model}")
    private String model;

    private final WebClient webClient;

    // 分类关键词映射（用于快速规则匹配）
    private final Map<Long, List<String>> categoryKeywords = new HashMap<>();

    public PromptClassificationService() {
        this.webClient = WebClient.builder().build();
    }

    @PostConstruct
    public void init() {
        initCategoryKeywords();
    }

    // 初始化分类关键词
    public void initCategoryKeywords() {
        categoryKeywords.put(1L, Arrays.asList(
            "写", "文章", "文案", "内容", "博客", "小说", "故事", "诗歌", "散文",
            "写作", "创作", "编辑", "润色", "改写", "续写", "标题", "摘要",
            "公众号", "小红书", "知乎", "微博", "推文", "软文", "稿"
        ));
        categoryKeywords.put(2L, Arrays.asList(
            "代码", "编程", "开发", "程序", "算法", "调试", "bug", "修复",
            "java", "python", "javascript", "js", "html", "css", "sql", "数据库",
            "api", "接口", "函数", "类", "方法", "变量", "循环", "条件",
            "框架", "spring", "vue", "react", "angular", "node", "docker"
        ));
        categoryKeywords.put(3L, Arrays.asList(
            "数据", "分析", "统计", "图表", "可视化", "报表", "excel", "csv",
            "趋势", "预测", "模型", "机器学习", "深度学习", "ai", "人工智能",
            "指标", "kpi", "增长", "下降", "对比", "环比", "同比"
        ));
        categoryKeywords.put(4L, Arrays.asList(
            "设计", "创意", "图像", "图片", "海报", "logo", "ui", "ux",
            "配色", "排版", "字体", "插画", "摄影", "视频", "剪辑", "特效",
            "艺术", "美学", "风格", "概念", "灵感", "头脑风暴"
        ));
        categoryKeywords.put(5L, Arrays.asList(
            "商业", "商务", "办公", "邮件", "报告", "ppt", "演示", "汇报",
            "合同", "协议", "法务", "财务", "营销", "销售", "客户", "管理",
            "战略", "规划", "计划", "方案", "提案", "投标", "招标"
        ));
        categoryKeywords.put(6L, Arrays.asList(
            "教育", "教学", "学习", "培训", "课程", "教案", "课件", "考试",
            "作业", "论文", "学术", "研究", "知识", "讲解", "辅导", "答疑",
            "学生", "老师", "教师", "教授", "导师", "学校", "大学"
        ));
        categoryKeywords.put(7L, Arrays.asList(
            "翻译", "语言", "英语", "中文", "日文", "韩文", "法文", "德文",
            "语法", "词汇", "口语", "听力", "阅读", "写作", "学习", "外语",
            "本地化", "国际化", "字幕", "配音"
        ));
        categoryKeywords.put(8L, Arrays.asList(
            "对话", "聊天", "问答", "咨询", "客服", "角色扮演", "模拟",
            "助手", "帮助", "建议", "意见", "讨论", "交流", "沟通",
            "心理", "情感", "陪伴", "娱乐", "游戏", "剧本"
        ));
    }

    /**
     * 自动分类和打标签（混合策略）
     * 1. 先使用规则匹配快速分类
     * 2. 如果规则匹配置信度低，则使用 LLM 进行分类
     */
    @Transactional
    public ClassificationResult autoClassifyAndTag(PromptResource prompt) {
        logger.info("开始自动分类和打标签: {}", prompt.getTaskDescription());

        // 步骤1：规则匹配分类
        ClassificationResult ruleResult = classifyByRules(prompt);

        // 步骤2：如果规则匹配置信度低，使用 LLM
        ClassificationResult finalResult;
        if (ruleResult.getConfidence() < 0.7) {
            logger.info("规则匹配置信度较低({})，使用LLM进行分类", ruleResult.getConfidence());
            ClassificationResult llmResult = classifyByLLM(prompt);
            finalResult = llmResult.getConfidence() > ruleResult.getConfidence() ? llmResult : ruleResult;
        } else {
            finalResult = ruleResult;
        }

        // 步骤3：保存分类和标签
        saveClassificationResult(prompt, finalResult);

        return finalResult;
    }

    /**
     * 基于规则的快速分类
     */
    public ClassificationResult classifyByRules(PromptResource prompt) {
        String text = (prompt.getTaskDescription() + " " + prompt.getGeneratedPrompt()).toLowerCase();

        Map<Long, Integer> scores = new HashMap<>();

        for (Map.Entry<Long, List<String>> entry : categoryKeywords.entrySet()) {
            int score = 0;
            for (String keyword : entry.getValue()) {
                Pattern pattern = Pattern.compile("\\b" + keyword + "\\b");
                Matcher matcher = pattern.matcher(text);
                while (matcher.find()) {
                    score++;
                }
            }
            if (score > 0) {
                scores.put(entry.getKey(), score);
            }
        }

        if (scores.isEmpty()) {
            return new ClassificationResult(9L, "其他", Collections.emptyList(), 0.0);
        }

        // 找出得分最高的分类
        Long bestCategoryId = scores.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse(9L);

        int maxScore = scores.getOrDefault(bestCategoryId, 0);
        int totalScore = scores.values().stream().mapToInt(Integer::intValue).sum();
        double confidence = (double) maxScore / totalScore;

        // 提取标签（基于匹配到的关键词）
        List<String> tags = extractTagsFromText(text);

        Optional<PromptCategory> category = categoryRepository.findById(bestCategoryId);
        String categoryName = category.map(PromptCategory::getName).orElse("其他");

        return new ClassificationResult(bestCategoryId, categoryName, tags, confidence);
    }

    /**
     * 基于 LLM 的智能分类
     */
    public ClassificationResult classifyByLLM(PromptResource prompt) {
        try {
            String classificationPrompt = buildClassificationPrompt(prompt);

            // 构建请求体
            Map<String, Object> requestBody = Map.of(
                "model", model,
                "messages", List.of(
                    Map.of("role", "user", "content", classificationPrompt)
                ),
                "temperature", 0.3,
                "max_tokens", 500
            );

            // 调用API
            Mono<Map> responseMono = webClient.post()
                .uri(baseUrl + "/chat/completions")
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(Map.class);

            Map<String, Object> response = responseMono.block();

            if (response != null && response.containsKey("choices")) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
                if (!choices.isEmpty()) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
                    String result = (String) message.get("content");

                    // 解析 JSON 结果
                    return parseClassificationResult(result);
                }
            }

            return new ClassificationResult(9L, "其他", Collections.emptyList(), 0.0);

        } catch (Exception e) {
            logger.error("LLM分类失败", e);
            // 失败时返回默认分类
            return new ClassificationResult(9L, "其他", Collections.emptyList(), 0.0);
        }
    }

    /**
     * 构建分类提示词
     */
    private String buildClassificationPrompt(PromptResource prompt) {
        // 获取所有分类
        List<PromptCategory> categories = categoryRepository.findByIsSystemTrueOrderBySortOrderAsc();
        String categoryList = categories.stream()
            .map(c -> c.getId() + ". " + c.getName() + " - " + c.getDescription())
            .collect(Collectors.joining("\n"));

        return String.format(
            "你是一个专业的提示词分类专家。请分析以下提示词内容，完成分类和标签提取任务。\n\n" +
            "## 可选分类：\n%s\n\n" +
            "## 提示词内容：\n" +
            "任务描述：%s\n" +
            "目标受众：%s\n" +
            "输出格式：%s\n" +
            "语调风格：%s\n" +
            "生成的提示词：%s\n\n" +
            "## 任务要求：\n" +
            "1. 选择最匹配的分类ID（只能选一个）\n" +
            "2. 精确提取3个关键词作为标签（只输出3个，不要多输出，标签应该反映提示词的核心主题和用途）\n" +
            "3. 给出置信度分数（0-1之间）\n\n" +
            "## 输出格式（必须严格遵循JSON格式）：\n" +
            "{\n" +
            "    \"categoryId\": 分类ID数字,\n" +
            "    \"categoryName\": \"分类名称\",\n" +
            "    \"tags\": [\"标签1\", \"标签2\", \"标签3\"],\n" +
            "    \"confidence\": 0.95\n" +
            "}",
            categoryList,
            prompt.getTaskDescription(),
            prompt.getTargetAudience(),
            prompt.getOutputFormat(),
            prompt.getTone(),
            prompt.getGeneratedPrompt().substring(0, Math.min(300, prompt.getGeneratedPrompt().length()))
        );
    }

    /**
     * 解析 LLM 返回的分类结果
     */
    private ClassificationResult parseClassificationResult(String result) {
        try {
            // 提取 JSON 部分
            int start = result.indexOf("{");
            int end = result.lastIndexOf("}") + 1;
            if (start >= 0 && end > start) {
                String json = result.substring(start, end);
                Map<String, Object> map = objectMapper.readValue(json, new TypeReference<Map<String, Object>>() {});

                Long categoryId = ((Number) map.get("categoryId")).longValue();
                String categoryName = (String) map.get("categoryName");
                @SuppressWarnings("unchecked")
                List<String> tags = (List<String>) map.get("tags");
                double confidence = ((Number) map.get("confidence")).doubleValue();

                return new ClassificationResult(categoryId, categoryName, tags, confidence);
            }
        } catch (Exception e) {
            logger.error("解析分类结果失败: {}", result, e);
        }

        return new ClassificationResult(9L, "其他", Collections.emptyList(), 0.0);
    }

    /**
     * 从文本中提取标签
     */
    private List<String> extractTagsFromText(String text) {
        Set<String> tags = new HashSet<>();

        // 常见技术标签
        Map<String, String> techTags = new HashMap<>();
        techTags.put("python", "Python");
        techTags.put("java", "Java");
        techTags.put("javascript", "JavaScript");
        techTags.put("js", "JavaScript");
        techTags.put("sql", "SQL");
        techTags.put("ai", "AI");
        techTags.put("人工智能", "AI");
        techTags.put("excel", "Excel");
        techTags.put("ppt", "PPT");

        for (Map.Entry<String, String> entry : techTags.entrySet()) {
            if (text.contains(entry.getKey())) {
                tags.add(entry.getValue());
            }
        }

        return new ArrayList<>(tags);
    }

    /**
     * 生成恰好5个AI标签（用于保存时自动打标签）
     * 使用LLM分析提示词内容，生成5个精准的标签
     */
    @Transactional
    public List<String> generateAiTagsExactly5(PromptResource prompt) {
        logger.info("开始为提示词生成5个AI标签: {}", prompt.getName());

        try {
            // 构建专门生成5个标签的提示词
            String tagPrompt = buildFiveTagsPrompt(prompt);

            Map<String, Object> requestBody = Map.of(
                "model", model,
                "messages", List.of(
                    Map.of("role", "user", "content", tagPrompt)
                ),
                "temperature", 0.7,
                "max_tokens", 300
            );

            Mono<Map> responseMono = webClient.post()
                .uri(baseUrl + "/chat/completions")
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(Map.class);

            Map<String, Object> response = responseMono.block();

            if (response != null && response.containsKey("choices")) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
                if (!choices.isEmpty()) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
                    String result = (String) message.get("content");

                    // 解析标签列表
                    List<String> tags = parseFiveTagsResult(result);
                    if (tags.size() == 5) {
                        logger.info("成功生成5个标签: {}", tags);
                        return tags;
                    }
                }
            }

            // 失败时返回空列表
            logger.warn("LLM生成标签失败，返回空列表");
            return Collections.emptyList();

        } catch (Exception e) {
            logger.error("生成AI标签异常", e);
            return Collections.emptyList();
        }
    }

    /**
     * 为提示词打上恰好5个标签（保存时自动调用）
     */
    @Transactional
    public void applyFiveTagsToPrompt(PromptResource prompt) {
        List<String> tagNames = generateAiTagsExactly5(prompt);

        if (tagNames.isEmpty()) {
            logger.warn("未能为提示词生成标签: {}", prompt.getName());
            return;
        }

        // 关联标签
        for (String tagName : tagNames) {
            PromptTag tag = getOrCreateTag(tagName);
            prompt.addTag(tag);
            tag.incrementUsageCount();
            tagRepository.save(tag);
        }

        // 保存标签到JSON字段
        try {
            String aiTagsJson = objectMapper.writeValueAsString(tagNames);
            prompt.setAiTags(aiTagsJson);
        } catch (Exception e) {
            logger.error("保存AI标签JSON失败", e);
        }

        promptResourceRepository.save(prompt);
        logger.info("为提示词 {} 打标签完成: {}", prompt.getName(), tagNames);
    }

    /**
     * 构建生成5个标签的提示词
     */
    private String buildFiveTagsPrompt(PromptResource prompt) {
        StringBuilder sb = new StringBuilder();
        sb.append("你是一个专业的提示词标签专家。请为以下提示词生成恰好5个精准的标签。\n\n");
        sb.append("## 提示词信息：\n");
        sb.append("名称：").append(prompt.getName() != null ? prompt.getName() : "").append("\n");

        if ("agent".equals(prompt.getPromptType())) {
            sb.append("角色描述：").append(prompt.getRoleDescription() != null ? prompt.getRoleDescription() : "").append("\n");
            sb.append("核心能力：").append(prompt.getCapabilities() != null ? prompt.getCapabilities() : "").append("\n");
            sb.append("行为准则：").append(prompt.getBehaviors() != null ? prompt.getBehaviors() : "").append("\n");
        } else if ("skill".equals(prompt.getPromptType())) {
            sb.append("描述：").append(prompt.getDescription() != null ? prompt.getDescription() : "").append("\n");
            sb.append("技能类型：").append(prompt.getSkillType() != null ? prompt.getSkillType() : "").append("\n");
            sb.append("方法：").append(prompt.getMethod() != null ? prompt.getMethod() : "").append("\n");
        }

        sb.append("生成的提示词：").append(prompt.getGeneratedPrompt() != null ? prompt.getGeneratedPrompt().substring(0, Math.min(500, prompt.getGeneratedPrompt().length())) : "").append("\n\n");

        sb.append("## 要求：\n");
        sb.append("1. 生成恰好5个标签，不要多也不要少\n");
        sb.append("2. 每个标签应该是2-4个汉字\n");
        sb.append("3. 标签要精准反映提示词的核心主题、功能和用途\n");
        sb.append("4. 只输出5个标签，用换行分隔，不要包含任何解释或其他内容\n\n");
        sb.append("## 输出格式：\n");
        sb.append("标签1\n标签2\n标签3\n标签4\n标签5");

        return sb.toString();
    }

    /**
     * 解析5个标签的LLM返回结果
     */
    private List<String> parseFiveTagsResult(String result) {
        List<String> tags = new ArrayList<>();
        String[] lines = result.split("\n");

        for (String line : lines) {
            String tag = line.trim();
            // 过滤掉编号、特殊字符等
            tag = tag.replaceAll("^[0-9]+[.、、]\\s*", "");
            tag = tag.replaceAll("^[「\"']|[」\"']$", "");

            if (!tag.isEmpty() && tag.length() >= 2 && tag.length() <= 6) {
                tags.add(tag);
            }
        }

        // 确保恰好返回5个
        if (tags.size() > 5) {
            return tags.subList(0, 5);
        }

        return tags;
    }

    /**
     * 保存分类和标签结果
     */
    @Transactional
    public void saveClassificationResult(PromptResource prompt, ClassificationResult result) {
        // 保存分类
        prompt.setCategoryId(result.getCategoryId());
        prompt.setIsAutoTagged(true);

        // 保存AI标签（JSON格式）
        try {
            String aiTagsJson = objectMapper.writeValueAsString(result.getTags());
            prompt.setAiTags(aiTagsJson);
        } catch (Exception e) {
            logger.error("保存AI标签失败", e);
        }

        // 处理标签关联
        for (String tagName : result.getTags()) {
            PromptTag tag = getOrCreateTag(tagName);
            prompt.addTag(tag);
            tag.incrementUsageCount();
            tagRepository.save(tag);
        }

        promptResourceRepository.save(prompt);
        logger.info("分类和标签保存完成: categoryId={}, tags={}", result.getCategoryId(), result.getTags());
    }

    /**
     * 获取或创建标签
     */
    private PromptTag getOrCreateTag(String tagName) {
        return tagRepository.findByName(tagName)
            .orElseGet(() -> {
                PromptTag newTag = new PromptTag();
                newTag.setName(tagName);
                newTag.setColor(generateTagColor(tagName));
                newTag.setIsSystem(false);
                return tagRepository.save(newTag);
            });
    }

    /**
     * 生成标签颜色（根据标签名称哈希）
     */
    private String generateTagColor(String tagName) {
        String[] colors = {
            "#3b82f6", "#10b981", "#f59e0b", "#ec4899",
            "#6366f1", "#8b5cf6", "#14b8a6", "#f97316"
        };
        int index = Math.abs(tagName.hashCode()) % colors.length;
        return colors[index];
    }

    /**
     * 批量处理未分类的提示词
     */
    @Transactional
    public void batchClassifyUnTaggedPrompts(int batchSize) {
        logger.info("开始批量处理未分类提示词，批次大小: {}", batchSize);

        List<PromptResource> unTaggedPrompts = promptResourceRepository.findUnTaggedPrompts();

        int count = 0;
        for (PromptResource prompt : unTaggedPrompts) {
            if (count >= batchSize) break;

            try {
                autoClassifyAndTag(prompt);
                count++;

                // 避免请求过快
                Thread.sleep(200);
            } catch (Exception e) {
                logger.error("处理提示词分类失败: id={}", prompt.getId(), e);
            }
        }

        logger.info("批量处理完成，共处理 {} 条提示词", count);
    }
}
