package com.promptflow.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.promptflow.dto.PromptRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.BufferedReader;
import java.io.StringReader;
import java.time.Duration;
import java.util.List;
import java.util.Map;

@Service
public class PromptGenerationService {
    
    private static final Logger logger = LoggerFactory.getLogger(PromptGenerationService.class);
    
    private final WebClient webClient;
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
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
    
    public PromptGenerationService() {
        this.webClient = WebClient.builder().build();
    }
    
    /**
     * 同步生成提示词（非流式）
     */
    public String generatePrompt(PromptRequest request) {
        try {
            // 首先检查缓存
            String cachedPrompt = promptCacheService.getCachedPrompt(request);
            if (cachedPrompt != null) {
                logger.info("从缓存返回结果，避免API调用");
                return cachedPrompt;
            }
            
            logger.info("缓存未命中，调用API生成新的提示词");
            String systemPrompt = buildSystemPrompt(request);
            
            // 构建请求体
            Map<String, Object> requestBody = Map.of(
                "model", model,
                "messages", List.of(
                    Map.of("role", "user", "content", systemPrompt)
                ),
                "temperature", temperature,
                "max_tokens", maxTokens
            );
            
            // 调用Qwen API
            Mono<Map> responseMono = webClient.post()
                .uri(baseUrl + "/chat/completions")
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(Map.class);
            
            Map<String, Object> response = responseMono.block();
            
            if (response != null && response.containsKey("choices")) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
                if (!choices.isEmpty()) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
                    String generatedPrompt = (String) message.get("content");
                    
                    // 保存到缓存
                    promptCacheService.saveToCache(request, generatedPrompt);
                    
                    return generatedPrompt;
                }
            }
            
