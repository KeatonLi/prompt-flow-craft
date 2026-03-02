<template>
  <AppLayout>
    <template #sidebar-left>
      <CategoryNav />
    </template>

    <template #main>
      <div class="home-container">
        <div class="mode-toggle">
          <button 
            :class="['mode-btn', { active: mode === 'simple' }]" 
            @click="mode = 'simple'"
          >
            💬 简单模式
          </button>
          <button 
            :class="['mode-btn', { active: mode === 'wizard' }]" 
            @click="mode = 'wizard'"
          >
            🧙 向导模式
          </button>
        </div>
        
        <PromptEditor v-if="mode === 'simple'" />
        <PromptWizard v-else :visible="mode === 'wizard'" @close="mode = 'simple'" @use="handleWizardUse" />
      </div>
    </template>

    <template #sidebar-right>
      <HistoryPanel />
    </template>
  </AppLayout>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import AppLayout from '@/components/layout/AppLayout.vue';
import CategoryNav from '@/components/layout/CategoryNav.vue';
import PromptEditor from '@/components/editor/PromptEditor.vue';
import HistoryPanel from '@/components/history/HistoryPanel.vue';
import PromptWizard from '@/components/PromptWizard.vue';

const router = useRouter()
const mode = ref('simple')

const handleWizardUse = (data) => {
  if (data.mode === 'wizard') {
    // 跳转到首页并填入生成的提示词
    router.push({ 
      path: '/', 
      query: { 
        task: data.taskDescription, 
        mode: 'wizard'
      } 
    })
    // 切换回简单模式
    mode.value = 'simple'
  }
}
</script>

<style scoped>
.home-container {
  min-height: 100%;
}

.mode-toggle {
  display: flex;
  justify-content: center;
  gap: 8px;
  padding: 16px;
  background: white;
  border-bottom: 1px solid #e2e8f0;
  margin: -24px -24px 24px -24px;
}

.mode-btn {
  padding: 10px 20px;
  border: 2px solid #e2e8f0;
  border-radius: 10px;
  background: white;
  color: #64748b;
  font-size: 0.95rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
}

.mode-btn:hover {
  border-color: #3b82f6;
  color: #3b82f6;
}

.mode-btn.active {
  background: #3b82f6;
  border-color: #3b82f6;
  color: white;
}
</style>
