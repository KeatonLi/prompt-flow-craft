<template>
  <AppLayout>
    <template #main>
      <div class="page-container">
        <div class="banner">
          <h1 class="banner-title">🎨 提示词变体生成器</h1>
          <p class="banner-desc">一个提示词，多种可能！生成不同风格和用途的提示词变体</p>
        </div>

        <div class="content-wrapper">
          <!-- 输入区域 -->
          <div class="input-section">
            <div class="form-group">
              <label class="form-label">
                <span class="label-icon">📝</span>
                输入基础提示词
              </label>
              <textarea 
                v-model="basePrompt" 
                class="prompt-textarea"
                placeholder="请输入你想要生成变体的基础提示词..."
                rows="4"
              ></textarea>
              <div class="char-info">{{ basePrompt.length }} 字符</div>
            </div>

            <div class="options-grid">
              <div class="option-item">
                <label class="option-label">
                  <span class="label-icon">🎯</span>
                  变体数量
                </label>
                <select v-model="variantCount" class="option-select">
                  <option :value="3">3 个变体</option>
                  <option :value="5">5 个变体</option>
                  <option :value="7">7 个变体</option>
                </select>
              </div>

              <div class="option-item">
                <label class="option-label">
                  <span class="label-icon">📏</span>
                  长度偏好
                </label>
                <select v-model="lengthPreference" class="option-select">
                  <option value="balanced">均衡</option>
                  <option value="short">简短精炼</option>
                  <option value="long">详细全面</option>
                </select>
              </div>

              <div class="option-item">
                <label class="option-label">
                  <span class="label-icon">💡</span>
                  变体风格
                </label>
                <div class="checkbox-group">
                  <label class="checkbox-label">
                    <input type="checkbox" value="formal" v-model="styles" />
                    <span>正式</span>
                  </label>
                  <label class="checkbox-label">
                    <input type="checkbox" value="casual" v-model="styles" />
                    <span>轻松</span>
                  </label>
                  <label class="checkbox-label">
                    <input type="checkbox" value="technical" v-model="styles" />
                    <span>技术</span>
                  </label>
                  <label class="checkbox-label">
                    <input type="checkbox" value="creative" v-model="styles" />
                    <span>创意</span>
                  </label>
                </div>
              </div>
            </div>

            <button 
              class="generate-btn"
              :disabled="loading || !basePrompt.trim()"
              @click="generateVariants"
            >
              <span v-if="loading" class="loading-spinner"></span>
              <span v-else class="btn-icon">⚡</span>
              {{ loading ? '生成中...' : '生成变体' }}
            </button>
          </div>

          <!-- 变体结果展示 -->
          <div v-if="variants.length > 0" class="variants-section">
            <div class="section-header">
              <h2>✨ 生成的变体</h2>
              <div class="header-actions">
                <button class="action-btn" @click="copyAll">
                  📋 复制全部
                </button>
                <button class="action-btn secondary" @click="resetForm">
                  🔄 重新生成
                </button>
              </div>
            </div>

            <div class="variants-grid">
              <div 
                v-for="(variant, index) in variants" 
                :key="index"
                class="variant-card"
                :class="{ active: selectedVariant === index }"
                @click="selectVariant(index)"
              >
                <div class="variant-header">
                  <span class="variant-badge" :style="{ background: getVariantColor(index) }">
                    变体 {{ index + 1 }}
                  </span>
                  <span class="variant-style">{{ variant.style }}</span>
                </div>
                
                <div class="variant-content">
                  <pre>{{ variant.prompt }}</pre>
                </div>

                <div class="variant-footer">
                  <div class="variant-meta">
                    <span class="meta-item">📝 {{ variant.prompt.length }} 字</span>
                    <span v-if="variant.usage" class="meta-item">🎯 {{ variant.usage }}</span>
                  </div>
                  <div class="variant-actions">
                    <button class="icon-btn" @click.stop="copyVariant(index)" title="复制">
                      📋
                    </button>
                    <button class="icon-btn" @click.stop="useVariant(index)" title="使用">
                      ▶️
                    </button>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- 使用说明 -->
          <div class="tips-section" v-if="variants.length === 0">
            <h3>💡 使用技巧</h3>
            <div class="tips-grid">
              <div class="tip-card">
                <div class="tip-icon">🎯</div>
                <h4>明确任务</h4>
                <p>输入具体、清晰的提示词作为基础</p>
              </div>
              <div class="tip-card">
                <div class="tip-icon">🎨</div>
                <h4>选择风格</h4>
                <p>勾选多种风格获得多样化变体</p>
              </div>
              <div class="tip-card">
                <div class="tip-icon">📋</div>
                <h4>比较选择</h4>
                <p>浏览不同变体，选择最适合的</p>
              </div>
              <div class="tip-card">
                <div class="tip-icon">✨</div>
                <h4>持续优化</h4>
                <p>基于变体再优化，获得最佳效果</p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </template>
  </AppLayout>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import AppLayout from '../components/layout/AppLayout.vue'

