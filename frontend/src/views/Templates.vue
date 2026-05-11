<template>
  <AppLayout>
    <template #main>
      <div class="page-wrapper">
        <div class="history-shell">
          <!-- Hero Header -->
          <div class="history-hero">
            <div class="hero-left">
              <div class="hero-kicker">Prompt Library</div>
              <h1 class="hero-title">提示词生成历史</h1>
              <p class="hero-desc">记录每一次创作，方便复用和追溯。</p>
            </div>
            <div class="hero-right">
              <button class="create-btn" @click="router.push('/generate')">
                <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <line x1="12" y1="5" x2="12" y2="19"/>
                  <line x1="5" y1="12" x2="19" y2="12"/>
                </svg>
                新建创作
              </button>
            </div>
          </div>

          <!-- Stats Bar -->
          <div class="stats-bar">
            <div class="stat-item">
              <span class="stat-icon">📝</span>
              <span class="stat-label">总计生成</span>
              <span class="stat-value">{{ totalCount }}</span>
            </div>
            <div class="stat-divider"></div>
            <div class="stat-item">
              <span class="stat-icon">🕐</span>
              <span class="stat-label">最近生成</span>
              <span class="stat-value">{{ lastGenerated }}</span>
            </div>
          </div>

          <!-- Filter Bar -->
          <div class="filter-bar">
            <div class="search-box">
              <svg class="search-icon" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <circle cx="11" cy="11" r="8"/>
                <path d="m21 21-4.35-4.35"/>
              </svg>
              <input
                v-model="search"
                type="text"
                placeholder="搜索历史记录..."
                class="search-input"
                @input="onSearch"
              />
            </div>
            <div class="type-tabs">
              <button
                :class="['type-btn', { active: typeFilter === 'all' }]"
                @click="changeTypeFilter('all')"
              >
                全部
              </button>
              <button
                :class="['type-btn', { active: typeFilter === 'agent' }]"
                @click="changeTypeFilter('agent')"
              >
                🤖 Agent
              </button>
              <button
                :class="['type-btn', { active: typeFilter === 'skill' }]"
                @click="changeTypeFilter('skill')"
              >
                ⚡ Skill
              </button>
            </div>
            <div class="sort-tabs">
              <button
                :class="['sort-btn', { active: sortBy === 'createdAt' }]"
                @click="changeSort('createdAt')"
              >
                <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <circle cx="12" cy="12" r="10"/>
                  <polyline points="12 6 12 12 16 14"/>
                </svg>
                最新
              </button>
              <button
                :class="['sort-btn', { active: sortBy === 'name' }]"
                @click="changeSort('name')"
              >
                <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M4 7V4h16v3"/>
                  <path d="M9 20h6"/>
                  <path d="M12 4v16"/>
                </svg>
                名称
              </button>
            </div>
          </div>

          <!-- History List -->
          <div class="history-list" ref="listRef">
            <!-- Loading State -->
            <div v-if="pending && list.length === 0" class="loading-state">
              <div class="loading-spinner"></div>
              <span>加载中...</span>
            </div>

            <!-- Empty State -->
            <div v-else-if="displayedList.length === 0" class="empty-state">
              <div class="empty-icon">
                <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
                  <circle cx="12" cy="12" r="10"/>
                  <polyline points="12 6 12 12 16 14"/>
                </svg>
              </div>
              <h3>暂无生成记录</h3>
              <p>开始创作你的第一个提示词吧</p>
              <button class="start-btn" @click="router.push('/generate')">
                前往创作
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M5 12h14M12 5l7 7-7 7"/>
                </svg>
              </button>
            </div>

            <!-- Cards -->
            <div v-else class="cards-grid">
              <div
                v-for="item in displayedList"
                :key="item.id"
                class="history-card"
                @click="openDetail(item)"
              >
                <div class="card-type-badge">
                  <span v-if="item.type === 'agent'" class="badge agent">🤖 Agent</span>
                  <span v-else class="badge skill">⚡ Skill</span>
                </div>

                <h3 class="card-name">{{ item.name || item.taskDescription || '未命名' }}</h3>

                <p class="card-preview">
                  {{ getPreview(item.generatedPrompt) }}
                </p>

                <div class="card-meta">
                  <span class="meta-time">
                    <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                      <circle cx="12" cy="12" r="10"/>
                      <polyline points="12 6 12 12 16 14"/>
                    </svg>
                    {{ fmt(item.createdAt) }}
                  </span>
                </div>

                <div class="card-actions">
                  <button class="action-btn reuse" @click.stop="reusePrompt(item)" title="复用">
                    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                      <polyline points="17 1 21 5 17 9"/>
                      <path d="M3 11V9a4 4 0 0 1 4-4h14"/>
                      <polyline points="7 23 3 19 7 15"/>
                      <path d="M21 13v2a4 4 0 0 1-4 4H3"/>
                    </svg>
                  </button>
                  <button class="action-btn view" @click.stop="openDetail(item)" title="查看">
                    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                      <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/>
                      <circle cx="12" cy="12" r="3"/>
                    </svg>
                  </button>
                  <button class="action-btn delete" @click.stop="deletePrompt(item)" title="删除">
                    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                      <polyline points="3 6 5 6 21 6"/>
                      <path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"/>
                    </svg>
                  </button>
                </div>
              </div>
            </div>

            <!-- Loading More -->
            <div v-if="pending && list.length > 0" class="loading-more">
              <div class="loading-spinner small"></div>
            </div>
            <div v-else-if="hasMore" class="load-more-trigger" @click="loadMore">
              加载更多
            </div>
          </div>
        </div>
      </div>
    </template>

    <!-- Detail Modal -->
    <Teleport to="body">
      <div v-if="showDetail" class="modal-overlay" @click.self="closeDetail">
        <div class="modal-content">
          <div class="modal-header">
            <div class="modal-title">
              <span v-if="curItem?.type === 'agent'" class="badge agent">🤖 Agent</span>
              <span v-else class="badge skill">⚡ Skill</span>
              <h2>{{ curItem?.name || curItem?.taskDescription || '提示词详情' }}</h2>
            </div>
            <button class="modal-close" @click="closeDetail">
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <line x1="18" y1="6" x2="6" y2="18"/>
                <line x1="6" y1="6" x2="18" y2="18"/>
              </svg>
            </button>
          </div>

          <div class="modal-body">
            <div class="detail-section">
              <label>基本信息</label>
              <div class="info-grid">
                <div class="info-item">
                  <span class="info-label">类型</span>
                  <span class="info-value">{{ curItem?.type === 'agent' ? 'Agent' : 'Skill' }}</span>
                </div>
                <div class="info-item">
                  <span class="info-label">生成时间</span>
                  <span class="info-value">{{ fmt(curItem?.createdAt) }}</span>
                </div>
              </div>
            </div>

            <div v-if="curItem?.roleDescription || curItem?.taskDescription" class="detail-section">
              <label>任务描述</label>
              <p class="detail-text">{{ curItem?.roleDescription || curItem?.taskDescription }}</p>
            </div>

            <div v-if="curItem?.generatedPrompt" class="detail-section">
              <label>生成的提示词</label>
              <div class="prompt-content" v-html="renderMarkdown(curItem.generatedPrompt)"></div>
            </div>
          </div>

          <div class="modal-footer">
            <button class="btn-secondary" @click="closeDetail">关闭</button>
            <button class="btn-primary" @click="reusePrompt(curItem)">
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <polyline points="17 1 21 5 17 9"/>
                <path d="M3 11V9a4 4 0 0 1 4-4h14"/>
              </svg>
              复用此提示词
            </button>
          </div>
        </div>
      </div>
    </Teleport>
  </AppLayout>
