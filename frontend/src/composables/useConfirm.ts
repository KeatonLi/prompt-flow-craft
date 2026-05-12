import { ref, provide, inject, InjectionKey } from 'vue'
import ConfirmDialog from '@/components/common/ConfirmDialog.vue'

export interface ConfirmOptions {
  title?: string
  message: string
  confirmText?: string
  cancelText?: string
  type?: 'warning' | 'danger' | 'info'
}

interface ConfirmAPI {
  confirm: (options: ConfirmOptions) => Promise<boolean>
}

const ConfirmKey: InjectionKey<ConfirmAPI> = Symbol('confirm')

export function createConfirm() {
  const dialogRef = ref<InstanceType<typeof ConfirmDialog> | null>(null)

  const confirm = (options: ConfirmOptions): Promise<boolean> => {
    return dialogRef.value?.show(options) || Promise.resolve(false)
  }

  return {
    dialogRef,
    confirm
  }
}

export function provideConfirm() {
  const { dialogRef, confirm } = createConfirm()
  provide(ConfirmKey, { confirm })
  return { dialogRef }
}

export function useConfirm(): ConfirmAPI {
  const ctx = inject(ConfirmKey)
  if (!ctx) {
    throw new Error('Confirm not provided')
  }
  return ctx
}

// 导出组件供全局注册
export { ConfirmDialog }
