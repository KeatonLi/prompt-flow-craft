<template>
  <AppLayout>
    <template #main>
      <div class="statistics-page">

        <!-- Page Header -->
        <div class="page-header">
          <div class="header-left">
            <h1 class="page-title">
              <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M18 20V10M12 20V4M6 20v-6"/>
              </svg>
              数据统计
            </h1>
            <p class="page-subtitle">了解您的提示词创作趋势和使用情况</p>
          </div>
          <button class="refresh-btn" @click="loadStats" :disabled="loading">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" :class="{ spinning: loading }">
              <path d="M3 12a9 9 0 1 0 9-9 9.75 9.75 0 0 0-6.74 2.74L3 8"/>
              <path d="M3 3v5h5"/>
            </svg>
            {{ loading ? '加载中' : '刷新数据' }}
          </button>
        </div>

        <!-- Loading State -->
        <div v-if="loading" class="loading-state">
          <div class="loading-spinner"></div>
          <p>正在加载统计数据...</p>
        </div>

        <!-- Stats Content -->
        <div v-else-if="stats" class="stats-content">

          <!-- Section 1: Core Metrics -->
          <div class="section">
            <div class="section-title">
              <span class="title-dot"></span>
              核心指标
            </div>
            <div class="metrics-grid">
              <div class="metric-card primary">
                <div class="metric-icon">
                  <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <path d="M14.5 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V7.5L14.5 2z"/>
                    <polyline points="14 2 14 8 20 8"/>
                  </svg>
                </div>
                <div class="metric-body">
                  <span class="metric-label">总提示词</span>
                  <span class="metric-value">{{ formatNumber(stats.totalPrompts) }}</span>
                </div>
              </div>

              <div class="metric-card">
                <div class="metric-icon green">
                  <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <rect x="3" y="4" width="18" height="18" rx="2"/>
                    <line x1="16" y1="2" x2="16" y2="6"/>
                    <line x1="8" y1="2" x2="8" y2="6"/>
                    <line x1="3" y1="10" x2="21" y2="10"/>
                  </svg>
                </div>
                <div class="metric-body">
                  <span class="metric-label">今日新增</span>
                  <span class="metric-value green">{{ formatNumber(stats.todayCount) }}</span>
                </div>
              </div>

              <div class="metric-card">
                <div class="metric-icon blue">
                  <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <rect x="3" y="4" width="18" height="18" rx="2"/>
                    <line x1="3" y1="10" x2="21" y2="10"/>
                  </svg>
                </div>
                <div class="metric-body">
                  <span class="metric-label">本周新增</span>
                  <span class="metric-value">{{ formatNumber(stats.weekCount) }}</span>
                </div>
              </div>

              <div class="metric-card">
                <div class="metric-icon purple">
                  <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <rect x="3" y="4" width="18" height="18" rx="2"/>
                    <line x1="3" y1="10" x2="21" y2="10"/>
                  </svg>
                </div>
                <div class="metric-body">
                  <span class="metric-label">本月新增</span>
                  <span class="metric-value">{{ formatNumber(stats.monthCount) }}</span>
                </div>
              </div>
            </div>
          </div>

          <!-- Section 2: Quality & Engagement -->
          <div class="section">
            <div class="section-title">
              <span class="title-dot"></span>
              互动质量
            </div>
            <div class="metrics-grid four-col">
              <div class="metric-card">
                <div class="metric-icon pink">
                  <svg width="20" height="20" viewBox="0 0 24 24" fill="currentColor">
                    <path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z"/>
                  </svg>
                </div>
                <div class="metric-body">
                  <span class="metric-label">总点赞</span>
                  <span class="metric-value">{{ formatNumber(stats.totalLikes) }}</span>
                </div>
              </div>

              <div class="metric-card">
                <div class="metric-icon yellow">
                  <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"/>
                  </svg>
                </div>
                <div class="metric-body">
                  <span class="metric-label">平均评分</span>
                  <span class="metric-value">{{ (stats.averageRating || 0).toFixed(1) }}<span class="metric-unit">/5</span></span>
                </div>
              </div>

              <div class="metric-card">
                <div class="metric-icon cyan">
                  <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"/>
                    <polyline points="22 4 12 14.01 9 11.01"/>
                  </svg>
                </div>
                <div class="metric-body">
                  <span class="metric-label">缓存命中</span>
                  <span class="metric-value cyan">{{ (stats.cacheHitRate || 0).toFixed(1) }}%</span>
                </div>
              </div>

              <div class="metric-card">
                <div class="metric-icon orange">
                  <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <circle cx="12" cy="12" r="10"/>
                    <polyline points="12 6 12 12 16 14"/>
                  </svg>
                </div>
                <div class="metric-body">
                  <span class="metric-label">评分次数</span>
                  <span class="metric-value">{{ formatNumber(stats.totalRatings) }}</span>
                </div>
              </div>
            </div>
          </div>

          <!-- Section 3: Trend Chart -->
          <div class="section">
            <div class="section-title">
              <span class="title-dot"></span>
              30天趋势
            </div>
            <div class="trend-card">
              <div class="trend-header">
                <div class="trend-legend">
                  <span class="legend-item">
                    <span class="legend-dot primary"></span>
                    生成数
                  </span>
                  <span class="legend-item">
                    <span class="legend-dot pink"></span>
                    点赞数
                  </span>
                </div>
                <span class="trend-total">共 {{ getTotalGenerated() }} 条</span>
              </div>
              <div class="trend-chart">
                <div class="chart-bars">
                  <div
                    v-for="day in stats.dailyTrends"
                    :key="day.date"
                    class="bar-group"
                    :title="`${day.date}: ${day.count}条生成, ${day.likes}个赞`"
                  >
                    <div class="bar-stack">
                      <div
                        class="chart-bar likes"
                        :style="{ height: getLikesHeight(day.likes) + 'px' }"
                      ></div>
                      <div
                        class="chart-bar counts"
                        :style="{ height: getCountHeight(day.count) + 'px' }"
                      ></div>
                    </div>
                  </div>
                </div>
                <div class="chart-labels">
                  <span v-for="(day, idx) in stats.dailyTrends" :key="day.date">
                    <span v-if="idx % 7 === 0" class="chart-label">{{ day.date.slice(5) }}</span>
                  </span>
                </div>
              </div>
            </div>
          </div>

          <!-- Section 4: Category & Rating Distribution -->
          <div class="section two-col">
            <!-- Category -->
            <div class="dist-card">
              <div class="dist-header">
                <h3>分类分布</h3>
              </div>
              <div class="dist-body">
                <div
                  v-for="cat in stats.categoryStats"
                  :key="cat.categoryName"
                  class="dist-item"
                >
                  <div class="dist-info">
                    <span class="dist-name">{{ cat.categoryName }}</span>
                    <span class="dist-count">{{ cat.count }} 条</span>
                  </div>
                  <div class="dist-bar">
                    <div
                      class="dist-bar-fill"
                      :style="{ width: cat.percentage + '%' }"
                    ></div>
                  </div>
                  <span class="dist-percent">{{ cat.percentage }}%</span>
                </div>
              </div>
            </div>

            <!-- Lists -->
            <div class="lists-card">
              <div class="lists-header">
                <h3>热门提示词 Top 10</h3>
              </div>
              <div class="lists-body">
                <div
                  v-for="(prompt, index) in stats.topPrompts"
                  :key="prompt.id"
                  class="list-item"
                >
                  <div class="item-rank" :class="getRankClass(index)">{{ index + 1 }}</div>
                  <div class="item-content">
                    <span class="item-title">{{ prompt.taskDescription || '未命名' }}</span>
                    <div class="item-meta">
                      <span class="meta-tag likes">
                        <svg width="10" height="10" viewBox="0 0 24 24" fill="currentColor">
                          <path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z"/>
                        </svg>
                        {{ prompt.likeCount || 0 }}
                      </span>
                      <span v-if="prompt.effectivenessScore" class="meta-tag score">
                        <svg width="10" height="10" viewBox="0 0 24 24" fill="currentColor">
                          <polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"/>
                        </svg>
                        {{ prompt.effectivenessScore }}
                      </span>
                    </div>
                  </div>
                </div>
                <div v-if="!stats.topPrompts?.length" class="empty-state">
                  暂无热门提示词
                </div>
              </div>
            </div>
          </div>

          <!-- Section 5: Token Stats & Recent Activity -->
          <div class="section two-col">
            <!-- Token Stats -->
            <div class="token-card">
              <div class="token-header">
                <h3>Token 消耗</h3>
              </div>
              <div class="token-body">
                <div class="token-item">
                  <div class="token-info">
                    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                      <polyline points="22 12 18 12 15 21 9 3 6 12 2 12"/>
                    </svg>
                    <span>输入 Tokens</span>
                  </div>
                  <span class="token-value">{{ formatTokens(stats.totalInputTokens) }}</span>
                </div>
                <div class="token-item">
                  <div class="token-info">
                    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                      <polyline points="22 12 18 12 15 21 9 3 6 12 2 12"/>
                    </svg>
                    <span>输出 Tokens</span>
                  </div>
                  <span class="token-value">{{ formatTokens(stats.totalOutputTokens) }}</span>
                </div>
                <div class="token-item total">
                  <div class="token-info">
                    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                      <circle cx="12" cy="12" r="10"/>
                      <path d="M12 6v6l4 2"/>
                    </svg>
                    <span>总计</span>
                  </div>
                  <span class="token-value primary">{{ formatTokens(stats.totalTokens) }}</span>
                </div>
              </div>
            </div>

            <!-- Recent Activity -->
            <div class="activity-card">
              <div class="activity-header">
                <h3>最近活动</h3>
              </div>
              <div class="activity-body">
                <div
                  v-for="prompt in stats.recentActivities"
                  :key="prompt.id"
                  class="activity-item"
                >
                  <div class="activity-icon">
                    <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                      <path d="M14.5 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V7.5L14.5 2z"/>
                    </svg>
                  </div>
                  <div class="activity-content">
                    <span class="activity-title">{{ prompt.taskDescription || '未命名' }}</span>
                    <span class="activity-time">{{ formatTime(prompt.createdAt) }}</span>
                  </div>
                </div>
                <div v-if="!stats.recentActivities?.length" class="empty-state">
                  暂无最近活动
                </div>
              </div>
            </div>
          </div>

        </div>

        <!-- Error State -->
        <div v-else class="error-state">
          <p>数据加载失败</p>
          <button class="retry-btn" @click="loadStats">重试</button>
        </div>
      </div>
    </template>
  </AppLayout>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import AppLayout from '@/components/layout/AppLayout.vue'

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || ''

