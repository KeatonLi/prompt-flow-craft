<template>
  <AppLayout>
    <template #main>
      <div class="generate-page">
        <div class="generate-container">
          <!-- Left Panel: Input -->
          <div class="input-panel">
            <div class="input-wrapper">
              <!-- Type + Mode Selector -->
              <div class="input-header">
                <div class="type-selector">
                  <button
                    v-for="t in promptTypes"
                    :key="t.value"
                    class="type-btn"
                    :class="{ active: currentType === t.value }"
                    @click="switchType(t.value)"
                  >
                    <span class="type-btn-icon" v-html="t.icon"></span>
                    <span class="type-btn-text">{{ t.name }}</span>
                  </button>
                </div>
                <label class="pipeline-toggle" title="启用多阶段流水线审查">
                  <input type="checkbox" v-model="pipelineMode" :disabled="loading" />
                  <span class="toggle-track">
                    <span class="toggle-knob"></span>
                  </span>
                  <span class="toggle-label">Pipeline</span>
                </label>
              </div>

              <!-- Big Text Input -->
              <div class="input-area">
                <textarea
                  v-model="userInput"
                  ref="inputRef"
                  class="big-input"
                  :placeholder="inputPlaceholder"
                  rows="6"
                  @keydown.ctrl.enter="doGenerate"
                ></textarea>
                <div class="input-footer">
                  <span class="input-hint">Ctrl+Enter 发送</span>
                  <div class="input-actions">
                    <button class="btn-example" @click="loadExample" title="填入示例">
                      <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                        <path d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.747 0 3.332.477 4.5 1.253v13C19.832 18.477 18.247 18 16.5 18c-1.746 0-3.332.477-4.5 1.253"/>
                      </svg>
                    </button>
                    <button class="btn-reset" @click="resetAll" :disabled="loading" title="清空">
                      <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                        <polyline points="1 4 1 10 7 10"/>
                        <path d="M3.51 15a9 9 0 1 0 2.13-9.36L1 10"/>
                      </svg>
                    </button>
                    <button
                      class="btn-generate"
                      :disabled="!userInput.trim() || loading"
                      @click="doGenerate"
                    >
                      <template v-if="loading">
                        <span class="spinner"></span>
                        生成中...
                      </template>
                      <template v-else>
                        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                          <line x1="22" y1="2" x2="11" y2="13"/>
                          <polygon points="22 2 15 22 11 13 2 9 22 2"/>
                        </svg>
                        生成
                      </template>
                    </button>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- Right Panel: Thinking + Result -->
          <div class="result-panel">
            <!-- Pipeline Thinking Process -->
            <Transition name="result-appear">
              <div v-if="showResult || thinkingTrace.length > 0" class="result-card">
                <div class="result-card-header">
                  <div class="result-title">
                    <svg class="result-icon" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                      <path d="M9.663 17h4.673M12 3v1m6.364 1.636l-.707.707M21 12h-1M4 12H3m3.343-5.657l-.707-.707m2.828 9.9a5 5 0 117.072 0l-.548.547A3.374 3.374 0 0014 18.469V19a2 2 0 11-4 0v-.531c0-.895-.356-1.754-.988-2.386l-.548-.547z"/>
                    </svg>
                    <span>生成过程</span>
                  </div>
                  <div v-if="!isStreaming && result" class="result-actions">
                    <button class="action-btn" :class="{ success: copySuccess }" @click="copyResult" title="复制">
                      <svg v-if="!copySuccess" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                        <rect x="9" y="9" width="13" height="13" rx="2" ry="2"/>
                        <path d="M5 15H4a2 2 0 0 1-2-2V4a2 2 0 0 1 2-2h9a2 2 0 0 1 2 2v1"/>
                      </svg>
                      <svg v-else width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                        <polyline points="20 6 9 17 4 12"/>
                      </svg>
                    </button>
                  </div>
                </div>

                <!-- Thinking Trace -->
                <div class="thinking-trace">
                  <div
                    v-for="(step, idx) in thinkingTrace"
                    :key="idx"
                    class="thinking-step"
                    :class="'step-' + step.type"
                  >
                    <div class="step-header">
                      <span class="step-icon">{{ stepIcons[step.type] || '●' }}</span>
                      <span class="step-title">{{ step.title }}</span>
                      <span v-if="step.status === 'running'" class="step-badge running">
                        <span class="running-dot"></span> 进行中
                      </span>
                      <span v-if="step.status === 'done'" class="step-badge done">✓ 完成</span>
                      <span v-if="step.status === 'error'" class="step-badge error">✗ 失败</span>
                      <span v-if="step.score !== undefined" class="step-score" :class="scoreClass(step.score)">
                        {{ step.score }}分
                      </span>
                    </div>
                    <div v-if="step.content" class="step-content" :class="{ 'is-streaming': step.status === 'running' }">
                      <div v-if="step.type === 'audit'" class="audit-summary">{{ step.content }}</div>
                      <div v-else class="step-markdown" v-html="renderMarkdown(step.content)"></div>
                    </div>
                    <div v-if="step.collapsible" class="step-toggle" @click="toggleStep(idx)">
                      <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                        <polyline points="6 9 12 15 18 9"/>
                      </svg>
                      {{ step.expanded ? '收起' : '展开详情' }}
                    </div>
                  </div>
                </div>

                <!-- Final Result -->
                <div v-if="result && !isStreaming" class="result-divider">
                  <span class="divider-label">最终输出</span>
                </div>
                <div v-if="result" class="result-body">
                  <div class="markdown-body" v-html="displayedResultWithCursor"></div>
                </div>
              </div>
            </Transition>

            <!-- Empty State -->
            <div v-if="!showResult && thinkingTrace.length === 0" class="result-empty">
              <div class="empty-icon">
                <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
                  <path d="M9.663 17h4.673M12 3v1m6.364 1.636l-.707.707M21 12h-1M4 12H3m3.343-5.657l-.707-.707m2.828 9.9a5 5 0 117.072 0l-.548.547A3.374 3.374 0 0014 18.469V19a2 2 0 11-4 0v-.531c0-.895-.356-1.754-.988-2.386l-.548-.547z"/>
                </svg>
              </div>
              <h3 class="empty-title">告诉我想创建什么样的提示词</h3>
              <p class="empty-desc">在左侧输入需求描述，点击生成或按 Ctrl+Enter</p>
              <div class="example-hints">
                <button class="hint-chip" @click="fillExample('agent')">
                  🤖 创建一个客服助手 Agent
                </button>
                <button class="hint-chip" @click="fillExample('skill')">
                  ⚡ 写一个天气查询工具
                </button>
                <button class="hint-chip" @click="fillExample('agent-dev')">
                  💻 帮我写一个代码审查 Agent
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </template>
  </AppLayout>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted, nextTick } from 'vue'
