import axios, { type AxiosError, type InternalAxiosRequestConfig } from 'axios'
import { message as antdMessage } from 'antd'
import type { ApiResponse } from '../types/auth'

const TOKEN_KEY = 'ai-learning.token'

export const getStoredToken = () => localStorage.getItem(TOKEN_KEY) || ''

export const setStoredToken = (token: string) => {
  localStorage.setItem(TOKEN_KEY, token)
}

export const clearStoredToken = () => {
  localStorage.removeItem(TOKEN_KEY)
}

export const http = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/api/v1',
  timeout: 15000,
})

http.interceptors.request.use((config: InternalAxiosRequestConfig) => {
  const token = getStoredToken()
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

http.interceptors.response.use(
  (response) => {
    const body = response.data as ApiResponse<unknown>
    if (body && typeof body === 'object' && 'code' in body) {
      if (body.code === 0) {
        return body.data
      }
      antdMessage.error(body.message || '请求处理失败')
      return Promise.reject(new Error(body.message || '请求处理失败'))
    }
    return response.data
  },
  (error: AxiosError<ApiResponse<unknown>>) => {
    const status = error.response?.status
    const message = error.response?.data?.message || error.message || '网络请求失败'
    if (status === 401 || status === 403) {
      clearStoredToken()
      const isLoginPage = window.location.pathname === '/login'
      antdMessage.error(isLoginPage ? message : status === 403 ? '无权限访问' : '登录状态已失效')
      if (window.location.pathname !== '/login') {
        window.location.href = '/login'
      }
      return Promise.reject(error)
    }
    antdMessage.error(message)
    return Promise.reject(error)
  },
)
