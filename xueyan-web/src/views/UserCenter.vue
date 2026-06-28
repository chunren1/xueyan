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
  <div class="user-container">
    <el-card>
      <h2>个人中心</h2>
      <el-descriptions :column="1" border v-if="userStore.user">
        <el-descriptions-item label="用户ID">{{ userStore.user.userId }}</el-descriptions-item>
        <el-descriptions-item label="用户名">{{ userStore.user.username }}</el-descriptions-item>
        <el-descriptions-item label="昵称">{{ userStore.user.nickname }}</el-descriptions-item>
      </el-descriptions>
      <div class="actions">
        <el-button type="danger" @click="handleLogout">退出登录</el-button>
      </div>
    </el-card>
  </div>
</template>

<style scoped>
.user-container {
  max-width: 600px;
  margin: 0 auto;
}
.user-container h2 {
  margin-bottom: 16px;
}
.actions {
  margin-top: 20px;
  text-align: center;
}
</style>
