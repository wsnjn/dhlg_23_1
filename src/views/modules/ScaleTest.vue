<template>
  <div class="scale-test-page">
    <!-- 顶部标题栏 -->
    <div class="header">
      <button class="back-button" @click="goBack">
        <i class="icon-arrow-left"></i>
      </button>
      <h1 class="title">量表测试</h1>
    </div>

    <!-- 主体内容 -->
    <div class="content">
      <button class="test-button" @click="navigateToTest('dementia')">
        痴呆及认知障碍测试表
      </button>

      <button class="test-button" @click="navigateToTest('evaluation')">
        评定表（试行）
      </button>
    </div>
  </div>
</template>

<script>
export default {
  name: 'ScaleTestPage',
  methods: {
    goBack() {
      this.$router.go(-1);
    },
    navigateToTest(testType) {
      if (testType === 'dementia') {
        this.$router.push('/dementia-test');
      } else {
        console.log(`Navigate to ${testType} test`);
      }
    }
  }
}
</script>

<style scoped>
.scale-test-page {
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
  font-size: 20px;
  margin-right: 15px;
  cursor: pointer;
}

.title {
  font-size: 18px;
  font-weight: 500;
  margin: 0;
}

.content {
  flex: 1;
  background-color: #f5f5f5; /* 浅灰色背景 */
  padding: 30px 20px;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.test-button {
  background-color: #4caf50; /* 绿色按钮 */
  color: white;
  border: none;
  border-radius: 8px;
  padding: 16px;
  font-size: 16px;
  font-weight: 500;
  text-align: center;
  cursor: pointer;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.2);
  transition: transform 0.2s, box-shadow 0.2s;
}

.test-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
}

.test-button:active {
  transform: translateY(0);
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.2);
}

/* 返回箭头图标样式 - 可以使用字体图标或SVG */
.icon-arrow-left {
  display: inline-block;
  width: 12px;
  height: 12px;
  border-left: 2px solid white;
  border-bottom: 2px solid white;
  transform: rotate(45deg);
}
</style>
