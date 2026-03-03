import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'
import About from '../views/About.vue'
import Templates from '../views/Templates.vue'
import TemplateMarket from '../views/TemplateMarket.vue'
import Popular from '../views/Popular.vue'
import PromptPlayground from '../views/PromptPlayground.vue'
import Statistics from '../views/Statistics.vue'
import PromptVariants from '../views/PromptVariants.vue'
import PromptWorkflow from '../views/PromptWorkflow.vue'

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
    path: '/playground',
    name: 'PromptPlayground',
    component: PromptPlayground
  },
  {
    path: '/statistics',
    name: 'Statistics',
    component: Statistics
  },
  {
    path: '/variants',
    name: 'PromptVariants',
    component: PromptVariants
  },
  {
    path: '/workflow',
    name: 'PromptWorkflow',
    component: PromptWorkflow
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
