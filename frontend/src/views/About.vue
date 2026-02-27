<template>
  <AppLayout>
    <template #sidebar-left>
      <div class="about-sidebar">
        <div class="sidebar-section">
          <h4>页面导航</h4>
          <router-link to="/" class="nav-item">
            <span class="nav-icon">🏠</span>
            <span>返回首页</span>
          </router-link>
        </div>
        
        <div class="sidebar-section">
          <h4>关于内容</h4>
          <a href="#overview" class="nav-link" @click.prevent="scrollTo('overview')">功能概览</a>
          <a href="#features" class="nav-link" @click.prevent="scrollTo('features')">核心功能</a>
          <a href="#guide" class="nav-link" @click.prevent="scrollTo('guide')">使用指南</a>
          <a href="#tech" class="nav-link" @click.prevent="scrollTo('tech')">技术架构</a>
        </div>
      </div>
    </template>

    <template #main>
      <div class="about-page">
        <!-- 头部区域 -->
        <div class="hero-section">
          <div class="hero-icon">🎨</div>
          <h1 class="hero-title">关于 Prompt Flow Craft</h1>
          <p class="hero-subtitle">专业的AI提示词创作平台，让每个人都能成为AI对话专家</p>
        </div>

        <!-- 页面预览 -->
        <div id="overview" class="preview-section">
          <h2 class="section-title">📱 页面功能概览</h2>
          <div class="preview-grid">
            <div class="preview-card">
              <div class="preview-icon">📝</div>
              <h3>提示词生成</h3>
              <p>输入任务描述，选择目标受众、输出格式、语调风格等参数，AI 将为您生成专业级提示词</p>
              <div class="preview-tags">
                <span class="tag">智能生成</span>
                <span class="tag">多参数配置</span>
              </div>
            </div>
            
            <div class="preview-card">
              <div class="preview-icon">📚</div>
              <h3>历史记录</h3>
              <p>右侧边栏展示所有生成的提示词历史，支持搜索、筛选、复用和查看详情</p>
              <div class="preview-tags">
                <span class="tag">永久保存</span>
                <span class="tag">一键复用</span>
              </div>
            </div>
            
            <div class="preview-card">
              <div class="preview-icon">📁</div>
              <h3>分类管理</h3>
              <p>左侧分类导航帮助您按类别组织提示词，快速筛选和查找相关内容</p>
              <div class="preview-tags">
                <span class="tag">智能分类</span>
                <span class="tag">标签系统</span>
              </div>
            </div>
            
            <div class="preview-card">
              <div class="preview-icon">⭐</div>
              <h3>收藏功能</h3>
              <p>收藏常用的提示词，方便快速访问和使用，提高工作效率</p>
              <div class="preview-tags">
                <span class="tag">快捷访问</span>
                <span class="tag">favorites</span>
              </div>
            </div>
          </div>
        </div>

        <!-- 核心功能 -->
        <div id="features" class="features-section">
          <h2 class="section-title">✨ 核心功能</h2>
          <div class="features-grid">
            <div class="feature-item">
              <div class="feature-icon">🎯</div>
              <div class="feature-content">
                <h4>智能提示词生成</h4>
                <p>基于先进AI算法，根据您的需求自动生成高质量提示词</p>
              </div>
            </div>
            
            <div class="feature-item">
              <div class="feature-icon">🎨</div>
              <div class="feature-content">
                <h4>多维度参数配置</h4>
                <p>支持目标受众、输出格式、语调风格、内容长度等多维度定制</p>
              </div>
            </div>
            
            <div class="feature-item">
              <div class="feature-icon">📋</div>
              <div class="feature-content">
                <h4>历史记录管理</h4>
                <p>自动保存所有生成的提示词，支持搜索、筛选、复用</p>
              </div>
            </div>
            
            <div class="feature-item">
              <div class="feature-icon">🏷️</div>
              <div class="feature-content">
                <h4>智能分类标签</h4>
                <p>自动为提示词分类打标签，方便后续查找和管理</p>
              </div>
            </div>
            
            <div class="feature-item">
              <div class="feature-icon">📱</div>
              <div class="feature-content">
                <h4>响应式设计</h4>
                <p>完美适配各种设备，随时随地创作提示词</p>
              </div>
            </div>
            
            <div class="feature-item">
              <div class="feature-icon">⚡</div>
              <div class="feature-content">
                <h4>快速响应</h4>
                <p>秒级生成响应，大幅提升工作效率</p>
              </div>
            </div>
          </div>
        </div>

        <!-- 使用指南 -->
        <div id="guide" class="guide-section">
          <h2 class="section-title">📖 使用指南</h2>
          <div class="guide-steps">
            <div class="step-item">
              <div class="step-number">1</div>
              <div class="step-content">
                <h4>描述需求</h4>
                <p>在表单中详细描述您希望AI完成的任务</p>
              </div>
            </div>
            
            <div class="step-arrow">→</div>
            
            <div class="step-item">
              <div class="step-number">2</div>
              <div class="step-content">
                <h4>配置参数</h4>
                <p>选择目标受众、输出格式、语调风格等参数</p>
              </div>
            </div>
            
            <div class="step-arrow">→</div>
            
            <div class="step-item">
              <div class="step-number">3</div>
              <div class="step-content">
                <h4>生成提示词</h4>
                <p>点击生成按钮，等待AI生成专业提示词</p>
              </div>
            </div>
            
            <div class="step-arrow">→</div>
            
            <div class="step-item">
              <div class="step-number">4</div>
              <div class="step-content">
                <h4>复用优化</h4>
                <p>在历史记录中复用或查看详情，持续优化</p>
              </div>
            </div>
          </div>
        </div>

        <!-- 技术栈 -->
        <div id="tech" class="tech-section">
          <h2 class="section-title">🛠️ 技术架构</h2>
          <div class="tech-grid">
            <div class="tech-category">
              <h4>前端技术</h4>
              <div class="tech-tags">
                <span class="tech-tag vue">Vue 3</span>
                <span class="tech-tag ts">TypeScript</span>
                <span class="tech-tag tailwind">Tailwind CSS</span>
                <span class="tech-tag vite">Vite</span>
              </div>
            </div>
            
            <div class="tech-category">
              <h4>后端技术</h4>
              <div class="tech-tags">
                <span class="tech-tag java">Java</span>
                <span class="tech-tag spring">Spring Boot</span>
                <span class="tech-tag jpa">JPA</span>
                <span class="tech-tag mysql">MySQL</span>
              </div>
            </div>
            
            <div class="tech-category">
              <h4>AI 能力</h4>
              <div class="tech-tags">
                <span class="tech-tag ai">大语言模型</span>
                <span class="tech-tag nlp">自然语言处理</span>
                <span class="tech-tag ml">机器学习</span>
              </div>
            </div>
          </div>
        </div>

        <!-- 页脚 -->
        <div class="footer-section">
          <p class="footer-text">🌟 感谢您使用 Prompt Flow Craft！让AI创作更简单、更高效</p>
          <p class="footer-version">版本 2.0.0 | 持续更新优化中</p>
        </div>
      </div>
    </template>

    <template #sidebar-right>
      <div class="quick-actions">
        <h4>快速操作</h4>
        <router-link to="/" class="action-btn primary">
          <span>✨</span>
          立即使用
        </router-link>
        <div class="action-links">
          <a href="https://github.com" target="_blank" class="action-link">
            <span>📂</span>
            GitHub 仓库
          </a>
        </div>
      </div>
    </template>
  </AppLayout>
