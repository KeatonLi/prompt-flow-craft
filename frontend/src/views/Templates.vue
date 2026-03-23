<template>
  <AppLayout>
    <template #main>
      <div class="page-wrapper">
        <div class="library-shell">
          <aside class="library-rail">
            <button class="rail-brand">+</button>
            <div class="rail-group">
              <button class="rail-item active" type="button" title="模板库">📚</button>
              <button class="rail-item" type="button" title="收藏">☆</button>
              <button class="rail-item" type="button" title="历史">🕘</button>
              <button class="rail-item" type="button" title="探索">✦</button>
            </div>
            <button class="rail-create" type="button" @click="router.push('/generate')">+ New</button>
          </aside>

          <section class="library-main">
            <div class="library-hero">
              <div class="hero-copy">
                <div class="hero-kicker">Prompt Library</div>
                <h1 class="hero-title">Template Library</h1>
                <p class="hero-desc">发现并复用高质量提示词模板，让生成、管理和部署都更顺手。</p>
              </div>
              <div class="hero-meta">
                <div class="hero-stat">
                  <span class="hero-stat-value">{{ displayedList.length }}</span>
                  <span class="hero-stat-label">Templates</span>
                </div>
                <div class="hero-stat">
                  <span class="hero-stat-value">{{ categories.length }}</span>
                  <span class="hero-stat-label">Categories</span>
                </div>
              </div>
            </div>

            <div class="content-container">
              <div class="filter-bar">
                <div class="filter-top">
                  <div class="search-box">
                    <span class="search-icon">🔍</span>
                    <input
                      v-model="search"
                      type="text"
                      placeholder="Search templates..."
                      class="search-input"
                      @input="onSearch"
                    />
                  </div>
                  <div class="sort-box">
                    <button
                      :class="['sort-btn', { active: sortBy === 'createdAt' }]"
                      @click="changeSort('createdAt')"
                    >
                      最新
                    </button>
                    <button
                      :class="['sort-btn', { active: sortBy === 'likeCount' }]"
                      @click="changeSort('likeCount')"
                    >
                      热门
                    </button>
                  </div>
                </div>
                <div class="category-tabs">
                  <button
                    :class="['cat-btn', { active: categoryId === null }]"
                    @click="selectCategory(null)"
                  >
                    All
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

              <div class="grid-container" ref="gridRef">
                <div v-if="pending && list.length === 0" class="loading-state">
                  <div class="loading-spinner"></div>
                  <span>加载中...</span>
                </div>

                <div v-else-if="displayedList.length === 0" class="empty-state">
                  <div class="empty-icon">📭</div>
                  <h3>没有匹配的模板</h3>
                  <p>试试更换关键词或切换分类</p>
                </div>

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
                      <button class="card-bookmark" type="button" @click.stop="handleLike(item)">✦</button>
                    </div>

                    <div class="card-body">
                      <div class="card-task">{{ item.taskDescription || '暂无描述' }}</div>
                      <div class="card-result">
                        <template v-if="item.promptSummary">{{ item.promptSummary }}</template>
                        <template v-else-if="item.generatedPrompt">{{ item.generatedPrompt.substring(0, 140) }}{{ item.generatedPrompt.length > 140 ? '...' : '' }}</template>
                        <span v-else class="result-empty">暂无生成结果</span>
                      </div>
                    </div>

                    <div class="card-tags">
                      <template v-if="item.tags?.length">
                        <span
                          v-for="t in item.tags.slice(0, 3)"
                          :key="t.id"
                          class="tag"
                          :style="{ background: t.color + '12', color: t.color, borderColor: t.color + '24' }"
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
                        <span class="metric-chip">❤ {{ item.likeCount || 0 }}</span>
                        <span class="metric-chip">{{ fmt(item.createdAt) }}</span>
                      </div>
                      <button class="use-btn" type="button" @click.stop="usePrompt(item)">
                        Use Template →
                      </button>
                    </div>
                  </div>
                </div>

                <div v-if="pending && list.length > 0" class="loading-more">
                  <div class="loading-spinner small"></div>
                </div>
                <div v-else-if="hasMore" class="load-more-trigger" @click="loadMore">
                  Load more templates
                </div>
              </div>
            </div>
          </section>

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

// 加载分类
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

// 点赞（每次点击+1）
const handleLike = async (item) => {
  try {
    await fetch(`${API}/history/${item.id}/like`, { method: 'POST' })
    item.likeCount = (item.likeCount || 0) + 1
  } catch (e) {
    console.error('[Templates] Like error:', e)
  }
}

// 点击卡片查看详情
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
  
  // 小于1小时显示 relative time
  if (diff < 3600000) {
    const mins = Math.floor(diff / 60000)
    return mins < 1 ? '刚刚' : `${mins}分钟前`
  }
  // 小于24小时
  if (diff < 86400000) {
    return `${Math.floor(diff / 3600000)}小时前`
  }
  
  const y = d.getFullYear()
  const m = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  return `${y}-${m}-${day}`
}

