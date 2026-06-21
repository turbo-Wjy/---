import { ArrowRightOutlined, EyeOutlined, ReadOutlined } from '@ant-design/icons'
import { Button, Card, Descriptions, Drawer, Empty, List, Segmented, Select, Statistic, Tag } from 'antd'
import { useEffect, useMemo, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { getResourceRecommendationsApi } from '../../api/dashboard'
import AppCard from '../../components/ui/AppCard'
import SectionHeader from '../../components/ui/SectionHeader'
import type { ResourceRecommendation } from '../../types/dashboard'
import { formatDateTime, resourceTitle, resourceTypeText, statusColor, statusText } from '../../utils/dashboardDisplay'
import { getResourceRoute } from '../../utils/dashboardDrilldown'

type ViewFilter = 'all' | 'unread' | 'read'

const DashboardResourcesPage = () => {
  const navigate = useNavigate()
  const [resources, setResources] = useState<ResourceRecommendation[]>([])
  const [viewFilter, setViewFilter] = useState<ViewFilter>('all')
  const [typeFilter, setTypeFilter] = useState('all')
  const [activeResource, setActiveResource] = useState<ResourceRecommendation | null>(null)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    let active = true
    setLoading(true)
    getResourceRecommendationsApi(viewFilter === 'all' ? undefined : viewFilter)
      .then((data) => {
        if (!active) return
        setResources(data || [])
      })
      .catch(() => {
        if (!active) return
        setResources([])
      })
      .finally(() => {
        if (active) setLoading(false)
      })
    return () => {
      active = false
    }
  }, [viewFilter])

  const resourceTypes = useMemo(
    () => Array.from(new Set(resources.map((item) => item.resource?.resourceType).filter(Boolean))) as string[],
    [resources],
  )
  const filteredResources = useMemo(
    () => resources.filter((item) => typeFilter === 'all' || item.resource?.resourceType === typeFilter),
    [resources, typeFilter],
  )

  return (
    <section className="dashboard-detail-page">
      <div className="detail-stat-grid">
        <Card variant="outlined">
          <Statistic prefix={<ReadOutlined />} title="推荐资源" value={resources.length} />
        </Card>
        <Card variant="outlined">
          <Statistic title="未读资源" value={resources.filter((item) => item.viewStatus === 'unread').length} />
        </Card>
        <Card variant="outlined">
          <Statistic title="资源类型" value={resourceTypes.length} />
        </Card>
      </div>

      <AppCard
        className="detail-main-card"
        loading={loading}
        title={
          <SectionHeader
            action={
              <div className="detail-toolbar">
                <Segmented
                  onChange={(value) => setViewFilter(value as ViewFilter)}
                  options={[
                    { label: '全部', value: 'all' },
                    { label: '未读', value: 'unread' },
                    { label: '已读', value: 'read' },
                  ]}
                  value={viewFilter}
                />
                <Select
                  onChange={setTypeFilter}
                  options={[
                    { label: '全部类型', value: 'all' },
                    ...resourceTypes.map((type) => ({ label: type, value: type })),
                  ]}
                  value={typeFilter}
                />
              </div>
            }
            title="推荐资源"
          />
        }
      >
        <List
          dataSource={filteredResources}
          locale={{ emptyText: <Empty description="暂无推荐资源" image={Empty.PRESENTED_IMAGE_SIMPLE} /> }}
          renderItem={(item) => (
            <List.Item
              actions={[
                <Button key="detail" onClick={() => setActiveResource(item)} type="link">
                  查看详情
                </Button>,
                <Button
                  icon={<ArrowRightOutlined />}
                  iconPlacement="end"
                  key="open"
                  onClick={() => navigate(getResourceRoute(item), { state: { recommendation: item } })}
                  type="primary"
                >
                  查看资源
                </Button>,
              ]}
            >
              <List.Item.Meta
                avatar={<EyeOutlined className="compact-icon" />}
                description={
                  <div className="detail-list-description">
                    <span>{item.recommendReason || '暂无推荐理由'}</span>
                    <span>推荐时间：{formatDateTime(item.createdAt)}</span>
                  </div>
                }
                title={
                  <span className="detail-list-title">
                    {resourceTitle(item)}
                    <Tag color="blue">{resourceTypeText(item)}</Tag>
                    <Tag color={statusColor(item.viewStatus)}>{statusText(item.viewStatus)}</Tag>
                    <Tag>{statusText(item.status)}</Tag>
                  </span>
                }
              />
            </List.Item>
          )}
        />
      </AppCard>

      <Drawer
        open={Boolean(activeResource)}
        size={520}
        title="推荐资源详情"
        onClose={() => setActiveResource(null)}
        extra={
          <Button
            onClick={() => {
              if (activeResource) navigate(getResourceRoute(activeResource), { state: { recommendation: activeResource } })
            }}
            type="primary"
          >
            查看资源
          </Button>
        }
      >
        {activeResource ? (
          <Descriptions
            column={1}
            items={[
              { key: 'title', label: '资源标题', children: resourceTitle(activeResource) },
              { key: 'type', label: '资源类型', children: resourceTypeText(activeResource) },
              { key: 'reason', label: '推荐理由', children: activeResource.recommendReason || '-' },
              { key: 'viewStatus', label: '阅读状态', children: statusText(activeResource.viewStatus) },
              { key: 'status', label: '资源状态', children: statusText(activeResource.status) },
              { key: 'createdAt', label: '推荐时间', children: formatDateTime(activeResource.createdAt) },
            ]}
          />
        ) : null}
      </Drawer>
    </section>
  )
}

export default DashboardResourcesPage
