<template>
  <AppLayout>
    <template #main>
      <div class="home-page">
        <!-- Hero Section -->
        <section class="hero-section">
          <!-- Animated Background Elements -->
          <div class="hero-bg">
            <div class="hero-grid"></div>
            <div class="hero-glow hero-glow-1"></div>
            <div class="hero-glow hero-glow-2"></div>
            <div class="hero-glow hero-glow-3"></div>
          </div>

          <div class="hero-content">
            <!-- Badge -->
            <div class="hero-badge animate-fade-in">
              <span class="badge-dot"></span>
              <span>企业级 AI 提示词工程平台</span>
            </div>

            <!-- Title -->
            <h1 class="hero-title animate-fade-in-up">
              <span class="title-line">Prompt</span>
              <span class="title-line gradient-text">Flow Craft</span>
            </h1>

            <!-- Subtitle -->
            <p class="hero-subtitle animate-fade-in-up stagger-1">
              多模态提示词生成与管理解决方案<br/>
              基于四层架构模型的智能生成引擎
            </p>

            <!-- CTA Buttons -->
            <div class="hero-cta animate-fade-in-up stagger-2">
              <router-link to="/generate" class="btn-primary">
                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <polygon points="13 2 3 14 12 14 11 22 21 10 12 10 13 2"/>
                </svg>
                <span>立即开始创作</span>
              </router-link>
              <router-link to="/templates" class="btn-secondary">
                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M4 19.5A2.5 2.5 0 0 1 6.5 17H20"/>
                  <path d="M6.5 2H20v20H6.5A2.5 2.5 0 0 1 4 19.5v-15A2.5 2.5 0 0 1 6.5 2z"/>
                </svg>
                <span>探索模板库</span>
              </router-link>
            </div>

            <!-- Stats -->
            <div class="hero-stats animate-fade-in-up stagger-3">
              <div class="stat-item">
                <span class="stat-value">8+</span>
                <span class="stat-label">预设分类</span>
              </div>
              <div class="stat-divider"></div>
              <div class="stat-item">
                <span class="stat-value">4层</span>
                <span class="stat-label">架构模型</span>
              </div>
              <div class="stat-divider"></div>
              <div class="stat-item">
                <span class="stat-value">AI</span>
                <span class="stat-label">智能分类</span>
              </div>
            </div>
          </div>

          <!-- Floating Cards -->
          <div class="hero-visual">
            <div class="floating-card card-1 animate-float">
              <div class="card-icon">🎯</div>
              <span>智能生成</span>
            </div>
            <div class="floating-card card-2 animate-float" style="animation-delay: -2s;">
              <div class="card-icon">⚡</div>
              <span>高效缓存</span>
            </div>
            <div class="floating-card card-3 animate-float" style="animation-delay: -4s;">
              <div class="card-icon">🔒</div>
              <span>安全可靠</span>
            </div>
          </div>
        </section>

        <!-- Features Section -->
        <section class="features-section">
          <div class="section-header">
            <h2 class="section-title">核心能力</h2>
            <p class="section-desc">强大的功能助您高效创作优质提示词</p>
          </div>

          <div class="features-grid">
            <div class="feature-card" v-for="(feature, index) in features" :key="index">
              <div class="feature-icon" :style="{ background: feature.iconBg }">
                <span v-html="feature.icon"></span>
              </div>
              <h3 class="feature-title">{{ feature.title }}</h3>
              <p class="feature-desc">{{ feature.desc }}</p>
              <div class="feature-tag">{{ feature.tag }}</div>
            </div>
          </div>
        </section>

        <!-- Categories Section -->
        <section class="categories-section">
          <div class="section-header">
            <h2 class="section-title">应用场景</h2>
            <p class="section-desc">覆盖 8 大常用场景，快速定位所需提示词</p>
          </div>

          <div class="categories-grid">
            <router-link
              v-for="(cat, index) in categories"
              :key="cat.name"
              to="/templates"
              class="category-card"
              :style="{ animationDelay: `${index * 0.05}s` }"
            >
              <div class="category-icon">{{ cat.icon }}</div>
              <div class="category-info">
                <span class="category-name">{{ cat.name }}</span>
                <span class="category-desc">{{ cat.desc }}</span>
              </div>
              <svg class="category-arrow" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M5 12h14M12 5l7 7-7 7"/>
              </svg>
            </router-link>
          </div>
        </section>

        <!-- Architecture Section -->
        <section class="architecture-section">
          <div class="section-header">
            <h2 class="section-title">四层架构模型</h2>
            <p class="section-desc">结构化的提示词生成方法论</p>
          </div>

          <div class="architecture-flow">
            <div class="flow-step" v-for="(step, index) in architecture" :key="step.name">
              <div class="step-number">{{ index + 1 }}</div>
              <div class="step-content">
                <h4>{{ step.name }}</h4>
                <p>{{ step.desc }}</p>
              </div>
              <svg v-if="index < architecture.length - 1" class="flow-arrow" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M5 12h14M12 5l7 7-7 7"/>
              </svg>
            </div>
          </div>
        </section>

        <!-- Tech Stack Section -->
        <section class="tech-section">
          <div class="section-header">
            <h2 class="section-title">技术栈</h2>
            <p class="section-desc">现代化技术架构，保障系统稳定高效</p>
          </div>

          <div class="tech-grid">
            <div class="tech-card" v-for="tech in techStack" :key="tech.name">
              <div class="tech-icon" :style="{ background: tech.bg }">{{ tech.icon }}</div>
              <div class="tech-info">
                <span class="tech-name">{{ tech.name }}</span>
                <span class="tech-items">{{ tech.items }}</span>
              </div>
            </div>
          </div>
        </section>

        <!-- CTA Section -->
        <section class="cta-section">
          <div class="cta-content">
            <h2>准备好提升你的 AI 创作效率了吗？</h2>
            <p>加入 thousands of 用户，开始使用 Prompt Flow Craft</p>
            <router-link to="/generate" class="btn-cta">
              <span>开始使用</span>
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M5 12h14M12 5l7 7-7 7"/>
              </svg>
            </router-link>
          </div>
        </section>

        <!-- Footer -->
        <footer class="footer">
          <p>Prompt Flow Craft v2.0</p>
          <p>让 AI 创作更简单、更高效</p>
        </footer>
      </div>
    </template>
  </AppLayout>
