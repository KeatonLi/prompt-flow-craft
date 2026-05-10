<template>
  <AppLayout>
    <template #main>
      <div class="generate-page">
        <!-- Type Selector -->
        <div class="type-selector">
          <button
            v-for="t in promptTypes"
            :key="t.value"
            class="type-btn"
            :class="{ active: currentType === t.value }"
            @click="switchType(t.value)"
          >
            <span class="type-icon">{{ t.icon }}</span>
            <span class="type-name">{{ t.name }}</span>
          </button>
        </div>

        <!-- Main Content -->
        <div class="content-area">
          <!-- Left: Form -->
          <div class="form-section">
            <!-- Agent Form -->
            <div v-show="currentType === 'agent'" class="form-card">
              <div class="card-header">
                <div class="header-tabs">
                  <button class="tab-btn active">
                    <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                      <circle cx="12" cy="12" r="10"/>
                      <path d="M12 6v6l4 2"/>
                    </svg>
                    <span>Agent 提示词</span>
                  </button>
                </div>
                <button class="btn-example" @click="loadAgentExample">
                  <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <polygon points="13 2 3 14 12 14 11 22 21 10 12 10 13 2"/>
                  </svg>
                  <span>示例</span>
                </button>
              </div>

              <div class="card-body">
                <div class="form-group">
                  <label class="form-label">Agent 名称 <span class="required">*</span></label>
                  <input
                    v-model="agentForm.name"
                    type="text"
                    class="form-input"
                    placeholder="例如: 写作助手, 代码审查员"
                  />
                </div>

                <div class="form-group">
                  <label class="form-label">Agent 角色定位 <span class="required">*</span></label>
                  <textarea
                    v-model="agentForm.role"
                    class="form-textarea"
                    placeholder="描述 Agent 的核心角色，例如：你是一个专业的技术文档写作助手..."
                    rows="3"
                  ></textarea>
                </div>

                <div class="form-group">
                  <label class="form-label">核心能力</label>
                  <textarea
                    v-model="agentForm.capabilities"
                    class="form-textarea"
                    placeholder="列出 Agent 可以做什么，例如：&#10;- 撰写技术文档&#10;- 回答用户问题&#10;- 提供代码示例"
                    rows="4"
                  ></textarea>
                </div>

                <div class="form-group">
                  <label class="form-label">行为规范</label>
                  <textarea
                    v-model="agentForm.behaviors"
                    class="form-textarea"
                    placeholder="定义 Agent 的行为准则..."
                    rows="3"
                  ></textarea>
                </div>

                <div class="form-group">
                  <label class="form-label">对话风格</label>
                  <select v-model="agentForm.communicationStyle" class="form-select">
                    <option value="professional">专业正式</option>
                    <option value="friendly">友好轻松</option>
                    <option value="concise">简洁干练</option>
                    <option value="detailed">详细全面</option>
                    <option value="casual">随性自然</option>
                  </select>
                </div>
              </div>

              <div class="card-footer">
                <button class="btn-reset" @click="resetAgent">重置</button>
                <button class="btn-generate" :disabled="!canGenerateAgent || loading" @click="generateAgent">
                  <template v-if="loading && currentType === 'agent'">
                    <span class="spinner"></span>
                    生成中...
                  </template>
                  <template v-else>
                    <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                      <polygon points="13 2 3 14 12 14 11 22 21 10 12 10 13 2"/>
                    </svg>
                    生成 Agent
                  </template>
                </button>
              </div>
            </div>

            <!-- Skill Form -->
            <div v-show="currentType === 'skill'" class="form-card">
              <div class="card-header">
                <div class="header-tabs">
                  <button class="tab-btn active">
                    <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                      <path d="M12 2L2 7l10 5 10-5-10-5z"/>
                      <path d="M2 17l10 5 10-5"/>
                      <path d="M2 12l10 5 10-5"/>
                    </svg>
                    <span>Skill 提示词</span>
                  </button>
                </div>
                <button class="btn-example" @click="loadSkillExample">
                  <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <polygon points="13 2 3 14 12 14 11 22 21 10 12 10 13 2"/>
                  </svg>
                  <span>示例</span>
                </button>
              </div>

              <div class="card-body">
                <div class="form-group">
                  <label class="form-label">Skill 名称 <span class="required">*</span></label>
                  <input
                    v-model="skillForm.name"
                    type="text"
                    class="form-input"
                    placeholder="例如: get_weather, search_news"
                  />
                  <div class="form-hint">使用 snake_case 命名</div>
                </div>

                <div class="form-group">
                  <label class="form-label">功能描述 <span class="required">*</span></label>
                  <textarea
                    v-model="skillForm.description"
                    class="form-textarea"
                    placeholder="详细描述这个 Skill 的功能和使用场景..."
                    rows="3"
                  ></textarea>
                </div>

                <div class="form-group">
                  <label class="form-label">Skill 类型</label>
                  <div class="skill-type-selector">
                    <button
                      v-for="t in skillTypes"
                      :key="t.value"
                      class="type-btn"
                      :class="{ active: skillForm.type === t.value }"
                      @click="skillForm.type = t.value"
                    >
                      <span>{{ t.icon }}</span>
                      <span>{{ t.name }}</span>
                    </button>
                  </div>
                </div>

                <div v-if="skillForm.type === 'api'" class="form-section">
                  <div class="form-grid">
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
                        placeholder="https://api.example.com/v1/..."
                      />
                    </div>
                  </div>
                </div>

                <div v-if="skillForm.type === 'function'" class="form-section">
                  <div class="form-group">
                    <label class="form-label">函数代码</label>
                    <textarea
                      v-model="skillForm.functionCode"
                      class="form-textarea code-textarea"
                      placeholder="// 输入函数代码"
                      rows="6"
                    ></textarea>
                  </div>
                </div>

                <div class="form-group">
                  <label class="form-label">输入参数 <span class="optional">(可选)</span></label>
                  <textarea
                    v-model="skillForm.parameters"
                    class="form-textarea"
                    placeholder="定义输入参数，JSON 格式"
                    rows="3"
                  ></textarea>
                </div>

                <div class="form-group">
                  <label class="form-label">输出描述 <span class="optional">(可选)</span></label>
                  <textarea
                    v-model="skillForm.outputDescription"
                    class="form-textarea"
                    placeholder="描述函数的返回值结构..."
                    rows="2"
                  ></textarea>
                </div>
              </div>

              <div class="card-footer">
                <button class="btn-reset" @click="resetSkill">重置</button>
                <button class="btn-generate" :disabled="!canGenerateSkill || loading" @click="generateSkill">
                  <template v-if="loading && currentType === 'skill'">
                    <span class="spinner"></span>
                    生成中...
                  </template>
                  <template v-else>
                    <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                      <polygon points="13 2 3 14 12 14 11 22 21 10 12 10 13 2"/>
                    </svg>
                    生成 Skill
                  </template>
                </button>
              </div>
            </div>
          </div>

          <!-- Right: Result -->
          <div class="result-section">
            <Transition name="result-appear">
              <div v-if="showResult" class="result-card">
                <div class="card-header">
                  <div class="result-title">
                    <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                      <path d="M12 3l1.5 4.5L18 9l-4.5 1.5L12 15l-1.5-4.5L6 9l4.5-1.5L12 3z"/>
                      <path d="M5 19l1 3 3-1-1-3-3 1z"/>
                      <path d="M19 5l1 3 3-1-1-3-3 1z"/>
                    </svg>
                    <span>生成结果</span>
                    <span v-if="isStreaming" class="streaming-badge">
                      <span class="stream-dot"></span>
                      AI 思考中...
                    </span>
                  </div>
                  <div class="result-actions">
                    <button class="action-btn-small" @click="copyResult" title="复制">
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
                          <button @click="exportPrompt('markdown')">📝 Markdown</button>
                          <button @click="exportPrompt('json')">📋 JSON</button>
                          <button @click="exportPrompt('txt')">📄 纯文本</button>
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

            <div v-if="!showResult" class="empty-result">
              <svg width="64" height="64" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
                <path d="M14.5 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V7.5L14.5 2z"/>
                <polyline points="14 2 14 8 20 8"/>
                <line x1="16" y1="13" x2="8" y2="13"/>
                <line x1="16" y1="17" x2="8" y2="17"/>
                <line x1="10" y1="9" x2="8" y2="9"/>
              </svg>
              <p>填写表单后点击生成</p>
              <span>结果将显示在这里</span>
            </div>
          </div>
        </div>
      </div>
    </template>

    <template #sidebar-right>
      <HistoryPanel />
    </template>
  </AppLayout>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRoute } from 'vue-router'
