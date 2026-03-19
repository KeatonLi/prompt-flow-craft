<template>
  <div class="chat-generator">
    <!-- 对话头部 -->
    <div class="chat-header">
      <div class="chat-title">
        <span class="chat-icon">💬</span>
        <div class="title-text">
          <h2>对话式生成</h2>
          <p>用自然语言描述你的需求，AI会理解并生成提示词</p>
        </div>
      </div>
      <div class="chat-actions">
        <button class="btn btn-sm btn-secondary" @click="clearChat">
          🗑️ 清空对话
        </button>
      </div>
    </div>

    <!-- 对话区域 -->
    <div class="chat-container" ref="chatContainer">
      <!-- 欢迎消息 -->
      <div v-if="messages.length === 0" class="welcome-state">
        <div class="welcome-animation">
          <div class="bubble-group">
            <div class="bubble b1">👋</div>
            <div class="bubble b2">🧪</div>
            <div class="bubble b3">✨</div>
          </div>
        </div>
        <h3>开始你的提示词实验</h3>
        <p>用自然语言告诉我你想要什么，比如：</p>
        <div class="suggestion-chips">
          <button
            v-for="suggestion in suggestions"
            :key="suggestion"
            class="chip"
            @click="sendMessage(suggestion)"
          >
            {{ suggestion }}
          </button>
        </div>
      </div>

      <!-- 消息列表 -->
      <div v-else class="message-list">
        <div
          v-for="(msg, index) in messages"
          :key="index"
          :class="['message', msg.role]"
        >
          <div class="message-avatar">
            {{ msg.role === 'user' ? '👤' : '🤖' }}
          </div>
          <div class="message-content">
            <div class="message-bubble">
              <div v-if="msg.role === 'assistant' && msg.isTyping" class="typing-indicator">
                <span></span><span></span><span></span>
              </div>
              <pre v-else>{{ msg.content }}</pre>
            </div>
            <div v-if="msg.role === 'assistant' && !msg.isTyping && msg.content" class="message-actions">
              <button class="action-btn" @click="copyMessage(msg.content)" title="复制">
                📋
              </button>
              <button class="action-btn" @click="useMessage(msg.content)" title="使用">
                ✨
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 输入区域 -->
    <div class="chat-input-area">
      <div class="input-container">
        <textarea
          v-model="inputText"
          class="chat-input"
          placeholder="描述你的提示词需求..."
          rows="1"
          @keydown.enter.exact.prevent="handleSend"
          @input="autoResize"
        ></textarea>
        <button
          class="send-btn"
          :disabled="!inputText.trim() || loading"
          @click="handleSend"
        >
          <span v-if="loading" class="loading-spinner"></span>
          <span v-else>➤</span>
        </button>
      </div>
      <p class="input-hint">按 Enter 发送，Shift + Enter 换行</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, nextTick } from 'vue';

interface Message {
  role: 'user' | 'assistant';
  content: string;
  isTyping?: boolean;
}

const inputText = ref('');
const loading = ref(false);
const messages = ref<Message[]>([]);
const chatContainer = ref<HTMLElement>();

const suggestions = [
  '帮我写一个写小红书文案的提示词',
  '创建一个代码review的提示词',
  '写一个AI新闻写作的提示词',
  '设计一个翻译提示词',
];

async function handleSend() {
  const text = inputText.value.trim();
  if (!text || loading.value) return;

  messages.value.push({ role: 'user', content: text });
  inputText.value = '';
  scrollToBottom();

  const aiMsg: Message = { role: 'assistant', content: '', isTyping: true };
  messages.value.push(aiMsg);
  loading.value = true;

  await new Promise(resolve => setTimeout(resolve, 1500));

  const prompt = generatePrompt(text);
  aiMsg.isTyping = false;
  aiMsg.content = prompt;

  loading.value = false;
  scrollToBottom();
}

