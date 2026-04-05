<template>
  <div class="app-layout" :class="{ 'dark': isDark }">
    <!-- Ambient Background Effects -->
    <div class="ambient-bg">
      <div class="ambient-orb ambient-orb-1"></div>
      <div class="ambient-orb ambient-orb-2"></div>
      <div class="ambient-orb ambient-orb-3"></div>
    </div>

    <!-- Premium Floating Navigation -->
    <header class="top-navbar">
      <div class="navbar-container">
        <!-- Brand -->
        <router-link to="/" class="navbar-brand">
          <div class="brand-icon">
            <svg width="28" height="28" viewBox="0 0 28 28" fill="none">
              <defs>
                <linearGradient id="brandGrad" x1="0%" y1="0%" x2="100%" y2="100%">
                  <stop offset="0%" stop-color="#3b82f6"/>
                  <stop offset="100%" stop-color="#1d4ed8"/>
                </linearGradient>
              </defs>
              <path d="M14 2L4 7v14l10 5 10-5V7L14 2z" stroke="url(#brandGrad)" stroke-width="2" fill="none"/>
              <path d="M14 8l-6 3v6l6 3 6-3v-6l-6-3z" fill="url(#brandGrad)" opacity="0.3"/>
              <circle cx="14" cy="14" r="3" fill="url(#brandGrad)"/>
            </svg>
          </div>
          <span class="brand-text">PromptFlow</span>
        </router-link>

        <!-- Desktop Navigation - Centered -->
        <nav class="navbar-nav desktop-nav">
          <router-link
            v-for="(item, index) in navItems"
            :key="item.path"
            :to="item.path"
            class="nav-item"
            :class="{ active: $route.path === item.path }"
            :style="{ animationDelay: `${index * 0.05}s` }"
          >
            <span class="nav-icon" v-html="item.icon"></span>
            <span class="nav-text">{{ item.label }}</span>
            <span class="nav-indicator"></span>
          </router-link>
        </nav>

        <!-- Actions -->
        <div class="navbar-actions">
          <!-- Theme Toggle -->
          <button class="action-btn theme-toggle" @click="toggleTheme" :title="isDark ? '切换亮色模式' : '切换暗黑模式'">
            <svg v-if="!isDark" class="icon-sun" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <circle cx="12" cy="12" r="5"/>
              <path d="M12 1v2M12 21v2M4.22 4.22l1.42 1.42M18.36 18.36l1.42 1.42M1 12h2M21 12h2M4.22 19.78l1.42-1.42M18.36 5.64l1.42-1.42"/>
            </svg>
            <svg v-else class="icon-moon" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M21 12.79A9 9 0 1 1 11.21 3 7 7 0 0 0 21 12.79z"/>
            </svg>
          </button>

          <!-- GitHub Link -->
          <a href="https://github.com/KeatonLi/prompt-flow-craft" target="_blank" class="action-btn github-link" title="GitHub">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="currentColor">
              <path d="M12 0C5.37 0 0 5.37 0 12c0 5.31 3.435 9.795 8.205 11.385.6.105.825-.255.825-.57 0-.285-.015-1.23-.015-2.235-3.015.555-3.795-.735-4.035-1.41-.135-.345-.72-1.41-1.23-1.695-.42-.225-1.02-.78-.015-.795.945-.015 1.62.87 1.845 1.23 1.08 1.815 2.805 1.305 3.495.99.105-.78.42-1.305.765-1.605-2.67-.3-5.46-1.335-5.46-5.925 0-1.305.465-2.385 1.23-3.225-.12-.3-.54-1.53.12-3.18 0 0 1.005-.315 3.3 1.23.96-.27 1.98-.405 3-.405s2.04.135 3 .405c2.295-1.56 3.3-1.23 3.3-1.23.66 1.65.24 2.88.12 3.18.765.84 1.23 1.905 1.23 3.225 0 4.605-2.805 5.625-5.475 5.925.435.375.81 1.095.81 2.22 0 1.605-.015 2.895-.015 3.3 0 .315.225.69.825.57A12.02 12.02 0 0024 12c0-6.63-5.37-12-12-12z"/>
            </svg>
          </a>

          <!-- Mobile Menu Toggle -->
          <button class="action-btn mobile-menu-btn" @click="isMobileMenuOpen = !isMobileMenuOpen">
            <span class="menu-icon" :class="{ open: isMobileMenuOpen }">
              <span></span>
              <span></span>
              <span></span>
            </span>
          </button>
        </div>
      </div>
    </header>

    <!-- Mobile Navigation Drawer -->
    <Transition name="drawer">
      <nav v-if="isMobileMenuOpen" class="mobile-nav">
        <div class="mobile-nav-header">
          <span class="mobile-nav-title">导航菜单</span>
          <button class="mobile-nav-close" @click="isMobileMenuOpen = false">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M18 6L6 18M6 6l12 12"/>
            </svg>
          </button>
        </div>
        <div class="mobile-nav-items">
          <router-link
            v-for="(item, index) in navItems"
            :key="item.path"
            :to="item.path"
            class="mobile-nav-item"
            :class="{ active: $route.path === item.path }"
            :style="{ animationDelay: `${index * 0.05}s` }"
            @click="isMobileMenuOpen = false"
          >
            <span class="nav-icon" v-html="item.icon"></span>
            <span class="nav-text">{{ item.label }}</span>
          </router-link>
        </div>
      </nav>
    </Transition>

    <!-- Mobile Overlay -->
    <Transition name="fade">
      <div v-if="isMobileMenuOpen" class="mobile-overlay" @click="isMobileMenuOpen = false"></div>
    </Transition>

    <!-- Main Layout Body -->
    <div class="layout-body">
      <!-- Left Sidebar (Categories) -->
      <aside v-if="showSidebars" class="sidebar sidebar-left" :class="{ collapsed: isLeftCollapsed }">
        <div class="sidebar-header">
          <div class="sidebar-brand" v-if="!isLeftCollapsed">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M3 7h18M3 12h18M3 17h18"/>
            </svg>
            <span>分类导航</span>
          </div>
          <button class="sidebar-toggle" @click="toggleLeft">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path v-if="isLeftCollapsed" d="M9 18l6-6-6-6"/>
              <path v-else d="M15 18l-6-6 6-6"/>
            </svg>
          </button>
        </div>
        <div v-if="!isLeftCollapsed" class="sidebar-content">
          <slot name="sidebar-left" />
        </div>
        <div v-else class="sidebar-collapsed">
          <span class="collapsed-icon">📁</span>
        </div>
      </aside>

      <!-- Main Content -->
      <main class="main-content" :class="{ 'full-width': !showSidebars }">
        <div class="content-wrapper">
          <slot name="main" />
        </div>
      </main>

      <!-- Right Sidebar (History) -->
      <aside v-if="showSidebars" class="sidebar sidebar-right" :class="{ collapsed: isRightCollapsed }">
        <div class="sidebar-header">
          <button class="sidebar-toggle" @click="toggleRight">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path v-if="isRightCollapsed" d="M15 18l-6-6 6-6"/>
              <path v-else d="M9 18l6-6-6-6"/>
            </svg>
          </button>
          <div class="sidebar-brand" v-if="!isRightCollapsed">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <circle cx="12" cy="12" r="10"/>
              <polyline points="12 6 12 12 16 14"/>
            </svg>
            <span>历史记录</span>
          </div>
        </div>
        <div v-if="!isRightCollapsed" class="sidebar-content">
          <slot name="sidebar-right" />
        </div>
        <div v-else class="sidebar-collapsed">
          <span class="collapsed-icon">📚</span>
        </div>
      </aside>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import { useRoute } from 'vue-router';

