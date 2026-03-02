<template>
  <div v-if="item" class="modal" @click="$emit('close')">
    <div class="modal-content" @click.stop>
      <div class="modal-header">
        <h2>{{ title }}</h2>
        <button @click="$emit('close')">✕</button>
      </div>
      <div class="modal-body">
        <div class="field">
          <label>任务描述</label>
          <div class="val">{{ item.taskDescription }}</div>
        </div>
        <div class="field">
          <label>生成结果</label>
          <pre class="result">{{ item.generatedPrompt }}</pre>
        </div>
        <div class="field">
          <label>关键词</label>
          <div class="tags">
            <span v-for="t in item.tags" :key="t.id" class="tag" :style="{background: t.color+'20', color: t.color}">{{ t.name }}</span>
          </div>
        </div>
        <div class="btns">
          <button class="btn1" @click="$emit('use')">使用此提示词</button>
          <button class="btn2" @click="copyPrompt">复制结果</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
const props = defineProps({
  item: Object,
  title: { type: String, default: '提示词详情' }
})

const emit = defineEmits(['close', 'use'])

const copyPrompt = () => {
  if (props.item?.generatedPrompt) {
    navigator.clipboard.writeText(props.item.generatedPrompt)
    alert('已复制')
  }
}
</script>

<style scoped>
.modal { position: fixed; inset: 0; background: rgba(0,0,0,0.5); display: flex; align-items: center; justify-content: center; z-index: 1000; }
.modal-content { background: white; border-radius: 14px; width: 90%; max-width: 650px; max-height: 85vh; overflow: auto; }
.modal-header { display: flex; justify-content: space-between; align-items: center; padding: 16px 20px; border-bottom: 1px solid #e2e8f0; }
.modal-header h2 { font-size: 1.15rem; color: #1e293b; margin: 0; }
.modal-header button { background: none; border: none; font-size: 1.1rem; color: #94a3b8; cursor: pointer; }
.modal-body { padding: 20px; }

.field { margin-bottom: 14px; }
.field label { display: block; font-weight: 600; color: #64748b; font-size: 0.85rem; margin-bottom: 6px; }
.val { background: #f8fafc; padding: 12px; border-radius: 8px; font-size: 0.9rem; color: #1e293b; }
.result { background: #f8fafc; padding: 14px; border-radius: 8px; font-size: 0.85rem; color: #334155; white-space: pre-wrap; margin: 0; max-height: 250px; overflow: auto; }
.tags { display: flex; gap: 6px; flex-wrap: wrap; }
.tag { font-size: 0.7rem; padding: 2px 8px; border-radius: 4px; }

.btns { display: flex; gap: 10px; margin-top: 20px; }
.btn1 { flex: 1; padding: 10px 16px; background: #3b82f6; color: white; border: none; border-radius: 8px; cursor: pointer; font-size: 0.9rem; }
.btn2 { padding: 10px 16px; background: #f1f5f9; color: #64748b; border: none; border-radius: 8px; cursor: pointer; font-size: 0.9rem; }
</style>
