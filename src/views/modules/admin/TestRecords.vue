<!--
 * 文件: TestRecords.vue - 测试记录管理组件
 * 描述: 管理端测试记录页面，提供测试记录列表、搜索、筛选和统计分析
 * 功能:
 *   - 测试记录列表展示
 *   - 搜索和筛选功能
 *   - 测试结果统计分析
 *   - 测试详情查看
 * 作者: 项目小组1组
 * 版本: 1.0.0
 * 版权: 2025 东莞佰和生物科技有限公司 保留所有权利
 * 许可证: MIT
 * 创建时间: 2025-01-27
 * 最后修改: 2025-01-27
 -->

<template>
  <div class="test-records">
    <!-- 页面标题和统计卡片 -->
    <div class="page-header">
      <h2>测试记录</h2>
      <div class="stats-cards">
        <div class="stat-card">
          <div class="stat-content">
            <div class="stat-icon total-icon">
              <div class="icon-text">总</div>
            </div>
            <div class="stat-info">
              <div class="stat-number">{{ testStats.total }}</div>
              <div class="stat-label">总测试数</div>
            </div>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-content">
            <div class="stat-icon today-icon">
              <div class="icon-text">今</div>
            </div>
            <div class="stat-info">
              <div class="stat-number">{{ testStats.today }}</div>
              <div class="stat-label">今日测试</div>
            </div>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-content">
            <div class="stat-icon week-icon">
              <div class="icon-text">周</div>
            </div>
            <div class="stat-info">
              <div class="stat-number">{{ testStats.thisWeek }}</div>
              <div class="stat-label">本周测试</div>
            </div>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-content">
            <div class="stat-icon month-icon">
              <div class="icon-text">月</div>
            </div>
            <div class="stat-info">
              <div class="stat-number">{{ testStats.thisMonth }}</div>
              <div class="stat-label">本月测试</div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 搜索和筛选区域 -->
    <div class="search-card">
      <div class="search-form">
        <div class="search-item">
          <input
            v-model="searchForm.keyword"
            type="text"
            placeholder="搜索病人姓名或ID"
            class="search-input"
            @input="handleSearch"
          />
        </div>
        <div class="search-item">
          <select v-model="searchForm.testType" class="search-select" @change="handleSearch">
            <option value="">测试类型</option>
            <option value="AD8">AD8测试</option>
            <option value="PSQI">PSQI测试</option>
            <option value="认知">认知评估</option>
            <option value="情感">情感聊天</option>
          </select>
        </div>
        <div class="search-item">
          <select v-model="searchForm.result" class="search-select" @change="handleSearch">
            <option value="">测试结果</option>
            <option value="正常">正常</option>
            <option value="轻度">轻度</option>
            <option value="中度">中度</option>
            <option value="重度">重度</option>
          </select>
        </div>
        <div class="search-item">
          <input
            v-model="searchForm.startDate"
            type="date"
            class="search-input"
            placeholder="开始日期"
            @change="handleSearch"
          />
        </div>
        <div class="search-item">
          <input
            v-model="searchForm.endDate"
            type="date"
            class="search-input"
            placeholder="结束日期"
            @change="handleSearch"
          />
        </div>
        <div class="search-actions">
          <button class="btn btn-primary" @click="handleSearch">搜索</button>
          <button class="btn btn-secondary" @click="resetSearch">重置</button>
        </div>
      </div>
    </div>

    <!-- 测试记录列表 -->
    <div class="test-list-card">
      <div class="table-container">
        <table class="data-table">
          <thead>
            <tr>
              <th>测试ID</th>
              <th>病人姓名</th>
              <th>病人ID</th>
              <th>测试类型</th>
              <th>测试时间</th>
              <th>得分</th>
              <th>结果</th>
              <th>用时</th>
              <th>状态</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="test in filteredTests" :key="test.id" @click="showTestDetail(test)">
              <td>{{ test.id }}</td>
              <td>{{ test.patientName }}</td>
              <td>{{ test.patientId }}</td>
              <td>
                <span class="tag" :class="getTestTypeColor(test.testType)">
                  {{ test.testType }}
                </span>
              </td>
              <td>{{ formatDateTime(test.testDate) }}</td>
              <td>
                <span :class="getScoreClass(test.score)">{{ test.score }}</span>
              </td>
              <td>
                <span class="tag" :class="getResultType(test.result)">
                  {{ test.result }}
                </span>
              </td>
              <td>{{ test.duration }}分钟</td>
              <td>
                <span
                  class="tag"
                  :class="test.status === 'completed' ? 'tag-success' : 'tag-warning'"
                >
                  {{ test.status === 'completed' ? '已完成' : '进行中' }}
                </span>
              </td>
              <td>
                <div class="action-buttons">
                  <button class="btn btn-sm btn-secondary" @click.stop="viewTestDetail(test)">
                    查看详情
                  </button>
                  <button class="btn btn-sm btn-danger" @click.stop="deleteTest(test)">删除</button>
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

    <!-- 测试详情对话框 -->
    <div v-if="testDetailVisible" class="modal-overlay" @click="handleCloseDetail">
      <div class="modal-content large" @click.stop>
        <div class="modal-header">
          <h3>测试详情</h3>
          <button class="modal-close" @click="handleCloseDetail">×</button>
        </div>
        <div class="modal-body">
          <div v-if="selectedTest" class="test-detail">
            <div class="detail-grid">
              <div class="detail-item">
                <label>测试ID:</label>
                <span>{{ selectedTest.id }}</span>
              </div>
              <div class="detail-item">
                <label>病人姓名:</label>
                <span>{{ selectedTest.patientName }}</span>
              </div>
              <div class="detail-item">
                <label>测试类型:</label>
                <span>{{ selectedTest.testType }}</span>
              </div>
              <div class="detail-item">
                <label>测试时间:</label>
                <span>{{ formatDateTime(selectedTest.testDate) }}</span>
              </div>
              <div class="detail-item">
                <label>得分:</label>
                <span :class="getScoreClass(selectedTest.score)">{{ selectedTest.score }}</span>
              </div>
              <div class="detail-item">
                <label>结果:</label>
                <span class="tag" :class="getResultType(selectedTest.result)">
                  {{ selectedTest.result }}
                </span>
              </div>
              <div class="detail-item">
                <label>用时:</label>
                <span>{{ selectedTest.duration }}分钟</span>
              </div>
              <div class="detail-item">
                <label>状态:</label>
                <span
                  class="tag"
                  :class="selectedTest.status === 'completed' ? 'tag-success' : 'tag-warning'"
                >
                  {{ selectedTest.status === 'completed' ? '已完成' : '进行中' }}
                </span>
              </div>
            </div>

            <!-- 测试问题详情 -->
            <div class="test-questions" v-if="selectedTest.questions">
              <h4>测试问题详情</h4>
              <div class="table-container">
                <table class="data-table">
                  <thead>
                    <tr>
                      <th>题号</th>
                      <th>问题内容</th>
                      <th>答案</th>
                      <th>得分</th>
                      <th>正确性</th>
                    </tr>
                  </thead>
                  <tbody>
                    <tr v-for="question in selectedTest.questions" :key="question.questionNumber">
                      <td>{{ question.questionNumber }}</td>
                      <td>{{ question.questionText }}</td>
                      <td>{{ question.answer }}</td>
                      <td>{{ question.score }}</td>
                      <td>
                        <span
                          class="tag"
                          :class="question.isCorrect ? 'tag-success' : 'tag-danger'"
                        >
                          {{ question.isCorrect ? '正确' : '错误' }}
                        </span>
                      </td>
                    </tr>
                  </tbody>
                </table>
              </div>
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
const totalTests = ref(0)
const testDetailVisible = ref(false)
const selectedTest = ref(null)

