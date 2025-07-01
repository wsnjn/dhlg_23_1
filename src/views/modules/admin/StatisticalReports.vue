<!--
 * 文件: StatisticalReports.vue - 统计报告组件
 * 描述: 管理端统计报告页面，提供各种统计报告生成和导出功能
 * 功能:
 *   - 月度/季度/年度报告
 *   - 自定义报告生成
 *   - 报告预览和导出
 *   - 历史报告管理
 * 作者: 项目小组1组
 * 版本: 1.0.0
 * 版权: 2025 东莞佰和生物科技有限公司 保留所有权利
 * 许可证: MIT
 * 创建时间: 2025-01-27
 * 最后修改: 2025-01-27
 -->

<template>
  <div class="statistical-reports">
    <!-- 页面标题和操作栏 -->
    <div class="page-header">
      <h2>统计报告</h2>
      <div class="header-actions">
        <button class="btn btn-primary" @click="showCreateReportDialog">创建报告</button>
        <button class="btn btn-secondary" @click="showTemplateDialog">报告模板</button>
      </div>
    </div>

    <!-- 快速报告卡片 -->
    <div class="quick-reports">
      <div class="reports-grid">
        <div
          v-for="report in quickReports"
          :key="report.id"
          class="quick-report-card"
          @click="generateQuickReport(report)"
        >
          <div class="report-icon" :class="report.type">
            <div class="icon-placeholder">{{ report.title.charAt(0) }}</div>
          </div>
          <div class="report-info">
            <h3>{{ report.title }}</h3>
            <p>{{ report.description }}</p>
            <div class="report-meta">
              <span class="last-update">最后更新: {{ report.lastUpdate }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 报告列表 -->
    <div class="reports-list-card">
      <div class="card-header">
        <span>报告列表</span>
        <div class="header-filters">
          <select v-model="filterType" class="filter-select">
            <option value="">报告类型</option>
            <option value="monthly">月度报告</option>
            <option value="quarterly">季度报告</option>
            <option value="yearly">年度报告</option>
            <option value="custom">自定义报告</option>
          </select>
          <input v-model="filterDate" type="date" class="filter-input" placeholder="筛选日期" />
          <button class="btn btn-primary" @click="filterReports">筛选</button>
          <button class="btn btn-secondary" @click="resetFilter">重置</button>
        </div>
      </div>

      <div class="table-container">
        <table class="data-table">
          <thead>
            <tr>
              <th>报告ID</th>
              <th>报告标题</th>
              <th>报告类型</th>
              <th>报告周期</th>
              <th>创建时间</th>
              <th>状态</th>
              <th>文件大小</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="report in filteredReports" :key="report.id">
              <td>{{ report.id }}</td>
              <td>{{ report.title }}</td>
              <td>
                <span class="tag" :class="getReportTypeColor(report.type)">
                  {{ getReportTypeName(report.type) }}
                </span>
              </td>
              <td>{{ report.period }}</td>
              <td>{{ formatDate(report.createDate) }}</td>
              <td>
                <span
                  class="tag"
                  :class="report.status === 'completed' ? 'tag-success' : 'tag-warning'"
                >
                  {{ report.status === 'completed' ? '已完成' : '生成中' }}
                </span>
              </td>
              <td>{{ report.size }}</td>
              <td>
                <div class="action-buttons">
                  <button class="btn btn-sm btn-secondary" @click="showPreviewReport(report)">
                    预览
                  </button>
                  <button class="btn btn-sm btn-primary" @click="downloadReport(report)">
                    下载
                  </button>
                  <button class="btn btn-sm btn-danger" @click="deleteReport(report)">删除</button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- 分页 -->
      <div class="pagination-container">
        <div class="pagination">
          <button
            class="btn btn-sm"
            :disabled="currentPage === 1"
            @click="handleCurrentChange(currentPage - 1)"
          >
            上一页
          </button>
          <span class="page-info">第 {{ currentPage }} 页，共 {{ totalPages }} 页</span>
          <button
            class="btn btn-sm"
            :disabled="currentPage === totalPages"
            @click="handleCurrentChange(currentPage + 1)"
          >
            下一页
          </button>
        </div>
      </div>
    </div>

    <!-- 创建报告对话框 -->
    <div v-if="createReportVisible" class="modal-overlay" @click="createReportVisible = false">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h3>创建报告</h3>
          <button class="modal-close" @click="createReportVisible = false">×</button>
        </div>
        <div class="modal-body">
          <form @submit.prevent="createReport" class="form">
            <div class="form-group">
              <label>报告标题:</label>
              <input
                v-model="newReport.title"
                type="text"
                required
                class="form-input"
                placeholder="请输入报告标题"
              />
            </div>
            <div class="form-group">
              <label>报告类型:</label>
              <select v-model="newReport.type" required class="form-input">
                <option value="">请选择报告类型</option>
                <option value="monthly">月度报告</option>
                <option value="quarterly">季度报告</option>
                <option value="yearly">年度报告</option>
                <option value="custom">自定义报告</option>
              </select>
            </div>
            <div class="form-group">
              <label>报告周期:</label>
              <input
                v-if="newReport.type === 'custom'"
                v-model="newReport.period"
                type="text"
                required
                class="form-input"
                placeholder="例如: 2025-01-01 至 2025-01-31"
              />
              <select v-else v-model="newReport.period" required class="form-input">
                <option value="">请选择报告周期</option>
                <option value="2025-01">2025年1月</option>
                <option value="2025-02">2025年2月</option>
                <option value="2025-03">2025年3月</option>
                <option value="2025-Q1">2025年第一季度</option>
                <option value="2025-Q2">2025年第二季度</option>
                <option value="2025">2025年</option>
              </select>
            </div>
            <div class="form-group">
              <label>报告内容:</label>
              <div class="checkbox-group">
                <label class="checkbox-item">
                  <input v-model="newReport.content" type="checkbox" value="patient_stats" />
                  <span>病人统计</span>
                </label>
                <label class="checkbox-item">
                  <input v-model="newReport.content" type="checkbox" value="test_stats" />
                  <span>测试统计</span>
                </label>
                <label class="checkbox-item">
                  <input v-model="newReport.content" type="checkbox" value="trend_analysis" />
                  <span>趋势分析</span>
                </label>
                <label class="checkbox-item">
                  <input v-model="newReport.content" type="checkbox" value="comparison_analysis" />
                  <span>对比分析</span>
                </label>
                <label class="checkbox-item">
                  <input v-model="newReport.content" type="checkbox" value="insights" />
                  <span>数据洞察</span>
                </label>
              </div>
            </div>
            <div class="form-group">
              <label>报告格式:</label>
              <div class="radio-group">
                <label class="radio-item">
                  <input v-model="newReport.format" type="radio" value="pdf" required />
                  <span>PDF</span>
                </label>
                <label class="radio-item">
                  <input v-model="newReport.format" type="radio" value="excel" />
                  <span>Excel</span>
                </label>
                <label class="radio-item">
                  <input v-model="newReport.format" type="radio" value="word" />
                  <span>Word</span>
                </label>
              </div>
            </div>
            <div class="form-actions">
              <button type="button" class="btn btn-secondary" @click="createReportVisible = false">
                取消
              </button>
              <button type="submit" class="btn btn-primary">创建</button>
            </div>
          </form>
        </div>
      </div>
    </div>

    <!-- 报告预览对话框 -->
    <div v-if="previewVisible" class="modal-overlay" @click="previewVisible = false">
      <div class="modal-content large" @click.stop>
        <div class="modal-header">
          <h3>报告预览</h3>
          <button class="modal-close" @click="previewVisible = false">×</button>
        </div>
        <div class="modal-body">
          <div v-if="previewReport" class="report-preview">
            <div class="preview-header">
              <h1>{{ previewReport.title }}</h1>
              <div class="preview-meta">
                <span>报告周期: {{ previewReport.period }}</span>
                <span>生成时间: {{ formatDate(previewReport.createDate) }}</span>
              </div>
            </div>

            <div class="preview-content">
              <!-- 执行摘要 -->
              <section class="preview-section">
                <h2>执行摘要</h2>
                <div class="summary-stats">
                  <div class="summary-grid">
                    <div
                      v-for="stat in previewReport.summary"
                      :key="stat.label"
                      class="summary-item"
                    >
                      <div class="summary-value">{{ stat.value }}</div>
                      <div class="summary-label">{{ stat.label }}</div>
                    </div>
                  </div>
                </div>
              </section>

              <!-- 详细分析 -->
              <section class="preview-section">
                <h2>详细分析</h2>
                <div class="analysis-content">
                  <p v-for="(paragraph, index) in previewReport.analysis" :key="index">
                    {{ paragraph }}
                  </p>
                </div>
              </section>

              <!-- 图表展示 -->
              <section class="preview-section">
                <h2>数据可视化</h2>
                <div class="charts-preview">
                  <div class="charts-grid">
                    <div class="chart-placeholder">
                      <div class="chart-icon">📊</div>
                      <p>趋势分析图表</p>
                    </div>
                    <div class="chart-placeholder">
                      <div class="chart-icon">📈</div>
                      <p>分布分析图表</p>
                    </div>
                  </div>
                </div>
              </section>

              <!-- 结论和建议 -->
              <section class="preview-section">
                <h2>结论和建议</h2>
                <div class="conclusions">
                  <ul>
                    <li v-for="(conclusion, index) in previewReport.conclusions" :key="index">
                      {{ conclusion }}
                    </li>
                  </ul>
                </div>
              </section>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, watch } from 'vue'

