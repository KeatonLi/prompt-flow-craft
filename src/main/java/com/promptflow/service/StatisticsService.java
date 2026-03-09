package com.promptflow.service;

import com.promptflow.dto.UsageStatistics;
import com.promptflow.entity.PromptCache;
import com.promptflow.entity.PromptCategory;
import com.promptflow.repository.PromptCacheRepository;
import com.promptflow.repository.PromptCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class StatisticsService {
    
    @Autowired
    private PromptCacheRepository promptCacheRepository;
    
    @Autowired
    private PromptCategoryRepository promptCategoryRepository;
    
    /**
     * 获取完整的使用统计数据
     */
    public UsageStatistics getUsageStatistics() {
        UsageStatistics stats = new UsageStatistics();
        
        // 基础统计
        stats.setTotalPrompts(promptCacheRepository.count());
        stats.setTodayCount(0L);
        stats.setWeekCount(0L);
        stats.setMonthCount(0L);
        stats.setTotalLikes(promptCacheRepository.sumTotalLikes());
        stats.setTotalRatings(0L);
        
        // 平均评分
        Double avgRating = promptCacheRepository.getAverageRating();
        stats.setAverageRating(avgRating != null ? Math.round(avgRating * 10) / 10.0 : 0.0);
        
        // 缓存命中率
        long totalHits = promptCacheRepository.sumTotalHits();
        long totalPrompts = promptCacheRepository.count();
        if (totalPrompts > 0) {
            double hitRate = (double) totalHits / (totalPrompts + totalHits) * 100;
            stats.setCacheHitRate(Math.round(hitRate * 10) / 10.0);
        } else {
            stats.setCacheHitRate(0.0);
        }
        
        // 分类统计
        stats.setCategoryStats(getCategoryStats());
        
        // 每日趋势（最近30天）
        stats.setDailyTrends(getDailyTrends(30));
        
        // 最热提示词（按点赞数）
        List<PromptCache> topPrompts = promptCacheRepository.findByLikeCountGreaterThanZeroOrderByLikeCountDesc(PageRequest.of(0, 10)).getContent();
        stats.setTopPrompts(topPrompts);
        
        // 最近活动
        List<Object[]> recent = promptCacheRepository.findRecentHistorySummary(PageRequest.of(0, 10));
        stats.setRecentActivities(convertToPromptCacheList(recent));
        
        return stats;
    }
    
    /**
     * 获取分类统计
     */
    private List<UsageStatistics.CategoryStat> getCategoryStats() {
        List<Object[]> results = promptCacheRepository.countByCategory();
        Map<Long, Long> categoryCountMap = new HashMap<>();
        
        long total = 0;
        for (Object[] row : results) {
            Long categoryId = (Long) row[0];
            Long count = (Long) row[1];
            categoryCountMap.put(categoryId, count);
            total += count;
        }
        
        // 获取所有分类
        List<PromptCategory> categories = promptCategoryRepository.findAll();
        Map<Long, String> categoryNameMap = categories.stream()
                .collect(Collectors.toMap(PromptCategory::getId, PromptCategory::getName));
        
        List<UsageStatistics.CategoryStat> stats = new ArrayList<>();
        
        // 填充有数据的分类
        for (Map.Entry<Long, Long> entry : categoryCountMap.entrySet()) {
            String name = categoryNameMap.getOrDefault(entry.getKey(), "未分类");
            double percentage = total > 0 ? Math.round((double) entry.getValue() / total * 1000) / 10.0 : 0.0;
            stats.add(new UsageStatistics.CategoryStat(name, entry.getValue(), percentage));
        }
        
        // 如果没有数据，添加提示
        if (stats.isEmpty()) {
            stats.add(new UsageStatistics.CategoryStat("暂无数据", 0, 0.0));
        }
        
        // 按数量排序
        stats.sort((a, b) -> Long.compare(b.getCount(), a.getCount()));
        
        return stats;
    }
    
    /**
     * 获取每日趋势
     */
    private List<UsageStatistics.DailyTrend> getDailyTrends(int days) {
        List<Object[]> results = promptCacheRepository.countByDayWithLikes(days);
        
        // 补全缺失的日期
        LocalDate today = LocalDate.now();
        Map<String, UsageStatistics.DailyTrend> trendMap = new LinkedHashMap<>();
        
        // 初始化最近N天
        for (int i = days - 1; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            String dateStr = date.format(DateTimeFormatter.ISO_LOCAL_DATE);
            trendMap.put(dateStr, new UsageStatistics.DailyTrend(dateStr, 0, 0));
        }
        
        // 填充数据
        for (Object[] row : results) {
            String dateStr = String.valueOf(row[0]);
            long count = ((Number) row[1]).longValue();
            long likes = ((Number) row[2]).longValue();
            
            // 处理日期格式
            if (dateStr.length() == 8) { // yyyyMMdd
                dateStr = dateStr.substring(0, 4) + "-" + dateStr.substring(4, 6) + "-" + dateStr.substring(6);
            }
            
            if (trendMap.containsKey(dateStr)) {
                trendMap.put(dateStr, new UsageStatistics.DailyTrend(dateStr, count, likes));
            }
        }
        
        return new ArrayList<>(trendMap.values());
    }
    
    /**
     * 将查询结果转换为PromptCache列表
     */
    private List<PromptCache> convertToPromptCacheList(List<Object[]> results) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        
        return results.stream()
                .map(row -> {
                    PromptCache p = new PromptCache();
                    p.setId((Long) row[0]);
                    p.setTaskDescription((String) row[1]);
                    p.setTargetAudience((String) row[2]);
                    p.setPromptSummary((String) row[3]);
                    if (row[4] != null) {
                        p.setCreatedAt(LocalDate.parse(String.valueOf(row[4]), formatter).atStartOfDay());
                    }
                    p.setHitCount((Integer) row[5]);
                    p.setCategoryId((Long) row[6]);
                    p.setLikeCount((Integer) row[7]);
                    p.setIsAutoTagged((Boolean) row[8]);
                    p.setUsageScenario((String) row[9]);
                    p.setEffectivenessScore((Integer) row[10]);
                    p.setAiTags((String) row[11]);
                    return p;
                })
                .collect(Collectors.toList());
    }
    
    /**
     * 获取简化的统计数据（用于首页展示）
     */
    public Map<String, Object> getSimpleStats() {
        Map<String, Object> stats = new HashMap<>();
        
        stats.put("totalPrompts", promptCacheRepository.count());
        stats.put("todayCount", 0L);
        stats.put("weekCount", 0L);
        stats.put("totalLikes", promptCacheRepository.sumTotalLikes());
        
        return stats;
    }
}
