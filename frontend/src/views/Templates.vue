<template>
  <AppLayout>
    <template #main>
      <div class="page-container">
        <div class="banner">
          <h1 class="banner-title">💡 提示词大全</h1>
          <p class="banner-desc">你生成的所有提示词</p>
        </div>

        <div class="filter-bar">
          <div class="search-box">
            <input v-model="search" type="text" placeholder="搜索提示词..." class="search-input" @input="onSearch" />
          </div>
          <div class="category-tabs">
            <button 
              :class="['cat-btn', { active: categoryId === null }]" 
              @click="selectCategory(null)"
            >全部</button>
            <button 
              v-for="cat in categories" 
              :key="cat.id"
              :class="['cat-btn', { active: categoryId === cat.id }]"
              @click="selectCategory(cat.id)"
            >
              {{ cat.icon }} {{ cat.name }}
            </button>
          </div>
        </div>

        <div class="grid-container" ref="gridRef">
          <div v-if="pending && list.length === 0" class="loading">加载中...</div>
          <div v-else-if="list.length === 0" class="empty">暂无记录</div>
          <div v-else class="cards-grid">
            <div v-for="item in list" :key="item.id" class="prompt-card" @click="open(item)">
              <div class="card-header">
                <span v-if="item.category" class="card-category" :style="{ color: item.category.color }">
                  {{ item.category.icon }} {{ item.category.name }}
                </span>
              </div>
              <div class="card-task">{{ item.taskDescription?.substring(0, 50) }}{{ item.taskDescription?.length > 50 ? '...' : '' }}</div>
              <div class="card-result">{{ item.promptSummary || item.generatedPrompt?.substring(0, 50) }}...</div>
              <div class="card-tags">
                <template v-if="item.tags?.length">
                  <span v-for="t in item.tags" :key="t.id" class="tag" :style="{ background: t.color+'15', color: t.color, borderColor: t.color+'30' }">{{ t.name }}</span>
                </template>
                <template v-else-if="item.aiTags?.length">
                  <span v-for="(tag, idx) in item.aiTags" :key="idx" class="tag" :style="getTagStyle(idx)">{{ tag }}</span>
                </template>
                <span v-else class="tag-empty">无标签</span>
              </div>
              <div class="card-footer">
                <span class="card-time">{{ fmt(item.createdAt) }}</span>
                <span v-if="item.hitCount" class="card-hits">👁 {{ item.hitCount }}</span>
              </div>
            </div>
          </div>
          <div v-if="pending && list.length > 0" class="loading-more">加载中...</div>
          <div v-else-if="hasMore" class="load-more" @click="loadMore">点击加载更多</div>
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
    const r = await fetch(`${API}/category/all`)
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
    let url = `${API}/history/recent?page=${p}&limit=${size.value}`
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

// 点击卡片查看详情 - 调用API获取完整数据
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
  const y = d.getFullYear()
  const m = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  return `${y}${m}${day}`
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
  // 跳转到首页并回填表单数据
  router.push({ 
    path: '/', 
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
.page-container { min-height: 100%; background: #f8fafc; margin: -24px; }
.banner { background: linear-gradient(135deg, #3b82f6 0%, #1d4ed8 100%); padding: 36px 40px; text-align: center; }
.banner-title { font-size: 1.6rem; font-weight: 700; color: white; margin: 0 0 6px; }
.banner-desc { color: rgba(255,255,255,0.85); font-size: 0.95rem; margin: 0; }

.filter-bar { padding: 16px 40px; background: white; border-bottom: 1px solid #e2e8f0; }
.search-box { margin-bottom: 12px; }
.search-input { width: 100%; max-width: 400px; display: block; padding: 10px 16px; font-size: 14px; border: 1px solid #e2e8f0; border-radius: 8px; outline: none; transition: border-color 0.2s; }
.search-input:focus { border-color: #3b82f6; }

.category-tabs { display: flex; gap: 8px; flex-wrap: wrap; }
.cat-btn { padding: 6px 14px; font-size: 13px; border: 1px solid #e2e8f0; border-radius: 20px; background: white; color: #64748b; cursor: pointer; transition: all 0.2s; }
.cat-btn:hover { border-color: #3b82f6; color: #3b82f6; }
.cat-btn.active { background: #3b82f6; color: white; border-color: #3b82f6; }

.grid-container { padding: 20px 40px 40px; min-height: 400px; }
.loading, .empty, .loading-more, .load-more { text-align: center; padding: 30px; color: #94a3b8; }
.load-more { cursor: pointer; color: #3b82f6; font-weight: 500; }
.load-more:hover { color: #1d4ed8; }

.cards-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(320px, 1fr)); gap: 16px; }

.prompt-card { background: white; border-radius: 14px; padding: 18px; cursor: pointer; border: 1px solid #e2e8f0; transition: all 0.2s; }
.prompt-card:hover { border-color: #3b82f6; box-shadow: 0 6px 20px rgba(59,130,246,0.12); transform: translateY(-2px); }

.card-header { margin-bottom: 8px; }
.card-category { font-size: 12px; font-weight: 500; }

.card-task { font-weight: 600; color: #1e293b; font-size: 0.95rem; margin-bottom: 10px; line-height: 1.5; }
.card-result { font-size: 0.85rem; color: #64748b; margin-bottom: 12px; line-height: 1.5; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; }

.card-tags { display: flex; gap: 6px; flex-wrap: wrap; margin-bottom: 12px; min-height: 24px; }
.tag { font-size: 0.7rem; padding: 3px 10px; border-radius: 12px; border: 1px solid; font-weight: 500; }
.tag-empty { font-size: 0.7rem; padding: 3px 10px; border-radius: 12px; background: #f1f5f9; color: #94a3b8; }

.card-footer { display: flex; justify-content: space-between; align-items: center; }
.card-time { font-size: 0.75rem; color: #94a3b8; }
.card-hits { font-size: 0.75rem; color: #94a3b8; }
</style>
