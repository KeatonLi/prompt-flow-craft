<template>
  <AppLayout>
    <template #main>
      <div class="about-page">
        <div class="hero-section">
          <div class="hero-icon">🎨</div>
          <h1 class="hero-title">Prompt Flow Craft</h1>
          <p class="hero-subtitle">企业级 AI 提示词工程平台，让 AI 对话更精准、更高效</p>
          
          <!-- 核心跳转按钮 -->
          <div class="hero-actions">
            <router-link to="/generate" class="btn-primary">
              <span class="btn-icon">✨</span>
              开始生成提示词
            </router-link>
          </div>
        </div>

        <div id="overview" class="preview-section">
          <h2 class="section-title">📱 功能概览</h2>
          <div class="preview-grid">
            <div class="preview-card">
              <div class="preview-icon">🧙</div>
              <h3>向导模式</h3>
              <p>多轮问答引导，渐进式构建专业级提示词</p>
            </div>
            <div class="preview-card">
              <div class="preview-icon">🎯</div>
              <h3>智能生成</h3>
              <p>基于四层架构模型，自动生成高质量提示词</p>
            </div>
            <div class="preview-card">
              <div class="preview-icon">💾</div>
              <h3>历史记录</h3>
              <p>永久保存所有提示词，支持搜索、筛选、复用</p>
            </div>
            <div class="preview-card">
              <div class="preview-icon">🏷️</div>
              <h3>智能分类</h3>
              <p>自动分类打标签，快速查找相关内容</p>
            </div>
          </div>
        </div>

        <div id="features" class="features-section">
          <h2 class="section-title">✨ 核心功能</h2>
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
                <h4>高效缓存机制</h4>
                <p>相同请求自动缓存，显著降低 API 调用成本</p>
              </div>
            </div>
            <div class="feature-item">
              <div class="feature-icon">🔄</div>
              <div class="feature-content">
                <h4>迭代优化生成</h4>
                <p>多轮问答持续优化，直到生成满意的提示词</p>
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
            <div class="feature-item">
              <div class="feature-icon">🚀</div>
              <div class="feature-content">
                <h4>高性能架构</h4>
                <p>微服务架构设计，支持高并发访问</p>
              </div>
            </div>
          </div>
        </div>

        <div id="tech" class="tech-section">
          <h2 class="section-title">🛠️ 技术架构</h2>
          <div class="tech-grid">
            <div class="tech-category">
              <h4>前端技术</h4>
              <div class="tech-tags">
                <span class="tech-tag vue">Vue 3</span>
                <span class="tech-tag ts">TypeScript</span>
                <span class="tech-tag tailwind">Vite</span>
                <span class="tech-tag vite">Pinia</span>
              </div>
            </div>
            <div class="tech-category">
              <h4>后端技术</h4>
              <div class="tech-tags">
                <span class="tech-tag java">Java</span>
                <span class="tech-tag spring">Spring Boot</span>
                <span class="tech-tag jpa">Hibernate</span>
                <span class="tech-tag mysql">MySQL</span>
              </div>
            </div>
            <div class="tech-category">
              <h4>AI 能力</h4>
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
  background: linear-gradient(135deg, #3b82f6 0%, #1d4ed8 100%);
  padding: 80px 40px 100px;
  text-align: center;
  position: relative;
  overflow: hidden;
}

.hero-section::before {
  content: '';
  position: absolute;
  top: -50%;
  left: -50%;
  width: 200%;
  height: 200%;
  background: radial-gradient(circle, rgba(255,255,255,0.1) 0%, transparent 60%);
  animation: pulse 4s ease-in-out infinite;
}

@keyframes pulse {
  0%, 100% { transform: scale(1); opacity: 0.5; }
  50% { transform: scale(1.1); opacity: 0.3; }
}

.hero-icon {
  font-size: 5rem;
  margin-bottom: 20px;
  position: relative;
}

.hero-title {
  font-size: 2.8rem;
  font-weight: 800;
  color: white;
  margin: 0 0 16px;
  position: relative;
  letter-spacing: -0.02em;
}

