export interface ApiResponse<T> {
  code: number
  message: string
  data: T
  traceId?: string
}

export interface LoginRequest {
  username: string
  password: string
}

export interface UserProfile {
  id: number
  username: string
  realName: string
  accountStatus: string
  mustChangePassword: boolean
  lastLoginAt?: string
  roleCodes: string[]
  permissionCodes?: string[]
}

export interface LoginResponse {
  token: string
  mustChangePassword: boolean
  user: UserProfile
  permissions: string[]
  menus: string[]
}

export interface ChangePasswordRequest {
  newPassword: string
}
