import { defineStore } from 'pinia'
import { ref } from 'vue'
import { userApi, type LoginResult } from '@/api/user'

export const useUserStore = defineStore('user', () => {
  const user = ref<LoginResult | null>(null)
  const isLoggedIn = ref(false)

  // 从 localStorage 恢复
  const saved = localStorage.getItem('user')
  if (saved) {
    try {
      user.value = JSON.parse(saved)
      isLoggedIn.value = true
    } catch {}
  }

  async function login(username: string, password: string) {
    const res = await userApi.login({ username, password })
    user.value = res.data
    isLoggedIn.value = true
    localStorage.setItem('user', JSON.stringify(res.data))
    localStorage.setItem('token', 'jwt-placeholder')
    return res.data
  }

  function logout() {
    user.value = null
    isLoggedIn.value = false
    localStorage.removeItem('user')
    localStorage.removeItem('token')
  }

  return { user, isLoggedIn, login, logout }
})