import MarkdownIt from 'markdown-it'
import hljs from 'highlight.js'
import AppLayout from '@/components/layout/AppLayout.vue'
import HistoryPanel from '@/components/history/HistoryPanel.vue'
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
const cancelStream = ref<(() => void) | null>(null)

const canGenerateAgent = computed(() => agentForm.value.name.length > 0 && agentForm.value.role.length > 0)
const canGenerateSkill = computed(() => skillForm.value.name.length > 0 && skillForm.value.description.length > 0)

const displayedResultWithCursor = computed(() => {
  const raw = displayedResult.value || result.value
  const content = md.render(raw)
  if (isStreaming.value) return content + '<span class="typing-cursor"></span>'
  return content
})

const switchType = (type: 'agent' | 'skill') => {
  currentType.value = type
  reset()
}

const loadAgentExample = () => {
  agentForm.value = {
    name: '写作助手',
    role: '你是一个专业的技术文档写作助手，擅长将复杂的技术概念解释得通俗易懂。',
    capabilities: '- 撰写技术文档和教程\n- 解释技术概念\n- 提供代码示例\n- 校对和润色文本',
    behaviors: '- 始终保持专业态度\n- 回答简洁明了\n- 主动提供相关示例',
    communicationStyle: 'professional'
  }
}

