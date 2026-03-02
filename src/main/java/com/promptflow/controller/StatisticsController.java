package com.promptflow.controller;

import com.promptflow.dto.ApiResponse;
import com.promptflow.dto.UsageStatistics;
import com.promptflow.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 统计相关接口
 */
@RestController
@RequestMapping("/api/statistics")
@CrossOrigin(origins = "*")
public class StatisticsController {
    
    @Autowired
    private StatisticsService statisticsService;
    
    /**
     * 获取完整的使用统计数据
     */
    @GetMapping
    public ApiResponse<UsageStatistics> getStatistics() {
        try {
            UsageStatistics stats = statisticsService.getUsageStatistics();
            return ApiResponse.success(stats);
        } catch (Exception e) {
            return ApiResponse.error("获取统计数据失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取简化的统计数据
     */
    @GetMapping("/simple")
    public ApiResponse<Map<String, Object>> getSimpleStats() {
        try {
            Map<String, Object> stats = statisticsService.getSimpleStats();
            return ApiResponse.success(stats);
        } catch (Exception e) {
            return ApiResponse.error("获取统计数据失败: " + e.getMessage());
        }
    }
}
