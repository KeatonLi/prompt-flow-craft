<template>
  <AppLayout>
    <template #main>
      <div class="skill-generator-page">
        <!-- Page Header -->
        <div class="page-header">
          <div class="header-content">
            <h1 class="page-title">
              <svg width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M12 2L2 7l10 5 10-5-10-5z"/>
                <path d="M2 17l10 5 10-5"/>
                <path d="M2 12l10 5 10-5"/>
              </svg>
              <span>Skill 生成器</span>
            </h1>
            <p class="page-subtitle">为 AI Agent 创建可调用的工具技能，支持 API、函数等多种类型</p>
          </div>
        </div>

        <!-- Main Editor Panel -->
        <div class="editor-container">
          <!-- Input Form Card -->
          <div class="form-card">
            <div class="card-header">
              <div class="header-tabs">
                <button
                  class="tab-btn"
                  :class="{ active: activeTab === 'basic' }"
                  @click="activeTab = 'basic'"
                >
                  <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"/>
                    <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"/>
                  </svg>
                  <span>基础配置</span>
                </button>
                <button
                  class="tab-btn"
                  :class="{ active: activeTab === 'parameters' }"
                  @click="activeTab = 'parameters'"
                >
                  <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <line x1="4" y1="21" x2="4" y2="14"/>
                    <line x1="4" y1="10" x2="4" y2="3"/>
                    <line x1="12" y1="21" x2="12" y2="12"/>
                    <line x1="12" y1="8" x2="12" y2="3"/>
                    <line x1="20" y1="21" x2="20" y2="16"/>
                    <line x1="20" y1="12" x2="20" y2="3"/>
                    <line x1="1" y1="14" x2="7" y2="14"/>
                    <line x1="9" y1="8" x2="15" y2="8"/>
                    <line x1="17" y1="16" x2="23" y2="16"/>
                  </svg>
                  <span>参数定义</span>
                </button>
                <button
                  class="tab-btn"
                  :class="{ active: activeTab === 'advanced' }"
                  @click="activeTab = 'advanced'"
                >
                  <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <circle cx="12" cy="12" r="3"/>
                    <path d="M19.4 15a1.65 1.65 0 0 0 .33 1.82l.06.06a2 2 0 0 1 0 2.83 2 2 0 0 1-2.83 0l-.06-.06a1.65 1.65 0 0 0-1.82-.33 1.65 1.65 0 0 0-1 1.51V21a2 2 0 0 1-2 2 2 2 0 0 1-2-2v-.09A1.65 1.65 0 0 0 9 19.4a1.65 1.65 0 0 0-1.82.33l-.06.06a2 2 0 0 1-2.83 0 2 2 0 0 1 0-2.83l.06-.06a1.65 1.65 0 0 0 .33-1.82 1.65 1.65 0 0 0-1.51-1H3a2 2 0 0 1-2-2 2 2 0 0 1 2-2h.09A1.65 1.65 0 0 0 4.6 9a1.65 1.65 0 0 0-.33-1.82l-.06-.06a2 2 0 0 1 0-2.83 2 2 0 0 1 2.83 0l.06.06a1.65 1.65 0 0 0 1.82.33H9a1.65 1.65 0 0 0 1-1.51V3a2 2 0 0 1 2-2 2 2 0 0 1 2 2v.09a1.65 1.65 0 0 0 1 1.51 1.65 1.65 0 0 0 1.82-.33l.06-.06a2 2 0 0 1 2.83 0 2 2 0 0 1 0 2.83l-.06.06a1.65 1.65 0 0 0-.33 1.82V9a1.65 1.65 0 0 0 1.51 1H21a2 2 0 0 1 2 2 2 2 0 0 1-2 2h-.09a1.65 1.65 0 0 0-1.51 1z"/>
                  </svg>
                  <span>高级配置</span>
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
              <!-- Basic Tab -->
              <div v-show="activeTab === 'basic'" class="tab-content">
                <!-- Skill Name -->
                <div class="form-group">
                  <label class="form-label">
                    <span>技能名称</span>
                    <span class="required">*</span>
                  </label>
                  <input
                    v-model="form.name"
                    type="text"
                    class="form-input"
                    placeholder="例如: get_weather, search_news, send_email"
                  />
                  <div class="form-hint">使用 snake_case 命名，如 get_weather</div>
                </div>

                <!-- Description -->
                <div class="form-group">
                  <label class="form-label">
                    <span>功能描述</span>
                    <span class="required">*</span>
                  </label>
                  <textarea
                    v-model="form.description"
                    class="form-textarea"
                    placeholder="详细描述这个技能的功能和使用场景，让 AI 能够理解何时调用它..."
                    rows="3"
                  ></textarea>
                </div>

                <!-- Skill Type -->
                <div class="form-group">
                  <label class="form-label">
                    <span>技能类型</span>
                    <span class="required">*</span>
                  </label>
                  <div class="type-selector">
                    <button
                      v-for="t in skillTypes"
                      :key="t.value"
                      class="type-btn"
                      :class="{ active: form.type === t.value }"
                      @click="form.type = t.value"
                    >
                      <span class="type-icon">{{ t.icon }}</span>
                      <span class="type-name">{{ t.name }}</span>
                    </button>
                  </div>
                </div>

                <!-- API Configuration (shown for api type) -->
                <div v-if="form.type === 'api'" class="form-section">
                  <h4 class="section-title">API 配置</h4>

                  <div class="form-grid">
                    <div class="form-group">
                      <label class="form-label">请求方法</label>
                      <select v-model="form.method" class="form-select">
                        <option value="GET">GET</option>
                        <option value="POST">POST</option>
                        <option value="PUT">PUT</option>
                        <option value="DELETE">DELETE</option>
                      </select>
                    </div>

                    <div class="form-group">
                      <label class="form-label">API 端点</label>
                      <input
                        v-model="form.endpoint"
                        type="text"
                        class="form-input"
                        placeholder="https://api.example.com/v1/..."
                      />
                    </div>
                  </div>

                  <div class="form-group">
                    <label class="form-label">认证方式</label>
                    <select v-model="form.authType" class="form-select">
                      <option value="none">无</option>
                      <option value="bearer">Bearer Token</option>
                      <option value="api_key">API Key</option>
                      <option value="basic">Basic Auth</option>
                    </select>
                  </div>

                  <div v-if="form.authType !== 'none'" class="form-group">
                    <label class="form-label">认证凭证</label>
                    <input
                      v-model="form.authCredential"
                      type="password"
                      class="form-input"
                      placeholder="输入 token 或 key"
                    />
                  </div>
                </div>

                <!-- Function Configuration (shown for function type) -->
                <div v-if="form.type === 'function'" class="form-section">
                  <h4 class="section-title">函数配置</h4>

                  <div class="form-group">
                    <label class="form-label">函数代码</label>
                    <textarea
                      v-model="form.functionCode"
                      class="form-textarea code-textarea"
                      placeholder="// 输入 JavaScript 函数代码&#10;// 函数会自动被包装为 Tool&#10;function myFunction(param) {&#10;  // ...&#10;  return result;&#10;}"
                      rows="8"
                    ></textarea>
                  </div>
                </div>
              </div>

              <!-- Parameters Tab -->
              <div v-show="activeTab === 'parameters'" class="tab-content">
                <div class="param-header">
                  <h4 class="section-title">输入参数</h4>
                  <button class="btn-add-param" @click="addParameter">
                    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                      <line x1="12" y1="5" x2="12" y2="19"/>
                      <line x1="5" y1="12" x2="19" y2="12"/>
                    </svg>
                    <span>添加参数</span>
                  </button>
                </div>

                <div v-if="form.parameters.length === 0" class="empty-params">
                  <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
                    <path d="M14.5 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V7.5L14.5 2z"/>
                    <polyline points="14 2 14 8 20 8"/>
                    <line x1="12" y1="18" x2="12" y2="12"/>
                    <line x1="9" y1="15" x2="15" y2="15"/>
                  </svg>
                  <p>暂无参数定义</p>
                  <span>点击上方按钮添加输入参数</span>
                </div>

                <div v-else class="param-list">
                  <div
                    v-for="(param, index) in form.parameters"
                    :key="index"
                    class="param-item"
                  >
                    <div class="param-main">
                      <div class="param-drag">
                        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                          <line x1="8" y1="6" x2="8" y2="6"/>
                          <line x1="16" y1="6" x2="16" y2="6"/>
                          <line x1="8" y1="12" x2="8" y2="12"/>
                          <line x1="16" y1="12" x2="16" y2="12"/>
                          <line x1="8" y1="18" x2="8" y2="18"/>
                          <line x1="16" y1="18" x2="16" y2="18"/>
                        </svg>
                      </div>

                      <div class="param-fields">
                        <input
                          v-model="param.name"
                          type="text"
                          class="form-input param-name"
                          placeholder="参数名"
                        />
                        <select v-model="param.type" class="form-select param-type">
                          <option value="string">string</option>
                          <option value="number">number</option>
                          <option value="boolean">boolean</option>
                          <option value="array">array</option>
                          <option value="object">object</option>
                        </select>
                        <input
                          v-model="param.description"
                          type="text"
                          class="form-input param-desc"
                          placeholder="参数描述"
                        />
                      </div>

                      <div class="param-required">
                        <label class="checkbox-label">
                          <input v-model="param.required" type="checkbox" />
                          <span>必填</span>
                        </label>
                      </div>

                      <button class="btn-remove-param" @click="removeParameter(index)">
                        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                          <line x1="18" y1="6" x2="6" y2="18"/>
                          <line x1="6" y1="6" x2="18" y2="18"/>
                        </svg>
                      </button>
                    </div>

                    <!-- Default Value -->
                    <div class="param-default">
                      <label class="form-label small">默认值 (可选)</label>
                      <input
                        v-model="param.defaultValue"
                        type="text"
                        class="form-input"
                        placeholder="默认值"
                      />
                    </div>
                  </div>
                </div>

                <!-- Output Schema -->
                <div class="form-group output-schema">
                  <label class="form-label">
                    <span>输出描述</span>
                    <span class="optional">(可选)</span>
                  </label>
                  <textarea
                    v-model="form.outputDescription"
                    class="form-textarea"
                    placeholder="描述函数的返回值结构，帮助 AI 理解如何处理返回结果..."
                    rows="3"
                  ></textarea>
                </div>
              </div>

              <!-- Advanced Tab -->
              <div v-show="activeTab === 'advanced'" class="tab-content">
                <!-- Headers -->
                <div class="form-section">
                  <h4 class="section-title">请求头配置</h4>
                  <div class="form-group">
                    <div class="key-value-editor">
                      <div
                        v-for="(header, index) in form.headers"
                        :key="index"
                        class="kv-row"
                      >
                        <input
                          v-model="header.key"
                          type="text"
                          class="form-input"
                          placeholder="Header Name"
                        />
                        <input
                          v-model="header.value"
                          type="text"
                          class="form-input"
                          placeholder="Header Value"
                        />
                        <button class="btn-remove" @click="removeHeader(index)">
                          <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                            <line x1="18" y1="6" x2="6" y2="18"/>
                            <line x1="6" y1="6" x2="18" y2="18"/>
                          </svg>
                        </button>
                      </div>
                    </div>
                    <button class="btn-add-row" @click="addHeader">
                      <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                        <line x1="12" y1="5" x2="12" y2="19"/>
                        <line x1="5" y1="12" x2="19" y2="12"/>
                      </svg>
                      <span>添加请求头</span>
                    </button>
                  </div>
                </div>

                <!-- Rate Limiting -->
                <div class="form-section">
                  <h4 class="section-title">限流配置</h4>
                  <div class="form-grid">
                    <div class="form-group">
                      <label class="form-label">最大调用次数 / 分钟</label>
                      <input
                        v-model.number="form.rateLimit"
                        type="number"
                        class="form-input"
                        placeholder="60"
                        min="1"
                      />
                    </div>
                    <div class="form-group">
                      <label class="form-label">超时时间 (毫秒)</label>
                      <input
                        v-model.number="form.timeout"
                        type="number"
                        class="form-input"
                        placeholder="30000"
                        min="1000"
                      />
                    </div>
                  </div>
                </div>

                <!-- Retry Policy -->
                <div class="form-section">
                  <h4 class="section-title">重试策略</h4>
                  <div class="form-grid">
                    <div class="form-group">
                      <label class="form-label">最大重试次数</label>
                      <input
                        v-model.number="form.maxRetries"
                        type="number"
                        class="form-input"
                        placeholder="3"
                        min="0"
                        max="10"
                      />
                    </div>
                    <div class="form-group">
                      <label class="form-label">重试延迟 (毫秒)</label>
                      <input
                        v-model.number="form.retryDelay"
                        type="number"
                        class="form-input"
                        placeholder="1000"
                        min="0"
                      />
                    </div>
                  </div>
                </div>

                <!-- Tags -->
                <div class="form-group">
                  <label class="form-label">
                    <span>标签</span>
                    <span class="optional">(可选)</span>
                  </label>
                  <input
                    v-model="form.tagsInput"
                    type="text"
                    class="form-input"
                    placeholder="输入标签，用逗号分隔，如: weather, tools, api"
                  />
                </div>
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
                <button class="btn-preview" @click="showPreview = true">
                  <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/>
                    <circle cx="12" cy="12" r="3"/>
                  </svg>
                  <span>预览</span>
                </button>
                <button class="btn-generate" :disabled="!canGenerate || loading" @click="generate">
                  <template v-if="loading">
                    <span class="spinner"></span>
                    <span>生成中...</span>
                  </template>
                  <template v-else>
                    <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                      <polygon points="13 2 3 14 12 14 11 22 21 10 12 10 13 2"/>
                    </svg>
                    <span>生成 Skill</span>
                  </template>
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
                </div>
                <div class="result-actions">
                  <button class="action-btn-small" @click="copyResult" title="复制结果">
                    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                      <rect x="9" y="9" width="13" height="13" rx="2" ry="2"/>
                      <path d="M5 15H4a2 2 0 0 1-2-2V4a2 2 0 0 1 2-2h9a2 2 0 0 1 2 2v1"/>
                    </svg>
                  </button>
                  <button class="action-btn-small" @click="downloadResult" title="下载">
                    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                      <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"/>
                      <polyline points="7 10 12 15 17 10"/>
                      <line x1="12" y1="15" x2="12" y2="3"/>
                    </svg>
                  </button>
                </div>
              </div>

              <div class="result-body">
                <div class="result-content">
                  <pre>{{ skillOutput }}</pre>
                </div>
              </div>
            </div>
          </Transition>
        </div>

        <!-- Preview Modal -->
        <Transition name="modal-fade">
          <div v-if="showPreview" class="modal-overlay" @click.self="showPreview = false">
            <div class="modal-content">
              <div class="modal-header">
                <h3>Skill 预览</h3>
                <button class="btn-close" @click="showPreview = false">
                  <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <line x1="18" y1="6" x2="6" y2="18"/>
                    <line x1="6" y1="6" x2="18" y2="18"/>
                  </svg>
                </button>
              </div>
              <div class="modal-body">
                <pre class="preview-code">{{ skillJsonPreview }}</pre>
              </div>
              <div class="modal-footer">
                <button class="btn-reset" @click="showPreview = false">关闭</button>
                <button class="btn-generate" @click="generateFromPreview">
                  <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <polygon points="13 2 3 14 12 14 11 22 21 10 12 10 13 2"/>
                  </svg>
                  <span>确认生成</span>
                </button>
              </div>
            </div>
          </div>
        </Transition>
      </div>
    </template>
  </AppLayout>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import AppLayout from '@/components/layout/AppLayout.vue'

