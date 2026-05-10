<template>
  <AppLayout>
    <template #main>
      <div class="share-page">
        <div class="hero-section">
          <h1>提示词分享</h1>
          <p>分享你的宝贵提示词财富，帮助更多人</p>
          <button class="publish-btn" @click="showPublishModal = true">
            发布提示词
          </button>
        </div>

        <div class="filter-section">
          <div class="sort-tabs">
            <button
              :class="{ active: sortType === 'latest' }"
              @click="sortType = 'latest'; loadData()"
            >
              最新
            </button>
            <button
              :class="{ active: sortType === 'popular' }"
              @click="sortType = 'popular'; loadData()"
            >
              热门
            </button>
          </div>
        </div>

        <div class="prompts-grid" v-if="!loading && list.length > 0">
          <SharedPromptCard
            v-for="prompt in list"
            :key="prompt.id"
            :prompt="prompt"
            @view="handleView"
            @contact="handleContact"
          />
        </div>

        <div v-if="loading" class="loading-state">
          <div class="spinner"></div>
          <span>加载中...</span>
        </div>

        <div v-if="!loading && list.length === 0" class="empty-state">
          暂无分享的提示词，成为第一个分享者吧！
        </div>

        <div v-if="hasMore && !loading" class="load-more">
          <button @click="loadMore">加载更多</button>
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
import { ref, onMounted } from 'vue';
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

const showPublishModal = ref(false);
const selectedPrompt = ref<SharedPrompt | null>(null);
const viewingPrompt = ref<SharedPrompt | null>(null);

const loadData = async () => {
  loading.value = true;
  try {
    const result = await shareApi.getList({ page: page.value, size: size.value, sort: sortType.value });
    list.value = result.list;
    hasMore.value = result.list.length === size.value;
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
    alert(`发布成功！\n\n您的删除令牌：${result.deleteToken}\n\n请妥善保管此令牌，用于后续删除您的分享。`);
    showPublishModal.value = false;
    loadData();
  } catch (error: any) {
    alert('发布失败：' + (error.message || '未知错误'));
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

.hero-section {
  text-align: center;
  padding: 60px 20px;
  background: linear-gradient(135deg, var(--color-primary-50), var(--bg-card));
  border-radius: var(--radius-2xl);
  margin-bottom: 32px;
}

.hero-section h1 {
  font-size: 32px;
  margin-bottom: 12px;
}

.hero-section p {
  color: var(--text-secondary);
  margin-bottom: 24px;
}

.publish-btn {
  padding: 14px 32px;
  background: linear-gradient(135deg, var(--color-primary-600), var(--color-primary-700));
  color: white;
  border: none;
  border-radius: var(--radius-xl);
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  box-shadow: 0 8px 24px rgba(59, 130, 246, 0.35);
  transition: all 0.3s;
}

.publish-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 12px 32px rgba(59, 130, 246, 0.45);
}

.filter-section {
  margin-bottom: 24px;
}

.sort-tabs {
  display: flex;
  gap: 8px;
}

.sort-tabs button {
  padding: 8px 20px;
  border: 1px solid var(--border-color);
  background: var(--bg-card);
  border-radius: var(--radius-lg);
  font-size: 14px;
  cursor: pointer;
  transition: all 0.2s;
}

.sort-tabs button.active {
  background: var(--color-primary-600);
  color: white;
  border-color: var(--color-primary-600);
}

.prompts-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(340px, 1fr));
  gap: 20px;
}

.loading-state, .empty-state {
  text-align: center;
  padding: 60px 20px;
  color: var(--text-secondary);
}

.spinner {
  width: 32px;
  height: 32px;
  border: 3px solid var(--border-color);
  border-top-color: var(--color-primary-600);
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin: 0 auto 12px;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.load-more {
  text-align: center;
  margin-top: 32px;
}

.load-more button {
  padding: 10px 24px;
  background: var(--bg-secondary);
  border: 1px solid var(--border-color);
  border-radius: var(--radius-lg);
  font-size: 14px;
  cursor: pointer;
}

.load-more button:hover {
  background: var(--bg-tertiary);
}
</style>