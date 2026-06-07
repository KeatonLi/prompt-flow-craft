<template>
  <div class="not-found-page">
    <div class="not-found-container">
      <div class="error-code">404</div>
      <div class="error-icon">
        <svg width="80" height="80" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
          <circle cx="12" cy="12" r="10"/>
          <path d="M16 16s-1.5-2-4-2-4 2-4 2"/>
          <line x1="9" y1="9" x2="9.01" y2="9"/>
          <line x1="15" y1="9" x2="15.01" y2="9"/>
        </svg>
      </div>
      <h1 class="not-found-title">页面不存在</h1>
      <p class="not-found-desc">你访问的页面不存在或已被移除</p>
      <div class="not-found-actions">
        <router-link to="/" class="btn-home">
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"/>
            <polyline points="9 22 9 12 15 12 15 22"/>
          </svg>
          返回首页
        </router-link>
        <router-link to="/generate" class="btn-create">
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M12 2L2 7l10 5 10-5-10-5z"/>
            <path d="M2 17l10 5 10-5"/>
            <path d="M2 12l10 5 10-5"/>
          </svg>
          去创作
        </router-link>
      </div>
    </div>
  </div>
</template>

<style scoped>
.not-found-page {
  min-height: calc(100vh - var(--header-height, 64px));
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px 20px;
}

.not-found-container {
  text-align: center;
  max-width: 420px;
}

.error-code {
  font-size: 6rem;
  font-weight: 800;
  color: var(--color-primary-500);
  line-height: 1;
  opacity: 0.15;
  margin-bottom: -20px;
  user-select: none;
}

.error-icon {
  color: var(--text-secondary);
  opacity: 0.6;
  margin-bottom: 20px;
}

.not-found-title {
  font-size: 1.5rem;
  font-weight: 700;
  color: var(--text-primary);
  margin: 0 0 8px;
}

.not-found-desc {
  font-size: 0.95rem;
  color: var(--text-secondary);
  margin: 0 0 32px;
}

.not-found-actions {
  display: flex;
  gap: 12px;
  justify-content: center;
  flex-wrap: wrap;
}

.btn-home,
.btn-create {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 24px;
  border-radius: var(--radius-lg);
  font-size: 0.9rem;
  font-weight: 600;
  cursor: pointer;
  transition: all var(--transition-base);
  text-decoration: none;
}

.btn-home {
  background: var(--bg-card);
  border: 1px solid var(--border-color);
  color: var(--text-primary);
}

.btn-home:hover {
  border-color: var(--color-primary-400);
  color: var(--color-primary-600);
  background: var(--glow-primary-soft);
}

.btn-create {
  background: linear-gradient(135deg, var(--color-primary-600), var(--color-primary-700));
  border: none;
  color: white;
  box-shadow: 0 4px 16px rgba(59, 130, 246, 0.3);
}

.btn-create:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(59, 130, 246, 0.4);
}
</style>
