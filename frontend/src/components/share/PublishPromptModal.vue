<template>
  <div class="modal-overlay" @click.self="$emit('close')">
    <div class="modal-content">
      <div class="modal-header">
        <h3>发布提示词</h3>
        <button class="close-btn" @click="$emit('close')">×</button>
      </div>
      <div class="modal-body">
        <div class="form-group">
          <label>提示词描述 *</label>
          <textarea v-model="form.description" placeholder="简单描述这个提示词的用途..." rows="3"></textarea>
        </div>
        <div class="form-group">
          <label>提示词内容 *</label>
          <textarea v-model="form.promptContent" placeholder="粘贴你的提示词内容..." rows="6"></textarea>
        </div>
        <div class="form-row">
          <div class="form-group">
            <label>作者昵称 *</label>
            <input v-model="form.authorNickname" type="text" placeholder="你的昵称" />
          </div>
          <div class="form-group">
            <label>联系方式 *</label>
            <input v-model="form.authorContact" type="text" placeholder="邮箱或链接" />
          </div>
        </div>
        <div class="history-btn-wrapper">
          <button class="btn-history" @click="showHistorySelector = true">
            从历史记录选择
          </button>
        </div>
      </div>
      <div class="modal-footer">
        <button class="btn-cancel" @click="$emit('close')">取消</button>
        <button class="btn-submit" @click="handleSubmit" :disabled="!isValid">发布</button>
      </div>
    </div>
    <HistorySelectorModal v-if="showHistorySelector" @select="handleHistorySelect" @close="showHistorySelector = false" />
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue';
import type { ShareRequest, PromptRecord } from '@/types';
import HistorySelectorModal from './HistorySelectorModal.vue';

const emit = defineEmits<{
  (e: 'close'): void;
  (e: 'submit', data: ShareRequest): void;
}>();

const form = ref<ShareRequest>({
  description: '',
  promptContent: '',
  authorNickname: '',
  authorContact: '',
  sourcePromptId: undefined
});

const showHistorySelector = ref(false);

const isValid = computed(() => {
  return form.value.description.trim() &&
         form.value.promptContent.trim() &&
         form.value.authorNickname.trim() &&
         form.value.authorContact.trim();
});

const handleSubmit = () => {
  if (isValid.value) {
    emit('submit', { ...form.value });
  }
};

const handleHistorySelect = (record: PromptRecord) => {
  form.value.promptContent = record.generatedPrompt;
  form.value.description = record.taskDescription;
  form.value.sourcePromptId = record.id;
  showHistorySelector.value = false;
};
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.6);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-content {
  background: var(--bg-card);
  border-radius: var(--radius-2xl);
  width: 90%;
  max-width: 600px;
  max-height: 90vh;
  overflow: auto;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  border-bottom: 1px solid var(--border-color);
}

.modal-header h3 {
  margin: 0;
  font-size: 18px;
}

.close-btn {
  background: none;
  border: none;
  font-size: 24px;
  cursor: pointer;
  color: var(--text-secondary);
}

.modal-body {
  padding: 20px;
}

.form-group {
  margin-bottom: 16px;
}

.form-group label {
  display: block;
  margin-bottom: 6px;
  font-size: 14px;
  font-weight: 500;
}

.form-group input,
.form-group textarea {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid var(--border-color);
  border-radius: var(--radius-lg);
  font-size: 14px;
  background: var(--bg-primary);
  color: var(--text-primary);
  resize: vertical;
}

.form-group input:focus,
.form-group textarea:focus {
  outline: none;
  border-color: var(--color-primary-500);
}

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
}

.history-btn-wrapper {
  margin-top: 8px;
}

.btn-history {
  background: var(--bg-secondary);
  border: 1px solid var(--border-color);
  padding: 8px 16px;
  border-radius: var(--radius-lg);
  font-size: 13px;
  cursor: pointer;
  color: var(--text-secondary);
}

.btn-history:hover {
  background: var(--bg-tertiary);
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding: 20px;
  border-top: 1px solid var(--border-color);
}

.btn-cancel, .btn-submit {
  padding: 10px 20px;
  border-radius: var(--radius-lg);
  font-size: 14px;
  border: none;
  cursor: pointer;
}

.btn-cancel {
  background: var(--bg-secondary);
  color: var(--text-primary);
}

.btn-submit {
  background: linear-gradient(135deg, var(--color-primary-600), var(--color-primary-700));
  color: white;
}

.btn-submit:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}
</style>