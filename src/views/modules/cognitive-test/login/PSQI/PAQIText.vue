<template>
  <div class="psqi-container">
    <!-- 用户信息显示 -->
    <div class="user-info" v-if="currentUser">
      <h3>当前用户: {{ currentUser.name }}</h3>
      <p>病历号: {{ currentUser.hospitalNumber }}</p>
    </div>

    <!-- 问题展示区域 -->
    <div v-if="!submitted && questions.length > 0">
      <h2>匹兹堡睡眠质量指数(PSQI)测试</h2>

      <div v-for="(question, qIndex) in questions" :key="question.id" class="question-section">
        <h3>{{ qIndex + 1 }}. {{ question.text }}</h3>

        <!-- 时间类型问题 -->
        <div v-if="question && question.type === 'time'" class="time-input">
          <input
            type="time"
            v-model="answers[question.id]"
            required
          >
        </div>

        <!-- 选项类型问题 -->
        <div v-if="question.type === 'option'" class="options">
          <label v-for="(option, optIndex) in question.options" :key="optIndex">
            <input
              type="radio"
              :name="'q' + question.id"
              :value="optIndex"
              v-model="answers[question.id]"
              required
            >
            {{ option }}
          </label>
        </div>

        <!-- 子问题 (排除j问题) -->
        <template v-if="question.subQuestions">
  <template v-for="subQuestion in question.subQuestions" :key="subQuestion.id">
    <div v-if="subQuestion.id !== 15" class="sub-question">
      <p>{{ subQuestion.text }}</p>
      <!-- 子问题时间类型 -->
      <div v-if="subQuestion.type === 'time'" class="time-input">
        <input
          type="time"
          v-model="answers[subQuestion.id]"
          required
        >
      </div>
      <!-- 子问题选项类型 -->
      <div v-if="subQuestion.type === 'option'" class="options">
        <label v-for="(option, optIndex) in subQuestion.options" :key="optIndex">
          <input
            type="radio"
            :name="'sq' + subQuestion.id"
            :value="optIndex"
            v-model="answers[subQuestion.id]"
            required
          >
          {{ option }}
        </label>
      </div>
    </div>

    <!-- 单独渲染 j 问题 -->
    <div v-else class="j-question">
      <h3>{{ subQuestion.text }}</h3>
      <div class="options">
        <label>
          <input
            type="radio"
            :name="'sq' + subQuestion.id"
            value="1"
            v-model="answers[subQuestion.id]"
            required
            @change="handleOtherChoiceChange(subQuestion.id, '1')"
          >
          是
        </label>
        <label>
          <input
            type="radio"
            :name="'sq' + subQuestion.id"
            value="0"
            v-model="answers[subQuestion.id]"
            required
            @change="handleOtherChoiceChange(subQuestion.id, '0')"
          >
          否
        </label>
      </div>
      <div v-if="answers[subQuestion.id] === '1'" class="text-input">
        <textarea
          v-model="answers[subQuestion.id + '_other']"
          placeholder="请具体写明其他影响睡眠的事情"
          rows="3"
          required
          class="other-textarea"
        ></textarea>
      </div>
    </div>
  </template>
</template>

      </div>

      <!-- 单独处理j问题 -->
      <div v-if="question && question.id === 5 && question.subQuestions" class="j-question">
        <h3>{{ question.subQuestions.find(sq => sq.id === 15)?.text || 'j. 有无其他影响睡眠的事情？（若有请具体写明）' }}</h3>
        <div class="options">
          <label>
            <input
              type="radio"
              name="sq15"
              value="1"
              v-model="answers[15]"
              required
              @change="handleOtherChoiceChange(15, '1')"
            >
            是
          </label>
          <label>
            <input
              type="radio"
              name="sq15"
              value="0"
              v-model="answers[15]"
              required
              @change="handleOtherChoiceChange(15, '0')"
            >
            否
          </label>
        </div>
        <div v-if="answers[15] === '1'" class="text-input">
          <textarea
            v-model="answers['15_other']"
            placeholder="请具体写明其他影响睡眠的事情"
            rows="3"
            required
            class="other-textarea"
          ></textarea>
        </div>
      </div>

      <button @click="submitAnswers" class="submit-btn">
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
      <button @click="goBack" class="back-btn">返回</button>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';

