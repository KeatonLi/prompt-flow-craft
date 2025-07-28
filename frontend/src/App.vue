<template>
  <div id="app">
    <!-- 头部导航 -->
    <el-header class="app-header">
      <div class="header-content">
        <div class="logo">
          <el-icon><Magic /></el-icon>
          <span>Prompt Flow Craft</span>
        </div>
        <nav class="header-nav">
          <router-link to="/" class="nav-item" :class="{ active: $route.path === '/' }">首页</router-link>
          <router-link to="/about" class="nav-item" :class="{ active: $route.path === '/about' }">关于</router-link>
        </nav>
      </div>
    </el-header>

    <!-- 主要内容区域 -->
    <el-main class="app-main">
      <router-view />
    </el-main>

    <!-- 底部 -->
    <el-footer class="app-footer">
      <div class="footer-content">
        <p>&copy; 2024 Prompt Flow Craft. 让AI更懂你的想法，创造无限可能</p>
      </div>
    </el-footer>
  </div>
</template>

<script>
export default {
  name: 'App'
}
</script>

<style>
#app {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.app-header {
  background: rgba(255, 255, 255, 0.98);
  backdrop-filter: blur(24px);
  color: #1e293b;
  padding: 0;
  height: 72px;
  line-height: 72px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05), 0 4px 16px rgba(0, 0, 0, 0.08);
  border-bottom: 1px solid rgba(226, 232, 240, 0.6);
  position: relative;
  z-index: 100;
}

.header-content {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
  height: 100%;
}

.logo {
  display: flex;
  align-items: center;
  font-size: 22px;
  font-weight: 700;
  color: #1e293b;
  letter-spacing: -0.025em;
}

.logo .el-icon {
  margin-right: 12px;
  font-size: 28px;
  background: linear-gradient(135deg, #3b82f6 0%, #1d4ed8 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.header-nav {
  display: flex;
  align-items: center;
  gap: 8px;
}

.nav-item {
  color: #64748b;
  text-decoration: none;
  padding: 12px 16px;
  border-bottom: 2px solid transparent;
  font-weight: 500;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  border-radius: 8px;
  display: flex;
  align-items: center;
  height: 72px;
  box-sizing: border-box;
}

.nav-item:hover,
.nav-item.active {
  background: rgba(59, 130, 246, 0.08);
  border-bottom-color: #3b82f6;
  color: #1e293b;
  transform: translateY(-1px);
}

.app-main {
  flex: 1;
  padding: 0;
  background: linear-gradient(135deg, #ffffff 0%, #f8fafc 50%, #f1f5f9 100%);
  position: relative;
  overflow-x: hidden;
  overflow-y: auto;
}

.app-main::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: radial-gradient(circle at 20% 80%, rgba(59, 130, 246, 0.03) 0%, transparent 50%),
              radial-gradient(circle at 80% 20%, rgba(99, 102, 241, 0.03) 0%, transparent 50%),
              radial-gradient(circle at 40% 40%, rgba(139, 92, 246, 0.02) 0%, transparent 50%);
  pointer-events: none;
}

.app-footer {
  background: linear-gradient(135deg, #1e293b 0%, #0f172a 100%);
  color: #e2e8f0;
  text-align: center;
  padding: 32px;
  box-shadow: 0 -1px 3px rgba(0, 0, 0, 0.05), 0 -4px 16px rgba(0, 0, 0, 0.08);
  backdrop-filter: blur(20px);
  position: relative;
}

.footer-content {
  max-width: 1200px;
  margin: 0 auto;
}

.tech-stack {
  margin-top: 10px;
}

.tech-stack .el-tag {
  margin: 0 5px;
}
</style>