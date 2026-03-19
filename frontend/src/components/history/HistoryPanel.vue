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
          :class="{ active: activeTab === 'recent' }"
          @click="setTab('recent')"
        >
          最新
        </button>
        <button
          class="tab-btn"
          :class="{ active: activeTab === 'popular' }"
          @click="setTab('popular')"
        >
          热门
        </button>
      </div>

      <button class="go-templates-btn" @click="goToTemplates">
        查看提示词大全 →
      </button>
    </div>

    <!-- 历史记录列表 -->
    <div class="panel-content" v-loading="historyStore.loading">
      <div v-if="displayedRecords.length === 0" class="empty-state">
        <div class="empty-icon">📭</div>
        <p>暂无历史记录</p>
        <span class="empty-tip">生成提示词后将显示在这里</span>
      </div>

      <div v-else class="history-list">
        <TransitionGroup name="list">
          <HistoryCard
            v-for="record in displayedRecords"
            :key="record.id"
            :history="record"
            @reuse="handleReuse"
            @view="handleViewDetail"
          />
        </TransitionGroup>

        <!-- 加载更多 -->
        <div v-if="hasMore" class="load-more">
          <button class="load-btn" @click="loadMore" :disabled="loading">
            <span v-if="loading" class="loading-spinner"></span>
            <span v-else>加载更多</span>
          </button>
        </div>
      </div>
    </div>

    <!-- 统计信息 -->
    <div class="panel-footer">
      <span>共 {{ historyStore.pagination.total }} 条记录</span>
    </div>

    <!-- 详情弹窗 -->
    <Teleport to="body">
      <PromptDetailModal 
        :item="selectedRecord" 
        :loading="detailLoading"
        @close="closeDetail" 
        @use="handleReuseFromDetail"
      />
    </Teleport>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { useHistoryStore } from '@/stores';
import { historyApi } from '@/api/history';
import type { PromptRecord } from '@/types';
import { debounce } from 'lodash-es';
import HistoryCard from '@/components/HistoryCard.vue';
import PromptDetailModal from '@/components/PromptDetailModal.vue';

const historyStore = useHistoryStore();
const router = useRouter();

const goToTemplates = () => {
  router.push('/templates');
};

const searchKeyword = ref('');
const activeTab = ref<'recent' | 'popular'>('recent');
const loading = ref(false);

// 详情弹窗相关
const detailLoading = ref(false);
const selectedRecord = ref<PromptRecord | null>(null);

const displayedRecords = computed(() => historyStore.filteredRecords);
const hasMore = computed(() => {
  return historyStore.pagination.page < historyStore.pagination.totalPages;
});

const handleSearch = debounce(() => {
  historyStore.setQueryParams({ keyword: searchKeyword.value, page: 1 });
  historyStore.fetchRecords();
}, 300);

