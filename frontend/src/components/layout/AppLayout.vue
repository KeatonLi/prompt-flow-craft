<template>
  <div class="app-layout">
    <!-- 顶部导航条 -->
    <header class="top-navbar">
      <div class="navbar-brand">
        <span class="brand-icon">🎨</span>
        <span class="brand-text">PromptFlow</span>
      </div>
      <nav class="navbar-nav">
        <router-link to="/" class="nav-link" :class="{ active: $route.path === '/' }">
          <span class="nav-icon">🏠</span>
          <span>首页</span>
        </router-link>
        <router-link to="/templates" class="nav-link" :class="{ active: $route.path === '/templates' }">
          <span class="nav-icon">💡</span>
          <span>提示词大全</span>
        </router-link>
        <router-link to="/template-market" class="nav-link" :class="{ active: $route.path === '/template-market' }">
          <span class="nav-icon">📦</span>
          <span>模板市场</span>
        </router-link>
        <router-link to="/optimize" class="nav-link" :class="{ active: $route.path === '/optimize' }">
          <span class="nav-icon">✨</span>
          <span>优化器</span>
        </router-link>
        <router-link to="/compare" class="nav-link" :class="{ active: $route.path === '/compare' }">
          <span class="nav-icon">⚖️</span>
          <span>对比</span>
        </router-link>
        <router-link to="/variable-extractor" class="nav-link" :class="{ active: $route.path === '/variable-extractor' }">
          <span class="nav-icon">🔧</span>
          <span>变量提取</span>
        </router-link>
        <router-link to="/prompt-score" class="nav-link" :class="{ active: $route.path === '/prompt-score' }">
          <span class="nav-icon">📊</span>
          <span>评分</span>
        </router-link>
        <router-link to="/prompt-convert" class="nav-link" :class="{ active: $route.path === '/prompt-convert' }">
          <span class="nav-icon">🔄</span>
          <span>转换</span>
        </router-link>
        <router-link to="/popular" class="nav-link" :class="{ active: $route.path === '/popular' }">
          <span class="nav-icon">🏆</span>
          <span>热门</span>
        </router-link>
        <router-link to="/community" class="nav-link" :class="{ active: $route.path === '/community' }">
          <span class="nav-icon">🌟</span>
          <span>社区</span>
        </router-link>
        <router-link to="/favorites" class="nav-link" :class="{ active: $route.path === '/favorites' }">
          <span class="nav-icon">⭐</span>
          <span>收藏</span>
        </router-link>
        <router-link to="/about" class="nav-link" :class="{ active: $route.path === '/about' }">
          <span class="nav-icon">ℹ️</span>
          <span>关于</span>
        </router-link>
        <router-link to="/settings" class="nav-link" :class="{ active: $route.path === '/settings' }">
          <span class="nav-icon">⚙️</span>
          <span>设置</span>
        </router-link>
      </nav>
      <div class="navbar-actions">
        <!-- 主题切换 -->
        <button class="theme-btn" @click="toggleTheme" :title="isDark ? '切换亮色模式' : '切换暗黑模式'">
          <!-- 太阳图标 -->
          <svg v-if="!isDark" xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <circle cx="12" cy="12" r="5"></circle>
            <line x1="12" y1="1" x2="12" y2="3"></line>
            <line x1="12" y1="21" x2="12" y2="23"></line>
            <line x1="4.22" y1="4.22" x2="5.64" y2="5.64"></line>
            <line x1="18.36" y1="18.36" x2="19.78" y2="19.78"></line>
            <line x1="1" y1="12" x2="3" y2="12"></line>
            <line x1="21" y1="12" x2="23" y2="12"></line>
            <line x1="4.22" y1="19.78" x2="5.64" y2="18.36"></line>
            <line x1="18.36" y1="5.64" x2="19.78" y2="4.22"></line>
          </svg>
          <!-- 月亮图标 -->
          <svg v-else xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <path d="M21 12.79A9 9 0 1 1 11.21 3 7 7 0 0 0 21 12.79z"></path>
          </svg>
        </button>
        <a href="https://github.com/KeatonLi/prompt-flow-craft" target="_blank" class="action-link" title="GitHub">
          <svg class="github-icon" viewBox="0 0 24 24" fill="currentColor">
            <path d="M12 0C5.37 0 0 5.37 0 12c0 5.31 3.435 9.795 8.205 11.385.6.105.825-.255.825-.57 0-.285-.015-1.23-.015-2.235-3.015.555-3.795-.735-4.035-1.41-.135-.345-.72-1.41-1.23-1.695-.42-.225-1.02-.78-.015-.795.945-.015 1.62.87 1.845 1.23 1.08 1.815 2.805 1.305 3.495.99.105-.78.42-1.305.765-1.605-2.67-.3-5.46-1.335-5.46-5.925 0-1.305.465-2.385 1.23-3.225-.12-.3-.54-1.53.12-3.18 0 0 1.005-.315 3.3 1.23.96-.27 1.98-.405 3-.405s2.04.135 3 .405c2.295-1.56 3.3-1.23 3.3-1.23.66 1.65.24 2.88.12 3.18.765.84 1.23 1.905 1.23 3.225 0 4.605-2.805 5.625-5.475 5.925.435.375.81 1.095.81 2.22 0 1.605-.015 2.895-.015 3.3 0 .315.225.69.825.57A12.02 12.02 0 0024 12c0-6.63-5.37-12-12-12z"/>
          </svg>
        </a>
      </div>
    </header>

    <div class="layout-body">
      <!-- 左侧分类导航 -->
      <aside v-if="showSidebars" class="sidebar-left" :class="{ collapsed: isLeftCollapsed }">
        <div class="sidebar-header">
          <h1 v-if="!isLeftCollapsed" class="sidebar-title">
            <span>📁</span>
            分类导航
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
      <aside v-if="showSidebars" class="sidebar-right" :class="{ collapsed: isRightCollapsed }">
        <div class="sidebar-header">
          <button class="toggle-btn" @click="toggleRight">
            <span v-if="isRightCollapsed">←</span>
            <span v-else>→</span>
          </button>
          <h2 v-if="!isRightCollapsed" class="sidebar-title">📚 历史记录</h2>
        </div>

        <div v-if="!isRightCollapsed" class="sidebar-content">
          <slot name="sidebar-right" />
        </div>

        <div v-else class="sidebar-collapsed-indicator">
          <span>📚</span>
        </div>
      </aside>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import { useRoute } from 'vue-router';

