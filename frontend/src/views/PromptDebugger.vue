<template>
  <div class="prompt-debugger">
    <div class="container">
      <div class="header">
        <h1 class="title">
          <el-icon class="title-icon"><Tools /></el-icon>
          提示词调试器
        </h1>
        <p class="subtitle">智能诊断提示词问题，让你的提示词更加完美</p>
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
              placeholder="请输入需要调试的提示词..."
              show-word-limit
            />
          </el-card>
          
          <div class="action-area">
            <el-button 
              type="primary" 
              size="large" 
              @click="debugPrompt" 
              :loading="loading"
              :icon="Tools"
              class="debug-btn"
            >
              开始调试
            </el-button>
            <el-button size="large" @click="clearInput" :icon="Delete">
              清空
            </el-button>
          </div>

          <!-- 快速检测 -->
          <el-card class="quick-check-card" shadow="hover">
            <template #header>
              <span>⚡ 快速检测</span>
            </template>
            <div class="quick-checks">
              <el-tag 
                v-for="check in quickChecks" 
                :key="check.name"
                :type="check.passed ? 'success' : 'danger'"
                :effect="check.passed ? 'light' : 'dark'"
                class="quick-tag"
                @click="highlightIssue(check)"
              >
                {{ check.icon }} {{ check.name }}
              </el-tag>
            </div>
          </el-card>
        </el-col>

        <!-- 右侧：结果区域 -->
        <el-col :span="12">
          <div v-if="!result" class="empty-result">
            <el-empty description="输入提示词后点击调试按钮查看分析结果">
              <template #image>
                <el-icon :size="80" color="#e6a23c"><Tools /></el-icon>
              </template>
            </el-empty>
          </div>
          
          <div v-else class="result-area">
            <!-- 问题汇总 -->
            <el-card class="summary-card" shadow="hover">
              <div class="summary-content">
                <div class="problem-count">
                  <span class="count">{{ result.problemCount }}</span>
                  <span class="label">个问题</span>
                </div>
                <div class="summary-text">
                  <div class="status" :class="result.problemCount > 0 ? 'has-issues' : 'no-issues'">
                    {{ result.problemCount > 0 ? '发现以下问题' : '未发现问题' }}
                  </div>
                  <div class="description">{{ result.summary }}</div>
                </div>
              </div>
            </el-card>

            <!-- 问题列表 -->
            <el-card v-if="result.issues && result.issues.length > 0" class="issues-card" shadow="hover">
              <template #header>
                <span>🔍 问题分析</span>
              </template>
              <div class="issues-list">
                <div 
                  v-for="(issue, index) in result.issues" 
                  :key="index" 
                  class="issue-item"
                  :class="issue.severity"
                >
                  <div class="issue-header">
                    <el-tag :type="getSeverityType(issue.severity)" size="small">
                      {{ getSeverityLabel(issue.severity) }}
                    </el-tag>
                    <span class="issue-type">{{ issue.type }}</span>
                  </div>
                  <div class="issue-description">{{ issue.description }}</div>
                  <div class="issue-suggestion">
                    <el-icon><InfoFilled /></el-icon>
                    {{ issue.suggestion }}
                  </div>
                </div>
              </div>
            </el-card>

            <!-- 修复后的提示词 -->
            <el-card v-if="result.fixedPrompt" class="fixed-card" shadow="hover">
              <template #header>
                <div class="card-header">
                  <span>✨ 优化后的提示词</span>
                  <el-button text type="primary" @click="copyFixed">
                    <el-icon><DocumentCopy /></el-icon>
                    复制
                  </el-button>
                </div>
              </template>
              <el-input
                v-model="result.fixedPrompt"
                type="textarea"
                :rows="10"
                readonly
              />
            </el-card>
          </div>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { 
  Tools, 
  Delete,
  InfoFilled,
  DocumentCopy
} from '@element-plus/icons-vue'
import { promptApi } from '../api'

