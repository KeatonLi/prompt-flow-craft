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
          点赞排行
        </button>
      </div>
    </div>

    <!-- 历史记录列表 -->
    <div class="panel-content" v-loading="historyStore.loading">
      <div v-if="displayedRecords.length === 0" class="empty-state">
        <div class="empty-icon">📭</div>
        <p>暂无历史记录</p>
      </div>

      <div v-else class="history-list">
        <HistoryCard
          v-for="record in displayedRecords"
          :key="record.id"
          :history="record"
          @reuse="handleReuse"
          @view="handleViewDetail"
        />

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

        <!-- 详情弹窗 - 使用统一组件 -->
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
import { useHistoryStore } from '@/stores';
import { historyApi } from '@/api/history';
import type { PromptRecord } from '@/types';
import dayjs from 'dayjs';
import { debounce } from 'lodash-es';
import HistoryCard from '@/components/HistoryCard.vue';
import PromptDetailModal from '@/components/PromptDetailModal.vue';
import MarkdownRender from '@/components/MarkdownRender.vue';

const historyStore = useHistoryStore();

const searchKeyword = ref('');
const activeTab = ref<'recent' | 'popular'>('recent');
const loading = ref(false);
const showBatchActions = ref(false);

// 详情弹窗相关
const detailVisible = ref(false);
const detailLoading = ref(false);
const selectedRecord = ref<PromptRecord | null>(null);
const showRaw = ref(false);
const modalBody = ref<HTMLElement | null>(null);

// 判断是否可能是 Markdown 格式
const isMarkdown = computed(() => {
  if (!selectedRecord.value?.generatedPrompt) return false;
  const content = selectedRecord.value.generatedPrompt;
  const markdownPatterns = [
    /#{1,6}\s+/, /\*\*|__/, /\*|_/, /```/, /\[.*?\]\(.*?\)/,
    /^\s*-\s+/, /^\s*\d+\.\s+/, /\|.*\|/, /^>/m,
  ];
  return markdownPatterns.some(pattern => pattern.test(content));
});

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

