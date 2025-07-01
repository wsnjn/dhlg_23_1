<!--
 * 文件: DataAnalytics.vue - 数据分析页面
 * 描述: 管理端数据分析页面，使用原生HTML实现
 * 功能:
 *   - 多维度数据可视化
 *   - 趋势分析图表
 *   - 对比分析功能
 *   - 数据洞察报告
 * 作者: 项目小组1组
 * 版本: 1.0.0
 * 版权: 2025 东莞佰和生物科技有限公司 保留所有权利
 * 许可证: MIT
 * 创建时间: 2025-01-27
 * 最后修改: 2025-07-01
 -->

<template>
  <div class="data-analytics">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2>数据分析</h2>
      <div class="header-actions">
        <select v-model="timeRange" @change="updateCharts" class="time-select">
          <option value="7">最近7天</option>
          <option value="30">最近30天</option>
          <option value="90">最近90天</option>
          <option value="365">最近一年</option>
        </select>
        <button @click="exportReport" class="export-btn">
          <span class="btn-icon">↓</span>
          导出报告
        </button>
      </div>
    </div>

    <!-- 关键指标卡片 -->
    <div class="kpi-cards">
      <div class="cards-row">
        <div class="kpi-card">
          <div class="kpi-content">
            <div class="kpi-icon positive">📈</div>
            <div class="kpi-info">
              <div class="kpi-value">{{ kpiData.testGrowth }}%</div>
              <div class="kpi-label">测试增长率</div>
              <div class="kpi-trend positive">+{{ kpiData.testGrowth }}%</div>
            </div>
          </div>
        </div>
        <div class="kpi-card">
          <div class="kpi-content">
            <div class="kpi-icon neutral">👤</div>
            <div class="kpi-info">
              <div class="kpi-value">{{ kpiData.patientGrowth }}%</div>
              <div class="kpi-label">病人增长率</div>
              <div class="kpi-trend neutral">+{{ kpiData.patientGrowth }}%</div>
            </div>
          </div>
        </div>
        <div class="kpi-card">
          <div class="kpi-content">
            <div class="kpi-icon positive">📊</div>
            <div class="kpi-info">
              <div class="kpi-value">{{ kpiData.avgScore }}</div>
              <div class="kpi-label">平均得分</div>
              <div class="kpi-trend positive">+{{ kpiData.scoreImprovement }}%</div>
            </div>
          </div>
        </div>
        <div class="kpi-card">
          <div class="kpi-content">
            <div class="kpi-icon negative">⚠️</div>
            <div class="kpi-info">
              <div class="kpi-value">{{ kpiData.abnormalRate }}%</div>
              <div class="kpi-label">异常检出率</div>
              <div class="kpi-trend negative">+{{ kpiData.abnormalRate }}%</div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 图表区域 -->
    <div class="charts-section">
      <!-- 第一行图表 -->
      <div class="chart-row">
        <div class="chart-card">
          <div class="chart-header">
            <span>测试趋势分析</span>
            <div class="chart-options">
              <label><input type="radio" v-model="trendChartType" value="daily" /> 日趋势</label>
              <label><input type="radio" v-model="trendChartType" value="weekly" /> 周趋势</label>
              <label><input type="radio" v-model="trendChartType" value="monthly" /> 月趋势</label>
            </div>
          </div>
          <div class="chart-container" ref="trendChart"></div>
        </div>
        <div class="chart-card">
          <div class="chart-header">
            <span>测试类型分布</span>
          </div>
          <div class="chart-container" ref="testTypeChart"></div>
        </div>
      </div>

      <!-- 第二行图表 -->
      <div class="chart-row">
        <div class="chart-card">
          <div class="chart-header">
            <span>年龄分布分析</span>
          </div>
          <div class="chart-container" ref="ageChart"></div>
        </div>
        <div class="chart-card">
          <div class="chart-header">
            <span>得分分布热力图</span>
          </div>
          <div class="chart-container" ref="heatmapChart"></div>
        </div>
      </div>

      <!-- 第三行图表 -->
      <div class="chart-row">
        <div class="chart-card full-width">
          <div class="chart-header">
            <span>多维度对比分析</span>
            <select v-model="comparisonDimension" class="dimension-select">
              <option value="testType">测试类型</option>
              <option value="ageGroup">年龄段</option>
              <option value="gender">性别</option>
            </select>
          </div>
          <div class="chart-container" ref="comparisonChart"></div>
        </div>
      </div>
    </div>

    <!-- 数据洞察 -->
    <div class="insights-card">
      <div class="card-header">
        <span>数据洞察</span>
        <button @click="generateInsights" class="insight-btn">生成洞察</button>
      </div>
      <div class="insights-content">
        <div class="insights-row">
          <div class="insight-item" v-for="insight in insights" :key="insight.id">
            <div class="insight-icon" :class="insight.type">
              <span v-if="insight.type === 'positive'">📈</span>
              <span v-else-if="insight.type === 'warning'">⚠️</span>
              <span v-else>📊</span>
            </div>
            <div class="insight-content">
              <h4>{{ insight.title }}</h4>
              <p>{{ insight.description }}</p>
              <div class="insight-metric">
                <span class="metric-value">{{ insight.metric }}</span>
                <span class="metric-label">{{ insight.metricLabel }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import * as echarts from 'echarts'

export default {
  name: 'DataAnalytics',
  data() {
    return {
      timeRange: '30',
      trendChartType: 'daily',
      comparisonDimension: 'testType',
      kpiData: {
        testGrowth: 15.2,
        patientGrowth: 8.7,
        avgScore: 78.5,
        scoreImprovement: 5.3,
        abnormalRate: 12.8,
      },
      insights: [
        {
          id: 1,
          type: 'positive',
          title: '测试参与度提升',
          description: '本月测试参与度较上月提升了15.2%，用户活跃度显著提高。',
          metric: '15.2%',
          metricLabel: '增长率',
        },
        {
          id: 2,
          type: 'warning',
          title: '异常检出率上升',
          description: '异常检出率较上月上升了2.1%，建议加强早期筛查。',
          metric: '12.8%',
          metricLabel: '检出率',
        },
        {
          id: 3,
          type: 'positive',
          title: '平均得分改善',
          description: '整体平均得分提升了5.3%，治疗效果良好。',
          metric: '78.5',
          metricLabel: '平均分',
        },
      ],
      trendChart: null,
      testTypeChart: null,
      ageChart: null,
      heatmapChart: null,
      comparisonChart: null,
    }
  },
  methods: {
    initCharts() {
      // 初始化趋势图
      this.trendChart = echarts.init(this.$refs.trendChart)
      this.trendChart.setOption({
        title: {
          text: '测试趋势分析',
          left: 'center',
        },
        tooltip: {
          trigger: 'axis',
        },
        legend: {
          data: ['AD8测试', 'PSQI测试', '认知评估', '情感聊天'],
        },
        xAxis: {
          type: 'category',
          data: [
            '1月',
            '2月',
            '3月',
            '4月',
            '5月',
            '6月',
            '7月',
            '8月',
            '9月',
            '10月',
            '11月',
            '12月',
          ],
        },
        yAxis: {
          type: 'value',
        },
        series: [
          {
            name: 'AD8测试',
            type: 'line',
            data: [20, 25, 30, 35, 40, 45, 50, 55, 60, 65, 70, 75],
            smooth: true,
          },
          {
            name: 'PSQI测试',
            type: 'line',
            data: [15, 20, 25, 30, 35, 40, 45, 50, 55, 60, 65, 70],
            smooth: true,
          },
          {
            name: '认知评估',
            type: 'line',
            data: [10, 15, 20, 25, 30, 35, 40, 45, 50, 55, 60, 65],
            smooth: true,
          },
          {
            name: '情感聊天',
            type: 'line',
            data: [5, 10, 15, 20, 25, 30, 35, 40, 45, 50, 55, 60],
            smooth: true,
          },
        ],
      })

      // 初始化测试类型分布图
      this.testTypeChart = echarts.init(this.$refs.testTypeChart)
      this.testTypeChart.setOption({
        title: {
          text: '测试类型分布',
          left: 'center',
        },
        tooltip: {
          trigger: 'item',
          formatter: '{a} <br/>{b}: {c} ({d}%)',
        },
        legend: {
          orient: 'vertical',
          left: 'left',
        },
        series: [
          {
            name: '测试类型',
            type: 'pie',
            radius: ['40%', '70%'],
            avoidLabelOverlap: false,
            label: {
              show: false,
              position: 'center',
            },
            emphasis: {
              label: {
                show: true,
                fontSize: '30',
                fontWeight: 'bold',
              },
            },
            labelLine: {
              show: false,
            },
            data: [
              { value: 35, name: 'AD8测试' },
              { value: 25, name: 'PSQI测试' },
              { value: 20, name: '认知评估' },
              { value: 15, name: '情感聊天' },
              { value: 5, name: '其他测试' },
            ],
          },
        ],
      })

      // 初始化年龄分布图
      this.ageChart = echarts.init(this.$refs.ageChart)
      this.ageChart.setOption({
        title: {
          text: '年龄分布分析',
          left: 'center',
        },
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'shadow',
          },
        },
        xAxis: {
          type: 'category',
          data: ['18-30岁', '31-50岁', '51-70岁', '70岁以上'],
        },
        yAxis: {
          type: 'value',
        },
        series: [
          {
            name: '人数',
            type: 'bar',
            data: [120, 280, 450, 200],
            itemStyle: {
              color: function (params) {
                const colors = ['#91cc75', '#fac858', '#ee6666', '#73c0de']
                return colors[params.dataIndex]
              },
            },
          },
        ],
      })

      // 初始化热力图
      this.heatmapChart = echarts.init(this.$refs.heatmapChart)
      this.heatmapChart.setOption({
        title: {
          text: '得分分布热力图',
          left: 'center',
        },
        tooltip: {
          position: 'top',
        },
        grid: {
          height: '50%',
          top: '10%',
        },
        xAxis: {
          type: 'category',
          data: ['AD8测试', 'PSQI测试', '认知评估', '情感聊天'],
          splitArea: {
            show: true,
          },
        },
        yAxis: {
          type: 'category',
          data: ['0-20', '21-40', '41-60', '61-80', '81-100'],
          splitArea: {
            show: true,
          },
        },
        visualMap: {
          min: 0,
          max: 10,
          calculable: true,
          orient: 'horizontal',
          left: 'center',
          bottom: '15%',
        },
        series: [
          {
            name: '得分分布',
            type: 'heatmap',
            data: [
              [0, 0, 5],
              [0, 1, 8],
              [0, 2, 12],
              [0, 3, 15],
              [0, 4, 10],
              [1, 0, 3],
              [1, 1, 6],
              [1, 2, 10],
              [1, 3, 12],
              [1, 4, 8],
              [2, 0, 2],
              [2, 1, 4],
              [2, 2, 8],
              [2, 3, 10],
              [2, 4, 6],
              [3, 0, 1],
              [3, 1, 3],
              [3, 2, 5],
              [3, 3, 8],
              [3, 4, 4],
            ],
            label: {
              show: true,
            },
            emphasis: {
              itemStyle: {
                shadowBlur: 10,
                shadowColor: 'rgba(0, 0, 0, 0.5)',
              },
            },
          },
        ],
      })

      // 初始化对比图
      this.comparisonChart = echarts.init(this.$refs.comparisonChart)
      this.comparisonChart.setOption({
        title: {
          text: '多维度对比分析',
          left: 'center',
        },
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'shadow',
          },
        },
        legend: {
          data: ['平均得分', '测试次数', '异常率'],
        },
        xAxis: {
          type: 'category',
          data: ['AD8测试', 'PSQI测试', '认知评估', '情感聊天'],
        },
        yAxis: [
          {
            type: 'value',
            name: '得分/次数',
            position: 'left',
          },
          {
            type: 'value',
            name: '异常率(%)',
            position: 'right',
          },
        ],
        series: [
          {
            name: '平均得分',
            type: 'bar',
            data: [82, 75, 88, 79],
          },
          {
            name: '测试次数',
            type: 'bar',
            data: [150, 120, 100, 80],
          },
          {
            name: '异常率',
            type: 'line',
            yAxisIndex: 1,
            data: [12, 18, 8, 15],
          },
        ],
      })
    },
    updateCharts() {
      alert(`已更新为最近${this.timeRange}天的数据`)
    },
    generateInsights() {
      alert('数据洞察已更新')
    },
    exportReport() {
      alert('报告导出功能开发中...')
    },
    handleResize() {
      if (this.trendChart) this.trendChart.resize()
      if (this.testTypeChart) this.testTypeChart.resize()
      if (this.ageChart) this.ageChart.resize()
      if (this.heatmapChart) this.heatmapChart.resize()
      if (this.comparisonChart) this.comparisonChart.resize()
    },
  },
  watch: {
    trendChartType(newType) {
      alert(`已切换到${newType === 'daily' ? '日' : newType === 'weekly' ? '周' : '月'}趋势`)
    },
    comparisonDimension(newDimension) {
      alert(
        `已切换到${newDimension === 'testType' ? '测试类型' : newDimension === 'ageGroup' ? '年龄段' : '性别'}对比`,
      )
    },
  },
  mounted() {
    this.initCharts()
    window.addEventListener('resize', this.handleResize)
  },
  beforeUnmount() {
    window.removeEventListener('resize', this.handleResize)
    if (this.trendChart) this.trendChart.dispose()
    if (this.testTypeChart) this.testTypeChart.dispose()
    if (this.ageChart) this.ageChart.dispose()
    if (this.heatmapChart) this.heatmapChart.dispose()
    if (this.comparisonChart) this.comparisonChart.dispose()
  },
}
</script>

