<template>
  <AppLayout>
    <template #main>
      <div class="share-page">
        <!-- Hero Section -->
        <div class="hero-section">
          <div class="hero-bg-pattern"></div>
          <div class="hero-content">
            <div class="hero-badge">
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <circle cx="18" cy="5" r="3"/>
                <circle cx="6" cy="12" r="3"/>
                <circle cx="18" cy="19" r="3"/>
                <path d="M8.59 13.51l6.83 3.98M15.41 6.51l-6.82 3.98"/>
              </svg>
              提示词分享社区
            </div>
            <h1 class="hero-title">发现优秀的 AI 提示词</h1>
            <p class="hero-desc">探索来自创作者社区的精选提示词，让你的 AI 创作更高效</p>
            <button class="publish-btn" @click="showPublishModal = true">
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M12 5v14M5 12h14"/>
              </svg>
              发布提示词
            </button>
          </div>
          <div class="hero-stats">
            <div class="stat-item">
              <span class="stat-number">{{ totalCount }}</span>
              <span class="stat-label">分享总数</span>
            </div>
            <div class="stat-divider"></div>
            <div class="stat-item">
              <span class="stat-number">{{ totalViews }}</span>
              <span class="stat-label">总浏览</span>
            </div>
          </div>
        </div>

        <!-- Filter Section -->
        <div class="filter-section">
          <div class="filter-left">
            <h2 class="section-title">
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"/>
              </svg>
              社区分享
            </h2>
          </div>
          <div class="filter-right">
            <div class="sort-tabs">
              <button
                :class="{ active: sortType === 'latest' }"
                @click="sortType = 'latest'; loadData()"
              >
                <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <circle cx="12" cy="12" r="10"/>
                  <polyline points="12 6 12 12 16 14"/>
                </svg>
                最新
              </button>
              <button
                :class="{ active: sortType === 'popular' }"
                @click="sortType = 'popular'; loadData()"
              >
                <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/>
                  <circle cx="9" cy="7" r="4"/>
                  <path d="M23 21v-2a4 4 0 0 0-3-3.87M16 3.13a4 4 0 0 1 0 7.75"/>
                </svg>
                热门
              </button>
            </div>
          </div>
        </div>

        <!-- Prompts Grid -->
        <div class="prompts-grid" v-if="!loading && list.length > 0">
          <SharedPromptCard
            v-for="prompt in list"
            :key="prompt.id"
            :prompt="prompt"
            @view="handleView"
            @contact="handleContact"
          />
        </div>

        <!-- Loading State -->
        <div v-if="loading && list.length === 0" class="loading-state">
          <div class="loading-cards">
            <div class="skeleton-card" v-for="i in 6" :key="i">
              <div class="skeleton-header">
                <div class="skeleton-avatar"></div>
                <div class="skeleton-meta"></div>
              </div>
              <div class="skeleton-desc"></div>
              <div class="skeleton-preview"></div>
              <div class="skeleton-footer"></div>
            </div>
          </div>
        </div>

        <!-- Empty State -->
        <div v-if="!loading && list.length === 0" class="empty-state">
          <div class="empty-icon">
            <svg width="64" height="64" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
              <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"/>
              <path d="M8 10h.01M12 10h.01M16 10h.01"/>
            </svg>
          </div>
          <h3>暂无分享内容</h3>
          <p>成为第一个分享者，让更多人看到你的创意！</p>
          <button class="publish-btn secondary" @click="showPublishModal = true">
            发布提示词
          </button>
        </div>

        <!-- Load More -->
        <div v-if="hasMore && !loading" class="load-more">
          <button @click="loadMore">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <polyline points="23 4 23 10 17 10"/>
              <polyline points="1 20 1 14 7 14"/>
              <path d="M3.51 9a9 9 0 0 1 14.85-3.36L23 10M1 14l4.64 4.36A9 9 0 0 0 20.49 15"/>
            </svg>
            加载更多
          </button>
        </div>
      </div>

      <PublishPromptModal
        v-if="showPublishModal"
        @close="showPublishModal = false"
        @submit="handlePublish"
      />

      <ContactAuthorModal
        v-if="selectedPrompt"
        :prompt="selectedPrompt"
        @close="selectedPrompt = null"
      />

      <SharedPromptDetailModal
        v-if="viewingPrompt"
        :prompt="viewingPrompt"
        @close="viewingPrompt = null"
      />
    </template>
  </AppLayout>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import AppLayout from '@/components/layout/AppLayout.vue';
