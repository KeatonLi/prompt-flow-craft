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
import PromptScore from '../views/PromptScore.vue'
import PromptConvert from '../views/PromptConvert.vue'
import PromptFormatter from '../views/PromptFormatter.vue'
import QualityChecker from '../views/QualityChecker.vue'
import PromptPlayground from '../views/PromptPlayground.vue'
import PromptStyleTransfer from '../views/PromptStyleTransfer.vue'
import Statistics from '../views/Statistics.vue'
import PromptVariants from '../views/PromptVariants.vue'
import TemplateEditor from '../views/TemplateEditor.vue'
import StructureAnalyzer from '../views/StructureAnalyzer.vue'

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
  },
  {
    path: '/prompt-score',
    name: 'PromptScore',
    component: PromptScore
  },
  {
    path: '/prompt-convert',
    name: 'PromptConvert',
    component: PromptConvert
  },
  {
    path: '/prompt-formatter',
    name: 'PromptFormatter',
    component: PromptFormatter
  },
  {
    path: '/quality-checker',
    name: 'QualityChecker',
    component: QualityChecker
  },
  {
    path: '/playground',
    name: 'PromptPlayground',
    component: PromptPlayground
  },
  {
    path: '/style-transfer',
    name: 'PromptStyleTransfer',
    component: PromptStyleTransfer
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
    path: '/structure-analyzer',
    name: 'StructureAnalyzer',
    component: StructureAnalyzer
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
