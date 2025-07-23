<template>
  <div class="home">
    <HistorySidebar ref="historySidebar" @reuse-history="handleReuseHistory" />
    <div class="container">
      <div class="header">
        <h1 class="title">
          <el-icon class="title-icon"><Magic /></el-icon>
          AI 提示词生成器
        </h1>
        <p class="subtitle">让AI更懂你的想法，一键生成专业级提示词</p>
        <div class="action-buttons">
          <el-button 
            type="primary" 
            :icon="Clock" 
            @click="toggleHistorySidebar"
            class="history-toggle-btn"
            size="large"
          >
            <span>历史记录</span>
          </el-button>
        </div>
      </div>

      <el-card class="form-card" shadow="hover">
        <el-form
          ref="promptForm"
          :model="formData"
          :rules="rules"
          label-width="120px"
          size="large"
        >
          <el-form-item label="任务描述" prop="taskDescription">
            <el-input
              v-model="formData.taskDescription"
              type="textarea"
              :rows="3"
              placeholder="请详细描述您希望AI完成的任务..."
              show-word-limit
              maxlength="500"
            />
          </el-form-item>

          <el-form-item label="目标受众" prop="targetAudience">
            <el-select
              v-model="formData.targetAudience"
              placeholder="请选择目标受众"
              style="width: 100%"
            >
              <el-option label="普通用户" value="general" />
              <el-option label="专业人士" value="professional" />
              <el-option label="学生" value="student" />
              <el-option label="开发者" value="developer" />
              <el-option label="创作者" value="creator" />
            </el-select>
          </el-form-item>

          <el-form-item label="输出格式" prop="outputFormat">
            <el-select
              v-model="formData.outputFormat"
              placeholder="请选择期望的输出格式"
              style="width: 100%"
            >
              <el-option label="文本" value="text" />
              <el-option label="列表" value="list" />
              <el-option label="表格" value="table" />
              <el-option label="代码" value="code" />
              <el-option label="JSON" value="json" />
            </el-select>
          </el-form-item>

          <el-form-item label="约束条件">
            <el-input
              v-model="formData.constraints"
              type="textarea"
              :rows="2"
              placeholder="请输入任何约束条件或特殊要求（可选）..."
              show-word-limit
              maxlength="200"
            />
          </el-form-item>

          <el-form-item label="参考示例">
            <el-input
              v-model="formData.examples"
              type="textarea"
              :rows="2"
              placeholder="请提供参考示例（可选）..."
              show-word-limit
              maxlength="300"
            />
          </el-form-item>

          <el-form-item label="语调风格" prop="tone">
            <el-select
              v-model="formData.tone"
              placeholder="请选择语调风格"
              style="width: 100%"
            >
              <el-option label="正式" value="formal" />
              <el-option label="友好" value="friendly" />
              <el-option label="专业" value="professional" />
              <el-option label="创意" value="creative" />
              <el-option label="简洁" value="concise" />
            </el-select>
          </el-form-item>

          <el-form-item label="内容长度" prop="length">
            <el-select
              v-model="formData.length"
              placeholder="请选择期望的内容长度"
              style="width: 100%"
            >
              <el-option label="简短（50-100字）" value="short" />
              <el-option label="中等（100-300字）" value="medium" />
              <el-option label="详细（300-500字）" value="long" />
              <el-option label="非常详细（500字以上）" value="very_long" />
            </el-select>
          </el-form-item>

          <el-form-item>
            <el-button
              type="primary"
              size="large"
              :loading="loading"
              @click="generatePrompt"
              style="width: 100%"
            >
              <el-icon><Magic /></el-icon>
              {{ loading ? '生成中...' : '生成提示词' }}
            </el-button>
          </el-form-item>
        </el-form>
      </el-card>

      <!-- 结果展示区域 -->
      <el-card v-if="result" class="result-card" shadow="hover">
        <template #header>
          <div class="result-header">
            <span>生成的提示词</span>
            <div class="result-actions">
              <el-button size="small" @click="copyToClipboard">
                <el-icon><DocumentCopy /></el-icon>
                复制
              </el-button>
              <el-button size="small" type="danger" @click="clearResult">
                <el-icon><Delete /></el-icon>
                清空
              </el-button>
            </div>
          </div>
        </template>
        <div class="result-content">
          <pre>{{ result }}</pre>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script>
