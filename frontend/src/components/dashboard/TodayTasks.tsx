import { ArrowRightOutlined, ClockCircleOutlined } from '@ant-design/icons'
import { Button, Empty, List, Tag } from 'antd'
import { useNavigate } from 'react-router-dom'
import type { DashboardItem, DashboardOverview } from '../../types/dashboard'
import { dashboardRoutes, getDashboardItemRoute } from '../../utils/dashboardDrilldown'
import AppCard from '../ui/AppCard'
import SectionHeader from '../ui/SectionHeader'

interface TodayTasksProps {
  overview: DashboardOverview
  loading?: boolean
}

const statusColorMap: Record<string, string> = {
  important: 'orange',
  normal: 'blue',
  pending_review: 'purple',
  done: 'green',
}

const getActionItems = (overview: DashboardOverview) =>
  overview.dashboardType === 'staff' ? overview.pendingReviews : overview.todayTasks

const getTagText = (item: DashboardItem) => item.status || item.itemType || '待处理'

const TodayTasks = ({ overview, loading }: TodayTasksProps) => {
  const navigate = useNavigate()
  const listRoute = overview.dashboardType === 'staff' ? dashboardRoutes.reviews : dashboardRoutes.reminders

  return (
    <AppCard
      className="today-tasks"
      loading={loading}
      title={
        <SectionHeader
          description={overview.dashboardType === 'staff' ? '优先处理审核和指导事项' : '优先展示今天需要推进的学习任务'}
          title="今日优先处理"
        />
      }
      extra={
        <Button icon={<ArrowRightOutlined />} iconPlacement="end" onClick={() => navigate(listRoute)} type="link">
          {overview.dashboardType === 'staff' ? '查看审核' : '查看全部'}
        </Button>
      }
    >
      <List
        className="priority-list"
        dataSource={getActionItems(overview).slice(0, 4)}
        locale={{ emptyText: <Empty description="今天暂无优先事项" image={Empty.PRESENTED_IMAGE_SIMPLE} /> }}
        renderItem={(item) => (
          <List.Item
            actions={[
              <Button
                key="action"
                onClick={() => navigate(getDashboardItemRoute(item), { state: { dashboardItem: item } })}
                size="small"
                type="link"
              >
                处理
              </Button>,
            ]}
          >
            <List.Item.Meta
              avatar={<ClockCircleOutlined className="priority-icon" />}
              description={
                <span className="priority-description">
                  <span>{item.description || '等待处理'}</span>
                  <Tag color={statusColorMap[item.priority || item.status || 'normal'] || 'default'}>{getTagText(item)}</Tag>
                </span>
              }
              title={<span className="priority-title">{item.title}</span>}
            />
          </List.Item>
        )}
      />
    </AppCard>
  )
}

export default TodayTasks
