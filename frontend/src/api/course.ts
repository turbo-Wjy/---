import axios from 'axios'
import { getStoredToken } from './http'
import type { ApiResponse } from '../types/auth'
import type { Course, CourseGraph, KnowledgePoint } from '../types/course'
import type { PageQuery, PageResult } from '../types/common'

const courseHttp = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/api/v1',
  timeout: 12000,
})

courseHttp.interceptors.request.use((config) => {
  const token = getStoredToken()
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

const unwrap = <T>(payload: ApiResponse<T> | T): T => {
  if (payload && typeof payload === 'object' && 'code' in payload) {
    const body = payload as ApiResponse<T>
    if (body.code === 0) return body.data
    throw new Error(body.message || '请求处理失败')
  }
  return payload as T
}

export const getCoursesApi = async (query: PageQuery = {}) => {
  const response = await courseHttp.get<ApiResponse<PageResult<Course>>>('/courses', {
    params: { page: 1, pageSize: 50, ...query },
  })
  return unwrap(response.data)
}

export const getCourseGraphApi = async (courseId: number) => {
  const response = await courseHttp.get<ApiResponse<CourseGraph>>(`/courses/${courseId}/graph`)
  return unwrap(response.data)
}

export const getCourseKnowledgePointsApi = async (courseId: number) => {
  const response = await courseHttp.get<ApiResponse<KnowledgePoint[]>>(`/courses/${courseId}/knowledge-points`)
  return unwrap(response.data)
}
