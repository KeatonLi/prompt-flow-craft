package com.promptflow.controller;

import com.promptflow.dto.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/convert")
@CrossOrigin(origins = {"*"}, 
           allowCredentials = "false", 
           allowedHeaders = "*")
public class PromptConvertController {
    
    private static final Logger logger = LoggerFactory.getLogger(PromptConvertController.class);
    
    /**
     * 转换提示词为不同格式
     */
    @PostMapping("/format")
    public ResponseEntity<ApiResponse<Map<String, String>>> convertFormat(
            @RequestBody ConvertRequest request) {
        
        String prompt = request.getPrompt();
        String targetFormat = request.getFormat();
        Map<String, String> result = new HashMap<>();
        
        try {
            switch (targetFormat) {
                case "json":
                    result.put("content", convertToJson(prompt, request.getName()));
                    result.put("mimeType", "application/json");
                    break;
                case "yaml":
                    result.put("content", convertToYaml(prompt, request.getName()));
                    result.put("mimeType", "text/yaml");
                    break;
                case "python":
                    result.put("content", convertToPython(prompt, request.getName()));
                    result.put("mimeType", "text/x-python");
                    break;
                case "curl":
                    result.put("content", convertToCurl(prompt, request.getApiEndpoint(), request.getApiKey()));
                    result.put("mimeType", "text/plain");
                    break;
                case "openapi":
                    result.put("content", convertToOpenAPI(prompt, request.getName()));
                    result.put("mimeType", "application/vnd.api+json");
                    break;
                default:
                    result.put("content", prompt);
                    result.put("mimeType", "text/plain");
            }
            
            return ResponseEntity.ok(ApiResponse.success(result));
        } catch (Exception e) {
            logger.error("格式转换失败", e);
            return ResponseEntity.ok(ApiResponse.error("格式转换失败: " + e.getMessage()));
        }
    }
    
    /**
     * 转换为JSON格式
     */
    private String convertToJson(String prompt, String name) {
        String jsonPrompt = prompt.replace("\\", "\\\\").replace("\"", "\\\"")
                                  .replace("\n", "\\n").replace("\r", "\\r");
        String timestamp = java.time.LocalDateTime.now().toString();
        String safeName = name != null ? name : "prompt";
        
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append("  \"name\": \"").append(safeName).append("\",\n");
        sb.append("  \"prompt\": \"").append(jsonPrompt).append("\",\n");
        sb.append("  \"version\": \"1.0\",\n");
        sb.append("  \"description\": \"AI Prompt - ").append(safeName).append("\",\n");
        sb.append("  \"variables\": [],\n");
        sb.append("  \"metadata\": {\n");
        sb.append("    \"created\": \"").append(timestamp).append("\",\n");
        sb.append("    \"tool\": \"Prompt Flow Craft\"\n");
        sb.append("  }\n");
        sb.append("}");
        return sb.toString();
    }
    
    /**
     * 转换为YAML格式
     */
    private String convertToYaml(String prompt, String name) {
        String timestamp = java.time.LocalDateTime.now().toString();
        String safeName = name != null ? name : "prompt";
        
        StringBuilder sb = new StringBuilder();
        sb.append("# Prompt Flow Craft - YAML Format\n");
        sb.append("# Generated: ").append(timestamp).append("\n\n");
        sb.append("name: ").append(safeName).append("\n");
        sb.append("description: AI Prompt - ").append(safeName).append("\n");
        sb.append("version: \"1.0\"\n\n");
        sb.append("prompt: |\n");
        
        String[] lines = prompt.split("\n");
        for (String line : lines) {
            sb.append("  ").append(line).append("\n");
        }
        
        sb.append("\nvariables: []\n\n");
        sb.append("metadata:\n");
        sb.append("  tool: Prompt Flow Craft\n");
        sb.append("  created: ").append(timestamp).append("\n");
        
        return sb.toString();
    }
    
