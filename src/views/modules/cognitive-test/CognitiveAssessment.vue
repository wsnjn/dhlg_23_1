<template>
  <div class="psychological-assessment-page">
    <!-- 顶部标题栏 -->
    <div class="header">
      <button class="back-button" @click="goBack">
        <span class="back-icon">←</span>
      </button>
      <h1 class="title">心理评估量表</h1>
    </div>

    <!-- 主体内容 -->
    <div class="content">
      <div class="assessment-list">
        <button
          v-for="(assessment, index) in assessments"
          :key="index"
          class="assessment-button"
          @click="navigateToAssessment(assessment.id)"
        >
          {{ assessment.name }}
        </button>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'PsychologicalAssessmentPage',
  data() {
    return {
      assessments: [
        { id: 'mini-cog', name: '简易智力状态评估表(MINI-COG)' },
        { id: 'casi', name: '早期痴呆简易筛选量表（AD8）' },
        { id: 'blessed', name: '匹兹堡睡眠质量指数量表（PSQI）' },
        { id: 'moca', name: '认知功能障碍评定表(MoCA)' },
        { id: 'hds', name: '老年痴呆检查量表(HDS)' },
        { id: 'mmse', name: '简易智力精神状态检查量表(MMSE)' }
      ]
    }
  },
  methods: {
    goBack() {
      this.$router.go(-1);
    },
    navigateToAssessment(assessmentId) {
      if (assessmentId === 'mini-cog' || assessmentId === 'casi' || assessmentId === 'blessed') {
        this.$router.push({
          path: '/login',
          query: { testType: assessmentId }
        });
      } else {
        console.log(`Navigate to ${assessmentId} assessment`);
      }
    }
  }
}
</script>

<style scoped>
.psychological-assessment-page {
  font-family: 'PingFang SC', 'Microsoft YaHei', sans-serif;
  height: 100vh;
  display: flex;
  flex-direction: column;
  background-color: #e6f2ff; /* 浅蓝色背景 */
}

.header {
  background-color: #1e88e5; /* 深蓝色 */
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

.content {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
}

.assessment-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.assessment-button {
  background-color: white;
  color: #333;
  border: none;
  border-radius: 12px;
  padding: 16px;
  font-size: 16px;
  text-align: left;
  cursor: pointer;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
  transition: transform 0.2s, box-shadow 0.2s;
  width: 100%;
}

.assessment-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.15);
}

.assessment-button:active {
  transform: translateY(0);
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
}
</style>
