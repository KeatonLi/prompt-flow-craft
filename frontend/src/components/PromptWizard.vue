<template>
  <div v-if="visible" class="wizard-overlay" @click.self="$emit('close')">
    <div class="wizard-container">
      <div class="wizard-header">
        <h2>🎯 提示词向导</h2>
        <button class="close-btn" @click="$emit('close')">✕</button>
      </div>
      
      <div class="wizard-progress">
        <div class="progress-bar">
          <div class="progress-fill" :style="{ width: progressPercent + '%' }"></div>
        </div>
        <span class="progress-text">第 {{ currentStep + 1 }} / {{ totalSteps }} 步</span>
      </div>

      <div class="wizard-content" v-if="currentQuestion">
        <div class="question-title">{{ currentQuestion.title }}</div>
        <div class="question-desc">{{ currentQuestion.description }}</div>
        
        <div class="options-grid">
          <div 
            v-for="(option, idx) in currentQuestion.options" 
            :key="idx"
            class="option-card"
            :class="{ selected: selectedOption === idx }"
            @click="selectOption(idx)"
          >
            <div class="option-label">{{ ['A', 'B', 'C'][idx] }}</div>
            <div class="option-text">{{ option.text }}</div>
            <div class="option-desc" v-if="option.description">{{ option.description }}</div>
          </div>
        </div>

        <div class="custom-input" v-if="selectedOption === 2 && currentQuestion.options[2]?.allowCustom">
          <input 
            v-model="customAnswer" 
            type="text" 
            :placeholder="currentQuestion.options[2].placeholder || '请输入...'"
            @input="onCustomInput"
          />
        </div>
      </div>

      <div class="wizard-result" v-else-if="generatedPrompt">
        <div class="result-header">✨ 生成的提示词</div>
        <pre class="result-content">{{ generatedPrompt }}</pre>
        <div class="result-actions">
          <button class="btn-use" @click="usePrompt">✨ 使用此提示词</button>
          <button class="btn-copy" @click="copyPrompt">📋 复制结果</button>
        </div>
      </div>

      <div class="wizard-footer">
        <button class="btn-back" v-if="currentStep > 0 && !generatedPrompt" @click="prevStep">
          ← 上一步
        </button>
        <button 
          class="btn-next" 
          v-if="!generatedPrompt" 
          :disabled="!canProceed"
          @click="nextStep"
        >
          {{ isLastStep ? '生成提示词' : '下一步' }} →
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { useRouter } from 'vue-router'

const props = defineProps({
  visible: Boolean
})

const emit = defineEmits(['close', 'use'])

const router = useRouter()

// 向导问题配置
const wizardSteps = [
  {
    title: '🎯 你的任务是什么？',
    description: '描述你想要 AI 帮你完成什么任务',
    options: [
      { text: '内容创作', description: '写文章、生成文案等', value: 'content' },
      { text: '代码开发', description: '编程、调试、技术问题', value: 'code' },
      { text: '自定义', description: '其他任务类型', allowCustom: true, placeholder: '请描述你的任务...' }
    ]
  },
  {
    title: '👥 目标用户是谁？',
    description: '提示词针对的受众群体',
    options: [
      { text: '普通用户', description: '面向大众，简单易懂', value: 'general' },
      { text: '专业人士', description: '技术人员、行业专家', value: 'professional' },
      { text: '自定义', description: '特定人群', allowCustom: true, placeholder: '请描述目标用户...' }
    ]
  },
  {
    title: '📝 希望输出什么格式？',
    description: 'AI 生成结果的呈现形式',
    options: [
      { text: '纯文本', description: '普通段落文字', value: 'text' },
      { text: '结构化', description: '列表、表格、JSON等', value: 'structured' },
      { text: '自定义', description: '其他格式', allowCustom: true, placeholder: '请描述期望格式...' }
    ]
  },
  {
    title: '🎭 需要什么风格？',
    description: '生成内容的语调风格',
    options: [
      { text: '专业正式', description: '商务、学术风格', value: 'formal' },
      { text: '友好亲切', description: '轻松、易读', value: 'friendly' },
      { text: '自定义', description: '其他风格', allowCustom: true, placeholder: '请描述期望风格...' }
    ]
  },
  {
    title: '📏 需要多详细？',
    description: '生成内容的详细程度',
    options: [
      { text: '简洁', description: '简短精炼', value: 'short' },
      { text: '中等', description: '适度详细', value: 'medium' },
      { text: '详细', description: '面面俱到', value: 'long' }
    ]
  }
]

