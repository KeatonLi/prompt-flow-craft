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

    <!-- 详情弹窗 - 自定义实现，居中显示 -->
    <Teleport to="body">
      <Transition name="fade">
        <div v-if="detailVisible" class="modal-overlay" @click.self="closeDetail">
          <Transition name="scale">
            <div v-if="detailVisible" class="modal-container">
              <!-- 弹窗头部 -->
              <div class="modal-header">
                <h3 class="modal-title">历史记录详情</h3>
                <button class="modal-close" @click="closeDetail">
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <path d="M6 18L18 6M6 6l12 12"/>
                  </svg>
                </button>
              </div>

              <!-- 弹窗内容 -->
              <div v-if="selectedRecord" class="modal-body" ref="modalBody">
                <!-- 头部信息 -->
                <div class="detail-header">
                  <div class="detail-title">{{ selectedRecord.taskDescription }}</div>
                  <div class="detail-meta">
                    <span class="meta-item">
                      <span class="meta-icon">📅</span>
                      {{ formatDateTime(selectedRecord.createdAt) }}
                    </span>
                    <span class="meta-item">
                      <span class="meta-icon">👁️</span>
                      命中 {{ selectedRecord.hitCount }} 次
                    </span>
                  </div>
                </div>

                <!-- 参数标签 -->
                <div class="detail-section">
                  <h4 class="section-title">
                    <span class="section-icon">⚙️</span>
                    配置参数
                  </h4>
                  <div class="detail-params">
                    <div v-if="selectedRecord.targetAudience" class="param-item">
                      <span class="param-label">受众</span>
                      <span class="param-value audience">{{ getAudienceLabel(selectedRecord.targetAudience) }}</span>
                    </div>
                    <div v-if="selectedRecord.outputFormat" class="param-item">
                      <span class="param-label">格式</span>
                      <span class="param-value format">{{ getFormatLabel(selectedRecord.outputFormat) }}</span>
                    </div>
                    <div v-if="selectedRecord.tone" class="param-item">
                      <span class="param-label">语调</span>
                      <span class="param-value tone">{{ getToneLabel(selectedRecord.tone) }}</span>
                    </div>
                    <div v-if="selectedRecord.length" class="param-item">
                      <span class="param-label">长度</span>
                      <span class="param-value length">{{ getLengthLabel(selectedRecord.length) }}</span>
                    </div>
                  </div>
                </div>

                <!-- 提示词标签 -->
                <div v-if="selectedRecord.tags && selectedRecord.tags.length > 0" class="detail-section">
                  <h4 class="section-title">
                    <span class="section-icon">🏷️</span>
                    标签
                  </h4>
                  <div class="detail-tags">
                    <span
                      v-for="tag in selectedRecord.tags"
                      :key="tag.id"
                      class="detail-tag"
                      :style="{ 
                        backgroundColor: tag.color ? tag.color + '15' : 'rgba(99, 102, 241, 0.1)', 
                        color: tag.color || '#6366f1',
                        borderColor: tag.color ? tag.color + '30' : 'rgba(99, 102, 241, 0.2)'
                      }"
                    >
                      {{ tag.name }}
                    </span>
                  </div>
                </div>

                <!-- 约束条件 -->
                <div v-if="selectedRecord.constraints" class="detail-section">
                  <h4 class="section-title">
                    <span class="section-icon">⚠️</span>
                    约束条件
                  </h4>
                  <div class="detail-box">
                    <p>{{ selectedRecord.constraints }}</p>
                  </div>
                </div>

                <!-- 参考示例 -->
                <div v-if="selectedRecord.examples" class="detail-section">
                  <h4 class="section-title">
                    <span class="section-icon">📝</span>
                    参考示例
                  </h4>
                  <div class="detail-box">
                    <p>{{ selectedRecord.examples }}</p>
                  </div>
                </div>

                <!-- 生成结果 - 使用 Markdown 渲染 -->
                <div class="detail-section">
                  <h4 class="section-title">
                    <span class="section-icon">✨</span>
                    生成结果
                    <span v-if="isMarkdown" class="format-badge">Markdown</span>
                  </h4>
                  
                  <!-- 格式切换按钮 -->
                  <div class="format-toggle">
                    <button 
                      class="toggle-btn" 
                      :class="{ active: showRaw }"
                      @click="showRaw = true"
                    >
                      原文
                    </button>
                    <button 
                      class="toggle-btn" 
                      :class="{ active: !showRaw }"
                      @click="showRaw = false"
                    >
                      渲染
                    </button>
                  </div>
                  
                  <!-- 内容显示 -->
                  <div class="result-box">
                    <!-- 原始文本 -->
                    <pre v-if="showRaw" class="raw-content">{{ selectedRecord.generatedPrompt }}</pre>
                    <!-- Markdown 渲染 -->
                    <MarkdownRender 
                      v-else 
                      :content="selectedRecord.generatedPrompt"
                    />
                  </div>
                </div>
              </div>

              <!-- 弹窗底部 -->
              <div class="modal-footer">
                <button class="btn btn-secondary" @click="closeDetail">
                  关闭
                </button>
                <button class="btn btn-primary" @click="handleReuseFromDetail">
                  <span class="btn-icon">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                      <path d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15"/>
                    </svg>
                  </span>
                  复用此记录
                </button>
              </div>
            </div>
          </Transition>
        </div>
      </Transition>
    </Teleport>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch, nextTick } from 'vue';
