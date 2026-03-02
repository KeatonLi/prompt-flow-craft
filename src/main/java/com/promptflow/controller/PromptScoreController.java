package com.promptflow.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.promptflow.dto.ApiResponse;
import com.promptflow.dto.PromptScoreRequest;
import com.promptflow.dto.PromptScoreResponse;
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
public class PromptScoreController {
    
    private static final Logger logger = LoggerFactory.getLogger(PromptScoreController.class);
    
    @Value("${api.key}")
    private String apiKey;
    
    @Value("${api.base-url}")
    private String baseUrl;
    
    @Value("${api.model}")
    private String model;
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    /**
     * 提示词质量评分API
     */
    @PostMapping("/prompt/score")
    public ResponseEntity<ApiResponse<PromptScoreResponse>> scorePrompt(@RequestBody PromptScoreRequest request) {
        try {
            logger.info("Received prompt score request");
            
            if (request.getPrompt() == null || request.getPrompt().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, "提示词内容不能为空"));
            }
            
            String prompt = request.getPrompt();
            
            // 调用AI进行评分分析
            PromptScoreResponse scoreResult = analyzePromptQuality(prompt);
            
            return ResponseEntity.ok(ApiResponse.success("评分完成", scoreResult));
            
        } catch (Exception e) {
            logger.error("Error scoring prompt", e);
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error(500, "评分时发生错误: " + e.getMessage()));
        }
    }
    
    /**
     * 分析提示词质量
     */
    private PromptScoreResponse analyzePromptQuality(String prompt) throws Exception {
        String analysisPrompt = buildAnalysisPrompt(prompt);
        String aiResponse = callLLMApi(analysisPrompt);
        
        // 解析AI响应
        return parseScoreResponse(aiResponse, prompt);
    }
    
    /**
     * 构建分析提示词
     */
    private String buildAnalysisPrompt(String prompt) {
        return "你是一个专业的AI提示词质量评估专家。请对以下提示词进行多维度质量评分。\n\n" +
            "评分维度：\n" +
            "1. 清晰度(Clarity)：提示词表达是否清晰明确，没有歧义\n" +
            "2. 完整性(Completeness)：是否包含必要的组成部分（角色、任务、约束等）\n" +
            "3. 结构化(Structure)：是否有清晰的结构层次\n" +
            "4. 示例质量(Examples)：是否包含有效的示例(few-shot)\n\n" +
            "请按以下JSON格式返回评分结果（只返回JSON，不要其他内容）：\n" +
            "{\n" +
            "    \"totalScore\": 85,\n" +
            "    \"clarityScore\": 90,\n" +
            "    \"completenessScore\": 85,\n" +
            "    \"structureScore\": 80,\n" +
            "    \"examplesScore\": 70,\n" +
            "    \"level\": \"优秀\",\n" +
            "    \"strengths\": [\"优点1\", \"优点2\"],\n" +
            "    \"weaknesses\": [\"不足1\", \"不足2\"],\n" +
            "    \"suggestions\": [\"建议1\", \"建议2\"],\n" +
            "    \"summary\": \"一句话总结\"\n" +
            "}\n\n" +
            "评分标准：\n" +
            "- 优秀(90-100)：可直接使用，质量很高\n" +
            "- 良好(70-89)：稍作优化即可使用\n" +
            "- 一般(50-69)：需要较大改进\n" +
            "- 待改进(0-49)：需要重新设计\n\n" +
            "要评分的提示词：\n" + prompt;
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
            Map.of("role", "system", "content", "你是一个专业的AI提示词质量评估专家。请严格按照要求的JSON格式返回结果。"),
            Map.of("role", "user", "content", userPrompt)
        ));
        messages.put("temperature", 0.3);
        messages.put("max_tokens", 2000);
        
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
    
    /**
     * 解析AI评分响应
     */
    private PromptScoreResponse parseScoreResponse(String aiResponse, String originalPrompt) {
        PromptScoreResponse result = new PromptScoreResponse();
        
        try {
            // 尝试提取JSON
            String jsonStr = extractJson(aiResponse);
            if (jsonStr != null) {
                Map<String, Object> data = objectMapper.readValue(jsonStr, Map.class);
                
                result.setTotalScore(toInt(data.get("totalScore")));
                result.setClarityScore(toInt(data.get("clarityScore")));
                result.setCompletenessScore(toInt(data.get("completenessScore")));
                result.setStructureScore(toInt(data.get("structureScore")));
                result.setExamplesScore(toInt(data.get("examplesScore")));
                result.setLevel((String) data.get("level"));
                result.setStrengths((List<String>) data.get("strengths"));
                result.setWeaknesses((List<String>) data.get("weaknesses"));
                result.setSuggestions((List<String>) data.get("suggestions"));
                result.setSummary((String) data.get("summary"));
            } else {
                // 如果无法解析JSON，使用默认评分
                setDefaultScore(result);
            }
        } catch (Exception e) {
            logger.warn("Failed to parse AI response, using default score", e);
            setDefaultScore(result);
        }
        
        return result;
    }
    
    /**
     * 从AI响应中提取JSON
     */
    private String extractJson(String response) {
        // 尝试直接解析
        try {
            objectMapper.readValue(response, Map.class);
            return response;
        } catch (Exception e) {}
        
        // 尝试找JSON块
        int start = response.indexOf("{");
        int end = response.lastIndexOf("}");
        
        if (start >= 0 && end > start) {
            return response.substring(start, end + 1);
        }
        
        return null;
    }
    
    /**
     * 设置默认评分（当无法解析AI响应时）
     */
    private void setDefaultScore(PromptScoreResponse result) {
        result.setTotalScore(0);
        result.setClarityScore(0);
        result.setCompletenessScore(0);
        result.setStructureScore(0);
        result.setExamplesScore(0);
        result.setLevel("分析失败");
        result.setStrengths(Collections.emptyList());
        result.setWeaknesses(Collections.singletonList("无法分析提示词质量，请稍后重试"));
        result.setSuggestions(Collections.singletonList("请确保提示词内容不为空"));
        result.setSummary("评分系统暂时不可用");
    }
    
    private int toInt(Object obj) {
        if (obj == null) return 0;
        if (obj instanceof Number) return ((Number) obj).intValue();
        try {
            return Integer.parseInt(obj.toString());
        } catch (Exception e) {
            return 0;
        }
    }
}