</template>

<script setup lang="ts">
import AppLayout from '@/components/layout/AppLayout.vue';

function scrollTo(id: string) {
  const element = document.getElementById(id);
  if (element) {
    element.scrollIntoView({ behavior: 'smooth', block: 'start' });
  }
}
</script>

<style scoped>
/* 左侧边栏导航 */
.about-sidebar {
  padding: 16px 0;
}

.sidebar-section {
  margin-bottom: 24px;
}

.sidebar-section h4 {
  font-size: 0.75rem;
  font-weight: 600;
  color: #94a3b8;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  margin: 0 0 12px 0;
  padding: 0 16px;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 16px;
  color: #475569;
  text-decoration: none;
  font-size: 0.875rem;
  transition: all 0.2s;
  border-radius: 0 8px 8px 0;
  margin-right: 16px;
}

.nav-item:hover {
  background: #f1f5f9;
  color: #3b82f6;
}

.nav-item.router-link-active {
  background: #eff6ff;
  color: #2563eb;
  font-weight: 500;
}

.nav-icon {
  font-size: 1.1rem;
}

.nav-link {
  display: block;
  padding: 8px 16px;
  color: #64748b;
  text-decoration: none;
  font-size: 0.85rem;
  transition: all 0.2s;
  border-radius: 0 6px 6px 0;
  margin-right: 16px;
  cursor: pointer;
}