import { useRoute } from 'vue-router'
import MarkdownIt from 'markdown-it'
import hljs from 'highlight.js'
import AppLayout from '@/components/layout/AppLayout.vue'
import { promptApi } from '@/api/prompt'
import type { PipelineStageEvent, PipelineStageCompleteEvent } from '@/api/prompt'

const highlightCode = (str: string, lang: string) => {
  if (lang && hljs.getLanguage(lang)) {
    try {
      return '<pre class="hljs-code"><code>' +
        hljs.highlight(str, { language: lang, ignoreIllegals: true }).value +
        '</code></pre>';
    } catch (_) {}
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

// ========== 状态 ==========
const currentType = ref<'agent' | 'skill'>('agent')
const userInput = ref('')
const loading = ref(false)
const isStreaming = ref(false)
const showResult = ref(false)
const result = ref('')
const cancelStream = ref<(() => void) | null>(null)
const pipelineMode = ref(true) // 默认启用 Pipeline
const copySuccess = ref(false)
const inputRef = ref<HTMLTextAreaElement | null>(null)

// 思考过程追踪
interface ThinkingStep {
  type: 'draft' | 'audit' | 'refine'
  title: string
  status: 'pending' | 'running' | 'done' | 'error'
  content: string
  score?: number
  expanded: boolean
  collapsible: boolean
}

const thinkingTrace = ref<ThinkingStep[]>([])
const currentStepIdx = ref(-1)

const stepIcons: Record<string, string> = {
  draft: '📝',
  audit: '🔍',
  refine: '✨'
}

// ========== 类型选择 ==========
const promptTypes = [
  {
    value: 'agent',
    name: 'Agent',
    icon: '<svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"/><path d="M12 6v6l4 2"/></svg>'
  },
  {
    value: 'skill',
    name: 'Skill',
    icon: '<svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M13 2L3 14h9l-1 8 10-12h-9l1-8z"/></svg>'
  }
]

const inputPlaceholder = computed(() => {
  if (currentType.value === 'agent') {
    return '描述你想要创建的 Agent...\n例如：帮我创建一个技术文档助手，它能将复杂的编程概念用通俗的语言解释清楚，并自动生成 API 文档\n\n提示：描述越详细，生成的提示词质量越高'
  }
  return '描述你想要创建的 Skill 工具...\n例如：帮我创建一个天气查询工具，接收城市名称参数，返回实时天气 JSON 数据'
})

// ========== 渲染 ==========
const displayedResultWithCursor = computed(() => {
  const content = result.value || ''
  if (isStreaming.value) {
    const rendered = md.render(content)
    return rendered + '<span class="typing-cursor"></span>'
  }
  return md.render(content)
})

function renderMarkdown(text: string): string {
  if (!text) return ''
  return md.render(text)
}

function scoreClass(score: number): string {
  if (score >= 80) return 'score-excellent'
  if (score >= 60) return 'score-good'
  return 'score-poor'
}

// ========== 操作 ==========
const switchType = (type: 'agent' | 'skill') => {
  currentType.value = type
  resetAll()
}

const loadExample = () => {
  if (currentType.value === 'agent') {
    userInput.value = '帮我创建一个技术文档助手，它能将复杂的编程概念和技术架构用通俗易懂的语言解释清楚，擅长撰写技术设计文档和API接口文档，语言简洁专业，主动提供实际应用案例'
  } else {
    userInput.value = '帮我创建一个天气查询工具，接收城市名称作为参数，通过HTTP GET请求获取实时天气数据，返回JSON格式的天气信息'
  }
}

const fillExample = (type: string) => {
  currentType.value = type as 'agent' | 'skill'
  loadExample()
}

const doGenerate = async () => {
  if (!userInput.value.trim()) {
    toast({ message: '请先描述你的需求', type: 'warning' })
    await nextTick()
    inputRef.value?.focus()
    return
  }
  if (loading.value) return
  
  // 重置状态
  result.value = ''
  showResult.value = true
  loading.value = true
  isStreaming.value = true
  thinkingTrace.value = []
  currentStepIdx.value = -1

  if (pipelineMode.value) {
    await generateWithPipeline()
  } else {
    await generateSimple()
  }
}

// 简单模式（单次生成）
const generateSimple = async () => {
  const text = userInput.value.trim()
  const api = currentType.value === 'agent'
    ? promptApi.generateAgentStream({
        name: extractName(text) || '助手',
        roleDescription: text,
        capabilities: '',
        behaviors: '',
        communicationStyle: 'professional'
      }, onStreamContent, onStreamDone, onStreamError)
    : promptApi.generateSkillStream({
        name: extractName(text) || '工具',
        description: text,
        skillType: 'api',
        method: 'GET',
        endpoint: '',
        parameters: '',
        outputDescription: ''
      }, onStreamContent, onStreamDone, onStreamError)

  cancelStream.value = api
}

// Pipeline 模式（多阶段）
const generateWithPipeline = async () => {
  const text = userInput.value.trim()
  const isAgent = currentType.value === 'agent'
  const name = extractName(text) || (isAgent ? '智能助手' : '智能工具')

  // 初始化思考步骤
  thinkingTrace.value = [
    { type: 'draft', title: '草稿生成', status: 'pending', content: '', expanded: true, collapsible: false },
    { type: 'audit', title: '质量审查', status: 'pending', content: '', expanded: true, collapsible: false },
    { type: 'refine', title: '精炼优化', status: 'pending', content: '', expanded: false, collapsible: true }
  ]

  // 当前输出的步骤索引（0=draft, 1=audit, 2=refine）
  let activeStep = 0
  currentStepIdx.value = 0

  const requestData = isAgent
    ? {
        promptType: 'agent' as const,
        name: name,
        roleDescription: text,
        capabilities: '',
        behaviors: '',
        communicationStyle: 'professional'
      }
    : {
        promptType: 'skill' as const,
        name: name,
        description: text,
        skillType: 'api' as const,
        method: 'GET',
        endpoint: '',
        parameters: '',
        outputDescription: ''
      }

  cancelStream.value = promptApi.generatePipelineStream(
    requestData,
    {
      onStageStart: (event: PipelineStageEvent) => {
        // 映射 stage 到步骤索引
        const stageMap: Record<string, number> = { draft: 0, audit: 1, refine: 2 }
        const idx = stageMap[event.stage] ?? 0
        activeStep = idx
        currentStepIdx.value = idx

        // 标记为进行中
        for (let i = 0; i < thinkingTrace.value.length; i++) {
          if (i < idx) thinkingTrace.value[i].status = 'done'
          else if (i === idx) thinkingTrace.value[i].status = 'running'
          else thinkingTrace.value[i].status = 'pending'
        }

        // 清空当前步骤的内容
        thinkingTrace.value[idx].content = ''
      },

      onMessage: (content: string) => {
        // 追加内容到当前步骤
        const step = thinkingTrace.value[activeStep]
        if (step) {
          step.content += content
        }
      },

      onStageComplete: (event: PipelineStageCompleteEvent) => {
        const stageMap: Record<string, number> = { draft: 0, audit: 1, refine: 2 }
        const idx = stageMap[event.stage] ?? 0

        if (event.status === 'ok') {
          thinkingTrace.value[idx].status = 'done'
          if (event.score !== undefined) {
            thinkingTrace.value[idx].score = event.score
          }
          // 审计阶段完成时展开精炼步骤
          if (event.stage === 'audit') {
            thinkingTrace.value[2].expanded = true
          }
        } else {
          thinkingTrace.value[idx].status = 'error'
        }
      },

      onDone: (fullContent: string) => {
        isStreaming.value = false
        loading.value = false
        cancelStream.value = null
        // 所有未完成步骤标记为完成
        thinkingTrace.value.forEach(s => {
          if (s.status === 'running') s.status = 'done'
        })
        // 最终结果展示
        result.value = fullContent
      },

      onError: (err: string) => {
        isStreaming.value = false
        loading.value = false
        cancelStream.value = null
        // 当前步骤标记错误
        if (thinkingTrace.value[currentStepIdx.value]) {
          thinkingTrace.value[currentStepIdx.value].status = 'error'
        }
        toast({ message: err || '生成失败', type: 'error' })
      }
    }
  )
}

/** 从用户输入中提取一个简短名称 */
function extractName(text: string): string {
  // 尝试从 "叫做XXX"、"名为XXX"、"名字是XXX" 中提取
  const patterns = [
    /叫(?:做)?\s*([\u4e00-\u9fa5\w]{2,10})/,
    /名(?:为|字)?\s*[:：]?\s*([\u4e00-\u9fa5\w]{2,10})/,
    /称(?:为)?\s*([\u4e00-\u9fa5\w]{2,10})/,
    /(?:创建|写|设计|生成)(?:一个|个)?([\u4e00-\u9fa5\w]{2,10})/
  ]
  for (const p of patterns) {
    const m = text.match(p)
    if (m) return m[1]
  }
  return ''
}

const onStreamContent = (content: string) => {
  result.value += content
}

const onStreamDone = (fullContent: string) => {
  isStreaming.value = false
  loading.value = false
  cancelStream.value = null
  result.value = fullContent || result.value
}

const onStreamError = () => {
  isStreaming.value = false
  loading.value = false
  cancelStream.value = null
}

const resetAll = () => {
  result.value = ''
  showResult.value = false
  thinkingTrace.value = []
  if (cancelStream.value) {
    cancelStream.value()
    cancelStream.value = null
  }
  loading.value = false
  isStreaming.value = false
  currentStepIdx.value = -1
}

const toggleStep = (idx: number) => {
  const step = thinkingTrace.value[idx]
  if (step) step.expanded = !step.expanded
}

const toast = (options: { message: string; type?: 'success' | 'error' | 'warning' | 'info'; duration?: number }) => {
  ;(window as any).showToast?.(options)
}

const copyResult = async () => {
  const textToCopy = result.value
  if (!textToCopy) {
    toast({ message: '没有内容可复制', type: 'warning' })
    return
  }
  try {
    await navigator.clipboard.writeText(textToCopy)
    copySuccess.value = true
    toast({ message: '复制成功', type: 'success' })
    setTimeout(() => { copySuccess.value = false }, 1000)
  } catch {
    const textarea = document.createElement('textarea')
    textarea.value = textToCopy
    textarea.style.position = 'fixed'
    textarea.style.opacity = '0'
    document.body.appendChild(textarea)
    textarea.select()
    document.execCommand('copy')
    document.body.removeChild(textarea)
    copySuccess.value = true
    setTimeout(() => { copySuccess.value = false }, 1000)
  }
}

onMounted(() => {
  if (route.query.task) {
    userInput.value = String(route.query.task)
  }
  if (cancelStream.value) cancelStream.value()
})

onUnmounted(() => {
  if (cancelStream.value) cancelStream.value()
})
</script>

<style scoped>
/* ===== Page Layout ===== */
.generate-page {
  padding: 24px;
  min-height: calc(100vh - var(--header-height) - 48px);
}

.generate-container {
  display: grid;
  grid-template-columns: 380px 1fr;
  gap: 24px;
  max-width: 1400px;
  margin: 0 auto;
  height: calc(100vh - var(--header-height) - 96px);
}

@media (max-width: 1024px) {
  .generate-container {
    grid-template-columns: 1fr;
    height: auto;
  }
}

/* ===== Input Panel ===== */
.input-panel {
  display: flex;
  flex-direction: column;
  min-width: 0;
}

.input-wrapper {
  display: flex;
  flex-direction: column;
  gap: 16px;
  flex: 1;
}

/* ===== Input Header ===== */
.input-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.type-selector {
  display: flex;
  gap: 6px;
  padding: 5px;
  background: var(--bg-panel);
  border: 1px solid var(--border-color);
  border-radius: var(--radius-xl);
}

.type-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 10px 18px;
  border: none;
  border-radius: var(--radius-lg);
  background: transparent;
  color: var(--text-secondary);
  font-size: 0.9rem;
  font-weight: 600;
  cursor: pointer;
  transition: all var(--transition-base);
}

.type-btn:hover {
  color: var(--text-primary);
  background: var(--bg-hover);
}

.type-btn.active {
  background: linear-gradient(135deg, var(--color-primary-500), var(--color-primary-600));
  color: white;
  box-shadow: 0 4px 16px rgba(59, 130, 246, 0.3);
}

.type-btn-icon {
  display: flex;
  align-items: center;
}

/* ===== Pipeline Toggle ===== */
.pipeline-toggle {
  display: flex;
  align-items: center;
  gap: 6px;
  cursor: pointer;
  user-select: none;
  opacity: 0.8;
  transition: opacity var(--transition-fast);
  flex-shrink: 0;
}

.pipeline-toggle:hover {
  opacity: 1;
}

.pipeline-toggle .toggle-label {
  font-size: 0.72rem;
  font-weight: 700;
  color: var(--text-secondary);
  letter-spacing: 0.03em;
}

.pipeline-toggle input {
  display: none;
}

.pipeline-toggle .toggle-track {
  position: relative;
  width: 34px;
  height: 18px;
  background: var(--border-color);
  border-radius: 999px;
  transition: background var(--transition-base);
}

.pipeline-toggle .toggle-knob {
  position: absolute;
  top: 2px;
  left: 2px;
  width: 14px;
  height: 14px;
  background: white;
  border-radius: 50%;
  transition: transform var(--transition-base);
  box-shadow: 0 1px 3px rgba(0,0,0,0.2);
}

.pipeline-toggle input:checked + .toggle-track {
  background: var(--color-primary-500);
}

.pipeline-toggle input:checked + .toggle-track .toggle-knob {
  transform: translateX(16px);
}

.pipeline-toggle input:disabled + .toggle-track {
  opacity: 0.5;
}

/* ===== Big Input Area ===== */
.input-area {
  background: var(--bg-card);
  border: 1px solid var(--border-color);
  border-radius: var(--radius-2xl);
  display: flex;
  flex-direction: column;
  flex: 1;
  overflow: hidden;
  transition: border-color var(--transition-base);
}

.input-area:focus-within {
  border-color: var(--color-primary-400);
  box-shadow: 0 0 0 3px var(--glow-primary-soft);
}

.big-input {
  flex: 1;
  width: 100%;
  padding: 20px;
  border: none;
  resize: none;
  font-size: 0.95rem;
  line-height: 1.7;
  color: var(--text-primary);
  background: transparent;
  font-family: inherit;
  min-height: 180px;
}

.big-input:focus {
  outline: none;
}

.big-input::placeholder {
  color: var(--text-placeholder);
  font-size: 0.9rem;
}

.input-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 16px;
  border-top: 1px solid var(--border-color);
  background: var(--bg-panel);
}

