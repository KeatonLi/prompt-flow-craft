<template>
  <div class="research-generator">
    <!-- 研究流头部 -->
    <div class="research-header">
      <div class="research-title">
        <span class="research-icon">🔬</span>
        <div class="title-text">
          <h2>研究流生成</h2>
          <p>分步骤深入研究，生成高质量提示词</p>
        </div>
      </div>
      <div class="research-progress">
        <div
          v-for="(step, index) in steps"
          :key="step.id"
          :class="['progress-step', {
            active: currentStep === index,
            completed: currentStep > index
          }]"
        >
          <div class="step-number">
            <span v-if="currentStep > index">✓</span>
            <span v-else>{{ index + 1 }}</span>
          </div>
          <span class="step-label">{{ step.label }}</span>
        </div>
      </div>
    </div>

    <!-- 研究流内容区 -->
    <div class="research-content">
      <!-- 步骤1：需求分析 -->
      <div v-if="currentStep === 0" class="step-panel">
        <div class="step-intro">
          <h3>📊 需求分析</h3>
          <p>详细描述你的需求，AI会进行深度分析和拆解</p>
        </div>

        <div class="analysis-form">
          <div class="form-group">
            <label>核心任务</label>
            <textarea
              v-model="researchData.coreTask"
              placeholder="你要AI完成什么核心任务？"
              rows="3"
            ></textarea>
          </div>

          <div class="form-group">
            <label>使用场景</label>
            <textarea
              v-model="researchData.scenario"
              placeholder="在什么场景下使用这个提示词？"
              rows="2"
            ></textarea>
          </div>

          <div class="form-group">
            <label>目标用户</label>
            <textarea
              v-model="researchData.targetUsers"
              placeholder="谁会使用这个提示词？他们有什么特点？"
              rows="2"
            ></textarea>
          </div>

          <div class="form-row">
            <div class="form-group">
              <label>预期输出类型</label>
              <select v-model="researchData.outputType">
                <option value="text">文本内容</option>
                <option value="code">代码</option>
                <option value="data">数据/表格</option>
                <option value="analysis">分析报告</option>
                <option value="creative">创意内容</option>
              </select>
            </div>
            <div class="form-group">
              <label>复杂程度</label>
              <select v-model="researchData.complexity">
                <option value="simple">简单</option>
                <option value="medium">中等</option>
                <option value="complex">复杂</option>
              </select>
            </div>
          </div>
        </div>

        <div class="step-actions">
          <button class="btn btn-primary" @click="nextStep">
            开始分析 ➤
          </button>
        </div>
      </div>

      <!-- 步骤2：AI分析 -->
      <div v-if="currentStep === 1" class="step-panel">
        <div class="step-intro">
          <h3>🧠 AI分析中</h3>
          <p>正在分析你的需求，构建提示词框架...</p>
        </div>

        <div class="analysis-progress">
          <div class="analysis-item" v-for="(item, index) in analysisItems" :key="index">
            <div :class="['analysis-status', item.status]">
              <span v-if="item.status === 'done'">✓</span>
              <span v-else-if="item.status === 'processing'" class="spin">◌</span>
              <span v-else>○</span>
            </div>
            <div class="analysis-content">
              <span class="analysis-label">{{ item.label }}</span>
              <span v-if="item.result" class="analysis-result">{{ item.result }}</span>
            </div>
          </div>
        </div>

        <div class="step-actions">
          <button class="btn btn-secondary" @click="prevStep">
            ← 上一步
          </button>
          <button class="btn btn-primary" :disabled="!analysisComplete" @click="nextStep">
            {{ analysisComplete ? '确认并继续 ➤' : '分析中...' }}
          </button>
        </div>
      </div>

      <!-- 步骤3：结构设计 -->
      <div v-if="currentStep === 2" class="step-panel">
        <div class="step-intro">
          <h3>🏗️ 提示词结构设计</h3>
          <p>根据分析结果，设计提示词的结构和组成部分</p>
        </div>

        <div class="structure-builder">
          <div class="structure-preview">
            <div class="preview-header">
              <span class="preview-title">提示词结构预览</span>
              <span class="preview-badge">Draft</span>
            </div>
            <div class="preview-content">
              <div class="struct-section" v-for="(section, index) in structure" :key="index">
                <div class="section-header">
                  <span class="section-icon">{{ section.icon }}</span>
                  <span class="section-name">{{ section.name }}</span>
                  <button class="section-toggle" @click="toggleSection(index)">
                    {{ section.expanded ? '−' : '+' }}
                  </button>
                </div>
                <div v-if="section.expanded" class="section-body">
                  <textarea
                    v-model="section.content"
                    :placeholder="section.placeholder"
                    rows="3"
                  ></textarea>
                </div>
              </div>
            </div>
          </div>

          <div class="structure-suggestions">
            <h4>推荐添加组件</h4>
            <div class="suggestion-list">
              <button
                v-for="comp in availableComponents"
                :key="comp.id"
                class="component-chip"
                :class="{ added: isComponentAdded(comp.id) }"
                @click="addComponent(comp)"
              >
                <span>{{ comp.icon }}</span>
                {{ comp.name }}
              </button>
            </div>
          </div>
        </div>

        <div class="step-actions">
          <button class="btn btn-secondary" @click="prevStep">
            ← 上一步
          </button>
          <button class="btn btn-primary" @click="nextStep">
            生成提示词 ➤
          </button>
        </div>
      </div>

      <!-- 步骤4：生成结果 -->
      <div v-if="currentStep === 3" class="step-panel">
        <div class="step-intro success">
          <h3>✨ 提示词生成完成</h3>
          <p>你的提示词已准备就绪，可以复制使用或进一步调整</p>
        </div>

        <div class="final-result">
          <div class="result-toolbar">
            <button class="tool-btn" @click="copyResult">
              📋 复制
            </button>
            <button class="tool-btn" @click="downloadResult">
              📥 导出
            </button>
            <button class="tool-btn" @click="resetResearch">
              🔄 重新开始
            </button>
          </div>
          <div class="result-code">
            <pre>{{ finalPrompt }}</pre>
          </div>
        </div>

        <div class="step-actions">
          <button class="btn btn-secondary" @click="prevStep">
            ← 修改结构
          </button>
          <button class="btn btn-primary" @click="saveToHistory">
            💾 保存到历史
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, reactive } from 'vue';

