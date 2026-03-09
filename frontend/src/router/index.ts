import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'
import About from '../views/About.vue'
import Templates from '../views/Templates.vue'
import TemplateMarket from '../views/TemplateMarket.vue'
import Popular from '../views/Popular.vue'
import Statistics from '../views/Statistics.vue'
import PromptVariants from '../views/PromptVariants.vue'
import TemplateEditor from '../views/TemplateEditor.vue'
import PromptPlayground from '../views/PromptPlayground.vue'

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
    path: '/template-editor',
    name: 'TemplateEditor',
    component: TemplateEditor
  },
  {
    path: '/playground',
    name: 'PromptPlayground',
    component: PromptPlayground
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
