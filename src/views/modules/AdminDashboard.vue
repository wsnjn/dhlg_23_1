<!--
 * 文件: AdminDashboard.vue - 管理端主页面
 * 描述: 佰和护理中心管理端，提供病人数据管理和可视化
 * 功能:
 *   - 显示病人数量统计
 *   - 提供数据可视化图表
 *   - 病人详情管理
 *   - 测试结果分析
 * 作者: 项目小组1组
 * 版本: 1.0.0
 * 版权: 2025 东莞佰和生物科技有限公司 保留所有权利
 * 许可证: MIT
 * 创建时间: 2025-01-27
 * 最后修改: 2025-01-27
 -->

<template>
  <div class="admin-dashboard">
    <!-- 顶部导航栏 -->
    <div class="header">
      <div class="header-content">
        <h1 class="logo">佰和护理中心 - 管理端</h1>
        <div class="user-info">
          <div class="user-avatar">
            <span class="avatar-text">管</span>
          </div>
          <span class="username">管理员</span>
        </div>
      </div>
    </div>

    <!-- 主要内容区域 -->
    <div class="main-container">
      <!-- 侧边栏 -->
      <div class="sidebar">
        <div class="sidebar-menu">
          <div
            class="menu-item"
            :class="{ active: activeMenu === 'dashboard' }"
            @click="handleMenuSelect('dashboard')"
          >
            <div class="menu-icon">📊</div>
            <span>数据概览</span>
          </div>
          <div
            class="menu-item"
            :class="{ active: activeMenu === 'patients' }"
            @click="handleMenuSelect('patients')"
          >
            <div class="menu-icon">👥</div>
            <span>病人管理</span>
          </div>
          <div
            class="menu-item"
            :class="{ active: activeMenu === 'tests' }"
            @click="handleMenuSelect('tests')"
          >
            <div class="menu-icon">📋</div>
            <span>测试记录</span>
          </div>
          <div
            class="menu-item"
            :class="{ active: activeMenu === 'analytics' }"
            @click="handleMenuSelect('analytics')"
          >
            <div class="menu-icon">📈</div>
            <span>数据分析</span>
          </div>
          <div
            class="menu-item"
            :class="{ active: activeMenu === 'reports' }"
            @click="handleMenuSelect('reports')"
          >
            <div class="menu-icon">📄</div>
            <span>统计报告</span>
          </div>
        </div>
      </div>

      <!-- 主内容区 -->
      <div class="main-content">
        <!-- 数据概览页面 -->
        <div v-if="activeMenu === 'dashboard'" class="dashboard-content">
          <!-- 统计卡片 -->
          <div class="stats-cards">
            <div class="stat-card">
              <div class="stat-content">
                <div class="stat-icon patients-icon">
                  <div class="icon-text">病</div>
                </div>
                <div class="stat-info">
                  <div class="stat-number">{{ patientStats.total }}</div>
                  <div class="stat-label">总病人数</div>
                </div>
              </div>
            </div>

            <div class="stat-card">
              <div class="stat-content">
                <div class="stat-icon tests-icon">
                  <div class="icon-text">测</div>
                </div>
                <div class="stat-info">
                  <div class="stat-number">{{ patientStats.tests }}</div>
                  <div class="stat-label">测试总数</div>
                </div>
              </div>
            </div>

            <div class="stat-card">
              <div class="stat-content">
                <div class="stat-icon new-icon">
                  <div class="icon-text">新</div>
                </div>
                <div class="stat-info">
                  <div class="stat-number">{{ patientStats.newThisMonth }}</div>
                  <div class="stat-label">本月新增</div>
                </div>
              </div>
            </div>

            <div class="stat-card">
              <div class="stat-content">
                <div class="stat-icon active-icon">
                  <div class="icon-text">活</div>
                </div>
                <div class="stat-info">
                  <div class="stat-number">{{ patientStats.active }}</div>
                  <div class="stat-label">活跃病人</div>
                </div>
              </div>
            </div>
          </div>

          <!-- 图表区域 -->
          <div class="charts-container">
            <div class="charts-row">
              <div class="chart-card">
                <div class="chart-header">
                  <span>病人增长趋势</span>
                </div>
                <div class="chart-content">
                  <v-chart class="chart" :option="patientGrowthOption" />
                </div>
              </div>
              <div class="chart-card">
                <div class="chart-header">
                  <span>测试类型分布</span>
                </div>
                <div class="chart-content">
                  <v-chart class="chart" :option="testTypeOption" />
                </div>
              </div>
            </div>

            <div class="charts-row">
              <div class="chart-card full-width">
                <div class="chart-header">
                  <span>月度测试统计</span>
                </div>
                <div class="chart-content">
                  <v-chart class="chart" :option="monthlyTestOption" />
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 病人管理页面 -->
        <div v-if="activeMenu === 'patients'" class="patients-content">
          <PatientManagement />
        </div>

        <!-- 测试记录页面 -->
        <div v-if="activeMenu === 'tests'" class="tests-content">
          <TestRecords />
        </div>

        <!-- 数据分析页面 -->
        <div v-if="activeMenu === 'analytics'" class="analytics-content">
          <DataAnalytics />
        </div>

        <!-- 统计报告页面 -->
        <div v-if="activeMenu === 'reports'" class="reports-content">
          <StatisticalReports />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { use } from 'echarts/core'