</template>

<script setup lang="ts">
import AppLayout from '../components/layout/AppLayout.vue'

const features = [
  {
    icon: '<svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"/><path d="M12 16v-4M12 8h.01"/></svg>',
    iconBg: 'linear-gradient(135deg, rgba(59, 130, 246, 0.15), rgba(59, 130, 246, 0.05))',
    title: '智能生成',
    desc: '基于四层架构模型，自动生成高质量、结构化的提示词',
    tag: 'AI Powered'
  },
  {
    icon: '<svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="2" y="3" width="20" height="14" rx="2"/><path d="M8 21h8M12 17v4"/></svg>',
    iconBg: 'linear-gradient(135deg, rgba(139, 92, 246, 0.15), rgba(139, 92, 246, 0.05))',
    title: '多维配置',
    desc: '支持目标受众、输出格式、语调风格、内容长度等多维度定制',
    tag: 'Flexible'
  },
  {
    icon: '<svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M12 2L2 7l10 5 10-5-10-5z"/><path d="M2 17l10 5 10-5M2 12l10 5 10-5"/></svg>',
    iconBg: 'linear-gradient(135deg, rgba(16, 185, 129, 0.15), rgba(16, 185, 129, 0.05))',
    title: '高效缓存',
    desc: '哈希算法精确匹配，相同参数自动缓存，节省 API 成本',
    tag: 'Cost Efficient'
  },
  {
    icon: '<svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z"/></svg>',
    iconBg: 'linear-gradient(135deg, rgba(239, 68, 68, 0.15), rgba(239, 68, 68, 0.05))',
    title: '历史管理',
    desc: '永久保存所有提示词，支持搜索、筛选、复用',
    tag: 'Persistent'
  },
  {
    icon: '<svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"/><polyline points="22 4 12 14.01 9 11.01"/></svg>',
    iconBg: 'linear-gradient(135deg, rgba(245, 158, 11, 0.15), rgba(245, 158, 11, 0.05))',
    title: '智能分类',
    desc: '规则匹配 + AI 智能分类，8 大预设分类自动打标签',
    tag: 'Auto-tag'
  },
  {
    icon: '<svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/><path d="M23 21v-2a4 4 0 0 0-3-3.87M16 3.13a4 4 0 0 1 0 7.75"/></svg>',
    iconBg: 'linear-gradient(135deg, rgba(14, 165, 233, 0.15), rgba(14, 165, 233, 0.05))',
    title: '团队协作',
    desc: '提示词收藏分组、一键分享复用、使用统计与效果追踪',
    tag: 'Collab'
  }
]