.hero-subtitle {
  color: rgba(255,255,255,0.85);
  font-size: 1.25rem;
  margin: 0 auto 40px;
  max-width: 600px;
  position: relative;
  line-height: 1.6;
}

.hero-actions {
  position: relative;
  display: flex;
  justify-content: center;
  gap: 16px;
  flex-wrap: wrap;
}

.btn-primary {
  display: inline-flex;
  align-items: center;
  gap: 10px;
  padding: 16px 36px;
  background: white;
  color: #3b82f6;
  font-size: 1.1rem;
  font-weight: 600;
  border-radius: 50px;
  text-decoration: none;
  box-shadow: 0 10px 30px rgba(0,0,0,0.2);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  border: none;
  cursor: pointer;
}

.btn-primary:hover {
  transform: translateY(-3px) scale(1.02);
  box-shadow: 0 15px 40px rgba(0,0,0,0.25);
  background: linear-gradient(135deg, #ffffff 0%, #f0f9ff 100%);
}

.btn-icon {
  font-size: 1.3rem;
}

.section-title {
  font-size: 1.5rem;
  font-weight: 700;
  color: #1e293b;
  margin: 0 0 32px;
  padding-bottom: 12px;
  border-bottom: 3px solid #e2e8f0;
}

.preview-section, .features-section, .tech-section {
  padding: 64px 40px;
  max-width: 1200px;
  margin: 0 auto;
}

.preview-grid, .features-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(260px, 1fr));
  gap: 24px;
}

.preview-card, .feature-item {
  background: white;
  border-radius: 16px;
  padding: 28px;
  border: 1px solid #e2e8f0;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.preview-card:hover, .feature-item:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 24px rgba(0,0,0,0.08);
  border-color: #3b82f6;
}

.preview-icon, .feature-icon {
  font-size: 2.5rem;
  margin-bottom: 16px;
}

.preview-card h3, .feature-content h4 {
  font-size: 1.15rem;
  font-weight: 600;
  color: #1e293b;
  margin: 0 0 10px;
}

.preview-card p, .feature-content p {
  font-size: 0.95rem;
  color: #64748b;
  margin: 0;
  line-height: 1.6;
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
.tech-tag.tailwind { background: #06b6d420; color: #06b6d4; }
.tech-tag.vite { background: #646cff20; color: #646cff; }
.tech-tag.java { background: #f9731620; color: #f97316; }
.tech-tag.spring { background: #6db33f20; color: #6db33f; }
.tech-tag.jpa { background: #59652c20; color: #59652c; }
.tech-tag.mysql { background: #00758f20; color: #00758f; }
.tech-tag.ai { background: #8b5cf620; color: #8b5cf6; }
.tech-tag.nlp { background: #ec489920; color: #ec4899; }
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
  
  .btn-primary {
    padding: 14px 28px;
    font-size: 1rem;
  }
  
  .preview-section, .features-section, .tech-section {
    padding: 48px 24px;
  }
}

/* 暗黑模式 */
:root.dark .about-page {
  background: #0f172a;
}

:root.dark .preview-card,
:root.dark .feature-item,
:root.dark .tech-category,
:root.dark .footer-section {
  background: rgba(30, 41, 59, 0.6);
  border-color: rgba(51, 65, 85, 0.6);
}

:root.dark .section-title {
  color: #f1f5f9;
  border-color: #334155;
}

:root.dark .preview-card h3,
:root.dark .feature-content h4,
:root.dark .tech-category h4,
:root.dark .footer-text {
  color: #f1f5f9;
}

:root.dark .preview-card p,
:root.dark .feature-content p,
:root.dark .footer-version {
  color: #94a3b8;
}

:root.dark .btn-primary {
  background: linear-gradient(135deg, #1e293b 0%, #0f172a 100%);
  color: white;
  border: 1px solid rgba(255,255,255,0.1);
}

:root.dark .btn-primary:hover {
  background: linear-gradient(135deg, #334155 0%, #1e293b 100%);
}
</style>
