package com.promptflow.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.promptflow.dto.ApiResponse;
import com.promptflow.dto.CompareRequest;
import com.promptflow.dto.CompareResponse;
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
@RequestMapping("/api")
@CrossOrigin(origins = {"*"}, 
           allowCredentials = "false", 
           allowedHeaders = "*", 
           methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.OPTIONS})
public class CompareController {
    
    private static final Logger logger = LoggerFactory.getLogger(CompareController.class);
    
    @Value("${api.key}")
    private String apiKey;
    
    @Value("${api.base-url}")
    private String baseUrl;
    
    @Value("${api.model}")
    private String model;
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    /**
     * 对比两个提示词的API
     */
    @PostMapping("/prompt/compare")
    public ResponseEntity<ApiResponse<CompareResponse>> comparePrompts(@RequestBody CompareRequest request) {
        try {
            logger.info("Received prompt comparison request");
            
            if (request.getPromptA() == null || request.getPromptA().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, "提示词A不能为空"));
            }
            
            if (request.getPromptB() == null || request.getPromptB().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, "提示词B不能为空"));
            }
            
            // 使用规则评估 + AI分析
            CompareResponse response = comparePromptsWithAI(request.getPromptA(), request.getPromptB());
            
            return ResponseEntity.ok(ApiResponse.success("对比完成", response));
            
        } catch (Exception e) {
            logger.error("Error processing prompt comparison request", e);
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error(500, "对比提示词时发生错误: " + e.getMessage()));
        }
    }
    
    /**
     * 使用AI进行提示词对比分析
     */
    private CompareResponse comparePromptsWithAI(String promptA, String promptB) throws Exception {
        // 首先用规则评估两个提示词
        int scoreA = evaluatePromptQuality(promptA);
        int scoreB = evaluatePromptQuality(promptB);
        
        int structureA = analyzeStructure(promptA);
        int structureB = analyzeStructure(promptB);
        int roleA = analyzeRole(promptA);
        int roleB = analyzeRole(promptB);
        int taskA = analyzeTask(promptA);
        int taskB = analyzeTask(promptB);
        int constraintA = analyzeConstraint(promptA);
        int constraintB = analyzeConstraint(promptB);
        int outputA = analyzeOutput(promptA);
        int outputB = analyzeOutput(promptB);
        
        // 调用AI进行深度分析
        String aiAnalysis = callAICompare(promptA, promptB, scoreA, scoreB);
        
        // 构建响应
        CompareResponse response = new CompareResponse();
        response.setScoreA(scoreA);
        response.setScoreB(scoreB);
        
        // 确定获胜者
        if (scoreA > scoreB) {
            response.setWinner("A");
        } else if (scoreB > scoreA) {
            response.setWinner("B");
        } else {
            response.setWinner("tie");
        }
        
        // 设置分数差异
        response.setStructureDiff(Math.abs(structureA - structureB));
        response.setRoleDiff(Math.abs(roleA - roleB));
        response.setTaskDiff(Math.abs(taskA - taskB));
        response.setConstraintDiff(Math.abs(constraintA - constraintB));
        response.setOutputDiff(Math.abs(outputA - outputB));
        
        // 构建对比项
        List<CompareResponse.ComparisonItem> comparisons = new ArrayList<>();
        
        comparisons.add(buildComparisonItem("结构完整度", structureA, structureB, 
            structureA > structureB ? "A的结构更完整，段落层次更清晰" : 
            structureB > structureA ? "B的结构更完整，段落层次更清晰" : "两者结构完整度相近"));
        
        comparisons.add(buildComparisonItem("角色定义", roleA, roleB,
            roleA > roleB ? "A明确定义了AI角色身份" : 
            roleB > roleA ? "B明确定义了AI角色身份" : "两者角色定义相近"));
        
        comparisons.add(buildComparisonItem("任务清晰度", taskA, taskB,
            taskA > taskB ? "A的任务描述更明确具体" : 
            taskB > taskA ? "B的任务描述更明确具体" : "两者任务清晰度相近"));
        
        comparisons.add(buildComparisonItem("约束条件", constraintA, constraintB,
            constraintA > constraintB ? "A包含了更明确的约束条件" : 
            constraintB > constraintA ? "B包含了更明确的约束条件" : "两者约束条件相近"));
        
        comparisons.add(buildComparisonItem("输出格式", outputA, outputB,
            outputA > outputB ? "A的输出格式要求更清晰" : 
            outputB > outputA ? "B的输出格式要求更清晰" : "两者输出格式要求相近"));
        
        response.setComparisons(comparisons);
        
        // 添加AI分析结果
        response.setSummary(aiAnalysis);
        
        // 生成改进建议
        List<String> suggestions = generateSuggestions(promptA, promptB, scoreA, scoreB, 
            structureA, structureB, roleA, roleB, taskA, taskB, constraintA, constraintB, outputA, outputB);
        response.setSuggestions(suggestions);
        
        return response;
    }
    
    private CompareResponse.ComparisonItem buildComparisonItem(String aspect, int scoreA, int scoreB, String analysis) {
        CompareResponse.ComparisonItem item = new CompareResponse.ComparisonItem();
        item.setAspect(aspect);
        item.setScoreA(scoreA);
        item.setScoreB(scoreB);
        item.setAnalysis(analysis);
        
        if (scoreA > scoreB) {
            item.setWinner("A");
        } else if (scoreB > scoreA) {
            item.setWinner("B");
        } else {
            item.setWinner("tie");
        }
        
        return item;
    }
    
    /**
     * 调用AI进行深度对比分析
     */
    private String callAICompare(String promptA, String promptB, int scoreA, int scoreB) throws Exception {
        String systemPrompt = "你是一位专业的AI提示词工程师。请对比分析以下两个提示词的优劣，并给出一个总结。\n\n" +
            "提示词A（评分：" + scoreA + "分）：\n" + promptA + "\n\n" +
            "提示词B（评分：" + scoreB + "分）：\n" + promptB + "\n\n" +
            "请从以下几个方面进行对比分析：\n" +
            "1. 哪个提示词更能准确传达任务意图？\n" +
            "2. 哪个提示词的约束条件更明确？\n" +
            "3. 哪个提示词更适合实际应用？\n" +
            "4. 综合评估哪个更好？\n\n" +
            "请直接输出总结分析，不需要JSON格式。";
        
        Map<String, Object> requestBody = Map.of(
            "model", model,
            "messages", List.of(
                Map.of("role", "user", "content", systemPrompt)
            ),
            "temperature", 0.7,
            "max_tokens", 2000
        );
        
        String jsonBody = objectMapper.writeValueAsString(requestBody);
        
        URL url = new URL(normalizeUrl(baseUrl) + "/chat/completions");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Authorization", "Bearer " + apiKey);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);
        conn.setConnectTimeout(30000);
        conn.setReadTimeout(120000);
        
        try (OutputStream os = conn.getOutputStream()) {
            os.write(jsonBody.getBytes(StandardCharsets.UTF_8));
        }
        
        int responseCode = conn.getResponseCode();
        if (responseCode != 200) {
            BufferedReader errorReader = new BufferedReader(new InputStreamReader(conn.getErrorStream(), StandardCharsets.UTF_8));
            StringBuilder errorMsg = new StringBuilder();
            String line;
            while ((line = errorReader.readLine()) != null) {
                errorMsg.append(line);
            }
            throw new RuntimeException("API调用失败: " + responseCode + " - " + errorMsg);
        }
        
        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        
        // 解析响应
        Map<String, Object> responseMap = objectMapper.readValue(response.toString(), Map.class);
        List<Map<String, Object>> choices = (List<Map<String, Object>>) responseMap.get("choices");
        if (choices != null && !choices.isEmpty()) {
            Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
            return (String) message.get("content");
        }
        
        return "对比分析完成。";
    }
    
    private String normalizeUrl(String url) {
        if (url == null) return "";
        return url.replaceAll("/+$", "");
    }
    
    /**
     * 生成改进建议
     */
    private List<String> generateSuggestions(String promptA, String promptB, int scoreA, int scoreB,
            int structureA, int structureB, int roleA, int roleB, 
            int taskA, int taskB, int constraintA, int constraintB, int outputA, int outputB) {
        List<String> suggestions = new ArrayList<>();
        
        // 如果A比B弱，给出A的改进建议
        if (scoreA < scoreB) {
            if (structureA < structureB) {
                suggestions.add("提示词A可以改进结构：添加清晰的段落分隔，使用编号列表组织内容");
            }
            if (roleA < roleB) {
                suggestions.add("提示词A可以添加角色定义：使用'你是...'或'你是一位...'来明确AI身份");
            }
            if (taskA < taskB) {
                suggestions.add("提示词A可以更明确任务：使用'请帮我...'、'请生成...'等清晰指令");
            }
            if (constraintA < constraintB) {
                suggestions.add("提示词A可以添加约束条件：使用'不能'、'不要'、'限制在'等词汇");
            }
            if (outputA < outputB) {
                suggestions.add("提示词A可以指定输出格式：如JSON、Markdown、列表等格式要求");
            }
        }
        // 如果B比A弱，给出B的改进建议
        else if (scoreB < scoreA) {
            if (structureB < structureA) {
                suggestions.add("提示词B可以改进结构：添加清晰的段落分隔，使用编号列表组织内容");
            }
            if (roleB < roleA) {
                suggestions.add("提示词B可以添加角色定义：使用'你是...'或'你是一位...'来明确AI身份");
            }
            if (taskB < taskA) {
                suggestions.add("提示词B可以更明确任务：使用'请帮我...'、'请生成...'等清晰指令");
            }
            if (constraintB < constraintA) {
                suggestions.add("提示词B可以添加约束条件：使用'不能'、'不要'、'限制在'等词汇");
            }
            if (outputB < outputA) {
                suggestions.add("提示词B可以指定输出格式：如JSON、Markdown、列表等格式要求");
            }
        } else {
            suggestions.add("两个提示词质量相近，都很优秀！可以尝试结合两者的优点");
        }
        
        return suggestions;
    }
    
    /**
     * 评估提示词质量（基于规则）
     */
    private int evaluatePromptQuality(String prompt) {
        if (prompt == null || prompt.trim().isEmpty()) {
            return 0;
        }
        
        int score = 50;
        
        if (prompt.length() > 50) score += 5;
        if (prompt.length() > 100) score += 5;
        if (prompt.length() > 300) score += 5;
        
        if (prompt.contains("角色") || prompt.contains("你是") || prompt.contains("你是一个") || 
            prompt.contains("role") || prompt.contains("You are")) {
            score += 10;
        }
        
        if (prompt.contains("请") || prompt.contains("帮我") || prompt.contains("需要") ||
            prompt.contains("Please") || prompt.contains("help") || prompt.contains("need")) {
            score += 10;
        }
        
        if (prompt.contains("不能") || prompt.contains("不要") || prompt.contains("必须") ||
            prompt.contains("不能") || prompt.contains("约束") || prompt.contains("限制")) {
            score += 10;
        }
        
        if (prompt.contains("格式") || prompt.contains("输出") || prompt.contains("返回") ||
            prompt.contains("format") || prompt.contains("output") || prompt.contains("return")) {
            score += 10;
        }
        
        return Math.min(score, 100);
    }
    
    private int analyzeStructure(String prompt) {
        int score = 60;
        if (prompt.length() > 50) score += 5;
        if (prompt.length() > 100) score += 10;
        if (prompt.length() > 300) score += 10;
        if (prompt.contains("\n\n") || prompt.contains("。\n")) score += 5;
        if (prompt.matches(".*\\d+[.、].*")) score += 5;
        return Math.min(score, 100);
    }
    
    private int analyzeRole(String prompt) {
        int score = 40;
        if (prompt.contains("你是") || prompt.contains("你是一个") || prompt.contains("你是一位")) {
            score += 20;
        }
        if (prompt.contains("角色") || prompt.contains("身份") || prompt.contains("扮演")) {
            score += 15;
        }
        if (prompt.contains("专家") || prompt.contains("助手") || prompt.contains("顾问")) {
            score += 10;
        }
        if (prompt.toLowerCase().contains("you are") || prompt.toLowerCase().contains("you act as")) {
            score += 20;
        }
        return Math.min(score, 100);
    }
    
    private int analyzeTask(String prompt) {
        int score = 40;
        if (prompt.contains("请") || prompt.contains("帮我") || prompt.contains("需要")) {
            score += 15;
        }
        if (prompt.contains("任务") || prompt.contains("目标") || prompt.contains("目的")) {
            score += 10;
        }
        if (prompt.contains("生成") || prompt.contains("创建") || prompt.contains("编写")) {
            score += 10;
        }
        if (prompt.toLowerCase().contains("please") || prompt.toLowerCase().contains("help me")) {
            score += 15;
        }
        return Math.min(score, 100);
    }
    
    private int analyzeConstraint(String prompt) {
        int score = 40;
        if (prompt.contains("不能") || prompt.contains("不要") || prompt.contains("避免")) {
            score += 15;
        }
        if (prompt.contains("必须") || prompt.contains("需要") || prompt.contains("应该")) {
            score += 10;
        }
        if (prompt.contains("限制") || prompt.contains("约束") || prompt.contains("范围")) {
            score += 10;
        }
        if (prompt.toLowerCase().contains("don't") || prompt.toLowerCase().contains("must not")) {
            score += 15;
        }
        return Math.min(score, 100);
    }
    
    private int analyzeOutput(String prompt) {
        int score = 40;
        if (prompt.contains("格式") || prompt.contains("输出") || prompt.contains("返回")) {
            score += 15;
        }
        if (prompt.contains("JSON") || prompt.contains("json") || prompt.contains("Markdown")) {
            score += 15;
        }
        if (prompt.contains("列表") || prompt.contains("表格") || prompt.contains("按照")) {
            score += 10;
        }
        if (prompt.toLowerCase().contains("format") || prompt.toLowerCase().contains("output")) {
            score += 15;
        }
        return Math.min(score, 100);
    }
}
