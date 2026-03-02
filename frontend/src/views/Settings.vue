<template>
  <AppLayout>
    <template #main>
      <div class="page-container">
        <div class="banner settings-banner">
          <h1 class="banner-title">⚙️ 设置</h1>
          <p class="banner-desc">个性化配置你的提示词工具</p>
        </div>

        <div class="settings-content">
          <div class="settings-section">
            <h3 class="section-title">🤖 AI 模型配置</h3>
            <div class="setting-item">
              <label>API Key</label>
              <input v-model="settings.apiKey" type="password" placeholder="输入你的 API Key" class="setting-input" />
              <p class="setting-tip">留空使用系统默认 API Key</p>
            </div>
            <div class="setting-item">
              <label>模型选择</label>
              <select v-model="settings.model" class="setting-select">
                <option value="MiniMax-M2.5">MiniMax M2.5</option>
                <option value="gpt-4">GPT-4</option>
                <option value="gpt-3.5">GPT-3.5</option>
              </select>
            </div>
          </div>

          <div class="settings-section">
            <h3 class="section-title">🎨 界面偏好</h3>
            <div class="setting-item">
              <label>主题模式</label>
              <div class="theme-toggle">
                <button :class="['theme-btn', { active: settings.theme === 'light' }]" @click="settings.theme = 'light'">
                  ☀️ 浅色
                </button>
                <button :class="['theme-btn', { active: settings.theme === 'dark' }]" @click="settings.theme = 'dark'">
                  🌙 深色
                </button>
              </div>
            </div>
            <div class="setting-item">
              <label>默认生成模式</label>
              <select v-model="settings.defaultMode" class="setting-select">
                <option value="simple">简单模式</option>
                <option value="wizard">向导模式</option>
              </select>
            </div>
          </div>

          <div class="settings-section">
            <h3 class="section-title">📝 生成偏好</h3>
            <div class="setting-item">
              <label>默认受众</label>
              <select v-model="settings.defaultAudience" class="setting-select">
                <option value="general">普通用户</option>
                <option value="professional">专业人士</option>
                <option value="student">学生</option>
                <option value="developer">开发者</option>
              </select>
            </div>
            <div class="setting-item">
              <label>默认输出格式</label>
              <select v-model="settings.defaultFormat" class="setting-select">
                <option value="text">纯文本</option>
                <option value="list">列表</option>
                <option value="table">表格</option>
                <option value="code">代码</option>
                <option value="json">JSON</option>
              </select>
            </div>
          </div>

          <div class="settings-actions">
            <button class="btn-save" @click="saveSettings">💾 保存设置</button>
            <button class="btn-reset" @click="resetSettings">🔄 重置</button>
          </div>
        </div>
      </div>
    </template>
  </AppLayout>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import AppLayout from '../components/layout/AppLayout.vue'

const settings = ref({
  apiKey: '',
  model: 'MiniMax-M2.5',
  theme: 'light',
  defaultMode: 'simple',
  defaultAudience: 'general',
  defaultFormat: 'text'
})

const loadSettings = () => {
  const saved = localStorage.getItem('promptflow-settings')
  if (saved) {
    try {
      settings.value = { ...settings.value, ...JSON.parse(saved) }
    } catch (e) { console.error(e) }
  }
}

const saveSettings = () => {
  localStorage.setItem('promptflow-settings', JSON.stringify(settings.value))
  alert('设置已保存！')
}

const resetSettings = () => {
  if (confirm('确定要重置所有设置吗？')) {
    settings.value = {
      apiKey: '',
      model: 'MiniMax-M2.5',
      theme: 'light',
      defaultMode: 'simple',
      defaultAudience: 'general',
      defaultFormat: 'text'
    }
    localStorage.removeItem('promptflow-settings')
  }
}

onMounted(() => loadSettings())
</script>

<style scoped>
.page-container { min-height: 100%; background: #f8fafc; margin: -24px; }
.settings-banner { background: linear-gradient(135deg, #6366f1 0%, #4f46e5 100%); }
.banner { padding: 36px 40px; text-align: center; }
.banner-title { font-size: 1.6rem; font-weight: 700; color: white; margin: 0 0 6px; }
.banner-desc { color: rgba(255,255,255,0.85); font-size: 0.95rem; margin: 0; }

.settings-content { max-width: 700px; margin: 0 auto; padding: 30px 40px; }
.settings-section { background: white; border-radius: 14px; padding: 24px; margin-bottom: 20px; border: 1px solid #e2e8f0; }
.section-title { font-size: 1.1rem; font-weight: 600; color: #1e293b; margin: 0 0 20px; padding-bottom: 12px; border-bottom: 1px solid #e2e8f0; }
.setting-item { margin-bottom: 18px; }
.setting-item:last-child { margin-bottom: 0; }
.setting-item label { display: block; font-size: 0.9rem; font-weight: 500; color: #374151; margin-bottom: 8px; }
.setting-input, .setting-select { width: 100%; padding: 10px 14px; border: 1px solid #e2e8f0; border-radius: 8px; font-size: 0.95rem; outline: none; transition: border-color 0.2s; }
.setting-input:focus, .setting-select:focus { border-color: #6366f1; }
.setting-tip { font-size: 0.8rem; color: #94a3b8; margin-top: 6px; }
.theme-toggle { display: flex; gap: 10px; }
.theme-btn { flex: 1; padding: 10px; border: 2px solid #e2e8f0; border-radius: 8px; background: white; cursor: pointer; font-size: 0.9rem; transition: all 0.2s; }
.theme-btn:hover { border-color: #6366f1; }
.theme-btn.active { background: #6366f1; color: white; border-color: #6366f1; }
.settings-actions { display: flex; gap: 12px; }
.btn-save { flex: 1; padding: 14px 24px; background: #6366f1; color: white; border: none; border-radius: 10px; font-size: 1rem; font-weight: 500; cursor: pointer; }
.btn-reset { padding: 14px 24px; background: #f1f5f9; color: #64748b; border: none; border-radius: 10px; font-size: 1rem; cursor: pointer; }
.btn-save:hover { background: #4f46e5; }
</style>
