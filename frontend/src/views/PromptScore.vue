<template>
  <div class="score-page">
    <div class="score-header">
      <h1>📊 提示词质量评分</h1>
      <p>AI 驱动的多维度提示词质量分析</p>
    </div>

    <!-- 输入区域 -->
    <div class="input-section">
      <div class="input-header">
        <h2>输入提示词</h2>
        <div class="quick-actions">
          <button class="quick-btn" @click="loadExample('simple')">简单示例</button>
          <button class="quick-btn" @click="loadExample('good')">优质示例</button>
          <button class="quick-btn" @click="loadExample('bad')">待改进示例</button>
        </div>
      </div>
      <textarea 
        v-model="promptText" 
        placeholder="请输入要评分的提示词内容..."
        rows="8"
      ></textarea>
      <div class="action-bar">
        <button class="analyze-btn" @click="analyzePrompt" :disabled="loading || !promptText.trim()">
          <span v-if="loading" class="loading">分析中...</span>
          <span v-else>🔍 开始分析</span>
        </button>
        <button class="clear-btn" @click="clearInput">清空</button>
      </div>
    </div>

    <!-- 评分结果 -->
    <div v-if="scoreResult" class="result-section">
      <!-- 总分卡片 -->
      <div class="score-card main-score">
        <div class="score-circle" :class="getScoreLevel(scoreResult.totalScore)">
          <span class="score-number">{{ scoreResult.totalScore }}</span>
          <span class="score-label">总分</span>
        </div>
        <div class="score-info">
          <h3 :class="getScoreLevel(scoreResult.totalScore)">{{ scoreResult.level }}</h3>
          <p class="summary">{{ scoreResult.summary }}</p>
        </div>
      </div>

      <!-- 分项评分 -->
      <div class="score-details">
        <h3>分项评分</h3>
        <div class="score-grid">
          <div class="score-item">
            <div class="score-bar">
              <div class="score-fill" :style="{ width: scoreResult.clarityScore + '%' }"></div>
            </div>
            <div class="score-meta">
              <span>清晰度</span>
              <span class="score-value">{{ scoreResult.clarityScore }}</span>
            </div>
          </div>
          <div class="score-item">
            <div class="score-bar">
              <div class="score-fill" :style="{ width: scoreResult.completenessScore + '%' }"></div>
            </div>
            <div class="score-meta">
              <span>完整性</span>
              <span class="score-value">{{ scoreResult.completenessScore }}</span>
            </div>
          </div>
          <div class="score-item">
            <div class="score-bar">
              <div class="score-fill" :style="{ width: scoreResult.structureScore + '%' }"></div>
            </div>
            <div class="score-meta">
              <span>结构化</span>
              <span class="score-value">{{ scoreResult.structureScore }}</span>
            </div>
          </div>
          <div class="score-item">
            <div class="score-bar">
              <div class="score-fill" :style="{ width: scoreResult.examplesScore + '%' }"></div>
            </div>
            <div class="score-meta">
              <span>示例质量</span>
              <span class="score-value">{{ scoreResult.examplesScore }}</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 优点与不足 -->
      <div class="analysis-grid">
        <div class="analysis-card strengths">
          <h3>✨ 优点</h3>
          <ul v-if="scoreResult.strengths && scoreResult.strengths.length">
            <li v-for="(item, index) in scoreResult.strengths" :key="index">{{ item }}</li>
          </ul>
          <p v-else class="empty-text">暂无明显优点</p>
        </div>
        <div class="analysis-card weaknesses">
          <h3>⚠️ 不足</h3>
          <ul v-if="scoreResult.weaknesses && scoreResult.weaknesses.length">
            <li v-for="(item, index) in scoreResult.weaknesses" :key="index">{{ item }}</li>
          </ul>
          <p v-else class="empty-text">暂无明显不足</p>
        </div>
      </div>

      <!-- 改进建议 -->
      <div class="suggestions-card">
        <h3>💡 改进建议</h3>
        <ul v-if="scoreResult.suggestions && scoreResult.suggestions.length">
          <li v-for="(item, index) in scoreResult.suggestions" :key="index">{{ item }}</li>
        </ul>
        <p v-else class="empty-text">暂无改进建议</p>
      </div>

      <!-- 重新分析 -->
      <div class="re-analyze">
        <button class="analyze-btn" @click="analyzePrompt" :disabled="loading">
          <span v-if="loading">分析中...</span>
          <span v-else>🔄 重新分析</span>
        </button>
      </div>
    </div>

    <!-- 加载状态 -->
    <div v-if="loading" class="loading-state">
      <div class="loading-spinner"></div>
      <p>AI 正在分析您的提示词...</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'