const currentStep = ref(0)
const answers = ref({})
const selectedOption = ref(null)
const customAnswer = ref('')
const generatedPrompt = ref('')

const currentQuestion = computed(() => wizardSteps[currentStep.value])
const totalSteps = computed(() => wizardSteps.length)
const progressPercent = computed(() => ((currentStep.value + 1) / totalSteps.value) * 100)
const isLastStep = computed(() => currentStep.value === totalSteps.value - 1)

const canProceed = computed(() => {
  if (selectedOption.value === null) return false
  if (selectedOption.value === 2 && currentQuestion.value.options[2]?.allowCustom) {
    return customAnswer.value.trim().length > 0
  }
  return true
})

const selectOption = (idx) => {
  selectedOption.value = idx
  if (idx !== 2) {
    customAnswer.value = ''
  }
}

const onCustomInput = () => {
  // Custom input handling
}

const nextStep = () => {
  // Save current answer
  const q = currentQuestion.value
  const opt = q.options[selectedOption.value]
  
  if (selectedOption.value === 2 && opt?.allowCustom) {
    answers.value[q.title] = customAnswer.value
  } else {
    answers.value[q.title] = opt
  }

  if (isLastStep.value) {
    generatePrompt()
  } else {
    currentStep.value++
    selectedOption.value = null
    customAnswer.value = ''
  }
}

const prevStep = () => {
  if (currentStep.value > 0) {
    currentStep.value--
    selectedOption.value = null
    customAnswer.value = ''
  }
}

const generatePrompt = () => {
  // Build prompt from answers
  let prompt = ''
  
  // Task
  const taskAns = answers.value[wizardSteps[0].title]
  prompt += `你是一位专业的AI提示词设计师。\n\n`
  prompt += `## 任务\n`
  prompt += `${typeof taskAns === 'object' ? taskAns.text : taskAns}\n\n`
  
  // Audience
  const audAns = answers.value[wizardSteps[1].title]
  if (audAns) {
    prompt += `## 目标受众\n`
    prompt += `${typeof audAns === 'object' ? audAns.text : audAns}\n\n`
  }
  
  // Format
  const fmtAns = answers.value[wizardSteps[2].title]
  if (fmtAns) {
    prompt += `## 输出格式\n`
    prompt += `${typeof fmtAns === 'object' ? fmtAns.text : fmtAns}\n\n`
  }
  
  // Tone
  const toneAns = answers.value[wizardSteps[3].title]
  if (toneAns) {
    prompt += `## 风格要求\n`
    prompt += `${typeof toneAns === 'object' ? toneAns.text : toneAns}\n\n`
  }
  
  // Length
  const lenAns = answers.value[wizardSteps[4].title]
  if (lenAns) {
    prompt += `## 详细程度\n`
    prompt += `${typeof lenAns === 'object' ? lenAns.text : lenAns}\n\n`
  }
  
  prompt += `请根据以上信息，生成一个高质量的AI提示词。`
  
  generatedPrompt.value = prompt
}

const usePrompt = () => {
  emit('use', {
    taskDescription: generatedPrompt.value,
    mode: 'wizard'
  })
  emit('close')
}

const copyPrompt = async () => {
  try {
    await navigator.clipboard.writeText(generatedPrompt.value)
    alert('已复制到剪贴板！')
  } catch (e) {
    console.error(e)
  }
}

// Reset when closed
watch(() => props.visible, (val) => {
  if (!val) {
    setTimeout(() => {
      currentStep.value = 0
      answers.value = {}
      selectedOption.value = null
      customAnswer.value = ''
      generatedPrompt.value = ''
    }, 300)
  }
})
</script>