.input-hint {
  font-size: 0.75rem;
  color: var(--text-muted);
}

.input-actions {
  display: flex;
  align-items: center;
  gap: 8px;
}

/* ===== Buttons ===== */
.btn-example {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 36px;
  height: 36px;
  border: 1px solid var(--border-color);
  border-radius: var(--radius-lg);
  background: var(--bg-card);
  color: var(--text-secondary);
  cursor: pointer;
  transition: all var(--transition-base);
}

.btn-example:hover {
  border-color: var(--color-primary-400);
  color: var(--color-primary-600);
  background: var(--glow-primary-soft);
}

.btn-reset {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 36px;
  height: 36px;
  border: 1px solid var(--border-color);
  border-radius: var(--radius-lg);
  background: transparent;
  color: var(--text-secondary);
  cursor: pointer;
  transition: all var(--transition-base);
}

.btn-reset:hover {
  background: var(--bg-hover);
  color: var(--text-primary);
}

.btn-reset:disabled {
  opacity: 0.4;
  cursor: not-allowed;
}

.btn-generate {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 24px;
  border: none;
  border-radius: var(--radius-lg);
  background: linear-gradient(135deg, var(--color-primary-600), var(--color-primary-700));
  color: white;
  font-size: 0.9rem;
  font-weight: 700;
  cursor: pointer;
  transition: all var(--transition-base);
  box-shadow: 0 4px 16px rgba(59, 130, 246, 0.3);
}