</template>

<script setup>
import { ref, onMounted, onUnmounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import MarkdownIt from 'markdown-it'
import AppLayout from '../components/layout/AppLayout.vue'

const router = useRouter()

const md = new MarkdownIt({
  html: true,
  linkify: true,
  typographer: true,
  breaks: true
})

const API = 'http://111.231.107.210:8080/api'

const search = ref('')
const typeFilter = ref('all')
const sortBy = ref('createdAt')
const pending = ref(false)
const list = ref([])
const page = ref(1)
const size = ref(20)
const hasMore = ref(true)
const listRef = ref(null)

const showDetail = ref(false)
const curItem = ref(null)

const totalCount = computed(() => list.value.length)
const lastGenerated = computed(() => {
  if (list.value.length === 0) return '-'
  return fmt(list.value[0]?.createdAt)
})

const searchKeyword = computed(() => search.value.trim().toLowerCase())

const displayedList = computed(() => {
  let result = list.value

  // Filter by type
  if (typeFilter.value !== 'all') {
    result = result.filter((item) => item.type === typeFilter.value)
  }

  // Filter by search keyword
  if (searchKeyword.value) {
    result = result.filter((item) => {
      const haystack = [
        item.name,
        item.taskDescription,
        item.roleDescription,
        item.generatedPrompt
      ].filter(Boolean).join(' ').toLowerCase()
      return haystack.includes(searchKeyword.value)
    })
  }

  return result
})

const changeTypeFilter = (type) => {
  typeFilter.value = type
}

const loadData = async (reset = false) => {
  if (pending.value) return
  pending.value = true

  try {
    const p = reset ? 1 : page.value
    const url = `${API}/history/page?page=${p}&size=${size.value}&sortBy=${sortBy.value}&sortOrder=desc`
    const r = await fetch(url)
    const d = await r.json()
    const newList = d.data?.list || d.data || []

    if (reset) {
      list.value = newList
    } else {
      list.value = [...list.value, ...newList]
    }
    hasMore.value = newList.length >= size.value
  } catch (e) {
    console.error('[History] Error:', e)
  }
  pending.value = false
}

const openDetail = async (item) => {
  curItem.value = item
  showDetail.value = true
  document.body.style.overflow = 'hidden'
}

const closeDetail = () => {
  showDetail.value = false
  curItem.value = null
  document.body.style.overflow = ''
}

const reusePrompt = (item) => {
  if (!item) return
  window.dispatchEvent(new CustomEvent('reuse-history', { detail: item }))
  closeDetail()
  router.push('/generate')
}

const deletePrompt = async (item) => {
  if (!confirm('确定要删除这条记录吗？')) return
  try {
    await fetch(`${API}/history/${item.id}`, { method: 'DELETE' })
    list.value = list.value.filter((i) => i.id !== item.id)
    closeDetail()
  } catch (e) {
    console.error('[History] Delete error:', e)
  }
}

const getPreview = (text) => {
  if (!text) return '暂无内容'
  return text.substring(0, 120) + (text.length > 120 ? '...' : '')
}

const renderMarkdown = (text) => {
  if (!text) return ''
  return md.render(text)
}

const fmt = (t) => {
  if (!t) return ''
  const d = new Date(t)
  const now = new Date()
  const diff = now - d

  if (diff < 60000) return '刚刚'
  if (diff < 3600000) return `${Math.floor(diff / 60000)}分钟前`
  if (diff < 86400000) return `${Math.floor(diff / 3600000)}小时前`

  const y = d.getFullYear()
  const m = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  return `${y}-${m}-${day}`
}

const onSearch = () => {
  page.value = 1
}

const changeSort = (sort) => {
  sortBy.value = sort
  page.value = 1
  hasMore.value = true
  loadData(true)
}

const loadMore = () => {
  page.value++
  loadData()
}

const handleScroll = () => {
  if (pending.value || !hasMore.value) return
  const scrollBottom = document.documentElement.scrollHeight - window.scrollY - window.innerHeight
  if (scrollBottom < 300) {
    loadMore()
  }
}

onMounted(() => {
  loadData(true)
  window.addEventListener('scroll', handleScroll)
})

onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll)
})
</script>

