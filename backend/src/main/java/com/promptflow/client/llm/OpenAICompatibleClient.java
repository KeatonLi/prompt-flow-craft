package com.promptflow.client.llm;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.promptflow.dto.llm.LLMRequest;
import com.promptflow.dto.llm.LLMResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * OpenAI 兼容格式的 LLM 客户端
 * 支持 OpenAI、通义千问、DeepSeek 等兼容 OpenAI API 格式的服务
 */
@Component
public class OpenAICompatibleClient implements LLMClient {
    
    private static final Logger logger = LoggerFactory.getLogger(OpenAICompatibleClient.class);
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Value("${api.key}")
    private String apiKey;
    
    @Value("${api.base-url}")
    private String baseUrl;
    
    @Value("${api.model}")
    private String defaultModel;
    
    @Value("${api.connect-timeout:30000}")
    private int connectTimeout;
    
    @Value("${api.read-timeout:120000}")
    private int readTimeout;
    
    @Override
    public String getClientType() {
        return "openai-compatible";
    }
    
    @Override
    public LLMResponse call(LLMRequest request) {
        try {
            Map<String, Object> requestBody = buildRequestBody(request, false);
            String jsonBody = objectMapper.writeValueAsString(requestBody);
            
            String response = executeHttpPost(jsonBody);
            return parseResponse(response);
            
        } catch (Exception e) {
            logger.error("LLM API call failed", e);
            return LLMResponse.error("API调用失败: " + e.getMessage());
        }
    }
    
    @Override
    public void callStream(LLMRequest request, 
                          Consumer<String> onContent,
                          Runnable onComplete,
                          Consumer<Throwable> onError) {
        try {
            Map<String, Object> requestBody = buildRequestBody(request, true);
            String jsonBody = objectMapper.writeValueAsString(requestBody);
            
            executeStreamHttpPost(jsonBody, onContent, onComplete, onError);
            
        } catch (Exception e) {
            logger.error("LLM stream API call failed", e);
            onError.accept(e);
        }
    }
    
    /**
     * 构建请求体
     */
    private Map<String, Object> buildRequestBody(LLMRequest request, boolean stream) {
        Map<String, Object> body = new HashMap<>();
        body.put("model", request.getModel() != null ? request.getModel() : defaultModel);
        body.put("messages", convertMessages(request.getMessages()));
        body.put("temperature", request.getTemperature() != null ? request.getTemperature() : 0.7);
        body.put("max_tokens", request.getMaxTokens() != null ? request.getMaxTokens() : 4000);
        body.put("stream", stream);
        
        if (request.getExtraParams() != null) {
            body.putAll(request.getExtraParams());
        }
        
        return body;
    }
    
    /**
     * 转换消息格式
     */
    private List<Map<String, String>> convertMessages(List<LLMRequest.Message> messages) {
        return messages.stream()
            .map(m -> Map.of("role", m.getRole(), "content", m.getContent()))
            .collect(Collectors.toList());
    }
    
    /**
     * 执行 HTTP POST 请求
     */
    private String executeHttpPost(String jsonBody) throws Exception {
        URL url = new URL(normalizeUrl(baseUrl) + "/chat/completions");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Authorization", "Bearer " + apiKey);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);
        conn.setConnectTimeout(connectTimeout);
        conn.setReadTimeout(readTimeout);
        
        try (OutputStream os = conn.getOutputStream()) {
            os.write(jsonBody.getBytes(StandardCharsets.UTF_8));
        }
        
        int responseCode = conn.getResponseCode();
        if (responseCode != 200) {
            String errorMsg = readErrorStream(conn);
            throw new RuntimeException("API调用失败: " + responseCode + " - " + errorMsg);
        }
        
        return readInputStream(conn);
    }
    
