<template>
  <div class="prompt-editor">
    <div class="editor-header">
      <h2>提示词生成器</h2>
      <p class="subtitle">输入任务描述，AI 将为您生成专业级提示词</p>
      <div class="mode-switch">
        <button 
          :class="['mode-btn', { active: !isMultiStep }]" 
          @click="isMultiStep = false"
        >
          简单模式
        </button>
        <button 
          :class="['mode-btn', { active: isMultiStep }]" 
          @click="isMultiStep = true"
        >
          🔄 工作流模式
        </button>
      </div>
    </div>

    <div class="editor-form">
      <div class="form-header">
        <button class="btn btn-example" @click="loadExample">
          <span>💡</span> 使用示例
        </button>
      </div>

      <div class="form-group">
        <label class="form-label">
          任务描述
          <span class="required">*</span>
        </label>
        <textarea
          v-model="form.taskDescription"
          class="form-textarea"
          rows="4"
          maxlength="500"
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
            <option value="text">文本</option>
            <option value="list">列表</option>
            <option value="table">表格</option>
            <option value="code">代码</option>
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
          maxlength="200"
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
          maxlength="200"
          placeholder="请提供参考示例..."
        />
      </div>

      <!-- 多步骤模式 -->
      <div v-if="isMultiStep" class="multi-step-section">
        <div class="steps-header">
          <h3>步骤列表</h3>
          <button class="btn btn-secondary" @click="addStep">
            <span>+</span> 添加步骤
          </button>
        </div>
        
        <div class="steps-list">
          <div 
            v-for="(step, index) in steps" 
            :key="index"
            class="step-card"
            :class="{ active: runningStep === index, completed: runningStep > index }"
            draggable="true"
            @dragstart="onDragStart(index, $event)"
            @dragover.prevent
            @drop="onDrop(index)"
          >
            <div class="step-drag-handle">⋮⋮</div>
            <div class="step-number">{{ index + 1 }}</div>
            <div class="step-content">
              <div class="step-header-row">
                <span class="step-label">步骤 {{ index + 1 }}</span>
                <button v-if="steps.length > 1" class="btn-remove-step" @click="removeStep(index)">×</button>
              </div>
              <textarea
                v-model="step.taskDescription"
                class="step-textarea"
                rows="2"
                placeholder="请输入任务描述..."
              />
              <div class="step-options">
                <select v-model="step.targetAudience" class="step-select">
                  <option value="general">普通用户</option>
                  <option value="professional">专业人士</option>
                  <option value="student">学生</option>
                  <option value="developer">开发者</option>
                  <option value="creator">创作者</option>
                </select>
                <select v-model="step.outputFormat" class="step-select">
                  <option value="text">文本</option>
                  <option value="list">列表</option>
                  <option value="table">表格</option>
                  <option value="code">代码</option>
                </select>
                <select v-model="step.tone" class="step-select">
                  <option value="formal">正式</option>
                  <option value="friendly">友好</option>
                  <option value="professional">专业</option>
                  <option value="creative">创意</option>
                  <option value="concise">简洁</option>
                </select>
              </div>
              <div v-if="stepResults[index]" class="step-result">
                <pre>{{ stepResults[index] }}</pre>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div class="form-actions">
        <!-- 单步模式按钮 -->
        <button
          v-if="!isMultiStep"
          class="btn btn-primary"
          :disabled="!canGenerate || loading"
          @click="generate"
        >
          <span v-if="loading" class="loading-spinner"></span>
          <span v-else>🚀</span>
          {{ loading ? '生成中...' : '生成提示词' }}
        </button>
        <!-- 多步骤模式按钮 -->
        <button
          v-else
          class="btn btn-primary"
          :disabled="loading"
          @click="runMultiStep"
        >
          <span v-if="loading" class="loading-spinner"></span>
          <span v-else>🚀</span>
          {{ loading ? '生成中...' : '运行全部步骤' }}
        </button>
        <button 
          v-if="loading && cancelStream" 
          class="btn btn-secondary" 
          @click="cancelGeneration"
        >
          停止
        </button>
        <button class="btn btn-secondary" @click="reset">
          重置
        </button>
      </div>
    </div>

    <!-- 生成的结果展示 - 流式展示 -->
    <div v-if="showResult" class="result-section" key="result-section">
      <div class="result-header">
        <h3>
          <span class="result-icon">✨</span>
          {{ isStreaming ? '正在生成...' : '生成的提示词' }}
          <span v-if="isStreaming" class="typing-cursor">|</span>
        </h3>
        <div class="result-actions">
          <div class="export-dropdown">
            <button class="btn btn-sm btn-export" @click="toggleExportMenu" :disabled="isStreaming">
              <span>📥</span>
              导出
            </button>
            <div v-if="showExportMenu" class="export-menu">
              <button @click="exportPrompt('markdown')">
                <span>📝</span> Markdown (.md)
              </button>
              <button @click="exportPrompt('json')">
                <span>{}</span> JSON (.json)
              </button>
              <button @click="exportPrompt('txt')">
                <span>📄</span> 纯文本 (.txt)
              </button>
            </div>
          </div>
          <button class="btn btn-sm btn-copy" @click="copyResult" :disabled="isStreaming">
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
        <pre>{{ displayedResult }}</pre>
      </div>
      <div v-if="isStreaming" class="stream-status">
        <span class="stream-dot"></span>
        <span>AI 正在思考中...</span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue';
