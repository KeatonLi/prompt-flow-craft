<template>
  <div class="convert-page">
    <div class="page-header">
      <h1>🔄 提示词格式转换器</h1>
      <p>将提示词转换为多种开发友好格式，一键集成到你的项目中</p>
    </div>

    <!-- 输入区域 -->
    <div class="input-section">
      <div class="input-header">
        <h2>输入提示词</h2>
        <div class="quick-actions">
          <button class="quick-btn" @click="loadExample">加载示例</button>
          <button class="quick-btn" @click="clearInput">清空</button>
        </div>
      </div>
      <textarea 
        v-model="promptText" 
        placeholder="请输入要转换的提示词内容..."
        rows="6"
      ></textarea>
      <div class="name-input">
        <label>提示词名称（可选）:</label>
        <input 
          v-model="promptName" 
          type="text" 
          placeholder="例如: 邮件回复助手"
        />
      </div>
    </div>

    <!-- 格式选择 -->
    <div class="format-section">
      <h2>选择目标格式</h2>
      <div class="format-grid">
        <div 
          v-for="format in formats" 
          :key="format.id"
          class="format-card"
          :class="{ active: selectedFormat === format.id }"
          @click="selectFormat(format.id)"
        >
          <div class="format-icon">{{ format.icon }}</div>
          <div class="format-info">
            <h3>{{ format.name }}</h3>
            <p>{{ format.description }}</p>
          </div>
        </div>
      </div>
    </div>

    <!-- 转换按钮 -->
    <div class="action-section">
      <button class="convert-btn" @click="convertPrompt" :disabled="loading || !promptText.trim()">
        <span v-if="loading" class="loading">转换中...</span>
        <span v-else>🚀 开始转换</span>
      </button>
    </div>

    <!-- 转换结果 -->
    <div v-if="convertedResult" class="result-section">
      <div class="result-header">
        <h2>转换结果</h2>
        <div class="result-actions">
          <button class="action-btn copy" @click="copyResult">
            <span v-if="copied">✓ 已复制</span>
            <span v-else>📋 复制代码</span>
          </button>
          <button class="action-btn download" @click="downloadResult">
            💾 下载文件
          </button>
        </div>
      </div>
      <div class="code-preview">
        <pre><code>{{ convertedResult }}</code></pre>
      </div>
    </div>

    <!-- API配置（可选） -->
    <div v-if="selectedFormat === 'curl'" class="config-section">
      <h3>API 配置（cURL格式需要）</h3>
      <div class="config-grid">
        <div class="config-item">
          <label>API 端点:</label>
          <input v-model="apiEndpoint" type="text" placeholder="https://api.example.com/v1/chat/completions" />
        </div>
        <div class="config-item">
          <label>API Key:</label>
          <input v-model="apiKey" type="password" placeholder="your-api-key" />
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'

export default {
  name: 'PromptConvert',
  setup() {
    const promptText = ref('')
    const promptName = ref('')
    const selectedFormat = ref('json')
    const loading = ref(false)
    const convertedResult = ref('')
    const copied = ref(false)
    const apiEndpoint = ref('')
    const apiKey = ref('')
    
    const formats = ref([
      { id: 'json', name: 'JSON', description: '标准JSON格式，适合API调用', icon: '{}' },
      { id: 'yaml', name: 'YAML', description: 'YAML格式，适合配置文件', icon: '📄' },
      { id: 'python', name: 'Python', description: 'Python LangChain代码', icon: '🐍' },
      { id: 'curl', name: 'cURL', description: 'cURL命令行语句', icon: '🔗' },
      { id: 'openapi', name: 'OpenAPI', description: 'OpenAPI 3.0规范', icon: '📚' }
    ])
    
    const loadExample = () => {
      promptText.value = `你是一位专业的邮件回复助手。你的任务是帮助用户撰写专业、礼貌、简洁的商务邮件。

请遵循以下原则：
1. 使用正式的语气
2. 保持简洁明了
3. 根据邮件内容给出合适的回复
4. 注意商务礼仪

请根据以下内容生成回复：`
      promptName.value = '邮件回复助手'
    }
    
    const clearInput = () => {
      promptText.value = ''
      promptName.value = ''
      convertedResult.value = ''
    }
    
    const selectFormat = (formatId) => {
      selectedFormat.value = formatId
    }
    
    const convertPrompt = async () => {
      loading.value = true
      try {
        const response = await fetch('http://111.231.107.210:8080/api/convert/format', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json'
          },
          body: JSON.stringify({
            prompt: promptText.value,
            format: selectedFormat.value,
            name: promptName.value || 'prompt',
            apiEndpoint: apiEndpoint.value || null,
            apiKey: apiKey.value || null
          })
        })
        
        const data = await response.json()
        if (data.code === 200) {
          convertedResult.value = data.data.content
        } else {
          alert('转换失败: ' + data.message)
        }
      } catch (error) {
        console.error('转换失败:', error)
        alert('转换失败，请稍后重试')
      } finally {
        loading.value = false
      }
    }
    
    const copyResult = async () => {
      try {
        await navigator.clipboard.writeText(convertedResult.value)
        copied.value = true
        setTimeout(() => {
          copied.value = false
        }, 2000)
      } catch (error) {
        console.error('复制失败:', error)
      }
    }
    
    const downloadResult = () => {
      const extensions = {
        json: 'json',
        yaml: 'yaml',
        python: 'py',
        curl: 'sh',
        openapi: 'json'
      }
      
      const extension = extensions[selectedFormat.value] || 'txt'
      const filename = `${promptName.value || 'prompt'}_${selectedFormat.value}.${extension}`
      
      const mimeTypes = {
        json: 'application/json',
        yaml: 'text/yaml',
        python: 'text/x-python',
        curl: 'text/plain',
        openapi: 'application/json'
      }
      
      const blob = new Blob([convertedResult.value], { type: mimeTypes[selectedFormat.value] })
      const url = URL.createObjectURL(blob)
      const link = document.createElement('a')
      link.href = url
      link.download = filename
      document.body.appendChild(link)
      link.click()
      document.body.removeChild(link)
      URL.revokeObjectURL(url)
    }
    
    return {
      promptText,
      promptName,
      selectedFormat,
      loading,
      convertedResult,
      copied,
      apiEndpoint,
      apiKey,
      formats,
      loadExample,
      clearInput,
      selectFormat,
      convertPrompt,
      copyResult,
      downloadResult
    }
  }
}
</script>

