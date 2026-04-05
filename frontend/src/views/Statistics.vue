<template>
  <AppLayout>
    <template #main>
      <div class="statistics-page">
        <!-- Page Header -->
        <div class="page-header">
          <div class="header-content">
            <h1 class="page-title">
              <svg width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M18 20V10"/>
                <path d="M12 20V4"/>
                <path d="M6 20v-6"/>
              </svg>
              <span>使用统计分析</span>
            </h1>
            <p class="page-subtitle">了解您的提示词创作趋势和使用情况</p>
          </div>
          <button class="refresh-btn" @click="loadStats" :disabled="loading">
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" :class="{ spinning: loading }">
              <path d="M3 12a9 9 0 1 0 9-9 9.75 9.75 0 0 0-6.74 2.74L3 8"/>
              <path d="M3 3v5h5"/>
            </svg>
            <span>刷新</span>
          </button>
        </div>

        <!-- Loading State -->
        <div v-if="loading" class="loading-state">
          <div class="loading-spinner"></div>
          <p>加载统计数据中...</p>
        </div>

        <!-- Stats Content -->
        <div v-else-if="stats" class="stats-content">
          <!-- Primary Stats Cards -->
          <div class="stats-cards primary">
            <div class="stat-card stat-primary">
              <div class="stat-icon">
                <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M14.5 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V7.5L14.5 2z"/>
                  <polyline points="14 2 14 8 20 8"/>
                </svg>
              </div>
              <div class="stat-info">
                <span class="stat-value">{{ formatNumber(stats.totalPrompts) }}</span>
                <span class="stat-label">总提示词数</span>
              </div>
              <div class="stat-glow"></div>
            </div>

            <div class="stat-card stat-success">
              <div class="stat-icon">
                <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M12 2v20M17 5H9.5a3.5 3.5 0 0 0 0 7h5a3.5 3.5 0 0 1 0 7H6"/>
                </svg>
              </div>
              <div class="stat-info">
                <span class="stat-value">{{ formatNumber(stats.todayCount) }}</span>
                <span class="stat-label">今日新增</span>
              </div>
              <div class="stat-glow"></div>
            </div>

            <div class="stat-card stat-info">
              <div class="stat-icon">
                <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <rect x="3" y="4" width="18" height="18" rx="2" ry="2"/>
                  <line x1="16" y1="2" x2="16" y2="6"/>
                  <line x1="8" y1="2" x2="8" y2="6"/>
                  <line x1="3" y1="10" x2="21" y2="10"/>
                </svg>
              </div>
              <div class="stat-info">
                <span class="stat-value">{{ formatNumber(stats.weekCount) }}</span>
                <span class="stat-label">本周新增</span>
              </div>
              <div class="stat-glow"></div>
            </div>

            <div class="stat-card stat-warning">
              <div class="stat-icon">
                <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z"/>
                </svg>
              </div>
              <div class="stat-info">
                <span class="stat-value">{{ formatNumber(stats.totalLikes) }}</span>
                <span class="stat-label">总点赞数</span>
              </div>
              <div class="stat-glow"></div>
            </div>
          </div>

          <!-- Secondary Stats - Tokens -->
          <div class="stats-cards secondary">
            <div class="stat-card stat-tokens">
              <div class="stat-icon">
                <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <polyline points="22 12 18 12 15 21 9 3 6 12 2 12"/>
                </svg>
              </div>
              <div class="stat-info">
                <span class="stat-value">{{ formatTokens(stats.totalInputTokens) }}</span>
                <span class="stat-label">输入 Tokens</span>
              </div>
            </div>

            <div class="stat-card stat-tokens">
              <div class="stat-icon">
                <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <polyline points="22 12 18 12 15 21 9 3 6 12 2 12"/>
                </svg>
              </div>
              <div class="stat-info">
                <span class="stat-value">{{ formatTokens(stats.totalOutputTokens) }}</span>
                <span class="stat-label">输出 Tokens</span>
              </div>
            </div>

            <div class="stat-card stat-tokens">
              <div class="stat-icon">
                <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <circle cx="12" cy="12" r="10"/>
                  <path d="M12 6v6l4 2"/>
                </svg>
              </div>
              <div class="stat-info">
                <span class="stat-value">{{ formatTokens(stats.totalTokens) }}</span>
                <span class="stat-label">总 Tokens</span>
              </div>
            </div>

            <div class="stat-card stat-tokens">
              <div class="stat-icon">
                <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"/>
                  <polyline points="22 4 12 14.01 9 11.01"/>
                </svg>
              </div>
              <div class="stat-info">
                <span class="stat-value">{{ stats.cacheHitRate?.toFixed(1) || 0 }}%</span>
                <span class="stat-label">缓存命中率</span>
              </div>
            </div>
          </div>

          <!-- Charts Row -->
          <div class="charts-row">
            <!-- Trend Chart -->
            <div class="chart-card">
              <div class="card-header">
                <h3>
                  <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <polyline points="22 12 18 12 15 21 9 3 6 12 2 12"/>
                  </svg>
                  30天趋势
                </h3>
              </div>
              <div class="chart-body">
                <div class="trend-chart">
                  <div
                    v-for="(day, index) in stats.dailyTrends"
                    :key="day.date"
                    class="chart-bar-wrapper"
                  >
                    <div
                      class="chart-bar"
                      :style="{ height: getBarHeight(day.count) + '%' }"
                      :title="`${day.date}: ${day.count}条`"
                    >
                      <span class="bar-tooltip">{{ day.count }}</span>
                    </div>
                  </div>
                </div>
                <div class="chart-labels">
                  <span v-for="(day, idx) in stats.dailyTrends" :key="day.date">
                    <span v-if="idx % 5 === 0" class="chart-label">{{ day.date.slice(5) }}</span>
                  </span>
                </div>
              </div>
            </div>

            <!-- Category Distribution -->
            <div class="chart-card category-card">
              <div class="card-header">
                <h3>
                  <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"/>
                    <polyline points="22 4 12 14.01 9 11.01"/>
                  </svg>
                  分类分布
                </h3>
              </div>
              <div class="chart-body">
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
            </div>
          </div>

          <!-- Bottom Row -->
          <div class="bottom-row">
            <!-- Top Prompts -->
            <div class="list-card">
              <div class="card-header">
                <h3>
                  <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/>
                    <circle cx="9" cy="7" r="4"/>
                    <path d="M23 21v-2a4 4 0 0 0-3-3.87"/>
                    <path d="M16 3.13a4 4 0 0 1 0 7.75"/>
                  </svg>
                  热门提示词 Top 10
                </h3>
              </div>
              <div class="list-body">
                <div
                  v-for="(prompt, index) in stats.topPrompts"
                  :key="prompt.id"
                  class="list-item"
                >
                  <div class="item-rank" :class="getRankClass(index)">{{ index + 1 }}</div>
                  <div class="item-content">
                    <span class="item-title">{{ prompt.taskDescription || '未命名' }}</span>
                    <div class="item-meta">
                      <span class="meta-chip">
                        <svg width="12" height="12" viewBox="0 0 24 24" fill="currentColor">
                          <path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z"/>
                        </svg>
                        {{ prompt.likeCount || 0 }}
                      </span>
                      <span v-if="prompt.effectivenessScore" class="meta-chip">
                        <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                          <polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"/>
                        </svg>
                        {{ prompt.effectivenessScore }}
                      </span>
                    </div>
                  </div>
                </div>
                <div v-if="!stats.topPrompts?.length" class="empty-tip">
                  暂无热门提示词，快去创作吧！
                </div>
              </div>
            </div>

            <!-- Recent Activity -->
            <div class="list-card">
              <div class="card-header">
                <h3>
                  <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <circle cx="12" cy="12" r="10"/>
                    <polyline points="12 6 12 12 16 14"/>
                  </svg>
                  最近活动
                </h3>
              </div>
              <div class="list-body">
                <div
                  v-for="prompt in stats.recentActivities"
                  :key="prompt.id"
                  class="list-item"
                >
                  <div class="item-icon">
                    <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                      <path d="M14.5 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V7.5L14.5 2z"/>
                      <polyline points="14 2 14 8 20 8"/>
                    </svg>
                  </div>
                  <div class="item-content">
                    <span class="item-title">{{ prompt.taskDescription || '未命名' }}</span>
                    <span class="item-time">{{ formatTime(prompt.createdAt) }}</span>
                  </div>
                </div>
                <div v-if="!stats.recentActivities?.length" class="empty-tip">
                  暂无最近活动
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Error State -->
        <div v-else class="error-state">
          <div class="error-icon">
            <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
              <circle cx="12" cy="12" r="10"/>
              <line x1="12" y1="8" x2="12" y2="12"/>
              <line x1="12" y1="16" x2="12.01" y2="16"/>
            </svg>
          </div>
          <h3>加载失败</h3>
          <p>请稍后重试</p>
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
const maxDailyCount = ref(1)

