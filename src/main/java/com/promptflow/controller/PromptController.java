package com.promptflow.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.promptflow.dto.AnalyzeRequest;
import com.promptflow.dto.AnalyzeResponse;
import com.promptflow.dto.ApiResponse;
import com.promptflow.dto.OptimizeRequest;
import com.promptflow.dto.OptimizeResponse;
import com.promptflow.dto.PromptRequest;
import com.promptflow.service.PromptCacheService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = {"*"}, 
           allowCredentials = "false", 
           allowedHeaders = "*", 
           methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.OPTIONS})
public class PromptController {
    
    private static final Logger logger = LoggerFactory.getLogger(PromptController.class);
    
    @Autowired
    private PromptCacheService promptCacheService;
    
    @Value("${api.key}")
    private String apiKey;
    
    @Value("${api.base-url}")
    private String baseUrl;
    
    @Value("${api.model}")
    private String model;
    
    @Value("${api.temperature}")
    private double temperature;
    
    @Value("${api.max-tokens}")
    private int maxTokens;
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    /**
     * 同步生成提示词（非流式）
     */
    @PostMapping("/generate-prompt")
    public ResponseEntity<ApiResponse<String>> generatePrompt(@RequestBody PromptRequest request) {
        try {
            logger.info("Received prompt generation request: {}", request);
            
            if (request.getTaskDescription() == null || request.getTaskDescription().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, "任务描述不能为空"));
            }
            
            // 检查缓存
            String cached = promptCacheService.getCachedPrompt(request);
            if (cached != null) {
                logger.info("缓存命中，直接返回");
                return ResponseEntity.ok(ApiResponse.success("提示词生成成功（来自缓存）", cached));
            }
            
            String generatedPrompt = callLLMApi(request, false);
            
