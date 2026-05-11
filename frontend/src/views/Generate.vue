<template>
  <AppLayout>
    <template #main>
      <div class="generate-page">

        <!-- Type Selector Tabs -->
        <div class="type-selector">
          <div class="selector-tabs">
            <button
              v-for="t in promptTypes"
              :key="t.value"
              class="selector-tab"
              :class="{ active: currentType === t.value }"
              @click="switchType(t.value)"
            >
              <span class="tab-icon">{{ t.icon }}</span>
              <span class="tab-text">{{ t.name }}</span>
            </button>
          </div>
        </div>

        <!-- Main Content - Full Width Grid -->
        <div class="content-grid">
          <!-- Form Panel -->
          <div class="form-panel">
            <!-- Agent Form -->
            <div v-show="currentType === 'agent'" class="form-card">
              <div class="form-card-header">
                <div class="card-title">
                  <span class="title-icon">🤖</span>
                  <span>创建 Agent</span>
                </div>
                <button class="btn-example" @click="loadAgentExample">
                  <span>📖 示例</span>
                </button>
              </div>

              <div class="form-card-body">
                <div class="form-row">
                  <div class="form-group">
                    <label class="form-label">
                      <span>Agent 名称</span>
                      <span class="required">*</span>
                    </label>
                    <input
                      v-model="agentForm.name"
                      type="text"
                      class="form-input"
                      :class="{ 'error': agentForm.name && !hasChinese(agentForm.name) }"
                      placeholder="例如：技术文档助手"
                    />
                    <div v-if="agentForm.name && !hasChinese(agentForm.name)" class="form-error">请输入中文名称</div>
                  </div>
                </div>

                <div class="form-row">
                  <div class="form-group">
                    <label class="form-label">
                      <span>角色定位</span>
                      <span class="required">*</span>
                    </label>
                    <textarea
                      v-model="agentForm.role"
                      class="form-textarea"
                      :class="{ 'error': agentForm.role && !hasChinese(agentForm.role) }"
                      placeholder="描述 Agent 的核心身份和专业领域..."
                      rows="3"
                    ></textarea>
                  </div>
                </div>

                <div class="form-row">
                  <div class="form-group">
                    <label class="form-label">核心能力</label>
                    <textarea
                      v-model="agentForm.capabilities"
                      class="form-textarea"
                      placeholder="用简洁的要点描述 Agent 能完成的任务..."
                      rows="3"
                    ></textarea>
                  </div>
                </div>

                <div class="form-row">
                  <div class="form-group">
                    <label class="form-label">行为准则</label>
                    <textarea
                      v-model="agentForm.behaviors"
                      class="form-textarea"
                      placeholder="定义 Agent 在服务过程中应遵循的原则..."
                      rows="2"
                    ></textarea>
                  </div>
                </div>

                <div class="form-row">
                  <div class="form-group">
                    <label class="form-label">对话风格</label>
                    <div class="style-chips">
                      <button
                        v-for="style in communicationStyles"
                        :key="style.value"
                        class="style-chip"
                        :class="{ active: agentForm.communicationStyle === style.value }"
                        @click="agentForm.communicationStyle = style.value"
                      >
                        <span class="chip-icon">{{ style.icon }}</span>
                        <span class="chip-text">{{ style.name }}</span>
                      </button>
                    </div>
                  </div>
                </div>
              </div>

              <div class="form-card-footer">
                <button class="btn-reset" @click="resetAgent">清空</button>
                <button class="btn-generate" :disabled="!canGenerateAgent || loading" @click="generateAgent">
                  <template v-if="loading && currentType === 'agent'">
                    <span class="spinner"></span>
                    生成中...
                  </template>
                  <template v-else>
                    <span>✨</span>
                    生成提示词
                  </template>
                </button>
              </div>
            </div>

            <!-- Skill Form -->
            <div v-show="currentType === 'skill'" class="form-card">
              <div class="form-card-header">
                <div class="card-title">
                  <span class="title-icon">⚡</span>
                  <span>创建 Skill</span>
                </div>
                <button class="btn-example" @click="loadSkillExample">
                  <span>📖 示例</span>
                </button>
              </div>

              <div class="form-card-body">
                <div class="form-row two-col">
                  <div class="form-group">
                    <label class="form-label">
                      <span>Skill 名称</span>
                      <span class="required">*</span>
                    </label>
                    <input
                      v-model="skillForm.name"
                      type="text"
                      class="form-input"
                      :class="{ 'error': skillForm.name && !hasChinese(skillForm.name) }"
                      placeholder="例如：天气查询、新闻搜索"
                    />
                    <div v-if="skillForm.name && !hasChinese(skillForm.name)" class="form-error">请输入中文名称</div>
                  </div>
                  <div class="form-group">
                    <label class="form-label">工具类型</label>
                    <div class="type-chips">
                      <button
                        v-for="t in skillTypes"
                        :key="t.value"
                        class="type-chip"
                        :class="{ active: skillForm.type === t.value }"
                        @click="skillForm.type = t.value"
                      >
                        <span class="chip-icon">{{ t.icon }}</span>
                        <span class="chip-text">{{ t.name }}</span>
                      </button>
                    </div>
                  </div>
                </div>

                <div class="form-row">
                  <div class="form-group">
                    <label class="form-label">
                      <span>功能描述</span>
                      <span class="required">*</span>
                    </label>
                    <textarea
                      v-model="skillForm.description"
                      class="form-textarea"
                      :class="{ 'error': skillForm.description && !hasChinese(skillForm.description) }"
                      placeholder="清晰描述这个工具能做什么，以及何时使用它..."
                      rows="3"
                    ></textarea>
                  </div>
                </div>

                <div v-if="skillForm.type === 'api'" class="form-row two-col">
                  <div class="form-group">
                    <label class="form-label">请求方法</label>
                    <select v-model="skillForm.method" class="form-select">
                      <option value="GET">GET</option>
                      <option value="POST">POST</option>
                      <option value="PUT">PUT</option>
                      <option value="DELETE">DELETE</option>
                    </select>
                  </div>
                  <div class="form-group">
                    <label class="form-label">API 端点</label>
                    <input
                      v-model="skillForm.endpoint"
                      type="text"
                      class="form-input"
                      placeholder="https://..."
                    />
                  </div>
                </div>

                <div v-if="skillForm.type === 'function'" class="form-row">
                  <div class="form-group">
                    <label class="form-label">函数代码</label>
                    <textarea
                      v-model="skillForm.functionCode"
                      class="form-textarea code"
                      placeholder="// 输入函数实现..."
                      rows="4"
                    ></textarea>
                  </div>
                </div>

                <div class="form-row two-col">
                  <div class="form-group">
                    <label class="form-label">输入参数 <span class="optional">(可选)</span></label>
                    <textarea
                      v-model="skillForm.parameters"
                      class="form-textarea"
                      placeholder="定义需要的参数，如：city: 城市名"
                      rows="2"
                    ></textarea>
                  </div>
                  <div class="form-group">
                    <label class="form-label">输出格式 <span class="optional">(可选)</span></label>
                    <textarea
                      v-model="skillForm.outputDescription"
                      class="form-textarea"
                      placeholder="描述返回的数据结构..."
                      rows="2"
                    ></textarea>
                  </div>
                </div>
              </div>

              <div class="form-card-footer">
                <button class="btn-reset" @click="resetSkill">清空</button>
                <button class="btn-generate" :disabled="!canGenerateSkill || loading" @click="generateSkill">
                  <template v-if="loading && currentType === 'skill'">
                    <span class="spinner"></span>
                    生成中...
                  </template>
                  <template v-else>
                    <span>✨</span>
                    生成提示词
                  </template>
                </button>
              </div>
            </div>
          </div>

          <!-- Result Panel -->
          <div class="result-panel">
            <Transition name="result-appear">
              <div v-if="showResult" class="result-card">
                <div class="result-card-header">
                  <div class="result-title">
                    <span class="result-icon">📝</span>
                    <span>生成的提示词</span>
                    <span v-if="isStreaming" class="streaming-indicator">
                      <span class="stream-dot"></span>
                      生成中
                    </span>
                  </div>
                  <div class="result-toolbar">
                    <button class="toolbar-btn" @click="copyResult" title="复制">
                      <span>📋</span>
                    </button>
                    <div class="export-wrapper">
                      <button class="toolbar-btn" @click="toggleExportMenu">
                        <span>📥</span>
                      </button>
                      <Transition name="dropdown">
                        <div v-if="showExportMenu" class="export-menu">
                          <button @click="exportPrompt('markdown')">📝 Markdown</button>
                          <button @click="exportPrompt('json')">📋 JSON</button>
                          <button @click="exportPrompt('txt')">📄 纯文本</button>
                        </div>
                      </Transition>
                    </div>
                  </div>
                </div>

                <div class="result-card-body">
                  <div class="result-content" ref="resultContentRef">
                    <div class="markdown-body" v-html="displayedResultWithCursor"></div>
                  </div>
                </div>
              </div>
            </Transition>

            <div v-if="!showResult" class="result-empty">
              <div class="empty-visual">
                <span class="empty-icon">💡</span>
              </div>
              <p class="empty-title">填写配置，一键生成</p>
              <p class="empty-desc">AI 将为你创建专业的提示词</p>
            </div>
          </div>
        </div>
      </div>
    </template>
  </AppLayout>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRoute } from 'vue-router'
