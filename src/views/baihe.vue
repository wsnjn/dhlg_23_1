<template>
  <div class="care-center-container">
    <!-- 主标题 -->
    <h1 class="title">佰和护理中心</h1>

    <!-- 功能按钮区域 -->
    <div class="button-grid">
      <div class="button customer-center" @click="navigateTo('customer-center')">
        <span>客户中心</span>
      </div>

      <div class="button scale-test" @click="navigateTo('scale-test')">
        <span>量表测试</span>
      </div>

      <div class="button ai-analysis" @click="navigateTo('ai-analysis')">
        <span>AI辅助数据分析</span>
      </div>

      <div class="button emotion-chat" @click="navigateTo('emotion-chat')">
        <span>情感聊天</span>
      </div>

      <div class="button entertainment" @click="navigateTo('entertainment')">
        <span>休闲娱乐</span>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'CareCenter',
  methods: {
    navigateTo(route) {
      this.$router.push({ name: route });
    }
  }
}
</script>

<style scoped>
.care-center-container {
  font-family: 'PingFang SC', 'Microsoft YaHei', sans-serif;
  background-color: #f5f5f5;
  min-height: 100vh;
  padding: 20px;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.title {
  color: #000;
  font-size: 24px;
  margin: 30px 0 50px;
  text-align: center;
}

.button-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  grid-template-rows: repeat(3, 1fr);
  gap: 20px;
  width: 100%;
  max-width: 600px;
  height: 600px;
}

.button {
  display: flex;
  justify-content: center;
  align-items: center;
  border-radius: 12px;
  color: white;
  font-size: 18px;
  font-weight: bold;
  cursor: pointer;
  transition: transform 0.2s;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.button:hover {
  transform: translateY(-5px);
}

.customer-center {
  background-color: #ff4757;
  grid-column: 1;
  grid-row: 1;
}

.scale-test {
  background-color: #2ed573;
  grid-column: 3;
  grid-row: 1;
}

.ai-analysis {
  background-color: #1e90ff;
  grid-column: 2;
  grid-row: 2;
}

.emotion-chat {
  background-color: #9c88ff;
  grid-column: 1;
  grid-row: 3;
}

.entertainment {
  background-color: #ff7f50;
  grid-column: 3;
  grid-row: 3;
}
</style>
