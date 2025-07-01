<!--
 * 文件: DementiaTest.vue - 痴呆测试页面组件
 * 描述: 提供痴呆症认知功能评估测试入口
 * 功能:
 *   - 显示痴呆测试主界面
 *   - 提供4种评估表入口
 *   - 处理导航逻辑
 * 作者: 项目小组1组
 * 版本: 1.0.0
 * 版权: 2025 东莞佰和生物科技有限公司 保留所有权利
 * 许可证: MIT
 * 创建时间: 2025-06-20
 * 最后修改: 2025-06-29
 * 路由路径: /dementia-test
 * 依赖组件:
 *   - CognitiveAssessment.vue (客观心理评估)
 * 注意事项:
 *   - 客观心理评估按钮会导航到/cognitive-assessment
 *   - 其他按钮当前未实现功能
 * 样式特点:
 *   - 顶部固定标题栏
 *   - 网格布局按钮
 *   - 响应式按钮效果
 -->

<template>
  <div class="cognitive-test-page">
    <!--
     * 顶部标题栏
     * 包含:
     *   - 返回按钮(带文字和图标)
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
        <span>返回</span>
      </button>
      <h1 class="title">认知功能及康复测试</h1>
    </div>

    <!--
     * 主体内容区域
     * 布局: 网格布局(2列)
     * 包含:
     *   - 客观心理评估按钮
     *   - 知情者报告者表按钮
     *   - (半)结构式问卷按钮
     *   - 其他情况评估表按钮
     * 样式:
     *   - 浅灰色背景(#f5f5f5)
     *   - 内边距: 30px 20px
     *   - 按钮间距: 20px
     -->
    <div class="content">
      <div class="button-grid">
        <button class="test-button" @click="navigateToTest('objective')">
          客观心理评估表
        </button>

        <button class="test-button" >
          知情者报告者表
        </button>

        <button class="test-button" >
          (半)结构式问卷
        </button>

        <button class="test-button" >
          其他情况评估表
        </button>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'CognitiveTestPage',
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
     * 导航到指定测试页面
     * @param {string} testType - 测试类型
     * @description
     *   - 如果testType为'objective'则跳转到认知评估页
     *   - 其他类型目前仅打印日志(待实现)
     * 路由逻辑:
     *   1. 检查测试类型
     *   2. 客观评估: 跳转/cognitive-assessment
     *   3. 其他测试: 控制台日志(待开发)
     */
    navigateToTest(testType) {
      switch(testType) {
        case 'objective':
          this.$router.push('/cognitive-assessment');
          break;
        default:
          console.log(`Navigate to ${testType} test`);
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
 * 字体: 系统默认中文字体
 */
.cognitive-test-page {
  font-family: 'PingFang SC', 'Microsoft YaHei', sans-serif;
  height: 100vh;
  display: flex;
  flex-direction: column;
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
  margin-right: 5px;
  font-size: 18px;
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
  background-color: #f5f5f5; /* 浅灰色背景 */
  padding: 30px 20px;
}

.button-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
}

/*
 * 测试按钮样式
 * 基础样式:
 *   - 白色背景
 *   - 黑色文字
 *   - 圆角:12px
 *   - 内边距:20px 10px
 * 交互效果:
 *   - 悬停: 上移2px,阴影加深
 *   - 点击: 恢复原位
 *   - 过渡动画: 0.2秒
 * 状态:
 *   - 默认: 轻微阴影
 *   - 悬停: 阴影加深
 */
.test-button {
  background-color: white;
  color: black;
  border: none;
  border-radius: 12px;
  padding: 20px 10px;
  font-size: 16px;
  text-align: center;
  cursor: pointer;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
  transition: transform 0.2s, box-shadow 0.2s;
}

.test-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.15);
}

.test-button:active {
  transform: translateY(0);
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
}
</style>