.btn-generate:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(59, 130, 246, 0.4);
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

/* ===== Result Panel ===== */
.result-panel {
  display: flex;
  flex-direction: column;
  min-width: 0;
}

.result-card {
  background: var(--bg-card);
  border: 1px solid var(--border-color);
  border-radius: var(--radius-2xl);
  display: flex;
  flex-direction: column;
  height: 100%;
  overflow: hidden;
}

.result-card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 14px 18px;
  border-bottom: 1px solid var(--border-color);
  background: var(--bg-panel);
  flex-shrink: 0;
}

.result-title {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 0.95rem;
  font-weight: 700;
  color: var(--text-primary);
}

.result-icon {
  color: var(--color-primary-500);
}

.result-actions {
  display: flex;
  gap: 8px;
}

.action-btn {
  width: 34px;
  height: 34px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 1px solid var(--border-color);
  border-radius: var(--radius-md);
  background: var(--bg-card);
  color: var(--text-secondary);
  cursor: pointer;
  transition: all var(--transition-base);
}

.action-btn:hover {
  background: var(--bg-hover);
  border-color: var(--color-primary-400);
  color: var(--color-primary-600);
}

.action-btn.success {
  background: var(--color-success);
  border-color: var(--color-success);
  color: white;
  animation: successPop 0.3s ease;
}

