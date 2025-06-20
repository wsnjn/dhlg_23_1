<template>
  <div class="ad8-container">
    <!-- 登录状态显示 -->
    <div class="user-info" v-if="currentUser">
      <h3>当前用户: {{ currentUser.name }}</h3>
      <p>病历号: {{ currentUser.hospitalNumber }}</p>
    </div>

    <!-- 问题展示区域 -->
    <div v-if="!submitted && questions.length > 0">
      <h2>AD8早期痴呆筛查量表</h2>

      <div v-for="(question, index) in questions" :key="question.id" class="question-item">
        <h3>{{ index + 1 }}. {{ question.text }}</h3>

        <div class="options">
          <label v-for="(option, optIndex) in question.options" :key="optIndex">
            <input
              type="radio"
              :name="'q' + question.id"
              :value="optIndex"
              v-model="answers[index]"
              @change="saveProgress"
            >
            {{ option }}
          </label>
        </div>
      </div>

      <button @click="submitAnswers" :disabled="!allAnswered" class="submit-btn">
        提交测试
      </button>
    </div>

    <!-- 加载状态 -->
    <div v-if="loading" class="loading">
      <p>加载中...</p>
    </div>

    <!-- 提交成功提示 -->
    <div v-if="submitted" class="result">
      <h3>测试提交成功！</h3>
      <p>您的测试ID: {{ responseId }}</p>
      <button @click="resetTest" class="reset-btn">返回首页</button>
    </div>

    <!-- 错误提示 -->
    <div v-if="error" class="error">
      <p>{{ error }}</p>
      <button @click="retry" class="retry-btn">重试</button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useRouter } from 'vue-router';

const router = useRouter();

// 从本地存储获取当前登录用户信息
const currentUser = ref(JSON.parse(localStorage.getItem('currentUser')) || null);
console.log('currentUser:', currentUser.value);

// 状态管理
const questions = ref([]);
const answers = ref(JSON.parse(localStorage.getItem('ad8Answers')) || Array(8).fill(null));
const loading = ref(false);
const error = ref(null);
const submitted = ref(false);
const responseId = ref('');

// 计算属性：检查是否所有问题都已回答
const allAnswered = computed(() => {
  return answers.value.every(answer => answer !== null);
});

// 获取问题列表
const fetchQuestions = async () => {
  try {
    loading.value = true;
    error.value = null;

    const response = await fetch('http://localhost:8080/questions');
    const data = await response.json();

    if (data.success) {
      questions.value = data.data;
    } else {
      throw new Error(data.message || '获取问题失败');
    }
  } catch (err) {
    error.value = err.message;
    console.error('获取问题失败:', err);
  } finally {
    loading.value = false;
  }
};

// 保存进度到本地存储
const saveProgress = () => {
  localStorage.setItem('ad8Answers', JSON.stringify(answers.value));
};

// 提交答案
const submitAnswers = async () => {
  try {
    loading.value = true;
    error.value = null;

    // 准备提交数据
    const payload = {
      hospitalInfo: {
        hospitalId: currentUser.value.hospitalNumber, // 使用病历号作为医院ID
        patientName: currentUser.value.name
      },
      answers: questions.value.map((q, index) => ({
        question_id: index + 1, // 问题ID从1开始
        selected_option: answers.value[index]
      }))
    };

    // 验证answers长度是否为8
    if (payload.answers.length !== 8) {
      throw new Error('必须回答全部8个问题');
    }

    console.log('提交数据:', payload); // 调试用

    const response = await fetch('http://localhost:8080/responses', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(payload)
    });

    const data = await response.json();
    if (data.success) {
      submitted.value = true;
      responseId.value = data.response_id;
      // 清除本地存储的进度
      localStorage.removeItem('ad8Answers');
    } else {
      throw new Error(data.message || '提交失败');
    }
  } catch (err) {
    error.value = err.message;
    console.error('提交失败:', err);
  } finally {
    loading.value = false;
  }
};

// 重置测试
const resetTest = () => {
  router.push('/'); // 假设使用vue-router，跳转回首页
};

// 重试操作
const retry = () => {
  error.value = null;
  if (questions.value.length === 0) {
    fetchQuestions();
  }
};

// 组件挂载时获取问题
onMounted(() => {
  if (!currentUser.value) {
    router.push('/login'); // 如果未登录，跳转到登录页
    return;
  }
  fetchQuestions();
});
</script>

<style scoped>
.ad8-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
  font-family: 'Microsoft YaHei', sans-serif;
}

.user-info {
  background: #f0f8ff;
  padding: 15px;
  border-radius: 8px;
  margin-bottom: 20px;
}

.user-info h3 {
  margin: 0 0 5px 0;
  color: #2c3e50;
}

h2 {
  color: #2c3e50;
  text-align: center;
  margin-bottom: 30px;
  border-bottom: 2px solid #3498db;
  padding-bottom: 10px;
}

.question-item {
  margin-bottom: 25px;
  padding: 15px;
  background: #f8f9fa;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.question-item h3 {
  margin-top: 0;
  color: #34495e;
}

.options {
  margin-top: 15px;
}

.options label {
  display: block;
  margin: 8px 0;
  padding: 10px;
  background: #fff;
  border-radius: 4px;
  cursor: pointer;
  transition: background 0.2s;
  border: 1px solid #ddd;
}

.options label:hover {
  background: #e9ecef;
}

.options input[type="radio"] {
  margin-right: 10px;
}

.submit-btn, .reset-btn, .retry-btn {
  display: block;
  width: 200px;
  margin: 30px auto;
  padding: 12px;
  background: #3498db;
  color: white;
  border: none;
  border-radius: 4px;
  font-size: 16px;
  cursor: pointer;
  transition: background 0.3s;
}

.submit-btn:hover, .reset-btn:hover, .retry-btn:hover {
  background: #2980b9;
}

.submit-btn:disabled {
  background: #95a5a6;
  cursor: not-allowed;
}

.loading, .error, .result {
  text-align: center;
  padding: 30px;
}

.error {
  color: #e74c3c;
}

.result {
  color: #27ae60;
}

.result h3 {
  margin-top: 0;
}
</style>
