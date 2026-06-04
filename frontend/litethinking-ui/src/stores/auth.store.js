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
    console.log('Login Response Data:', authData)
    token.value = authData.token
    
    // Check if role and username are directly in the response
    if (authData.role) {
      role.value = authData.role
    }
    if (authData.username) {
      username.value = authData.username
    }
    
    // Fallback: decode JWT if role or username is missing
    if ((!role.value || !username.value) && authData.token) {
      try {
        const base64Url = authData.token.split('.')[1]
        const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/')
        const jsonPayload = decodeURIComponent(atob(base64).split('').map(c => {
          return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2)
        }).join(''))
        const payload = JSON.parse(jsonPayload)
        console.log('Decoded JWT payload:', payload)
        if (!role.value) role.value = payload.role || null
        if (!username.value) username.value = payload.sub || null
      } catch (e) {
        console.error('Error decoding token:', e)
      }
    }
    console.log('Auth Store State updated:', {
      token: token.value,
      role: role.value,
      username: username.value
    })
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
