import { ROLES } from '../constants/index.js'

const routes = [
  {
    path: '/login',
    component: () => import('../layouts/AuthLayout.vue'),
    meta: { requiresAuth: false, title: 'Iniciar Sesión' },
    children: [
      { path: '', component: () => import('../pages/LoginPage.vue') }
    ]
  },
  {
    path: '/',
    component: () => import('../layouts/MainLayout.vue'),
    meta: { requiresAuth: true },
    children: [
      { path: '', redirect: '/companies' },
      {
        path: 'companies',
        component: () => import('../pages/CompanyPage.vue'),
        meta: { requiresAuth: true, requiredRole: null, title: 'Empresas' }
      },
      {
        path: 'products',
        component: () => import('../pages/ProductPage.vue'),
        meta: { requiresAuth: true, requiredRole: ROLES.ADMIN, title: 'Productos' }
      },
      {
        path: 'inventory',
        component: () => import('../pages/InventoryPage.vue'),
        meta: { requiresAuth: true, requiredRole: ROLES.ADMIN, title: 'Inventario' }
      }
    ]
  },
  {
    path: '/:catchAll(.*)*',
    redirect: '/companies'
  }
]

export default routes
