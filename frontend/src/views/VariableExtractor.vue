<template>
  <div class="variable-extractor">
    <div class="container">
      <div class="header">
        <h1 class="title">
          <el-icon class="title-icon"><EditPen /></el-icon>
          提示词变量提取器
        </h1>
        <p class="subtitle">自动识别提示词中的变量，生成动态表单快速填充</p>
      </div>

      <el-row :gutter="24">
        <!-- 左侧：输入区域 -->
        <el-col :span="12">
          <el-card class="input-card" shadow="hover">
            <template #header>
              <div class="card-header">
                <span>原始提示词</span>
                <el-button text type="primary" @click="loadExample">
                  加载示例
                </el-button>
              </div>
            </template>
            <el-input
              v-model="rawPrompt"
              type="textarea"
              :rows="12"
              placeholder="请输入包含变量的提示词...

支持的变量格式：
- {变量名} 或 {{变量名}}
- [变量名]
- &lt;变量名&gt;"
              show-word-limit
              @input="extractVariables"
            />
            <div class="format-tips">
              <el-tag size="small" type="info">支持格式: {var} {{var}} [var] &lt;var&gt;</el-tag>
            </div>
          </el-card>
        </el-col>

        <!-- 右侧：变量表单 -->
        <el-col :span="12">
          <el-card class="variable-card" shadow="hover">
            <template #header>
              <div class="card-header">
                <span>变量填写</span>
                <el-tag v-if="variables.length > 0" type="success">
                  已识别 {{ variables.length }} 个变量
                </el-tag>
                <el-tag v-else type="info">无变量</el-tag>
              </div>
            </template>
            
            <div v-if="variables.length > 0" class="variable-form">
              <el-form label-width="100px" size="default">
                <el-form-item
                  v-for="(variable, index) in variables"
                  :key="index"
                  :label="variable.name"
                >
                  <el-input
                    v-model="variableValues[variable.name]"
                    :placeholder="'请输入' + variable.name"
                    @input="updatePreview"
                  >
                    <template #prefix>
                      <el-tag size="small" :type="getVarTypeTag(variable.type)">
                        {{ variable.type }}
                      </el-tag>
                    </template>
                  </el-input>
                </el-form-item>
              </el-form>
              
              <div class="variable-actions">
                <el-button type="primary" @click="fillAllRandom" :icon="Refresh">
                  随机填充
                </el-button>
                <el-button @click="clearAllValues" :icon="Delete">
                  清空
                </el-button>
              </div>
            </div>
            
            <el-empty v-else description="未识别到变量，请在左侧输入包含变量的提示词" />
          </el-card>
        </el-col>
      </el-row>

      <!-- 预览区域 -->
      <el-card v-if="variables.length > 0" class="preview-card" shadow="hover">
        <template #header>
          <div class="card-header">
            <span>实时预览</span>
            <div class="preview-actions">
              <el-button type="primary" size="small" @click="copyResult" :icon="CopyDocument">
                复制结果
              </el-button>
              <el-dropdown trigger="click" @command="handleExport">
                <el-button size="small" :icon="Download">
                  导出
                </el-button>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item command="txt">📄 纯文本 (.txt)</el-dropdown-item>
                    <el-dropdown-item command="md">📝 Markdown (.md)</el-dropdown-item>
                    <el-dropdown-item command="json">{} JSON (.json)</el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </div>
          </div>
        </template>
        
        <div class="preview-content">
          <pre>{{ filledPrompt || '填写变量后预览结果...' }}</pre>
        </div>
        
        <div v-if="copySuccess" class="copy-success">
          <el-icon><CircleCheck /></el-icon> 已复制到剪贴板
        </div>
      </el-card>

      <!-- 变量统计 -->
      <el-card v-if="variables.length > 0" class="stats-card" shadow="hover">
        <template #header>
          <span>变量分析</span>
        </template>
        <el-row :gutter="20">
          <el-col :span="6">
            <div class="stat-item">
              <div class="stat-value">{{ variables.length }}</div>
              <div class="stat-label">变量数量</div>
            </div>
          </el-col>
          <el-col :span="6">
            <div class="stat-item">
              <div class="stat-value">{{ filledCount }}</div>
              <div class="stat-label">已填充</div>
            </div>
          </el-col>
          <el-col :span="6">
            <div class="stat-item">
              <div class="stat-value">{{ variables.length - filledCount }}</div>
              <div class="stat-label">待填充</div>
            </div>
          </el-col>
          <el-col :span="6">
            <div class="stat-item">
              <div class="stat-value">{{ fillProgress }}%</div>
              <div class="stat-label">完成进度</div>
            </div>
          </el-col>
        </el-row>
        
        <el-progress 
          :percentage="fillProgress" 
          :status="fillProgress === 100 ? 'success' : ''"
          :stroke-width="8"
          class="fill-progress"
        />
      </el-card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { 
  EditPen, 
  Refresh, 
  Delete, 
  CopyDocument, 
  Download, 
  CircleCheck 
} from '@element-plus/icons-vue'