<style scoped>
.convert-page {
  max-width: 1200px;
  margin: 0 auto;
  padding: 24px;
}

.page-header {
  text-align: center;
  margin-bottom: 32px;
}

.page-header h1 {
  font-size: 28px;
  color: #2c3e50;
  margin-bottom: 8px;
}

.page-header p {
  color: #666;
  font-size: 14px;
}

.input-section,
.format-section,
.result-section,
.config-section {
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  margin-bottom: 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.input-header,
.result-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.input-header h2,
.format-section h2,
.result-section h2,
.config-section h3 {
  font-size: 18px;
  color: #2c3e50;
  margin: 0;
}

.quick-actions {
  display: flex;
  gap: 8px;
}

.quick-btn {
  padding: 6px 12px;
  background: #f0f4ff;
  color: #4a90e2;
  border: none;
  border-radius: 6px;
  font-size: 13px;
  cursor: pointer;
  transition: all 0.2s;
}

.quick-btn:hover {
  background: #4a90e2;
  color: #fff;
}

textarea {
  width: 100%;
  padding: 16px;
  border: 2px solid #e8e8e8;
  border-radius: 8px;
  font-size: 14px;
  font-family: inherit;
  resize: vertical;
  transition: border-color 0.2s;
  box-sizing: border-box;
}

textarea:focus {
  outline: none;
  border-color: #4a90e2;
}

.name-input {
  margin-top: 16px;
  display: flex;
  align-items: center;
  gap: 12px;
}

.name-input label {
  font-size: 14px;
  color: #666;
  white-space: nowrap;
}

.name-input input {
  flex: 1;
  max-width: 300px;
  padding: 8px 12px;
  border: 2px solid #e8e8e8;
  border-radius: 6px;
  font-size: 14px;
}

.name-input input:focus {
  outline: none;
  border-color: #4a90e2;
}

.format-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 16px;
  margin-top: 16px;
}

.format-card {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px;
  border: 2px solid #e8e8e8;
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.2s;
}

.format-card:hover {
  border-color: #4a90e2;
  background: #f8faff;
}

.format-card.active {
  border-color: #4a90e2;
  background: #e8f4ff;
}

.format-icon {
  font-size: 28px;
  width: 48px;
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f0f4ff;
  border-radius: 10px;
}

.format-info h3 {
  font-size: 16px;
  margin: 0 0 4px 0;
  color: #2c3e50;
}

.format-info p {
  font-size: 12px;
  color: #888;
  margin: 0;
}

.action-section {
  text-align: center;
  margin-bottom: 24px;
}

.convert-btn {
  padding: 14px 48px;
  background: linear-gradient(135deg, #4a90e2, #67b26f);
  color: #fff;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
}

.convert-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 4px 16px rgba(74, 144, 226, 0.4);
}

.convert-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.loading {
  display: inline-block;
  animation: pulse 1s infinite;
}

@keyframes pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.5; }
}

.result-actions {
  display: flex;
  gap: 8px;
}

.action-btn {
  padding: 8px 16px;
  border: none;
  border-radius: 6px;
  font-size: 13px;
  cursor: pointer;
  transition: all 0.2s;
}

.action-btn.copy {
  background: #f0f4ff;
  color: #4a90e2;
}

.action-btn.copy:hover {
  background: #4a90e2;
  color: #fff;
}

.action-btn.download {
  background: #e8f8f0;
  color: #67b26f;
}

.action-btn.download:hover {
  background: #67b26f;
  color: #fff;
}

.code-preview {
  background: #1e1e1e;
  border-radius: 8px;
  padding: 20px;
  overflow-x: auto;
  max-height: 400px;
  overflow-y: auto;
}

.code-preview pre {
  margin: 0;
}

.code-preview code {
  color: #d4d4d4;
  font-family: 'Fira Code', 'Monaco', 'Consolas', monospace;
  font-size: 13px;
  line-height: 1.6;
}

.config-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 16px;
  margin-top: 16px;
}

.config-item {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.config-item label {
  font-size: 14px;
  color: #666;
  font-weight: 500;
}

.config-item input {
  padding: 10px 12px;
  border: 2px solid #e8e8e8;
  border-radius: 6px;
  font-size: 14px;
}

.config-item input:focus {
  outline: none;
  border-color: #4a90e2;
}
</style>
