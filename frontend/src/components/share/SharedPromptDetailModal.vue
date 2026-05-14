<template>
  <div class="modal-overlay" @click.self="$emit('close')">
    <div class="modal-container">
      <!-- Header -->
      <div class="modal-header">
        <div class="header-content">
          <div class="header-badge">
            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M4 12v8a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2v-8"/>
              <polyline points="16 6 12 2 8 6"/>
              <line x1="12" y1="2" x2="12" y2="15"/>
            </svg>
            分享提示词
          </div>
          <h2>提示词详情</h2>
        </div>
        <button class="close-btn" @click="$emit('close')">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <line x1="18" y1="6" x2="6" y2="18"/>
            <line x1="6" y1="6" x2="18" y2="18"/>
          </svg>
        </button>
      </div>

      <!-- Body -->
      <div class="modal-body" v-if="promptData">
        <!-- Author Card -->
        <div class="author-card">
          <div class="author-avatar" :style="{ background: getAvatarGradient(promptData.authorNickname || '') }">
            {{ (promptData.authorNickname || '?')[0].toUpperCase() }}
          </div>
          <div class="author-info">
            <span class="author-name">@{{ promptData.authorNickname || '匿名用户' }}</span>
            <span class="publish-time">
              <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <circle cx="12" cy="12" r="10"/>
                <polyline points="12 6 12 12 16 14"/>
              </svg>
              {{ formatTime(promptData.createdAt) }} 发布
            </span>
          </div>
          <div class="author-stats">
            <div class="stat">
              <span class="stat-value">{{ promptData.viewCount }}</span>
              <span class="stat-label">浏览</span>
            </div>
            <div class="stat">
              <span class="stat-value">{{ promptData.likeCount }}</span>
              <span class="stat-label">点赞</span>
            </div>
          </div>
        </div>

        <!-- Description -->
        <div class="content-section">
          <div class="section-header">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"/>
              <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"/>
            </svg>
            <h3>描述</h3>
          </div>
          <p class="description-text">{{ promptData.description }}</p>
        </div>

        <!-- Prompt Content -->
        <div class="content-section prompt-section">
          <div class="section-header">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <polyline points="16 18 22 12 16 6"/>
              <polyline points="8 6 2 12 8 18"/>
            </svg>
            <h3>提示词内容</h3>
          </div>
          <div class="markdown-body" v-html="renderedContent"></div>
        </div>

        <!-- Actions -->
        <div class="modal-actions">
          <button class="btn-like" :class="{ liked: isLiked }" @click="handleLike">
            <svg width="18" height="18" viewBox="0 0 24 24" :fill="isLiked ? 'currentColor' : 'none'" stroke="currentColor" stroke-width="2">
              <path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z"/>
            </svg>
            {{ isLiked ? '已点赞' : '点赞' }}
          </button>
          <button class="btn-copy" :class="{ success: copySuccess }" @click="copyContent">
            <svg v-if="!copySuccess" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <rect x="9" y="9" width="13" height="13" rx="2" ry="2"/>
              <path d="M5 15H4a2 2 0 0 1-2-2V4a2 2 0 0 1 2-2h9a2 2 0 0 1 2 2v1"/>
            </svg>
            <svg v-else width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <polyline points="20 6 9 17 4 12"/>
            </svg>
            {{ copySuccess ? '已复制' : '复制提示词' }}
          </button>
          <button class="btn-contact" @click="$emit('contact', promptData)">
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M4 4h16c1.1 0 2 .9 2 2v12c0 1.1-.9 2-2 2H4c-1.1 0-2-.9-2-2V6c0-1.1.9-2 2-2z"/>
              <polyline points="22,6 12,13 2,6"/>
            </svg>
            联系作者
          </button>
        </div>
      </div>

      <!-- Loading -->
      <div v-if="loading" class="loading-state">
        <div class="spinner"></div>
        <span>加载中...</span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import MarkdownIt from 'markdown-it';