const router = useRouter()
const API = 'http://111.231.107.210:8080/api'

const basePrompt = ref('')
const variantCount = ref(5)
const lengthPreference = ref('balanced')
const styles = ref(['formal', 'casual', 'technical', 'creative'])
const loading = ref(false)
const variants = ref([])
const selectedVariant = ref(null)

const variantColors = [
  'linear-gradient(135deg, #3b82f6, #1d4ed8)',
  'linear-gradient(135deg, #60a5fa, #3b82f6)',
  'linear-gradient(135deg, #93c5fd, #60a5fa)',
  'linear-gradient(135deg, #2563eb, #1d4ed8)',
  'linear-gradient(135deg, #1e40af, #1e3a8a)',
  'linear-gradient(135deg, #1d4ed8, #1e3a8a)',
  'linear-gradient(135deg, #3b82f6, #2563eb)',
]

const getVariantColor = (index) => {
  return variantColors[index % variantColors.length]
}

const generateVariants = async () => {
  if (!basePrompt.value.trim() || loading.value) return
  
  loading.value = true
  variants.value = []
  selectedVariant.value = null
  
  try {
    const response = await fetch(`${API}/prompt/variants`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        basePrompt: basePrompt.value,
        count: variantCount.value,
        lengthPreference: lengthPreference.value,
        styles: styles.value
      })
    })
    
    const data = await response.json()
    
    if (data.success && data.data) {
      variants.value = data.data
    } else {
      // 如果API失败，使用模拟数据演示
      variants.value = generateMockVariants()
    }
  } catch (error) {
    console.error('Generate variants error:', error)
    // 使用模拟数据
    variants.value = generateMockVariants()
  } finally {
    loading.value = false
  }
}

// 生成模拟变体数据（用于演示）
const generateMockVariants = () => {
  const base = basePrompt.value
  const mockVariants = []
  const styleNames = {
    formal: '正式风格',
    casual: '轻松风格',
    technical: '技术风格',
    creative: '创意风格'
  }
  const usages = {
    formal: '商务沟通、报告撰写',
    casual: '日常对话、社交媒体',
    technical: '代码开发、技术文档',
    creative: '创意写作、内容创作'
  }
  
  const styleList = styles.value.length > 0 ? styles.value : ['formal', 'casual', 'technical', 'creative']
  
  for (let i = 0; i < variantCount.value; i++) {
    const style = styleList[i % styleList.length]
    const styleName = styleNames[style] || '混合风格'
    
    mockVariants.push({
      prompt: generateVariantPrompt(base, style, lengthPreference.value),
      style: styleName,
      usage: usages[style] || '通用场景'
    })
  }
  
  return mockVariants
}

const generateVariantPrompt = (base, style, length) => {
  const lengthModifier = {
    short: '简洁版：',
    balanced: '',
    long: '详细版：'
  }
  
  const styleModifier = {
    formal: '请用正式、专业的语言描述以下任务：',
    casual: '用轻松、易懂的方式表达：',
    technical: '请用技术性、精确的语言描述：',
    creative: '请用创意、有趣的方式表达：'
  }
  
  const prefix = lengthModifier[length] || ''
  const stylePrefix = styleModifier[style] || ''
  
  return `${prefix}${stylePrefix}${base}\n\n${getStyleGuidance(style)}`
}

const getStyleGuidance = (style) => {
  const guidances = {
    formal: '• 使用正式用语\n• 保持专业语气\n• 结构清晰，条理分明\n• 避免口语化表达',
    casual: '• 使用日常语言\n• 保持友好氛围\n• 轻松自然，易于理解\n• 可以适当使用表情',
    technical: '• 使用专业术语\n• 精确描述需求\n• 包含具体参数\n• 强调可执行性',
    creative: '• 使用生动描述\n• 加入创意元素\n• 富有想象力\n• 鼓励创新思维'
  }
  return guidances[style] || ''
}

