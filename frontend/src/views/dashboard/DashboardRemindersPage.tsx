import { ArrowRightOutlined, BellOutlined, ClockCircleOutlined, FlagOutlined } from '@ant-design/icons'
import { Button, Card, Empty, List, Progress, Segmented, Statistic, Tag } from 'antd'
import { useEffect, useMemo, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { getDashboardOverviewApi } from '../../api/dashboard'
import AppCard from '../../components/ui/AppCard'
import SectionHeader from '../../components/ui/SectionHeader'
import type { DashboardItem, DashboardOverview } from '../../types/dashboard'
import {
  emptyDashboardOverview,
  formatDateTime,
  itemTypeText,
  priorityColor,
  priorityText,
  statusColor,
  statusText,
} from '../../utils/dashboardDisplay'
import { getDashboardItemRoute } from '../../utils/dashboardDrilldown'

type ReminderFilter = 'all' | 'important' | 'normal'

const DashboardRemindersPage = () => {
  const navigate = useNavigate()
  const [overview, setOverview] = useState<DashboardOverview>(emptyDashboardOverview)
  const [filter, setFilter] = useState<ReminderFilter>('all')
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    let active = true
    getDashboardOverviewApi()
      .then((data) => {
        if (!active) return
        setOverview({ ...emptyDashboardOverview, ...data, learningReminders: data.learningReminders || [] })
      })
      .catch(() => {
        if (!active) return
        setOverview(emptyDashboardOverview)
      })
      .finally(() => {
        if (active) setLoading(false)
      })
    return () => {
      active = false
    }
  }, [])

  const reminders = overview.learningReminders || []
  const filteredReminders = useMemo(
    () => reminders.filter((item) => filter === 'all' || item.priority === filter),
    [filter, reminders],
  )

  const handleDrilldown = (item: DashboardItem) => {
    navigate(getDashboardItemRoute(item), { state: { dashboardItem: item } })
  }

  return (
    <section className="dashboard-detail-page">
      <div className="detail-stat-grid">
        <Card variant="outlined">
          <Statistic prefix={<BellOutlined />} title="全部提醒" value={reminders.length} />
        </Card>
        <Card variant="outlined">
          <Statistic
            prefix={<FlagOutlined />}
            title="重要提醒"
            value={reminders.filter((item) => item.priority === 'important').length}
          />
        </Card>
        <Card variant="outlined">
          <Statistic
            prefix={<ClockCircleOutlined />}
            title="普通提醒"
            value={reminders.filter((item) => item.priority === 'normal').length}
          />
        </Card>
      </div>

      <div className="dashboard-detail-grid">
        <AppCard
          className="detail-main-card"
          loading={loading}
          title={
            <SectionHeader
              action={
                <Segmented
                  onChange={(value) => setFilter(value as ReminderFilter)}
                  options={[
                    { label: '全部', value: 'all' },
                    { label: '重要', value: 'important' },
                    { label: '普通', value: 'normal' },
                  ]}
                  value={filter}
                />
              }
              title="学习提醒"
            />
          }
        >
          <List
            dataSource={filteredReminders}
            locale={{ emptyText: <Empty description="暂无学习提醒" image={Empty.PRESENTED_IMAGE_SIMPLE} /> }}
            renderItem={(item) => (
              <List.Item
                actions={[
                  <Button
                    icon={<ArrowRightOutlined />}
                    iconPlacement="end"
                    key="drill"
                    onClick={() => handleDrilldown(item)}
                    type="link"
                  >
                    去处理
                  </Button>,
                ]}
              >
                <List.Item.Meta
                  avatar={<BellOutlined className={`compact-icon ${item.priority === 'important' ? 'compact-icon-warning' : ''}`} />}
                  description={
                    <div className="detail-list-description">
                      <span>{item.description || '暂无说明'}</span>
                      <span>目标：{itemTypeText(item)}</span>
                      <span>创建：{formatDateTime(item.createdAt)}</span>
                    </div>
                  }
                  title={
                    <span className="detail-list-title">
                      {item.title}
                      <Tag color={priorityColor(item.priority)}>{priorityText(item.priority)}</Tag>
                      <Tag color={statusColor(item.status)}>{statusText(item.status)}</Tag>
                    </span>
                  }
                />
              </List.Item>
            )}
          />
        </AppCard>

        <AppCard
          className="detail-side-card"
          title={<SectionHeader description="来自首页工作台接口" title="学习路径摘要" />}
        >
          {overview.learningPathProgress ? (
            <div className="detail-progress-panel">
              <strong>{overview.learningPathProgress.pathTitle || '学习路径'}</strong>
              <Progress percent={overview.learningPathProgress.progressPercent} strokeColor="#315bff" />
              <p>
                已完成 {overview.learningPathProgress.completedSteps}/{overview.learningPathProgress.totalSteps} 个阶段
              </p>
              <Button onClick={() => navigate('/ai-learning-center/learning-path')} type="primary">
                查看学习路径
              </Button>
            </div>
          ) : (
            <Empty description="暂无学习路径进度" image={Empty.PRESENTED_IMAGE_SIMPLE}>
              <Button onClick={() => navigate('/ai-learning-center/learning-path')} type="primary">
                去生成路径
              </Button>
            </Empty>
          )}
        </AppCard>
      </div>
    </section>
  )
}

export default DashboardRemindersPage
