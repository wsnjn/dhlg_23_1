<template>
  <div class="assessment-form-page">
    <!-- 顶部标题栏 -->
    <div class="header">
      <button class="back-button" @click="goBack">
        <span class="back-icon">←</span>
      </button>
      <h1 class="title">{{ testTitles[testType] }}</h1>
    </div>

    <!-- 主体内容 -->
    <div class="content">
      <div class="form-container">
        <div class="form-group">
          <label>姓名</label>
          <input type="text" v-model="formData.name" placeholder="请输入姓名">
        </div>

        <div class="form-group">
          <label>性别</label>
          <input type="text" v-model="formData.gender" placeholder="请输入性别">
        </div>

        <div class="form-group">
          <label>年龄</label>
          <input type="number" v-model="formData.age" placeholder="请输入年龄">
        </div>

        <div class="form-group">
          <label>住院号</label>
          <input type="text" v-model="formData.hospitalNumber" placeholder="请输入住院号">
        </div>

        <div class="form-group">
          <label>评定日期</label>
          <input type="date" v-model="formData.assessmentDate">
        </div>
      </div>

      <button class="start-button" @click="startAssessment">
        开始测试
      </button>
    </div>
  </div>
</template>

<script>
export default {
  name: 'AssessmentFormPage',
  data() {
    return {
      testType: '',
      testTitles: {
        'mini-cog': '简易智力状态评估表(MINI-COG)',
        'casi': '早期痴呆简易筛选量表（AD8）',
        'blessed': '匹兹堡睡眠质量指数量表（PSQI）',
        'customer-center': '客户中心'
      },
      formData: {
        name: '',
        gender: '',
        age: '',
        hospitalNumber: '',
        assessmentDate: ''
      }
    }
  },
  created() {
    this.testType = this.$route.query.redirect === 'customer-center'
      ? 'customer-center'
      : this.$route.query.testType || 'mini-cog';
  },
  methods: {
    goBack() {
      this.$router.go(-1);
    },
    async startAssessment() {
      try {
        const response = await fetch('http://localhost:8080/api/login', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json'
          },
          body: JSON.stringify(this.formData)
        });

        if (response.ok) {
          const data = await response.json();
          if (data.success) {
            console.log('登录用户信息:', this.formData);
            console.log('服务器返回数据:', data);
            // 存储用户信息到localStorage
            localStorage.setItem('currentUser', JSON.stringify(this.formData));
            // 登录成功，优先根据redirect参数跳转
            if (this.$route.query.redirect) {
              this.$router.push({ name: this.$route.query.redirect });
            } else {
              // 否则根据测试类型跳转
              let targetRoute;
              if (this.testType === 'casi') {
                targetRoute = '/ad8-test';
              } else if (this.testType === 'blessed') {
                targetRoute = '/psqi';
              } else {
                targetRoute = '/text';
              }
              this.$router.push(targetRoute);
            }
          } else {
            console.log('验证失败的用户信息:', this.formData);
            console.log('服务器返回错误:', data);
            alert('用户验证失败: ' + data.message);
          }
        } else {
          throw new Error('网络响应错误');
        }
      } catch (error) {
        console.error('登录请求失败:', error);
        alert('登录请求失败，请稍后重试');
      }
    }
  }
}
</script>

<style scoped>
.assessment-form-page {
  font-family: 'PingFang SC', 'Microsoft YaHei', sans-serif;
  height: 100vh;
  display: flex;
  flex-direction: column;
  background: linear-gradient(to bottom, #87CEEB, #E0F7FA); /* 蓝天白云渐变背景 */
}

.header {
  background-color: rgba(30, 136, 229, 0.8); /* 半透明深蓝色 */
  color: white;
  padding: 15px 20px;
  display: flex;
  align-items: center;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.back-button {
  background: none;
  border: none;
  color: white;
  font-size: 16px;
  display: flex;
  align-items: center;
  cursor: pointer;
  margin-right: 15px;
}

.back-icon {
  font-size: 20px;
}

.title {
  font-size: 18px;
  font-weight: 500;
  margin: 0;
  flex: 1;
  text-align: center;
}

.content {
  flex: 1;
  padding: 20px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.form-container {
  background-color: rgba(255, 255, 255, 0.8); /* 半透明白色背景 */
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  font-weight: 500;
  color: #333;
}

.form-group input[type="text"],
.form-group input[type="number"],
.form-group input[type="date"] {
  width: 95%;
  padding: 12px;
  border: 1px solid #ddd;
  border-radius: 8px;
  font-size: 16px;
}

.radio-group {
  display: flex;
  gap: 20px;
  margin-top: 8px;
}

.radio-group label {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: normal;
}

.start-button {
  background-color: #9c27b0; /* 紫色按钮 */
  color: white;
  border: none;
  border-radius: 8px;
  padding: 16px;
  font-size: 18px;
  font-weight: bold;
  text-align: center;
  cursor: pointer;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
  transition: transform 0.2s, box-shadow 0.2s;
  width: 100%;
  margin-top: 20px;
}

.start-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 12px rgba(0, 0, 0, 0.2);
}

.start-button:active {
  transform: translateY(0);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
}


</style>

