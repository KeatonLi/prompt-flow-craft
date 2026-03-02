<template>
  <div class="quality-checker">
    <div class="container">
      <div class="header">
        <h1 class="title">
          <el-icon class="title-icon"><Search /></el-icon>
          提示词质量检查器
        </h1>
        <p class="subtitle">AI驱动的提示词质量分析，让你的提示词更高效</p>
      </div>

      <el-row :gutter="24">
        <!-- 左侧：输入区域 -->
        <el-col :span="12">
          <el-card class="input-card" shadow="hover">
            <template #header>
              <div class="card-header">
                <span>输入提示词</span>
                <el-button text type="primary" @click="loadExample">
                  加载示例
                </el-button>
              </div>
            </template>
            <el-input
              v-model="prompt"
              type="textarea"
              :rows="15"
              placeholder="请输入需要检查的提示词..."
              show-word-limit
            />
          </el-card>
          
          <div class="action-area">
            <el-button 
              type="primary" 
              size="large" 
              @click="checkQuality" 
              :loading="loading"
              :icon="Check"
              class="check-btn"
            >
              开始检查
            </el-button>
            <el-button size="large" @click="clearInput" :icon="Delete">
              清空
            </el-button>
          </div>
        </el-col>

        <!-- 右侧：结果区域 -->
        <el-col :span="12">
          <div v-if="!result" class="empty-result">
            <el-empty description="输入提示词后点击检查按钮查看分析结果">
              <template #image>
                <el-icon :size="80" color="#409eff"><Search /></el-icon>
              </template>
            </el-empty>
          </div>
          
          <div v-else class="result-area">
            <!-- 分数卡片 -->
            <el-card class="score-card" shadow="hover">
              <div class="score-content">
                <div class="score-circle" :class="getScoreClass(result.score)">
                  <span class="score-value">{{ result.score }}</span>
                  <span class="score-label">分</span>
                </div>
                <div class="score-info">
                  <div class="score-level" :class="getScoreClass(result.score)">
                    {{ result.level }}
                  </div>
                  <div class="score-summary">{{ result.summary }}</div>
                </div>
              </div>
            </el-card>

            <!-- 检查项列表 -->
            <el-card class="items-card" shadow="hover">
              <template #header>
                <span>检查详情</span>
              </template>
              <div class="check-items">
                <div 
                  v-for="(item, index) in result.items" 
                  :key="index" 
                  class="check-item"
                  :class="{ passed: item.passed, failed: !item.passed }"
                >
                  <el-icon class="item-icon">
                    <CircleCheck v-if="item.passed" />
                    <CircleClose v-else />
                  </el-icon>
                  <div class="item-content">
                    <div class="item-name">{{ item.name }}</div>
                    <div class="item-message">{{ item.message }}</div>
                  </div>
                  <el-tag :type="item.passed ? 'success' : 'warning'" size="small">
                    {{ item.passed ? '通过' : '建议' }}
                  </el-tag>
                </div>
              </div>
            </el-card>

            <!-- 改进建议 -->
            <el-card v-if="result.suggestions && result.suggestions.length > 0" class="suggestions-card" shadow="hover">
              <template #header>
                <span>💡 改进建议</span>
              </template>
              <ul class="suggestions-list">
                <li v-for="(suggestion, index) in result.suggestions" :key="index">
                  {{ suggestion }}
                </li>
              </ul>
            </el-card>
          </div>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { 
  Search, 
  Check, 
  Delete,
  CircleCheck,
  CircleClose
} from '@element-plus/icons-vue'
import { qualityApi } from '../api'

interface CheckItem {
  name: string
  passed: boolean
  message: string
  weight: number
}

interface QualityResult {
  score: number
  level: string
  items: CheckItem[]
  suggestions: string[]
  summary: string
}

const prompt = ref('')
const loading = ref(false)
const result = ref<QualityResult | null>(null)

// 示例提示词
const goodExample = `你是一位专业的法律顾问，精通合同法和知识产权法。

请帮我审查以下合作协议：
1. 检查条款的合法性和完整性
2. 识别潜在的法律风险
3. 提出具体的修改建议

请以以下格式返回审查结果：
- 风险等级：高/中/低
- 问题描述：...
- 建议修改：...

例如：
风险等级：中
问题描述：第3条关于违约责任的约定不够明确
建议修改：建议明确违约金的具体计算方式`

