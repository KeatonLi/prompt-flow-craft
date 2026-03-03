<template>
  <AppLayout>
    <div class="template-editor">
      <div class="container">
        <div class="header">
          <h1 class="title">
            <el-icon class="title-icon"><DocumentAdd /></el-icon>
            提示词模板编辑器
          </h1>
          <p class="subtitle">创建可复用的提示词模板，支持变量替换和实时预览</p>
        </div>

        <!-- 模板列表 -->
        <el-card v-if="showList" class="list-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <span>我的模板</span>
              <el-button type="primary" @click="createNew">
                新建模板
              </el-button>
            </div>
          </template>
          
          <el-empty v-if="templates.length === 0" description="暂无模板，点击上方按钮创建第一个模板" />
          
          <div v-else class="template-grid">
            <div 
              v-for="template in templates" 
              :key="template.id" 
              class="template-item"
              @click="editTemplate(template)"
            >
              <div class="template-icon">
                <el-icon :size="32"><Document /></el-icon>
              </div>
              <div class="template-info">
                <h3>{{ template.name || '未命名模板' }}</h3>
                <p>{{ template.description || '无描述' }}</p>
                <div class="template-meta">
                  <el-tag size="small">{{ template.variables?.length || 0 }} 个变量</el-tag>
                  <span class="template-date">{{ formatDate(template.updatedAt) }}</span>
                </div>
              </div>
              <div class="template-actions" @click.stop>
                <el-button size="small" type="primary" @click="useTemplate(template)">
                  使用
                </el-button>
                <el-button size="small" type="danger" @click="deleteTemplate(template.id)">
                  删除
                </el-button>
              </div>
            </div>
          </div>
        </el-card>

        <!-- 编辑器 -->
        <div v-else class="editor-area">
          <el-card class="editor-card" shadow="hover">
            <template #header>
              <div class="card-header">
                <el-button text @click="backToList">
                  返回列表
                </el-button>
                <span>{{ isEditing ? '编辑模板' : '新建模板' }}</span>
                <div class="header-actions">
                  <el-button type="success" @click="saveTemplate">
                    保存模板
                  </el-button>
                </div>
              </div>
            </template>

            <el-form :model="currentTemplate" label-width="100px">
              <el-form-item label="模板名称">
                <el-input v-model="currentTemplate.name" placeholder="请输入模板名称" />
              </el-form-item>
              <el-form-item label="模板描述">
                <el-input v-model="currentTemplate.description" type="textarea" :rows="2" placeholder="简短描述模板用途" />
              </el-form-item>
              <el-form-item label="模板分类">
                <el-select v-model="currentTemplate.category" placeholder="选择分类">
                  <el-option label="通用" value="general" />
                  <el-option label="写作" value="writing" />
                  <el-option label="编程" value="coding" />
                  <el-option label="数据分析" value="analysis" />
                  <el-option label="营销" value="marketing" />
                  <el-option label="教育" value="education" />
                </el-select>
              </el-form-item>
            </el-form>

            <el-row :gutter="24">
              <!-- 模板内容 -->
              <el-col :span="12">
                <el-card class="content-card" shadow="never">
                  <template #header>
                    <div class="card-header">
                      <span>模板内容</span>
                      <el-button text type="primary" size="small" @click="insertVariable">
                        插入变量
                      </el-button>
                    </div>
                  </template>
                  <el-input
                    v-model="currentTemplate.content"
                    type="textarea"
                    :rows="15"
                    placeholder="在此编写模板内容...

