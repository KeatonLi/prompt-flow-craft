package com.promptflow.service;

import com.promptflow.dto.UsageStatistics;
import com.promptflow.entity.PromptResource;
import com.promptflow.entity.PromptCategory;
import com.promptflow.repository.PromptResourceRepository;
import com.promptflow.repository.PromptCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class StatisticsService {

    @Autowired
    private PromptResourceRepository promptResourceRepository;

    @Autowired
    private PromptCategoryRepository promptCategoryRepository;

    /**
     * 获取完整的使用统计数据
     */
    public UsageStatistics getUsageStatistics() {
        UsageStatistics stats = new UsageStatistics();

        // 基础统计
        stats.setTotalPrompts(promptResourceRepository.count());
        stats.setTodayCount(promptResourceRepository.countToday());
        stats.setWeekCount(promptResourceRepository.countThisWeek());
        stats.setMonthCount(promptResourceRepository.countThisMonth());

        // 总点赞数（处理null）
        Long totalLikes = promptResourceRepository.sumTotalLikes();
        stats.setTotalLikes(totalLikes != null ? totalLikes : 0L);
        stats.setTotalRatings(0L);

        // 平均评分
        Double avgRating = promptResourceRepository.getAverageRating();
        stats.setAverageRating(avgRating != null ? Math.round(avgRating * 10) / 10.0 : 0.0);

        // 缓存命中率
        Long totalHits = promptResourceRepository.sumTotalHits();
        long totalPrompts = promptResourceRepository.count();
        if (totalHits != null && totalHits > 0 && totalPrompts > 0) {
            double hitRate = (double) totalHits / (totalPrompts + totalHits) * 100;
            stats.setCacheHitRate(Math.round(hitRate * 10) / 10.0);
        } else {
            stats.setCacheHitRate(0.0);
        }

        // Tokens 统计
        Long totalInputTokens = promptResourceRepository.sumTotalInputTokens();
        Long totalOutputTokens = promptResourceRepository.sumTotalOutputTokens();
        stats.setTotalInputTokens(totalInputTokens != null ? totalInputTokens : 0L);
        stats.setTotalOutputTokens(totalOutputTokens != null ? totalOutputTokens : 0L);
        stats.setTotalTokens(stats.getTotalInputTokens() + stats.getTotalOutputTokens());

        // 分类统计
        stats.setCategoryStats(getCategoryStats());

        // 每日趋势（最近30天）
        stats.setDailyTrends(getDailyTrends(30));

        // 最热提示词（按点赞数）
        List<PromptResource> topPrompts = promptResourceRepository.findByLikeCountGreaterThanZeroOrderByLikeCountDesc(PageRequest.of(0, 10)).getContent();
        stats.setTopPrompts(topPrompts);

        // 最近活动
        List<Object[]> recent = promptResourceRepository.findRecentHistorySummary(PageRequest.of(0, 10));
        stats.setRecentActivities(convertToPromptResourceList(recent));

        return stats;
    }

    /**
     * 获取分类统计
     */
    private List<UsageStatistics.CategoryStat> getCategoryStats() {
        List<Object[]> results = promptResourceRepository.countByCategory();
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
        // 计算起始日期
        LocalDateTime startDate = LocalDateTime.now().minusDays(days);
        List<Object[]> results;

        try {
            // 尝试使用原生SQL查询
            results = promptResourceRepository.countByDayWithLikes(startDate);
        } catch (Exception e) {
            // 如果失败，使用JPQL查询
            try {
                results = promptResourceRepository.countByDayWithLikesJpql(startDate);
            } catch (Exception ex) {
                // 如果都失败，返回空数据
                results = new ArrayList<>();
            }
        }

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
            if (row[0] == null) continue;
            String dateStr = String.valueOf(row[0]);
            long count = ((Number) row[1]).longValue();
            long likes = row.length > 2 && row[2] != null ? ((Number) row[2]).longValue() : 0;

            // 处理日期格式 - 支持多种格式
            // 1. ISO格式: 2026-03-04T16:47:49.190167 -> 2026-03-04
            if (dateStr.contains("T")) {
                dateStr = dateStr.substring(0, dateStr.indexOf("T"));
            }
            // 2. 带时间格式: 2026-03-04 16:47:49 -> 2026-03-04
            else if (dateStr.contains(" ")) {
                dateStr = dateStr.substring(0, dateStr.indexOf(" "));
            }
            // 3. yyyyMMdd格式
            else if (dateStr.length() == 8 && !dateStr.contains("-")) {
                dateStr = dateStr.substring(0, 4) + "-" + dateStr.substring(4, 6) + "-" + dateStr.substring(6);
            }

            if (trendMap.containsKey(dateStr)) {
                trendMap.put(dateStr, new UsageStatistics.DailyTrend(dateStr, count, likes));
            }
        }

        return new ArrayList<>(trendMap.values());
    }

    /**
     * 将查询结果转换为PromptResource列表
     * 查询返回的列顺序: id, name, promptSummary, createdAt, hitCount, categoryId, likeCount, isAutoTagged, usageScenario, effectivenessScore, aiTags, promptType
     */
    private List<PromptResource> convertToPromptResourceList(List<Object[]> results) {
        return results.stream()
                .map(row -> {
                    PromptResource p = new PromptResource();
                    p.setId((Long) row[0]);
                    p.setName((String) row[1]);
                    p.setPromptSummary((String) row[2]);
                    if (row[3] != null) {
                        if (row[3] instanceof LocalDateTime) {
                            // 直接使用 LocalDateTime 对象
                            p.setCreatedAt((LocalDateTime) row[3]);
                        } else if (row[3] instanceof String) {
                            // 支持多种日期格式解析
                            String dateStr = (String) row[3];
                            try {
                                if (dateStr.contains("T")) {
                                    // ISO格式: 2026-03-04T16:47:49.190167
                                    p.setCreatedAt(LocalDateTime.parse(dateStr));
                                } else if (dateStr.contains(" ")) {
                                    // 标准格式: 2026-03-04 16:47:49
                                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                                    p.setCreatedAt(LocalDateTime.parse(dateStr, formatter));
                                } else {
                                    // 只有日期: 2026-03-04
                                    p.setCreatedAt(LocalDate.parse(dateStr).atStartOfDay());
                                }
                            } catch (Exception e) {
                                // 解析失败，使用当前时间
                                p.setCreatedAt(LocalDateTime.now());
                            }
                        }
                    }
                    p.setHitCount(row[4] != null ? ((Number) row[4]).intValue() : 0);
                    p.setCategoryId(row[5] != null ? ((Number) row[5]).longValue() : null);
                    p.setLikeCount(row[6] != null ? ((Number) row[6]).intValue() : 0);
                    p.setIsAutoTagged((Boolean) row[7]);
                    p.setUsageScenario((String) row[8]);
                    p.setEffectivenessScore(row[9] != null ? ((Number) row[9]).intValue() : null);
                    p.setAiTags((String) row[10]);
                    return p;
                })
                .collect(Collectors.toList());
    }

    /**
     * 获取简化的统计数据（用于首页展示）
     */
    public Map<String, Object> getSimpleStats() {
        Map<String, Object> stats = new HashMap<>();

        Long totalLikes = promptResourceRepository.sumTotalLikes();

        stats.put("totalPrompts", promptResourceRepository.count());
        stats.put("todayCount", 0L);
        stats.put("weekCount", 0L);
        stats.put("totalLikes", totalLikes != null ? totalLikes : 0L);

        return stats;
    }
}
