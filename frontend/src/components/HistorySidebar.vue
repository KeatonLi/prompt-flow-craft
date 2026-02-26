<template>
  <div class="history-sidebar" :class="{ 'collapsed': !isOpen }">
    <div class="sidebar-header">
      <div class="header-content">
        <h3 v-if="isOpen">历史记录</h3>
        <el-button
          circle
          size="small"
          @click="toggleSidebar"
          class="toggle-btn"
        >
          <el-icon>
            <ArrowLeft v-if="isOpen" />
            <ArrowRight v-else />
          </el-icon>
        </el-button>
      </div>
    </div>

    <div class="sidebar-content" v-if="isOpen">
      <div class="search-section">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索历史记录..."
          size="small"
          clearable
          @input="handleSearch"
          class="search-input"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
      </div>

      <div class="filter-section">
        <el-select
          v-model="limit"
          placeholder="显示数量"
          size="small"
          @change="loadHistory"
          class="limit-select"
        >
          <el-option label="最近10条" :value="10" />
          <el-option label="最近20条" :value="20" />
          <el-option label="最近50条" :value="50" />
          <el-option label="全部" :value="0" />
        </el-select>
      </div>

      <div class="history-list" v-loading="loading">
        <div v-if="historyList.length === 0 && !loading" class="empty-state">
          <el-empty description="暂无历史记录" />
        </div>
        
        <HistoryCard
          v-for="history in displayedHistory"
          :key="history.id"
          :history="history"
          @reuse="handleReuse"
          @view="handleViewDetail"
        />
        
        <div v-if="hasMore && !loading" class="load-more">
          <el-button size="small" @click="loadMore" plain>
            加载更多
          </el-button>
        </div>
      </div>
    </div>

    <div class="sidebar-collapsed" v-if="!isOpen" @click="toggleSidebar">
      <el-icon><Clock /></el-icon>
      <span class="count-badge" v-if="totalCount > 0">{{ totalCount }}</span>
    </div>

    <!-- 历史记录详情弹窗 -->
    <el-dialog
      v-model="detailVisible"
      title="历史记录详情"
      width="600px"
      :destroy-on-close="true"
      center
      :z-index="9999"
      :modal="true"
      :append-to-body="true"
    >
      <div v-if="selectedHistory" class="detail-content">
        <div class="detail-section">
          <h4>任务描述</h4>
          <p>{{ selectedHistory.taskDescription }}</p>
        </div>
        
        <div class="detail-section">
          <h4>配置参数</h4>
          <div class="detail-params">
            <el-tag type="info" v-if="selectedHistory.targetAudience">
              受众: {{ getAudienceLabel(selectedHistory.targetAudience) }}
            </el-tag>
            <el-tag type="success" v-if="selectedHistory.outputFormat">
              格式: {{ getFormatLabel(selectedHistory.outputFormat) }}
            </el-tag>
            <el-tag type="warning" v-if="selectedHistory.tone">
              语调: {{ getToneLabel(selectedHistory.tone) }}
            </el-tag>
            <el-tag type="danger" v-if="selectedHistory.length">
              长度: {{ getLengthLabel(selectedHistory.length) }}
            </el-tag>
          </div>
        </div>
        
        <div class="detail-section" v-if="selectedHistory.constraints">
          <h4>约束条件</h4>
          <p>{{ selectedHistory.constraints }}</p>
        </div>
        
        <div class="detail-section" v-if="selectedHistory.examples">
          <h4>参考示例</h4>
          <p>{{ selectedHistory.examples }}</p>
        </div>
        
        <div class="detail-section">
          <h4>生成结果</h4>
          <el-input
            type="textarea"
            :rows="6"
            :value="selectedHistory.generatedPrompt"
            readonly
            class="generated-result"
          />
        </div>
        
        <div class="detail-section">
          <h4>记录信息</h4>
          <p class="record-info">
            创建时间: {{ formatDateTime(selectedHistory.createdAt) }}<br>
            缓存命中: {{ selectedHistory.hitCount }} 次
          </p>
        </div>
      </div>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="detailVisible = false">关闭</el-button>
          <el-button type="primary" @click="handleReuse(selectedHistory)">
            复用此记录
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { historyApi } from '@/api/history'
import HistoryCard from './HistoryCard.vue'
import { Search, Clock, ArrowLeft, ArrowRight } from '@element-plus/icons-vue'

