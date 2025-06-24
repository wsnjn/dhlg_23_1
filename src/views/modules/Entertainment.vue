<template>
  <div class="entertainment">
    <div class="back-button-container">
      <button class="back-button" @click="goBack">← 返回</button>
    </div>
    <h2>娱乐中心</h2>

    <div v-if="!currentGame" class="game-selection">
      <h3>请选择游戏</h3>
      <div class="game-options">
        <div class="game-option" @click="selectGame('memory')">
          <h4>记忆翻牌</h4>
          <p>测试记忆力的翻牌配对游戏</p>
        </div>
        <div class="game-option" @click="selectGame('puzzle')">
          <h4>数字拼图</h4>
          <p>拖动数字完成拼图的益智游戏</p>
        </div>
        <div class="game-option" @click="selectGame('quiz')">
          <h4>知识问答</h4>
          <p>回答简单问题的知识挑战</p>
        </div>
      </div>
    </div>

    <!-- 记忆翻牌游戏 -->
    <MemoryGame v-if="currentGame === 'memory'" @back="currentGame = null"/>

    <!-- 数字拼图游戏 -->
    <PuzzleGame v-if="currentGame === 'puzzle'" @back="currentGame = null"/>

    <!-- 知识问答游戏 -->
    <QuizGame v-if="currentGame === 'quiz'" @back="currentGame = null"/>
  </div>
</template>

<script>
import MemoryGame from './games/MemoryGame.vue'
import PuzzleGame from './games/PuzzleGame.vue'
import QuizGame from './games/QuizGame.vue'

export default {
  name: 'EntertainmentPage',
  components: { MemoryGame, PuzzleGame, QuizGame },
  data() {
    return {
      currentGame: null
    }
  },
  methods: {
    selectGame(game) {
      this.currentGame = game
    },
    goBack() {
      this.$router.go(-1)
    }
  }
}
</script>

<style scoped>
.entertainment {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
  text-align: center;
}

.back-button-container {
  display: flex;
  justify-content: center;
  margin-bottom: 20px;
}

.back-button {
  padding: 8px 16px;
  background: #4a6fa5;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.95em;
  transition: background-color 0.2s;
  width: 120px;
}

.back-button:hover {
  background: #3a5a8f;
}

.game-selection {
  margin-top: 30px;
}

.game-options {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
  margin-top: 20px;
}

.game-option {
  padding: 20px;
  background: #e0e0e0;
  border-radius: 8px;
  cursor: pointer;
  transition: transform 0.2s;
}

.game-option:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 8px rgba(0,0,0,0.1);
}

.game-option h4 {
  margin-top: 0;
  color: #2196F3;
}
</style>
