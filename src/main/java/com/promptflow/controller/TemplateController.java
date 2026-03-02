package com.promptflow.controller;

import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/template")
@CrossOrigin(origins = "*")
public class TemplateController {

    // 预设提示词模板
    private static final List<Map<String, Object>> TEMPLATES = Arrays.asList(
        // 代码生成类
        Map.of("id", 1, "name", "代码生成器", "category", "developer", "icon", "💻",
            "description", "生成高质量代码，包含注释和错误处理",
            "taskDescription", "生成一段高质量的代码，要求包含完整的注释和错误处理",
            "targetAudience", "developer", "outputFormat", "code", "tone", "technical",
            "examples", "// 示例代码\npublic class Example {\n    public void process() {\n        // 实现逻辑\n    }\n}"),
        
        // 代码审查
        Map.of("id", 2, "name", "代码审查助手", "category", "developer", "icon", "🔍",
            "description", "分析代码问题，提供改进建议",
            "taskDescription", "审查以下代码，分析潜在问题并提供改进建议",
            "targetAudience", "developer", "outputFormat", "text", "tone", "technical",
            "examples", "// 待审查代码\npublic void example() { }"),
            
        // 文档生成
        Map.of("id", 3, "name", "API文档生成", "category", "developer", "icon", "📚",
            "description", "根据代码生成专业的API文档",
            "taskDescription", "根据提供的代码生成完整的API文档",
            "targetAudience", "developer", "outputFormat", "markdown", "tone", "formal",
            "examples", "// 接口代码\npublic interface UserService {\n    User getUserById(Long id);\n}"),

        // 内容创作类
        Map.of("id", 4, "name", "文章写手", "category", "content", "icon", "✍️",
            "description", "生成结构清晰、内容丰富的文章",
            "taskDescription", "撰写一篇主题明确、结构清晰的文章",
            "targetAudience", "general", "outputFormat", "text", "tone", "friendly",
            "examples", "标题：人工智能的未来\n\n正文：..."),
            
        Map.of("id", 5, "name", "社交媒体运营", "category", "content", "icon", "📱",
            "description", "生成吸引人的社交媒体内容",
            "taskDescription", "生成适合社交媒体发布的创意内容",
            "targetAudience", "general", "outputFormat", "text", "tone", "friendly",
            "examples", "标题：惊喜！我们的产品又更新了\n\n内容：..."),

        Map.of("id", 6, "name", "营销文案", "category", "content", "icon", "📣",
            "description", "生成有感染力的营销文案",
            "taskDescription", "撰写有吸引力的营销文案，突出产品卖点",
            "targetAudience", "professional", "outputFormat", "text", "tone", "enthusiastic",
            "examples", "产品名称：XXX\n核心卖点：..."),

        // 教育类
        Map.of("id", 7, "name", "学习计划制定", "category", "education", "icon", "📖",
            "description", "根据目标制定个性化学习计划",
            "taskDescription", "制定一个系统的学习计划，包含目标和里程碑",
            "targetAudience", "student", "outputFormat", "list", "tone", "friendly",
            "examples", "目标：学习Python\n时间：3个月"),
            
        Map.of("id", 8, "name", "知识点解释", "category", "education", "icon", "🎓",
            "description", "用通俗易懂的方式解释复杂概念",
            "taskDescription", "用简单易懂的语言解释一个复杂概念",
            "targetAudience", "student", "outputFormat", "text", "tone", "friendly",
            "examples", "概念：机器学习\n受众：初学者"),

        // 角色扮演类
        Map.of("id", 9, "name", "AI助手", "category", "assistant", "icon", "🤖",
            "description", "扮演专业的AI助手回答问题",
            "taskDescription", "扮演一个专业、耐心的AI助手，回答用户问题",
            "targetAudience", "general", "outputFormat", "text", "tone", "friendly",
            "examples", "问题：什么是机器学习？"),
            
        Map.of("id", 10, "name", "面试官", "category", "assistant", "icon", "👔",
            "description", "扮演技术面试官进行模拟面试",
            "taskDescription", "扮演技术面试官，进行模拟面试并给出反馈",
            "targetAudience", "professional", "outputFormat", "text", "tone", "formal",
            "examples", "岗位：Java开发工程师\n经验要求：3年+"),

        // 数据分析类
        Map.of("id", 11, "name", "数据分析报告", "category", "data", "icon", "📊",
            "description", "生成专业的数据分析报告",
            "taskDescription", "分析提供的数据，生成专业的分析报告",
            "targetAudience", "professional", "outputFormat", "text", "tone", "formal",
            "examples", "数据：销售数据\n目标：分析增长趋势"),
            
        Map.of("id", 12, "name", "数据可视化建议", "category", "data", "icon", "📈",
            "description", "推荐合适的数据可视化方案",
            "taskDescription", "根据数据类型推荐最合适的数据可视化方式",
            "targetAudience", "professional", "outputFormat", "list", "tone", "technical",
            "examples", "数据类型：时间序列\n数据量：1000+条"),

        // 翻译语言类
        Map.of("id", 13, "name", "专业翻译", "category", "language", "icon", "🌍",
            "description", "提供准确、流畅的翻译服务",
            "taskDescription", "准确翻译指定内容，保持原文风格",
            "targetAudience", "general", "outputFormat", "text", "tone", "formal",
            "examples", "原文：Hello, world!\n目标语言：中文"),
            
        Map.of("id", 14, "name", "语法纠错", "category", "language", "icon", "✏️",
            "description", "检查并纠正语法错误",
            "taskDescription", "检查文本语法错误并提供纠正建议",
            "targetAudience", "general", "outputFormat", "text", "tone", "friendly",
            "examples", "原文：He go to school yesterday."),

        // 创意类
        Map.of("id", 15, "name", "故事创作", "category", "creative", "icon", "🎨",
            "description", "创作引人入胜的故事",
            "taskDescription", "创作一个有趣、情节完整的故事",
            "targetAudience", "general", "outputFormat", "text", "tone", "friendly",
            "examples", "主题：冒险\n主角：一个小男孩"),
            
        Map.of("id", 16, "name", "诗歌创作", "category", "creative", "icon", "📝",
            "description", "创作优美诗句",
            "taskDescription", "根据主题创作诗歌或诗句",
            "targetAudience", "general", "outputFormat", "text", "tone", "artistic",
            "examples", "主题：春天\n风格：现代诗"),

        // 问答类
        Map.of("id", 17, "name", "FAQ问答", "category", "qa", "icon", "❓",
            "description", "生成常见问题及答案",
            "taskDescription", "针对产品/服务生成常见问题及答案",
            "targetAudience", "professional", "outputFormat", "list", "tone", "formal",
            "examples", "产品：在线教育平台"),
            
        Map.of("id", 18, "name", "知识问答", "category", "qa", "icon", "💡",
            "description", "生成知识问答题目",
            "taskDescription", "根据知识点生成问答题目和答案",
            "targetAudience", "student", "outputFormat", "list", "tone", "friendly",
            "examples", "知识点：Python基础")
    );

