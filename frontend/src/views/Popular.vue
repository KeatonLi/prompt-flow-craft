<template>
  <AppLayout>
    <template #main>
      <div class="page-wrapper">
        <div class="banner">
          <h1 class="banner-title">🏆 热门提示词</h1>
          <p class="banner-desc">最受大家喜爱的提示词排行</p>
        </div>

        <div class="content-container">
          <div v-if="pending && list.length === 0" class="loading-state">
            <div class="loading-spinner"></div>
            <span>加载中...</span>
          </div>
          
          <div v-else-if="list.length === 0" class="empty-state">
            <div class="empty-icon">📭</div>
            <h3>暂无数据</h3>
            <p>还没有热门提示词，快去创作吧！</p>
          </div>
          
          <div v-else class="cards-grid">
            <div 
              v-for="(item, idx) in list" 
              :key="item.id" 
              class="prompt-card" 
              @click="open(item)"
              :class="{ 'top-three': idx < 3 }"
            >
              <div class="card-rank">
                <span class="rank-badge" :class="'rank-' + (idx + 1)">
                  {{ idx < 3 ? ['🥇', '🥈', '🥉'][idx] : idx + 1 }}
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
                </template>
                <template v-else-if="item.aiTags?.length">
                  <span 
                    v-for="(tag, i) in item.aiTags.slice(0, 3)" 
                    :key="i" 
                    class="tag ai-tag"
                    :style="getTagStyle(i)"
                  >
                    {{ tag }}
                  </span>
                </template>
              </div>
              <div class="card-footer">
                <span class="card-likes">
                  <span class="like-icon">❤️</span>
                  {{ item.likeCount || 0 }}
                </span>
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
]

const getTagStyle = (idx) => tagColors[idx % tagColors.length]

const usePrompt = (item) => {
  router.push({ path: '/generate', query: { task: item.taskDescription } })
  cur.value = null
}

onMounted(() => loadData())
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

.loading-state,
.empty-state {
  text-align: center;
  padding: 80px 20px;
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
  border-top-color: #f59e0b;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
  margin: 0 auto 16px;
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
  border-color: #f59e0b;
  box-shadow: 0 8px 24px rgba(245, 158, 11, 0.12);
  transform: translateY(-3px);
}

.prompt-card.top-three {
  border-color: rgba(245, 158, 11, 0.3);
}

.card-rank {
  margin-bottom: 12px;
}

.rank-badge {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 36px;
  height: 36px;
  font-size: 1.2rem;
  font-weight: 700;
  border-radius: 10px;
  background: #f1f5f9;
  color: #64748b;
}

.rank-badge.rank-1 {
  background: linear-gradient(135deg, #ffd700 0%, #f59e0b 100%);
  color: white;
  box-shadow: 0 4px 12px rgba(245, 158, 11, 0.3);
}

.rank-badge.rank-2 {
  background: linear-gradient(135deg, #e5e7eb 0%, #9ca3af 100%);
  color: white;
  box-shadow: 0 4px 12px rgba(156, 163, 175, 0.3);
}

.rank-badge.rank-3 {
  background: linear-gradient(135deg, #fdba74 0%, #ea580c 100%);
  color: white;
  box-shadow: 0 4px 12px rgba(234, 88, 12, 0.3);
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

.card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 12px;
  border-top: 1px solid #f1f5f9;
}

.card-likes {
  font-size: 0.85rem;
  font-weight: 600;
  color: #ef4444;
  display: flex;
  align-items: center;
  gap: 4px;
}

.like-icon {
  font-size: 0.9rem;
}

.card-time {
  font-size: 0.8rem;
  color: #94a3b8;
}

/* 响应式 */
@media (max-width: 768px) {
  .content-container {
    padding: 16px;
  }
  
  .cards-grid {
    grid-template-columns: 1fr;
  }
}
</style>