const categories = [
  { icon: '💼', name: '办公效率', desc: '会议纪要、邮件撰写' },
  { icon: '📝', name: '内容创作', desc: '文章写作、文案策划' },
  { icon: '💻', name: '代码开发', desc: '代码生成、代码审查' },
  { icon: '📚', name: '教育培训', desc: '学习大纲、试题生成' },
  { icon: '🎬', name: '视频媒体', desc: '脚本创作、创意策划' },
  { icon: '📊', name: '数据分析', desc: '报告生成、图表解读' },
  { icon: '🎨', name: '设计创意', desc: 'UI设计、Logo创意' },
  { icon: '💬', name: '智能对话', desc: '客服话术、聊天机器人' }
]

const architecture = [
  { name: '核心定义层', desc: '明确任务目标与核心概念' },
  { name: '交互接口层', desc: '设计用户交互方式与格式' },
  { name: '内部处理层', desc: '规定处理逻辑与步骤' },
  { name: '全局约束层', desc: '设定边界条件与限制' }
]

const techStack = [
  { icon: '🎨', name: '前端技术', items: 'Vue 3 + TypeScript + Vite', bg: 'linear-gradient(135deg, #42b883 0%, #347474 100%)' },
  { icon: '⚙️', name: '后端技术', items: 'Java 17 + Spring Boot 3.2', bg: 'linear-gradient(135deg, #6db33f 0%, #4a7c23 100%)' },
  { icon: '🤖', name: 'AI 能力', items: 'LLM + Prompt Engineering', bg: 'linear-gradient(135deg, #8b5cf6 0%, #6d28d9 100%)' },
  { icon: '💾', name: '数据存储', items: 'MySQL 8.0 + Hibernate 6.4', bg: 'linear-gradient(135deg, #00758f 0%, #004d5a 100%)' }
]
</script>

<style scoped>
/* ============================================
   Home Page Container
   ============================================ */
.home-page {
  min-height: 100vh;
  background: var(--bg-primary);
}

/* ============================================
   Hero Section
   ============================================ */
.hero-section {
  position: relative;
  min-height: 90vh;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 80px 60px;
  overflow: hidden;
}

/* Hero Background */
.hero-bg {
  position: absolute;
  inset: 0;
  z-index: 0;
}

.hero-grid {
  position: absolute;
  inset: 0;
  background-image:
    linear-gradient(rgba(59, 130, 246, 0.03) 1px, transparent 1px),
    linear-gradient(90deg, rgba(59, 130, 246, 0.03) 1px, transparent 1px);
  background-size: 60px 60px;
  mask-image: radial-gradient(ellipse at center, black 0%, transparent 70%);
}

.hero-glow {
  position: absolute;
  border-radius: 50%;
  filter: blur(100px);
  opacity: 0.6;
  animation: pulseGlow 8s ease-in-out infinite;
}

.hero-glow-1 {
  width: 600px;
  height: 600px;
  background: radial-gradient(circle, rgba(59, 130, 246, 0.3) 0%, transparent 70%);
  top: -200px;
  right: -100px;
}

.hero-glow-2 {
  width: 400px;
  height: 400px;
  background: radial-gradient(circle, rgba(139, 92, 246, 0.2) 0%, transparent 70%);
  bottom: -100px;
  left: 10%;
  animation-delay: -3s;
}

.hero-glow-3 {
  width: 300px;
  height: 300px;
  background: radial-gradient(circle, rgba(34, 211, 238, 0.15) 0%, transparent 70%);
  top: 40%;
  left: 40%;
  animation-delay: -6s;
}

@keyframes pulseGlow {
  0%, 100% { opacity: 0.4; transform: scale(1); }
  50% { opacity: 0.7; transform: scale(1.1); }
}

/* Hero Content */
.hero-content {
  position: relative;
  z-index: 1;
  max-width: 600px;
}

