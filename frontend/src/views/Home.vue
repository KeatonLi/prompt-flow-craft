<template>
  <AppLayout>
    <template #main>
      <div class="about-page">
        <div class="hero-section">
          <div class="hero-icon">🧠</div>
          <h1 class="hero-title">Prompt Flow Craft</h1>
          <p class="hero-subtitle">企业级 AI 提示词工程平台 | 多模态提示词生成与管理解决方案</p>

          <!-- 核心跳转按钮 -->
          <div class="hero-actions">
            <router-link to="/generate" class="btn-primary">
              <span class="btn-icon">🚀</span>
              立即开始创作
            </router-link>
            <router-link to="/templates" class="btn-secondary">
              <span class="btn-icon">📚</span>
              查看提示词库
            </router-link>
          </div>

          <!-- 核心数据展示 -->
          <div class="hero-stats">
            <div class="stat-item">
              <span class="stat-value">8+</span>
              <span class="stat-label">预设分类</span>
            </div>
            <div class="stat-item">
              <span class="stat-value">4层</span>
              <span class="stat-label">架构模型</span>
            </div>
            <div class="stat-item">
              <span class="stat-value">AI</span>
              <span class="stat-label">智能分类</span>
            </div>
          </div>
        </div>

        <div id="overview" class="preview-section">
          <h2 class="section-title">💡 核心能力</h2>
          <div class="preview-grid">
            <div class="preview-card">
              <div class="preview-icon">🎯</div>
              <h3>智能生成</h3>
              <p>基于四层架构模型，自动生成高质量提示词</p>
            </div>
            <div class="preview-card">
              <div class="preview-icon">🏗️</div>
              <h3>四层架构</h3>
              <p>核心定义层 → 交互接口层 → 内部处理层 → 全局约束层</p>
            </div>
            <div class="preview-card">
              <div class="preview-icon">💾</div>
              <h3>历史记录</h3>
              <p>永久保存所有提示词，支持搜索、筛选、复用</p>
            </div>
            <div class="preview-card">
              <div class="preview-icon">🏷️</div>
              <h3>智能分类</h3>
              <p>规则匹配 + AI 智能分类，8 大预设分类自动打标签</p>
            </div>
          </div>
        </div>

        <div id="features" class="features-section">
          <h2 class="section-title">🔧 功能特性</h2>
          <div class="features-grid">
            <div class="feature-item">
              <div class="feature-icon">🎨</div>
              <div class="feature-content">
                <h4>多维度参数配置</h4>
                <p>支持目标受众、输出格式、语调风格、内容长度等多维度定制</p>
              </div>
            </div>
            <div class="feature-item">
              <div class="feature-icon">⚡</div>
              <div class="feature-content">
                <h4>高效缓存系统</h4>
                <p>相同请求参数自动缓存，哈希算法精确匹配，节省 API 成本</p>
              </div>
            </div>
            <div class="feature-item">
              <div class="feature-icon">👥</div>
              <div class="feature-content">
                <h4>团队协作</h4>
                <p>提示词收藏分组、一键分享复用、使用统计与效果追踪</p>
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
              <div class="feature-icon">🔒</div>
              <div class="feature-content">
                <h4>企业级安全</h4>
                <p>数据加密存储，保障用户隐私安全</p>
              </div>
            </div>
          </div>
        </div>

        <!-- 分类展示区域 -->
        <div id="categories" class="categories-section">
          <h2 class="section-title">📂 预设分类</h2>
          <p class="section-desc">覆盖8大常用场景，快速定位所需提示词</p>
          <div class="categories-grid">
            <router-link to="/templates" class="category-card">
              <span class="cat-icon">💼</span>
              <span class="cat-name">办公效率</span>
              <span class="cat-desc">会议纪要、邮件撰写...</span>
            </router-link>
            <router-link to="/templates" class="category-card">
              <span class="cat-icon">📝</span>
              <span class="cat-name">内容创作</span>
              <span class="cat-desc">文章写作、文案策划...</span>
            </router-link>
            <router-link to="/templates" class="category-card">
              <span class="cat-icon">💻</span>
              <span class="cat-name">代码开发</span>
              <span class="cat-desc">代码生成、代码审查...</span>
            </router-link>
            <router-link to="/templates" class="category-card">
              <span class="cat-icon">📚</span>
              <span class="cat-name">教育培训</span>
              <span class="cat-desc">学习大纲、试题生成...</span>
            </router-link>
            <router-link to="/templates" class="category-card">
              <span class="cat-icon">🎬</span>
              <span class="cat-name">视频媒体</span>
              <span class="cat-desc">脚本创作、创意策划...</span>
            </router-link>
            <router-link to="/templates" class="category-card">
              <span class="cat-icon">📊</span>
              <span class="cat-name">数据分析</span>
              <span class="cat-desc">报告生成、图表解读...</span>
            </router-link>
            <router-link to="/templates" class="category-card">
              <span class="cat-icon">🎨</span>
              <span class="cat-name">设计创意</span>
              <span class="cat-desc">UI设计、Logo创意...</span>
            </router-link>
            <router-link to="/templates" class="category-card">
              <span class="cat-icon">💬</span>
              <span class="cat-name">智能对话</span>
              <span class="cat-desc">客服话术、聊天机器人...</span>
            </router-link>
          </div>
        </div>

        <div id="tech" class="tech-section">
          <h2 class="section-title">🛠️ 技术架构</h2>
          <div class="tech-grid">
            <div class="tech-category">
              <div class="tech-header">
                <span class="tech-icon">🎨</span>
                <h4>前端技术</h4>
              </div>
              <div class="tech-tags">
                <span class="tech-tag vue">Vue 3.4+</span>
                <span class="tech-tag ts">TypeScript 5.0+</span>
                <span class="tech-tag vite">Vite 5.0+</span>
                <span class="tech-tag pinia">Pinia 2.1+</span>
              </div>
            </div>
            <div class="tech-category">
              <div class="tech-header">
                <span class="tech-icon">⚙️</span>
                <h4>后端技术</h4>
              </div>
              <div class="tech-tags">
                <span class="tech-tag java">Java 17+</span>
                <span class="tech-tag spring">Spring Boot 3.2</span>
                <span class="tech-tag jpa">Hibernate 6.4</span>
                <span class="tech-tag mysql">MySQL 8.0+</span>
              </div>
            </div>
            <div class="tech-category">
              <div class="tech-header">
                <span class="tech-icon">🤖</span>
                <h4>AI 能力</h4>
              </div>
              <div class="tech-tags">
                <span class="tech-tag ai">MiniMax</span>
                <span class="tech-tag nlp">LLM</span>
                <span class="tech-tag ml">Prompt Engineering</span>
              </div>
            </div>
          </div>
        </div>

        <div class="footer-section">
          <p class="footer-text">🌟 感谢使用 Prompt Flow Craft！让 AI 创作更简单、更高效</p>
          <p class="footer-version">版本 2.0.0 | 持续更新优化中</p>
        </div>
      </div>
    </template>
  </AppLayout>