const selectVariant = (index) => {
  selectedVariant.value = index
}

const copyVariant = async (index) => {
  const variant = variants.value[index]
  try {
    await navigator.clipboard.writeText(variant.prompt)
    alert('已复制到剪贴板！')
  } catch (err) {
    console.error('Copy failed:', err)
  }
}

const copyAll = async () => {
  const allText = variants.value.map((v, i) => 
    `=== 变体 ${i + 1} (${v.style}) ===\n${v.prompt}`
  ).join('\n\n')
  
  try {
    await navigator.clipboard.writeText(allText)
    alert('已复制全部变体！')
  } catch (err) {
    console.error('Copy all failed:', err)
  }
}

const useVariant = (index) => {
  const variant = variants.value[index]
  router.push({
    path: '/',
    query: { 
      task: variant.prompt,
      source: 'variant'
    }
  })
}

const resetForm = () => {
  variants.value = []
  selectedVariant.value = null
}
</script>

<style scoped>
.page-container {
  min-height: 100%;
}

.banner {
  background: linear-gradient(135deg, #3b82f6 0%, #1d4ed8 100%);
  padding: 40px 32px;
  text-align: center;
  position: relative;
  overflow: hidden;
}

.banner::before {
  content: '';
  position: absolute;
  top: -50%;
  left: -50%;
  width: 200%;
  height: 200%;
  background: radial-gradient(circle, rgba(255,255,255,0.1) 0%, transparent 60%);
  animation: pulse 4s ease-in-out infinite;
}

@keyframes pulse {
  0%, 100% { transform: scale(1); opacity: 0.5; }
  50% { transform: scale(1.1); opacity: 0.3; }
}

.banner-title {
  font-size: 1.75rem;
  font-weight: 700;
  color: white;
  margin: 0 0 8px;
  position: relative;
}

.banner-desc {
  color: rgba(255,255,255,0.85);
  font-size: 1rem;
  margin: 0;
  position: relative;
}

.content-wrapper {
  max-width: 1200px;
  margin: 0 auto;
  padding: 24px;
}

/* 输入区域 */
.input-section {
  background: white;
  border-radius: 16px;
  padding: 28px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.06);
  margin-bottom: 30px;
}

.form-group {
  margin-bottom: 24px;
}

.form-label {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 600;
  color: #1e293b;
  margin-bottom: 10px;
  font-size: 0.95rem;
}

.label-icon {
  font-size: 1.1rem;
}

.prompt-textarea {
  width: 100%;
  padding: 14px;
  border: 1px solid #e2e8f0;
  border-radius: 10px;
  font-size: 0.95rem;
  font-family: inherit;
  line-height: 1.6;
  resize: vertical;
  transition: border-color 0.2s, box-shadow 0.2s;
  box-sizing: border-box;
}

.prompt-textarea:focus {
  outline: none;
  border-color: #3b82f6;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
}

.char-info {
  text-align: right;
  font-size: 0.8rem;
  color: #94a3b8;
  margin-top: 6px;
}

.options-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
  margin-bottom: 24px;
}