    /**
     * 转换为Python代码 (LangChain格式)
     */
    private String convertToPython(String prompt, String name) {
        String varName = name != null ? name.toLowerCase().replaceAll("[^a-z0-9]", "_") : "prompt";
        String safeName = name != null ? name : "Prompt";
        
        StringBuilder sb = new StringBuilder();
        sb.append("# -*- coding: utf-8 -*-\n");
        sb.append("\"\"\"\n");
        sb.append(safeName).append(" - AI Prompt Template\n");
        sb.append("Generated by Prompt Flow Craft\n");
        sb.append("\"\"\"\n\n");
        sb.append("from langchain.prompts import PromptTemplate\n\n");
        sb.append("# Prompt content\n");
        sb.append("prompt_text = '''").append(prompt).append("'''\n\n");
        sb.append("# Create PromptTemplate\n");
        sb.append(varName).append("_template = PromptTemplate(\n");
        sb.append("    input_variables=[],\n");
        sb.append("    template=prompt_text,\n");
        sb.append("    validate_template=True\n");
        sb.append(")\n\n");
        sb.append("# Usage example\n");
        sb.append("if __name__ == \"__main__\":\n");
        sb.append("    # Format with variables (if any)\n");
        sb.append("    # formatted = ").append(varName).append("_template.format()\n");
        sb.append("    print(\"Prompt Template Ready!\")\n");
        sb.append("    print(prompt_text)\n");
        
        return sb.toString();
    }
    
    /**
     * 转换为cURL命令
     */
    private String convertToCurl(String prompt, String apiEndpoint, String apiKey) {
        String endpoint = apiEndpoint != null ? apiEndpoint : "https://api.example.com/v1/chat/completions";
        String key = apiKey != null ? apiKey : "YOUR_API_KEY";
        String escapedPrompt = prompt.replace("\"", "\\\"").replace("\n", "\\n");
        String timestamp = java.time.LocalDateTime.now().toString();
        
        StringBuilder sb = new StringBuilder();
        sb.append("# Prompt Flow Craft - cURL Command\n");
        sb.append("# Generated: ").append(timestamp).append("\n\n");
        sb.append("curl -X POST \"").append(endpoint).append("\" \\\n");
        sb.append("  -H \"Content-Type: application/json\" \\\n");
        sb.append("  -H \"Authorization: Bearer ").append(key).append("\" \\\n");
        sb.append("  -d '{\n");
        sb.append("    \"model\": \"gpt-4\",\n");
        sb.append("    \"messages\": [\n");
        sb.append("      {\n");
        sb.append("        \"role\": \"user\",\n");
        sb.append("        \"content\": \"").append(escapedPrompt).append("\"\n");
        sb.append("      }\n");
        sb.append("    ],\n");
        sb.append("    \"temperature\": 0.7\n");
        sb.append("  }'");
        
        return sb.toString();
    }
    