</template>

<script setup>
import AppLayout from '../components/layout/AppLayout.vue'
</script>

<style scoped>
.about-page {
  min-height: 100vh;
  background: #f8fafc;
}

.hero-section {
  background: linear-gradient(135deg, #1e40af 0%, #1d4ed8 50%, #2563eb 100%);
  padding: 100px 40px 120px;
  text-align: center;
  position: relative;
  overflow: hidden;
}

.hero-section::before {
  content: '';
  position: absolute;
  top: -20%;
  left: -10%;
  width: 60%;
  height: 60%;
  background: radial-gradient(circle, rgba(255, 255, 255, 0.2) 0%, transparent 70%);
  filter: blur(60px);
  animation: float 8s ease-in-out infinite;
}

.hero-section::after {
  content: '';
  position: absolute;
  bottom: -10%;
  right: -10%;
  width: 50%;
  height: 50%;
  background: radial-gradient(circle, rgba(96, 165, 250, 0.3) 0%, transparent 70%);
  filter: blur(60px);
  animation: float 10s ease-in-out infinite reverse;
}

/* 装饰性圆形 */
.hero-decoration {
  position: absolute;
  border-radius: 50%;
  border: 1px solid rgba(255, 255, 255, 0.1);
}

.hero-decoration-1 {
  width: 300px;
  height: 300px;
  top: -100px;
  right: 10%;
  animation: rotate 20s linear infinite;
}

.hero-decoration-2 {
  width: 200px;
  height: 200px;
  bottom: -50px;
  left: 5%;
  animation: rotate 15s linear infinite reverse;
}

@keyframes float {
  0%, 100% { transform: translate(0, 0); }
  50% { transform: translate(20px, -20px); }
}

@keyframes rotate {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

@keyframes pulse {
  0%, 100% { transform: scale(1); opacity: 0.5; }
  50% { transform: scale(1.1); opacity: 0.3; }
}

.hero-icon {
  font-size: 5rem;
  margin-bottom: 24px;
  position: relative;
  animation: iconFloat 3s ease-in-out infinite;
  filter: drop-shadow(0 8px 24px rgba(59, 130, 246, 0.4));
}

@keyframes iconFloat {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-8px); }
}