export default {
  name: 'HistorySidebar',
  components: {
    HistoryCard,
    Search,
    Clock,
    ArrowLeft,
    ArrowRight
  },
  data() {
    return {
      isOpen: false,
      loading: false,
      searchKeyword: '',
      limit: 20,
      historyList: [],
      displayedHistory: [],
      currentPage: 1,
      pageSize: 20,
      selectedHistory: null,
      detailVisible: false,
      totalCount: 0
    }
  },
  mounted() {
    this.loadHistory()
  },
  methods: {
    async loadHistory() {
      this.loading = true
      try {
        console.log('开始加载历史记录...')
        const data = await historyApi.getRecent(this.limit)
        console.log('获取到的历史记录数据:', data)
        this.historyList = data || []
        this.displayedHistory = this.historyList.slice(0, this.pageSize)
        this.totalCount = this.historyList.length
        this.currentPage = 1
        console.log('displayedHistory:', this.displayedHistory)
      } catch (error) {
        console.error('加载历史记录失败:', error)
        this.$message.error('加载历史记录失败')
      } finally {
        this.loading = false
      }
    },
    
    async handleSearch() {
      if (this.searchKeyword.trim()) {
        this.loading = true
        try {
          const data = await historyApi.search(this.searchKeyword.trim())
          this.historyList = data || []
          this.displayedHistory = this.historyList.slice(0, this.pageSize)
          this.totalCount = this.historyList.length
          this.currentPage = 1
        } catch (error) {
          console.error('搜索失败:', error)
          this.$message.error('搜索失败')
        } finally {
          this.loading = false
        }
      } else {
        this.loadHistory()
      }
    },
    
    loadMore() {
      const start = this.currentPage * this.pageSize
      const end = start + this.pageSize
      const moreItems = this.historyList.slice(start, end)
      this.displayedHistory.push(...moreItems)
      this.currentPage++
    },
    
    toggleSidebar() {
      this.isOpen = !this.isOpen
      if (this.isOpen) {
        this.loadHistory()
      }
    },
    
    handleReuse(history) {
      this.$emit('reuse-history', {
        taskDescription: history.taskDescription,
        targetAudience: history.targetAudience,
        outputFormat: history.outputFormat,
        constraints: history.constraints,
        examples: history.examples,
        tone: history.tone,
        length: history.length
      })
      this.$message.success('已复用历史记录')
    },
    
    handleViewDetail(history) {
      this.selectedHistory = history
      this.detailVisible = true
    },
    
    formatDateTime(timestamp) {
      if (!timestamp) return ''
      return new Date(timestamp).toLocaleString('zh-CN')
    },
    
    getAudienceLabel(value) {
      const labels = {
        'general': '普通用户',
        'professional': '专业人士',
        'student': '学生',
        'developer': '开发者',
        'creator': '创作者'
      }
      return labels[value] || value
    },
    
    getFormatLabel(value) {
      const labels = {
        'text': '文本',
        'list': '列表',
        'table': '表格',
        'code': '代码',
        'json': 'JSON'
      }
      return labels[value] || value
    },
    
    getToneLabel(value) {
      const labels = {
        'formal': '正式',
        'friendly': '友好',
        'professional': '专业',
        'creative': '创意',
        'concise': '简洁'
      }
      return labels[value] || value
    },
    
    getLengthLabel(value) {
      const labels = {
        'short': '简短',
        'medium': '中等',
        'long': '详细',
        'very_long': '非常详细'
      }
      return labels[value] || value
    },
    
    hasMore() {
      return this.displayedHistory.length < this.historyList.length;
    }
  },
  computed: {
    historyItemClass() {
      return {
        'history-item': true,
        'history-item--modern': true
      }
    }
  }
}
</script>

