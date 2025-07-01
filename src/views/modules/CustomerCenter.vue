/**
 * @文件 CustomerCenter.vue - 客户中心页面组件
 * @作者 项目小组1组
 * @版本 1.0.0
 * @版权 2025 东莞佰和生物科技有限公司 保留所有权利
 * @许可证 MIT
 * @创建时间 2025-06-20
 * @最后修改 2025-06-29
 */

<template>
  <div class="customer-center">
    <button class="back-button" @click="goBack">← 返回</button>
    <!-- 用户信息卡片 -->
    <div class="user-card">
      <h2>用户信息</h2>
      <div class="user-info" v-if="currentUser">
        <div><strong>姓名:</strong> {{ currentUser.name }}</div>
        <div><strong>性别:</strong> {{ currentUser.gender }}</div>
        <div><strong>年龄:</strong> {{ currentUser.age }}</div>
        <div><strong>住院号:</strong> {{ currentUser.hospitalNumber }}</div>
      </div>
    </div>

    <!-- 测试记录表格 -->
    <div class="test-records">
      <h2>测试记录</h2>
      <div class="table-container">
        <table>
          <thead>
            <tr>
              <th>测试类型</th>
              <th>测试日期</th>
              <th>测试次数</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <template v-for="(group, groupName) in groupedTests" :key="groupName">
              <tr class="group-header" @click="toggleGroup(groupName)">
                <td colspan="4" class="group-title">
                  {{ groupName }} ({{ group.length }}次)
                  <span class="expand-text">{{ expandedGroups[groupName] ? '收起' : '展开' }}</span>
                </td>
              </tr>
              <tr v-for="(test, index) in group" :key="index" v-show="expandedGroups[groupName]">
                <td>{{ test.testType }}</td>
                <td>{{ formatDate(test.testDate) }}</td>
                <td>{{ test.questionCount }}题</td>
                <td>
                  <button @click="viewDetails(test)">查看详情</button>
                  <button @click="exportTest(test)" class="export-btn">导出</button>
                  <button @click="diagnoseTest(test)" class="diagnose-btn">诊断</button>
                </td>
              </tr>
            </template>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'CustomerCenter',
  data() {
    return {
      currentUser: null,
      testGroups: [],
      ad8Groups: [],
      psqiGroups: [],
      expandedGroups: {}
    }
  },
  computed: {
    groupedTests() {
      const groups = {};
      this.testGroups.forEach(testItem => {
        if (!groups[testItem.testType]) {
          groups[testItem.testType] = [];
        }
        groups[testItem.testType].push(testItem);
      });
      return groups;
    },
    latestTestDate() {
      if (this.testGroups.length === 0) return '无记录';
      const latest = [...this.testGroups].sort((a, b) =>
        new Date(b.created_at) - new Date(a.created_at)
      )[0];
      return `${latest.testType} (${this.formatDate(latest.created_at)})`;
    },
    totalTests() {
      return this.testGroups.reduce((sum, group) => sum + group.responses.length, 0);
    }
  },
  created() {
    this.loadUserData();
    this.fetchTestRecords();
  },
  methods: {
    /**
     * 切换测试记录分组展开状态
     * @param {string} groupName - 要切换的分组名称
     */
    toggleGroup(groupName) {
      this.expandedGroups = {
        ...this.expandedGroups,
        [groupName]: !this.expandedGroups[groupName]
      };
    },
    /**
     * 从本地存储加载当前用户数据
     */
    loadUserData() {
      const user = localStorage.getItem('currentUser');
      this.currentUser = user ? JSON.parse(user) : null;
    },
    /**
     * 异步获取用户的测试记录数据
     * @async
     * @throws {Error} 当获取数据失败时抛出错误
     */
    async fetchTestRecords() {
      if (!this.currentUser) return;

      try {
        const apiBaseUrl = 'http://localhost:8080';
        const ad8Url = `${apiBaseUrl}/api/user/ad8_responses`;
        const psqiUrl = `${apiBaseUrl}/api/user/psqi_responses`;

        const params = {
          hospitalId: this.currentUser.hospitalNumber,
          patientName: this.currentUser.name
        };

        const token = localStorage.getItem('authToken');
        const headers = token ? {'Authorization': `Bearer ${token}`} : {};

        // Build URL with query params
        const ad8UrlWithParams = `${ad8Url}?${new URLSearchParams(params)}`;
        const psqiUrlWithParams = `${psqiUrl}?${new URLSearchParams(params)}`;

        const [ad8Response, psqiResponse] = await Promise.all([
          fetch(ad8UrlWithParams, { headers }),
          fetch(psqiUrlWithParams, { headers })
        ]);

        const ad8Data = await ad8Response.json();
        const psqiData = await psqiResponse.json();

        console.log('AD8 API Response:', ad8Data);
        console.log('PSQI API Response:', psqiData);

        // 处理AD8测试数据 - 每个data项代表一次完整测试
        this.ad8Groups = ad8Data.success ? ad8Data.data.map(test => ({
          testId: test.responses[0]?.response_id || '',
          testType: 'AD8测试',
          testDate: test.created_at,
          score: test.responses.reduce((sum, r) => sum + (r.selected_option || 0), 0),
          questionCount: test.responses.length,
          responses: test.responses.map(r => ({
            questionId: r.question_id,
            questionText: r.question_text,
            answer: r.answer_text,
            selectedOption: r.selected_option
          }))
        })) : [];

        // 处理PSQI测试数据 - 每个data项代表一次完整测试
        this.psqiGroups = psqiData.success ? psqiData.data.map(test => ({
          testId: test.responses[0]?.response_id || '',
          testType: 'PSQI测试',
          testDate: test.created_at,
          score: test.responses.reduce((sum, r) => sum + (r.option_value || 0), 0),
          questionCount: test.responses.length,
          responses: test.responses.map(r => ({
            questionId: r.question_id,
            questionText: r.question_text,
            answer: r.answer_text,
            optionValue: r.option_value
          }))
        })) : [];

        // 合并所有测试组
        this.testGroups = [...this.ad8Groups, ...this.psqiGroups]
          .sort((a, b) => new Date(b.created_at) - new Date(a.created_at));

        console.log('Processed Test Groups:', this.testGroups);
      } catch (error) {
        console.error('获取测试记录失败:', error);
        alert('获取测试记录失败，请稍后重试');
      }
    },
    /**
     * 格式化日期字符串为本地日期格式
     * @param {string} dateString - 要格式化的日期字符串
     * @returns {string} 格式化后的日期字符串
     */
    formatDate(dateString) {
      return new Date(dateString).toLocaleDateString();
    },
    /**
     * 导出测试结果为文本文件并下载
     * @param {Object} test - 要导出的测试对象
     */
    exportTest(test) {
      // Format test data as text
      let content = `${test.testType} - ${this.formatDate(test.testDate)}\n\n`;
      content += `得分: ${test.score}\n\n`;

      test.responses.forEach(responseItem => {
        content += `问题: ${responseItem.questionText || responseItem.question_text}\n`;
        content += `回答: ${responseItem.answer || responseItem.answer_text}\n\n`;
      });

      // Create blob and download
      const blob = new Blob([content], { type: 'text/plain' });
      const url = URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = `${test.testType}_${this.formatDate(test.testDate)}.txt`;
      document.body.appendChild(a);
      a.click();
      document.body.removeChild(a);
      URL.revokeObjectURL(url);
    },

    /**
     * 诊断测试结果并跳转到AI分析页面
     * @param {Object} test - 要诊断的测试对象
     */
    diagnoseTest(test) {
      // Format test data as text
      let content = `${test.testType} - ${this.formatDate(test.testDate)}\n\n`;
      content += `得分: ${test.score}\n\n`;

      test.responses.forEach(responseItem => {
        content += `问题: ${responseItem.questionText || responseItem.question_text}\n`;
        content += `回答: ${responseItem.answer || responseItem.answer_text}\n\n`;
      });

      // Create blob and store in localStorage
      const blob = new Blob([content], { type: 'text/plain' });
      const reader = new FileReader();
      reader.onload = () => {
        localStorage.setItem('diagnosisFile', reader.result);
        this.$router.push({
          name: 'ai-analysis',
          query: { fileName: `${test.testType}_${this.formatDate(test.testDate)}.txt` }
        });
      };
      reader.readAsDataURL(blob);
    },

    /**
     * 返回上一页
     */
    goBack() {
      this.$router.go(-2);
    },

    /**
     * 查看测试详情，显示模态框
     * @param {Object} group - 要查看详情的测试组
     */
    viewDetails(group) {
      const modal = document.createElement('div');
      modal.className = 'test-details-modal';
      modal.style.position = 'fixed';
      modal.style.top = '0';
      modal.style.left = '0';
      modal.style.width = '100%';
      modal.style.height = '100%';
      modal.style.backgroundColor = 'rgba(0,0,0,0.5)';
      modal.style.display = 'flex';
      modal.style.justifyContent = 'center';
      modal.style.alignItems = 'center';
      modal.style.zIndex = '1000';

      const content = document.createElement('div');
      content.className = 'test-details-content';
      content.style.backgroundColor = 'white';
      content.style.padding = '20px';
      content.style.borderRadius = '8px';
      content.style.maxWidth = '800px';
      content.style.maxHeight = '80vh';
      content.style.overflowY = 'auto';

      const title = document.createElement('h2');
      title.textContent = `${group.testType} - ${group.testDate ? this.formatDate(group.testDate) : '无日期'}`;
      content.appendChild(title);

      const questionsDiv = document.createElement('div');
      questionsDiv.className = 'questions-container';

      group.responses.forEach(responseItem => {
        const qDiv = document.createElement('div');
        qDiv.style.marginBottom = '15px';
        qDiv.style.paddingBottom = '15px';
        qDiv.style.borderBottom = '1px solid #eee';

        const question = document.createElement('p');
        question.style.fontWeight = 'bold';
        question.textContent = responseItem.questionText || responseItem.question_text || `问题 ${responseItem.question_id}`;
        qDiv.appendChild(question);

        const answer = document.createElement('p');
        answer.textContent = `回答: ${responseItem.answer || responseItem.answer_text || '无'}`;
        qDiv.appendChild(answer);

        questionsDiv.appendChild(qDiv);
      });

      content.appendChild(questionsDiv);

      const closeBtn = document.createElement('button');
      closeBtn.textContent = '关闭';
      closeBtn.style.marginTop = '20px';
      closeBtn.style.padding = '8px 16px';
      closeBtn.style.backgroundColor = '#4CAF50';
      closeBtn.style.color = 'white';
      closeBtn.style.border = 'none';
      closeBtn.style.borderRadius = '4px';
      closeBtn.style.cursor = 'pointer';
      closeBtn.onclick = () => document.body.removeChild(modal);
      content.appendChild(closeBtn);

      modal.appendChild(content);
      document.body.appendChild(modal);
    }
  }
}
</script>

