<template>
  <div class="test-details-view">
    <div class="header">
      <h1>{{ testData.testType }} - {{ formattedDate }}</h1>
      <div class="score">总得分: {{ testData.score }}</div>
      <button @click="goBack" class="back-btn">返回</button>
    </div>

    <div class="questions-container">
      <div v-for="(response, index) in testData.responses" :key="index" class="question-item">
        <div class="question-text">{{ response.questionText || response.question_text || `问题 ${response.question_id}` }}</div>
        <div class="answer-text">{{ response.answer || response.answer_text || '无回答' }}</div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'TestDetails',
  data() {
    return {
      testData: {}
    }
  },
  computed: {
    formattedDate() {
      return this.testData.testDate ? new Date(this.testData.testDate).toLocaleDateString() : '无日期'
    }
  },
  created() {
    if (this.$route.params.testData) {
      this.testData = JSON.parse(this.$route.params.testData)
    }
  },
  methods: {
    goBack() {
      this.$router.go(-1)
    }
  }
}
</script>

<style scoped>
.test-details-view {
  padding: 2rem;
  max-width: 800px;
  margin: 0 auto;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2rem;
  padding-bottom: 1rem;
  border-bottom: 1px solid #eee;
}

h1 {
  margin: 0;
  font-size: 1.5rem;
  color: #2c3e50;
}

.score {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 0.5rem 1rem;
  border-radius: 20px;
  font-weight: 500;
}

.back-btn {
  padding: 0.5rem 1rem;
  background: #6c757d;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
}

.questions-container {
  margin-top: 1rem;
}

.question-item {
  margin-bottom: 1.5rem;
  padding: 1rem;
  background: #f8f9fa;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.05);
}

.question-text {
  font-weight: 600;
  color: #2c3e50;
  margin-bottom: 0.5rem;
}

.answer-text {
  color: #555;
  padding-left: 1rem;
  border-left: 3px solid #667eea;
}
</style>
