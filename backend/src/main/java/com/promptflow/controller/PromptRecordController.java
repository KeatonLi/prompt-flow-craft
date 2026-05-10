package com.promptflow.controller;

import com.promptflow.dto.ApiResponse;
import com.promptflow.dto.PagedResult;
import com.promptflow.entity.AgentPrompt;
import com.promptflow.entity.SkillPrompt;
import com.promptflow.service.PromptRecordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/records")
@CrossOrigin
public class PromptRecordController {

    private static final Logger logger = LoggerFactory.getLogger(PromptRecordController.class);

    @Autowired
    private PromptRecordService promptRecordService;

    // ==================== Agent Records ====================

    /**
     * 获取所有 Agent 记录
     */
    @GetMapping("/agents")
    public ResponseEntity<Map<String, Object>> getAllAgents() {
        try {
            List<AgentPrompt> agents = promptRecordService.getAllAgents();
            List<Map<String, Object>> data = agents.stream()
                .map(this::convertAgentToMap)
                .collect(Collectors.toList());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", data);
            response.put("total", data.size());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("获取Agent记录失败", e);
            return errorResponse(e.getMessage());
        }
    }

    /**
     * 获取最近的 Agent 记录
     */
    @GetMapping("/agents/recent")
    public ResponseEntity<Map<String, Object>> getRecentAgents(
            @RequestParam(value = "limit", defaultValue = "20") int limit) {
        try {
            List<AgentPrompt> agents = promptRecordService.getRecentAgents(limit);
            List<Map<String, Object>> data = agents.stream()
                .map(this::convertAgentToMap)
                .collect(Collectors.toList());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", data);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("获取最近Agent记录失败", e);
            return errorResponse(e.getMessage());
        }
    }

