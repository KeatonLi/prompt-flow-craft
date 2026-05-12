<template>
  <div class="modal-overlay" @click.self="$emit('close')">
    <div class="modal-container">
      <!-- Header -->
      <div class="modal-header">
        <div class="header-content">
          <div class="header-icon">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M12 5v14M5 12h14"/>
            </svg>
          </div>
          <div>
            <h2>发布提示词</h2>
            <p>分享你的创意，帮助更多创作者</p>
          </div>
        </div>
        <button class="close-btn" @click="$emit('close')">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <line x1="18" y1="6" x2="6" y2="18"/>
            <line x1="6" y1="6" x2="18" y2="18"/>
          </svg>
        </button>
      </div>

      <!-- Body -->
      <div class="modal-body">
        <!-- Form -->
        <div class="form-section">
          <label class="form-label">
            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"/>
              <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"/>
            </svg>
            提示词描述 <span class="required">*</span>
          </label>
          <textarea
            v-model="form.description"
            placeholder="简单描述这个提示词的用途、特色和使用场景..."
            rows="3"
            class="form-textarea"
          ></textarea>
          <div class="char-count">{{ form.description.length }}/200</div>
        </div>

        <div class="form-section">
          <label class="form-label">
            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <polyline points="16 18 22 12 16 6"/>
              <polyline points="8 6 2 12 8 18"/>
            </svg>
            提示词内容 <span class="required">*</span>
          </label>
          <textarea
            v-model="form.promptContent"
            placeholder="粘贴你的提示词内容..."
            rows="8"
            class="form-textarea code"
          ></textarea>
        </div>

        <div class="form-row">
          <div class="form-section">
            <label class="form-label">
              <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/>
                <circle cx="12" cy="7" r="4"/>
              </svg>
              作者昵称 <span class="required">*</span>
            </label>
            <input
              v-model="form.authorNickname"
              type="text"
              placeholder="你的昵称"
              class="form-input"
            />
          </div>
          <div class="form-section">
            <label class="form-label">
              <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M4 4h16c1.1 0 2 .9 2 2v12c0 1.1-.9 2-2 2H4c-1.1 0-2-.9-2-2V6c0-1.1.9-2 2-2z"/>
                <polyline points="22,6 12,13 2,6"/>
              </svg>
              联系方式 <span class="required">*</span>
            </label>
            <input
              v-model="form.authorContact"
              type="text"
              placeholder="邮箱或链接"
              class="form-input"
            />
          </div>
        </div>

        <button class="btn-history" @click="showHistorySelector = true">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/>
            <polyline points="14 2 14 8 20 8"/>
            <line x1="12" y1="18" x2="12" y2="12"/>
            <line x1="9" y1="15" x2="15" y2="15"/>
          </svg>
          从历史记录选择
        </button>
      </div>

      <!-- Footer -->
      <div class="modal-footer">
        <button class="btn-cancel" @click="$emit('close')">取消</button>
        <button class="btn-submit" @click="handleSubmit" :disabled="!isValid">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M12 5v14M5 12h14"/>
          </svg>
          发布提示词
        </button>
      </div>
    </div>

    <HistorySelectorModal
      v-if="showHistorySelector"
      @select="handleHistorySelect"
      @close="showHistorySelector = false"
    />
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
  form.value.description = record.taskDescription || record.description || '';
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
  background: rgba(15, 23, 42, 0.7);
  backdrop-filter: blur(4px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  padding: 20px;
}

.modal-container {
  background: var(--bg-card);
  border-radius: var(--radius-3xl);
  width: 100%;
  max-width: 640px;
  max-height: 90vh;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  box-shadow:
    0 25px 50px -12px rgba(0, 0, 0, 0.25),
    0 0 0 1px rgba(255, 255, 255, 0.1);
}

/* Header */
.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  padding: 28px 28px 24px;
  background: linear-gradient(135deg, #0f172a 0%, #1e293b 100%);
}

.header-content {
  display: flex;
  align-items: flex-start;
  gap: 16px;
}

.header-icon {
  width: 48px;
  height: 48px;
  border-radius: 14px;
  background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  box-shadow: 0 8px 20px rgba(37, 99, 235, 0.4);
}

.modal-header h2 {
  margin: 0 0 4px;
  font-size: 20px;
  font-weight: 800;
  color: #f8fafc;
}

.modal-header p {
  margin: 0;
  font-size: 13px;
  color: #94a3b8;
}

.close-btn {
  background: rgba(255, 255, 255, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.1);
  width: 36px;
  height: 36px;
  border-radius: var(--radius-lg);
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #94a3b8;
  transition: all 0.2s;
}

.close-btn:hover {
  background: rgba(239, 68, 68, 0.2);
  border-color: rgba(239, 68, 68, 0.3);
  color: #f87171;
}

/* Body */
.modal-body {
  padding: 28px;
  overflow-y: auto;
  flex: 1;
}

.form-section {
  margin-bottom: 20px;
  position: relative;
}

.form-label {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 10px;
  font-size: 13px;
  font-weight: 600;
  color: var(--text-primary);
}

.form-label svg {
  color: var(--color-primary-600);
}

.required {
  color: #ef4444;
}

.form-input,
.form-textarea {
  width: 100%;
  padding: 12px 16px;
  border: 1px solid var(--border-color);
  border-radius: var(--radius-xl);
  font-size: 14px;
  background: var(--bg-primary);
  color: var(--text-primary);
  transition: all 0.2s;
  font-family: inherit;
}

.form-input:focus,
.form-textarea:focus {
  outline: none;
  border-color: var(--color-primary-500);
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
}

.form-input::placeholder,
.form-textarea::placeholder {
  color: var(--text-secondary);
}

.form-textarea {
  resize: vertical;
  line-height: 1.6;
}

.form-textarea.code {
  font-family: var(--font-mono, monospace);
  font-size: 13px;
  background: #0f172a;
  color: #e2e8f0;
  border-color: #334155;
}

.form-textarea.code:focus {
  border-color: var(--color-primary-500);
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.15);
}

.char-count {
  position: absolute;
  right: 12px;
  bottom: -20px;
  font-size: 11px;
  color: var(--text-secondary);
}

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
}

.btn-history {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  width: 100%;
  padding: 12px 20px;
  background: var(--bg-secondary);
  border: 1px dashed var(--border-color);
  border-radius: var(--radius-xl);
  font-size: 13px;
  font-weight: 500;
  color: var(--text-secondary);
  cursor: pointer;
  transition: all 0.2s;
}

.btn-history:hover {
  background: var(--color-primary-50);
  border-color: var(--color-primary-300);
  border-style: solid;
  color: var(--color-primary-600);
}

/* Footer */
.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding: 20px 28px;
  background: #f8fafc;
  border-top: 1px solid var(--border-color);
}

.btn-cancel,
.btn-submit {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 24px;
  border-radius: var(--radius-xl);
  font-size: 14px;
  font-weight: 600;
  border: none;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-cancel {
  background: white;
  color: var(--text-secondary);
  border: 1px solid var(--border-color);
}

.btn-cancel:hover {
  background: #f1f5f9;
  color: var(--text-primary);
}

.btn-submit {
  background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
  color: white;
  box-shadow: 0 4px 16px rgba(37, 99, 235, 0.3);
}

.btn-submit:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(37, 99, 235, 0.4);
}

.btn-submit:disabled {
  opacity: 0.5;
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
}
</style>