const API_BASE = import.meta.env.VITE_API_BASE || 'http://localhost:8080/api'

const promptText = ref('')
const loading = ref(false)
const scoreResult = ref<any>(null)

const examples = {
  simple: '写一首关于春天的诗',
  good: `你是一位专业的诗歌作家。

请根据用户给定的关键词创作一首关于春天的七言绝句。

要求：
1. 押韵工整
2. 意境优美
3. 表达对春天的赞美之情

请先确认用户提供的关键词，然后开始创作。`,
  bad: '帮我写点东西'
}

const loadExample = (type: string) => {
  promptText.value = examples[type as keyof typeof examples]
}

const clearInput = () => {
  promptText.value = ''
  scoreResult.value = null
}

const analyzePrompt = async () => {
  if (!promptText.value.trim()) return
  
  loading.value = true
  scoreResult.value = null
  
  try {
    const response = await fetch(`${API_BASE}/prompt/score`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        prompt: promptText.value
      })
    })
    
    const data = await response.json()
    if (data.code === 200) {
      scoreResult.value = data.data
    } else {
      alert(data.message || '分析失败')
    }
  } catch (error) {
    console.error('分析失败:', error)
    alert('网络错误，请稍后重试')
  } finally {
    loading.value = false
  }
}

const getScoreLevel = (score: number): string => {
  if (score >= 90) return 'excellent'
  if (score >= 70) return 'good'
  if (score >= 50) return 'fair'
  return 'poor'
}
</script>

<style scoped>
.score-page {
  max-width: 900px;
  margin: 0 auto;
  padding: 30px 20px;
}

.score-header {
  text-align: center;
  margin-bottom: 30px;
}

.score-header h1 {
  font-size: 2.2rem;
  margin-bottom: 10px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.score-header p {
  color: #666;
  font-size: 1.1rem;
}

.input-section {
  background: white;
  border-radius: 16px;
  padding: 25px;
  box-shadow: 0 2px 15px rgba(0,0,0,0.08);
  margin-bottom: 30px;
}

.input-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.input-header h2 {
  font-size: 1.3rem;
  color: #333;
}

.quick-actions {
  display: flex;
  gap: 10px;
}

.quick-btn {
  padding: 6px 14px;
  background: #f0f0f0;
  border: none;
  border-radius: 15px;
  font-size: 0.85rem;
  cursor: pointer;
  transition: all 0.2s;
  color: #555;
}

.quick-btn:hover {
  background: #e0e0e0;
  transform: translateY(-1px);
}

textarea {
  width: 100%;
  padding: 15px;
  border: 2px solid #e8e8e8;
  border-radius: 12px;
  font-size: 1rem;
  font-family: inherit;
  resize: vertical;
  transition: border-color 0.3s;
  box-sizing: border-box;
}

textarea:focus {
  outline: none;
  border-color: #667eea;
}

.action-bar {
  display: flex;
  gap: 15px;
  margin-top: 15px;
}

.analyze-btn {
  flex: 1;
  padding: 14px 30px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 12px;
  font-size: 1.1rem;
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
}

.analyze-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 5px 20px rgba(102, 126, 234, 0.4);
}

.analyze-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.clear-btn {
  padding: 14px 30px;
  background: #f5f5f5;
  color: #666;
  border: none;
  border-radius: 12px;
  font-size: 1rem;
  cursor: pointer;
  transition: all 0.2s;
}

.clear-btn:hover {
  background: #e8e8e8;
}

/* 结果区域 */
.result-section {
  animation: fadeIn 0.5s ease;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
}

.score-card {
  background: white;
  border-radius: 16px;
  padding: 25px;
  box-shadow: 0 2px 15px rgba(0,0,0,0.08);
  display: flex;
  align-items: center;
  gap: 30px;
  margin-bottom: 20px;
}

.score-circle {
  width: 120px;
  height: 120px;
  border-radius: 50%;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  flex-shrink: 0;
}

