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
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
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

    // 提示词模板
    private String classificationPromptTemplate;
    private String fiveTagsPromptTemplate;

    public PromptClassificationService() {
        this.webClient = WebClient.builder().build();
    }

    @PostConstruct
    public void init() {
        initCategoryKeywords();
        loadPromptTemplates();
    }

    /**
     * 加载提示词模板
     */
    private void loadPromptTemplates() {
        try {
            classificationPromptTemplate = loadTemplate("prompts/classification_prompt_template.txt");
            fiveTagsPromptTemplate = loadTemplate("prompts/five_tags_prompt_template.txt");
            logger.info("分类标签提示词模板加载成功");
        } catch (Exception e) {
            logger.error("加载分类标签提示词模板失败", e);
            throw new RuntimeException("加载分类标签提示词模板失败", e);
        }
    }

    /**
     * 从 classpath 加载模板文件
     */
    private String loadTemplate(String path) {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(path)) {
            if (is == null) {
                throw new RuntimeException("找不到模板文件: " + path);
            }
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("读取模板文件失败: " + path, e);
        }
    }

    /**
     * 替换模板中的占位符
     */
    private String replacePlaceholders(String template, Object... keyValues) {
        String result = template;
        for (int i = 0; i < keyValues.length; i += 2) {
            String key = (String) keyValues[i];
            String value = keyValues[i + 1] != null ? (String) keyValues[i + 1] : "";
            result = result.replace("{" + key + "}", value);
        }
        return result;
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

        String generatedPrompt = prompt.getGeneratedPrompt() != null
            ? prompt.getGeneratedPrompt().substring(0, Math.min(300, prompt.getGeneratedPrompt().length()))
            : "";

        return replacePlaceholders(classificationPromptTemplate,
            "categoryList", categoryList,
            "taskDescription", prompt.getTaskDescription() != null ? prompt.getTaskDescription() : "",
            "targetAudience", prompt.getTargetAudience() != null ? prompt.getTargetAudience() : "",
            "outputFormat", prompt.getOutputFormat() != null ? prompt.getOutputFormat() : "",
            "tone", prompt.getTone() != null ? prompt.getTone() : "",
            "generatedPrompt", generatedPrompt);
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

        // 标记为已自动打标签
        prompt.setIsAutoTagged(true);

        promptResourceRepository.save(prompt);
        logger.info("为提示词 {} 打标签完成: {}", prompt.getName(), tagNames);
    }

    /**
     * 构建生成5个标签的提示词
     */
    private String buildFiveTagsPrompt(PromptResource prompt) {
        String generatedPrompt = prompt.getGeneratedPrompt() != null
            ? prompt.getGeneratedPrompt().substring(0, Math.min(500, prompt.getGeneratedPrompt().length()))
            : "";

        String roleDescription = "";
        String capabilities = "";
        String behaviors = "";
        String description = "";
        String skillType = "";
        String method = "";

        if ("agent".equals(prompt.getPromptType())) {
            roleDescription = prompt.getRoleDescription() != null ? prompt.getRoleDescription() : "";
            capabilities = prompt.getCapabilities() != null ? prompt.getCapabilities() : "";
            behaviors = prompt.getBehaviors() != null ? prompt.getBehaviors() : "";
        } else if ("skill".equals(prompt.getPromptType())) {
            description = prompt.getDescription() != null ? prompt.getDescription() : "";
            skillType = prompt.getSkillType() != null ? prompt.getSkillType() : "";
            method = prompt.getMethod() != null ? prompt.getMethod() : "";
        }

        return replacePlaceholders(fiveTagsPromptTemplate,
            "name", prompt.getName() != null ? prompt.getName() : "",
            "roleDescription", roleDescription,
            "capabilities", capabilities,
            "behaviors", behaviors,
            "description", description,
            "skillType", skillType,
            "method", method,
            "generatedPrompt", generatedPrompt);
    }

    /**
     * 解析5个标签的LLM返回结果
     */
    private List<String> parseFiveTagsResult(String result) {
        List<String> tags = new ArrayList<>();
        String[] lines = result.split("\n");

        // 过滤掉LLM返回的干扰前缀
        String[] prefixesToFilter = {"关键主题", "可能的标签", "标签", "以下是", "输出", "结果", "请输出", "标签列表"};

        for (String line : lines) {
            String tag = line.trim();

            // 跳过包含这些关键词的行（它们是LLM的说明文字）
            boolean isInterference = false;
            for (String prefix : prefixesToFilter) {
                if (tag.contains(prefix) || tag.startsWith(prefix + "：") || tag.startsWith(prefix + ":")) {
                    isInterference = true;
                    break;
                }
            }
            if (isInterference) continue;

            // 过滤掉编号、特殊字符、前缀符号
            tag = tag.replaceAll("^[0-9]+[.、、.\\s]*", "");
            tag = tag.replaceAll("^[「\"'‘’\"\"\\-–—\\s]+|[」\"'‘’\"\"\\-–—\\s]+$", "");

            // 过滤掉以 "-"、"*" 等开头的项
            tag = tag.replaceAll("^[-*+.\\s]+", "");

            // 去重检查
            if (tags.contains(tag)) continue;

            // 中文标签：2-4个汉字
            if (tag.matches("^[一-龥]{2,4}$")) {
                tags.add(tag);
            }
            // 英文标签：2-10个字母
            else if (tag.matches("^[a-zA-Z]{2,10}$")) {
                tags.add(tag);
            }
        }

        // 如果解析出的标签少于5个，尝试更宽松的匹配
        if (tags.size() < 5) {
            for (String line : lines) {
                String tag = line.trim();
                tag = tag.replaceAll("^[0-9]+[.、、.\\s]*", "");
                tag = tag.replaceAll("^[「\"'‘’\"\"\\-–—\\s]+|[」\"'‘’\"\"\\-–—\\s]+$", "");
                tag = tag.replaceAll("^[-*+.\\s]+", "");

                // 去重检查
                if (tags.contains(tag)) continue;

                // 中英文混合：2-8个字符
                if (tag.matches("^[a-zA-Z一-龥]{2,8}$")) {
                    tags.add(tag);
                    if (tags.size() >= 5) break;
                }
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
