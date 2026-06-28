<script setup lang="ts">
import { useUserStore } from '@/stores/user'
import { useRouter } from 'vue-router'

const userStore = useUserStore()
const router = useRouter()

function handleLogout() {
  userStore.logout()
  router.push('/')
}
</script>

<template>
  <el-menu mode="horizontal" :ellipsis="false" class="nav-bar">
    <div class="nav-left">
      <router-link to="/" class="logo">🏫 学研在线</router-link>
    </div>
    <div class="flex-grow" />
    <div class="nav-right">
      <template v-if="userStore.isLoggedIn">
        <span class="greeting">👋 {{ userStore.user?.nickname }}</span>
        <router-link to="/order">
          <el-button type="primary" text>我的订单</el-button>
        </router-link>
        <el-button @click="handleLogout" text>退出</el-button>
      </template>
      <template v-else>
        <router-link to="/login">
          <el-button text>登录</el-button>
        </router-link>
        <router-link to="/register">
          <el-button type="primary">注册</el-button>
        </router-link>
      </template>
    </div>
  </el-menu>
</template>

<style scoped>
.nav-bar {
  display: flex;
  align-items: center;
  padding: 0 24px;
  height: 60px;
  border-bottom: 1px solid #eee;
}
.nav-left .logo {
  font-size: 20px;
  font-weight: 700;
  color: #409eff;
  text-decoration: none;
}
.flex-grow {
  flex: 1;
}
.nav-right {
  display: flex;
  align-items: center;
  gap: 12px;
}
.greeting {
  color: #666;
  font-size: 14px;
}
</style>