import { useRoute } from 'vue-router';
import { promptApi } from '@/api';
import type { PromptRequest, PromptRecord } from '@/types';

const route = useRoute();
const loading = ref(false);
const result = ref('');
const displayedResult = ref('');  // 用于流式展示的结果
const showResult = ref(false);
const isStreaming = ref(false);
const cancelStream = ref<(() => void) | null>(null);
const showExportMenu = ref(false);

const isMultiStep = ref(false);
const steps = ref([
  { taskDescription: '', targetAudience: 'general', outputFormat: 'text', tone: 'professional', length: 'short' }
]);
const stepResults = ref<string[]>([]);
const runningStep = ref(-1);
const draggedStepIndex = ref<number | null>(null);

const form = ref<PromptRequest>({
  taskDescription: '',
  targetAudience: 'general',
  outputFormat: 'text',
  constraints: '',
  examples: '',
  tone: 'professional',
  length: 'short'
});

const defaultExample = {
  taskDescription: '写一篇关于人工智能发展趋势的文章',
  targetAudience: 'general',
  outputFormat: 'text',
  constraints: '需要包含最新的技术发展动态和未来展望',
  examples: '',
  tone: 'professional',
  length: 'short'
};

function loadExample() {
  form.value = { ...defaultExample };
}

const canGenerate = computed(() => {
  return form.value.taskDescription.length >= 10;
});

async function generate() {
  if (!canGenerate.value || loading.value) return;

  loading.value = true;
  isStreaming.value = true;
  showResult.value = true;
  result.value = '';
  displayedResult.value = '';
  
  // 先滚动到结果区域
  setTimeout(() => {
    const resultSection = document.querySelector('.result-section');
    if (resultSection) {
      resultSection.scrollIntoView({ behavior: 'smooth', block: 'center' });
    }
  }, 100);
  
  try {
    
    
    let lastUpdateTime = Date.now();
    let pendingContent = '';
    
    // 使用流式 API
    cancelStream.value = promptApi.generateStream(
      form.value,
      // onMessage - 收到每个片段时
      (content: string) => {
        pendingContent += content;
        
        // 节流更新，每 50ms 更新一次 UI，避免过于频繁
        const now = Date.now();
        if (now - lastUpdateTime > 50) {
          result.value += pendingContent;
          displayedResult.value += pendingContent;
          pendingContent = '';
          lastUpdateTime = now;
          
          // 自动滚动到底部
          setTimeout(() => {
            const resultContent = document.querySelector('.result-content');
            if (resultContent) {
              resultContent.scrollTop = resultContent.scrollHeight;
            }
          }, 0);
        }
      },
      // onDone - 完成时
      (fullContent: string) => {
        // 处理剩余未更新的内容
        if (pendingContent) {
          result.value += pendingContent;
          displayedResult.value += pendingContent;
          pendingContent = '';
        }
        
        
        isStreaming.value = false;
        loading.value = false;
        cancelStream.value = null;
        
        // 确保最终内容一致
        if (fullContent && fullContent.length > result.value.length) {
          result.value = fullContent;
          displayedResult.value = fullContent;
        }
        
        showToast('✨ 提示词生成成功！', 'success');
      },
      // onError - 出错时
      (error: string) => {
        // 处理剩余未更新的内容
        if (pendingContent) {
          result.value += pendingContent;
          displayedResult.value += pendingContent;
          pendingContent = '';
        }
        
        
        isStreaming.value = false;
        loading.value = false;
        cancelStream.value = null;
        showToast(error || '生成失败，请稍后重试', 'error');
        if (!result.value) {
          showResult.value = false;
        }
      }
    );
    
  } catch (error: any) {
    
    isStreaming.value = false;
    loading.value = false;
    showToast(error?.message || '生成失败，请稍后重试', 'error');
    showResult.value = false;
  }
}

