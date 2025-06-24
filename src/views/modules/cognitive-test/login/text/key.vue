<template>
  <div class="score-display-page">
    <div class="header">
      <h1 class="title">测试结果</h1>
    </div>

    <div class="score-display">
      <div class="total-score">总分: {{ totalScore }}/10</div>
    </div>

    <div class="controls">
      <button class="next-button" @click="goToNextStep">请点击确认</button>
    </div>
  </div>
</template>

<script>
export default {
  name: 'ScoreDisplayPage',
  data() {
    return {
      totalScore: 0
    }
  },
  created() {
    this.calculateScore();
  },
  methods: {
    calculateScore() {
      // 从draw测试获取分数 (画钟表测试)
      const drawScore = parseInt(localStorage.getItem('drawScore'))  || 0;

      // 从select测试获取分数
      const selectScore = parseInt(localStorage.getItem('selectScore')) || 0;

      // 计算总分
      this.totalScore = drawScore + selectScore;

      // 控制台输出各项目得分
      console.log('画钟表测试得分:', drawScore);
      console.log('选择测试得分:', selectScore);
      console.log('总分:', this.totalScore);
    },
    goToNextStep() {
      // 重置尝试次数为1
      sessionStorage.setItem('drawAttempts', '1');
      // 跳转到首页
      this.$router.push('/');
    }
  }
}
</script>

<style scoped>
.score-display-page {
  font-family: 'PingFang SC', 'Microsoft YaHei', sans-serif;
  height: 100vh;
  display: flex;
  flex-direction: column;
  background: linear-gradient(to bottom, #87CEEB, #E0F7FA);
  padding: 20px;
}

.header {
  text-align: center;
  margin-bottom: 40px;
}

.title {
  font-size: 24px;
  color: #333;
}

.score-display {
  flex: 1;
  display: flex;
  justify-content: center;
  align-items: center;
}

.total-score {
  font-size: 32px;
  font-weight: bold;
  color: #1e88e5;
}

.controls {
  display: flex;
  justify-content: center;
  margin-bottom: 30px;
}

.next-button {
  padding: 12px 24px;
  background-color: #1e88e5;
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  cursor: pointer;
}
</style>