.hero-badge {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 8px 16px;
  background: var(--bg-panel);
  backdrop-filter: blur(10px);
  border: 1px solid var(--border-color);
  border-radius: var(--radius-full);
  font-size: var(--text-sm);
  font-weight: 500;
  color: var(--text-secondary);
  margin-bottom: 24px;
  animation: fadeIn 0.6s ease-out;
}

.badge-dot {
  width: 8px;
  height: 8px;
  background: var(--color-success);
  border-radius: 50%;
  animation: pulse 2s ease-in-out infinite;
}

.hero-title {
  font-family: var(--font-display);
  font-size: clamp(3rem, 6vw, 5rem);
  font-weight: 900;
  line-height: 1;
  margin-bottom: 24px;
  letter-spacing: -0.03em;
}

.title-line {
  display: block;
}

.gradient-text {
  background: linear-gradient(135deg, var(--color-primary-500) 0%, var(--color-primary-400) 50%, var(--color-primary-600) 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.hero-subtitle {
  font-size: 1.25rem;
  line-height: 1.7;
  color: var(--text-secondary);
  margin-bottom: 40px;
  animation: fadeInUp 0.6s ease-out 0.1s backwards;
}

.hero-cta {
  display: flex;
  gap: 16px;
  margin-bottom: 48px;
  animation: fadeInUp 0.6s ease-out 0.2s backwards;
}

.btn-primary {
  display: inline-flex;
  align-items: center;
  gap: 10px;
  padding: 16px 32px;
  background: linear-gradient(135deg, var(--color-primary-600), var(--color-primary-700));
  color: white;
  font-family: var(--font-body);
  font-size: var(--text-base);
  font-weight: 600;
  border-radius: var(--radius-xl);
  text-decoration: none;
  box-shadow: 0 8px 32px rgba(59, 130, 246, 0.35);
  transition: all var(--transition-base);
  position: relative;
  overflow: hidden;
}

.btn-primary::before {
  content: '';
  position: absolute;
  inset: 0;
  background: linear-gradient(135deg, rgba(255,255,255,0.1), transparent);
  opacity: 0;
  transition: opacity var(--transition-base);
}

.btn-primary:hover {
  transform: translateY(-3px);
  box-shadow: 0 12px 40px rgba(59, 130, 246, 0.45);
}

.btn-primary:hover::before {
  opacity: 1;
}

.btn-secondary {
  display: inline-flex;
  align-items: center;
  gap: 10px;
  padding: 16px 32px;
  background: var(--bg-panel);
  backdrop-filter: blur(10px);
  color: var(--text-primary);
  font-family: var(--font-body);
  font-size: var(--text-base);
  font-weight: 600;
  border-radius: var(--radius-xl);
  text-decoration: none;
  border: 1px solid var(--border-color);
  transition: all var(--transition-base);
}

.btn-secondary:hover {
  background: var(--bg-card);
  border-color: var(--border-hover);
  transform: translateY(-3px);
}

/* Hero Stats */
.hero-stats {
  display: flex;
  align-items: center;
  gap: 24px;
  animation: fadeInUp 0.6s ease-out 0.3s backwards;
}

.stat-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.stat-value {
  font-family: var(--font-display);
  font-size: 1.75rem;
  font-weight: 800;
  background: linear-gradient(135deg, var(--color-primary-600), var(--color-primary-400));
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.stat-label {
  font-size: var(--text-sm);
  color: var(--text-muted);
  font-weight: 500;
}

.stat-divider {
  width: 1px;
  height: 40px;
  background: var(--border-color);
}

/* Floating Cards */
.hero-visual {
  position: relative;
  width: 400px;
  height: 400px;
  z-index: 1;
}

.floating-card {
  position: absolute;
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px 24px;
  background: var(--bg-panel);
  backdrop-filter: blur(16px);
  border: 1px solid var(--border-color);
  border-radius: var(--radius-xl);
  box-shadow: var(--shadow-lg);
  font-weight: 600;
  color: var(--text-primary);
  animation: floatCard 6s ease-in-out infinite;
}

.card-icon {
  font-size: 1.5rem;
}

.card-1 {
  top: 20%;
  left: 0;
  animation-delay: 0s;
}

.card-2 {
  top: 50%;
  right: 0;
  animation-delay: -2s;
}

.card-3 {
  bottom: 20%;
  left: 10%;
  animation-delay: -4s;
}

@keyframes floatCard {
  0%, 100% { transform: translateY(0) rotate(0deg); }
  50% { transform: translateY(-15px) rotate(2deg); }
}

/* ============================================
   Section Styles
   ============================================ */
.features-section,
.categories-section,
.architecture-section,
.tech-section,
.cta-section {
  padding: 100px 60px;
  max-width: 1400px;
  margin: 0 auto;
}

.section-header {
  text-align: center;
  margin-bottom: 60px;
}

.section-title {
  font-family: var(--font-display);
  font-size: 2.5rem;
  font-weight: 800;
  color: var(--text-primary);
  margin-bottom: 16px;
  letter-spacing: -0.02em;
}

.section-desc {
  font-size: 1.125rem;
  color: var(--text-secondary);
}

/* ============================================
   Features Grid
   ============================================ */
.features-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 24px;
}

.feature-card {
  padding: 32px;
  background: var(--bg-card);
  border: 1px solid var(--border-color);
  border-radius: var(--radius-2xl);
  transition: all var(--transition-base);
  position: relative;
  overflow: hidden;
}

.feature-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 3px;
  background: linear-gradient(90deg, var(--color-primary-500), var(--color-primary-400));
  opacity: 0;
  transition: opacity var(--transition-base);
}

.feature-card:hover {
  transform: translateY(-6px);
  border-color: var(--glow-primary);
  box-shadow: var(--shadow-card-hover);
}

.feature-card:hover::before {
  opacity: 1;
}

.feature-icon {
  width: 56px;
  height: 56px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: var(--radius-lg);
  margin-bottom: 20px;
}

.feature-icon svg {
  color: var(--color-primary-500);
}

.feature-title {
  font-family: var(--font-display);
  font-size: 1.25rem;
  font-weight: 700;
  color: var(--text-primary);
  margin-bottom: 12px;
}

.feature-desc {
  font-size: var(--text-sm);
  color: var(--text-secondary);
  line-height: 1.7;
  margin-bottom: 16px;
}

.feature-tag {
  display: inline-block;
  padding: 4px 12px;
  background: var(--glow-primary-soft);
  color: var(--color-primary-600);
  font-size: var(--text-xs);
  font-weight: 600;
  border-radius: var(--radius-full);
}

/* ============================================
   Categories Grid
   ============================================ */
.categories-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
}

