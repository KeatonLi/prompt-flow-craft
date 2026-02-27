package com.promptflow.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.promptflow.dto.ApiResponse;
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
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
}
