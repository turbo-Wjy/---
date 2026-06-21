export interface DashboardMetric {
  code: string
  name: string
  value: number | string
  unit?: string
}

export interface DashboardItem {
  id?: number
  itemType?: string
  title: string
  description?: string
  status?: string
  priority?: string
  targetType?: string
  targetId?: number
  createdAt?: string
}

export interface AiGeneratedResource {
  id?: number
  taskId?: number
  resourceType?: string
  title?: string
  contentUrl?: string
  contentText?: string
  metadata?: string
  status?: string
}

export interface ResourceRecommendation {
  id?: number
  studentId?: number
  resourceId?: number
  recommendReason?: string
  sourceProfileId?: number
  viewStatus?: string
  status?: string
  createdAt?: string
  resource?: AiGeneratedResource
}

export interface LearningPathProgress {
  pathId?: number
  pathTitle?: string
  pathStatus?: string
  totalSteps: number
  completedSteps: number
  progressPercent: number
}

export interface DashboardOverview {
  dashboardType?: 'student' | 'staff' | string
  greetingName?: string
  metrics: DashboardMetric[]
  todayTasks: DashboardItem[]
  learningReminders: DashboardItem[]
  recommendedResources: ResourceRecommendation[]
  pendingReviews: DashboardItem[]
  learningPathProgress?: LearningPathProgress | null
}
