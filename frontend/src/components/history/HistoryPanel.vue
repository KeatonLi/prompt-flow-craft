<template>
  <div class="history-panel">
    <!-- 搜索和筛选 -->
    <div class="panel-header">
      <div class="search-box">
        <input
          v-model="searchKeyword"
          type="text"
          placeholder="搜索历史记录..."
          class="search-input"
          @input="handleSearch"
        />
        <span class="search-icon">🔍</span>
      </div>

      <div class="filter-tabs">
        <button
          class="tab-btn"
          :class="{ active: activeTab === 'all' }"
          @click="setTab('all')"
        >
          全部
        </button>
        <button
          class="tab-btn"
          :class="{ active: activeTab === 'favorite' }"
          @click="setTab('favorite')"
        >
          收藏
        </button>
      </div>
    </div>

    <!-- 历史记录列表 -->
    <div class="panel-content" v-loading="historyStore.loading">
      <div v-if="displayedRecords.length === 0" class="empty-state">
        <div class="empty-icon">📭</div>
        <p>暂无历史记录</p>
      </div>

      <div v-else class="virtual-list-container" ref="listContainer">
        <div
          v-for="record in displayedRecords"
          :key="record.id"
          class="history-card"
          :class="{ active: selectedId === record.id }"
          @click="selectRecord(record)"
        >
          <div class="card-header">
            <span
              class="category-badge"
              :style="{ backgroundColor: getCategoryColor(record.categoryId) + '20', color: getCategoryColor(record.categoryId) }"
            >
              {{ getCategoryIcon(record.categoryId) }}
            </span>
            <span class="record-time">{{ formatTime(record.createdAt) }}</span>
          </div>

          <p class="record-title">{{ truncate(record.taskDescription, 60) }}</p>

          <div class="card-footer">
            <div class="record-tags">
              <span
                v-for="tag in record.tags?.slice(0, 2)"
                :key="tag.id"
                class="tag-badge"
                :style="{ backgroundColor: tag.color + '20', color: tag.color }"
              >
                {{ tag.name }}
              </span>
            </div>
            <button
              class="favorite-btn"
              :class="{ active: record.isFavorite }"
              @click.stop="toggleFavorite(record.id)"
            >
              {{ record.isFavorite ? '⭐' : '☆' }}
            </button>
          </div>
        </div>

        <!-- 加载更多 -->
        <div v-if="hasMore" class="load-more">
          <button class="load-btn" @click="loadMore" :disabled="loading">
            {{ loading ? '加载中...' : '加载更多' }}
          </button>
        </div>
      </div>
    </div>

    <!-- 统计信息 -->
    <div class="panel-footer">
      <span>共 {{ historyStore.pagination.total }} 条</span>
      <button class="batch-btn" @click="showBatchActions = true">
        批量操作
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import { useHistoryStore, useCategoryStore } from '@/stores';
import type { PromptRecord } from '@/types';
import dayjs from 'dayjs';
import { debounce } from 'lodash-es';

const historyStore = useHistoryStore();
const categoryStore = useCategoryStore();

const searchKeyword = ref('');
const activeTab = ref<'all' | 'favorite'>('all');
const selectedId = ref<number | null>(null);
const loading = ref(false);
const showBatchActions = ref(false);

const displayedRecords = computed(() => historyStore.filteredRecords);
const hasMore = computed(() => {
  return historyStore.pagination.page < historyStore.pagination.totalPages;
});

const handleSearch = debounce(() => {
  historyStore.setQueryParams({ keyword: searchKeyword.value, page: 1 });
  historyStore.fetchRecords();
}, 300);

function setTab(tab: 'all' | 'favorite') {
  activeTab.value = tab;
  historyStore.setQueryParams({
    isFavorite: tab === 'favorite' ? true : undefined,
    page: 1
  });
  historyStore.fetchRecords();
}

function selectRecord(record: PromptRecord) {
  selectedId.value = record.id;
  // 可以触发事件通知父组件
}

async function toggleFavorite(id: number) {
  await historyStore.toggleFavorite(id);
}

