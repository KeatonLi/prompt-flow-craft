<template>
  <AppLayout>
    <template #sidebar-left>
      <CategoryNav />
    </template>

    <template #main>
      <div class="generate-page">
        <!-- Page Header -->
        <div class="page-header">
          <div class="header-content">
            <h1 class="page-title">
              <svg width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M12 2L2 7l10 5 10-5-10-5z"/>
                <path d="M2 17l10 5 10-5"/>
                <path d="M2 12l10 5 10-5"/>
              </svg>
              <span>提示词实验室</span>
            </h1>
            <p class="page-subtitle">基于四层架构模型，生成高质量、结构化的 AI 提示词</p>
          </div>
        </div>

        <!-- Main Editor Panel -->
        <div class="editor-container">
          <!-- Input Form Card -->
          <div class="form-card">
            <div class="card-header">
              <div class="header-tabs">
                <button class="tab-btn active">
                  <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"/>
                    <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"/>
                  </svg>
                  <span>配置参数</span>
                </button>
              </div>
              <button class="btn-example" @click="loadExample">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <polygon points="13 2 3 14 12 14 11 22 21 10 12 10 13 2"/>
                </svg>
                <span>加载示例</span>
              </button>
            </div>

            <div class="card-body">
              <!-- Task Description -->
              <div class="form-group">
                <label class="form-label">
                  <span>任务描述</span>
                  <span class="required">*</span>
                </label>
                <textarea
                  v-model="form.taskDescription"
                  class="form-textarea"
                  placeholder="描述你想要 AI 完成的任务，例如：写一篇关于人工智能发展趋势的文章..."
                  rows="4"
                  @input="updateCharCount"
                ></textarea>
                <div class="char-indicator">
                  <span :class="{ 'text-warning': form.taskDescription.length < 10 }">
                    {{ form.taskDescription.length }}
                  </span>
                  <span class="text-muted"> / 最少 10 字符</span>
                </div>
              </div>

              <!-- Configuration Grid -->
              <div class="form-grid">
                <div class="form-group">
                  <label class="form-label">目标受众</label>
                  <select v-model="form.targetAudience" class="form-select">
                    <option value="general">通用</option>
                    <option value="beginner">初学者</option>
                    <option value="intermediate">中级用户</option>
                    <option value="expert">专家</option>
                    <option value="professional">专业人士</option>
                  </select>
                </div>

                <div class="form-group">
                  <label class="form-label">输出格式</label>
                  <select v-model="form.outputFormat" class="form-select">
                    <option value="text">文本</option>
                    <option value="list">列表</option>
                    <option value="table">表格</option>
                    <option value="code">代码</option>
                    <option value="json">JSON</option>
                  </select>
                </div>

                <div class="form-group">
                  <label class="form-label">语调风格</label>
                  <select v-model="form.tone" class="form-select">
                    <option value="professional">专业</option>
                    <option value="casual">轻松</option>
                    <option value="formal">正式</option>
                    <option value="friendly">友好</option>
                    <option value="academic">学术</option>
                  </select>
                </div>

                <div class="form-group">
                  <label class="form-label">内容长度</label>
                  <select v-model="form.length" class="form-select">
                    <option value="short">简短</option>
                    <option value="medium">中等</option>
                    <option value="long">详细</option>
                  </select>
                </div>
              </div>

              <!-- Constraints -->
              <div class="form-group">
                <label class="form-label">
                  <span>约束条件</span>
                  <span class="optional">(可选)</span>
                </label>
                <textarea
                  v-model="form.constraints"
                  class="form-textarea"
                  placeholder="添加任何特定的约束或要求，例如：需要包含最新技术发展动态..."
                  rows="3"
                ></textarea>
              </div>

              <!-- Examples -->
              <div class="form-group">
                <label class="form-label">
                  <span>示例输入</span>
                  <span class="optional">(可选)</span>
                </label>
                <textarea
                  v-model="form.examples"
                  class="form-textarea"
                  placeholder="提供示例可以帮助 AI 更好地理解你的需求..."
                  rows="3"
                ></textarea>
              </div>
            </div>

            <!-- Action Buttons -->
            <div class="card-footer">
              <button class="btn-reset" @click="reset">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M3 12a9 9 0 1 0 9-9 9.75 9.75 0 0 0-6.74 2.74L3 8"/>
                  <path d="M3 3v5h5"/>
                </svg>
                <span>重置</span>
              </button>
              <div class="action-right">
                <button class="btn-generate" :disabled="!canGenerate || loading" @click="generate">
                  <template v-if="loading">
                    <span class="spinner"></span>
                    <span>生成中...</span>
                  </template>
                  <template v-else>
                    <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                      <polygon points="13 2 3 14 12 14 11 22 21 10 12 10 13 2"/>
                    </svg>
                    <span>生成提示词</span>
                  </template>
                </button>
                <button v-if="loading" class="btn-cancel" @click="cancelGeneration">
                  <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <rect x="3" y="3" width="18" height="18" rx="2" ry="2"/>
                  </svg>
                  <span>取消</span>
                </button>
              </div>
            </div>
          </div>

          <!-- Result Card -->
          <Transition name="result-appear">
            <div v-if="showResult" class="result-card">
              <div class="card-header">
                <div class="result-title">
                  <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <path d="M14.5 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V7.5L14.5 2z"/>
                    <polyline points="14 2 14 8 20 8"/>
                  </svg>
                  <span>生成结果</span>
                  <span v-if="isStreaming" class="streaming-badge">
                    <span class="stream-dot"></span>
                    AI 思考中...
                  </span>
                </div>
                <div class="result-actions">
                  <button class="action-btn-small" @click="copyResult" :title="'复制结果'">
                    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                      <rect x="9" y="9" width="13" height="13" rx="2" ry="2"/>
                      <path d="M5 15H4a2 2 0 0 1-2-2V4a2 2 0 0 1 2-2h9a2 2 0 0 1 2 2v1"/>
                    </svg>
                  </button>
                  <div class="export-dropdown">
                    <button class="action-btn-small" @click="toggleExportMenu">
                      <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                        <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"/>
                        <polyline points="7 10 12 15 17 10"/>
                        <line x1="12" y1="15" x2="12" y2="3"/>
                      </svg>
                    </button>
                    <Transition name="dropdown-fade">
                      <div v-if="showExportMenu" class="export-menu">
                        <button @click="exportPrompt('markdown')">
                          <span>📝</span> Markdown
                        </button>
                        <button @click="exportPrompt('json')">
                          <span>📋</span> JSON
                        </button>
                        <button @click="exportPrompt('txt')">
                          <span>📄</span> 纯文本
                        </button>
                      </div>
                    </Transition>
                  </div>
                </div>
              </div>

              <div class="result-body">
                <div class="result-content" ref="resultContentRef">
                  <div class="markdown-body" v-html="displayedResultWithCursor"></div>
                </div>
              </div>
            </div>
          </Transition>
        </div>
      </div>
    </template>

    <template #sidebar-right>
      <HistoryPanel />
    </template>
  </AppLayout>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import MarkdownIt from 'markdown-it'
