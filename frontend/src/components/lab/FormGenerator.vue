<template>
  <div class="form-generator">
    <div class="form-header">
      <div class="form-title">
        <span class="form-icon">📝</span>
        <div class="title-text">
          <h2>结构化生成</h2>
          <p>通过结构化表单精确控制提示词生成的各个环节</p>
        </div>
      </div>
    </div>

    <div class="form-body">
      <div class="form-section">
        <div class="section-label">
          <span class="label-num">01</span>
          <span class="label-text">基础信息</span>
        </div>

        <div class="form-group primary">
          <label class="form-label">
            任务描述 <span class="required">*</span>
          </label>
          <textarea
            v-model="form.taskDescription"
            class="form-textarea"
            placeholder="描述你想要AI完成的具体任务..."
            rows="3"
          ></textarea>
          <span class="char-hint">{{ form.taskDescription.length }} / 500</span>
        </div>

        <div class="form-row">
          <div class="form-group">
            <label class="form-label">目标受众</label>
            <select v-model="form.targetAudience" class="form-select">
              <option value="general">普通大众</option>
              <option value="professional">专业人士</option>
              <option value="technical">技术人员</option>
              <option value="academic">学术研究</option>
              <option value="creative">创意工作者</option>
            </select>
          </div>

          <div class="form-group">
            <label class="form-label">输出格式</label>
            <select v-model="form.outputFormat" class="form-select">
              <option value="text">纯文本</option>
              <option value="markdown">Markdown</option>
              <option value="json">JSON</option>
              <option value="code">代码</option>
              <option value="list">列表</option>
            </select>
          </div>
        </div>
      </div>

      <div class="form-section">
        <div class="section-label">
          <span class="label-num">02</span>
          <span class="label-text">风格控制</span>
        </div>

        <div class="form-row">
          <div class="form-group">
            <label class="form-label">语气风格</label>
            <select v-model="form.tone" class="form-select">
              <option value="professional">专业正式</option>
              <option value="casual">轻松随意</option>
              <option value="friendly">友好亲切</option>
              <option value="humorous">幽默风趣</option>
              <option value="academic">学术严谨</option>
            </select>
          </div>

          <div class="form-group">
            <label class="form-label">内容长度</label>
            <select v-model="form.length" class="form-select">
              <option value="short">简短精炼</option>
              <option value="medium">中等长度</option>
              <option value="long">详细全面</option>
            </select>
          </div>
        </div>
      </div>

      <div class="form-section">
        <div class="section-label">
          <span class="label-num">03</span>
          <span class="label-text">约束条件</span>
        </div>

        <div class="form-group">
          <label class="form-label">
            额外约束 <span class="optional">(可选)</span>
          </label>
          <textarea
            v-model="form.constraints"
            class="form-textarea"
            placeholder="添加任何特殊的约束条件或要求..."
            rows="2"
          ></textarea>
        </div>

        <div class="form-group">
          <label class="form-label">
            示例参考 <span class="optional">(可选)</span>
          </label>
          <textarea
            v-model="form.examples"
            class="form-textarea"
            placeholder="提供一些参考示例，帮助AI更好地理解你的需求..."
            rows="2"
          ></textarea>
        </div>
      </div>
    </div>

    <div class="form-actions">
      <button class="btn btn-secondary" @click="resetForm">
        <span>🔄</span> 重置
      </button>
      <button
        class="btn btn-primary"
        :disabled="!canGenerate || loading"
        @click="handleGenerate"
      >
        <span v-if="loading" class="loading-spinner"></span>
        <span v-else>⚗️</span>
        {{ loading ? '生成中...' : '开始生成' }}
      </button>
    </div>

    <!-- 生成结果 -->
    <div v-if="showResult" class="result-section">
      <div class="result-header">
        <div class="result-title">
          <span class="result-icon">✨</span>
          <h3>生成结果</h3>
        </div>
        <div class="result-actions">
          <button class="btn btn-sm btn-copy" @click="copyResult">
            📋 复制
          </button>
          <button class="btn btn-sm btn-secondary" @click="clearResult">
            🗑️ 清除
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

const emit = defineEmits(['generate']);

const form = ref({
  taskDescription: '',
  targetAudience: 'general',
  outputFormat: 'text',
  tone: 'professional',
  length: 'short',
  constraints: '',
  examples: '',
});

const loading = ref(false);
const showResult = ref(false);
const result = ref('');

const canGenerate = computed(() => {
  return form.value.taskDescription.trim().length >= 10;
});

function handleGenerate() {
  if (!canGenerate.value || loading.value) return;

  loading.value = true;

  setTimeout(() => {
    result.value = `你是一个专业的AI助手。请根据以下要求完成任务：

任务：${form.value.taskDescription}

目标受众：${form.value.targetAudience}
输出格式：${form.value.outputFormat}
语气风格：${form.value.tone}
内容长度：${form.value.length}

${form.value.constraints ? `约束条件：${form.value.constraints}\n` : ''}
${form.value.examples ? `参考示例：\n${form.value.examples}\n` : ''}

请严格按照以上要求生成内容。`;

    showResult.value = true;
    loading.value = false;
    emit('generate', result.value);
  }, 1500);
}

function resetForm() {
  form.value = {
    taskDescription: '',
    targetAudience: 'general',
    outputFormat: 'text',
    tone: 'professional',
    length: 'short',
    constraints: '',
    examples: '',
  };
  showResult.value = false;
  result.value = '';
}