// 标签颜色数组
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
.page-wrapper {
  min-height: 100%;
}

.library-shell {
  display: grid;
  grid-template-columns: 72px minmax(0, 1fr);
  gap: 22px;
  min-height: 100%;
}

.library-rail {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 18px;
  padding: 20px 12px;
  background: rgba(255, 255, 255, 0.72);
  border: 1px solid rgba(148, 163, 184, 0.16);
  border-radius: 28px;
  box-shadow: 0 18px 34px rgba(15, 23, 42, 0.05);
}

.rail-brand,
.rail-item {
  width: 40px;
  height: 40px;
  border: none;
  border-radius: 14px;
  background: rgba(239, 244, 255, 0.95);
  color: #3157c8;
  cursor: pointer;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.rail-brand {
  background: linear-gradient(135deg, #2258d8 0%, #1947b6 100%);
  color: white;
  font-weight: 700;
}

.rail-item.active,
.rail-item:hover,
.rail-brand:hover {
  transform: translateY(-1px);
  box-shadow: 0 10px 18px rgba(34, 88, 216, 0.18);
}

.rail-group {
  display: flex;
  flex-direction: column;
  gap: 10px;
  align-items: center;
}

.rail-create {
  margin-top: auto;
  border: none;
  border-radius: 999px;
  background: linear-gradient(135deg, #2258d8 0%, #1947b6 100%);
  color: white;
  padding: 9px 12px;
  font-size: 0.75rem;
  font-weight: 700;
  cursor: pointer;
}

.library-main {
  min-width: 0;
}

.library-hero {
  display: flex;
  justify-content: space-between;
  gap: 20px;
  padding: 8px 4px 22px;
  align-items: end;
}

.hero-kicker {
  font-size: 0.8rem;
  font-weight: 700;
  letter-spacing: 0.14em;
  text-transform: uppercase;
  color: #5270c7;
  margin-bottom: 10px;
}

.hero-title {
  margin: 0;
  font-size: clamp(2.2rem, 3vw, 3rem);
  line-height: 1;
  letter-spacing: -0.05em;
  color: #1b2333;
}

.hero-desc {
  margin: 14px 0 0;
  max-width: 700px;
  color: #68758c;
  font-size: 1rem;
  line-height: 1.7;
}

.hero-meta {
  display: flex;
  gap: 12px;
}

.hero-stat {
  min-width: 112px;
  padding: 16px 18px;
  border-radius: 20px;
  background: rgba(255, 255, 255, 0.74);
  border: 1px solid rgba(148, 163, 184, 0.16);
  box-shadow: 0 14px 30px rgba(15, 23, 42, 0.04);
}

.hero-stat-value {
  display: block;
  font-size: 1.35rem;
  font-weight: 800;
  color: #1741af;
}

.hero-stat-label {
  display: block;
  margin-top: 4px;
  color: #7b8799;
  font-size: 0.78rem;
  font-weight: 700;
  letter-spacing: 0.08em;
  text-transform: uppercase;
}

.content-container {
  padding: 28px;
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.78) 0%, rgba(248, 250, 252, 0.88) 100%);
  border: 1px solid rgba(148, 163, 184, 0.16);
  border-radius: 32px;
  box-shadow: 0 24px 48px rgba(15, 23, 42, 0.05);
}

.filter-bar {
  background: transparent;
  border-radius: 0;
  padding: 0 0 22px;
  margin-bottom: 24px;
  box-shadow: none;
  border: none;
}

.search-box {
  position: relative;
  flex: 1;
}

.filter-top {
  display: flex;
  gap: 16px;
  align-items: center;
  margin-bottom: 18px;
}

.sort-box {
  display: flex;
  align-items: center;
  gap: 8px;
}

.sort-btn {
  padding: 10px 16px;
  border: 1px solid rgba(148, 163, 184, 0.16);
  border-radius: 999px;
  font-size: 0.85rem;
  color: #64748b;
  background: rgba(255, 255, 255, 0.8);
  cursor: pointer;
  transition: all 0.2s;
}

.sort-btn:hover {
  border-color: #3b82f6;
  color: #3b82f6;
}

.sort-btn.active {
  background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
  color: white;
  border-color: transparent;
}

.search-icon {
  position: absolute;
  left: 14px;
  top: 50%;
  transform: translateY(-50%);
  font-size: 1rem;
  opacity: 0.5;
}

.search-input {
  width: 100%;
  padding: 14px 18px 14px 44px;
  font-size: 0.95rem;
  border: 1px solid rgba(148, 163, 184, 0.14);
  border-radius: 16px;
  outline: none;
  transition: all 0.2s;
  background: rgba(255, 255, 255, 0.88);
}

.search-input:focus {
  border-color: #3b82f6;
  background: white;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
}

.category-tabs {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.cat-btn {
  padding: 9px 16px;
  font-size: 0.85rem;
  font-weight: 600;
  border: 1px solid rgba(148, 163, 184, 0.14);
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.78);
  color: #64748b;
  cursor: pointer;
  transition: all 0.2s;
  display: inline-flex;
  align-items: center;
  gap: 6px;
}

.cat-btn:hover {
  border-color: #3b82f6;
  color: #3b82f6;
  background: rgba(59, 130, 246, 0.05);
}

.cat-btn.active {
  background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
  color: white;
  border-color: transparent;
  box-shadow: 0 2px 8px rgba(59, 130, 246, 0.25);
}

.grid-container {
  min-height: 400px;
}

.loading-state,
.empty-state {
  text-align: center;
  padding: 60px 20px;
  color: #94a3b8;
}

.empty-icon {
  font-size: 4rem;
  margin-bottom: 16px;
  opacity: 0.6;
}

.empty-state h3 {
  font-size: 1.25rem;
  font-weight: 600;
  color: #64748b;
  margin: 0 0 8px;
}

.empty-state p {
  margin: 0;
  font-size: 0.9rem;
}

.loading-spinner {
  width: 40px;
  height: 40px;
  border: 3px solid rgba(148, 163, 184, 0.3);
  border-top-color: #3b82f6;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
  margin: 0 auto 16px;
}

.loading-spinner.small {
  width: 24px;
  height: 24px;
  border-width: 2px;
  margin: 0 auto;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.cards-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(290px, 1fr));
  gap: 18px;
}