<style scoped>
.data-analytics {
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.page-header h2 {
  margin: 0;
  color: #2c3e50;
}

.header-actions {
  display: flex;
  gap: 15px;
  align-items: center;
}

.kpi-cards {
  margin-bottom: 30px;
}

.kpi-card {
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  padding: 15px;
  margin: 10px;
}

.cards-row {
  display: flex;
  flex-wrap: wrap;
  justify-content: space-between;
}

.kpi-content {
  display: flex;
  align-items: center;
  gap: 15px;
}

.kpi-icon {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  color: white;
}

.kpi-icon.positive {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
}

.kpi-icon.neutral {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.kpi-icon.negative {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.kpi-info {
  flex: 1;
}

.kpi-value {
  font-size: 28px;
  font-weight: bold;
  color: #2c3e50;
  line-height: 1;
}

.kpi-label {
  font-size: 14px;
  color: #7f8c8d;
  margin: 5px 0;
}

.kpi-trend {
  font-size: 12px;
  font-weight: 500;
}

.kpi-trend.positive {
  color: #67c23a;
}

.kpi-trend.neutral {
  color: #409eff;
}

.kpi-trend.negative {
  color: #f56c6c;
}

.charts-section {
  margin-bottom: 30px;
}

.chart-row {
  display: flex;
  flex-wrap: wrap;
  margin-bottom: 20px;
  gap: 20px;
}

.chart-card {
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  padding: 15px;
  flex: 1;
  min-width: 300px;
}

.chart-card.full-width {
  flex: 100%;
}

.chart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 16px;
  font-weight: 600;
  color: #2c3e50;
  margin-bottom: 15px;
}

.chart-container {
  height: 350px;
  width: 100%;
}

.chart-options {
  display: flex;
  gap: 10px;
}

.insights-card {
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  padding: 15px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.insights-content {
  padding: 10px 0;
}

.insights-row {
  display: flex;
  flex-wrap: wrap;
  gap: 15px;
}

.insight-item {
  display: flex;
  align-items: flex-start;
  gap: 15px;
  padding: 15px;
  border-radius: 8px;
  background: #f8f9fa;
  flex: 1;
  min-width: 250px;
}

.insight-icon {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  color: white;
  flex-shrink: 0;
}

.insight-icon.positive {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
}

.insight-icon.warning {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.insight-content {
  flex: 1;
}

.insight-content h4 {
  margin: 0 0 8px 0;
  color: #2c3e50;
  font-size: 16px;
}

.insight-content p {
  margin: 0 0 10px 0;
  color: #7f8c8d;
  font-size: 14px;
  line-height: 1.5;
}

.insight-metric {
  display: flex;
  align-items: center;
  gap: 8px;
}

.metric-value {
  font-size: 18px;
  font-weight: bold;
  color: #2c3e50;
}

.metric-label {
  font-size: 12px;
  color: #7f8c8d;
}

.time-select {
  padding: 8px 12px;
  border-radius: 4px;
  border: 1px solid #dcdfe6;
}

.export-btn {
  padding: 8px 16px;
  background-color: #409eff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 5px;
}

.insight-btn {
  padding: 6px 12px;
  background-color: #409eff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.dimension-select {
  padding: 6px 12px;
  border-radius: 4px;
  border: 1px solid #dcdfe6;
}
</style>