const loadStats = async () => {
  loading.value = true
  stats.value = null

  try {
    const url = API_BASE_URL ? `${API_BASE_URL}/statistics` : '/api/statistics'
    const response = await axios.get(url, { timeout: 10000 })

    if (response.data && response.data.success) {
      stats.value = response.data.data
      if (stats.value.dailyTrends?.length > 0) {
        maxDailyCount.value = Math.max(...stats.value.dailyTrends.map(d => d.count || 0), 1)
      }
    }
  } catch (error) {
    console.error('Failed to load statistics:', error)
  } finally {
    loading.value = false
  }
}

const formatNumber = (num) => {
  if (num === null || num === undefined) return '0'
  if (num >= 10000) return (num / 10000).toFixed(1) + 'w'
  return num.toString()
}

const formatTokens = (num) => {
  if (num === null || num === undefined) return '0'
  if (num >= 1000000) return (num / 1000000).toFixed(1) + 'M'
  if (num >= 1000) return (num / 1000).toFixed(1) + 'K'
  return num.toString()
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
  } catch (e) {
    return timeStr
  }
}

const getBarHeight = (count) => {
  if (!count || maxDailyCount.value === 0) return 0
  return Math.max((count / maxDailyCount.value) * 100, 5)
}

const getRankClass = (index) => {
  if (index === 0) return 'rank-gold'
  if (index === 1) return 'rank-silver'
  if (index === 2) return 'rank-bronze'
  return ''
}