function cancelGeneration() {
  if (cancelStream.value) {
    cancelStream.value();
    cancelStream.value = null;
  }
  isStreaming.value = false;
  loading.value = false;
  showToast('已停止生成', 'info');
}

function addStep() {
  steps.value.push({
    taskDescription: '',
    targetAudience: 'general',
    outputFormat: 'text',
    tone: 'professional',
    length: 'short'
  });
}

function removeStep(index: number) {
  if (steps.value.length > 1) {
    steps.value.splice(index, 1);
    stepResults.value.splice(index, 1);
  }
}

function onDragStart(index: number, event: DragEvent) {
  draggedStepIndex.value = index;
  event.dataTransfer?.setData('text/plain', String(index));
}

function onDrop(targetIndex: number) {
  if (draggedStepIndex.value === null || draggedStepIndex.value === targetIndex) return;
  const newSteps = [...steps.value];
  const [removed] = newSteps.splice(draggedStepIndex.value, 1);
  newSteps.splice(targetIndex, 0, removed);
  steps.value = newSteps;
  
  const newResults = [...stepResults.value];
  const [removedResult] = newResults.splice(draggedStepIndex.value, 1);
  newResults.splice(targetIndex, 0, removedResult);
  stepResults.value = newResults;
  
  draggedStepIndex.value = null;
}

async function runMultiStep() {
  loading.value = true;
  stepResults.value = [];
  
  for (let i = 0; i < steps.value.length; i++) {
    if (!steps.value[i].taskDescription) {
      showToast(`请填写步骤 ${i + 1} 的任务描述`, 'warning');
      continue;
    }
    
    runningStep.value = i;
    
    try {
      const response = await fetch('/api/prompt/generate', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(steps.value[i])
      });
      const data = await response.json();
      stepResults.value[i] = data.data?.prompt || data.prompt || '生成失败';
    } catch (e) {
      stepResults.value[i] = '生成失败: ' + (e as Error).message;
    }
  }
  
  runningStep.value = -1;
  loading.value = false;
  
  if (stepResults.value.length > 0) {
    result.value = stepResults.value.join('\n\n---\n\n');
    displayedResult.value = result.value;
    showResult.value = true;
  }
  
  showToast('多步骤生成完成！', 'success');
}

function reset() {
  form.value = {
    taskDescription: '',
    targetAudience: 'general',
    outputFormat: 'text',
    constraints: '',
    examples: '',
    tone: 'professional',
    length: 'short'
  };
  result.value = '';
  displayedResult.value = '';
  showResult.value = false;
  isStreaming.value = false;
  if (cancelStream.value) {
    cancelStream.value();
    cancelStream.value = null;
  }
}

function clearResult() {
  result.value = '';
  displayedResult.value = '';
  showResult.value = false;
}

async function copyResult() {
  try {
    await navigator.clipboard.writeText(result.value);
    showToast('已复制到剪贴板', 'success');
  } catch (error) {
    
    showToast('复制失败', 'error');
  }
}

function toggleExportMenu() {
  showExportMenu.value = !showExportMenu.value;
}