function setTab(tab: 'recent' | 'popular') {
  activeTab.value = tab;
  const sortBy = tab === 'popular' ? 'likeCount' : 'createdAt';
  historyStore.setQueryParams({
    sortBy: sortBy,
    sortOrder: 'DESC',
    page: 1
  });
  historyStore.fetchRecords();
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

// 处理复用事件
function handleReuse(record: PromptRecord) {
  window.dispatchEvent(new CustomEvent('reuse-history', { detail: record }));
}

// 处理查看详情
async function handleViewDetail(record: PromptRecord) {
  selectedRecord.value = record;
  detailLoading.value = true;
  document.body.style.overflow = 'hidden';
  
  try {
    const res = await historyApi.getById(record.id);
    if (res) {
      selectedRecord.value = res;
    }
  } catch (e) {
    console.error('Failed to load detail:', e);
  }
  detailLoading.value = false;
}

// 关闭详情
function closeDetail() {
  document.body.style.overflow = '';
  selectedRecord.value = null;
}

// 从详情弹窗复用
function handleReuseFromDetail() {
  if (selectedRecord.value) {
    handleReuse(selectedRecord.value);
    closeDetail();
  }
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
  border-bottom: 1px solid rgba(226, 232, 240, 0.6);
}

.go-templates-btn {
  width: 100%;
  padding: 10px;
  margin-top: 12px;
  background: #f8fafc;
  color: #475569;
  border: 1px solid #e2e8f0;
  border-radius: 10px;
  font-size: 0.85rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
}

.go-templates-btn:hover {
  background: #f1f5f9;
  border-color: #cbd5e1;
  color: #334155;
  transform: translateY(-1px);
}

.search-box {
  position: relative;
  margin-bottom: 12px;
}

.search-input {
  width: 100%;
  padding: 10px 12px 10px 38px;
  border: 1px solid #e2e8f0;
  border-radius: 10px;
  font-size: 0.875rem;
  transition: all 0.2s;
  background: rgba(255, 255, 255, 0.8);
}

.search-input:focus {
  outline: none;
  border-color: #3b82f6;
  background: white;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
}

.search-icon {
  position: absolute;
  left: 12px;
  top: 50%;
  transform: translateY(-50%);
  font-size: 0.9rem;
  opacity: 0.6;
}

.filter-tabs {
  display: flex;
  gap: 8px;
}

.tab-btn {
  flex: 1;
  padding: 8px 12px;
  border: none;
  background: rgba(241, 245, 249, 0.8);
  border-radius: 8px;
  font-size: 0.8rem;
  font-weight: 500;
  color: #64748b;
  cursor: pointer;
  transition: all 0.2s;
}

.tab-btn:hover {
  background: #e2e8f0;
  color: #334155;
}

.tab-btn.active {
  background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
  color: white;
  box-shadow: 0 2px 8px rgba(59, 130, 246, 0.25);
}

.panel-content {
  flex: 1;
  overflow-y: auto;
  padding: 12px;
}

.empty-state {
  text-align: center;
  padding: 50px 20px;
  color: #94a3b8;
}

.empty-icon {
  font-size: 3rem;
  margin-bottom: 12px;
  opacity: 0.7;
}

.empty-tip {
  display: block;
  font-size: 0.8rem;
  margin-top: 8px;
  opacity: 0.7;
}

.history-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.load-more {
  text-align: center;
  padding: 16px;
}

.load-btn {
  padding: 8px 24px;
  border: 1px solid #e2e8f0;
  background: white;
  border-radius: 8px;
  font-size: 0.875rem;
  font-weight: 500;
  color: #64748b;
  cursor: pointer;
  transition: all 0.2s;
  display: inline-flex;
  align-items: center;
  gap: 8px;
}

.load-btn:hover:not(:disabled) {
  border-color: #3b82f6;
  color: #3b82f6;
  background: #f8fafc;
}

.load-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.loading-spinner {
  width: 14px;
  height: 14px;
  border: 2px solid rgba(100, 116, 139, 0.3);
  border-top-color: currentColor;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.panel-footer {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 12px 16px;
  border-top: 1px solid rgba(226, 232, 240, 0.6);
  font-size: 0.75rem;
  color: #94a3b8;
  background: rgba(255, 255, 255, 0.5);
}

/* 列表动画 */
.list-enter-active,
.list-leave-active {
  transition: all 0.3s ease;
}

.list-enter-from,
.list-leave-to {
  opacity: 0;
  transform: translateX(-20px);
}

.panel-header {
  padding: 18px;
  border-bottom: 1px solid var(--line-soft);
}

.go-templates-btn {
  padding: 12px 14px;
  background: rgba(248, 250, 252, 0.85);
  color: var(--text-primary);
  border: 1px solid rgba(148, 163, 184, 0.18);
  border-radius: 16px;
}

.go-templates-btn:hover {
  background: white;
  border-color: rgba(37, 99, 235, 0.16);
  box-shadow: 0 12px 20px rgba(15, 23, 42, 0.06);
}

.search-input {
  padding: 12px 14px 12px 40px;
  border: 1px solid rgba(148, 163, 184, 0.18);
  border-radius: 16px;
}

.search-input:focus {
  border-color: #2563eb;
  box-shadow: 0 0 0 4px rgba(37, 99, 235, 0.08);
}

.tab-btn {
  padding: 10px 12px;
  border-radius: 14px;
}

.tab-btn.active {
  background: linear-gradient(135deg, #2563eb 0%, #1d4ed8 100%);
  box-shadow: 0 12px 22px rgba(37, 99, 235, 0.22);
}

.panel-content {
  padding: 14px;
}

.load-btn {
  padding: 10px 24px;
  border: 1px solid rgba(148, 163, 184, 0.18);
  background: rgba(255, 255, 255, 0.88);
  border-radius: 999px;
}

.load-btn:hover:not(:disabled) {
  background: white;
  border-color: #2563eb;
  box-shadow: 0 12px 20px rgba(15, 23, 42, 0.06);
}

.panel-footer {
  border-top: 1px solid var(--line-soft);
  color: var(--text-muted);
  background: rgba(255, 255, 255, 0.45);
}
</style>
