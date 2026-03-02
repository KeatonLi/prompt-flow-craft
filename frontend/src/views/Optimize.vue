<template>
  <AppLayout>
    <template #main>
      <div class="page-container">
        <div class="banner">
          <h1 class="banner-title">✨ 提示词优化器</h1>
          <p class="banner-desc">输入你的提示词，AI帮你优化提升效果</p>
        </div>

        <div class="content-wrapper">
          <div class="optimize-form">
            <div class="form-section">
              <label class="section-label">📝 输入你的提示词</label>
              <textarea 
                v-model="originalPrompt" 
                class="prompt-input"
                placeholder="在这里粘贴你需要优化的提示词..."
                rows="8"
              ></textarea>
              <div class="char-count">{{ originalPrompt.length }} 字符</div>
            </div>

            <div class="options-row">
              <div class="option-group">
                <label class="option-label">🎯 优化类型</label>
                <select v-model="optimizationType" class="option-select">
                  <option value="general">全面优化</option>
                  <option value="clarity">提升清晰度</option>
                  <option value="specificity">增加具体性</option>
                  <option value="examples">添加示例</option>
                  <option value="structure">优化结构</option>
                </select>
              </div>
              
              <div class="option-group">
                <label class="option-label">🤖 目标模型</label>
                <select v-model="targetModel" class="option-select">
                  <option value="general">通用</option>
                  <option value="gpt">GPT 系列</option>
                  <option value="claude">Claude</option>
                </select>
              </div>
            </div>

            <button 
              class="optimize-btn" 
              :disabled="loading || !originalPrompt.trim()"
              @click="optimizePrompt"
            >
              <span v-if="loading" class="loading-spinner"></span>
              {{ loading ? '优化中...' : '开始优化' }}
            </button>
          </div>

          <!-- 结果展示 -->
          <div v-if="result" class="result-section">
            <div class="score-comparison">
              <div class="score-card before">
                <span class="score-label">优化前</span>
                <span class="score-value">{{ result.scoreBefore }}</span>
              </div>
              <div class="score-arrow">→</div>
              <div class="score-card after">
                <span class="score-label">优化后</span>
                <span class="score-value">{{ result.scoreAfter }}</span>
              </div>
              <div class="score-improvement" :class="{ positive: result.scoreAfter > result.scoreBefore }">
                {{ result.scoreAfter - result.scoreBefore > 0 ? '+' : '' }}{{ result.scoreAfter - result.scoreBefore }}
              </div>
            </div>

            <div class="result-tabs">
              <button 
                :class="['tab-btn', { active: activeTab === 'optimized' }]"
                @click="activeTab = 'optimized'"
              >
                优化后提示词
              </button>
              <button 
                :class="['tab-btn', { active: activeTab === 'improvements' }]"
                @click="activeTab = 'improvements'"
              >
                改进详情
              </button>
            </div>

            <div v-if="activeTab === 'optimized'" class="optimized-content">
              <div class="result-header">
                <span>优化后的提示词</span>
                <div class="result-actions">
                  <div class="export-dropdown">
                    <button class="copy-btn export-btn" @click="toggleExportMenu">
                      📥 导出
                    </button>
                    <div v-if="showExportMenu" class="export-menu">
                      <button @click="exportPrompt('markdown')">📝 Markdown</button>
                      <button @click="exportPrompt('json')">{} JSON</button>
                      <button @click="exportPrompt('txt')">📄 文本</button>
                    </div>
                  </div>
                  <button class="copy-btn" @click="copyOptimized">
                    <span v-if="copied">✓ 已复制</span>
                    <span v-else>📋 复制</span>
                  </button>
                </div>
              </div>
              <pre class="result-text">{{ result.optimizedPrompt }}</pre>
            </div>

            <div v-if="activeTab === 'improvements'" class="improvements-content">
              <div class="explanation" v-if="result.explanation">
                <h4>📋 优化说明</h4>
                <p>{{ result.explanation }}</p>
              </div>
              
              <div class="improvements-list" v-if="result.improvements && result.improvements.length">
                <h4>🔧 具体改进</h4>
                <div 
                  v-for="(imp, idx) in result.improvements" 
                  :key="idx" 
                  class="improvement-item"
                >
                  <div class="imp-type">{{ imp.type }}</div>
                  <div class="imp-desc">{{ imp.description }}</div>
                  <div class="imp-suggestion">💡 {{ imp.suggestion }}</div>
                </div>
              </div>
              
              <div v-else class="no-improvements">
                <p>未发现明显的改进空间，当前提示词质量良好！</p>
              </div>
            </div>

            <div class="result-actions">
              <button class="action-btn secondary" @click="resetForm">
                🔄 重新优化
              </button>
              <button class="action-btn primary" @click="useOptimized">
                ▶ 使用此提示词
              </button>
            </div>
          </div>

          <!-- 示例提示 -->
          <div class="tips-section" v-if="!result">
            <h3>💡 优化提示词的小技巧</h3>
            <ul>
              <li>明确告诉AI它扮演什么角色（"你是一位专业的..."）</li>
              <li>具体描述你想要的输出格式（"用JSON格式返回"）</li>
              <li>添加约束条件（"不要超过100字"）</li>
              <li>提供参考示例帮助AI理解期望</li>
            </ul>
          </div>
        </div>
      </div>
    </template>
  </AppLayout>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import AppLayout from '../components/layout/AppLayout.vue'

