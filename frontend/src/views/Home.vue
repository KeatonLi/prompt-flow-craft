<template>
  <AppLayout>
    <template #main>
      <div class="home-page">

        <!-- Hero Section - Compact & Informative -->
        <section class="hero-section">
          <div class="hero-content">
            <div class="hero-badge">
              <span class="badge-dot"></span>
              <span>v2.0 全新发布</span>
            </div>

            <h1 class="hero-title">
              <span class="title-main">Prompt Flow Craft</span>
            </h1>

            <p class="hero-desc">
              <span class="highlight">企业级 AI 提示词工程平台</span><br/>
              通过四层架构模型，智能生成、优化、管理高质量提示词
            </p>

            <div class="hero-actions">
              <router-link to="/generate" class="btn-primary">
                <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <polygon points="13 2 3 14 12 14 11 22 21 10 12 10 13 2"/>
                </svg>
                开始创作
              </router-link>
              <router-link to="/templates" class="btn-secondary">
                查看模板
              </router-link>
            </div>

            <div class="hero-quick-stats">
              <div class="quick-stat">
                <span class="stat-num">8</span>
                <span class="stat-label">场景分类</span>
              </div>
              <div class="quick-stat">
                <span class="stat-num">4层</span>
                <span class="stat-label">架构模型</span>
              </div>
              <div class="quick-stat">
                <span class="stat-num">API</span>
                <span class="stat-label">成本节省</span>
              </div>
            </div>
          </div>

          <!-- Quick Demo Card -->
          <div class="hero-demo">
            <div class="demo-card">
              <div class="demo-header">
                <span class="demo-dot red"></span>
                <span class="demo-dot yellow"></span>
                <span class="demo-dot green"></span>
                <span class="demo-title">提示词生成示例</span>
              </div>
              <div class="demo-body">
                <div class="demo-input">
                  <span class="demo-label">输入</span>
                  <div class="demo-text">帮我写一封商务合作邮件，目标受众是企业高管，语气专业</div>
                </div>
                <div class="demo-arrow">
                  <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <path d="M12 5v14M5 12l7 7 7-7"/>
                  </svg>
                  <span>AI 智能生成</span>
                </div>
                <div class="demo-output">
                  <span class="demo-label">输出</span>
                  <div class="demo-result">
                    <div class="result-line">Subject: 战略合作提案 - [公司名称]</div>
                    <div class="result-line">Dear [CEO姓名]:</div>
                    <div class="result-line">我代表[公司]...</div>
                    <div class="result-tag">专业商务邮件 · 500字 · 正式语气</div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </section>

        <!-- Problem & Solution Section -->
        <section class="info-section">
          <div class="info-grid">
            <div class="info-card problem">
              <div class="info-icon">
                <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <circle cx="12" cy="12" r="10"/>
                  <path d="M12 8v4M12 16h.01"/>
                </svg>
              </div>
              <h3>痛点</h3>
              <ul>
                <li>写提示词费时费力，效果不稳定</li>
                <li>生成内容格式混乱，质量参差不齐</li>
                <li>重复调用 API，成本居高不下</li>
                <li>提示词分散，难以复用和管理</li>
              </ul>
            </div>

            <div class="info-card solution">
              <div class="info-icon">
                <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"/>
                  <polyline points="22 4 12 14.01 9 11.01"/>
                </svg>
              </div>
              <h3>解决方案</h3>
              <ul>
                <li>四层架构模型，结构化生成优质提示词</li>
                <li>多维度配置，输出格式高度统一</li>
                <li>智能缓存，相同参数零成本复用</li>
                <li>历史记录永久保存，一键搜索复用</li>
              </ul>
            </div>

            <div class="info-card features">
              <div class="info-icon">
                <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <polygon points="13 2 3 14 12 14 11 22 21 10 12 10 13 2"/>
                </svg>
              </div>
              <h3>核心功能</h3>
              <ul>
                <li>智能生成 - AI 自动构建最优提示词</li>
                <li>质量分析 - 多维度评估提示词效果</li>
                <li>自动分类 - 8 大场景智能打标签</li>
                <li>团队协作 - 收藏分享使用追踪</li>
              </ul>
            </div>
          </div>
        </section>

        <!-- Architecture Section -->
        <section class="arch-section">
          <div class="section-header">
            <h2>四层架构模型</h2>
            <p>结构化的提示词生成方法论，确保输出质量稳定可控</p>
          </div>

          <div class="arch-flow">
            <div class="arch-step" v-for="(step, i) in architecture" :key="i">
              <div class="arch-num">{{ i + 1 }}</div>
              <div class="arch-content">
                <h4>{{ step.name }}</h4>
                <p>{{ step.desc }}</p>
              </div>
            </div>
          </div>
        </section>

        <!-- Categories Section -->
        <section class="cats-section">
          <div class="section-header">
            <h2>应用场景</h2>
            <p>覆盖 8 大常用场景，快速定位所需提示词</p>
          </div>

          <div class="cats-grid">
            <router-link v-for="cat in categories" :key="cat.name" to="/templates" class="cat-card">
              <span class="cat-icon">{{ cat.icon }}</span>
              <span class="cat-name">{{ cat.name }}</span>
              <span class="cat-desc">{{ cat.desc }}</span>
            </router-link>
          </div>
        </section>

        <!-- Tech Stack -->
        <section class="tech-section">
          <div class="tech-grid">
            <div class="tech-item" v-for="tech in techStack" :key="tech.name">
              <span class="tech-icon">{{ tech.icon }}</span>
              <div class="tech-info">
                <span class="tech-name">{{ tech.name }}</span>
                <span class="tech-value">{{ tech.value }}</span>
              </div>
            </div>
          </div>
        </section>

        <!-- CTA Section -->
        <section class="cta-section">
          <div class="cta-box">
            <h2>提升 AI 创作效率</h2>
            <p>免费开始使用，永久保存历史记录</p>
            <router-link to="/generate" class="btn-cta">
              立即开始创作
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M5 12h14M12 5l7 7-7 7"/>
              </svg>
            </router-link>
          </div>
        </section>

        <!-- Footer -->
        <footer class="footer">
          <span>Prompt Flow Craft v2.0</span>
          <span>让 AI 创作更简单、更高效</span>
        </footer>
      </div>
    </template>
  </AppLayout>