// 响应式数据
const currentPage = ref(1)
const pageSize = ref(20)
const totalReports = ref(0)
const createReportVisible = ref(false)
const previewVisible = ref(false)
const filterType = ref('')
const filterDate = ref('')
const previewReport = ref(null)

// 新报告表单
const newReport = reactive({
  title: '',
  type: '',
  period: '',
  content: [],
  format: 'pdf',
})

// 快速报告
const quickReports = ref([
  {
    id: 1,
    type: 'monthly',
    title: '月度报告',
    description: '生成本月病人和测试数据统计报告',
    lastUpdate: '2025-01-27',
  },
  {
    id: 2,
    type: 'quarterly',
    title: '季度报告',
    description: '生成本季度趋势分析和对比报告',
    lastUpdate: '2025-01-27',
  },
  {
    id: 3,
    type: 'yearly',
    title: '年度报告',
    description: '生成年度综合统计和分析报告',
    lastUpdate: '2025-01-27',
  },
  {
    id: 4,
    type: 'custom',
    title: '自定义报告',
    description: '根据自定义条件生成专项报告',
    lastUpdate: '2025-01-27',
  },
])

// 报告列表
const reports = ref([])

// 计算属性
const filteredReports = computed(() => {
  let result = reports.value

  if (filterType.value) {
    result = result.filter((report) => report.type === filterType.value)
  }

  if (filterDate.value) {
    result = result.filter((report) => {
      const reportDate = report.createDate
      return reportDate === filterDate.value
    })
  }

  return result
})