function copyResult() {
  navigator.clipboard.writeText(result.value);
}

function clearResult() {
  showResult.value = false;
  result.value = '';
}
</script>

<style scoped>
.form-generator {
  background: white;
  border-radius: 24px;
  padding: 28px;
  box-shadow:
    0 4px 6px -1px rgba(0, 0, 0, 0.05),
    0 0 0 1px rgba(226, 232, 240, 0.6);
}

.form-header {
  margin-bottom: 28px;
}

.form-title {
  display: flex;
  align-items: flex-start;
  gap: 16px;
}

.form-icon {
  font-size: 2.5rem;
}

.title-text h2 {
  font-size: 1.4rem;
  font-weight: 700;
  color: #1e293b;
  margin: 0 0 6px 0;
}

.title-text p {
  color: #64748b;
  margin: 0;
  font-size: 0.9rem;
}

.form-body {
  margin-bottom: 24px;
}

.form-section {
  margin-bottom: 24px;
  padding-bottom: 24px;
  border-bottom: 1px dashed #e2e8f0;
}

.form-section:last-child {
  margin-bottom: 0;
  padding-bottom: 0;
  border-bottom: none;
}

.section-label {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 16px;
}

.label-num {
  font-size: 0.75rem;
  font-weight: 700;
  color: white;
  background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
  padding: 4px 10px;
  border-radius: 6px;
}

.label-text {
  font-size: 0.9rem;
  font-weight: 700;
  color: #334155;
}

.form-group {
  margin-bottom: 14px;
  position: relative;
}

.form-group.primary .form-textarea {
  border-color: #3b82f6;
  background: linear-gradient(145deg, #f8fafc 0%, #f1f5f9 100%);
}

.form-row {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 14px;
}

.form-label {
  display: block;
  font-size: 0.875rem;
  font-weight: 600;
  color: #374151;
  margin-bottom: 6px;
}

.required {
  color: #ef4444;
  margin-left: 4px;
}

.optional {
  color: #9ca3af;
  font-weight: 400;
  font-size: 0.8rem;
}

.form-textarea,
.form-select {
  width: 100%;
  padding: 12px 14px;
  border: 1px solid #e2e8f0;
  border-radius: 10px;
  font-size: 0.9rem;
  transition: all 0.25s cubic-bezier(0.4, 0, 0.2, 1);
  background: #f8fafc;
  color: #334155;
}

.form-textarea:hover,
.form-select:hover {
  border-color: #cbd5e1;
  background: white;
}

.form-textarea:focus,
.form-select:focus {
  outline: none;
  border-color: #3b82f6;
  background: white;
  box-shadow: 0 0 0 4px rgba(59, 130, 246, 0.1);
}

.form-textarea {
  resize: vertical;
  min-height: 70px;
}

.char-hint {
  position: absolute;
  right: 12px;
  bottom: 10px;
  font-size: 0.75rem;
  color: #9ca3af;
}

.form-actions {
  display: flex;
  gap: 12px;
  margin-top: 24px;
  justify-content: center;
}

.btn {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 12px 24px;
  border-radius: 12px;
  font-size: 0.9rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.25s cubic-bezier(0.4, 0, 0.2, 1);
  border: none;
}

.btn-primary {
  background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
  color: white;
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.35);
}

.btn-primary:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(59, 130, 246, 0.4);
}

.btn-primary:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.btn-secondary {
  background: white;
  color: #475569;
  border: 1px solid #e2e8f0;
}

.btn-secondary:hover {
  background: #f8fafc;
  border-color: #cbd5e1;
}

.btn-sm {
  padding: 8px 14px;
  font-size: 0.8rem;
}

.btn-copy {
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
  color: white;
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
  to { transform: rotate(360deg); }
}

/* 结果区域 */
.result-section {
  margin-top: 24px;
  background: white;
  border-radius: 20px;
  padding: 24px;
  box-shadow: 0 0 0 1px rgba(226, 232, 240, 0.6);
  animation: slideUp 0.4s cubic-bezier(0.16, 1, 0.3, 1);
}

@keyframes slideUp {
  from { opacity: 0; transform: translateY(16px); }
  to { opacity: 1; transform: translateY(0); }
}

.result-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  padding-bottom: 16px;
  border-bottom: 1px solid #f1f5f9;
}

.result-title {
  display: flex;
  align-items: center;
  gap: 10px;
}

.result-icon {
  font-size: 1.5rem;
}

.result-title h3 {
  font-size: 1.1rem;
  font-weight: 700;
  color: #1e293b;
  margin: 0;
}

.result-actions {
  display: flex;
  gap: 8px;
}

.result-content {
  background: linear-gradient(145deg, #1e293b 0%, #0f172a 100%);
  border-radius: 12px;
  padding: 18px;
  max-height: 350px;
  overflow-y: auto;
}

.result-content pre {
  margin: 0;
  white-space: pre-wrap;
  font-family: 'JetBrains Mono', 'Fira Code', monospace;
  font-size: 0.85rem;
  line-height: 1.6;
  color: #e2e8f0;
}

@media (max-width: 768px) {
  .form-row {
    grid-template-columns: 1fr;
  }

  .form-actions {
    flex-direction: column;
  }

  .btn {
    width: 100%;
    justify-content: center;
  }
}
</style>