const currentStep = ref(0);
const steps = [
  { id: 'analysis', label: '需求分析' },
  { id: 'processing', label: 'AI分析' },
  { id: 'structure', label: '结构设计' },
  { id: 'result', label: '生成结果' },
];

const researchData = reactive({
  coreTask: '',
  scenario: '',
  targetUsers: '',
  outputType: 'text',
  complexity: 'medium',
});

const analysisItems = ref([
  { label: '理解核心任务', status: 'pending', result: '' },
  { label: '分析用户特征', status: 'pending', result: '' },
  { label: '识别约束条件', status: 'pending', result: '' },
  { label: '构建框架', status: 'pending', result: '' },
]);

const structure = ref([
  { id: 'role', name: '角色定义', icon: '👤', expanded: true, content: '', placeholder: '定义AI扮演的角色...' },
  { id: 'task', name: '任务说明', icon: '📝', expanded: true, content: '', placeholder: '具体任务描述...' },
  { id: 'output', name: '输出格式', icon: '📄', expanded: true, content: '', placeholder: '期望的输出格式...' },
  { id: 'constraint', name: '约束条件', icon: '⚠️', expanded: false, content: '', placeholder: '必须遵守的约束...' },
  { id: 'example', name: '示例', icon: '💡', expanded: false, content: '', placeholder: '参考示例...' },
]);

