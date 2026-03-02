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
@RequestMapping("/api")
@CrossOrigin(origins = {"*"}, 
           allowCredentials = "false", 
           allowedHeaders = "*", 
           methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.OPTIONS})
public class PromptFormatterController {
    
    private static final Logger logger = LoggerFactory.getLogger(PromptFormatterController.class);
    
    @Value("${api.key}")
    private String apiKey;
    
    @Value("${api.base-url}")
    private String baseUrl;
    
    @Value("${api.model}")
    private String model;
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    /**
     * 格式化提示词API
     */
    @PostMapping("/prompt/format")
    public ResponseEntity<ApiResponse<FormatResponse>> formatPrompt(@RequestBody FormatRequest request) {
        try {
            logger.info("Received prompt format request");
            
            if (request.getPrompt() == null || request.getPrompt().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, "提示词内容不能为空"));
            }
            
            String formatType = request.getFormatType();
            if (formatType == null || formatType.isEmpty()) {
                formatType = "standard";
            }
            
            // 调用LLM进行格式化
            FormatResponse response = callLLMFormat(request.getPrompt(), formatType);
            
            return ResponseEntity.ok(ApiResponse.success("格式化完成", response));
            
        } catch (Exception e) {
            logger.error("Error processing prompt format request", e);
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error(500, "格式化提示词时发生错误: " + e.getMessage()));
        }
    }
    
    /**
     * 构建格式化提示词的系统prompt
     */
    private String buildFormatPrompt(String userPrompt, String formatType) {
        StringBuilder promptBuilder = new StringBuilder();
        
        promptBuilder.append("你是一位专业的AI提示词格式化专家。你的任务是格式化和完善提示词。\n\n");
        
        promptBuilder.append("## 原始提示词\n");
        promptBuilder.append(userPrompt).append("\n\n");
        
        promptBuilder.append("## 格式化类型\n");
        switch (formatType) {
            case "markdown":
                promptBuilder.append("使用Markdown格式美化提示词，添加标题、列表、代码块等\n");
                break;
            case "structured":
                promptBuilder.append("使用结构化格式，分成角色定义、任务目标、约束条件、输出格式等部分\n");
                break;
            case "concise":
                promptBuilder.append("精简提示词，去除冗余内容，保留核心信息\n");
                break;
            case "detailed":
                promptBuilder.append("详细扩充提示词，添加更多细节和示例\n");
                break;
            default:
                promptBuilder.append("标准格式化：整理结构，规范表达，添加必要的分隔\n");
        }
        
        promptBuilder.append("\n## 输出要求\n");
        promptBuilder.append("请按以下JSON格式返回格式化结果：\n");
        promptBuilder.append("{\n");
        promptBuilder.append("  \"formattedPrompt\": \"格式化后的完整提示词\",\n");
        promptBuilder.append("  \"formatType\": \"实际使用的格式化类型\",\n");
        promptBuilder.append("  \"changes\": [\n");
        promptBuilder.append("    {\n");
        promptBuilder.append("      \"type\": \"修改类型\",\n");
        promptBuilder.append("      \"description\": \"具体修改内容\"\n");
        promptBuilder.append("    }\n");
        promptBuilder.append("  ]\n");
        promptBuilder.append("}\n\n");
        promptBuilder.append("请确保返回的是有效的JSON格式。");
        
        return promptBuilder.toString();
    }
    
    /**
     * 调用LLM进行提示词格式化
     */
    private FormatResponse callLLMFormat(String userPrompt, String formatType) throws Exception {
        String systemPrompt = buildFormatPrompt(userPrompt, formatType);
        
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
            return parseFormatResponse(content);
        }
        
        throw new RuntimeException("API响应格式错误");
    }
    
    /**
     * 解析LLM返回的格式化结果
     */
    private FormatResponse parseFormatResponse(String content) {
        try {
            // 尝试提取JSON部分
            int startIdx = content.indexOf("{");
            int endIdx = content.lastIndexOf("}");
            
            if (startIdx >= 0 && endIdx >= 0) {
                String jsonStr = content.substring(startIdx, endIdx + 1);
                Map<String, Object> result = objectMapper.readValue(jsonStr, Map.class);
                
                FormatResponse response = new FormatResponse();
                response.setFormattedPrompt((String) result.get("formattedPrompt"));
                response.setFormatType((String) result.getOrDefault("formatType", "standard"));
                
                // 解析changes
                List<Map<String, String>> changesList = (List<Map<String, String>>) result.get("changes");
                if (changesList != null) {
                    List<FormatResponse.Change> changes = new ArrayList<>();
                    for (Map<String, String> change : changesList) {
                        changes.add(new FormatResponse.Change(
                            change.get("type"),
                            change.get("description")
                        ));
                    }
                    response.setChanges(changes);
                }
                
                return response;
            }
            
            // 如果没有找到JSON，返回原始内容
            FormatResponse response = new FormatResponse();
            response.setFormattedPrompt(content);
            response.setFormatType("standard");
            response.setChanges(new ArrayList<>());
            return response;
            
        } catch (Exception e) {
            logger.warn("解析格式化结果JSON失败，使用原始文本", e);
            FormatResponse response = new FormatResponse();
            response.setFormattedPrompt(content);
            response.setFormatType("standard");
            response.setChanges(new ArrayList<>());
            return response;
        }
    }
    
    private String normalizeUrl(String url) {
        if (url == null) return "";
        return url.replaceAll("/+$", "");
    }
    
    // ===== 请求和响应类 =====
    
    public static class FormatRequest {
        private String prompt;
        private String formatType;
        
        public String getPrompt() { return prompt; }
        public void setPrompt(String prompt) { this.prompt = prompt; }
        public String getFormatType() { return formatType; }
        public void setFormatType(String formatType) { this.formatType = formatType; }
    }
    
    public static class FormatResponse {
        private String formattedPrompt;
        private String formatType;
        private List<Change> changes;
        
        public String getFormattedPrompt() { return formattedPrompt; }
        public void setFormattedPrompt(String formattedPrompt) { this.formattedPrompt = formattedPrompt; }
        public String getFormatType() { return formatType; }
        public void setFormatType(String formatType) { this.formatType = formatType; }
        public List<Change> getChanges() { return changes; }
        public void setChanges(List<Change> changes) { this.changes = changes; }
        
        public static class Change {
            private String type;
            private String description;
            
            public Change() {}
            public Change(String type, String description) {
                this.type = type;
                this.description = description;
            }
            
            public String getType() { return type; }
            public void setType(String type) { this.type = type; }
            public String getDescription() { return description; }
            public void setDescription(String description) { this.description = description; }
        }
    }
}