onMounted(() => {
  loadStats()
})
</script>

<style scoped>
/* ============================================
   Statistics Page
   ============================================ */
.statistics-page {
  display: flex;
  flex-direction: column;
  gap: 24px;
  padding-bottom: 40px;
}

/* ============================================
   Page Header
   ============================================ */
.page-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 24px;
}

.header-content {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.page-title {
  display: flex;
  align-items: center;
  gap: 12px;
  font-family: var(--font-display);
  font-size: 1.75rem;
  font-weight: 800;
  color: var(--text-primary);
  letter-spacing: -0.02em;
}

.page-title svg {
  color: var(--color-primary-500);
}

.page-subtitle {
  font-size: var(--text-sm);
  color: var(--text-secondary);
  margin-left: 40px;
}

.refresh-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 18px;
  border: 1px solid var(--border-color);
  border-radius: var(--radius-xl);
  background: var(--bg-card);
  color: var(--text-secondary);
  font-family: var(--font-body);
  font-size: var(--text-sm);
  font-weight: 600;
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

/* ============================================
   Loading State
   ============================================ */
.loading-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 100px 20px;
  text-align: center;
}

.loading-spinner {
  width: 48px;
  height: 48px;
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

/* ============================================
   Stats Cards
   ============================================ */
.stats-cards {
  display: grid;
  gap: 16px;
}

.stats-cards.primary {
  grid-template-columns: repeat(4, 1fr);
}

.stats-cards.secondary {
  grid-template-columns: repeat(4, 1fr);
}

.stat-card {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px 24px;
  background: var(--bg-card);
  border: 1px solid var(--border-color);
  border-radius: var(--radius-2xl);
  position: relative;
  overflow: hidden;
  transition: all var(--transition-base);
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: var(--shadow-card-hover);
}

.stat-icon {
  width: 52px;
  height: 52px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: var(--radius-xl);
  flex-shrink: 0;
}

.stat-primary .stat-icon {
  background: linear-gradient(135deg, rgba(59, 130, 246, 0.15), rgba(59, 130, 246, 0.05));
  color: var(--color-primary-500);
}

.stat-success .stat-icon {
  background: linear-gradient(135deg, rgba(16, 185, 129, 0.15), rgba(16, 185, 129, 0.05));
  color: var(--color-success);
}

.stat-info .stat-icon {
  background: linear-gradient(135deg, rgba(14, 165, 233, 0.15), rgba(14, 165, 233, 0.05));
  color: #0ea5e9;
}

.stat-warning .stat-icon {
  background: linear-gradient(135deg, rgba(245, 158, 11, 0.15), rgba(245, 158, 11, 0.05));
  color: var(--color-warning);
}

.stat-tokens .stat-icon {
  background: linear-gradient(135deg, rgba(139, 92, 246, 0.15), rgba(139, 92, 246, 0.05));
  color: #8b5cf6;
}

.stat-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
  position: relative;
  z-index: 1;
}

