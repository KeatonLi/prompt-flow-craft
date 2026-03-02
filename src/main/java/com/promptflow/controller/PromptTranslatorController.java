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
public class PromptTranslatorController {
    
    private static final Logger logger = LoggerFactory.getLogger(PromptTranslatorController.class);
    
    @Value("${api.key}")
    private String apiKey;
    
    @Value("${api.base-url}")
    private String baseUrl;
    
    @Value("${api.model}")
    private String model;
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    /**
     * 翻译提示词API
     */
    @PostMapping("/translate")
    public ResponseEntity<ApiResponse<Map<String, Object>>> translatePrompt(@RequestBody Map<String, String> request) {
        try {
            String prompt = request.get("prompt");
            String targetLang = request.getOrDefault("targetLang", "en");
            String sourceLang = request.getOrDefault("sourceLang", "auto");
            
            if (prompt == null || prompt.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, "提示词内容不能为空"));
            }
            
            // 翻译提示词
            Map<String, Object> result = translatePrompt(prompt, sourceLang, targetLang);
            
            return ResponseEntity.ok(ApiResponse.success("翻译完成", result));
            
        } catch (Exception e) {
            logger.error("Error processing prompt translation request", e);
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error(500, "翻译提示词时发生错误: " + e.getMessage()));
        }
    }
    
    /**
     * 翻译提示词
     */
    private Map<String, Object> translatePrompt(String prompt, String sourceLang, String targetLang) throws Exception {
        Map<String, Object> result = new HashMap<>();
        
        // 检测源语言
        String detectedLang = detectLanguage(prompt);
        
        // 构建翻译提示词
        String translationPrompt = buildTranslationPrompt(prompt, sourceLang, targetLang);
        
        // 调用AI API进行翻译
        String translatedText = callAiApi(translationPrompt);
        
        // 清理翻译结果
        translatedText = cleanTranslationResult(translatedText);
        
        result.put("original", prompt);
        result.put("translated", translatedText);
        result.put("sourceLang", detectedLang);
        result.put("targetLang", targetLang);
        
        return result;
    }
    
    /**
     * 检测语言
     */
    private String detectLanguage(String text) {
        // 简单的语言检测：检查是否包含中文字符
        boolean hasChinese = false;
        boolean hasEnglish = false;
        
        for (char c : text.toCharArray()) {
            if (Character.UnicodeBlock.of(c) == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS) {
                hasChinese = true;
            }
            if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')) {
                hasEnglish = true;
            }
        }
        
        if (hasChinese && !hasEnglish) {
            return "zh";
        } else if (hasEnglish && !hasChinese) {
            return "en";
        } else if (hasChinese && hasEnglish) {
            return "mixed";
        }
        return "unknown";
    }
    
    /**
     * 构建翻译提示词
     */
    private String buildTranslationPrompt(String prompt, String sourceLang, String targetLang) {
        String targetLanguage;
        switch (targetLang) {
            case "en":
            case "english":
                targetLanguage = "English";
                break;
            case "zh":
            case "chinese":
                targetLanguage = "中文";
                break;
            case "ja":
            case "japanese":
                targetLanguage = "日本語";
                break;
            case "ko":
            case "korean":
                targetLanguage = "한국어";
                break;
            case "es":
            case "spanish":
                targetLanguage = "Español";
                break;
            case "fr":
            case "french":
                targetLanguage = "Français";
                break;
            case "de":
            case "german":
                targetLanguage = "Deutsch";
                break;
            default:
                targetLanguage = "English";
        }
        
        return "请将以下提示词翻译成" + targetLanguage + "。翻译时注意：\n" +
               "1. 保持原文的专业术语和AI提示词的特定格式\n" +
               "2. 如果有变量占位符（如{{variable}}、[variable]、{variable}等），保持不变\n" +
               "3. 保持命令语气和格式说明的风格\n" +
               "4. 只返回翻译结果，不要有任何解释\n\n" +
               "原文：\n" + prompt;
    }
    
    /**
     * 调用AI API
     */
    private String callAiApi(String prompt) throws Exception {
        String urlStr = baseUrl + "/v1/chat/completions";
        if (!urlStr.endsWith("/")) {
            urlStr += "/";
        }
        
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Authorization", "Bearer " + apiKey);
        conn.setDoOutput(true);
        
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", model);
        requestBody.put("messages", new Object[] {
            Map.of("role", "user", "content", prompt)
        });
        requestBody.put("temperature", 0.3);
        requestBody.put("max_tokens", 4000);
        
        String jsonBody = objectMapper.writeValueAsString(requestBody);
        
        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = jsonBody.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }
        
        int responseCode = conn.getResponseCode();
        if (responseCode != 200) {
            throw new RuntimeException("API request failed with code: " + responseCode);
        }
        
        StringBuilder response = new StringBuilder();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                response.append(line);
            }
        }
        
        Map<String, Object> responseMap = objectMapper.readValue(response.toString(), Map.class);
        List<Map<String, Object>> choices = (List<Map<String, Object>>) responseMap.get("choices");
        
        if (choices == null || choices.isEmpty()) {
            throw new RuntimeException("No response from API");
        }
        
        Map<String, Object> firstChoice = choices.get(0);
        Map<String, Object> message = (Map<String, Object>) firstChoice.get("message");
        
        return (String) message.get("content");
    }
    
    /**
     * 清理翻译结果
     */
    private String cleanTranslationResult(String result) {
        // 移除可能的引号包装
        result = result.trim();
        if ((result.startsWith("\"") && result.endsWith("\"") ||
             result.startsWith("'") && result.endsWith("'"))) {
            result = result.substring(1, result.length() - 1);
        }
        
        // 移除"翻译结果："等前缀
        String[] prefixes = {"翻译结果：", "Translation:", "translated:", "Result:"};
        for (String prefix : prefixes) {
            if (result.startsWith(prefix)) {
                result = result.substring(prefix.length()).trim();
            }
        }
        
        return result;
    }
}