使用 {{变量名}} 格式定义变量
例如：请为{{公司名称}}撰写一篇关于{{主题}}的文章"
                    @input="extractVariablesFromTemplate"
                  />
                  <div class="format-tips">
                    <el-tag size="small" type="info">变量格式: {{变量名}}</el-tag>
                  </div>
                </el-card>
              </el-col>

              <!-- 变量配置 + 预览 -->
              <el-col :span="12">
                <el-card class="variable-card" shadow="never">
                  <template #header>
                    <div class="card-header">
                      <span>变量配置</span>
                      <el-tag v-if="extractedVars.length > 0" type="success" size="small">
                        {{ extractedVars.length }} 个变量
                      </el-tag>
                    </div>
                  </template>
                  
                  <div v-if="extractedVars.length > 0" class="var-config">
                    <div 
                      v-for="(v, idx) in extractedVars" 
                      :key="idx" 
                      class="var-item"
                    >
                      <el-input 
                        v-model="v.name" 
                        placeholder="变量名" 
                        size="small" 
                        style="width: 120px"
                      />
                      <el-select 
                        v-model="v.type" 
                        placeholder="类型" 
                        size="small" 
                        style="width: 100px"
                      >
                        <el-option label="文本" value="text" />
                        <el-option label="数字" value="number" />
                        <el-option label="选择" value="select" />
                      </el-select>
                      <el-button size="small" type="danger" text @click="removeVar(idx)">
                        <el-icon><Delete /></el-icon>
                      </el-button>
                    </div>
                  </div>
                  <el-empty v-else description="在模板内容中输入 {{变量名}} 自动识别" :image-size="60" />
                </el-card>

                <!-- 预览 -->
                <el-card class="preview-card" shadow="never" style="margin-top: 16px;">
                  <template #header>
                    <div class="card-header">
                      <span>效果预览</span>
                      <el-button text type="primary" size="small" @click="loadExample">
                        示例数据
                      </el-button>
                    </div>
                  </template>
                  
                  <div v-if="extractedVars.length > 0" class="preview-form">
                    <el-form size="small" label-width="80px">
                      <el-form-item 
                        v-for="(v, idx) in extractedVars" 
                        :key="idx" 
                        :label="v.name"
                      >
                        <el-input 
                          v-model="previewValues[v.name]" 
                          :placeholder="'请输入' + v.name" 
                        />
                      </el-form-item>
                    </el-form>
                    <el-button type="primary" size="small" @click="renderPreview" style="margin-top: 12px;">
                      预览结果
                    </el-button>
                  </div>
                  
                  <div v-if="previewResult" class="preview-result">
                    <el-divider content-position="left">填充后</el-divider>
                    <div class="result-content">{{ previewResult }}</div>
                    <el-button type="primary" size="small" @click="copyPreview" style="margin-top: 12px;">
                      复制
                    </el-button>
                  </div>
                </el-card>
              </el-col>
            </el-row>
          </el-card>
        </div>

        <!-- 使用模板对话框 -->
        <el-dialog v-model="showUseDialog" title="使用模板" width="600px">
          <div v-if="templateToUse">
            <h3>{{ templateToUse.name }}</h3>
            <p>{{ templateToUse.description }}</p>
          </div>
          <template #footer>
            <el-button @click="showUseDialog = false">取消</el-button>
            <el-button type="primary" @click="generateFromTemplate">生成提示词</el-button>
          </template>
        </el-dialog>
      </div>
    </div>
  </AppLayout>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { DocumentAdd, Document, Delete } from '@element-plus/icons-vue'
import AppLayout from '../components/layout/AppLayout.vue'

interface TemplateVariable {
  name: string
  type: string
  options?: string
}

interface Template {
  id: string
  name: string
  description: string
  content: string
  category: string
  variables: TemplateVariable[]
  createdAt: number
  updatedAt: number
}

const showList = ref(true)
const isEditing = ref(false)
const templates = ref<Template[]>([])
const currentTemplate = ref<Template>({
  id: '',
  name: '',
  description: '',
  content: '',
  category: 'general',
  variables: [],
  createdAt: 0,
  updatedAt: 0
})
const extractedVars = ref<TemplateVariable[]>([])
const previewValues = ref<Record<string, string>>({})
const previewResult = ref('')
const showUseDialog = ref(false)
const templateToUse = ref<Template | null>(null)
const useValues = ref<Record<string, string>>({})