const loading = ref(true)
const stats = ref(null)
const maxCount = ref(1)
const maxLikes = ref(1)

const loadStats = async () => {
  loading.value = true
  stats.value = null
  try {
    const url = API_BASE_URL ? `${API_BASE_URL}/statistics` : '/api/statistics'
    const response = await axios.get(url, { timeout: 10000 })
    if (response.data?.success) {
      stats.value = response.data.data
      if (stats.value.dailyTrends?.length > 0) {
        maxCount.value = Math.max(...stats.value.dailyTrends.map(d => d.count || 0), 1)
        maxLikes.value = Math.max(...stats.value.dailyTrends.map(d => d.likes || 0), 1)
      }
    }
  } catch (error) {
    console.error('Failed to load statistics:', error)
  } finally {
    loading.value = false
  }
}

const getTotalGenerated = () => {
  if (!stats.value?.dailyTrends) return 0
  return stats.value.dailyTrends.reduce((sum, d) => sum + (d.count || 0), 0)
}

const formatNumber = (num) => {
  if (num == null) return '0'
  if (num >= 10000) return (num / 10000).toFixed(1) + 'w'
  return num.toLocaleString()
}

const formatTokens = (num) => {
  if (num == null) return '0'
  if (num >= 1000000) return (num / 1000000).toFixed(1) + 'M'
  if (num >= 1000) return (num / 1000).toFixed(1) + 'K'
  return num.toLocaleString()
}