const activeTab = ref('basic')
const loading = ref(false)
const showResult = ref(false)
const showPreview = ref(false)
const skillOutput = ref('')

interface Parameter {
  name: string
  type: string
  description: string
  required: boolean
  defaultValue?: string
}

interface Header {
  key: string
  value: string
}

const form = ref({
  name: '',
  description: '',
  type: 'api',
  method: 'GET',
  endpoint: '',
  authType: 'none',
  authCredential: '',
  functionCode: '',
  parameters: [] as Parameter[],
  outputDescription: '',
  headers: [] as Header[],
  rateLimit: 60,
  timeout: 30000,
  maxRetries: 3,
  retryDelay: 1000,
  tagsInput: ''
})

const skillTypes = [
  { value: 'api', name: 'API', icon: '🌐' },
  { value: 'function', name: '函数', icon: '⚡' },
  { value: 'webhook', name: 'Webhook', icon: '🪝' },
  { value: 'data', name: '数据源', icon: '📊' }
]

const canGenerate = computed(() => {
  if (!form.value.name.trim() || !form.value.description.trim()) return false
  if (form.value.type === 'api' && !form.value.endpoint.trim()) return false
  return true
})

const skillJsonPreview = computed(() => {
  return JSON.stringify(buildSkillObject(), null, 2)
})

