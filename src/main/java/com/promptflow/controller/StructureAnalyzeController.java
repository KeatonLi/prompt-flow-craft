package com.promptflow.controller;

import com.promptflow.dto.ApiResponse;
import com.promptflow.dto.StructureAnalyzeRequest;
import com.promptflow.dto.StructureAnalyzeResponse;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/structure")
@CrossOrigin(origins = "*")
public class StructureAnalyzeController {

    @PostMapping("/analyze")
    public ApiResponse<StructureAnalyzeResponse> analyzeStructure(@RequestBody StructureAnalyzeRequest request) {
        String prompt = request.getPrompt();
        StructureAnalyzeResponse response = new StructureAnalyzeResponse();
        
        List<StructureAnalyzeResponse.StructureElement> elements = new ArrayList<>();
        List<String> suggestions = new ArrayList<>();
        
        // 1. 背景/上下文分析
        analyzeContext(prompt, elements, suggestions);
        
        // 2. 角色定义分析
        analyzeRole(prompt, elements, suggestions);
        
        // 3. 任务目标分析
        analyzeTask(prompt, elements, suggestions);
        
        // 4. 输出格式分析
        analyzeOutputFormat(prompt, elements, suggestions);
        
        // 5. 约束条件分析
        analyzeConstraints(prompt, elements, suggestions);
        
        // 6. 示例分析
        analyzeExamples(prompt, elements, suggestions);
        
        // 7. 变量占位符分析
        analyzeVariables(prompt, elements);
        
        // 计算总分
        int totalWeight = elements.stream().mapToInt(StructureAnalyzeResponse.StructureElement::getWeight).sum();
        int earnedWeight = elements.stream()
            .filter(StructureAnalyzeResponse.StructureElement::isPresent)
            .mapToInt(StructureAnalyzeResponse.StructureElement::getWeight)
            .sum();
        int score = totalWeight > 0 ? (int) ((earnedWeight * 100.0) / totalWeight) : 0;
        
        // 确定等级
        String level;
        if (score >= 90) level = "优秀";
        else if (score >= 70) level = "良好";
        else if (score >= 50) level = "一般";
        else level = "待改进";
        
        // 生成总结
        long presentCount = elements.stream().filter(StructureAnalyzeResponse.StructureElement::isPresent).count();
        String summary = String.format("您的提示词包含 %d/%d 个结构化元素，得分为 %d 分（%s）。", 
            presentCount, elements.size(), score, level);
        
        if (score >= 80) {
            summary += "结构清晰，AI能够很好地理解您的需求。";
        } else if (score >= 50) {
            summary += "建议补充缺失的结构元素以提高提示词效果。";
        } else {
            summary += "建议重构提示词，添加完整的结构化元素。";
        }
        
        response.setScore(score);
        response.setLevel(level);
        response.setElements(elements);
        response.setSuggestions(suggestions);
        response.setSummary(summary);
        
        return ApiResponse.success(response);
    }
    
    private void analyzeContext(String prompt, List<StructureAnalyzeResponse.StructureElement> elements, 
                                  List<String> suggestions) {
        String[] patterns = {"背景", "场景", "上下文", "情况是", "目前", "现在有", "前提是", 
            "在", "情况下", "情境", "环境", "设定", "前提条件"};
        boolean present = containsAny(prompt, patterns);
        String content = extractContent(prompt, patterns);
        
        elements.add(new StructureAnalyzeResponse.StructureElement(
            "背景/上下文",
            "描述任务发生的场景或背景信息",
            present,
            content,
            15
        ));
        
        if (!present) {
            suggestions.add("添加背景信息，如'在电商客服场景中...'或'针对初创公司...'");
        }
    }
    
    private void analyzeRole(String prompt, List<StructureAnalyzeResponse.StructureElement> elements,
                              List<String> suggestions) {
        String[] patterns = {"你是一位", "你是一个", "假设你", "扮演", "角色是", "作为", 
            "你的身份", "你担任", "请以", "你是"};
        boolean present = containsAny(prompt, patterns);
        String content = extractContent(prompt, patterns);
        
        elements.add(new StructureAnalyzeResponse.StructureElement(
            "角色定义",
            "明确指定AI扮演的角色",
            present,
            content,
            20
        ));
        
        if (!present) {
            suggestions.add("定义角色，如'你是一位资深的产品经理'或'作为技术顾问'");
        }
    }
    