const route = useRoute();
const isLeftCollapsed = ref(false);
const isRightCollapsed = ref(false);
const isDark = ref(false);
const isMobileMenuOpen = ref(false);

// Navigation items with SVG icons
const navItems = [
  {
    path: '/',
    label: '首页',
    icon: '<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"/><polyline points="9 22 9 12 15 12 15 22"/></svg>'
  },
  {
    path: '/generate',
    label: '创作',
    icon: '<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M12 2L2 7l10 5 10-5-10-5z"/><path d="M2 17l10 5 10-5"/><path d="M2 12l10 5 10-5"/></svg>'
  },
  {
    path: '/templates',
    label: '模板库',
    icon: '<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="3" y="3" width="18" height="18" rx="2"/><path d="M3 9h18"/><path d="M9 21V9"/></svg>'
  },
  {
    path: '/statistics',
    label: '数据',
    icon: '<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M18 20V10"/><path d="M12 20V4"/><path d="M6 20v-6"/></svg>'
  }
];

// Show sidebars only on generate page
const showSidebars = computed(() => {
  return route.path === '/generate';
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
/* ============================================
   App Layout Container
   ============================================ */
.app-layout {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  background: var(--bg-primary);
  position: relative;
  overflow-x: hidden;
}

/* ============================================
   Ambient Background Effects
   ============================================ */
.ambient-bg {
  position: fixed;
  inset: 0;
  pointer-events: none;
  z-index: 0;
  overflow: hidden;
}

.ambient-orb {
  position: absolute;
  border-radius: 50%;
  filter: blur(80px);
  opacity: 0.5;
  animation: floatAmbient 20s ease-in-out infinite;
}

.ambient-orb-1 {
  width: 600px;
  height: 600px;
  background: radial-gradient(circle, rgba(59, 130, 246, 0.15) 0%, transparent 70%);
  top: -200px;
  right: -100px;
  animation-delay: 0s;
}

.ambient-orb-2 {
  width: 500px;
  height: 500px;
  background: radial-gradient(circle, rgba(139, 92, 246, 0.1) 0%, transparent 70%);
  bottom: -150px;
  left: -100px;
  animation-delay: -7s;
}

.ambient-orb-3 {
  width: 400px;
  height: 400px;
  background: radial-gradient(circle, rgba(34, 211, 238, 0.08) 0%, transparent 70%);
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  animation-delay: -14s;
}

@keyframes floatAmbient {
  0%, 100% { transform: translate(0, 0) scale(1); }
  33% { transform: translate(30px, -30px) scale(1.05); }
  66% { transform: translate(-20px, 20px) scale(0.95); }
}

/* ============================================
   Premium Navigation Bar
   ============================================ */
.top-navbar {
  position: sticky;
  top: 0;
  z-index: var(--z-sticky);
  padding: 0 24px;
  height: var(--header-height);
  display: flex;
  align-items: center;
}

.navbar-container {
  width: 100%;
  max-width: 1400px;
  margin: 0 auto;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 24px;
  padding: 12px 20px;
  background: var(--bg-panel);
  backdrop-filter: blur(20px);
  border: 1px solid var(--border-color);
  border-radius: var(--radius-2xl);
  box-shadow: var(--shadow-lg), 0 0 0 1px rgba(255, 255, 255, 0.05) inset;
}

/* Brand */
.navbar-brand {
  display: flex;
  align-items: center;
  gap: 12px;
  text-decoration: none;
  flex-shrink: 0;
}

.brand-icon {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, var(--color-primary-600), var(--color-primary-700));
  border-radius: var(--radius-lg);
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.3);
}