</template>

<script setup lang="ts">
import AppLayout from '../components/layout/AppLayout.vue'

const architecture = [
  { name: '核心定义层', desc: '明确任务目标与核心概念' },
  { name: '交互接口层', desc: '设计用户交互方式与格式' },
  { name: '内部处理层', desc: '规定处理逻辑与步骤' },
  { name: '全局约束层', desc: '设定边界条件与限制' }
]

const categories = [
  { icon: '💼', name: '办公效率', desc: '邮件、会议纪要' },
  { icon: '📝', name: '内容创作', desc: '文章、文案策划' },
  { icon: '💻', name: '代码开发', desc: '生成、审查代码' },
  { icon: '📚', name: '教育培训', desc: '学习大纲、试题' },
  { icon: '🎬', name: '视频媒体', desc: '脚本、创意策划' },
  { icon: '📊', name: '数据分析', desc: '报告、图表解读' },
  { icon: '🎨', name: '设计创意', desc: 'UI、Logo创意' },
  { icon: '💬', name: '智能对话', desc: '客服、聊天机器人' }
]

const techStack = [
  { icon: '🎨', name: '前端', value: 'Vue 3 + TS + Vite' },
  { icon: '⚙️', name: '后端', value: 'Java 17 + Spring Boot' },
  { icon: '🤖', name: 'AI', value: 'LLM + Prompt Engineering' },
  { icon: '💾', name: '存储', value: 'MySQL 8 + Hibernate' }
]
</script>