.hero-title {
  font-size: 3.5rem;
  font-weight: 800;
  color: white;
  margin: 0 0 16px;
  position: relative;
  letter-spacing: -0.02em;
}

.hero-subtitle {
  color: rgba(255,255,255,0.75);
  font-size: 1.35rem;
  margin: 0 auto 48px;
  max-width: 680px;
  position: relative;
  line-height: 1.7;
  font-weight: 400;
}

.hero-actions {
  position: relative;
  display: flex;
  justify-content: center;
  gap: 20px;
  flex-wrap: wrap;
}

.btn-primary {
  display: inline-flex;
  align-items: center;
  gap: 12px;
  padding: 20px 48px;
  background: linear-gradient(135deg, #f97316 0%, #ea580c 50%, #fb923c 100%);
  background-size: 200% 200%;
  color: white;
  font-size: 1.2rem;
  font-weight: 700;
  border-radius: 50px;
  text-decoration: none;
  box-shadow: 0 8px 32px rgba(249, 115, 22, 0.5), 0 0 0 0 rgba(249, 115, 22, 0.4);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  border: none;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  animation: gradientMove 3s ease infinite;
  text-transform: uppercase;
  letter-spacing: 1px;
}

@keyframes gradientMove {
  0% { background-position: 0% 50%; }
  50% { background-position: 100% 50%; }
  100% { background-position: 0% 50%; }
}

.btn-primary::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255,255,255,0.3), transparent);
  transition: left 0.6s;
}

.btn-primary::after {
  content: '';
  position: absolute;
  top: -50%;
  left: -50%;
  width: 200%;
  height: 200%;
  background: radial-gradient(circle, rgba(255,255,255,0.1) 0%, transparent 60%);
  animation: pulse 2s ease-in-out infinite;
}

@keyframes pulse {
  0%, 100% { transform: scale(1); opacity: 0.5; }
  50% { transform: scale(1.1); opacity: 0.3; }
}

.btn-primary:hover::before {
  left: 100%;
}

.btn-primary:hover {
  transform: translateY(-4px) scale(1.03);
  box-shadow: 0 16px 48px rgba(249, 115, 22, 0.6), 0 0 20px rgba(249, 115, 22, 0.4);
}