import MarkdownIt from 'markdown-it'
import hljs from 'highlight.js'
import AppLayout from '@/components/layout/AppLayout.vue'
import { promptApi } from '@/api/prompt'
import type { PromptRecord } from '@/types'

const highlightCode = (str: string, lang: string) => {
  if (lang && hljs.getLanguage(lang)) {
    try {
      return '<pre class="hljs-code"><div class="hljs-header"><span class="hljs-lang">' + lang + '</span></div><code>' +
        hljs.highlight(str, { language: lang, ignoreIllegals: true }).value +
        '</code></pre>';
    } catch (__) {}
  }
  return '<pre class="hljs-code"><code>' + str.replace(/[&<>"']/g, (c) => ({
    '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;'
  })[c]!) + '</code></pre>';
};

const md = new MarkdownIt({
  html: true,
  linkify: true,
  typographer: true,
  breaks: true,
  highlight: highlightCode
})

const route = useRoute()
const resultContentRef = ref<HTMLElement | null>(null)
const showExportMenu = ref(false)

const currentType = ref<'agent' | 'skill'>('agent')

const promptTypes = [
  { value: 'agent', name: 'Agent', icon: '🤖' },
  { value: 'skill', name: 'Skill', icon: '⚡' }
]

const skillTypes = [
  { value: 'api', name: 'API', icon: '🌐' },
  { value: 'function', name: '函数', icon: '⚡' },
  { value: 'webhook', name: 'Webhook', icon: '🪝' },
  { value: 'data', name: '数据源', icon: '📊' }
]

