<template>
  <AppLayout>
    <template #main>
      <div class="page-container">
        <div class="banner">
          <h1 class="banner-title">🧪 提示词测试场</h1>
          <p class="banner-desc">实时测试你的提示词，获得AI即时反馈</p>
        </div>

        <div class="content-wrapper">
          <div class="playground-grid">
            <!-- 左侧：输入区 -->
            <div class="input-panel">
              <div class="panel-header">
                <h3>📝 提示词输入</h3>
                <div class="quick-actions">
                  <button class="action-btn" @click="loadFromHistory" title="从历史加载">
                    📜
                  </button>
                  <button class="action-btn" @click="clearAll" title="清空">
                    🗑️
                  </button>
                </div>
              </div>
              
              <div class="form-section">
                <label class="section-label">系统提示词 (System Prompt)</label>
                <textarea 
                  v-model="systemPrompt" 
                  class="prompt-input"
                  placeholder="设置AI的角色、身份和行为方式..."
                  rows="4"
                ></textarea>
              </div>
              
              <div class="form-section">
                <label class="section-label">用户提示词 (User Prompt)</label>
                <textarea 
                  v-model="userPrompt" 
                  class="prompt-input"
                  placeholder="输入你的具体请求..."
                  rows="6"
                ></textarea>
                <div class="char-count">{{ userPrompt.length }} 字符</div>
              </div>
              
              <div class="options-row">
                <div class="option-group">
                  <label class="option-label">🤖 模型选择</label>
                  <select v-model="selectedModel" class="option-select">
                    <option value="abab6.5s">MiniMax (abab6.5s)</option>
                    <option value="gpt-4">GPT-4</option>
                    <option value="gpt-3.5">GPT-3.5</option>
                    <option value="claude-3">Claude-3</option>
                  </select>
                </div>
                
                <div class="option-group">
                  <label class="option-label">🌡️ 温度参数</label>
                  <div class="temperature-control">
                    <input 
                      type="range" 
                      v-model.number="temperature" 
                      min="0" 
                      max="2" 
                      step="0.1"
                      class="temp-slider"
                    />
                    <span class="temp-value">{{ temperature }}</span>
                  </div>
                </div>
              </div>
              
              <div class="button-group">
                <button 
                  class="run-btn" 
                  :disabled="loading || !userPrompt.trim()"
                  @click="runPrompt"
                >
                  <span v-if="loading" class="loading-spinner"></span>
                  {{ loading ? '运行中...' : '🚀 运行测试' }}
                </button>
                <button 
                  class="stop-btn"
                  v-if="loading"
                  @click="stopGeneration"
                >
                  ⏹️ 停止
                </button>
              </div>
            </div>
            
            <!-- 右侧：输出区 -->
            <div class="output-panel">
              <div class="panel-header">
                <h3>💬 AI 响应</h3>
                <div class="quick-actions">
                  <button class="action-btn" @click="copyResponse" :disabled="!response" title="复制">
                    📋
                  </button>
                  <button class="action-btn" @click="regenerate" :disabled="loading || !userPrompt.trim()" title="重新生成">
                    🔄
                  </button>
                </div>
              </div>
              
              <div class="response-area" v-if="response || loading">
                <div v-if="loading && !response" class="loading-placeholder">
                  <span class="loading-spinner large"></span>
                  <p>AI 正在思考中...</p>
                </div>
                <div v-else class="response-content">
                  <MarkdownRender :content="response" />
                </div>
              </div>
              
              <div v-else class="empty-state">
                <div class="empty-icon">💭</div>
                <p>输入提示词并点击运行，查看AI响应</p>
                <div class="tips">
                  <h4>💡 使用技巧</h4>
                  <ul>
                    <li>设置清晰的系统提示词帮助AI更好地理解任务</li>
                    <li>调整温度参数控制输出的随机性</li>
                    <li>低温度(0-0.3)适合精确任务，高温度(0.7-1.0)适合创意生成</li>
                  </ul>
                </div>
              </div>
              
              <!-- 响应统计 -->
              <div v-if="response" class="response-stats">
                <span class="stat-item">📊 {{ response.length }} 字符</span>
                <span class="stat-item">⏱️ {{ responseTime }}ms</span>
                <span class="stat-item">🤖 {{ selectedModel }}</span>
              </div>
            </div>
          </div>
          
          <!-- 历史记录预览 -->
          <div class="history-section" v-if="testHistory.length > 0">
            <div class="section-header">
              <h3>📜 测试历史</h3>
              <button class="clear-history-btn" @click="clearHistory">清空历史</button>
            </div>
            <div class="history-list">
              <div 
                v-for="(item, index) in testHistory" 
                :key="index" 
                class="history-item"
                @click="loadHistoryItem(item)"
              >
                <div class="history-prompt">{{ item.userPrompt.substring(0, 50) }}...</div>
                <div class="history-meta">
                  <span>{{ item.model }}</span>
                  <span>{{ item.timestamp }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </template>
  </AppLayout>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import AppLayout from '@/components/layout/AppLayout.vue'
import MarkdownRender from '@/components/MarkdownRender.vue'
import { promptApi } from '@/api/prompt'

// 状态
const systemPrompt = ref('')
const userPrompt = ref('')
const response = ref('')
const loading = ref(false)
const selectedModel = ref('abab6.5s')
const temperature = ref(0.7)
const responseTime = ref(0)
const testHistory = ref<Array<{
  systemPrompt: string
  userPrompt: string
  response: string
  model: string
  temperature: number
  timestamp: string
}>>([])

// 流式生成控制
let abortController: AbortController | null = null

// 运行提示词
const runPrompt = async () => {
  if (!userPrompt.value.trim()) return
  
  loading.value = true
  response.value = ''
  const startTime = Date.now()
  
  try {
    abortController = new AbortController()
    
    // 使用流式API
    promptApi.generateStream(
      {
        prompt: userPrompt.value,
        systemPrompt: systemPrompt.value,
        model: selectedModel.value,
        temperature: temperature.value,
        type: 'general'
      },
      (content) => {
        response.value += content
      },
      (fullContent) => {
        responseTime.value = Date.now() - startTime
        loading.value = false
        
        // 保存到历史记录
        testHistory.value.unshift({
          systemPrompt: systemPrompt.value,
          userPrompt: userPrompt.value,
          response: fullContent,
          model: selectedModel.value,
          temperature: temperature.value,
          timestamp: new Date().toLocaleTimeString()
        })
        
        // 限制历史记录数量
        if (testHistory.value.length > 10) {
          testHistory.value.pop()
        }
      },
      (error) => {
        console.error('生成失败:', error)
        response.value = `❌ 生成失败: ${error}`
        loading.value = false
      }
    )
  } catch (error: any) {
    console.error('API调用失败:', error)
    response.value = `❌ 请求失败: ${error.message}`
    loading.value = false
  }
}

// 停止生成
const stopGeneration = () => {
  if (abortController) {
    abortController.abort()
    loading.value = false
  }
}

// 重新生成
const regenerate = () => {
  if (userPrompt.value.trim()) {
    runPrompt()
  }
}

// 复制响应
const copyResponse = async () => {
  if (response.value) {
    await navigator.clipboard.writeText(response.value)
    alert('已复制到剪贴板！')
  }
}

// 清空所有
const clearAll = () => {
  systemPrompt.value = ''
  userPrompt.value = ''
  response.value = ''
  responseTime.value = 0
}

// 从历史加载
const loadFromHistory = () => {
  // 可以添加一个模态框来选择历史记录
  if (testHistory.value.length > 0) {
    const item = testHistory.value[0]
    systemPrompt.value = item.systemPrompt
    userPrompt.value = item.userPrompt
    response.value = item.response
    selectedModel.value = item.model
    temperature.value = item.temperature
  }
}

// 加载历史记录项
const loadHistoryItem = (item: any) => {
  systemPrompt.value = item.systemPrompt
  userPrompt.value = item.userPrompt
  response.value = item.response
  selectedModel.value = item.model
  temperature.value = item.temperature
}

// 清空历史
const clearHistory = () => {
  testHistory.value = []
}
</script>

<style scoped>
.page-container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 24px;
}

