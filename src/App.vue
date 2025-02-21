<template>
  <div v-if="$route.path !== '/login'">
    <el-container class="app-container">
      <el-aside width="200px">
        <div class="logo">
          <el-icon class="logo-icon"><Money /></el-icon>
          <span>家庭记账助手</span>
        </div>
        <el-menu
          :router="true"
          :default-active="$route.path"
          class="side-menu"
        >
          <el-menu-item index="/">
            <el-icon><DataLine /></el-icon>
            <span>总览</span>
          </el-menu-item>
          <el-menu-item index="/expenses">
            <el-icon><Money /></el-icon>
            <span>支出记录</span>
          </el-menu-item>
          <el-menu-item index="/income">
            <el-icon><Wallet /></el-icon>
            <span>收入记录</span>
          </el-menu-item>
        </el-menu>
      </el-aside>
      <el-container>
        <el-header>
          <div class="header-content">
            <div class="breadcrumb">
              <el-breadcrumb>
                <el-breadcrumb-item>首页</el-breadcrumb-item>
                <el-breadcrumb-item>{{ getPageTitle }}</el-breadcrumb-item>
              </el-breadcrumb>
            </div>
            <div class="user-info">
              <el-dropdown @command="handleCommand">
                <span class="user-dropdown">
                  <el-avatar :size="32" icon="UserFilled" />
                  <span>{{ username }}</span>
                  <el-icon><CaretBottom /></el-icon>
                </span>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item command="logout">退出登录</el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </div>
          </div>
        </el-header>
        <el-main>
          <router-view></router-view>
        </el-main>
      </el-container>
    </el-container>
  </div>
  <router-view v-else></router-view>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { DataLine, Money, Wallet, CaretBottom } from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()

const username = computed(() => {
  const user = localStorage.getItem('user')
  return user ? JSON.parse(user).username : ''
})

const getPageTitle = computed(() => {
  const pathMap = {
    '/': '总览',
    '/expenses': '支出记录',
    '/income': '收入记录'
  }
  return pathMap[route.path] || ''
})

const handleCommand = (command) => {
  if (command === 'logout') {
    localStorage.removeItem('user')
    router.push('/login')
  }
}

onMounted(() => {
  // 开发环境自动跳转到主页
  if (import.meta.env.DEV && router.currentRoute.value.path === '/login') {
    router.push('/')
  }
})
</script>

<style>
.app-container {
  height: 100vh;
}

.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0 20px;
  color: #2c3e50;
  font-size: 18px;
  font-weight: bold;
  border-bottom: 1px solid #e6e6e6;
}

.logo-icon {
  font-size: 24px;
  margin-right: 10px;
  color: #409EFF;
}

.side-menu {
  height: calc(100vh - 60px);
  border-right: none;
}

.el-header {
  background-color: white;
  border-bottom: 1px solid #e6e6e6;
  padding: 0 20px;
}

.header-content {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.breadcrumb {
  font-size: 14px;
}

.user-info {
  display: flex;
  align-items: center;
}

.user-dropdown {
  display: flex;
  align-items: center;
  cursor: pointer;
  color: #2c3e50;
}

.user-dropdown span {
  margin: 0 8px;
}

.el-aside {
  background-color: #f5f7fa;
  border-right: 1px solid #e6e6e6;
}

.el-main {
  background-color: #f5f7fa;
  padding: 20px;
}

body {
  margin: 0;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', 'Roboto', 'Oxygen',
    'Ubuntu', 'Cantarell', 'Fira Sans', 'Droid Sans', 'Helvetica Neue',
    sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}
</style> 