    /**
     * 获取单个 Agent 记录
     */
    @GetMapping("/agents/{id}")
    public ResponseEntity<Map<String, Object>> getAgentById(@PathVariable("id") Long id) {
        try {
            return promptRecordService.getAgentById(id)
                .map(agent -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("success", true);
                    response.put("data", convertAgentToMap(agent));
                    return ResponseEntity.ok(response);
                })
                .orElse(notFoundResponse());
        } catch (Exception e) {
            logger.error("获取Agent记录详情失败", e);
            return errorResponse(e.getMessage());
        }
    }

    /**
     * 分页获取 Agent 记录
     */
    @GetMapping("/agents/page")
    public ResponseEntity<Map<String, Object>> getAgentPage(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "20") int size,
            @RequestParam(value = "sortBy", defaultValue = "createdAt") String sortBy) {
        try {
            Pageable pageable = PageRequest.of(page - 1, size);
            Page<AgentPrompt> agentPage = "likeCount".equals(sortBy)
                ? promptRecordService.getAgentPageByLikeCount(pageable)
                : promptRecordService.getAgentPage(pageable);

            List<Map<String, Object>> data = agentPage.getContent().stream()
                .map(this::convertAgentToMap)
                .collect(Collectors.toList());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", data);
            response.put("total", agentPage.getTotalElements());
            response.put("totalPages", agentPage.getTotalPages());
            response.put("page", page);
            response.put("size", size);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("分页获取Agent记录失败", e);
            return errorResponse(e.getMessage());
        }
    }

    /**
     * 删除 Agent 记录
     */
    @DeleteMapping("/agents/{id}")
    public ResponseEntity<Map<String, Object>> deleteAgent(@PathVariable("id") Long id) {
        try {
            boolean success = promptRecordService.deleteAgent(id);
            Map<String, Object> response = new HashMap<>();
            response.put("success", success);
            response.put("message", success ? "删除成功" : "记录不存在");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("删除Agent记录失败", e);
            return errorResponse(e.getMessage());
        }
    }

    // ==================== Skill Records ====================

    /**
     * 获取所有 Skill 记录
     */
    @GetMapping("/skills")
    public ResponseEntity<Map<String, Object>> getAllSkills() {
        try {
            List<SkillPrompt> skills = promptRecordService.getAllSkills();
            List<Map<String, Object>> data = skills.stream()
                .map(this::convertSkillToMap)
                .collect(Collectors.toList());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", data);
            response.put("total", data.size());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("获取Skill记录失败", e);
            return errorResponse(e.getMessage());
        }
    }

    /**
     * 获取最近的 Skill 记录
     */
    @GetMapping("/skills/recent")
    public ResponseEntity<Map<String, Object>> getRecentSkills(
            @RequestParam(value = "limit", defaultValue = "20") int limit) {
        try {
            List<SkillPrompt> skills = promptRecordService.getRecentSkills(limit);
            List<Map<String, Object>> data = skills.stream()
                .map(this::convertSkillToMap)
                .collect(Collectors.toList());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", data);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("获取最近Skill记录失败", e);
            return errorResponse(e.getMessage());
        }
    }

    /**
     * 获取单个 Skill 记录
     */
    @GetMapping("/skills/{id}")
    public ResponseEntity<Map<String, Object>> getSkillById(@PathVariable("id") Long id) {
        try {
            return promptRecordService.getSkillById(id)
                .map(skill -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("success", true);
                    response.put("data", convertSkillToMap(skill));
                    return ResponseEntity.ok(response);
                })
                .orElse(notFoundResponse());
        } catch (Exception e) {
            logger.error("获取Skill记录详情失败", e);
            return errorResponse(e.getMessage());
        }
    }

    /**
     * 分页获取 Skill 记录
     */
    @GetMapping("/skills/page")
    public ResponseEntity<Map<String, Object>> getSkillPage(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "20") int size,
            @RequestParam(value = "sortBy", defaultValue = "createdAt") String sortBy) {
        try {
            Pageable pageable = PageRequest.of(page - 1, size);
            Page<SkillPrompt> skillPage = "likeCount".equals(sortBy)
                ? promptRecordService.getSkillPageByLikeCount(pageable)
                : promptRecordService.getSkillPage(pageable);

            List<Map<String, Object>> data = skillPage.getContent().stream()
                .map(this::convertSkillToMap)
                .collect(Collectors.toList());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", data);
            response.put("total", skillPage.getTotalElements());
            response.put("totalPages", skillPage.getTotalPages());
            response.put("page", page);
            response.put("size", size);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("分页获取Skill记录失败", e);
            return errorResponse(e.getMessage());
        }
    }

    /**
     * 删除 Skill 记录
     */
    @DeleteMapping("/skills/{id}")
    public ResponseEntity<Map<String, Object>> deleteSkill(@PathVariable("id") Long id) {
        try {
            boolean success = promptRecordService.deleteSkill(id);
            Map<String, Object> response = new HashMap<>();
            response.put("success", success);
            response.put("message", success ? "删除成功" : "记录不存在");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("删除Skill记录失败", e);
            return errorResponse(e.getMessage());
        }
    }

    // ==================== Helper Methods ====================

    private Map<String, Object> convertAgentToMap(AgentPrompt agent) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", agent.getId());
        map.put("name", agent.getName());
        map.put("roleDescription", agent.getRoleDescription());
        map.put("capabilities", agent.getCapabilities());
        map.put("behaviors", agent.getBehaviors());
        map.put("communicationStyle", agent.getCommunicationStyle());
        map.put("generatedPrompt", agent.getGeneratedPrompt());
        map.put("authorId", agent.getAuthorId());
        map.put("authorNickname", agent.getAuthorNickname());
        map.put("likeCount", agent.getLikeCount());
        map.put("viewCount", agent.getViewCount());
        map.put("createdAt", agent.getCreatedAt());
        map.put("updatedAt", agent.getUpdatedAt());
        // For compatibility with frontend
        map.put("taskDescription", agent.getRoleDescription());
        map.put("promptSummary", agent.getRoleDescription());
        return map;
    }

    private Map<String, Object> convertSkillToMap(SkillPrompt skill) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", skill.getId());
        map.put("name", skill.getName());
        map.put("description", skill.getDescription());
        map.put("skillType", skill.getSkillType() != null ? skill.getSkillType().name() : null);
        map.put("method", skill.getMethod());
        map.put("endpoint", skill.getEndpoint());
        map.put("parameters", skill.getParameters());
        map.put("outputDescription", skill.getOutputDescription());
        map.put("generatedPrompt", skill.getGeneratedPrompt());
        map.put("authorId", skill.getAuthorId());
        map.put("authorNickname", skill.getAuthorNickname());
        map.put("likeCount", skill.getLikeCount());
        map.put("viewCount", skill.getViewCount());
        map.put("createdAt", skill.getCreatedAt());
        map.put("updatedAt", skill.getUpdatedAt());
        return map;
    }

    private ResponseEntity<Map<String, Object>> errorResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", message);
        return ResponseEntity.internalServerError().body(response);
    }

    private ResponseEntity<Map<String, Object>> notFoundResponse() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", "记录不存在");
        return ResponseEntity.notFound().build();
    }
}