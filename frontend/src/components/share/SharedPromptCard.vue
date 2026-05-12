<template>
  <div class="shared-prompt-card" @click="$emit('view', prompt)">
    <!-- Card Glow Effect -->
    <div class="card-glow"></div>

    <!-- Header -->
    <div class="card-header">
      <div class="author-section">
        <div class="author-avatar" :style="{ background: getAvatarGradient(prompt.authorNickname) }">
          {{ prompt.authorNickname[0].toUpperCase() }}
        </div>
        <div class="author-meta">
          <span class="author-name">@{{ prompt.authorNickname }}</span>
          <span class="publish-time">{{ formatTime(prompt.createdAt) }}</span>
        </div>
      </div>
      <div class="card-type-badge">
        <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <path d="M4 12v8a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2v-8"/>
          <polyline points="16 6 12 2 8 6"/>
          <line x1="12" y1="2" x2="12" y2="15"/>
        </svg>
        分享
      </div>
    </div>

    <!-- Description -->
    <div class="card-body">
      <h3 class="prompt-title">{{ truncatedDescription }}</h3>

      <div class="prompt-preview">
        <div class="preview-label">
          <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/>
            <polyline points="14 2 14 8 20 8"/>
          </svg>
          提示词内容
        </div>
        <pre class="preview-text">{{ truncatedContent }}</pre>
      </div>
    </div>

    <!-- Footer -->
    <div class="card-footer">
      <div class="stats">
        <div class="stat-item views">
          <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/>
            <circle cx="12" cy="12" r="3"/>
          </svg>
          <span>{{ formatCount(prompt.viewCount) }}</span>
        </div>
        <div class="stat-item likes" :class="{ liked: isLiked }" @click.stop="handleLike">
          <svg width="14" height="14" viewBox="0 0 24 24" :fill="isLiked ? 'currentColor' : 'none'" stroke="currentColor" stroke-width="2">
            <path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z"/>
          </svg>
          <span>{{ formatCount(prompt.likeCount) }}</span>
        </div>
      </div>

      <div class="actions">
        <button class="btn-view" @click.stop="$emit('view', prompt)">
          <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/>
            <circle cx="12" cy="12" r="3"/>
          </svg>
          查看
        </button>
        <button class="btn-contact" @click.stop="$emit('contact', prompt)">
          <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M4 4h16c1.1 0 2 .9 2 2v12c0 1.1-.9 2-2 2H4c-1.1 0-2-.9-2-2V6c0-1.1.9-2 2-2z"/>
            <polyline points="22,6 12,13 2,6"/>
          </svg>
          联系
        </button>
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

const isLiked = ref(props.prompt.isLiked || false);

const truncatedContent = computed(() => {
  const content = props.prompt.promptContent;
  if (content.length > 120) {
    return content.substring(0, 120) + '...';
  }
  return content;
});

const truncatedDescription = computed(() => {
  const desc = props.prompt.description;
  if (desc.length > 60) {
    return desc.substring(0, 60) + '...';
  }
  return desc;
});

const formatTime = (time: string) => {
  const date = new Date(time);
  const now = new Date();
  const diff = now.getTime() - date.getTime();
  const days = Math.floor(diff / (1000 * 60 * 60 * 24));

  if (days === 0) {
    return '今天';
  } else if (days === 1) {
    return '昨天';
  } else if (days < 7) {
    return `${days}天前`;
  } else {
    return date.toLocaleDateString('zh-CN', { month: 'short', day: 'numeric' });
  }
};

const formatCount = (count: number) => {
  if (count >= 1000) {
    return (count / 1000).toFixed(1) + 'k';
  }
  return count.toString();
};

const getAvatarGradient = (name: string) => {
  const gradients = [
    'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
    'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)',
    'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)',
    'linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)',
    'linear-gradient(135deg, #fa709a 0%, #fee140 100%)',
    'linear-gradient(135deg, #a8edea 0%, #fed6e3 100%)',
    'linear-gradient(135deg, #5ee7df 0%, #b490ca 100%)',
    'linear-gradient(135deg, #d299c2 0%, #fef9d7 100%)',
  ];
  const index = name.charCodeAt(0) % gradients.length;
  return gradients[index];
};

