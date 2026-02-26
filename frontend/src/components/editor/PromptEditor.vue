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

    <div v-if="result" class="result-section">
      <div class="result-header">
        <h3>生成的提示词</h3>
        <div class="result-actions">
          <button class="btn btn-sm" @click="copyResult">
            📋 复制
          </button>
          <button class="btn btn-sm" @click="clearResult">
            🗑️ 清空
          </button>
        </div>
      </div>
      <div class="result-content">
        <pre>{{ result }}</pre>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue';
import { promptApi } from '@/api';
import type { PromptRequest } from '@/types';

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
  try {
    const generated = await promptApi.generate(form.value);
    result.value = generated;
  } catch (error) {
    console.error('生成失败:', error);
    alert('生成失败，请稍后重试');
  } finally {
    loading.value = false;
  }
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
  margin-top: 24px;
  background: white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.result-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
}

.result-header h3 {
  font-size: 1.125rem;
  font-weight: 600;
  color: #1e293b;
  margin: 0;
}

.result-actions {
  display: flex;
  gap: 8px;
}

.result-content {
  background: #f8fafc;
  border-radius: 8px;
  padding: 16px;
}

.result-content pre {
  margin: 0;
  white-space: pre-wrap;
  word-wrap: break-word;
  font-family: 'JetBrains Mono', 'Fira Code', monospace;
  font-size: 0.875rem;
  line-height: 1.6;
  color: #334155;
}

@media (max-width: 768px) {
  .form-row {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style>
