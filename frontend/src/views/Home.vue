<template>
  <AppLayout>
    <template #main>
      <div class="home-page">

        <!-- Hero Section -->
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
              首创 Agent / Skill 双引擎架构，让 AI 工具调用如虎添翼
            </p>

            <div class="hero-actions">
              <router-link to="/generate" class="btn-primary">
                <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <polygon points="13 2 3 14 12 14 11 22 21 10 12 10 13 2"/>
                </svg>
                开始创作
              </router-link>
              <router-link to="/templates" class="btn-secondary">
                探索模板
              </router-link>
            </div>

            <div class="hero-quick-stats">
              <div class="quick-stat">
                <span class="stat-num">Agent</span>
                <span class="stat-label">智能体构建</span>
              </div>
              <div class="quick-stat">
                <span class="stat-num">Skill</span>
                <span class="stat-label">工具技能生成</span>
              </div>
              <div class="quick-stat">
                <span class="stat-num">社区</span>
                <span class="stat-label">模板分享</span>
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
                <span class="demo-title">Agent 生成示例</span>
              </div>
              <div class="demo-body">
                <div class="demo-input">
                  <span class="demo-label">输入</span>
                  <div class="demo-text">创建一个代码审查助手，自动审查 PR 并给出建议</div>
                </div>
                <div class="demo-arrow">
                  <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <path d="M12 5v14M5 12l7 7 7-7"/>
                  </svg>
                  <span>AI 结构化生成</span>
                </div>
                <div class="demo-output">
                  <span class="demo-label">输出</span>
                  <div class="demo-result">
                    <div class="result-line">Role: 代码审查助手</div>
                    <div class="result-line">Capabilities: PR审查, 漏洞检测...</div>
                    <div class="result-tag">Agent · 四层架构 · 即刻使用</div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </section>

        <!-- Agent & Skill Section -->
        <section class="feature-section">
          <div class="section-header centered">
            <h2>双引擎驱动 AI 工具调用</h2>
            <p>无论是构建智能体还是定义工具技能，我们都能满足</p>
          </div>

          <div class="feature-grid">
            <div class="feature-card agent-card">
              <div class="feature-icon">
                <svg width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <circle cx="12" cy="12" r="10"/>
                  <path d="M12 6v6l4 2"/>
                </svg>
              </div>
              <h3>Agent 引擎</h3>
              <p>构建具有角色定位、核心能力和行为规范的智能体，让 AI 扮演专家角色</p>
              <ul class="feature-list">
                <li>定义清晰的角色与目标</li>
                <li>配置核心能力与工具</li>
                <li>设定行为规范与限制</li>
                <li>选择对话风格与语气</li>
              </ul>
              <router-link to="/generate?type=agent" class="feature-link">
                创建 Agent →
              </router-link>
            </div>

            <div class="feature-card skill-card">
              <div class="feature-icon">
                <svg width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M12 2L2 7l10 5 10-5-10-5z"/>
                  <path d="M2 17l10 5 10-5"/>
                  <path d="M2 12l10 5 10-5"/>
                </svg>
              </div>
              <h3>Skill 引擎</h3>
              <p>为 AI Agent 打造可调用的工具技能，支持 API、函数、Webhook 等多种类型</p>
              <ul class="feature-list">
                <li>API / 函数 / Webhook</li>
                <li>自动生成调用参数</li>
                <li>认证与限流配置</li>
                <li>社区模板即插即用</li>
              </ul>
              <router-link to="/generate?type=skill" class="feature-link">
                创建 Skill →
              </router-link>
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

        <!-- Community Section -->
        <section class="community-section">
          <div class="community-box">
            <div class="community-content">
              <div class="community-badge">
                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/>
                  <circle cx="9" cy="7" r="4"/>
                  <path d="M23 21v-2a4 4 0 0 0-3-3.87"/>
                  <path d="M16 3.13a4 4 0 0 1 0 7.75"/>
                </svg>
                <span>社区发布</span>
              </div>
              <h2>加入社区，共享优质提示词模板</h2>
              <p>发现他人分享的 Agent 和 Skill，一键导入使用。构建自己的模板，供社区复用。</p>
              <div class="community-actions">
                <router-link to="/templates" class="btn-community-primary">
                  探索社区模板
                </router-link>
                <router-link to="/generate" class="btn-community-secondary">
                  贡献你的模板
                </router-link>
              </div>
            </div>
            <div class="community-visual">
              <div class="template-preview">
                <div class="template-card">
                  <span class="template-icon">🤖</span>
                  <span class="template-name">代码审查助手</span>
                  <span class="template-author">@li.bkai</span>
                </div>
                <div class="template-card">
                  <span class="template-icon">⚡</span>
                  <span class="template-name">get_weather</span>
                  <span class="template-author">@contributor</span>
                </div>
                <div class="template-card">
                  <span class="template-icon">📝</span>
                  <span class="template-name">技术文档写作</span>
                  <span class="template-author">@dev_team</span>
                </div>
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

        <!-- CTA Section -->
        <section class="cta-section">
          <div class="cta-box">
            <h2>开始构建你的 AI 工具库</h2>
            <p>免费开始使用，Agent + Skill 双引擎驱动</p>
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
          <span class="footer-brand" @click="toggleAdmin" title="点击开启/关闭管理员模式">
            Prompt Flow Craft v2.0
          </span>
          <span>Agent + Skill 双引擎 · 社区模板共享</span>
        </footer>
      </div>
    </template>
  </AppLayout>
</template>

<script setup lang="ts">
import AppLayout from '../components/layout/AppLayout.vue'
import { useAdmin } from '@/composables/useAdmin'

