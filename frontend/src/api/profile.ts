import axios from 'axios'
import { getStoredToken } from './http'
import type { ApiResponse } from '../types/auth'
import type {
  LearningProfile,
  ProfileConfirmRequest,
  ProfileExtractRequest,
  ProfileSession,
  ProfileSessionCreateRequest,
  ProfileSessionMessageRequest,
} from '../types/profile'

const profileHttp = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/api/v1',
  timeout: 12000,
})

profileHttp.interceptors.request.use((config) => {
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

export const createProfileSessionApi = async (payload: ProfileSessionCreateRequest) => {
  const response = await profileHttp.post<ApiResponse<ProfileSession>>('/profile-sessions', payload)
  return unwrap(response.data)
}

export const addProfileSessionMessageApi = async (sessionId: number, payload: ProfileSessionMessageRequest) => {
  const response = await profileHttp.post<ApiResponse<ProfileSession>>(`/profile-sessions/${sessionId}/messages`, payload)
  return unwrap(response.data)
}

export const getProfileSessionApi = async (sessionId: number) => {
  const response = await profileHttp.get<ApiResponse<ProfileSession>>(`/profile-sessions/${sessionId}`)
  return unwrap(response.data)
}

export const extractProfileSessionApi = async (sessionId: number, payload: ProfileExtractRequest = {}) => {
  const response = await profileHttp.post<ApiResponse<ProfileSession>>(`/profile-sessions/${sessionId}/extract`, payload)
  return unwrap(response.data)
}

export const confirmLearningProfileApi = async (payload: ProfileConfirmRequest) => {
  const response = await profileHttp.post<ApiResponse<LearningProfile>>('/learning-profiles/me/confirm', payload)
  return unwrap(response.data)
}

export const getMyLearningProfileApi = async () => {
  const response = await profileHttp.get<ApiResponse<LearningProfile>>('/learning-profiles/me')
  return unwrap(response.data)
}

export const getMyLearningProfileVersionsApi = async () => {
  const response = await profileHttp.get<ApiResponse<LearningProfile[]>>('/learning-profiles/me/versions')
  return unwrap(response.data)
}

export const getMyProfileEvidenceApi = async () => {
  const response = await profileHttp.get<ApiResponse<ProfileSession[]>>('/learning-profiles/me/evidence')
  return unwrap(response.data)
}
