<template>
  <div class="structure-analyzer">
    <div class="container">
      <div class="header">
        <h1 class="title">
          <el-icon class="title-icon"><Connection /></el-icon>
          提示词结构分析器
        </h1>
        <p class="subtitle">分析提示词的结构完整性，让AI更好地理解你的需求</p>
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
              placeholder="请输入需要分析结构的提示词..."
              show-word-limit
            />
          </el-card>
          
          <div class="action-area">
            <el-button 
              type="primary" 
              size="large" 
              @click="analyzeStructure" 
              :loading="loading"
              :icon="Connection"
              class="analyze-btn"
            >
              开始分析
            </el-button>
            <el-button size="large" @click="clearInput" :icon="Delete">
              清空
            </el-button>
          </div>
        </el-col>

        <!-- 右侧：结果区域 -->
        <el-col :span="12">
          <div v-if="!result" class="empty-result">
            <el-empty description="输入提示词后点击分析按钮查看结构分析结果">
              <template #image>
                <el-icon :size="80" color="#409eff"><Connection /></el-icon>
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

            <!-- 结构元素列表 -->
            <el-card class="elements-card" shadow="hover">
              <template #header>
                <span>结构元素分析</span>
              </template>
              <div class="structure-elements">
                <div 
                  v-for="(element, index) in result.elements" 
                  :key="index" 
                  class="structure-element"
                  :class="{ present: element.present, absent: !element.present }"
                >
                  <div class="element-icon">
                    <el-icon :size="24">
                      <SuccessFilled v-if="element.present" />
                      <WarningFilled v-else />
                    </el-icon>
                  </div>
                  <div class="element-content">
                    <div class="element-header">
                      <span class="element-name">{{ element.name }}</span>
                      <el-tag :type="element.present ? 'success' : 'info'" size="small">
                        {{ element.present ? '已包含' : '未检测到' }}
                      </el-tag>
                    </div>
                    <div class="element-desc">{{ element.description }}</div>
                    <div v-if="element.present && element.content" class="element-preview">
                      "{{ element.content }}"
                    </div>
                  </div>
                  <div class="element-weight">
                    <span class="weight-value">{{ element.weight }}</span>
                    <span class="weight-label">分值</span>
                  </div>
                </div>
              </div>
            </el-card>

            <!-- 优化建议 -->
            <el-card v-if="result.suggestions.length > 0" class="suggestions-card" shadow="hover">
              <template #header>
                <span>优化建议</span>
              </template>
              <div class="suggestions">
                <div 
                  v-for="(suggestion, index) in result.suggestions" 
                  :key="index" 
                  class="suggestion-item"
                >
                  <el-icon class="suggestion-icon"><ArrowRight /></el-icon>
                  <span>{{ suggestion }}</span>
                </div>
              </div>
            </el-card>

            <!-- 一键优化按钮 -->
            <el-card v-if="result.suggestions.length > 0" class="optimize-card" shadow="hover">
              <el-button 
                type="success" 
                size="large" 
                @click="applySuggestions" 
                :icon="MagicStick"
                class="optimize-btn"
              >
                一键优化提示词
              </el-button>
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
  Connection, 
  Delete, 
  SuccessFilled, 
  WarningFilled, 
  ArrowRight,
  MagicStick
} from '@element-plus/icons-vue'
import { structureApi } from '../api/structure'

interface StructureElement {
  name: string
  description: string
  present: boolean
  content: string
  weight: number
}

interface StructureResult {
  score: number
  level: string
  elements: StructureElement[]
  suggestions: string[]
  summary: string
}

const prompt = ref('')
const loading = ref(false)
const result = ref<StructureResult | null>(null)

const examplePrompt = `你是一位资深的产品经理，正在为一家互联网公司规划新功能。

背景：公司目前用户增长放缓，需要通过提升用户体验来增加留存率。

任务：请帮我分析当前用户流失的主要原因，并提供3-5个具体的优化建议。

要求：
1. 每个建议都要有明确的数据支撑
2. 考虑技术实现难度和开发成本
3. 给出优先级排序

输出格式：请用表格形式呈现，包含建议内容、预期效果、实现难度、优先级等列。

例如：
| 优化建议 | 预期提升 | 难度 | 优先级 |
|---------|---------|------|--------|
| 优化加载速度 | 15% | 中 | 高 |`

const loadExample = () => {
  prompt.value = examplePrompt
}

