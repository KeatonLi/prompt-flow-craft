<template>
  <div class="home-page">
    <div class="hero-section">
      <div class="hero-content">
        <h1 class="hero-title">
          <span class="hero-icon">🎨</span>
          智能提示词引擎
        </h1>
        <p class="hero-subtitle">基于四层架构模型，一键生成高质量AI提示词</p>
        <div class="hero-actions">
          <router-link to="/generate" class="btn btn-primary">
            <span>开始生成</span>
            <span class="btn-icon">→</span>
          </router-link>
          <router-link to="/templates" class="btn btn-secondary">
            <span>浏览模板</span>
          </router-link>
        </div>
      </div>
    </div>

    <div class="features-section">
      <h2 class="section-title">核心功能</h2>
      <div class="features-grid">
        <div class="feature-card">
          <div class="feature-icon">🎯</div>
          <h3>智能生成</h3>
          <p>基于四层架构模型，生成专业级提示词</p>
        </div>
        <div class="feature-card">
          <div class="feature-icon">💾</div>
          <h3>智能缓存</h3>
          <p>相同请求自动缓存，降低API成本</p>
        </div>
        <div class="feature-card">
          <div class="feature-icon">🏷️</div>
          <h3>自动分类</h3>
          <p>AI自动识别标签，智能分类管理</p>
        </div>
        <div class="feature-card">
          <div class="feature-icon">📊</div>
          <h3>数据统计</h3>
          <p>全面追踪使用数据，优化提示词效果</p>
        </div>
      </div>
    </div>

    <div class="categories-section">
      <h2 class="section-title">预设分类</h2>
      <div class="categories-grid">
        <div class="category-card">
          <span class="category-icon">📝</span>
          <span>办公效率</span>
        </div>
        <div class="category-card">
          <span class="category-icon">✍️</span>
          <span>内容创作</span>
        </div>
        <div class="category-card">
          <span class="category-icon">💻</span>
          <span>代码开发</span>
        </div>
        <div class="category-card">
          <span class="category-icon">📚</span>
          <span>教育培训</span>
        </div>
        <div class="category-card">
          <span class="category-icon">🎬</span>
          <span>视频媒体</span>
        </div>
        <div class="category-card">
          <span class="category-icon">📊</span>
          <span>数据分析</span>
        </div>
        <div class="category-card">
          <span class="category-icon">🎨</span>
          <span>设计创意</span>
        </div>
        <div class="category-card">
          <span class="category-icon">💬</span>
          <span>智能对话</span>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.home-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 40px 20px;
}

.hero-section {
  text-align: center;
  padding: 60px 20px;
}

.hero-content {
  max-width: 800px;
  margin: 0 auto;
}

.hero-title {
  font-size: 3.5rem;
  font-weight: 700;
  color: #fff;
  margin-bottom: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 16px;
}

.hero-icon {
  font-size: 3rem;
}

.hero-subtitle {
  font-size: 1.5rem;
  color: rgba(255, 255, 255, 0.9);
  margin-bottom: 40px;
}

.hero-actions {
  display: flex;
  gap: 20px;
  justify-content: center;
}

.btn {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 14px 32px;
  border-radius: 12px;
  font-size: 1.1rem;
  font-weight: 600;
  text-decoration: none;
  transition: all 0.3s ease;
}

.btn-primary {
  background: #fff;
  color: #667eea;
}

.btn-primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2);
}

.btn-secondary {
  background: rgba(255, 255, 255, 0.2);
  color: #fff;
  border: 2px solid rgba(255, 255, 255, 0.5);
}

.btn-secondary:hover {
  background: rgba(255, 255, 255, 0.3);
}

.btn-icon {
  transition: transform 0.3s ease;
}

.btn-primary:hover .btn-icon {
  transform: translateX(4px);
}

.features-section,
.categories-section {
  max-width: 1200px;
  margin: 60px auto;
  padding: 40px;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 20px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.1);
}

.section-title {
  font-size: 2rem;
  font-weight: 700;
  color: #1a202c;
  text-align: center;
  margin-bottom: 40px;
}

.features-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 24px;
}

.feature-card {
  padding: 30px;
  background: #f7fafc;
  border-radius: 16px;
  text-align: center;
  transition: all 0.3s ease;
}

.feature-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
}

.feature-icon {
  font-size: 3rem;
  margin-bottom: 16px;
}

.feature-card h3 {
  font-size: 1.3rem;
  color: #2d3748;
  margin-bottom: 12px;
}

.feature-card p {
  color: #718096;
  line-height: 1.6;
}

.categories-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 16px;
}

.category-card {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px 24px;
  background: #f7fafc;
  border-radius: 12px;
  font-size: 1rem;
  color: #4a5568;
  transition: all 0.3s ease;
  cursor: pointer;
}

.category-card:hover {
  background: #667eea;
  color: #fff;
}

.category-icon {
  font-size: 1.5rem;
}

@media (max-width: 768px) {
  .hero-title {
    font-size: 2.5rem;
    flex-direction: column;
  }

  .hero-subtitle {
    font-size: 1.2rem;
  }

  .hero-actions {
    flex-direction: column;
  }

  .features-section,
  .categories-section {
    padding: 24px;
    margin: 40px 16px;
  }
}
</style>