const router = useRouter()

const API = 'http://111.231.107.210:8080/api'

const originalPrompt = ref('')
const optimizationType = ref('general')
const targetModel = ref('general')
const loading = ref(false)
const result = ref(null)
const activeTab = ref('optimized')
const copied = ref(false)
const showExportMenu = ref(false)

function toggleExportMenu() {
  showExportMenu.value = !showExportMenu.value
}

function exportPrompt(format) {
  showExportMenu.value = false
  const timestamp = new Date().toISOString().slice(0, 10)
  let content = ''
  let filename = `optimized-prompt-${timestamp}`
  let mimeType = 'text/plain'
  
  if (format === 'markdown') {
    content = `# AI 提示词（优化版）\n\n${result.value.optimizedPrompt}\n\n---\n*由 Prompt Flow Craft 生成*`
    filename += '.md'
    mimeType = 'text/markdown'
  } else if (format === 'json') {
    const exportData = {
      prompt: result.value.optimizedPrompt,
      originalPrompt: originalPrompt.value,
      scoreBefore: result.value.scoreBefore,
      scoreAfter: result.value.scoreAfter,
      optimizationType: optimizationType.value,
      generatedAt: new Date().toISOString()
    }
    content = JSON.stringify(exportData, null, 2)
    filename += '.json'
    mimeType = 'application/json'
  } else {
    content = result.value.optimizedPrompt
    filename += '.txt'
  }
  
  const blob = new Blob([content], { type: mimeType })
  const url = URL.createObjectURL(blob)
  const a = document.createElement('a')
  a.href = url
  a.download = filename
  document.body.appendChild(a)
  a.click()
  document.body.removeChild(a)
  URL.revokeObjectURL(url)
  
  copied.value = true
  setTimeout(() => { copied.value = false }, 2000)
}

const optimizePrompt = async () => {
  if (!originalPrompt.value.trim() || loading.value) return
  
  loading.value = true
  result.value = null
  activeTab.value = 'optimized'
  
  try {
    const response = await fetch(`${API}/prompt/optimize`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        prompt: originalPrompt.value,
        optimizationType: optimizationType.value,
        targetModel: targetModel.value
      })
    })
    
    const data = await response.json()
    
    if (data.success && data.data) {
      result.value = data.data
    } else {
      alert(data.message || '优化失败，请重试')
    }
  } catch (error) {
    console.error('Optimize error:', error)
    alert('优化失败，请检查网络后重试')
  } finally {
    loading.value = false
  }
}

const copyOptimized = async () => {
  if (!result.value?.optimizedPrompt) return
  
  try {
    await navigator.clipboard.writeText(result.value.optimizedPrompt)
    copied.value = true
    setTimeout(() => {
      copied.value = false
    }, 2000)
  } catch (err) {
    console.error('Copy failed:', err)
  }
}

const resetForm = () => {
  result.value = null
  activeTab.value = 'optimized'
}

const useOptimized = () => {
  // 跳转到首页并填充优化后的提示词
  router.push({
    path: '/',
    query: { usePrompt: result.value?.optimizedPrompt }
  })
}

function handleClickOutside(event) {
  const target = event.target
  if (!target.closest('.export-dropdown')) {
    showExportMenu.value = false
  }
}

onMounted(() => {
  window.addEventListener('click', handleClickOutside)
})

