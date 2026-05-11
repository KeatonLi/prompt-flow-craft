package com.promptflow.dto;

import com.promptflow.entity.PromptResource;
import java.util.List;
import java.util.Map;

/**
 * 使用统计数据DTO
 */
public class UsageStatistics {
    
    /** 总提示词数 */
    private long totalPrompts;
    
    /** 今日生成数 */
    private long todayCount;
    
    /** 本周生成数 */
    private long weekCount;
    
    /** 本月生成数 */
    private long monthCount;
    
    /** 总点赞数 */
    private long totalLikes;
    
    /** 总评分次数 */
    private long totalRatings;
    
    /** 平均评分 */
    private double averageRating;
    
    /** 缓存命中率 */
    private double cacheHitRate;

    /** 总输入 Tokens */
    private long totalInputTokens;

    /** 总输出 Tokens */
    private long totalOutputTokens;

    /** 总 Tokens */
    private long totalTokens;

    /** 分类统计 */
    private List<CategoryStat> categoryStats;
    
    /** 每日趋势数据 */
    private List<DailyTrend> dailyTrends;
    
    /** 最热提示词 */
    private List<PromptResource> topPrompts;
    
    /** 最近活动 */
    private List<PromptResource> recentActivities;
    
    // Getters and Setters
    public long getTotalPrompts() {
        return totalPrompts;
    }
    
    public void setTotalPrompts(long totalPrompts) {
        this.totalPrompts = totalPrompts;
    }
    
    public long getTodayCount() {
        return todayCount;
    }
    
    public void setTodayCount(long todayCount) {
        this.todayCount = todayCount;
    }
    
    public long getWeekCount() {
        return weekCount;
    }
    
    public void setWeekCount(long weekCount) {
        this.weekCount = weekCount;
    }
    
    public long getMonthCount() {
        return monthCount;
    }
    
    public void setMonthCount(long monthCount) {
        this.monthCount = monthCount;
    }
    
    public long getTotalLikes() {
        return totalLikes;
    }
    
    public void setTotalLikes(long totalLikes) {
        this.totalLikes = totalLikes;
    }
    
    public long getTotalRatings() {
        return totalRatings;
    }
    
    public void setTotalRatings(long totalRatings) {
        this.totalRatings = totalRatings;
    }
    
    public double getAverageRating() {
        return averageRating;
    }
    
    public void setAverageRating(double averageRating) {
        this.averageRating = averageRating;
    }
    
    public double getCacheHitRate() {
        return cacheHitRate;
    }
    
    public void setCacheHitRate(double cacheHitRate) {
        this.cacheHitRate = cacheHitRate;
    }

    public long getTotalInputTokens() {
        return totalInputTokens;
    }

    public void setTotalInputTokens(long totalInputTokens) {
        this.totalInputTokens = totalInputTokens;
    }

    public long getTotalOutputTokens() {
        return totalOutputTokens;
    }

    public void setTotalOutputTokens(long totalOutputTokens) {
        this.totalOutputTokens = totalOutputTokens;
    }

    public long getTotalTokens() {
        return totalTokens;
    }

    public void setTotalTokens(long totalTokens) {
        this.totalTokens = totalTokens;
    }

    public List<CategoryStat> getCategoryStats() {
        return categoryStats;
    }
    
    public void setCategoryStats(List<CategoryStat> categoryStats) {
        this.categoryStats = categoryStats;
    }
    
    public List<DailyTrend> getDailyTrends() {
        return dailyTrends;
    }
    
    public void setDailyTrends(List<DailyTrend> dailyTrends) {
        this.dailyTrends = dailyTrends;
    }
    
    public List<PromptResource> getTopPrompts() {
        return topPrompts;
    }
    
    public void setTopPrompts(List<PromptResource> topPrompts) {
        this.topPrompts = topPrompts;
    }
    
    public List<PromptResource> getRecentActivities() {
        return recentActivities;
    }
    
    public void setRecentActivities(List<PromptResource> recentActivities) {
        this.recentActivities = recentActivities;
    }
    
    /**
     * 分类统计
     */
    public static class CategoryStat {
        private String categoryName;
        private long count;
        private double percentage;
        
        public CategoryStat() {}
        
        public CategoryStat(String categoryName, long count, double percentage) {
            this.categoryName = categoryName;
            this.count = count;
            this.percentage = percentage;
        }
        
        public String getCategoryName() {
            return categoryName;
        }
        
        public void setCategoryName(String categoryName) {
            this.categoryName = categoryName;
        }
        
        public long getCount() {
            return count;
        }
        
        public void setCount(long count) {
            this.count = count;
        }
        
        public double getPercentage() {
            return percentage;
        }
        
        public void setPercentage(double percentage) {
            this.percentage = percentage;
        }
    }
    
    /**
     * 每日趋势
     */
    public static class DailyTrend {
        private String date;
        private long count;
        private long likes;
        
        public DailyTrend() {}
        
        public DailyTrend(String date, long count, long likes) {
            this.date = date;
            this.count = count;
            this.likes = likes;
        }
        
        public String getDate() {
            return date;
        }
        
        public void setDate(String date) {
            this.date = date;
        }
        
        public long getCount() {
            return count;
        }
        
        public void setCount(long count) {
            this.count = count;
        }
        
        public long getLikes() {
            return likes;
        }
        
        public void setLikes(long likes) {
            this.likes = likes;
        }
    }
}