<style scoped>
.back-button {
  background: none;
  border: none;
  color: #667eea;
  font-size: 16px;
  cursor: pointer;
  padding: 0;
  margin-bottom: 15px;
  transition: color 0.3s;
}

.back-button:hover {
  color: #4a90e2;
}

.customer-center {
  padding: 1rem;
  width: 100%;
  margin: 0 auto;
  font-family: 'PingFang SC', 'Microsoft YaHei', sans-serif;
  box-sizing: border-box;
}

.user-card {
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  border-radius: 12px;
  padding: 1.5rem;
  margin-bottom: 2rem;
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
  border: 1px solid rgba(255,255,255,0.3);
}

.user-card h2 {
  color: #2c3e50;
  margin-bottom: 1rem;
  font-size: 1.5rem;
  font-weight: 600;
}

.user-info {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
  gap: 0.8rem;
  margin-top: 1rem;
}

.user-info div {
  background: white;
  padding: 0.8rem;
  border-radius: 8px;
  box-shadow: 0 2px 6px rgba(0,0,0,0.05);
}

.test-records {
  background: white;
  border-radius: 12px;
  padding: 1.5rem;
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
}

.test-records h2 {
  color: #2c3e50;
  margin-bottom: 1.5rem;
  font-size: 1.5rem;
  font-weight: 600;
}

