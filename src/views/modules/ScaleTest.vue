<!--
 * 文件: ScaleTest.vue - 量表测试页面组件
 * 描述: 提供认知功能评估量表测试入口
 * 功能:
 *   - 显示量表测试主界面
 *   - 提供两种测试类型入口
 *   - 处理导航逻辑
 * 作者: 项目小组1组
 * 版本: 1.0.0
 * 版权: 2025 东莞佰和生物科技有限公司 保留所有权利
 * 许可证: MIT
 * 创建时间: 2025-06-20
 * 最后修改: 2025-06-29
 * 路由路径: /scale-test
 * 依赖组件:
 *   - DementiaTest.vue (痴呆测试)
 * 注意事项:
 *   - 痴呆测试按钮会导航到/dementia-test
 *   - 评定表按钮当前仅打印日志(待实现)
 * 样式特点:
 *   - 顶部固定标题栏
 *   - 响应式按钮效果
 -->

<template>
  <div class="scale-test-page">
    <!--
     * 顶部标题栏
     * 包含:
     *   - 返回按钮(左箭头图标)
     *   - 页面标题
     * 样式:
     *   - 深蓝色背景(#1e88e5)
     *   - 白色文字
     *   - 底部阴影
     -->
    <div class="header">
      <button class="back-button" @click="goBack">
        <i class="icon-arrow-left"></i>
      </button>
      <h1 class="title">量表测试</h1>
    </div>

    <!--
     * 主体内容区域
     * 布局: 弹性布局(垂直排列)
     * 包含:
     *   - 痴呆及认知障碍测试按钮
     *   - 评定表测试按钮
     * 样式:
     *   - 浅灰色背景(#f5f5f5)
     *   - 内边距: 30px 20px
     *   - 按钮间距: 20px
     -->
    <div class="content">
      <button class="test-button" @click="navigateToTest('dementia')">
        痴呆及认知障碍测试表
      </button>

      <button class="test-button" @click="navigateToTest('evaluation')">
        评定表（试行）
      </button>
    </div>
  </div>
</template>

<script>
export default {
  name: 'ScaleTestPage',
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
     *   - 如果testType为'dementia'则跳转到痴呆测试页
     *   - 其他类型目前仅打印日志(待实现)
     * 路由逻辑:
     *   1. 检查测试类型
     *   2. 痴呆测试: 跳转/dementia-test
     *   3. 其他测试: 控制台日志(待开发)
     */
    navigateToTest(testType) {
      if (testType === 'dementia') {
        this.$router.push('/dementia-test');
      } else {
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
.scale-test-page {
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
  font-size: 20px;
  margin-right: 15px;
  cursor: pointer;
}

.title {
  font-size: 18px;
  font-weight: 500;
  margin: 0;
}

.content {
  flex: 1;
  background-color: #f5f5f5; /* 浅灰色背景 */
  padding: 30px 20px;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

/*
 * 测试按钮样式
 * 基础样式:
 *   - 绿色背景(#4caf50)
 *   - 白色文字
 *   - 圆角:8px
 *   - 内边距:16px
 * 交互效果:
 *   - 悬停: 上移2px,阴影加深
 *   - 点击: 恢复原位
 *   - 过渡动画: 0.2秒
 * 状态:
 *   - 默认: 轻微阴影
 *   - 悬停: 阴影加深
 */
.test-button {
  background-color: #4caf50;
  color: white;
  border: none;
  border-radius: 8px;
  padding: 16px;
  font-size: 16px;
  font-weight: 500;
  text-align: center;
  cursor: pointer;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.2);
  transition: transform 0.2s, box-shadow 0.2s;
}

.test-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
}

.test-button:active {
  transform: translateY(0);
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.2);
}

/* 返回箭头图标样式 - 可以使用字体图标或SVG */
.icon-arrow-left {
  display: inline-block;
  width: 12px;
  height: 12px;
  border-left: 2px solid white;
  border-bottom: 2px solid white;
  transform: rotate(45deg);
}
</style>