const loadTemplates = () => {
  const saved = localStorage.getItem('prompt-templates')
  if (saved) {
    try {
      templates.value = JSON.parse(saved)
    } catch {
      templates.value = []
    }
  }
}

const saveTemplates = () => {
  localStorage.setItem('prompt-templates', JSON.stringify(templates.value))
}

const createNew = () => {
  currentTemplate.value = {
    id: Date.now().toString(),
    name: '',
    description: '',
    content: '',
    category: 'general',
    variables: [],
    createdAt: Date.now(),
    updatedAt: Date.now()
  }
  extractedVars.value = []
  previewValues.value = {}
  previewResult.value = ''
  showList.value = false
  isEditing.value = false
}

const editTemplate = (template: Template) => {
  currentTemplate.value = { ...template, updatedAt: Date.now() }
  extractedVars.value = [...(template.variables || [])]
  previewValues.value = {}
  previewResult.value = ''
  showList.value = false
  isEditing.value = true
}

const backToList = () => {
  showList.value = true
  loadTemplates()
}

const saveTemplate = () => {
  if (!currentTemplate.value.name.trim()) {
    ElMessage.warning('请输入模板名称')
    return
  }
  if (!currentTemplate.value.content.trim()) {
    ElMessage.warning('请输入模板内容')
    return
  }
  
  currentTemplate.value.variables = extractedVars.value
  currentTemplate.value.updatedAt = Date.now()
  
  const idx = templates.value.findIndex(t => t.id === currentTemplate.value.id)
  if (idx >= 0) {
    templates.value[idx] = { ...currentTemplate.value }
  } else {
    currentTemplate.value.createdAt = Date.now()
    templates.value.push({ ...currentTemplate.value })
  }
  
  saveTemplates()
  ElMessage.success('模板保存成功')
  showList.value = true
}

const deleteTemplate = async (id: string) => {
  try {
    await ElMessageBox.confirm('确定要删除这个模板吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    templates.value = templates.value.filter(t => t.id !== id)
    saveTemplates()
    ElMessage.success('删除成功')
  } catch {}
}

const useTemplate = (template: Template) => {
  templateToUse.value = template
  useValues.value = {}
  if (template.variables) {
    template.variables.forEach(v => {
      useValues.value[v.name] = ''
    })
  }
  showUseDialog.value = true
}

const generateFromTemplate = () => {
  if (!templateToUse.value) return
  
  let result = templateToUse.value.content
  templateToUse.value.variables?.forEach(v => {
    const value = useValues.value[v.name] || ''
    result = result.replace(new RegExp(`{{${v.name}}}`, 'g'), value)
  })
  
  navigator.clipboard.writeText(result).then(() => {
    ElMessage.success('已复制到剪贴板')
    showUseDialog.value = false
  })
}

const extractVariablesFromTemplate = () => {
  const content = currentTemplate.value.content
  const regex = /\{\{([^}]+)\}\}/g
  const vars: TemplateVariable[] = []
  const seen = new Set<string>()
  
  let match
  while ((match = regex.exec(content)) !== null) {
    const name = match[1].trim()
    if (!seen.has(name)) {
      seen.add(name)
      const existing = extractedVars.value.find(v => v.name === name)
      vars.push(existing || { name, type: 'text' })
    }
  }
  
  extractedVars.value = vars
  const newPreviewValues: Record<string, string> = {}
  vars.forEach(v => {
    if (previewValues.value[v.name]) {
      newPreviewValues[v.name] = previewValues.value[v.name]
    }
  })
  previewValues.value = newPreviewValues
}

const removeVar = (idx: number) => {
  extractedVars.value.splice(idx, 1)
}

const insertVariable = () => {
  const varName = '变量' + (extractedVars.value.length + 1)
  currentTemplate.value.content += `{{${varName}}}`
  extractVariablesFromTemplate()
}

