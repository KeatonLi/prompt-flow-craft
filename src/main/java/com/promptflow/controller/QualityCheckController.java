package com.promptflow.controller;

import com.promptflow.dto.ApiResponse;
import com.promptflow.dto.QualityCheckRequest;
import com.promptflow.dto.QualityCheckResponse;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/quality")
@CrossOrigin(origins = "*")
public class QualityCheckController {

    @PostMapping("/check")
    public ApiResponse<QualityCheckResponse> checkQuality(@RequestBody QualityCheckRequest request) {
        String prompt = request.getPrompt();
        QualityCheckResponse response = new QualityCheckResponse();
        
        List<QualityCheckResponse.CheckItem> items = new ArrayList<>();
        List<String> suggestions = new ArrayList<>();
        
        // 1. 检查长度
        int length = prompt != null ? prompt.trim().length() : 0;
        boolean lengthCheck = length >= 50 && length <= 4000;
        items.add(new QualityCheckResponse.CheckItem(
            "长度适中",
            lengthCheck,
            length < 50 ? "提示词过短，建议至少50个字符" : 
            length > 4000 ? "提示词过长，建议控制在4000字符以内" : "长度合适",
            10
        ));
        if (!lengthCheck && length < 50) {
            suggestions.add("增加提示词的详细程度，添加更多上下文信息");
        }
        
        // 2. 检查是否包含角色定义
        String[] rolePatterns = {"你是一位", "你是一个", "假设你", "扮演", "角色", "作为", "请以", "你的身份"};
        boolean hasRole = containsAny(prompt, rolePatterns);
        items.add(new QualityCheckResponse.CheckItem(
            "角色定义",
            hasRole,
            hasRole ? "已定义明确的角色" : "建议指定AI扮演的角色",
            15
        ));
        if (!hasRole) {
            suggestions.add("添加角色定义，如'你是一位专业的法律顾问'");
        }
        
        // 3. 检查是否包含任务描述
        String[] taskPatterns = {"请", "帮我", "需要", "要求", "写", "生成", "创建", "分析", "总结", "翻译"};
        boolean hasTask = containsAny(prompt, taskPatterns);
        items.add(new QualityCheckResponse.CheckItem(
            "任务明确",
            hasTask,
            hasTask ? "任务描述清晰" : "建议明确说明需要完成的任务",
            20
        ));
        if (!hasTask) {
            suggestions.add("明确说明需要AI完成什么任务");
        }
        
        // 4. 检查是否包含输出格式要求
        String[] formatPatterns = {"格式", "输出", "返回", "以", "形式", "结构", "JSON", "Markdown", "列表", "表格", "请以", "按照"};
        boolean hasFormat = containsAny(prompt, formatPatterns);
        items.add(new QualityCheckResponse.CheckItem(
            "输出格式",
            hasFormat,
            hasFormat ? "已指定输出格式" : "建议指定期望的输出格式",
            15
        ));
        if (!hasFormat) {
            suggestions.add("指定输出格式，如'请以JSON格式返回'或'用表格形式展示'");
        }
        
        // 5. 检查是否包含约束条件
        String[] constraintPatterns = {"不要", "避免", "只能", "必须", "限制", "不能", "确保", "必须包含", "不要包含"};
        boolean hasConstraint = containsAny(prompt, constraintPatterns);
        items.add(new QualityCheckResponse.CheckItem(
            "约束条件",
            hasConstraint,
            hasConstraint ? "已包含约束条件" : "建议添加约束条件",
            15
        ));
        if (!hasConstraint) {
            suggestions.add("添加约束条件，如'不要包含敏感信息'或'控制在200字以内'");
        }
        
        // 6. 检查是否包含示例
        String[] examplePatterns = {"例如", "比如", "示例", "比如", "如下", "这样", "案例"};
        boolean hasExample = containsAny(prompt, examplePatterns);
        items.add(new QualityCheckResponse.CheckItem(
            "示例说明",
            hasExample,
            hasExample ? "已包含示例" : "建议添加示例以提高准确性",
            10
        ));
        if (!hasExample) {
            suggestions.add("添加示例可以帮助AI更好地理解你的需求");
        }
        
        // 7. 检查是否使用变量占位符
        boolean hasVariables = Pattern.compile("\\{[^{}]+\\}").matcher(prompt).find();
        items.add(new QualityCheckResponse.CheckItem(
            "变量占位符",
            hasVariables,
            hasVariables ? "使用了变量占位符" : "可使用变量提高复用性",
            5
        ));
        
        // 8. 检查是否有问号（是否清晰提问）
        boolean hasQuestion = prompt.contains("？") || prompt.contains("?");
        items.add(new QualityCheckResponse.CheckItem(
            "清晰提问",
            hasQuestion,
            hasQuestion ? "以问题形式呈现需求" : "可以将需求转化为问题形式",
            10
        ));
        
        // 计算总分
        int totalWeight = items.stream().mapToInt(QualityCheckResponse.CheckItem::getWeight).sum();
        int earnedWeight = items.stream()
            .filter(QualityCheckResponse.CheckItem::isPassed)
            .mapToInt(QualityCheckResponse.CheckItem::getWeight)
            .sum();
        int score = totalWeight > 0 ? (int) ((earnedWeight * 100.0) / totalWeight) : 0;
        
        // 确定等级
        String level;
        if (score >= 90) level = "优秀";
        else if (score >= 70) level = "良好";
        else if (score >= 50) level = "一般";
        else level = "待改进";
        
        // 生成总结
        String summary = String.format("您的提示词得分为%d分（%s）。", score, level);
        if (score >= 70) {
            summary += "整体质量不错，按建议微调效果会更好。";
        } else {
            summary += "建议根据上述建议进行优化，以提高AI输出质量。";
        }
        
        response.setScore(score);
        response.setLevel(level);
        response.setItems(items);
        response.setSuggestions(suggestions);
        response.setSummary(summary);
        
        return ApiResponse.success(response);
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
}
