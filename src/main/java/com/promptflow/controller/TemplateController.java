package com.promptflow.controller;

import org.springframework.web.bind.annotation.*;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/template")
@CrossOrigin(origins = "*")
public class TemplateController {

    // 预设提示词模板
    private static final List<Map<String, Object>> TEMPLATES;

    static {
        TEMPLATES = new ArrayList<>();

        // 代码生成类
        Map<String, Object> t1 = new HashMap<>();
        t1.put("id", 1); t1.put("name", "代码生成器"); t1.put("category", "developer");
        t1.put("icon", "💻"); t1.put("description", "生成高质量代码，包含注释和错误处理");
        t1.put("taskDescription", "生成一段高质量的代码，要求包含完整的注释和错误处理");
        t1.put("targetAudience", "developer"); t1.put("outputFormat", "code");
        t1.put("tone", "technical"); t1.put("examples", "// 示例代码\npublic class Example {\n    public void process() {\n        // 实现逻辑\n    }\n}");
        TEMPLATES.add(t1);

        Map<String, Object> t2 = new HashMap<>();
        t2.put("id", 2); t2.put("name", "代码审查助手"); t2.put("category", "developer");
        t2.put("icon", "🔍"); t2.put("description", "分析代码问题，提供改进建议");
        t2.put("taskDescription", "审查以下代码，分析潜在问题并提供改进建议");
        t2.put("targetAudience", "developer"); t2.put("outputFormat", "text");
        t2.put("tone", "technical"); t2.put("examples", "// 待审查代码\npublic void example() { }");
        TEMPLATES.add(t2);

        Map<String, Object> t3 = new HashMap<>();
        t3.put("id", 3); t3.put("name", "API文档生成"); t3.put("category", "developer");
        t3.put("icon", "📚"); t3.put("description", "根据代码生成专业的API文档");
        t3.put("taskDescription", "根据提供的代码生成完整的API文档");
        t3.put("targetAudience", "developer"); t3.put("outputFormat", "markdown");
        t3.put("tone", "formal"); t3.put("examples", "// 接口代码\npublic interface UserService {\n    User getUserById(Long id);\n}");
        TEMPLATES.add(t3);

        // 内容创作类
        Map<String, Object> t4 = new HashMap<>();
        t4.put("id", 4); t4.put("name", "文章写手"); t4.put("category", "content");
        t4.put("icon", "✍️"); t4.put("description", "生成结构清晰、内容丰富的文章");
        t4.put("taskDescription", "撰写一篇主题明确、结构清晰的文章");
        t4.put("targetAudience", "general"); t4.put("outputFormat", "text");
        t4.put("tone", "friendly"); t4.put("examples", "标题：人工智能的未来\n\n正文：...");
        TEMPLATES.add(t4);

        Map<String, Object> t5 = new HashMap<>();
        t5.put("id", 5); t5.put("name", "社交媒体运营"); t5.put("category", "content");
        t5.put("icon", "📱"); t5.put("description", "生成吸引人的社交媒体内容");
        t5.put("taskDescription", "生成适合社交媒体发布的创意内容");
        t5.put("targetAudience", "general"); t5.put("outputFormat", "text");
        t5.put("tone", "friendly"); t5.put("examples", "标题：惊喜！我们的产品又更新了\n\n内容：...");
        TEMPLATES.add(t5);

        Map<String, Object> t6 = new HashMap<>();
        t6.put("id", 6); t6.put("name", "营销文案"); t6.put("category", "content");
        t6.put("icon", "📣"); t6.put("description", "生成有感染力的营销文案");
        t6.put("taskDescription", "撰写有吸引力的营销文案，突出产品卖点");
        t6.put("targetAudience", "professional"); t6.put("outputFormat", "text");
        t6.put("tone", "enthusiastic"); t6.put("examples", "产品名称：XXX\n核心卖点：...");
        TEMPLATES.add(t6);

        // 教育类
        Map<String, Object> t7 = new HashMap<>();
        t7.put("id", 7); t7.put("name", "学习计划制定"); t7.put("category", "education");
        t7.put("icon", "📖"); t7.put("description", "根据目标制定个性化学习计划");
        t7.put("taskDescription", "制定一个系统的学习计划，包含目标和里程碑");
        t7.put("targetAudience", "student"); t7.put("outputFormat", "list");
        t7.put("tone", "friendly"); t7.put("examples", "目标：学习Python\n时间：3个月");
        TEMPLATES.add(t7);

        Map<String, Object> t8 = new HashMap<>();
        t8.put("id", 8); t8.put("name", "知识点解释"); t8.put("category", "education");
        t8.put("icon", "🎓"); t8.put("description", "用通俗易懂的方式解释复杂概念");
        t8.put("taskDescription", "用简单易懂的语言解释一个复杂概念");
        t8.put("targetAudience", "student"); t8.put("outputFormat", "text");
        t8.put("tone", "friendly"); t8.put("examples", "概念：机器学习\n受众：初学者");
        TEMPLATES.add(t8);

        // 角色扮演类
        Map<String, Object> t9 = new HashMap<>();
        t9.put("id", 9); t9.put("name", "AI助手"); t9.put("category", "assistant");
        t9.put("icon", "🤖"); t9.put("description", "扮演专业的AI助手回答问题");
        t9.put("taskDescription", "扮演一个专业、耐心的AI助手，回答用户问题");
        t9.put("targetAudience", "general"); t9.put("outputFormat", "text");
        t9.put("tone", "friendly"); t9.put("examples", "问题：什么是机器学习？");
        TEMPLATES.add(t9);

        Map<String, Object> t10 = new HashMap<>();
        t10.put("id", 10); t10.put("name", "面试官"); t10.put("category", "assistant");
        t10.put("icon", "👔"); t10.put("description", "扮演技术面试官进行模拟面试");
        t10.put("taskDescription", "扮演技术面试官，进行模拟面试并给出反馈");
        t10.put("targetAudience", "professional"); t10.put("outputFormat", "text");
        t10.put("tone", "formal"); t10.put("examples", "岗位：Java开发工程师\n经验要求：3年+");
        TEMPLATES.add(t10);

        // 数据分析类
        Map<String, Object> t11 = new HashMap<>();
        t11.put("id", 11); t11.put("name", "数据分析报告"); t11.put("category", "data");
        t11.put("icon", "📊"); t11.put("description", "生成专业的数据分析报告");
        t11.put("taskDescription", "分析提供的数据，生成专业的分析报告");
        t11.put("targetAudience", "professional"); t11.put("outputFormat", "text");
        t11.put("tone", "formal"); t11.put("examples", "数据：销售数据\n目标：分析增长趋势");
        TEMPLATES.add(t11);

        Map<String, Object> t12 = new HashMap<>();
        t12.put("id", 12); t12.put("name", "数据可视化建议"); t12.put("category", "data");
        t12.put("icon", "📈"); t12.put("description", "推荐合适的数据可视化方案");
        t12.put("taskDescription", "根据数据类型推荐最合适的数据可视化方式");
        t12.put("targetAudience", "professional"); t12.put("outputFormat", "list");
        t12.put("tone", "technical"); t12.put("examples", "数据类型：时间序列\n数据量：1000+条");
        TEMPLATES.add(t12);

        // 翻译语言类
        Map<String, Object> t13 = new HashMap<>();
        t13.put("id", 13); t13.put("name", "专业翻译"); t13.put("category", "language");
        t13.put("icon", "🌍"); t13.put("description", "提供准确、流畅的翻译服务");
        t13.put("taskDescription", "准确翻译指定内容，保持原文风格");
        t13.put("targetAudience", "general"); t13.put("outputFormat", "text");
        t13.put("tone", "formal"); t13.put("examples", "原文：Hello, world!\n目标语言：中文");
        TEMPLATES.add(t13);

        Map<String, Object> t14 = new HashMap<>();
        t14.put("id", 14); t14.put("name", "语法纠错"); t14.put("category", "language");
        t14.put("icon", "✏️"); t14.put("description", "检查并纠正语法错误");
        t14.put("taskDescription", "检查文本语法错误并提供纠正建议");
        t14.put("targetAudience", "general"); t14.put("outputFormat", "text");
        t14.put("tone", "friendly"); t14.put("examples", "原文：He go to school yesterday.");
        TEMPLATES.add(t14);

        // 创意类
        Map<String, Object> t15 = new HashMap<>();
        t15.put("id", 15); t15.put("name", "故事创作"); t15.put("category", "creative");
        t15.put("icon", "🎨"); t15.put("description", "创作引人入胜的故事");
        t15.put("taskDescription", "创作一个有趣、情节完整的故事");
        t15.put("targetAudience", "general"); t15.put("outputFormat", "text");
        t15.put("tone", "friendly"); t15.put("examples", "主题：冒险\n主角：一个小男孩");
        TEMPLATES.add(t15);

        Map<String, Object> t16 = new HashMap<>();
        t16.put("id", 16); t16.put("name", "诗歌创作"); t16.put("category", "creative");
        t16.put("icon", "📝"); t16.put("description", "创作优美诗句");
        t16.put("taskDescription", "根据主题创作诗歌或诗句");
        t16.put("targetAudience", "general"); t16.put("outputFormat", "text");
        t16.put("tone", "artistic"); t16.put("examples", "主题：春天\n风格：现代诗");
        TEMPLATES.add(t16);

        // 问答类
        Map<String, Object> t17 = new HashMap<>();
        t17.put("id", 17); t17.put("name", "FAQ问答"); t17.put("category", "qa");
        t17.put("icon", "❓"); t17.put("description", "生成常见问题及答案");
        t17.put("taskDescription", "针对产品/服务生成常见问题及答案");
        t17.put("targetAudience", "professional"); t17.put("outputFormat", "list");
        t17.put("tone", "formal"); t17.put("examples", "产品：在线教育平台");
        TEMPLATES.add(t17);

        Map<String, Object> t18 = new HashMap<>();
        t18.put("id", 18); t18.put("name", "知识问答"); t18.put("category", "qa");
        t18.put("icon", "💡"); t18.put("description", "生成知识问答题目");
        t18.put("taskDescription", "根据知识点生成问答题目和答案");
        t18.put("targetAudience", "student"); t18.put("outputFormat", "list");
        t18.put("tone", "friendly"); t18.put("examples", "知识点：Python基础");
        TEMPLATES.add(t18);
    }

