<template>
  <div class="statistics-page">
    <div class="page-header">
      <h1>📊 使用统计分析</h1>
      <p class="subtitle">了解您的提示词创作趋势和使用情况</p>
    </div>

    <!-- 加载状态 -->
    <div v-if="loading" class="loading">
      <div class="spinner"></div>
      <p>加载统计数据中...</p>
    </div>

    <!-- 统计内容 -->
    <div v-else-if="stats" class="stats-content">
      
      <!-- 核心指标卡片 -->
      <div class="stats-cards">
        <div class="stat-card primary">
          <div class="stat-icon">📝</div>
          <div class="stat-info">
            <div class="stat-value">{{ formatNumber(stats.totalPrompts) }}</div>
            <div class="stat-label">总提示词数</div>
          </div>
        </div>
        
        <div class="stat-card success">
          <div class="stat-icon">✨</div>
          <div class="stat-info">
            <div class="stat-value">{{ formatNumber(stats.todayCount) }}</div>
            <div class="stat-label">今日新增</div>
          </div>
        </div>
        
        <div class="stat-card info">
          <div class="stat-icon">📅</div>
          <div class="stat-info">
            <div class="stat-value">{{ formatNumber(stats.weekCount) }}</div>
            <div class="stat-label">本周新增</div>
          </div>
        </div>
        
        <div class="stat-card warning">
          <div class="stat-icon">❤️</div>
          <div class="stat-info">
            <div class="stat-value">{{ formatNumber(stats.totalLikes) }}</div>
            <div class="stat-label">总点赞数</div>
          </div>
        </div>
      </div>

      <!-- 第二行指标 -->
      <div class="stats-cards secondary">
        <div class="stat-card">
          <div class="stat-icon">⭐</div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.averageRating.toFixed(1) }}</div>
            <div class="stat-label">平均评分</div>
          </div>
        </div>
        
        <div class="stat-card">
          <div class="stat-icon">💾</div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.cacheHitRate.toFixed(1) }}%</div>
            <div class="stat-label">缓存命中率</div>
          </div>
        </div>
        
        <div class="stat-card">
          <div class="stat-icon">📈</div>
          <div class="stat-info">
            <div class="stat-value">{{ formatNumber(stats.monthCount) }}</div>
            <div class="stat-label">本月新增</div>
          </div>
        </div>
        
        <div class="stat-card">
          <div class="stat-icon">💬</div>
          <div class="stat-info">
            <div class="stat-value">{{ formatNumber(stats.totalRatings) }}</div>
            <div class="stat-label">评分次数</div>
          </div>
        </div>
      </div>

      <!-- 趋势图表 -->
      <div class="chart-section">
        <h2>📈 30天趋势</h2>
        <div class="trend-chart">
          <div 
            v-for="day in stats.dailyTrends" 
            :key="day.date"
            class="chart-bar"
            :style="{ height: getBarHeight(day.count) + '%' }"
            :title="`${day.date}: ${day.count}条`"
          >
            <span class="bar-value">{{ day.count }}</span>
          </div>
        </div>
        <div class="chart-labels">
          <span v-for="(day, index) in stats.dailyTrends" :key="day.date">
            <span v-if="index % 5 === 0">{{ day.date.slice(5) }}</span>
          </span>
        </div>
      </div>

      <!-- 分类统计 -->
      <div class="category-section">
        <h2>🏷️ 分类分布</h2>
        <div class="category-list">
          <div 
            v-for="cat in stats.categoryStats" 
            :key="cat.categoryName"
            class="category-item"
          >
            <div class="category-info">
              <span class="category-name">{{ cat.categoryName }}</span>
              <span class="category-count">{{ cat.count }} 条</span>
            </div>
            <div class="category-bar">
              <div 
                class="category-bar-fill" 
                :style="{ width: cat.percentage + '%' }"
              ></div>
            </div>
            <span class="category-percentage">{{ cat.percentage }}%</span>
          </div>
        </div>
      </div>

      <!-- 热门提示词 -->
      <div class="top-prompts-section">
        <h2>🔥 热门提示词 Top 10</h2>
        <div class="top-prompts-list">
          <div 
            v-for="(prompt, index) in stats.topPrompts" 
            :key="prompt.id"
            class="top-prompt-item"
          >
            <div class="prompt-rank">{{ index + 1 }}</div>
            <div class="prompt-content">
              <div class="prompt-title">{{ prompt.taskDescription || '未命名' }}</div>
              <div class="prompt-meta">
                <span>❤️ {{ prompt.likeCount }} 赞</span>
                <span v-if="prompt.effectivenessScore">⭐ {{ prompt.effectivenessScore }}分</span>
                <span v-if="prompt.averageRating">📊 {{ prompt.averageRating.toFixed(1) }}分</span>
              </div>
            </div>
          </div>
          <div v-if="!stats.topPrompts || stats.topPrompts.length === 0" class="empty-tip">
            暂无热门提示词，快去创作吧！ 🚀
          </div>
        </div>
      </div>

      <!-- 最近活动 -->
      <div class="recent-section">
        <h2>🕐 最近活动</h2>
        <div class="recent-list">
          <div 
            v-for="prompt in stats.recentActivities" 
            :key="prompt.id"
            class="recent-item"
          >
            <div class="recent-icon">📝</div>
            <div class="recent-content">
              <div class="recent-title">{{ prompt.taskDescription || '未命名' }}</div>
              <div class="recent-time">{{ formatTime(prompt.createdAt) }}</div>
            </div>
          </div>
          <div v-if="!stats.recentActivities || stats.recentActivities.length === 0" class="empty-tip">
            暂无最近活动
          </div>
        </div>
      </div>

    </div>

    <!-- 错误提示 -->
    <div v-else class="error-tip">
      <p>加载失败，请稍后重试 😢</p>
      <button @click="loadStats" class="retry-btn">重试</button>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import axios from 'axios'

