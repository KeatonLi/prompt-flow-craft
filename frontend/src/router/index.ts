import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'
import About from '../views/About.vue'
import Templates from '../views/Templates.vue'
import TemplateMarket from '../views/TemplateMarket.vue'
import Popular from '../views/Popular.vue'
import Favorites from '../views/Favorites.vue'
import Settings from '../views/Settings.vue'
import Optimize from '../views/Optimize.vue'
import Compare from '../views/Compare.vue'
import Community from '../views/Community.vue'
import VariableExtractor from '../views/VariableExtractor.vue'

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
    path: '/template-market',
    name: 'TemplateMarket',
    component: TemplateMarket
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
  },
  {
    path: '/compare',
    name: 'Compare',
    component: Compare
  },
  {
    path: '/community',
    name: 'Community',
    component: Community
  },
  {
    path: '/variable-extractor',
    name: 'VariableExtractor',
    component: VariableExtractor
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