const communicationStyles = [
  { value: 'professional', name: '专业', icon: '💼' },
  { value: 'friendly', name: '友好', icon: '😊' },
  { value: 'concise', name: '简洁', icon: '⚡' },
  { value: 'detailed', name: '详细', icon: '📚' },
  { value: 'casual', name: '轻松', icon: '☕' }
]

const agentForm = ref({
  name: '',
  role: '',
  capabilities: '',
  behaviors: '',
  communicationStyle: 'professional'
})

const skillForm = ref({
  name: '',
  description: '',
  type: 'api' as 'api' | 'function' | 'webhook' | 'data',
  method: 'GET',
  endpoint: '',
  functionCode: '',
  parameters: '',
  outputDescription: ''
})

const loading = ref(false)
const isStreaming = ref(false)
const showResult = ref(false)
const result = ref('')
const displayedResult = ref('')
const renderedResult = ref('')
const cancelStream = ref<(() => void) | null>(null)
let renderTimer: ReturnType<typeof setTimeout> | null = null

const hasChinese = (text: string): boolean => /[一-龥]/.test(text)

const canGenerateAgent = computed(() => {
  const name = agentForm.value.name.trim()
  const role = agentForm.value.role.trim()
  if (!name || !role) return false
  return hasChinese(name) && hasChinese(role)
})

const canGenerateSkill = computed(() => {
  const name = skillForm.value.name.trim()
  const desc = skillForm.value.description.trim()
  if (!name || !desc) return false
  return hasChinese(name) && hasChinese(desc)
})

const displayedResultWithCursor = computed(() => {
  if (isStreaming.value) {
    const content = renderedResult.value || md.render(displayedResult.value || result.value)
    return content + '<span class="typing-cursor"></span>'
  }
  return md.render(displayedResult.value || result.value)
})

