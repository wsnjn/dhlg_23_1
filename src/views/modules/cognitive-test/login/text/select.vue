<template>
  <div class="word-selection-page">
    <div class="header">
      <h1 class="title">单词选择测试</h1>
    </div>

    <div class="instruction">
      <p>请选择您之前看到的3个单词</p>
    </div>

    <div class="word-grid">
      <button
        v-for="(word, index) in wordOptions"
        :key="index"
        class="word-button"
        :class="{ selected: selectedWords.includes(word) }"
        @click="toggleWord(word)"
      >
        {{ word }}
      </button>
    </div>

    <div class="controls">
      <button class="submit-button" @click="submitSelection">提交</button>
    </div>
  </div>
</template>

<script>
export default {
  name: 'WordSelectionPage',
  data() {
    return {
      targetWords: [], // 从数据库获取的目标单词
      wordOptions: [], // 包含目标单词的选项列表
      selectedWords: [], // 用户选择的单词
      isLoading: true // 加载状态
    }
  },
  async created() {
    await this.fetchTargetWords();
    this.generateWordOptions();
  },
  methods: {
    async fetchTargetWords() {
      try {
        const user = JSON.parse(localStorage.getItem('currentUser'));
        console.log('当前用户信息:', user);
        if (!user || !user.hospitalNumber || !user.name) {
          console.error('用户信息不完整:', user);
          throw new Error('用户信息不完整');
        }
        console.log('请求参数:', {
          hospitalNumber: user.hospitalNumber,
          name: user.name
        });

        const response = await fetch(
          `http://localhost:8080/api/select?hospitalNumber=${user.hospitalNumber}&name=${encodeURIComponent(user.name)}`
        );
        if (!response.ok) {
          throw new Error('获取单词失败');
        }

        const data = await response.json();
        this.targetWords = data.words;
        console.log('从API获取的正确单词:', this.targetWords);
      } catch (error) {
        console.error('获取目标单词错误:', error);
        alert('获取单词失败: ' + error.message);
      } finally {
        this.isLoading = false;
      }
    },

    generateWordOptions() {
      // 从固定词库中随机选择6个额外单词
      const allWords = ['苹果', '香蕉', '橙子', '梨', '葡萄', '西瓜', '芒果', '草莓', '菠萝'];
      const otherWords = allWords.filter(word => !this.targetWords.includes(word))
                                .sort(() => 0.5 - Math.random())
                                .slice(0, 6);

      // 合并目标单词和随机单词
      this.wordOptions = [...this.targetWords, ...otherWords]
                          .sort(() => Math.random() - 0.5);
    },
    toggleWord(word) {
      if (this.selectedWords.includes(word)) {
        this.selectedWords = this.selectedWords.filter(w => w !== word);
      } else if (this.selectedWords.length < 3) {
        this.selectedWords.push(word);
      }
    },
    submitSelection() {
      const correctCount = this.selectedWords.filter(word =>
        this.targetWords.includes(word)
      ).length;

      // 计算得分：1个=1分，2个=3分，3个=5分
      const score = correctCount === 3 ? 5 :
                   correctCount === 2 ? 3 :
                   correctCount === 1 ? 1 : 0;

      // 存储得分到localStorage
      localStorage.setItem('selectScore', score);

      // 直接跳转到key测试页
      this.$router.push('/key');
    }
  }
}
</script>

<style scoped>
.word-selection-page {
  font-family: 'PingFang SC', 'Microsoft YaHei', sans-serif;
  height: 100vh;
  display: flex;
  flex-direction: column;
  background: linear-gradient(to bottom, #87CEEB, #E0F7FA);
  padding: 20px;
}

.header {
  text-align: center;
  margin-bottom: 20px;
}

.title {
  font-size: 24px;
  color: #333;
}

.instruction {
  text-align: center;
  margin-bottom: 30px;
  font-size: 18px;
}

.word-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 15px;
  margin-bottom: 30px;
}

.word-button {
  padding: 15px;
  border: 2px solid #ddd;
  border-radius: 8px;
  background-color: white;
  font-size: 16px;
  cursor: pointer;
  transition: all 0.2s;
}

.word-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(0,0,0,0.1);
}

.word-button.selected {
  background-color: #4CAF50;
  color: white;
  border-color: #4CAF50;
}

.controls {
  display: flex;
  justify-content: center;
}

.submit-button {
  padding: 12px 24px;
  background-color: #4CAF50;
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  cursor: pointer;
}
</style>
