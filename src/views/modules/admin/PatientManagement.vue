<!--
 * 文件: PatientManagement.vue - 病人管理组件
 * 描述: 管理端病人管理页面，提供病人列表、搜索、筛选和详情查看
 * 功能:
 *   - 病人列表展示
 *   - 搜索和筛选功能
 *   - 病人详情查看
 *   - 测试记录查看
 * 作者: 项目小组1组
 * 版本: 1.0.0
 * 版权: 2025 东莞佰和生物科技有限公司 保留所有权利
 * 许可证: MIT
 * 创建时间: 2025-01-27
 * 最后修改: 2025-01-27
 -->

<template>
  <div class="patient-management">
    <!-- 页面标题和操作栏 -->
    <div class="page-header">
      <h2>病人管理</h2>
      <div class="header-actions">
        <button class="btn btn-primary" @click="showAddPatientDialog">添加病人</button>
        <button class="btn btn-secondary" @click="exportData">导出数据</button>
      </div>
    </div>

    <!-- 搜索和筛选区域 -->
    <div class="search-card">
      <div class="search-form">
        <div class="search-item">
          <input
            v-model="searchForm.keyword"
            type="text"
            placeholder="搜索姓名、手机号或ID"
            class="search-input"
            @input="handleSearch"
          />
        </div>
        <div class="search-item">
          <select v-model="searchForm.gender" class="search-select" @change="handleSearch">
            <option value="">性别</option>
            <option value="male">男</option>
            <option value="female">女</option>
          </select>
        </div>
        <div class="search-item">
          <select v-model="searchForm.ageRange" class="search-select" @change="handleSearch">
            <option value="">年龄段</option>
            <option value="18-30">18-30岁</option>
            <option value="31-50">31-50岁</option>
            <option value="51-70">51-70岁</option>
            <option value="70+">70岁以上</option>
          </select>
        </div>
        <div class="search-item">
          <select v-model="searchForm.status" class="search-select" @change="handleSearch">
            <option value="">状态</option>
            <option value="active">活跃</option>
            <option value="inactive">非活跃</option>
          </select>
        </div>
        <div class="search-actions">
          <button class="btn btn-primary" @click="handleSearch">搜索</button>
          <button class="btn btn-secondary" @click="resetSearch">重置</button>
        </div>
      </div>
    </div>

    <!-- 病人列表 -->
    <div class="patient-list-card">
      <div class="table-container">
        <table class="data-table">
          <thead>
            <tr>
              <th>姓名</th>
              <th>性别</th>
              <th>年龄</th>
              <th>住院号</th>
              <th>最近评定日期</th>
              <th>测试次数</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody v-if="patientInfo">
            <tr>
              <td>{{ patientInfo.name }}</td>
              <td>
                <span
                  class="tag"
                  :class="patientInfo.gender === 'male' ? 'tag-primary' : 'tag-danger'"
                >
                  {{ patientInfo.gender === 'male' ? '男' : '女' }}
                </span>
              </td>
              <td>{{ patientInfo.age }}</td>
              <td>{{ patientInfo.hospitalNumber }}</td>
              <td>{{ patientInfo.assessmentDate }}</td>
              <td>{{ patientInfo.testCount }}</td>
              <td>
                <button class="btn btn-sm btn-secondary" @click="showHistory = !showHistory">
                  历史
                </button>
                <button class="btn btn-sm btn-primary" @click="viewTests(patientInfo)">
                  查看测试
                </button>
              </td>
            </tr>
            <template v-if="showHistory">
              <tr v-for="record in ad8Records" :key="record.created_at">
                <td colspan="4">历史评定日期：{{ record.created_at }}</td>
                <td colspan="3">
                  <button class="btn btn-sm btn-secondary" @click="viewAd8Detail(record)">
                    详情
                  </button>
                </td>
              </tr>
            </template>
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

    <!-- 病人详情对话框 -->
    <div v-if="patientDetailVisible" class="modal-overlay" @click="handleCloseDetail">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h3>病人详情</h3>
          <button class="modal-close" @click="handleCloseDetail">×</button>
        </div>
        <div class="modal-body">
          <div v-if="selectedPatient" class="patient-detail">
            <div class="detail-grid">
              <div class="detail-item">
                <label>ID:</label>
                <span>{{ selectedPatient.id }}</span>
              </div>
              <div class="detail-item">
                <label>姓名:</label>
                <span>{{ selectedPatient.name }}</span>
              </div>
              <div class="detail-item">
                <label>性别:</label>
                <span>{{ selectedPatient.gender === 'male' ? '男' : '女' }}</span>
              </div>
              <div class="detail-item">
                <label>年龄:</label>
                <span>{{ selectedPatient.age }}岁</span>
              </div>
              <div class="detail-item">
                <label>手机号:</label>
                <span>{{ selectedPatient.phone }}</span>
              </div>
              <div class="detail-item">
                <label>状态:</label>
                <span
                  class="tag"
                  :class="selectedPatient.status === 'active' ? 'tag-success' : 'tag-info'"
                >
                  {{ selectedPatient.status === 'active' ? '活跃' : '非活跃' }}
                </span>
              </div>
              <div class="detail-item">
                <label>注册时间:</label>
                <span>{{ formatDate(selectedPatient.registerDate) }}</span>
              </div>
              <div class="detail-item">
                <label>最后测试:</label>
                <span>{{ formatDate(selectedPatient.lastTestDate) }}</span>
              </div>
              <div class="detail-item full-width">
                <label>测试次数:</label>
                <span>{{ selectedPatient.testCount }}次</span>
              </div>
            </div>

            <!-- 测试记录 -->
            <div class="test-records">
              <h4>测试记录</h4>
              <div class="table-container">
                <table class="data-table">
                  <thead>
                    <tr>
                      <th>测试类型</th>
                      <th>测试时间</th>
                      <th>得分</th>
                      <th>结果</th>
                      <th>操作</th>
                    </tr>
                  </thead>
                  <tbody>
                    <tr v-for="record in selectedPatient.testRecords" :key="record.testDate">
                      <td>{{ record.testType }}</td>
                      <td>{{ formatDate(record.testDate) }}</td>
                      <td>{{ record.score }}</td>
                      <td>
                        <span class="tag" :class="getResultType(record.result)">
                          {{ record.result }}
                        </span>
                      </td>
                      <td>
                        <button class="btn btn-sm btn-secondary" @click="viewTestDetail(record)">
                          详情
                        </button>
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

    <!-- 添加病人对话框 -->
    <div v-if="addPatientVisible" class="modal-overlay" @click="addPatientVisible = false">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h3>添加病人</h3>
          <button class="modal-close" @click="addPatientVisible = false">×</button>
        </div>
        <div class="modal-body">
          <form @submit.prevent="addPatient" class="form">
            <div class="form-group">
              <label>姓名:</label>
              <input
                v-model="formData.name"
                type="text"
                required
                class="form-input"
                placeholder="请输入姓名"
              />
            </div>
            <div class="form-group">
              <label>性别:</label>
              <div class="radio-group">
                <label class="radio-item">
                  <input v-model="formData.gender" type="radio" value="male" required />
                  <span>男</span>
                </label>
                <label class="radio-item">
                  <input v-model="formData.gender" type="radio" value="female" />
                  <span>女</span>
                </label>
              </div>
            </div>
            <div class="form-group">
              <label>年龄:</label>
              <input
                v-model.number="formData.age"
                type="number"
                min="1"
                max="120"
                required
                class="form-input"
              />
            </div>
            <div class="form-group">
              <label>住院号:</label>
              <input
                v-model="formData.hospitalNumber"
                type="text"
                required
                class="form-input"
                placeholder="请输入住院号"
              />
            </div>
            <div class="form-group">
              <label>评定日期:</label>
              <input
                v-model="formData.assessmentDate"
                type="date"
                required
                class="form-input"
                placeholder="请选择评定日期"
              />
            </div>
            <div class="form-actions">
              <button type="button" class="btn btn-secondary" @click="addPatientVisible = false">
                取消
              </button>
              <button type="submit" class="btn btn-primary">确定</button>
            </div>
          </form>
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
const totalPatients = ref(0)
const patientDetailVisible = ref(false)
const addPatientVisible = ref(false)
const selectedPatient = ref(null)
const ad8Records = ref([])
const showHistory = ref(false)

