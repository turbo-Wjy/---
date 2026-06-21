import { ArrowRightOutlined, ReadOutlined } from '@ant-design/icons'
import { Button, Empty, List, Tag } from 'antd'
import { useNavigate } from 'react-router-dom'
import type { ResourceRecommendation } from '../../types/dashboard'
import { dashboardRoutes, getResourceRoute } from '../../utils/dashboardDrilldown'
import AppCard from '../ui/AppCard'
import SectionHeader from '../ui/SectionHeader'

interface RecommendPanelProps {
  resources: ResourceRecommendation[]
  loading?: boolean
}

const getResourceTitle = (item: ResourceRecommendation) => item.resource?.title || item.recommendReason || '推荐学习资源'

const getResourceType = (item: ResourceRecommendation) => {
  const type = item.resource?.resourceType
  if (type === 'course') return '课程'
  if (type === 'project') return '项目'
  if (type === 'quiz') return '题库'
  return '资源'
}

const RecommendPanel = ({ resources, loading }: RecommendPanelProps) => {
  const navigate = useNavigate()
  const drillToResource = (item: ResourceRecommendation) =>
    navigate(getResourceRoute(item), { state: { recommendation: item } })

  return (
    <AppCard
      className="side-panel-card recommend-card"
      loading={loading}
      title={<SectionHeader title="推荐资源" />}
      extra={
        <Button icon={<ArrowRightOutlined />} iconPlacement="end" onClick={() => navigate(dashboardRoutes.resources)} type="link">
          更多推荐
        </Button>
      }
    >
      <List
        className="compact-list resource-preview-list"
        dataSource={resources.slice(0, 2)}
        locale={{ emptyText: <Empty description="暂无推荐资源" image={Empty.PRESENTED_IMAGE_SIMPLE} /> }}
        renderItem={(item) => (
          <List.Item
            onClick={() => drillToResource(item)}
            onKeyDown={(event) => {
              if (event.key === 'Enter' || event.key === ' ') drillToResource(item)
            }}
            role="button"
            tabIndex={0}
          >
            <List.Item.Meta
              avatar={<ReadOutlined className="compact-icon" />}
              description={<span>{item.recommendReason || '根据画像与学习路径推荐'}</span>}
              title={
                <span className="compact-title">
                  {getResourceTitle(item)}
                  <Tag color={item.viewStatus === 'unread' ? 'blue' : 'default'}>{getResourceType(item)}</Tag>
                </span>
              }
            />
          </List.Item>
        )}
      />
    </AppCard>
  )
}

export default RecommendPanel