<style scoped>
.page-wrapper {
  min-height: 100%;
  padding: 8px 4px 40px;
}

.history-shell {
  max-width: 1200px;
  margin: 0 auto;
}

/* Hero */
.history-hero {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  gap: 24px;
  padding: 8px 4px 32px;
}

.hero-kicker {
  font-size: 0.75rem;
  font-weight: 700;
  letter-spacing: 0.12em;
  text-transform: uppercase;
  color: var(--color-primary-500);
  margin-bottom: 10px;
}

.hero-title {
  font-family: var(--font-display);
  font-size: clamp(2rem, 4vw, 2.5rem);
  font-weight: 900;
  color: var(--text-primary);
  margin: 0;
  letter-spacing: -0.04em;
  line-height: 1;
}

.hero-desc {
  margin: 14px 0 0;
  color: var(--text-secondary);
  font-size: var(--text-base);
  line-height: 1.7;
}

.create-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 14px 24px;
  border: none;
  border-radius: 12px;
  background: linear-gradient(135deg, var(--color-primary-600), var(--color-primary-700));
  color: white;
  font-size: var(--text-sm);
  font-weight: 700;
  cursor: pointer;
  transition: all 0.25s;
  box-shadow: 0 4px 16px rgba(59, 130, 246, 0.3);
}