const buildSkillObject = () => {
  const skill: any = {
    name: form.value.name,
    description: form.value.description,
    type: form.value.type,
    parameters: {
      type: 'object',
      properties: {} as any,
      required: [] as string[]
    }
  }

  form.value.parameters.forEach(p => {
    skill.parameters.properties[p.name] = {
      type: p.type,
      description: p.description
    }
    if (p.defaultValue) {
      (skill.parameters.properties[p.name] as any).default = p.defaultValue
    }
    if (p.required) {
      skill.parameters.required.push(p.name)
    }
  })

  if (form.value.outputDescription) {
    skill.output = {
      description: form.value.outputDescription
    }
  }

  if (form.value.type === 'api') {
    skill.api = {
      method: form.value.method,
      endpoint: form.value.endpoint
    }
    if (form.value.authType !== 'none') {
      skill.api.auth = {
        type: form.value.authType,
        credential: form.value.authCredential
      }
    }
  }

  if (form.value.type === 'function') {
    skill.function = {
      code: form.value.functionCode
    }
  }

  if (form.value.headers.length > 0) {
    skill.headers = {}
    form.value.headers.forEach(h => {
      if (h.key.trim()) {
        skill.headers[h.key] = h.value
      }
    })
  }

  skill.config = {
    rateLimit: form.value.rateLimit,
    timeout: form.value.timeout,
    maxRetries: form.value.maxRetries,
    retryDelay: form.value.retryDelay
  }

  if (form.value.tagsInput.trim()) {
    skill.tags = form.value.tagsInput.split(',').map(t => t.trim()).filter(t => t)
  }

  return skill
}

