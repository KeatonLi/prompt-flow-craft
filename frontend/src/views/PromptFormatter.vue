<template>
  <div class="formatter-page">
    <div class="formatter-header">
      <h1>✨ 提示词格式化</h1>
      <p>AI 驱动的智能提示词格式化和美化工具</p>
    </div>

    <!-- 输入区域 -->
    <div class="input-section">
      <div class="input-header">
        <h2>原始提示词</h2>
        <div class="quick-actions">
          <button class="quick-btn" @click="loadExample('chaos')">杂乱的提示词</button>
          <button class="quick-btn" @click="loadExample('simple')">简单示例</button>
        </div>
      </div>
      <textarea 
        v-model="promptText" 
        placeholder="请输入需要格式化的提示词内容..."
        rows="8"
      ></textarea>
      
      <!-- 格式化选项 -->
      <div class="format-options">
        <label>格式化类型：</label>
        <div class="format-types">
          <button 
            v-for="type in formatTypes" 
            :key="type.value"
            class="type-btn"
            :class="{ active: selectedType === type.value }"
            @click="selectedType = type.value"
          >
            {{ type.icon }} {{ type.label }}
          </button>
        </div>
      </div>
      
      <div class="action-bar">
        <button class="format-btn" @click="formatPrompt" :disabled="loading || !promptText.trim()">
          <span v-if="loading" class="loading">格式化中...</span>
          <span v-else>✨ 开始格式化</span>
        </button>
        <button class="clear-btn" @click="clearInput">清空</button>
      </div>
    </div>

    <!-- 格式化结果 -->
    <div v-if="formatResult" class="result-section">
      <div class="result-header">
        <h2>格式化结果</h2>
        <div class="result-actions">
          <span class="format-tag">{{ formatResult.formatType }}</span>
          <button class="copy-btn" @click="copyResult">
            📋 {{ copyText }}
          </button>
        </div>
      </div>
      
      <!-- 修改记录 -->
      <div v-if="formatResult.changes && formatResult.changes.length > 0" class="changes-section">
        <h3>📝 修改记录</h3>
        <div class="changes-list">
          <div v-for="(change, index) in formatResult.changes" :key="index" class="change-item">
            <span class="change-type">{{ change.type }}</span>
            <span class="change-desc">{{ change.description }}</span>
          </div>
        </div>
      </div>
      
      <!-- 结果展示 -->
      <div class="result-content">
        <pre>{{ formatResult.formattedPrompt }}</pre>
      </div>
      
      <div class="result-actions-bottom">
        <button class="action-btn" @click="copyResult">
          📋 复制结果
        </button>
        <button class="action-btn secondary" @click="applyAsNew">
          🔄 作为新输入
        </button>
      </div>
    </div>

    <!-- 示例展示 -->
    <div class="tips-section">
      <h3>💡 格式化类型说明</h3>
      <div class="tips-grid">
        <div class="tip-card">
          <span class="tip-icon">📝</span>
          <h4>标准格式</h4>
          <p>整理结构，规范表达，添加必要的分隔</p>
        </div>
        <div class="tip-card">
          <span class="tip-icon">📋</span>
          <h4>Markdown</h4>
          <p>使用Markdown格式美化，添加标题、列表等</p>
        </div>
        <div class="tip-card">
          <span class="tip-icon">🏗️</span>
          <h4>结构化</h4>
          <p>分成角色、任务、约束、输出等部分</p>
        </div>
        <div class="tip-card">
          <span class="tip-icon">✂️</span>
          <h4>精简版</h4>
          <p>去除冗余，保留核心信息</p>
        </div>
        <div class="tip-card">
          <span class="tip-icon">📖</span>
          <h4>详细版</h4>
          <p>添加更多细节和示例</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const promptText = ref('')
const selectedType = ref('standard')
const loading = ref(false)
const copyText = ref('复制')
const formatResult = ref(null)

const formatTypes = [
  { value: 'standard', label: '标准格式', icon: '📝' },
  { value: 'markdown', label: 'Markdown', icon: '📋' },
  { value: 'structured', label: '结构化', icon: '🏗️' },
  { value: 'concise', label: '精简版', icon: '✂️' },
  { value: 'detailed', label: '详细版', icon: '📖' }
]

const examples = {
  chaos: `帮我写一个文章内容摘要的工具我需要一个能自动提取文章主要内容的程序他应该能识别重点内容并且以简洁的方式呈现给用户最好支持不同的文章类型比如新闻报道产品介绍等等`,
  simple: `请帮我写一个Python函数来计算斐波那契数列`
}

const loadExample = (type) => {
  promptText.value = examples[type]
}