export default {
  name: 'StatisticsPage',
  setup() {
    const loading = ref(true)
    const stats = ref(null)
    const maxDailyCount = ref(1)

    const loadStats = async () => {
      loading.value = true
      try {
        const response = await axios.get('/api/statistics')
        if (response.data.code === 200) {
          stats.value = response.data.data
          // 计算最大每日数量用于图表
          if (stats.value.dailyTrends) {
            maxDailyCount.value = Math.max(...stats.value.dailyTrends.map(d => d.count), 1)
          }
        }
      } catch (error) {
        console.error('Failed to load statistics:', error)
      } finally {
        loading.value = false
      }
    }

    const formatNumber = (num) => {
      if (num >= 10000) {
        return (num / 10000).toFixed(1) + 'w'
      }
      return num.toString()
    }

    const formatTime = (timeStr) => {
      if (!timeStr) return ''
      const date = new Date(timeStr)
      const now = new Date()
      const diff = now - date
      
      if (diff < 60000) return '刚刚'
      if (diff < 3600000) return Math.floor(diff / 60000) + '分钟前'
      if (diff < 86400000) return Math.floor(diff / 3600000) + '小时前'
      if (diff < 604800000) return Math.floor(diff / 86400000) + '天前'
      
      return timeStr.split('T')[0] || timeStr.substring(0, 10)
    }

    const getBarHeight = (count) => {
      if (maxDailyCount.value === 0) return 0
      return Math.max((count / maxDailyCount.value) * 100, 5)
    }

    onMounted(() => {
      loadStats()
    })

    return {
      loading,
      stats,
      formatNumber,
      formatTime,
      getBarHeight,
      loadStats
    }
  }
}
</script>

<style scoped>
.statistics-page {
  max-width: 1200px;
  margin: 0 auto;
  padding: 24px;
}

.page-header {
  margin-bottom: 32px;
}

.page-header h1 {
  font-size: 28px;
  color: #1a1a2e;
  margin-bottom: 8px;
}

.subtitle {
  color: #666;
  font-size: 14px;
}

