<template>
  <div class="modal-overlay" @click.self="$emit('close')">
    <div class="modal-content">
      <div class="modal-header">
        <h3>提示词详情</h3>
        <button class="close-btn" @click="$emit('close')">×</button>
      </div>
      <div class="modal-body">
        <div class="author-section">
          <div class="author-avatar">{{ prompt.authorNickname[0].toUpperCase() }}</div>
          <div class="author-info">
            <span class="author-name">@{{ prompt.authorNickname }}</span>
            <span class="publish-time">{{ formatTime(prompt.createdAt) }} 发布</span>
          </div>
        </div>

        <div class="description-section">
          <label>描述</label>
          <p>{{ prompt.description }}</p>
        </div>

        <div class="prompt-content-section">
          <label>提示词内容</label>
          <pre>{{ prompt.promptContent }}</pre>
        </div>

        <div class="stats-section">
          <span class="stat">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/>
              <circle cx="12" cy="12" r="3"/>
            </svg>
            {{ prompt.viewCount }} 次浏览
          </span>
          <span class="stat">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z"/>
            </svg>
            {{ prompt.likeCount }} 次点赞
          </span>
        </div>

        <div class="actions">
          <button class="btn-copy" @click="copyContent">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <rect x="9" y="9" width="13" height="13" rx="2" ry="2"/>
              <path d="M5 15H4a2 2 0 0 1-2-2V4a2 2 0 0 1 2-2h9a2 2 0 0 1 2 2v1"/>
            </svg>
            复制提示词
          </button>
          <button class="btn-contact" @click="$emit('contact', prompt)">
            联系作者
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import type { SharedPrompt } from '@/types';

const props = defineProps<{
  prompt: SharedPrompt;
}>();

defineEmits<{
  (e: 'close'): void;
  (e: 'contact', prompt: SharedPrompt): void;
}>();

const formatTime = (time: string) => {
  const date = new Date(time);
  return date.toLocaleDateString('zh-CN', { year: 'numeric', month: 'long', day: 'numeric' });
};

const copyContent = async () => {
  await navigator.clipboard.writeText(props.prompt.promptContent);
  alert('已复制到剪贴板');
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
  max-height: 85vh;
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

.author-section {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 24px;
  padding-bottom: 20px;
  border-bottom: 1px solid var(--border-color);
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

.author-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.author-name {
  font-weight: 600;
  color: var(--color-primary-600);
}

.publish-time {
  font-size: 12px;
  color: var(--text-secondary);
}

.description-section {
  margin-bottom: 20px;
}

.description-section label {
  display: block;
  font-size: 12px;
  color: var(--text-secondary);
  margin-bottom: 8px;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.description-section p {
  margin: 0;
  font-size: 14px;
  line-height: 1.6;
  color: var(--text-primary);
}

.prompt-content-section {
  margin-bottom: 20px;
}

.prompt-content-section label {
  display: block;
  font-size: 12px;
  color: var(--text-secondary);
  margin-bottom: 8px;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.prompt-content-section pre {
  background: var(--bg-secondary);
  padding: 16px;
  border-radius: var(--radius-lg);
  font-size: 13px;
  line-height: 1.6;
  white-space: pre-wrap;
  word-break: break-all;
  margin: 0;
  max-height: 300px;
  overflow: auto;
}

.stats-section {
  display: flex;
  gap: 20px;
  margin-bottom: 24px;
}

.stat {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  color: var(--text-secondary);
}

.actions {
  display: flex;
  gap: 12px;
}

.btn-copy, .btn-contact {
  flex: 1;
  padding: 12px 20px;
  border-radius: var(--radius-lg);
  font-size: 14px;
  border: none;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  transition: all 0.2s;
}

.btn-copy {
  background: var(--bg-secondary);
  color: var(--text-primary);
}

.btn-copy:hover {
  background: var(--bg-tertiary);
}

.btn-contact {
  background: linear-gradient(135deg, var(--color-primary-600), var(--color-primary-700));
  color: white;
}

.btn-contact:hover {
  opacity: 0.9;
}
</style>