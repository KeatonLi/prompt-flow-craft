<template>
  <div id="app">
    <!-- 头部导航 -->
    <el-header class="app-header">
      <div class="header-content">
        <div class="logo">
          <el-icon><Magic /></el-icon>
          <span>Prompt Flow Craft</span>
        </div>
        <el-menu
          :default-active="$route.path"
          class="header-menu"
          mode="horizontal"
          router
        >
          <el-menu-item index="/">首页</el-menu-item>
          <el-menu-item index="/about">关于</el-menu-item>
        </el-menu>
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
        <div class="tech-stack">
          <el-tag size="small" type="info">智能创作</el-tag>
          <el-tag size="small" type="success">专业品质</el-tag>
          <el-tag size="small" type="warning">高效便捷</el-tag>
          <el-tag size="small" type="danger">持续创新</el-tag>
        </div>
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
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  color: #2d5a3d;
  padding: 0;
  height: 60px;
  line-height: 60px;
  box-shadow: 0 4px 20px rgba(168, 230, 207, 0.15), 0 2px 8px rgba(0, 0, 0, 0.05);
  border-bottom: 1px solid rgba(168, 230, 207, 0.2);
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
  font-size: 20px;
  font-weight: bold;
  color: #2d5a3d;
}

.logo .el-icon {
  margin-right: 8px;
  font-size: 24px;
  background: linear-gradient(135deg, #7dd3a0 0%, #a8e6cf 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.header-menu {
  background: transparent;
  border: none;
}

.header-menu .el-menu-item {
  color: #2d5a3d;
  border-bottom: 2px solid transparent;
  font-weight: 500;
  transition: all 0.3s ease;
}

.header-menu .el-menu-item:hover,
.header-menu .el-menu-item.is-active {
  background: rgba(125, 211, 160, 0.1);
  border-bottom-color: #7dd3a0;
  color: #2d5a3d;
  border-radius: 8px 8px 0 0;
}

.app-main {
  flex: 1;
  padding: 0;
  background: linear-gradient(135deg, #f8fdfa 0%, #f0f9f4 50%, #e8f5e8 100%);
  position: relative;
  overflow: hidden;
}

.app-main::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: radial-gradient(circle at 20% 80%, rgba(168, 230, 207, 0.1) 0%, transparent 50%),
              radial-gradient(circle at 80% 20%, rgba(125, 211, 160, 0.1) 0%, transparent 50%),
              radial-gradient(circle at 40% 40%, rgba(168, 230, 207, 0.05) 0%, transparent 50%);
  pointer-events: none;
}

.app-footer {
  background: linear-gradient(135deg, #4a7c59 0%, #2d5a3d 100%);
  color: white;
  text-align: center;
  padding: 25px;
  box-shadow: 0 -4px 20px rgba(168, 230, 207, 0.2), 0 -2px 8px rgba(0, 0, 0, 0.1);
  backdrop-filter: blur(10px);
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