export default {
  name: 'PAQIText',
  setup() {
    const router = useRouter();
    const currentUser = ref(JSON.parse(localStorage.getItem('currentUser')) || null);
    const questions = ref([]);
    const answers = ref({});
    const loading = ref(false);
    const submitted = ref(false);
    const responseId = ref('');

    // 获取问题列表
    const fetchQuestions = async () => {
      try {
        loading.value = true;
        const response = await fetch('http://localhost:8080/psqi/questions');
        const data = await response.json();

        if (data.success) {
          console.log('获取到的PSQI问题数据:', data.data);
          questions.value = data.data.map(q => ({
            id: q.id,
            text: q.text,
            type: q.type || 'radio', // 默认为单选类型
            options: q.options || [],
            subQuestions: (q.subQuestions || []).map(sq => ({
              id: sq.id,
              text: sq.text,
              type: sq.type || 'radio', // 默认为单选类型
              options: sq.options || []
            }))
          }));
          console.log('格式化后的PSQI问题:', questions.value);
        } else {
          throw new Error(data.message || '获取问题失败');
        }
      } catch (error) {
        console.error('获取问题失败:', error);
        alert('获取问题失败，请稍后重试');
      } finally {
        loading.value = false;
      }
    };

    // 验证答案是否完整
    const validateAnswers = () => {
      for (const question of questions.value) {
        // 检查主问题
        if (answers.value[question.id] === undefined || answers.value[question.id] === '') {
          return false;
        }

        // 检查子问题
        for (const subQuestion of question.subQuestions || []) {
          if (answers.value[subQuestion.id] === undefined || answers.value[subQuestion.id] === '') {
            return false;
          }
        }
      }
      return true;
    };

    // 提交答案
    const submitAnswers = async () => {
      try {
        // 验证必填项
        if (!validateAnswers()) {
          alert('请完成所有必填问题');
          return;
        }

        loading.value = true;

        // 准备提交数据
        const payload = {
          created_at: new Date().toISOString(),
          hospitalInfo: {
            hospitalId: currentUser.value.hospitalNumber,
            patientName: currentUser.value.name
          },
          answers: Object.entries(answers.value)
            .filter(([, answer_value]) => answer_value !== undefined && answer_value !== '')
            .map(([question_id, answer_value]) => {
            // 处理其他选项的文本输入
            if (question_id.endsWith('_other')) {
              return {
                question_id: parseInt(question_id.split('_')[0]),
                answer_value: `其他: ${String(answer_value)}`
              };
            }
            // 处理问题5的j选项选择
            if (question_id === '15') {
              return {
                question_id: parseInt(question_id),
                answer_value: String(answer_value) === '1' ? '是' : '否'
              };
            }
            return {
              question_id: parseInt(question_id),
              answer_value: String(answer_value) // 确保所有答案为字符串
            };
            })
            // 添加子问题的其他选项数据
            .concat(
              Object.entries(answers.value)
                .filter(([question_id]) => question_id.endsWith('_other') && question_id.includes('sq'))
                .map(([question_id, answer_value]) => ({
                  question_id: parseInt(question_id.split('_')[0]),
                  answer_value: `其他: ${String(answer_value)}`
                }))
            )
            // 确保j问题的"是/否"选项也被提交
            .concat(
              Object.entries(answers.value)
                .filter(([question_id, answer_value]) =>
                  question_id === '15' &&
                  answer_value !== undefined
                )
                .map(([question_id, answer_value]) => ({
                  question_id: parseInt(question_id),
                  answer_value: String(answer_value) === '1' ? '是' : '否'
                }))
            )
        };

        const response = await fetch('http://localhost:8080/psqi/responses', {
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
        } else {
          throw new Error(data.message || '提交失败');
        }
      } catch (error) {
        console.error('提交失败:', error);
        alert('提交失败，请稍后重试');
      } finally {
        loading.value = false;
      }
    };

    const handleOtherChoiceChange = (questionId, choice) => {
      if (choice === '0') {
        answers.value[`${questionId}_other`] = "无";
      } else {
        answers.value[`${questionId}_other`] = "";
      }
      answers.value[questionId] = choice;
    };

    const goBack = () => {
      router.go(-2);
    };

    onMounted(() => {
      if (!currentUser.value) {
        router.push('/login');
        return;
      }
      fetchQuestions().then(() => {
        console.log('All questions loaded:', questions.value);
        questions.value.forEach((q, i) => {
          console.log(`Question ${i+1}:`, q);
          if(q.subQuestions) {
            console.log(`Subquestions for Q${i+1}:`, q.subQuestions);
            // 特别输出问题5的子问题详情
            if(q.id === 5) {
              console.log('Detailed subquestions for Q5:');
              q.subQuestions.forEach((sq, j) => {
                console.log(`  Subquestion ${j+1}:`, {
                  id: sq.id,
                  text: sq.text,
                  type: sq.type,
                  options: sq.options,
                  isJQuestion: sq.id === 15
                });
                // 特别输出j问题的完整结构
                if(sq.id === 15) {
                  console.log('  J Question Details:', JSON.parse(JSON.stringify(sq)));
                }
              });
            }
          }
        });
      });
    });

    return {
      currentUser,
      questions,
      answers,
      loading,
      submitted,
      responseId,
      submitAnswers,
      goBack,
      handleOtherChoiceChange
    };
  }
};
</script>

<style scoped>
.psqi-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.user-info {
  background: #f0f8ff;
  padding: 15px;
  border-radius: 8px;
  margin-bottom: 20px;
}

.question-section {
  margin-bottom: 30px;
  padding: 15px;
  background: #f8f9fa;
  border-radius: 8px;
}

.sub-question {
  margin-left: 20px;
  margin-top: 15px;
}

.options label {
  display: block;
  margin: 8px 0;
  padding: 10px;
  background: #fff;
  border-radius: 4px;
  cursor: pointer;
}

.text-input input,
.other-textarea {
  padding: 8px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 16px;
  width: 100%;
  margin-top: 8px;
}

.other-textarea {
  min-height: 80px;
  resize: vertical;
}

.submit-btn {
  padding: 12px 24px;
  background: #4CAF50;
  color: white;
  border: none;
  border-radius: 4px;
  font-size: 16px;
  cursor: pointer;
  margin-top: 20px;
}

.loading, .result {
  text-align: center;
  padding: 30px;
}

.j-question {
  margin: 20px 0;
  padding: 15px;
  background: #f8f9fa;
  border-radius: 8px;
}

.j-question h3 {
  margin-bottom: 15px;
  font-size: 16px;
  color: #333;
}
</style>