const badExample = `帮我写一个合同`

// 加载示例
const loadExample = () => {
  prompt.value = goodExample
  ElMessage.success('已加载优质示例')
}

// 清空输入
const clearInput = () => {
  prompt.value = ''
  result.value = null
}

// 检查质量
const checkQuality = async () => {
  if (!prompt.value.trim()) {
    ElMessage.warning('请输入提示词')
    return
  }
  
  loading.value = true
  try {
    const response = await qualityApi.check(prompt.value)
    if (response.code === 200) {
      result.value = response.data
      ElMessage.success('检查完成！')
    } else {
      ElMessage.error(response.message || '检查失败')
    }
  } catch (error) {
    console.error('检查失败:', error)
    ElMessage.error('检查失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

// 获取分数样式类
const getScoreClass = (score: number): string => {
  if (score >= 90) return 'excellent'
  if (score >= 70) return 'good'
  if (score >= 50) return 'normal'
  return 'poor'
}
</script>

<style scoped>
.quality-checker {
  padding: 24px;
  min-height: 100vh;
  background: linear-gradient(135deg, #11998e 0%, #38ef7d 100%);
}

.container {
  max-width: 1400px;
  margin: 0 auto;
}

.header {
  text-align: center;
  margin-bottom: 32px;
  color: white;
}

.title {
  font-size: 36px;
  font-weight: bold;
  margin-bottom: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
}

.title-icon {
  font-size: 40px;
}

.subtitle {
  font-size: 18px;
  opacity: 0.9;
}

.input-card {
  margin-bottom: 16px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.action-area {
  display: flex;
  gap: 12px;
}

.check-btn {
  flex: 1;
}

.empty-result {
  background: white;
  border-radius: 12px;
  padding: 40px;
  min-height: 400px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.result-area {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

/* 分数卡片 */
.score-card {
  border-radius: 12px;
}

.score-content {
  display: flex;
  align-items: center;
  gap: 24px;
}

.score-circle {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  flex-shrink: 0;
}

.score-circle.excellent {
  background: linear-gradient(135deg, #67c23a, #85ce61);
  color: white;
}

.score-circle.good {
  background: linear-gradient(135deg, #409eff, #66b1ff);
  color: white;
}

.score-circle.normal {
  background: linear-gradient(135deg, #e6a23c, #ebb563);
  color: white;
}

.score-circle.poor {
  background: linear-gradient(135deg, #f56c6c, #f78989);
  color: white;
}

.score-value {
  font-size: 36px;
  line-height: 1;
}

.score-label {
  font-size: 14px;
  opacity: 0.8;
}

.score-info {
  flex: 1;
}

.score-level {
  font-size: 24px;
  font-weight: bold;
  margin-bottom: 8px;
}

.score-level.excellent { color: #67c23a; }
.score-level.good { color: #409eff; }
.score-level.normal { color: #e6a23c; }
.score-level.poor { color: #f56c6c; }

.score-summary {
  color: #606266;
  line-height: 1.6;
}

/* 检查项 */
.items-card {
  border-radius: 12px;
}

.check-items {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.check-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  border-radius: 8px;
  background: #f5f7fa;
}

.check-item.passed {
  border-left: 3px solid #67c23a;
}

.check-item.failed {
  border-left: 3px solid #e6a23c;
}

.item-icon {
  font-size: 20px;
}

.check-item.passed .item-icon {
  color: #67c23a;
}

.check-item.failed .item-icon {
  color: #e6a23c;
}

.item-content {
  flex: 1;
}

.item-name {
  font-weight: 600;
  margin-bottom: 4px;
}

.item-message {
  font-size: 13px;
  color: #909399;
}

/* 建议 */
.suggestions-card {
  border-radius: 12px;
}

.suggestions-list {
  margin: 0;
  padding-left: 20px;
}

.suggestions-list li {
  margin-bottom: 8px;
  color: #606266;
  line-height: 1.6;
}

.suggestions-list li:last-child {
  margin-bottom: 0;
}

:deep(.el-card) {
  border-radius: 12px;
}

:deep(.el-card__header) {
  border-bottom: 1px solid #eee;
  font-weight: 600;
}
</style>
