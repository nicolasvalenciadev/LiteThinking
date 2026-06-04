import { ref } from 'vue'
import { api } from '../boot/axios.js'
import { useAuthStore } from '../stores/auth.store.js'

export function useAuth() {
  const loading = ref(false)
  const error = ref(null)
  const authStore = useAuthStore()

  const login = async (username, password) => {
    loading.value = true
    error.value = null
    try {
      const { data } = await api.post('/api/auth/login', { username, password })
      authStore.setAuth(data)
      return true
    } catch (err) {
      if (err.response && err.response.status === 401) {
        error.value = 'Usuario o contraseña incorrectos'
      } else if (err.response && err.response.data && err.response.data.message) {
        error.value = err.response.data.message
      } else {
        error.value = 'No se pudo conectar con el servidor de autenticación'
      }
      return false
    } finally {
      loading.value = false
    }
  }

  const logout = () => {
    authStore.clearAuth()
  }

  return {
    loading,
    error,
    login,
    logout,
    isAuthenticated: () => authStore.isAuthenticated,
    hasRole: (role) => authStore.role === role
  }
}