// 搜索表单
const searchForm = reactive({
  keyword: '',
  gender: '',
  ageRange: '',
  status: '',
})

// 新病人表单
const formData = reactive({
  name: '', // 姓名
  gender: '', // 性别
  age: '', // 年龄
  hospitalNumber: '', // 住院号
  assessmentDate: '', // 评定日期
})

const patients = ref([])
const patientInfo = ref(null)

// 计算属性
const filteredPatients = computed(() => {
  let result = patients.value

  if (searchForm.keyword) {
    const keyword = searchForm.keyword.toLowerCase()
    result = result.filter(
      (patient) =>
        patient.name.toLowerCase().includes(keyword) ||
        patient.phone.includes(keyword) ||
        patient.id.toLowerCase().includes(keyword),
    )
  }

  if (searchForm.gender) {
    result = result.filter((patient) => patient.gender === searchForm.gender)
  }

  if (searchForm.ageRange) {
    const [min, max] = searchForm.ageRange.split('-').map(Number)
    result = result.filter((patient) => {
      if (max) {
        return patient.age >= min && patient.age <= max
      } else {
        return patient.age >= min
      }
    })
  }

  if (searchForm.status) {
    result = result.filter((patient) => patient.status === searchForm.status)
  }

  return result
})

const totalPages = computed(() => Math.ceil(totalPatients.value / pageSize.value))