    /**
     * 转换为OpenAPI规范
     */
    private String convertToOpenAPI(String prompt, String name) {
        String operationId = name != null ? name.toLowerCase().replaceAll("[^a-z0-9]", "_") : "execute_prompt";
        String safeName = name != null ? name : "Prompt";
        String escapedPrompt = prompt.replace("\\", "\\\\").replace("\"", "\\\"")
                                       .replace("\n", "\\n").replace("\r", "\\r");
        
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append("  \"openapi\": \"3.0.0\",\n");
        sb.append("  \"info\": {\n");
        sb.append("    \"title\": \"").append(safeName).append(" API\",\n");
        sb.append("    \"description\": \"AI Prompt API - ").append(safeName).append("\",\n");
        sb.append("    \"version\": \"1.0.0\"\n");
        sb.append("  },\n");
        sb.append("  \"servers\": [\n");
        sb.append("    {\n");
        sb.append("      \"url\": \"https://api.example.com\",\n");
        sb.append("      \"description\": \"Production server\"\n");
        sb.append("    }\n");
        sb.append("  ],\n");
        sb.append("  \"paths\": {\n");
        sb.append("    \"/prompt/execute\": {\n");
        sb.append("      \"post\": {\n");
        sb.append("        \"operationId\": \"").append(operationId).append("\",\n");
        sb.append("        \"summary\": \"Execute AI Prompt\",\n");
        sb.append("        \"description\": \"Execute the prompt and get AI response\",\n");
        sb.append("        \"requestBody\": {\n");
        sb.append("          \"required\": true,\n");
        sb.append("          \"content\": {\n");
        sb.append("            \"application/json\": {\n");
        sb.append("              \"schema\": {\n");
        sb.append("                \"type\": \"object\",\n");
        sb.append("                \"properties\": {\n");
        sb.append("                  \"prompt\": {\n");
        sb.append("                    \"type\": \"string\",\n");
        sb.append("                    \"default\": \"").append(escapedPrompt).append("\"\n");
        sb.append("                  },\n");
        sb.append("                  \"model\": {\n");
        sb.append("                    \"type\": \"string\",\n");
        sb.append("                    \"default\": \"gpt-4\"\n");
        sb.append("                  },\n");
        sb.append("                  \"temperature\": {\n");
        sb.append("                    \"type\": \"number\",\n");
        sb.append("                    \"default\": 0.7\n");
        sb.append("                  }\n");
        sb.append("                }\n");
        sb.append("              }\n");
        sb.append("            }\n");
        sb.append("          }\n");
        sb.append("        },\n");
        sb.append("        \"responses\": {\n");
        sb.append("          \"200\": {\n");
        sb.append("            \"description\": \"Successful response\",\n");
        sb.append("            \"content\": {\n");
        sb.append("              \"application/json\": {\n");
        sb.append("                \"schema\": {\n");
        sb.append("                  \"type\": \"object\",\n");
        sb.append("                  \"properties\": {\n");
        sb.append("                    \"result\": {\n");
        sb.append("                      \"type\": \"string\"\n");
        sb.append("                    }\n");
        sb.append("                  }\n");
        sb.append("                }\n");
        sb.append("              }\n");
        sb.append("            }\n");
        sb.append("          }\n");
        sb.append("        }\n");
        sb.append("      }\n");
        sb.append("    }\n");
        sb.append("  }\n");
        sb.append("}");
        
        return sb.toString();
    }
    
    /**
     * 获取支持的格式列表
     */
    @GetMapping("/formats")
    public ResponseEntity<ApiResponse<List<FormatInfo>>> getFormats() {
        List<FormatInfo> formats = Arrays.asList(
            new FormatInfo("json", "JSON", "标准JSON格式，适合API调用", "application/json"),
            new FormatInfo("yaml", "YAML", "YAML格式，适合配置文件", "text/yaml"),
            new FormatInfo("python", "Python", "Python LangChain代码", "text/x-python"),
            new FormatInfo("curl", "cURL", "cURL命令行语句", "text/plain"),
            new FormatInfo("openapi", "OpenAPI", "OpenAPI 3.0规范", "application/vnd.api+json")
        );
        return ResponseEntity.ok(ApiResponse.success(formats));
    }
    
    // 请求和响应类
    public static class ConvertRequest {
        private String prompt;
        private String format;
        private String name;
        private String apiEndpoint;
        private String apiKey;
        
        public String getPrompt() { return prompt; }
        public void setPrompt(String prompt) { this.prompt = prompt; }
        public String getFormat() { return format; }
        public void setFormat(String format) { this.format = format; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getApiEndpoint() { return apiEndpoint; }
        public void setApiEndpoint(String apiEndpoint) { this.apiEndpoint = apiEndpoint; }
        public String getApiKey() { return apiKey; }
        public void setApiKey(String apiKey) { this.apiKey = apiKey; }
    }
    
    public static class FormatInfo {
        private String id;
        private String name;
        private String description;
        private String mimeType;
        
        public FormatInfo(String id, String name, String description, String mimeType) {
            this.id = id;
            this.name = name;
            this.description = description;
            this.mimeType = mimeType;
        }
        
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public String getMimeType() { return mimeType; }
        public void setMimeType(String mimeType) { this.mimeType = mimeType; }
    }
}
