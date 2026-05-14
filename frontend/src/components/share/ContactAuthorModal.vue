<template>
  <div class="modal-overlay" @click.self="$emit('close')">
    <div class="modal-content">
      <div class="modal-header">
        <h3>联系作者</h3>
        <button class="close-btn" @click="$emit('close')">×</button>
      </div>
      <div class="modal-body">
        <div class="author-info">
          <div class="author-avatar">{{ (prompt.authorNickname || '?')[0].toUpperCase() }}</div>
          <div class="author-name">{{ prompt.authorNickname || '匿名用户' }}</div>
        </div>
        <div class="contact-section">
          <label>联系方式</label>
          <div class="contact-value">
            <input type="text" :value="prompt.authorContact" readonly />
            <button class="copy-btn" @click="copyContact">
              {{ copied ? '已复制' : '复制' }}
            </button>
          </div>
        </div>
        <p class="tip">点击复制后，通过邮箱或链接联系作者</p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import type { SharedPrompt } from '@/types';

const props = defineProps<{
  prompt: SharedPrompt;
}>();

defineEmits<{
  (e: 'close'): void;
}>();

const copied = ref(false);

const copyContact = async () => {
  await navigator.clipboard.writeText(props.prompt.authorContact);
  copied.value = true;
  setTimeout(() => {
    copied.value = false;
  }, 2000);
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
  max-width: 400px;
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

.author-info {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 24px;
}

.author-avatar {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  background: linear-gradient(135deg, var(--color-primary-600), var(--color-primary-700));
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  font-weight: 600;
}

.author-name {
  font-size: 16px;
  font-weight: 600;
}

.contact-section label {
  display: block;
  margin-bottom: 8px;
  font-size: 14px;
  color: var(--text-secondary);
}

.contact-value {
  display: flex;
  gap: 8px;
}

.contact-value input {
  flex: 1;
  padding: 10px 12px;
  border: 1px solid var(--border-color);
  border-radius: var(--radius-lg);
  background: var(--bg-secondary);
  font-size: 14px;
}

.copy-btn {
  padding: 10px 16px;
  background: linear-gradient(135deg, var(--color-primary-600), var(--color-primary-700));
  color: white;
  border: none;
  border-radius: var(--radius-lg);
  font-size: 13px;
  cursor: pointer;
}

.tip {
  margin-top: 16px;
  font-size: 12px;
  color: var(--text-secondary);
  text-align: center;
}
</style>