const switchType = (type: 'agent' | 'skill') => {
  currentType.value = type
  reset()
}

const loadAgentExample = () => {
  agentForm.value = {
    name: '技术文档助手',
    role: '你是一位资深技术文档专家，擅长将复杂的编程概念和技术架构用通俗易懂的语言解释清楚。你能够深入理解代码背后的设计思想，并将其转化为清晰的文档。',
    capabilities: '- 撰写技术设计文档和API接口文档\n- 解释系统架构、的设计模式和微服务设计\n- 提供代码示例和最佳实践建议\n- 校对和优化技术文案，确保表达准确',
    behaviors: '- 语言简洁专业，避免冗余和模糊表达\n- 主动提供实际应用案例和代码片段\n- 适当使用图表辅助说明复杂概念\n- 注意文档的可读性和可维护性',
    communicationStyle: 'professional'
  }
}

const loadSkillExample = () => {
  skillForm.value = {
    name: '天气查询助手',
    description: '根据用户提供的城市名称或位置信息，获取该城市的实时天气信息，包括温度、湿度、空气质量指数、风力等级和未来天气预报等数据。适用于需要了解特定地区天气情况的场景。',
    type: 'api',
    method: 'GET',
    endpoint: 'https://api.weather.example.com/v1/current',
    functionCode: '',
    parameters: 'city: 用户所在城市名称，字符串类型，必填\nunits: 温度单位，默认 metric（公制）可选 imperial（英制）',
    outputDescription: '返回 JSON 格式的完整天气数据，包含温度、湿度、风速、空气质量指数、AQI 等字段，以及未来3天的天气预报'
  }
}

const generateAgent = async () => {
  if (!canGenerateAgent.value || loading.value) return
  startGeneration()

  cancelStream.value = promptApi.generateAgentStream(
    {
      name: agentForm.value.name,
      roleDescription: agentForm.value.role,
      capabilities: agentForm.value.capabilities,
      behaviors: agentForm.value.behaviors,
      communicationStyle: agentForm.value.communicationStyle
    },
    onStreamContent,
    onStreamDone,
    onStreamError
  )
}

const generateSkill = async () => {
  if (!canGenerateSkill.value || loading.value) return
  startGeneration()

  cancelStream.value = promptApi.generateSkillStream(
    {
      name: skillForm.value.name,
      description: skillForm.value.description,
      skillType: skillForm.value.type,
      method: skillForm.value.method,
      endpoint: skillForm.value.endpoint,
      parameters: skillForm.value.parameters,
      outputDescription: skillForm.value.outputDescription
    },
    onStreamContent,
    onStreamDone,
    onStreamError
  )
}

const startGeneration = () => {
  loading.value = true
  isStreaming.value = true
  showResult.value = true
  result.value = ''
  displayedResult.value = ''
}

const onStreamContent = (content: string) => {
  result.value += content
  displayedResult.value += content

  if (!renderTimer) {
    renderTimer = setTimeout(() => {
      renderedResult.value = md.render(result.value)
      renderTimer = null
    }, 50)
  }
}

const onStreamDone = () => {
  isStreaming.value = false
  loading.value = false
  cancelStream.value = null
  if (renderTimer) {
    clearTimeout(renderTimer)
    renderTimer = null
  }
  renderedResult.value = md.render(result.value)
}

const onStreamError = () => {
  isStreaming.value = false
  loading.value = false
  cancelStream.value = null
}

const reset = () => {
  result.value = ''
  displayedResult.value = ''
  renderedResult.value = ''
  showResult.value = false
  if (cancelStream.value) {
    cancelStream.value()
    cancelStream.value = null
  }
  if (renderTimer) {
    clearTimeout(renderTimer)
    renderTimer = null
  }
  loading.value = false
  isStreaming.value = false
}

const resetAgent = () => {
  agentForm.value = { name: '', role: '', capabilities: '', behaviors: '', communicationStyle: 'professional' }
  reset()
}

const resetSkill = () => {
  skillForm.value = { name: '', description: '', type: 'api', method: 'GET', endpoint: '', functionCode: '', parameters: '', outputDescription: '' }
  reset()
}

