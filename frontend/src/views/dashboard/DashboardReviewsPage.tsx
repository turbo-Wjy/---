import { ArrowRightOutlined, AuditOutlined } from '@ant-design/icons'
import { Button, Card, Descriptions, Drawer, Empty, Segmented, Statistic, Table, Tag, type TableColumnsType } from 'antd'
import { useEffect, useMemo, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { getDashboardOverviewApi, getTeacherPendingReviewsApi } from '../../api/dashboard'
import AppCard from '../../components/ui/AppCard'
import SectionHeader from '../../components/ui/SectionHeader'
import { useAuth } from '../../stores/auth'
import type { DashboardItem } from '../../types/dashboard'
import { formatDateTime, itemTypeText, priorityColor, priorityText, statusColor, statusText } from '../../utils/dashboardDisplay'
import { getDashboardItemRoute } from '../../utils/dashboardDrilldown'

type ReviewFilter = 'all' | 'resource_package' | 'competition_result' | 'certificate_result' | 'project_deliverable'

const reviewFilters = [
  { label: '全部', value: 'all' },
  { label: '资源包', value: 'resource_package' },
  { label: '竞赛成果', value: 'competition_result' },
  { label: '证书成果', value: 'certificate_result' },
  { label: '项目交付物', value: 'project_deliverable' },
]

const isReviewType = (item: DashboardItem, filter: ReviewFilter) => {
  if (filter === 'all') return true
  return item.targetType === filter || item.itemType?.includes(filter)
}

const DashboardReviewsPage = () => {
  const auth = useAuth()
  const navigate = useNavigate()
  const [reviews, setReviews] = useState<DashboardItem[]>([])
  const [filter, setFilter] = useState<ReviewFilter>('all')
  const [activeReview, setActiveReview] = useState<DashboardItem | null>(null)
  const [loading, setLoading] = useState(true)

  const canUseTeacherReviews =
    auth.hasPermission('teacher_dashboard.view.assigned') ||
    Boolean(auth.user?.roleCodes?.some((role) => ['admin', 'teacher', 'major_leader', '系统管理员', '教师', '专业负责人'].includes(role)))

  useEffect(() => {
    let active = true
    const request = canUseTeacherReviews
      ? getTeacherPendingReviewsApi()
      : getDashboardOverviewApi().then((overview) => overview.pendingReviews || [])

    request
      .then((data) => {
        if (!active) return
        setReviews(data || [])
      })
      .catch(() => {
        if (!active) return
        setReviews([])
      })
      .finally(() => {
        if (active) setLoading(false)
      })

    return () => {
      active = false
    }
  }, [canUseTeacherReviews])

  const filteredReviews = useMemo(() => reviews.filter((item) => isReviewType(item, filter)), [filter, reviews])

  const columns: TableColumnsType<DashboardItem> = [
    {
      title: '事项标题',
      dataIndex: 'title',
      key: 'title',
      ellipsis: true,
    },
    {
      title: '类型',
      key: 'type',
      width: 130,
      render: (_, record) => itemTypeText(record),
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      width: 110,
      render: (value: string) => <Tag color={statusColor(value)}>{statusText(value)}</Tag>,
    },
    {
      title: '优先级',
      dataIndex: 'priority',
      key: 'priority',
      width: 110,
      render: (value: string) => <Tag color={priorityColor(value)}>{priorityText(value)}</Tag>,
    },
    {
      title: '说明',
      dataIndex: 'description',
      key: 'description',
      ellipsis: true,
    },
    {
      title: '创建时间',
      dataIndex: 'createdAt',
      key: 'createdAt',
      width: 150,
      render: (value: string) => formatDateTime(value),
    },
    {
      title: '操作',
      key: 'action',
      fixed: 'right',
      width: 150,
      render: (_, record) => (
        <div className="table-actions">
          <Button onClick={() => setActiveReview(record)} size="small" type="link">
            详情
          </Button>
          <Button
            icon={<ArrowRightOutlined />}
            iconPlacement="end"
            onClick={() => navigate(getDashboardItemRoute(record), { state: { dashboardItem: record } })}
            size="small"
            type="link"
          >
            处理
          </Button>
        </div>
      ),
    },
  ]

  return (
    <section className="dashboard-detail-page">
      <div className="detail-stat-grid">
        <Card variant="outlined">
          <Statistic prefix={<AuditOutlined />} title="待办审核" value={reviews.length} />
        </Card>
        <Card variant="outlined">
          <Statistic title="重要事项" value={reviews.filter((item) => item.priority === 'important').length} />
        </Card>
        <Card variant="outlined">
          <Statistic title="当前视图" value={filteredReviews.length} />
        </Card>
      </div>

      <AppCard
        className="detail-main-card"
        title={
          <SectionHeader
            action={<Segmented onChange={(value) => setFilter(value as ReviewFilter)} options={reviewFilters} value={filter} />}
            description={canUseTeacherReviews ? '来自教师工作台待审核接口' : '来自首页工作台个人状态接口'}
            title="待办审核"
          />
        }
      >
        <Table
          columns={columns}
          dataSource={filteredReviews}
          loading={loading}
          locale={{ emptyText: <Empty description="暂无待审核事项" image={Empty.PRESENTED_IMAGE_SIMPLE} /> }}
          pagination={{ pageSize: 8, showSizeChanger: false }}
          rowKey={(record) => `${record.itemType || record.targetType || 'review'}-${record.id || record.targetId || record.title}`}
          scroll={{ x: 980 }}
          size="small"
        />
      </AppCard>

      <Drawer
        open={Boolean(activeReview)}
        size={520}
        title="审核事项详情"
        onClose={() => setActiveReview(null)}
        extra={
          <Button
            onClick={() => {
              if (activeReview) navigate(getDashboardItemRoute(activeReview), { state: { dashboardItem: activeReview } })
            }}
            type="primary"
          >
            去处理
          </Button>
        }
      >
        {activeReview ? (
          <Descriptions
            column={1}
            items={[
              { key: 'title', label: '事项标题', children: activeReview.title },
              { key: 'type', label: '类型', children: itemTypeText(activeReview) },
              { key: 'description', label: '说明', children: activeReview.description || '-' },
              { key: 'status', label: '状态', children: statusText(activeReview.status) },
              { key: 'priority', label: '优先级', children: priorityText(activeReview.priority) },
              { key: 'createdAt', label: '创建时间', children: formatDateTime(activeReview.createdAt) },
            ]}
          />
        ) : null}
      </Drawer>
    </section>
  )
}

export default DashboardReviewsPage