const totalPages = computed(() => Math.ceil(totalReports.value / pageSize.value))

// 监听筛选结果变化，更新总数
watch(
  filteredReports,
  (newValue) => {
    totalReports.value = newValue.length
  },
  { immediate: true },
)

// 方法
const showCreateReportDialog = () => {
  createReportVisible.value = true
  Object.keys(newReport).forEach((key) => {
    newReport[key] = key === 'content' ? [] : key === 'format' ? 'pdf' : ''
  })
}

const showTemplateDialog = () => {
  alert('报告模板功能开发中...')
}

const generateQuickReport = (report) => {
  alert(`正在生成${report.title}...`)
}

const filterReports = () => {
  currentPage.value = 1
}

const resetFilter = () => {
  filterType.value = ''
  filterDate.value = ''
  currentPage.value = 1
}

const handleCurrentChange = (val) => {
  currentPage.value = val
}

const formatDate = (dateString) => {
  if (!dateString) return '无'
  return new Date(dateString).toLocaleDateString()
}

const getReportTypeColor = (type) => {
  const colors = {
    monthly: 'tag-primary',
    quarterly: 'tag-success',
    yearly: 'tag-warning',
    custom: 'tag-info',
  }
  return colors[type] || 'tag-default'
}

const getReportTypeName = (type) => {
  const names = {
    monthly: '月度报告',
    quarterly: '季度报告',
    yearly: '年度报告',
    custom: '自定义报告',
  }
  return names[type] || type
}

