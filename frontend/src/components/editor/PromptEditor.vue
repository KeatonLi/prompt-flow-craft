<template>
  <div class="prompt-editor">
    <div class="editor-header">
      <h2>提示词生成器</h2>
      <p class="subtitle">输入任务描述，AI 将为您生成专业级提示词</p>
    </div>

    <div class="editor-form">
      <div class="form-group">
        <label class="form-label">
          任务描述
          <span class="required">*</span>
        </label>
        <textarea
          v-model="form.taskDescription"
          class="form-textarea"
          rows="4"
          placeholder="请详细描述您希望AI完成的任务，例如：写一篇关于人工智能发展趋势的文章..."
          @keydown.ctrl.enter="generate"
        />
        <span class="char-count">{{ form.taskDescription.length }}/500</span>
      </div>

      <div class="form-row">
        <div class="form-group">
          <label class="form-label">目标受众</label>
          <select v-model="form.targetAudience" class="form-select">
            <option value="">请选择</option>
            <option value="general">普通用户</option>
            <option value="professional">专业人士</option>
            <option value="student">学生</option>
            <option value="developer">开发者</option>
            <option value="creator">创作者</option>
          </select>
        </div>

        <div class="form-group">
          <label class="form-label">输出格式</label>
          <select v-model="form.outputFormat" class="form-select">
            <option value="">请选择</option>
            <option value="text">文本</option>
            <option value="list">列表</option>
            <option value="table">表格</option>
            <option value="code">代码</option>
            <option value="json">JSON</option>
          </select>
        </div>

        <div class="form-group">
          <label class="form-label">语调风格</label>
          <select v-model="form.tone" class="form-select">
            <option value="">请选择</option>
            <option value="formal">正式</option>
            <option value="friendly">友好</option>
            <option value="professional">专业</option>
            <option value="creative">创意</option>
            <option value="concise">简洁</option>
          </select>
        </div>

        <div class="form-group">
          <label class="form-label">内容长度</label>
          <select v-model="form.length" class="form-select">
            <option value="">请选择</option>
            <option value="short">简短</option>
            <option value="medium">中等</option>
            <option value="long">详细</option>
            <option value="very-long">非常详细</option>
          </select>
        </div>
      </div>

      <div class="form-group">
        <label class="form-label">
          约束条件
          <span class="optional">（可选）</span>
        </label>
        <textarea
          v-model="form.constraints"
          class="form-textarea"
          rows="2"
          placeholder="请输入任何约束条件或特殊要求..."
        />
      </div>

      <div class="form-group">
        <label class="form-label">
          参考示例
          <span class="optional">（可选）</span>
        </label>
        <textarea
          v-model="form.examples"
          class="form-textarea"
          rows="2"
          placeholder="请提供参考示例..."
        />
      </div>

      <div class="form-actions">
        <button
          class="btn btn-primary"
          :disabled="!canGenerate || loading"
          @click="generate"
        >
          <span v-if="loading" class="loading-spinner"></span>
          <span v-else>🚀</span>
          {{ loading ? '生成中...' : '生成提示词' }}
        </button>
        <button class="btn btn-secondary" @click="reset">
          重置
        </button>
      </div>
    </div>

    <!-- 生成的结果展示 -->
    <div v-if="result && result.trim()" class="result-section" key="result-section">
      <div class="result-header">
        <h3>
          <span class="result-icon">✨</span>
          生成的提示词
        </h3>
        <div class="result-actions">
          <button class="btn btn-sm btn-copy" @click="copyResult">
            <span>📋</span>
            复制
          </button>
          <button class="btn btn-sm btn-clear" @click="clearResult">
            <span>🗑️</span>
            清空
          </button>
        </div>
      </div>
      <div class="result-content">
        <pre>{{ result }}</pre>
      </div>
    </div>
    
    <!-- 调试信息（开发时可用） -->
    <div v-else-if="loading" class="loading-hint">
      <div class="loading-spinner"></div>
      <p>正在生成提示词，请稍候...</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue';
import { promptApi } from '@/api';
import type { PromptRequest, PromptRecord } from '@/types';

