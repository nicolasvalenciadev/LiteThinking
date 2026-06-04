import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { ROLES } from '../constants/index.js'

export const useAuthStore = defineStore('auth', () => {
  const token = ref(null)
  const role = ref(null)
  const username = ref(null)

  const isAuthenticated = computed(() => !!token.value)
  const isAdmin = computed(() => role.value === ROLES.ADMIN)
  const isExternal = computed(() => role.value === ROLES.EXTERNAL)

  function setAuth(authData) {
    token.value = authData.token

    if (authData.role) {
      role.value = authData.role
    }
    if (authData.username) {
      username.value = authData.username
    }

    if ((!role.value || !username.value) && authData.token) {
      try {
        const base64Url = authData.token.split('.')[1]
        const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/')
        const jsonPayload = decodeURIComponent(
          atob(base64).split('').map(c =>
            '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2)
          ).join('')
        )
        const payload = JSON.parse(jsonPayload)
        if (!role.value) role.value = payload.role || null
        if (!username.value) username.value = payload.sub || null
      } catch {
        // JWT decode failed; fields remain null
      }
    }
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