import hljs from 'highlight.js'
import AppLayout from '@/components/layout/AppLayout.vue'
import CategoryNav from '@/components/layout/CategoryNav.vue'
import HistoryPanel from '@/components/history/HistoryPanel.vue'
import { promptApi } from '@/api/prompt'
import type { PromptRecord } from '@/types'

// Markdown-it 配置
const md = new MarkdownIt({
  html: true,
  linkify: true,
  typographer: true,
  highlight: function (str: string, lang: string) {
    if (lang && hljs.getLanguage(lang)) {
      try {
        return '<pre class="hljs-code"><div class="hljs-header"><span class="hljs-lang">' + lang + '</span></div><code>' +
          hljs.highlight(str, { language: lang, ignoreIllegals: true }).value +
          '</code></pre>';
      } catch (__) {}
    }
    return '<pre class="hljs-code"><code>' + md.utils.escapeHtml(str) + '</code></pre>';
  }
})

const route = useRoute()
const resultContentRef = ref<HTMLElement | null>(null)
const showExportMenu = ref(false)

// Form state
const form = ref({
  taskDescription: '',
  targetAudience: 'general',
  outputFormat: 'text',
  constraints: '',
  examples: '',
  tone: 'professional',
  length: 'medium'
})

// UI state
const loading = ref(false)
const isStreaming = ref(false)
const showResult = ref(false)
const result = ref('')
const displayedResult = ref('')
const cancelStream = ref<(() => void) | null>(null)