import SharedPromptCard from '@/components/share/SharedPromptCard.vue';
import PublishPromptModal from '@/components/share/PublishPromptModal.vue';
import ContactAuthorModal from '@/components/share/ContactAuthorModal.vue';
import SharedPromptDetailModal from '@/components/share/SharedPromptDetailModal.vue';
import { shareApi } from '@/api/share';
import type { SharedPrompt, ShareRequest } from '@/types';

const loading = ref(false);
const list = ref<SharedPrompt[]>([]);
const page = ref(1);
const size = ref(20);
const hasMore = ref(true);
const sortType = ref<'latest' | 'popular'>('latest');
const totalCount = ref(0);
const totalViews = ref(0);

const showPublishModal = ref(false);
const selectedPrompt = ref<SharedPrompt | null>(null);
const viewingPrompt = ref<SharedPrompt | null>(null);

const loadData = async () => {
  loading.value = true;
  try {
    const result = await shareApi.getList({ page: page.value, size: size.value, sort: sortType.value });
    list.value = result.list;
    hasMore.value = result.list.length === size.value;
    totalCount.value = result.total || result.list.length;
    totalViews.value = result.totalViews || 0;
  } finally {
    loading.value = false;
  }
};

const loadMore = async () => {
  page.value++;
  loading.value = true;
  try {
    const result = await shareApi.getList({ page: page.value, size: size.value, sort: sortType.value });
    list.value.push(...result.list);
    hasMore.value = result.list.length === size.value;
  } finally {
    loading.value = false;
  }
};

const handlePublish = async (data: ShareRequest) => {
  try {
    const result = await shareApi.publish(data);
    (window as any).showToast?.({ message: `发布成功！删除令牌：${result.deleteToken}`, type: 'success', duration: 5000 });
    showPublishModal.value = false;
    loadData();
  } catch (error: any) {
    (window as any).showToast?.({ message: '发布失败：' + (error.message || '未知错误'), type: 'error' });
  }
};

const handleView = (prompt: SharedPrompt) => {
  viewingPrompt.value = prompt;
};

const handleContact = (prompt: SharedPrompt) => {
  selectedPrompt.value = prompt;
};

onMounted(() => {
  loadData();
});
</script>

<style scoped>
.share-page {
  padding: 0 24px 40px;
}

/* Hero Section */
.hero-section {
  position: relative;
  background: linear-gradient(135deg, #0f172a 0%, #1e293b 50%, #0f172a 100%);
  border-radius: var(--radius-3xl);
  padding: 48px 40px;
  margin-bottom: 32px;
  overflow: hidden;
  text-align: center;
}

.hero-bg-pattern {
  position: absolute;
  inset: 0;
  background-image:
    radial-gradient(circle at 20% 50%, rgba(59, 130, 246, 0.15) 0%, transparent 50%),
    radial-gradient(circle at 80% 50%, rgba(139, 92, 246, 0.15) 0%, transparent 50%),
    radial-gradient(circle at 50% 100%, rgba(59, 130, 246, 0.1) 0%, transparent 40%);
  pointer-events: none;
}

.hero-content {
  position: relative;
  z-index: 1;
}

.hero-badge {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 6px 14px;
  background: rgba(59, 130, 246, 0.15);
  border: 1px solid rgba(59, 130, 246, 0.3);
  border-radius: 999px;
  color: #60a5fa;
  font-size: 12px;
  font-weight: 600;
  margin-bottom: 20px;
  letter-spacing: 0.5px;
}

.hero-title {
  font-size: 36px;
  font-weight: 800;
  color: #f8fafc;
  margin: 0 0 12px;
  letter-spacing: -0.5px;
  background: linear-gradient(135deg, #f8fafc 0%, #cbd5e1 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.hero-desc {
  font-size: 16px;
  color: #94a3b8;
  margin: 0 0 28px;
  max-width: 400px;
  margin-left: auto;
  margin-right: auto;
}

.hero-section .publish-btn {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 14px 28px;
  background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
  color: white;
  border: none;
  border-radius: var(--radius-xl);
  font-size: 15px;
  font-weight: 600;
  cursor: pointer;
  box-shadow: 0 8px 24px rgba(37, 99, 235, 0.4);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.hero-section .publish-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 12px 32px rgba(37, 99, 235, 0.5);
}

.hero-stats {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 32px;
  margin-top: 36px;
  padding-top: 28px;
  border-top: 1px solid rgba(148, 163, 184, 0.1);
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
}

.stat-number {
  font-size: 28px;
  font-weight: 800;
  color: #f8fafc;
}

.stat-label {
  font-size: 12px;
  color: #64748b;
  text-transform: uppercase;
  letter-spacing: 1px;
}

.stat-divider {
  width: 1px;
  height: 40px;
  background: rgba(148, 163, 184, 0.2);
}

/* Filter Section */
.filter-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.section-title {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 18px;
  font-weight: 700;
  color: var(--text-primary);
  margin: 0;
}

.section-title svg {
  color: var(--color-primary-600);
}

.sort-tabs {
  display: flex;
  gap: 6px;
  background: var(--bg-secondary);
  padding: 4px;
  border-radius: var(--radius-lg);
}

.sort-tabs button {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 16px;
  border: none;
  background: transparent;
  border-radius: var(--radius-md);
  font-size: 13px;
  font-weight: 500;
  color: var(--text-secondary);
  cursor: pointer;
  transition: all 0.2s;
}

.sort-tabs button:hover {
  color: var(--text-primary);
}

.sort-tabs button.active {
  background: white;
  color: var(--color-primary-600);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

/* Prompts Grid */
.prompts-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(340px, 1fr));
  gap: 20px;
}

/* Loading State */
.loading-state {
  padding: 20px 0;
}

.loading-cards {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(340px, 1fr));
  gap: 20px;
}