// 监听筛选结果变化，更新总数
watch(
  filteredPatients,
  (newValue) => {
    totalPatients.value = newValue.length
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

const formatDate = (dateString) => {
  if (!dateString) return '无'
  return new Date(dateString).toLocaleDateString()
}

const getResultType = (result) => {
  switch (result) {
    case '正常':
      return 'tag-success'
    case '轻度':
      return 'tag-warning'
    case '中度':
      return 'tag-danger'
    default:
      return 'tag-info'
  }
}

const handleCloseDetail = () => {
  patientDetailVisible.value = false
  selectedPatient.value = null
}

const showAddPatientDialog = () => {
  addPatientVisible.value = true
  Object.keys(formData).forEach((key) => {
    formData[key] = key === 'gender' ? 'male' : key === 'age' ? 30 : ''
  })
}

const addPatient = async () => {
  try {
    const response = await fetch('http://localhost:8080/api/login', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(formData),
    })
    if (response.ok) {
      const data = await response.json()
      if (data.success) {
        if (data.message && data.message.includes('已存在')) {
          alert('已有该用户')
        } else {
          alert('添加病人成功')
        }
        console.log('登录用户信息:', formData)
        console.log('服务器返回数据:', data)
        localStorage.setItem('currentUser', JSON.stringify(formData))
        // 可根据需要刷新病人列表
      } else {
        alert(data.message || '添加失败')
      }
    } else {
      alert('服务器错误')
    }
  } catch (err) {
    alert('请求失败')
    console.error(err)
  }
}

const viewTests = (row) => {
  selectedPatient.value = row
  patientDetailVisible.value = true
}

const viewTestDetail = () => {
  alert('测试详情功能开发中...')
}

const exportData = () => {
  alert('导出功能开发中...')
}

const viewAd8Detail = (record) => {
  console.log(record)
  alert('详情功能可自定义弹窗展示record.responses内容')
}

// 生命周期
onMounted(async () => {
  const apiBaseUrl = 'http://localhost:8080'
  const ad8Url = `${apiBaseUrl}/api/user/ad8_responses`
  const psqiUrl = `${apiBaseUrl}/api/user/psqi_responses`
  const params = {
    hospitalId: '管理员',
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
  patientInfo.value = {
    name: '牛迦楠',
    gender: 'male',
    age: 21,
    hospitalNumber: '管理员',
    assessmentDate: ad8Data.data && ad8Data.data.length > 0 ? ad8Data.data[0].created_at : '',
    testCount:
      (ad8Data.data ? ad8Data.data.length : 0) + (psqiData.data ? psqiData.data.length : 0),
  }
  ad8Records.value = ad8Data.data || []
})
</script>

<style scoped>
.patient-management {
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

.patient-list-card {
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

.tag-danger {
  background: #e74c3c;
  color: white;
}

.tag-success {
  background: #27ae60;
  color: white;
}

.tag-info {
  background: #95a5a6;
  color: white;
}

.tag-warning {
  background: #f39c12;
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

.detail-item.full-width {
  grid-column: 1 / -1;
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

.test-records h4 {
  margin: 20px 0 15px 0;
  color: #2c3e50;
  border-bottom: 2px solid #ecf0f1;
  padding-bottom: 10px;
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

.radio-group {
  display: flex;
  gap: 20px;
}

.radio-item {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
}

.radio-item input[type='radio'] {
  margin: 0;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 20px;
}

/* 响应式设计 */
@media (max-width: 768px) {
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