.brand-text {
  font-family: var(--font-display);
  font-size: 1.25rem;
  font-weight: 800;
  background: linear-gradient(135deg, var(--color-primary-600), var(--color-primary-400));
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  letter-spacing: -0.02em;
}

/* Navigation */
.navbar-nav {
  display: flex;
  align-items: center;
  gap: 4px;
  flex: 1;
  justify-content: center;
}

.nav-item {
  position: relative;
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 18px;
  border-radius: var(--radius-lg);
  color: var(--text-secondary);
  text-decoration: none;
  font-family: var(--font-body);
  font-size: var(--text-sm);
  font-weight: 500;
  transition: all var(--transition-base);
  animation: fadeInUp 0.4s ease-out backwards;
}

.nav-item:hover {
  color: var(--text-primary);
  background: var(--bg-hover);
}

.nav-item.active {
  color: var(--color-primary-600);
  background: var(--glow-primary-soft);
}

.nav-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0.8;
  transition: opacity var(--transition-fast);
}

.nav-item:hover .nav-icon,
.nav-item.active .nav-icon {
  opacity: 1;
}

.nav-indicator {
  position: absolute;
  bottom: 6px;
  left: 50%;
  transform: translateX(-50%);
  width: 20px;
  height: 3px;
  background: var(--color-primary-500);
  border-radius: var(--radius-full);
  opacity: 0;
  transition: all var(--transition-base);
}