const loading = ref(false);
const result = ref('');

const form = ref<PromptRequest>({
  taskDescription: '',
  targetAudience: '',
  outputFormat: '',
  constraints: '',
  examples: '',
  tone: '',
  length: ''
});

const canGenerate = computed(() => {
  return form.value.taskDescription.length >= 10;
});

async function generate() {
  if (!canGenerate.value || loading.value) return;

  loading.value = true;
  result.value = ''; // 清空之前的结果
  
  try {
    console.log('开始生成提示词...', form.value);
    const generated = await promptApi.generate(form.value);
    
    console.log('生成成功，结果长度:', generated?.length);
    
    // 确保有内容
    if (!generated || generated.trim() === '') {
      showToast('生成结果为空，请重试', 'error');
      return;
    }
    
    result.value = generated;
    
    // 显示成功提示
    showToast('✨ 提示词生成成功！', 'success');
    
    // 强制更新DOM后滚动
    setTimeout(() => {
      const resultSection = document.querySelector('.result-section');
      console.log('结果区域元素:', resultSection);
      if (resultSection) {
        resultSection.scrollIntoView({ behavior: 'smooth', block: 'center' });
      }
    }, 150);
    
  } catch (error: any) {
    console.error('生成失败:', error);
    showToast(error?.message || '生成失败，请稍后重试', 'error');
  } finally {
    loading.value = false;
  }
}

// 显示提示消息
function showToast(message: string, type: 'success' | 'error' = 'success') {
  // 创建提示元素
  const toast = document.createElement('div');
  toast.className = `toast toast-${type}`;
  toast.textContent = message;
  
  // 样式
  toast.style.cssText = `
    position: fixed;
    top: 80px;
    left: 50%;
    transform: translateX(-50%) translateY(-20px);
    padding: 12px 24px;
    border-radius: 8px;
    font-size: 14px;
    font-weight: 500;
    z-index: 9999;
    opacity: 0;
    transition: all 0.3s ease;
    ${type === 'success' 
      ? 'background: linear-gradient(135deg, #10b981 0%, #059669 100%); color: white; box-shadow: 0 4px 12px rgba(16, 185, 129, 0.3);' 
      : 'background: linear-gradient(135deg, #ef4444 0%, #dc2626 100%); color: white; box-shadow: 0 4px 12px rgba(239, 68, 68, 0.3);'}
  `;
  
  document.body.appendChild(toast);
  
  // 显示动画
  requestAnimationFrame(() => {
    toast.style.opacity = '1';
    toast.style.transform = 'translateX(-50%) translateY(0)';
  });
  
  // 自动隐藏
  setTimeout(() => {
    toast.style.opacity = '0';
    toast.style.transform = 'translateX(-50%) translateY(-20px)';
    setTimeout(() => {
      document.body.removeChild(toast);
    }, 300);
  }, 3000);
}

function reset() {
  form.value = {
    taskDescription: '',
    targetAudience: '',
    outputFormat: '',
    constraints: '',
    examples: '',
    tone: '',
    length: ''
  };
  result.value = '';
}

function clearResult() {
  result.value = '';
}

async function copyResult() {
  try {
    await navigator.clipboard.writeText(result.value);
    alert('已复制到剪贴板');
  } catch (error) {
    console.error('复制失败:', error);
  }
}

// 监听复用历史记录事件
function handleReuseHistory(event: Event) {
  const customEvent = event as CustomEvent<PromptRecord>;
  const record = customEvent.detail;
  
  form.value = {
    taskDescription: record.taskDescription || '',
    targetAudience: record.targetAudience || '',
    outputFormat: record.outputFormat || '',
    constraints: record.constraints || '',
    examples: record.examples || '',
    tone: record.tone || '',
    length: record.length || ''
  };
  
  // 滚动到表单顶部
  window.scrollTo({ top: 0, behavior: 'smooth' });
  
  // 显示提示
  // 使用 Element Plus 的 message 组件（如果可用）
  const message = (window as any).$message;
  if (message) {
    message.success('已加载历史记录到表单');
  }
}

