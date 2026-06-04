import { route } from 'quasar/wrappers'
import { createRouter, createMemoryHistory, createWebHistory, createWebHashHistory } from 'vue-router'
import routes from './routes.js'
import { useAuthStore } from '../stores/auth.store.js'

export default route(function (/* { store, ssrContext } */) {
  const createHistory = process.env.SERVER
    ? createMemoryHistory
    : (process.env.VUE_ROUTER_MODE === 'history' ? createWebHistory : createWebHashHistory)

  const Router = createRouter({
    scrollBehavior: () => ({ left: 0, top: 0 }),
    routes,
    history: createHistory(process.env.VUE_ROUTER_BASE)
  })

  // Navigation Guard
  Router.beforeEach((to, from, next) => {
    const authStore = useAuthStore()

    const requiresAuth = to.matched.some(record => record.meta.requiresAuth)

    if (requiresAuth && !authStore.isAuthenticated) {
      // User is not authenticated, redirect to login
      next('/login')
    } else if (to.path === '/login' && authStore.isAuthenticated) {
      // User is already authenticated, redirect to dashboard/companies
      next('/companies')
    } else {
      // Proceed
      next()
    }
  })

  return Router
})