interface Variable {
  name: string
  type: string
  original: string
}

const rawPrompt = ref('')
const variables = ref<Variable[]>([])
const variableValues = ref<Record<string, string>>({})
const filledPrompt = ref('')
const copySuccess = ref(false)

// 示例提示词
const examplePrompt = `你是一位专业的{{职业}}，擅长{{专业领域}}。

请根据以下信息为客户撰写一份{{文档类型}}：

客户姓名：{{客户姓名}}
目标受众：{{目标受众}}
字数要求：{{字数}}字以内
特殊要求：{{特殊要求}}

请确保内容{{风格要求}}，并且包含实用的{{内容要点}}。`

// 加载示例
const loadExample = () => {
  rawPrompt.value = examplePrompt
  extractVariables()
}

// 提取变量
const extractVariables = () => {
  const text = rawPrompt.value
  const varMap = new Map<string, Variable>()
  
  // 匹配各种格式的变量
  const patterns = [
    /\{\{([^}]+)\}\}/g,    // {{var}}
    /\{([^}]+)\}/g,        // {var}
    /\[([^\]]+)\]/g,       // [var]
    /<([^>]+)>/g           // <var>
  ]
  
  patterns.forEach(pattern => {
    let match
    while ((match = pattern.exec(text)) !== null) {
      const name = match[1].trim()
      const original = match[0]
      if (!varMap.has(name)) {
        varMap.set(name, {
          name,
          type: inferType(name),
          original
        })
      }
    }
  })
  
  variables.value = Array.from(varMap.values())
  
  // 初始化变量值
  variables.value.forEach(v => {
    if (!(v.name in variableValues.value)) {
      variableValues.value[v.name] = ''
    }
  })
  
  updatePreview()
}

// 推断变量类型
const inferType = (name: string): string => {
  const lowerName = name.toLowerCase()
  if (lowerName.includes('name') || lowerName.includes('姓名') || lowerName.includes('名')) return '姓名'
  if (lowerName.includes('desc') || lowerName.includes('描述') || lowerName.includes('说明')) return '描述'
  if (lowerName.includes('num') || lowerName.includes('count') || lowerName.includes('数量') || lowerName.includes('字')) return '数字'
  if (lowerName.includes('date') || lowerName.includes('日期') || lowerName.includes('时间')) return '日期'
  if (lowerName.includes('url') || lowerName.includes('链接') || lowerName.includes('网址')) return '链接'
  if (lowerName.includes('email') || lowerName.includes('邮件')) return '邮箱'
  if (lowerName.includes('phone') || lowerName.includes('电话') || lowerName.includes('手机')) return '电话'
  if (lowerName.includes('content') || lowerName.includes('内容') || lowerName.includes('正文')) return '内容'
  if (lowerName.includes('type') || lowerName.includes('类型') || lowerName.includes('类别')) return '类型'
  if (lowerName.includes('role') || lowerName.includes('职业') || lowerName.includes('身份')) return '角色'
  if (lowerName.includes('field') || lowerName.includes('领域') || lowerName.includes('专业')) return '领域'
  if (lowerName.includes('requirement') || lowerName.includes('要求') || lowerName.includes('需求')) return '要求'
  if (lowerName.includes('style') || lowerName.includes('风格') || lowerName.includes('语气')) return '风格'
  return '文本'
}

// 获取变量类型标签
const getVarTypeTag = (type: string): string => {
  const tagMap: Record<string, string> = {
    '姓名': 'success',
    '描述': 'warning',
    '数字': 'danger',
    '日期': 'info',
    '链接': 'primary',
    '邮箱': 'primary',
    '电话': 'danger',
    '内容': 'success',
    '类型': 'warning',
    '角色': 'success',
    '领域': 'info',
    '要求': 'warning',
    '风格': 'success',
    '文本': 'info'
  }
  return tagMap[type] || 'info'
}

// 更新预览
const updatePreview = () => {
  let result = rawPrompt.value
  
  variables.value.forEach(v => {
    const value = variableValues.value[v.name] || `[${v.name}]`
    // 替换所有出现的目标变量
    const regex = new RegExp(v.original.replace(/[{}[\]<>]/g, '\\$&'), 'g')
    result = result.replace(regex, value)
  })
  
  filledPrompt.value = result
}

