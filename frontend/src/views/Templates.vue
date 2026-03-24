<template>
  <AppLayout>
    <template #main>
      <div class="page-wrapper">
        <div class="library-shell">
          <!-- Side Rail -->
          <aside class="library-rail">
            <button class="rail-brand">
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <polygon points="13 2 3 14 12 14 11 22 21 10 12 10 13 2"/>
              </svg>
            </button>
            <div class="rail-group">
              <button class="rail-item active" title="模板库">
                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M4 19.5A2.5 2.5 0 0 1 6.5 17H20"/>
                  <path d="M6.5 2H20v20H6.5A2.5 2.5 0 0 1 4 19.5v-15A2.5 2.5 0 0 1 6.5 2z"/>
                </svg>
              </button>
              <button class="rail-item" title="收藏">
                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"/>
                </svg>
              </button>
              <button class="rail-item" title="历史">
                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <circle cx="12" cy="12" r="10"/>
                  <polyline points="12 6 12 12 16 14"/>
                </svg>
              </button>
            </div>
            <button class="rail-create" @click="router.push('/generate')">
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <line x1="12" y1="5" x2="12" y2="19"/>
                <line x1="5" y1="12" x2="19" y2="12"/>
              </svg>
              <span>New</span>
            </button>
          </aside>

          <!-- Main Content -->
          <section class="library-main">
            <!-- Hero Header -->
            <div class="library-hero">
              <div class="hero-copy">
                <div class="hero-kicker">Prompt Library</div>
                <h1 class="hero-title">Template Library</h1>
                <p class="hero-desc">发现并复用高质量提示词模板，让生成、管理和部署都更顺手。</p>
              </div>
              <div class="hero-stats">
                <div class="hero-stat">
                  <span class="stat-value">{{ displayedList.length }}</span>
                  <span class="stat-label">Templates</span>
                </div>
                <div class="hero-stat">
                  <span class="stat-value">{{ categories.length }}</span>
                  <span class="stat-label">Categories</span>
                </div>
              </div>
            </div>

            <!-- Content Container -->
            <div class="content-container">
              <!-- Filter Bar -->
              <div class="filter-bar">
                <div class="filter-top">
                  <div class="search-box">
                    <svg class="search-icon" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                      <circle cx="11" cy="11" r="8"/>
                      <path d="m21 21-4.35-4.35"/>
                    </svg>
                    <input
                      v-model="search"
                      type="text"
                      placeholder="搜索模板..."
                      class="search-input"
                      @input="onSearch"
                    />
                  </div>
                  <div class="sort-box">
                    <button
                      :class="['sort-btn', { active: sortBy === 'createdAt' }]"
                      @click="changeSort('createdAt')"
                    >
                      <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                        <rect x="3" y="4" width="18" height="18" rx="2" ry="2"/>
                        <line x1="16" y1="2" x2="16" y2="6"/>
                        <line x1="8" y1="2" x2="8" y2="6"/>
                        <line x1="3" y1="10" x2="21" y2="10"/>
                      </svg>
                      最新
                    </button>
                    <button
                      :class="['sort-btn', { active: sortBy === 'likeCount' }]"
                      @click="changeSort('likeCount')"
                    >
                      <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                        <path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z"/>
                      </svg>
                      热门
                    </button>
                  </div>
                </div>
                <div class="category-tabs">
                  <button
                    :class="['cat-btn', { active: categoryId === null }]"
                    @click="selectCategory(null)"
                  >
                    全部
                  </button>
                  <button
                    v-for="cat in categories"
                    :key="cat.id"
                    :class="['cat-btn', { active: categoryId === cat.id }]"
                    @click="selectCategory(cat.id)"
                  >
                    {{ cat.name }}
                  </button>
                </div>
              </div>

              <!-- Grid Container -->
              <div class="grid-container" ref="gridRef">
                <!-- Loading State -->
                <div v-if="pending && list.length === 0" class="loading-state">
                  <div class="loading-spinner"></div>
                  <span>加载中...</span>
                </div>

                <!-- Empty State -->
                <div v-else-if="displayedList.length === 0" class="empty-state">
                  <div class="empty-icon">
                    <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
                      <path d="M14.5 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V7.5L14.5 2z"/>
                      <polyline points="14 2 14 8 20 8"/>
                      <line x1="9" y1="15" x2="15" y2="15"/>
                    </svg>
                  </div>
                  <h3>没有匹配的模板</h3>
                  <p>试试更换关键词或切换分类</p>
                </div>

                <!-- Cards Grid -->
                <div v-else class="cards-grid">
                  <div
                    v-for="item in displayedList"
                    :key="item.id"
                    class="prompt-card"
                    @click="open(item)"
                  >
                    <div class="card-header">
                      <span class="card-category" :class="{ default: !item.category }">
                        {{ item.category?.name || 'General' }}
                      </span>
                      <button class="card-bookmark" @click.stop="handleLike(item)">
                        <svg width="18" height="18" viewBox="0 0 24 24" :fill="item.liked ? 'currentColor' : 'none'" stroke="currentColor" stroke-width="2">
                          <polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"/>
                        </svg>
                      </button>
                    </div>

                    <div class="card-body">
                      <h3 class="card-task">{{ item.taskDescription || '暂无描述' }}</h3>
                      <p class="card-result">
                        <template v-if="item.promptSummary">{{ item.promptSummary }}</template>
                        <template v-else-if="item.generatedPrompt">{{ item.generatedPrompt.substring(0, 140) }}{{ item.generatedPrompt.length > 140 ? '...' : '' }}</template>
                        <span v-else class="result-empty">暂无生成结果</span>
                      </p>
                    </div>

                    <div class="card-tags">
                      <template v-if="item.tags?.length">
                        <span
                          v-for="t in item.tags.slice(0, 3)"
                          :key="t.id"
                          class="tag"
                          :style="{ background: t.color + '15', color: t.color, borderColor: t.color + '30' }"
                        >
                          {{ t.name }}
                        </span>
                      </template>
                      <template v-else-if="item.aiTags?.length">
                        <span
                          v-for="(tag, idx) in item.aiTags.slice(0, 3)"
                          :key="idx"
                          class="tag ai-tag"
                          :style="getTagStyle(idx)"
                        >
                          {{ tag }}
                        </span>
                      </template>
                      <span v-else class="tag-empty">No tags</span>
                    </div>

                    <div class="card-footer">
                      <div class="card-metrics">
                        <span class="metric-chip">
                          <svg width="12" height="12" viewBox="0 0 24 24" fill="currentColor">
                            <path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z"/>
                          </svg>
                          {{ item.likeCount || 0 }}
                        </span>
                        <span class="metric-chip">{{ fmt(item.createdAt) }}</span>
                      </div>
                      <button class="use-btn" @click.stop="usePrompt(item)">
                        使用
                        <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                          <path d="M5 12h14M12 5l7 7-7 7"/>
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
                  加载更多模板
                </div>
              </div>
            </div>
          </section>

          <!-- Detail Modal -->
          <PromptDetailModal :item="cur" :loading="detailLoading" @close="cur = null" @use="usePrompt" />
        </div>
      </div>
    </template>
  </AppLayout>
