package com.promptflow.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {
    
    /**
     * 处理所有非API路径的请求，返回前端应用
     * 这样可以支持Vue Router的History模式
     */
    @GetMapping(value = {"/", "/{path:[^\\.]*}"})
    public String index() {
        return "forward:/index.html";
    }
}