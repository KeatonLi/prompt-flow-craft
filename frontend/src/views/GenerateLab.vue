<template>
  <AppLayout>
    <template #main>
      <div class="lab-container">
        <!-- 实验室视图切换 -->
        <div class="lab-view-switcher">
          <div class="view-tabs">
            <button
              v-for="view in views"
              :key="view.id"
              :class="['view-tab', { active: currentView === view.id }]"
              @click="currentView = view.id"
            >
              <span class="tab-icon">{{ view.icon }}</span>
              <span class="tab-label">{{ view.label }}</span>
            </button>
          </div>
        </div>

        <!-- 实验室工作台 -->
        <div class="lab-workbench">
          <!-- 表单模式 -->
          <div v-show="currentView === 'form'" class="view-panel">
            <FormGenerator />
          </div>

          <!-- 对话模式 -->
          <div v-show="currentView === 'chat'" class="view-panel">
            <ChatGenerator />
          </div>

          <!-- 研究流模式 -->
          <div v-show="currentView === 'research'" class="view-panel">
            <ResearchGenerator />
          </div>
        </div>
      </div>
    </template>
  </AppLayout>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import AppLayout from '@/components/layout/AppLayout.vue';
import FormGenerator from '@/components/lab/FormGenerator.vue';
import ChatGenerator from '@/components/lab/ChatGenerator.vue';
import ResearchGenerator from '@/components/lab/ResearchGenerator.vue';

const currentView = ref('form');

const views = [
  { id: 'form', label: '结构化生成', icon: '📝' },
  { id: 'chat', label: '对话生成', icon: '💬' },
  { id: 'research', label: '研究流', icon: '🔬' },
];
</script>

<style scoped>
.lab-container {
  max-width: 1000px;
  margin: 0 auto;
}

/* 视图切换器 */
.lab-view-switcher {
  display: flex;
  justify-content: center;
  margin-bottom: 24px;
}

.view-tabs {
  display: inline-flex;
  gap: 4px;
  padding: 4px;
  background: rgba(255, 255, 255, 0.9);
  border-radius: 16px;
  border: 1px solid rgba(226, 232, 240, 0.6);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.view-tab {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 24px;
  border: none;
  background: transparent;
  border-radius: 12px;
  font-size: 0.9rem;
  font-weight: 600;
  color: #64748b;
  cursor: pointer;
  transition: all 0.25s cubic-bezier(0.4, 0, 0.2, 1);
}

.view-tab:hover {
  color: #3b82f6;
  background: rgba(59, 130, 246, 0.06);
}

.view-tab.active {
  background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
  color: white;
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.35);
}

.tab-icon {
  font-size: 1.1rem;
}

.tab-label {
  font-weight: 600;
}

/* 实验室工作台 */
.lab-workbench {
  animation: fadeIn 0.3s ease;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.view-panel {
  animation: slideIn 0.3s ease;
}

@keyframes slideIn {
  from {
    opacity: 0;
    transform: translateX(10px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

/* 响应式 */
@media (max-width: 768px) {
  .view-tabs {
    width: 100%;
    justify-content: center;
  }

  .tab-label {
    display: none;
  }

  .view-tab {
    padding: 12px 16px;
  }
}
</style>