.nav-item.active .nav-indicator {
  opacity: 1;
}

/* Actions */
.navbar-actions {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-shrink: 0;
}

.action-btn {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: none;
  border-radius: var(--radius-lg);
  background: transparent;
  color: var(--text-secondary);
  cursor: pointer;
  transition: all var(--transition-base);
}

.action-btn:hover {
  background: var(--bg-hover);
  color: var(--text-primary);
}

.theme-toggle:hover {
  color: #f59e0b;
}

.icon-sun {
  transition: transform var(--transition-base);
}

.theme-toggle:hover .icon-sun {
  transform: rotate(45deg);
}

.icon-moon {
  transition: transform var(--transition-base);
}

.theme-toggle:hover .icon-moon {
  transform: rotate(-15deg);
}

/* Mobile Menu Button */
.mobile-menu-btn {
  display: none;
}

.menu-icon {
  display: flex;
  flex-direction: column;
  gap: 5px;
  width: 20px;
}

.menu-icon span {
  display: block;
  width: 100%;
  height: 2px;
  background: currentColor;
  border-radius: 2px;
  transition: all var(--transition-base);
}

.menu-icon.open span:nth-child(1) {
  transform: rotate(45deg) translate(5px, 5px);
}

.menu-icon.open span:nth-child(2) {
  opacity: 0;
}

.menu-icon.open span:nth-child(3) {
  transform: rotate(-45deg) translate(5px, -5px);
}

/* ============================================
   Mobile Navigation
   ============================================ */
.mobile-nav {
  position: fixed;
  top: 0;
  right: 0;
  bottom: 0;
  width: 300px;
  background: var(--bg-panel);
  backdrop-filter: blur(20px);
  border-left: 1px solid var(--border-color);
  z-index: var(--z-modal);
  padding: 24px;
  display: flex;
  flex-direction: column;
}

.mobile-nav-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 32px;
  padding-bottom: 16px;
  border-bottom: 1px solid var(--border-color);
}

.mobile-nav-title {
  font-family: var(--font-display);
  font-size: var(--text-lg);
  font-weight: 700;
  color: var(--text-primary);
}

.mobile-nav-close {
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: none;
  border-radius: var(--radius-md);
  background: var(--bg-hover);
  color: var(--text-secondary);
  cursor: pointer;
  transition: all var(--transition-base);
}

.mobile-nav-close:hover {
  background: var(--bg-tertiary);
  color: var(--text-primary);
}

.mobile-nav-items {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.mobile-nav-item {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 14px 18px;
  border-radius: var(--radius-lg);
  color: var(--text-secondary);
  text-decoration: none;
  font-family: var(--font-body);
  font-size: var(--text-base);
  font-weight: 500;
  transition: all var(--transition-base);
  animation: fadeInRight 0.4s ease-out backwards;
}

.mobile-nav-item:hover {
  background: var(--bg-hover);
  color: var(--text-primary);
}

.mobile-nav-item.active {
  background: var(--glow-primary-soft);
  color: var(--color-primary-600);
}

.mobile-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.5);
  backdrop-filter: blur(4px);
  z-index: var(--z-modal - 1);
}