function exportPrompt(format: 'markdown' | 'json' | 'txt') {
  showExportMenu.value = false;
  
  const timestamp = new Date().toISOString().slice(0, 10);
  let content = '';
  let filename = `prompt-${timestamp}`;
  let mimeType = 'text/plain';
  
  if (format === 'markdown') {
    content = `# AI 提示词\n\n${result.value}\n\n---\n*由 Prompt Flow Craft 生成*\n`;
    filename += '.md';
    mimeType = 'text/markdown';
  } else if (format === 'json') {
    const exportData = {
      prompt: result.value,
      metadata: {
        generatedAt: new Date().toISOString(),
        taskDescription: form.value.taskDescription,
        targetAudience: form.value.targetAudience,
        outputFormat: form.value.outputFormat,
        tone: form.value.tone,
        length: form.value.length
      }
    };
    content = JSON.stringify(exportData, null, 2);
    filename += '.json';
    mimeType = 'application/json';
  } else {
    content = result.value;
    filename += '.txt';
  }
  
  // 创建下载
  const blob = new Blob([content], { type: mimeType });
  const url = URL.createObjectURL(blob);
  const a = document.createElement('a');
  a.href = url;
  a.download = filename;
  document.body.appendChild(a);
  a.click();
  document.body.removeChild(a);
  URL.revokeObjectURL(url);
  
  showToast(`已导出为 ${format.toUpperCase()} 格式`, 'success');
}

// 点击外部关闭导出菜单
function handleClickOutside(event: MouseEvent) {
  const target = event.target as HTMLElement;
  if (!target.closest('.export-dropdown')) {
    showExportMenu.value = false;
  }
}

