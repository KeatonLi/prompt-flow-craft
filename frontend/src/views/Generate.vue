<template>
  <AppLayout>
    <template #main>
      <div class="generate-page">
        <div class="generate-container">

          <!-- Left Panel: Configuration -->
          <div class="config-panel">
            <div class="config-wrapper">
              <!-- Type Selector -->
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

              <!-- Agent Form -->
              <Transition name="slide-fade" mode="out-in">
                <div v-if="currentType === 'agent'" key="agent" class="form-card">
                  <div class="form-card-header">
                    <h2 class="form-card-title">
                      <span class="title-icon">🤖</span>
                      创建 Agent
                    </h2>
                    <button class="btn-example" @click="loadAgentExample">
                      <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                        <path d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.747 0 3.332.477 4.5 1.253v13C19.832 18.477 18.247 18 16.5 18c-1.746 0-3.332.477-4.5 1.253"/>
                      </svg>
                      示例
                    </button>
                  </div>

                  <div class="form-card-body">
                    <div class="form-group">
                      <label class="form-label">
                        Agent 名称 <span class="required">*</span>
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

                    <div class="form-group">
                      <label class="form-label">
                        角色定位 <span class="required">*</span>
                      </label>
                      <textarea
                        v-model="agentForm.role"
                        class="form-textarea"
                        :class="{ 'error': agentForm.role && !hasChinese(agentForm.role) }"
                        placeholder="描述 Agent 的核心身份和专业领域..."
                        rows="4"
                      ></textarea>
                    </div>

                    <div class="form-group">
                      <label class="form-label">核心能力</label>
                      <textarea
                        v-model="agentForm.capabilities"
                        class="form-textarea"
                        placeholder="- 撰写技术设计文档&#10;- 解释系统架构和设计模式..."
                        rows="3"
                      ></textarea>
                    </div>

                    <div class="form-group">
                      <label class="form-label">行为准则</label>
                      <textarea
                        v-model="agentForm.behaviors"
                        class="form-textarea"
                        placeholder="- 语言简洁专业...&#10;- 主动提供实际应用案例..."
                        rows="2"
                      ></textarea>
                    </div>

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
                          <span class="chip-icon" v-html="style.icon"></span>
                          <span class="chip-text">{{ style.name }}</span>
                        </button>
                      </div>
                    </div>
                  </div>

                  <div class="form-card-footer">
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
                        生成提示词
                      </template>
                    </button>
                  </div>
                </div>

                <!-- Skill Form -->
                <div v-else key="skill" class="form-card">
                  <div class="form-card-header">
                    <h2 class="form-card-title">
                      <span class="title-icon">⚡</span>
                      创建 Skill
                    </h2>
                    <button class="btn-example" @click="loadSkillExample">
                      <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                        <path d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.747 0 3.332.477 4.5 1.253v13C19.832 18.477 18.247 18 16.5 18c-1.746 0-3.332.477-4.5 1.253"/>
                      </svg>
                      示例
                    </button>
                  </div>

                  <div class="form-card-body">
                    <div class="form-row two-col">
                      <div class="form-group">
                        <label class="form-label">
                          Skill 名称 <span class="required">*</span>
                        </label>
                        <input
                          v-model="skillForm.name"
                          type="text"
                          class="form-input"
                          :class="{ 'error': skillForm.name && !hasChinese(skillForm.name) }"
                          placeholder="例如：天气查询"
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

                    <div class="form-group">
                      <label class="form-label">
                        功能描述 <span class="required">*</span>
                      </label>
                      <textarea
                        v-model="skillForm.description"
                        class="form-textarea"
                        :class="{ 'error': skillForm.description && !hasChinese(skillForm.description) }"
                        placeholder="清晰描述这个工具能做什么..."
                        rows="3"
                      ></textarea>
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

                    <div v-if="skillForm.type === 'function'" class="form-group">
                      <label class="form-label">函数代码</label>
                      <textarea
                        v-model="skillForm.functionCode"
                        class="form-textarea code"
                        placeholder="// 输入函数实现..."
                        rows="4"
                      ></textarea>
                    </div>

                    <div class="form-row two-col">
                      <div class="form-group">
                        <label class="form-label">输入参数 <span class="optional">(可选)</span></label>
                        <textarea
                          v-model="skillForm.parameters"
                          class="form-textarea"
                          placeholder="city: 城市名..."
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
                        生成提示词
                      </template>
                    </button>
                  </div>
                </div>
              </Transition>
            </div>
          </div>

          <!-- Right Panel: Result -->
          <div class="result-panel">
            <Transition name="result-appear">
              <div v-if="showResult" class="result-card">
                <div class="result-card-header">
                  <div class="result-title">
                    <svg class="result-icon" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                      <path d="M14.5 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V7.5L14.5 2z"/>
                      <polyline points="14 2 14 8 20 8"/>
                    </svg>
                    <span>生成的提示词</span>
                    <span v-if="isStreaming" class="streaming-badge">
                      <span class="stream-dot"></span>
                      生成中
                    </span>
                  </div>
                  <div class="result-actions">
                    <button class="action-btn" @click="copyResult" title="复制">
                      <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                        <rect x="9" y="9" width="13" height="13" rx="2" ry="2"/>
                        <path d="M5 15H4a2 2 0 0 1-2-2V4a2 2 0 0 1 2-2h9a2 2 0 0 1 2 2v1"/>
                      </svg>
                    </button>
                    <button class="action-btn" @click="savePrompt" title="保存" :disabled="isStreaming || !hasResult">
                      <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                        <path d="M19 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h11l5 5v11a2 2 0 0 1-2 2z"/>
                        <polyline points="17 21 17 13 7 13 7 21"/>
                        <polyline points="7 3 7 8 15 8"/>
                      </svg>
                    </button>
                    <div class="export-wrapper">
                      <button class="action-btn" @click="toggleExportMenu" title="导出">
                        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                          <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"/>
                          <polyline points="7 10 12 15 17 10"/>
                          <line x1="12" y1="15" x2="12" y2="3"/>
                        </svg>
                      </button>
                      <Transition name="dropdown">
                        <div v-if="showExportMenu" class="export-menu">
                          <button @click="exportPrompt('markdown')">
                            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                              <path d="M14.5 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V7.5L14.5 2z"/>
                            </svg>
                            Markdown
                          </button>
                          <button @click="exportPrompt('json')">
                            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                              <polyline points="16 18 22 12 16 6"/>
                              <polyline points="8 6 2 12 8 18"/>
                            </svg>
                            JSON
                          </button>
                          <button @click="exportPrompt('txt')">
                            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                              <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/>
                            </svg>
                            纯文本
                          </button>
                        </div>
                      </Transition>
                    </div>
                  </div>
                </div>
                <div class="result-card-body">
                  <div class="result-content">
                    <div class="markdown-body" v-html="displayedResultWithCursor"></div>
                  </div>
                </div>
              </div>
            </Transition>

            <!-- Empty State -->
            <div v-if="!showResult" class="result-empty">
              <div class="empty-icon">
                <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
                  <path d="M9.663 17h4.673M12 3v1m6.364 1.636l-.707.707M21 12h-1M4 12H3m3.343-5.657l-.707-.707m2.828 9.9a5 5 0 117.072 0l-.548.547A3.374 3.374 0 0014 18.469V19a2 2 0 11-4 0v-.531c0-.895-.356-1.754-.988-2.386l-.548-.547z"/>
                </svg>
              </div>
              <h3 class="empty-title">开始创建提示词</h3>
              <p class="empty-desc">填写左侧配置信息，点击生成按钮</p>
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
const showExportMenu = ref(false)

