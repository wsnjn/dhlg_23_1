//import { createRouter, createWebHistory } from 'vue-router'
import { createRouter, createWebHashHistory } from 'vue-router'

const router = createRouter({
  history: createWebHashHistory(import.meta.env.BASE_URL),
  routes: [
      {
        path: '/',
        name: 'home',
        component: () => import('../views/baihe.vue')
      },
      {
        path: '/ad8-test',
        name: 'AD8Test',
        component: () => import('../views/modules/cognitive-test/login/Ad8/AD8Test.vue')
      },
      {
        path: '/psqi',
        name: 'PAQIText',
        component: () => import('../views/modules/cognitive-test/login/PSQI/PAQIText.vue')
      },
    {
      path: '/customer-center',
      name: 'customer-center',
      component: () => import('../views/modules/CustomerCenter.vue'),
    },
    {
      path: '/scale-test',
      name: 'scale-test',
      component: () => import('../views/modules/ScaleTest.vue'),
    },
    {
      path: '/ai-analysis',
      name: 'ai-analysis',
      component: () => import('../views/modules/AIAnalysis.vue'),
    },
    {
      path: '/emotion-chat',
      name: 'emotion-chat',
      component: () => import('../views/modules/EmotionChat.vue'),
    },
    {
      path: '/entertainment',
      name: 'entertainment',
      component: () => import('../views/modules/Entertainment.vue'),
    },
    {
      path: '/dementia-test',
      name: 'dementia-test',
      component: () => import('../views/modules/dementia-test/DementiaTest.vue'),
    },
    {
      path: '/cognitive-assessment',
      name: 'cognitive-assessment',
      component: () => import('../views/modules/cognitive-test/CognitiveAssessment.vue'),
    },
    {
      path: '/rehabilitation-plan',
      name: 'rehabilitation-plan',
      component: () => import('../views/modules/cognitive-test/RehabilitationPlan.vue'),
    },
    {
      path: '/progress-tracking',
      name: 'progress-tracking',
      component: () => import('../views/modules/cognitive-test/ProgressTracking.vue'),
    },
    {
      path: '/report-analysis',
      name: 'report-analysis',
      component: () => import('../views/modules/cognitive-test/ReportAnalysis.vue'),
    },
    {
      path: '/login',
      name: 'login',
      component: () => import('../views/modules/cognitive-test/login/login.vue'),
    },
    {
      path: '/text',
      name: 'text',
      component: () => import('../views/modules/cognitive-test/login/text/text.vue'),
    },
    {
      path: '/draw',
      name: 'draw',
      component: () => import('../views/modules/cognitive-test/login/text/draw.vue'),
    },
    {
      path: '/select',
      name: 'select',
      component: () => import('../views/modules/cognitive-test/login/text/select.vue'),
    },
    {
      path: '/key',
      name: 'key',
      component: () => import('../views/modules/cognitive-test/login/text/key.vue'),
    },
    {
      path: '/test-details',
      name: 'test-details',
      component: () => import('../views/modules/TestDetails.vue'),
    },
  ],
})

export default router