    // 获取所有模板
    @GetMapping("/list")
    public Map<String, Object> getTemplates(@RequestParam(required = false) String category) {
        List<Map<String, Object>> result;
        try {
            if (category != null && !category.isEmpty()) {
                result = new ArrayList<>();
                for (Map<String, Object> t : TEMPLATES) {
                    Object catObj = t.get("category");
                    if (catObj != null && category.equals(catObj.toString())) {
                        result.add(t);
                    }
                }
            } else {
                result = new ArrayList<>(TEMPLATES);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", result);
            return response;
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "获取模板列表失败: " + e.getMessage());
            return response;
        }
    }

    // 获取模板详情
    @GetMapping("/{id}")
    public Map<String, Object> getTemplate(@PathVariable Integer id) {
        Map<String, Object> template = null;
        for (Map<String, Object> t : TEMPLATES) {
            Object idObj = t.get("id");
            if (idObj != null && id.equals(idObj)) {
                template = t;
                break;
            }
        }

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
        List<Map<String, Object>> categories = new ArrayList<>();

        Map<String, Object> c1 = new HashMap<>();
        c1.put("id", "developer"); c1.put("name", "💻 开发"); c1.put("count", 3);
        categories.add(c1);

        Map<String, Object> c2 = new HashMap<>();
        c2.put("id", "content"); c2.put("name", "✍️ 内容创作"); c2.put("count", 3);
        categories.add(c2);

        Map<String, Object> c3 = new HashMap<>();
        c3.put("id", "education"); c3.put("name", "📖 教育学习"); c3.put("count", 2);
        categories.add(c3);

        Map<String, Object> c4 = new HashMap<>();
        c4.put("id", "assistant"); c4.put("name", "🤖 AI助手"); c4.put("count", 2);
        categories.add(c4);

        Map<String, Object> c5 = new HashMap<>();
        c5.put("id", "data"); c5.put("name", "📊 数据分析"); c5.put("count", 2);
        categories.add(c5);

        Map<String, Object> c6 = new HashMap<>();
        c6.put("id", "language"); c6.put("name", "🌍 语言翻译"); c6.put("count", 2);
        categories.add(c6);

        Map<String, Object> c7 = new HashMap<>();
        c7.put("id", "creative"); c7.put("name", "🎨 创意写作"); c7.put("count", 2);
        categories.add(c7);

        Map<String, Object> c8 = new HashMap<>();
        c8.put("id", "qa"); c8.put("name", "❓ 问答知识"); c8.put("count", 2);
        categories.add(c8);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", categories);
        return response;
    }
}
