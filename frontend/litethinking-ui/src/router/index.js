import { route } from 'quasar/wrappers'
import {
  createRouter,
  createMemoryHistory,
  createWebHistory,
  createWebHashHistory
} from 'vue-router'
import routes from './routes.js'
import { useAuthStore } from '../stores/auth.store.js'
import { ROLES } from '../constants/index.js'

export default route(function () {
  const createHistory = process.env.SERVER
    ? createMemoryHistory
    : (process.env.VUE_ROUTER_MODE === 'history' ? createWebHistory : createWebHashHistory)

  const Router = createRouter({
    scrollBehavior: () => ({ left: 0, top: 0 }),
    routes,
    history: createHistory(process.env.VUE_ROUTER_BASE)
  })

  Router.beforeEach((to, from, next) => {
    const authStore = useAuthStore()
    const requiresAuth = to.matched.some(record => record.meta.requiresAuth)
    const requiredRole = to.meta.requiredRole

    if (requiresAuth && !authStore.isAuthenticated) {
      return next('/login')
    }

    if (to.path === '/login' && authStore.isAuthenticated) {
      return next('/companies')
    }

    if (requiredRole === ROLES.ADMIN && authStore.isExternal) {
      return next('/companies')
    }

    next()
  })

  Router.afterEach((to) => {
    document.title = to.meta.title
      ? `${to.meta.title} — LiteThinking`
      : 'LiteThinking — Gestión Empresarial'
  })

  return Router
})