onUnmounted(() => {
  window.removeEventListener('click', handleClickOutside)
})
</script>

<style scoped>
.page-container {
  min-height: 100%;
  background: #f8fafc;
  margin: -24px;
}

.banner {
  background: linear-gradient(135deg, #8b5cf6 0%, #6366f1 100%);
  padding: 36px 40px;
  text-align: center;
}

.banner-title {
  font-size: 1.6rem;
  font-weight: 700;
  color: white;
  margin: 0 0 6px;
}

.banner-desc {
  color: rgba(255,255,255,0.85);
  font-size: 0.95rem;
  margin: 0;
}

.content-wrapper {
  max-width: 900px;
  margin: 0 auto;
  padding: 30px 40px 40px;
}

.optimize-form {
  background: white;
  border-radius: 16px;
  padding: 28px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.06);
}

.form-section {
  margin-bottom: 20px;
}

.section-label {
  display: block;
  font-weight: 600;
  color: #1e293b;
  margin-bottom: 10px;
  font-size: 0.95rem;
}

.prompt-input {
  width: 100%;
  padding: 14px;
  border: 1px solid #e2e8f0;
  border-radius: 10px;
  font-size: 0.95rem;
  font-family: inherit;
  line-height: 1.6;
  resize: vertical;
  transition: border-color 0.2s, box-shadow 0.2s;
  box-sizing: border-box;
}

.prompt-input:focus {
  outline: none;
  border-color: #8b5cf6;
  box-shadow: 0 0 0 3px rgba(139, 92, 246, 0.1);
}

.char-count {
  text-align: right;
  font-size: 0.8rem;
  color: #94a3b8;
  margin-top: 6px;
}

.options-row {
  display: flex;
  gap: 20px;
  margin-bottom: 24px;
}

.option-group {
  flex: 1;
}

.option-label {
  display: block;
  font-weight: 600;
  color: #1e293b;
  margin-bottom: 8px;
  font-size: 0.9rem;
}

.option-select {
  width: 100%;
  padding: 10px 14px;
  border: 1px solid #e2e8f0;
  border-radius: 10px;
  font-size: 0.9rem;
  background: white;
  cursor: pointer;
  transition: border-color 0.2s;
}

.option-select:focus {
  outline: none;
  border-color: #8b5cf6;
}

.optimize-btn {
  width: 100%;
  padding: 14px 28px;
  background: linear-gradient(135deg, #8b5cf6 0%, #6366f1 100%);
  color: white;
  border: none;
  border-radius: 10px;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.optimize-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(139, 92, 246, 0.3);
}

.optimize-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.loading-spinner {
  width: 18px;
  height: 18px;
  border: 2px solid rgba(255,255,255,0.3);
  border-top-color: white;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

/* 结果区域 */
.result-section {
  margin-top: 30px;
  background: white;
  border-radius: 16px;
  padding: 28px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.06);
}

.score-comparison {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 20px;
  margin-bottom: 24px;
  padding: 20px;
  background: #f8fafc;
  border-radius: 12px;
}

.score-card {
  text-align: center;
  padding: 12px 24px;
  border-radius: 10px;
  min-width: 100px;
}

.score-card.before {
  background: #fee2e2;
}

.score-card.after {
  background: #dcfce7;
}

.score-label {
  display: block;
  font-size: 0.8rem;
  color: #64748b;
  margin-bottom: 4px;
}

.score-value {
  font-size: 1.8rem;
  font-weight: 700;
}

.score-card.before .score-value {
  color: #ef4444;
}

.score-card.after .score-value {
  color: #22c55e;
}

.score-arrow {
  font-size: 1.5rem;
  color: #94a3b8;
}

.score-improvement {
  padding: 8px 16px;
  background: #f1f5f9;
  border-radius: 8px;
  font-weight: 600;
  font-size: 0.9rem;
  color: #64748b;
}

.score-improvement.positive {
  background: #dcfce7;
  color: #22c55e;
}

.result-tabs {
  display: flex;
  gap: 8px;
  margin-bottom: 20px;
  border-bottom: 1px solid #e2e8f0;
  padding-bottom: 12px;
}

.tab-btn {
  padding: 10px 20px;
  background: none;
  border: none;
  border-radius: 8px;
  font-size: 0.9rem;
  font-weight: 500;
  color: #64748b;
  cursor: pointer;
  transition: all 0.2s;
}

.tab-btn:hover {
  background: #f1f5f9;
}

.tab-btn.active {
  background: #8b5cf6;
  color: white;
}

.optimized-content {
  margin-bottom: 20px;
}

.result-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
  font-weight: 600;
  color: #1e293b;
}