const currentType = ref<'agent' | 'skill'>('agent')

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

const skillTypes = [
  { value: 'api', name: 'API', icon: '🌐' },
  { value: 'function', name: '函数', icon: '⚡' },
  { value: 'webhook', name: 'Webhook', icon: '🪝' },
  { value: 'data', name: '数据源', icon: '📊' }
]

const communicationStyles = [
  { value: 'professional', name: '专业', icon: '<svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="2" y="7" width="20" height="14" rx="2"/></svg>' },
  { value: 'friendly', name: '友好', icon: '<svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"/><path d="M8 14s1.5 2 4 2 4-2 4-2"/><line x1="9" y1="9" x2="9.01" y2="9"/></svg>' },
  { value: 'concise', name: '简洁', icon: '<svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="5" y1="12" x2="19" y2="12"/></svg>' },
  { value: 'detailed', name: '详细', icon: '<svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="8" y1="6" x2="21" y2="6"/><line x1="8" y1="12" x2="21" y2="12"/><line x1="8" y1="18" x2="21" y2="18"/></svg>' },
  { value: 'casual', name: '轻松', icon: '<svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9"/></svg>' }
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
const cancelStream = ref<(() => void) | null>(null)

// 流式渲染优化：直接存储纯文本，渲染只在 done 时进行一次
let pendingRender = false
let renderTimer: ReturnType<typeof setTimeout> | null = null

// 是否有生成结果（用于保存按钮状态）
const hasResult = computed(() => {
  // 检查 result.value 是否有内容（去掉 HTML 标签后）
  const text = result.value.replace(/<[^>]*>/g, '').trim()
  return text.length > 0
})

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

// 流式时：实时渲染 markdown
// 结束时：渲染 markdown
const displayedResultWithCursor = computed(() => {
  const content = result.value || ''
  if (isStreaming.value) {
    // 流式时：实时渲染 markdown
    const rendered = md.render(content)
    return rendered + '<span class="typing-cursor"></span>'
  }
  // 结束时：渲染 markdown
  return md.render(content)
})

const switchType = (type: 'agent' | 'skill') => {
  currentType.value = type
  reset()
}

const loadAgentExample = () => {
  agentForm.value = {
    name: '技术文档助手',
    role: '你是一位资深技术文档专家，擅长将复杂的编程概念和技术架构用通俗易懂的语言解释清楚。',
    capabilities: '- 撰写技术设计文档和API接口文档\n- 解释系统架构、设计模式和微服务设计\n- 提供代码示例和最佳实践建议',
    behaviors: '- 语言简洁专业，避免冗余\n- 主动提供实际应用案例',
    communicationStyle: 'professional'
  }
}

const loadSkillExample = () => {
  skillForm.value = {
    name: '天气查询助手',
    description: '根据用户提供的城市名称或位置信息，获取该城市的实时天气信息。',
    type: 'api',
    method: 'GET',
    endpoint: 'https://api.weather.example.com/v1/current',
    functionCode: '',
    parameters: 'city: 城市名，必填\nunits: 温度单位，默认 metric',
    outputDescription: '返回 JSON 格式的天气数据'
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
}

const onStreamContent = (content: string) => {
  // 直接追加纯文本，不做 markdown 渲染
  result.value += content
}

const onStreamDone = () => {
  isStreaming.value = false
  loading.value = false
  cancelStream.value = null
  // 注意：result.value 此时已经是实时渲染的 HTML（由 displayedResultWithCursor 处理）
  // 不需要再次调用 md.render()
}

const onStreamError = () => {
  isStreaming.value = false
  loading.value = false
  cancelStream.value = null
}

const reset = () => {
  result.value = ''
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

const toast = (options: { message: string; type?: 'success' | 'error' | 'warning' | 'info'; duration?: number }) => {
  ;(window as any).showToast?.(options)
}

const copyResult = async () => {
  try {
    // result.value 是纯文本 markdown 内容
    const textToCopy = result.value
    if (!textToCopy) {
      toast({ message: '没有内容可复制', type: 'warning' })
      return
    }
    // 使用 textarea 方法，兼容性更好
    const textarea = document.createElement('textarea')
    textarea.value = textToCopy
    textarea.style.position = 'fixed'
    textarea.style.opacity = '0'
    document.body.appendChild(textarea)
    textarea.select()
    const success = document.execCommand('copy')
    document.body.removeChild(textarea)
    if (success) {
      toast({ message: '已复制到剪贴板', type: 'success' })
    } else {
      // 降级方案：尝试 navigator.clipboard
      try {
        await navigator.clipboard.writeText(textToCopy)
        toast({ message: '已复制到剪贴板', type: 'success' })
      } catch {
        toast({ message: '复制失败，请手动复制', type: 'error' })
      }
    }
  } catch (error) {
    console.error('Copy failed:', error)
    toast({ message: '复制失败，请重试', type: 'error' })
  }
}

const savePrompt = async () => {
  try {
    // result.value 是纯文本 markdown 内容
    const generatedPrompt = result.value.trim()
    if (!generatedPrompt) {
      toast({ message: '没有内容可保存', type: 'warning' })
      return
    }

    if (currentType.value === 'agent') {
      await promptApi.saveAgent({
        name: agentForm.value.name,
        roleDescription: agentForm.value.role,
        capabilities: agentForm.value.capabilities,
        behaviors: agentForm.value.behaviors,
        communicationStyle: agentForm.value.communicationStyle,
        generatedPrompt: generatedPrompt
      })
      toast({ message: '已保存到历史记录', type: 'success' })
    } else {
      // 将 parameters 转为 JSON 字符串
      let parametersJson = skillForm.value.parameters
      if (parametersJson && parametersJson.trim()) {
        // 如果是普通文本格式，尝试转为 JSON
        try {
          // 检查是否已经是 JSON
          JSON.parse(parametersJson)
        } catch {
          // 不是 JSON，转换为 JSON 格式
          const lines = parametersJson.split('\n').filter(l => l.trim())
          const obj: Record<string, string> = {}
          lines.forEach(line => {
            const [key, ...valueParts] = line.split(':')
            if (key && valueParts.length) {
              obj[key.trim()] = valueParts.join(':').trim()
            }
          })
          parametersJson = JSON.stringify(obj, null, 2)
        }
      }

      await promptApi.saveSkill({
        name: skillForm.value.name,
        description: skillForm.value.description,
        skillType: skillForm.value.type,
        method: skillForm.value.method,
        endpoint: skillForm.value.endpoint,
        parameters: parametersJson,
        outputDescription: skillForm.value.outputDescription,
        generatedPrompt: generatedPrompt
      })
      toast({ message: '已保存到历史记录', type: 'success' })
    }
  } catch (error) {
    console.error('Save failed:', error)
    toast({ message: '保存失败: ' + (error as Error).message, type: 'error' })
  }
}

const toggleExportMenu = () => { showExportMenu.value = !showExportMenu.value }

const exportPrompt = (format: 'markdown' | 'json' | 'txt') => {
  showExportMenu.value = false
  // 使用表单名称作为文件名
  const name = currentType.value === 'agent' ? agentForm.value.name : skillForm.value.name
  const safeName = name ? name.replace(/[^a-zA-Z0-9一-龥]/g, '_').substring(0, 20) : 'prompt'
  const timestamp = new Date().toISOString().slice(0, 10)
  let content = ''
  let filename = `${safeName}_${timestamp}`
  let mimeType = 'text/plain'

  if (format === 'markdown') {
    content = `# ${name || 'AI 提示词'}\n\n${result.value}\n\n---\n*由 Prompt Flow Craft 生成*\n`
    filename += '.md'
    mimeType = 'text/markdown'
  } else if (format === 'json') {
    content = JSON.stringify({ name: name, prompt: result.value, type: currentType.value, generatedAt: new Date().toISOString() }, null, 2)
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
  padding: 24px;
  min-height: calc(100vh - var(--header-height) - 48px);
}

.generate-container {
  display: grid;
  grid-template-columns: 1fr 1fr;
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

/* ===== Config Panel ===== */
.config-panel {
  display: flex;
  flex-direction: column;
  min-width: 0;
}

.config-wrapper {
  display: flex;
  flex-direction: column;
  gap: 16px;
  flex: 1;
  overflow-y: auto;
}

/* ===== Type Selector ===== */
.type-selector {
  display: flex;
  gap: 8px;
  padding: 6px;
  background: var(--bg-panel);
  border: 1px solid var(--border-color);
  border-radius: var(--radius-xl);
  width: fit-content;
}

.type-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 24px;
  border: none;
  border-radius: var(--radius-lg);
  background: transparent;
  color: var(--text-secondary);
  font-size: 0.95rem;
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

/* ===== Form Card ===== */
.form-card {
  background: var(--bg-card);
  border: 1px solid var(--border-color);
  border-radius: var(--radius-2xl);
  display: flex;
  flex-direction: column;
  flex: 1;
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

.form-card-title {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 1rem;
  font-weight: 700;
  color: var(--text-primary);
  margin: 0;
}

.title-icon {
  font-size: 1.25rem;
}

.btn-example {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 14px;
  border: 1px solid var(--border-color);
  border-radius: var(--radius-lg);
  background: var(--bg-card);
  color: var(--text-secondary);
  font-size: 0.85rem;
  font-weight: 600;
  cursor: pointer;
  transition: all var(--transition-base);
}

.btn-example:hover {
  border-color: var(--color-primary-400);
  color: var(--color-primary-600);
  background: var(--glow-primary-soft);
}

.form-card-body {
  padding: 20px;
  display: flex;
  flex-direction: column;
  gap: 16px;
  flex: 1;
  overflow-y: auto;
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

@media (max-width: 640px) {
  .form-row.two-col {
    flex-direction: column;
  }
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
  font-size: 0.85rem;
  font-weight: 600;
  color: var(--text-primary);
}

.required {
  color: var(--color-error);
  font-size: 0.7rem;
}

.optional {
  color: var(--text-muted);
  font-weight: 400;
  font-size: 0.75rem;
}

.form-error {
  font-size: 0.75rem;
  color: var(--color-error);
  font-weight: 500;
}

.form-input,
.form-select,
.form-textarea {
  width: 100%;
  padding: 12px 14px;
  font-size: 0.9rem;
  color: var(--text-primary);
  background: var(--bg-input);
  border: 1px solid var(--border-color);
  border-radius: var(--radius-lg);
  transition: all var(--transition-base);
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
}

.form-textarea {
  resize: vertical;
  min-height: 72px;
  line-height: 1.6;
}

.form-textarea.code {
  font-family: var(--font-mono);
  font-size: 0.85rem;
}

.form-select {
  cursor: pointer;
}

/* ===== Chips ===== */
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
  border-radius: var(--radius-lg);
  background: var(--bg-card);
  color: var(--text-secondary);
  font-size: 0.85rem;
  font-weight: 600;
  cursor: pointer;
  transition: all var(--transition-base);
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

/* ===== Buttons ===== */
.btn-reset {
  padding: 12px 20px;
  border: 1px solid var(--border-color);
  border-radius: var(--radius-lg);
  background: transparent;
  color: var(--text-secondary);
  font-size: 0.9rem;
  font-weight: 600;
  cursor: pointer;
  transition: all var(--transition-base);
}

.btn-reset:hover {
  background: var(--bg-hover);
  color: var(--text-primary);
}

.btn-generate {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 28px;
  border: none;
  border-radius: var(--radius-lg);
  background: linear-gradient(135deg, var(--color-primary-600), var(--color-primary-700));
  color: white;
  font-size: 0.95rem;
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

.streaming-badge {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 4px 10px;
  background: var(--glow-primary-soft);
  color: var(--color-primary-600);
  font-size: 0.75rem;
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
  0%, 100% { opacity: 1; }
  50% { opacity: 0.4; }
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
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-dropdown);
  min-width: 130px;
  z-index: 10;
}

.export-menu button {
  display: flex;
  align-items: center;
  gap: 10px;
  width: 100%;
  padding: 10px 12px;
  border: none;
  border-radius: var(--radius-md);
  background: transparent;
  color: var(--text-primary);
  font-size: 0.85rem;
  cursor: pointer;
  transition: all var(--transition-fast);
}

.export-menu button:hover {
  background: var(--glow-primary-soft);
  color: var(--color-primary-600);
}

.result-card-body {
  flex: 1;
  overflow-y: auto;
}

.result-content {
  padding: 20px;
  background: var(--bg-panel);
}

.result-content :deep(.markdown-body) {
  font-size: 0.9rem;
  line-height: 1.8;
  color: var(--text-secondary);
}

.result-content :deep(.markdown-body h1),
.result-content :deep(.markdown-body h2),
.result-content :deep(.markdown-body h3) {
  color: var(--text-primary);
  margin-top: 1em;
  margin-bottom: 0.5em;
  font-weight: 700;
}

.result-content :deep(.markdown-body h2) {
  color: var(--color-primary-600);
  font-size: 1em;
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
  border-radius: var(--radius-lg);
  overflow: hidden;
  background: #1e1e2e;
}

.result-content :deep(.markdown-body pre code) {
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
  margin: 0;
}

/* ===== Transitions ===== */
.slide-fade-enter-active,
.slide-fade-leave-active {
  transition: all 0.3s ease;
}

.slide-fade-enter-from,
.slide-fade-leave-to {
  opacity: 0;
  transform: translateX(20px);
}

.result-appear-enter-active {
  animation: slideUp 0.4s cubic-bezier(0.16, 1, 0.3, 1);
}

@keyframes slideUp {
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
}

.dropdown-enter-active,
.dropdown-leave-active {
  transition: all 0.2s ease;
}

.dropdown-enter-from,
.dropdown-leave-to {
  opacity: 0;
  transform: translateY(-8px);
}

/* ===== Responsive ===== */
@media (max-width: 768px) {
  .generate-page {
    padding: 16px;
  }

  .type-selector {
    width: 100%;
  }

  .type-btn {
    flex: 1;
    justify-content: center;
  }

  .form-card-footer {
    flex-direction: column;
  }

  .btn-reset, .btn-generate {
    width: 100%;
    justify-content: center;
  }

  .result-empty {
    min-height: 300px;
  }
}
</style>
