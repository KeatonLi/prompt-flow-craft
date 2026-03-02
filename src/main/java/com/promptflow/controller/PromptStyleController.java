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
public class PromptStyleController {
    
    private static final Logger logger = LoggerFactory.getLogger(PromptStyleController.class);
    
    @Value("${api.key}")
    private String apiKey;
    
    @Value("${api.base-url}")
    private String baseUrl;
    
    @Value("${api.model}")
    private String model;
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    /**
     * 获取支持的风格类型
     */
    @GetMapping("/prompt/styles")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getStyles() {
        List<Map<String, Object>> styles = Arrays.asList(
            Map.of("id", "concise", "name", "简洁版", "desc", "去除冗余信息，保留核心要点"),
            Map.of("id", "detailed", "name", "详细版", "desc", "补充更多细节和上下文"),
            Map.of("id", "formal", "name", "正式版", "desc", "使用正式、专业的语言风格"),
            Map.of("id", "casual", "name", "口语版", "desc", "轻松活泼的对话式表达"),
            Map.of("id", "professional", "name", "技术版", "desc", "适合技术/专业人士"),
            Map.of("id", "beginner", "name", "新手版", "desc", "简单易懂，适合初学者"),
            Map.of("id", "creative", "name", "创意版", "desc", "更具创造性和吸引力"),
            Map.of("id", "structured", "name", "结构化", "desc", "添加清晰的层次结构")
        );
        return ResponseEntity.ok(ApiResponse.success("获取成功", styles));
    }
    
    /**
     * 风格迁移API
     */
    @PostMapping("/prompt/transform-style")
    public ResponseEntity<ApiResponse<Map<String, Object>>> transformStyle(@RequestBody Map<String, Object> request) {
        try {
            String prompt = (String) request.get("prompt");
            String targetStyle = (String) request.get("style");
            
            if (prompt == null || prompt.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, "提示词内容不能为空"));
            }
            
            if (targetStyle == null || targetStyle.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, "请选择目标风格"));
            }
            
            logger.info("Transforming prompt to style: {}", targetStyle);
            
            // 调用AI进行风格转换
            String transformedPrompt = transformPromptStyle(prompt, targetStyle);
            
            Map<String, Object> result = new HashMap<>();
            result.put("originalPrompt", prompt);
            result.put("transformedPrompt", transformedPrompt);
            result.put("style", targetStyle);
            
            return ResponseEntity.ok(ApiResponse.success("风格转换完成", result));
            
        } catch (Exception e) {
            logger.error("Error transforming prompt style", e);
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error(500, "风格转换时发生错误: " + e.getMessage()));
        }
    }
    
    /**
     * 批量风格转换
     */
    @PostMapping("/prompt/transform-batch")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> batchTransform(@RequestBody Map<String, Object> request) {
        try {
            String prompt = (String) request.get("prompt");
            List<String> styles = (List<String>) request.get("styles");
            
            if (prompt == null || prompt.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, "提示词内容不能为空"));
            }
            
            if (styles == null || styles.isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, "请至少选择一个目标风格"));
            }
            
            List<Map<String, Object>> results = new ArrayList<>();
            
            for (String style : styles) {
                try {
                    String transformed = transformPromptStyle(prompt, style);
                    Map<String, Object> item = new HashMap<>();
                    item.put("style", style);
                    item.put("prompt", transformed);
                    results.add(item);
                } catch (Exception e) {
                    logger.warn("Failed to transform to style {}: {}", style, e.getMessage());
                }
            }
            
            return ResponseEntity.ok(ApiResponse.success("批量转换完成", results));
            
        } catch (Exception e) {
            logger.error("Error in batch transform", e);
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error(500, "批量转换时发生错误: " + e.getMessage()));
        }
    }
    
    /**
     * 执行风格转换
     */
    private String transformPromptStyle(String prompt, String style) throws Exception {
        String stylePrompt = buildStylePrompt(prompt, style);
        String aiResponse = callLLMApi(stylePrompt);
        return aiResponse.trim();
    }
    
    /**
     * 构建风格转换提示词
     */
    private String buildStylePrompt(String prompt, String style) {
        String styleDesc = getStyleDescription(style);
        
        return "你是一个专业的AI提示词工程师。请将下面的提示词转换为【" + styleDesc + "】风格。\n\n" +
            "要求：\n" +
            "1. 只返回转换后的提示词内容，不要添加任何解释或前缀\n" +
            "2. 保持提示词的核心功能和目标不变\n" +
            "3. 根据目标风格适当调整语言表达、结构组织、细节详略\n\n" +
            "原始提示词：\n" +
            prompt + "\n\n" +
            "请直接输出转换后的提示词：";
    }
    
    /**
     * 获取风格描述
     */
    private String getStyleDescription(String style) {
        Map<String, String> descMap = Map.of(
            "concise", "简洁版",
            "detailed", "详细版",
            "formal", "正式版",
            "casual", "口语版",
            "professional", "技术版",
            "beginner", "新手版",
            "creative", "创意版",
            "structured", "结构化"
        );
        return descMap.getOrDefault(style, style);
    }
    
    /**
     * 调用LLM API
     */
    private String callLLMApi(String userPrompt) throws Exception {
        String urlStr = baseUrl + "/v1/chat/completions";
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Authorization", "Bearer " + apiKey);
        conn.setDoOutput(true);
        
        Map<String, Object> messages = new HashMap<>();
        messages.put("model", model);
        messages.put("messages", Arrays.asList(
            Map.of("role", "system", "content", "你是一个专业的AI提示词工程师，擅长将提示词转换为不同风格。请只返回转换后的内容，不要添加任何解释。"),
            Map.of("role", "user", "content", userPrompt)
        ));
        messages.put("temperature", 0.7);
        messages.put("max_tokens", 4000);
        
        String jsonInput = objectMapper.writeValueAsString(messages);
        
        try (java.io.OutputStream os = conn.getOutputStream()) {
            byte[] input = jsonInput.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }
        
        StringBuilder response = new StringBuilder();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                response.append(line);
            }
        }
        
        // 解析响应
        Map<String, Object> responseMap = objectMapper.readValue(response.toString(), Map.class);
        List<Map<String, Object>> choices = (List<Map<String, Object>>) responseMap.get("choices");
        if (choices == null || choices.isEmpty()) {
            throw new Exception("API返回结果为空");
        }
        
        Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
        return (String) message.get("content");
    }
}