    private void analyzeTask(String prompt, List<StructureAnalyzeResponse.StructureElement> elements,
                              List<String> suggestions) {
        String[] patterns = {"请", "帮我", "需要", "要求", "写", "生成", "创建", "分析", 
            "总结", "翻译", "编写", "制作", "提供", "完成", "实现"};
        boolean present = containsAny(prompt, patterns);
        String content = extractContent(prompt, patterns);
        
        elements.add(new StructureAnalyzeResponse.StructureElement(
            "任务目标",
            "明确说明需要完成的具体任务",
            present,
            content,
            25
        ));
        
        if (!present) {
            suggestions.add("明确任务，如'请帮我分析...'或'需要生成一份...'");
        }
    }
    
    private void analyzeOutputFormat(String prompt, List<StructureAnalyzeResponse.StructureElement> elements,
                                      List<String> suggestions) {
        String[] patterns = {"格式", "输出", "返回", "以", "形式", "结构", "JSON", "Markdown",
            "列表", "表格", "请以", "按照", "用", "展示为", "格式为"};
        boolean present = containsAny(prompt, patterns);
        String content = extractContent(prompt, patterns);
        
        elements.add(new StructureAnalyzeResponse.StructureElement(
            "输出格式",
            "指定期望的输出格式和结构",
            present,
            content,
            15
        ));
        
        if (!present) {
            suggestions.add("指定格式，如'请以JSON格式返回'或'用表格形式展示'");
        }
    }
    
    private void analyzeConstraints(String prompt, List<StructureAnalyzeResponse.StructureElement> elements,
                                      List<String> suggestions) {
        String[] patterns = {"不要", "避免", "只能", "必须", "限制", "不能", "确保", 
            "必须包含", "不要包含", "只允许", "不超过", "控制在", "注意", "禁止"};
        boolean present = containsAny(prompt, patterns);
        String content = extractContent(prompt, patterns);
        
        elements.add(new StructureAnalyzeResponse.StructureElement(
            "约束条件",
            "设定任务执行的限制和条件",
            present,
            content,
            15
        ));
        
        if (!present) {
            suggestions.add("添加约束，如'不要超过200字'或'仅列出前5项'");
        }
    }
    
    private void analyzeExamples(String prompt, List<StructureAnalyzeResponse.StructureElement> elements,
                                  List<String> suggestions) {
        String[] patterns = {"例如", "比如", "示例", "如下", "这样", "案例", "比如说是",
            "像这样", "举例", "样例", "比如："};
        boolean present = containsAny(prompt, patterns);
        String content = extractContent(prompt, patterns);
        
        elements.add(new StructureAnalyzeResponse.StructureElement(
            "示例说明",
            "提供输入输出的示例",
            present,
            content,
            5
        ));
        
        if (!present) {
            suggestions.add("添加示例，如'例如：输入...输出...'有助于提高准确性");
        }
    }
    
    private void analyzeVariables(String prompt, List<StructureAnalyzeResponse.StructureElement> elements) {
        Pattern pattern = Pattern.compile("\\{[^{}]+\\}");
        Matcher matcher = pattern.matcher(prompt);
        boolean present = matcher.find();
        
        String content = "";
        if (present) {
            matcher.reset();
            List<String> vars = new ArrayList<>();
            while (matcher.find()) {
                vars.add(matcher.group());
            }
            content = String.join(", ", vars);
        }
        
        elements.add(new StructureAnalyzeResponse.StructureElement(
            "变量占位符",
            "使用变量提高提示词复用性",
            present,
            content,
            5
        ));
    }
    
    private boolean containsAny(String text, String[] patterns) {
        if (text == null || text.isEmpty()) return false;
        String lowerText = text.toLowerCase();
        for (String pattern : patterns) {
            if (lowerText.contains(pattern.toLowerCase())) {
                return true;
            }
        }
        return false;
    }
    
    private String extractContent(String text, String[] patterns) {
        if (text == null || text.isEmpty()) return "";
        
        // 找到匹配的模式位置，返回上下文
        String lowerText = text.toLowerCase();
        for (String pattern : patterns) {
            int idx = lowerText.indexOf(pattern.toLowerCase());
            if (idx != -1) {
                int start = Math.max(0, idx - 10);
                int end = Math.min(text.length(), idx + pattern.length() + 30);
                return "..." + text.substring(start, end) + "...";
            }
        }
        return "";
    }
}