.create-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(59, 130, 246, 0.4);
}

/* Stats Bar */
.stats-bar {
  display: flex;
  align-items: center;
  gap: 24px;
  padding: 20px 24px;
  background: var(--bg-card);
  border: 1px solid var(--border-color);
  border-radius: 16px;
  margin-bottom: 24px;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 10px;
}

.stat-icon {
  font-size: 1.2rem;
}

.stat-label {
  font-size: var(--text-sm);
  color: var(--text-muted);
}

.stat-value {
  font-size: var(--text-base);
  font-weight: 700;
  color: var(--text-primary);
}

.stat-divider {
  width: 1px;
  height: 24px;
  background: var(--border-color);
}

/* Filter Bar */
.filter-bar {
  display: flex;
  gap: 16px;
  align-items: center;
  margin-bottom: 24px;
}

.search-box {
  position: relative;
  flex: 1;
}

.search-icon {
  position: absolute;
  left: 16px;
  top: 50%;
  transform: translateY(-50%);
  color: var(--text-muted);
}

.search-input {
  width: 100%;
  padding: 14px 18px 14px 48px;
  font-family: var(--font-body);
  font-size: var(--text-sm);
  color: var(--text-primary);
  background: var(--bg-card);
  border: 1px solid var(--border-color);
  border-radius: 12px;
  transition: all 0.2s;
}

.search-input:focus {
  outline: none;
  border-color: var(--color-primary-500);
  box-shadow: 0 0 0 4px var(--glow-primary-soft);
}

.search-input::placeholder {
  color: var(--text-placeholder);
}

.sort-tabs {
  display: flex;
  gap: 8px;
}

.sort-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 10px 16px;
  border: 1px solid var(--border-color);
  border-radius: 10px;
  background: var(--bg-card);
  color: var(--text-secondary);
  font-size: var(--text-sm);
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
}

.sort-btn:hover {
  border-color: var(--color-primary-400);
  color: var(--color-primary-600);
}

.sort-btn.active {
  border-color: var(--color-primary-500);
  background: var(--glow-primary-soft);
  color: var(--color-primary-600);
}

.type-tabs {
  display: flex;
  gap: 6px;
  padding: 4px;
  background: var(--bg-panel);
  border-radius: 10px;
}

