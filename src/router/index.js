import { createRouter, createWebHistory } from 'vue-router'
import LoginPage from '../views/LoginPage.vue'
import DashboardPage from '../views/DashboardPage.vue'
import ExpensePage from '../views/ExpensePage.vue'
import IncomePage from '../views/IncomePage.vue'
import StatisticsPage from '../views/StatisticsPage.vue'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: LoginPage,
    meta: { requiresAuth: false }
  },
  {
    path: '/',
    name: 'Dashboard',
    component: DashboardPage,
    meta: { requiresAuth: true }
  },
  {
    path: '/expenses',
    name: 'expenses',
    component: ExpensePage,
    meta: { requiresAuth: true }
  },
  {
    path: '/income',
    name: 'income',
    component: IncomePage,
    meta: { requiresAuth: true }
  },
  {
    path: '/statistics',
    name: 'Statistics',
    component: StatisticsPage,
    meta: { requiresAuth: true }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  // 开发环境跳过验证
  if (import.meta.env.DEV) {
    next()
    return
  }

  const token = localStorage.getItem('token')
  if (to.meta.requiresAuth && !token) {
    next('/login')
  } else {
    next()
  }
})

export default router 