const availableComponents = [
  { id: 'context', name: '上下文', icon: '📚' },
  { id: 'tone', name: '语气风格', icon: '🎨' },
  { id: 'quality', name: '质量要求', icon: '✅' },
  { id: 'step', name: '分步指引', icon: '🔢' },
];

const finalPrompt = ref('');

const analysisComplete = computed(() => {
  return analysisItems.value.every(item => item.status === 'done');
});

async function nextStep() {
  if (currentStep.value === 0) {
    currentStep.value = 1;
    await runAnalysis();
  } else if (currentStep.value === 1) {
    currentStep.value = 2;
    prepareStructure();
  } else if (currentStep.value === 2) {
    currentStep.value = 3;
    generatePrompt();
  }
}

function prevStep() {
  if (currentStep.value > 0) {
    currentStep.value--;
  }
}

async function runAnalysis() {
  for (let i = 0; i < analysisItems.value.length; i++) {
    analysisItems.value[i].status = 'processing';
    await new Promise(resolve => setTimeout(resolve, 800));

    const results = [
      `核心任务：${researchData.coreTask || '待定义'}`,
      `目标用户：${researchData.targetUsers || '通用用户'}`,
      `场景约束：${researchData.scenario || '无特殊约束'}`,
      `框架类型：${researchData.complexity === 'simple' ? '线性结构' : researchData.complexity === 'complex' ? '多层次结构' : '标准结构'}`,
    ];
    analysisItems.value[i].result = results[i];
    analysisItems.value[i].status = 'done';
  }
}

function prepareStructure() {
  if (researchData.outputType === 'code') {
    structure.value = [
      { id: 'role', name: '角色定义', icon: '👤', expanded: true, content: '你是一个专业的程序员', placeholder: '定义AI扮演的角色...' },
      { id: 'task', name: '任务说明', icon: '📝', expanded: true, content: researchData.coreTask, placeholder: '具体任务描述...' },
      { id: 'tech', name: '技术要求', icon: '🔧', expanded: true, content: '', placeholder: '编程语言、技术栈要求...' },
      { id: 'output', name: '输出格式', icon: '📄', expanded: true, content: '代码块+注释', placeholder: '期望的输出格式...' },
    ];
  }
}

function generatePrompt() {
  let prompt = '';

  for (const section of structure.value) {
    if (section.content) {
      prompt += `${section.name}:\n${section.content}\n\n`;
    }
  }

  if (!prompt.trim()) {
    prompt = `你是一个专业的AI助手。

任务：${researchData.coreTask || '待定义'}

请严格按照上述要求生成内容。`;
  }

  finalPrompt.value = prompt.trim();
}

function toggleSection(index: number) {
  structure.value[index].expanded = !structure.value[index].expanded;
}

function addComponent(comp: any) {
  if (isComponentAdded(comp.id)) return;

  const sectionMap: any = {
    context: { name: '上下文', icon: '📚', placeholder: '提供背景信息...' },
    tone: { name: '语气风格', icon: '🎨', placeholder: '描述期望的语气...' },
    quality: { name: '质量要求', icon: '✅', placeholder: '列出质量标准...' },
    step: { name: '分步指引', icon: '🔢', placeholder: '描述步骤...' },
  };

  const newSection = { id: comp.id, ...sectionMap[comp.id], expanded: true, content: '' };
  structure.value.push(newSection);
}

function isComponentAdded(id: string): boolean {
  return structure.value.some(s => s.id === id);
}

function copyResult() {
  navigator.clipboard.writeText(finalPrompt.value);
}

function downloadResult() {
  const blob = new Blob([finalPrompt.value], { type: 'text/plain' });
  const url = URL.createObjectURL(blob);
  const a = document.createElement('a');
  a.href = url;
  a.download = 'prompt.txt';
  a.click();
}

function resetResearch() {
  currentStep.value = 0;
  researchData.coreTask = '';
  researchData.scenario = '';
  researchData.targetUsers = '';
  researchData.outputType = 'text';
  researchData.complexity = 'medium';
  analysisItems.value.forEach(item => {
    item.status = 'pending';
    item.result = '';
  });
  finalPrompt.value = '';
}