<style scoped>
.home-page {
  min-height: 100vh;
  background: var(--bg-primary);
}

/* ============================================
   Hero Section
   ============================================ */
.hero-section {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 48px;
  padding: 48px 60px;
  max-width: 1400px;
  margin: 0 auto;
}

.hero-content {
  flex: 1;
  max-width: 520px;
}

.hero-badge {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 6px 14px;
  background: var(--bg-panel);
  border: 1px solid var(--border-color);
  border-radius: var(--radius-full);
  font-size: var(--text-xs);
  font-weight: 600;
  color: var(--text-secondary);
  margin-bottom: 16px;
}

.badge-dot {
  width: 6px;
  height: 6px;
  background: var(--color-success);
  border-radius: 50%;
  animation: pulse 2s infinite;
}

.hero-title {
  font-family: var(--font-display);
  font-size: 2.5rem;
  font-weight: 900;
  line-height: 1.1;
  margin-bottom: 16px;
  letter-spacing: -0.02em;
}

.title-main {
  background: linear-gradient(135deg, var(--color-primary-500), var(--color-primary-600), var(--color-primary-400));
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.hero-desc {
  font-size: 1rem;
  line-height: 1.7;
  color: var(--text-secondary);
  margin-bottom: 24px;
}

.highlight {
  color: var(--text-primary);
  font-weight: 600;
}

.hero-actions {
  display: flex;
  gap: 12px;
  margin-bottom: 28px;
}

.btn-primary {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 12px 24px;
  background: linear-gradient(135deg, var(--color-primary-600), var(--color-primary-700));
  color: white;
  font-size: var(--text-sm);
  font-weight: 600;
  border-radius: var(--radius-lg);
  text-decoration: none;
  transition: all var(--transition-base);
}

.btn-primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(59, 130, 246, 0.35);
}

.btn-secondary {
  display: inline-flex;
  align-items: center;
  padding: 12px 24px;
  background: var(--bg-panel);
  color: var(--text-primary);
  font-size: var(--text-sm);
  font-weight: 600;
  border-radius: var(--radius-lg);
  text-decoration: none;
  border: 1px solid var(--border-color);
  transition: all var(--transition-base);
}

.btn-secondary:hover {
  background: var(--bg-card);
  border-color: var(--border-hover);
}

.hero-quick-stats {
  display: flex;
  gap: 24px;
}

