import type { LearningProfile, ProfileDimension, ProfileSession } from '../types/profile'

export const formatDateTime = (value?: string) => {
  if (!value) return '-'
  const date = new Date(value)
  if (Number.isNaN(date.getTime())) return value
  return date.toLocaleString('zh-CN', {
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
  })
}

export const percentValue = (value?: number) => {
  if (value === undefined || value === null) return 0
  const normalized = value <= 1 ? value * 100 : value
  return Math.max(0, Math.min(100, Math.round(normalized)))
}

export const averageConfidence = (dimensions?: ProfileDimension[]) => {
  const values = (dimensions || []).map((item) => percentValue(item.confidence)).filter((value) => value > 0)
  if (!values.length) return 0
  return Math.round(values.reduce((sum, item) => sum + item, 0) / values.length)
}

export const confirmStatusText = (status?: string) => {
  const statusMap: Record<string, string> = {
    draft: '草稿',
    extracted: '已抽取',
    confirmed: '已确认',
    active: '有效',
  }
  return status ? statusMap[status] || status : '未开始'
}

export const confirmStatusColor = (status?: string) => {
  const colorMap: Record<string, string> = {
    draft: 'default',
    extracted: 'processing',
    confirmed: 'success',
    active: 'success',
  }
  return status ? colorMap[status] || 'default' : 'default'
}

export const sourceText = (source?: string) => {
  const sourceMap: Record<string, string> = {
    profile_session: '画像对话',
    learning_record: '学习记录',
    evaluation: '学习评估',
    quiz: '答题练习',
    resource_feedback: '资源反馈',
    project: '项目实训',
    dashboard: '工作台汇总',
  }
  return source ? sourceMap[source] || source : '系统补齐'
}

export const profileTitle = (profile?: LearningProfile) => `V${profile?.profileVersion ?? 1} 学习画像`

export const sessionTitle = (session?: ProfileSession) => session?.sessionTitle || '学习画像构建'
