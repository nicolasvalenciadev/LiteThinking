import { boot } from 'quasar/wrappers'
import axios from 'axios'
import { useAuthStore } from '../stores/auth.store.js'

// Create axios instance
const api = axios.create({
  baseURL: 'http://localhost:8080'
})

export default boot(({ app, router }) => {
  // Set global properties
  app.config.globalProperties.$axios = axios
  app.config.globalProperties.$api = api

  const authStore = useAuthStore()

  // Request Interceptor: Attach bearer token
  api.interceptors.request.use(
    (config) => {
      if (authStore.token) {
        config.headers.Authorization = `Bearer ${authStore.token}`
      }
      return config
    },
    (error) => {
      return Promise.reject(error)
    }
  );

  // Response Interceptor: Handle 401 status
  api.interceptors.response.use(
    (response) => {
      return response
    },
    (error) => {
      if (error.response && error.response.status === 401) {
        authStore.clearAuth()
        // If not already on login page, redirect
        if (router.currentRoute.value.path !== '/login') {
          router.push('/login')
        }
      }
      return Promise.reject(error)
    }
  );
})

export { api }