            throw new RuntimeException("API响应格式错误");
            
        } catch (Exception e) {
            logger.error("Error generating prompt", e);
            throw new RuntimeException("生成提示词时发生错误: " + e.getMessage(), e);
        }
    }
    
    /**
     * 流式生成提示词
     * @return Flux<String> 流式返回生成的文本片段
     */
    public Flux<String> generatePromptStream(PromptRequest request) {
        return Flux.defer(() -> {
            // 首先检查缓存
            String cachedPrompt = promptCacheService.getCachedPrompt(request);
            if (cachedPrompt != null) {
                logger.info("从缓存返回结果（流式），避免API调用");
                // 将缓存内容逐字流式返回，模拟打字机效果
                return Flux.fromArray(cachedPrompt.split(""))
                    .delayElements(Duration.ofMillis(10)) // 每个字符延迟10ms
                    .concatWith(Flux.just("[DONE]"));
            }
            
            logger.info("缓存未命中，调用API流式生成新的提示词");
            String systemPrompt = buildSystemPrompt(request);
            
            // 构建流式请求体
            Map<String, Object> requestBody = Map.of(
                "model", model,
                "messages", List.of(
                    Map.of("role", "user", "content", systemPrompt)
                ),
                "temperature", temperature,
                "max_tokens", maxTokens,
                "stream", true  // 启用流式输出
            );
            
            StringBuilder fullContent = new StringBuilder();
            
            return webClient.post()
                .uri(baseUrl + "/chat/completions")
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToFlux(String.class)  // 按行读取响应
                .flatMap(line -> {
                    logger.debug("收到流式数据行: {}", line);
                    
                    // 处理 SSE 格式的数据
                    if (line.startsWith("data: ")) {
                        String data = line.substring(6);
                        
                        // 流结束标记
                        if ("[DONE]".equals(data.trim())) {
                            // 保存完整内容到缓存
                            String completeContent = fullContent.toString();
                            if (!completeContent.isEmpty()) {
                                promptCacheService.saveToCache(request, completeContent);
                                logger.info("流式生成完成，已保存到缓存，长度: {}", completeContent.length());
                            }
                            return Flux.just("[DONE]");
                        }
                        
                        // 解析 JSON 数据
                        try {
                            Map<String, Object> chunk = objectMapper.readValue(data, Map.class);
                            if (chunk != null && chunk.containsKey("choices")) {
                                @SuppressWarnings("unchecked")
                                List<Map<String, Object>> choices = (List<Map<String, Object>>) chunk.get("choices");
                                if (!choices.isEmpty()) {
                                    @SuppressWarnings("unchecked")
                                    Map<String, Object> delta = (Map<String, Object>) choices.get(0).get("delta");
                                    if (delta != null && delta.containsKey("content")) {
                                        String content = (String) delta.get("content");
                                        if (content != null && !content.isEmpty()) {
                                            fullContent.append(content);
                                            return Flux.just(content);
                                        }
                                    }
                                }
                            }
                        } catch (Exception e) {
                            logger.debug("解析流式数据失败: {}, 错误: {}", data, e.getMessage());
                        }
                    }
                    return Flux.empty();
                })
                .onErrorResume(e -> {
                    logger.error("流式生成失败", e);
                    return Flux.error(new RuntimeException("流式生成失败: " + e.getMessage()));
                });
        });
    }
    
    private String buildSystemPrompt(PromptRequest request) {
        StringBuilder promptBuilder = new StringBuilder();
        
        // 引入系统架构思维的理论基础
        promptBuilder.append("# 系统架构思维提示词工程师\n");
        promptBuilder.append("你是一位精通系统架构思维的资深AI提示词工程师，拥有深厚的人工智能交互设计经验和系统工程背景。");
        promptBuilder.append("你深谙大语言模型的内在机制，擅长运用系统架构思维设计高效、精准、可维护的提示词系统。\n\n");
        
        // 核心设计理念
        promptBuilder.append("## 核心设计理念\n");
        promptBuilder.append("你遵循系统架构思维的四层设计框架：\n");
        promptBuilder.append("1. **核心定义层**：明确AI的身份、使命和价值观\n");
        promptBuilder.append("2. **交互接口层**：规范输入输出的数据契约\n");
        promptBuilder.append("3. **内部处理层**：设计模块化的能力和工作流程\n");
        promptBuilder.append("4. **全局约束层**：建立不可逾越的安全边界\n\n");
        
        // 任务目标
        promptBuilder.append("## 任务目标\n");
        promptBuilder.append("根据用户提供的需求信息，运用系统架构思维设计一个结构化、可执行的AI提示词系统。这个提示词必须：\n");
        promptBuilder.append("- **系统性**：具备清晰的架构层次和模块边界\n");
        promptBuilder.append("- **精准性**：准确传达用户的真实意图和约束条件\n");
        promptBuilder.append("- **稳定性**：具备良好的可重复性和抗干扰能力\n");
        promptBuilder.append("- **可维护性**：支持模块化修改和功能扩展\n\n");
        
        // 用户需求信息
        promptBuilder.append("# 用户需求信息\n");
        
        if (request.getTaskDescription() != null && !request.getTaskDescription().trim().isEmpty()) {
            promptBuilder.append("**核心任务：** ").append(request.getTaskDescription()).append("\n");
        }
        
        if (request.getTargetAudience() != null && !request.getTargetAudience().trim().isEmpty()) {
            promptBuilder.append("**目标用户：** ").append(request.getTargetAudience()).append("\n");
        }
        
        if (request.getOutputFormat() != null && !request.getOutputFormat().trim().isEmpty()) {
            promptBuilder.append("**期望格式：** ").append(request.getOutputFormat()).append("\n");
        }
        
        if (request.getConstraints() != null && !request.getConstraints().trim().isEmpty()) {
            promptBuilder.append("**限制条件：** ").append(request.getConstraints()).append("\n");
        }
        
        if (request.getExamples() != null && !request.getExamples().trim().isEmpty()) {
            promptBuilder.append("**参考案例：** ").append(request.getExamples()).append("\n");
        }
        
        if (request.getTone() != null && !request.getTone().trim().isEmpty()) {
            promptBuilder.append("**语言风格：** ").append(request.getTone()).append("\n");
        }
        
        if (request.getLength() != null && !request.getLength().trim().isEmpty()) {
            promptBuilder.append("**内容篇幅：** ").append(request.getLength()).append("\n");
        }
        
        // 系统架构思维设计要求
        promptBuilder.append("\n## 系统架构设计要求\n");
        promptBuilder.append("请严格按照系统架构思维的四层框架设计提示词系统：\n\n");
        
        promptBuilder.append("### 第一层：核心定义（必需）\n");
        promptBuilder.append("**1.1 角色建模**\n");
        promptBuilder.append("- **身份定义**：为AI定义具体的名称、来源和核心定位\n");
        promptBuilder.append("- **人格特征**：明确AI的交互风格、语调和态度\n");
        promptBuilder.append("- **价值立场**：在关键议题上的基本态度和原则\n\n");
        
        promptBuilder.append("**1.2 目标定义**\n");
        promptBuilder.append("- **功能性目标**：具体的、可执行的任务清单\n");
        promptBuilder.append("- **价值性目标**：为用户创造的核心价值\n");
        promptBuilder.append("- **质量标准**：可量化的效果指标和不可逾越的红线\n\n");
        
        promptBuilder.append("### 第二层：交互接口（必需）\n");
        promptBuilder.append("**2.1 输入规范**\n");
        promptBuilder.append("- **输入源识别**：使用标签明确不同来源的输入信息\n");
        promptBuilder.append("- **优先级定义**：规定不同输入源的处理优先级\n");
        promptBuilder.append("- **安全过滤**：设定输入处理的安全原则\n\n");
        
        promptBuilder.append("**2.2 输出规格**\n");
        promptBuilder.append("- **响应结构**：规划输出的宏观骨架和组织方式\n");
        promptBuilder.append("- **格式化规则**：详细定义所有输出元素的具体格式\n");
        promptBuilder.append("- **禁用项清单**：明确列出绝对不能出现的内容或格式\n\n");
        
        promptBuilder.append("### 第三层：内部处理（必需）\n");
        promptBuilder.append("**3.1 能力拆解**\n");
        promptBuilder.append("- **模块化设计**：将AI功能拆解为独立的技能模块\n");
        promptBuilder.append("- **单一职责**：每个模块只负责一件事情\n");
        promptBuilder.append("- **模块规则**：为每个模块定义专属的执行规则\n\n");
        
        promptBuilder.append("**3.2 流程设计**\n");
        promptBuilder.append("- **标准化步骤**：定义从输入到输出的固定阶段\n");
        promptBuilder.append("- **决策逻辑**：在关键节点明确判断依据和优先级\n");
        promptBuilder.append("- **示例引导**：提供端到端的执行示例\n\n");
        
        promptBuilder.append("### 第四层：全局约束（必需）\n");
        promptBuilder.append("**4.1 约束设定**\n");
        promptBuilder.append("- **硬性规则**：绝对不能违反的指令（使用MUST NEVER等强硬词汇）\n");
        promptBuilder.append("- **求助机制**：无法处理情况时的固定行为模式\n");
        promptBuilder.append("- **安全边界**：系统不可逾越的红线和护栏\n\n");
        
        // 六大编译原则
        promptBuilder.append("\n## 六大编译原则\n");
        promptBuilder.append("将系统架构编译为高效提示词时，必须遵循以下原则：\n\n");
        
        promptBuilder.append("**原则1：结构映射**\n");
        promptBuilder.append("- 将系统架构的层次结构直接映射到提示词的组织结构\n");
        promptBuilder.append("- 保持逻辑层次的清晰对应关系\n\n");
        
        promptBuilder.append("**原则2：模块化封装**\n");
        promptBuilder.append("- 每个功能模块在提示词中独立成段\n");
        promptBuilder.append("- 模块间通过明确的接口进行交互\n\n");
        
        promptBuilder.append("**原则3：策略性冗余**\n");
        promptBuilder.append("- 在关键指令处适度重复，确保AI理解\n");
        promptBuilder.append("- 用不同表述强化重要概念\n\n");
        
        promptBuilder.append("**原则4：示例驱动**\n");
        promptBuilder.append("- 为复杂行为提供具体的执行示例\n");
        promptBuilder.append("- 通过示例展示期望的思考模式\n\n");
        
        promptBuilder.append("**原则5：指令强度编码**\n");
        promptBuilder.append("- 使用MUST、NEVER等强硬词汇表达硬性约束\n");
        promptBuilder.append("- 用should、prefer等词汇表达软性建议\n\n");
        
        promptBuilder.append("**原则6：格式化契约**\n");
        promptBuilder.append("- 建立清晰的输入输出格式约定\n");
        promptBuilder.append("- 确保格式规范的一致性和可预测性\n\n");
        
        // 系统化输出要求
        promptBuilder.append("\n## 输出要求\n");
        promptBuilder.append("请按照系统架构思维输出完整的提示词系统，确保：\n\n");
        
        promptBuilder.append("**结构要求**\n");
        promptBuilder.append("- 严格按照四层架构组织内容（核心定义→交互接口→内部处理→全局约束）\n");
        promptBuilder.append("- 每层内容完整且逻辑清晰\n");
        promptBuilder.append("- 层次间关系明确，无重叠冲突\n\n");
        
        promptBuilder.append("**质量要求**\n");
        promptBuilder.append("- 遵循六大编译原则，确保系统性和稳定性\n");
        promptBuilder.append("- 语言精准，指令强度恰当\n");
        promptBuilder.append("- 包含必要的示例和约束\n\n");
        
        promptBuilder.append("**实用要求**\n");
        promptBuilder.append("- 生成的提示词可直接用于AI对话\n");
        promptBuilder.append("- 具备良好的可维护性和扩展性\n");
        promptBuilder.append("- 能够在不同场景下保持稳定表现\n\n");
        
        promptBuilder.append("**输出格式**\n");
        promptBuilder.append("请直接输出完整的提示词系统，无需额外解释或说明。\n");
        
        return promptBuilder.toString();
    }
}
