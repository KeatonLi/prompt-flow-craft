import { ref, computed } from 'vue'

const isAdmin = ref(sessionStorage.getItem('adminMode') === 'true')

export function useAdmin() {
  const toggleAdmin = () => {
    isAdmin.value = !isAdmin.value
    sessionStorage.setItem('adminMode', String(isAdmin.value))
    if (isAdmin.value) {
      (window as any).showToast?.({ message: '管理员模式已开启', type: 'success' })
    } else {
      (window as any).showToast?.({ message: '管理员模式已关闭', type: 'info' })
    }
  }

  const enableAdmin = () => {
    isAdmin.value = true
    sessionStorage.setItem('adminMode', 'true')
    ;(window as any).showToast?.({ message: '管理员模式已开启', type: 'success' })
  }

  const disableAdmin = () => {
    isAdmin.value = false
    sessionStorage.setItem('adminMode', 'false')
    ;(window as any).showToast?.({ message: '管理员模式已关闭', type: 'info' })
  }

  return {
    isAdmin: computed(() => isAdmin.value),
    toggleAdmin,
    enableAdmin,
    disableAdmin
  }
}
