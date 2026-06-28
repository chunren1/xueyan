import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/',
      name: 'Home',
      component: () => import('@/views/Home.vue'),
    },
    {
      path: '/login',
      name: 'Login',
      component: () => import('@/views/Login.vue'),
    },
    {
      path: '/register',
      name: 'Register',
      component: () => import('@/views/Register.vue'),
    },
    {
      path: '/course/:id',
      name: 'CourseDetail',
      component: () => import('@/views/CourseDetail.vue'),
    },
    {
      path: '/order',
      name: 'Order',
      component: () => import('@/views/Order.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/user',
      name: 'UserCenter',
      component: () => import('@/views/UserCenter.vue'),
      meta: { requiresAuth: true },
    },
  ],
})

// 路由守卫：需要登录的页面
router.beforeEach((to, _from, next) => {
  const token = localStorage.getItem('token')
  if (to.meta.requiresAuth && !token) {
    next('/login')
  } else {
    next()
  }
})

export default router
