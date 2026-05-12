<template>
  <Teleport to="body">
    <Transition name="dialog">
      <div v-if="visible" class="confirm-overlay" @click.self="handleCancel">
        <div class="confirm-dialog">
          <div class="confirm-icon" :class="iconClass">
            <svg v-if="type === 'warning'" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M10.29 3.86L1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z"/>
              <line x1="12" y1="9" x2="12" y2="13"/>
              <line x1="12" y1="17" x2="12.01" y2="17"/>
            </svg>
            <svg v-else-if="type === 'danger'" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <circle cx="12" cy="12" r="10"/>
              <line x1="15" y1="9" x2="9" y2="15"/>
              <line x1="9" y1="9" x2="15" y2="15"/>
            </svg>
            <svg v-else width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <circle cx="12" cy="12" r="10"/>
              <line x1="12" y1="16" x2="12" y2="12"/>
              <line x1="12" y1="8" x2="12.01" y2="8"/>
            </svg>
          </div>
          <h3 class="confirm-title">{{ title }}</h3>
          <p class="confirm-message">{{ message }}</p>
          <div class="confirm-actions">
            <button class="btn-cancel" @click="handleCancel">{{ cancelText }}</button>
            <button class="btn-confirm" :class="confirmClass" @click="handleConfirm">{{ confirmText }}</button>
          </div>
        </div>
      </div>
    </Transition>
  </Teleport>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'

export interface ConfirmOptions {
  title?: string
  message: string
  confirmText?: string
  cancelText?: string
  type?: 'warning' | 'danger' | 'info'
}

const visible = ref(false)
const options = ref<ConfirmOptions>({ message: '' })
let resolvePromise: ((value: boolean) => void) | null = null

const title = computed(() => options.value.title || '确认操作')
const message = computed(() => options.value.message)
const confirmText = computed(() => options.value.confirmText || '确定')
const cancelText = computed(() => options.value.cancelText || '取消')
const type = computed(() => options.value.type || 'warning')

const iconClass = computed(() => `icon-${type.value}`)
const confirmClass = computed(() => `confirm-${type.value}`)

const show = (opts: ConfirmOptions): Promise<boolean> => {
  options.value = { title: '确认操作', type: 'warning', ...opts }
  visible.value = true
  return new Promise((resolve) => {
    resolvePromise = resolve
  })
}

const handleConfirm = () => {
  visible.value = false
  resolvePromise?.(true)
  resolvePromise = null
}

const handleCancel = () => {
  visible.value = false
  resolvePromise?.(false)
  resolvePromise = null
}

defineExpose({ show })
</script>

<style scoped>
.confirm-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.6);
  backdrop-filter: blur(4px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 9999;
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

.icon-warning {
  background: rgba(245, 158, 11, 0.15);
  color: #f59e0b;
}

.icon-danger {
  background: rgba(239, 68, 68, 0.15);
  color: #ef4444;
}

.icon-info {
  background: rgba(59, 130, 246, 0.15);
  color: #3b82f6;
}

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

.btn-cancel:hover {
  background: var(--bg-tertiary, #e5e7eb);
}

.btn-confirm {
  border: none;
  color: white;
}

.confirm-warning {
  background: linear-gradient(135deg, #f59e0b, #d97706);
}

.confirm-warning:hover {
  box-shadow: 0 4px 16px rgba(245, 158, 11, 0.4);
}

.confirm-danger {
  background: linear-gradient(135deg, #ef4444, #dc2626);
}

.confirm-danger:hover {
  box-shadow: 0 4px 16px rgba(239, 68, 68, 0.4);
}

.confirm-info {
  background: linear-gradient(135deg, #3b82f6, #2563eb);
}

.confirm-info:hover {
  box-shadow: 0 4px 16px rgba(59, 130, 246, 0.4);
}

/* Transitions */
.dialog-enter-active {
  animation: dialogIn 0.25s cubic-bezier(0.16, 1, 0.3, 1);
}

.dialog-leave-active {
  animation: dialogOut 0.2s ease-out forwards;
}

@keyframes dialogIn {
  from {
    opacity: 0;
    transform: scale(0.95) translateY(10px);
  }
  to {
    opacity: 1;
    transform: scale(1) translateY(0);
  }
}

@keyframes dialogOut {
  from {
    opacity: 1;
    transform: scale(1) translateY(0);
  }
  to {
    opacity: 0;
    transform: scale(0.98) translateY(-5px);
  }
}
</style>