const handleLike = async () => {
  if (isLiked.value) {
    await shareApi.unlike(props.prompt.id);
    props.prompt.likeCount--;
  } else {
    await shareApi.like(props.prompt.id);
    props.prompt.likeCount++;
  }
  isLiked.value = !isLiked.value;
};
</script>

<style scoped>
.shared-prompt-card {
  position: relative;
  background: var(--bg-card);
  border-radius: var(--radius-2xl);
  padding: 0;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  display: flex;
  flex-direction: column;
  height: 100%;
  border: 1px solid transparent;
}

.shared-prompt-card:hover {
  transform: translateY(-4px);
  border-color: var(--color-primary-200);
  box-shadow:
    0 20px 40px rgba(0, 0, 0, 0.08),
    0 0 0 1px rgba(59, 130, 246, 0.05);
}

.card-glow {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 3px;
  background: linear-gradient(90deg, #3b82f6, #8b5cf6, #ec4899, #f59e0b);
  opacity: 0;
  transition: opacity 0.3s;
}

.shared-prompt-card:hover .card-glow {
  opacity: 1;
}

/* Header */
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  padding: 20px 20px 0;
}

.author-section {
  display: flex;
  align-items: center;
  gap: 12px;
}

.author-avatar {
  width: 40px;
  height: 40px;
  border-radius: 12px;
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
  font-weight: 700;
  flex-shrink: 0;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.author-meta {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.author-name {
  font-weight: 600;
  color: var(--text-primary);
  font-size: 14px;
}

.publish-time {
  font-size: 11px;
  color: var(--text-secondary);
}

.card-type-badge {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 4px 10px;
  background: var(--color-primary-50);
  color: var(--color-primary-600);
  border-radius: 999px;
  font-size: 11px;
  font-weight: 600;
}

/* Body */
.card-body {
  padding: 16px 20px;
  flex: 1;
  display: flex;
  flex-direction: column;
}

.prompt-title {
  font-size: 15px;
  font-weight: 600;
  color: var(--text-primary);
  margin: 0 0 14px;
  line-height: 1.4;
}

.prompt-preview {
  background: linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%);
  border-radius: var(--radius-lg);
  padding: 14px;
  flex: 1;
  min-height: 90px;
  border: 1px solid rgba(148, 163, 184, 0.1);
}

.preview-label {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 10px;
  font-weight: 600;
  color: var(--text-secondary);
  text-transform: uppercase;
  letter-spacing: 0.5px;
  margin-bottom: 8px;
}

.preview-text {
  margin: 0;
  font-size: 12px;
  color: var(--text-secondary);
  white-space: pre-wrap;
  word-break: break-word;
  font-family: inherit;
  line-height: 1.6;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

/* Footer */
.card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  background: #fafbfc;
  border-top: 1px solid rgba(148, 163, 184, 0.1);
}

.stats {
  display: flex;
  gap: 16px;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 5px;
  font-size: 13px;
  color: var(--text-secondary);
  transition: color 0.2s;
}

.stat-item.views svg {
  color: #64748b;
}

.stat-item.likes {
  cursor: pointer;
}

.stat-item.likes svg {
  transition: all 0.2s;
}

.stat-item.likes:hover svg {
  transform: scale(1.15);
}

.stat-item.likes.liked {
  color: #ef4444;
}

.stat-item.likes.liked svg {
  color: #ef4444;
}

.actions {
  display: flex;
  gap: 8px;
}

.btn-view,
.btn-contact {
  display: flex;
  align-items: center;
  gap: 5px;
  padding: 7px 14px;
  border-radius: var(--radius-lg);
  font-size: 12px;
  font-weight: 600;
  border: none;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-view {
  background: white;
  color: var(--text-primary);
  border: 1px solid var(--border-color);
}

.btn-view:hover {
  background: var(--color-primary-50);
  border-color: var(--color-primary-200);
  color: var(--color-primary-600);
}

.btn-contact {
  background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
  color: white;
  box-shadow: 0 4px 12px rgba(37, 99, 235, 0.25);
}

.btn-contact:hover {
  transform: translateY(-1px);
  box-shadow: 0 6px 16px rgba(37, 99, 235, 0.35);
}

.btn-contact:active {
  transform: translateY(0);
}
</style>
