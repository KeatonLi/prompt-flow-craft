<template>
  <AppLayout>
    <template #main>
      <div class="page-container">
        <div class="banner">
          <h1 class="banner-title">🔄 提示词工作流</h1>
          <p class="banner-desc">创建自动化提示词流程，一键生成系列提示词</p>
        </div>

        <!-- 工作流列表 -->
        <div class="section">
          <div class="section-header">
            <h2 class="section-title">我的工作流</h2>
            <el-button type="primary" @click="createWorkflow">
              <el-icon><Plus /></el-icon>
              新建工作流
            </el-button>
          </div>
          
          <div v-if="workflows.length === 0" class="empty-state">
            <div class="empty-icon">🔄</div>
            <p>暂无工作流</p>
            <p class="empty-tip">创建一个工作流来自动化你的提示词生成</p>
          </div>
          
          <div v-else class="workflows-grid">
            <div 
              v-for="wf in workflows" 
              :key="wf.id" 
              class="workflow-card"
              @click="editWorkflow(wf)"
            >
              <div class="workflow-icon">{{ wf.icon || '📋' }}</div>
              <div class="workflow-info">
                <div class="workflow-name">{{ wf.name }}</div>
                <div class="workflow-desc">{{ wf.description || '暂无描述' }}</div>
                <div class="workflow-meta">
                  <span class="meta-item">{{ wf.steps?.length || 0 }} 个步骤</span>
                  <span class="meta-item">🕐 {{ fmtDate(wf.createdAt) }}</span>
                </div>
              </div>
              <div class="workflow-actions">
                <el-button size="small" type="primary" @click.stop="runWorkflow(wf)">
                  运行
                </el-button>
                <el-button size="small" @click.stop="editWorkflow(wf)">编辑</el-button>
              </div>
            </div>
          </div>
        </div>

        <!-- 推荐工作流 -->
        <div class="section">
          <h2 class="section-title">推荐工作流</h2>
          <div class="workflows-grid">
            <div 
              v-for="wf in recommendedWorkflows" 
              :key="wf.id" 
              class="workflow-card recommended"
              @click="useRecommendedWorkflow(wf)"
            >
              <div class="workflow-icon">{{ wf.icon }}</div>
              <div class="workflow-info">
                <div class="workflow-name">{{ wf.name }}</div>
                <div class="workflow-desc">{{ wf.description }}</div>
                <div class="workflow-tags">
                  <span v-for="tag in wf.tags" :key="tag" class="tag">{{ tag }}</span>
                </div>
              </div>
              <div class="workflow-actions">
                <el-button size="small" type="success" @click.stop="useRecommendedWorkflow(wf)">
                  使用
                </el-button>
              </div>
            </div>
          </div>
        </div>

        <!-- 创建/编辑工作流弹窗 -->
        <el-dialog
          v-model="showDialog"
          :title="isEditing ? '编辑工作流' : '新建工作流'"
          width="800px"
          :close-on-click-modal="false"
        >
          <el-form :model="form" label-width="100px">
            <el-form-item label="工作流名称">
              <el-input v-model="form.name" placeholder="例如：文章创作工作流" />
            </el-form-item>
            <el-form-item label="描述">
              <el-input v-model="form.description" type="textarea" :rows="2" placeholder="描述这个工作流的用途" />
            </el-form-item>
            <el-form-item label="图标">
              <el-select v-model="form.icon" placeholder="选择图标">
                <el-option v-for="icon in icons" :key="icon" :label="icon" :value="icon">
                  <span>{{ icon }} {{ icon }}</span>
                </el-option>
              </el-select>
            </el-form-item>
            
            <el-form-item label="步骤">
              <div class="steps-editor">
                <div v-for="(step, idx) in form.steps" :key="idx" class="step-item">
                  <div class="step-header">
                    <span class="step-number">步骤 {{ idx + 1 }}</span>
                    <el-button size="small" type="danger" text @click="removeStep(idx)">删除</el-button>
                  </div>
                  <el-input v-model="step.name" placeholder="步骤名称" class="step-name-input" />
                  <el-input v-model="step.taskDescription" type="textarea" :rows="3" placeholder="任务描述" />
                  <div class="step-params">
                    <el-select v-model="step.targetAudience" placeholder="目标受众" clearable>
                      <el-option label="普通用户" value="general" />
                      <el-option label="专业人士" value="professional" />
                      <el-option label="学生" value="student" />
                      <el-option label="开发者" value="developer" />
                      <el-option label="创作者" value="creator" />
                    </el-select>
                    <el-select v-model="step.outputFormat" placeholder="输出格式" clearable>
                      <el-option label="文本" value="text" />
                      <el-option label="列表" value="list" />
                      <el-option label="表格" value="table" />
                      <el-option label="代码" value="code" />
                      <el-option label="JSON" value="json" />
                    </el-select>
                    <el-select v-model="step.tone" placeholder="语调风格" clearable>
                      <el-option label="正式" value="formal" />
                      <el-option label="友好" value="friendly" />
                      <el-option label="专业" value="professional" />
                      <el-option label="创意" value="creative" />
                      <el-option label="简洁" value="concise" />
                    </el-select>
                  </div>
                </div>
                <el-button type="dashed" @click="addStep" class="add-step-btn">
                  <el-icon><Plus /></el-icon>
                  添加步骤
                </el-button>
              </div>
            </el-form-item>
          </el-form>
          
          <template #footer>
            <el-button @click="showDialog = false">取消</el-button>
            <el-button type="primary" @click="saveWorkflow">保存</el-button>
          </template>
        </el-dialog>

        <!-- 运行工作流弹窗 -->
        <el-dialog
          v-model="showRunDialog"
          title="运行工作流"
          width="900px"
          :close-on-click-modal="false"
        >
          <div v-if="runningWorkflow" class="run-progress">
            <div class="run-header">
              <span class="run-icon">{{ runningWorkflow.icon }}</span>
              <span class="run-name">{{ runningWorkflow.name }}</span>
            </div>
            
            <el-progress 
              :percentage="runProgress" 
              :status="runComplete ? 'success' : undefined"
              :stroke-width="12"
              class="run-progress-bar"
            />
            
            <div class="run-steps">
              <div 
                v-for="(step, idx) in runningWorkflow.steps" 
                :key="idx"
                :class="['run-step', { active: currentStep === idx, completed: idx < currentStep }]"
              >
                <div class="step-status">
                  <el-icon v-if="idx < currentStep"><Check /></el-icon>
                  <span v-else>{{ idx + 1 }}</span>
                </div>
                <div class="step-content">
                  <div class="step-title">{{ step.name }}</div>
                  <div v-if="idx === currentStep && running" class="step-loading">
                    正在生成...
                  </div>
                  <div v-else-if="stepResults[idx]" class="step-result">
                    <pre>{{ stepResults[idx] }}</pre>
                  </div>
                </div>
              </div>
            </div>
          </div>
          
          <template #footer>
            <el-button @click="showRunDialog = false">关闭</el-button>
            <el-button v-if="!running && !runComplete" type="primary" @click="startRunWorkflow">
              开始运行
            </el-button>
            <el-button v-if="runComplete" type="success" @click="copyAllResults">
              复制全部结果
            </el-button>
          </template>
        </el-dialog>
      </div>
    </template>
  </AppLayout>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Plus, Check } from '@element-plus/icons-vue'
