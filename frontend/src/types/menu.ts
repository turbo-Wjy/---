export interface MenuPageConfig {
  key: string
  title: string
  path: string
  description: string
  requiredAnyPermissions?: string[]
  statusItems?: string[]
}

export interface MenuGroupConfig {
  key: string
  title: string
  menuCode?: string
  requiredAnyPermissions?: string[]
  description: string
  children: MenuPageConfig[]
}
