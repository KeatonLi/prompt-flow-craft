<template>
  <AppLayout>
    <template #main>
      <div class="page-wrapper">
        <div class="banner">
          <h1 class="banner-title">📦 模板市场</h1>
          <p class="banner-desc">精选专业模板，一键快速生成提示词</p>
        </div>

        <div class="content-container">
          <div class="filter-bar">
            <div class="category-tabs">
              <button 
                :class="['cat-btn', { active: categoryId === null }]" 
                @click="selectCategory(null)"
              >
                全部模板
              </button>
              <button 
                v-for="cat in categories" 
                :key="cat.id"
                :class="['cat-btn', { active: categoryId === cat.id }]"
                @click="selectCategory(cat.id)"
              >
                {{ cat.name }}
                <span class="count-badge">{{ cat.count }}</span>
              </button>
            </div>
          </div>

          <div class="content-area">
            <div v-if="pending && list.length === 0" class="loading-state">
              <div class="loading-spinner"></div>
              <span>加载中...</span>
            </div>
            
            <div v-else-if="list.length === 0" class="empty-state">
              <div class="empty-icon">📦</div>
              <h3>暂无模板</h3>
              <p>该分类下暂无模板</p>
            </div>
            
            <div v-else class="templates-grid">
              <div 
                v-for="item in list" 
                :key="item.id" 
                class="template-card"
                @click="openTemplate(item)"
              >
                <div class="card-header">
                  <div class="card-icon">{{ item.icon }}</div>
                  <div class="card-badges">
                    <span class="badge">{{ getCategoryName(item.category) }}</span>
                  </div>
                </div>
                <div class="card-content">
                  <h3 class="card-name">{{ item.name }}</h3>
                  <p class="card-desc">{{ item.description }}</p>
                </div>
                <div class="card-action">
                  <el-button type="primary" size="small" @click.stop="useTemplate(item)">
                    <span class="btn-icon">⚡</span>
                    使用模板
                  </el-button>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 模板详情弹窗 -->
        <el-dialog
          v-model="showDetail"
          :title="`${currentTemplate?.icon || ''} ${currentTemplate?.name || ''}`"
          width="600px"
          :close-on-click-modal="false"
          class="template-dialog"
        >
          <div v-if="currentTemplate" class="template-detail">
            <div class="detail-section">
              <div class="detail-label">描述</div>
              <div class="detail-value">{{ currentTemplate.description }}</div>
            </div>
            <div class="detail-section">
              <div class="detail-label">预设任务描述</div>
              <div class="detail-value code">{{ currentTemplate.taskDescription }}</div>
            </div>
            <div class="detail-section">
              <div class="detail-label">预设参数</div>
              <div class="params-grid">
                <div class="param-item">
                  <span class="param-label">目标受众：</span>
                  <span class="param-value">{{ getAudienceText(currentTemplate.targetAudience) }}</span>
                </div>
                <div class="param-item">
                  <span class="param-label">输出格式：</span>
                  <span class="param-value">{{ getFormatText(currentTemplate.outputFormat) }}</span>
                </div>
                <div class="param-item">
                  <span class="param-label">语调风格：</span>
                  <span class="param-value">{{ getToneText(currentTemplate.tone) }}</span>
                </div>
              </div>
            </div>
            <div v-if="currentTemplate.examples" class="detail-section">
              <div class="detail-label">参考示例</div>
              <div class="detail-value code example">{{ currentTemplate.examples }}</div>
            </div>
          </div>
          <template #footer>
            <el-button @click="showDetail = false">关闭</el-button>
            <el-button type="primary" @click="useTemplate(currentTemplate)">
              使用此模板
            </el-button>
          </template>
        </el-dialog>
      </div>
    </template>
  </AppLayout>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import AppLayout from '../components/layout/AppLayout.vue'

const router = useRouter()
const API = 'http://111.231.107.210:8080/api'

const categories = ref([])
const list = ref([])
const pending = ref(false)
const categoryId = ref(null)
const showDetail = ref(false)
const currentTemplate = ref(null)

// 加载分类
const loadCategories = async () => {
  try {
    const r = await fetch(`${API}/template/categories`)
    const d = await r.json()
    if (d.success) {
      categories.value = d.data || []
    }
  } catch (e) {
    console.error('[Template] Error loading categories:', e)
  }
}

// 加载模板列表
const loadTemplates = async () => {
  pending.value = true
  try {
    let url = `${API}/template/list`
    if (categoryId.value) {
      url += `?category=${categoryId.value}`
    }
    const r = await fetch(url)
    const d = await r.json()
    if (d.success) {
      list.value = d.data || []
    }
  } catch (e) {
    console.error('[Template] Error loading templates:', e)
  }
  pending.value = false
}

const selectCategory = (id) => {
  categoryId.value = id
  loadTemplates()
}

const openTemplate = (item) => {
  currentTemplate.value = item
  showDetail.value = true
}

const useTemplate = (item) => {
  if (!item) return
  router.push({ 
    path: '/', 
    query: { 
      task: item.taskDescription, 
      audience: item.targetAudience,
      format: item.outputFormat,
      tone: item.tone,
      examples: item.examples,
      fromTemplate: 'true'
    } 
  })
  showDetail.value = false
}

const getCategoryName = (catId) => {
  const cat = categories.value.find(c => c.id === catId)
  return cat ? cat.name : catId
}

const getAudienceText = (val) => {
  const map = {
    'general': '普通用户',
    'professional': '专业人士',
    'student': '学生',
    'developer': '开发者',
    'creator': '创作者'
  }
  return map[val] || val
}