const copyResult = async () => {
  try {
    await navigator.clipboard.writeText(displayedResult.value || result.value)
    alert('已复制到剪贴板')
  } catch (error) {
    console.error('Copy failed:', error)
  }
}

const toggleExportMenu = () => { showExportMenu.value = !showExportMenu.value }

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
    content = JSON.stringify({ prompt: result.value, type: currentType.value, generatedAt: new Date().toISOString() }, null, 2)
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
  if (!target.closest('.export-wrapper')) showExportMenu.value = false
}

function handleReuseHistory(event: Event) {
  const customEvent = event as CustomEvent<PromptRecord>
  const record = customEvent.detail

  if (record.generatedPrompt) {
    agentForm.value = {
      name: record.name || '',
      role: record.roleDescription || record.taskDescription || '',
      capabilities: record.capabilities || '',
      behaviors: record.behaviors || '',
      communicationStyle: record.communicationStyle || 'professional'
    }
  }
  currentType.value = 'agent'
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

onMounted(() => {
  if (route.query.task) {
    agentForm.value.role = String(route.query.task)
  }
  window.addEventListener('click', handleClickOutside)
  window.addEventListener('reuse-history', handleReuseHistory)
})

onUnmounted(() => {
  window.removeEventListener('click', handleClickOutside)
  window.removeEventListener('reuse-history', handleReuseHistory)
  if (cancelStream.value) cancelStream.value()
})
</script>

<style scoped>
/* ===== Page Layout ===== */
.generate-page {
  display: flex;
  flex-direction: column;
  gap: 20px;
  padding: 0 0 60px 0;
  min-height: calc(100vh - 200px);
}

/* ===== Type Selector ===== */
.type-selector {
  display: flex;
  justify-content: center;
  padding: 0 24px;
}

.selector-tabs {
  display: inline-flex;
  gap: 4px;
  padding: 6px;
  background: var(--bg-panel);
  border: 1px solid var(--border-color);
  border-radius: 16px;
}

.selector-tab {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 28px;
  border: none;
  border-radius: 12px;
  background: transparent;
  color: var(--text-secondary);
  font-size: 15px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.25s ease;
}

.selector-tab:hover {
  color: var(--text-primary);
  background: var(--bg-hover);
}

.selector-tab.active {
  background: linear-gradient(135deg, var(--color-primary-500), var(--color-primary-600));
  color: white;
  box-shadow: 0 4px 16px rgba(59, 130, 246, 0.3);
}

.selector-tab .tab-icon {
  font-size: 18px;
}

/* ===== Content Grid ===== */
.content-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
  padding: 0 24px;
  align-items: start;
}

@media (max-width: 1024px) {
  .content-grid {
    grid-template-columns: 1fr;
  }
}

/* ===== Form Panel ===== */
.form-panel {
  display: flex;
  flex-direction: column;
}

.form-card {
  background: var(--bg-card);
  border: 1px solid var(--border-color);
  border-radius: 16px;
  overflow: hidden;
}

.form-card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 20px;
  border-bottom: 1px solid var(--border-color);
  background: var(--bg-panel);
}

.card-title {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 16px;
  font-weight: 700;
  color: var(--text-primary);
}

.title-icon {
  font-size: 20px;
}