interface Issue {
  type: string
  severity: 'error' | 'warning' | 'info'
  description: string
  suggestion: string
}

interface DebugResult {
  problemCount: number
  summary: string
  issues: Issue[]
  fixedPrompt: string
}

const prompt = ref('')
const loading = ref(false)
const result = ref<DebugResult | null>(null)

// 快速检测
const quickChecks = computed(() => {
  const text = prompt.value
  return [
    { name: '角色定义', icon: '👤', passed: text.includes('你是') || text.includes('角色') },
    { name: '任务明确', icon: '🎯', passed: text.includes('请') || text.includes('帮我') || text.includes('生成') },
    { name: '输出格式', icon: '📝', passed: text.includes('格式') || text.includes('返回') || text.includes('输出') },
    { name: '示例提供', icon: '📋', passed: text.includes('例如') || text.includes('示例') },
    { name: '长度适中', icon: '📏', passed: text.length >= 20 && text.length <= 2000 },
  ]
})

const highlightIssue = (check: any) => {
  if (!check.passed) {
    ElMessage.warning(`${check.name} 缺失，请完善`)
  } else {
    ElMessage.success(`${check.name} 已完善`)
  }
}

// 示例提示词
const badExample = `帮我写一个故事`

const goodExample = `你是一位资深故事作家，擅长创作各种类型的精彩故事。

请根据以下主题创作一个短故事：
主题：人工智能与人类的友谊

要求：
1. 情节完整，有开端、发展、高潮、结局
2. 人物形象鲜明，对话生动
3. 主题积极向上，体现人文关怀
4. 字数在2000字左右

请以以下格式输出：
- 标题：...
- 正文：...

例如：
标题：星尘之友
正文：...`

// 加载示例
const loadExample = () => {
  prompt.value = goodExample
  ElMessage.success('已加载示例')
}

// 清空输入
const clearInput = () => {
  prompt.value = ''
  result.value = null
}

// 调试提示词
const debugPrompt = async () => {
  if (!prompt.value.trim()) {
    ElMessage.warning('请输入提示词')
    return
  }
  
  loading.value = true
  try {
    const response = await promptApi.debug(prompt.value)
    if (response.code === 200) {
      result.value = response.data
      if (result.value.value.problemCount > 0) {
        ElMessage.warning(`发现 ${result.value.value.problemCount} 个问题`)
      } else {
        ElMessage.success('调试完成，未发现问题')
      }
    } else {
      ElMessage.error(response.message || '调试失败')
    }
  } catch (error) {
    console.error('调试失败:', error)
    // 使用本地模拟结果
    result.value = mockDebug(prompt.value)
    ElMessage.success('调试完成（本地分析）')
  } finally {
    loading.value = false
  }
}

// 模拟调试结果
const mockDebug = (text: string): DebugResult => {
  const issues: Issue[] = []
  
  // 检测问题
  if (!text.includes('你是') && !text.includes('角色')) {
    issues.push({
      type: '缺少角色定义',
      severity: 'warning',
      description: '提示词中缺少明确的角色定义，AI可能无法准确理解你的需求',
      suggestion: '建议添加类似"你是一位..."的角色描述'
    })
  }
  
  if (text.length < 50) {
    issues.push({
      type: '提示词过于简短',
      severity: 'error',
      description: '提示词内容过短，可能无法提供足够的上下文信息',
      suggestion: '建议详细描述任务要求、背景信息、输出格式等'
    })
  }
  
  if (!text.includes('格式') && !text.includes('返回') && !text.includes('输出')) {
    issues.push({
      type: '缺少输出格式说明',
      severity: 'warning',
      description: '没有明确指定输出格式，AI的输出可能不符合预期',
      suggestion: '建议添加"请以...格式输出"等说明'
    })
  }
  
  if (!text.includes('例如') && !text.includes('示例')) {
    issues.push({
      type: '缺少示例',
      severity: 'info',
      description: '没有提供示例，可能导致AI理解有偏差',
      suggestion: '建议添加"例如：..."的示例来明确期望'
    })
  }
  
  // 生成优化版本
  let fixedPrompt = text
  if (!text.includes('你是')) {
    fixedPrompt = `你是一位专业的AI助手。\n\n${text}`
  }
  
  if (!text.includes('请') && !text.includes('帮我')) {
    fixedPrompt = fixedPrompt.replace(text, `请${text}`)
  }
  
  if (!text.includes('格式') && !text.includes('返回')) {
    fixedPrompt += `\n\n请以清晰的格式返回结果。`
  }
  
  return {
    problemCount: issues.length,
    summary: issues.length === 0 
      ? '你的提示词结构完整，表达清晰！' 
      : issues.length === 1 
        ? '发现1个问题，建议修复后再使用' 
        : `共发现${issues.length}个问题，建议逐一修复`,
    issues,
    fixedPrompt
  }
}

