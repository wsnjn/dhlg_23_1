<!--
 * 文件: baihe.vue - 应用主页组件
 * 描述: 佰和护理中心主界面，提供系统功能入口
 * 功能:
 *   - 展示护理中心主界面
 *   - 提供5个主要功能入口按钮
 *   - 处理导航逻辑
 * 作者: 项目小组1组
 * 版本: 1.0.0
 * 版权: 2025 东莞佰和生物科技有限公司 保留所有权利
 * 许可证: MIT
 * 创建时间: 2025-06-20
 * 最后修改: 2025-06-29
 * 路由路径: / (根路径)
 * 依赖组件: 无
 * 注意事项:
 *   - 客户中心按钮会先跳转到登录页
 *   - 按钮布局使用CSS Grid实现
 *   - 按钮样式包含悬停效果
 -->

<template>
  <div class="care-center-container">
    <!--
     * 主标题
     * 样式类: title
     * 位置: 顶部居中
     * 字体大小: 24px
     * 边距: 上30px 下50px
     -->
    <h1 class="title">佰和护理中心</h1>

    <!--
     * 功能按钮区域
     * 布局方式: CSS Grid (3列3行)
     * 间距: 15px
     * 高度: 视口高度的70%
     * 包含5个功能按钮:
     *   1. 客户中心
     *   2. 量表测试
     *   3. AI辅助数据分析
     *   4. 情感聊天
     *   5. 休闲娱乐
     -->
    <div class="button-grid">
      <div class="button customer-center" @click="navigateTo('customer-center')">
        <span>客户中心</span>
      </div>

      <div class="button scale-test" @click="navigateTo('scale-test')">
        <span>量表测试</span>
      </div>

      <div class="button ai-analysis" @click="navigateTo('ai-analysis')">
        <span>AI辅助数据分析</span>
      </div>

      <div class="button emotion-chat" @click="navigateTo('emotion-chat')">
        <span>情感聊天</span>
      </div>

      <div class="button entertainment" @click="navigateTo('entertainment')">
        <span>休闲娱乐</span>
      </div>

      <div class="button admin" @click="navigateTo('admin')">
        <span>管理端</span>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'CareCenter',
  methods: {
    /**
     * 导航到指定功能页面
     * @param {string} route - 目标路由名称
     * @description
     *   - 对于customer-center路由会先跳转到登录页
     *   - 其他路由直接导航
     * 路由跳转逻辑:
     *   1. 检查是否是客户中心路由
     *   2. 如果是，跳转登录页并携带redirect参数
     *   3. 否则直接跳转到目标路由
     */
    navigateTo(route) {
      if (route === 'customer-center') {
        this.$router.push({
          name: 'login',
          query: { redirect: 'customer-center' },
        })
      } else {
        this.$router.push({ name: route })
      }
    },
  },
}
</script>

<style scoped>
/*
 * 容器样式
 * 作用域: 仅当前组件
 * 布局: 弹性布局(垂直方向)
 * 背景: 浅灰色(#f5f5f5)
 * 最小高度: 100vh (填满整个视口)
 */
.care-center-container {
  font-family: 'PingFang SC', 'Microsoft YaHei', sans-serif;
  background-color: #f5f5f5;
  min-height: 100vh;
  padding: 20px;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.title {
  color: #000;
  font-size: 24px;
  margin: 30px 0 50px;
  text-align: center;
}

/*
 * 按钮网格布局
 * 布局方式: CSS Grid (3列3行)
 * 间距: 15px
 * 宽度: 100%
 * 高度: 视口高度的70%
 * 内边距: 10px
 * 盒子模型: border-box
 */
.button-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  grid-template-rows: repeat(3, 1fr);
  gap: 15px;
  width: 100%;
  max-width: 100%;
  height: 70vh;
  padding: 10px;
  box-sizing: border-box;
}

/*
 * 基础按钮样式
 * 布局: 弹性布局(居中)
 * 圆角: 12px
 * 文字: 白色、18px、加粗
 * 交互:
 *   - 鼠标指针变为手形
 *   - 悬停时上移5px
 *   - 过渡动画: 0.2秒
 * 阴影: 轻微底部阴影
 */
.button {
  display: flex;
  justify-content: center;
  align-items: center;
  border-radius: 12px;
  color: white;
  font-size: 18px;
  font-weight: bold;
  cursor: pointer;
  transition: transform 0.2s;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.button:hover {
  transform: translateY(-5px);
}

.customer-center {
  background-color: #ff4757;
  grid-column: 1;
  grid-row: 1;
}

.scale-test {
  background-color: #2ed573;
  grid-column: 3;
  grid-row: 1;
}

.ai-analysis {
  background-color: #1e90ff;
  grid-column: 2;
  grid-row: 2;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  text-align: center;
  padding: 10px;
}

.ai-analysis span {
  display: block;
  margin-bottom: 5px;
}

.admin {
  background-color: #9c27b0;
  grid-column: 1;
  grid-row: 3;
}

.emotion-chat {
  background-color: #9c88ff;
  grid-column: 1;
  grid-row: 3;
}

.entertainment {
  background-color: #ff7f50;
  grid-column: 3;
  grid-row: 3;
}
</style>
