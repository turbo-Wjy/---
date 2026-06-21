import axios from 'axios'
import { getStoredToken } from './http'
import type { ApiResponse } from '../types/auth'
import type { PageQuery, PageResult } from '../types/common'
import type { FusionGraph, FusionRelation, FusionRelationQuery, JobCapability, JobRole } from '../types/fusion'

const fusionHttp = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/api/v1',
  timeout: 12000,
})

fusionHttp.interceptors.request.use((config) => {
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

export const getJobRolesApi = async (query: PageQuery = {}) => {
  const response = await fusionHttp.get<ApiResponse<PageResult<JobRole>>>('/job-roles', {
    params: { page: 1, pageSize: 50, ...query },
  })
  return unwrap(response.data)
}

export const getJobCapabilitiesApi = async (jobRoleId: number) => {
  const response = await fusionHttp.get<ApiResponse<JobCapability[]>>(`/job-roles/${jobRoleId}/capabilities`)
  return unwrap(response.data)
}

export const getMyFusionGraphApi = async (jobRoleId?: number) => {
  const response = await fusionHttp.get<ApiResponse<FusionGraph>>('/fusion-graph/me', {
    params: jobRoleId ? { jobRoleId } : undefined,
  })
  return unwrap(response.data)
}

export const getJobFusionGraphApi = async (jobRoleId: number) => {
  const response = await fusionHttp.get<ApiResponse<FusionGraph>>(`/fusion-graph/jobs/${jobRoleId}`)
  return unwrap(response.data)
}

export const getCourseFusionGraphApi = async (courseId: number) => {
  const response = await fusionHttp.get<ApiResponse<FusionGraph>>(`/fusion-graph/courses/${courseId}`)
  return unwrap(response.data)
}

export const getFusionRelationsApi = async (query: FusionRelationQuery = {}) => {
  const response = await fusionHttp.get<ApiResponse<PageResult<FusionRelation>>>('/fusion-relations', {
    params: { page: 1, pageSize: 50, ...query },
  })
  return unwrap(response.data)
}
