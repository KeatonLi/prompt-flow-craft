<template>
  <AppLayout>
    <template #main>
      <div class="style-transfer-page">
        <!-- 顶部横幅 -->
        <div class="banner">
          <div class="banner-icon">🎨</div>
          <h1 class="banner-title">提示词风格迁移器</h1>
          <p class="banner-desc">一键将提示词转换为不同风格，找到最适合你的表达方式</p>
        </div>

        <div class="main-content">
          <!-- 输入区域 -->
          <div class="input-section">
            <div class="section-header">
              <span class="section-title">📝 原始提示词</span>
              <el-button text type="primary" @click="loadExample">
                加载示例
              </el-button>
            </div>
            <el-input
              v-model="originalPrompt"
              type="textarea"
              :rows="8"
              placeholder="请输入需要转换风格的提示词..."
              show-word-limit
              :maxlength="5000"
            />
          </div>

          <!-- 风格选择 -->
          <div class="style-section">
            <div class="section-header">
              <span class="section-title">🎯 目标风格</span>
              <el-switch
                v-model="batchMode"
                active-text="批量转换"
                inactive-text="单风格转换"
              />
            </div>
            
            <div class="style-grid">
              <div
                v-for="style in styles"
                :key="style.id"
                :class="['style-card', { active: isSelected(style.id) }]"
                @click="toggleStyle(style.id)"
              >
                <div class="style-icon">{{ getStyleIcon(style.id) }}</div>
                <div class="style-name">{{ style.name }}</div>
                <div class="style-desc">{{ style.desc }}</div>
              </div>
            </div>
          </div>

          <!-- 转换按钮 -->
          <div class="action-section">
            <el-button 
              type="primary" 
              size="large"
              :loading="transforming"
              :disabled="!canTransform"
              @click="transformPrompt"
            >
              {{ transforming ? '转换中...' : '开始转换' }}
            </el-button>
            <el-button 
              size="large"
              @click="reset"
            >
              重置
            </el-button>
          </div>

          <!-- 结果展示 -->
          <div v-if="results.length > 0" class="results-section">
            <div class="section-header">
              <span class="section-title">✨ 转换结果</span>
              <el-tag type="success">{{ results.length }} 个结果</el-tag>
            </div>
            
            <div class="results-grid">
              <div v-for="(result, idx) in results" :key="idx" class="result-card">
                <div class="result-header">
                  <span class="result-style">{{ getStyleName(result.style) }}</span>
                  <div class="result-actions">
                    <el-button text size="small" @click="copyResult(result.prompt)">
                      复制
                    </el-button>
                    <el-button text size="small" type="primary" @click="usePrompt(result.prompt)">
                      使用
                    </el-button>
                  </div>
                </div>
                <div class="result-content">
                  {{ result.prompt }}
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 复制成功提示 -->
        <el-transition name="fade">
          <div v-if="showCopySuccess" class="copy-toast">
            ✅ 已复制到剪贴板
          </div>
        </el-transition>
      </div>
    </template>
  </AppLayout>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import AppLayout from '../components/layout/AppLayout.vue'

const router = useRouter()
const API = 'http://111.231.107.210:8080/api'

// 数据
const originalPrompt = ref('')
const styles = ref([])
const selectedStyles = ref([])
const batchMode = ref(false)
const transforming = ref(false)
const results = ref([])
const showCopySuccess = ref(false)

// 示例提示词
const examplePrompt = `你是一位专业的AI助手，擅长回答各种问题。请用简洁清晰的语言回答用户的问题，并提供准确的答案。`

// 加载风格列表
const loadStyles = async () => {
  try {
    const res = await fetch(`${API}/prompt/styles`)
    const data = await res.json()
    if (data.success || data.code === 0) {
      styles.value = data.data || []
    }
  } catch (e) {
    console.error('加载风格列表失败:', e)
    // 使用默认风格
    styles.value = [
      { id: 'concise', name: '简洁版', desc: '去除冗余信息，保留核心要点' },
      { id: 'detailed', name: '详细版', desc: '补充更多细节和上下文' },
      { id: 'formal', name: '正式版', desc: '使用正式、专业的语言风格' },
      { id: 'casual', name: '口语版', desc: '轻松活泼的对话式表达' },
      { id: 'professional', name: '技术版', desc: '适合技术/专业人士' },
      { id: 'beginner', name: '新手版', desc: '简单易懂，适合初学者' },
      { id: 'creative', name: '创意版', desc: '更具创造性和吸引力' },
      { id: 'structured', name: '结构化', desc: '添加清晰的层次结构' }
    ]
  }
}

// 加载示例
const loadExample = () => {
  originalPrompt.value = examplePrompt
}