const route = useRoute()
const isLeftCollapsed = ref(false);
const isRightCollapsed = ref(false);
const isDark = ref(false);

// 根据路由控制侧边栏显示
const showSidebars = computed(() => {
  return route.path === '/' || route.path === '/home'
});

const toggleLeft = () => {
  isLeftCollapsed.value = !isLeftCollapsed.value;
};

const toggleRight = () => {
  isRightCollapsed.value = !isRightCollapsed.value;
};

const toggleTheme = () => {
  isDark.value = !isDark.value;
  document.documentElement.classList.toggle('dark', isDark.value);
  localStorage.setItem('theme', isDark.value ? 'dark' : 'light');
};

onMounted(() => {
  const savedTheme = localStorage.getItem('theme');
  if (savedTheme === 'dark' || (!savedTheme && window.matchMedia('(prefers-color-scheme: dark)').matches)) {
    isDark.value = true;
    document.documentElement.classList.add('dark');
  }
});
</script>

<style scoped>
.app-layout {
  display: flex;
  flex-direction: column;
  height: 100vh;
  overflow: hidden;
  background: #f1f5f9;
}

/* 顶部导航条 */
.top-navbar {
  height: 56px;
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  border-bottom: 1px solid rgba(226, 232, 240, 0.6);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  flex-shrink: 0;
  z-index: 100;
  position: relative;
}

.top-navbar::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 1px;
  background: linear-gradient(90deg, transparent, rgba(59, 130, 246, 0.3), transparent);
}

.navbar-brand {
  display: flex;
  align-items: center;
  gap: 10px;
}

.brand-icon {
  font-size: 1.5rem;
}