.option-item {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.option-label {
  display: flex;
  align-items: center;
  gap: 6px;
  font-weight: 600;
  color: #1e293b;
  font-size: 0.9rem;
}

.option-select {
  padding: 10px 14px;
  border: 1px solid #e2e8f0;
  border-radius: 10px;
  font-size: 0.9rem;
  background: white;
  cursor: pointer;
  transition: border-color 0.2s;
}

.option-select:focus {
  outline: none;
  border-color: #3b82f6;
}

.checkbox-group {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.checkbox-label {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 0.85rem;
  color: #64748b;
  cursor: pointer;
}

.checkbox-label input {
  accent-color: #3b82f6;
}

.generate-btn {
  width: 100%;
  padding: 14px 28px;
  background: linear-gradient(135deg, #3b82f6 0%, #1d4ed8 100%);
  color: white;
  border: none;
  border-radius: 10px;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.generate-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(245, 158, 11, 0.3);
}

.generate-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.btn-icon {
  font-size: 1.1rem;
}

.loading-spinner {
  width: 18px;
  height: 18px;
  border: 2px solid rgba(255,255,255,0.3);
  border-top-color: white;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

/* 变体结果 */
.variants-section {
  margin-bottom: 30px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.section-header h2 {
  margin: 0;
  color: #1f2937;
  font-size: 1.2rem;
}

.header-actions {
  display: flex;
  gap: 10px;
}

.action-btn {
  padding: 8px 16px;
  background: #3b82f6;
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 0.85rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
}

.action-btn:hover {
  background: #d97706;
}

.action-btn.secondary {
  background: #f1f5f9;
  color: #64748b;
}

.action-btn.secondary:hover {
  background: #e2e8f0;
}

.variants-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: 20px;
}

.variant-card {
  background: white;
  border-radius: 14px;
  padding: 20px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.06);
  cursor: pointer;
  transition: all 0.3s;
  border: 2px solid transparent;
}

.variant-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 8px 24px rgba(0,0,0,0.1);
}

.variant-card.active {
  border-color: #3b82f6;
}

.variant-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 14px;
}

.variant-badge {
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 0.75rem;
  font-weight: 600;
  color: white;
}

.variant-style {
  font-size: 0.8rem;
  color: #64748b;
}

.variant-content {
  margin-bottom: 14px;
}

.variant-content pre {
  margin: 0;
  padding: 14px;
  background: #f8fafc;
  border-radius: 8px;
  font-size: 0.85rem;
  line-height: 1.6;
  white-space: pre-wrap;
  word-wrap: break-word;
  max-height: 180px;
  overflow-y: auto;
  color: #475569;
}

.variant-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 12px;
  border-top: 1px solid #e2e8f0;
}

.variant-meta {
  display: flex;
  gap: 12px;
}

.meta-item {
  font-size: 0.75rem;
  color: #94a3b8;
}

.variant-actions {
  display: flex;
  gap: 8px;
}

.icon-btn {
  padding: 6px 10px;
  background: #f1f5f9;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 0.85rem;
  transition: all 0.2s;
}

.icon-btn:hover {
  background: #e2e8f0;
}

/* 技巧区域 */
.tips-section {
  background: white;
  border-radius: 16px;
  padding: 28px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.06);
}

.tips-section h3 {
  margin: 0 0 20px;
  color: #1f2937;
  font-size: 1.1rem;
}

.tips-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
  gap: 16px;
}

.tip-card {
  padding: 20px;
  background: #f8fafc;
  border-radius: 12px;
  text-align: center;
}

.tip-icon {
  font-size: 2rem;
  margin-bottom: 10px;
}

.tip-card h4 {
  margin: 0 0 8px;
  color: #1f2937;
  font-size: 0.95rem;
}

.tip-card p {
  margin: 0;
  color: #64748b;
  font-size: 0.85rem;
  line-height: 1.5;
}

/* 暗黑模式 */
:root.dark .banner {
  background: linear-gradient(135deg, #059669 0%, #047857 100%);
}

:root.dark .input-section,
:root.dark .variant-card {
  background: rgba(30, 41, 59, 0.6);
  border-color: rgba(51, 65, 85, 0.6);
}

:root.dark .prompt-textarea,
:root.dark .option-select {
  background: rgba(15, 23, 42, 0.5);
  border-color: #334155;
  color: #e2e8f0;
}

:root.dark .form-label,
:root.dark .option-label {
  color: #e2e8f0;
}

:root.dark .section-header h2 {
  color: #f1f5f9;
}

:root.dark .variant-content {
  color: #cbd5e1;
}

:root.dark .variant-meta {
  border-color: rgba(51, 65, 85, 0.6);
}

:root.dark .comparison-section,
:root.dark .tips-section {
  background: rgba(30, 41, 59, 0.6);
  border-color: rgba(51, 65, 85, 0.6);
}

:root.dark .comparison-section h3,
:root.dark .tips-section h3 {
  color: #f1f5f9;
}

:root.dark .comparison-text {
  color: #cbd5e1;
}

:root.dark .tip-card {
  background: rgba(15, 23, 42, 0.5);
}

:root.dark .tip-card h4 {
  color: #f1f5f9;
}

:root.dark .tip-card p {
  color: #94a3b8;
}

@media (max-width: 768px) {
  .content-wrapper {
    padding: 16px;
  }
  
  .variants-grid {
    grid-template-columns: 1fr;
  }
  
  .section-header {
    flex-direction: column;
    gap: 12px;
    align-items: flex-start;
  }
}
</style>