@keyframes successPop {
  0% { transform: scale(1); }
  50% { transform: scale(1.1); }
  100% { transform: scale(1); }
}

/* ===== Thinking Trace ===== */
.thinking-trace {
  padding: 16px;
  display: flex;
  flex-direction: column;
  gap: 12px;
  overflow-y: auto;
  flex: 1;
}

.thinking-step {
  border: 1px solid var(--border-color);
  border-radius: var(--radius-xl);
  overflow: hidden;
  transition: all var(--transition-base);
}

.thinking-step.step-draft {
  border-left: 3px solid var(--color-primary-500);
}

.thinking-step.step-audit {
  border-left: 3px solid #f59e0b;
}

.thinking-step.step-refine {
  border-left: 3px solid #8b5cf6;
}

.step-header {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 14px;
  background: var(--bg-panel);
  flex-wrap: wrap;
}

.step-icon {
  font-size: 1rem;
  flex-shrink: 0;
}

.step-title {
  font-size: 0.85rem;
  font-weight: 700;
  color: var(--text-primary);
  flex: 1;
}

.step-badge {
  font-size: 0.7rem;
  font-weight: 600;
  padding: 2px 8px;
  border-radius: var(--radius-full);
  white-space: nowrap;
}

.step-badge.running {
  background: var(--glow-primary-soft);
  color: var(--color-primary-600);
}