.prompt-card {
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.98) 0%, rgba(248, 250, 252, 0.96) 100%);
  border-radius: 18px;
  padding: 18px;
  cursor: pointer;
  border: 1px solid rgba(198, 208, 225, 0.72);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  display: flex;
  flex-direction: column;
  min-height: 266px;
  position: relative;
  overflow: hidden;
  box-shadow: 0 12px 28px rgba(15, 23, 42, 0.04);
}

.prompt-card:hover {
  border-color: #2657d6;
  box-shadow: 0 22px 34px rgba(38, 87, 214, 0.12);
  transform: translateY(-4px);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 18px;
}

.card-category {
  display: inline-flex;
  font-size: 0.68rem;
  font-weight: 800;
  padding: 6px 10px;
  border-radius: 999px;
  background: #eef4ff;
  color: #2b57c7;
  letter-spacing: 0.08em;
  text-transform: uppercase;
}

.card-category.default {
  background: #f0f3f8;
  color: #6e7d93;
}

.card-bookmark {
  border: none;
  background: transparent;
  color: #a2acbc;
  font-size: 1rem;
  cursor: pointer;
}

.card-body {
  flex: 1;
  margin-bottom: 16px;
}

.card-task {
  font-weight: 700;
  color: #151d2d;
  font-size: 1.2rem;
  line-height: 1.35;
  margin-bottom: 12px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.card-result {
  font-size: 0.87rem;
  color: #6d788a;
  line-height: 1.75;
  display: -webkit-box;
  -webkit-line-clamp: 4;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.result-empty {
  color: #94a3b8;
  font-style: italic;
}

.card-tags {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
  min-height: 30px;
  margin-bottom: 18px;
}

.tag {
  font-size: 0.72rem;
  padding: 5px 10px;
  border-radius: 999px;
  border: 1px solid;
  font-weight: 700;
}

.tag.ai-tag {
  font-weight: 600;
}

.tag-empty {
  font-size: 0.72rem;
  padding: 5px 10px;
  border-radius: 999px;
  background: #f1f5f9;
  color: #94a3b8;
}

.card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 14px;
  border-top: 1px solid #eef2f7;
  gap: 12px;
}

.card-metrics {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.metric-chip {
  font-size: 0.72rem;
  font-weight: 700;
  color: #59708d;
  background: #f3f6fb;
  border-radius: 999px;
  padding: 6px 10px;
}

.use-btn {
  border: none;
  background: transparent;
  color: #2258d8;
  font-size: 0.8rem;
  font-weight: 800;
  cursor: pointer;
}

.loading-more {
  text-align: center;
  padding: 24px;
}

.load-more-trigger {
  text-align: center;
  padding: 20px;
  color: #3b82f6;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
}

.load-more-trigger:hover {
  color: #1d4ed8;
}

/* 响应式 */
@media (max-width: 768px) {
  .library-shell {
    grid-template-columns: 1fr;
  }

  .library-rail {
    display: none;
  }

  .library-hero {
    flex-direction: column;
    align-items: flex-start;
    padding: 2px 2px 18px;
  }

  .hero-meta {
    width: 100%;
  }

  .hero-stat {
    flex: 1;
  }

  .content-container {
    padding: 20px 16px;
  }

  .filter-top {
    flex-direction: column;
    align-items: stretch;
  }

  .cards-grid {
    grid-template-columns: 1fr;
  }

  .category-tabs {
    overflow-x: auto;
    flex-wrap: nowrap;
    padding-bottom: 4px;
  }

  .cat-btn {
    white-space: nowrap;
  }
}
</style>