</template>

<script setup>
import { ref, onMounted, onUnmounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import AppLayout from '../components/layout/AppLayout.vue'
import PromptDetailModal from '../components/PromptDetailModal.vue'

const router = useRouter()
const search = ref('')
const categoryId = ref(null)
const sortBy = ref('createdAt')
const categories = ref([])
const pending = ref(false)
const detailLoading = ref(false)
const list = ref([])
const cur = ref(null)
const page = ref(1)
const size = ref(20)
const hasMore = ref(true)
const gridRef = ref(null)
const searchKeyword = computed(() => search.value.trim().toLowerCase())

const API = 'http://111.231.107.210:8080/api'

const loadCategories = async () => {
  try {
    const r = await fetch(`${API}/categories`)
    const d = await r.json()
    categories.value = d.data || []
  } catch (e) { console.error(e) }
}

const selectCategory = (id) => {
  categoryId.value = id
  page.value = 1
  hasMore.value = true
  loadData(true)
}

const loadData = async (reset = false) => {
  if (pending.value) return
  pending.value = true

  try {
    const p = reset ? 1 : page.value
    let url = `${API}/history/page?page=${p}&size=${size.value}&sortBy=${sortBy.value}&sortOrder=desc`
    if (categoryId.value) {
      url += `&categoryId=${categoryId.value}`
    }
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
    console.error('[Templates] Error:', e)
  }
  pending.value = false
}

const handleLike = async (item) => {
  try {
    await fetch(`${API}/history/${item.id}/like`, { method: 'POST' })
    item.likeCount = (item.likeCount || 0) + 1
    item.liked = true
  } catch (e) {
    console.error('[Templates] Like error:', e)
  }
}

const open = async (item) => {
  detailLoading.value = true
  cur.value = item
  try {
    const r = await fetch(`${API}/history/${item.id}`)
    const d = await r.json()
    if (d.success && d.data) {
      cur.value = d.data
    }
  } catch (e) {
    console.error('[Templates] Error loading detail:', e)
  }
  detailLoading.value = false
}

onMounted(() => {
  loadCategories()
  loadData(true)
  window.addEventListener('scroll', handleScroll)
})

onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll)
})

