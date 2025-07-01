/*
 * 文件: index.js - 应用路由配置
 * 作者: 项目小组1组
 * 版本: 1.0.0
 * 版权: 2025 东莞佰和生物科技有限公司 保留所有权利
 * 许可证: MIT
 * 创建时间: 2025-06-20
 * 最后修改: 2025-06-29
 */

import { createRouter, createWebHashHistory } from 'vue-router'

const router = createRouter({
  history: createWebHashHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: () => import('../views/baihe.vue'),
    },
    {
      path: '/ad8-test',
      name: 'AD8Test',
      component: () => import('../views/modules/cognitive-test/login/Ad8/AD8Test.vue'),
    },
    {
      path: '/psqi',
      name: 'PAQIText',
      component: () => import('../views/modules/cognitive-test/login/PSQI/PAQIText.vue'),
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
    {
      path: '/admin',
      name: 'admin',
      component: () => import('../views/modules/AdminDashboard.vue'),
    },
  ],
})

export default router