// 搜索表单
const searchForm = reactive({
  keyword: '',
  testType: '',
  result: '',
  startDate: '',
  endDate: '',
})

// 测试统计
const testStats = ref({
  total: 0,
  today: 0,
  thisWeek: 0,
  thisMonth: 0,
})

const tests = ref([])

// 计算属性
const filteredTests = computed(() => {
  let result = tests.value

  if (searchForm.keyword) {
    const keyword = searchForm.keyword.toLowerCase()
    result = result.filter(
      (test) =>
        test.patientName.toLowerCase().includes(keyword) ||
        test.patientId.toLowerCase().includes(keyword),
    )
  }

  if (searchForm.testType) {
    result = result.filter((test) => test.testType.includes(searchForm.testType))
  }

  if (searchForm.result) {
    result = result.filter((test) => test.result === searchForm.result)
  }

  if (searchForm.startDate && searchForm.endDate) {
    result = result.filter((test) => {
      const testDate = test.testDate.split(' ')[0]
      return testDate >= searchForm.startDate && testDate <= searchForm.endDate
    })
  }

  return result
})

const totalPages = computed(() => Math.ceil(totalTests.value / pageSize.value))

// 监听筛选结果变化，更新总数
watch(
  filteredTests,
  (newValue) => {
    totalTests.value = newValue.length
  },
  { immediate: true },
)

// 方法
const handleSearch = () => {
  currentPage.value = 1
}

const resetSearch = () => {
  Object.keys(searchForm).forEach((key) => {
    searchForm[key] = ''
  })
  handleSearch()
}

const handleCurrentChange = (val) => {
  currentPage.value = val
}

const formatDateTime = (dateTimeString) => {
  if (!dateTimeString) return '无'
  return new Date(dateTimeString).toLocaleString()
}