const { toggleAdmin } = useAdmin()

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
   Feature Section (Agent & Skill)
   ============================================ */
.feature-section {
  padding: 48px 60px;
  max-width: 1400px;
  margin: 0 auto;
}

.section-header {
  margin-bottom: 32px;
}

.section-header.centered {
  text-align: center;
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

.feature-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 24px;
}

.feature-card {
  padding: 32px;
  background: var(--bg-card);
  border: 1px solid var(--border-color);
  border-radius: var(--radius-2xl);
  transition: all var(--transition-base);
}

.feature-card:hover {
  border-color: var(--color-primary-400);
  transform: translateY(-4px);
  box-shadow: var(--shadow-card-hover);
}

.agent-card {
  border-top: 3px solid var(--color-primary-500);
}

.skill-card {
  border-top: 3px solid var(--color-accent);
}

.feature-icon {
  width: 56px;
  height: 56px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--glow-primary-soft);
  border-radius: var(--radius-xl);
  color: var(--color-primary-600);
  margin-bottom: 20px;
}

.skill-card .feature-icon {
  background: linear-gradient(135deg, rgba(139, 92, 246, 0.15), rgba(99, 102, 241, 0.1));
  color: var(--color-accent);
}

.feature-card h3 {
  font-family: var(--font-display);
  font-size: 1.25rem;
  font-weight: 800;
  color: var(--text-primary);
  margin-bottom: 12px;
}

.feature-card > p {
  font-size: var(--text-sm);
  color: var(--text-secondary);
  line-height: 1.7;
  margin-bottom: 20px;
}

.feature-list {
  list-style: none;
  padding: 0;
  margin: 0 0 24px 0;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.feature-list li {
  font-size: var(--text-sm);
  color: var(--text-secondary);
  padding-left: 20px;
  position: relative;
}

.feature-list li::before {
  content: '✓';
  position: absolute;
  left: 0;
  color: var(--color-primary-500);
  font-weight: 700;
}

.feature-link {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  font-size: var(--text-sm);
  font-weight: 700;
  color: var(--color-primary-600);
  text-decoration: none;
  transition: all var(--transition-base);
}

.feature-link:hover {
  gap: 10px;
  color: var(--color-primary-500);
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
   Community Section
   ============================================ */
.community-section {
  padding: 48px 60px;
  max-width: 1400px;
  margin: 0 auto;
}

.community-box {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 48px;
  padding: 48px;
  background: linear-gradient(135deg, rgba(59, 130, 246, 0.08), rgba(99, 102, 241, 0.05));
  border: 1px solid var(--color-primary-500);
  border-radius: var(--radius-2xl);
}

.community-content {
  flex: 1;
  max-width: 480px;
}

.community-badge {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 6px 14px;
  background: var(--glow-primary-soft);
  border-radius: var(--radius-full);
  font-size: var(--text-xs);
  font-weight: 700;
  color: var(--color-primary-600);
  margin-bottom: 20px;
}

.community-content h2 {
  font-family: var(--font-display);
  font-size: 1.5rem;
  font-weight: 800;
  color: var(--text-primary);
  margin-bottom: 12px;
}

.community-content p {
  font-size: var(--text-sm);
  color: var(--text-secondary);
  line-height: 1.7;
  margin-bottom: 24px;
}

.community-actions {
  display: flex;
  gap: 12px;
}

.btn-community-primary {
  display: inline-flex;
  align-items: center;
  padding: 12px 24px;
  background: linear-gradient(135deg, var(--color-primary-600), var(--color-primary-700));
  color: white;
  font-size: var(--text-sm);
  font-weight: 700;
  border-radius: var(--radius-lg);
  text-decoration: none;
  transition: all var(--transition-base);
}

.btn-community-primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(59, 130, 246, 0.35);
}

.btn-community-secondary {
  display: inline-flex;
  align-items: center;
  padding: 12px 24px;
  background: var(--bg-card);
  color: var(--text-primary);
  font-size: var(--text-sm);
  font-weight: 600;
  border-radius: var(--radius-lg);
  text-decoration: none;
  border: 1px solid var(--border-color);
  transition: all var(--transition-base);
}

.btn-community-secondary:hover {
  background: var(--bg-hover);
  border-color: var(--color-primary-400);
}

.community-visual {
  flex-shrink: 0;
}

.template-preview {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.template-card {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 14px 18px;
  background: var(--bg-card);
  border: 1px solid var(--border-color);
  border-radius: var(--radius-xl);
  min-width: 240px;
  transition: all var(--transition-base);
}

.template-card:hover {
  border-color: var(--color-primary-400);
  transform: translateX(-4px);
}

.template-icon {
  font-size: 1.25rem;
}

.template-name {
  font-size: var(--text-sm);
  font-weight: 600;
  color: var(--text-primary);
}

.template-author {
  margin-left: auto;
  font-size: var(--text-xs);
  color: var(--text-muted);
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

.footer-brand {
  cursor: pointer;
  transition: color 0.2s;
}

.footer-brand:hover {
  color: var(--color-primary-600);
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

  .feature-grid {
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

  .community-box {
    flex-direction: column;
    text-align: center;
  }

  .community-actions {
    justify-content: center;
  }
}

@media (max-width: 768px) {
  .hero-section,
  .feature-section,
  .arch-section,
  .cats-section,
  .community-section,
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

  .hero-quick-stats {
    gap: 20px;
  }

  .cats-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .community-actions {
    flex-direction: column;
  }

  .template-preview {
    display: none;
  }
}
</style>
