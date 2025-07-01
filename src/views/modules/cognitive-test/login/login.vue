<!--
 * 文件: login.vue - 认知测试登录页面组件
 * 描述: 提供认知测试前的用户信息收集和验证
 * 功能:
 *   - 收集用户基本信息(姓名、性别、年龄等)
 *   - 验证用户信息并跳转到相应测试
 *   - 处理客户中心登录流程
 * 作者: 项目小组1组
 * 版本: 1.0.0
 * 版权: 2025 东莞佰和生物科技有限公司 保留所有权利
 * 许可证: MIT
 * 创建时间: 2025-06-20
 * 最后修改: 2025-06-29
 * 路由参数:
 *   - testType: 测试类型(mini-cog/casi/blessed)
 *   - redirect: 重定向目标(如customer-center)
 * 数据:
 *   - testTitles: 测试类型对应的标题
 *   - formData: 用户表单数据
 * 注意事项:
 *   - 使用POST请求验证用户信息
 *   - 成功后将用户信息存储到localStorage
 *   - 根据testType或redirect参数跳转
 * 样式特点:
 *   - 蓝天白云渐变背景
 *   - 半透明表单容器
 *   - 响应式按钮效果
 -->

<template>
  <div class="assessment-form-page">
    <!--
     * 顶部标题栏
     * 包含:
     *   - 返回按钮(左箭头图标)
     *   - 动态标题(根据testType显示)
     * 样式:
     *   - 半透明深蓝色背景
     *   - 白色文字
     *   - 底部阴影
     *   - 弹性布局
     -->
    <div class="header">
      <button class="back-button" @click="goBack">
        <span class="back-icon">←</span>
      </button>
      <h1 class="title">{{ testTitles[testType] }}</h1>
    </div>

    <!--
     * 主体内容区域
     * 布局: 弹性布局(垂直方向)
     * 包含:
     *   - 用户信息表单
     *   - 开始测试按钮
     * 样式:
     *   - 内边距: 20px
     *   - 半透明白色表单背景
     *   - 圆角:12px
     *   - 阴影效果
     -->
    <div class="content">
      <div class="form-container">
        <div class="form-group">
          <label>姓名</label>
          <input type="text" v-model="formData.name" placeholder="请输入姓名">
        </div>

        <div class="form-group">
          <label>性别</label>
          <input type="text" v-model="formData.gender" placeholder="请输入性别">
        </div>

        <div class="form-group">
          <label>年龄</label>
          <input type="number" v-model="formData.age" placeholder="请输入年龄">
        </div>

        <div class="form-group">
          <label>住院号</label>
          <input type="text" v-model="formData.hospitalNumber" placeholder="请输入住院号">
        </div>

        <div class="form-group">
          <label>评定日期</label>
          <input type="date" v-model="formData.assessmentDate">
        </div>
      </div>

      <button class="start-button" @click="startAssessment">
        开始测试
      </button>
    </div>
  </div>
</template>

