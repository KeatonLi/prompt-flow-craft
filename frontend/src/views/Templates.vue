<template>
  <AppLayout>
    <template #main>
      <div class="page-container">
        <div class="banner">
          <h1 class="banner-title">💡 提示词大全</h1>
          <p class="banner-desc">你生成的所有提示词</p>
        </div>

        <div class="search-section">
          <input v-model="search" type="text" placeholder="搜索..." class="search-input" />
        </div>

        <div class="grid-container">
          <div v-if="pending" class="loading">加载中...</div>
          <div v-else-if="list.length === 0" class="empty">暂无记录</div>
          <div v-else class="cards-grid">
            <div v-for="item in list" :key="item.id" class="prompt-card" @click="open(item)">
              <div class="card-task">{{ item.taskDescription?.substring(0, 50) }}{{ item.taskDescription?.length > 50 ? '...' : '' }}</div>
              <div class="card-result">{{ item.generatedPrompt?.substring(0, 60) }}...</div>
              <div class="card-tags">
                <span v-for="t in item.tags" :key="t.id" class="tag" :style="{background: t.color+'20', color: t.color}">{{ t.name }}</span>
              </div>
              <div class="card-time">{{ fmt(item.createdAt) }}</div>
            </div>
          </div>
        </div>

        <PromptDetailModal :item="cur" @close="cur = null" @use="usePrompt" />
      </div>
    </template>
  </AppLayout>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import AppLayout from '../components/layout/AppLayout.vue'
import PromptDetailModal from '../components/PromptDetailModal.vue'

const router = useRouter()
const search = ref('')
const pending = ref(true)
const list = ref([])
const cur = ref(null)

const API = 'http://111.231.107.210:8080/api'

onMounted(async () => {
  try {
    const r = await fetch(`${API}/history/recent?limit=100`)
    const d = await r.json()
    list.value = d.data || []
  } catch (e) { console.error(e) }
  pending.value = false
})

const filtered = computed(() => {
  if (!search.value) return list.value
  const k = search.value.toLowerCase()
  return list.value.filter(i => 
    (i.taskDescription||'').toLowerCase().includes(k) ||
    (i.generatedPrompt||'').toLowerCase().includes(k)
  )
})

const fmt = (t) => {
  if (!t) return ''
  const d = new Date(t)
  return `${d.getMonth()+1}-${d.getDate()}`
}

const open = (item) => { cur.value = item }

const usePrompt = () => {
  if (cur.value) {
    router.push({ path: '/', query: { task: cur.value.taskDescription, audience: cur.value.targetAudience } })
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

.grid-container { padding: 20px 40px 40px; }
.loading, .empty { text-align: center; padding: 40px; color: #94a3b8; }

.cards-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(280px, 1fr)); gap: 14px; }

.prompt-card { background: white; border-radius: 12px; padding: 16px; cursor: pointer; border: 1px solid #e2e8f0; transition: all 0.15s; }
.prompt-card:hover { border-color: #3b82f6; box-shadow: 0 4px 12px rgba(59,130,246,0.1); transform: translateY(-1px); }

.card-task { font-weight: 600; color: #1e293b; font-size: 0.9rem; margin-bottom: 8px; line-height: 1.4; }
.card-result { font-size: 0.8rem; color: #64748b; margin-bottom: 10px; line-height: 1.4; }
.card-tags { display: flex; gap: 6px; flex-wrap: wrap; margin-bottom: 8px; }
.tag { font-size: 0.7rem; padding: 2px 8px; border-radius: 4px; }
.card-time { font-size: 0.75rem; color: #94a3b8; }
</style>
