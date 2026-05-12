<template>
  <router-view />
  <!-- Global Toast Container -->
  <Teleport to="body">
    <Transition name="toast">
      <div v-if="toast.visible" class="global-toast" :class="`toast-${toast.type}`">
        <div class="toast-icon">
          <svg v-if="toast.type === 'success'" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"/>
            <polyline points="22 4 12 14.01 9 11.01"/>
          </svg>
          <svg v-else-if="toast.type === 'error'" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <circle cx="12" cy="12" r="10"/>
            <line x1="15" y1="9" x2="9" y2="15"/>
            <line x1="9" y1="9" x2="15" y2="15"/>
          </svg>
          <svg v-else-if="toast.type === 'warning'" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M10.29 3.86L1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z"/>
            <line x1="12" y1="9" x2="12" y2="13"/>
            <line x1="12" y1="17" x2="12.01" y2="17"/>
          </svg>
          <svg v-else width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <circle cx="12" cy="12" r="10"/>
            <line x1="12" y1="16" x2="12" y2="12"/>
            <line x1="12" y1="8" x2="12.01" y2="8"/>
          </svg>
        </div>
        <div class="toast-content">
          <p class="toast-message">{{ toast.message }}</p>
        </div>
        <button class="toast-close" @click="hideToast">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <line x1="18" y1="6" x2="6" y2="18"/>
            <line x1="6" y1="6" x2="18" y2="18"/>
          </svg>
        </button>
      </div>
    </Transition>
  </Teleport>

  <!-- Global Confirm Dialog -->
  <Teleport to="body">
    <Transition name="dialog">
      <div v-if="confirm.visible" class="confirm-overlay" @click.self="confirmCancel">
        <div class="confirm-dialog">
          <div class="confirm-icon" :class="`icon-${confirm.type}`">
            <svg v-if="confirm.type === 'danger'" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <circle cx="12" cy="12" r="10"/>
              <line x1="15" y1="9" x2="9" y2="15"/>
              <line x1="9" y1="9" x2="15" y2="15"/>
            </svg>
            <svg v-else-if="confirm.type === 'warning'" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M10.29 3.86L1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z"/>
              <line x1="12" y1="9" x2="12" y2="13"/>
              <line x1="12" y1="17" x2="12.01" y2="17"/>
            </svg>
            <svg v-else width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <circle cx="12" cy="12" r="10"/>
              <line x1="12" y1="16" x2="12" y2="12"/>
              <line x1="12" y1="8" x2="12.01" y2="8"/>
            </svg>
          </div>
          <h3 class="confirm-title">{{ confirm.title }}</h3>
          <p class="confirm-message">{{ confirm.message }}</p>
          <div class="confirm-actions">
            <button class="btn-cancel" @click="confirmCancel">{{ confirm.cancelText }}</button>
            <button class="btn-confirm" :class="`confirm-${confirm.type}`" @click="confirmOk">{{ confirm.confirmText }}</button>
          </div>
        </div>
      </div>
    </Transition>
  </Teleport>
</template>

<script setup lang="ts">
import { reactive, onMounted } from 'vue'

// ========== Toast ==========
const toast = reactive({
  visible: false,
  message: '',
  type: 'info' as 'success' | 'error' | 'warning' | 'info',
  timer: null as ReturnType<typeof setTimeout> | null
})

const hideToast = () => {
  toast.visible = false
  if (toast.timer) {
    clearTimeout(toast.timer)
    toast.timer = null
  }
}

const showToast = (options: { message: string; type?: 'success' | 'error' | 'warning' | 'info'; duration?: number }) => {
  if (toast.timer) {
    clearTimeout(toast.timer)
  }
  toast.message = options.message
  toast.type = options.type || 'info'
  toast.visible = true
  const duration = options.duration ?? 3000
  if (duration > 0) {
    toast.timer = setTimeout(() => {
      toast.visible = false
    }, duration)
  }
}

// ========== Confirm ==========
const confirm = reactive({
  visible: false,
  title: '确认操作',
  message: '',
  confirmText: '确定',
  cancelText: '取消',
  type: 'warning' as 'warning' | 'danger' | 'info',
  resolve: null as ((value: boolean) => void) | null
})

const confirmOk = () => {
  confirm.visible = false
  confirm.resolve?.(true)
  confirm.resolve = null
}

const confirmCancel = () => {
  confirm.visible = false
  confirm.resolve?.(false)
  confirm.resolve = null
}

const showConfirm = (options: {
  title?: string
  message: string
  confirmText?: string
  cancelText?: string
  type?: 'warning' | 'danger' | 'info'
}): Promise<boolean> => {
  confirm.title = options.title || '确认操作'
  confirm.message = options.message
  confirm.confirmText = options.confirmText || '确定'
  confirm.cancelText = options.cancelText || '取消'
  confirm.type = options.type || 'warning'
  confirm.visible = true
  return new Promise((resolve) => {
    confirm.resolve = resolve
  })
}

