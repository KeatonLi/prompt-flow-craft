<template>
  <div class="app-layout">
    <!-- 左侧分类导航 -->
    <aside class="sidebar-left" :class="{ collapsed: isLeftCollapsed }">
      <div class="sidebar-header">
        <h1 v-if="!isLeftCollapsed" class="app-title">
          <span class="app-icon">🎨</span>
          PromptFlow
        </h1>
        <button class="toggle-btn" @click="toggleLeft">
          <span v-if="isLeftCollapsed">→</span>
          <span v-else>←</span>
        </button>
      </div>

      <div v-if="!isLeftCollapsed" class="sidebar-content">
        <slot name="sidebar-left" />
      </div>

      <div v-else class="sidebar-collapsed-indicator">
        <span>📁</span>
      </div>
    </aside>

    <!-- 中间主内容区 -->
    <main class="main-content">
      <slot name="main" />
    </main>

    <!-- 右侧历史记录 -->
    <aside class="sidebar-right" :class="{ collapsed: isRightCollapsed }">
      <div class="sidebar-header">
        <button class="toggle-btn" @click="toggleRight">
          <span v-if="isRightCollapsed">←</span>
          <span v-else>→</span>
        </button>
        <h2 v-if="!isRightCollapsed" class="sidebar-title">历史记录</h2>
      </div>

      <div v-if="!isRightCollapsed" class="sidebar-content">
        <slot name="sidebar-right" />
      </div>

      <div v-else class="sidebar-collapsed-indicator">
        <span>📚</span>
      </div>
    </aside>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';

const isLeftCollapsed = ref(false);
const isRightCollapsed = ref(false);

const toggleLeft = () => {
  isLeftCollapsed.value = !isLeftCollapsed.value;
};

const toggleRight = () => {
  isRightCollapsed.value = !isRightCollapsed.value;
};
</script>

<style scoped>
.app-layout {
  display: flex;
  height: 100vh;
  overflow: hidden;
  background: #f1f5f9;
}

/* 左侧边栏 */
.sidebar-left {
  width: 240px;
  background: white;
  border-right: 1px solid #e2e8f0;
  display: flex;
  flex-direction: column;
  transition: width 0.3s ease;
}

.sidebar-left.collapsed {
  width: 60px;
}

/* 右侧边栏 */
.sidebar-right {
  width: 360px;
  background: white;
  border-left: 1px solid #e2e8f0;
  display: flex;
  flex-direction: column;
  transition: width 0.3s ease;
}

.sidebar-right.collapsed {
  width: 60px;
}

/* 头部 */
.sidebar-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px;
  border-bottom: 1px solid #e2e8f0;
}

.app-title {
  font-size: 1.25rem;
  font-weight: 700;
  color: #1e293b;
  display: flex;
  align-items: center;
  gap: 8px;
  margin: 0;
}

.app-icon {
  font-size: 1.5rem;
}

.sidebar-title {
  font-size: 1rem;
  font-weight: 600;
  color: #475569;
  margin: 0;
}

/* 切换按钮 */
.toggle-btn {
  width: 28px;
  height: 28px;
  border: none;
  background: #f1f5f9;
  border-radius: 6px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  color: #64748b;
  transition: all 0.2s;
}

.toggle-btn:hover {
  background: #e2e8f0;
  color: #334155;
}

/* 内容区 */
.sidebar-content {
  flex: 1;
  overflow-y: auto;
  padding: 16px;
}

.sidebar-collapsed-indicator {
  flex: 1;
  display: flex;
  align-items: flex-start;
  justify-content: center;
  padding-top: 20px;
  font-size: 1.5rem;
  cursor: pointer;
}

/* 主内容区 */
.main-content {
  flex: 1;
  overflow-y: auto;
  padding: 24px;
}

/* 滚动条样式 */
.sidebar-content::-webkit-scrollbar,
.main-content::-webkit-scrollbar {
  width: 6px;
}

.sidebar-content::-webkit-scrollbar-track,
.main-content::-webkit-scrollbar-track {
  background: transparent;
}

.sidebar-content::-webkit-scrollbar-thumb,
.main-content::-webkit-scrollbar-thumb {
  background: #cbd5e1;
  border-radius: 3px;
}

.sidebar-content::-webkit-scrollbar-thumb:hover,
.main-content::-webkit-scrollbar-thumb:hover {
  background: #94a3b8;
}
</style>
