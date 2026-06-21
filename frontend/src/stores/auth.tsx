import { createContext, useCallback, useContext, useMemo, useState, type ReactNode } from 'react'
import {
  changePasswordApi,
  forceChangePasswordApi,
  getCurrentMenusApi,
  getCurrentPermissionsApi,
  getCurrentUserApi,
  loginApi,
  logoutApi,
} from '../api/auth'
import { clearStoredToken, getStoredToken, setStoredToken } from '../api/http'
import { menuGroups } from '../constants/menu'
import type { LoginRequest, LoginResponse, UserProfile } from '../types/auth'

const USER_KEY = 'ai-learning.user'
const MENUS_KEY = 'ai-learning.menus'
const PERMISSIONS_KEY = 'ai-learning.permissions'
const AUTH_BYPASS_ENABLED = import.meta.env.VITE_AUTH_BYPASS === 'true'

interface AuthState {
  token: string
  user: UserProfile | null
  menus: string[]
  permissions: string[]
}

interface AuthContextValue extends AuthState {
  isAuthed: boolean
  roleText: string
  hasPermission: (permission?: string) => boolean
  hasAnyPermission: (permissions?: string[]) => boolean
  hasMenu: (menuCode?: string, requiredAnyPermissions?: string[]) => boolean
  login: (payload: LoginRequest) => Promise<LoginResponse>
  refreshCurrentUser: () => Promise<void>
  changePassword: (newPassword: string) => Promise<void>
  logout: () => Promise<void>
  clearSession: () => void
}

const parseArray = (value: string | null) => {
  if (!value) return []
  try {
    const parsed = JSON.parse(value)
    return Array.isArray(parsed) ? parsed.filter((item) => typeof item === 'string') : []
  } catch {
    return []
  }
}

const parseUser = (value: string | null): UserProfile | null => {
  if (!value) return null
  try {
    return JSON.parse(value) as UserProfile
  } catch {
    return null
  }
}

const unique = (values: string[]) => Array.from(new Set(values.filter(Boolean)))

const getAllMenuCodes = () => unique(menuGroups.map((group) => group.menuCode).filter((code): code is string => Boolean(code)))

const getAllPermissions = () =>
  unique(
    menuGroups.flatMap((group) => [
      ...(group.requiredAnyPermissions || []),
      ...group.children.flatMap((child) => child.requiredAnyPermissions || []),
    ]),
  )

const createBypassState = (): AuthState => ({
  token: 'dev-auth-bypass-token',
  user: {
    id: 0,
    username: 'dev',
    realName: '开发预览用户',
    accountStatus: 'ENABLED',
    mustChangePassword: false,
    roleCodes: ['系统管理员'],
  },
  menus: getAllMenuCodes(),
  permissions: getAllPermissions(),
})

const createInitialState = (): AuthState => {
  if (AUTH_BYPASS_ENABLED) {
    return createBypassState()
  }
  return {
    token: getStoredToken(),
    user: parseUser(localStorage.getItem(USER_KEY)),
    menus: parseArray(localStorage.getItem(MENUS_KEY)),
    permissions: parseArray(localStorage.getItem(PERMISSIONS_KEY)),
  }
}

const persistSession = (state: AuthState) => {
  if (state.user) {
    localStorage.setItem(USER_KEY, JSON.stringify(state.user))
  }
  localStorage.setItem(MENUS_KEY, JSON.stringify(state.menus))
  localStorage.setItem(PERMISSIONS_KEY, JSON.stringify(state.permissions))
}

const AuthContext = createContext<AuthContextValue | null>(null)

export const AuthProvider = ({ children }: { children: ReactNode }) => {
  const [state, setState] = useState<AuthState>(() => createInitialState())

  const clearSession = useCallback(() => {
    if (AUTH_BYPASS_ENABLED) {
      setState(createBypassState())
      return
    }
    clearStoredToken()
    localStorage.removeItem(USER_KEY)
    localStorage.removeItem(MENUS_KEY)
    localStorage.removeItem(PERMISSIONS_KEY)
    setState({ token: '', user: null, menus: [], permissions: [] })
  }, [])

  const setSession = useCallback((payload: AuthState) => {
    setStoredToken(payload.token)
    persistSession(payload)
    setState(payload)
  }, [])

  const hasPermission = useCallback(
    (permission?: string) => {
      if (!permission) return true
      return state.permissions.includes(permission) || state.menus.includes(permission)
    },
    [state.menus, state.permissions],
  )

  const hasAnyPermission = useCallback(
    (permissions?: string[]) => {
      if (!permissions || permissions.length === 0) return true
      return permissions.some((permission) => hasPermission(permission))
    },
    [hasPermission],
  )

  const hasMenu = useCallback(
    (menuCode?: string, requiredAnyPermissions?: string[]) => {
      if (menuCode && state.menus.includes(menuCode)) return true
      return hasAnyPermission(requiredAnyPermissions)
    },
    [hasAnyPermission, state.menus],
  )

  const login = useCallback(
    async (payload: LoginRequest) => {
      const response = await loginApi(payload)
      setSession({
        token: response.token,
        user: response.user,
        menus: response.menus,
        permissions: response.permissions,
      })
      return response
    },
    [setSession],
  )

  const refreshCurrentUser = useCallback(async () => {
    if (AUTH_BYPASS_ENABLED) return
    if (!state.token) return
    const [user, menus, permissions] = await Promise.all([
      getCurrentUserApi(),
      getCurrentMenusApi(),
      getCurrentPermissionsApi(),
    ])
    setSession({ token: state.token, user, menus, permissions })
  }, [setSession, state.token])

  const changePassword = useCallback(
    async (newPassword: string) => {
      if (AUTH_BYPASS_ENABLED) return
      const api = state.user?.mustChangePassword ? forceChangePasswordApi : changePasswordApi
      await api({ newPassword })
      setState((current) => {
        if (!current.user) return current
        const next = {
          ...current,
          user: { ...current.user, mustChangePassword: false },
        }
        persistSession(next)
        return next
      })
    },
    [state.user?.mustChangePassword],
  )

  const logout = useCallback(async () => {
    if (AUTH_BYPASS_ENABLED) {
      clearSession()
      return
    }
    try {
      await logoutApi()
    } finally {
      clearSession()
    }
  }, [clearSession])

  const value = useMemo<AuthContextValue>(
    () => ({
      ...state,
      isAuthed: Boolean(state.token),
      roleText: state.user?.roleCodes?.join(' / ') || '未分配角色',
      hasPermission,
      hasAnyPermission,
      hasMenu,
      login,
      refreshCurrentUser,
      changePassword,
      logout,
      clearSession,
    }),
    [changePassword, clearSession, hasAnyPermission, hasMenu, hasPermission, login, logout, refreshCurrentUser, state],
  )

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}

export const useAuth = () => {
  const context = useContext(AuthContext)
  if (!context) {
    throw new Error('useAuth must be used within AuthProvider')
  }
  return context
}
