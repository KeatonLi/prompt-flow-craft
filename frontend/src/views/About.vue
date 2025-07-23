<template>
  <div class="about">
    <div class="container">
      <div class="hero-section">
        <el-icon class="hero-icon"><InfoFilled /></el-icon>
        <h1 class="hero-title">关于 Prompt Flow Craft</h1>
        <p class="hero-subtitle">专业的AI提示词创作平台，让每个人都能成为AI对话专家</p>
      </div>

      <div class="content-grid">
        <el-card class="info-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <el-icon><Star /></el-icon>
              <span>项目简介</span>
            </div>
          </template>
          <p>
            Prompt Flow Craft 是新一代智能提示词创作平台，专为提升AI对话效果而生。
            我们运用先进的AI技术，帮助用户轻松创建精准、高效的提示词，
            让每一次AI对话都能达到专业水准。无论您是创作者、研究者还是商务人士，
            都能在这里找到最适合的AI沟通方式。
          </p>
        </el-card>

        <el-card class="info-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <el-icon><Tools /></el-icon>
              <span>核心功能</span>
            </div>
          </template>
          <ul class="feature-list">
            <li><el-icon><Check /></el-icon>智能提示词生成</li>
            <li><el-icon><Check /></el-icon>多种输出格式支持</li>
            <li><el-icon><Check /></el-icon>个性化参数配置</li>
            <li><el-icon><Check /></el-icon>一键复制分享</li>
            <li><el-icon><Check /></el-icon>响应式设计</li>
          </ul>
        </el-card>

        <el-card class="info-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <el-icon><Trophy /></el-icon>
              <span>特色优势</span>
            </div>
          </template>
          <div class="advantages">
            <div class="advantage-item">
              <el-tag type="success" size="large">高质量</el-tag>
              <span>采用业界领先AI技术，生成效果卓越</span>
            </div>
            <div class="advantage-item">
              <el-tag type="primary" size="large">易使用</el-tag>
              <span>零门槛上手，3步即可生成专业提示词</span>
            </div>
            <div class="advantage-item">
              <el-tag type="warning" size="large">个性化</el-tag>
              <span>智能适配不同场景，满足个性化需求</span>
            </div>
            <div class="advantage-item">
              <el-tag type="danger" size="large">效率高</el-tag>
              <span>秒级响应，大幅提升工作效率</span>
            </div>
          </div>
        </el-card>

        <el-card class="info-card tech-stack-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <el-icon><Monitor /></el-icon>
              <span>平台特色</span>
            </div>
          </template>
          <div class="tech-categories">
            <div class="tech-category">
              <h4>用户体验</h4>
              <div class="tech-tags">
                <el-tag>现代化界面</el-tag>
                <el-tag>响应式设计</el-tag>
                <el-tag>流畅交互</el-tag>
                <el-tag>直观操作</el-tag>
              </div>
            </div>
            <div class="tech-category">
              <h4>性能保障</h4>
              <div class="tech-tags">
                <el-tag type="success">高可用架构</el-tag>
                <el-tag type="success">快速响应</el-tag>
                <el-tag type="success">稳定服务</el-tag>
                <el-tag type="success">安全可靠</el-tag>
              </div>
            </div>
            <div class="tech-category">
              <h4>智能引擎</h4>
              <div class="tech-tags">
                <el-tag type="warning">先进AI算法</el-tag>
                <el-tag type="warning">深度学习</el-tag>
              </div>
            </div>
            <div class="tech-category">
              <h4>服务支持</h4>
              <div class="tech-tags">
                <el-tag type="info">7x24小时</el-tag>
                <el-tag type="info">API接口</el-tag>
                <el-tag type="info">数据安全</el-tag>
              </div>
            </div>
          </div>
        </el-card>

        <el-card class="info-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <el-icon><Document /></el-icon>
              <span>使用指南</span>
            </div>
          </template>
          <ol class="guide-list">
            <li>📝 描述您的创意想法，让AI理解您的真实需求</li>
            <li>🎯 选择目标场景，精准定位使用对象</li>
            <li>🎨 自定义输出风格，打造专属AI对话体验</li>
            <li>⚡ 一键智能生成，秒获专业级提示词</li>
            <li>📋 轻松复制使用，立即提升AI对话效果</li>
            <li>🚀 持续优化迭代，解锁更多AI可能性</li>
          </ol>
        </el-card>

        <el-card class="info-card contact-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <el-icon><Message /></el-icon>
              <span>联系我们</span>
            </div>
          </template>
          <div class="contact-info">
            <p>🌟 感谢您选择 Prompt Flow Craft！我们致力于为每位用户提供最佳的AI提示词创作体验。</p>
            <div class="contact-methods">
              <div class="contact-item">
                <el-icon><User /></el-icon>
                <span>团队：专业AI产品团队</span>
              </div>
              <div class="contact-item">
                <el-icon><Calendar /></el-icon>
                <span>版本：持续更新优化</span>
              </div>
              <div class="contact-item">
                <el-icon><Clock /></el-icon>
                <span>服务：全年无休在线</span>
              </div>
            </div>
          </div>
        </el-card>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'About'
}
</script>

<style scoped>
.about {
  min-height: calc(100vh - 140px);
  background: linear-gradient(135deg, #eff6ff 0%, #dbeafe 100%);
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 40px 20px;
}

.hero-section {
  text-align: center;
  margin-bottom: 50px;
}

.hero-icon {
  font-size: 4rem;
  color: #3b82f6;
  margin-bottom: 20px;
}

.hero-title {
  font-size: 3rem;
  color: #1e293b;
  margin-bottom: 15px;
  font-weight: 700;
}

.hero-subtitle {
  font-size: 1.3rem;
  color: #64748b;
  margin: 0;
}

.content-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(350px, 1fr));
  gap: 30px;
}

.info-card {
  height: fit-content;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 10px;
  font-weight: 600;
  color: #2d5a3d;
}

.card-header .el-icon {
  font-size: 1.2rem;
  color: #7dd3a0;
}

.feature-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.feature-list li {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 8px 0;
  color: #2d5a3d;
}

.feature-list .el-icon {
  color: #3b82f6;
  font-size: 1.1rem;
}

.advantages {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.advantage-item {
  display: flex;
  align-items: center;
  gap: 15px;
}

.tech-stack-card {
  grid-column: 1 / -1;
}

.tech-categories {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 25px;
}

.tech-category h4 {
  margin: 0 0 15px 0;
  color: #2d5a3d;
  font-size: 1.1rem;
}

.tech-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.guide-list {
  color: #2d5a3d;
  line-height: 1.8;
}

.guide-list li {
  margin-bottom: 10px;
}

.contact-card {
  grid-column: 1 / -1;
}

.contact-info p {
  color: #2d5a3d;
  margin-bottom: 20px;
  line-height: 1.6;
}

.contact-methods {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
}

.contact-item {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #2d5a3d;
}

.contact-item .el-icon {
  color: #7dd3a0;
}

@media (max-width: 768px) {
  .hero-title {
    font-size: 2.2rem;
  }
  
  .hero-subtitle {
    font-size: 1.1rem;
  }
  
  .content-grid {
    grid-template-columns: 1fr;
  }
  
  .tech-stack-card {
    grid-column: 1;
  }
  
  .contact-card {
    grid-column: 1;
  }
  
  .tech-categories {
    grid-template-columns: 1fr;
  }
  
  .contact-methods {
    flex-direction: column;
    gap: 10px;
  }
}
</style>