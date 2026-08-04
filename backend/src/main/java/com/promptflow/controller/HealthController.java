package com.promptflow.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 根路径健康检查
 * 微信云托管等平台的默认健康检查路径为 /，需返回 2xx 才能通过探活。
 * GET /  -> 200
 */
@RestController
public class HealthController {

    @GetMapping("/")
    public Map<String, Object> root() {
        return Map.of(
                "success", true,
                "service", "prompt-flow-craft",
                "message", "Prompt Flow Craft is running!"
        );
    }
}