    // 获取所有模板
    @GetMapping("/list")
    public Map<String, Object> getTemplates(@RequestParam(required = false) String category) {
        List<Map<String, Object>> result;
        if (category != null && !category.isEmpty()) {
            result = TEMPLATES.stream()
                .filter(t -> category.equals(t.get("category")))
                .collect(java.util.stream.Collectors.toList());
        } else {
            result = new ArrayList<>(TEMPLATES);
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", result);
        return response;
    }

    // 获取模板详情
    @GetMapping("/{id}")
    public Map<String, Object> getTemplate(@PathVariable Integer id) {
        Map<String, Object> template = TEMPLATES.stream()
            .filter(t -> id.equals(t.get("id")))
            .findFirst()
            .orElse(null);
            
        Map<String, Object> response = new HashMap<>();
        if (template != null) {
            response.put("success", true);
            response.put("data", template);
        } else {
            response.put("success", false);
            response.put("message", "模板不存在");
        }
        return response;
    }

    // 获取模板分类
    @GetMapping("/categories")
    public Map<String, Object> getCategories() {
        List<Map<String, Object>> categories = Arrays.asList(
            Map.of("id", "developer", "name", "💻 开发", "count", 3),
            Map.of("id", "content", "name", "✍️ 内容创作", "count", 3),
            Map.of("id", "education", "name", "📖 教育学习", "count", 2),
            Map.of("id", "assistant", "name", "🤖 AI助手", "count", 2),
            Map.of("id", "data", "name", "📊 数据分析", "count", 2),
            Map.of("id", "language", "name", "🌍 语言翻译", "count", 2),
            Map.of("id", "creative", "name", "🎨 创意写作", "count", 2),
            Map.of("id", "qa", "name", "❓ 问答知识", "count", 2)
        );
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", categories);
        return response;
    }
}
