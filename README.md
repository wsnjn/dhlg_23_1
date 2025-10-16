# 佰和护理中心 - 老年认知与睡眠健康数字评估系统

## 项目概述

佰和护理中心是一个专为老年人设计的认知与睡眠健康数字评估系统，提供全面的健康评估、数据分析和娱乐功能。该系统采用现代化的Web技术栈，为医护人员和患者提供便捷的数字化服务。

### 项目信息
- **项目名称**: 佰和护理中心
- **开发团队**: 项目小组1组
- **公司**: 东莞佰和生物科技有限公司
- **版本**: 1.0.0
- **创建时间**: 2025-01-27
- **技术栈**: Vue 3 + Node.js + MySQL

## 系统架构

### 前端架构 (Vue.js)
```
baiheapp/
├── src/
│   ├── App.vue                 # 应用根组件
│   ├── main.js                 # 应用入口
│   ├── router/index.js         # 路由配置
│   ├── stores/                 # 状态管理
│   │   ├── user.js            # 用户状态
│   │   └── counter.js         # 计数器状态
│   ├── views/                  # 页面组件
│   │   ├── baihe.vue          # 主页面
│   │   ├── LoginPage.vue      # 登录页面
│   │   └── modules/           # 功能模块
│   │       ├── AdminDashboard.vue    # 管理端仪表板
│   │       ├── CustomerCenter.vue      # 客户中心
│   │       ├── ScaleTest.vue          # 量表测试
│   │       ├── AIAnalysis.vue         # AI分析
│   │       ├── EmotionChat.vue        # 情感聊天
│   │       ├── Entertainment.vue     # 娱乐功能
│   │       ├── cognitive-test/        # 认知测试模块
│   │       │   ├── CognitiveAssessment.vue
│   │       │   └── login/
│   │       │       ├── Ad8/AD8Test.vue
│   │       │       ├── PSQI/PAQIText.vue
│   │       │       └── text/          # 文本测试
│   │       ├── dementia-test/         # 痴呆测试
│   │       ├── games/                 # 游戏模块
│   │       │   ├── MemoryGame.vue
│   │       │   ├── PuzzleGame.vue
│   │       │   └── QuizGame.vue
│   │       └── admin/                 # 管理功能
│   │           ├── DataAnalytics.vue
│   │           ├── PatientManagement.vue
│   │           ├── StatisticalReports.vue
│   │           └── TestRecords.vue
│   └── components/
│       └── ThreeModelViewer.vue      # 3D模型查看器
```

### 后端架构 (Node.js)
```
houduan/
├── server.js                 # 服务器主文件
├── package.json              # 依赖配置
└── md/                       # 文档目录
    ├── baiheapp.md
    └── baiheapp(1).md
```

## 核心功能模块

### 1. 主页面 (baihe.vue)
- **功能**: 系统入口，提供5个主要功能模块的导航
- **布局**: 3x3网格布局，响应式设计
- **功能按钮**:
  - 客户中心 (红色)
  - 量表测试 (绿色)
  - AI辅助数据分析 (蓝色)
  - 情感聊天 (紫色)
  - 管理端 (紫色)

### 2. 认知测试模块

#### AD8测试 (AD8Test.vue)
- **功能**: AD8早期痴呆筛查量表测试
- **特点**:
  - 8个标准化问题
  - 实时进度保存
  - 用户身份验证
  - 测试结果提交
- **技术实现**:
  - 本地存储进度
  - RESTful API交互
  - 响应式UI设计

#### PSQI测试 (PAQIText.vue)
- **功能**: 匹兹堡睡眠质量指数测试
- **特点**:
  - 复杂的问题结构（主问题+子问题）
  - 时间输入和选项选择
  - 特殊处理"其他"选项
  - 完整的答案验证

### 3. 管理端系统 (AdminDashboard.vue)

#### 数据概览
- **统计卡片**: 总病人数、测试总数、本月新增、活跃病人
- **可视化图表**:
  - 病人增长趋势图 (折线图)
  - 测试类型分布图 (饼图)
  - 月度测试统计图 (柱状图)

#### 功能模块
1. **病人管理** (PatientManagement.vue)
   - 病人列表展示
   - 搜索和筛选功能
   - 病人详情管理

2. **测试记录** (TestRecords.vue)
   - 测试数据统计
   - 记录列表管理
   - 高级筛选功能

3. **数据分析** (DataAnalytics.vue)
   - 关键指标分析
   - 趋势分析图表
   - 数据洞察报告

4. **统计报告** (StatisticalReports.vue)
   - 快速报告生成
   - 多格式导出
   - 报告模板管理

### 4. 娱乐功能模块

#### 记忆游戏 (MemoryGame.vue)
- **游戏规则**: 翻牌配对游戏
- **功能特点**:
  - 12张卡片配对
  - 60秒倒计时
  - 得分系统
  - 游戏状态管理

#### 其他游戏
- **拼图游戏** (PuzzleGame.vue)
- **问答游戏** (QuizGame.vue)

### 5. 用户认证系统
- **登录页面** (LoginPage.vue)
- **用户状态管理** (stores/user.js)
- **路由守卫**: 管理端访问控制

## 技术栈详情