.btn-secondary {
  display: inline-flex;
  align-items: center;
  gap: 10px;
  padding: 16px 32px;
  background: rgba(255,255,255,0.08);
  color: white;
  font-size: 1rem;
  font-weight: 500;
  border-radius: 16px;
  text-decoration: none;
  border: 1px solid rgba(255,255,255,0.2);
  backdrop-filter: blur(10px);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.btn-secondary:hover {
  background: rgba(255,255,255,0.15);
  border-color: rgba(255,255,255,0.4);
  transform: translateY(-2px);
}

.hero-stats {
  display: flex;
  justify-content: center;
  gap: 32px;
  margin-top: 56px;
  position: relative;
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20px 32px;
  background: rgba(255,255,255,0.08);
  border-radius: 16px;
  border: 1px solid rgba(255,255,255,0.1);
  backdrop-filter: blur(10px);
  min-width: 120px;
}

.stat-value {
  font-size: 2rem;
  font-weight: 700;
  color: white;
  line-height: 1.2;
  background: linear-gradient(135deg, #fff 0%, #bfdbfe 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.stat-label {
  font-size: 0.9rem;
  color: rgba(255,255,255,0.6);
  margin-top: 8px;
  font-weight: 500;
}

.btn-icon {
  font-size: 1.5rem;
  animation: iconBounce 2s ease-in-out infinite;
}

@keyframes iconBounce {
  0%, 100% { transform: scale(1); }
  50% { transform: scale(1.2); }
}

.section-title {
  font-size: 1.8rem;
  font-weight: 700;
  color: #1e293b;
  margin: 0 0 16px;
  padding: 0;
  display: inline-block;
  position: relative;
  letter-spacing: -0.02em;
}

.section-title::after {
  content: '';
  position: absolute;
  bottom: -8px;
  left: 0;
  width: 48px;
  height: 4px;
  background: linear-gradient(90deg, #3b82f6, #60a5fa);
  border-radius: 2px;
}

.preview-section, .features-section, .tech-section, .categories-section {
  padding: 64px 40px;
  max-width: 1200px;
  margin: 0 auto;
}

.section-desc {
  text-align: center;
  color: #64748b;
  margin: -20px 0 32px;
  font-size: 0.95rem;
}

.preview-grid, .features-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(260px, 1fr));
  gap: 24px;
}

.preview-card, .feature-item {
  background: white;
  border-radius: 16px;
  padding: 32px;
  border: 1px solid #e2e8f0;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.preview-card:hover, .feature-item:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 24px rgba(59, 130, 246, 0.12);
  border-color: #3b82f6;
}

.preview-icon, .feature-icon {
  font-size: 2.8rem;
  margin-bottom: 20px;
  display: inline-block;
  padding: 16px;
  background: linear-gradient(135deg, rgba(59, 130, 246, 0.1) 0%, rgba(96, 165, 250, 0.1) 100%);
  border-radius: 16px;
}

.preview-card h3, .feature-content h4 {
  font-size: 1.2rem;
  font-weight: 600;
  color: #1e293b;
  margin: 0 0 12px;
}

.preview-card p, .feature-content p {
  font-size: 0.95rem;
  color: #64748b;
  margin: 0;
  line-height: 1.7;
}

/* 分类展示区域 */
.categories-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
}

.category-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 28px 20px;
  background: white;
  border-radius: 16px;
  border: 1px solid #e2e8f0;
  text-decoration: none;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.category-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 20px rgba(59, 130, 246, 0.15);
  border-color: #3b82f6;
}

.category-card .cat-icon {
  font-size: 3rem;
  margin-bottom: 16px;
}

.category-card .cat-name {
  font-size: 1.15rem;
  font-weight: 600;
  color: #1e293b;
  margin-bottom: 8px;
}

.category-card .cat-desc {
  font-size: 0.85rem;
  color: #64748b;
  text-align: center;
}

.tech-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 24px;
}

.tech-category {
  background: white;
  border-radius: 16px;
  padding: 28px;
  border: 1px solid #e2e8f0;
}

.tech-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 20px;
}

.tech-icon {
  font-size: 1.5rem;
}

.tech-category h4 {
  font-size: 1.1rem;
  font-weight: 600;
  color: #1e293b;
  margin: 0 0 20px;
}

.tech-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.tech-tag {
  padding: 8px 16px;
  border-radius: 20px;
  font-size: 0.85rem;
  font-weight: 500;
}

