<template>
  <AppLayout>
    <template #sidebar-left>
      <div class="templates-sidebar">
        <div class="sidebar-section">
          <h4>📚 提示词模板</h4>
          <div class="template-list">
            <div 
              v-for="category in categories" 
              :key="category.name"
              class="template-category"
            >
              <div class="category-header" @click="toggleCategory(category.name)">
                <span class="category-icon">{{ category.icon }}</span>
                <span class="category-name">{{ category.name }}</span>
                <span class="category-arrow">{{ expandedCategories.includes(category.name) ? '▼' : '▶' }}</span>
              </div>
              <div v-if="expandedCategories.includes(category.name)" class="category-items">
                <div 
                  v-for="template in category.templates" 
                  :key="template.title"
                  class="template-item"
                  @click="selectTemplate(template)"
                >
                  <span class="template-title">{{ template.title }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </template>

    <template #main>
      <div class="templates-page">
        <div class="header">
          <h1 class="title">
            <span class="title-icon">📚</span>
            提示词模板大全
          </h1>
          <p class="subtitle">点击任意模板快速使用，一键生成专业提示词</p>
        </div>

        <!-- 搜索框 -->
        <div class="search-box">
          <input 
            v-model="searchKeyword" 
            type="text" 
            placeholder="搜索提示词模板..."
            class="search-input"
          />
        </div>

        <!-- 分类展示 -->
        <div class="templates-grid">
          <div 
            v-for="category in filteredCategories" 
            :key="category.name"
            class="category-card"
          >
            <div class="card-header">
              <span class="card-icon">{{ category.icon }}</span>
              <h3>{{ category.name }}</h3>
            </div>
            <div class="card-items">
              <div 
                v-for="template in category.templates" 
                :key="template.title"
                class="template-card"
                @click="selectTemplate(template)"
              >
                <div class="template-card-title">{{ template.title }}</div>
                <div class="template-card-desc">{{ template.description }}</div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </template>
  </AppLayout>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import AppLayout from '../components/layout/AppLayout.vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const searchKeyword = ref('')
const expandedCategories = ref(['写作', '编程'])

const categories = ref([
  {
    name: '写作',
    icon: '✍️',
    templates: [
      { title: '文章润色', description: '润色文章，提升表达', task: '请帮我润色以下文章，提升语言表达能力和逻辑连贯性', audience: 'general', output: 'text', tone: 'friendly', length: 'medium' },
      { title: '标题生成', description: '生成吸引人的标题', task: '为以下内容生成5个吸引人的标题，要求简洁有力', audience: 'general', output: 'list', tone: 'formal', length: 'short' },
      { title: '文案创作', description: '生成营销文案', task: '请根据产品特点创作一段营销文案', audience: 'professional', output: 'text', tone: 'friendly', length: 'medium' },
      { title: '摘要总结', description: '提取文章核心要点', task: '请提取以下文章的核心要点，生成简洁摘要', audience: 'general', output: 'list', tone: 'formal', length: 'short' },
    ]
  },
  {
    name: '编程',
    icon: '💻',
    templates: [
      { title: '代码解释', description: '解释代码功能', task: '请详细解释以下代码的功能和工作原理', audience: 'developer', output: 'text', tone: 'formal', length: 'medium' },
      { title: '代码审查', description: '代码优化建议', task: '请审查以下代码，提出优化建议和潜在问题', audience: 'developer', output: 'text', tone: 'formal', length: 'medium' },
      { title: 'Bug修复', description: '定位并修复Bug', task: '请分析以下代码中的bug并提供修复方案', audience: 'developer', output: 'code', tone: 'formal', length: 'medium' },
      { title: '文档生成', description: '生成代码文档', task: '请为以下代码生成详细的API文档', audience: 'developer', output: 'text', tone: 'formal', length: 'long' },
    ]
  },
  {
    name: '数据分析',
    icon: '📊',
    templates: [
      { title: '数据分析', description: '分析数据趋势', task: '请分析以下数据，找出趋势和规律', audience: 'professional', output: 'text', tone: 'formal', length: 'medium' },
      { title: '可视化建议', description: '推荐图表类型', task: '根据以下数据特点，推荐合适的可视化方式', audience: 'professional', output: 'list', tone: 'formal', length: 'short' },
      { title: '报告撰写', description: '生成数据分析报告', task: '请根据以下数据生成一份专业的数据分析报告', audience: 'professional', output: 'text', tone: 'formal', length: 'long' },
    ]
  },
  {
    name: '设计',
    icon: '🎨',
    templates: [
      { title: 'UI设计建议', description: 'UI优化建议', task: '请为以下界面设计提供优化建议', audience: 'professional', output: 'list', tone: 'formal', length: 'medium' },
      { title: '配色方案', description: '推荐配色方案', task: '请为以下场景推荐合适的配色方案', audience: 'creator', output: 'list', tone: 'friendly', length: 'short' },
      { title: '图标设计', description: '图标设计指导', task: '请为以下场景设计简洁的图标概念', audience: 'creator', output: 'text', tone: 'friendly', length: 'short' },
    ]
  },
  {
    name: '商业',
    icon: '💼',
    templates: [
      { title: '商业计划书', description: '生成商业计划', task: '请帮我生成一份商业计划书框架', audience: 'professional', output: 'text', tone: 'formal', length: 'long' },
      { title: 'SWOT分析', description: 'SWOT分析模板', task: '请对以下产品/公司进行SWOT分析', audience: 'professional', output: 'table', tone: 'formal', length: 'medium' },
      { title: '邮件撰写', description: '商务邮件模板', task: '请生成一封商务合作邮件', audience: 'professional', output: 'text', tone: 'formal', length: 'medium' },
    ]
  },
  {
    name: '教育',
    icon: '📚',
    templates: [
      { title: '教案生成', description: '生成教学教案', task: '请为以下知识点生成一份详细的教案', audience: 'teacher', output: 'text', tone: 'formal', length: 'long' },
      { title: '试题生成', description: '生成练习题', task: '请根据以下知识点生成一套练习题', audience: 'teacher', output: 'list', tone: 'formal', length: 'medium' },
      { title: '学习计划', description: '制定学习计划', task: '请为以下学习目标制定详细的学习计划', audience: 'student', output: 'list', tone: 'friendly', length: 'medium' },
    ]
  },
  {
    name: '翻译',
    icon: '🌍',
    templates: [
      { title: '中英翻译', description: '中英文互译', task: '请翻译以下内容，保持原文风格', audience: 'general', output: 'text', tone: 'formal', length: 'medium' },
      { title: '口语化翻译', description: '翻译成口语', task: '请将以下内容翻译成地道的口语表达', audience: 'general', output: 'text', tone: 'friendly', length: 'short' },
      { title: '专业术语', description: '专业术语翻译', task: '请翻译以下专业内容，注意术语准确性', audience: 'professional', output: 'text', tone: 'formal', length: 'medium' },
    ]
  },
])

const filteredCategories = computed(() => {
  if (!searchKeyword.value) return categories.value
  const keyword = searchKeyword.value.toLowerCase()
  return categories.value.map(cat => ({
    ...cat,
    templates: cat.templates.filter(t => 
      t.title.toLowerCase().includes(keyword) || 
      t.description.toLowerCase().includes(keyword)
    )
  })).filter(cat => cat.templates.length > 0)
})

const toggleCategory = (name: string) => {
  const idx = expandedCategories.value.indexOf(name)
  if (idx > -1) {
    expandedCategories.value.splice(idx, 1)
  } else {
    expandedCategories.value.push(name)
  }
}

const selectTemplate = (template: any) => {
  // 跳转到首页并填充表单
  router.push({
    path: '/',
    query: {
      task: template.task,
      audience: template.audience,
      output: template.output,
      tone: template.tone,
      length: template.length
    }
  })
}
</script>

<style scoped>
.templates-sidebar {
  padding: 16px;
}

.sidebar-section h4 {
  font-size: 14px;
  font-weight: 600;
  color: #475569;
  margin-bottom: 16px;
}

.template-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.template-category {
  border-radius: 8px;
  overflow: hidden;
}

.category-header {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 12px;
  background: #f8fafc;
  cursor: pointer;
  border-radius: 8px;
  transition: all 0.2s;
}

.category-header:hover {
  background: #f1f5f9;
}

.category-icon {
  font-size: 16px;
}

.category-name {
  flex: 1;
  font-size: 14px;
  font-weight: 500;
  color: #334155;
}

.category-arrow {
  font-size: 10px;
  color: #94a3b8;
}

.category-items {
  padding: 8px 0 8px 20px;
}

.template-item {
  padding: 8px 12px;
  cursor: pointer;
  border-radius: 6px;
  transition: all 0.2s;
}

.template-item:hover {
  background: #f1f5f9;
}

.template-title {
  font-size: 13px;
  color: #64748b;
}

.templates-page {
  padding: 40px;
  max-width: 1200px;
  margin: 0 auto;
}

.header {
  text-align: center;
  margin-bottom: 40px;
}

.title {
  font-size: 2.5rem;
  font-weight: 700;
  background: linear-gradient(135deg, #1e293b 0%, #3b82f6 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 16px;
  margin-bottom: 12px;
}

.title-icon {
  font-size: 2.5rem;
}

.subtitle {
  font-size: 1.1rem;
  color: #64748b;
}

.search-box {
  max-width: 500px;
  margin: 0 auto 40px;
}

.search-input {
  width: 100%;
  padding: 16px 24px;
  font-size: 16px;
  border: 2px solid #e2e8f0;
  border-radius: 16px;
  outline: none;
  transition: all 0.3s;
}

.search-input:focus {
  border-color: #3b82f6;
  box-shadow: 0 0 0 4px rgba(59, 130, 246, 0.1);
}

.templates-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: 24px;
}

.category-card {
  background: white;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.06);
  transition: all 0.3s;
}

.category-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
}

.card-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 20px;
  padding-bottom: 16px;
  border-bottom: 1px solid #f1f5f9;
}

.card-icon {
  font-size: 28px;
}

.card-header h3 {
  font-size: 1.25rem;
  font-weight: 600;
  color: #1e293b;
}

.card-items {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.template-card {
  padding: 16px;
  background: #f8fafc;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.2s;
}

.template-card:hover {
  background: #f1f5f9;
  transform: translateX(4px);
}

.template-card-title {
  font-weight: 600;
  color: #334155;
  margin-bottom: 4px;
}

.template-card-desc {
  font-size: 13px;
  color: #64748b;
}
</style>