### 前端技术
- **框架**: Vue 3 (Composition API)
- **构建工具**: Vite 7.1.2
- **UI组件库**: Element Plus 2.10.7
- **图表库**: ECharts 6.0.0 + vue-echarts 7.0.3
- **路由**: Vue Router 4.5.1
- **状态管理**: Pinia 3.0.3
- **3D渲染**: Three.js 0.179.1
- **HTTP客户端**: Axios 1.11.0
- **代码规范**: ESLint + Prettier

### 后端技术
- **运行环境**: Node.js
- **框架**: Express 5.1.0
- **数据库**: MySQL 2 (mysql2 3.14.1)
- **中间件**:
  - CORS 2.8.5 (跨域处理)
  - Body-parser 2.2.0 (请求体解析)
  - Multer 2.0.1 (文件上传)
- **HTTP客户端**: node-fetch 3.3.2

### 数据库配置
```javascript
const dbConfig = {
  host: '8.138.36.103',        // 宝塔服务器公网IP
  port: 3306,                  // MySQL端口
  user: 'group1',              // 数据库用户
  password: 'TWaD4DGHHF23s5fa', // 数据库密码
  database: 'group1'          // 数据库名
}
```

## API接口设计

### 用户相关接口
- `GET /api/users` - 获取所有用户
- `POST /api/login` - 用户登录

### 测试相关接口
- `GET /api/ad8-responses` - 获取AD8测试回答数据
- `GET /api/psqi-questions` - 获取PSQI问题数据
- `POST /api/responses` - 提交AD8测试答案
- `POST /api/psqi/responses` - 提交PSQI测试答案

### 问题相关接口
- `GET /api/questions` - 获取AD8问题列表
- `GET /api/psqi/questions` - 获取PSQI问题列表

## 项目特色

### 1. 用户体验设计
- **响应式布局**: 适配不同屏幕尺寸
- **现代化UI**: 基于Element Plus的美观界面
- **交互反馈**: 悬停效果、过渡动画
- **进度保存**: 测试过程中自动保存进度

### 2. 数据可视化
- **丰富图表**: 折线图、柱状图、饼图
- **实时更新**: 动态数据展示
- **交互式图表**: ECharts提供的交互功能

### 3. 系统架构
- **模块化设计**: 功能模块独立开发
- **状态管理**: Pinia集中状态管理
- **路由守卫**: 安全的访问控制
- **错误处理**: 完善的错误处理机制

### 4. 数据管理
- **本地存储**: localStorage保存用户状态和进度
- **数据库集成**: MySQL数据持久化
- **API设计**: RESTful风格接口

## 部署说明

### 前端部署
```bash
cd baiheapp
npm install
npm run dev        # 开发环境
npm run build      # 生产构建
npm run preview    # 预览构建结果
```

### 后端部署
```bash
cd houduan
npm install
npm start          # 启动服务器
```

### 访问地址
- **前端**: http://localhost:5173
- **后端**: http://localhost:3001
- **管理端**: http://localhost:5173/#/admin

## 开发规范

### 代码规范
- **ESLint**: 代码质量检查
- **Prettier**: 代码格式化
- **Vue 3**: 使用Composition API
- **TypeScript**: 类型安全（可选）

### 文件命名
- **组件文件**: PascalCase (如: AdminDashboard.vue)
- **工具文件**: camelCase (如: user.js)
- **样式文件**: kebab-case (如: admin-dashboard.css)

### 注释规范
- **文件头注释**: 包含作者、版本、版权信息
- **函数注释**: JSDoc格式
- **代码注释**: 关键逻辑说明

## 项目亮点

### 1. 医疗专业性
- **标准化量表**: AD8、PSQI等专业评估工具
- **数据完整性**: 完整的测试数据收集
- **结果分析**: 专业的健康数据分析

### 2. 技术先进性
- **Vue 3**: 最新前端框架
- **Vite**: 快速构建工具
- **ECharts**: 专业图表库
- **Three.js**: 3D可视化

### 3. 用户体验
- **直观界面**: 简洁易用的操作界面
- **响应式设计**: 适配各种设备
- **游戏化元素**: 增加用户参与度

### 4. 数据安全
- **用户认证**: 完整的登录验证
- **数据加密**: 敏感信息保护
- **权限控制**: 分级访问管理

## 未来发展规划

### 短期目标
1. **数据持久化**: 完善数据库连接
2. **用户权限**: 实现角色权限管理
3. **实时数据**: 添加WebSocket支持
4. **移动端**: 开发移动端应用

### 长期目标
1. **AI分析**: 集成机器学习算法
2. **云端部署**: 迁移到云服务器
3. **多语言**: 支持国际化
4. **API开放**: 提供第三方集成

## 贡献指南

### 开发环境设置
1. 克隆项目仓库
2. 安装依赖: `npm install`
3. 启动开发服务器: `npm run dev`
4. 开始开发

### 提交规范
- **feat**: 新功能
- **fix**: 修复bug
- **docs**: 文档更新
- **style**: 代码格式
- **refactor**: 代码重构
- **test**: 测试相关

## 许可证

本项目采用 MIT 许可证，详情请参阅 LICENSE 文件。

## 联系方式

- **开发团队**: 项目小组1组
- **公司**: 东莞佰和生物科技有限公司
- **版本**: 1.0.0
- **最后更新**: 2025-01-27

---

*本文档详细介绍了佰和护理中心系统的架构、功能和技术实现，为开发者和用户提供全面的项目信息。*