const handleScroll = () => {
  if (pending.value || !hasMore.value) return
  const scrollBottom = document.documentElement.scrollHeight - window.scrollY - window.innerHeight
  if (scrollBottom < 300) {
    page.value++
    loadData()
  }
}

const loadMore = () => {
  page.value++
  loadData()
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

const fmt = (t) => {
  if (!t) return ''
  const d = new Date(t)
  const now = new Date()
  const diff = now - d

  if (diff < 3600000) {
    const mins = Math.floor(diff / 60000)
    return mins < 1 ? '刚刚' : `${mins}分钟前`
  }
  if (diff < 86400000) {
    return `${Math.floor(diff / 3600000)}小时前`
  }

  const y = d.getFullYear()
  const m = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  return `${y}-${m}-${day}`
}

const tagColors = [
  { bg: '#f9731615', color: '#f97316', border: '#f9731630' },
  { bg: '#3b82f615', color: '#3b82f6', border: '#3b82f630' },
  { bg: '#14b8a615', color: '#14b8a6', border: '#14b8a630' },
  { bg: '#ec489915', color: '#ec4899', border: '#ec489930' },
  { bg: '#8b5cf615', color: '#8b5cf6', border: '#8b5cf630' },
  { bg: '#10b98115', color: '#10b981', border: '#10b98130' },
  { bg: '#ef444415', color: '#ef4444', border: '#ef444430' },
  { bg: '#f59e0b15', color: '#f59e0b', border: '#f59e0b30' },
]

const getTagStyle = (idx) => {
  const c = tagColors[idx % tagColors.length]
  return { background: c.bg, color: c.color, borderColor: c.border }
}

const displayedList = computed(() => {
  if (!searchKeyword.value) return list.value
  return list.value.filter((item) => {
    const haystack = [
      item.taskDescription,
      item.promptSummary,
      item.generatedPrompt,
      item.category?.name,
      ...(item.tags || []).map((tag) => tag.name),
      ...(item.aiTags || [])
    ]
      .filter(Boolean)
      .join(' ')
      .toLowerCase()

    return haystack.includes(searchKeyword.value)
  })
})

const usePrompt = (item) => {
  if (!item) return
  router.push({
    path: '/generate',
    query: {
      task: item.taskDescription,
      audience: item.targetAudience,
      format: item.outputFormat,
      tone: item.tone,
      length: item.length,
      constraints: item.constraints,
      examples: item.examples
    }
  })
  cur.value = null
}
</script>

<style scoped>
/* ============================================
   Page Wrapper
   ============================================ */
.page-wrapper {
  min-height: 100%;
}

.library-shell {
  display: grid;
  grid-template-columns: 80px minmax(0, 1fr);
  gap: 24px;
  min-height: 100%;
}

/* ============================================
   Side Rail
   ============================================ */
.library-rail {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  padding: 20px 14px;
  background: var(--bg-panel);
  border: 1px solid var(--border-color);
  border-radius: var(--radius-2xl);
  backdrop-filter: blur(16px);
  height: fit-content;
  position: sticky;
  top: 88px;
}

.rail-brand {
  width: 48px;
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: none;
  border-radius: var(--radius-xl);
  background: linear-gradient(135deg, var(--color-primary-600), var(--color-primary-700));
  color: white;
  cursor: pointer;
  transition: all var(--transition-base);
  box-shadow: 0 4px 16px rgba(59, 130, 246, 0.3);
}

.rail-brand:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(59, 130, 246, 0.4);
}

.rail-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
  align-items: center;
}

.rail-item {
  width: 44px;
  height: 44px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: none;
  border-radius: var(--radius-lg);
  background: transparent;
  color: var(--text-secondary);
  cursor: pointer;
  transition: all var(--transition-base);
}

.rail-item:hover {
  background: var(--bg-hover);
  color: var(--text-primary);
}

.rail-item.active,
.rail-item.active:hover {
  background: var(--glow-primary-soft);
  color: var(--color-primary-600);
}

