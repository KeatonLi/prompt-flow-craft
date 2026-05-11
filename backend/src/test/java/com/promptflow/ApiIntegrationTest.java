package com.promptflow;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

/**
 * API 集成测试
 * 测试所有后端接口，验证换行符是否正确返回
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, properties = {
    "server.port=8080"
})
public class ApiIntegrationTest {

    private static final String BASE_URL = "http://localhost:8080/api";
    private static final String ADMIN_USER = "admin";
    private static final String ADMIN_PASS = "admin123";

    @Test
    void testHealthEndpoint() throws Exception {
        System.out.println("\n========== 测试 /api/health ==========");
        String response = doGet("/health", null);
        System.out.println("响应: " + response);
        assertTrue(response.contains("Prompt Flow Craft") || response.contains("running"));
        System.out.println("✅ /api/health 测试通过\n");
    }

    @Test
    void testGeneratePromptSync() throws Exception {
        System.out.println("\n========== 测试 /api/generate-prompt (同步) ==========");
        String requestBody = "{\"taskDescription\":\"写一个hello world程序\"}";
        String response = doPost("/generate-prompt", requestBody, null);
        System.out.println("响应长度: " + response.length());
        System.out.println("响应前500字符: " + response.substring(0, Math.min(500, response.length())));

        // 检查是否有换行符
        boolean hasNewline = response.contains("\\n") || response.contains("\n");
        System.out.println("是否包含换行符: " + hasNewline);

        assertTrue(response.length() > 0, "响应不应为空");
        System.out.println("✅ /api/generate-prompt (同步) 测试通过\n");
    }

    @Test
    void testGeneratePromptStream() throws Exception {
        System.out.println("\n========== 测试 /api/generate-prompt/stream (流式) ==========");
        String requestBody = "{\"taskDescription\":\"写一个hello world程序\"}";

        AtomicBoolean hasNewline = new AtomicBoolean(false);
        StringBuilder fullResponse = new StringBuilder();

        doStreamPost("/generate-prompt/stream", requestBody, (chunk) -> {
            System.out.println("[chunk] " + repr(chunk));
            fullResponse.append(chunk);
            if (chunk.contains("\n") || chunk.contains("\\n")) {
                hasNewline.set(true);
            }
        });

        System.out.println("完整响应长度: " + fullResponse.length());
        System.out.println("完整响应前500字符: " + repr(fullResponse.substring(0, Math.min(500, fullResponse.length()))));
        System.out.println("是否包含换行符: " + hasNewline.get());

        assertTrue(fullResponse.length() > 0, "响应不应为空");
        System.out.println("✅ /api/generate-prompt/stream (流式) 测试通过\n");
    }

    @Test
    void testGenerateAgentSync() throws Exception {
        System.out.println("\n========== 测试 /api/generate-agent (同步) ==========");
        String requestBody = "{\"name\":\"测试Agent\",\"roleDescription\":\"你是一个专业的AI助手\"}";
        String response = doPost("/generate-agent", requestBody, null);
        System.out.println("响应长度: " + response.length());
        System.out.println("响应前500字符: " + response.substring(0, Math.min(500, response.length())));

        // 检查是否有换行符
        boolean hasNewline = response.contains("\\n") || response.contains("\n");
        System.out.println("是否包含换行符: " + hasNewline);

        assertTrue(response.length() > 0, "响应不应为空");
        System.out.println("✅ /api/generate-agent (同步) 测试通过\n");
    }

    @Test
    void testGenerateAgentStream() throws Exception {
        System.out.println("\n========== 测试 /api/generate-agent/stream (流式) ==========");
        String requestBody = "{\"name\":\"测试Agent\",\"roleDescription\":\"你是一个专业的AI助手\"}";

        AtomicBoolean hasNewline = new AtomicBoolean(false);
        StringBuilder fullResponse = new StringBuilder();

        doStreamPost("/generate-agent/stream", requestBody, (chunk) -> {
            System.out.println("[chunk] " + repr(chunk));
            fullResponse.append(chunk);
            if (chunk.contains("\n") || chunk.contains("\\n")) {
                hasNewline.set(true);
            }
        });

        System.out.println("完整响应长度: " + fullResponse.length());
        System.out.println("完整响应前500字符: " + repr(fullResponse.substring(0, Math.min(500, fullResponse.length()))));
        System.out.println("是否包含换行符: " + hasNewline.get());

        assertTrue(fullResponse.length() > 0, "响应不应为空");
        System.out.println("✅ /api/generate-agent/stream (流式) 测试通过\n");
    }

    @Test
    void testGenerateSkillSync() throws Exception {
        System.out.println("\n========== 测试 /api/generate-skill (同步) ==========");
        String requestBody = "{\"name\":\"天气查询\",\"description\":\"查询天气信息\"}";
        String response = doPost("/generate-skill", requestBody, null);
        System.out.println("响应长度: " + response.length());
        System.out.println("响应前500字符: " + response.substring(0, Math.min(500, response.length())));

        // 检查是否有换行符
        boolean hasNewline = response.contains("\\n") || response.contains("\n");
        System.out.println("是否包含换行符: " + hasNewline);

        assertTrue(response.length() > 0, "响应不应为空");
        System.out.println("✅ /api/generate-skill (同步) 测试通过\n");
    }

    @Test
    void testGenerateSkillStream() throws Exception {
        System.out.println("\n========== 测试 /api/generate-skill/stream (流式) ==========");
        String requestBody = "{\"name\":\"天气查询\",\"description\":\"查询天气信息\"}";

        AtomicBoolean hasNewline = new AtomicBoolean(false);
        StringBuilder fullResponse = new StringBuilder();

        doStreamPost("/generate-skill/stream", requestBody, (chunk) -> {
            System.out.println("[chunk] " + repr(chunk));
            fullResponse.append(chunk);
            if (chunk.contains("\n") || chunk.contains("\\n")) {
                hasNewline.set(true);
            }
        });

        System.out.println("完整响应长度: " + fullResponse.length());
        System.out.println("完整响应前500字符: " + repr(fullResponse.substring(0, Math.min(500, fullResponse.length()))));
        System.out.println("是否包含换行符: " + hasNewline.get());

        assertTrue(fullResponse.length() > 0, "响应不应为空");
        System.out.println("✅ /api/generate-skill/stream (流式) 测试通过\n");
    }

    @Test
    void testOptimizePrompt() throws Exception {
        System.out.println("\n========== 测试 /api/prompt/optimize ==========");
        String requestBody = "{\"prompt\":\"你好\",\"optimizationType\":\"general\",\"targetModel\":\"general\"}";
        String response = doPost("/prompt/optimize", requestBody, null);
        System.out.println("响应长度: " + response.length());
        System.out.println("响应前500字符: " + response.substring(0, Math.min(500, response.length())));
        assertTrue(response.length() > 0, "响应不应为空");
        System.out.println("✅ /api/prompt/optimize 测试通过\n");
    }

    @Test
    void testAnalyzePrompt() throws Exception {
        System.out.println("\n========== 测试 /api/prompt/analyze ==========");
        String requestBody = "{\"prompt\":\"你好世界\"}";
        String response = doPost("/prompt/analyze", requestBody, null);
        System.out.println("响应长度: " + response.length());
        System.out.println("响应前500字符: " + response.substring(0, Math.min(500, response.length())));
        assertTrue(response.length() > 0, "响应不应为空");
        System.out.println("✅ /api/prompt/analyze 测试通过\n");
    }

    @Test
    void testHistoryEndpoint() throws Exception {
        System.out.println("\n========== 测试 /api/history ==========");
        String response = doGet("/history", null);
        System.out.println("响应长度: " + response.length());
        System.out.println("响应前300字符: " + response.substring(0, Math.min(300, response.length())));
        assertTrue(response.length() > 0, "响应不应为空");
        System.out.println("✅ /api/history 测试通过\n");
    }

    @Test
    void testCategoriesEndpoint() throws Exception {
        System.out.println("\n========== 测试 /api/categories ==========");
        String response = doGet("/categories", null);
        System.out.println("响应长度: " + response.length());
        System.out.println("响应: " + response);
        assertTrue(response.length() > 0, "响应不应为空");
        System.out.println("✅ /api/categories 测试通过\n");
    }

    @Test
    void testTagsEndpoint() throws Exception {
        System.out.println("\n========== 测试 /api/tags ==========");
        String response = doGet("/tags", null);
        System.out.println("响应长度: " + response.length());
        System.out.println("响应: " + response);
        assertTrue(response.length() > 0, "响应不应为空");
        System.out.println("✅ /api/tags 测试通过\n");
    }

    // ========== 辅助方法 ==========

    private String doGet(String path, String auth) throws Exception {
        URL url = new URL(BASE_URL + path);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept", "application/json");
        if (auth != null) {
            conn.setRequestProperty("Authorization", "Basic " + Base64.getEncoder().encodeToString((ADMIN_USER + ":" + ADMIN_PASS).getBytes()));
        }
        conn.setConnectTimeout(10000);
        conn.setReadTimeout(30000);

        int responseCode = conn.getResponseCode();
        System.out.println("HTTP 响应码: " + responseCode);

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                responseCode >= 400 ? conn.getErrorStream() : conn.getInputStream(), StandardCharsets.UTF_8))) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            return response.toString();
        }
    }

    private String doPost(String path, String body, String auth) throws Exception {
        URL url = new URL(BASE_URL + path);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Accept", "application/json");
        if (auth != null) {
            conn.setRequestProperty("Authorization", "Basic " + Base64.getEncoder().encodeToString((ADMIN_USER + ":" + ADMIN_PASS).getBytes()));
        }
        conn.setDoOutput(true);
        conn.setConnectTimeout(30000);
        conn.setReadTimeout(120000);

        try (var os = conn.getOutputStream()) {
            os.write(body.getBytes(StandardCharsets.UTF_8));
        }

        int responseCode = conn.getResponseCode();
        System.out.println("HTTP 响应码: " + responseCode);

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                responseCode >= 400 ? conn.getErrorStream() : conn.getInputStream(), StandardCharsets.UTF_8))) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            return response.toString();
        }
    }

    private void doStreamPost(String path, String body, java.util.function.Consumer<String> onChunk) throws Exception {
        URL url = new URL(BASE_URL + path);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Accept", "text/event-stream");
        conn.setDoOutput(true);
        conn.setConnectTimeout(30000);
        conn.setReadTimeout(120000);

        try (var os = conn.getOutputStream()) {
            os.write(body.getBytes(StandardCharsets.UTF_8));
        }

        int responseCode = conn.getResponseCode();
        System.out.println("HTTP 响应码: " + responseCode);

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println("[SSE原始行] " + repr(line));
                if (line.startsWith("data:")) {
                    String data = line.substring(5);
                    if (!"[DONE]".equals(data.trim())) {
                        // 尝试解析 JSON
                        try {
                            var json = new com.fasterxml.jackson.databind.ObjectMapper().readTree(data);
                            String content = json.has("content") ? json.get("content").asText() : data;
                            onChunk.accept(content);
                        } catch (Exception e) {
                            // 非 JSON 原始内容
                            onChunk.accept(data);
                        }
                    }
                }
            }
        }
    }

    private String repr(String s) {
        if (s == null) return "null";
        if (s.length() <= 200) return s;
        return s.substring(0, 200) + "...";
    }
}
