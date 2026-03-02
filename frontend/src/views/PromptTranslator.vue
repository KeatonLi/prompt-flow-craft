<template>
  <AppLayout>
    <template #main>
      <div class="page-container">
        <div class="banner">
          <h1 class="banner-title">🌐 提示词翻译器</h1>
          <p class="banner-desc">支持中英文及其他语言提示词互译，保留专业术语和变量格式</p>
        </div>

        <div class="content-wrapper">
          <div class="translate-grid">
            <!-- 左侧：输入区 -->
            <div class="input-panel">
              <div class="panel-header">
                <h3>📝 原始提示词</h3>
                <div class="quick-actions">
                  <button class="action-btn" @click="loadExample" title="加载示例">
                    📋
                  </button>
                  <button class="action-btn" @click="clearInput" title="清空">
                    🗑️
                  </button>
                </div>
              </div>
              
              <div class="lang-selector">
                <div class="lang-group">
                  <label class="lang-label">源语言</label>
                  <select v-model="sourceLang" class="lang-select">
                    <option value="auto">自动检测</option>
                    <option value="zh">中文</option>
                    <option value="en">English</option>
                    <option value="ja">日本語</option>
                    <option value="ko">한국어</option>
                  </select>
                </div>
                
                <div class="lang-arrow">→</div>
                
                <div class="lang-group">
                  <label class="lang-label">目标语言</label>
                  <select v-model="targetLang" class="lang-select">
                    <option value="en">English</option>
                    <option value="zh">中文</option>
                    <option value="ja">日本語</option>
                    <option value="ko">한국어</option>
                    <option value="es">Español</option>
                    <option value="fr">Français</option>
                    <option value="de">Deutsch</option>
                  </select>
                </div>
              </div>
              
              <textarea 
                v-model="originalPrompt" 
                class="prompt-input"
                placeholder="请输入需要翻译的提示词...

支持的变量格式会被保留：
- {variable}
- {{variable}}
- [variable]
- &lt;variable&gt;"
                rows="12"
              ></textarea>
              
              <div class="char-info">
                <span class="char-count">{{ originalPrompt.length }} 字符</span>
                <span v-if="detectedLang" class="detected-lang">
                  检测到语言: {{ getLangName(detectedLang) }}
                </span>
              </div>
              
              <div class="button-group">
                <button 
                  class="translate-btn" 
                  :disabled="loading || !originalPrompt.trim()"
                  @click="translatePrompt"
                >
                  <span v-if="loading" class="loading-spinner"></span>
                  {{ loading ? '翻译中...' : '🌍 开始翻译' }}
                </button>
              </div>
            </div>
            
            <!-- 右侧：输出区 -->
            <div class="output-panel">
              <div class="panel-header">
                <h3>✨ 翻译结果</h3>
                <div class="quick-actions">
                  <button class="action-btn" @click="copyResult" :disabled="!translatedPrompt" title="复制">
                    📋
                  </button>
                  <button class="action-btn" @click="swapLanguages" title="交换语言">
                    🔄
                  </button>
                </div>
              </div>
              
              <div v-if="!translatedPrompt && !loading" class="empty-result">
                <div class="empty-icon">🌐</div>
                <p>翻译结果将显示在这里</p>
              </div>
              
              <div v-else-if="loading" class="loading-result">
                <div class="loading-animation">
                  <div class="loading-dot"></div>
                  <div class="loading-dot"></div>
                  <div class="loading-dot"></div>
                </div>
                <p>正在翻译中...</p>
              </div>
              
              <textarea 
                v-else
                v-model="translatedPrompt" 
                class="result-output"
                rows="12"
                readonly
              ></textarea>
              
              <div v-if="translatedPrompt" class="result-actions">
                <button class="copy-btn" @click="copyResult">
                  📋 复制翻译结果
                </button>
                <button class="edit-btn" @click="editResult">
                  ✏️ 编辑结果
                </button>
              </div>
            </div>
          </div>
          
          <!-- 翻译说明 -->
          <div class="tips-section">
            <h4>💡 翻译说明</h4>
            <ul>
              <li>自动保留提示词中的变量格式，如 <code>{variable}</code>、<code>{{variable}}</code> 等</li>
              <li>专业术语和AI提示词特定格式会被保留原样</li>
              <li>命令语气和格式说明会保持原有的风格</li>
              <li>支持中、英、日、韩等多语言互译</li>
            </ul>
          </div>
        </div>
      </div>
    </template>
  </AppLayout>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { translateApi } from '@/api/translate'