.rail-create {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
  margin-top: auto;
  padding: 12px 8px;
  border: none;
  border-radius: var(--radius-lg);
  background: transparent;
  color: var(--text-secondary);
  font-size: 0.7rem;
  font-weight: 600;
  cursor: pointer;
  transition: all var(--transition-base);
}

.rail-create:hover {
  background: var(--glow-primary-soft);
  color: var(--color-primary-600);
}

/* ============================================
   Library Main
   ============================================ */
.library-main {
  min-width: 0;
}

/* ============================================
   Hero Header
   ============================================ */
.library-hero {
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
  font-size: clamp(2rem, 4vw, 3rem);
  font-weight: 900;
  color: var(--text-primary);
  margin: 0;
  letter-spacing: -0.04em;
  line-height: 1;
}

.hero-desc {
  margin: 14px 0 0;
  max-width: 600px;
  color: var(--text-secondary);
  font-size: var(--text-base);
  line-height: 1.7;
}

.hero-stats {
  display: flex;
  gap: 12px;
  flex-shrink: 0;
}

.hero-stat {
  min-width: 100px;
  padding: 16px 20px;
  background: var(--bg-card);
  border: 1px solid var(--border-color);
  border-radius: var(--radius-xl);
  text-align: center;
  backdrop-filter: blur(8px);
}

.stat-value {
  display: block;
  font-family: var(--font-display);
  font-size: 1.5rem;
  font-weight: 800;
  background: linear-gradient(135deg, var(--color-primary-600), var(--color-primary-400));
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.stat-label {
  display: block;
  margin-top: 4px;
  font-size: 0.7rem;
  font-weight: 600;
  letter-spacing: 0.1em;
  text-transform: uppercase;
  color: var(--text-muted);
}

/* ============================================
   Content Container
   ============================================ */
.content-container {
  padding: 32px;
  background: var(--bg-card);
  border: 1px solid var(--border-color);
  border-radius: var(--radius-2xl);
  backdrop-filter: blur(16px);
}

/* ============================================
   Filter Bar
   ============================================ */
.filter-bar {
  margin-bottom: 28px;
}

.filter-top {
  display: flex;
  gap: 16px;
  align-items: center;
  margin-bottom: 20px;
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
  background: var(--bg-input);
  border: 1px solid var(--border-color);
  border-radius: var(--radius-xl);
  transition: all var(--transition-base);
}

.search-input:focus {
  outline: none;
  border-color: var(--color-primary-500);
  box-shadow: 0 0 0 4px var(--glow-primary-soft);
  background: var(--bg-card);
}

.search-input::placeholder {
  color: var(--text-placeholder);
}

.sort-box {
  display: flex;
  gap: 8px;
}

.sort-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 18px;
  border: 1px solid var(--border-color);
  border-radius: var(--radius-xl);
  background: transparent;
  color: var(--text-secondary);
  font-family: var(--font-body);
  font-size: var(--text-sm);
  font-weight: 600;
  cursor: pointer;
  transition: all var(--transition-base);
}

.sort-btn:hover {
  border-color: var(--color-primary-500);
  color: var(--color-primary-600);
  background: var(--glow-primary-soft);
}

.sort-btn.active {
  border-color: var(--color-primary-500);
  background: linear-gradient(135deg, var(--color-primary-600), var(--color-primary-700));
  color: white;
}

.category-tabs {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.cat-btn {
  padding: 10px 18px;
  border: 1px solid var(--border-color);
  border-radius: var(--radius-full);
  background: transparent;
  color: var(--text-secondary);
  font-family: var(--font-body);
  font-size: var(--text-sm);
  font-weight: 600;
  cursor: pointer;
  transition: all var(--transition-base);
}

.cat-btn:hover {
  border-color: var(--color-primary-500);
  color: var(--color-primary-600);
  background: var(--glow-primary-soft);
}

.cat-btn.active {
  border-color: var(--color-primary-500);
  background: linear-gradient(135deg, var(--color-primary-600), var(--color-primary-700));
  color: white;
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.25);
}

/* ============================================
   Grid Container
   ============================================ */
.grid-container {
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
}

.empty-icon {
  width: 80px;
  height: 80px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--bg-hover);
  border-radius: var(--radius-2xl);
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
  margin: 0;
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

.loading-spinner.small {
  width: 24px;
  height: 24px;
  border-width: 2px;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

/* ============================================
   Cards Grid
   ============================================ */
.cards-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
}

