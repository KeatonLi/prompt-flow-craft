<template>
  <AppLayout>
    <template #main>
      <div class="page-container">
        <div class="banner popular-banner">
          <h1 class="banner-title">🏆 热门提示词</h1>
          <p class="banner-desc">最受大家喜爱的提示词排行</p>
        </div>

        <div class="grid-container">
          <div v-if="pending && list.length === 0" class="loading">加载中...</div>
          <div v-else-if="list.length === 0" class="empty">暂无数据</div>
          <div v-else class="cards-grid">
            <div v-for="(item, idx) in list" :key="item.id" class="prompt-card" @click="open(item)">
              <div class="card-rank" v-if="idx < 3">
                <span class="rank-badge" :class="'rank-' + (idx + 1)">{{ ['🥇', '🥈', '🥉'][idx] }}</span>
              </div>
              <div class="card-task">{{ item.taskDescription?.substring(0, 50) }}{{ item.taskDescription?.length > 50 ? '...' : '' }}</div>
              <div class="card-result">{{ item.promptSummary || item.generatedPrompt?.substring(0, 50) }}...</div>
              <div class="card-tags">
                <template v-if="item.tags?.length">
                  <span v-for="t in item.tags" :key="t.id" class="tag" :style="{ background: t.color+'15', color: t.color }">{{ t.name }}</span>
                </template>
                <template v-else-if="item.aiTags?.length">
                  <span v-for="(tag, i) in item.aiTags" :key="i" class="tag" :style="getTagStyle(i)">{{ tag }}</span>
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
    const r = await fetch(`${API}/history/top-liked?page=1&limit=20`)
    const d = await r.json()
    list.value = d.data?.list || d.data || []
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

const tagColors = [
  { bg: '#f9731615', color: '#f97316' },
  { bg: '#3b82f615', color: '#3b82f6' },
  { bg: '#14b8a615', color: '#14b8a6' },
  { bg: '#ec489915', color: '#ec4899' },
  { bg: '#8b5cf615', color: '#8b5cf6' },
]
const getTagStyle = (idx) => tagColors[idx % tagColors.length]

const usePrompt = (item) => {
  router.push({ path: '/', query: { task: item.taskDescription } })
  cur.value = null
}

onMounted(() => loadData())
</script>

<style scoped>
.page-container { min-height: 100%; background: #f8fafc; margin: -24px; }
.popular-banner { background: linear-gradient(135deg, #3b82f6 0%, #1d4ed8 100%); }
.banner { padding: 36px 40px; text-align: center; }
.banner-title { font-size: 1.6rem; font-weight: 700; color: white; margin: 0 0 6px; }
.banner-desc { color: rgba(255,255,255,0.85); font-size: 0.95rem; margin: 0; }
.grid-container { padding: 20px 40px 40px; min-height: 400px; }
.loading, .empty { text-align: center; padding: 40px; color: #94a3b8; }
.cards-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(320px, 1fr)); gap: 16px; }
.prompt-card { background: white; border-radius: 14px; padding: 18px; cursor: pointer; border: 1px solid #e2e8f0; transition: all 0.2s; }
.prompt-card:hover { border-color: #3b82f6; box-shadow: 0 6px 20px rgba(59,130,246,0.15); transform: translateY(-2px); }
.card-rank { margin-bottom: 8px; }
.rank-badge { font-size: 1.2rem; }
.card-task { font-weight: 600; color: #1e293b; font-size: 0.95rem; margin-bottom: 10px; }
.card-result { font-size: 0.85rem; color: #64748b; margin-bottom: 12px; }
.card-tags { display: flex; gap: 6px; flex-wrap: wrap; margin-bottom: 12px; }
.tag { font-size: 0.7rem; padding: 3px 10px; border-radius: 12px; }
.card-footer { display: flex; justify-content: space-between; font-size: 0.8rem; color: #94a3b8; align-items: center; }
.card-likes { color: #ef4444; display: flex; align-items: center; gap: 4px; }
.card-likes::before { content: '❤️'; font-size: 0.9rem; }
</style>