async function loadMore() {
  if (loading.value) return;
  loading.value = true;
  try {
    await historyStore.fetchRecords({
      page: historyStore.pagination.page + 1
    });
  } finally {
    loading.value = false;
  }
}

function getCategoryColor(categoryId?: number): string {
  return categoryStore.getCategoryColor(categoryId || 0);
}

function getCategoryIcon(categoryId?: number): string {
  const category = categoryStore.getCategoryById(categoryId || 0);
  return category?.icon || '📄';
}

function formatTime(time: string): string {
  return dayjs(time).format('MM-DD HH:mm');
}

function truncate(text: string, length: number): string {
  if (text.length <= length) return text;
  return text.slice(0, length) + '...';
}

onMounted(() => {
  historyStore.fetchRecords();
});
</script>

<style scoped>
.history-panel {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.panel-header {
  padding: 16px;
  border-bottom: 1px solid #e2e8f0;
}

.search-box {
  position: relative;
  margin-bottom: 12px;
}

.search-input {
  width: 100%;
  padding: 8px 12px 8px 36px;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  font-size: 0.875rem;
  transition: all 0.2s;
}

.search-input:focus {
  outline: none;
  border-color: #3b82f6;
}

.search-icon {
  position: absolute;
  left: 12px;
  top: 50%;
  transform: translateY(-50%);
  font-size: 0.875rem;
}

.filter-tabs {
  display: flex;
  gap: 8px;
}

.tab-btn {
  flex: 1;
  padding: 6px 12px;
  border: none;
  background: #f1f5f9;
  border-radius: 6px;
  font-size: 0.75rem;
  color: #64748b;
  cursor: pointer;
  transition: all 0.2s;
}

.tab-btn.active {
  background: #3b82f6;
  color: white;
}

.panel-content {
  flex: 1;
  overflow-y: auto;
  padding: 12px;
}

.empty-state {
  text-align: center;
  padding: 40px 20px;
  color: #94a3b8;
}

.empty-icon {
  font-size: 3rem;
  margin-bottom: 12px;
}

.history-card {
  background: white;
  border: 1px solid #e2e8f0;
  border-radius: 10px;
  padding: 12px;
  margin-bottom: 8px;
  cursor: pointer;
  transition: all 0.2s;
}

.history-card:hover {
  border-color: #3b82f6;
  box-shadow: 0 2px 8px rgba(59, 130, 246, 0.1);
}

.history-card.active {
  border-color: #3b82f6;
  background: #eff6ff;
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 8px;
}

.category-badge {
  font-size: 0.875rem;
  padding: 2px 8px;
  border-radius: 4px;
}

.record-time {
  font-size: 0.75rem;
  color: #94a3b8;
}

.record-title {
  font-size: 0.875rem;
  color: #334155;
  line-height: 1.5;
  margin: 0 0 12px 0;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.card-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.record-tags {
  display: flex;
  gap: 4px;
  flex-wrap: wrap;
}

.tag-badge {
  font-size: 0.625rem;
  padding: 2px 6px;
  border-radius: 4px;
}

.favorite-btn {
  border: none;
  background: transparent;
  font-size: 1rem;
  cursor: pointer;
  opacity: 0.5;
  transition: opacity 0.2s;
}

.favorite-btn:hover,
.favorite-btn.active {
  opacity: 1;
}

.load-more {
  text-align: center;
  padding: 16px;
}

.load-btn {
  padding: 8px 24px;
  border: 1px solid #e2e8f0;
  background: white;
  border-radius: 6px;
  font-size: 0.875rem;
  color: #64748b;
  cursor: pointer;
  transition: all 0.2s;
}

.load-btn:hover:not(:disabled) {
  border-color: #3b82f6;
  color: #3b82f6;
}

.load-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.panel-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 16px;
  border-top: 1px solid #e2e8f0;
  font-size: 0.75rem;
  color: #64748b;
}

.batch-btn {
  padding: 4px 12px;
  border: none;
  background: #f1f5f9;
  border-radius: 4px;
  font-size: 0.75rem;
  color: #64748b;
  cursor: pointer;
}

.batch-btn:hover {
  background: #e2e8f0;
}
</style>