<style scoped>
.history-sidebar {
  position: fixed;
  left: 0;
  top: 0;
  bottom: 0;
  width: 420px;
  background: linear-gradient(135deg, #f8fafc 0%, #ffffff 100%);
  backdrop-filter: blur(24px);
  border-right: 2px solid #e2e8f0;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  z-index: 1000;
  box-shadow: 4px 0 24px rgba(0, 0, 0, 0.08), 1px 0 8px rgba(0, 0, 0, 0.04);
  border-radius: 0 20px 20px 0;
}

.history-sidebar.collapsed {
  transform: translateX(-100%);
}

.sidebar-header {
  padding: 32px 24px 24px;
  border-bottom: 2px solid #e2e8f0;
  background: linear-gradient(135deg, #f8fafc 0%, #ffffff 100%);
  border-radius: 0 20px 0 0;
  position: relative;
}

.sidebar-header::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 24px;
  right: 24px;
  height: 3px;
  background: linear-gradient(90deg, #3b82f6 0%, #1d4ed8 50%, #2563eb 100%);
  border-radius: 2px;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-content h3 {
  margin: 0;
  color: #1e293b;
  font-size: 20px;
  font-weight: 700;
  letter-spacing: -0.025em;
}

.toggle-btn {
  background: linear-gradient(135deg, #3b82f6 0%, #1d4ed8 100%);
  border: none;
  color: white;
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.25);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  border-radius: 8px;
}

.toggle-btn:hover {
  background: linear-gradient(135deg, #2563eb 0%, #1e40af 100%);
  transform: scale(1.05);
  box-shadow: 0 6px 16px rgba(59, 130, 246, 0.35);
}

.sidebar-content {
  height: calc(100% - 100px);
  overflow-y: auto;
  padding: 24px;
  scrollbar-width: thin;
  scrollbar-color: rgba(200, 200, 200, 0.4) transparent;
  background: linear-gradient(135deg, #f8fafc 0%, #ffffff 100%);
}

.sidebar-content::-webkit-scrollbar {
  width: 6px;
}

.sidebar-content::-webkit-scrollbar-track {
  background: transparent;
}

.sidebar-content::-webkit-scrollbar-thumb {
  background: rgba(200, 200, 200, 0.4);
  border-radius: 3px;
}

.sidebar-content::-webkit-scrollbar-thumb:hover {
  background: rgba(150, 150, 150, 0.6);
}

.search-section {
  margin-bottom: 20px;
}

.search-input :deep(.el-input__wrapper) {
  background: rgba(248, 250, 252, 0.9);
  border: 1px solid rgba(226, 232, 240, 0.8);
  border-radius: 12px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

.search-input :deep(.el-input__wrapper:hover) {
  border-color: rgba(59, 130, 246, 0.4);
  background: rgba(255, 255, 255, 0.95);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.search-input :deep(.el-input__wrapper.is-focus) {
  border-color: #3b82f6;
  background: rgba(255, 255, 255, 0.98);
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1), 0 2px 8px rgba(0, 0, 0, 0.08);
}

.filter-section {
  margin-bottom: 20px;
}

.limit-select {
  width: 100%;
}

.limit-select :deep(.el-input__wrapper) {
  border-radius: 12px;
  border: 1px solid rgba(226, 232, 240, 0.8);
  background: rgba(248, 250, 252, 0.9);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

.limit-select :deep(.el-input__wrapper:hover) {
  border-color: rgba(59, 130, 246, 0.4);
  background: rgba(255, 255, 255, 0.95);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.history-list {
  min-height: 200px;
}

.empty-state {
  padding: 60px 0;
  text-align: center;
}

.empty-state :deep(.el-empty__description) {
  color: #999999;
  font-size: 14px;
}

.load-more {
  text-align: center;
  margin-top: 20px;
}

.load-more :deep(.el-button) {
  border-radius: 10px;
  border: 1px solid rgba(230, 230, 230, 0.6);
  background: #f8f9fa;
  color: #666666;
  font-weight: 500;
  transition: all 0.3s ease;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.04);
}

.load-more :deep(.el-button:hover) {
  border-color: #3b82f6;
  background: #ffffff;
  color: #3b82f6;
  transform: translateY(-1px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.08);
}

.sidebar-collapsed {
  position: fixed;
  left: 10px;
  top: 50%;
  transform: translateY(-50%);
  width: 60px;
  height: 100px;
  background: linear-gradient(135deg, #ffffff 0%, #f8fafc 100%);
  border: 2px solid #e2e8f0;
  border-radius: 12px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  color: #3b82f6;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.12), 0 4px 16px rgba(0, 0, 0, 0.08);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  font-size: 24px;
  z-index: 1001;
  backdrop-filter: blur(12px);
}

.sidebar-collapsed:hover {
  background: linear-gradient(135deg, #f8fafc 0%, #ffffff 100%);
  border-color: #3b82f6;
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.15), 0 6px 20px rgba(59, 130, 246, 0.2);
  transform: translateY(-50%) scale(1.08);
  color: #1d4ed8;
}

.count-badge {
  position: absolute;
  top: -8px;
  right: -8px;
  background: #f56c6c;
  color: white;
  border-radius: 50%;
  width: 16px;
  height: 16px;
  font-size: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.detail-content {
  max-height: 70vh;
  overflow-y: auto;
}

.detail-section {
  margin-bottom: 20px;
}

.detail-section h4 {
  margin: 0 0 8px 0;
  color: #1e293b;
  font-size: 14px;
  font-weight: 600;
}

.detail-section p {
  margin: 0;
  color: #64748b;
  line-height: 1.6;
  word-break: break-word;
  overflow-wrap: break-word;
  white-space: pre-wrap;
}

.record-info {
  font-size: 13px;
  color: #94a3b8;
  background: #f8fafc;
  padding: 12px;
  border-radius: 8px;
  border-left: 3px solid #3b82f6;
}

.detail-section {
  margin-bottom: 16px;
}

.detail-section h4 {
  margin: 0 0 8px 0;
  color: #303133;
  font-size: 14px;
}

.detail-section p {
  margin: 0;
  color: #606266;
  line-height: 1.6;
}

.detail-params {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.generated-result :deep(.el-textarea__inner) {
  font-family: 'Helvetica Neue', Arial, sans-serif;
  line-height: 1.6;
  color: #1e293b;
}

.record-info {
  font-size: 12px;
  color: #909399;
}

@media (max-width: 768px) {
  .history-sidebar {
    width: 100%;
    max-width: 320px;
  }
}

.item-content {
  margin-bottom: 16px;
}

.item-title {
  font-size: 15px;
  font-weight: 600;
  color: #1e293b;
  margin-bottom: 10px;
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.item-preview {
  font-size: 13px;
  color: #64748b;
  line-height: 1.6;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
  background: rgba(248, 250, 252, 0.9);
  padding: 12px 16px;
  border-radius: 12px;
  border-left: 3px solid rgba(59, 130, 246, 0.3);
}

.item-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 12px;
  color: #94a3b8;
  margin-top: 12px;
}

.item-actions {
  display: flex;
  gap: 8px;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.history-item:hover .item-actions {
  opacity: 1;
}
</style>

<!-- 添加全局样式确保布局正确 -->
<style>
body {
  overflow-x: hidden;
}

#app {
  transition: margin-right 0.3s ease;
}

.history-sidebar:not(.collapsed) + #app {
  margin-right: 300px;
}

/* 弹窗样式优化 */
:deep(.el-dialog) {
  z-index: 9999 !important;
  position: fixed !important;
  top: 50% !important;
  left: 50% !important;
  transform: translate(-50%, -50%) !important;
  margin: 0 !important;
}

:deep(.el-overlay) {
  z-index: 9998 !important;
}

:deep(.el-dialog__wrapper) {
  z-index: 9999 !important;
  position: fixed !important;
  top: 0 !important;
  left: 0 !important;
  width: 100vw !important;
  height: 100vh !important;
  display: flex !important;
  align-items: center !important;
  justify-content: center !important;
}

@media (max-width: 768px) {
  .history-sidebar:not(.collapsed) + #app {
    margin-right: 0;
  }
}
</style>