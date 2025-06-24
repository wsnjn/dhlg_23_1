<template>
  <div class="word-memory-test-page">
    <!-- 顶部导航栏 -->
    <div class="header">
      <button class="back-button" @click="goBack">
        <span class="back-icon">←</span>
      </button>
      <div class="timer" style="position: absolute; left: 20px; top: 15px; color: red; font-size: 24px; font-weight: bold;">{{ formattedTime }}</div>
    </div>

    <!-- 提示文字 -->
    <div class="instruction">
      请快速说出随机词，确认记住以后请点击下一步
    </div>

    <!-- 词语展示区域 -->
    <div class="word-display">
      <div class="word" v-for="(word, index) in currentWords" :key="index">
        {{ word }}
      </div>
    </div>

    <!-- 下一步按钮 -->
    <button class="next-button" @click="goToNextStep">
      下一步
    </button>
  </div>
</template>

<script>
export default {
  name: 'WordMemoryTestPage',
  data() {
    return {
      wordBank: [
        '蔚蓝', '胖瘦', '牛奶', '天空', '书本',
        '电脑', '手机', '快乐', '跑步', '音乐',
        '花园', '茶杯', '铅笔', '沙发', '窗户'
      ],
      currentWords: [],
      timeLeft: 5, // 5秒倒计时
      timer: null
    }
  },
  computed: {
    formattedTime() {
      return `${this.timeLeft}秒`;
    }
  },
  created() {
    this.generateRandomWords();
    this.startTimer();
  },
  beforeUnmount() {
    this.clearTimer();
  },
  methods: {
    generateRandomWords() {
      const shuffled = [...this.wordBank].sort(() => 0.5 - Math.random());
      this.currentWords = shuffled.slice(0, 3);
    },
    startTimer() {
      this.clearTimer();
      this.timer = setInterval(() => {
        this.timeLeft--;
        if (this.timeLeft <= 0) {
          this.goToNextStep();
        }
      }, 1000);
    },
    clearTimer() {
      if (this.timer) {
        clearInterval(this.timer);
        this.timer = null;
      }
    },
    goBack() {
      this.$router.go(-1);
    },
    async goToNextStep() {
      this.clearTimer();

      try {
        // 获取当前登录用户
        const user = JSON.parse(localStorage.getItem('currentUser'));
        if (!user) {
          throw new Error('用户未登录');
        }

        // 检查用户数据是否完整
        if (!user.hospitalNumber || !user.name) {
          throw new Error('用户信息不完整，缺少住院号或姓名');
        }

        // 发送单词到后端存储
        const response = await fetch('http://localhost:8080/api/store-words', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json'
          },
          body: JSON.stringify({
            userId: user.id || user.hospitalNumber, // 优先使用用户ID，没有则使用住院号
            words: this.currentWords,
            hospitalNumber: user.hospitalNumber,
            name: user.name
          })
        });

        if (!response.ok) {
          throw new Error('存储单词失败');
        }

        // 存储成功后再跳转
        this.$router.push('/draw');
      } catch (error) {
        console.error('存储单词错误:', error);
        alert('存储单词失败: ' + error.message);
      }
    }
  }
}
</script>
<style scoped>
.word-memory-test-page {
  font-family: 'PingFang SC', 'Microsoft YaHei', sans-serif;
  height: 100vh;
  display: flex;
  flex-direction: column;
  background: linear-gradient(to bottom, #87CEEB, #E0F7FA); /* 蓝天白云渐变背景 */
}

.header {
  padding: 15px 20px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.back-button {
  background: none;
  border: none;
  color: #333;
  font-size: 16px;
  display: flex;
  align-items: center;
  cursor: pointer;
}

.back-icon {
  font-size: 20px;
}

.timer {
  font-size: 16px;
  font-weight: bold;
  color: #333;
}

.instruction {
  text-align: center;
  margin: 20px 0;
  padding: 0 20px;
  font-size: 16px;
  color: #333;
}

.word-display {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  gap: 30px;
  padding: 0 20px;
}

.word {
  font-size: 24px;
  font-weight: bold;
  color: #333;
  text-align: center;
  width: 100%;
  max-width: 200px;
  padding: 15px;
  background-color: rgba(255, 255, 255, 0.7);
  border-radius: 10px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.next-button {
  background-color: #1e88e5; /* 蓝色按钮 */
  color: white;
  border: none;
  border-radius: 8px;
  padding: 16px;
  font-size: 18px;
  font-weight: bold;
  text-align: center;
  cursor: pointer;
  margin: 20px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
  transition: transform 0.2s, box-shadow 0.2s;
}

.next-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 12px rgba(0, 0, 0, 0.2);
}

.next-button:active {
  transform: translateY(0);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
}
</style>
