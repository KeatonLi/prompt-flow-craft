<template>
  <AppLayout>
    <template #main>
      <div class="page-wrapper">
        <div class="banner">
          <h1 class="banner-title">💡 提示词大全</h1>
          <p class="banner-desc">探索和管理您生成的所有提示词</p>
        </div>

        <div class="content-container">
          <div class="filter-bar">
            <div class="search-box">
              <span class="search-icon">🔍</span>
              <input 
                v-model="search" 
                type="text" 
                placeholder="搜索提示词..." 
                class="search-input" 
                @input="onSearch" 
              />
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
                <span class="cat-icon">{{ cat.icon }}</span>
                {{ cat.name }}
              </button>
            </div>
          </div>

          <div class="grid-container" ref="gridRef">
            <div v-if="pending && list.length === 0" class="loading-state">
              <div class="loading-spinner"></div>
              <span>加载中...</span>
            </div>
            
            <div v-else-if="list.length === 0" class="empty-state">
              <div class="empty-icon">📭</div>
              <h3>暂无记录</h3>
              <p>开始生成您的第一个提示词吧</p>
            </div>
            
            <div v-else class="cards-grid">
              <div 
                v-for="item in list" 
                :key="item.id" 
                class="prompt-card" 
                @click="open(item)"
              >
                <div class="card-header">
                  <span 
                    v-if="item.category" 
                    class="card-category" 
                    :style="{ background: item.category.color + '15', color: item.category.color }"
                  >
                    <span class="category-icon">{{ item.category.icon }}</span>
                    {{ item.category.name }}
                  </span>
                  <span v-else class="card-category default">
                    <span class="category-icon">📄</span>
                    未分类
                  </span>
                </div>
                <div class="card-body">
                  <div class="card-task">{{ item.taskDescription?.substring(0, 60) }}{{ item.taskDescription?.length > 60 ? '...' : '' }}</div>
                  <div class="card-result">{{ item.promptSummary || item.generatedPrompt?.substring(0, 80) }}...</div>
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
                    <span v-if="item.tags.length > 3" class="tag-more">+{{ item.tags.length - 3 }}</span>
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
                  <span v-else class="tag-empty">暂无标签</span>
                </div>
                <div class="card-footer">
                  <span class="card-time">{{ fmt(item.createdAt) }}</span>
                  <span v-if="item.hitCount" class="card-hits">
                    <span class="hit-icon">👁</span>
                    {{ item.hitCount }}
                  </span>
                </div>
              </div>
            </div>
            
            <div v-if="pending && list.length > 0" class="loading-more">
              <div class="loading-spinner small"></div>
            </div>
            <div v-else-if="hasMore" class="load-more-trigger" @click="loadMore">
              点击加载更多
            </div>
          </div>
        </div>

        <PromptDetailModal :item="cur" :loading="detailLoading" @close="cur = null" @use="usePrompt" />
      </div>
    </template>
  </AppLayout>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import AppLayout from '../components/layout/AppLayout.vue'
import PromptDetailModal from '../components/PromptDetailModal.vue'

const router = useRouter()
const search = ref('')
const categoryId = ref(null)
const categories = ref([])
const pending = ref(false)
const detailLoading = ref(false)
const list = ref([])
const cur = ref(null)
const page = ref(1)
const size = ref(20)
const hasMore = ref(true)
const gridRef = ref(null)

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
    let url = `${API}/history/page?page=${p}&size=${size.value}`
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

.banner {
  background: linear-gradient(135deg, #3b82f6 0%, #1d4ed8 100%);
  padding: 40px 32px;
  text-align: center;
  position: relative;
  overflow: hidden;
}

.banner::before {
  content: '';
  position: absolute;
  top: -50%;
  left: -50%;
  width: 200%;
  height: 200%;
  background: radial-gradient(circle, rgba(255,255,255,0.1) 0%, transparent 60%);
  animation: pulse 4s ease-in-out infinite;
}

@keyframes pulse {
  0%, 100% { transform: scale(1); opacity: 0.5; }
  50% { transform: scale(1.1); opacity: 0.3; }
}

.banner-title {
  font-size: 1.75rem;
  font-weight: 700;
  color: white;
  margin: 0 0 8px;
  position: relative;
}

.banner-desc {
  color: rgba(255, 255, 255, 0.85);
  font-size: 1rem;
  margin: 0;
  position: relative;
}

.content-container {
  padding: 24px;
  max-width: 1400px;
  margin: 0 auto;
}

.filter-bar {
  background: white;
  border-radius: 16px;
  padding: 20px 24px;
  margin-bottom: 24px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
  border: 1px solid rgba(226, 232, 240, 0.6);
}

.search-box {
  position: relative;
  margin-bottom: 16px;
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
  padding: 12px 16px 12px 42px;
  font-size: 0.95rem;
  border: 1px solid #e2e8f0;
  border-radius: 10px;
  outline: none;
  transition: all 0.2s;
  background: #f8fafc;
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
  padding: 8px 16px;
  font-size: 0.85rem;
  font-weight: 500;
  border: 1px solid #e2e8f0;
  border-radius: 20px;
  background: white;
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

.cat-icon {
  font-size: 0.9rem;
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
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 20px;
}

.prompt-card {
  background: white;
  border-radius: 16px;
  padding: 20px;
  cursor: pointer;
  border: 1px solid #e2e8f0;
  transition: all 0.25s cubic-bezier(0.4, 0, 0.2, 1);
  display: flex;
  flex-direction: column;
  height: 100%;
}

.prompt-card:hover {
  border-color: #3b82f6;
  box-shadow: 0 8px 24px rgba(59, 130, 246, 0.12);
  transform: translateY(-3px);
}

.card-header {
  margin-bottom: 12px;
}

.card-category {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  font-size: 0.75rem;
  font-weight: 600;
  padding: 6px 12px;
  border-radius: 20px;
}

.card-category.default {
  background: #f1f5f9;
  color: #94a3b8;
}

.category-icon {
  font-size: 0.85rem;
}

.card-body {
  flex: 1;
  margin-bottom: 12px;
}

.card-task {
  font-weight: 600;
  color: #1e293b;
  font-size: 1rem;
  margin-bottom: 10px;
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.card-result {
  font-size: 0.85rem;
  color: #64748b;
  line-height: 1.6;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.card-tags {
  display: flex;
  gap: 6px;
  flex-wrap: wrap;
  margin-bottom: 16px;
  min-height: 24px;
}

.tag {
  font-size: 0.7rem;
  padding: 4px 10px;
  border-radius: 12px;
  border: 1px solid;
  font-weight: 500;
}

.tag.ai-tag {
  background: #f1f5f9;
  color: #64748b;
  border-color: #e2e8f0;
}

.tag-more {
  font-size: 0.7rem;
  padding: 4px 8px;
  background: #f1f5f9;
  color: #94a3b8;
  border-radius: 12px;
  font-weight: 500;
}

.tag-empty {
  font-size: 0.7rem;
  padding: 4px 10px;
  border-radius: 12px;
  background: #f1f5f9;
  color: #94a3b8;
}

.card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 12px;
  border-top: 1px solid #f1f5f9;
}

.card-time {
  font-size: 0.8rem;
  color: #94a3b8;
  font-weight: 500;
}

.card-hits {
  font-size: 0.8rem;
  color: #64748b;
  display: flex;
  align-items: center;
  gap: 4px;
}

.hit-icon {
  font-size: 0.75rem;
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
  .content-container {
    padding: 16px;
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