import { generatePromptAPI } from '../api/prompt'
import HistorySidebar from '../components/HistorySidebar.vue'
import { Clock } from '@element-plus/icons-vue'

export default {
  name: 'Home',
  components: {
    HistorySidebar,
    Clock
  },
  data() {
    return {
      loading: false,
      result: '',
      formData: {
        taskDescription: '',
        targetAudience: '',
        outputFormat: '',
        constraints: '',
        examples: '',
        tone: '',
        length: ''
      },
      rules: {
        taskDescription: [
          { required: true, message: '请输入任务描述', trigger: 'blur' },
          { min: 10, message: '任务描述至少需要10个字符', trigger: 'blur' }
        ],
        targetAudience: [
          { required: true, message: '请选择目标受众', trigger: 'change' }
        ],
        outputFormat: [
          { required: true, message: '请选择输出格式', trigger: 'change' }
        ],
        tone: [
          { required: true, message: '请选择语调风格', trigger: 'change' }
        ],
        length: [
          { required: true, message: '请选择内容长度', trigger: 'change' }
        ]
      }
    }
  },
  methods: {
    async generatePrompt() {
      try {
        const valid = await this.$refs.promptForm.validate()
        if (!valid) return

        this.loading = true
        const response = await generatePromptAPI(this.formData)
        
        // 检查响应是否成功
        if (response.success) {
          this.result = response.generated_prompt
        } else {
          throw new Error(response.message || '生成失败')
        }
        
        this.$message.success('提示词生成成功！')
      } catch (error) {
        console.error('生成提示词失败:', error)
        this.$message.error('生成提示词失败，请稍后重试')
      } finally {
        this.loading = false
      }
    },
    
    async copyToClipboard() {
      try {
        // 优先使用现代的 Clipboard API
        if (navigator.clipboard && window.isSecureContext) {
          await navigator.clipboard.writeText(this.result)
          this.$message.success('已复制到剪贴板')
          return
        }
        
        // 降级方案：使用传统的 execCommand 方法
        const textArea = document.createElement('textarea')
        textArea.value = this.result
        textArea.style.position = 'fixed'
        textArea.style.left = '-999999px'
        textArea.style.top = '-999999px'
        document.body.appendChild(textArea)
        textArea.focus()
        textArea.select()
        
        const successful = document.execCommand('copy')
        document.body.removeChild(textArea)
        
        if (successful) {
          this.$message.success('已复制到剪贴板')
        } else {
          throw new Error('execCommand failed')
        }
      } catch (error) {
        console.error('复制失败:', error)
        // 最后的降级方案：选中文本让用户手动复制
        this.selectResultText()
        this.$message.error('自动复制失败，已为您选中文本，请使用 Ctrl+C (或 Cmd+C) 手动复制')
      }
    },
    
    selectResultText() {
      // 选中结果文本
      this.$nextTick(() => {
        const preElement = document.querySelector('.result-content pre')
        if (preElement) {
          const range = document.createRange()
          range.selectNodeContents(preElement)
          const selection = window.getSelection()
          selection.removeAllRanges()
          selection.addRange(range)
        }
      })
    },
    
    clearResult() {
      this.result = ''
      this.$message.info('已清空结果')
    },
    
    toggleHistorySidebar() {
      // 通过ref访问HistorySidebar组件并切换其显示状态
      this.$refs.historySidebar?.toggleSidebar()
    },
    
    handleReuseHistory(historyData) {
      // 复用历史记录到表单
      this.formData = {
        taskDescription: historyData.taskDescription || '',
        targetAudience: historyData.targetAudience || '',
        outputFormat: historyData.outputFormat || '',
        constraints: historyData.constraints || '',
        examples: historyData.examples || '',
        tone: historyData.tone || '',
        length: historyData.length || ''
      };
      // this.$message.success('已加载历史记录');
      
      // 滚动到表单顶部
      this.$nextTick(() => {
        this.$refs.promptForm.$el.scrollIntoView({ behavior: 'smooth' });
      });
    },
    
    isTextOverflow(text) {
      if (!text) return false
      // 简单判断：如果文本长度超过100个字符，则认为可能溢出
      return text.length > 100
    }
  }
}
</script>