.table-container {
  overflow-x: auto;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.08);
  -webkit-overflow-scrolling: touch;
  width: 100%;
}

table {
  width: 100%;
  border-collapse: collapse;
  background: white;
}

th {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  font-weight: 500;
  padding: 1rem;
  text-align: left;
}

td {
  padding: 1rem;
  border-bottom: 1px solid #f0f0f0;
}

tr:hover {
  background-color: #f8f9fa;
}

button {
  padding: 0.4rem 0.8rem;
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.3s ease;
  font-weight: 500;
  font-size: 14px;
  white-space: nowrap;
}

button:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(0,0,0,0.1);
}

.export-btn {
  margin-left: 8px;
  background: linear-gradient(135deg, #6c5ce7 0%, #a29bfe 100%);
}

.diagnose-btn {
  margin-left: 8px;
  background: linear-gradient(135deg, #00b894 0%, #55efc4 100%);
}

.group-header {
  background-color: #f5f7fa;
  cursor: pointer;
  transition: background-color 0.2s;
}

.group-header:hover {
  background-color: #e1e5eb;
}

.group-title {
  font-weight: 600;
  padding: 12px 16px;
}

.expand-text {
  float: right;
  color: #4facfe;
  font-weight: normal;
}

.test-details-modal {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0,0,0,0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.test-details-content {
  background-color: white;
  padding: 2rem;
  border-radius: 12px;
  max-width: 800px;
  max-height: 80vh;
  overflow-y: auto;
  box-shadow: 0 10px 25px rgba(0,0,0,0.2);
}

.test-details-content h2 {
  color: #2c3e50;
  margin-bottom: 1.5rem;
  padding-bottom: 0.5rem;
  border-bottom: 1px solid #eee;
}

.questions-container div {
  margin-bottom: 1rem;
  padding-bottom: 1rem;
  border-bottom: 1px solid #f0f0f0;
}

@media (max-width: 768px) {
  .customer-center {
    padding: 0.5rem;
  }

  .user-info {
    grid-template-columns: 1fr;
  }

  table {
    font-size: 14px;
  }

  td {
    padding: 0.5rem;
  }

  button {
    padding: 0.3rem 0.6rem;
    margin-left: 4px;
  }

  .export-btn, .diagnose-btn {
    margin-top: 4px;
    margin-left: 0;
  }
}
</style>
