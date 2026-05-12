<template>
  <div class="history-card">
    <!-- 头部：任务描述和时间 -->
    <div class="card-header">
      <div class="card-title">
        {{ history.taskDescription || '无标题' }}
      </div>
      <div class="card-meta">
        <div class="card-time">
          {{ formatTime(history.createdAt) }}
        </div>
      </div>
    </div>
    
    <!-- 内容区 -->
    <div class="card-content">
      <!-- 参数标签 -->
      <div class="card-tags">
        <span v-if="history.targetAudience" class="param-tag audience">
          {{ getAudienceLabel(history.targetAudience) }}
        </span>
        <span v-if="history.outputFormat" class="param-tag format">
          {{ getFormatLabel(history.outputFormat) }}
        </span>
        <span v-if="history.tone" class="param-tag tone">
          {{ getToneLabel(history.tone) }}
        </span>
        <span v-if="history.length" class="param-tag length">
          {{ getLengthLabel(history.length) }}
        </span>
      </div>
      
      <!-- 提示词标签 -->
      <div v-if="history.tags && history.tags.length > 0" class="prompt-tags">
        <span
          v-for="tag in history.tags.slice(0, 5)"
          :key="tag.id"
          class="prompt-tag"
          :style="{
            backgroundColor: tag.color ? tag.color + '15' : 'rgba(99, 102, 241, 0.1)',
            color: tag.color || '#6366f1',
            borderColor: tag.color ? tag.color + '30' : 'rgba(99, 102, 241, 0.2)'
          }"
        >
          {{ tag.name }}
        </span>
        <span v-if="history.tags.length > 5" class="more-tags">+{{ history.tags.length - 5 }}</span>
      </div>
      
      <!-- 生成的提示词预览 -->
      <div class="card-preview">
        <div class="preview-label">
          <span class="label-icon">✨</span>
          生成结果
        </div>
        <div class="preview-text">
          {{ truncate(history.generatedPrompt, 120) || '暂无生成内容' }}
        </div>
      </div>
    </div>
    
    <!-- 操作按钮 -->
    <div class="card-actions">
      <div class="like-wrapper">
        <button 
          class="action-btn btn-like" 
          :class="{ 'is-liked': likeCount > 0 }"
          @click.stop="handleLike"
        >
          <span class="btn-icon">
            <svg viewBox="0 0 24 24" fill="currentColor" class="heart-icon">
              <path d="M12 21.35l-1.45-1.32C5.4 15.36 2 12.28 2 8.5 2 5.42 4.42 3 7.5 3c1.74 0 3.41.81 4.5 2.09C13.09 3.81 14.76 3 16.5 3 19.58 3 22 5.42 22 8.5c0 3.78-3.4 6.86-8.55 11.54L12 21.35z"/>
            </svg>
          </span>
          <span class="like-text">{{ likeCount > 0 ? likeCount : '点赞' }}</span>
        </button>
        <div class="like-particles" v-if="showLikeAnimation">
          <span class="particle" v-for="n in 6" :key="n">❤️</span>
        </div>
      </div>
      <button class="action-btn btn-reuse" @click.stop="handleReuse">
        <span class="btn-icon">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15"/>
          </svg>
        </span>
        <span class="btn-text">复用</span>
      </button>
      <button class="action-btn btn-detail" @click.stop="handleView">
        <span class="btn-icon">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M15 12a3 3 0 11-6 0 3 3 0 016 0z"/>
            <path d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z"/>
          </svg>
        </span>
        <span class="btn-text">详情</span>
      </button>
    </div>
  </div>
</template>

<script>
import { historyApi } from '@/api/history';

export default {
  name: 'HistoryCard',
  props: {
    history: {
      type: Object,
      required: true
    }
  },
  data() {
    return {
      isLiked: false,
      likeCount: 0,
      liking: false,
      showLikeAnimation: false
    };
  },
  watch: {
    history: {
      immediate: true,
      handler(newVal) {
        this.likeCount = newVal?.likeCount || 0;
        this.isLiked = (newVal?.likeCount || 0) > 0;
      }
    }
  },
  methods: {
    formatTime(timestamp) {
      if (!timestamp) return ''
      const date = new Date(timestamp)
      const now = new Date()
      
      if (date.toDateString() === now.toDateString()) {
        return date.toLocaleTimeString('zh-CN', { 
          hour: '2-digit', 
          minute: '2-digit' 
        })
      }
      
      const yesterday = new Date(now)
      yesterday.setDate(yesterday.getDate() - 1)
      if (date.toDateString() === yesterday.toDateString()) {
        return '昨天 '
      }
      
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
    },
    
    truncate(text, length) {
      if (!text) return ''
      if (text.length <= length) return text
      return text.slice(0, length) + '...'
    },
    
    handleLike() {
      const id = this.history?.id;
      if (!id) {
        console.error('历史记录ID不存在');
        return;
      }
      
      historyApi.like(id).then(() => {
        this.likeCount += 1;
        this.showLikeAnimation = true;
        setTimeout(() => {
          this.showLikeAnimation = false;
        }, 800);
      }).catch(err => {
        console.error('点赞失败:', err);
      });
    },
    
    handleReuse() {
      this.$emit('reuse', this.history)
    },
    
    handleView() {
      this.$emit('view', this.history)
    }
  }
}
</script>