// 随机填充
const fillAllRandom = () => {
  const randomValues: Record<string, string[]> = {
    '职业': ['律师', '医生', '教师', '工程师', '设计师', '作家', '记者', '顾问'],
    '专业领域': ['金融', '医疗', '教育', '科技', '法律', '营销', '人力资源', '项目管理'],
    '文档类型': ['商业计划书', '研究报告', '产品说明书', '合同', '简历', '推荐信', '新闻稿', '分析报告'],
    '客户姓名': ['张三', '李四', '王五', '赵六', '孙七', '周八', '吴九', '郑十'],
    '目标受众': ['企业客户', '个人用户', '学生群体', '专业人士', '普通消费者', '投资人', '合作伙伴'],
    '字数': ['500', '800', '1000', '1500', '2000', '3000'],
    '特殊要求': ['简洁明了', '专业严谨', '通俗易懂', '图文并茂', '数据详实', '案例丰富'],
    '风格要求': ['专业正式', '轻松活泼', '亲切友好', '权威专业', '简洁清晰', '富有创意'],
    '内容要点': ['核心观点', '数据支持', '案例分析', '行动建议', '风险提示', '解决方案'],
    '描述': ['简洁描述', '详细说明', '全面概述', '重点突出', '逻辑清晰'],
    '类型': ['技术', '创意', '商业', '学术', '生活', '娱乐'],
    '角色': ['专家', '顾问', '导师', '教练', '助手', '评论员'],
    '领域': ['人工智能', '市场营销', '财务管理', '人力资源', '产品设计', '客户服务'],
    '要求': ['准确', '完整', '实用', '创新', '合规', '高效'],
    '文本': ['示例文本', '待填充内容', '请输入内容']
  }
  
  variables.value.forEach(v => {
    const options = randomValues[v.name] || ['示例' + v.name, v.name + '值', '请填写' + v.name]
    variableValues.value[v.name] = options[Math.floor(Math.random() * options.length)]
  })
  
  updatePreview()
  ElMessage.success('已随机填充所有变量')
}

// 清空所有值
const clearAllValues = () => {
  Object.keys(variableValues.value).forEach(key => {
    variableValues.value[key] = ''
  })
  updatePreview()
}

// 复制结果
const copyResult = async () => {
  if (!filledPrompt.value) return
  
  try {
    await navigator.clipboard.writeText(filledPrompt.value)
    copySuccess.value = true
    ElMessage.success('已复制到剪贴板')
    setTimeout(() => {
      copySuccess.value = false
    }, 2000)
  } catch {
    ElMessage.error('复制失败，请手动复制')
  }
}

// 导出结果
const handleExport = (format) => {
  if (!filledPrompt.value) return
  
  const timestamp = new Date().toISOString().slice(0, 10)
  let content = ''
  let filename = `prompt-filled-${timestamp}`
  let mimeType = 'text/plain'
  
  if (format === 'md') {
    content = `# 填充后的提示词\n\n> 生成时间: ${new Date().toLocaleString('zh-CN')}\n\n## 原始提示词\n\n${rawPrompt.value}\n\n## 填充结果\n\n${filledPrompt.value}\n\n---\n*由 Prompt Flow Craft 生成*`
    filename += '.md'
    mimeType = 'text/markdown'
  } else if (format === 'json') {
    const exportData = {
      originalPrompt: rawPrompt.value,
      filledPrompt: filledPrompt.value,
      variables: variables.value.map(v => ({
        name: v.name,
        type: v.type,
        value: variableValues.value[v.name] || ''
      })),
      generatedAt: new Date().toISOString()
    }
    content = JSON.stringify(exportData, null, 2)
    filename += '.json'
    mimeType = 'application/json'
  } else {
    content = filledPrompt.value
    filename += '.txt'
  }
  
  const blob = new Blob([content], { type: mimeType + ';charset=utf-8' })
  const url = URL.createObjectURL(blob)
  const a = document.createElement('a')
  a.href = url
  a.download = filename
  a.click()
  URL.revokeObjectURL(url)
  ElMessage.success(`已导出为 ${format.toUpperCase()} 格式`)
}

// 计算属性
const filledCount = computed(() => {
  return Object.values(variableValues.value).filter(v => v.trim() !== '').length
})

const fillProgress = computed(() => {
  if (variables.value.length === 0) return 0
  return Math.round((filledCount.value / variables.value.length) * 100)
})
</script>

<style scoped>
.variable-extractor {
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

.input-card,
.variable-card,
.preview-card,
.stats-card {
  margin-bottom: 24px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.format-tips {
  margin-top: 12px;
}

.variable-form {
  max-height: 400px;
  overflow-y: auto;
}

.variable-actions {
  display: flex;
  gap: 12px;
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #eee;
}

.preview-content {
  background: #f5f7fa;
  border-radius: 8px;
  padding: 16px;
  min-height: 150px;
}

.preview-content pre {
  white-space: pre-wrap;
  word-wrap: break-word;
  font-family: 'Consolas', 'Monaco', monospace;
  font-size: 14px;
  line-height: 1.6;
  margin: 0;
}

.preview-actions {
  display: flex;
  gap: 8px;
}

.copy-success {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #67c23a;
  margin-top: 12px;
  font-size: 14px;
}

.stats-card .stat-item {
  text-align: center;
  padding: 16px;
}

.stats-card .stat-value {
  font-size: 32px;
  font-weight: bold;
  color: #409eff;
}

.stats-card .stat-label {
  font-size: 14px;
  color: #909399;
  margin-top: 8px;
}

.fill-progress {
  margin-top: 20px;
}

:deep(.el-card) {
  border-radius: 12px;
}

:deep(.el-card__header) {
  border-bottom: 1px solid #eee;
  font-weight: 600;
}
</style>
