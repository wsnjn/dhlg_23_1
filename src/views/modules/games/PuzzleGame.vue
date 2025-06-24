<template>
  <div class="puzzle-game">
    <button class="back-button" @click="$emit('back')">← 返回</button>
    <h3>数字拼图</h3>

    <div class="puzzle-container">
      <div
        v-for="(tile, index) in tiles"
        :key="index"
        class="puzzle-tile"
        :class="{ empty: tile === 0 }"
        @click="moveTile(index)"
      >
        {{ tile !== 0 ? tile : '' }}
      </div>
    </div>

    <div class="puzzle-controls">
      <p>移动次数: {{ moves }}</p>
      <button @click="shuffle">重新开始</button>
    </div>
  </div>
</template>

<script>
export default {
  name: 'PuzzleGame',
  data() {
    return {
      tiles: [1, 2, 3, 4, 5, 6, 7, 8, 0],
      moves: 0
    }
  },
  created() {
    this.shuffle()
  },
  methods: {
    shuffle() {
      this.tiles = [1, 2, 3, 4, 5, 6, 7, 8, 0]
        .sort(() => Math.random() - 0.5)
      this.moves = 0
    },
    moveTile(index) {
      const emptyIndex = this.tiles.indexOf(0)
      const row = Math.floor(index / 3)
      const col = index % 3
      const emptyRow = Math.floor(emptyIndex / 3)
      const emptyCol = emptyIndex % 3

      if ((row === emptyRow && Math.abs(col - emptyCol) === 1) ||
          (col === emptyCol && Math.abs(row - emptyRow) === 1)) {
        // 交换位置
        [this.tiles[index], this.tiles[emptyIndex]] =
          [this.tiles[emptyIndex], this.tiles[index]]
        this.moves++
      }
    }
  }
}
</script>

<style scoped>
.puzzle-game {
  max-width: 400px;
  margin: 0 auto;
  padding: 20px;
}

.puzzle-container {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 5px;
  margin: 20px auto;
  width: 300px;
}

.puzzle-tile {
  height: 100px;
  background: #2196F3;
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  cursor: pointer;
  border-radius: 5px;
}

.puzzle-tile.empty {
  background: transparent;
  cursor: default;
}

.puzzle-controls {
  margin-top: 20px;
  text-align: center;
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