const addParameter = () => {
  form.value.parameters.push({
    name: '',
    type: 'string',
    description: '',
    required: false,
    defaultValue: ''
  })
}

const removeParameter = (index: number) => {
  form.value.parameters.splice(index, 1)
}

const addHeader = () => {
  form.value.headers.push({ key: '', value: '' })
}

const removeHeader = (index: number) => {
  form.value.headers.splice(index, 1)
}

const loadExample = () => {
  form.value = {
    name: 'get_weather',
    description: '获取指定城市的当前天气信息，包括温度、湿度、风速和天气状况',
    type: 'api',
    method: 'GET',
    endpoint: 'https://api.weather.example.com/v1/current',
    authType: 'api_key',
    authCredential: 'your-api-key',
    functionCode: '',
    parameters: [
      { name: 'city', type: 'string', description: '城市名称或ID', required: true, defaultValue: '' },
      { name: 'units', type: 'string', description: '温度单位: metric 或 imperial', required: false, defaultValue: 'metric' }
    ],
    outputDescription: '返回城市名称、温度、湿度、风速、天气描述等信息',
    headers: [
      { key: 'Content-Type', value: 'application/json' }
    ],
    rateLimit: 60,
    timeout: 5000,
    maxRetries: 3,
    retryDelay: 1000,
    tagsInput: 'weather, tools, api'
  }
}