<style scoped>
.history-card {
  background: white;
  border-radius: 16px;
  border: 1px solid #e2e8f0;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05), 0 1px 2px rgba(0, 0, 0, 0.03);
  transition: all 0.25s cubic-bezier(0.4, 0, 0.2, 1);
  overflow: hidden;
  position: relative;
}

.history-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 10px 25px -5px rgba(0, 0, 0, 0.08), 0 4px 10px -3px rgba(0, 0, 0, 0.04);
  border-color: #cbd5e1;
}

/* 头部 */
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  padding: 16px 16px 12px;
  gap: 12px;
  border-bottom: 1px solid #f1f5f9;
}

.card-title {
  flex: 1;
  font-weight: 600;
  font-size: 15px;
  color: #1e293b;
  line-height: 1.5;
  word-break: break-word;
  overflow-wrap: break-word;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.card-time {
  font-size: 11px;
  color: #94a3b8;
  white-space: nowrap;
  font-weight: 500;
  background: #f8fafc;
  padding: 4px 10px;
  border-radius: 20px;
  flex-shrink: 0;
}

.card-meta {
  display: flex;
  align-items: center;
  gap: 8px;
}

/* 内容区 */
.card-content {
  padding: 12px 16px;
}

/* 参数标签 */
.card-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  margin-bottom: 10px;
}

.param-tag {
  font-size: 11px;
  font-weight: 500;
  padding: 3px 10px;
  border-radius: 6px;
  border: 1px solid transparent;
}

.param-tag.audience {
  background: rgba(99, 102, 241, 0.08);
  color: #6366f1;
  border-color: rgba(99, 102, 241, 0.15);
}

.param-tag.format {
  background: rgba(16, 185, 129, 0.08);
  color: #10b981;
  border-color: rgba(16, 185, 129, 0.15);
}

.param-tag.tone {
  background: rgba(245, 158, 11, 0.08);
  color: #f59e0b;
  border-color: rgba(245, 158, 11, 0.15);
}

.param-tag.length {
  background: rgba(239, 68, 68, 0.08);
  color: #ef4444;
  border-color: rgba(239, 68, 68, 0.15);
}

/* 提示词标签 */
.prompt-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  margin-bottom: 12px;
  align-items: center;
}

.prompt-tag {
  font-size: 11px;
  font-weight: 500;
  border: 1px solid;
  padding: 3px 10px;
  border-radius: 20px;
}

.more-tags {
  font-size: 11px;
  color: #94a3b8;
  background: #f1f5f9;
  padding: 3px 8px;
  border-radius: 12px;
}

/* 提示词预览 */
.card-preview {
  background: linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%);
  padding: 12px;
  border-radius: 10px;
  border: 1px solid #e2e8f0;
}

.preview-label {
  font-size: 11px;
  color: #64748b;
  font-weight: 600;
  margin-bottom: 6px;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  display: flex;
  align-items: center;
  gap: 4px;
}

.label-icon {
  font-size: 12px;
}