// 显示提示消息
function showToast(message: string, type: 'success' | 'error' | 'info' = 'success') {
  const toast = document.createElement('div');
  toast.className = `toast toast-${type}`;
  toast.textContent = message;
  
  const colors = {
    success: 'background: linear-gradient(135deg, #10b981 0%, #059669 100%); color: white; box-shadow: 0 4px 12px rgba(16, 185, 129, 0.3);',
    error: 'background: linear-gradient(135deg, #ef4444 0%, #dc2626 100%); color: white; box-shadow: 0 4px 12px rgba(239, 68, 68, 0.3);',
    info: 'background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%); color: white; box-shadow: 0 4px 12px rgba(59, 130, 246, 0.3);'
  };
  
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
    ${colors[type]}
  `;
  
  document.body.appendChild(toast);
  
  requestAnimationFrame(() => {
    toast.style.opacity = '1';
    toast.style.transform = 'translateX(-50%) translateY(0)';
  });
  
  setTimeout(() => {
    toast.style.opacity = '0';
    toast.style.transform = 'translateX(-50%) translateY(-20px)';
    setTimeout(() => {
      document.body.removeChild(toast);
    }, 300);
  }, 3000);
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
  
  window.scrollTo({ top: 0, behavior: 'smooth' });
}

onMounted(() => {
  // 从URL query参数回填表单
  if (route.query.task) {
    form.value.taskDescription = String(route.query.task);
  }
  if (route.query.audience) {
    form.value.targetAudience = String(route.query.audience);
  }
  if (route.query.format) {
    form.value.outputFormat = String(route.query.format);
  }
  if (route.query.tone) {
    form.value.tone = String(route.query.tone);
  }
  if (route.query.length) {
    form.value.length = String(route.query.length);
  }
  if (route.query.constraints) {
    form.value.constraints = String(route.query.constraints);
  }
  if (route.query.examples) {
    form.value.examples = String(route.query.examples);
  }

  window.addEventListener('reuse-history', handleReuseHistory);
  window.addEventListener('click', handleClickOutside);
});

onUnmounted(() => {
  window.removeEventListener('reuse-history', handleReuseHistory);
  window.removeEventListener('click', handleClickOutside);
  if (cancelStream.value) {
    cancelStream.value();
  }
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

.mode-switch {
  display: inline-flex;
  background: #f1f5f9;
  border-radius: 10px;
  padding: 4px;
  margin-top: 16px;
}

.mode-btn {
  padding: 8px 20px;
  border: none;
  background: transparent;
  color: #64748b;
  font-size: 0.875rem;
  font-weight: 500;
  cursor: pointer;
  border-radius: 8px;
  transition: all 0.2s;
}

.mode-btn.active {
  background: white;
  color: #3b82f6;
  box-shadow: 0 2px 8px rgba(59, 130, 246, 0.15);
}

.mode-btn:hover:not(.active) {
  color: #334155;
}

.editor-form {
  background: white;
  border-radius: 16px;
  padding: 28px;
  box-shadow: 
    0 4px 6px -1px rgba(0, 0, 0, 0.05),
    0 2px 4px -2px rgba(0, 0, 0, 0.05),
    0 0 0 1px rgba(226, 232, 240, 0.6);
  position: relative;
  overflow: hidden;
}

.editor-form::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, #3b82f6, #3b82f6, #3b82f6);
}

.form-group {
  margin-bottom: 20px;
  position: relative;
}

.form-header {
  display: flex;
  justify-content: flex-end;
  margin-bottom: 16px;
}

.multi-step-section {
  margin-top: 24px;
  padding-top: 24px;
  border-top: 1px solid #e2e8f0;
}

.steps-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.steps-header h3 {
  margin: 0;
  font-size: 1rem;
  font-weight: 600;
  color: #1e293b;
}

.steps-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.step-card {
  background: #f8fafc;
  border: 1px solid #e2e8f0;
  border-radius: 12px;
  padding: 16px;
  display: flex;
  gap: 12px;
  transition: all 0.2s;
  cursor: grab;
}

.step-card:hover {
  border-color: #3b82f6;
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.1);
}

.step-card.active {
  border-color: #3b82f6;
  background: #eff6ff;
}

.step-card.completed {
  border-color: #10b981;
  background: #f0fdf4;
}

.step-drag-handle {
  color: #94a3b8;
  font-size: 1.2rem;
  cursor: grab;
  padding: 4px;
}

.step-number {
  width: 28px;
  height: 28px;
  background: linear-gradient(135deg, #3b82f6, #2563eb);
  color: white;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
  font-size: 0.875rem;
  flex-shrink: 0;
}

.step-content {
  flex: 1;
  min-width: 0;
}

.step-header-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.step-label {
  font-weight: 600;
  color: #1e293b;
  font-size: 0.875rem;
}

.btn-remove-step {
  width: 24px;
  height: 24px;
  border: none;
  background: #fee2e2;
  color: #ef4444;
  border-radius: 6px;
  cursor: pointer;
  font-size: 1rem;
  display: flex;
  align-items: center;
  justify-content: center;
}

.btn-remove-step:hover {
  background: #fecaca;
}

.step-textarea {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  font-size: 0.875rem;
  resize: vertical;
  margin-bottom: 8px;
}

.step-textarea:focus {
  outline: none;
  border-color: #3b82f6;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
}

.step-options {
  display: flex;
  gap: 8px;
}

.step-select {
  flex: 1;
  padding: 6px 10px;
  border: 1px solid #e2e8f0;
  border-radius: 6px;
  font-size: 0.8rem;
  background: white;
}

.step-result {
  margin-top: 12px;
  padding: 12px;
  background: white;
  border-radius: 8px;
  border: 1px solid #e2e8f0;
}

.step-result pre {
  margin: 0;
  font-size: 0.8rem;
  white-space: pre-wrap;
  max-height: 100px;
  overflow-y: auto;
}

.btn-example {
  background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
  color: white;
  padding: 8px 16px;
  font-size: 0.8rem;
}

.btn-example:hover {
  background: linear-gradient(135deg, #7c3aed 0%, #6d28d9 100%);
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
  box-shadow: 0 0 0 4px rgba(59, 130, 246, 0.1), 0 2px 8px rgba(59, 130, 246, 0.08);
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
  padding: 12px 24px;
  border-radius: 10px;
  font-size: 0.9rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.25s cubic-bezier(0.4, 0, 0.2, 1);
  border: none;
  position: relative;
  overflow: hidden;
}

.btn::after {
  content: '';
  position: absolute;
  inset: 0;
  background: linear-gradient(white, transparent);
  opacity: 0;
  transition: opacity 0.2s;
}

.btn:hover::after {
  opacity: 0.1;
}

.btn:active {
  transform: scale(0.98);
}

.btn-primary {
  background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
  color: white;
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.35);
}

.btn-primary:hover:not(:disabled) {
  background: linear-gradient(135deg, #2563eb 0%, #1d4ed8 100%);
  box-shadow: 0 6px 16px rgba(59, 130, 246, 0.4);
  transform: translateY(-1px);
}

.btn-primary:disabled {
  opacity: 0.5;
  cursor: not-allowed;
  box-shadow: none;
}

.btn-secondary {
  background: white;
  color: #475569;
  border: 1px solid #e2e8f0;
}

.btn-secondary:hover {
  background: #f8fafc;
  border-color: #cbd5e1;
  color: #334155;
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

/* 结果区域 */
.result-section {
  margin-top: 32px;
  background: white;
  border-radius: 20px;
  padding: 28px;
  box-shadow: 
    0 20px 40px -12px rgba(0, 0, 0, 0.12),
    0 0 0 1px rgba(226, 232, 240, 0.6);
  border: 1px solid rgba(226, 232, 240, 0.8);
  animation: slideUp 0.5s cubic-bezier(0.16, 1, 0.3, 1);
  position: relative;
  overflow: hidden;
}

.result-section::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 3px;
  background: linear-gradient(90deg, #3b82f6, #3b82f6, #3b82f6);
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(24px) scale(0.98);
  }
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
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

.typing-cursor {
  display: inline-block;
  width: 2px;
  height: 24px;
  background: #3b82f6;
  margin-left: 4px;
  animation: blink 1s step-end infinite;
}

@keyframes blink {
  0%, 100% { opacity: 1; }
  50% { opacity: 0; }
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

.btn-copy:hover:not(:disabled) {
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

/* 导出下拉菜单 */
.export-dropdown {
  position: relative;
}

.btn-export {
  background: linear-gradient(135deg, #6366f1 0%, #4f46e5 100%) !important;
  color: white !important;
  border: none !important;
}

.btn-export:hover:not(:disabled) {
  background: linear-gradient(135deg, #4f46e5 0%, #4338ca 100%) !important;
}

.export-menu {
  position: absolute;
  top: 100%;
  right: 0;
  margin-top: 8px;
  background: white;
  border-radius: 10px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.15), 0 0 0 1px rgba(0, 0, 0, 0.05);
  min-width: 160px;
  z-index: 100;
  overflow: hidden;
  animation: dropdownFade 0.2s ease;
}

@keyframes dropdownFade {
  from {
    opacity: 0;
    transform: translateY(-8px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.export-menu button {
  width: 100%;
  padding: 10px 16px;
  border: none;
  background: none;
  text-align: left;
  font-size: 0.85rem;
  color: #374151;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 10px;
  transition: all 0.15s;
}

.export-menu button:hover {
  background: #f1f5f9;
  color: #4f46e5;
}

.result-content {
  background: linear-gradient(145deg, #1e293b 0%, #0f172a 100%);
  border-radius: 14px;
  padding: 24px;
  padding-top: 44px;
  max-height: 500px;
  overflow-y: auto;
  position: relative;
}

.result-content::before {
  content: '';
  position: absolute;
  top: 12px;
  left: 12px;
  width: 12px;
  height: 12px;
  border-radius: 50%;
  background: #ef4444;
  box-shadow: 20px 0 0 #f59e0b, 40px 0 0 #22c55e;
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

/* 流式状态 */
.stream-status {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-top: 16px;
  padding: 12px 16px;
  background: #f0fdf4;
  border-radius: 8px;
  color: #166534;
  font-size: 0.875rem;
}

.stream-dot {
  width: 8px;
  height: 8px;
  background: #22c55e;
  border-radius: 50%;
  animation: pulse 1.5s ease-in-out infinite;
}

@keyframes pulse {
  0%, 100% { 
    opacity: 1;
    transform: scale(1);
  }
  50% { 
    opacity: 0.5;
    transform: scale(1.2);
  }
}

@media (max-width: 768px) {
  .form-row {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style>