<script>
export default {
  name: 'AssessmentFormPage',
  data() {
    return {
      // 当前测试类型(从路由参数获取)
      testType: '',
      // 测试类型对应的标题映射
      testTitles: {
        'mini-cog': '简易智力状态评估表(MINI-COG)',
        'casi': '早期痴呆简易筛选量表（AD8）',
        'blessed': '匹兹堡睡眠质量指数量表（PSQI）',
        'customer-center': '客户中心'
      },
      // 用户表单数据
      formData: {
        name: '',       // 姓名
        gender: '',     // 性别
        age: '',        // 年龄
        hospitalNumber: '',  // 住院号
        assessmentDate: ''  // 评定日期
      }
    }
  },
    created() {
      // 初始化时设置测试类型:
      // 1. 优先检查redirect参数(用于客户中心)
      // 2. 其次检查testType参数
      // 3. 默认使用mini-cog
      this.testType = this.$route.query.redirect === 'customer-center'
        ? 'customer-center'
        : this.$route.query.testType || 'mini-cog';
    },
  methods: {
    /**
     * 返回上一页
     */
    goBack() {
      this.$router.go(-1);
    },
    /**
     * 开始测试流程
     * @async
     * @description
     *   1. 提交用户信息到服务器验证
     *   2. 成功后将用户信息存储到localStorage
     *   3. 根据testType或redirect参数跳转
     * 跳转逻辑:
     *   - 优先使用redirect参数跳转
     *   - casi测试: 跳转/ad8-test
     *   - blessed测试: 跳转/psqi
     *   - 其他测试: 跳转/text
     * @throws {Error} 网络请求失败时抛出错误
     */
    async startAssessment() {
      try {
        const response = await fetch('http://localhost:8080/api/login', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json'
          },
          body: JSON.stringify(this.formData)
        });

        if (response.ok) {
          const data = await response.json();
          if (data.success) {
            console.log('登录用户信息:', this.formData);
            console.log('服务器返回数据:', data);
            // 存储用户信息到localStorage
            localStorage.setItem('currentUser', JSON.stringify(this.formData));
            // 登录成功，优先根据redirect参数跳转
            if (this.$route.query.redirect) {
              this.$router.push({ name: this.$route.query.redirect });
            } else {
              // 否则根据测试类型跳转
              let targetRoute;
              if (this.testType === 'casi') {
                targetRoute = '/ad8-test';
              } else if (this.testType === 'blessed') {
                targetRoute = '/psqi';
              } else {
                targetRoute = '/text';
              }
              this.$router.push(targetRoute);
            }
          } else {
            console.log('验证失败的用户信息:', this.formData);
            console.log('服务器返回错误:', data);
            alert('用户验证失败: ' + data.message);
          }
        } else {
          throw new Error('网络响应错误');
        }
      } catch (error) {
        console.error('登录请求失败:', error);
        alert('登录请求失败，请稍后重试');
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
 * 背景: 蓝天白云渐变(#87CEEB到#E0F7FA)
 * 字体: 系统默认中文字体
 */
.assessment-form-page {
  font-family: 'PingFang SC', 'Microsoft YaHei', sans-serif;
  height: 100vh;
  display: flex;
  flex-direction: column;
  background: linear-gradient(to bottom, #87CEEB, #E0F7FA);
}

.header {
  background-color: rgba(30, 136, 229, 0.8); /* 半透明深蓝色 */
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
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.form-container {
  background-color: rgba(255, 255, 255, 0.8); /* 半透明白色背景 */
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  font-weight: 500;
  color: #333;
}

.form-group input[type="text"],
.form-group input[type="number"],
.form-group input[type="date"] {
  width: 95%;
  padding: 12px;
  border: 1px solid #ddd;
  border-radius: 8px;
  font-size: 16px;
}

.radio-group {
  display: flex;
  gap: 20px;
  margin-top: 8px;
}

.radio-group label {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: normal;
}

/*
 * 开始测试按钮样式
 * 基础样式:
 *   - 紫色背景(#9c27b0)
 *   - 白色文字
 *   - 圆角:8px
 *   - 内边距:16px
 *   - 宽度:100%
 * 交互效果:
 *   - 悬停: 上移2px,阴影加深
 *   - 点击: 恢复原位
 *   - 过渡动画: 0.2秒
 * 状态:
 *   - 默认: 中等阴影
 *   - 悬停: 更深阴影
 */
.start-button {
  background-color: #9c27b0;
  color: white;
  border: none;
  border-radius: 8px;
  padding: 16px;
  font-size: 18px;
  font-weight: bold;
  text-align: center;
  cursor: pointer;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
  transition: transform 0.2s, box-shadow 0.2s;
  width: 100%;
  margin-top: 20px;
}

.start-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 12px rgba(0, 0, 0, 0.2);
}

.start-button:active {
  transform: translateY(0);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
}


</style>