.step-badge.done {
  background: rgba(34, 197, 94, 0.1);
  color: var(--color-success);
}

.step-badge.error {
  background: rgba(239, 68, 68, 0.1);
  color: var(--color-error);
}

.running-dot {
  display: inline-block;
  width: 6px;
  height: 6px;
  background: var(--color-primary-500);
  border-radius: 50%;
  animation: pulse-dot 1s ease-in-out infinite;
  margin-right: 2px;
}

@keyframes pulse-dot {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.4; }
}

.step-score {
  font-size: 0.75rem;
  font-weight: 700;
  padding: 2px 8px;
  border-radius: var(--radius-full);
}

.step-score.score-excellent {
  background: rgba(34, 197, 94, 0.1);
  color: var(--color-success);
  border: 1px solid rgba(34, 197, 94, 0.2);
}

.step-score.score-good {
  background: rgba(234, 179, 8, 0.1);
  color: #b8860b;
  border: 1px solid rgba(234, 179, 8, 0.2);
}

.step-score.score-poor {
  background: rgba(239, 68, 68, 0.1);
  color: var(--color-error);
  border: 1px solid rgba(239, 68, 68, 0.2);
}

.step-content {
  padding: 12px 14px;
  border-top: 1px solid var(--border-color);
  font-size: 0.85rem;
  line-height: 1.6;
  color: var(--text-secondary);
  max-height: 400px;
  overflow-y: auto;
}

