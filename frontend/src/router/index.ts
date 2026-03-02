import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'
import About from '../views/About.vue'
import Templates from '../views/Templates.vue'
import Popular from '../views/Popular.vue'
import Favorites from '../views/Favorites.vue'
import Settings from '../views/Settings.vue'
import Optimize from '../views/Optimize.vue'

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
  },
  {
    path: '/popular',
    name: 'Popular',
    component: Popular
  },
  {
    path: '/favorites',
    name: 'Favorites',
    component: Favorites
  },
  {
    path: '/settings',
    name: 'Settings',
    component: Settings
  },
  {
    path: '/optimize',
    name: 'Optimize',
    component: Optimize
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