// 处理查看详情 - 调用API获取完整数据
async function handleViewDetail(record: PromptRecord) {
  selectedRecord.value = record;
  detailLoading.value = true;
  detailVisible.value = true;
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
  detailVisible.value = false;
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

// 格式化时间
function formatDateTime(timestamp: string): string {
  return dayjs(timestamp).format('YYYY年MM月DD日 HH:mm');
}

// 获取标签文本
function getAudienceLabel(value: string): string {
  const labels: Record<string, string> = {
    'general': '普通用户', 'professional': '专业人士', 'student': '学生',
    'developer': '开发者', 'creator': '创作者'
  };
  return labels[value] || value;
}

function getFormatLabel(value: string): string {
  const labels: Record<string, string> = {
    'text': '文本', 'list': '列表', 'table': '表格', 'code': '代码', 'json': 'JSON'
  };
  return labels[value] || value;
}

function getToneLabel(value: string): string {
  const labels: Record<string, string> = {
    'formal': '正式', 'friendly': '友好', 'professional': '专业', 'creative': '创意', 'concise': '简洁'
  };
  return labels[value] || value;
}

function getLengthLabel(value: string): string {
  const labels: Record<string, string> = {
    'short': '简短', 'medium': '中等', 'long': '详细', 'very_long': '非常详细'
  };
  return labels[value] || value;
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

.history-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
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

/* ========== 美化后的弹窗样式 ========== */

/* 遮罩层 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(15, 23, 42, 0.6);
  backdrop-filter: blur(8px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 9999;
  padding: 20px;
}

/* 弹窗容器 */
.modal-container {
  background: linear-gradient(180deg, #f8fafc 0%, #ffffff 100%);
  border-radius: 24px;
  width: 100%;
  max-width: 720px;
  max-height: 90vh;
  display: flex;
  flex-direction: column;
  box-shadow: 
    0 25px 50px -12px rgba(0, 0, 0, 0.25),
    0 0 0 1px rgba(255, 255, 255, 0.5) inset;
  overflow: hidden;
  animation: modalEnter 0.3s ease-out;
}

@keyframes modalEnter {
  from {
    opacity: 0;
    transform: scale(0.95) translateY(10px);
  }
  to {
    opacity: 1;
    transform: scale(1) translateY(0);
  }
}

/* 弹窗头部 */
.modal-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20px 24px;
  background: linear-gradient(135deg, #1e293b 0%, #334155 100%);
  color: white;
  flex-shrink: 0;
  position: relative;
  overflow: hidden;
}

.modal-header::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 1px;
  background: linear-gradient(90deg, transparent, rgba(255,255,255,0.3), transparent);
}

.modal-header-content {
  display: flex;
  align-items: center;
  gap: 12px;
}

.modal-icon {
  width: 44px;
  height: 44px;
  background: rgba(255, 255, 255, 0.15);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 22px;
  backdrop-filter: blur(8px);
}

.modal-title {
  margin: 0;
  font-size: 18px;
  font-weight: 700;
  letter-spacing: -0.025em;
}

.modal-subtitle {
  margin: 2px 0 0 0;
  font-size: 13px;
  color: rgba(255, 255, 255, 0.6);
}

.modal-close {
  width: 36px;
  height: 36px;
  border: none;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 10px;
  color: white;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s;
}

.modal-close:hover {
  background: rgba(255, 255, 255, 0.2);
  transform: rotate(90deg);
}

.modal-close svg {
  width: 18px;
  height: 18px;
}

/* 弹窗内容 */
.modal-body {
  flex: 1;
  overflow-y: auto;
  padding: 24px;
  scroll-behavior: smooth;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

/* 信息卡片 */
.info-card {
  background: white;
  border-radius: 16px;
  padding: 16px 20px;
  border: 1px solid #e2e8f0;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.04);
  transition: all 0.2s;
}

.info-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.06);
  border-color: #cbd5e1;
}

.info-card.primary {
  background: linear-gradient(135deg, #eff6ff 0%, #ffffff 100%);
  border-color: #bfdbfe;
}

.info-card.warning {
  background: linear-gradient(135deg, #fffbeb 0%, #ffffff 100%);
  border-color: #fde68a;
}

.info-card.info {
  background: linear-gradient(135deg, #f0fdfa 0%, #ffffff 100%);
  border-color: #99f6e4;
}

.info-card-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 12px;
}

.info-icon {
  font-size: 16px;
}

.info-label {
  font-size: 13px;
  font-weight: 600;
  color: #64748b;
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.task-description {
  font-size: 16px;
  font-weight: 500;
  color: #1e293b;
  line-height: 1.6;
  margin: 0;
}

.constraints-text, .examples-text {
  font-size: 14px;
  color: #475569;
  line-height: 1.7;
  margin: 0;
}

/* 参数网格 */
.params-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.param-badge {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 14px;
  background: #f8fafc;
  border-radius: 10px;
  border: 1px solid #e2e8f0;
}

.param-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
}

.param-dot.audience { background: linear-gradient(135deg, #3b82f6, #2563eb); }
.param-dot.format { background: linear-gradient(135deg, #10b981, #34d399); }
.param-dot.tone { background: linear-gradient(135deg, #f59e0b, #fbbf24); }
.param-dot.length { background: linear-gradient(135deg, #ef4444, #f87171); }

.param-name {
  font-size: 12px;
  color: #94a3b8;
  font-weight: 500;
}

.param-value-text {
  font-size: 13px;
  font-weight: 600;
  color: #1e293b;
}

/* 标签云 */
.tags-cloud {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.tag-item {
  font-size: 13px;
  font-weight: 500;
  border: 1px solid;
  padding: 6px 14px;
  border-radius: 20px;
  transition: all 0.2s;
}

.tag-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

/* 结果区块 */
.result-section {
  background: #0f172a;
  border-radius: 16px;
  overflow: hidden;
  margin-top: 8px;
}

.result-header-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 14px 18px;
  background: rgba(255, 255, 255, 0.05);
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.result-title-group {
  display: flex;
  align-items: center;
  gap: 8px;
}

.result-icon {
  font-size: 16px;
}

.result-title {
  font-size: 14px;
  font-weight: 600;
  color: #e2e8f0;
}

.badge {
  font-size: 11px;
  padding: 2px 8px;
  background: linear-gradient(135deg, #3b82f6, #6366f1);
  color: white;
  border-radius: 10px;
  font-weight: 500;
}

.format-switch {
  display: flex;
  gap: 4px;
  background: rgba(0, 0, 0, 0.3);
  padding: 4px;
  border-radius: 8px;
}

.switch-btn {
  padding: 4px 12px;
  border: none;
  background: transparent;
  border-radius: 6px;
  font-size: 12px;
  color: #94a3b8;
  cursor: pointer;
  transition: all 0.2s;
}

.switch-btn:hover {
  color: #e2e8f0;
}

.switch-btn.active {
  background: rgba(255, 255, 255, 0.1);
  color: white;
}

/* 代码块 */
.result-code-block {
  padding: 18px;
  max-height: 400px;
  overflow-y: auto;
}

.code-content {
  margin: 0;
  white-space: pre-wrap;
  word-wrap: break-word;
  font-family: 'JetBrains Mono', 'Fira Code', 'Monaco', monospace;
  font-size: 13px;
  line-height: 1.7;
  color: #e2e8f0;
}

/* 统计栏 */
.stats-bar {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 14px 20px;
  background: linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%);
  border-radius: 12px;
  border: 1px solid #e2e8f0;
  margin-top: 8px;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 6px;
}

.stat-icon {
  font-size: 14px;
}

.stat-label {
  font-size: 12px;
  color: #64748b;
}

.stat-value {
  font-size: 13px;
  font-weight: 600;
  color: #1e293b;
}

.stat-divider {
  width: 1px;
  height: 16px;
  background: #cbd5e1;
}

/* 弹窗底部 */
.modal-footer {
  display: flex;
  gap: 12px;
  justify-content: flex-end;
  padding: 18px 24px;
  border-top: 1px solid #e2e8f0;
  background: white;
  flex-shrink: 0;
}

.btn {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 10px 20px;
  border-radius: 10px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
  border: none;
  outline: none;
}

.btn-icon {
  width: 18px;
  height: 18px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.btn-icon svg {
  width: 100%;
  height: 100%;
}

.btn-primary {
  background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
  color: white;
  box-shadow: 0 2px 8px rgba(59, 130, 246, 0.3);
}

.btn-primary:hover {
  background: linear-gradient(135deg, #2563eb 0%, #1d4ed8 100%);
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.4);
  transform: translateY(-1px);
}

.btn-secondary {
  background: #f1f5f9;
  color: #475569;
  border: 1px solid #e2e8f0;
}

.btn-secondary:hover {
  background: #e2e8f0;
  color: #1e293b;
}

/* 过渡动画 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

.scale-enter-active,
.scale-leave-active {
  transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
}

.scale-enter-from,
.scale-leave-to {
  opacity: 0;
  transform: scale(0.9);
}
</style>