onMounted(() => {
  window.addEventListener('reuse-history', handleReuseHistory);
});

onUnmounted(() => {
  window.removeEventListener('reuse-history', handleReuseHistory);
});
</script>

<style scoped>
.prompt-editor {
  max-width: 800px;
  margin: 0 auto;
}

.editor-header {
  text-align: center;
  margin-bottom: 32px;
}

.editor-header h2 {
  font-size: 1.875rem;
  font-weight: 700;
  color: #1e293b;
  margin: 0 0 8px 0;
}

.subtitle {
  color: #64748b;
  margin: 0;
}

.editor-form {
  background: white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.form-group {
  margin-bottom: 20px;
  position: relative;
}

.form-row {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
}

.form-label {
  display: block;
  font-size: 0.875rem;
  font-weight: 500;
  color: #374151;
  margin-bottom: 6px;
}

.form-label .required {
  color: #ef4444;
  margin-left: 4px;
}

.form-label .optional {
  color: #9ca3af;
  font-weight: 400;
}

.form-textarea,
.form-select {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid #d1d5db;
  border-radius: 8px;
  font-size: 0.875rem;
  transition: all 0.2s;
  background: white;
}

.form-textarea:focus,
.form-select:focus {
  outline: none;
  border-color: #3b82f6;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
}

.form-textarea {
  resize: vertical;
  min-height: 80px;
}

.char-count {
  position: absolute;
  right: 12px;
  bottom: 12px;
  font-size: 0.75rem;
  color: #9ca3af;
}

.form-actions {
  display: flex;
  gap: 12px;
  margin-top: 24px;
}

.btn {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 10px 20px;
  border-radius: 8px;
  font-size: 0.875rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
  border: none;
}

.btn-primary {
  background: #3b82f6;
  color: white;
}

.btn-primary:hover:not(:disabled) {
  background: #2563eb;
}

.btn-primary:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.btn-secondary {
  background: #f3f4f6;
  color: #374151;
}

.btn-secondary:hover {
  background: #e5e7eb;
}

.btn-sm {
  padding: 6px 12px;
  font-size: 0.75rem;
}

.loading-spinner {
  width: 16px;
  height: 16px;
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-top-color: white;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

.result-section {
  margin-top: 32px;
  background: white;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 10px 25px -5px rgba(0, 0, 0, 0.1), 0 4px 10px -3px rgba(0, 0, 0, 0.05);
  border: 1px solid #e2e8f0;
  animation: slideUp 0.4s ease-out;
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.result-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20px;
  padding-bottom: 16px;
  border-bottom: 2px solid #f1f5f9;
}

.result-header h3 {
  font-size: 1.25rem;
  font-weight: 700;
  color: #1e293b;
  margin: 0;
  display: flex;
  align-items: center;
  gap: 8px;
}

.result-icon {
  font-size: 1.4rem;
}

.result-actions {
  display: flex;
  gap: 10px;
}

.btn-copy {
  background: linear-gradient(135deg, #10b981 0%, #059669 100%) !important;
  color: white !important;
  border: none !important;
}

.btn-copy:hover {
  background: linear-gradient(135deg, #059669 0%, #047857 100%) !important;
}

.btn-clear {
  background: #f1f5f9 !important;
  color: #64748b !important;
  border: 1px solid #e2e8f0 !important;
}

.btn-clear:hover {
  background: #e2e8f0 !important;
  color: #475569 !important;
}

.result-content {
  background: #1e293b;
  border-radius: 12px;
  padding: 20px;
  max-height: 600px;
  overflow-y: auto;
}

.result-content pre {
  margin: 0;
  white-space: pre-wrap;
  word-wrap: break-word;
  font-family: 'JetBrains Mono', 'Fira Code', 'Monaco', monospace;
  font-size: 0.9rem;
  line-height: 1.7;
  color: #e2e8f0;
}

/* 加载提示 */
.loading-hint {
  margin-top: 32px;
  text-align: center;
  padding: 40px;
  color: #64748b;
}

.loading-hint p {
  margin-top: 12px;
  font-size: 0.9rem;
}

@media (max-width: 768px) {
  .form-row {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style>