const canGenerate = computed(() => {
  return form.value.taskDescription.length >= 10
})

const displayedResultWithCursor = computed(() => {
  const raw = displayedResult.value || result.value
  const content = md.render(raw)
  if (isStreaming.value) {
    return content + '<span class="typing-cursor"></span>'
  }
  return content
})

// 复用历史记录
function handleReuseHistory(event: Event) {
  const customEvent = event as CustomEvent<PromptRecord>
  const record = customEvent.detail

  form.value = {
    taskDescription: record.taskDescription || '',
    targetAudience: record.targetAudience || 'general',
    outputFormat: record.outputFormat || 'text',
    constraints: record.constraints || '',
    examples: record.examples || '',
    tone: record.tone || 'professional',
    length: record.length || 'medium'
  }

  // 滚动到表单顶部
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

const updateCharCount = () => {
  // Char count is handled in template
}

const loadExample = () => {
  form.value = {
    taskDescription: '写一篇关于人工智能发展趋势的文章',
    targetAudience: 'general',
    outputFormat: 'text',
    constraints: '需要包含最新的技术发展动态和未来展望',
    examples: '',
    tone: 'professional',
    length: 'medium'
  }
}

const generate = async () => {
  if (!canGenerate.value || loading.value) return

  loading.value = true
  isStreaming.value = true
  showResult.value = true
  result.value = ''
  displayedResult.value = ''

  // Auto scroll to result
  setTimeout(() => {
    const resultSection = document.querySelector('.result-card')
    if (resultSection) {
      resultSection.scrollIntoView({ behavior: 'smooth', block: 'start' })
    }
  }, 100)

  try {
    cancelStream.value = promptApi.generateStream(
      form.value,
      (content: string) => {
        result.value += content
        displayedResult.value += content

        // Auto scroll during streaming
        setTimeout(() => {
          if (resultContentRef.value) {
            resultContentRef.value.scrollTop = resultContentRef.value.scrollHeight
          }
        }, 0)
      },
      () => {
        isStreaming.value = false
        loading.value = false
        cancelStream.value = null
      },
      (error: string) => {
        isStreaming.value = false
        loading.value = false
        cancelStream.value = null
        console.error('Generation error:', error)
      }
    )
  } catch (error: any) {
    isStreaming.value = false
    loading.value = false
    showResult.value = false
    console.error('Generation failed:', error)
  }
}

const cancelGeneration = () => {
  if (cancelStream.value) {
    cancelStream.value()
    cancelStream.value = null
  }
  isStreaming.value = false
  loading.value = false
}

const reset = () => {
  form.value = {
    taskDescription: '',
    targetAudience: 'general',
    outputFormat: 'text',
    constraints: '',
    examples: '',
    tone: 'professional',
    length: 'medium'
  }
  result.value = ''
  displayedResult.value = ''
  showResult.value = false
  if (cancelStream.value) {
    cancelStream.value()
    cancelStream.value = null
  }
  loading.value = false
  isStreaming.value = false
}

const copyResult = async () => {
  try {
    await navigator.clipboard.writeText(displayedResult.value || result.value)
    // Simple feedback - could be improved with toast
    alert('已复制到剪贴板')
  } catch (error) {
    console.error('Copy failed:', error)
  }
}

const toggleExportMenu = () => {
  showExportMenu.value = !showExportMenu.value
}

const exportPrompt = (format: 'markdown' | 'json' | 'txt') => {
  showExportMenu.value = false

  const timestamp = new Date().toISOString().slice(0, 10)
  let content = ''
  let filename = `prompt-${timestamp}`
  let mimeType = 'text/plain'

  if (format === 'markdown') {
    content = `# AI 提示词\n\n${result.value}\n\n---\n*由 Prompt Flow Craft 生成*\n`
    filename += '.md'
    mimeType = 'text/markdown'
  } else if (format === 'json') {
    const exportData = {
      prompt: result.value,
      metadata: {
        generatedAt: new Date().toISOString(),
        ...form.value
      }
    }
    content = JSON.stringify(exportData, null, 2)
    filename += '.json'
    mimeType = 'application/json'
  } else {
    content = result.value
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
}

const handleClickOutside = (event: MouseEvent) => {
  const target = event.target as HTMLElement
  if (!target.closest('.export-dropdown')) {
    showExportMenu.value = false
  }
}

onMounted(() => {
  // Load query params if present
  if (route.query.task) {
    form.value.taskDescription = String(route.query.task)
  }

  window.addEventListener('click', handleClickOutside)
  window.addEventListener('reuse-history', handleReuseHistory)
})

onUnmounted(() => {
  window.removeEventListener('click', handleClickOutside)
  window.removeEventListener('reuse-history', handleReuseHistory)
  if (cancelStream.value) {
    cancelStream.value()
  }
})
</script>

<style scoped>
/* ============================================
   Generate Page
   ============================================ */
.generate-page {
  display: flex;
  flex-direction: column;
  gap: 24px;
  padding-bottom: 40px;
}

/* ============================================
   Page Header
   ============================================ */
.page-header {
  margin-bottom: 8px;
}

.header-content {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.page-title {
  display: flex;
  align-items: center;
  gap: 12px;
  font-family: var(--font-display);
  font-size: 1.75rem;
  font-weight: 800;
  color: var(--text-primary);
  letter-spacing: -0.02em;
}

.page-title svg {
  color: var(--color-primary-500);
}

.page-subtitle {
  font-size: var(--text-sm);
  color: var(--text-secondary);
  margin-left: 40px;
}

/* ============================================
   Editor Container
   ============================================ */
.editor-container {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

/* ============================================
   Form Card
   ============================================ */
.form-card {
  background: var(--bg-card);
  border: 1px solid var(--border-color);
  border-radius: var(--radius-2xl);
  box-shadow: var(--shadow-card);
  overflow: hidden;
  backdrop-filter: blur(12px);
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 18px 24px;
  border-bottom: 1px solid var(--border-color);
  background: var(--bg-panel);
}

.header-tabs {
  display: flex;
  gap: 8px;
}

.tab-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 18px;
  border: none;
  border-radius: var(--radius-lg);
  background: transparent;
  color: var(--text-secondary);
  font-family: var(--font-body);
  font-size: var(--text-sm);
  font-weight: 600;
  cursor: pointer;
  transition: all var(--transition-base);
}

.tab-btn:hover {
  background: var(--bg-hover);
  color: var(--text-primary);
}

.tab-btn.active {
  background: var(--glow-primary-soft);
  color: var(--color-primary-600);
}

.btn-example {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 18px;
  border: none;
  border-radius: var(--radius-lg);
  background: linear-gradient(135deg, var(--color-primary-600), var(--color-primary-700));
  color: white;
  font-family: var(--font-body);
  font-size: var(--text-sm);
  font-weight: 600;
  cursor: pointer;
  transition: all var(--transition-base);
  box-shadow: 0 4px 16px rgba(59, 130, 246, 0.25);
}

.btn-example:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(59, 130, 246, 0.35);
}

.card-body {
  padding: 24px;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

/* ============================================
   Form Elements
   ============================================ */
.form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-label {
  display: flex;
  align-items: center;
  gap: 6px;
  font-family: var(--font-body);
  font-size: var(--text-sm);
  font-weight: 600;
  color: var(--text-primary);
}

.required {
  color: var(--color-error);
}

.optional {
  color: var(--text-muted);
  font-weight: 400;
}

.form-textarea,
.form-select {
  width: 100%;
  padding: 14px 18px;
  font-family: var(--font-body);
  font-size: var(--text-sm);
  color: var(--text-primary);
  background: var(--bg-input);
  border: 1px solid var(--border-color);
  border-radius: var(--radius-xl);
  transition: all var(--transition-base);
  resize: vertical;
}

.form-textarea:hover,
.form-select:hover {
  border-color: var(--border-hover);
  background: var(--bg-card);
}

.form-textarea:focus,
.form-select:focus {
  outline: none;
  border-color: var(--color-primary-500);
  box-shadow: 0 0 0 4px var(--glow-primary-soft);
  background: var(--bg-card);
}

.form-textarea::placeholder,
.form-select::placeholder {
  color: var(--text-placeholder);
}

.form-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}

.char-indicator {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: var(--text-xs);
  color: var(--text-muted);
  justify-content: flex-end;
}

.char-indicator .text-warning {
  color: var(--color-warning);
}

/* ============================================
   Card Footer
   ============================================ */
.card-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 18px 24px;
  border-top: 1px solid var(--border-color);
  background: var(--bg-panel);
}

.action-right {
  display: flex;
  align-items: center;
  gap: 12px;
}

.btn-reset {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 20px;
  border: 1px solid var(--border-color);
  border-radius: var(--radius-xl);
  background: transparent;
  color: var(--text-secondary);
  font-family: var(--font-body);
  font-size: var(--text-sm);
  font-weight: 600;
  cursor: pointer;
  transition: all var(--transition-base);
}

.btn-reset:hover {
  background: var(--bg-hover);
  border-color: var(--border-hover);
  color: var(--text-primary);
}

.btn-generate {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 14px 28px;
  border: none;
  border-radius: var(--radius-xl);
  background: linear-gradient(135deg, var(--color-primary-600), var(--color-primary-700));
  color: white;
  font-family: var(--font-body);
  font-size: var(--text-base);
  font-weight: 700;
  cursor: pointer;
  transition: all var(--transition-base);
  box-shadow: 0 8px 24px rgba(59, 130, 246, 0.35);
}

.btn-generate:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 12px 32px rgba(59, 130, 246, 0.45);
}

