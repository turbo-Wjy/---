import {
  BookOutlined,
  CheckCircleOutlined,
  ClockCircleOutlined,
  FileSearchOutlined,
  FolderOpenOutlined,
  LineChartOutlined,
  ReadOutlined,
  TeamOutlined,
} from '@ant-design/icons'
import type { ComponentType } from 'react'
import type { DashboardMetric } from '../../types/dashboard'
import MetricCard from '../ui/MetricCard'

interface MetricGridProps {
  metrics: DashboardMetric[]
}

const metricColors = ['#315bff', '#17c983', '#6757ff', '#ff8a3d']

const metricIconMap: Record<string, ComponentType> = {
  today_tasks: ClockCircleOutlined,
  path_progress: LineChartOutlined,
  recommended_resources: ReadOutlined,
  pending_reviews: FileSearchOutlined,
  profile_versions: FolderOpenOutlined,
  active_students: TeamOutlined,
  active_paths: BookOutlined,
  recommendations: ReadOutlined,
}

const fallbackMetrics: DashboardMetric[] = [
  { code: 'today_tasks', name: '今日待办', value: 0, unit: '项' },
  { code: 'path_progress', name: '路径进度', value: 0, unit: '%' },
  { code: 'recommended_resources', name: '推荐资源', value: 0, unit: '个' },
  { code: 'pending_reviews', name: '待审核事项', value: 0, unit: '项' },
]

const getPercent = (metric: DashboardMetric) => {
  if (metric.unit === '%') return Math.max(0, Math.min(100, Number(metric.value) || 0))
  return Math.max(4, Math.min(100, Number(metric.value) * 8 || 8))
}

const MetricGrid = ({ metrics }: MetricGridProps) => (
  <div className="metric-grid">
    {[...metrics, ...fallbackMetrics]
      .filter((item, index, list) => list.findIndex((candidate) => candidate.code === item.code) === index)
      .slice(0, 4)
      .map((item, index) => (
        <MetricCard
          color={metricColors[index] || '#315bff'}
          icon={metricIconMap[item.code] || CheckCircleOutlined}
          key={item.code}
          percent={getPercent(item)}
          title={item.name}
          unit={item.unit}
          value={item.value}
        />
      ))}
  </div>
)

export default MetricGrid