const formatTime = (timeStr) => {
  if (!timeStr) return ''
  try {
    const date = new Date(timeStr)
    const now = new Date()
    const diff = now - date
    if (diff < 60000) return '刚刚'
    if (diff < 3600000) return Math.floor(diff / 60000) + '分钟前'
    if (diff < 86400000) return Math.floor(diff / 3600000) + '小时前'
    if (diff < 604800000) return Math.floor(diff / 86400000) + '天前'
    return timeStr.split('T')[0] || timeStr.substring(0, 10)
  } catch {
    return timeStr
  }
}

const getCountHeight = (count) => {
  if (!count) return 2
  return Math.max(Math.round((count / maxCount.value) * 100), 2)
}

const getLikesHeight = (likes) => {
  if (!likes) return 2
  return Math.max(Math.round((likes / maxLikes.value) * 60), 2)
}

const getRankClass = (index) => {
  if (index === 0) return 'gold'
  if (index === 1) return 'silver'
  if (index === 2) return 'bronze'
  return ''
}

onMounted(() => {
  loadStats()
})
</script>

<style scoped>
.statistics-page {
  display: flex;
  flex-direction: column;
  gap: 28px;
  padding-bottom: 40px;
}

/* Page Header */
.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.header-left {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.page-title {
  display: flex;
  align-items: center;
  gap: 10px;
  font-family: var(--font-display);
  font-size: 1.5rem;
  font-weight: 800;
  color: var(--text-primary);
  margin: 0;
}

.page-title svg {
  color: var(--color-primary-500);
}

.page-subtitle {
  font-size: var(--text-sm);
  color: var(--text-muted);
  margin: 0 0 0 32px;
}

.refresh-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 16px;
  border: 1px solid var(--border-color);
  border-radius: var(--radius-lg);
  background: var(--bg-card);
  color: var(--text-secondary);
  font-size: var(--text-sm);
  font-weight: 500;
  cursor: pointer;
  transition: all var(--transition-base);
}