const loadSkillExample = () => {
  skillForm.value = {
    name: 'get_weather',
    description: '获取指定城市的当前天气信息，包括温度、湿度、风速和天气状况',
    type: 'api',
    method: 'GET',
    endpoint: 'https://api.weather.example.com/v1/current',
    functionCode: '',
    parameters: '{"city": {"type": "string", "description": "城市名称"}}',
    outputDescription: '返回城市名称，温度、湿度、风速等信息'
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
}

const onStreamDone = () => {
  isStreaming.value = false
  loading.value = false
  cancelStream.value = null
}

const onStreamError = () => {
  isStreaming.value = false
  loading.value = false
  cancelStream.value = null
}

const reset = () => {
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
  if (!target.closest('.export-dropdown')) showExportMenu.value = false
}

function handleReuseHistory(event: Event) {
  const customEvent = event as CustomEvent<PromptRecord>
  const record = customEvent.detail

  // Support both old PromptRecord and new AgentRecord formats
  if (record.generatedPrompt) {
    // New AgentRecord format
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
.generate-page {
  display: flex;
  flex-direction: column;
  gap: 20px;
  padding-bottom: 40px;
}

.type-selector {
  display: flex;
  gap: 12px;
  justify-content: center;
}

.type-btn {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 14px 32px;
  border: 2px solid var(--border-color);
  border-radius: var(--radius-2xl);
  background: var(--bg-card);
  cursor: pointer;
  transition: all 0.3s;
  font-size: var(--text-base);
  font-weight: 600;
}

.type-btn:hover {
  border-color: var(--color-primary-400);
  transform: translateY(-2px);
}

.type-btn.active {
  border-color: var(--color-primary-500);
  background: linear-gradient(135deg, var(--color-primary-500), var(--color-primary-600));
  color: white;
  box-shadow: 0 8px 24px rgba(59, 130, 246, 0.35);
}

.type-icon {
  font-size: 1.4rem;
}

.content-area {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 24px;
  align-items: start;
}

.form-section {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.result-section {
  position: sticky;
  top: 20px;
  max-height: calc(100vh - 120px);
  overflow-y: auto;
}

.form-card {
  background: var(--bg-card);
  border: 1px solid var(--border-color);
  border-radius: var(--radius-2xl);
  box-shadow: var(--shadow-card);
  overflow: hidden;
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 20px;
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
  padding: 8px 16px;
  border: none;
  border-radius: var(--radius-lg);
  background: transparent;
  color: var(--text-secondary);
  font-size: var(--text-sm);
  font-weight: 600;
  cursor: pointer;
}

.tab-btn.active {
  background: var(--glow-primary-soft);
  color: var(--color-primary-600);
}

.btn-example {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 16px;
  border: none;
  border-radius: var(--radius-lg);
  background: linear-gradient(135deg, var(--color-primary-600), var(--color-primary-700));
  color: white;
  font-size: var(--text-sm);
  font-weight: 600;
  cursor: pointer;
}

.card-body {
  padding: 20px;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.form-label {
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

.form-hint {
  font-size: var(--text-xs);
  color: var(--text-muted);
}

.form-input,
.form-select,
.form-textarea {
  width: 100%;
  padding: 12px 16px;
  font-size: var(--text-sm);
  color: var(--text-primary);
  background: var(--bg-input);
  border: 1px solid var(--border-color);
  border-radius: var(--radius-xl);
  transition: all 0.2s;
}

.form-input:focus,
.form-select:focus,
.form-textarea:focus {
  outline: none;
  border-color: var(--color-primary-500);
  box-shadow: 0 0 0 4px var(--glow-primary-soft);
}

.form-textarea {
  resize: vertical;
}

.form-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
}

.form-section {
  padding: 16px;
  background: var(--bg-panel);
  border-radius: var(--radius-xl);
  border: 1px solid var(--border-color);
}

.skill-type-selector {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.skill-type-selector .type-btn {
  padding: 8px 16px;
  font-size: var(--text-xs);
}

.code-textarea {
  font-family: var(--font-mono);
  font-size: var(--text-xs);
}

.card-footer {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: 12px;
  padding: 16px 20px;
  border-top: 1px solid var(--border-color);
  background: var(--bg-panel);
}

.btn-reset {
  padding: 10px 20px;
  border: 1px solid var(--border-color);
  border-radius: var(--radius-xl);
  background: transparent;
  color: var(--text-secondary);
  font-size: var(--text-sm);
  font-weight: 600;
  cursor: pointer;
}

.btn-reset:hover {
  background: var(--bg-hover);
}

.btn-generate {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 24px;
  border: none;
  border-radius: var(--radius-xl);
  background: linear-gradient(135deg, var(--color-primary-600), var(--color-primary-700));
  color: white;
  font-size: var(--text-sm);
  font-weight: 700;
  cursor: pointer;
  box-shadow: 0 8px 24px rgba(59, 130, 246, 0.35);
}

.btn-generate:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 12px 32px rgba(59, 130, 246, 0.45);
}

.btn-generate:disabled {
  opacity: 0.5;
  cursor: not-allowed;
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

.result-card {
  background: var(--bg-card);
  border: 1px solid var(--border-color);
  border-radius: var(--radius-2xl);
  box-shadow: var(--shadow-card);
  overflow: hidden;
}

.result-title {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: var(--text-base);
  font-weight: 600;
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
}

.action-btn-small:hover {
  background: var(--bg-hover);
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
  min-width: 140px;
  z-index: 10;
}

.export-menu button {
  display: flex;
  align-items: center;
  gap: 8px;
  width: 100%;
  padding: 10px 14px;
  border: none;
  border-radius: var(--radius-lg);
  background: transparent;
  color: var(--text-primary);
  font-size: var(--text-sm);
  cursor: pointer;
}

.export-menu button:hover {
  background: var(--glow-primary-soft);
  color: var(--color-primary-600);
}

.result-body {
  padding: 0;
}

.result-content {
  max-height: 500px;
  overflow-y: auto;
  background: var(--bg-panel);
  padding: 20px;
}

.empty-result {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 80px 40px;
  text-align: center;
  background: var(--bg-card);
  border: 1px dashed var(--border-color);
  border-radius: var(--radius-2xl);
  color: var(--text-secondary);
}

.empty-result svg {
  margin-bottom: 16px;
  opacity: 0.4;
}

.empty-result p {
  font-weight: 600;
  margin: 0 0 4px 0;
}

.empty-result span {
  font-size: var(--text-sm);
}

.markdown-body {
  font-size: var(--text-sm);
  line-height: 1.8;
  color: var(--text-secondary);
}

.markdown-body h1, .markdown-body h2, .markdown-body h3 {
  color: var(--text-primary);
  margin-top: 1.5em;
  margin-bottom: 0.5em;
}

.markdown-body h2 { color: var(--color-primary-600); }

.markdown-body p { margin: 0 0 1em 0; }

.markdown-body ul, .markdown-body ol {
  margin: 0.5em 0;
  padding-left: 1.5em;
}

.markdown-body code {
  padding: 0.15em 0.4em;
  background: var(--bg-hover);
  border-radius: var(--radius-md);
  font-family: var(--font-mono);
  font-size: 0.88em;
  color: var(--color-primary-600);
  border: 1px solid var(--border-color);
}

.markdown-body pre {
  margin: 1em 0;
  border-radius: var(--radius-xl);
  overflow: hidden;
  background: #1e1e2e;
  border: 1px solid rgba(255, 255, 255, 0.06);
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

.result-appear-enter-active {
  animation: slideUp 0.5s cubic-bezier(0.16, 1, 0.3, 1);
}

@keyframes slideUp {
  from { opacity: 0; transform: translateY(30px) scale(0.98); }
  to { opacity: 1; transform: translateY(0) scale(1); }
}

.dropdown-fade-enter-active,
.dropdown-fade-leave-active {
  transition: all 0.2s ease;
}

.dropdown-fade-enter-from,
.dropdown-fade-leave-to {
  opacity: 0;
  transform: translateY(-8px);
}

@media (max-width: 1200px) {
  .content-area {
    grid-template-columns: 1fr;
  }

  .result-section {
    position: static;
  }
}

@media (max-width: 768px) {
  .type-selector {
    flex-direction: column;
  }

  .form-grid {
    grid-template-columns: 1fr;
  }

  .card-footer {
    flex-direction: column;
  }

  .btn-reset, .btn-generate {
    width: 100%;
    justify-content: center;
  }
}
</style>