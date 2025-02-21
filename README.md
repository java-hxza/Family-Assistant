# Family Assistant (家庭助手)

[English](#english) | [中文](#中文)

## English

### Introduction
Family Assistant is a modern web application for family financial management, helping users track and analyze their income and expenses with intuitive visualizations and detailed statistics.

### Features
- 🔐 User Authentication
- 📊 Financial Dashboard
  - Monthly overview with income/expense/balance
  - Income & expense trends
  - Category-based analysis charts
- 💰 Transaction Management
  - Income & expense records
  - Category management
  - Transaction history
- 📈 Statistical Analysis
  - Monthly/Yearly statistics
  - Category distribution
  - Trend analysis
- 📱 Responsive Design
  - Mobile-friendly interface
  - Adaptive charts
  - Cross-platform support

### Tech Stack
#### Frontend
- Vue 3 (Composition API)
- Element Plus UI
- ECharts
- Vue Router
- Axios
- Vite

#### Backend
- Spring Boot
- MySQL
- MyBatis-Plus
- Spring Security
- JWT Authentication

### Quick Start

#### Prerequisites
```bash
Node.js >= 16
JDK >= 17
MySQL >= 8.0
```

#### Frontend Setup
```bash
# Clone repository
git clone https://github.com/yourusername/family-assistant.git

# Enter project directory
cd family-assistant

# Install dependencies
npm install

# Start development server
npm run dev
```

#### Backend Setup
```bash
# Enter backend directory
cd family-assistant/backend

# Build project
mvn clean package

# Run application
java -jar target/family-assistant.jar
```

---

## 中文

### 项目介绍
家庭助手是一个现代化的家庭财务管理应用，通过直观的可视化和详细的统计功能，帮助用户追踪和分析收支情况。

### 功能特点
- 🔐 用户认证
    - 登录/注册
    - JWT token 认证
    - 用户信息管理
- 📊 财务仪表板
    - 月度收支概览
    - 收支趋势图表
    - 分类统计分析
- 💰 交易管理
    - 收入支出记录
    - 分类管理
    - 交易历史
- 📈 统计分析
    - 月度/年度统计
    - 分类分布
    - 趋势分析

### 项目结构
```
family-assistant/
├── src/
│   ├── api/                 # API 接口
│   │   ├── auth.js         # 认证接口
│   │   └── statistics.js   # 统计接口
│   ├── components/         # 组件
│   │   ├── Statistics.vue  # 统计组件
│   │   └── IncomeList.vue  # 收入列表
│   ├── router/             # 路由配置
│   │   └── index.js       
│   ├── views/              # 页面
│   │   ├── DashboardPage.vue
│   │   └── ExpensePage.vue
│   └── main.js            # 入口文件
├── backend/                # 后端项目
│   ├── src/main/
│   │   ├── java/          # Java 源码
│   │   └── resources/     # 配置文件
│   └── pom.xml
└── README.md
```

### 主要功能模块

#### 1. 仪表板
- 月度收支总览
- 收支趋势图表
- 分类统计分析
- 最近交易记录

#### 2. 收支管理
- 收入记录管理
- 支出记录管理
- 分类标签管理
- 交易历史查询

#### 3. 统计分析
- 月度统计报表
- 年度对比分析
- 分类占比分析
- 趋势图表展示

### 开发环境配置
1. 前端配置
```bash
# 安装依赖
npm install

# 启动开发服务器
npm run dev

# 构建生产版本
npm run build
```

2. 后端配置
```yaml
# application.yml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/family_assistant
    username: your_username
    password: your_password
```

### 开发计划
- [x] 用户认证系统
- [x] 基础收支管理
- [x] 统计分析功能
- [ ] 预算管理功能
- [ ] 多用户家庭组
- [ ] 账单提醒功能
- [ ] 数据导出功能

### 贡献指南
1. Fork 项目
2. 创建特性分支
3. 提交改动
4. 推送到分支
5. 创建 Pull Request

### 许可证
MIT License

### 联系方式
- Issue: https://github.com/java-hxza/family-assistant/issues
- Email: hxzwye@gmail.com