import AppLayout from '../components/layout/AppLayout.vue'

const router = useRouter()

// 数据
const workflows = ref([])
const showDialog = ref(false)
const isEditing = ref(false)
const editingId = ref(null)

// 运行相关
const showRunDialog = ref(false)
const runningWorkflow = ref(null)
const currentStep = ref(0)
const running = ref(false)
const runProgress = ref(0)
const runComplete = ref(false)
const stepResults = ref([])

// 表单
const form = reactive({
  name: '',
  description: '',
  icon: '📋',
  steps: [
    { name: '', taskDescription: '', targetAudience: '', outputFormat: '', tone: '' }
  ]
})

// 图标列表
const icons = ['📋', '📝', '📚', '💡', '🎨', '🔧', '📊', '🌟', '🚀', '💼', '📰', '🎬', '🛠️', '🎯']

// 推荐工作流
const recommendedWorkflows = [
  {
    id: 1,
    icon: '📰',
    name: '文章创作工作流',
    description: '从主题到SEO优化，自动化完成整篇文章的创作流程',
    tags: ['写作', '内容创作'],
    steps: [
      { name: '生成文章大纲', taskDescription: '为{topic}生成一个详细的文章大纲，包含引言、3-5个主要章节和结论', targetAudience: 'professional', outputFormat: 'list', tone: 'professional' },
      { name: '撰写文章内容', taskDescription: '根据以下大纲撰写一篇完整的文章：{outline}', targetAudience: 'general', outputFormat: 'text', tone: 'friendly' },
      { name: '生成文章摘要', taskDescription: '为以下文章生成一个简洁的摘要（100字以内）：{article}', targetAudience: 'general', outputFormat: 'text', tone: 'concise' },
      { name: '生成SEO元描述', taskDescription: '为这篇文章生成SEO友好的元描述（150字符以内）：{article}', targetAudience: 'developer', outputFormat: 'text', tone: 'concise' }
    ]
  },
  {
    id: 2,
    icon: '🔍',
    name: '代码审查工作流',
    description: '自动化代码审查流程，包括安全检查、性能优化建议',
    tags: ['开发', '代码审查'],
    steps: [
      { name: '代码审查', taskDescription: '请审查以下代码，指出潜在问题和改进建议：{code}', targetAudience: 'developer', outputFormat: 'text', tone: 'professional' },
      { name: '安全检查', taskDescription: '请检查以下代码的安全漏洞：{code}', targetAudience: 'developer', outputFormat: 'list', tone: 'professional' },
      { name: '性能优化', taskDescription: '请分析以下代码的性能问题并提供优化建议：{code}', targetAudience: 'developer', outputFormat: 'list', tone: 'professional' }
    ]
  },
  {
    id: 3,
    icon: '📚',
    name: '学习资料整理工作流',
    description: '从主题研究到笔记整理，完整的学习资料整理流程',
    tags: ['学习', '教育'],
    steps: [
      { name: '生成学习大纲', taskDescription: '为{topic}生成一个系统的学习大纲，包含核心概念和知识点', targetAudience: 'student', outputFormat: 'list', tone: 'friendly' },
      { name: '创建知识卡片', taskDescription: '将以下内容整理成知识点卡片格式：{content}', targetAudience: 'student', outputFormat: 'table', tone: 'concise' },
      { name: '生成复习题', taskDescription: '基于以下内容生成10道复习选择题：{content}', targetAudience: 'student', outputFormat: 'list', tone: 'professional' }
    ]
  },
  {
    id: 4,
    icon: '🎬',
    name: '视频脚本工作流',
    description: '从创意到完整脚本，自动化视频制作流程',
    tags: ['视频', '内容创作'],
    steps: [
      { name: '生成视频创意', taskDescription: '为{topic}生成5个有吸引力的视频创意点子', targetAudience: 'creator', outputFormat: 'list', tone: 'creative' },
      { name: '撰写脚本大纲', taskDescription: '为以下视频创意撰写详细脚本大纲：{idea}', targetAudience: 'creator', outputFormat: 'list', tone: 'professional' },
      { name: '完善脚本', taskDescription: '根据以下大纲撰写完整的视频脚本（约5分钟）：{outline}', targetAudience: 'creator', outputFormat: 'text', tone: 'friendly' },
      { name: '生成开场和结尾', taskDescription: '为视频脚本生成吸引人的开场和结尾文案：{script}', targetAudience: 'creator', outputFormat: 'text', tone: 'creative' }
    ]
  }
]