const analyzeStructure = async () => {
  if (!prompt.value.trim()) {
    ElMessage.warning('请输入提示词')
    return
  }
  
  loading.value = true
  try {
    const response = await structureApi.analyze(prompt.value)
    if (response.data.code === 200) {
      result.value = response.data.data
      ElMessage.success('分析完成')
    } else {
      ElMessage.error(response.data.message || '分析失败')
    }
  } catch (error: any) {
    ElMessage.error(error.message || '请求失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

const clearInput = () => {
  prompt.value = ''
  result.value = null
}

const getScoreClass = (score: number): string => {
  if (score >= 80) return 'excellent'
  if (score >= 60) return 'good'
  if (score >= 40) return 'fair'
  return 'poor'
}

const applySuggestions = () => {
  ElMessage.info('该功能需要结合优化模块使用，请前往优化页面')
}
</script>

<style scoped>
.structure-analyzer {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 40px 20px;
}

.container {
  max-width: 1400px;
  margin: 0 auto;
}

.header {
  text-align: center;
  margin-bottom: 40px;
}

.title {
  font-size: 36px;
  color: #fff;
  margin: 0 0 16px 0;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
}

.title-icon {
  font-size: 42px;
}

.subtitle {
  font-size: 18px;
  color: rgba(255, 255, 255, 0.85);
  margin: 0;
}

.input-card {
  border-radius: 16px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.action-area {
  margin-top: 24px;
  display: flex;
  gap: 12px;
}

.analyze-btn {
  flex: 1;
}

.empty-result {
  background: rgba(255, 255, 255, 0.95);
  border-radius: 16px;
  padding: 60px;
}

.result-area {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.score-card {
  border-radius: 16px;
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
  flex-shrink: 0;
}

.score-circle.excellent {
  background: linear-gradient(135deg, #67c23a 0%, #85ce61 100%);
}

.score-circle.good {
  background: linear-gradient(135deg, #409eff 0%, #66b1ff 100%);
}

.score-circle.fair {
  background: linear-gradient(135deg, #e6a23c 0%, #ebb563 100%);
}

.score-circle.poor {
  background: linear-gradient(135deg, #f56c6c 0%, #f78989 100%);
}

.score-value {
  font-size: 36px;
  font-weight: bold;
  color: #fff;
  line-height: 1;
}

.score-label {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.9);
}

.score-info {
  flex: 1;
}

.score-level {
  font-size: 24px;
  font-weight: bold;
  margin-bottom: 8px;
}

.score-level.excellent {
  color: #67c23a;
}

.score-level.good {
  color: #409eff;
}

.score-level.fair {
  color: #e6a23c;
}

.score-level.poor {
  color: #f56c6c;
}

.score-summary {
  color: #606266;
  font-size: 14px;
  line-height: 1.6;
}

.elements-card {
  border-radius: 16px;
}

.structure-elements {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.structure-element {
  display: flex;
  align-items: flex-start;
  gap: 16px;
  padding: 16px;
  border-radius: 12px;
  background: #f5f7fa;
  transition: all 0.3s;
}

.structure-element.present {
  background: linear-gradient(135deg, #f0f9eb 0%, #e1f3d8 100%);
  border: 1px solid #67c23a;
}

.structure-element.absent {
  background: #f5f7fa;
  border: 1px dashed #dcdfe6;
}

.element-icon {
  flex-shrink: 0;
  width: 40px;
  height: 40px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.present .element-icon {
  background: #67c23a;
  color: #fff;
}

.absent .element-icon {
  background: #909399;
  color: #fff;
}

.element-content {
  flex: 1;
}

.element-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 6px;
}

.element-name {
  font-weight: 600;
  font-size: 16px;
  color: #303133;
}

.element-desc {
  font-size: 13px;
  color: #909399;
  margin-bottom: 6px;
}

.element-preview {
  font-size: 12px;
  color: #606266;
  background: rgba(255, 255, 255, 0.8);
  padding: 8px 12px;
  border-radius: 6px;
  margin-top: 8px;
}

.element-weight {
  flex-shrink: 0;
  text-align: center;
  padding: 8px 12px;
  background: rgba(0, 0, 0, 0.05);
  border-radius: 8px;
}

.weight-value {
  font-size: 20px;
  font-weight: bold;
  color: #303133;
  display: block;
}

.weight-label {
  font-size: 12px;
  color: #909399;
}

.suggestions-card {
  border-radius: 16px;
}

.suggestions {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.suggestion-item {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  padding: 12px;
  background: #fef0f0;
  border-radius: 8px;
  color: #f56c6c;
}

.suggestion-icon {
  flex-shrink: 0;
  margin-top: 2px;
}

.optimize-card {
  border-radius: 16px;
}

.optimize-btn {
  width: 100%;
}
</style>