.category-card {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px 24px;
  background: var(--bg-card);
  border: 1px solid var(--border-color);
  border-radius: var(--radius-xl);
  text-decoration: none;
  transition: all var(--transition-base);
  animation: fadeInUp 0.5s ease-out backwards;
}

.category-card:hover {
  transform: translateX(8px);
  border-color: var(--glow-primary);
  background: var(--bg-panel);
}

.category-icon {
  font-size: 2rem;
  flex-shrink: 0;
}

.category-info {
  flex: 1;
  min-width: 0;
}

.category-name {
  display: block;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 4px;
}

.category-desc {
  display: block;
  font-size: var(--text-xs);
  color: var(--text-muted);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.category-arrow {
  color: var(--text-muted);
  flex-shrink: 0;
  opacity: 0;
  transform: translateX(-8px);
  transition: all var(--transition-base);
}

.category-card:hover .category-arrow {
  opacity: 1;
  transform: translateX(0);
}

/* ============================================
   Architecture Flow
   ============================================ */
.architecture-flow {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 16px;
  flex-wrap: wrap;
}

.flow-step {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 24px 32px;
  background: var(--bg-card);
  border: 1px solid var(--border-color);
  border-radius: var(--radius-2xl);
  transition: all var(--transition-base);
}

.flow-step:hover {
  transform: translateY(-4px);
  border-color: var(--glow-primary);
  box-shadow: var(--shadow-card-hover);
}

.step-number {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, var(--color-primary-600), var(--color-primary-700));
  color: white;
  font-family: var(--font-display);
  font-weight: 800;
  font-size: 1.25rem;
  border-radius: var(--radius-lg);
}

.step-content h4 {
  font-family: var(--font-display);
  font-weight: 700;
  color: var(--text-primary);
  margin-bottom: 4px;
}

.step-content p {
  font-size: var(--text-sm);
  color: var(--text-muted);
}

.flow-arrow {
  color: var(--color-primary-400);
  flex-shrink: 0;
}

/* ============================================
   Tech Grid
   ============================================ */
