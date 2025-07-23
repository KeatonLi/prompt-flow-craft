<template>
  <el-card class="history-card" shadow="hover">
    <div class="card-header">
      <div class="card-title">
        <el-text truncated>{{ history.taskDescription || '无标题' }}</el-text>
      </div>
      <div class="card-time">
        {{ formatTime(history.createdAt) }}
      </div>
    </div>
    
    <div class="card-content">
      <div class="card-tags">
        <el-tag v-if="history.targetAudience" size="small" type="info">
          {{ getAudienceLabel(history.targetAudience) }}
        </el-tag>
        <el-tag v-if="history.outputFormat" size="small" type="success">
          {{ getFormatLabel(history.outputFormat) }}
        </el-tag>
        <el-tag v-if="history.tone" size="small" type="warning">
          {{ getToneLabel(history.tone) }}
        </el-tag>
        <el-tag v-if="history.length" size="small" type="danger">
          {{ getLengthLabel(history.length) }}
        </el-tag>
      </div>
      
      <div class="card-preview">
        <el-text truncated :line-clamp="2">
          {{ history.generatedPrompt }}
        </el-text>
      </div>
    </div>
    
    <div class="card-actions">
      <el-button size="small" type="primary" @click="$emit('reuse', history)">
        <el-icon><RefreshRight /></el-icon>
        复用
      </el-button>
      <el-button size="small" @click="$emit('view', history)">
        <el-icon><View /></el-icon>
        详情
      </el-button>
    </div>
  </el-card>
</template>

<script>
export default {
  name: 'HistoryCard',
  props: {
    history: {
      type: Object,
      required: true
    }
  },
  methods: {
    formatTime(timestamp) {
      if (!timestamp) return ''
      const date = new Date(timestamp)
      const now = new Date()
      const diff = now - date
      
      // 如果是今天
      if (date.toDateString() === now.toDateString()) {
        return date.toLocaleTimeString('zh-CN', { 
          hour: '2-digit', 
          minute: '2-digit' 
        })
      }
      
      // 如果是昨天
      const yesterday = new Date(now)
      yesterday.setDate(yesterday.getDate() - 1)
      if (date.toDateString() === yesterday.toDateString()) {
        return '昨天'
      }
      
      // 如果是本周内
      const weekStart = new Date(now)
      weekStart.setDate(weekStart.getDate() - weekStart.getDay())
      if (date > weekStart) {
        return date.toLocaleDateString('zh-CN', { 
          weekday: 'short' 
        })
      }
      
      // 其他情况
      return date.toLocaleDateString('zh-CN', {
        month: 'short',
        day: 'numeric'
      })
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
    }
  }
}
</script>

<style scoped>
.history-card {
  margin-bottom: 16px;
  border-radius: 12px;
  border: 1px solid rgba(230, 230, 230, 0.4);
  background: #ffffff;
  backdrop-filter: blur(10px);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06), 0 1px 4px rgba(0, 0, 0, 0.04);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  cursor: pointer;
  overflow: hidden;
  position: relative;
}

.history-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 2px;
  background: linear-gradient(90deg, #7dd3a0 0%, #a8e6cf 50%, #88d8a3 100%);
  opacity: 0.6;
  transition: opacity 0.3s ease;
}

.history-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1), 0 2px 8px rgba(0, 0, 0, 0.06);
}

.history-card:hover::before {
  opacity: 1;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 12px;
  padding: 16px 20px 12px;
  border-bottom: 1px solid rgba(240, 240, 240, 0.6);
  background: rgba(250, 250, 250, 0.5);
}

.card-title {
  flex: 1;
  margin-right: 12px;
}

.card-title :deep(.el-text) {
  font-weight: 600;
  font-size: 15px;
  color: #333333;
  line-height: 1.4;
}

.card-time {
  font-size: 12px;
  color: #888888;
  white-space: nowrap;
  font-weight: 500;
  background: rgba(125, 211, 160, 0.1);
  padding: 4px 8px;
  border-radius: 8px;
}

.card-content {
  margin-bottom: 16px;
  padding: 16px 20px;
  background: #ffffff;
}

.card-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  margin-bottom: 12px;
}

.card-tags :deep(.el-tag) {
  border-radius: 8px;
  font-size: 11px;
  font-weight: 500;
  border: none;
  padding: 4px 8px;
}

.card-tags :deep(.el-tag--info) {
  background: rgba(125, 211, 160, 0.08);
  color: #666666;
}

.card-tags :deep(.el-tag--success) {
  background: rgba(168, 230, 207, 0.08);
  color: #666666;
}

.card-tags :deep(.el-tag--warning) {
  background: rgba(255, 193, 7, 0.08);
  color: #666666;
}

.card-tags :deep(.el-tag--danger) {
  background: rgba(220, 53, 69, 0.08);
  color: #666666;
}

.card-preview {
  font-size: 13px;
  color: #555555;
  line-height: 1.6;
  background: #f8f9fa;
  padding: 12px;
  border-radius: 10px;
  border-left: 2px solid #7dd3a0;
}

.card-actions {
  display: flex;
  gap: 10px;
  justify-content: flex-end;
}

.card-actions :deep(.el-button) {
  padding: 8px 16px;
  font-size: 12px;
  border-radius: 8px;
  font-weight: 500;
  transition: all 0.3s ease;
}

.card-actions :deep(.el-button--primary) {
  background: #7dd3a0;
  border: none;
  color: white;
}

.card-actions :deep(.el-button--primary:hover) {
  background: #6bc98a;
  transform: translateY(-1px);
}

.card-actions :deep(.el-button:not(.el-button--primary)) {
  background: #f8f9fa;
  border: 1px solid rgba(230, 230, 230, 0.6);
  color: #666666;
}

.card-actions :deep(.el-button:not(.el-button--primary):hover) {
  background: #ffffff;
  border-color: #7dd3a0;
  color: #7dd3a0;
  transform: translateY(-1px);
}
</style>