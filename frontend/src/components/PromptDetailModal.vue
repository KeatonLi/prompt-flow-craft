<template>
  <div v-if="item" class="modal" @click="$emit('close')">
    <div class="modal-content" @click.stop>
      <div class="modal-header">
        <h2>{{ title }}</h2>
        <button @click="$emit('close')">✕</button>
      </div>
      <div v-if="loading" class="loading-overlay">
        <div class="spinner"></div>
      </div>
      <div class="modal-body" v-else>
        <div class="field">
          <label>任务描述</label>
          <div class="val">{{ item.taskDescription }}</div>
        </div>
        
        <div class="params-row" v-if="item.targetAudience || item.outputFormat || item.tone || item.length">
          <div class="param-badge" v-if="item.targetAudience">
            <span class="param-dot">👤</span>
            <span>{{ getAudienceText(item.targetAudience) }}</span>
          </div>
          <div class="param-badge" v-if="item.outputFormat">
            <span class="param-dot">📝</span>
            <span>{{ getFormatText(item.outputFormat) }}</span>
          </div>
          <div class="param-badge" v-if="item.tone">
            <span class="param-dot">🎭</span>
            <span>{{ getToneText(item.tone) }}</span>
          </div>
          <div class="param-badge" v-if="item.length">
            <span class="param-dot">📏</span>
            <span>{{ getLengthText(item.length) }}</span>
          </div>
        </div>
        
        <div class="field">
          <label>生成结果</label>
          <pre class="result">{{ item.generatedPrompt || item.promptSummary }}</pre>
        </div>
        
        <div class="field" v-if="item.tags?.length || item.aiTags?.length">
          <label>关键词</label>
          <div class="tags">
            <template v-if="item.tags?.length">
              <span v-for="t in item.tags" :key="t.id" class="tag" :style="{background: t.color+'15', color: t.color, borderColor: t.color+'30'}">{{ t.name }}</span>
            </template>
            <template v-else-if="item.aiTags?.length">
              <span v-for="(tag, idx) in item.aiTags" :key="idx" class="tag" :style="getTagStyle(idx)">{{ tag }}</span>
            </template>
          </div>
        </div>
        
        <div class="field" v-if="item.category">
          <label>分类</label>
          <div class="category-badge">
            <span :style="{ color: item.category.color }">{{ item.category.icon }} {{ item.category.name }}</span>
          </div>
        </div>
        
        <div class="btns">
          <button class="btn-use" @click="useTemplate">✨ 使用此提示词</button>
          <button class="btn-copy" @click="copyPrompt">📋 复制结果</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
const props = defineProps({
  item: Object,
  title: { type: String, default: '提示词详情' },
  loading: { type: Boolean, default: false }
})

const emit = defineEmits(['close', 'use'])

const getAudienceText = (v) => ({ general: '普通用户', professional: '专业人士', student: '学生', developer: '开发者', creator: '创作者' }[v] || v)
const getFormatText = (v) => ({ text: '文本', list: '列表', table: '表格', code: '代码', json: 'JSON' }[v] || v)
const getToneText = (v) => ({ formal: '正式', friendly: '友好', professional: '专业', creative: '创意', concise: '简洁' }[v] || v)
const getLengthText = (v) => ({ short: '简短', medium: '中等', long: '详细', very_long: '非常详细' }[v] || v)

const tagColors = [
  { bg: '#f9731615', color: '#f97316' },
  { bg: '#3b82f615', color: '#3b82f6' },
  { bg: '#14b8a615', color: '#14b8a6' },
  { bg: '#ec489915', color: '#ec4899' },
  { bg: '#8b5cf615', color: '#8b5cf6' },
  { bg: '#10b98115', color: '#10b981' },
  { bg: '#ef444415', color: '#ef4444' },
  { bg: '#f59e0b15', color: '#f59e0b' },
]
const getTagStyle = (idx) => tagColors[idx % tagColors.length]