.step-content.is-streaming {
  background: rgba(59, 130, 246, 0.02);
}

.step-content :deep(.markdown-body),
.step-markdown :deep(.markdown-body) {
  font-size: 0.85rem;
  line-height: 1.7;
}

.step-content :deep(.markdown-body h1),
.step-content :deep(.markdown-body h2),
.step-content :deep(.markdown-body h3),
.step-markdown :deep(.markdown-body h1),
.step-markdown :deep(.markdown-body h2),
.step-markdown :deep(.markdown-body h3) {
  font-size: 0.95rem;
  margin: 0.5em 0 0.25em;
  color: var(--text-primary);
}

.step-content :deep(.markdown-body p),
.step-markdown :deep(.markdown-body p) {
  margin: 0 0 0.4em;
}

.step-content :deep(.markdown-body ul),
.step-content :deep(.markdown-body ol),
.step-markdown :deep(.markdown-body ul),
.step-markdown :deep(.markdown-body ol) {
  margin: 0.3em 0;
  padding-left: 1.2em;
}

.step-content :deep(.markdown-body code),
.step-markdown :deep(.markdown-body code) {
  font-size: 0.82em;
}

.step-content :deep(.markdown-body pre),
.step-markdown :deep(.markdown-body pre) {
  margin: 0.5em 0;
  border-radius: var(--radius-md);
}

.audit-summary {
  font-size: 0.85rem;
  line-height: 1.6;
  color: var(--text-secondary);
}