.nav-link:hover {
  background: #f1f5f9;
  color: #3b82f6;
}

/* 右侧快速操作 */
.quick-actions {
  padding: 16px;
}

.quick-actions h4 {
  font-size: 0.75rem;
  font-weight: 600;
  color: #94a3b8;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  margin: 0 0 12px 0;
}

.action-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 12px 16px;
  border-radius: 10px;
  font-size: 0.875rem;
  font-weight: 500;
  text-decoration: none;
  transition: all 0.2s;
  margin-bottom: 12px;
}

.action-btn.primary {
  background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
  color: white;
  box-shadow: 0 2px 8px rgba(59, 130, 246, 0.3);
}

.action-btn.primary:hover {
  background: linear-gradient(135deg, #2563eb 0%, #1d4ed8 100%);
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.4);
  transform: translateY(-1px);
}

.action-links {
  border-top: 1px solid #e2e8f0;
  padding-top: 12px;
}

.action-link {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 0;
  color: #64748b;
  text-decoration: none;
  font-size: 0.85rem;
  transition: color 0.2s;
}

.action-link:hover {
  color: #3b82f6;
}

/* 主内容区域 */
.about-page {
  min-height: 100%;
  padding-bottom: 40px;
}

/* 头部区域 */
.hero-section {
  text-align: center;
  margin-bottom: 50px;
  padding: 40px 0;
  background: linear-gradient(135deg, rgba(59, 130, 246, 0.05) 0%, rgba(99, 102, 241, 0.05) 100%);
  border-radius: 20px;
  margin: 0 0 40px 0;
}

.hero-icon {
  font-size: 5rem;
  margin-bottom: 20px;
  animation: bounce 2s ease infinite;
}

@keyframes bounce {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-10px); }
}

.hero-title {
  font-size: 2.2rem;
  color: #1e293b;
  margin-bottom: 12px;
  font-weight: 700;
  background: linear-gradient(135deg, #3b82f6 0%, #6366f1 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.hero-subtitle {
  font-size: 1.1rem;
  color: #64748b;
  margin: 0;
}

/* 区块标题 */
.section-title {
  font-size: 1.4rem;
  color: #1e293b;
  margin-bottom: 24px;
  font-weight: 600;
  text-align: center;
  scroll-margin-top: 20px;
}

/* 页面预览 */
.preview-section {
  margin-bottom: 50px;
}

.preview-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
  gap: 16px;
}

.preview-card {
  background: white;
  border-radius: 16px;
  padding: 20px;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.05), 0 2px 4px -1px rgba(0, 0, 0, 0.03);
  transition: all 0.3s ease;
  border: 1px solid #e2e8f0;
}

.preview-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1), 0 10px 10px -5px rgba(0, 0, 0, 0.04);
}

.preview-icon {
  font-size: 2.2rem;
  margin-bottom: 10px;
}

.preview-card h3 {
  font-size: 1rem;
  color: #1e293b;
  margin-bottom: 6px;
  font-weight: 600;
}

.preview-card p {
  font-size: 0.85rem;
  color: #64748b;
  line-height: 1.5;
  margin-bottom: 10px;
}

.preview-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}

.tag {
  font-size: 0.7rem;
  padding: 3px 8px;
  background: rgba(59, 130, 246, 0.1);
  color: #3b82f6;
  border-radius: 20px;
  font-weight: 500;
}

/* 核心功能 */
.features-section {
  margin-bottom: 50px;
}

.features-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 14px;
}

