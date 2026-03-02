<template>
  <AppLayout>
    <template #main>
      <div class="page-container">
        <div class="banner fav-banner">
          <h1 class="banner-title">⭐ 我的收藏</h1>
          <p class="banner-desc">你收藏的提示词</p>
        </div>

        <div class="grid-container">
          <div v-if="pending && list.length === 0" class="loading">加载中...</div>
          <div v-else-if="list.length === 0" class="empty">
            <p>暂无收藏内容</p>
            <p class="empty-tip">点击提示词卡片上的❤️即可收藏</p>
          </div>
          <div v-else class="cards-grid">
            <div v-for="item in list" :key="item.id" class="prompt-card" @click="open(item)">
              <div class="card-task">{{ item.taskDescription?.substring(0, 50) }}{{ item.taskDescription?.length > 50 ? '...' : '' }}</div>
              <div class="card-result">{{ item.promptSummary || item.generatedPrompt?.substring(0, 50) }}...</div>
              <div class="card-tags">
                <template v-if="item.tags?.length">
                  <span v-for="t in item.tags" :key="t.id" class="tag" :style="{ background: t.color+'15', color: t.color }">{{ t.name }}</span>
                </template>
              </div>
              <div class="card-footer">
                <span class="card-likes">❤️ {{ item.likeCount || 0 }}</span>
                <span class="card-time">{{ fmt(item.createdAt) }}</span>
              </div>
            </div>
          </div>
        </div>

        <PromptDetailModal :item="cur" :loading="detailLoading" @close="cur = null" @use="usePrompt" />
      </div>
    </template>
  </AppLayout>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import AppLayout from '../components/layout/AppLayout.vue'
import PromptDetailModal from '../components/PromptDetailModal.vue'

const router = useRouter()
const pending = ref(false)
const list = ref([])
const cur = ref(null)
const detailLoading = ref(false)

const API = 'http://111.231.107.210:8080/api'

const loadData = async () => {
  pending.value = true
  try {
    const r = await fetch(`${API}/history/recent?limit=50`)
    const d = await r.json()
    list.value = d.data || []
  } catch (e) { console.error(e) }
  pending.value = false
}

const open = async (item) => {
  detailLoading.value = true
  cur.value = item
  try {
    const r = await fetch(`${API}/history/${item.id}`)
    const d = await r.json()
    if (d.success && d.data) cur.value = d.data
  } catch (e) { console.error(e) }
  detailLoading.value = false
}

const fmt = (t) => {
  if (!t) return ''
  const d = new Date(t)
  return `${d.getFullYear()}${(d.getMonth()+1+'').padStart(2,'0')}${(d.getDate()+'').padStart(2,'0')}`
}

const usePrompt = (item) => {
  router.push({ path: '/', query: { task: item.taskDescription } })
  cur.value = null
}

onMounted(() => loadData())
</script>

<style scoped>
.page-container { min-height: 100%; background: #f8fafc; margin: -24px; }
.fav-banner { background: linear-gradient(135deg, #ec4899 0%, #be185d 100%); }
.banner { padding: 36px 40px; text-align: center; }
.banner-title { font-size: 1.6rem; font-weight: 700; color: white; margin: 0 0 6px; }
.banner-desc { color: rgba(255,255,255,0.85); font-size: 0.95rem; margin: 0; }
.grid-container { padding: 20px 40px 40px; min-height: 400px; }
.loading, .empty { text-align: center; padding: 40px; color: #94a3b8; }
.empty-tip { font-size: 0.85rem; margin-top: 8px; }
.cards-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(320px, 1fr)); gap: 16px; }
.prompt-card { background: white; border-radius: 14px; padding: 18px; cursor: pointer; border: 1px solid #e2e8f0; transition: all 0.2s; }
.prompt-card:hover { border-color: #ec4899; box-shadow: 0 6px 20px rgba(236,72,153,0.15); transform: translateY(-2px); }
.card-task { font-weight: 600; color: #1e293b; font-size: 0.95rem; margin-bottom: 10px; }
.card-result { font-size: 0.85rem; color: #64748b; margin-bottom: 12px; }
.card-tags { display: flex; gap: 6px; flex-wrap: wrap; margin-bottom: 12px; }
.tag { font-size: 0.7rem; padding: 3px 10px; border-radius: 12px; }
.card-footer { display: flex; justify-content: space-between; font-size: 0.8rem; color: #94a3b8; }
.card-likes { color: #ef4444; }
</style>