import { CanvasRenderer } from 'echarts/renderers'
import { LineChart, BarChart } from 'echarts/charts'
import {
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  GridComponent,
} from 'echarts/components'
import VChart from 'vue-echarts'

import PatientManagement from './admin/PatientManagement.vue'
import TestRecords from './admin/TestRecords.vue'
import DataAnalytics from './admin/DataAnalytics.vue'
import StatisticalReports from './admin/StatisticalReports.vue'

// 注册 ECharts 组件
use([
  CanvasRenderer,
  LineChart,
  BarChart,
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  GridComponent,
])

// 响应式数据
const activeMenu = ref('dashboard')
const patientStats = ref({
  total: 16, // 病人人数，暂时写死为16，实际应为users表数量
  tests: 0, // 测试总数，后面通过接口获取
  newThisMonth: 0, // 本月新增，暂时写死为0
  active: 0, // 活跃病人，暂时写死为0
})

// 图表配置
const patientGrowthOption = ref({
  title: {
    text: '病人增长趋势',
    left: 'center',
  },
  tooltip: {
    trigger: 'axis',
  },
  xAxis: {
    type: 'category',
    data: ['1月', '2月', '3月', '4月', '5月', '6月', '7月', '8月', '9月', '10月', '11月', '12月'],
  },
  yAxis: {
    type: 'value',
  },
  series: [
    {
      name: '新增病人',
      type: 'line',
      data: [12, 19, 15, 25, 22, 30, 28, 35, 40, 38, 45, 50],
      smooth: true,
      itemStyle: {
        color: '#409EFF',
      },
    },
  ],
})

const testTypeOption = ref({
  title: {
    text: '测试类型分布',
    left: 'center',
  },
  tooltip: {
    trigger: 'item',
  },
  legend: {
    orient: 'vertical',
    left: 'left',
  },
  series: [
    {
      name: '测试类型',
      type: 'pie',
      radius: '50%',
      data: [
        { value: 35, name: 'AD8测试' },
        { value: 25, name: 'PSQI测试' },
        { value: 20, name: '认知评估' },
        { value: 15, name: '情感聊天' },
        { value: 5, name: '其他测试' },
      ],
      emphasis: {
        itemStyle: {
          shadowBlur: 10,
          shadowOffsetX: 0,
          shadowColor: 'rgba(0, 0, 0, 0.5)',
        },
      },
    },
  ],
})

const monthlyTestOption = ref({
  title: {
    text: '月度测试统计',
    left: 'center',
  },
  tooltip: {
    trigger: 'axis',
    axisPointer: {
      type: 'shadow',
    },
  },
  legend: {
    data: ['AD8测试', 'PSQI测试', '认知评估', '情感聊天'],
  },
  xAxis: {
    type: 'category',
    data: ['1月', '2月', '3月', '4月', '5月', '6月', '7月', '8月', '9月', '10月', '11月', '12月'],
  },
  yAxis: {
    type: 'value',
  },
  series: [
    {
      name: 'AD8测试',
      type: 'bar',
      data: [20, 25, 30, 35, 40, 45, 50, 55, 60, 65, 70, 75],
    },
    {
      name: 'PSQI测试',
      type: 'bar',
      data: [15, 20, 25, 30, 35, 40, 45, 50, 55, 60, 65, 70],
    },
    {
      name: '认知评估',
      type: 'bar',
      data: [10, 15, 20, 25, 30, 35, 40, 45, 50, 55, 60, 65],
    },
    {
      name: '情感聊天',
      type: 'bar',
      data: [5, 10, 15, 20, 25, 30, 35, 40, 45, 50, 55, 60],
    },
  ],
})

// 方法
const handleMenuSelect = (index) => {
  activeMenu.value = index
}