.banner {
  text-align: center;
  padding: 32px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 16px;
  margin-bottom: 24px;
}

.banner-title {
  font-size: 32px;
  color: white;
  margin: 0 0 8px 0;
}

.banner-desc {
  font-size: 16px;
  color: rgba(255, 255, 255, 0.9);
  margin: 0;
}

.content-wrapper {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.playground-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 24px;
}

@media (max-width: 1024px) {
  .playground-grid {
    grid-template-columns: 1fr;
  }
}

.input-panel,
.output-panel {
  background: #1e1e1e;
  border-radius: 12px;
  padding: 20px;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.panel-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-bottom: 12px;
  border-bottom: 1px solid #333;
}

.panel-header h3 {
  margin: 0;
  font-size: 18px;
  color: #fff;
}

.quick-actions {
  display: flex;
  gap: 8px;
}

.action-btn {
  background: #333;
  border: none;
  padding: 8px 12px;
  border-radius: 8px;
  cursor: pointer;
  font-size: 16px;
  transition: background 0.2s;
}

.action-btn:hover {
  background: #444;
}

.action-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.form-section {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.section-label {
  font-size: 14px;
  color: #aaa;
  font-weight: 500;
}

.prompt-input {
  background: #2a2a2a;
  border: 1px solid #444;
  border-radius: 8px;
  padding: 12px;
  color: #fff;
  font-size: 14px;
  font-family: inherit;
  resize: vertical;
  transition: border-color 0.2s;
}

.prompt-input:focus {
  outline: none;
  border-color: #667eea;
}

.prompt-input::placeholder {
  color: #666;
}

.char-count {
  text-align: right;
  font-size: 12px;
  color: #666;
}

.options-row {
  display: flex;
  gap: 16px;
  flex-wrap: wrap;
}

.option-group {
  flex: 1;
  min-width: 200px;
}

.option-label {
  display: block;
  font-size: 14px;
  color: #aaa;
  margin-bottom: 8px;
}

.option-select {
  width: 100%;
  padding: 10px 12px;
  background: #2a2a2a;
  border: 1px solid #444;
  border-radius: 8px;
  color: #fff;
  font-size: 14px;
  cursor: pointer;
}

.option-select:focus {
  outline: none;
  border-color: #667eea;
}

.temperature-control {
  display: flex;
  align-items: center;
  gap: 12px;
}

.temp-slider {
  flex: 1;
  height: 6px;
  -webkit-appearance: none;
  background: #333;
  border-radius: 3px;
  outline: none;
}

.temp-slider::-webkit-slider-thumb {
  -webkit-appearance: none;
  width: 18px;
  height: 18px;
  background: #667eea;
  border-radius: 50%;
  cursor: pointer;
}

.temp-value {
  min-width: 40px;
  text-align: center;
  color: #667eea;
  font-weight: 600;
}

.button-group {
  display: flex;
  gap: 12px;
}

.run-btn {
  flex: 1;
  padding: 14px 24px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  border-radius: 8px;
  color: white;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: transform 0.2s, opacity 0.2s;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.run-btn:hover:not(:disabled) {
  transform: translateY(-2px);
}

.run-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.stop-btn {
  padding: 14px 24px;
  background: #dc3545;
  border: none;
  border-radius: 8px;
  color: white;
  font-size: 16px;
  cursor: pointer;
  transition: background 0.2s;
}

.stop-btn:hover {
  background: #c82333;
}

.response-area {
  flex: 1;
  min-height: 300px;
  background: #2a2a2a;
  border-radius: 8px;
  padding: 16px;
  overflow-y: auto;
}

.loading-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 200px;
  color: #666;
}

.loading-spinner {
  width: 24px;
  height: 24px;
  border: 3px solid #333;
  border-top-color: #667eea;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

.loading-spinner.large {
  width: 40px;
  height: 40px;
  border-width: 4px;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.response-content {
  color: #e0e0e0;
  font-size: 14px;
  line-height: 1.6;
  white-space: pre-wrap;
  word-break: break-word;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px;
  color: #666;
  text-align: center;
}

.empty-icon {
  font-size: 48px;
  margin-bottom: 16px;
}

.tips {
  margin-top: 24px;
  padding: 16px;
  background: #2a2a2a;
  border-radius: 8px;
  text-align: left;
  width: 100%;
}

.tips h4 {
  margin: 0 0 12px 0;
  color: #aaa;
}

.tips ul {
  margin: 0;
  padding-left: 20px;
}

.tips li {
  margin: 8px 0;
  color: #888;
  font-size: 13px;
}

.response-stats {
  display: flex;
  gap: 16px;
  padding-top: 12px;
  border-top: 1px solid #333;
  flex-wrap: wrap;
}

.stat-item {
  font-size: 12px;
  color: #666;
}

.history-section {
  background: #1e1e1e;
  border-radius: 12px;
  padding: 20px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.section-header h3 {
  margin: 0;
  color: #fff;
}

.clear-history-btn {
  background: transparent;
  border: 1px solid #444;
  color: #888;
  padding: 6px 12px;
  border-radius: 6px;
  cursor: pointer;
  font-size: 12px;
}

.clear-history-btn:hover {
  background: #333;
  color: #fff;
}

.history-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 12px;
}

.history-item {
  background: #2a2a2a;
  border-radius: 8px;
  padding: 12px;
  cursor: pointer;
  transition: background 0.2s;
}

.history-item:hover {
  background: #333;
}

.history-prompt {
  color: #e0e0e0;
  font-size: 13px;
  margin-bottom: 8px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.history-meta {
  display: flex;
  justify-content: space-between;
  font-size: 11px;
  color: #666;
}
</style>