import AppLayout from '@/components/layout/AppLayout.vue'

const originalPrompt = ref('')
const translatedPrompt = ref('')
const sourceLang = ref('auto')
const targetLang = ref('en')
const detectedLang = ref('')
const loading = ref(false)

// 语言代码映射
const langNames: Record<string, string> = {
  'zh': '中文',
  'en': 'English',
  'ja': '日本語',
  'ko': '한국어',
  'es': 'Español',
  'fr': 'Français',
  'de': 'Deutsch',
  'auto': '自动检测',
  'mixed': '混合'
}

const getLangName = (code: string) => {
  return langNames[code] || code
}

// 加载示例
const loadExample = () => {
  originalPrompt.value = `你是一位专业的文章编辑。

请帮我修改以下文章，使其更加通顺流畅：

{{文章内容}}

要求：
1. 保持原文的核心意思不变
2. 修正语法错误和错别字
3. 优化句子的表达
4. 输出格式为JSON：{"original": "原文", "improved": "修改后的文章", "changes": ["修改点1", "修改点2"]}`
  sourceLang.value = 'auto'
  targetLang.value = 'en'
}

// 清空输入
const clearInput = () => {
  originalPrompt.value = ''
  translatedPrompt.value = ''
  detectedLang.value = ''
}

// 翻译提示词
const translatePrompt = async () => {
  if (!originalPrompt.value.trim()) {
    ElMessage.warning('请输入需要翻译的提示词')
    return
  }
  
  loading.value = true
  
  try {
    const response = await translateApi.translate(
      originalPrompt.value,
      targetLang.value,
      sourceLang.value
    )
    
    if (response.code === 200 && response.data) {
      translatedPrompt.value = response.data.translated
      detectedLang.value = response.data.sourceLang
      ElMessage.success('翻译成功！')
    } else {
      ElMessage.error(response.message || '翻译失败')
    }
  } catch (error: any) {
    console.error('Translation error:', error)
    ElMessage.error(error.message || '翻译请求失败')
  } finally {
    loading.value = false
  }
}

// 复制结果
const copyResult = async () => {
  if (!translatedPrompt.value) return
  
  try {
    await navigator.clipboard.writeText(translatedPrompt.value)
    ElMessage.success('已复制到剪贴板')
  } catch {
    ElMessage.error('复制失败')
  }
}

// 交换语言
const swapLanguages = () => {
  if (!translatedPrompt.value) {
    // 如果没有翻译结果，只交换语言
    const temp = sourceLang.value
    sourceLang.value = targetLang.value
    targetLang.value = temp
    return
  }
  
  // 如果有翻译结果，交换源和目标
  originalPrompt.value = translatedPrompt.value
  const temp = sourceLang.value
  sourceLang.value = targetLang.value
  targetLang.value = temp
  translatedPrompt.value = ''
  detectedLang.value = ''
  
  // 自动翻译
  translatePrompt()
}

// 编辑结果
const editResult = () => {
  // 将结果复制到输入框进行修改
  const currentResult = translatedPrompt.value
  originalPrompt.value = currentResult
  translatedPrompt.value = ''
  detectedLang.value = ''
  ElMessage.info('已复制到输入框，可以修改后重新翻译')
}
</script>

<style scoped>
.page-container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 24px;
}

.banner {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 16px;
  padding: 32px;
  margin-bottom: 24px;
  text-align: center;
}

.banner-title {
  color: white;
  font-size: 32px;
  margin: 0 0 8px 0;
}