.score-circle.excellent {
  background: linear-gradient(135deg, #4ade80 0%, #22c55e 100%);
  box-shadow: 0 5px 20px rgba(34, 197, 94, 0.3);
}

.score-circle.good {
  background: linear-gradient(135deg, #60a5fa 0%, #3b82f6 100%);
  box-shadow: 0 5px 20px rgba(59, 130, 246, 0.3);
}

.score-circle.fair {
  background: linear-gradient(135deg, #fbbf24 0%, #f59e0b 100%);
  box-shadow: 0 5px 20px rgba(245, 158, 11, 0.3);
}

.score-circle.poor {
  background: linear-gradient(135deg, #f87171 0%, #ef4444 100%);
  box-shadow: 0 5px 20px rgba(239, 68, 68, 0.3);
}

.score-number {
  font-size: 2.5rem;
  font-weight: bold;
  color: white;
}

.score-label {
  font-size: 0.9rem;
  color: rgba(255,255,255,0.9);
}

.score-info h3 {
  font-size: 1.8rem;
  margin-bottom: 10px;
}

.score-info h3.excellent { color: #22c55e; }
.score-info h3.good { color: #3b82f6; }
.score-info h3.fair { color: #f59e0b; }
.score-info h3.poor { color: #ef4444; }

.summary {
  color: #666;
  font-size: 1rem;
}

/* 分项评分 */
.score-details {
  background: white;
  border-radius: 16px;
  padding: 25px;
  box-shadow: 0 2px 15px rgba(0,0,0,0.08);
  margin-bottom: 20px;
}

.score-details h3 {
  margin-bottom: 20px;
  color: #333;
}

.score-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
}

@media (max-width: 600px) {
  .score-grid {
    grid-template-columns: 1fr;
  }
}

.score-item {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.score-bar {
  height: 12px;
  background: #f0f0f0;
  border-radius: 6px;
  overflow: hidden;
}

.score-fill {
  height: 100%;
  background: linear-gradient(90deg, #667eea 0%, #764ba2 100%);
  border-radius: 6px;
  transition: width 0.5s ease;
}

.score-meta {
  display: flex;
  justify-content: space-between;
  font-size: 0.95rem;
  color: #555;
}

.score-value {
  font-weight: bold;
  color: #667eea;
}

/* 优点与不足 */
.analysis-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
  margin-bottom: 20px;
}

@media (max-width: 600px) {
  .analysis-grid {
    grid-template-columns: 1fr;
  }
}

.analysis-card {
  background: white;
  border-radius: 16px;
  padding: 25px;
  box-shadow: 0 2px 15px rgba(0,0,0,0.08);
}

.analysis-card h3 {
  margin-bottom: 15px;
  font-size: 1.1rem;
}

.strengths h3 { color: #22c55e; }
.weaknesses h3 { color: #ef4444; }

.analysis-card ul {
  list-style: none;
  padding: 0;
  margin: 0;
}

.analysis-card li {
  padding: 8px 0;
  border-bottom: 1px solid #f0f0f0;
  font-size: 0.95rem;
  color: #444;
  line-height: 1.5;
}

.analysis-card li:last-child {
  border-bottom: none;
}

.empty-text {
  color: #999;
  font-style: italic;
}

/* 改进建议 */
.suggestions-card {
  background: white;
  border-radius: 16px;
  padding: 25px;
  box-shadow: 0 2px 15px rgba(0,0,0,0.08);
  margin-bottom: 20px;
}

.suggestions-card h3 {
  margin-bottom: 15px;
  color: #667eea;
}

.suggestions-card ul {
  list-style: none;
  padding: 0;
  margin: 0;
}

.suggestions-card li {
  padding: 10px 0;
  border-bottom: 1px solid #f0f0f0;
  font-size: 0.95rem;
  color: #444;
  line-height: 1.5;
  display: flex;
  align-items: flex-start;
  gap: 10px;
}

.suggestions-card li:last-child {
  border-bottom: none;
}

.suggestions-card li::before {
  content: "→";
  color: #667eea;
  font-weight: bold;
  flex-shrink: 0;
}

/* 重新分析 */
.re-analyze {
  text-align: center;
  margin-top: 20px;
}

.re-analyze .analyze-btn {
  max-width: 200px;
}

/* 加载状态 */
.loading-state {
  text-align: center;
  padding: 60px 20px;
}

.loading-spinner {
  width: 50px;
  height: 50px;
  border: 4px solid #f3f3f3;
  border-top: 4px solid #667eea;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin: 0 auto 20px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.loading-state p {
  color: #666;
  font-size: 1.1rem;
}

.loading {
  display: inline-block;
}
</style>