const loadPatientStats = async () => {
  // 测试总数：调用 /api/user/ad8_responses 和 /api/user/psqi_responses，参数 name=牛迦楠，hospital_number=管理员，统计答题总数
  let total = 0
  try {
    const apiBaseUrl = 'http://localhost:8080'
    const ad8Url = `${apiBaseUrl}/api/user/ad8_responses`
    const psqiUrl = `${apiBaseUrl}/api/user/psqi_responses`
    const params = {
      hospitalId: '管理员', // 按你的接口参数要求
      patientName: '牛迦楠',
    }
    const ad8UrlWithParams = `${ad8Url}?${new URLSearchParams(params)}`
    const psqiUrlWithParams = `${psqiUrl}?${new URLSearchParams(params)}`
    const [ad8Response, psqiResponse] = await Promise.all([
      fetch(ad8UrlWithParams),
      fetch(psqiUrlWithParams),
    ])
    const ad8Data = await ad8Response.json()
    const psqiData = await psqiResponse.json()
    console.log('AD8 API Response:', ad8Data)
    console.log('PSQI API Response:', psqiData)
    total =
      (Array.isArray(ad8Data.data) ? ad8Data.data.length : 0) +
      (Array.isArray(psqiData.data) ? psqiData.data.length : 0)
  } catch (err) {
    console.error('获取测试总数接口出错:', err)
  }
  patientStats.value.tests = total // 测试总数
  // 本月新增和活跃病人已写死为0
}

// 生命周期
onMounted(async () => {
  await loadPatientStats()
})
</script>

<style scoped>
.admin-dashboard {
  height: 100vh;
  display: flex;
  flex-direction: column;
  background: #f5f7fa;
}

.header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  height: 60px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 100%;
  padding: 0 20px;
}

.logo {
  margin: 0;
  font-size: 20px;
  font-weight: 600;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.user-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.2);
  display: flex;
  align-items: center;
  justify-content: center;
  border: 2px solid rgba(255, 255, 255, 0.3);
}

.avatar-text {
  font-size: 16px;
  font-weight: bold;
  color: white;
}

.username {
  font-size: 14px;
  font-weight: 500;
}

.main-container {
  flex: 1;
  display: flex;
  height: calc(100vh - 60px);
}

.sidebar {
  width: 250px;
  background: #2c3e50;
  box-shadow: 2px 0 8px rgba(0, 0, 0, 0.1);
}

.sidebar-menu {
  padding: 20px 0;
}

.menu-item {
  display: flex;
  align-items: center;
  padding: 15px 20px;
  color: #bdc3c7;
  cursor: pointer;
  transition: all 0.3s ease;
  border-left: 3px solid transparent;
}

.menu-item:hover {
  background: rgba(255, 255, 255, 0.1);
  color: #ecf0f1;
}

.menu-item.active {
  background: rgba(52, 152, 219, 0.2);
  color: #3498db;
  border-left-color: #3498db;
}

.menu-icon {
  font-size: 18px;
  margin-right: 12px;
  width: 20px;
  text-align: center;
}

.main-content {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
}

.dashboard-content {
  max-width: 1200px;
  margin: 0 auto;
}

.stats-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
}

.stat-card {
  background: white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  transition:
    transform 0.3s ease,
    box-shadow 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
}

.stat-content {
  display: flex;
  align-items: center;
  gap: 20px;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  color: white;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
}

.icon-text {
  font-size: 20px;
  font-weight: bold;
  color: white;
}

.patients-icon {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.tests-icon {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.new-icon {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.active-icon {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
}

.stat-info {
  flex: 1;
}

.stat-number {
  font-size: 32px;
  font-weight: bold;
  color: #2c3e50;
  line-height: 1;
  margin-bottom: 5px;
}

.stat-label {
  font-size: 14px;
  color: #7f8c8d;
  font-weight: 500;
}

.charts-container {
  margin-top: 20px;
}

.charts-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
  margin-bottom: 20px;
}

.chart-card {
  background: white;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  transition:
    transform 0.3s ease,
    box-shadow 0.3s ease;
}

.chart-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
}

.chart-card.full-width {
  grid-column: 1 / -1;
}

.chart-header {
  padding: 20px;
  border-bottom: 1px solid #ecf0f1;
  font-size: 16px;
  font-weight: 600;
  color: #2c3e50;
  background: #f8f9fa;
}

.chart-content {
  padding: 20px;
}

.chart {
  height: 300px;
  width: 100%;
}

.patients-content,
.tests-content,
.analytics-content,
.reports-content {
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  min-height: 600px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .sidebar {
    width: 200px;
  }

  .stats-cards {
    grid-template-columns: 1fr;
  }

  .charts-row {
    grid-template-columns: 1fr;
  }

  .main-content {
    padding: 15px;
  }
}

@media (max-width: 480px) {
  .sidebar {
    width: 60px;
  }

  .menu-item span {
    display: none;
  }

  .menu-icon {
    margin-right: 0;
  }

  .header-content {
    padding: 0 15px;
  }

  .logo {
    font-size: 16px;
  }
}
</style>
