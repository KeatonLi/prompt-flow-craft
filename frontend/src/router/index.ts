import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'
import About from '../views/About.vue'
import Templates from '../views/Templates.vue'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: HomeView
  },
  {
    path: '/about',
    name: 'About',
    component: About
  },
  {
    path: '/templates',
    name: 'Templates',
    component: Templates
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