.tech-tag.vue { background: #42b88320; color: #42b883; }
.tech-tag.ts { background: #3178c620; color: #3178c6; }
.tech-tag.vite { background: #646cff20; color: #646cff; }
.tech-tag.pinia { background: #ffd85920; color: #d4a400; }
.tech-tag.java { background: #f9731620; color: #f97316; }
.tech-tag.spring { background: #6db33f20; color: #6db33f; }
.tech-tag.jpa { background: #59652c20; color: #59652c; }
.tech-tag.mysql { background: #00758f20; color: #00758f; }
.tech-tag.ai { background: #8b5cf620; color: #8b5cf6; }
.tech-tag.nlp { background: #60a5fa20; color: #60a5fa; }
.tech-tag.ml { background: #14b8a620; color: #14b8a6; }

.footer-section {
  padding: 48px 40px;
  text-align: center;
  background: white;
  border-top: 1px solid #e2e8f0;
}

.footer-text {
  font-size: 1.1rem;
  color: #475569;
  margin: 0 0 8px;
  font-weight: 500;
}

.footer-version {
  font-size: 0.9rem;
  color: #94a3b8;
  margin: 0;
}

/* 响应式 */
@media (max-width: 768px) {
  .hero-section {
    padding: 60px 24px 80px;
  }

  .hero-icon {
    font-size: 4rem;
  }

  .hero-title {
    font-size: 2rem;
  }

  .hero-subtitle {
    font-size: 1.05rem;
  }

  .hero-stats {
    gap: 24px;
    margin-top: 36px;
  }

  .stat-value {
    font-size: 1.4rem;
  }

  .stat-label {
    font-size: 0.8rem;
  }

  .btn-primary {
    padding: 14px 28px;
    font-size: 1rem;
    width: 100%;
    justify-content: center;
  }

  .btn-secondary {
    width: 100%;
    justify-content: center;
  }

  .preview-section, .features-section, .tech-section {
    padding: 48px 24px;
  }
}

.about-page {
  background:
    radial-gradient(circle at top, rgba(37, 99, 235, 0.10), transparent 28%),
    linear-gradient(180deg, #f8fbff 0%, #f3f7fb 52%, #eef4f9 100%);
}

.hero-section {
  background:
    radial-gradient(circle at top left, rgba(255, 255, 255, 0.20), transparent 25%),
    linear-gradient(135deg, #0f172a 0%, #1d4ed8 55%, #0ea5e9 100%);
  padding: 110px 40px 128px;
  border-bottom-left-radius: 40px;
  border-bottom-right-radius: 40px;
}

.hero-title {
  letter-spacing: -0.04em;
}

.hero-subtitle {
  color: rgba(255, 255, 255, 0.78);
}

.btn-primary,
.btn-secondary {
  border-radius: 999px;
}

.btn-primary {
  box-shadow: 0 18px 38px rgba(249, 115, 22, 0.42), 0 0 0 0 rgba(249, 115, 22, 0.4);
}

.stat-item,
.preview-card,
.feature-item,
.category-card,
.tech-category {
  border-radius: 24px;
}

.preview-card,
.feature-item,
.category-card,
.tech-category {
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.96) 0%, rgba(248, 250, 252, 0.94) 100%);
  border: 1px solid rgba(148, 163, 184, 0.14);
  box-shadow: 0 18px 36px rgba(15, 23, 42, 0.05);
}

.preview-card:hover,
.feature-item:hover,
.category-card:hover {
  transform: translateY(-6px);
  box-shadow: 0 28px 48px rgba(37, 99, 235, 0.12);
  border-color: rgba(37, 99, 235, 0.16);
}

.footer-section {
  background: rgba(255, 255, 255, 0.58);
  backdrop-filter: blur(12px);
}

@media (max-width: 768px) {
  .hero-section {
    padding: 72px 24px 88px;
    border-bottom-left-radius: 28px;
    border-bottom-right-radius: 28px;
  }
}

/* Banner performance tuning */
.hero-section,
.hero-section::before,
.hero-section::after,
.hero-icon,
.btn-primary,
.btn-primary::after,
.stat-item {
  will-change: transform;
}

.hero-section {
  isolation: isolate;
}

.hero-section::before,
.hero-section::after {
  filter: blur(28px);
  opacity: 0.8;
  animation-duration: 16s;
}

@keyframes floatSoft {
  0%, 100% { transform: translate3d(0, 0, 0); }
  50% { transform: translate3d(12px, -12px, 0); }
}

.hero-section::before,
.hero-section::after {
  animation-name: floatSoft;
  animation-timing-function: ease-in-out;
  animation-iteration-count: infinite;
}

.hero-icon {
  animation: iconFloatLite 6s ease-in-out infinite;
}

@keyframes iconFloatLite {
  0%, 100% { transform: translate3d(0, 0, 0); }
  50% { transform: translate3d(0, -5px, 0); }
}

.btn-primary {
  background-size: 100% 100%;
  animation: none;
}

.btn-primary::after {
  animation: none;
  opacity: 0.45;
}

.btn-icon {
  animation: none;
}

.stat-item {
  backdrop-filter: blur(6px);
}

@media (max-width: 768px) {
  .hero-section::before,
  .hero-section::after {
    filter: blur(20px);
    animation: none;
  }

  .hero-icon {
    animation: none;
  }
}

@media (prefers-reduced-motion: reduce) {
  .hero-section::before,
  .hero-section::after,
  .hero-icon,
  .btn-primary::before,
  .btn-primary::after,
  .btn-icon {
    animation: none !important;
    transition: none !important;
  }
}
</style>
