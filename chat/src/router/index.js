import Vue from 'vue'
import VueRouter from 'vue-router'

Vue.use(VueRouter)

const routes = [
  {
    path: '/',
    name: 'home',
    component: () => import('@/views/HomeView')
  },
  {
    path: '/login',
    name: 'login',
    component: () => import('@/views/Login')
  },
  {
    path: '/callVideo',
    name: 'callVideo',
    component: () => import('@/components/CallVideo')
  },
  {
    path: "/register",
    name: "register",
    component: () => import('@/views/Login/register.vue')
  }
]

const router = new VueRouter({
  routes
})

export default router
