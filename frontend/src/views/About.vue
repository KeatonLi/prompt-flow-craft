<template>
  <AppLayout>
    <template #main>
      <div class="about-page">
        <div class="hero-section">
          <div class="hero-icon">🎨</div>
          <h1 class="hero-title">关于 Prompt Flow Craft</h1>
          <p class="hero-subtitle">企业级 AI 提示词工程平台，让 AI 对话更精准、更高效</p>
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
  padding: 60px 40px;
  text-align: center;
}

.hero-icon {
  font-size: 4rem;
  margin-bottom: 16px;
}

.hero-title {
  font-size: 2.2rem;
  font-weight: 700;
  color: white;
  margin: 0 0 12px;
}

.hero-subtitle {
  color: rgba(255,255,255,0.8);
  font-size: 1.1rem;
  margin: 0;
  max-width: 600px;
  margin: 0 auto;
}

.section-title {
  font-size: 1.4rem;
  font-weight: 600;
  color: #1e293b;
  margin: 0 0 24px;
  padding-bottom: 12px;
  border-bottom: 2px solid #e2e8f0;
}

.preview-section, .features-section, .tech-section {
  padding: 48px 40px;
  max-width: 1200px;
  margin: 0 auto;
}

.preview-grid, .features-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(260px, 1fr));
  gap: 20px;
}

.preview-card, .feature-item {
  background: white;
  border-radius: 16px;
  padding: 24px;
  border: 1px solid #e2e8f0;
  transition: all 0.3s;
}

.preview-card:hover, .feature-item:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 24px rgba(0,0,0,0.08);
}

.preview-icon, .feature-icon {
  font-size: 2.2rem;
  margin-bottom: 12px;
}

.preview-card h3, .feature-content h4 {
  font-size: 1.1rem;
  font-weight: 600;
  color: #1e293b;
  margin: 0 0 8px;
}

.preview-card p, .feature-content p {
  font-size: 0.9rem;
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
  padding: 24px;
  border: 1px solid #e2e8f0;
}

.tech-category h4 {
  font-size: 1rem;
  font-weight: 600;
  color: #1e293b;
  margin: 0 0 16px;
}

.tech-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.tech-tag {
  padding: 6px 14px;
  border-radius: 20px;
  font-size: 0.8rem;
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
  padding: 40px;
  text-align: center;
  background: white;
  border-top: 1px solid #e2e8f0;
}

.footer-text {
  font-size: 1rem;
  color: #64748b;
  margin: 0 0 8px;
}

.footer-version {
  font-size: 0.85rem;
  color: #94a3b8;
  margin: 0;
}
</style>
