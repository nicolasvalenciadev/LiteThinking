import { boot } from 'quasar/wrappers'
import axios from 'axios'
import { Notify } from 'quasar'
import { useAuthStore } from '../stores/auth.store.js'

const api = axios.create({
  baseURL: process.env.API_URL || 'http://localhost:8080'
})

export default boot(({ app, router }) => {
  app.config.globalProperties.$axios = axios
  app.config.globalProperties.$api = api

  const authStore = useAuthStore()

  api.interceptors.request.use(
    (config) => {
      if (authStore.token) {
        config.headers.Authorization = `Bearer ${authStore.token}`
      }
      return config
    },
    (error) => Promise.reject(error)
  )

  api.interceptors.response.use(
    (response) => response,
    (error) => {
      if (!error.response) {
        Notify.create({ type: 'negative', message: 'Sin conexión al servidor' })
        return Promise.reject(error)
      }

      const { status } = error.response

      if (status === 401) {
        authStore.clearAuth()
        if (router.currentRoute.value.path !== '/login') {
          router.push('/login')
        }
      } else if (status === 403) {
        Notify.create({ type: 'negative', message: 'Sin permisos para realizar esta acción' })
      } else if (status >= 500) {
        Notify.create({ type: 'negative', message: 'Error del servidor. Intente nuevamente' })
      }

      return Promise.reject(error)
    }
  )
})

export { api }