// ========== Global ==========
;(window as any).showToast = showToast
;(window as any).showConfirm = showConfirm

onMounted(() => {
  window.addEventListener('beforeunload', hideToast)
})
</script>

<style>
/* ========== Toast Styles ========== */
.global-toast {
  position: fixed;
  top: 24px;
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 14px 18px;
  background: var(--bg-card, #ffffff);
  border: 1px solid var(--border-color, #e5e7eb);
  border-radius: 12px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.15), 0 2px 8px rgba(0, 0, 0, 0.1);
  z-index: 9999;
  min-width: 280px;
  max-width: 480px;
}

.toast-icon {
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: center;
}

.toast-success .toast-icon { color: #10b981; }
.toast-error .toast-icon { color: #ef4444; }
.toast-warning .toast-icon { color: #f59e0b; }
.toast-info .toast-icon { color: #3b82f6; }

.toast-content { flex: 1; }

.toast-message {
  margin: 0;
  font-size: 14px;
  font-weight: 500;
  color: var(--text-primary, #111827);
  line-height: 1.4;
}

.toast-close {
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 28px;
  height: 28px;
  border: none;
  border-radius: 6px;
  background: transparent;
  color: var(--text-muted, #9ca3af);
  cursor: pointer;
  transition: all 0.2s;
}

.toast-close:hover {
  background: var(--bg-hover, #f3f4f6);
  color: var(--text-primary, #111827);
}

.toast-enter-active { animation: toastIn 0.3s cubic-bezier(0.16, 1, 0.3, 1); }
.toast-leave-active { animation: toastOut 0.25s ease-out forwards; }

@keyframes toastIn {
  from { opacity: 0; transform: translateX(-50%) translateY(-20px) scale(0.95); }
  to { opacity: 1; transform: translateX(-50%) translateY(0) scale(1); }
}

@keyframes toastOut {
  from { opacity: 1; transform: translateX(-50%) translateY(0) scale(1); }
  to { opacity: 0; transform: translateX(-50%) translateY(-10px) scale(0.98); }
}

/* ========== Confirm Dialog Styles ========== */
.confirm-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.6);
  backdrop-filter: blur(4px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 10000;
}

.confirm-dialog {
  background: var(--bg-card, #ffffff);
  border-radius: 16px;
  padding: 28px;
  max-width: 400px;
  width: 90%;
  text-align: center;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.2);
  animation: dialogIn 0.25s cubic-bezier(0.16, 1, 0.3, 1);
}

.confirm-icon {
  width: 56px;
  height: 56px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 20px;
}

.icon-warning { background: rgba(245, 158, 11, 0.15); color: #f59e0b; }
.icon-danger { background: rgba(239, 68, 68, 0.15); color: #ef4444; }
.icon-info { background: rgba(59, 130, 246, 0.15); color: #3b82f6; }

.confirm-title {
  margin: 0 0 12px;
  font-size: 18px;
  font-weight: 700;
  color: var(--text-primary, #111827);
}

.confirm-message {
  margin: 0 0 24px;
  font-size: 14px;
  color: var(--text-secondary, #6b7280);
  line-height: 1.6;
}

.confirm-actions {
  display: flex;
  gap: 12px;
  justify-content: center;
}

.btn-cancel,
.btn-confirm {
  padding: 12px 28px;
  border-radius: 10px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-cancel {
  background: var(--bg-hover, #f3f4f6);
  border: 1px solid var(--border-color, #e5e7eb);
  color: var(--text-secondary, #6b7280);
}

.btn-cancel:hover { background: var(--bg-tertiary, #e5e7eb); }

.btn-confirm { border: none; color: white; }
.confirm-warning { background: linear-gradient(135deg, #f59e0b, #d97706); }
.confirm-warning:hover { box-shadow: 0 4px 16px rgba(245, 158, 11, 0.4); }
.confirm-danger { background: linear-gradient(135deg, #ef4444, #dc2626); }
.confirm-danger:hover { box-shadow: 0 4px 16px rgba(239, 68, 68, 0.4); }
.confirm-info { background: linear-gradient(135deg, #3b82f6, #2563eb); }
.confirm-info:hover { box-shadow: 0 4px 16px rgba(59, 130, 246, 0.4); }

.dialog-enter-active { animation: dialogIn 0.25s cubic-bezier(0.16, 1, 0.3, 1); }
.dialog-leave-active { animation: dialogOut 0.2s ease-out forwards; }

@keyframes dialogIn {
  from { opacity: 0; transform: scale(0.95) translateY(10px); }
  to { opacity: 1; transform: scale(1) translateY(0); }
}

@keyframes dialogOut {
  from { opacity: 1; transform: scale(1) translateY(0); }
  to { opacity: 0; transform: scale(0.98) translateY(-5px); }
}
</style>
