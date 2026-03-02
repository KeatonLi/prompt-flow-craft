package com.promptflow.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.promptflow.dto.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

@RestController
@RequestMapping("/api/prompt")
@CrossOrigin(origins = {"*"}, 
           allowCredentials = "false", 
           allowedHeaders = "*", 
           methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.OPTIONS})
public class PromptDebuggerController {
    
    private static final Logger logger = LoggerFactory.getLogger(PromptDebuggerController.class);
    
    @Value("${api.key}")
    private String apiKey;
    
    @Value("${api.base-url}")
    private String baseUrl;
    
    @Value("${api.model}")
    private String model;
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    /**
     * 调试提示词API
     */
    @PostMapping("/debug")
    public ResponseEntity<ApiResponse<Map<String, Object>>> debugPrompt(@RequestBody Map<String, String> request) {
        try {
            String prompt = request.get("prompt");
            
            if (prompt == null || prompt.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, "提示词内容不能为空"));
            }
            
            // 本地分析（基于规则）
            Map<String, Object> debugResult = analyzePromptLocally(prompt);
            
            return ResponseEntity.ok(ApiResponse.success("调试完成", debugResult));
            
        } catch (Exception e) {
            logger.error("Error processing prompt debug request", e);
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error(500, "调试提示词时发生错误: " + e.getMessage()));
        }
    }
    
    /**
     * 本地分析提示词（基于规则）
     */
    private Map<String, Object> analyzePromptLocally(String prompt) {
        Map<String, Object> result = new HashMap<>();
        List<Map<String, String>> issues = new ArrayList<>();
        
        // 1. 检测角色定义
        if (!prompt.contains("你是") && !prompt.contains("你是一个") && !prompt.contains("你是一位") &&
            !prompt.contains("角色") && !prompt.toLowerCase().contains("you are") && 
            !prompt.toLowerCase().contains("role")) {
            Map<String, String> issue = new HashMap<>();
            issue.put("type", "缺少角色定义");
            issue.put("severity", "warning");
            issue.put("description", "提示词中缺少明确的角色定义，AI可能无法准确理解你的需求");
            issue.put("suggestion", "建议添加类似'你是...'或'你是一位...'的角色描述");
            issues.add(issue);
        }
        
        // 2. 检测提示词长度
        if (prompt.length() < 30) {
            Map<String, String> issue = new HashMap<>();
            issue.put("type", "提示词过于简短");
            issue.put("severity", "error");
            issue.put("description", "提示词内容过短，可能无法提供足够的上下文信息");
            issue.put("suggestion", "建议详细描述任务要求、背景信息、输出格式等");
            issues.add(issue);
        } else if (prompt.length() > 2000) {
            Map<String, String> issue = new HashMap<>();
            issue.put("type", "提示词过长");
            issue.put("severity", "info");
            issue.put("description", "提示词内容过长，可能导致AI忽略重要信息");
            issue.put("suggestion", "建议精简内容，突出核心需求");
            issues.add(issue);
        }
        
        // 3. 检测输出格式
        if (!prompt.contains("格式") && !prompt.contains("输出") && !prompt.contains("返回") &&
            !prompt.contains("请以") && !prompt.toLowerCase().contains("format") && 
            !prompt.toLowerCase().contains("output")) {
            Map<String, String> issue = new HashMap<>();
            issue.put("type", "缺少输出格式说明");
            issue.put("severity", "warning");
            issue.put("description", "没有明确指定输出格式，AI的输出可能不符合预期");
            issue.put("suggestion", "建议添加'请以...格式输出'或'返回JSON格式'等说明");
            issues.add(issue);
        }
        
        // 4. 检测示例
        if (!prompt.contains("例如") && !prompt.contains("示例") && !prompt.contains("比如") &&
            !prompt.toLowerCase().contains("example") && !prompt.toLowerCase().contains("such as")) {
            Map<String, String> issue = new HashMap<>();
            issue.put("type", "缺少示例");
            issue.put("severity", "info");
            issue.put("description", "没有提供示例，可能导致AI理解有偏差");
            issue.put("suggestion", "建议添加'例如：...'的示例来明确期望");
            issues.add(issue);
        }
        
        // 5. 检测约束条件
        if (!prompt.contains("不能") && !prompt.contains("不要") && !prompt.contains("避免") &&
            !prompt.contains("必须") && !prompt.contains("限制") &&
            !prompt.toLowerCase().contains("don't") && !prompt.toLowerCase().contains("must not") &&
            !prompt.toLowerCase().contains("avoid") && !prompt.toLowerCase().contains("limit")) {
            Map<String, String> issue = new HashMap<>();
            issue.put("type", "缺少约束条件");
            issue.put("severity", "info");
            issue.put("description", "没有明确指定约束条件，AI可能产生不符合要求的输出");
            issue.put("suggestion", "建议添加'不要...'、'必须...'等约束条件");
            issues.add(issue);
        }
        
        // 6. 检测任务明确性
        if (!prompt.contains("请") && !prompt.contains("帮我") && !prompt.contains("需要") &&
            !prompt.contains("生成") && !prompt.contains("创建") &&
            !prompt.toLowerCase().contains("please") && !prompt.toLowerCase().contains("help") &&
            !prompt.toLowerCase().contains("generate") && !prompt.toLowerCase().contains("create")) {
            Map<String, String> issue = new HashMap<>();
            issue.put("type", "任务指令不明确");
            issue.put("severity", "warning");
            issue.put("description", "缺少明确的任务指令，AI可能不清楚要做什么");
            issue.put("suggestion", "建议使用'请帮我...'、'请生成...'等明确指令");
            issues.add(issue);
        }
        
        // 生成问题数量
        result.put("problemCount", issues.size());
        
        // 生成摘要
        String summary;
        if (issues.isEmpty()) {
            summary = "你的提示词结构完整，表达清晰！继续保持！";
        } else if (issues.size() == 1) {
            summary = "发现1个问题，建议修复后再使用";
        } else {
            summary = "共发现" + issues.size() + "个问题，建议逐一修复以提升提示词质量";
        }
        result.put("summary", summary);
        result.put("issues", issues);
        
        // 生成优化后的提示词
        String fixedPrompt = generateFixedPrompt(prompt, issues);
        result.put("fixedPrompt", fixedPrompt);
        
        return result;
    }
    
    /**
     * 生成优化后的提示词
     */
    private String generateFixedPrompt(String originalPrompt, List<Map<String, String>> issues) {
        StringBuilder fixed = new StringBuilder(originalPrompt.trim());
        
        // 添加角色定义
        boolean hasRole = originalPrompt.contains("你是") || originalPrompt.contains("角色") || 
                         originalPrompt.toLowerCase().contains("you are");
        if (!hasRole) {
            fixed.insert(0, "你是一位专业的AI助手。\n\n");
        }
        
        // 检查是否需要添加格式说明
        boolean hasFormat = originalPrompt.contains("格式") || originalPrompt.contains("输出") || 
                          originalPrompt.toLowerCase().contains("format");
        if (!hasFormat) {
            fixed.append("\n\n请以清晰的结构化格式返回结果。");
        }
        
        // 检查是否需要添加约束
        boolean hasConstraint = originalPrompt.contains("不能") || originalPrompt.contains("约束") ||
                              originalPrompt.toLowerCase().contains("don't");
        if (!hasConstraint) {
            fixed.append("\n请确保输出内容准确、简洁。");
        }
        
        return fixed.toString();
    }
    
    private String normalizeUrl(String url) {
        if (url == null) return "";
        return url.replaceAll("/+$", "");
    }
}