const getFormatText = (val) => {
  const map = {
    'text': '文本',
    'list': '列表',
    'table': '表格',
    'code': '代码',
    'json': 'JSON',
    'markdown': 'Markdown'
  }
  return map[val] || val
}

const getToneText = (val) => {
  const map = {
    'formal': '正式',
    'friendly': '友好',
    'technical': '技术',
    'enthusiastic': '热情',
    'artistic': '文艺'
  }
  return map[val] || val
}

onMounted(() => {
  loadCategories()
  loadTemplates()
})
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

.category-tabs {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.cat-btn {
  padding: 10px 18px;
  font-size: 0.9rem;
  font-weight: 500;
  border: 1px solid #e2e8f0;
  border-radius: 20px;
  background: white;
  color: #64748b;
  cursor: pointer;
  transition: all 0.2s;
  display: inline-flex;
  align-items: center;
  gap: 8px;
}

.cat-btn:hover {
  border-color: #8b5cf6;
  color: #8b5cf6;
  background: rgba(139, 92, 246, 0.05);
}

.cat-btn.active {
  background: linear-gradient(135deg, #8b5cf6 0%, #6d28d9 100%);
  color: white;
  border-color: transparent;
  box-shadow: 0 2px 8px rgba(139, 92, 246, 0.25);
}

.count-badge {
  font-size: 0.75rem;
  padding: 2px 8px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 10px;
  font-weight: 600;
}

.content-area {
  min-height: 400px;
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
  border-top-color: #8b5cf6;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
  margin: 0 auto 16px;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.templates-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 20px;
}

.template-card {
  background: white;
  border-radius: 16px;
  padding: 24px;
  cursor: pointer;
  border: 1px solid #e2e8f0;
  transition: all 0.25s cubic-bezier(0.4, 0, 0.2, 1);
  display: flex;
  flex-direction: column;
  height: 100%;
}

.template-card:hover {
  border-color: #8b5cf6;
  box-shadow: 0 8px 24px rgba(139, 92, 246, 0.15);
  transform: translateY(-3px);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 16px;
}

.card-icon {
  font-size: 2.5rem;
  line-height: 1;
}

.card-badges {
  display: flex;
  gap: 6px;
}

.badge {
  font-size: 0.7rem;
  padding: 4px 10px;
  background: #f1f5f9;
  color: #64748b;
  border-radius: 12px;
  font-weight: 500;
}

.card-content {
  flex: 1;
  margin-bottom: 20px;
}

.card-name {
  font-weight: 700;
  color: #1e293b;
  font-size: 1.1rem;
  margin: 0 0 10px;
}

.card-desc {
  color: #64748b;
  font-size: 0.9rem;
  line-height: 1.6;
  margin: 0;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.card-action {
  padding-top: 16px;
  border-top: 1px solid #f1f5f9;
}

.btn-icon {
  margin-right: 4px;
}

/* 详情弹窗样式 */
.template-detail {
  padding: 8px 0;
}

.detail-section {
  margin-bottom: 24px;
}

.detail-section:last-child {
  margin-bottom: 0;
}

.detail-label {
  font-weight: 600;
  color: #374151;
  font-size: 0.85rem;
  margin-bottom: 10px;
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.detail-value {
  color: #4b5563;
  font-size: 0.95rem;
  line-height: 1.7;
}

.detail-value.code {
  background: #f8fafc;
  padding: 16px;
  border-radius: 10px;
  font-family: 'JetBrains Mono', 'Fira Code', monospace;
  font-size: 0.85rem;
  white-space: pre-wrap;
  border: 1px solid #e2e8f0;
}

.detail-value.example {
  max-height: 150px;
  overflow-y: auto;
}

.params-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 12px;
}

.param-item {
  font-size: 0.9rem;
  padding: 10px 14px;
  background: #f8fafc;
  border-radius: 8px;
}

.param-label {
  color: #64748b;
  font-size: 0.8rem;
}

.param-value {
  color: #1e293b;
  font-weight: 600;
  margin-left: 4px;
}

/* 响应式 */
@media (max-width: 768px) {
  .content-container {
    padding: 16px;
  }
  
  .templates-grid {
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

/* 暗黑模式 */
:root.dark .banner {
  background: linear-gradient(135deg, #7c3aed 0%, #5b21b6 100%);
}

:root.dark .filter-bar {
  background: rgba(30, 41, 59, 0.6);
  border-color: rgba(51, 65, 85, 0.6);
}

:root.dark .cat-btn {
  background: rgba(30, 41, 59, 0.6);
  border-color: #334155;
  color: #94a3b8;
}

:root.dark .cat-btn:hover {
  border-color: #8b5cf6;
  color: #a78bfa;
}

:root.dark .template-card {
  background: rgba(30, 41, 59, 0.6);
  border-color: rgba(51, 65, 85, 0.6);
}

:root.dark .template-card:hover {
  border-color: #8b5cf6;
  box-shadow: 0 8px 24px rgba(139, 92, 246, 0.2);
}

:root.dark .card-name {
  color: #f1f5f9;
}

:root.dark .card-desc {
  color: #94a3b8;
}

:root.dark .badge {
  background: rgba(51, 65, 85, 0.8);
  color: #94a3b8;
}

:root.dark .card-action {
  border-color: rgba(51, 65, 85, 0.6);
}

:root.dark .detail-label {
  color: #94a3b8;
}

:root.dark .detail-value {
  color: #cbd5e1;
}

:root.dark .detail-value.code,
:root.dark .param-item {
  background: rgba(15, 23, 42, 0.6);
  border-color: #334155;
}

:root.dark .param-value {
  color: #e2e8f0;
}

:root.dark .param-label {
  color: #94a3b8;
}
</style>
