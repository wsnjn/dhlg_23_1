<template>
  <div class="memory-game">
    <button class="back-button" @click="$emit('back')">← 返回</button>
    <h3>记忆翻牌游戏</h3>

    <div class="game-info">
      <p>得分: {{ score }}</p>
      <p>剩余时间: {{ timeLeft }}秒</p>
      <button @click="startGame">开始游戏</button>
    </div>

    <div class="game-board">
      <div
        v-for="(card, index) in cards"
        :key="index"
        class="game-card"
        :class="{ flipped: card.flipped, matched: card.matched }"
        @click="flipCard(index)"
      >
        <div class="card-front">?</div>
        <div class="card-back">{{ card.value }}</div>
      </div>
    </div>

    <div v-if="gameOver" class="game-over">
      <h3>游戏结束!</h3>
      <p>你的最终得分: {{ score }}</p>
      <button @click="resetGame">再玩一次</button>
    </div>
  </div>
</template>

<script>
export default {
  name: 'MemoryGame',
  data() {
    return {
      cards: [],
      score: 0,
      timeLeft: 60,
      timer: null,
      gameOver: false,
      flippedCards: []
    }
  },
  created() {
    this.resetGame()
  },
  methods: {
    resetGame() {
      const values = ['A', 'B', 'C', 'D', 'E', 'F', 'A', 'B', 'C', 'D', 'E', 'F']
      this.cards = values
        .sort(() => Math.random() - 0.5)
        .map(value => ({
          value,
          flipped: false,
          matched: false
        }))

      this.score = 0
      this.timeLeft = 60
      this.gameOver = false
      this.flippedCards = []
    },
    startGame() {
      if (this.timer) clearInterval(this.timer)
      this.resetGame()
      this.timer = setInterval(() => {
        this.timeLeft--
        if (this.timeLeft <= 0) {
          this.endGame()
        }
      }, 1000)
    },
    flipCard(index) {
      if (this.gameOver || this.cards[index].flipped || this.flippedCards.length >= 2) return

      this.cards[index].flipped = true
      this.flippedCards.push(index)

      if (this.flippedCards.length === 2) {
        this.checkMatch()
      }
    },
    checkMatch() {
      const [first, second] = this.flippedCards
      if (this.cards[first].value === this.cards[second].value) {
        this.cards[first].matched = true
        this.cards[second].matched = true
        this.score += 10
      } else {
        setTimeout(() => {
          this.cards[first].flipped = false
          this.cards[second].flipped = false
        }, 1000)
      }
      this.flippedCards = []
    },
    endGame() {
      clearInterval(this.timer)
      this.gameOver = true
    }
  }
}
</script>

<style scoped>
.memory-game {
  max-width: 100%;
  margin: 0 auto;
  padding: 15px;
  box-sizing: border-box;
}

.game-info {
  margin-bottom: 20px;
}

.game-board {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  grid-template-rows: repeat(4, 1fr);
  gap: 8px;
  margin: 0 auto;
  width: 100%;
  max-width: 400px;
}

.game-card {
  width: 100%;
  aspect-ratio: 1;
  position: relative;
  cursor: pointer;
  transform-style: preserve-3d;
  transition: transform 0.5s;
}

.game-card.flipped {
  transform: rotateY(180deg);
}

.game-card.matched {
  opacity: 0.5;
  cursor: default;
}

.card-front, .card-back {
  position: absolute;
  width: 100%;
  height: 100%;
  backface-visibility: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 8px;
  font-size: 20px;
  font-weight: bold;
}

.card-front {
  background-color: #4CAF50;
  color: white;
}

.card-back {
  background-color: #f5f5f5;
  transform: rotateY(180deg);
}

button {
  padding: 8px 16px;
  background-color: #2196F3;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  margin: 0 5px;
}

button:hover {
  background-color: #0b7dda;
}

.game-over {
  margin-top: 20px;
  padding: 20px;
  background-color: #f5f5f5;
  border-radius: 8px;
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
