const routes = [
  {
    path: '/login',
    component: () => import('../layouts/AuthLayout.vue'),
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
      { path: 'companies', component: () => import('../pages/CompanyPage.vue') },
      { path: 'products', component: () => import('../pages/ProductPage.vue') },
      { path: 'inventory', component: () => import('../pages/InventoryPage.vue') }
    ]
  },

  // Catch-all route to redirect back to main page or a fallback
  {
    path: '/:catchAll(.*)*',
    redirect: '/companies'
  }
]

export default routes
