import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '../router'

// 根据环境设置baseURL
const baseURL = import.meta.env.DEV 
  ? 'http://localhost:8080'  // 移除多余的 /api
  : '/api'  // 生产环境

const request = axios.create({
  baseURL,
  timeout: 5000,
  withCredentials: true // 允许跨域携带cookie
})

// 请求拦截器
request.interceptors.request.use(
  config => {
    // 开发环境不添加token
    if (!import.meta.env.DEV) {
      const token = localStorage.getItem('token')
      if (token) {
        config.headers['Authorization'] = 'Bearer ' + token
      }
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// 响应拦截器
request.interceptors.response.use(
  response => {
    const res = response.data
    if (res.code !== 200) {
      ElMessage.error(res.message || 'Error')
      // 开发环境不跳转登录页
      if (res.code === 401 && !import.meta.env.DEV) {
        router.push('/login')
      }
      return Promise.reject(new Error(res.message || 'Error'))
    }
    return res
  },
  error => {
    ElMessage.error(error.message || 'Request Error')
    return Promise.reject(error)
  }
)

export default request 