.stat-value {
  font-family: var(--font-display);
  font-size: 1.75rem;
  font-weight: 800;
  color: var(--text-primary);
  line-height: 1;
}

.stat-label {
  font-size: var(--text-xs);
  font-weight: 600;
  color: var(--text-muted);
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.stat-glow {
  position: absolute;
  top: -50%;
  right: -20%;
  width: 100px;
  height: 100px;
  border-radius: 50%;
  opacity: 0.5;
  filter: blur(40px);
  pointer-events: none;
}

.stat-primary .stat-glow { background: var(--color-primary-500); }
.stat-success .stat-glow { background: var(--color-success); }
.stat-info .stat-glow { background: #0ea5e9; }
.stat-warning .stat-glow { background: var(--color-warning); }
.stat-tokens .stat-glow { background: #8b5cf6; }

/* ============================================
   Charts Row
   ============================================ */
.charts-row {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 20px;
}

.chart-card,
.list-card {
  background: var(--bg-card);
  border: 1px solid var(--border-color);
  border-radius: var(--radius-2xl);
  overflow: hidden;
  transition: all var(--transition-base);
}

.chart-card:hover,
.list-card:hover {
  box-shadow: var(--shadow-card);
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20px 24px;
  border-bottom: 1px solid var(--border-color);
}

.card-header h3 {
  display: flex;
  align-items: center;
  gap: 10px;
  font-family: var(--font-display);
  font-size: var(--text-base);
  font-weight: 700;
  color: var(--text-primary);
  margin: 0;
}

.card-header h3 svg {
  color: var(--color-primary-500);
}

.chart-body {
  padding: 24px;
}

/* ============================================
   Trend Chart
   ============================================ */
.trend-chart {
  display: flex;
  align-items: flex-end;
  gap: 4px;
  height: 160px;
  padding-bottom: 8px;
}

.chart-bar-wrapper {
  flex: 1;
  height: 100%;
  display: flex;
  align-items: flex-end;
}

.chart-bar {
  width: 100%;
  background: linear-gradient(180deg, var(--color-primary-500) 0%, var(--color-primary-400) 100%);
  border-radius: var(--radius-sm) var(--radius-sm) 0 0;
  min-height: 4px;
  position: relative;
  cursor: pointer;
  transition: all var(--transition-base);
}

.chart-bar:hover {
  background: linear-gradient(180deg, var(--color-primary-400) 0%, var(--color-primary-500) 100%);
  transform: scaleY(1.05);
  transform-origin: bottom;
}

.chart-bar:hover .bar-tooltip {
  opacity: 1;
  transform: translateX(-50%) translateY(-8px);
}

.bar-tooltip {
  position: absolute;
  bottom: 100%;
  left: 50%;
  transform: translateX(-50%) translateY(0);
  padding: 4px 8px;
  background: var(--color-gray-900);
  color: white;
  font-size: 0.7rem;
  font-weight: 600;
  border-radius: var(--radius-sm);
  white-space: nowrap;
  opacity: 0;
  transition: all var(--transition-fast);
  pointer-events: none;
}

.chart-labels {
  display: flex;
  justify-content: space-between;
  padding-top: 8px;
  border-top: 1px solid var(--border-color);
}

.chart-label {
  font-size: 0.7rem;
  color: var(--text-muted);
}

/* ============================================
   Category List
   ============================================ */
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
  width: 120px;
  display: flex;
  flex-direction: column;
  gap: 2px;
  flex-shrink: 0;
}

.category-name {
  font-size: var(--text-sm);
  font-weight: 600;
  color: var(--text-primary);
}

.category-count {
  font-size: var(--text-xs);
  color: var(--text-muted);
}

.category-bar {
  flex: 1;
  height: 8px;
  background: var(--bg-hover);
  border-radius: var(--radius-full);
  overflow: hidden;
}

.category-bar-fill {
  height: 100%;
  background: linear-gradient(90deg, var(--color-primary-500), var(--color-primary-400));
  border-radius: var(--radius-full);
  transition: width 0.5s ease;
}

.category-percentage {
  width: 45px;
  text-align: right;
  font-size: var(--text-sm);
  font-weight: 600;
  color: var(--text-secondary);
}

/* ============================================
   Bottom Row
   ============================================ */
.bottom-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
}

.list-body {
  padding: 8px;
  max-height: 400px;
  overflow-y: auto;
}

.list-item {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 14px;
  border-radius: var(--radius-xl);
  transition: all var(--transition-base);
}

.list-item:hover {
  background: var(--bg-hover);
}

.item-rank {
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-family: var(--font-display);
  font-size: var(--text-sm);
  font-weight: 800;
  background: var(--bg-hover);
  color: var(--text-secondary);
  border-radius: var(--radius-lg);
  flex-shrink: 0;
}

.rank-gold {
  background: linear-gradient(135deg, #fbbf24, #f59e0b);
  color: white;
}

.rank-silver {
  background: linear-gradient(135deg, #94a3b8, #64748b);
  color: white;
}

.rank-bronze {
  background: linear-gradient(135deg, #d97706, #b45309);
  color: white;
}

.item-icon {
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--glow-primary-soft);
  color: var(--color-primary-500);
  border-radius: var(--radius-lg);
  flex-shrink: 0;
}

.item-content {
  flex: 1;
  min-width: 0;
}

.item-title {
  display: block;
  font-size: var(--text-sm);
  font-weight: 600;
  color: var(--text-primary);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  margin-bottom: 4px;
}

.item-meta {
  display: flex;
  gap: 8px;
}

.meta-chip {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  font-size: 0.7rem;
  color: var(--text-muted);
}

.item-time {
  font-size: 0.7rem;
  color: var(--text-muted);
}

.empty-tip {
  text-align: center;
  padding: 40px 20px;
  color: var(--text-muted);
  font-size: var(--text-sm);
}

/* ============================================
   Error State
   ============================================ */
.error-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 100px 20px;
  text-align: center;
}

.error-icon {
  width: 80px;
  height: 80px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--color-error-light);
  color: var(--color-error);
  border-radius: var(--radius-2xl);
  margin-bottom: 20px;
}

.error-state h3 {
  font-family: var(--font-display);
  font-size: 1.25rem;
  font-weight: 700;
  color: var(--text-primary);
  margin: 0 0 8px;
}

.error-state p {
  font-size: var(--text-sm);
  color: var(--text-muted);
  margin: 0 0 24px;
}

.retry-btn {
  padding: 12px 24px;
  border: none;
  border-radius: var(--radius-xl);
  background: linear-gradient(135deg, var(--color-primary-600), var(--color-primary-700));
  color: white;
  font-family: var(--font-body);
  font-size: var(--text-sm);
  font-weight: 600;
  cursor: pointer;
  transition: all var(--transition-base);
}

.retry-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(59, 130, 246, 0.3);
}

/* ============================================
   Responsive
   ============================================ */
@media (max-width: 1200px) {
  .stats-cards.primary,
  .stats-cards.secondary {
    grid-template-columns: repeat(2, 1fr);
  }

  .charts-row {
    grid-template-columns: 1fr;
  }

  .bottom-row {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .page-header {
    flex-direction: column;
    gap: 16px;
  }

  .stats-cards.primary,
  .stats-cards.secondary {
    grid-template-columns: 1fr;
  }

  .stat-card {
    padding: 16px 20px;
  }

  .stat-value {
    font-size: 1.5rem;
  }
}
</style>
