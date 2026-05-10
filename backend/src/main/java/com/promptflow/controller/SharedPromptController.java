package com.promptflow.controller;

import com.promptflow.dto.*;
import com.promptflow.service.SharedPromptService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/share")
@CrossOrigin
public class SharedPromptController {

    private final SharedPromptService service;

    public SharedPromptController(SharedPromptService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> publish(@RequestBody ShareRequest request) {
        PublishResponse response = service.publish(request);
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "发布成功");
        result.put("data", response);
        return ResponseEntity.ok(result);
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "latest") String sort) {

        Page<SharedPromptResponse> result;
        if ("popular".equals(sort)) {
            result = service.getTopLiked(page, size);
        } else {
            result = service.getAll(page, size);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "获取成功");
        response.put("data", result.getContent());
        response.put("total", result.getTotalElements());
        response.put("totalPages", result.getTotalPages());
        response.put("page", page);
        response.put("size", size);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getById(@PathVariable Long id) {
        return service.getById(id)
                .map(prompt -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("success", true);
                    response.put("message", "获取成功");
                    response.put("data", prompt);
                    return ResponseEntity.ok(response);
                })
                .orElseGet(() -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("success", false);
                    response.put("message", "不存在");
                    return ResponseEntity.ok(response);
                });
    }

    @GetMapping("/recent")
    public ResponseEntity<Map<String, Object>> recent(@RequestParam(defaultValue = "20") int limit) {
        List<SharedPromptResponse> list = service.getRecent(limit);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "获取成功");
        response.put("data", list);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/top-liked")
    public ResponseEntity<Map<String, Object>> topLiked(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<SharedPromptResponse> result = service.getTopLiked(page, size);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "获取成功");
        response.put("data", result.getContent());
        response.put("total", result.getTotalElements());
        response.put("totalPages", result.getTotalPages());
        response.put("page", page);
        response.put("size", size);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> delete(
            @PathVariable Long id,
            @RequestParam String token) {
        boolean deleted = service.delete(id, token);
        Map<String, Object> response = new HashMap<>();
        if (deleted) {
            response.put("success", true);
            response.put("message", "删除成功");
        } else {
            response.put("success", false);
            response.put("message", "删除失败，token无效或记录不存在");
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/like")
    public ResponseEntity<Map<String, Object>> like(@PathVariable Long id) {
        boolean success = service.like(id);
        Map<String, Object> response = new HashMap<>();
        if (success) {
            response.put("success", true);
            response.put("message", "点赞成功");
        } else {
            response.put("success", false);
            response.put("message", "点赞失败，请稍后再试");
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/unlike")
    public ResponseEntity<Map<String, Object>> unlike(@PathVariable Long id) {
        boolean success = service.unlike(id);
        Map<String, Object> response = new HashMap<>();
        if (success) {
            response.put("success", true);
            response.put("message", "取消点赞成功");
        } else {
            response.put("success", false);
            response.put("message", "取消点赞失败");
        }
        return ResponseEntity.ok(response);
    }
}