const createReport = () => {
  if (!newReport.title || !newReport.type || !newReport.period || newReport.content.length === 0) {
    alert('请填写完整信息')
    return
  }

  const reportId = `R${String(reports.value.length + 1).padStart(3, '0')}`
  const report = {
    id: reportId,
    title: newReport.title,
    type: newReport.type,
    period: newReport.period,
    createDate: new Date().toISOString().split('T')[0],
    status: 'completed',
    size: '2.1MB',
  }

  reports.value.unshift(report)
  createReportVisible.value = false
  alert('报告创建成功')
}

const showPreviewReport = (report) => {
  // 模拟报告预览数据
  previewReport.value = {
    ...report,
    summary: [
      { label: '总病人数', value: '1,250' },
      { label: '测试总数', value: '3,420' },
      { label: '平均得分', value: '78.5' },
      { label: '异常检出率', value: '12.8%' },
    ],
    analysis: [
      '本月病人数量较上月增长了8.7%，新增病人45人，整体增长趋势良好。',
      '测试参与度显著提升，本月共完成测试342次，较上月增长15.2%。',
      '平均测试得分达到78.5分，较上月提升5.3%，治疗效果明显。',
      '异常检出率为12.8%，需要重点关注高风险人群的早期筛查。',
    ],
    conclusions: [
      '病人数量稳定增长，系统使用率持续提升',
      '测试质量良好，平均得分稳步提高',
      '建议加强异常病例的跟踪和干预',
      '继续优化用户体验，提高测试完成率',
    ],
  }
  previewVisible.value = true
}

const downloadReport = (report) => {
  alert(`正在下载${report.title}...`)
}

const deleteReport = (report) => {
  if (confirm(`确定要删除报告 ${report.title} 吗？`)) {
    const index = reports.value.findIndex((r) => r.id === report.id)
    if (index > -1) {
      reports.value.splice(index, 1)
      alert('删除成功')
    }
  }
}

// 生命周期
onMounted(async () => {
  // 用 /api/questions 作为报表数据（仅为演示）
  const res = await fetch('/api/questions')
  const data = await res.json()
  reports.value = data.data || [] // 注：此为AD8问题列表
  totalReports.value = reports.value.length
})
</script>

<style scoped>
.statistical-reports {
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
  font-size: 24px;
}

.header-actions {
  display: flex;
  gap: 10px;
}

.btn {
  padding: 8px 16px;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.3s ease;
  text-decoration: none;
  display: inline-block;
}

.btn-primary {
  background: #3498db;
  color: white;
}

.btn-primary:hover {
  background: #2980b9;
}

.btn-secondary {
  background: #95a5a6;
  color: white;
}

.btn-secondary:hover {
  background: #7f8c8d;
}

.btn-danger {
  background: #e74c3c;
  color: white;
}

.btn-danger:hover {
  background: #c0392b;
}

.btn-sm {
  padding: 4px 8px;
  font-size: 12px;
}

.btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.quick-reports {
  margin-bottom: 30px;
}

.reports-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 20px;
}

.quick-report-card {
  background: white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  cursor: pointer;
  transition:
    transform 0.2s,
    box-shadow 0.2s;
}

.quick-report-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
}

.report-icon {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  color: white;
  margin-bottom: 15px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
}

.icon-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  font-weight: bold;
  color: white;
}

