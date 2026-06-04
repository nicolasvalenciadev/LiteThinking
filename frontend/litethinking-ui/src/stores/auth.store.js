import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useAuthStore = defineStore('auth', () => {
  const token = ref(null)
  const role = ref(null)
  const username = ref(null)

  const isAuthenticated = computed(() => !!token.value)
  const isAdmin = computed(() => role.value === 'ADMIN')
  const isExternal = computed(() => role.value === 'EXTERNAL')

  function setAuth(authData) {
    token.value = authData.token
    role.value = authData.role
    username.value = authData.username
  }

  function clearAuth() {
    token.value = null
    role.value = null
    username.value = null
  }

  return {
    token,
    role,
    username,
    isAuthenticated,
    isAdmin,
    isExternal,
    setAuth,
    clearAuth
  }
})