.btn-generate:disabled {
  opacity: 0.5;
  cursor: not-allowed;
  transform: none;
}

.btn-cancel {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 20px;
  border: 1px solid var(--color-error);
  border-radius: var(--radius-xl);
  background: transparent;
  color: var(--color-error);
  font-family: var(--font-body);
  font-size: var(--text-sm);
  font-weight: 600;
  cursor: pointer;
  transition: all var(--transition-base);
}

.btn-cancel:hover {
  background: var(--color-error-light);
}

.spinner {
  width: 16px;
  height: 16px;
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-top-color: white;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

/* ============================================
   Result Card
   ============================================ */
.result-card {
  background: var(--bg-card);
  border: 1px solid var(--border-color);
  border-radius: var(--radius-2xl);
  box-shadow: var(--shadow-card);
  overflow: hidden;
  backdrop-filter: blur(12px);
}

.result-title {
  display: flex;
  align-items: center;
  gap: 10px;
  font-family: var(--font-body);
  font-size: var(--text-base);
  font-weight: 600;
  color: var(--text-primary);
}

.result-title svg {
  color: var(--color-primary-500);
}

.streaming-badge {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 4px 10px;
  background: var(--glow-primary-soft);
  color: var(--color-primary-600);
  font-size: var(--text-xs);
  font-weight: 600;
  border-radius: var(--radius-full);
}

.stream-dot {
  width: 6px;
  height: 6px;
  background: var(--color-primary-500);
  border-radius: 50%;
  animation: pulse 1s ease-in-out infinite;
}

@keyframes pulse {
  0%, 100% { opacity: 1; transform: scale(1); }
  50% { opacity: 0.5; transform: scale(1.2); }
}

.result-actions {
  display: flex;
  align-items: center;
  gap: 8px;
}

.action-btn-small {
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 1px solid var(--border-color);
  border-radius: var(--radius-lg);
  background: transparent;
  color: var(--text-secondary);
  cursor: pointer;
  transition: all var(--transition-base);
}

.action-btn-small:hover {
  background: var(--bg-hover);
  border-color: var(--border-hover);
  color: var(--text-primary);
}

.export-dropdown {
  position: relative;
}

.export-menu {
  position: absolute;
  top: 100%;
  right: 0;
  margin-top: 8px;
  padding: 8px;
  background: var(--bg-panel);
  border: 1px solid var(--border-color);
  border-radius: var(--radius-xl);
  box-shadow: var(--shadow-dropdown);
  min-width: 160px;
  z-index: 10;
}

.export-menu button {
  display: flex;
  align-items: center;
  gap: 10px;
  width: 100%;
  padding: 10px 14px;
  border: none;
  border-radius: var(--radius-lg);
  background: transparent;
  color: var(--text-primary);
  font-family: var(--font-body);
  font-size: var(--text-sm);
  cursor: pointer;
  transition: all var(--transition-fast);
}

.export-menu button:hover {
  background: var(--glow-primary-soft);
  color: var(--color-primary-600);
}

.result-body {
  padding: 0;
}

.result-content {
  max-height: 600px;
  overflow-y: auto;
  background: var(--bg-panel);
  padding: 24px;
  position: relative;
}

/* ============================================
   Markdown Body Styles
   ============================================ */
.markdown-body {
  color: var(--text-primary);
  font-family: var(--font-body);
  font-size: var(--text-sm);
  line-height: 1.9;
}

.markdown-body > * + * {
  margin-top: 0.75em;
}

.markdown-body > *:first-child {
  margin-top: 0;
}

.markdown-body h1,
.markdown-body h2,
.markdown-body h3,
.markdown-body h4,
.markdown-body h5,
.markdown-body h6 {
  margin-top: 1.5em;
  margin-bottom: 0.5em;
  font-weight: 700;
  line-height: 1.4;
  color: var(--text-primary);
}

.markdown-body h1 { font-size: 1.4em; font-weight: 800; }
.markdown-body h2 { font-size: 1.2em; font-weight: 700; color: var(--color-primary-600); }
.markdown-body h3 { font-size: 1.05em; font-weight: 700; }
.markdown-body h4 { font-size: 1em; font-weight: 600; }

.markdown-body p {
  margin: 0;
  line-height: 1.8;
  color: var(--text-secondary);
}

.markdown-body ul,
.markdown-body ol {
  margin: 0.5em 0;
  padding-left: 1.5em;
}

.markdown-body li {
  margin: 0.3em 0;
  color: var(--text-secondary);
}

.markdown-body ul li {
  list-style-type: disc;
}

.markdown-body ol li {
  list-style-type: decimal;
}

.markdown-body li > ul,
.markdown-body li > ol {
  margin: 0.25em 0;
}

/* 行内代码 */
.markdown-body code {
  padding: 0.15em 0.4em;
  background: var(--bg-hover);
  border-radius: var(--radius-md);
  font-family: var(--font-mono);
  font-size: 0.88em;
  color: var(--color-primary-600);
  border: 1px solid var(--border-color);
}

/* 代码块 */
.markdown-body pre {
  margin: 1em 0;
  border-radius: var(--radius-xl);
  overflow: hidden;
  background: #1e1e2e;
  border: 1px solid rgba(255, 255, 255, 0.06);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
}

.markdown-body pre code {
  display: block;
  padding: 1em 1.25em;
  background: transparent;
  color: #cdd6f4;
  font-size: var(--text-xs);
  line-height: 1.7;
  overflow-x: auto;
  border: none;
}

/* 代码块高亮样式 */
.markdown-body .hljs-code {
  margin: 0;
  padding: 0;
  background: transparent;
}

.markdown-body .hljs-header {
  display: flex;
  align-items: center;
  padding: 10px 16px;
  background: rgba(255, 255, 255, 0.05);
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.markdown-body .hljs-lang {
  font-size: 0.7em;
  font-weight: 600;
  color: #89b4fa;
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.markdown-body blockquote {
  margin: 0.75em 0;
  padding: 0.75em 1em;
  border-left: 3px solid var(--color-primary-400);
  background: var(--glow-primary-soft);
  border-radius: 0 var(--radius-md) var(--radius-md) 0;
  color: var(--text-secondary);
}

.markdown-body blockquote p {
  margin: 0;
  font-size: 0.95em;
}

.markdown-body table {
  width: 100%;
  margin: 1em 0;
  border-collapse: collapse;
  border-radius: var(--radius-lg);
  overflow: hidden;
  border: 1px solid var(--border-color);
}

.markdown-body th,
.markdown-body td {
  padding: 10px 14px;
  border-bottom: 1px solid var(--border-color);
  text-align: left;
  font-size: var(--text-xs);
}

.markdown-body th {
  background: var(--bg-hover);
  font-weight: 600;
  color: var(--text-primary);
}

.markdown-body tr:last-child td {
  border-bottom: none;
}

.markdown-body tr:hover td {
  background: var(--bg-hover);
}

.markdown-body hr {
  margin: 1.5em 0;
  border: none;
  height: 1px;
  background: var(--border-color);
}

.markdown-body a {
  color: var(--color-primary-600);
  text-decoration: none;
  border-bottom: 1px solid transparent;
  transition: border-color 0.2s;
}

.markdown-body a:hover {
  border-bottom-color: var(--color-primary-500);
}

.markdown-body img {
  max-width: 100%;
  border-radius: var(--radius-lg);
  margin: 0.5em 0;
}

.markdown-body strong {
  font-weight: 700;
  color: var(--text-primary);
}

.markdown-body em {
  font-style: italic;
}

.typing-cursor {
  display: inline-block;
  width: 2px;
  height: 18px;
  background: var(--color-primary-400);
  margin-left: 2px;
  animation: blink 1s step-end infinite;
  vertical-align: text-bottom;
}

@keyframes blink {
  0%, 100% { opacity: 1; }
  50% { opacity: 0; }
}

/* Result Appear Animation */
.result-appear-enter-active {
  animation: slideUp 0.5s cubic-bezier(0.16, 1, 0.3, 1);
}

.result-appear-leave-active {
  animation: slideUp 0.3s ease reverse;
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(30px) scale(0.98);
  }
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

/* Dropdown Fade */
.dropdown-fade-enter-active,
.dropdown-fade-leave-active {
  transition: all 0.2s ease;
}

.dropdown-fade-enter-from,
.dropdown-fade-leave-to {
  opacity: 0;
  transform: translateY(-8px);
}

/* ============================================
   Responsive
   ============================================ */
@media (max-width: 768px) {
  .page-header {
    margin-bottom: 16px;
  }

  .page-title {
    font-size: 1.5rem;
  }

  .page-subtitle {
    margin-left: 0;
    margin-top: 4px;
  }

  .card-header {
    flex-direction: column;
    gap: 16px;
    align-items: flex-start;
  }

  .form-grid {
    grid-template-columns: 1fr;
  }

  .card-footer {
    flex-direction: column;
    gap: 16px;
  }

  .action-right {
    width: 100%;
    flex-direction: column;
  }

  .btn-reset,
  .btn-generate,
  .btn-cancel {
    width: 100%;
    justify-content: center;
  }
}
</style>