<style scoped>
.wizard-overlay { position: fixed; inset: 0; background: rgba(0,0,0,0.6); display: flex; align-items: center; justify-content: center; z-index: 2000; padding: 20px; }
.wizard-container { background: white; border-radius: 20px; width: 100%; max-width: 600px; max-height: 90vh; overflow: auto; box-shadow: 0 20px 60px rgba(0,0,0,0.2); }

.wizard-header { display: flex; justify-content: space-between; align-items: center; padding: 20px 24px; border-bottom: 1px solid #e2e8f0; }
.wizard-header h2 { margin: 0; font-size: 1.3rem; color: #1e293b; }
.close-btn { background: none; border: none; font-size: 1.2rem; color: #94a3b8; cursor: pointer; padding: 4px; }

.wizard-progress { padding: 16px 24px; display: flex; align-items: center; gap: 12px; }
.progress-bar { flex: 1; height: 6px; background: #e2e8f0; border-radius: 3px; overflow: hidden; }
.progress-fill { height: 100%; background: linear-gradient(90deg, #3b82f6, #8b5cf6); border-radius: 3px; transition: width 0.3s; }
.progress-text { font-size: 0.85rem; color: #64748b; white-space: nowrap; }

.wizard-content { padding: 24px; }
.question-title { font-size: 1.2rem; font-weight: 600; color: #1e293b; margin-bottom: 8px; }
.question-desc { font-size: 0.95rem; color: #64748b; margin-bottom: 20px; }

.options-grid { display: flex; flex-direction: column; gap: 12px; }
.option-card { display: flex; align-items: center; gap: 14px; padding: 14px 16px; border: 2px solid #e2e8f0; border-radius: 12px; cursor: pointer; transition: all 0.2s; }
.option-card:hover { border-color: #3b82f6; background: #f8fafc; }
.option-card.selected { border-color: #3b82f6; background: #eff6ff; }
.option-label { width: 32px; height: 32px; border-radius: 8px; background: #e2e8f0; display: flex; align-items: center; justify-content: center; font-weight: 600; color: #64748b; }
.option-card.selected .option-label { background: #3b82f6; color: white; }
.option-text { flex: 1; font-weight: 500; color: #1e293b; }
.option-desc { font-size: 0.8rem; color: #94a3b8; }

.custom-input { margin-top: 12px; }
.custom-input input { width: 100%; padding: 12px 14px; border: 2px solid #e2e8f0; border-radius: 10px; font-size: 0.95rem; outline: none; transition: border-color 0.2s; }
.custom-input input:focus { border-color: #3b82f6; }

.wizard-result { padding: 24px; }
.result-header { font-size: 1rem; font-weight: 600; color: #1e293b; margin-bottom: 12px; }
.result-content { background: #f8fafc; padding: 16px; border-radius: 10px; font-size: 0.85rem; color: #334155; white-space: pre-wrap; max-height: 300px; overflow: auto; margin: 0; }

.result-actions { display: flex; gap: 12px; margin-top: 16px; }
.btn-use { flex: 1; padding: 12px 20px; background: #3b82f6; color: white; border: none; border-radius: 10px; font-size: 0.95rem; font-weight: 500; cursor: pointer; }
.btn-copy { padding: 12px 20px; background: #f1f5f9; color: #64748b; border: none; border-radius: 10px; font-size: 0.95rem; cursor: pointer; }

.wizard-footer { padding: 16px 24px; border-top: 1px solid #e2e8f0; display: flex; justify-content: space-between; gap: 12px; }
.btn-back { padding: 10px 20px; background: #f1f5f9; color: #64748b; border: none; border-radius: 8px; font-size: 0.9rem; cursor: pointer; }
.btn-next { flex: 1; padding: 12px 24px; background: linear-gradient(135deg, #3b82f6, #1d4ed8); color: white; border: none; border-radius: 10px; font-size: 1rem; font-weight: 600; cursor: pointer; }
.btn-next:disabled { opacity: 0.5; cursor: not-allowed; }
.btn-next:hover:not(:disabled) { transform: translateY(-1px); box-shadow: 0 4px 12px rgba(59,130,246,0.3); }
</style>