.tech-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 24px;
}

.tech-card {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 24px;
  background: var(--bg-card);
  border: 1px solid var(--border-color);
  border-radius: var(--radius-xl);
  transition: all var(--transition-base);
}

.tech-card:hover {
  transform: translateY(-4px);
  border-color: var(--border-hover);
  box-shadow: var(--shadow-card);
}

.tech-icon {
  width: 48px;
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.5rem;
  border-radius: var(--radius-lg);
  flex-shrink: 0;
}

.tech-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.tech-name {
  font-weight: 600;
  color: var(--text-primary);
}

.tech-items {
  font-size: var(--text-xs);
  color: var(--text-muted);
}

/* ============================================
   CTA Section
   ============================================ */
.cta-section {
  text-align: center;
  padding: 120px 60px;
  background: linear-gradient(180deg, var(--bg-primary) 0%, var(--bg-secondary) 100%);
  position: relative;
  overflow: hidden;
}

.cta-section::before {
  content: '';
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 800px;
  height: 400px;
  background: radial-gradient(ellipse, var(--glow-primary-soft) 0%, transparent 70%);
  pointer-events: none;
}

.cta-content {
  position: relative;
  z-index: 1;
}

.cta-content h2 {
  font-family: var(--font-display);
  font-size: 2.5rem;
  font-weight: 800;
  color: var(--text-primary);
  margin-bottom: 16px;
}

.cta-content p {
  font-size: 1.125rem;
  color: var(--text-secondary);
  margin-bottom: 40px;
}

.btn-cta {
  display: inline-flex;
  align-items: center;
  gap: 12px;
  padding: 18px 40px;
  background: linear-gradient(135deg, var(--color-primary-600), var(--color-primary-700));
  color: white;
  font-family: var(--font-body);
  font-size: var(--text-lg);
  font-weight: 700;
  border-radius: var(--radius-full);
  text-decoration: none;
  box-shadow: 0 12px 40px rgba(59, 130, 246, 0.4);
  transition: all var(--transition-base);
}

.btn-cta:hover {
  transform: translateY(-4px);
  box-shadow: 0 16px 48px rgba(59, 130, 246, 0.5);
}

/* ============================================
   Footer
   ============================================ */
.footer {
  text-align: center;
  padding: 40px;
  border-top: 1px solid var(--border-color);
}

.footer p {
  font-size: var(--text-sm);
  color: var(--text-muted);
}

.footer p:first-child {
  font-weight: 600;
  color: var(--text-secondary);
  margin-bottom: 8px;
}

/* ============================================
   Animations
   ============================================ */
@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes float {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-10px); }
}

@keyframes pulse {
  0%, 100% { opacity: 1; transform: scale(1); }
  50% { opacity: 0.6; transform: scale(1.1); }
}

.animate-fade-in {
  animation: fadeIn 0.6s ease-out;
}

.animate-fade-in-up {
  animation: fadeInUp 0.6s ease-out backwards;
}

.animate-float {
  animation: float 6s ease-in-out infinite;
}

.stagger-1 { animation-delay: 0.1s; }
.stagger-2 { animation-delay: 0.2s; }
.stagger-3 { animation-delay: 0.3s; }

/* ============================================
   Responsive
   ============================================ */
@media (max-width: 1200px) {
  .hero-visual {
    display: none;
  }

  .features-grid,
  .tech-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .categories-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .hero-section {
    padding: 60px 24px;
    min-height: auto;
  }

  .hero-title {
    font-size: 2.5rem;
  }

  .hero-subtitle {
    font-size: 1rem;
  }

  .hero-cta {
    flex-direction: column;
  }

  .btn-primary,
  .btn-secondary {
    width: 100%;
    justify-content: center;
  }

  .hero-stats {
    flex-wrap: wrap;
    gap: 16px;
  }

  .stat-divider {
    display: none;
  }

  .features-section,
  .categories-section,
  .architecture-section,
  .tech-section,
  .cta-section {
    padding: 60px 24px;
  }

  .section-title {
    font-size: 2rem;
  }

  .features-grid,
  .categories-grid,
  .tech-grid {
    grid-template-columns: 1fr;
  }

  .architecture-flow {
    flex-direction: column;
  }

  .flow-arrow {
    transform: rotate(90deg);
  }
}
</style>