.refresh-btn:hover:not(:disabled) {
  border-color: var(--color-primary-500);
  color: var(--color-primary-600);
  background: var(--glow-primary-soft);
}

.refresh-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.spinning {
  animation: spin 1s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

/* Loading */
.loading-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 100px 20px;
}

.loading-spinner {
  width: 40px;
  height: 40px;
  border: 3px solid var(--border-color);
  border-top-color: var(--color-primary-500);
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
  margin-bottom: 16px;
}

.loading-state p {
  color: var(--text-secondary);
  font-size: var(--text-sm);
}

/* Section */
.section {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.section-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: var(--text-sm);
  font-weight: 700;
  color: var(--text-secondary);
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.title-dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: var(--color-primary-500);
}

/* Metrics Grid */
.metrics-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 14px;
}

.metrics-grid.four-col {
  grid-template-columns: repeat(4, 1fr);
}

/* Metric Card */
.metric-card {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 16px 18px;
  background: var(--bg-card);
  border: 1px solid var(--border-color);
  border-radius: var(--radius-xl);
  transition: all var(--transition-base);
}

.metric-card:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-card);
}

.metric-card.primary {
  background: linear-gradient(135deg, rgba(59, 130, 246, 0.08), rgba(59, 130, 246, 0.02));
  border-color: rgba(59, 130, 246, 0.2);
}

.metric-icon {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: var(--radius-lg);
  background: var(--bg-hover);
  color: var(--text-muted);
  flex-shrink: 0;
}