/* Drawer Animation */
.drawer-enter-active,
.drawer-leave-active {
  transition: transform 0.3s cubic-bezier(0.16, 1, 0.3, 1);
}

.drawer-enter-from,
.drawer-leave-to {
  transform: translateX(100%);
}

/* Fade Animation */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

/* ============================================
   Layout Body
   ============================================ */
.layout-body {
  display: flex;
  flex: 1;
  gap: 20px;
  padding: 20px 24px;
  max-width: 1600px;
  width: 100%;
  margin: 0 auto;
  position: relative;
  z-index: 1;
}

/* ============================================
   Sidebars
   ============================================ */
.sidebar {
  display: flex;
  flex-direction: column;
  background: var(--bg-panel);
  backdrop-filter: blur(16px);
  border: 1px solid var(--border-color);
  border-radius: var(--radius-2xl);
  box-shadow: var(--shadow-lg);
  transition: all var(--transition-base);
  overflow: hidden;
}

.sidebar-left {
  width: var(--sidebar-width);
  flex-shrink: 0;
}

.sidebar-left.collapsed {
  width: var(--sidebar-width-collapsed);
}

.sidebar-right {
  width: 340px;
  flex-shrink: 0;
}

.sidebar-right.collapsed {
  width: var(--sidebar-width-collapsed);
}

.sidebar-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 18px;
  border-bottom: 1px solid var(--border-color);
  min-height: 60px;
}

.sidebar-brand {
  display: flex;
  align-items: center;
  gap: 10px;
  font-family: var(--font-body);
  font-size: var(--text-sm);
  font-weight: 600;
  color: var(--text-primary);
}

.sidebar-brand svg {
  color: var(--color-primary-500);
}

.sidebar-toggle {
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: none;
  border-radius: var(--radius-md);
  background: var(--bg-hover);
  color: var(--text-secondary);
  cursor: pointer;
  transition: all var(--transition-base);
}

.sidebar-toggle:hover {
  background: var(--color-primary-500);
  color: white;
}

.sidebar-content {
  flex: 1;
  overflow-y: auto;
  padding: 16px;
}

.sidebar-collapsed {
  flex: 1;
  display: flex;
  align-items: flex-start;
  justify-content: center;
  padding-top: 20px;
}

.collapsed-icon {
  font-size: 1.5rem;
}

/* ============================================
   Main Content
   ============================================ */
.main-content {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
}

.main-content.full-width {
  width: 100%;
}

.content-wrapper {
  flex: 1;
  animation: fadeInUp 0.5s ease-out;
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(16px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes fadeInRight {
  from {
    opacity: 0;
    transform: translateX(20px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

/* ============================================
   Responsive Design
   ============================================ */
@media (max-width: 1200px) {
  .sidebar-left {
    width: 240px;
  }

  .sidebar-right {
    width: 300px;
  }
}

@media (max-width: 992px) {
  .desktop-nav {
    display: none;
  }

  .mobile-menu-btn {
    display: flex;
  }

  .layout-body {
    padding: 16px;
    gap: 16px;
  }

  .sidebar-left,
  .sidebar-right {
    position: fixed;
    top: calc(var(--header-height) + 16px);
    bottom: 16px;
    z-index: var(--z-dropdown);
    border-radius: var(--radius-2xl);
  }

  .sidebar-left {
    left: 16px;
    width: 280px;
  }

  .sidebar-left.collapsed {
    transform: translateX(-120%);
    width: 280px;
  }

  .sidebar-right {
    right: 16px;
    width: 320px;
  }

  .sidebar-right.collapsed {
    transform: translateX(120%);
    width: 320px;
  }
}

@media (max-width: 640px) {
  .top-navbar {
    padding: 0 12px;
  }

  .navbar-container {
    padding: 10px 14px;
    border-radius: var(--radius-xl);
  }

  .brand-text {
    display: none;
  }

  .layout-body {
    padding: 12px;
  }

  .sidebar-left,
  .sidebar-right {
    width: calc(100vw - 32px);
    left: 16px;
    right: 16px;
  }
}
</style>
