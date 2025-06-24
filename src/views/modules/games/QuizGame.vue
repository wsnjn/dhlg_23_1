<template>
  <div class="quiz-game">
    <button class="back-button" @click="$emit('back')">← 返回</button>
    <h3>知识问答</h3>

    <div v-if="currentQuestion < questions.length" class="quiz-container">
      <div class="question">{{ questions[currentQuestion].text }}</div>
      <div class="options">
        <button
          v-for="(option, index) in questions[currentQuestion].options"
          :key="index"
          @click="checkAnswer(index)"
          :disabled="answered"
        >
          {{ option }}
        </button>
      </div>
      <div v-if="answered" class="feedback">
        <p v-if="isCorrect">✓ 回答正确!</p>
        <p v-else>✗ 回答错误! 正确答案是: {{ correctAnswer }}</p>
        <button @click="nextQuestion">下一题</button>
      </div>
    </div>

    <div v-else class="quiz-results">
      <h4>测验完成!</h4>
      <p>你的得分: {{ score }} / {{ questions.length }}</p>
      <button @click="resetQuiz">再玩一次</button>
    </div>
  </div>
</template>

<script>
export default {
  name: 'QuizGame',
  data() {
    return {
      questions: [
        {
          text: '中国的首都是哪个城市?',
          options: ['上海', '北京', '广州', '深圳'],
          answer: 1
        },
        {
          text: '太阳系中最大的行星是?',
          options: ['地球', '木星', '土星', '火星'],
          answer: 1
        },
        {
          text: '水的化学式是?',
          options: ['H2O', 'CO2', 'NaCl', 'O2'],
          answer: 0
        },
        {
          text: '一年有多少个月?',
          options: ['10', '11', '12', '13'],
          answer: 2
        },
        {
          text: '以下哪个不是颜色?',
          options: ['红色', '苹果', '蓝色', '绿色'],
          answer: 1
        }
      ],
      currentQuestion: 0,
      score: 0,
      answered: false,
      isCorrect: false,
      correctAnswer: ''
    }
  },
  methods: {
    checkAnswer(selectedIndex) {
      this.answered = true
      const question = this.questions[this.currentQuestion]
      this.isCorrect = selectedIndex === question.answer
      this.correctAnswer = question.options[question.answer]

      if (this.isCorrect) {
        this.score++
      }
    },
    nextQuestion() {
      this.currentQuestion++
      this.answered = false
      this.isCorrect = false
    },
    resetQuiz() {
      this.currentQuestion = 0
      this.score = 0
      this.answered = false
      this.isCorrect = false
    }
  }
}
</script>

<style scoped>
.quiz-game {
  max-width: 600px;
  margin: 0 auto;
  padding: 20px;
}

.question {
  font-size: 20px;
  margin: 20px 0;
  font-weight: bold;
}

.options {
  display: grid;
  grid-template-columns: 1fr;
  gap: 10px;
  margin: 20px 0;
}

.options button {
  padding: 10px;
  background: #f5f5f5;
  border: 1px solid #ddd;
  border-radius: 5px;
  cursor: pointer;
}

.options button:hover {
  background: #e0e0e0;
}

.options button:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.feedback {
  margin: 20px 0;
  padding: 15px;
  background: #f9f9f9;
  border-radius: 5px;
}

.feedback p {
  margin: 0 0 10px 0;
  font-size: 18px;
}

.quiz-results {
  text-align: center;
  margin-top: 50px;
}

.back-button {
  background: none;
  border: none;
  color: #2196F3;
  cursor: pointer;
  font-size: 16px;
  margin-bottom: 20px;
}
</style>