// 获取严重程度类型
const getSeverityType = (severity: string): string => {
  switch (severity) {
    case 'error': return 'danger'
    case 'warning': return 'warning'
    default: return 'info'
  }
}

// 获取严重程度标签
const getSeverityLabel = (severity: string): string => {
  switch (severity) {
    case 'error': return '严重'
    case 'warning': return '警告'
    default: return '提示'
  }
}

// 复制修复后的提示词
const copyFixed = () => {
  if (result.value?.fixedPrompt) {
    navigator.clipboard.writeText(result.value.fixedPrompt)
    ElMessage.success('已复制到剪贴板')
  }
}
</script>

<style scoped>
.prompt-debugger {
  padding: 24px;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
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

.debug-btn {
  flex: 1;
}

.quick-check-card {
  margin-top: 16px;
}

.quick-checks {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.quick-tag {
  cursor: pointer;
  transition: all 0.3s;
}

.quick-tag:hover {
  transform: scale(1.05);
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

/* 汇总卡片 */
.summary-card {
  border-radius: 12px;
}

.summary-content {
  display: flex;
  align-items: center;
  gap: 24px;
}

.problem-count {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  background: linear-gradient(135deg, #f56c6c, #e74c3c);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: white;
  flex-shrink: 0;
}

.problem-count .count {
  font-size: 32px;
  font-weight: bold;
  line-height: 1;
}

.problem-count .label {
  font-size: 12px;
  opacity: 0.9;
}

.summary-text {
  flex: 1;
}

.status {
  font-size: 20px;
  font-weight: bold;
  margin-bottom: 8px;
}

.status.has-issues {
  color: #f56c6c;
}

.status.no-issues {
  color: #67c23a;
}

.description {
  color: #606266;
  line-height: 1.6;
}

/* 问题列表 */
.issues-card {
  border-radius: 12px;
}

.issues-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.issue-item {
  padding: 16px;
  border-radius: 8px;
  background: #f5f7fa;
  border-left: 4px solid;
}

.issue-item.error {
  border-left-color: #f56c6c;
  background: #fef0f0;
}

.issue-item.warning {
  border-left-color: #e6a23c;
  background: #fdf6ec;
}

.issue-item.info {
  border-left-color: #409eff;
  background: #f0f9ff;
}

.issue-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
}

.issue-type {
  font-weight: 600;
  color: #303133;
}

.issue-description {
  color: #606266;
  margin-bottom: 8px;
  line-height: 1.5;
}

.issue-suggestion {
  display: flex;
  align-items: center;
  gap: 6px;
  color: #409eff;
  font-size: 13px;
  background: white;
  padding: 8px 12px;
  border-radius: 6px;
}

/* 修复卡片 */
.fixed-card {
  border-radius: 12px;
}

:deep(.el-card) {
  border-radius: 12px;
}

:deep(.el-card__header) {
  border-bottom: 1px solid #eee;
  font-weight: 600;
}
</style>