// 方法
const createWorkflow = () => {
  isEditing.value = false
  editingId.value = null
  form.name = ''
  form.description = ''
  form.icon = '📋'
  form.steps = [{ name: '', taskDescription: '', targetAudience: '', outputFormat: '', tone: '' }]
  showDialog.value = true
}

const editWorkflow = (wf) => {
  isEditing.value = true
  editingId.value = wf.id
  form.name = wf.name
  form.description = wf.description || ''
  form.icon = wf.icon || '📋'
  form.steps = wf.steps?.map(s => ({ ...s })) || []
  showDialog.value = true
}

const addStep = () => {
  form.steps.push({ name: '', taskDescription: '', targetAudience: '', outputFormat: '', tone: '' })
}

const removeStep = (idx) => {
  if (form.steps.length > 1) {
    form.steps.splice(idx, 1)
  }
}

const saveWorkflow = () => {
  if (!form.name) {
    ElMessage.warning('请输入工作流名称')
    return
  }
  if (form.steps.length === 0) {
    ElMessage.warning('请添加至少一个步骤')
    return
  }
  
  // 保存到 localStorage
  let saved = JSON.parse(localStorage.getItem('prompt-workflows') || '[]')
  
  if (isEditing.value) {
    saved = saved.map(w => w.id === editingId.value ? { ...form, id: editingId.value, updatedAt: new Date().toISOString() } : w)
  } else {
    saved.push({ ...form, id: Date.now(), createdAt: new Date().toISOString() })
  }
  
  localStorage.setItem('prompt-workflows', JSON.stringify(saved))
  workflows.value = saved
  
  showDialog.value = false
  ElMessage.success(isEditing.value ? '工作流已更新' : '工作流已创建')
}

