import type { DashboardItem, ResourceRecommendation } from '../types/dashboard'

export const dashboardRoutes = {
  overview: '/dashboard/today',
  reminders: '/dashboard/reminders',
  resources: '/dashboard/resources',
  reviews: '/dashboard/reviews',
  learningPath: '/ai-learning-center/learning-path',
  courseOnline: '/course-learning/online',
  aiTutor: '/ai-learning-center/tutor',
} as const

const targetRouteMap: Record<string, string> = {
  assignment: '/course-learning/records',
  certificate_result: '/certificate-standard/results',
  competition: '/competition-growth/tasks',
  competition_result: '/competition-growth/results',
  course: '/course-learning/online',
  course_resource: '/course-learning/resources',
  job_application: '/job-ability/applications',
  project_deliverable: '/project-training/evaluation',
  resource_package: '/ai-learning-center/resource-generation',
}

const itemTypeRouteMap: Record<string, string> = {
  certificate_result_review: '/certificate-standard/results',
  competition_result_review: '/competition-growth/results',
  course_learning: '/course-learning/online',
  live_course: '/course-learning/online',
  project_deliverable_review: '/project-training/evaluation',
  quiz: '/course-learning/quiz',
  resource_package_review: '/ai-learning-center/resource-generation',
}

export const getDashboardItemRoute = (item?: DashboardItem) => {
  if (!item) return dashboardRoutes.overview
  if (item.targetType && targetRouteMap[item.targetType]) return targetRouteMap[item.targetType]
  if (item.itemType && itemTypeRouteMap[item.itemType]) return itemTypeRouteMap[item.itemType]
  return dashboardRoutes.overview
}

export const getResourceRoute = (resource?: ResourceRecommendation) => {
  const type = resource?.resource?.resourceType
  if (type === 'course') return '/course-learning/resources'
  if (type === 'project') return '/project-training/projects'
  if (type === 'quiz') return '/course-learning/quiz'
  return dashboardRoutes.resources
}