function generatePrompt(userInput: string): string {
  return `【提示词生成结果】

# 角色定义
你是一个专业的AI助手，擅长${getTopic(userInput)}。

# 任务要求
${userInput}

# 输出格式
- 结构清晰
- 语言精炼
- 便于直接使用

# 约束条件
1. 内容准确可靠
2. 语言表达地道
3. 符合目标受众习惯

# 使用说明
请直接输出符合上述要求的提示词内容。`;
}

function getTopic(input: string): string {
  if (input.includes('小红书') || input.includes('文案')) return '内容创作与文案写作';
  if (input.includes('代码') || input.includes('review') || input.includes('代码审查')) return '编程与代码审查';
  if (input.includes('翻译')) return '多语言翻译';
  if (input.includes('新闻')) return '新闻写作';
  if (input.includes('写作') || input.includes('文章')) return '文章写作';
  return '各类专业任务';
}

function sendMessage(text: string) {
  inputText.value = text;
  handleSend();
}

function scrollToBottom() {
  nextTick(() => {
    if (chatContainer.value) {
      chatContainer.value.scrollTop = chatContainer.value.scrollHeight;
    }
  });
}

function autoResize(e: Event) {
  const textarea = e.target as HTMLTextAreaElement;
  textarea.style.height = 'auto';
  textarea.style.height = Math.min(textarea.scrollHeight, 150) + 'px';
}

function clearChat() {
  messages.value = [];
}

function copyMessage(content: string) {
  navigator.clipboard.writeText(content);
}

function useMessage(content: string) {
  console.log('Using prompt:', content);
}
</script>

<style scoped>
.chat-generator {
  display: flex;
  flex-direction: column;
  height: 600px;
  max-width: 900px;
  margin: 0 auto;
  background: white;
  border-radius: 20px;
  box-shadow: 0 0 0 1px rgba(226, 232, 240, 0.6);
  overflow: hidden;
}

/* 头部 */
.chat-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  background: linear-gradient(180deg, rgba(248, 250, 252, 0.9) 0%, rgba(255, 255, 255, 0.8) 100%);
  border-bottom: 1px solid rgba(226, 232, 240, 0.6);
}

.chat-title {
  display: flex;
  align-items: center;
  gap: 12px;
}

.chat-icon {
  font-size: 1.8rem;
}

.title-text h2 {
  font-size: 1.1rem;
  font-weight: 700;
  color: #1e293b;
  margin: 0 0 2px 0;
}

.title-text p {
  font-size: 0.8rem;
  color: #64748b;
  margin: 0;
}

/* 对话容器 */
.chat-container {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
}

/* 欢迎状态 */
.welcome-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  text-align: center;
}

.welcome-animation {
  margin-bottom: 20px;
}

.bubble-group {
  position: relative;
  width: 100px;
  height: 80px;
}

.bubble {
  position: absolute;
  font-size: 2rem;
  animation: float 3s ease-in-out infinite;
}

.b1 { left: 0; top: 20px; animation-delay: 0s; }
.b2 { left: 35px; top: 0; animation-delay: 0.5s; }
.b3 { right: 0; top: 20px; animation-delay: 1s; }

@keyframes float {
  0%, 100% { transform: translateY(0) scale(1); }
  50% { transform: translateY(-10px) scale(1.1); }
}

.welcome-state h3 {
  font-size: 1.2rem;
  font-weight: 700;
  color: #334155;
  margin: 0 0 6px 0;
}

.welcome-state p {
  color: #64748b;
  margin: 0 0 16px 0;
  font-size: 0.85rem;
}

.suggestion-chips {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  justify-content: center;
  max-width: 550px;
}

.chip {
  padding: 8px 16px;
  background: #f8fafc;
  border: 1px solid #e2e8f0;
  border-radius: 18px;
  font-size: 0.85rem;
  color: #475569;
  cursor: pointer;
  transition: all 0.25s ease;
}