const runWorkflow = (wf) => {
  runningWorkflow.value = JSON.parse(JSON.stringify(wf))
  currentStep.value = 0
  runProgress.value = 0
  runComplete.value = false
  stepResults.value = []
  showRunDialog.value = true
}

const useRecommendedWorkflow = (wf) => {
  // 复制推荐工作流到用户的工作流
  let saved = JSON.parse(localStorage.getItem('prompt-workflows') || '[]')
  saved.push({ ...wf, id: Date.now(), createdAt: new Date().toISOString() })
  localStorage.setItem('prompt-workflows', JSON.stringify(saved))
  workflows.value = saved
  ElMessage.success('工作流已添加到你的工作流列表')
}

const startRunWorkflow = async () => {
  running.value = true
  runComplete.value = false
  
  for (let i = 0; i < runningWorkflow.value.steps.length; i++) {
    currentStep.value = i
    
    // 调用API生成提示词
    try {
      const response = await fetch('http://111.231.107.210:8080/api/prompt/generate', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(runningWorkflow.value.steps[i])
      })
      const data = await response.json()
      stepResults.value[i] = data.data || data.prompt || '生成失败'
    } catch (e) {
      stepResults.value[i] = '生成失败: ' + e.message
    }
    
    runProgress.value = Math.round(((i + 1) / runningWorkflow.value.steps.length) * 100)
  }
  
  running.value = false
  runComplete.value = true
  ElMessage.success('工作流执行完成！')
}

const copyAllResults = () => {
  const allText = stepResults.value.map((r, i) => `=== 步骤${i + 1}: ${runningWorkflow.value.steps[i].name} ===\n${r}`).join('\n\n')
  navigator.clipboard.writeText(allText)
  ElMessage.success('已复制全部结果到剪贴板')
}

const fmtDate = (t) => {
  if (!t) return ''
  const d = new Date(t)
  return `${d.getMonth() + 1}/${d.getDate()}`
}

// 加载保存的工作流
const loadWorkflows = () => {
  const saved = localStorage.getItem('prompt-workflows')
  if (saved) {
    workflows.value = JSON.parse(saved)
  }
}

loadWorkflows()
</script>

<style scoped>
.page-container {
  min-height: 100%;
  background: #f8fafc;
  margin: -24px;
}