<style scoped>
.home {
  min-height: calc(100vh - 144px);
  background: linear-gradient(135deg, #ffffff 0%, #f8fafc 50%, #f1f5f9 100%);
  position: relative;
  overflow-x: hidden;
  overflow-y: auto;
  margin-left: 420px;
  transition: margin-left 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.home::before {
  content: '';
  position: absolute;
  top: -50%;
  left: -50%;
  width: 200%;
  height: 200%;
  background: radial-gradient(circle, rgba(59, 130, 246, 0.04) 0%, transparent 70%);
  animation: float 20s ease-in-out infinite;
  pointer-events: none;
}

@keyframes float {
  0%, 100% { transform: translate(0, 0) rotate(0deg); }
  33% { transform: translate(30px, -30px) rotate(120deg); }
  66% { transform: translate(-20px, 20px) rotate(240deg); }
}

.container {
  max-width: 1000px;
  margin: 0 auto;
  padding: 40px 40px;
  transition: all 0.3s ease;
  position: relative;
  z-index: 1;
}

@media (max-width: 1200px) {
  .home {
    margin-left: 0;
  }
  .container {
    max-width: 100%;
    padding: 20px;
  }
}

@media (max-width: 768px) {
  .home {
    margin-left: 0;
  }
  .container {
    padding: 16px;
  }
}

.header {
  text-align: center;
  margin-bottom: 50px;
  animation: fadeInUp 0.8s ease-out;
}

.action-buttons {
  margin-top: 32px;
  display: flex;
  justify-content: center;
  gap: 16px;
}

.history-toggle-btn {
  font-size: 16px;
  padding: 16px 32px;
  border-radius: 16px;
  font-weight: 600;
  background: linear-gradient(135deg, #f8fafc 0%, #ffffff 100%);
  border: 2px solid #e2e8f0;
  color: #475569;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08), 0 2px 8px rgba(0, 0, 0, 0.04);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  backdrop-filter: blur(12px);
}

.history-toggle-btn:hover {
  background: linear-gradient(135deg, #3b82f6 0%, #1d4ed8 100%);
  border-color: #3b82f6;
  color: white;
  transform: translateY(-3px);
  box-shadow: 0 8px 24px rgba(59, 130, 246, 0.25), 0 4px 12px rgba(59, 130, 246, 0.15);
}

.history-toggle-btn:active {
  transform: translateY(-1px);
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.title {
  font-size: 3.25rem;
  background: linear-gradient(135deg, #1e293b 0%, #334155 50%, #3b82f6 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  margin-bottom: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 20px;
  font-weight: 800;
  letter-spacing: -0.025em;
}

.title-icon {
  font-size: 3.5rem;
  background: linear-gradient(135deg, #3b82f6 0%, #1d4ed8 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  animation: pulse 2s ease-in-out infinite;
}

@keyframes pulse {
  0%, 100% { transform: scale(1); }
  50% { transform: scale(1.05); }
}

.subtitle {
  font-size: 1.375rem;
  color: #64748b;
  margin: 0;
  font-weight: 500;
  opacity: 0.9;
}

.form-card {
  margin-bottom: 40px;
  border: 1px solid rgba(226, 232, 240, 0.6);
  border-radius: 24px;
  background: rgba(255, 255, 255, 0.98);
  backdrop-filter: blur(24px);
  box-shadow: 0 4px 24px rgba(0, 0, 0, 0.06), 0 1px 3px rgba(0, 0, 0, 0.05);
  transition: all 0.3s ease;
  animation: slideInUp 0.6s ease-out 0.2s both;
}

.form-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 12px 48px rgba(0, 0, 0, 0.12), 0 6px 24px rgba(0, 0, 0, 0.08);
}

@keyframes slideInUp {
  from {
    opacity: 0;
    transform: translateY(40px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.form-card :deep(.el-card__body) {
  padding: 40px;
  border-radius: 20px;
}

.result-card {
  margin-top: 40px;
  border: none;
  border-radius: 20px;
  background: #ffffff;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.08), 0 4px 16px rgba(0, 0, 0, 0.04);
  animation: slideInUp 0.6s ease-out 0.4s both;
  overflow: hidden;
}

.result-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 25px 50px rgba(125, 211, 160, 0.2), 0 10px 20px rgba(0, 0, 0, 0.08);
}

.result-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: 600;
  color: #333333;
  font-size: 1.1rem;
}

.result-actions {
  display: flex;
  gap: 12px;
}

.result-content {
  background: #f8f9fa;
  border: 1px solid rgba(230, 230, 230, 0.6);
  border-radius: 16px;
  padding: 30px;
  margin-top: 20px;
  position: relative;
  overflow: hidden;
}

.result-content::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, #3b82f6 0%, #60a5fa 50%, #2563eb 100%);
}

.result-content pre {
  margin: 0;
  white-space: pre-wrap;
  word-wrap: break-word;
  word-break: break-word;
  overflow-wrap: break-word;
  font-family: 'SF Mono', 'Monaco', 'Inconsolata', 'Roboto Mono', monospace;
  line-height: 1.7;
  color: #555555;
  font-size: 0.95rem;
  max-width: 100%;
  overflow-x: auto;
  padding: 0;
}

:deep(.el-form-item) {
  margin-bottom: 28px;
}

:deep(.el-form-item__label) {
  font-weight: 600;
  color: #333333;
  font-size: 1rem;
  margin-bottom: 8px;
}

:deep(.el-input__wrapper) {
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
  transition: all 0.3s ease;
  border: 1px solid rgba(230, 230, 230, 0.6);
  background: #f8f9fa;
}

:deep(.el-input__wrapper:hover) {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  border-color: rgba(125, 211, 160, 0.4);
  background: #ffffff;
}

:deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1), 0 4px 12px rgba(0, 0, 0, 0.08);
  border-color: #3b82f6;
  background: #ffffff;
}

:deep(.el-textarea__inner) {
  border-radius: 12px;
  border: 1px solid rgba(230, 230, 230, 0.6);
  transition: all 0.3s ease;
  font-family: inherit;
  line-height: 1.6;
  background: #f8f9fa;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
  resize: vertical;
  min-height: 120px;
}

:deep(.el-textarea__inner:hover) {
  border-color: rgba(125, 211, 160, 0.4);
  background: #ffffff;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}

:deep(.el-textarea__inner:focus) {
  border-color: #7dd3a0;
  box-shadow: 0 0 0 3px rgba(125, 211, 160, 0.1), 0 4px 12px rgba(0, 0, 0, 0.08);
  background: #ffffff;
}

:deep(.el-select) {
  width: 100%;
}

:deep(.el-select .el-input__wrapper) {
  border-radius: 12px;
  border: 1px solid rgba(230, 230, 230, 0.6);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
  background: #f8f9fa;
}

:deep(.el-button--primary) {
  background: linear-gradient(135deg, #3b82f6 0%, #1d4ed8 100%);
  border: none;
  border-radius: 12px;
  padding: 16px 36px;
  font-size: 16px;
  font-weight: 600;
  color: white;
  box-shadow: 0 4px 16px rgba(59, 130, 246, 0.25), 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  overflow: hidden;
}

:deep(.el-button--primary::before) {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
  transition: left 0.5s ease;
}

:deep(.el-button--primary:hover) {
  background: linear-gradient(135deg, #2563eb 0%, #1e40af 100%);
  transform: translateY(-2px) scale(1.02);
  box-shadow: 0 8px 24px rgba(59, 130, 246, 0.35), 0 4px 12px rgba(0, 0, 0, 0.15);
}

:deep(.el-button--primary:hover::before) {
  left: 100%;
}

:deep(.el-button--primary:active) {
  transform: translateY(0) scale(0.98);
  box-shadow: 0 2px 8px rgba(59, 130, 246, 0.25), 0 1px 4px rgba(0, 0, 0, 0.1);
}

:deep(.el-button--small) {
  border-radius: 10px;
  padding: 10px 18px;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.3s ease;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  background: rgba(248, 250, 252, 0.9);
  border: 1px solid rgba(226, 232, 240, 0.8);
  color: #64748b;
}

:deep(.el-button--small:hover) {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  background: rgba(255, 255, 255, 0.95);
  border-color: #3b82f6;
  color: #3b82f6;
}
</style>