.chip:hover {
  background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
  color: white;
  border-color: transparent;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.3);
}

/* 消息列表 */
.message-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.message {
  display: flex;
  gap: 10px;
  animation: messageIn 0.3s ease;
}

@keyframes messageIn {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}

.message.user {
  flex-direction: row-reverse;
}

.message-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.1rem;
  flex-shrink: 0;
}

.message.user .message-avatar {
  background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
}

.message.assistant .message-avatar {
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
}

.message-content {
  max-width: 75%;
}

.message.user .message-content {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
}

.message-bubble {
  padding: 12px 16px;
  border-radius: 14px;
  font-size: 0.9rem;
  line-height: 1.5;
}

.message.user .message-bubble {
  background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
  color: white;
  border-bottom-right-radius: 4px;
}

.message.assistant .message-bubble {
  background: #f8fafc;
  color: #334155;
  border-bottom-left-radius: 4px;
  border: 1px solid #e2e8f0;
}

.message-bubble pre {
  margin: 0;
  white-space: pre-wrap;
  font-family: inherit;
}

.typing-indicator {
  display: flex;
  gap: 4px;
  padding: 4px 0;
}

.typing-indicator span {
  width: 8px;
  height: 8px;
  background: #94a3b8;
  border-radius: 50%;
  animation: typing 1.4s ease-in-out infinite;
}

.typing-indicator span:nth-child(2) { animation-delay: 0.2s; }
.typing-indicator span:nth-child(3) { animation-delay: 0.4s; }

@keyframes typing {
  0%, 60%, 100% { transform: translateY(0); opacity: 0.4; }
  30% { transform: translateY(-6px); opacity: 1; }
}

.message-actions {
  display: flex;
  gap: 6px;
  margin-top: 6px;
  opacity: 0;
  transition: opacity 0.2s;
}

.message:hover .message-actions {
  opacity: 1;
}

.action-btn {
  padding: 5px 8px;
  background: white;
  border: 1px solid #e2e8f0;
  border-radius: 6px;
  font-size: 0.75rem;
  cursor: pointer;
  transition: all 0.2s;
}

.action-btn:hover {
  background: #f1f5f9;
  border-color: #3b82f6;
}

/* 输入区域 */
.chat-input-area {
  padding: 14px 20px 16px;
  background: rgba(248, 250, 252, 0.9);
  border-top: 1px solid rgba(226, 232, 240, 0.6);
}

.input-container {
  display: flex;
  gap: 10px;
  align-items: flex-end;
  background: white;
  border: 1px solid #e2e8f0;
  border-radius: 14px;
  padding: 6px 6px 6px 14px;
  transition: all 0.2s;
}

.input-container:focus-within {
  border-color: #3b82f6;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
}

.chat-input {
  flex: 1;
  border: none;
  outline: none;
  font-size: 0.9rem;
  line-height: 1.5;
  resize: none;
  background: transparent;
  color: #334155;
}

.chat-input::placeholder {
  color: #94a3b8;
}

.send-btn {
  width: 40px;
  height: 40px;
  border: none;
  border-radius: 10px;
  background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
  color: white;
  font-size: 1.1rem;
  cursor: pointer;
  transition: all 0.2s;
  display: flex;
  align-items: center;
  justify-content: center;
}

.send-btn:hover:not(:disabled) {
  transform: scale(1.05);
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.4);
}

.send-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.input-hint {
  margin: 6px 0 0 0;
  font-size: 0.7rem;
  color: #94a3b8;
  text-align: center;
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

/* 按钮 */
.btn {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  border-radius: 8px;
  font-size: 0.85rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
  border: none;
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
  padding: 6px 10px;
  font-size: 0.8rem;
}

@media (max-width: 768px) {
  .chat-generator {
    height: 500px;
    border-radius: 16px;
  }

  .message-content {
    max-width: 85%;
  }
}
</style>