function saveToHistory() {
  console.log('Saving to history:', finalPrompt.value);
}
</script>

<style scoped>
.research-generator {
  background: white;
  border-radius: 20px;
  padding: 24px;
  box-shadow: 0 0 0 1px rgba(226, 232, 240, 0.6);
}

/* 头部 */
.research-header {
  margin-bottom: 24px;
}

.research-title {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 20px;
}

.research-icon {
  font-size: 1.8rem;
}

.title-text h2 {
  font-size: 1.2rem;
  font-weight: 700;
  color: #1e293b;
  margin: 0 0 4px 0;
}

.title-text p {
  font-size: 0.8rem;
  color: #64748b;
  margin: 0;
}

/* 进度条 */
.research-progress {
  display: flex;
  justify-content: space-between;
  position: relative;
  padding: 0 40px;
}

.research-progress::before {
  content: '';
  position: absolute;
  top: 14px;
  left: 70px;
  right: 70px;
  height: 2px;
  background: #e2e8f0;
}

.progress-step {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6px;
  position: relative;
  z-index: 1;
}

.step-number {
  width: 28px;
  height: 28px;
  border-radius: 50%;
  background: #e2e8f0;
  color: #94a3b8;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 0.8rem;
  font-weight: 700;
  transition: all 0.3s;
}

.progress-step.active .step-number {
  background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
  color: white;
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.4);
}

.progress-step.completed .step-number {
  background: #10b981;
  color: white;
}

.step-label {
  font-size: 0.75rem;
  font-weight: 600;
  color: #94a3b8;
}

.progress-step.active .step-label,
.progress-step.completed .step-label {
  color: #334155;
}

/* 内容区 */
.research-content {
  min-height: 350px;
}

.step-panel {
  animation: fadeIn 0.3s ease;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}

.step-intro {
  text-align: center;
  margin-bottom: 24px;
}

.step-intro h3 {
  font-size: 1.2rem;
  font-weight: 700;
  color: #1e293b;
  margin: 0 0 6px 0;
}

.step-intro p {
  color: #64748b;
  margin: 0;
}

.step-intro.success h3 {
  color: #10b981;
}

/* 分析表单 */
.analysis-form {
  max-width: 550px;
  margin: 0 auto 20px;
}

.form-group {
  margin-bottom: 16px;
}

.form-group label {
  display: block;
  font-size: 0.85rem;
  font-weight: 600;
  color: #374151;
  margin-bottom: 6px;
}

.form-group textarea,
.form-group select {
  width: 100%;
  padding: 12px 14px;
  border: 1px solid #e2e8f0;
  border-radius: 10px;
  font-size: 0.85rem;
  background: #f8fafc;
  color: #334155;
  transition: all 0.2s;
}

.form-group textarea::placeholder,
.form-group select::placeholder {
  color: #94a3b8;
}

.form-group textarea:focus,
.form-group select:focus {
  outline: none;
  border-color: #3b82f6;
  background: white;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
}

.form-group select option {
  background: white;
}

.form-row {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
}

/* 分析进度 */
.analysis-progress {
  max-width: 450px;
  margin: 0 auto 20px;
}

.analysis-item {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 14px;
  background: #f8fafc;
  border-radius: 10px;
  margin-bottom: 10px;
  border: 1px solid #e2e8f0;
}

.analysis-status {
  width: 24px;
  height: 24px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 0.8rem;
  background: #e2e8f0;
  color: #94a3b8;
}

.analysis-status.done {
  background: #10b981;
  color: white;
}

.analysis-status.processing {
  background: #3b82f6;
  color: white;
}

.spin {
  animation: spin 1s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.analysis-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.analysis-label {
  font-weight: 600;
  font-size: 0.85rem;
  color: #334155;
}

.analysis-result {
  font-size: 0.8rem;
  color: #64748b;
}

/* 结构设计 */
.structure-builder {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 20px;
}

.structure-preview {
  background: #f8fafc;
  border-radius: 14px;
  overflow: hidden;
  border: 1px solid #e2e8f0;
}

.preview-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  background: #1e293b;
}