.metric-icon.green { background: rgba(16, 185, 129, 0.1); color: #10b981; }
.metric-icon.blue { background: rgba(59, 130, 246, 0.1); color: #3b82f6; }
.metric-icon.purple { background: rgba(139, 92, 246, 0.1); color: #8b5cf6; }
.metric-icon.pink { background: rgba(236, 72, 153, 0.1); color: #ec4899; }
.metric-icon.yellow { background: rgba(245, 158, 11, 0.1); color: #f59e0b; }
.metric-icon.cyan { background: rgba(6, 182, 212, 0.1); color: #06b6d4; }
.metric-icon.orange { background: rgba(249, 115, 22, 0.1); color: #f97316; }

.metric-body {
  display: flex;
  flex-direction: column;
  gap: 2px;
  min-width: 0;
}

.metric-label {
  font-size: var(--text-xs);
  font-weight: 500;
  color: var(--text-muted);
}

.metric-value {
  font-family: var(--font-display);
  font-size: 1.5rem;
  font-weight: 800;
  color: var(--text-primary);
  line-height: 1.1;
}

.metric-value.green { color: #10b981; }
.metric-value.cyan { color: #06b6d4; }

.metric-unit {
  font-size: var(--text-xs);
  font-weight: 600;
  color: var(--text-muted);
}

/* Trend Card */
.trend-card {
  background: var(--bg-card);
  border: 1px solid var(--border-color);
  border-radius: var(--radius-xl);
  padding: 20px 24px;
}

.trend-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
}

.trend-legend {
  display: flex;
  gap: 16px;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: var(--text-xs);
  color: var(--text-secondary);
}

.legend-dot {
  width: 8px;
  height: 8px;
  border-radius: 2px;
}

.legend-dot.primary { background: var(--color-primary-500); }
.legend-dot.pink { background: #ec4899; }

.trend-total {
  font-size: var(--text-xs);
  color: var(--text-muted);
  font-weight: 500;
}

.trend-chart {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.chart-bars {
  display: flex;
  align-items: flex-end;
  gap: 2px;
  height: 100px;
}

.bar-group {
  flex: 1;
  height: 100%;
  display: flex;
  align-items: flex-end;
}

.bar-stack {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column-reverse;
  gap: 1px;
  justify-content: flex-start;
}

.chart-bar {
  width: 100%;
  border-radius: 2px 2px 0 0;
  min-height: 2px;
}

.chart-bar.counts {
  background: linear-gradient(180deg, var(--color-primary-500), var(--color-primary-400));
}

.chart-bar.likes {
  background: linear-gradient(180deg, #ec4899, #f472b6);
}

.chart-labels {
  display: flex;
  justify-content: space-between;
  padding-top: 8px;
  border-top: 1px solid var(--border-color);
}

.chart-label {
  font-size: 10px;
  color: var(--text-muted);
}

/* Two Column Layout */
.section.two-col {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 14px;
}

/* Distribution Card */
.dist-card,
.lists-card,
.token-card,
.activity-card {
  background: var(--bg-card);
  border: 1px solid var(--border-color);
  border-radius: var(--radius-xl);
  overflow: hidden;
}

.dist-header,
.lists-header,
.token-header,
.activity-header {
  padding: 16px 20px;
  border-bottom: 1px solid var(--border-color);
}

.dist-header h3,
.lists-header h3,
.token-header h3,
.activity-header h3 {
  font-size: var(--text-sm);
  font-weight: 700;
  color: var(--text-primary);
  margin: 0;
}

.dist-body,
.lists-body,
.token-body,
.activity-body {
  padding: 16px 20px;
}

/* Distribution Items */
.dist-item {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
}

.dist-item:last-child {
  margin-bottom: 0;
}

.dist-info {
  width: 90px;
  flex-shrink: 0;
}

.dist-name {
  display: block;
  font-size: var(--text-sm);
  font-weight: 600;
  color: var(--text-primary);
}

.dist-count {
  font-size: 10px;
  color: var(--text-muted);
}

.dist-bar {
  flex: 1;
  height: 6px;
  background: var(--bg-hover);
  border-radius: var(--radius-full);
  overflow: hidden;
}

.dist-bar-fill {
  height: 100%;
  background: linear-gradient(90deg, var(--color-primary-500), var(--color-primary-400));
  border-radius: var(--radius-full);
  transition: width 0.5s ease;
}

.dist-percent {
  width: 40px;
  text-align: right;
  font-size: var(--text-xs);
  font-weight: 600;
  color: var(--text-secondary);
}

/* List Items */
.list-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 10px 12px;
  margin: 0 -12px;
  border-radius: var(--radius-lg);
  transition: background var(--transition-fast);
}

.list-item:hover {
  background: var(--bg-hover);
}

.item-rank {
  width: 24px;
  height: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 11px;
  font-weight: 800;
  background: var(--bg-hover);
  color: var(--text-secondary);
  border-radius: var(--radius-md);
  flex-shrink: 0;
}

.item-rank.gold { background: linear-gradient(135deg, #fbbf24, #f59e0b); color: white; }
.item-rank.silver { background: linear-gradient(135deg, #94a3b8, #64748b); color: white; }
.item-rank.bronze { background: linear-gradient(135deg, #d97706, #b45309); color: white; }

.item-content {
  flex: 1;
  min-width: 0;
}

.item-title {
  display: block;
  font-size: var(--text-sm);
  font-weight: 500;
  color: var(--text-primary);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.item-meta {
  display: flex;
  gap: 8px;
  margin-top: 4px;
}

.meta-tag {
  display: inline-flex;
  align-items: center;
  gap: 3px;
  font-size: 10px;
  color: var(--text-muted);
}

.meta-tag.likes { color: #ec4899; }
.meta-tag.score { color: #f59e0b; }

/* Token Items */
.token-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 0;
  border-bottom: 1px solid var(--border-color);
}

.token-item:last-child {
  border-bottom: none;
  padding-bottom: 0;
}

.token-item.total {
  padding-top: 16px;
  margin-top: 8px;
  border-top: 1px solid var(--border-color);
  border-bottom: none;
}

.token-info {
  display: flex;
  align-items: center;
  gap: 10px;
  color: var(--text-secondary);
  font-size: var(--text-sm);
}

.token-value {
  font-family: var(--font-display);
  font-size: 1.125rem;
  font-weight: 700;
  color: var(--text-primary);
}

.token-value.primary {
  color: var(--color-primary-500);
}

/* Activity Items */
.activity-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 10px 0;
  border-bottom: 1px solid var(--border-color);
}

.activity-item:last-child {
  border-bottom: none;
}

.activity-icon {
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--glow-primary-soft);
  color: var(--color-primary-500);
  border-radius: var(--radius-md);
  flex-shrink: 0;
}

.activity-content {
  flex: 1;
  min-width: 0;
}

.activity-title {
  display: block;
  font-size: var(--text-sm);
  font-weight: 500;
  color: var(--text-primary);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.activity-time {
  font-size: 10px;
  color: var(--text-muted);
}

/* Empty State */
.empty-state {
  text-align: center;
  padding: 32px 16px;
  color: var(--text-muted);
  font-size: var(--text-sm);
}

/* Error State */
.error-state {
  text-align: center;
  padding: 80px 20px;
}

.error-state p {
  color: var(--text-secondary);
  margin-bottom: 16px;
}

.retry-btn {
  padding: 10px 20px;
  border: none;
  border-radius: var(--radius-lg);
  background: var(--color-primary-600);
  color: white;
  font-weight: 600;
  cursor: pointer;
}

/* Responsive */
@media (max-width: 1200px) {
  .metrics-grid,
  .metrics-grid.four-col {
    grid-template-columns: repeat(2, 1fr);
  }

  .section.two-col {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }

  .metrics-grid,
  .metrics-grid.four-col {
    grid-template-columns: 1fr;
  }
}
</style>
