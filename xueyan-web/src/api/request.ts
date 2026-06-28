import axios from 'axios'
import { ElMessage } from 'element-plus'

const request = axios.create({
  baseURL: import.meta.env.VITE_API_BASE,
  timeout: 10000,
})

// 响应拦截
request.interceptors.response.use(
  (res) => {
    const data = res.data
    if (data.code === 200) return data
    ElMessage.error(data.message || '请求失败')
    return Promise.reject(new Error(data.message))
  },
  (err) => {
    ElMessage.error('网络错误，请稍后重试')
    return Promise.reject(err)
  },
)

export default request
