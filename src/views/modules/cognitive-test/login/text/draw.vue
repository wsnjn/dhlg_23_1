<template>
  <div class="clock-drawing-page">
    <div class="header">
      <button class="back-button" @click="goBack">
        <span class="back-icon">←</span>
      </button>
      <h1 class="title">时钟绘制测试</h1>
    </div>

    <div class="instruction">
      <p>请调整时针和分针，使时间为 {{ targetTime }}</p>
    </div>

    <div class="drawing-area">
      <canvas ref="canvas"></canvas>
    </div>

    <div class="controls">
      <div>
        <label>时针角度 (°)</label>
        <input type="range" min="0" max="360" v-model="userHourAngleDeg" @input="drawClock"/>
      </div>
      <div>
        <label>分针角度 (°)</label>
        <input type="range" min="0" max="360" v-model="userMinuteAngleDeg" @input="drawClock"/>
      </div>
    </div>

    <div class="controls">
      <button class="submit-button" @click="submitDrawing">提交</button>
    </div>
  </div>
</template>

<script>
export default {
  name: 'ClockDrawingPage',
  data() {
    // 生成随机时间 (小时1-12，分钟0或30)
    const randomHour = Math.floor(Math.random() * 12) + 1;
    const randomMinute = Math.random() > 0.5 ? 0 : 30;
    const targetTime = `${randomHour}:${randomMinute.toString().padStart(2, '0')}`;

    return {
      targetTime,
      canvas: null,
      ctx: null,
      userHourAngleDeg: 0,
      userMinuteAngleDeg: 0
    };
  },
  mounted() {
    this.initCanvas();
    this.drawClock();
  },
  methods: {
    initCanvas() {
      this.canvas = this.$refs.canvas;
      this.ctx = this.canvas.getContext('2d');
      this.canvas.width = 400;
      this.canvas.height = 400;
      this.ctx.lineWidth = 2;
      this.ctx.strokeStyle = '#000';
    },
    drawClock() {
      const ctx = this.ctx;
      const centerX = this.canvas.width / 2;
      const centerY = this.canvas.height / 2;
      const radius = 150;

      ctx.clearRect(0, 0, this.canvas.width, this.canvas.height);

      // 外圈
      ctx.beginPath();
      ctx.arc(centerX, centerY, radius, 0, Math.PI * 2);
      ctx.stroke();

      // 数字
      ctx.font = '20px Arial';
      ctx.textAlign = 'center';
      ctx.textBaseline = 'middle';
      for (let i = 1; i <= 12; i++) {
        const angle = (i - 3) * (Math.PI * 2) / 12;
        const x = centerX + Math.cos(angle) * (radius - 30);
        const y = centerY + Math.sin(angle) * (radius - 30);
        ctx.fillText(i.toString(), x, y);
      }

      // 时针
      const hourAngle = (this.userHourAngleDeg - 90) * Math.PI / 180;
      this.drawHand(centerX, centerY, hourAngle, 60, 6);

      // 分针
      const minuteAngle = (this.userMinuteAngleDeg - 90) * Math.PI / 180;
      this.drawHand(centerX, centerY, minuteAngle, 90, 4);
    },
    drawHand(centerX, centerY, angle, length, width) {
      this.ctx.beginPath();
      this.ctx.moveTo(centerX, centerY);
      this.ctx.lineTo(centerX + Math.cos(angle) * length, centerY + Math.sin(angle) * length);
      this.ctx.lineWidth = width;
      this.ctx.stroke();
      this.ctx.lineWidth = 2;
    },
    calculateExpectedScore() {
      const attempts = parseInt(sessionStorage.getItem('drawAttempts') || '0');
      return attempts === 0 ? 5 : Math.max(0, 5 - (attempts - 1));
    },
    submitDrawing() {
      console.log('当前尝试次数:', parseInt(sessionStorage.getItem('drawAttempts') || '0'));
      console.log('预计得分:', this.calculateExpectedScore());

      const [targetHour, targetMinute] = this.targetTime.split(':').map(Number);

      const correctHourAngleDeg = ((targetHour % 12 + targetMinute / 60) * 30);
      const correctMinuteAngleDeg = targetMinute * 6;

      const hourDiff = Math.abs((this.userHourAngleDeg - correctHourAngleDeg + 180) % 360 - 180);
      const minuteDiff = Math.abs((this.userMinuteAngleDeg - correctMinuteAngleDeg + 180) % 360 - 180);

      // 获取当前尝试次数
      const attempts = parseInt(sessionStorage.getItem('drawAttempts') || '0') + 1;
      sessionStorage.setItem('drawAttempts', attempts);

      if (hourDiff <= 10 && minuteDiff <= 10) {
        // 计算得分：第一次成功5分，每重试一次扣1分
        const score = attempts === 1 ? 5 : Math.max(0, 5 - (attempts - 1));
        localStorage.setItem('drawScore', score);
        this.$router.push('/select');
      } else if (hourDiff <= 10) {
        alert(`时针正确(差${hourDiff.toFixed(1)}°)，分针需调整(差${minuteDiff.toFixed(1)}°)`);
      } else if (minuteDiff <= 10) {
        alert(`分针正确(差${minuteDiff.toFixed(1)}°)，时针需调整(差${hourDiff.toFixed(1)}°)`);
      } else {
        alert(`请重新调整:\n时针差${hourDiff.toFixed(1)}°\n分针差${minuteDiff.toFixed(1)}°`);
      }
    },
    goBack() {
      this.$router.go(-1);
    }
  }
};
</script>


<style scoped>
.clock-drawing-page {
  font-family: 'PingFang SC', 'Microsoft YaHei', sans-serif;
  height: 100vh;
  display: flex;
  flex-direction: column;
  background: linear-gradient(to bottom, #87CEEB, #E0F7FA);
}

.header {
  background-color: rgba(30, 136, 229, 0.8);
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
  font-size: 16px;
  display: flex;
  align-items: center;
  cursor: pointer;
  margin-right: 15px;
}

.back-icon {
  font-size: 20px;
}

.title {
  font-size: 18px;
  font-weight: 500;
  margin: 0;
  flex: 1;
  text-align: center;
}

.instruction {
  text-align: center;
  padding: 20px;
  font-size: 16px;
}

.drawing-area {
  flex: 1;
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 20px;
}

canvas {
  border: 1px solid #ddd;
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.controls {
  display: flex;
  justify-content: center;
  padding: 20px;
  gap: 20px;
}

.clear-button, .submit-button {
  padding: 12px 24px;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  font-weight: bold;
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
}

.clear-button {
  background-color: #f44336;
  color: white;
}

.submit-button {
  background-color: #4CAF50;
  color: white;
}

.clear-button:hover, .submit-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
}

.clear-button:active, .submit-button:active {
  transform: translateY(0);
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
}
</style>