.loading {
  text-align: center;
  padding: 60px;
  color: #666;
}

.spinner {
  width: 40px;
  height: 40px;
  margin: 0 auto 16px;
  border: 3px solid #f3f3f3;
  border-top: 3px solid #4FC08D;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.stats-cards {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  margin-bottom: 16px;
}

.stat-card {
  background: white;
  border-radius: 12px;
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  transition: transform 0.2s, box-shadow 0.2s;
}

.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
}

.stat-card.primary { border-left: 4px solid #4FC08D; }
.stat-card.success { border-left: 4px solid #52c41a; }
.stat-card.info { border-left: 4px solid #1890ff; }
.stat-card.warning { border-left: 4px solid #faad14; }

.stat-icon {
  font-size: 32px;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #1a1a2e;
}

.stat-label {
  font-size: 13px;
  color: #888;
}

.chart-section, .category-section, .top-prompts-section, .recent-section {
  background: white;
  border-radius: 12px;
  padding: 24px;
  margin-top: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.chart-section h2, .category-section h2, .top-prompts-section h2, .recent-section h2 {
  font-size: 18px;
  margin-bottom: 20px;
  color: #1a1a2e;
}

.trend-chart {
  display: flex;
  align-items: flex-end;
  height: 150px;
  gap: 4px;
  padding: 10px 0;
}

.chart-bar {
  flex: 1;
  background: linear-gradient(180deg, #4FC08D 0%, #36a359 100%);
  border-radius: 4px 4px 0 0;
  min-height: 4px;
  position: relative;
  cursor: pointer;
  transition: opacity 0.2s;
}

.chart-bar:hover {
  opacity: 0.8;
}

.bar-value {
  position: absolute;
  top: -20px;
  left: 50%;
  transform: translateX(-50%);
  font-size: 10px;
  color: #666;
}

.chart-labels {
  display: flex;
  justify-content: space-between;
  font-size: 11px;
  color: #999;
  margin-top: 8px;
}

.category-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.category-item {
  display: flex;
  align-items: center;
  gap: 12px;
}

.category-info {
  width: 150px;
  display: flex;
  justify-content: space-between;
  font-size: 13px;
}

.category-name {
  font-weight: 500;
  color: #333;
}

.category-count {
  color: #888;
}

.category-bar {
  flex: 1;
  height: 8px;
  background: #f0f0f0;
  border-radius: 4px;
  overflow: hidden;
}

.category-bar-fill {
  height: 100%;
  background: linear-gradient(90deg, #4FC08D, #36a359);
  border-radius: 4px;
  transition: width 0.5s ease;
}

.category-percentage {
  width: 50px;
  text-align: right;
  font-size: 13px;
  color: #666;
}

.top-prompts-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.top-prompt-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 12px;
  background: #f9f9f9;
  border-radius: 8px;
}

.prompt-rank {
  width: 28px;
  height: 28px;
  background: linear-gradient(135deg, #4FC08D, #36a359);
  color: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  font-size: 14px;
}

.prompt-title {
  font-size: 14px;
  color: #333;
  margin-bottom: 4px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: 500px;
}

.prompt-meta {
  display: flex;
  gap: 16px;
  font-size: 12px;
  color: #888;
}

.recent-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.recent-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  background: #f9f9f9;
  border-radius: 8px;
}

.recent-icon {
  font-size: 20px;
}

.recent-title {
  font-size: 14px;
  color: #333;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: 500px;
}

.recent-time {
  font-size: 12px;
  color: #888;
  margin-top: 4px;
}

.empty-tip {
  text-align: center;
  padding: 40px;
  color: #999;
  font-size: 14px;
}

.error-tip {
  text-align: center;
  padding: 60px;
  color: #666;
}

.retry-btn {
  margin-top: 16px;
  padding: 10px 24px;
  background: #4FC08D;
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-size: 14px;
}

.retry-btn:hover {
  background: #36a359;
}

@media (max-width: 768px) {
  .stats-cards {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .stat-value {
    font-size: 22px;
  }
}
</style>