import hljs from 'highlight.js';
import type { SharedPrompt } from '@/types';
import { shareApi } from '@/api/share';

const props = defineProps<{
  prompt: SharedPrompt;
}>();

const emit = defineEmits<{
  (e: 'close'): void;
  (e: 'contact', prompt: SharedPrompt): void;
}>();

const loading = ref(false);
const promptData = ref<SharedPrompt | null>(null);
const isLiked = ref(false);
const copySuccess = ref(false);

const highlightCode = (str: string, lang: string) => {
  if (lang && hljs.getLanguage(lang)) {
    try {
      return '<pre class="hljs-code"><code>' +
        hljs.highlight(str, { language: lang, ignoreIllegals: true }).value +
        '</code></pre>';
    } catch (_) {}
  }
  return '<pre class="hljs-code"><code>' + str.replace(/[&<>"']/g, (c) => ({
    '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;'
  })[c]!) + '</code></pre>';
};

const md = new MarkdownIt({
  html: true,
  linkify: true,
  typographer: true,
  breaks: true,
  highlight: highlightCode
});

const renderedContent = computed(() => {
  if (!promptData.value?.promptContent) return '';
  return md.render(promptData.value.promptContent);
});

const fetchDetail = async () => {
  loading.value = true;
  try {
    const data = await shareApi.getById(props.prompt.id);
    promptData.value = data;
    isLiked.value = data.isLiked || false;
  } catch (error) {
    console.error('Failed to fetch detail:', error);
    (window as any).showToast?.({ message: '加载详情失败', type: 'error' });
  } finally {
    loading.value = false;
  }
};

const formatTime = (time: string) => {
  const date = new Date(time);
  return date.toLocaleDateString('zh-CN', { year: 'numeric', month: 'long', day: 'numeric' });
};

const handleLike = async () => {
  if (!promptData.value) return;
  try {
    if (isLiked.value) {
      await shareApi.unlike(promptData.value.id);
      promptData.value.likeCount--;
    } else {
      await shareApi.like(promptData.value.id);
      promptData.value.likeCount++;
    }
    isLiked.value = !isLiked.value;
  } catch (error) {
    console.error('Like failed:', error);
  }
};

const copyContent = async () => {
  if (!promptData.value?.promptContent) return;
  try {
    await navigator.clipboard.writeText(promptData.value.promptContent);
    copySuccess.value = true;
    setTimeout(() => { copySuccess.value = false; }, 2000);
  } catch (error) {
    (window as any).showToast?.({ message: '复制失败', type: 'error' });
  }
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

onMounted(() => {
  fetchDetail();
});
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
  max-width: 720px;
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
  padding: 24px 28px;
  border-bottom: 1px solid var(--border-color);
  background: linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%);
}

.header-content {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.header-badge {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 5px 12px;
  background: var(--color-primary-50);
  color: var(--color-primary-600);
  border-radius: 999px;
  font-size: 11px;
  font-weight: 600;
  width: fit-content;
}

.modal-header h2 {
  margin: 0;
  font-size: 22px;
  font-weight: 800;
  color: var(--text-primary);
  letter-spacing: -0.5px;
}

.close-btn {
  background: white;
  border: 1px solid var(--border-color);
  width: 36px;
  height: 36px;
  border-radius: var(--radius-lg);
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--text-secondary);
  transition: all 0.2s;
}

.close-btn:hover {
  background: #fee2e2;
  border-color: #fecaca;
  color: #ef4444;
}

/* Body */
.modal-body {
  padding: 28px;
  overflow-y: auto;
  flex: 1;
}

/* Author Card */
.author-card {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px;
  background: linear-gradient(135deg, #0f172a 0%, #1e293b 100%);
  border-radius: var(--radius-2xl);
  margin-bottom: 24px;
}

.author-avatar {
  width: 56px;
  height: 56px;
  border-radius: 16px;
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 22px;
  font-weight: 700;
  flex-shrink: 0;
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.3);
}