const reset = () => {
  form.value = {
    name: '',
    description: '',
    type: 'api',
    method: 'GET',
    endpoint: '',
    authType: 'none',
    authCredential: '',
    functionCode: '',
    parameters: [],
    outputDescription: '',
    headers: [],
    rateLimit: 60,
    timeout: 30000,
    maxRetries: 3,
    retryDelay: 1000,
    tagsInput: ''
  }
  showResult.value = false
  skillOutput.value = ''
}

const generate = async () => {
  if (!canGenerate.value || loading.value) return

  loading.value = true
  showResult.value = true

  // Simulate generation delay
  await new Promise(resolve => setTimeout(resolve, 1000))

  skillOutput.value = JSON.stringify(buildSkillObject(), null, 2)
  loading.value = false
}

const generateFromPreview = () => {
  showPreview.value = false
  generate()
}

const copyResult = async () => {
  try {
    await navigator.clipboard.writeText(skillOutput.value)
    (window as any).showToast?.({ message: '已复制到剪贴板', type: 'success' })
  } catch (error) {
    console.error('Copy failed:', error)
    (window as any).showToast?.({ message: '复制失败', type: 'error' })
  }
}

const downloadResult = () => {
  const blob = new Blob([skillOutput.value], { type: 'application/json' })
  const url = URL.createObjectURL(blob)
  const a = document.createElement('a')
  a.href = url
  a.download = `${form.value.name || 'skill'}-definition.json`
  document.body.appendChild(a)
  a.click()
  document.body.removeChild(a)
  URL.revokeObjectURL(url)
}
</script>

