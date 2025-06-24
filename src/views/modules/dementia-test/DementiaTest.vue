<template>
  <div class="cognitive-test-page">
    <!-- 顶部标题栏 -->
    <div class="header">
      <button class="back-button" @click="goBack">
        <span class="back-icon">←</span>
        <span>返回</span>
      </button>
      <h1 class="title">认知功能及康复测试</h1>
    </div>

    <!-- 主体内容 -->
    <div class="content">
      <div class="button-grid">
        <button class="test-button" @click="navigateToTest('objective')">
          客观心理评估表
        </button>

        <button class="test-button" @click="navigateToTest('informer')">
          知情者报告者表
        </button>

        <button class="test-button" @click="navigateToTest('questionnaire')">
          (半)结构式问卷
        </button>

        <button class="test-button" @click="navigateToTest('other')">
          其他情况评估表
        </button>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'CognitiveTestPage',
  methods: {
    goBack() {
      this.$router.go(-1);
    },
    navigateToTest(testType) {
      switch(testType) {
        case 'objective':
          this.$router.push('/cognitive-assessment');
          break;
        case 'informer':
          this.$router.push('/rehabilitation-plan');
          break;
        case 'questionnaire':
          this.$router.push('/progress-tracking');
          break;
        case 'other':
          this.$router.push('/report-analysis');
          break;
        default:
          console.log(`Navigate to ${testType} test`);
      }
    }
  }
}
</script>

<style scoped>
.cognitive-test-page {
  font-family: 'PingFang SC', 'Microsoft YaHei', sans-serif;
  height: 100vh;
  display: flex;
  flex-direction: column;
}

.header {
  background-color: #1e88e5; /* 深蓝色 */
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
  margin-right: 5px;
  font-size: 18px;
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
  background-color: #f5f5f5; /* 浅灰色背景 */
  padding: 30px 20px;
}

.button-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
}

.test-button {
  background-color: white;
  color: black;
  border: none;
  border-radius: 12px;
  padding: 20px 10px;
  font-size: 16px;
  text-align: center;
  cursor: pointer;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
  transition: transform 0.2s, box-shadow 0.2s;
}

.test-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.15);
}

.test-button:active {
  transform: translateY(0);
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
}
</style>