    /**
     * 执行流式 HTTP POST 请求
     */
    private void executeStreamHttpPost(String jsonBody,
                                      Consumer<String> onContent,
                                      Runnable onComplete,
                                      Consumer<Throwable> onError) {
        try {
            URL url = new URL(normalizeUrl(baseUrl) + "/chat/completions");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Bearer " + apiKey);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);
            conn.setConnectTimeout(connectTimeout);
            conn.setReadTimeout(readTimeout);
            
            try (OutputStream os = conn.getOutputStream()) {
                os.write(jsonBody.getBytes(StandardCharsets.UTF_8));
            }
            
            int responseCode = conn.getResponseCode();
            if (responseCode != 200) {
                String errorMsg = readErrorStream(conn);
                onError.accept(new RuntimeException("API调用失败: " + responseCode + " - " + errorMsg));
                return;
            }
            
            // 读取流式响应
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.startsWith("data: ")) {
                        String data = line.substring(6);
                        if ("[DONE]".equals(data)) {
                            onComplete.run();
                            return;
                        }
                        
                        String content = extractContentFromStream(data);
                        if (content != null && !content.isEmpty()) {
                            onContent.accept(content);
                        }
                    }
                }
                onComplete.run();
            }
            
        } catch (Exception e) {
            onError.accept(e);
        }
    }
    
    /**
     * 从流式响应中提取内容
     */
    private String extractContentFromStream(String data) {
        try {
            Map<String, Object> event = objectMapper.readValue(data, Map.class);
            List<Map<String, Object>> choices = (List<Map<String, Object>>) event.get("choices");
            if (choices != null && !choices.isEmpty()) {
                Map<String, Object> delta = (Map<String, Object>) choices.get(0).get("delta");
                if (delta != null) {
                    return (String) delta.get("content");
                }
            }
        } catch (Exception e) {
            logger.debug("Failed to parse stream data: {}", data);
        }
        return null;
    }
    
    /**
     * 解析响应
     */
    private LLMResponse parseResponse(String response) {
        try {
            Map<String, Object> responseMap = objectMapper.readValue(response, Map.class);
            
            LLMResponse llmResponse = new LLMResponse();
            llmResponse.setId((String) responseMap.get("id"));
            llmResponse.setModel((String) responseMap.get("model"));
            Object created = responseMap.get("created");
            if (created instanceof Number) {
                llmResponse.setCreated(((Number) created).longValue());
            }
            
            // 解析 choices
            List<Map<String, Object>> choices = (List<Map<String, Object>>) responseMap.get("choices");
            if (choices != null && !choices.isEmpty()) {
                Map<String, Object> choice = choices.get(0);
                Map<String, Object> message = (Map<String, Object>) choice.get("message");
                if (message != null) {
                    llmResponse.setContent((String) message.get("content"));
                }
            }
            
            // 解析 usage
            Map<String, Object> usage = (Map<String, Object>) responseMap.get("usage");
            if (usage != null) {
                llmResponse.setPromptTokens((Integer) usage.get("prompt_tokens"));
                llmResponse.setCompletionTokens((Integer) usage.get("completion_tokens"));
                llmResponse.setTotalTokens((Integer) usage.get("total_tokens"));
            }
            
            return llmResponse;
            
        } catch (Exception e) {
            logger.error("Failed to parse response", e);
            return LLMResponse.error("解析响应失败: " + e.getMessage());
        }
    }
    
    /**
     * 读取输入流
     */
    private String readInputStream(HttpURLConnection conn) throws Exception {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            return response.toString();
        }
    }
    
    /**
     * 读取错误流
     */
    private String readErrorStream(HttpURLConnection conn) {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(conn.getErrorStream(), StandardCharsets.UTF_8))) {
            StringBuilder error = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                error.append(line);
            }
            return error.toString();
        } catch (Exception e) {
            return "无法读取错误信息";
        }
    }
    
    /**
     * 规范化 URL
     */
    private String normalizeUrl(String url) {
        if (url.endsWith("/")) {
            return url.substring(0, url.length() - 1);
        }
        return url;
    }
}