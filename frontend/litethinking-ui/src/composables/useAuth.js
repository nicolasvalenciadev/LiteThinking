import { ref } from 'vue'
import { Notify } from 'quasar'
import { api } from '../boot/axios.js'
import { useAuthStore } from '../stores/auth.store.js'
import { API } from '../constants/index.js'

export function useAuth() {
  const loading = ref(false)
  const error = ref(null)
  const authStore = useAuthStore()

  const login = async (username, password) => {
    loading.value = true
    error.value = null
    try {
      const { data } = await api.post(API.AUTH_LOGIN, { username, password })
      authStore.setAuth(data)
      return true
    } catch (err) {
      if (err.response?.status === 401) {
        error.value = 'Usuario o contraseña incorrectos'
      } else if (err.response?.data?.message) {
        error.value = err.response.data.message
      } else if (!err.response) {
        error.value = 'Sin conexión al servidor de autenticación'
      } else {
        error.value = 'Ha ocurrido un error inesperado'
      }
      Notify.create({ type: 'negative', message: error.value })
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