.report-icon.monthly {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.report-icon.quarterly {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.report-icon.yearly {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.report-icon.custom {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
}

.report-info h3 {
  margin: 0 0 8px 0;
  color: #2c3e50;
  font-size: 16px;
}

.report-info p {
  margin: 0 0 10px 0;
  color: #7f8c8d;
  font-size: 14px;
  line-height: 1.5;
}

.report-meta {
  font-size: 12px;
  color: #95a5a6;
}

.reports-list-card {
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px solid #ecf0f1;
}

.header-filters {
  display: flex;
  gap: 10px;
  align-items: center;
}

.filter-select,
.filter-input {
  padding: 8px 12px;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-size: 14px;
}

.filter-select:focus,
.filter-input:focus {
  outline: none;
  border-color: #3498db;
  box-shadow: 0 0 0 2px rgba(52, 152, 219, 0.2);
}

.table-container {
  overflow-x: auto;
}

.data-table {
  width: 100%;
  border-collapse: collapse;
  margin-bottom: 20px;
}

.data-table th,
.data-table td {
  padding: 12px;
  text-align: left;
  border-bottom: 1px solid #ecf0f1;
}

.data-table th {
  background: #f8f9fa;
  font-weight: 600;
  color: #2c3e50;
}

.data-table tbody tr {
  transition: background-color 0.3s ease;
}

.data-table tbody tr:hover {
  background: #f8f9fa;
}

.tag {
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 500;
}

.tag-primary {
  background: #3498db;
  color: white;
}

.tag-success {
  background: #27ae60;
  color: white;
}

.tag-warning {
  background: #f39c12;
  color: white;
}

.tag-info {
  background: #17a2b8;
  color: white;
}

.tag-danger {
  background: #e74c3c;
  color: white;
}

.action-buttons {
  display: flex;
  gap: 5px;
}

.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}

.pagination {
  display: flex;
  align-items: center;
  gap: 15px;
}

.page-info {
  color: #7f8c8d;
  font-size: 14px;
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-content {
  background: white;
  border-radius: 12px;
  max-width: 600px;
  width: 90%;
  max-height: 80vh;
  overflow-y: auto;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.3);
}

.modal-content.large {
  max-width: 900px;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  border-bottom: 1px solid #ecf0f1;
}

.modal-header h3 {
  margin: 0;
  color: #2c3e50;
}

.modal-close {
  background: none;
  border: none;
  font-size: 24px;
  cursor: pointer;
  color: #7f8c8d;
}

.modal-close:hover {
  color: #e74c3c;
}

.modal-body {
  padding: 20px;
}

.form {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-group label {
  font-weight: 600;
  color: #2c3e50;
}

.form-input {
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-size: 14px;
}

.form-input:focus {
  outline: none;
  border-color: #3498db;
  box-shadow: 0 0 0 2px rgba(52, 152, 219, 0.2);
}

.checkbox-group,
.radio-group {
  display: flex;
  flex-wrap: wrap;
  gap: 15px;
}

.checkbox-item,
.radio-item {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
}

.checkbox-item input[type='checkbox'],
.radio-item input[type='radio'] {
  margin: 0;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 20px;
}

.report-preview {
  max-height: 70vh;
  overflow-y: auto;
}

.preview-header {
  text-align: center;
  margin-bottom: 30px;
  padding-bottom: 20px;
  border-bottom: 2px solid #eee;
}

.preview-header h1 {
  margin: 0 0 10px 0;
  color: #2c3e50;
}

.preview-meta {
  display: flex;
  justify-content: center;
  gap: 30px;
  color: #7f8c8d;
  font-size: 14px;
}

.preview-section {
  margin-bottom: 30px;
}

.preview-section h2 {
  color: #2c3e50;
  border-bottom: 1px solid #eee;
  padding-bottom: 10px;
  margin-bottom: 20px;
}

.summary-stats {
  margin-bottom: 20px;
}

.summary-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
}

.summary-item {
  text-align: center;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 8px;
}

.summary-value {
  font-size: 24px;
  font-weight: bold;
  color: #2c3e50;
  margin-bottom: 5px;
}

.summary-label {
  font-size: 14px;
  color: #7f8c8d;
}

.analysis-content p {
  line-height: 1.8;
  color: #2c3e50;
  margin-bottom: 15px;
}

.charts-preview {
  margin: 20px 0;
}

.charts-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 20px;
}

.chart-placeholder {
  height: 200px;
  background: #f8f9fa;
  border: 2px dashed #ddd;
  border-radius: 8px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #7f8c8d;
}

.chart-icon {
  font-size: 48px;
  margin-bottom: 10px;
}

.conclusions ul {
  padding-left: 20px;
}

.conclusions li {
  line-height: 1.8;
  color: #2c3e50;
  margin-bottom: 10px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .reports-grid {
    grid-template-columns: 1fr;
  }

  .header-filters {
    flex-direction: column;
    align-items: stretch;
  }

  .action-buttons {
    flex-direction: column;
  }

  .summary-grid {
    grid-template-columns: 1fr;
  }

  .charts-grid {
    grid-template-columns: 1fr;
  }

  .preview-meta {
    flex-direction: column;
    gap: 10px;
  }
}
</style>
