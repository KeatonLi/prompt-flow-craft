<template>
  <div class="shared-prompt-card">
    <div class="card-header">
      <span class="author">@{{ prompt.authorNickname }}</span>
      <span class="time">{{ formatTime(prompt.createdAt) }}</span>
    </div>
    <div class="description">{{ prompt.description }}</div>
    <div class="prompt-preview">
      <pre>{{ truncatedContent }}</pre>
    </div>
    <div class="card-footer">
      <div class="stats">
        <span class="stat-item">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/>
            <circle cx="12" cy="12" r="3"/>
          </svg>
          {{ prompt.viewCount }}
        </span>
        <span class="stat-item" :class="{ liked: isLiked }" @click="handleLike">
          <svg width="16" height="16" viewBox="0 0 24 24" :fill="isLiked ? 'currentColor' : 'none'" stroke="currentColor" stroke-width="2">
            <path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z"/>
          </svg>
          {{ prompt.likeCount }}
        </span>
      </div>
      <div class="actions">
        <button class="btn-view" @click="$emit('view', prompt)">查看详情</button>
        <button class="btn-contact" @click="$emit('contact', prompt)">联系作者</button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue';
import type { SharedPrompt } from '@/types';
import { shareApi } from '@/api/share';

const props = defineProps<{
  prompt: SharedPrompt;
}>();

const emit = defineEmits<{
  (e: 'view', prompt: SharedPrompt): void;
  (e: 'contact', prompt: SharedPrompt): void;
}>();

const isLiked = ref(false);

const truncatedContent = computed(() => {
  const content = props.prompt.promptContent;
  if (content.length > 150) {
    return content.substring(0, 150) + '...';
  }
  return content;
});

const formatTime = (time: string) => {
  const date = new Date(time);
  return date.toLocaleDateString('zh-CN', { month: 'short', day: 'numeric' });
};

const handleLike = async () => {
  if (isLiked.value) {
    await shareApi.unlike(props.prompt.id);
  } else {
    await shareApi.like(props.prompt.id);
  }
  isLiked.value = !isLiked.value;
};
</script>

<style scoped>
.shared-prompt-card {
  background: var(--bg-card);
  border: 1px solid var(--border-color);
  border-radius: var(--radius-2xl);
  padding: 20px;
  transition: all 0.3s;
}

.shared-prompt-card:hover {
  box-shadow: var(--shadow-lg);
  transform: translateY(-2px);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.author {
  font-weight: 600;
  color: var(--color-primary-600);
}

.time {
  font-size: 12px;
  color: var(--text-secondary);
}

.description {
  font-size: 14px;
  color: var(--text-primary);
  margin-bottom: 12px;
  line-height: 1.5;
}

.prompt-preview {
  background: var(--bg-secondary);
  border-radius: var(--radius-lg);
  padding: 12px;
  margin-bottom: 12px;
}

.prompt-preview pre {
  margin: 0;
  font-size: 12px;
  color: var(--text-secondary);
  white-space: pre-wrap;
  word-break: break-all;
  font-family: inherit;
}

.card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.stats {
  display: flex;
  gap: 16px;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 14px;
  color: var(--text-secondary);
}

.stat-item.liked {
  color: var(--color-danger);
}

.stat-item:not(.liked) {
  cursor: pointer;
}

.actions {
  display: flex;
  gap: 8px;
}

.btn-view, .btn-contact {
  padding: 6px 12px;
  border-radius: var(--radius-lg);
  font-size: 12px;
  border: none;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-view {
  background: var(--bg-secondary);
  color: var(--text-primary);
}

.btn-view:hover {
  background: var(--color-primary-100);
}

.btn-contact {
  background: linear-gradient(135deg, var(--color-primary-600), var(--color-primary-700));
  color: white;
}

.btn-contact:hover {
  opacity: 0.9;
}
</style>