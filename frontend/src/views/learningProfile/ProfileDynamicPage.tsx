import { ArrowRightOutlined, FileTextOutlined, NodeIndexOutlined, ThunderboltOutlined } from '@ant-design/icons'
import { Button, Card, Empty, Progress, Space, Statistic, Tag } from 'antd'
import { useEffect, useMemo, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { getMyLearningProfileApi } from '../../api/profile'
import AppCard from '../../components/ui/AppCard'
import PageContainer from '../../components/ui/PageContainer'
import SectionHeader from '../../components/ui/SectionHeader'
import { withDisplayProfile } from '../../mocks/profile'
import type { LearningProfile } from '../../types/profile'
import { averageConfidence, formatDateTime, percentValue, sourceText } from '../../utils/profileDisplay'

const ProfileDynamicPage = () => {
  const navigate = useNavigate()
  const [profile, setProfile] = useState<LearningProfile | null>(null)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    let active = true
    getMyLearningProfileApi()
      .then((data) => {
        if (active) setProfile(data)
      })
      .catch(() => {
        if (active) setProfile(null)
      })
      .finally(() => {
        if (active) setLoading(false)
      })
    return () => {
      active = false
    }
  }, [])

  const displayProfile = useMemo(() => withDisplayProfile(profile), [profile])
  const dimensions = displayProfile.dimensions || []

  return (
    <PageContainer
      description="展示当前学生画像摘要、维度完整度、可信度和后续学习动作入口。"
      extra={
        <Space wrap>
          <Button icon={<NodeIndexOutlined />} onClick={() => navigate('/fusion-graph/map')}>
            查看融合图谱
          </Button>
          <Button icon={<ArrowRightOutlined />} onClick={() => navigate('/learning-profile/chat')} type="primary">
            继续构建画像
          </Button>
        </Space>
      }
      tags={displayProfile.displayOnly ? <Tag color="default">展示补齐</Tag> : <Tag color="success">接口数据</Tag>}
      title="动态画像"
    >
      <div className="profile-stat-grid">
        <Card loading={loading} variant="outlined">
          <Statistic title="画像完整度" suffix="%" value={percentValue(displayProfile.completenessScore)} />
        </Card>
        <Card loading={loading} variant="outlined">
          <Statistic title="平均可信度" suffix="%" value={averageConfidence(dimensions)} />
        </Card>
        <Card loading={loading} variant="outlined">
          <Statistic title="画像维度" value={dimensions.length} />
        </Card>
        <Card loading={loading} variant="outlined">
          <Statistic title="画像版本" prefix="V" value={displayProfile.profileVersion || 1} />
        </Card>
      </div>

      <div className="profile-main-grid">
        <AppCard
          className="profile-summary-card"
          loading={loading}
          title={<SectionHeader icon={<FileTextOutlined />} title="画像摘要" />}
        >
          {displayProfile.profileSummary ? (
            <div className="profile-summary">
              <p>{displayProfile.profileSummary}</p>
              <span>最近更新：{formatDateTime(displayProfile.lastGeneratedAt)}</span>
            </div>
          ) : (
            <Empty description="暂无画像摘要" image={Empty.PRESENTED_IMAGE_SIMPLE} />
          )}
        </AppCard>

        <AppCard
          className="profile-summary-card"
          loading={loading}
          title={<SectionHeader icon={<ThunderboltOutlined />} title="下一步建议" />}
        >
          <div className="profile-action-cards">
            <button onClick={() => navigate('/ai-learning-center/learning-path')} type="button">
              <strong>生成学习路径</strong>
              <span>基于画像、目标岗位和知识短板规划阶段任务</span>
            </button>
            <button onClick={() => navigate('/ai-learning-center/resource-generation')} type="button">
              <strong>生成个性化资源</strong>
              <span>把画像维度转成文档、题库、案例和项目材料</span>
            </button>
            <button onClick={() => navigate('/job-ability/match')} type="button">
              <strong>查看岗位匹配</strong>
              <span>对比岗位能力点和当前画像证据链</span>
            </button>
          </div>
        </AppCard>
      </div>

      <AppCard loading={loading} title={<SectionHeader title="画像维度" />}>
        <div className="profile-dimension-grid">
          {dimensions.map((item) => (
            <Card key={item.code} size="small" variant="outlined">
              <div className="profile-dimension-card-head">
                <strong>{item.name}</strong>
                <Tag>{sourceText(item.source)}</Tag>
              </div>
              <p>{item.value}</p>
              <Progress percent={percentValue(item.confidence)} size="small" strokeColor="#315bff" />
            </Card>
          ))}
        </div>
      </AppCard>
    </PageContainer>
  )
}

export default ProfileDynamicPage