/* ============================================
   Prompt Card
   ============================================ */
.prompt-card {
  display: flex;
  flex-direction: column;
  padding: 20px;
  background: var(--bg-card);
  border: 1px solid var(--border-color);
  border-radius: var(--radius-2xl);
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  overflow: hidden;
}

.prompt-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 3px;
  background: linear-gradient(90deg, var(--color-primary-500), var(--color-primary-400));
  opacity: 0;
  transition: opacity var(--transition-base);
}

.prompt-card:hover {
  transform: translateY(-6px);
  border-color: var(--glow-primary);
  box-shadow: var(--shadow-card-hover);
}

.prompt-card:hover::before {
  opacity: 1;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 16px;
}

.card-category {
  display: inline-flex;
  padding: 6px 12px;
  font-size: 0.7rem;
  font-weight: 700;
  letter-spacing: 0.08em;
  text-transform: uppercase;
  background: var(--glow-primary-soft);
  color: var(--color-primary-600);
  border-radius: var(--radius-full);
}

.card-category.default {
  background: var(--bg-hover);
  color: var(--text-muted);
}

.card-bookmark {
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: none;
  border-radius: var(--radius-md);
  background: transparent;
  color: var(--text-muted);
  cursor: pointer;
  transition: all var(--transition-base);
}

.card-bookmark:hover {
  background: var(--bg-hover);
  color: var(--color-warning);
}

.card-body {
  flex: 1;
  margin-bottom: 16px;
}

.card-task {
  font-family: var(--font-display);
  font-size: 1.1rem;
  font-weight: 700;
  color: var(--text-primary);
  margin: 0 0 10px;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.card-result {
  font-size: var(--text-sm);
  color: var(--text-secondary);
  line-height: 1.7;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
  margin: 0;
}

.result-empty {
  color: var(--text-muted);
  font-style: italic;
}

.card-tags {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
  min-height: 28px;
  margin-bottom: 16px;
}

.tag {
  display: inline-flex;
  padding: 4px 10px;
  font-size: 0.7rem;
  font-weight: 600;
  border-radius: var(--radius-full);
  border: 1px solid;
}

.tag.ai-tag {
  font-weight: 500;
}

.tag-empty {
  display: inline-flex;
  padding: 4px 10px;
  font-size: 0.7rem;
  font-weight: 500;
  background: var(--bg-hover);
  color: var(--text-muted);
  border-radius: var(--radius-full);
}

.card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 14px;
  border-top: 1px solid var(--border-color);
  gap: 12px;
}

.card-metrics {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.metric-chip {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 5px 10px;
  font-size: 0.7rem;
  font-weight: 600;
  background: var(--bg-hover);
  color: var(--text-secondary);
  border-radius: var(--radius-full);
}

.use-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 14px;
  border: none;
  border-radius: var(--radius-lg);
  background: var(--glow-primary-soft);
  color: var(--color-primary-600);
  font-family: var(--font-body);
  font-size: var(--text-sm);
  font-weight: 700;
  cursor: pointer;
  transition: all var(--transition-base);
}

.use-btn:hover {
  background: linear-gradient(135deg, var(--color-primary-600), var(--color-primary-700));
  color: white;
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.25);
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
  transition: all var(--transition-base);
}

.load-more-trigger:hover {
  color: var(--color-primary-600);
}

/* ============================================
   Responsive
   ============================================ */
@media (max-width: 1024px) {
  .library-shell {
    grid-template-columns: 1fr;
  }

  .library-rail {
    flex-direction: row;
    justify-content: center;
    position: sticky;
    top: 0;
    z-index: 10;
    border-radius: 0 0 var(--radius-2xl) var(--radius-2xl);
    margin: 0 -24px;
    padding: 12px 20px;
  }

  .rail-group {
    flex-direction: row;
  }

  .rail-create {
    flex-direction: row;
    margin-top: 0;
    margin-left: auto;
  }

  .rail-create span {
    display: inline;
  }
}

@media (max-width: 768px) {
  .library-hero {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }

  .hero-stats {
    width: 100%;
  }

  .hero-stat {
    flex: 1;
  }

  .content-container {
    padding: 20px;
    border-radius: var(--radius-xl);
  }

  .filter-top {
    flex-direction: column;
  }

  .sort-box {
    width: 100%;
  }

  .sort-btn {
    flex: 1;
    justify-content: center;
  }

  .cards-grid {
    grid-template-columns: 1fr;
  }
}
</style>
