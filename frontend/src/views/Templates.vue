<template>
  <AppLayout>
    <template #main>
      <div class="page-container">
        <div class="banner">
          <h1 class="banner-title">💡 提示词大全</h1>
          <p class="banner-desc">你生成的所有提示词</p>
        </div>

        <div class="search-section">
          <input v-model="search" type="text" placeholder="搜索..." class="search-input" @input="onSearch" />
        </div>

        <div class="grid-container" ref="gridRef">
          <div v-if="pending && list.length === 0" class="loading">加载中...</div>
          <div v-else-if="list.length === 0" class="empty">暂无记录</div>
          <div v-else class="cards-grid">
            <div v-for="item in list" :key="item.id" class="prompt-card" @click="open(item)">
              <div class="card-task">{{ item.taskDescription?.substring(0, 50) }}{{ item.taskDescription?.length > 50 ? '...' : '' }}</div>
              <div class="card-result">{{ item.generatedPrompt?.substring(0, 30) }}...</div>
              <div class="card-tags">
                <span v-for="t in item.tags" :key="t.id" class="tag" :style="{background: t.color+'20', color: t.color}">{{ t.name }}</span>
                <span v-if="!item.tags?.length" class="tag-empty">无标签</span>
              </div>
              <div class="card-time">{{ fmt(item.createdAt) }}</div>
            </div>
          </div>
          <div v-if="pending && list.length > 0" class="loading-more">加载中...</div>
          <div v-if="!pending && hasMore" class="load-more" @click="loadMore">点击加载更多</div>
        </div>

        <PromptDetailModal :item="cur" @close="cur = null" @use="usePrompt" />
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
const pending = ref(true)
const list = ref([])
const cur = ref(null)
const page = ref(1)
const size = ref(20)
const hasMore = ref(true)
const gridRef = ref(null)

const API = 'http://111.231.107.210:8080/api'

const loadData = async (reset = false) => {
  if (pending.value) return
  pending.value = true
  
  try {
    const p = reset ? 1 : page.value
    const r = await fetch(`${API}/history/recent?page=${p}&limit=${size.value}`)
    const d = await r.json()
    const newList = d.data?.list || d.data || []
    
    if (reset) {
      list.value = newList
    } else {
      list.value = [...list.value, ...newList]
    }
    hasMore.value = newList.length >= size.value
  } catch (e) { console.error(e) }
  pending.value = false
}

onMounted(() => {
  loadData(true)
  
  // 滚动加载
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

const onSearch = () => {
  page.value = 1
  hasMore.value = true
  loadData(true)
}

const fmt = (t) => {
  if (!t) return ''
  const d = new Date(t)
  return `${d.getMonth()+1}-${d.getDate()}`
}

const open = (item) => { cur.value = item }

const usePrompt = async () => {
  if (cur.value) {
    // 复制结果
    if (cur.value.generatedPrompt) {
      await navigator.clipboard.writeText(cur.value.generatedPrompt)
    }
    // 跳转首页回填
    router.push({ 
      path: '/', 
      query: { 
        task: cur.value.taskDescription, 
        audience: cur.value.targetAudience 
      } 
    })
    cur.value = null
  }
}
</script>

<style scoped>
.page-container { min-height: 100%; background: #f8fafc; margin: -24px; }
.banner { background: linear-gradient(135deg, #3b82f6 0%, #1d4ed8 100%); padding: 36px 40px; text-align: center; }
.banner-title { font-size: 1.6rem; font-weight: 700; color: white; margin: 0 0 6px; }
.banner-desc { color: rgba(255,255,255,0.85); font-size: 0.95rem; margin: 0; }
.search-section { padding: 16px 40px; margin-top: -16px; }
.search-input { width: 100%; max-width: 500px; display: block; margin: 0 auto; padding: 12px 16px; font-size: 15px; border: none; border-radius: 10px; box-shadow: 0 2px 10px rgba(0,0,0,0.08); outline: none; }

.grid-container { padding: 20px 40px 40px; min-height: 400px; }
.loading, .empty, .loading-more, .load-more { text-align: center; padding: 30px; color: #94a3b8; }
.load-more { cursor: pointer; color: #3b82f6; }

.cards-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(280px, 1fr)); gap: 14px; }

.prompt-card { background: white; border-radius: 12px; padding: 16px; cursor: pointer; border: 1px solid #e2e8f0; transition: all 0.15s; }
.prompt-card:hover { border-color: #3b82f6; box-shadow: 0 4px 12px rgba(59,130,246,0.1); transform: translateY(-1px); }

.card-task { font-weight: 600; color: #1e293b; font-size: 0.9rem; margin-bottom: 8px; line-height: 1.4; }
.card-result { font-size: 0.8rem; color: #64748b; margin-bottom: 10px; line-height: 1.4; }
.card-tags { display: flex; gap: 6px; flex-wrap: wrap; margin-bottom: 8px; }
.tag { font-size: 0.7rem; padding: 2px 8px; border-radius: 4px; }
.tag-empty { font-size: 0.7rem; padding: 2px 8px; border-radius: 4px; background: #f1f5f9; color: #94a3b8; }
.card-time { font-size: 0.75rem; color: #94a3b8; }
</style>
