<template>
  <div class="history-sidebar" :class="{ 'collapsed': !isOpen }">
    <div class="sidebar-header">
      <div class="header-content">
        <h3 v-if="isOpen">历史记录</h3>
        <el-button
          :icon="isOpen ? 'ArrowRight' : 'ArrowLeft'"
          circle
          size="small"
          @click="toggleSidebar"
          class="toggle-btn"
        />
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
import { getRecentHistory, searchHistory } from '@/api/history'
import HistoryCard from './HistoryCard.vue'

export default {
  name: 'HistorySidebar',
  components: {
    HistoryCard
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
        const response = await getRecentHistory(this.limit)
        if (response.success) {
          this.historyList = response.data
          this.displayedHistory = this.historyList.slice(0, this.pageSize)
          this.totalCount = this.historyList.length
          this.currentPage = 1
        }
      } catch (error) {
        this.$message.error('加载历史记录失败')
      } finally {
        this.loading = false
      }
    },
    
    async handleSearch() {
      if (this.searchKeyword.trim()) {
        this.loading = true
        try {
          const response = await searchHistory(this.searchKeyword.trim())
          if (response.success) {
            this.historyList = response.data
            this.displayedHistory = this.historyList.slice(0, this.pageSize)
            this.totalCount = response.total
            this.currentPage = 1
          }
        } catch (error) {
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
  }
}
</script>

<style scoped>
.history-sidebar {
  position: fixed;
  right: 0;
  top: 60px;
  bottom: 80px;
  width: 340px;
  background: rgba(255, 255, 255, 0.98);
  backdrop-filter: blur(25px);
  border-left: 1px solid rgba(230, 230, 230, 0.3);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  z-index: 1000;
  box-shadow: -12px 0 40px rgba(0, 0, 0, 0.08), -4px 0 20px rgba(0, 0, 0, 0.04);
  border-radius: 16px 0 0 16px;
}

.history-sidebar.collapsed {
  transform: translateX(100%);
}

.sidebar-header {
  padding: 24px;
  border-bottom: 1px solid rgba(230, 230, 230, 0.4);
  background: linear-gradient(135deg, #fafafa 0%, #f5f5f5 100%);
  border-radius: 16px 0 0 0;
  position: relative;
}

.sidebar-header::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 24px;
  right: 24px;
  height: 2px;
  background: linear-gradient(90deg, #7dd3a0 0%, #a8e6cf 50%, #88d8a3 100%);
  border-radius: 1px;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-content h3 {
  margin: 0;
  color: #333333;
  font-size: 18px;
  font-weight: 600;
  letter-spacing: -0.02em;
}

.toggle-btn {
  background: #ffffff;
  border: 1px solid rgba(230, 230, 230, 0.6);
  color: #666666;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
  border-radius: 8px;
}

.toggle-btn:hover {
  background: #f8f9fa;
  border-color: #7dd3a0;
  color: #7dd3a0;
  transform: scale(1.05);
  box-shadow: 0 4px 12px rgba(125, 211, 160, 0.2);
}

.sidebar-content {
  height: calc(100% - 88px);
  overflow-y: auto;
  padding: 24px;
  scrollbar-width: thin;
  scrollbar-color: rgba(200, 200, 200, 0.4) transparent;
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
  margin-bottom: 16px;
}

.search-input :deep(.el-input__wrapper) {
  background: #f8f9fa;
  border: 1px solid rgba(230, 230, 230, 0.6);
  border-radius: 10px;
  transition: all 0.3s ease;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.04);
}

.search-input :deep(.el-input__wrapper:hover) {
  border-color: rgba(125, 211, 160, 0.4);
  background: #ffffff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.search-input :deep(.el-input__wrapper.is-focus) {
  border-color: #7dd3a0;
  background: #ffffff;
  box-shadow: 0 0 0 3px rgba(125, 211, 160, 0.1), 0 2px 8px rgba(0, 0, 0, 0.08);
}

.filter-section {
  margin-bottom: 16px;
}

.limit-select {
  width: 100%;
}

.limit-select :deep(.el-input__wrapper) {
  border-radius: 10px;
  border: 1px solid rgba(230, 230, 230, 0.6);
  background: #f8f9fa;
  transition: all 0.3s ease;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.04);
}

.limit-select :deep(.el-input__wrapper:hover) {
  border-color: rgba(125, 211, 160, 0.4);
  background: #ffffff;
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
  border-color: #7dd3a0;
  background: #ffffff;
  color: #7dd3a0;
  transform: translateY(-1px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.08);
}

.sidebar-collapsed {
  position: absolute;
  left: -50px;
  top: 50%;
  transform: translateY(-50%);
  width: 50px;
  height: 80px;
  background: linear-gradient(135deg, #88d8a3 0%, #7dd3a0 100%);
  border-radius: 12px 0 0 12px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  color: white;
  box-shadow: -8px 0 24px rgba(125, 211, 160, 0.3);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  font-size: 20px;
}

.sidebar-collapsed:hover {
  background: linear-gradient(135deg, #7dd3a0 0%, #6bc98a 100%);
  width: 55px;
  box-shadow: -12px 0 32px rgba(125, 211, 160, 0.4);
  transform: translateY(-50%) scale(1.05);
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
  color: #2d5a3d;
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

@media (max-width: 768px) {
  .history-sidebar:not(.collapsed) + #app {
    margin-right: 0;
  }
}
</style>