const getTestTypeColor = (testType) => {
  const colors = {
    AD8测试: 'tag-primary',
    PSQI测试: 'tag-success',
    认知评估: 'tag-warning',
    情感聊天: 'tag-info',
  }
  return colors[testType] || 'tag-default'
}

const getResultType = (result) => {
  const types = {
    正常: 'tag-success',
    轻度: 'tag-warning',
    中度: 'tag-danger',
    重度: 'tag-danger',
  }
  return types[result] || 'tag-info'
}

const getScoreClass = (score) => {
  if (score >= 80) return 'score-high'
  if (score >= 60) return 'score-medium'
  return 'score-low'
}

const showTestDetail = (row) => {
  selectedTest.value = row
  testDetailVisible.value = true
}

const handleCloseDetail = () => {
  testDetailVisible.value = false
  selectedTest.value = null
}

const viewTestDetail = (row) => {
  selectedTest.value = row
  testDetailVisible.value = true
}

const deleteTest = (row) => {
  if (confirm(`确定要删除测试记录 ${row.id} 吗？`)) {
    const index = tests.value.findIndex((t) => t.id === row.id)
    if (index > -1) {
      tests.value.splice(index, 1)
      alert('删除成功')
      loadTestStats()
    }
  }
}

const loadTestStats = () => {
  const today = new Date().toDateString()
  const weekAgo = new Date(Date.now() - 7 * 24 * 60 * 60 * 1000).toDateString()
  const monthAgo = new Date(Date.now() - 30 * 24 * 60 * 60 * 1000).toDateString()

  testStats.value = {
    total: tests.value.length,
    today: tests.value.filter((test) => new Date(test.testDate).toDateString() === today).length,
    thisWeek: tests.value.filter((test) => new Date(test.testDate) >= new Date(weekAgo)).length,
    thisMonth: tests.value.filter((test) => new Date(test.testDate) >= new Date(monthAgo)).length,
  }
}

// 生命周期
onMounted(async () => {
  // 用 /api/user/psqi_responses 作为测试记录（仅为演示）
  const res = await fetch('/api/user/psqi_responses?hospitalId=1&patientName=张三')
  const data = await res.json()
  tests.value = data.data || [] // 注：此为PSQI测试记录
  loadTestStats()
})
</script>

<style scoped>
.test-records {
  padding: 20px;
}

.page-header {
  margin-bottom: 20px;
}

.page-header h2 {
  margin: 0 0 20px 0;
  color: #2c3e50;
  font-size: 24px;
}

.stats-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
  margin-bottom: 20px;
}

.stat-card {
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  transition:
    transform 0.3s ease,
    box-shadow 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
}

.stat-content {
  display: flex;
  align-items: center;
  gap: 15px;
}

.stat-icon {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  color: white;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
}

.icon-text {
  font-size: 16px;
  font-weight: bold;
  color: white;
}

.total-icon {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.today-icon {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.week-icon {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.month-icon {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
}

.stat-info {
  flex: 1;
}

.stat-number {
  font-size: 24px;
  font-weight: bold;
  color: #2c3e50;
  line-height: 1;
  margin-bottom: 5px;
}

.stat-label {
  font-size: 12px;
  color: #7f8c8d;
  font-weight: 500;
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

.search-card {
  background: white;
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 20px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.search-form {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 15px;
  align-items: end;
}

.search-input,
.search-select {
  width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-size: 14px;
}

.search-input:focus,
.search-select:focus {
  outline: none;
  border-color: #3498db;
  box-shadow: 0 0 0 2px rgba(52, 152, 219, 0.2);
}

.search-actions {
  display: flex;
  gap: 10px;
}

.test-list-card {
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
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
  cursor: pointer;
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
  max-width: 800px;
  width: 90%;
  max-height: 80vh;
  overflow-y: auto;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.3);
}

.modal-content.large {
  max-width: 1000px;
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

.detail-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 15px;
  margin-bottom: 20px;
}

.detail-item {
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.detail-item label {
  font-weight: 600;
  color: #7f8c8d;
  font-size: 14px;
}

.detail-item span {
  color: #2c3e50;
  font-size: 16px;
}

.test-questions {
  margin-top: 20px;
}

.test-questions h4 {
  margin-bottom: 15px;
  color: #2c3e50;
  border-bottom: 2px solid #ecf0f1;
  padding-bottom: 10px;
}

.score-high {
  color: #67c23a;
  font-weight: bold;
}

.score-medium {
  color: #e6a23c;
  font-weight: bold;
}

.score-low {
  color: #f56c6c;
  font-weight: bold;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .stats-cards {
    grid-template-columns: 1fr;
  }

  .search-form {
    grid-template-columns: 1fr;
  }

  .search-actions {
    justify-content: center;
  }

  .action-buttons {
    flex-direction: column;
  }

  .detail-grid {
    grid-template-columns: 1fr;
  }
}
</style>
