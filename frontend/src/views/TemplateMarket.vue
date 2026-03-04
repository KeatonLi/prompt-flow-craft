<template>
  <AppLayout>
    <template #main>
      <div class="page-container">
        <div class="banner">
          <h1 class="banner-title">📦 提示词模板市场</h1>
          <p class="banner-desc">精选专业模板，一键快速生成提示词</p>
        </div>

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
              {{ cat.name }} ({{ cat.count }})
            </button>
          </div>
        </div>

        <div class="content-area">
          <div v-if="pending && list.length === 0" class="loading">加载中...</div>
          <div v-else-if="list.length === 0" class="empty">暂无模板</div>
          <div v-else class="templates-grid">
            <div 
              v-for="item in list" 
              :key="item.id" 
              class="template-card"
              @click="openTemplate(item)"
            >
              <div class="card-icon">{{ item.icon }}</div>
              <div class="card-content">
                <div class="card-name">{{ item.name }}</div>
                <div class="card-desc">{{ item.description }}</div>
                <div class="card-tags">
                  <span class="tag">{{ getCategoryName(item.category) }}</span>
                </div>
              </div>
              <div class="card-action">
                <el-button type="primary" size="small" @click.stop="useTemplate(item)">
                  使用模板
                </el-button>
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
  // 跳转到首页并预填表单数据
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
.page-container { min-height: 100%; background: #f8fafc; margin: -24px; }
.banner { background: linear-gradient(135deg, #3b82f6 0%, #1d4ed8 100%); padding: 36px 40px; text-align: center; }
.banner-title { font-size: 1.6rem; font-weight: 700; color: white; margin: 0 0 6px; }
.banner-desc { color: rgba(255,255,255,0.85); font-size: 0.95rem; margin: 0; }

.filter-bar { padding: 16px 40px; background: white; border-bottom: 1px solid #e2e8f0; }
.category-tabs { display: flex; gap: 8px; flex-wrap: wrap; }
.cat-btn { padding: 8px 16px; font-size: 13px; border: 1px solid #e2e8f0; border-radius: 20px; background: white; color: #64748b; cursor: pointer; transition: all 0.2s; }
.cat-btn:hover { border-color: #3b82f6; color: #3b82f6; }
.cat-btn.active { background: #3b82f6; color: white; border-color: #3b82f6; }

.content-area { padding: 24px 40px 40px; min-height: 400px; }
.loading, .empty { text-align: center; padding: 60px 30px; color: #94a3b8; font-size: 1rem; }

.templates-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(340px, 1fr)); gap: 20px; }

.template-card { 
  background: white; 
  border-radius: 16px; 
  padding: 20px; 
  cursor: pointer; 
  border: 1px solid #e2e8f0; 
  transition: all 0.25s; 
  display: flex;
  flex-direction: column;
}
.template-card:hover { 
  border-color: #8b5cf6; 
  box-shadow: 0 8px 24px rgba(139, 92, 246, 0.15); 
  transform: translateY(-3px); 
}

.card-icon { font-size: 2.2rem; margin-bottom: 12px; }
.card-content { flex: 1; }
.card-name { font-weight: 700; color: #1e293b; font-size: 1.1rem; margin-bottom: 8px; }
.card-desc { color: #64748b; font-size: 0.9rem; line-height: 1.5; margin-bottom: 12px; }

.card-tags { display: flex; gap: 6px; flex-wrap: wrap; }
.tag { font-size: 0.75rem; padding: 4px 12px; border-radius: 12px; background: #f1f5f9; color: #64748b; }

.card-action { margin-top: 16px; padding-top: 16px; border-top: 1px solid #f1f5f9; }

/* 详情弹窗样式 */
.template-detail { padding: 8px 0; }
.detail-section { margin-bottom: 20px; }
.detail-label { font-weight: 600; color: #374151; font-size: 0.9rem; margin-bottom: 8px; }
.detail-value { color: #64748b; font-size: 0.95rem; line-height: 1.6; }
.detail-value.code { 
  background: #f8fafc; 
  padding: 12px 16px; 
  border-radius: 8px; 
  font-family: monospace; 
  font-size: 0.85rem;
  white-space: pre-wrap;
}
.detail-value.example { max-height: 120px; overflow-y: auto; }

.params-grid { display: flex; flex-direction: column; gap: 8px; }
.param-item { font-size: 0.9rem; }
.param-label { color: #64748b; }
.param-value { color: #1e293b; font-weight: 500; }
</style>