.banner-desc {
  color: rgba(255, 255, 255, 0.9);
  font-size: 16px;
  margin: 0;
}

.content-wrapper {
  background: white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.translate-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 24px;
}

.input-panel,
.output-panel {
  background: #f8f9fc;
  border-radius: 12px;
  padding: 20px;
}

.panel-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.panel-header h3 {
  margin: 0;
  font-size: 18px;
  color: #333;
}

.quick-actions {
  display: flex;
  gap: 8px;
}

.action-btn {
  width: 36px;
  height: 36px;
  border: none;
  background: white;
  border-radius: 8px;
  cursor: pointer;
  font-size: 16px;
  transition: all 0.2s;
}

.action-btn:hover:not(:disabled) {
  background: #e8eaf6;
  transform: scale(1.05);
}

.action-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.lang-selector {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 16px;
  padding: 12px;
  background: white;
  border-radius: 8px;
}

.lang-group {
  flex: 1;
}

.lang-label {
  display: block;
  font-size: 12px;
  color: #666;
  margin-bottom: 4px;
}

.lang-select {
  width: 100%;
  padding: 8px 12px;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-size: 14px;
  background: white;
}

.lang-arrow {
  font-size: 20px;
  color: #667eea;
}

.prompt-input,
.result-output {
  width: 100%;
  padding: 16px;
  border: 1px solid #ddd;
  border-radius: 8px;
  font-size: 14px;
  font-family: 'Monaco', 'Menlo', monospace;
  line-height: 1.6;
  resize: vertical;
  box-sizing: border-box;
}

.prompt-input:focus,
.result-output:focus {
  outline: none;
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.char-info {
  display: flex;
  justify-content: space-between;
  margin-top: 8px;
  font-size: 12px;
  color: #666;
}

.detected-lang {
  color: #667eea;
}

.button-group {
  margin-top: 16px;
  display: flex;
  gap: 12px;
}

.translate-btn {
  flex: 1;
  padding: 14px 24px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.translate-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}

.translate-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.loading-spinner {
  width: 20px;
  height: 20px;
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-top-color: white;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.empty-result,
.loading-result {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 300px;
  color: #999;
}

.empty-icon {
  font-size: 64px;
  margin-bottom: 16px;
}

.loading-animation {
  display: flex;
  gap: 8px;
  margin-bottom: 16px;
}

.loading-dot {
  width: 12px;
  height: 12px;
  background: #667eea;
  border-radius: 50%;
  animation: bounce 1.4s infinite ease-in-out both;
}

.loading-dot:nth-child(1) { animation-delay: -0.32s; }
.loading-dot:nth-child(2) { animation-delay: -0.16s; }

@keyframes bounce {
  0%, 80%, 100% { transform: scale(0); }
  40% { transform: scale(1); }
}

.result-actions {
  margin-top: 16px;
  display: flex;
  gap: 12px;
}

.copy-btn,
.edit-btn {
  flex: 1;
  padding: 10px 16px;
  border: none;
  border-radius: 6px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.2s;
}

.copy-btn {
  background: #667eea;
  color: white;
}

.copy-btn:hover {
  background: #5568d3;
}

.edit-btn {
  background: white;
  color: #666;
  border: 1px solid #ddd;
}

.edit-btn:hover {
  background: #f5f5f5;
}

.tips-section {
  margin-top: 24px;
  padding: 16px;
  background: #f8f9fc;
  border-radius: 8px;
}

.tips-section h4 {
  margin: 0 0 12px 0;
  color: #333;
}

.tips-section ul {
  margin: 0;
  padding-left: 20px;
}

.tips-section li {
  color: #666;
  line-height: 1.8;
}

.tips-section code {
  background: #e8eaf6;
  padding: 2px 6px;
  border-radius: 4px;
  font-size: 13px;
  color: #667eea;
}

@media (max-width: 768px) {
  .translate-grid {
    grid-template-columns: 1fr;
  }
  
  .lang-selector {
    flex-direction: column;
  }
  
  .lang-arrow {
    transform: rotate(90deg);
  }
}
</style>