.preview-title {
  font-weight: 600;
  font-size: 0.85rem;
  color: white;
}

.preview-badge {
  font-size: 0.65rem;
  padding: 2px 6px;
  background: #f59e0b;
  color: #0a0f1a;
  border-radius: 4px;
  font-weight: 600;
}

.preview-content {
  padding: 14px;
}

.struct-section {
  margin-bottom: 10px;
  background: white;
  border-radius: 8px;
  overflow: hidden;
  border: 1px solid #e2e8f0;
}

.section-header {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 12px;
  cursor: pointer;
  transition: background 0.2s;
}

.section-header:hover {
  background: #f1f5f9;
}

.section-icon {
  font-size: 0.9rem;
}

.section-name {
  flex: 1;
  font-weight: 600;
  font-size: 0.85rem;
  color: #334155;
}

.section-toggle {
  width: 22px;
  height: 22px;
  border: none;
  background: #e2e8f0;
  border-radius: 5px;
  font-size: 0.9rem;
  cursor: pointer;
  color: #64748b;
}

.section-body {
  padding: 0 12px 12px;
}

.section-body textarea {
  width: 100%;
  padding: 8px 10px;
  border: 1px solid #e2e8f0;
  border-radius: 6px;
  font-size: 0.8rem;
  resize: vertical;
  min-height: 50px;
  background: #f8fafc;
  color: #334155;
}

.section-body textarea:focus {
  outline: none;
  border-color: #3b82f6;
}

.structure-suggestions h4 {
  font-size: 0.9rem;
  font-weight: 700;
  color: #334155;
  margin: 0 0 12px 0;
}

.suggestion-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.component-chip {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 12px;
  background: #f8fafc;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  font-size: 0.8rem;
  color: #475569;
  cursor: pointer;
  transition: all 0.2s;
}

.component-chip:hover {
  background: #f1f5f9;
  border-color: #3b82f6;
  color: #3b82f6;
}

.component-chip.added {
  background: #f0fdf4;
  border-color: #10b981;
  color: #166534;
}

/* 最终结果 */
.final-result {
  background: #1e293b;
  border-radius: 14px;
  overflow: hidden;
  margin-bottom: 20px;
}

.result-toolbar {
  display: flex;
  gap: 6px;
  padding: 12px 16px;
  background: #0f172a;
}

.tool-btn {
  padding: 6px 12px;
  background: rgba(255, 255, 255, 0.1);
  border: none;
  border-radius: 6px;
  color: #e2e8f0;
  font-size: 0.8rem;
  cursor: pointer;
  transition: background 0.2s;
}

.tool-btn:hover {
  background: rgba(255, 255, 255, 0.2);
}

.result-code {
  padding: 16px;
  max-height: 350px;
  overflow-y: auto;
}

.result-code pre {
  margin: 0;
  white-space: pre-wrap;
  font-family: 'JetBrains Mono', monospace;
  font-size: 0.8rem;
  line-height: 1.6;
  color: #e2e8f0;
}

/* 按钮 */
.step-actions {
  display: flex;
  justify-content: center;
  gap: 10px;
  margin-top: 20px;
}

.btn {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 12px 24px;
  border-radius: 10px;
  font-size: 0.9rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
  border: none;
}

.btn-primary {
  background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
  color: white;
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.35);
}

.btn-primary:hover:not(:disabled) {
  transform: translateY(-1px);
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

@media (max-width: 768px) {
  .research-progress::before {
    display: none;
  }

  .progress-step {
    flex-direction: column;
  }

  .step-label {
    display: none;
  }

  .form-row {
    grid-template-columns: 1fr;
  }

  .structure-builder {
    grid-template-columns: 1fr;
  }
}
</style>