.brand-text {
  font-size: 1.25rem;
  font-weight: 700;
  color: #1e293b;
  background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.navbar-nav {
  display: flex;
  gap: 8px;
}

.nav-link {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 16px;
  border-radius: 8px;
  color: #64748b;
  text-decoration: none;
  font-size: 0.875rem;
  font-weight: 500;
  transition: all 0.2s;
}

.nav-link:hover {
  background: #f1f5f9;
  color: #334155;
}

.nav-link.active {
  background: #eff6ff;
  color: #2563eb;
}

.nav-icon {
  font-size: 1rem;
}

.navbar-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.action-link {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 36px;
  height: 36px;
  border-radius: 8px;
  color: #64748b;
  transition: all 0.2s;
}

.action-link:hover {
  background: #f1f5f9;
  color: #334155;
}

.github-icon {
  width: 20px;
  height: 20px;
}

/* 布局主体 */
.layout-body {
  display: flex;
  flex: 1;
  overflow: hidden;
}

/* 左侧边栏 */
.sidebar-left {
  width: 260px;
  background: white;
  border-right: 1px solid #e2e8f0;
  display: flex;
  flex-direction: column;
  transition: width 0.3s ease;
  flex-shrink: 0;
}

.sidebar-left.collapsed {
  width: 60px;
}

/* 右侧边栏 */
.sidebar-right {
  width: 380px;
  background: white;
  border-left: 1px solid #e2e8f0;
  display: flex;
  flex-direction: column;
  transition: width 0.3s ease;
  flex-shrink: 0;
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
  height: 56px;
}

.sidebar-title {
  font-size: 1rem;
  font-weight: 600;
  color: #475569;
  margin: 0;
  display: flex;
  align-items: center;
  gap: 8px;
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
  background: 
    radial-gradient(ellipse at 20% 0%, rgba(59, 130, 246, 0.08) 0%, transparent 50%),
    radial-gradient(ellipse at 80% 100%, rgba(59, 130, 246, 0.08) 0%, transparent 50%),
    linear-gradient(180deg, #f8fafc 0%, #f1f5f9 100%);
  position: relative;
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

/* 响应式 */
@media (max-width: 1200px) {
  .sidebar-left {
    width: 220px;
  }
  .sidebar-right {
    width: 320px;
  }
}

@media (max-width: 992px) {
  .sidebar-left {
    position: absolute;
    left: 0;
    top: 56px;
    bottom: 0;
    z-index: 50;
    box-shadow: 4px 0 12px rgba(0, 0, 0, 0.1);
  }
  
  .sidebar-left.collapsed {
    transform: translateX(-100%);
  }
  
  .sidebar-right {
    position: absolute;
    right: 0;
    top: 56px;
    bottom: 0;
    z-index: 50;
    box-shadow: -4px 0 12px rgba(0, 0, 0, 0.1);
  }
  
  .sidebar-right.collapsed {
    transform: translateX(100%);
  }
}

/* 主题切换按钮 */
.theme-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 36px;
  height: 36px;
  border: none;
  background: transparent;
  border-radius: 8px;
  cursor: pointer;
  color: #64748b;
  transition: all 0.2s;
}

.theme-btn:hover {
  background: #f1f5f9;
  color: #334155;
}

/* 暗黑模式 */
:root {
  --bg-primary: #f1f5f9;
  --bg-secondary: #ffffff;
  --text-primary: #1e293b;
  --text-secondary: #64748b;
  --border-color: #e2e8f0;
  --hover-bg: #f1f5f9;
}

:root.dark {
  --bg-primary: #0f172a;
  --bg-secondary: #1e293b;
  --text-primary: #f1f5f9;
  --text-secondary: #94a3b8;
  --border-color: #334155;
  --hover-bg: #334155;
}

:root.dark .app-layout {
  background: var(--bg-primary);
}

:root.dark .top-navbar {
  background: rgba(30, 41, 59, 0.8);
  border-bottom-color: var(--border-color);
}

:root.dark .brand-text {
  color: #60a5fa;
}

:root.dark .nav-link {
  color: var(--text-secondary);
}

:root.dark .nav-link:hover {
  background: var(--hover-bg);
  color: var(--text-primary);
}

:root.dark .nav-link.active {
  background: rgba(59, 130, 246, 0.2);
  color: #60a5fa;
}

:root.dark .sidebar-left,
:root.dark .sidebar-right {
  background: var(--bg-secondary);
  border-color: var(--border-color);
}

:root.dark .sidebar-title {
  color: var(--text-primary);
}

:root.dark .toggle-btn {
  background: var(--hover-bg);
  color: var(--text-secondary);
}

:root.dark .toggle-btn:hover {
  background: #475569;
}

:root.dark .main-content {
  background: 
    radial-gradient(ellipse at 20% 0%, rgba(59, 130, 246, 0.15) 0%, transparent 50%),
    radial-gradient(ellipse at 80% 100%, rgba(59, 130, 246, 0.15) 0%, transparent 50%),
    linear-gradient(180deg, #0f172a 0%, #1e293b 100%);
}

:root.dark .theme-btn {
  color: var(--text-secondary);
}

:root.dark .theme-btn:hover {
  background: var(--hover-bg);
  color: #fbbf24;
}

:root.dark .action-link:hover {
  background: var(--hover-bg);
  color: var(--text-primary);
}
</style>