            return ResponseEntity.ok(ApiResponse.success("提示词生成成功", generatedPrompt));
            
        } catch (Exception e) {
            logger.error("Error processing prompt generation request", e);
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error(500, "生成提示词时发生错误: " + e.getMessage()));
        }
    }
    
    /**
     * 流式生成提示词（Server-Sent Events）
     */
    @PostMapping(value = "/generate-prompt/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter generatePromptStream(@RequestBody PromptRequest request) {
        logger.info("Received stream prompt generation request: {}", request);
        
        SseEmitter emitter = new SseEmitter(300000L); // 5分钟超时
        ExecutorService executor = Executors.newSingleThreadExecutor();
        
        executor.execute(() -> {
            try {
                // 验证请求
                if (request.getTaskDescription() == null || request.getTaskDescription().trim().isEmpty()) {
                    sendEvent(emitter, "error", "{\"error\": \"任务描述不能为空\"}");
                    emitter.complete();
                    return;
                }
                
                // 检查缓存
                String cached = promptCacheService.getCachedPrompt(request);
                if (cached != null) {
                    logger.info("缓存命中，流式返回缓存内容");
                    // 逐字发送缓存内容，模拟流式效果
                    for (int i = 0; i < cached.length(); i += 5) {
                        String chunk = cached.substring(i, Math.min(i + 5, cached.length()));
                        String json = objectMapper.writeValueAsString(Map.of("content", chunk));
                        sendEvent(emitter, "message", json);
                        Thread.sleep(10); // 小延迟模拟打字机效果
                    }
                    sendEvent(emitter, "done", "{\"done\": true}");
                    emitter.complete();
                    return;
                }
                
                // 调用流式API
                callLLMStreamApi(request, emitter);
                
            } catch (Exception e) {
                logger.error("流式生成失败", e);
                try {
                    sendEvent(emitter, "error", "{\"error\": \"" + e.getMessage() + "\"}");
                    emitter.complete();
                } catch (Exception ex) {
                    emitter.completeWithError(ex);
                }
            }
        });
        
        emitter.onCompletion(executor::shutdown);
        emitter.onTimeout(executor::shutdown);
        emitter.onError((e) -> executor.shutdown());
        
        return emitter;
    }
    
    /**
     * 调用LLM API（非流式）
     */
    private String callLLMApi(PromptRequest request, boolean stream) throws Exception {
        String systemPrompt = buildSystemPrompt(request);
        
        Map<String, Object> requestBody = Map.of(
            "model", model,
            "messages", List.of(
                Map.of("role", "user", "content", systemPrompt)
            ),
            "temperature", temperature,
            "max_tokens", maxTokens,
            "stream", stream
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
            String content = (String) message.get("content");
            
            // 保存到缓存
            promptCacheService.saveToCache(request, content);
            
            return content;
        }
        
        throw new RuntimeException("API响应格式错误");
    }
    
    /**
     * 调用LLM流式API
     */
    private void callLLMStreamApi(PromptRequest request, SseEmitter emitter) throws Exception {
        String systemPrompt = buildSystemPrompt(request);
        
        Map<String, Object> requestBody = Map.of(
            "model", model,
            "messages", List.of(
                Map.of("role", "user", "content", systemPrompt)
            ),
            "temperature", temperature,
            "max_tokens", maxTokens,
            "stream", true
        );
        
        String jsonBody = objectMapper.writeValueAsString(requestBody);
        
        URL url = new URL(normalizeUrl(baseUrl) + "/chat/completions");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Authorization", "Bearer " + apiKey);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);
        conn.setConnectTimeout(30000);
        conn.setReadTimeout(300000); // 5分钟读取超时
        
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
        
        // 读取流式响应
        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
        String line;
        StringBuilder fullContent = new StringBuilder();
        
        while ((line = reader.readLine()) != null) {
            logger.debug("收到流式数据行: {}", line);
            
            if (line.startsWith("data: ")) {
                String data = line.substring(6);
                
                if ("[DONE]".equals(data.trim())) {
                    logger.info("收到 [DONE] 标记，流式响应结束");
                    break;
                }
                
                try {
                    Map<String, Object> chunk = objectMapper.readValue(data, Map.class);
                    List<Map<String, Object>> choices = (List<Map<String, Object>>) chunk.get("choices");
                    
                    if (choices != null && !choices.isEmpty()) {
                        Map<String, Object> delta = (Map<String, Object>) choices.get(0).get("delta");
                        if (delta != null && delta.containsKey("content")) {
                            String content = (String) delta.get("content");
                            if (content != null && !content.isEmpty()) {
                                fullContent.append(content);
                                String json = objectMapper.writeValueAsString(Map.of("content", content));
                                sendEvent(emitter, "message", json);
                            }
                        }
                    }
                } catch (Exception e) {
                    logger.debug("解析流式数据失败: {}", data, e);
                }
            }
        }
        
        reader.close();
        
        // 保存完整内容到缓存
        String completeContent = fullContent.toString();
        if (!completeContent.isEmpty()) {
            promptCacheService.saveToCache(request, completeContent);
            logger.info("流式生成完成，已保存到缓存，长度: {}", completeContent.length());
        }
        
        // 发送完成标记
        sendEvent(emitter, "done", "{\"done\": true}");
        emitter.complete();
    }
    
    private void sendEvent(SseEmitter emitter, String eventName, String data) throws Exception {
        emitter.send(SseEmitter.event()
            .name(eventName)
            .data(data));
    }
    
    private String normalizeUrl(String url) {
        if (url == null) return "";
        // 移除末尾的斜杠，避免双斜杠
        return url.replaceAll("/+$", "");
    }
    
    private String buildSystemPrompt(PromptRequest request) {
        StringBuilder promptBuilder = new StringBuilder();
        
        promptBuilder.append("# 系统架构思维提示词工程师\n\n");
        promptBuilder.append("你是一位精通系统架构思维的资深AI提示词工程师。\n\n");
        
        promptBuilder.append("## 用户需求\n");
        promptBuilder.append("任务描述：").append(request.getTaskDescription()).append("\n");
        
        if (request.getTargetAudience() != null && !request.getTargetAudience().isEmpty()) {
            promptBuilder.append("目标受众：").append(request.getTargetAudience()).append("\n");
        }
        if (request.getOutputFormat() != null && !request.getOutputFormat().isEmpty()) {
            promptBuilder.append("输出格式：").append(request.getOutputFormat()).append("\n");
        }
        if (request.getTone() != null && !request.getTone().isEmpty()) {
            promptBuilder.append("语调风格：").append(request.getTone()).append("\n");
        }
        if (request.getLength() != null && !request.getLength().isEmpty()) {
            promptBuilder.append("内容长度：").append(request.getLength()).append("\n");
        }
        if (request.getConstraints() != null && !request.getConstraints().isEmpty()) {
            promptBuilder.append("约束条件：").append(request.getConstraints()).append("\n");
        }
        if (request.getExamples() != null && !request.getExamples().isEmpty()) {
            promptBuilder.append("参考示例：").append(request.getExamples()).append("\n");
        }
        
        promptBuilder.append("\n## 输出要求\n");
        promptBuilder.append("请按照系统架构思维的四层框架设计提示词系统：\n");
        promptBuilder.append("1. 核心定义层：角色建模、目标定义\n");
        promptBuilder.append("2. 交互接口层：输入规范、输出规格\n");
        promptBuilder.append("3. 内部处理层：能力拆解、流程设计\n");
        promptBuilder.append("4. 全局约束层：行为边界、安全规则\n\n");
        promptBuilder.append("请直接输出完整的提示词系统。");
        
        return promptBuilder.toString();
    }
    
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Prompt Flow Craft API is running!");
    }
    
    /**
     * 优化提示词API
     */
    @PostMapping("/prompt/optimize")
    public ResponseEntity<ApiResponse<OptimizeResponse>> optimizePrompt(@RequestBody OptimizeRequest request) {
        try {
            logger.info("Received prompt optimization request");
            
            if (request.getPrompt() == null || request.getPrompt().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, "提示词内容不能为空"));
            }
            
            String optimizationType = request.getOptimizationType();
            if (optimizationType == null || optimizationType.isEmpty()) {
                optimizationType = "general";
            }
            
            String targetModel = request.getTargetModel();
            if (targetModel == null || targetModel.isEmpty()) {
                targetModel = "general";
            }
            
            // 评估原始提示词质量
            int scoreBefore = evaluatePromptQuality(request.getPrompt());
            
            // 调用LLM进行优化
            OptimizeResponse response = callLLMOptimize(request.getPrompt(), optimizationType, targetModel);
            
            // 评估优化后的质量
            int scoreAfter = evaluatePromptQuality(response.getOptimizedPrompt());
            response.setScoreBefore(scoreBefore);
            response.setScoreAfter(scoreAfter);
            
            return ResponseEntity.ok(ApiResponse.success("优化完成", response));
            
        } catch (Exception e) {
            logger.error("Error processing prompt optimization request", e);
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error(500, "优化提示词时发生错误: " + e.getMessage()));
        }
    }
    
    /**
     * 构建优化提示词的系统prompt
     */
    private String buildOptimizePrompt(String userPrompt, String optimizationType, String targetModel) {
        StringBuilder promptBuilder = new StringBuilder();
        
        promptBuilder.append("你是一位专业的AI提示词优化专家。你的任务是根据用户的需求优化提示词。\n\n");
        
        promptBuilder.append("## 原始提示词\n");
        promptBuilder.append(userPrompt).append("\n\n");
        
        promptBuilder.append("## 优化类型\n");
        switch (optimizationType) {
            case "clarity":
                promptBuilder.append("重点：提升清晰度，让AI更准确地理解任务要求\n");
                break;
            case "specificity":
                promptBuilder.append("重点：增加具体性和细节，减少模糊性\n");
                break;
            case "examples":
                promptBuilder.append("重点：添加示例，让AI更好地理解期望的输出格式\n");
                break;
            case "structure":
                promptBuilder.append("重点：优化结构，使提示词更有逻辑性\n");
                break;
            default:
                promptBuilder.append("全面优化：提升提示词的整体质量\n");
        }
        
        promptBuilder.append("\n## 目标模型\n");
        switch (targetModel) {
            case "gpt":
                promptBuilder.append("针对GPT系列模型优化\n");
                break;
            case "claude":
                promptBuilder.append("针对Claude模型优化\n");
                break;
            default:
                promptBuilder.append("通用优化，适用于各种LLM\n");
        }
        
        promptBuilder.append("\n## 输出要求\n");
        promptBuilder.append("请按以下JSON格式返回优化结果：\n");
        promptBuilder.append("{\n");
        promptBuilder.append("  \"optimizedPrompt\": \"优化后的完整提示词\",\n");
        promptBuilder.append("  \"explanation\": \"优化说明，解释你做了哪些改进及其原因\",\n");
        promptBuilder.append("  \"improvements\": [\n");
        promptBuilder.append("    {\n");
        promptBuilder.append("      \"type\": \"改进类型\",\n");
        promptBuilder.append("      \"description\": \"发现的问题\",\n");
        promptBuilder.append("      \"suggestion\": \"改进建议\"\n");
        promptBuilder.append("    }\n");
        promptBuilder.append("  ]\n");
        promptBuilder.append("}\n\n");
        promptBuilder.append("请确保返回的是有效的JSON格式。");
        
        return promptBuilder.toString();
    }
    
    /**
     * 调用LLM进行提示词优化
     */
    private OptimizeResponse callLLMOptimize(String userPrompt, String optimizationType, String targetModel) throws Exception {
        String systemPrompt = buildOptimizePrompt(userPrompt, optimizationType, targetModel);
        
        Map<String, Object> requestBody = Map.of(
            "model", model,
            "messages", List.of(
                Map.of("role", "user", "content", systemPrompt)
            ),
            "temperature", 0.7,
            "max_tokens", 4000
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
            String content = (String) message.get("content");
            
            // 解析JSON响应
            return parseOptimizeResponse(content);
        }
        
        throw new RuntimeException("API响应格式错误");
    }
    
    /**
     * 解析LLM返回的优化结果
     */
    private OptimizeResponse parseOptimizeResponse(String content) {
        try {
            // 尝试提取JSON部分
            Pattern jsonPattern = Pattern.compile("\\{[\\s\\S]*\\}", Pattern.MULTILINE);
            Matcher matcher = jsonPattern.matcher(content);
            
            String jsonStr = content;
            if (matcher.find()) {
                jsonStr = matcher.group();
            }
            
            Map<String, Object> result = objectMapper.readValue(jsonStr, Map.class);
            
            OptimizeResponse response = new OptimizeResponse();
            response.setOptimizedPrompt((String) result.get("optimizedPrompt"));
            response.setExplanation((String) result.get("explanation"));
            
            // 解析improvements
            List<Map<String, String>> improvementsList = (List<Map<String, String>>) result.get("improvements");
            if (improvementsList != null) {
                List<OptimizeResponse.Improvement> improvements = new ArrayList<>();
                for (Map<String, String> imp : improvementsList) {
                    improvements.add(new OptimizeResponse.Improvement(
                        imp.get("type"),
                        imp.get("description"),
                        imp.get("suggestion")
                    ));
                }
                response.setImprovements(improvements);
            }
            
            return response;
            
        } catch (Exception e) {
            logger.warn("解析优化结果JSON失败，使用原始文本", e);
            // 如果解析失败，返回原始内容
            OptimizeResponse response = new OptimizeResponse();
            response.setOptimizedPrompt(content);
            response.setExplanation("优化完成，但解析详细结果时出现问题");
            response.setImprovements(new ArrayList<>());
            return response;
        }
    }
    
    /**
     * 简单评估提示词质量（基于规则）
     */
    private int evaluatePromptQuality(String prompt) {
        if (prompt == null || prompt.trim().isEmpty()) {
            return 0;
        }
        
        int score = 50; // 基础分数
        
        // 检查长度
        if (prompt.length() > 50) score += 5;
        if (prompt.length() > 100) score += 5;
        if (prompt.length() > 300) score += 5;
        
        // 检查是否包含角色定义
        if (prompt.contains("角色") || prompt.contains("你是") || prompt.contains("你是一个") || 
            prompt.contains("role") || prompt.contains("You are")) {
            score += 10;
        }
        
        // 检查是否包含任务描述
        if (prompt.contains("请") || prompt.contains("帮我") || prompt.contains("需要") ||
            prompt.contains("Please") || prompt.contains("help") || prompt.contains("need")) {
            score += 10;
        }
        
        // 检查是否包含约束条件
        if (prompt.contains("不能") || prompt.contains("不要") || prompt.contains("必须") ||
            prompt.contains("不能") || prompt.contains("约束") || prompt.contains("限制")) {
            score += 10;
        }
        
        // 检查是否有输出格式要求
        if (prompt.contains("格式") || prompt.contains("输出") || prompt.contains("返回") ||
            prompt.contains("format") || prompt.contains("output") || prompt.contains("return")) {
            score += 10;
        }
        
        return Math.min(score, 100);
    }
    
    /**
     * 提示词质量分析API - 分析提示词的结构、角色、任务、约束、输出格式等
     */
    @PostMapping("/prompt/analyze")
    public ResponseEntity<ApiResponse<AnalyzeResponse>> analyzePrompt(@RequestBody AnalyzeRequest request) {
        try {
            logger.info("Received prompt analysis request");
            
            if (request.getPrompt() == null || request.getPrompt().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, "提示词内容不能为空"));
            }
            
            AnalyzeResponse response = analyzePromptQuality(request.getPrompt());
            return ResponseEntity.ok(ApiResponse.success("分析完成", response));
            
        } catch (Exception e) {
            logger.error("Error processing prompt analysis request", e);
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error(500, "分析提示词时发生错误: " + e.getMessage()));
        }
    }
    
    /**
     * 分析提示词质量的详细实现
     */
    private AnalyzeResponse analyzePromptQuality(String prompt) {
        AnalyzeResponse response = new AnalyzeResponse();
        List<AnalyzeResponse.Improvement> improvements = new ArrayList<>();
        List<String> strengths = new ArrayList<>();
        
        // 1. 结构完整度分析
        int structureScore = analyzeStructure(prompt);
        response.setStructureScore(structureScore);
        
        // 2. 角色定义分析
        int roleScore = analyzeRole(prompt);
        response.setRoleScore(roleScore);
        
        // 3. 任务清晰度分析
        int taskScore = analyzeTask(prompt);
        response.setTaskScore(taskScore);
        
        // 4. 约束条件分析
        int constraintScore = analyzeConstraint(prompt);
        response.setConstraintScore(constraintScore);
        
        // 5. 输出格式分析
        int outputScore = analyzeOutput(prompt);
        response.setOutputScore(outputScore);
        
        // 计算总分
        int totalScore = (structureScore + roleScore + taskScore + constraintScore + outputScore) / 5;
        response.setTotalScore(totalScore);
        
        // 确定评分等级
        if (totalScore >= 90) {
            response.setScoreLevel("优秀");
        } else if (totalScore >= 70) {
            response.setScoreLevel("良好");
        } else if (totalScore >= 50) {
            response.setScoreLevel("一般");
        } else {
            response.setScoreLevel("较差");
        }
        
        // 生成改进建议
        if (structureScore < 80) {
            improvements.add(new AnalyzeResponse.Improvement("结构完整度", "建议添加清晰的任务目标和背景信息，使提示词结构更加完整", 2));
        }
        if (roleScore < 80) {
            improvements.add(new AnalyzeResponse.Improvement("角色定义", "建议明确定义AI的角色身份，例如'你是一位专业的...'或'你是一个...'", 1));
        }
        if (taskScore < 80) {
            improvements.add(new AnalyzeResponse.Improvement("任务清晰度", "建议更清晰地描述任务要求，使用'请帮我...'、'请生成...'等明确指令", 1));
        }
        if (constraintScore < 80) {
            improvements.add(new AnalyzeResponse.Improvement("约束条件", "建议添加具体的约束条件，如'不要...'、'必须...'、'限制在...'等", 2));
        }
        if (outputScore < 80) {
            improvements.add(new AnalyzeResponse.Improvement("输出格式", "建议明确指定输出格式，如JSON、Markdown、列表等格式要求", 1));
        }
        
        // 识别优点
        if (roleScore >= 80) {
            strengths.add("✅ 角色定义清晰");
        }
        if (taskScore >= 80) {
            strengths.add("✅ 任务描述明确");
        }
        if (constraintScore >= 80) {
            strengths.add("✅ 包含约束条件");
        }
        if (outputScore >= 80) {
            strengths.add("✅ 输出格式规范");
        }
        if (prompt.length() > 200) {
            strengths.add("✅ 内容详实");
        }
        
        // 按优先级排序
        improvements.sort(Comparator.comparingInt(AnalyzeResponse.Improvement::getPriority));
        
        response.setImprovements(improvements);
        response.setStrengths(strengths);
        
        return response;
    }
    
    /**
     * 分析提示词结构完整度
     */
    private int analyzeStructure(String prompt) {
        int score = 60;
        
        // 检查基本长度
        if (prompt.length() > 50) score += 5;
        if (prompt.length() > 100) score += 10;
        if (prompt.length() > 300) score += 10;
        if (prompt.length() > 500) score += 5;
        
        // 检查是否有明确的段落分隔
        if (prompt.contains("\n\n") || prompt.contains("。\n")) score += 5;
        
        // 检查是否有编号列表
        if (prompt.matches(".*\\d+[.、].*")) score += 5;
        
        return Math.min(score, 100);
    }
    
    /**
     * 分析角色定义
     */
    private int analyzeRole(String prompt) {
        int score = 40;
        
        // 中文角色关键词
        if (prompt.contains("你是") || prompt.contains("你是一个") || prompt.contains("你是一位")) {
            score += 20;
        }
        if (prompt.contains("角色") || prompt.contains("身份") || prompt.contains("扮演")) {
            score += 15;
        }
        if (prompt.contains("专家") || prompt.contains("助手") || prompt.contains("顾问")) {
            score += 10;
        }
        if (prompt.contains("具备") || prompt.contains("拥有") || prompt.contains("擅长")) {
            score += 10;
        }
        
        // 英文角色关键词
        if (prompt.toLowerCase().contains("you are") || prompt.toLowerCase().contains("you act as")) {
            score += 20;
        }
        if (prompt.toLowerCase().contains("expert") || prompt.toLowerCase().contains("assistant")) {
            score += 10;
        }
        
        return Math.min(score, 100);
    }
    
    /**
     * 分析任务清晰度
     */
    private int analyzeTask(String prompt) {
        int score = 40;
        
        // 中文任务关键词
        if (prompt.contains("请") || prompt.contains("帮我") || prompt.contains("需要")) {
            score += 15;
        }
        if (prompt.contains("任务") || prompt.contains("目标") || prompt.contains("目的")) {
            score += 10;
        }
        if (prompt.contains("生成") || prompt.contains("创建") || prompt.contains("编写")) {
            score += 10;
        }
        if (prompt.contains("分析") || prompt.contains("处理") || prompt.contains("计算")) {
            score += 10;
        }
        
        // 英文任务关键词
        if (prompt.toLowerCase().contains("please") || prompt.toLowerCase().contains("help me") || prompt.toLowerCase().contains("i need")) {
            score += 15;
        }
        if (prompt.toLowerCase().contains("generate") || prompt.toLowerCase().contains("create") || prompt.toLowerCase().contains("write")) {
            score += 10;
        }
        
        return Math.min(score, 100);
    }
    
    /**
     * 分析约束条件
     */
    private int analyzeConstraint(String prompt) {
        int score = 40;
        
        // 中文约束关键词
        if (prompt.contains("不能") || prompt.contains("不要") || prompt.contains("避免")) {
            score += 15;
        }
        if (prompt.contains("必须") || prompt.contains("需要") || prompt.contains("应该")) {
            score += 10;
        }
        if (prompt.contains("限制") || prompt.contains("约束") || prompt.contains("范围")) {
            score += 10;
        }
        if (prompt.contains("只") || prompt.contains("仅") || prompt.contains("不超过")) {
            score += 10;
        }
        
        // 英文约束关键词
        if (prompt.toLowerCase().contains("don't") || prompt.toLowerCase().contains("must not") || prompt.toLowerCase().contains("avoid")) {
            score += 15;
        }
        if (prompt.toLowerCase().contains("must") || prompt.toLowerCase().contains("should") || prompt.toLowerCase().contains("need to")) {
            score += 10;
        }
        if (prompt.toLowerCase().contains("limit") || prompt.toLowerCase().contains("only") || prompt.toLowerCase().contains("maximum")) {
            score += 10;
        }
        
        return Math.min(score, 100);
    }
    
    /**
     * 分析输出格式
     */
    private int analyzeOutput(String prompt) {
        int score = 40;
        
        // 中文格式关键词
        if (prompt.contains("格式") || prompt.contains("输出") || prompt.contains("返回")) {
            score += 15;
        }
        if (prompt.contains("JSON") || prompt.contains("json") || prompt.contains("Markdown") || prompt.contains("markdown")) {
            score += 15;
        }
        if (prompt.contains("列表") || prompt.contains("表格") || prompt.contains("按照")) {
            score += 10;
        }
        if (prompt.contains("包含") || prompt.contains("包括") || prompt.contains("字段")) {
            score += 10;
        }
        
        // 英文格式关键词
        if (prompt.toLowerCase().contains("format") || prompt.toLowerCase().contains("output") || prompt.toLowerCase().contains("return")) {
            score += 15;
        }
        if (prompt.toLowerCase().contains("json") || prompt.toLowerCase().contains("markdown") || prompt.toLowerCase().contains("md")) {
            score += 15;
        }
        if (prompt.toLowerCase().contains("list") || prompt.toLowerCase().contains("table") || prompt.toLowerCase().contains("array")) {
            score += 10;
        }
        
        return Math.min(score, 100);
    }
}
