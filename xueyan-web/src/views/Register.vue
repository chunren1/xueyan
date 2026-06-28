<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { userApi } from '@/api/user'
import { ElMessage } from 'element-plus'

const router = useRouter()
const form = ref({ username: '', password: '', confirmPassword: '' })
const loading = ref(false)

async function handleRegister() {
  if (!form.value.username || !form.value.password) {
    ElMessage.warning('请填写必填项')
    return
  }
  if (form.value.password !== form.value.confirmPassword) {
    ElMessage.warning('两次密码不一致')
    return
  }
  loading.value = true
  try {
    await userApi.register({
      username: form.value.username,
      password: form.value.password,
    })
    ElMessage.success('注册成功，请登录')
    router.push('/login')
  } catch {
    // 错误已拦截
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="register-container">
    <el-card class="register-card" shadow="always">
      <h2 class="title">注册</h2>
      <el-form @submit.prevent="handleRegister">
        <el-form-item>
          <el-input v-model="form.username" placeholder="用户名（3-50位）" size="large" />
        </el-form-item>
        <el-form-item>
          <el-input
            v-model="form.password"
            type="password"
            placeholder="密码（6-100位）"
            size="large"
            show-password
          />
        </el-form-item>
        <el-form-item>
          <el-input
            v-model="form.confirmPassword"
            type="password"
            placeholder="确认密码"
            size="large"
            show-password
          />
        </el-form-item>
        <el-form-item>
          <el-button
            type="primary"
            size="large"
            class="w-full"
            :loading="loading"
            @click="handleRegister"
          >
            注册
          </el-button>
        </el-form-item>
      </el-form>
      <p class="tip">
        已有账号？
        <router-link to="/login">立即登录</router-link>
      </p>
    </el-card>
  </div>
</template>

<style scoped>
.register-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 60vh;
}
.register-card {
  width: 400px;
}
.title {
  text-align: center;
  margin-bottom: 24px;
}
.tip {
  text-align: center;
  font-size: 14px;
  color: #909399;
}
.tip a {
  color: #409eff;
}
.w-full {
  width: 100%;
}
</style>