.skeleton-card {
  background: var(--bg-card);
  border-radius: var(--radius-2xl);
  padding: 20px;
}

.skeleton-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
}

.skeleton-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: linear-gradient(90deg, #f0f0f0 25%, #e0e0e0 50%, #f0f0f0 75%);
  background-size: 200% 100%;
  animation: shimmer 1.5s infinite;
}

.skeleton-meta {
  width: 120px;
  height: 14px;
  border-radius: 4px;
  background: linear-gradient(90deg, #f0f0f0 25%, #e0e0e0 50%, #f0f0f0 75%);
  background-size: 200% 100%;
  animation: shimmer 1.5s infinite;
}

.skeleton-desc {
  width: 100%;
  height: 16px;
  border-radius: 4px;
  margin-bottom: 12px;
  background: linear-gradient(90deg, #f0f0f0 25%, #e0e0e0 50%, #f0f0f0 75%);
  background-size: 200% 100%;
  animation: shimmer 1.5s infinite;
}

.skeleton-preview {
  width: 100%;
  height: 80px;
  border-radius: var(--radius-lg);
  margin-bottom: 16px;
  background: linear-gradient(90deg, #f0f0f0 25%, #e0e0e0 50%, #f0f0f0 75%);
  background-size: 200% 100%;
  animation: shimmer 1.5s infinite;
}

.skeleton-footer {
  width: 60%;
  height: 32px;
  border-radius: var(--radius-lg);
  background: linear-gradient(90deg, #f0f0f0 25%, #e0e0e0 50%, #f0f0f0 75%);
  background-size: 200% 100%;
  animation: shimmer 1.5s infinite;
}

@keyframes shimmer {
  0% { background-position: 200% 0; }
  100% { background-position: -200% 0; }
}

/* Empty State */
.empty-state {
  text-align: center;
  padding: 80px 20px;
}

.empty-icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 120px;
  height: 120px;
  background: var(--bg-secondary);
  border-radius: 50%;
  margin-bottom: 24px;
  color: var(--text-secondary);
}

.empty-state h3 {
  font-size: 20px;
  font-weight: 700;
  color: var(--text-primary);
  margin: 0 0 8px;
}

.empty-state p {
  font-size: 14px;
  color: var(--text-secondary);
  margin: 0 0 24px;
}

.empty-state .publish-btn.secondary {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 12px 24px;
  background: var(--bg-secondary);
  color: var(--text-primary);
  border: 1px solid var(--border-color);
  border-radius: var(--radius-xl);
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
}

.empty-state .publish-btn.secondary:hover {
  background: var(--color-primary-50);
  border-color: var(--color-primary-200);
  color: var(--color-primary-600);
}

/* Load More */
.load-more {
  text-align: center;
  margin-top: 40px;
}

.load-more button {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 12px 28px;
  background: var(--bg-card);
  border: 1px solid var(--border-color);
  border-radius: var(--radius-xl);
  font-size: 14px;
  font-weight: 500;
  color: var(--text-secondary);
  cursor: pointer;
  transition: all 0.2s;
}

.load-more button:hover {
  background: var(--color-primary-50);
  border-color: var(--color-primary-200);
  color: var(--color-primary-600);
}
</style>