.type-btn {
  padding: 8px 16px;
  border: none;
  border-radius: 8px;
  background: transparent;
  color: var(--text-secondary);
  font-size: var(--text-sm);
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
}

.type-btn:hover {
  color: var(--text-primary);
  background: var(--bg-hover);
}

.type-btn.active {
  background: var(--bg-card);
  color: var(--color-primary-600);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

/* History List */
.history-list {
  min-height: 400px;
}

.loading-state,
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 80px 20px;
  text-align: center;
  background: var(--bg-card);
  border: 1px solid var(--border-color);
  border-radius: 20px;
}

.empty-icon {
  width: 80px;
  height: 80px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--bg-hover);
  border-radius: 20px;
  color: var(--text-muted);
  margin-bottom: 20px;
}

.empty-state h3 {
  font-family: var(--font-display);
  font-size: 1.25rem;
  font-weight: 700;
  color: var(--text-primary);
  margin: 0 0 8px;
}

.empty-state p {
  font-size: var(--text-sm);
  color: var(--text-muted);
  margin: 0 0 24px;
}

.start-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 20px;
  border: none;
  border-radius: 10px;
  background: linear-gradient(135deg, var(--color-primary-600), var(--color-primary-700));
  color: white;
  font-size: var(--text-sm);
  font-weight: 700;
  cursor: pointer;
  transition: all 0.2s;
}

.start-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 16px rgba(59, 130, 246, 0.3);
}

/* Cards Grid */
.cards-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 20px;
}

.history-card {
  display: flex;
  flex-direction: column;
  padding: 20px;
  background: var(--bg-card);
  border: 1px solid var(--border-color);
  border-radius: 16px;
  cursor: pointer;
  transition: all 0.25s;
}

.history-card:hover {
  transform: translateY(-4px);
  border-color: var(--color-primary-300);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.08);
}

.card-type-badge {
  margin-bottom: 12px;
}

.badge {
  display: inline-flex;
  padding: 4px 10px;
  font-size: 0.7rem;
  font-weight: 700;
  border-radius: 6px;
}

.badge.agent {
  background: rgba(99, 102, 241, 0.1);
  color: #6366f1;
}

.badge.skill {
  background: rgba(34, 211, 238, 0.1);
  color: #22d3ee;
}

.card-name {
  font-family: var(--font-display);
  font-size: 1rem;
  font-weight: 700;
  color: var(--text-primary);
  margin: 0 0 10px;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.card-preview {
  font-size: var(--text-sm);
  color: var(--text-secondary);
  line-height: 1.6;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
  margin: 0 0 16px;
  flex: 1;
}

.card-meta {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
}

.meta-time {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 0.75rem;
  color: var(--text-muted);
}

.card-actions {
  display: flex;
  gap: 8px;
  padding-top: 14px;
  border-top: 1px solid var(--border-color);
}

.action-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 36px;
  height: 36px;
  border: 1px solid var(--border-color);
  border-radius: 8px;
  background: var(--bg-card);
  color: var(--text-secondary);
  cursor: pointer;
  transition: all 0.2s;
}

.action-btn:hover {
  border-color: var(--color-primary-400);
  color: var(--color-primary-600);
  background: var(--glow-primary-soft);
}

.action-btn.delete:hover {
  border-color: var(--color-error);
  color: var(--color-error);
  background: rgba(239, 68, 68, 0.1);
}

/* Loading */
.loading-spinner {
  width: 40px;
  height: 40px;
  border: 3px solid var(--border-color);
  border-top-color: var(--color-primary-500);
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
  margin-bottom: 16px;
}

.loading-spinner.small {
  width: 24px;
  height: 24px;
  border-width: 2px;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.loading-more {
  text-align: center;
  padding: 24px;
}

.load-more-trigger {
  text-align: center;
  padding: 20px;
  color: var(--color-primary-500);
  font-weight: 600;
  cursor: pointer;
}

.load-more-trigger:hover {
  color: var(--color-primary-600);
}

/* Modal */
.modal-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.6);
  backdrop-filter: blur(4px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  padding: 20px;
}