import { useHistoryStore } from '@/stores';
import type { PromptRecord } from '@/types';
import dayjs from 'dayjs';
import { debounce } from 'lodash-es';
import HistoryCard from '@/components/HistoryCard.vue';
import MarkdownRender from '@/components/MarkdownRender.vue';

const historyStore = useHistoryStore();

const searchKeyword = ref('');
const activeTab = ref<'all' | 'favorite'>('all');
const loading = ref(false);
const showBatchActions = ref(false);

// 详情弹窗相关
const detailVisible = ref(false);
const selectedRecord = ref<PromptRecord | null>(null);
const showRaw = ref(false);
const modalBody = ref<HTMLElement | null>(null);

// 判断是否可能是 Markdown 格式
const isMarkdown = computed(() => {
  if (!selectedRecord.value?.generatedPrompt) return false;
  const content = selectedRecord.value.generatedPrompt;
  // 检查是否包含 Markdown 特征
  const markdownPatterns = [
    /#{1,6}\s+/,           // 标题
    /\*\*|__/,             // 粗体
    /\*|_/,                // 斜体
    /```/,                 // 代码块
    /\[.*?\]\(.*?\)/,      // 链接
    /^\s*-\s+/,            // 无序列表
    /^\s*\d+\.\s+/,         // 有序列表
    /\|.*\|/,              // 表格
    /^>/m,                 // 引用
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

function setTab(tab: 'all' | 'favorite') {
  activeTab.value = tab;
  historyStore.setQueryParams({
    isFavorite: tab === 'favorite' ? true : undefined,
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
function handleViewDetail(record: PromptRecord) {
  selectedRecord.value = record;
  // 默认如果包含 Markdown 则渲染，否则显示原文
  showRaw.value = !isMarkdown.value;
  detailVisible.value = true;
  document.body.style.overflow = 'hidden';
  
  // 滚动到顶部
  nextTick(() => {
    if (modalBody.value) {
      modalBody.value.scrollTop = 0;
    }
  });
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
  return dayjs(timestamp).format('YYYY-MM-DD HH:mm:ss');
}

// 获取标签文本
function getAudienceLabel(value: string): string {
  const labels: Record<string, string> = {
    'general': '普通用户',
    'professional': '专业人士',
    'student': '学生',
    'developer': '开发者',
    'creator': '创作者'
  };
  return labels[value] || value;
}

function getFormatLabel(value: string): string {
  const labels: Record<string, string> = {
    'text': '文本',
    'list': '列表',
    'table': '表格',
    'code': '代码',
    'json': 'JSON'
  };
  return labels[value] || value;
}

function getToneLabel(value: string): string {
  const labels: Record<string, string> = {
    'formal': '正式',
    'friendly': '友好',
    'professional': '专业',
    'creative': '创意',
    'concise': '简洁'
  };
  return labels[value] || value;
}

function getLengthLabel(value: string): string {
  const labels: Record<string, string> = {
    'short': '简短',
    'medium': '中等',
    'long': '详细',
    'very_long': '非常详细'
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

/* ========== 弹窗样式 ========== */

/* 遮罩层 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.6);
  backdrop-filter: blur(4px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 9999;
  padding: 20px;
}

/* 弹窗容器 */
.modal-container {
  background: white;
  border-radius: 20px;
  width: 100%;
  max-width: 800px;
  max-height: 85vh;
  display: flex;
  flex-direction: column;
  box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.25);
  overflow: hidden;
}

/* 弹窗头部 */
.modal-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20px 24px;
  background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
  color: white;
  flex-shrink: 0;
}

.modal-title {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
}

.modal-close {
  width: 32px;
  height: 32px;
  border: none;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 8px;
  color: white;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s;
}

.modal-close:hover {
  background: rgba(255, 255, 255, 0.3);
}

.modal-close svg {
  width: 20px;
  height: 20px;
}

/* 弹窗内容 */
.modal-body {
  flex: 1;
  overflow-y: auto;
  padding: 24px;
  scroll-behavior: smooth;
}

/* 头部信息 */
.detail-header {
  margin-bottom: 24px;
  padding-bottom: 20px;
  border-bottom: 2px solid #f1f5f9;
}

.detail-title {
  font-size: 18px;
  font-weight: 600;
  color: #1e293b;
  line-height: 1.5;
  margin-bottom: 12px;
  word-break: break-word;
}

.detail-meta {
  display: flex;
  gap: 16px;
  flex-wrap: wrap;
}

.meta-item {
  font-size: 13px;
  color: #64748b;
  display: flex;
  align-items: center;
  gap: 4px;
}

.meta-icon {
  font-size: 14px;
}

/* 章节样式 */
.detail-section {
  margin-bottom: 24px;
}

.detail-section:last-child {
  margin-bottom: 0;
}

.section-title {
  display: flex;
  align-items: center;
  gap: 8px;
  margin: 0 0 12px 0;
  color: #1e293b;
  font-size: 14px;
  font-weight: 600;
}

.section-icon {
  font-size: 16px;
}

.format-badge {
  font-size: 11px;
  padding: 2px 8px;
  background: linear-gradient(135deg, #3b82f6 0%, #6366f1 100%);
  color: white;
  border-radius: 12px;
  margin-left: 8px;
  font-weight: 500;
}

/* 格式切换 */
.format-toggle {
  display: flex;
  gap: 8px;
  margin-bottom: 12px;
}

.toggle-btn {
  padding: 6px 14px;
  border: 1px solid #e2e8f0;
  background: white;
  border-radius: 6px;
  font-size: 12px;
  color: #64748b;
  cursor: pointer;
  transition: all 0.2s;
}

.toggle-btn:hover {
  border-color: #cbd5e1;
}

.toggle-btn.active {
  background: #3b82f6;
  color: white;
  border-color: #3b82f6;
}

/* 参数网格 */
.detail-params {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(140px, 1fr));
  gap: 12px;
}

.param-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.param-label {
  font-size: 12px;
  color: #94a3b8;
  font-weight: 500;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.param-value {
  font-size: 13px;
  font-weight: 500;
  padding: 6px 12px;
  border-radius: 6px;
  width: fit-content;
}

.param-value.audience {
  background: rgba(99, 102, 241, 0.1);
  color: #6366f1;
}

.param-value.format {
  background: rgba(16, 185, 129, 0.1);
  color: #10b981;
}

.param-value.tone {
  background: rgba(245, 158, 11, 0.1);
  color: #f59e0b;
}

.param-value.length {
  background: rgba(239, 68, 68, 0.1);
  color: #ef4444;
}

/* 标签样式 */
.detail-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.detail-tag {
  font-size: 12px;
  font-weight: 500;
  border: 1px solid;
  padding: 5px 12px;
  border-radius: 20px;
}

/* 内容盒子 */
.detail-box {
  background: #f8fafc;
  border-radius: 10px;
  padding: 14px;
  border: 1px solid #e2e8f0;
}

.detail-box p {
  margin: 0;
  color: #475569;
  font-size: 14px;
  line-height: 1.7;
  word-break: break-word;
}

/* 结果盒子 */
.result-box {
  background: #1e293b;
  border-radius: 10px;
  overflow: hidden;
}

.raw-content {
  margin: 0;
  padding: 16px;
  white-space: pre-wrap;
  word-wrap: break-word;
  font-family: 'JetBrains Mono', 'Fira Code', monospace;
  font-size: 13px;
  line-height: 1.7;
  color: #e2e8f0;
  max-height: 400px;
  overflow-y: auto;
}

/* 弹窗底部 */
.modal-footer {
  display: flex;
  gap: 12px;
  justify-content: flex-end;
  padding: 16px 24px;
  border-top: 1px solid #e2e8f0;
  background: #fafafa;
  flex-shrink: 0;
}

/* 按钮样式 */
.btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
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
  background: white;
  color: #475569;
  border: 1px solid #e2e8f0;
}

.btn-secondary:hover {
  background: #f8fafc;
  border-color: #cbd5e1;
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
