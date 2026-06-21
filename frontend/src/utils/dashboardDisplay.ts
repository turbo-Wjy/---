import type { DashboardItem, DashboardOverview, ResourceRecommendation } from '../types/dashboard'

export const emptyDashboardOverview: DashboardOverview = {
  dashboardType: 'unknown',
  greetingName: '',
  metrics: [],
  todayTasks: [],
  learningReminders: [],
  recommendedResources: [],
  pendingReviews: [],
  learningPathProgress: null,
}

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

export const priorityText = (priority?: string) => {
  if (priority === 'important') return '重要'
  if (priority === 'normal') return '普通'
  return priority || '-'
}

export const priorityColor = (priority?: string) => {
  if (priority === 'important') return 'orange'
  if (priority === 'normal') return 'blue'
  return 'default'
}

export const statusText = (status?: string) => {
  const map: Record<string, string> = {
    active: '进行中',
    completed: '已完成',
    generated: '已生成',
    new: '新提醒',
    not_started: '未开始',
    pending: '待处理',
    pending_review: '待审核',
    process: '进行中',
    read: '已读',
    rejected: '已驳回',
    unread: '未读',
    upcoming: '即将开始',
  }
  return status ? map[status] || status : '-'
}

export const statusColor = (status?: string) => {
  if (status === 'completed' || status === 'read') return 'green'
  if (status === 'pending_review' || status === 'pending') return 'orange'
  if (status === 'rejected') return 'red'
  if (status === 'unread' || status === 'new') return 'blue'
  return 'default'
}

export const itemTypeText = (item?: DashboardItem) => {
  const type = item?.targetType || item?.itemType || ''
  const map: Record<string, string> = {
    certificate_result: '证书成果',
    certificate_result_review: '证书成果',
    competition: '竞赛任务',
    competition_result: '竞赛成果',
    competition_result_review: '竞赛成果',
    course: '课程学习',
    learning_path: '学习路径',
    learning_path_step: '学习步骤',
    project_deliverable: '项目交付物',
    project_deliverable_review: '项目交付物',
    resource_package: '资源包',
    resource_package_review: '资源包',
    resource_package_status: '资源包状态',
    resource_recommendation: '推荐资源',
  }
  return map[type] || type || '-'
}

export const resourceTypeText = (item?: ResourceRecommendation) => {
  const type = item?.resource?.resourceType || ''
  const map: Record<string, string> = {
    course: '课程',
    project: '项目',
    quiz: '题库',
    document: '资料',
    video: '视频',
  }
  return map[type] || type || '资源'
}

export const resourceTitle = (item: ResourceRecommendation) => item.resource?.title || item.recommendReason || '推荐学习资源'