.author-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.author-name {
  font-weight: 700;
  color: #f8fafc;
  font-size: 16px;
}

.publish-time {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 12px;
  color: #94a3b8;
}

.author-stats {
  display: flex;
  gap: 24px;
}

.author-stats .stat {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 2px;
}

.author-stats .stat-value {
  font-size: 20px;
  font-weight: 800;
  color: #f8fafc;
}

.author-stats .stat-label {
  font-size: 10px;
  color: #64748b;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

/* Content Sections */
.content-section {
  margin-bottom: 24px;
}

.section-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 12px;
}

.section-header svg {
  color: var(--color-primary-600);
}

.section-header h3 {
  margin: 0;
  font-size: 14px;
  font-weight: 700;
  color: var(--text-primary);
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.description-text {
  margin: 0;
  font-size: 15px;
  line-height: 1.7;
  color: var(--text-secondary);
}

.prompt-section .markdown-body {
  background: #0f172a;
  padding: 20px;
  border-radius: var(--radius-xl);
  font-size: 13px;
  line-height: 1.8;
  max-height: 350px;
  overflow-y: auto;
  color: #e2e8f0;
}

.markdown-body :deep(h1),
.markdown-body :deep(h2),
.markdown-body :deep(h3) {
  color: #f8fafc;
  margin-top: 1em;
  margin-bottom: 0.5em;
  font-weight: 700;
}

.markdown-body :deep(h2) {
  color: #60a5fa;
  font-size: 1.1em;
  border-bottom: 1px solid #334155;
  padding-bottom: 6px;
}

.markdown-body :deep(p) {
  margin: 0 0 0.8em 0;
  color: #cbd5e1;
}

.markdown-body :deep(ul),
.markdown-body :deep(ol) {
  margin: 0.5em 0;
  padding-left: 1.5em;
  color: #cbd5e1;
}

.markdown-body :deep(li) {
  margin: 4px 0;
}

.markdown-body :deep(code) {
  padding: 0.2em 0.4em;
  background: #1e293b;
  border-radius: 4px;
  font-family: var(--font-mono);
  font-size: 0.9em;
  color: #fbbf24;
}

.markdown-body :deep(pre) {
  margin: 1em 0;
  border-radius: var(--radius-lg);
  overflow: hidden;
  background: #1e293b;
}

.markdown-body :deep(pre code) {
  display: block;
  padding: 1em;
  background: transparent;
  color: #e2e8f0;
  font-size: 0.85rem;
  line-height: 1.6;
}

/* Actions */
.modal-actions {
  display: flex;
  gap: 12px;
  padding-top: 20px;
  border-top: 1px solid var(--border-color);
}

.btn-like,
.btn-copy,
.btn-contact {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 14px 20px;
  border-radius: var(--radius-xl);
  font-size: 14px;
  font-weight: 600;
  border: none;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-like {
  background: #f1f5f9;
  color: var(--text-secondary);
}

.btn-like:hover {
  background: #fee2e2;
  color: #ef4444;
}

.btn-like.liked {
  background: #fef2f2;
  color: #ef4444;
}

.btn-copy {
  background: #f1f5f9;
  color: var(--text-primary);
}

.btn-copy:hover {
  background: #e2e8f0;
}

.btn-copy.success {
  background: #dcfce7;
  color: #16a34a;
}

.btn-contact {
  background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
  color: white;
  box-shadow: 0 4px 16px rgba(37, 99, 235, 0.3);
}

.btn-contact:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(37, 99, 235, 0.4);
}

/* Loading */
.loading-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 80px 20px;
  color: var(--text-secondary);
}

.spinner {
  width: 36px;
  height: 36px;
  border: 3px solid var(--border-color);
  border-top-color: var(--color-primary-600);
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: 12px;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}
</style>
