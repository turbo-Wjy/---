export interface PageResult<T> {
  items: T[]
  page: number
  pageSize: number
  total: number
}

export interface PageQuery {
  page?: number
  pageSize?: number
  keyword?: string
  status?: string
}