.modal-content {
  width: 100%;
  max-width: 700px;
  max-height: 85vh;
  background: var(--bg-card);
  border: 1px solid var(--border-color);
  border-radius: 20px;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 16px;
  padding: 24px;
  border-bottom: 1px solid var(--border-color);
}

.modal-title {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.modal-title h2 {
  font-family: var(--font-display);
  font-size: 1.25rem;
  font-weight: 700;
  color: var(--text-primary);
  margin: 0;
}

.modal-close {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 36px;
  height: 36px;
  border: none;
  border-radius: 8px;
  background: var(--bg-hover);
  color: var(--text-secondary);
  cursor: pointer;
  transition: all 0.2s;
  flex-shrink: 0;
}

.modal-close:hover {
  background: var(--color-error);
  color: white;
}

.modal-body {
  flex: 1;
  overflow-y: auto;
  padding: 24px;
}

.detail-section {
  margin-bottom: 24px;
}

.detail-section:last-child {
  margin-bottom: 0;
}

.detail-section label {
  display: block;
  font-size: 0.75rem;
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 0.08em;
  color: var(--text-muted);
  margin-bottom: 10px;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
}

.info-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.info-label {
  font-size: 0.75rem;
  color: var(--text-muted);
}

.info-value {
  font-size: var(--text-sm);
  font-weight: 600;
  color: var(--text-primary);
}

.detail-text {
  font-size: var(--text-sm);
  color: var(--text-secondary);
  line-height: 1.7;
  margin: 0;
}

.prompt-content {
  background: var(--bg-panel);
  border: 1px solid var(--border-color);
  border-radius: 12px;
  padding: 16px;
  font-size: var(--text-sm);
  line-height: 1.8;
  color: var(--text-secondary);
  max-height: 300px;
  overflow-y: auto;
}

.prompt-content :deep(h1),
.prompt-content :deep(h2),
.prompt-content :deep(h3) {
  color: var(--text-primary);
  margin-top: 1em;
  margin-bottom: 0.5em;
}

.prompt-content :deep(p) {
  margin: 0 0 1em;
}

.prompt-content :deep(code) {
  padding: 0.2em 0.4em;
  background: var(--bg-hover);
  border-radius: 4px;
  font-family: var(--font-mono);
  font-size: 0.9em;
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding: 20px 24px;
  border-top: 1px solid var(--border-color);
}

.btn-secondary {
  padding: 10px 20px;
  border: 1px solid var(--border-color);
  border-radius: 10px;
  background: transparent;
  color: var(--text-secondary);
  font-size: var(--text-sm);
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-secondary:hover {
  background: var(--bg-hover);
  color: var(--text-primary);
}

.btn-primary {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 20px;
  border: none;
  border-radius: 10px;
  background: linear-gradient(135deg, var(--color-primary-600), var(--color-primary-700));
  color: white;
  font-size: var(--text-sm);
  font-weight: 700;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-primary:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.3);
}

/* Responsive */
@media (max-width: 768px) {
  .history-hero {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }

  .create-btn {
    width: 100%;
    justify-content: center;
  }

  .stats-bar {
    flex-wrap: wrap;
    gap: 16px;
  }

  .stat-divider {
    display: none;
  }

  .filter-bar {
    flex-direction: column;
  }

  .sort-tabs {
    width: 100%;
  }

  .type-tabs {
    width: 100%;
  }

  .type-btn {
    flex: 1;
    justify-content: center;
  }

  .sort-btn {
    flex: 1;
    justify-content: center;
  }

  .cards-grid {
    grid-template-columns: 1fr;
  }

  .info-grid {
    grid-template-columns: 1fr;
  }
}
</style>