.step-toggle {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 14px;
  border-top: 1px solid var(--border-color);
  font-size: 0.75rem;
  font-weight: 600;
  color: var(--color-primary-600);
  cursor: pointer;
  transition: all var(--transition-fast);
  user-select: none;
}

.step-toggle:hover {
  background: var(--bg-hover);
}

/* ===== Result Divider ===== */
.result-divider {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 0 18px 8px;
}

.result-divider::before,
.result-divider::after {
  content: '';
  flex: 1;
  height: 1px;
  background: var(--border-color);
}

.divider-label {
  font-size: 0.75rem;
  font-weight: 700;
  color: var(--text-muted);
  white-space: nowrap;
}

.result-body {
  flex: 1;
  overflow-y: auto;
  padding: 18px;
  background: var(--bg-panel);
}

.result-body :deep(.markdown-body) {
  font-size: 0.9rem;
  line-height: 1.8;
  color: var(--text-secondary);
}

.result-body :deep(.markdown-body h1),
.result-body :deep(.markdown-body h2),
.result-body :deep(.markdown-body h3) {
  color: var(--text-primary);
  margin-top: 1em;
  margin-bottom: 0.5em;
  font-weight: 700;
}

.result-body :deep(.markdown-body h2) {
  color: var(--color-primary-600);
  font-size: 1em;
  border-bottom: 1px solid var(--border-color);
  padding-bottom: 6px;
}

.result-body :deep(.markdown-body p) {
  margin: 0 0 0.8em;
}

.result-body :deep(.markdown-body ul),
.result-body :deep(.markdown-body ol) {
  margin: 0.5em 0;
  padding-left: 1.3em;
}

.result-body :deep(.markdown-body li) {
  margin: 4px 0;
}

.result-body :deep(.markdown-body code) {
  padding: 0.2em 0.4em;
  background: var(--bg-hover);
  border-radius: 4px;
  font-family: var(--font-mono);
  font-size: 0.9em;
  color: var(--color-primary-600);
}

.result-body :deep(.markdown-body pre) {
  margin: 1em 0;
  border-radius: var(--radius-lg);
  overflow: hidden;
  background: #1e1e2e;
}

.result-body :deep(.markdown-body pre code) {
  display: block;
  padding: 1em;
  background: transparent;
  color: #cdd6f4;
  font-size: 0.85rem;
  line-height: 1.6;
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
  border-radius: var(--radius-2xl);
  height: 100%;
  min-height: 400px;
}

.empty-icon {
  width: 80px;
  height: 80px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, var(--color-primary-500), var(--color-primary-600));
  border-radius: var(--radius-2xl);
  color: white;
  margin-bottom: 20px;
}

.empty-title {
  font-size: 1.1rem;
  font-weight: 700;
  color: var(--text-primary);
  margin: 0 0 8px 0;
}

.empty-desc {
  font-size: 0.9rem;
  color: var(--text-secondary);
  margin: 0 0 20px;
}

.example-hints {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  justify-content: center;
  max-width: 400px;
}

.hint-chip {
  padding: 10px 16px;
  border: 1px solid var(--border-color);
  border-radius: var(--radius-xl);
  background: var(--bg-card);
  color: var(--text-secondary);
  font-size: 0.85rem;
  font-weight: 600;
  cursor: pointer;
  transition: all var(--transition-base);
  text-align: left;
}

.hint-chip:hover {
  border-color: var(--color-primary-400);
  color: var(--color-primary-600);
  background: var(--glow-primary-soft);
}

/* ===== Transitions ===== */
.result-appear-enter-active {
  animation: slideUp 0.4s cubic-bezier(0.16, 1, 0.3, 1);
}

@keyframes slideUp {
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
}

/* ===== Responsive ===== */
@media (max-width: 768px) {
  .generate-page {
    padding: 16px;
  }

  .generate-container {
    grid-template-columns: 1fr;
    height: auto;
  }

  .input-panel {
    min-height: 200px;
  }

  .input-header {
    flex-direction: column;
    align-items: stretch;
  }

  .type-selector {
    width: 100%;
  }

  .type-btn {
    flex: 1;
    justify-content: center;
  }

  .big-input {
    min-height: 120px;
  }

  .result-empty {
    min-height: 200px;
  }
}
</style>

