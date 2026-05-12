import { ref, reactive, provide, inject, InjectionKey } from 'vue'

export interface ToastOptions {
  message: string
  type?: 'success' | 'error' | 'warning' | 'info'
  duration?: number
}

interface ToastAPI {
  show: (options: ToastOptions) => void
  success: (message: string, duration?: number) => void
  error: (message: string, duration?: number) => void
  warning: (message: string, duration?: number) => void
  info: (message: string, duration?: number) => void
}

const ToastKey: InjectionKey<ToastAPI> = Symbol('toast')

export function createToast() {
  const toast = reactive<ToastOptions>({
    message: '',
    type: 'info',
    duration: 3000
  })
  const visible = ref(false)
  let timer: ReturnType<typeof setTimeout> | null = null

  const show = (options: ToastOptions) => {
    if (timer) clearTimeout(timer)
    toast.message = options.message
    toast.type = options.type || 'info'
    toast.duration = options.duration ?? 3000
    visible.value = true
    if (toast.duration > 0) {
      timer = setTimeout(() => {
        visible.value = false
      }, toast.duration)
    }
  }

  const close = () => {
    visible.value = false
    if (timer) clearTimeout(timer)
  }

  const api: ToastAPI = {
    show,
    success: (message: string, duration?: number) => show({ message, type: 'success', duration }),
    error: (message: string, duration?: number) => show({ message, type: 'error', duration }),
    warning: (message: string, duration?: number) => show({ message, type: 'warning', duration }),
    info: (message: string, duration?: number) => show({ message, type: 'info', duration })
  }

  return {
    toast,
    visible,
    show,
    close,
    api
  }
}

export function provideToast() {
  const { toast, visible, close, api } = createToast()
  provide(ToastKey, api)
  return { toast, visible, close }
}

export function useToast(): ToastAPI {
  const toast = inject(ToastKey)
  if (!toast) {
    throw new Error('Toast not provided')
  }
  return toast
}