<style scoped>
/* ============================================
   Skill Generator Page
   ============================================ */
.skill-generator-page {
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
}

.tab-content {
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

.form-label.small {
  font-size: var(--text-xs);
  color: var(--text-secondary);
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
  margin-top: -4px;
}

.form-input,
.form-select,
.form-textarea {
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

.form-input:hover,
.form-select:hover,
.form-textarea:hover {
  border-color: var(--border-hover);
  background: var(--bg-card);
}

.form-input:focus,
.form-select:focus,
.form-textarea:focus {
  outline: none;
  border-color: var(--color-primary-500);
  box-shadow: 0 0 0 4px var(--glow-primary-soft);
  background: var(--bg-card);
}

.form-input::placeholder,
.form-select::placeholder,
.form-textarea::placeholder {
  color: var(--text-placeholder);
}

.code-textarea {
  font-family: var(--font-mono);
  font-size: var(--text-xs);
  line-height: 1.6;
}

.form-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}

.form-section {
  padding: 16px;
  background: var(--bg-panel);
  border-radius: var(--radius-xl);
  border: 1px solid var(--border-color);
}

.section-title {
  font-family: var(--font-body);
  font-size: var(--text-sm);
  font-weight: 700;
  color: var(--text-primary);
  margin: 0 0 16px 0;
}

/* Type Selector */
.type-selector {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

.type-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 20px;
  border: 2px solid var(--border-color);
  border-radius: var(--radius-xl);
  background: var(--bg-card);
  color: var(--text-secondary);
  font-family: var(--font-body);
  font-size: var(--text-sm);
  font-weight: 600;
  cursor: pointer;
  transition: all var(--transition-base);
}

.type-btn:hover {
  border-color: var(--color-primary-400);
  color: var(--text-primary);
}

.type-btn.active {
  border-color: var(--color-primary-500);
  background: var(--glow-primary-soft);
  color: var(--color-primary-600);
}

.type-icon {
  font-size: 1.2rem;
}

/* Parameters */
.param-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
}

.param-header .section-title {
  margin: 0;
}

.btn-add-param {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 16px;
  border: 1px dashed var(--border-color);
  border-radius: var(--radius-lg);
  background: transparent;
  color: var(--text-secondary);
  font-family: var(--font-body);
  font-size: var(--text-xs);
  font-weight: 600;
  cursor: pointer;
  transition: all var(--transition-base);
}

.btn-add-param:hover {
  border-color: var(--color-primary-500);
  color: var(--color-primary-600);
  background: var(--glow-primary-soft);
}

.empty-params {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 48px 20px;
  text-align: center;
  color: var(--text-muted);
}

.empty-params svg {
  margin-bottom: 16px;
  opacity: 0.5;
}

.empty-params p {
  font-weight: 600;
  margin: 0 0 4px 0;
}

.empty-params span {
  font-size: var(--text-xs);
}

.param-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.param-item {
  padding: 16px;
  background: var(--bg-input);
  border: 1px solid var(--border-color);
  border-radius: var(--radius-xl);
}

.param-main {
  display: flex;
  align-items: center;
  gap: 12px;
}

.param-drag {
  color: var(--text-muted);
  cursor: grab;
}

.param-fields {
  display: flex;
  gap: 8px;
  flex: 1;
}

.param-name {
  width: 140px;
  flex-shrink: 0;
}

.param-type {
  width: 100px;
  flex-shrink: 0;
}

.param-desc {
  flex: 1;
}

.param-required {
  flex-shrink: 0;
}

.checkbox-label {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: var(--text-xs);
  color: var(--text-secondary);
  cursor: pointer;
}

.checkbox-label input[type="checkbox"] {
  width: 16px;
  height: 16px;
  accent-color: var(--color-primary-500);
}

.btn-remove-param {
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: none;
  border-radius: var(--radius-lg);
  background: transparent;
  color: var(--text-muted);
  cursor: pointer;
  transition: all var(--transition-base);
  flex-shrink: 0;
}

.btn-remove-param:hover {
  background: var(--color-error-light);
  color: var(--color-error);
}