// 选择/取消风格
const toggleStyle = (styleId) => {
  const idx = selectedStyles.value.indexOf(styleId)
  if (idx > -1) {
    selectedStyles.value.splice(idx, 1)
  } else {
    if (!batchMode.value) {
      selectedStyles.value = [styleId]
    } else {
      selectedStyles.value.push(styleId)
    }
  }
}

// 检查是否已选择
const isSelected = (styleId) => {
  return selectedStyles.value.includes(styleId)
}

// 是否可以转换
const canTransform = computed(() => {
  return originalPrompt.value.trim().length > 0 && selectedStyles.value.length > 0
})

// 获取风格图标
const getStyleIcon = (styleId) => {
  const iconMap = {
    'concise': '📋',
    'detailed': '📝',
    'formal': '👔',
    'casual': '💬',
    'professional': '🔧',
    'beginner': '🌱',
    'creative': '💡',
    'structured': '🏗️'
  }
  return iconMap[styleId] || '✨'
}

// 获取风格名称
const getStyleName = (styleId) => {
  const style = styles.value.find(s => s.id === styleId)
  return style ? style.name : styleId
}

// 执行转换
const transformPrompt = async () => {
  if (!canTransform.value) return
  
  transforming.value = true
  results.value = []
  
  try {
    const res = await fetch(`${API}/prompt/transform-batch`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        prompt: originalPrompt.value,
        styles: selectedStyles.value
      })
    })
    
    const data = await res.json()
    if (data.success || data.code === 0) {
      results.value = data.data || []
      ElMessage.success(`转换完成，获得 ${results.value.length} 个结果`)
    } else {
      ElMessage.error(data.message || '转换失败')
    }
  } catch (e) {
    console.error('转换失败:', e)
    ElMessage.error('转换失败，请稍后重试')
  }
  
  transforming.value = false
}

// 复制结果
const copyResult = async (text) => {
  try {
    await navigator.clipboard.writeText(text)
    showCopySuccess.value = true
    setTimeout(() => {
      showCopySuccess.value = false
    }, 2000)
  } catch (e) {
    ElMessage.error('复制失败')
  }
}

// 使用提示词
const usePrompt = (prompt) => {
  router.push({ path: '/', query: { task: prompt } })
}

// 重置
const reset = () => {
  originalPrompt.value = ''
  selectedStyles.value = []
  results.value = []
}

onMounted(() => {
  loadStyles()
})
</script>

<style scoped>
.style-transfer-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #1a1a2e 0%, #16213e 100%);
  margin: -24px;
  padding-bottom: 40px;
}

.banner {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 40px;
  text-align: center;
  color: white;
}

.banner-icon {
  font-size: 48px;
  margin-bottom: 12px;
}

.banner-title {
  font-size: 28px;
  font-weight: 700;
  margin: 0 0 8px;
}

.banner-desc {
  font-size: 16px;
  opacity: 0.9;
  margin: 0;
}

.main-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 24px;
}

.input-section,
.style-section,
.results-section {
  background: white;
  border-radius: 16px;
  padding: 24px;
  margin-bottom: 24px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.section-title {
  font-size: 18px;
  font-weight: 600;
  color: #1e293b;
}

.style-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 16px;
}

.style-card {
  padding: 20px;
  border: 2px solid #e2e8f0;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s;
  text-align: center;
}

.style-card:hover {
  border-color: #667eea;
  transform: translateY(-2px);
}

.style-card.active {
  border-color: #667eea;
  background: #667eea10;
}

.style-icon {
  font-size: 32px;
  margin-bottom: 8px;
}

.style-name {
  font-weight: 600;
  color: #1e293b;
  margin-bottom: 4px;
}

.style-desc {
  font-size: 12px;
  color: #64748b;
}

.action-section {
  display: flex;
  justify-content: center;
  gap: 16px;
  margin-bottom: 24px;
}

.results-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(400px, 1fr));
  gap: 20px;
}

.result-card {
  background: #f8fafc;
  border-radius: 12px;
  padding: 20px;
  border: 1px solid #e2e8f0;
}

.result-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.result-style {
  font-weight: 600;
  color: #667eea;
}

.result-actions {
  display: flex;
  gap: 8px;
}

.result-content {
  font-size: 14px;
  line-height: 1.8;
  color: #334155;
  white-space: pre-wrap;
  max-height: 300px;
  overflow-y: auto;
}

.copy-toast {
  position: fixed;
  bottom: 40px;
  left: 50%;
  transform: translateX(-50%);
  background: #10b981;
  color: white;
  padding: 12px 24px;
  border-radius: 8px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.2);
  z-index: 1000;
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
