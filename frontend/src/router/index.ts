import { createRouter, createWebHistory } from 'vue-router'
import About from '../views/About.vue'
import HomeView from '../views/HomeView.vue'
import Templates from '../views/Templates.vue'
import Statistics from '../views/Statistics.vue'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: About
  },
  {
    path: '/generate',
    name: 'Generate',
    component: HomeView
  },
  {
    path: '/templates',
    name: 'Templates',
    component: Templates
  },
  {
    path: '/statistics',
    name: 'Statistics',
    component: Statistics
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
