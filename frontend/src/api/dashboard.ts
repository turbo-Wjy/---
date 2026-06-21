import axios from 'axios'
import { getStoredToken } from './http'
import type { ApiResponse } from '../types/auth'
import type { DashboardItem, DashboardOverview, ResourceRecommendation } from '../types/dashboard'

const dashboardHttp = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/api/v1',
  timeout: 12000,
})

dashboardHttp.interceptors.request.use((config) => {
  const token = getStoredToken()
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

const unwrap = <T>(payload: ApiResponse<T> | T): T => {
  if (payload && typeof payload === 'object' && 'code' in payload) {
    const body = payload as ApiResponse<T>
    if (body.code === 0) {
      return body.data
    }
    throw new Error(body.message || '请求处理失败')
  }
  return payload as T
}

export const getDashboardOverviewApi = async () => {
  const response = await dashboardHttp.get<ApiResponse<DashboardOverview>>('/dashboard/overview')
  return unwrap(response.data)
}

export const getTeacherPendingReviewsApi = async () => {
  const response = await dashboardHttp.get<ApiResponse<DashboardItem[]>>('/teacher-dashboard/pending-reviews')
  return unwrap(response.data)
}

export const getResourceRecommendationsApi = async (viewStatus?: string) => {
  const response = await dashboardHttp.get<ApiResponse<ResourceRecommendation[]>>('/resource-recommendations/me', {
    params: viewStatus ? { viewStatus } : undefined,
  })
  return unwrap(response.data)
}