.feature-item {
  display: flex;
  align-items: flex-start;
  gap: 14px;
  background: white;
  padding: 16px;
  border-radius: 12px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
  border: 1px solid #e2e8f0;
  transition: all 0.2s;
}

.feature-item:hover {
  border-color: #3b82f6;
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.1);
}

.feature-icon {
  font-size: 1.6rem;
  flex-shrink: 0;
}

.feature-content h4 {
  font-size: 0.95rem;
  color: #1e293b;
  margin-bottom: 3px;
  font-weight: 600;
}

.feature-content p {
  font-size: 0.8rem;
  color: #64748b;
  line-height: 1.5;
  margin: 0;
}

/* 使用指南 */
.guide-section {
  margin-bottom: 50px;
}

.guide-steps {
  display: flex;
  align-items: center;
  justify-content: center;
  flex-wrap: wrap;
  gap: 12px;
  background: white;
  padding: 24px;
  border-radius: 16px;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.05);
}

.step-item {
  display: flex;
  align-items: center;
  gap: 10px;
  flex: 1;
  min-width: 160px;
  max-width: 200px;
}

.step-number {
  width: 36px;
  height: 36px;
  background: linear-gradient(135deg, #3b82f6 0%, #6366f1 100%);
  color: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 700;
  font-size: 1rem;
  flex-shrink: 0;
}

.step-content h4 {
  font-size: 0.9rem;
  color: #1e293b;
  margin-bottom: 2px;
  font-weight: 600;
}

.step-content p {
  font-size: 0.75rem;
  color: #64748b;
  margin: 0;
}

.step-arrow {
  font-size: 1.3rem;
  color: #cbd5e1;
  font-weight: 700;
}

/* 技术栈 */
.tech-section {
  margin-bottom: 50px;
}

.tech-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
  gap: 16px;
}

.tech-category {
  background: white;
  padding: 20px;
  border-radius: 12px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
  border: 1px solid #e2e8f0;
}

.tech-category h4 {
  font-size: 0.95rem;
  color: #1e293b;
  margin-bottom: 14px;
  font-weight: 600;
}

.tech-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.tech-tag {
  font-size: 0.75rem;
  padding: 5px 10px;
  border-radius: 6px;
  font-weight: 500;
}

.tech-tag.vue { background: rgba(66, 184, 131, 0.1); color: #42b883; }
.tech-tag.ts { background: rgba(49, 120, 198, 0.1); color: #3178c6; }
.tech-tag.tailwind { background: rgba(56, 189, 248, 0.1); color: #38bdf8; }
.tech-tag.vite { background: rgba(100, 108, 255, 0.1); color: #646cff; }
.tech-tag.java { background: rgba(83, 130, 161, 0.1); color: #5382a1; }
.tech-tag.spring { background: rgba(109, 179, 63, 0.1); color: #6db33f; }
.tech-tag.jpa { background: rgba(0, 0, 0, 0.05); color: #333; }
.tech-tag.mysql { background: rgba(0, 117, 143, 0.1); color: #00758f; }
.tech-tag.ai { background: rgba(239, 68, 68, 0.1); color: #ef4444; }
.tech-tag.nlp { background: rgba(245, 158, 11, 0.1); color: #f59e0b; }
.tech-tag.ml { background: rgba(139, 92, 246, 0.1); color: #8b5cf6; }

/* 页脚 */
.footer-section {
  text-align: center;
  padding: 30px;
  background: white;
  border-radius: 16px;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.05);
}

.footer-text {
  font-size: 0.95rem;
  color: #475569;
  margin-bottom: 8px;
}

.footer-version {
  font-size: 0.8rem;
  color: #94a3b8;
  margin: 0;
}

/* 响应式 */
@media (max-width: 768px) {
  .hero-title {
    font-size: 1.6rem;
  }
  
  .hero-subtitle {
    font-size: 0.9rem;
  }
  
  .guide-steps {
    flex-direction: column;
  }
  
  .step-arrow {
    transform: rotate(90deg);
  }
  
  .step-item {
    max-width: 100%;
  }
  
  .preview-grid {
    grid-template-columns: 1fr;
  }
  
  .features-grid {
    grid-template-columns: 1fr;
  }
}
</style>
