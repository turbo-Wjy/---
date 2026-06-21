import { ArrowRightOutlined, BellOutlined } from '@ant-design/icons'
import { Button, Empty, List, Tag } from 'antd'
import { useNavigate } from 'react-router-dom'
import type { DashboardItem } from '../../types/dashboard'
import { dashboardRoutes, getDashboardItemRoute } from '../../utils/dashboardDrilldown'
import AppCard from '../ui/AppCard'
import SectionHeader from '../ui/SectionHeader'

interface ReminderPanelProps {
  reminders: DashboardItem[]
  loading?: boolean
}

const ReminderPanel = ({ reminders, loading }: ReminderPanelProps) => {
  const navigate = useNavigate()
  const drillToReminder = (item: DashboardItem) => navigate(getDashboardItemRoute(item), { state: { dashboardItem: item } })

  return (
    <AppCard
      className="side-panel-card"
      loading={loading}
      title={<SectionHeader icon={<BellOutlined />} title="学习提醒" />}
      extra={
        <Button icon={<ArrowRightOutlined />} iconPlacement="end" onClick={() => navigate(dashboardRoutes.reminders)} type="link">
          查看全部
        </Button>
      }
    >
      <List
        className="compact-list"
        dataSource={reminders.slice(0, 3)}
        locale={{ emptyText: <Empty description="暂无提醒" image={Empty.PRESENTED_IMAGE_SIMPLE} /> }}
        renderItem={(item) => (
          <List.Item
            onClick={() => drillToReminder(item)}
            onKeyDown={(event) => {
              if (event.key === 'Enter' || event.key === ' ') drillToReminder(item)
            }}
            role="button"
            tabIndex={0}
          >
            <List.Item.Meta
              avatar={
                <BellOutlined className={`compact-icon ${item.priority === 'important' ? 'compact-icon-warning' : ''}`} />
              }
              description={<span>{item.description || '请按计划完成'}</span>}
              title={
                <span className="compact-title">
                  {item.title}
                  {item.priority === 'important' ? <Tag color="orange">重要</Tag> : null}
                </span>
              }
            />
          </List.Item>
        )}
      />
    </AppCard>
  )
}

export default ReminderPanel