.preview-text {
  font-size: 13px;
  color: #475569;
  line-height: 1.6;
  word-break: break-word;
  overflow-wrap: break-word;
  white-space: pre-wrap;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

/* 操作按钮区域 */
.card-actions {
  display: flex;
  gap: 8px;
  padding: 12px 16px;
  border-top: 1px solid #f1f5f9;
  background: #fafafa;
}

.action-btn {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  padding: 8px 12px;
  border-radius: 8px;
  font-size: 13px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
  border: none;
  outline: none;
}

.action-btn:active {
  transform: scale(0.98);
}

.btn-icon {
  width: 16px;
  height: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.btn-icon svg {
  width: 100%;
  height: 100%;
}

/* 点赞按钮 */
.btn-like {
  background: transparent;
  color: #9ca3af;
  border: none;
  padding: 6px 10px;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  display: flex;
  align-items: center;
  gap: 4px;
}

.btn-like:hover {
  background: rgba(239, 68, 68, 0.1);
  color: #ef4444;
}

.btn-like:active {
  transform: scale(0.9);
}

.btn-like.is-liked {
  color: #ef4444;
  background: rgba(239, 68, 68, 0.1);
}

.btn-like .btn-icon {
  width: 18px;
  height: 18px;
  transition: transform 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.btn-like:hover .btn-icon {
  transform: scale(1.2);
}

.btn-like.is-liked .heart-icon {
  animation: heartBeat 0.6s ease-in-out;
}

.heart-icon {
  width: 100%;
  height: 100%;
  transition: all 0.3s;
}

@keyframes heartBeat {
  0% { transform: scale(1); }
  25% { transform: scale(1.3); }
  50% { transform: scale(0.9); }
  75% { transform: scale(1.15); }
  100% { transform: scale(1); }
}

.like-text {
  font-size: 13px;
  font-weight: 500;
}

.like-wrapper {
  position: relative;
  display: inline-flex;
  align-items: center;
}

.like-particles {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  pointer-events: none;
}

.particle {
  position: absolute;
  font-size: 12px;
  animation: particleFly 0.8s ease-out forwards;
  opacity: 0;
}

.particle:nth-child(1) { animation-delay: 0ms; --angle: -60deg; --distance: 30px; }
.particle:nth-child(2) { animation-delay: 50ms; --angle: -30deg; --distance: 35px; }
.particle:nth-child(3) { animation-delay: 100ms; --angle: 0deg; --distance: 40px; }
.particle:nth-child(4) { animation-delay: 150ms; --angle: 30deg; --distance: 35px; }
.particle:nth-child(5) { animation-delay: 200ms; --angle: 60deg; --distance: 30px; }
.particle:nth-child(6) { animation-delay: 250ms; --angle: 90deg; --distance: 25px; }

@keyframes particleFly {
  0% {
    opacity: 1;
    transform: translate(0, 0) scale(0.5);
  }
  50% {
    opacity: 1;
    transform: translate(calc(cos(var(--angle)) * var(--distance)), calc(sin(var(--angle)) * var(--distance) - 20px)) scale(1.2);
  }
  100% {
    opacity: 0;
    transform: translate(calc(cos(var(--angle)) * var(--distance)), calc(sin(var(--angle)) * var(--distance) - 40px)) scale(0.3);
  }
}

/* 复用按钮 - 渐变蓝色 */
.btn-reuse {
  background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
  color: white;
  box-shadow: 0 2px 6px rgba(59, 130, 246, 0.3);
}

.btn-reuse:hover {
  background: linear-gradient(135deg, #2563eb 0%, #1d4ed8 100%);
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.4);
  transform: translateY(-1px);
}

/* 详情按钮 - 灰色边框 */
.btn-detail {
  background: white;
  color: #475569;
  border: 1px solid #e2e8f0;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
}

.btn-detail:hover {
  background: #f8fafc;
  border-color: #cbd5e1;
  color: #1e293b;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.08);
}

/* 点击时的涟漪效果 */
.action-btn::after {
  content: '';
  position: absolute;
  top: 50%;
  left: 50%;
  width: 0;
  height: 0;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.3);
  transform: translate(-50%, -50%);
  transition: width 0.3s, height 0.3s;
}

.action-btn:active::after {
  width: 100%;
  height: 100%;
}

.history-card {
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.96) 0%, rgba(248, 250, 252, 0.92) 100%);
  border-radius: 22px;
  border: 1px solid rgba(148, 163, 184, 0.16);
  box-shadow: 0 14px 28px rgba(15, 23, 42, 0.05);
}

.history-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 24px 38px rgba(15, 23, 42, 0.09);
  border-color: rgba(37, 99, 235, 0.16);
}

.card-header {
  padding: 18px 18px 12px;
  border-bottom: 1px solid rgba(148, 163, 184, 0.12);
}

.card-time,
.param-tag,
.prompt-tag,
.more-tags {
  border-radius: 999px;
}

.card-time {
  color: var(--text-muted);
  background: rgba(248, 250, 252, 0.9);
  padding: 5px 10px;
}

.card-content {
  padding: 14px 18px 16px;
}

.param-tag {
  padding: 4px 10px;
}

.card-preview {
  padding: 14px;
  border-radius: 18px;
  border: 1px solid rgba(148, 163, 184, 0.14);
  background: linear-gradient(135deg, #f8fbff 0%, #f1f6fb 100%);
}

.card-actions {
  padding: 12px 18px 18px;
  border-top: 1px solid rgba(148, 163, 184, 0.12);
  background: rgba(255, 255, 255, 0.34);
}

.action-btn {
  border-radius: 14px;
}

.btn-like {
  background: rgba(248, 250, 252, 0.75);
  border: 1px solid rgba(148, 163, 184, 0.12);
}

.btn-detail {
  background: rgba(255, 255, 255, 0.9);
}
</style>