// 使用此提示词 - 跳转首页并回填表单
const useTemplate = () => {
  emit('use', props.item)
}

// 复制结果到剪贴板
const copyPrompt = async () => {
  const text = props.item?.generatedPrompt || props.item?.promptSummary
  if (text) {
    try {
      await navigator.clipboard.writeText(text)
      // 提示用户复制成功
      const btn = document.querySelector('.btn-copy')
      if (btn) {
        const originalText = btn.textContent
        btn.textContent = '✅ 已复制!'
        setTimeout(() => btn.textContent = originalText, 1500)
      }
    } catch (e) {
      console.error('Copy failed:', e)
    }
  }
}
</script>

<style scoped>
.modal { position: fixed; inset: 0; background: rgba(0,0,0,0.5); display: flex; align-items: center; justify-content: center; z-index: 1000; }
.modal-content { background: white; border-radius: 16px; width: 90%; max-width: 680px; max-height: 85vh; overflow: auto; position: relative; }
.loading-overlay { display: flex; align-items: center; justify-content: center; min-height: 300px; }
.spinner { width: 40px; height: 40px; border: 3px solid #e2e8f0; border-top-color: #3b82f6; border-radius: 50%; animation: spin 0.8s linear infinite; }
@keyframes spin { to { transform: rotate(360deg); } }

.modal-header { display: flex; justify-content: space-between; align-items: center; padding: 18px 22px; border-bottom: 1px solid #e2e8f0; }
.modal-header h2 { font-size: 1.2rem; color: #1e293b; margin: 0; font-weight: 600; }
.modal-header button { background: none; border: none; font-size: 1.2rem; color: #94a3b8; cursor: pointer; padding: 4px; }
.modal-header button:hover { color: #64748b; }

.modal-body { padding: 22px; }

.field { margin-bottom: 16px; }
.field label { display: block; font-weight: 600; color: #64748b; font-size: 0.82rem; margin-bottom: 8px; text-transform: uppercase; letter-spacing: 0.5px; }
.val { background: #f8fafc; padding: 14px; border-radius: 10px; font-size: 0.95rem; color: #1e293b; line-height: 1.5; }

.params-row { display: flex; flex-wrap: wrap; gap: 8px; margin-bottom: 16px; }
.param-badge { display: inline-flex; align-items: center; gap: 5px; padding: 6px 12px; background: #f1f5f9; border-radius: 20px; font-size: 0.8rem; color: #64748b; }
.param-dot { font-size: 0.9rem; }

.result { background: #f8fafc; padding: 16px; border-radius: 10px; font-size: 0.85rem; color: #334155; white-space: pre-wrap; margin: 0; max-height: 280px; overflow: auto; line-height: 1.6; border: 1px solid #e2e8f0; }

.tags { display: flex; gap: 8px; flex-wrap: wrap; }
.tag { font-size: 0.75rem; padding: 4px 12px; border-radius: 14px; border: 1px solid; font-weight: 500; }

.category-badge { display: inline-flex; align-items: center; gap: 6px; padding: 6px 14px; background: #f8fafc; border-radius: 20px; font-size: 0.85rem; font-weight: 500; }

.btns { display: flex; gap: 12px; margin-top: 24px; }
.btn-use { flex: 1; padding: 14px 20px; background: linear-gradient(135deg, #3b82f6 0%, #1d4ed8 100%); color: white; border: none; border-radius: 10px; cursor: pointer; font-size: 0.95rem; font-weight: 600; transition: transform 0.2s, box-shadow 0.2s; }
.btn-use:hover { transform: translateY(-1px); box-shadow: 0 4px 12px rgba(59,130,246,0.3); }
.btn-copy { padding: 14px 20px; background: #f1f5f9; color: #64748b; border: none; border-radius: 10px; cursor: pointer; font-size: 0.95rem; font-weight: 500; transition: all 0.2s; }
.btn-copy:hover { background: #e2e8f0; color: #334155; }
</style>