.btn-example {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 14px;
  border: 1px solid var(--border-color);
  border-radius: 8px;
  background: var(--bg-card);
  color: var(--text-secondary);
  font-size: 13px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-example:hover {
  border-color: var(--color-primary-400);
  color: var(--color-primary-600);
}

.form-card-body {
  padding: 20px;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.form-card-footer {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: 12px;
  padding: 16px 20px;
  border-top: 1px solid var(--border-color);
  background: var(--bg-panel);
}

/* ===== Form Elements ===== */
.form-row {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.form-row.two-col {
  flex-direction: row;
  gap: 16px;
}

.form-row.two-col > .form-group {
  flex: 1;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.form-label {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  font-weight: 600;
  color: var(--text-primary);
}

.required {
  color: var(--color-error);
  font-size: 11px;
}

.optional {
  color: var(--text-muted);
  font-weight: 400;
  font-size: 11px;
}

.form-error {
  font-size: 11px;
  color: var(--color-error);
  font-weight: 500;
}

.form-input,
.form-select,
.form-textarea {
  width: 100%;
  padding: 12px 14px;
  font-size: 14px;
  color: var(--text-primary);
  background: var(--bg-input);
  border: 1px solid var(--border-color);
  border-radius: 10px;
  transition: all 0.2s;
  font-family: inherit;
}

.form-input:hover,
.form-select:hover,
.form-textarea:hover {
  border-color: var(--border-hover);
}

.form-input:focus,
.form-select:focus,
.form-textarea:focus {
  outline: none;
  border-color: var(--color-primary-500);
  box-shadow: 0 0 0 3px var(--glow-primary-soft);
  background: var(--bg-card);
}

.form-input::placeholder,
.form-textarea::placeholder {
  color: var(--text-placeholder);
}

.form-input.error,
.form-textarea.error {
  border-color: var(--color-error);
  background: rgba(239, 68, 68, 0.05);
}

.form-textarea {
  resize: vertical;
  min-height: 72px;
  line-height: 1.6;
}

.form-textarea.code {
  font-family: var(--font-mono);
  font-size: 13px;
}

.form-select {
  cursor: pointer;
}

/* ===== Style/Type Chips ===== */
.style-chips,
.type-chips {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.style-chip,
.type-chip {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 14px;
  border: 1px solid var(--border-color);
  border-radius: 8px;
  background: var(--bg-card);
  color: var(--text-secondary);
  font-size: 13px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
}

.style-chip:hover,
.type-chip:hover {
  border-color: var(--color-primary-400);
  color: var(--text-primary);
}

.style-chip.active,
.type-chip.active {
  border-color: var(--color-primary-500);
  background: var(--glow-primary-soft);
  color: var(--color-primary-600);
}

.chip-icon {
  font-size: 14px;
}

/* ===== Buttons ===== */
.btn-reset {
  padding: 12px 20px;
  border: 1px solid var(--border-color);
  border-radius: 10px;
  background: transparent;
  color: var(--text-secondary);
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-reset:hover {
  background: var(--bg-hover);
  color: var(--text-primary);
}

.btn-generate {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 24px;
  border: none;
  border-radius: 10px;
  background: linear-gradient(135deg, var(--color-primary-600), var(--color-primary-700));
  color: white;
  font-size: 14px;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.25s;
  box-shadow: 0 4px 16px rgba(59, 130, 246, 0.3);
}

.btn-generate:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(59, 130, 246, 0.4);
}

.btn-generate:disabled {
  opacity: 0.5;
  cursor: not-allowed;
  transform: none;
}

.spinner {
  width: 14px;
  height: 14px;
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-top-color: white;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

/* ===== Result Panel ===== */
.result-panel {
  position: sticky;
  top: 20px;
}

.result-card {
  background: var(--bg-card);
  border: 1px solid var(--border-color);
  border-radius: 16px;
  overflow: hidden;
  height: fit-content;
  max-height: calc(100vh - 100px);
  display: flex;
  flex-direction: column;
}

.result-card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 20px;
  border-bottom: 1px solid var(--border-color);
  background: var(--bg-panel);
  flex-shrink: 0;
}

.result-title {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 15px;
  font-weight: 700;
  color: var(--text-primary);
}

.result-icon {
  font-size: 18px;
}

.streaming-indicator {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 4px 10px;
  background: var(--glow-primary-soft);
  color: var(--color-primary-600);
  font-size: 11px;
  font-weight: 600;
  border-radius: 20px;
}

.stream-dot {
  width: 6px;
  height: 6px;
  background: var(--color-primary-500);
  border-radius: 50%;
  animation: pulse 1s ease-in-out infinite;
}

@keyframes pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.4; }
}

.result-toolbar {
  display: flex;
  gap: 8px;
}

.toolbar-btn {
  width: 34px;
  height: 34px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 1px solid var(--border-color);
  border-radius: 8px;
  background: var(--bg-card);
  cursor: pointer;
  transition: all 0.2s;
  font-size: 15px;
}

.toolbar-btn:hover {
  background: var(--bg-hover);
  border-color: var(--color-primary-400);
}

.export-wrapper {
  position: relative;
}

.export-menu {
  position: absolute;
  top: 100%;
  right: 0;
  margin-top: 8px;
  padding: 6px;
  background: var(--bg-panel);
  border: 1px solid var(--border-color);
  border-radius: 10px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
  min-width: 130px;
  z-index: 10;
}

.export-menu button {
  display: flex;
  align-items: center;
  gap: 8px;
  width: 100%;
  padding: 10px 12px;
  border: none;
  border-radius: 6px;
  background: transparent;
  color: var(--text-primary);
  font-size: 13px;
  cursor: pointer;
}

.export-menu button:hover {
  background: var(--glow-primary-soft);
  color: var(--color-primary-600);
}

.result-card-body {
  flex: 1;
  overflow-y: auto;
  min-height: 300px;
}

.result-content {
  padding: 16px;
  background: var(--bg-panel);
}

.result-content :deep(.markdown-body) {
  font-size: 14px;
  line-height: 1.8;
  color: var(--text-secondary);
}

.result-content :deep(.markdown-body h1),
.result-content :deep(.markdown-body h2),
.result-content :deep(.markdown-body h3) {
  color: var(--text-primary);
  margin-top: 1.2em;
  margin-bottom: 0.5em;
  font-weight: 700;
}

.result-content :deep(.markdown-body h2) {
  color: var(--color-primary-600);
  font-size: 1.1em;
  border-bottom: 1px solid var(--border-color);
  padding-bottom: 6px;
}

.result-content :deep(.markdown-body p) {
  margin: 0 0 0.8em 0;
}

.result-content :deep(.markdown-body ul),
.result-content :deep(.markdown-body ol) {
  margin: 0.5em 0;
  padding-left: 1.3em;
}

.result-content :deep(.markdown-body li) {
  margin: 4px 0;
}

.result-content :deep(.markdown-body code) {
  padding: 0.2em 0.4em;
  background: var(--bg-hover);
  border-radius: 4px;
  font-family: var(--font-mono);
  font-size: 0.9em;
  color: var(--color-primary-600);
}

.result-content :deep(.markdown-body pre) {
  margin: 1em 0;
  border-radius: 10px;
  overflow: hidden;
  background: #1e1e2e;
}

.result-content :deep(.markdown-body pre code) {
  display: block;
  padding: 1em;
  background: transparent;
  color: #cdd6f4;
  font-size: 13px;
  line-height: 1.6;
  overflow-x: auto;
}

.typing-cursor {
  display: inline-block;
  width: 2px;
  height: 16px;
  background: var(--color-primary-400);
  margin-left: 2px;
  animation: blink 1s step-end infinite;
  vertical-align: text-bottom;
}

@keyframes blink {
  0%, 100% { opacity: 1; }
  50% { opacity: 0; }
}

/* ===== Empty State ===== */
.result-empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 40px;
  text-align: center;
  background: var(--bg-card);
  border: 2px dashed var(--border-color);
  border-radius: 16px;
  min-height: 400px;
}

.empty-visual {
  width: 80px;
  height: 80px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--glow-primary-soft);
  border-radius: 20px;
  margin-bottom: 20px;
}

.empty-icon {
  font-size: 36px;
}

.empty-title {
  font-size: 16px;
  font-weight: 700;
  color: var(--text-primary);
  margin: 0 0 8px 0;
}

.empty-desc {
  font-size: 14px;
  color: var(--text-muted);
  margin: 0;
}

/* ===== Transitions ===== */
.result-appear-enter-active {
  animation: slideUp 0.35s cubic-bezier(0.16, 1, 0.3, 1);
}

@keyframes slideUp {
  from { opacity: 0; transform: translateY(16px); }
  to { opacity: 1; transform: translateY(0); }
}

.dropdown-enter-active,
.dropdown-leave-active {
  transition: all 0.2s ease;
}

.dropdown-enter-from,
.dropdown-leave-to {
  opacity: 0;
  transform: translateY(-6px);
}

/* ===== Responsive ===== */
@media (max-width: 768px) {
  .generate-page {
    padding: 0 0 40px 0;
  }

  .content-grid {
    padding: 0 16px;
    gap: 16px;
  }

  .form-row.two-col {
    flex-direction: column;
  }

  .selector-tabs {
    width: 100%;
    justify-content: center;
  }

  .selector-tab {
    flex: 1;
    justify-content: center;
    padding: 12px 16px;
  }

  .form-card-footer {
    flex-direction: column;
  }

  .btn-reset, .btn-generate {
    width: 100%;
    justify-content: center;
  }
}
</style>
