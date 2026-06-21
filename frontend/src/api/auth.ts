import { http } from './http'
import type { ChangePasswordRequest, LoginRequest, LoginResponse, UserProfile } from '../types/auth'

export const loginApi = (payload: LoginRequest) => http.post<unknown, LoginResponse>('/auth/login', payload)

export const getCurrentUserApi = () => http.get<unknown, UserProfile>('/auth/me')

export const getCurrentMenusApi = () => http.get<unknown, string[]>('/auth/me/menus')

export const getCurrentPermissionsApi = () => http.get<unknown, string[]>('/auth/me/permissions')

export const changePasswordApi = (payload: ChangePasswordRequest) =>
  http.post<unknown, null>('/auth/change-password', payload)

export const forceChangePasswordApi = (payload: ChangePasswordRequest) =>
  http.post<unknown, null>('/auth/force-change-password', payload)

export const logoutApi = () => http.post<unknown, null>('/auth/logout')