.param-default {
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px dashed var(--border-color);
  display: flex;
  align-items: center;
  gap: 12px;
}

.param-default .form-label {
  flex-shrink: 0;
}

.param-default .form-input {
  flex: 1;
}

.output-schema {
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px solid var(--border-color);
}

/* Key-Value Editor */
.key-value-editor {
  display: flex;
  flex-direction: column;
  gap: 8px;
  margin-bottom: 12px;
}

.kv-row {
  display: flex;
  gap: 8px;
  align-items: center;
}

.kv-row .form-input {
  flex: 1;
}

.btn-remove {
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 1px solid var(--border-color);
  border-radius: var(--radius-lg);
  background: transparent;
  color: var(--text-muted);
  cursor: pointer;
  transition: all var(--transition-base);
  flex-shrink: 0;
}

.btn-remove:hover {
  border-color: var(--color-error);
  color: var(--color-error);
  background: var(--color-error-light);
}

.btn-add-row {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 16px;
  border: 1px dashed var(--border-color);
  border-radius: var(--radius-lg);
  background: transparent;
  color: var(--text-secondary);
  font-family: var(--font-body);
  font-size: var(--text-xs);
  font-weight: 600;
  cursor: pointer;
  transition: all var(--transition-base);
}

.btn-add-row:hover {
  border-color: var(--color-primary-500);
  color: var(--color-primary-600);
  background: var(--glow-primary-soft);
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

.btn-preview {
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

.btn-preview:hover {
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

.result-body {
  padding: 0;
}

.result-content {
  max-height: 500px;
  overflow-y: auto;
  background: linear-gradient(145deg, var(--color-gray-900), var(--color-gray-800));
  padding: 24px;
  padding-top: 48px;
  position: relative;
}

.result-content::before {
  content: '';
  position: absolute;
  top: 16px;
  left: 16px;
  width: 12px;
  height: 12px;
  background: var(--color-error);
  border-radius: 50%;
  box-shadow: 20px 0 0 var(--color-warning), 40px 0 0 var(--color-success);
}

.result-content pre {
  margin: 0;
  font-family: var(--font-mono);
  font-size: var(--text-sm);
  line-height: 1.8;
  color: var(--color-gray-100);
  white-space: pre-wrap;
  word-wrap: break-word;
}

/* ============================================
   Modal
   ============================================ */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.6);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  padding: 20px;
}

.modal-content {
  width: 100%;
  max-width: 700px;
  max-height: 80vh;
  background: var(--bg-card);
  border-radius: var(--radius-2xl);
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.modal-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20px 24px;
  border-bottom: 1px solid var(--border-color);
}

.modal-header h3 {
  font-family: var(--font-display);
  font-size: 1.25rem;
  font-weight: 700;
  margin: 0;
}

.btn-close {
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: none;
  border-radius: var(--radius-lg);
  background: transparent;
  color: var(--text-muted);
  cursor: pointer;
  transition: all var(--transition-base);
}

.btn-close:hover {
  background: var(--bg-hover);
  color: var(--text-primary);
}

.modal-body {
  padding: 24px;
  overflow-y: auto;
  flex: 1;
}

.preview-code {
  margin: 0;
  padding: 20px;
  background: var(--color-gray-900);
  border-radius: var(--radius-xl);
  font-family: var(--font-mono);
  font-size: var(--text-xs);
  line-height: 1.6;
  color: var(--color-gray-100);
  overflow-x: auto;
}

.modal-footer {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: 12px;
  padding: 20px 24px;
  border-top: 1px solid var(--border-color);
}

/* ============================================
   Animations
   ============================================ */
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

.modal-fade-enter-active,
.modal-fade-leave-active {
  transition: all 0.3s ease;
}

.modal-fade-enter-from,
.modal-fade-leave-to {
  opacity: 0;
}

.modal-fade-enter-from .modal-content,
.modal-fade-leave-to .modal-content {
  transform: scale(0.95) translateY(20px);
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

  .header-tabs {
    width: 100%;
    overflow-x: auto;
  }

  .form-grid {
    grid-template-columns: 1fr;
  }

  .param-fields {
    flex-direction: column;
  }

  .param-name,
  .param-type,
  .param-desc {
    width: 100%;
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
  .btn-preview,
  .btn-generate {
    width: 100%;
    justify-content: center;
  }
}
</style>