const formatPrompt = async () => {
  if (!promptText.value.trim()) {
    alert('请输入提示词内容')
    return
  }
  
  loading.value = true
  formatResult.value = null
  
  try {
    const response = await fetch('/api/prompt/format', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        prompt: promptText.value,
        formatType: selectedType.value
      })
    })
    
    const data = await response.json()
    
    if (data.code === 200) {
      formatResult.value = data.data
      alert('格式化完成！')
    } else {
      alert(data.message || '格式化失败')
    }
  } catch (error) {
    console.error('Format error:', error)
    alert('格式化请求失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

const copyResult = async () => {
  if (!formatResult.value?.formattedPrompt) return
  
  try {
    await navigator.clipboard.writeText(formatResult.value.formattedPrompt)
    copyText.value = '已复制!'
    alert('复制成功')
    setTimeout(() => {
      copyText.value = '复制'
    }, 2000)
  } catch (error) {
    alert('复制失败')
  }
}

const applyAsNew = () => {
  if (formatResult.value?.formattedPrompt) {
    promptText.value = formatResult.value.formattedPrompt
    formatResult.value = null
  }
}

const clearInput = () => {
  promptText.value = ''
  formatResult.value = null
}
</script>

<style scoped>
.formatter-page {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.formatter-header {
  text-align: center;
  margin-bottom: 30px;
}

.formatter-header h1 {
  font-size: 2rem;
  color: #2c3e50;
  margin-bottom: 10px;
}

.formatter-header p {
  color: #666;
  font-size: 1.1rem;
}

.input-section {
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.08);
  margin-bottom: 24px;
}

.input-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.input-header h2 {
  font-size: 1.3rem;
  color: #333;
}

.quick-actions {
  display: flex;
  gap: 8px;
}

.quick-btn {
  padding: 6px 12px;
  background: #f5f7fa;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-size: 0.85rem;
  cursor: pointer;
  transition: all 0.2s;
}

.quick-btn:hover {
  background: #e8f4ff;
  border-color: #409eff;
}

textarea {
  width: 100%;
  padding: 16px;
  border: 1px solid #ddd;
  border-radius: 8px;
  font-size: 1rem;
  resize: vertical;
  font-family: inherit;
  transition: border-color 0.2s;
}

textarea:focus {
  outline: none;
  border-color: #409eff;
}

.format-options {
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px solid #eee;
}

.format-options label {
  font-weight: 500;
  color: #333;
  margin-bottom: 12px;
  display: block;
}

.format-types {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 8px;
}

.type-btn {
  padding: 8px 16px;
  background: #f5f7fa;
  border: 1px solid #ddd;
  border-radius: 20px;
  font-size: 0.9rem;
  cursor: pointer;
  transition: all 0.2s;
}

.type-btn:hover {
  background: #e8f4ff;
}

.type-btn.active {
  background: #409eff;
  color: #fff;
  border-color: #409eff;
}

.action-bar {
  display: flex;
  gap: 12px;
  margin-top: 20px;
}

.format-btn {
  flex: 1;
  padding: 14px 24px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
  border: none;
  border-radius: 8px;
  font-size: 1.1rem;
  font-weight: 500;
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
}

.format-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}

.format-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.clear-btn {
  padding: 14px 24px;
  background: #fff;
  color: #666;
  border: 1px solid #ddd;
  border-radius: 8px;
  font-size: 1rem;
  cursor: pointer;
  transition: all 0.2s;
}

.clear-btn:hover {
  background: #f5f7fa;
}

.result-section {
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.08);
  margin-bottom: 24px;
}

.result-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.result-header h2 {
  font-size: 1.3rem;
  color: #333;
}

.result-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.format-tag {
  padding: 4px 12px;
  background: #e8f4ff;
  color: #409eff;
  border-radius: 12px;
  font-size: 0.85rem;
}

.copy-btn {
  padding: 6px 12px;
  background: #f5f7fa;
  border: 1px solid #ddd;
  border-radius: 6px;
  cursor: pointer;
  font-size: 0.9rem;
}

.copy-btn:hover {
  background: #e8f4ff;
}

.changes-section {
  margin-bottom: 20px;
  padding: 16px;
  background: #f9f9f9;
  border-radius: 8px;
}

.changes-section h3 {
  font-size: 1rem;
  color: #333;
  margin-bottom: 12px;
}

.changes-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.change-item {
  display: flex;
  align-items: flex-start;
  gap: 8px;
  font-size: 0.9rem;
}

.change-type {
  padding: 2px 8px;
  background: #e8f4ff;
  color: #409eff;
  border-radius: 4px;
  font-size: 0.8rem;
  white-space: nowrap;
}

.change-desc {
  color: #666;
}

.result-content {
  background: #f9f9f9;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 20px;
  max-height: 400px;
  overflow-y: auto;
}

.result-content pre {
  white-space: pre-wrap;
  word-wrap: break-word;
  font-family: 'Consolas', 'Monaco', monospace;
  font-size: 0.95rem;
  line-height: 1.6;
  color: #333;
}

.result-actions-bottom {
  display: flex;
  gap: 12px;
}

.action-btn {
  flex: 1;
  padding: 12px 20px;
  background: #409eff;
  color: #fff;
  border: none;
  border-radius: 8px;
  font-size: 1rem;
  cursor: pointer;
  transition: all 0.2s;
}

.action-btn:hover {
  background: #66b1ff;
}

.action-btn.secondary {
  background: #fff;
  color: #409eff;
  border: 1px solid #409eff;
}

.action-btn.secondary:hover {
  background: #e8f4ff;
}

.tips-section {
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.08);
}

.tips-section h3 {
  font-size: 1.2rem;
  color: #333;
  margin-bottom: 20px;
  text-align: center;
}

.tips-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 16px;
}

.tip-card {
  padding: 16px;
  background: #f9f9f9;
  border-radius: 8px;
  text-align: center;
  transition: transform 0.2s;
}

.tip-card:hover {
  transform: translateY(-4px);
}

.tip-icon {
  font-size: 2rem;
  display: block;
  margin-bottom: 8px;
}

.tip-card h4 {
  font-size: 1rem;
  color: #333;
  margin-bottom: 8px;
}

.tip-card p {
  font-size: 0.85rem;
  color: #666;
  line-height: 1.4;
}

.loading {
  display: inline-block;
  animation: pulse 1.5s infinite;
}

@keyframes pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.5; }
}
</style>