.quick-stat {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.stat-num {
  font-family: var(--font-display);
  font-size: 1.5rem;
  font-weight: 800;
  background: linear-gradient(135deg, var(--color-primary-500), var(--color-primary-600));
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.stat-label {
  font-size: var(--text-xs);
  color: var(--text-muted);
  font-weight: 500;
}

/* Demo Card */
.hero-demo {
  flex-shrink: 0;
  width: 420px;
}

.demo-card {
  background: var(--bg-panel);
  border: 1px solid var(--border-color);
  border-radius: var(--radius-2xl);
  overflow: hidden;
  box-shadow: var(--shadow-lg);
}

.demo-header {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 16px;
  background: var(--bg-card);
  border-bottom: 1px solid var(--border-color);
}

.demo-dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
}

.demo-dot.red { background: #ff5f57; }
.demo-dot.yellow { background: #febc2e; }
.demo-dot.green { background: #28c840; }

.demo-title {
  margin-left: 8px;
  font-size: var(--text-xs);
  font-weight: 600;
  color: var(--text-muted);
}

.demo-body {
  padding: 20px;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.demo-label {
  display: block;
  font-size: var(--text-xs);
  font-weight: 600;
  color: var(--text-muted);
  margin-bottom: 8px;
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.demo-text {
  padding: 12px 14px;
  background: var(--bg-card);
  border: 1px solid var(--border-color);
  border-radius: var(--radius-md);
  font-size: var(--text-sm);
  color: var(--text-secondary);
  line-height: 1.6;
}

.demo-arrow {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6px;
  color: var(--color-primary-500);
}

.demo-arrow span {
  font-size: var(--text-xs);
  font-weight: 600;
  color: var(--color-primary-600);
}

.demo-result {
  padding: 14px;
  background: linear-gradient(135deg, rgba(59, 130, 246, 0.08), rgba(99, 102, 241, 0.05));
  border: 1px solid var(--color-primary-500);
  border-radius: var(--radius-md);
}

.result-line {
  font-size: var(--text-xs);
  color: var(--text-primary);
  line-height: 1.8;
  font-family: var(--font-mono);
}

.result-tag {
  display: inline-block;
  margin-top: 10px;
  padding: 4px 10px;
  background: var(--color-primary-500);
  color: white;
  font-size: 10px;
  font-weight: 600;
  border-radius: var(--radius-full);
}

/* ============================================
   Info Section
   ============================================ */
.info-section {
  padding: 48px 60px;
  max-width: 1400px;
  margin: 0 auto;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
}

.info-card {
  padding: 24px;
  background: var(--bg-card);
  border: 1px solid var(--border-color);
  border-radius: var(--radius-xl);
}

.info-card h3 {
  font-family: var(--font-display);
  font-size: 1rem;
  font-weight: 700;
  margin-bottom: 16px;
  color: var(--text-primary);
}

.info-card ul {
  list-style: none;
  padding: 0;
  margin: 0;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.info-card li {
  font-size: var(--text-sm);
  color: var(--text-secondary);
  padding-left: 16px;
  position: relative;
  line-height: 1.5;
}

.info-card li::before {
  content: '';
  position: absolute;
  left: 0;
  top: 8px;
  width: 4px;
  height: 4px;
  border-radius: 50%;
  background: var(--color-primary-500);
}

.info-icon {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--glow-primary-soft);
  border-radius: var(--radius-lg);
  color: var(--color-primary-600);
  margin-bottom: 16px;
}

/* ============================================
   Section Header
   ============================================ */
.section-header {
  margin-bottom: 32px;
}

.section-header h2 {
  font-family: var(--font-display);
  font-size: 1.75rem;
  font-weight: 800;
  color: var(--text-primary);
  margin-bottom: 8px;
}

.section-header p {
  font-size: var(--text-sm);
  color: var(--text-secondary);
}

/* ============================================
   Architecture Section
   ============================================ */
.arch-section {
  padding: 48px 60px;
  max-width: 1400px;
  margin: 0 auto;
}

.arch-flow {
  display: flex;
  gap: 16px;
}

.arch-step {
  flex: 1;
  display: flex;
  align-items: flex-start;
  gap: 14px;
  padding: 20px;
  background: var(--bg-card);
  border: 1px solid var(--border-color);
  border-radius: var(--radius-xl);
  transition: all var(--transition-base);
}

.arch-step:hover {
  border-color: var(--color-primary-500);
  transform: translateY(-2px);
}

.arch-num {
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, var(--color-primary-600), var(--color-primary-700));
  color: white;
  font-family: var(--font-display);
  font-weight: 800;
  font-size: 0.875rem;
  border-radius: var(--radius-md);
  flex-shrink: 0;
}

.arch-content h4 {
  font-size: var(--text-sm);
  font-weight: 700;
  color: var(--text-primary);
  margin-bottom: 4px;
}

.arch-content p {
  font-size: var(--text-xs);
  color: var(--text-muted);
  line-height: 1.5;
}

/* ============================================
   Categories Section
   ============================================ */
.cats-section {
  padding: 48px 60px;
  max-width: 1400px;
  margin: 0 auto;
}

.cats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 14px;
}

.cat-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  padding: 20px 16px;
  background: var(--bg-card);
  border: 1px solid var(--border-color);
  border-radius: var(--radius-xl);
  text-decoration: none;
  text-align: center;
  transition: all var(--transition-base);
}

.cat-card:hover {
  border-color: var(--color-primary-500);
  transform: translateY(-3px);
  box-shadow: var(--shadow-card-hover);
}

.cat-icon {
  font-size: 1.75rem;
}

.cat-name {
  font-size: var(--text-sm);
  font-weight: 700;
  color: var(--text-primary);
}

.cat-desc {
  font-size: var(--text-xs);
  color: var(--text-muted);
}

/* ============================================
   Tech Section
   ============================================ */
.tech-section {
  padding: 32px 60px 48px;
  max-width: 1400px;
  margin: 0 auto;
}

.tech-grid {
  display: flex;
  gap: 20px;
  justify-content: center;
}

.tech-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px 24px;
  background: var(--bg-card);
  border: 1px solid var(--border-color);
  border-radius: var(--radius-xl);
}

.tech-icon {
  font-size: 1.25rem;
}

.tech-info {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.tech-name {
  font-size: var(--text-xs);
  font-weight: 600;
  color: var(--text-muted);
}

.tech-value {
  font-size: var(--text-sm);
  font-weight: 600;
  color: var(--text-primary);
}

/* ============================================
   CTA Section
   ============================================ */
.cta-section {
  padding: 48px 60px 64px;
  max-width: 1400px;
  margin: 0 auto;
}

.cta-box {
  text-align: center;
  padding: 48px;
  background: linear-gradient(135deg, rgba(59, 130, 246, 0.1), rgba(99, 102, 241, 0.05));
  border: 1px solid var(--color-primary-500);
  border-radius: var(--radius-2xl);
}

.cta-box h2 {
  font-family: var(--font-display);
  font-size: 1.75rem;
  font-weight: 800;
  color: var(--text-primary);
  margin-bottom: 8px;
}

.cta-box p {
  font-size: var(--text-sm);
  color: var(--text-secondary);
  margin-bottom: 24px;
}

.btn-cta {
  display: inline-flex;
  align-items: center;
  gap: 10px;
  padding: 14px 32px;
  background: linear-gradient(135deg, var(--color-primary-600), var(--color-primary-700));
  color: white;
  font-size: var(--text-base);
  font-weight: 700;
  border-radius: var(--radius-lg);
  text-decoration: none;
  transition: all var(--transition-base);
}

.btn-cta:hover {
  transform: translateY(-2px);
  box-shadow: 0 12px 32px rgba(59, 130, 246, 0.4);
}

/* ============================================
   Footer
   ============================================ */
.footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24px 60px;
  border-top: 1px solid var(--border-color);
  font-size: var(--text-sm);
  color: var(--text-muted);
}

.footer span:first-child {
  font-weight: 600;
  color: var(--text-secondary);
}

/* ============================================
   Animations
   ============================================ */
@keyframes pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.5; }
}

/* ============================================
   Responsive
   ============================================ */
@media (max-width: 1100px) {
  .hero-section {
    flex-direction: column;
    padding: 40px 40px;
  }

  .hero-demo {
    width: 100%;
    max-width: 500px;
  }

  .info-grid {
    grid-template-columns: 1fr;
  }

  .cats-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .arch-flow {
    flex-wrap: wrap;
  }

  .arch-step {
    flex: 1 1 calc(50% - 8px);
    min-width: 200px;
  }
}

@media (max-width: 768px) {
  .hero-section,
  .info-section,
  .arch-section,
  .cats-section,
  .tech-section,
  .cta-section {
    padding-left: 24px;
    padding-right: 24px;
  }

  .footer {
    flex-direction: column;
    gap: 8px;
    text-align: center;
    padding: 24px;
  }

  .tech-grid {
    flex-wrap: wrap;
  }

  .hero-quick-stats {
    gap: 16px;
  }

  .cats-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style>
