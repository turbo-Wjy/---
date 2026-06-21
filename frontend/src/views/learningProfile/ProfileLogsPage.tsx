import { ArrowRightOutlined, ClockCircleOutlined, HistoryOutlined, MessageOutlined } from '@ant-design/icons'
import { Button, Card, Descriptions, Drawer, Empty, List, Space, Statistic, Tag, Timeline } from 'antd'
import { useEffect, useMemo, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { getMyLearningProfileVersionsApi, getMyProfileEvidenceApi, getProfileSessionApi } from '../../api/profile'
import AppCard from '../../components/ui/AppCard'
import PageContainer from '../../components/ui/PageContainer'
import SectionHeader from '../../components/ui/SectionHeader'
import { withDisplayEvidence, withDisplayVersions } from '../../mocks/profile'
import type { LearningProfile, ProfileSession } from '../../types/profile'
import {
  confirmStatusColor,
  confirmStatusText,
  formatDateTime,
  percentValue,
  profileTitle,
  sessionTitle,
} from '../../utils/profileDisplay'

const ProfileLogsPage = () => {
  const navigate = useNavigate()
  const [versions, setVersions] = useState<LearningProfile[]>([])
  const [evidence, setEvidence] = useState<ProfileSession[]>([])
  const [activeSession, setActiveSession] = useState<ProfileSession | null>(null)
  const [loading, setLoading] = useState(true)
  const [drawerLoading, setDrawerLoading] = useState(false)

  useEffect(() => {
    let active = true
    const load = async () => {
      setLoading(true)
      const [versionsResult, evidenceResult] = await Promise.allSettled([
        getMyLearningProfileVersionsApi(),
        getMyProfileEvidenceApi(),
      ])
      if (!active) return
      setVersions(versionsResult.status === 'fulfilled' ? versionsResult.value : [])
      setEvidence(evidenceResult.status === 'fulfilled' ? evidenceResult.value : [])
      setLoading(false)
    }
    load()
    return () => {
      active = false
    }
  }, [])

  const displayVersions = useMemo(() => withDisplayVersions(versions), [versions])
  const displayEvidence = useMemo(() => withDisplayEvidence(evidence), [evidence])

  const openSession = async (session: ProfileSession) => {
    setActiveSession(session)
    if (!session.id) return
    setDrawerLoading(true)
    try {
      const detail = await getProfileSessionApi(session.id)
      setActiveSession(detail)
    } catch {
      setActiveSession(session)
    } finally {
      setDrawerLoading(false)
    }
  }

  return (
    <PageContainer
      description="追溯画像版本、对话证据、抽取状态和确认记录，帮助后续解释推荐与统计来源。"
      extra={
        <Space wrap>
          <Button onClick={() => navigate('/learning-profile/dimensions')}>维度分析</Button>
          <Button icon={<ArrowRightOutlined />} onClick={() => navigate('/learning-profile/chat')} type="primary">
            继续构建
          </Button>
        </Space>
      }
      title="画像更新记录"
    >
      <div className="profile-stat-grid">
        <Card loading={loading} variant="outlined">
          <Statistic prefix={<HistoryOutlined />} title="画像版本" value={displayVersions.length} />
        </Card>
        <Card loading={loading} variant="outlined">
          <Statistic prefix={<MessageOutlined />} title="证据会话" value={displayEvidence.length} />
        </Card>
        <Card loading={loading} variant="outlined">
          <Statistic
            title="已确认会话"
            value={displayEvidence.filter((item) => item.confirmStatus === 'confirmed').length}
          />
        </Card>
        <Card loading={loading} variant="outlined">
          <Statistic title="最新完整度" suffix="%" value={percentValue(displayVersions[0]?.completenessScore)} />
        </Card>
      </div>

      <div className="profile-main-grid">
        <AppCard
          loading={loading}
          title={<SectionHeader icon={<ClockCircleOutlined />} title="画像版本时间线" />}
        >
          <Timeline
            items={displayVersions.map((item) => ({
              color: item.displayOnly ? 'gray' : 'blue',
              children: (
                <div className="profile-timeline-item">
                  <strong>{profileTitle(item)}</strong>
                  <p>{item.profileSummary || '暂无画像摘要'}</p>
                  <span>
                    完整度 {percentValue(item.completenessScore)}% · {formatDateTime(item.lastGeneratedAt)}
                  </span>
                </div>
              ),
            }))}
          />
        </AppCard>

        <AppCard loading={loading} title={<SectionHeader icon={<MessageOutlined />} title="画像证据" />}>
          <List
            dataSource={displayEvidence}
            locale={{ emptyText: <Empty description="暂无画像证据" image={Empty.PRESENTED_IMAGE_SIMPLE} /> }}
            renderItem={(item) => (
              <List.Item
                actions={[
                  <Button key="detail" onClick={() => openSession(item)} type="link">
                    查看详情
                  </Button>,
                ]}
              >
                <List.Item.Meta
                  description={
                    <span>
                      {formatDateTime(item.createdAt)} · 可信度 {percentValue(item.confidenceScore)}%
                    </span>
                  }
                  title={
                    <span className="detail-list-title">
                      {sessionTitle(item)}
                      <Tag color={confirmStatusColor(item.confirmStatus)}>{confirmStatusText(item.confirmStatus)}</Tag>
                    </span>
                  }
                />
              </List.Item>
            )}
          />
        </AppCard>
      </div>

      <Drawer
        extra={
          <Button onClick={() => navigate('/learning-profile/chat')} type="primary">
            进入画像构建
          </Button>
        }
        loading={drawerLoading}
        open={Boolean(activeSession)}
        size={560}
        title="画像证据详情"
        onClose={() => setActiveSession(null)}
      >
        {activeSession ? (
          <div className="profile-drawer-stack">
            <Descriptions
              column={1}
              items={[
                { key: 'title', label: '会话标题', children: sessionTitle(activeSession) },
                {
                  key: 'status',
                  label: '确认状态',
                  children: confirmStatusText(activeSession.confirmStatus),
                },
                { key: 'confidence', label: '可信度', children: `${percentValue(activeSession.confidenceScore)}%` },
                { key: 'createdAt', label: '创建时间', children: formatDateTime(activeSession.createdAt) },
                { key: 'draft', label: '画像草稿', children: activeSession.draftProfile || '-' },
              ]}
            />
            <List
              dataSource={activeSession.messages || []}
              header="对话消息"
              locale={{ emptyText: <Empty description="暂无对话消息" image={Empty.PRESENTED_IMAGE_SIMPLE} /> }}
              renderItem={(message) => (
                <List.Item>
                  <List.Item.Meta
                    description={formatDateTime(message.createdAt)}
                    title={`${message.role === 'student' ? '学生' : '智能体'}：${message.content}`}
                  />
                </List.Item>
              )}
            />
          </div>
        ) : null}
      </Drawer>
    </PageContainer>
  )
}

export default ProfileLogsPage