.copy-btn {
  padding: 6px 14px;
  background: #f1f5f9;
  border: none;
  border-radius: 6px;
  font-size: 0.85rem;
  color: #64748b;
  cursor: pointer;
  transition: all 0.2s;
}

.copy-btn:hover {
  background: #e2e8f0;
  color: #8b5cf6;
}

.export-dropdown {
  position: relative;
}

.export-btn {
  background: linear-gradient(135deg, #8b5cf6 0%, #6366f1 100%) !important;
  color: white !important;
}

.export-btn:hover {
  background: linear-gradient(135deg, #7c3aed 0%, #4f46e5 100%) !important;
}

.export-menu {
  position: absolute;
  top: 100%;
  right: 0;
  margin-top: 8px;
  background: white;
  border-radius: 10px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.15);
  min-width: 140px;
  z-index: 100;
  overflow: hidden;
}

.export-menu button {
  width: 100%;
  padding: 10px 14px;
  border: none;
  background: none;
  text-align: left;
  font-size: 0.85rem;
  color: #374151;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 8px;
}

.export-menu button:hover {
  background: #f1f5f9;
  color: #8b5cf6;
}

.result-actions {
  display: flex;
  gap: 8px;
  align-items: center;
}

.result-text {
  background: #f8fafc;
  padding: 20px;
  border-radius: 10px;
  font-size: 0.9rem;
  line-height: 1.7;
  white-space: pre-wrap;
  word-wrap: break-word;
  max-height: 400px;
  overflow-y: auto;
  border: 1px solid #e2e8f0;
}

.improvements-content {
  margin-bottom: 20px;
}

.explanation {
  background: #f0f9ff;
  border: 1px solid #bae6fd;
  border-radius: 10px;
  padding: 16px;
  margin-bottom: 20px;
}

.explanation h4 {
  margin: 0 0 8px;
  color: #0369a1;
  font-size: 0.95rem;
}

.explanation p {
  margin: 0;
  color: #075985;
  font-size: 0.9rem;
  line-height: 1.6;
}

.improvements-list h4 {
  margin: 0 0 12px;
  color: #1e293b;
  font-size: 0.95rem;
}

.improvement-item {
  background: #f8fafc;
  border: 1px solid #e2e8f0;
  border-radius: 10px;
  padding: 14px;
  margin-bottom: 10px;
}

.imp-type {
  font-weight: 600;
  color: #8b5cf6;
  font-size: 0.85rem;
  margin-bottom: 6px;
}

.imp-desc {
  color: #64748b;
  font-size: 0.9rem;
  margin-bottom: 8px;
}

.imp-suggestion {
  color: #22c55e;
  font-size: 0.85rem;
}

.no-improvements {
  text-align: center;
  padding: 30px;
  color: #22c55e;
  background: #f0fdf4;
  border-radius: 10px;
}

.result-actions {
  display: flex;
  gap: 12px;
  justify-content: flex-end;
  padding-top: 20px;
  border-top: 1px solid #e2e8f0;
}

.action-btn {
  padding: 10px 20px;
  border-radius: 8px;
  font-size: 0.9rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
}

.action-btn.secondary {
  background: #f1f5f9;
  border: 1px solid #e2e8f0;
  color: #64748b;
}

.action-btn.secondary:hover {
  background: #e2e8f0;
  color: #1e293b;
}

.action-btn.primary {
  background: linear-gradient(135deg, #8b5cf6 0%, #6366f1 100%);
  border: none;
  color: white;
}

.action-btn.primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(139, 92, 246, 0.3);
}

.tips-section {
  margin-top: 30px;
  background: white;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.06);
}

.tips-section h3 {
  margin: 0 0 16px;
  color: #1e293b;
  font-size: 1rem;
}

.tips-section ul {
  margin: 0;
  padding-left: 20px;
}

.tips-section li {
  color: #64748b;
  font-size: 0.9rem;
  line-height: 1.8;
}

@media (max-width: 640px) {
  .options-row {
    flex-direction: column;
  }
  
  .content-wrapper {
    padding: 20px 16px;
  }
  
  .score-comparison {
    flex-wrap: wrap;
  }
}
</style>
