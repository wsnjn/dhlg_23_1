<!--
 * 文件: CognitiveAssessment.vue - 认知评估页面组件
 * 描述: 提供多种认知功能评估量表入口
 * 功能:
 *   - 显示认知评估量表列表
 *   - 处理导航到具体评估测试的逻辑
 * 作者: 项目小组1组
 * 版本: 1.0.0
 * 版权: 2025 东莞佰和生物科技有限公司 保留所有权利
 * 许可证: MIT
 * 创建时间: 2025-06-20
 * 最后修改: 2025-06-29
 * 路由路径: /cognitive-assessment
 * 依赖组件:
 *   - login.vue (用于需要登录的测试)
 * 数据:
 *   - assessments: 包含6种评估量表信息
 * 注意事项:
 *   - mini-cog/casi/blessed测试会跳转到登录页
 *   - 其他测试当前仅打印日志(待实现)
 * 样式特点:
 *   - 浅蓝色背景
 *   - 垂直列表布局
 *   - 响应式按钮效果
 -->

<template>
  <div class="psychological-assessment-page">
    <!--
     * 顶部标题栏
     * 包含:
     *   - 返回按钮(左箭头图标)
     *   - 页面标题
     * 样式:
     *   - 深蓝色背景(#1e88e5)
     *   - 白色文字
     *   - 底部阴影
     *   - 弹性布局
     -->
    <div class="header">
      <button class="back-button" @click="goBack">
        <span class="back-icon">←</span>
      </button>
      <h1 class="title">心理评估量表</h1>
    </div>

    <!--
     * 主体内容区域
     * 布局: 垂直列表
     * 包含:
     *   - 6种认知评估量表按钮
     * 样式:
     *   - 浅蓝色背景(#e6f2ff)
     *   - 内边距: 20px
     *   - 可滚动内容
     *   - 按钮间距: 15px
     -->
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
      // 评估量表列表数据
      // 每个量表包含:
      //   - id: 唯一标识符
      //   - name: 显示名称
      assessments: [
        { id: 'mini-cog', name: '简易智力状态评估表(MINI-COG)' }, // 需要登录
        { id: 'casi', name: '早期痴呆简易筛选量表（AD8）' },      // 需要登录
        { id: 'blessed', name: '匹兹堡睡眠质量指数量表（PSQI）' }, // 需要登录
        { id: 'moca', name: '认知功能障碍评定表(MoCA)' },        // 待实现
        { id: 'hds', name: '老年痴呆检查量表(HDS)' },            // 待实现
        { id: 'mmse', name: '简易智力精神状态检查量表(MMSE)' }    // 待实现
      ]
    }
  },
  methods: {
    /**
     * 返回上一页
     */
    /**
     * 返回上一页
     * @description 使用vue-router的go(-1)方法返回历史记录中的上一页
     */
    goBack() {
      this.$router.go(-1);
    },
    /**
     * 导航到指定评估测试
     * @param {string} assessmentId - 评估测试ID
     * @description
     *   - mini-cog/casi/blessed测试: 跳转登录页并携带testType参数
     *   - 其他测试: 控制台日志(待实现)
     * 路由逻辑:
     *   1. 检查评估ID
     *   2. 需要登录的测试: 跳转/login并传递testType
     *   3. 其他测试: 控制台日志(待开发)
     */
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
/*
 * 页面容器样式
 * 作用域: 仅当前组件
 * 布局: 弹性布局(垂直方向)
 * 高度: 100vh (填满视口)
 * 背景: 浅蓝色(#e6f2ff)
 * 字体: 系统默认中文字体
 */
.psychological-assessment-page {
  font-family: 'PingFang SC', 'Microsoft YaHei', sans-serif;
  height: 100vh;
  display: flex;
  flex-direction: column;
  background-color: #e6f2ff;
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

/*
 * 评估按钮样式
 * 基础样式:
 *   - 白色背景
 *   - 深灰色文字(#333)
 *   - 圆角:12px
 *   - 内边距:16px
 *   - 宽度:100%
 * 交互效果:
 *   - 悬停: 上移2px,阴影加深
 *   - 点击: 恢复原位
 *   - 过渡动画: 0.2秒
 * 状态:
 *   - 默认: 轻微阴影
 *   - 悬停: 阴影加深
 */
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