.banner {
  background: linear-gradient(135deg, #6366f1 0%, #4f46e5 100%);
  padding: 36px 40px;
  text-align: center;
}

.banner-title {
  font-size: 1.6rem;
  font-weight: 700;
  color: white;
  margin: 0 0 6px;
}

.banner-desc {
  color: rgba(255,255,255,0.85);
  font-size: 0.95rem;
  margin: 0;
}

.section {
  padding: 24px 40px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.section-title {
  font-size: 1.2rem;
  font-weight: 600;
  color: #1e293b;
  margin: 0;
}

.empty-state {
  text-align: center;
  padding: 60px 20px;
  color: #94a3b8;
}

.empty-icon {
  font-size: 4rem;
  margin-bottom: 16px;
}

.empty-tip {
  font-size: 0.85rem;
  margin-top: 8px;
}

.workflows-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(380px, 1fr));
  gap: 16px;
}

.workflow-card {
  background: white;
  border-radius: 14px;
  padding: 20px;
  border: 1px solid #e2e8f0;
  cursor: pointer;
  transition: all 0.2s;
  display: flex;
  gap: 16px;
}

.workflow-card:hover {
  border-color: #6366f1;
  box-shadow: 0 6px 20px rgba(99, 102, 241, 0.15);
  transform: translateY(-2px);
}

.workflow-card.recommended {
  border-color: #10b981;
}

.workflow-card.recommended:hover {
  border-color: #10b981;
  box-shadow: 0 6px 20px rgba(16, 185, 129, 0.15);
}

.workflow-icon {
  font-size: 2.5rem;
  flex-shrink: 0;
}

.workflow-info {
  flex: 1;
  min-width: 0;
}

.workflow-name {
  font-weight: 600;
  color: #1e293b;
  font-size: 1rem;
  margin-bottom: 4px;
}

.workflow-desc {
  font-size: 0.85rem;
  color: #64748b;
  margin-bottom: 8px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.workflow-meta {
  display: flex;
  gap: 12px;
  font-size: 0.75rem;
  color: #94a3b8;
}

.workflow-tags {
  display: flex;
  gap: 6px;
  flex-wrap: wrap;
}

.tag {
  font-size: 0.7rem;
  padding: 2px 8px;
  background: #f1f5f9;
  color: #64748b;
  border-radius: 10px;
}

.workflow-actions {
  display: flex;
  flex-direction: column;
  gap: 8px;
  justify-content: center;
}

/* 步骤编辑器 */
.steps-editor {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.step-item {
  background: #f8fafc;
  border-radius: 12px;
  padding: 16px;
  border: 1px solid #e2e8f0;
}

.step-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.step-number {
  font-weight: 600;
  color: #6366f1;
}

.step-name-input {
  margin-bottom: 12px;
}

.step-params {
  display: flex;
  gap: 12px;
  margin-top: 12px;
}

.step-params .el-select {
  flex: 1;
}

.add-step-btn {
  width: 100%;
  border-style: dashed;
}

/* 运行进度 */
.run-progress {
  padding: 20px;
}

.run-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 20px;
}

.run-icon {
  font-size: 2rem;
}

.run-name {
  font-size: 1.2rem;
  font-weight: 600;
  color: #1e293b;
}

.run-progress-bar {
  margin-bottom: 24px;
}

.run-steps {
  display: flex;
  flex-direction: column;
  gap: 16px;
  max-height: 400px;
  overflow-y: auto;
}

.run-step {
  display: flex;
  gap: 16px;
  padding: 16px;
  border-radius: 12px;
  background: #f8fafc;
  border: 1px solid #e2e8f0;
}

.run-step.active {
  border-color: #6366f1;
  background: #eef2ff;
}

.run-step.completed {
  border-color: #10b981;
  background: #ecfdf5;
}

.step-status {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: #e2e8f0;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
  color: #64748b;
  flex-shrink: 0;
}

.run-step.completed .step-status {
  background: #10b981;
  color: white;
}

.run-step.active .step-status {
  background: #6366f1;
  color: white;
}

.step-content {
  flex: 1;
  min-width: 0;
}

.step-title {
  font-weight: 600;
  color: #1e293b;
  margin-bottom: 8px;
}

.step-loading {
  color: #6366f1;
  font-size: 0.85rem;
}

.step-result pre {
  background: white;
  padding: 12px;
  border-radius: 8px;
  font-size: 0.85rem;
  max-height: 120px;
  overflow-y: auto;
  white-space: pre-wrap;
  word-wrap: break-word;
}
</style>