const loadExample = () => {
  currentTemplate.value = {
    id: Date.now().toString(),
    name: '产品介绍生成器',
    description: '根据产品信息生成专业的产品介绍文案',
    content: `请为 {{产品名称}} 撰写一篇专业的产品介绍文案。

产品特点：
- {{特点1}}
- {{特点2}}
- {{特点3}}

目标受众：{{目标人群}}
文案风格：{{风格要求}}

要求：
1. 突出产品核心优势
2. 语言简洁有力
3. 包含行动号召`,
    category: 'marketing',
    variables: [],
    createdAt: Date.now(),
    updatedAt: Date.now()
  }
  extractedVars.value = [
    { name: '产品名称', type: 'text' },
    { name: '特点1', type: 'text' },
    { name: '特点2', type: 'text' },
    { name: '特点3', type: 'text' },
    { name: '目标人群', type: 'select', options: '企业用户,个人用户,年轻群体,中年群体' },
    { name: '风格要求', type: 'select', options: '专业正式,轻松活泼,高端大气,亲民实用' }
  ]
  previewValues.value = {
    '产品名称': '智能助手',
    '特点1': 'AI驱动',
    '特点2': '高效便捷',
    '特点3': '安全可靠',
    '目标人群': '企业用户',
    '风格要求': '专业正式'
  }
  extractVariablesFromTemplate()
}

const renderPreview = () => {
  let result = currentTemplate.value.content
  extractedVars.value.forEach(v => {
    const value = previewValues.value[v.name] || `[${v.name}]`
    result = result.replace(new RegExp(`\\{\\{${v.name}\\}\\}`, 'g'), value)
  })
  previewResult.value = result
}

const copyPreview = () => {
  if (previewResult.value) {
    navigator.clipboard.writeText(previewResult.value)
    ElMessage.success('已复制到剪贴板')
  }
}

const formatDate = (timestamp: number) => {
  if (!timestamp) return ''
  const date = new Date(timestamp)
  return `${date.getMonth() + 1}-${date.getDate()}`
}

onMounted(() => {
  loadTemplates()
})
</script>

<style scoped>
.template-editor {
  min-height: 100vh;
  background: linear-gradient(135deg, #f5f7fa 0%, #e4e8ec 100%);
  padding: 24px;
}

.container {
  max-width: 1400px;
  margin: 0 auto;
}

.header {
  text-align: center;
  margin-bottom: 32px;
}

.title {
  font-size: 28px;
  font-weight: 600;
  color: #303133;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  margin-bottom: 8px;
}

.title-icon {
  color: #409eff;
}

.subtitle {
  color: #909399;
  font-size: 14px;
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.template-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 16px;
}

.template-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px;
  background: #fafafa;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
  border: 1px solid transparent;
}

.template-item:hover {
  background: #fff;
  border-color: #409eff;
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.15);
}

.template-icon {
  width: 56px;
  height: 56px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #3b82f6 0%, #1d4ed8 100%);
  border-radius: 12px;
  color: #fff;
}

.template-info {
  flex: 1;
  min-width: 0;
}

.template-info h3 {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  margin: 0 0 4px 0;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.template-info p {
  font-size: 13px;
  color: #909399;
  margin: 0 0 8px 0;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.template-meta {
  display: flex;
  align-items: center;
  gap: 12px;
}

.template-date {
  font-size: 12px;
  color: #c0c4cc;
}

.template-actions {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.editor-area {
  animation: fadeIn 0.3s ease;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}

.format-tips {
  margin-top: 12px;
}

.var-config {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.var-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px;
  background: #f5f7fa;
  border-radius: 6px;
}

.preview-form {
  margin-bottom: 16px;
}

.preview-result {
  margin-top: 16px;
}

.result-content {
  padding: 16px;
  background: #f0f9ff;
  border: 1px solid #91d5ff;
  border-radius: 6px;
  font-size: 14px;
  line-height: 1.8;
  white-space: pre-wrap;
  max-height: 200px;
  overflow-y: auto;
}
</style>
