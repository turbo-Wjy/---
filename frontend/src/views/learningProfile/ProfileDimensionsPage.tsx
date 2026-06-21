import { BarChartOutlined, EyeOutlined, RadarChartOutlined } from '@ant-design/icons'
import { Button, Card, Descriptions, Drawer, Empty, List, Progress, Segmented, Space, Statistic, Tag } from 'antd'
import { useEffect, useMemo, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { getMyLearningProfileApi } from '../../api/profile'
import AppCard from '../../components/ui/AppCard'
import PageContainer from '../../components/ui/PageContainer'
import SectionHeader from '../../components/ui/SectionHeader'
import { withDisplayProfile } from '../../mocks/profile'
import type { LearningProfile, ProfileDimension } from '../../types/profile'
import { averageConfidence, percentValue, sourceText } from '../../utils/profileDisplay'

type DimensionFilter = 'all' | 'real' | 'display'

const ProfileDimensionsPage = () => {
  const navigate = useNavigate()
  const [profile, setProfile] = useState<LearningProfile | null>(null)
  const [filter, setFilter] = useState<DimensionFilter>('all')
  const [activeDimension, setActiveDimension] = useState<ProfileDimension | null>(null)
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
  const filteredDimensions = useMemo(
    () =>
      dimensions.filter((item) => {
        if (filter === 'real') return !item.displayOnly
        if (filter === 'display') return item.displayOnly
        return true
      }),
    [dimensions, filter],
  )

  return (
    <PageContainer
      description="按画像维度查看当前学生知识基础、目标、认知风格、短板、偏好和学习进度。"
      extra={
        <Space wrap>
          <Button onClick={() => navigate('/learning-profile/logs')}>查看更新记录</Button>
          <Button onClick={() => navigate('/ai-learning-center/recommendations')} type="primary">
            查看推荐资源
          </Button>
        </Space>
      }
      title="画像维度分析"
    >
      <div className="profile-stat-grid">
        <Card loading={loading} variant="outlined">
          <Statistic prefix={<RadarChartOutlined />} title="维度数量" value={dimensions.length} />
        </Card>
        <Card loading={loading} variant="outlined">
          <Statistic title="平均可信度" suffix="%" value={averageConfidence(dimensions)} />
        </Card>
        <Card loading={loading} variant="outlined">
          <Statistic title="接口维度" value={dimensions.filter((item) => !item.displayOnly).length} />
        </Card>
        <Card loading={loading} variant="outlined">
          <Statistic title="展示补齐" value={dimensions.filter((item) => item.displayOnly).length} />
        </Card>
      </div>

      <AppCard
        loading={loading}
        title={
          <SectionHeader
            action={
              <Segmented
                onChange={(value) => setFilter(value as DimensionFilter)}
                options={[
                  { label: '全部', value: 'all' },
                  { label: '接口数据', value: 'real' },
                  { label: '展示补齐', value: 'display' },
                ]}
                value={filter}
              />
            }
            icon={<BarChartOutlined />}
            title="维度列表"
          />
        }
      >
        <List
          dataSource={filteredDimensions}
          grid={{ gutter: 14, xs: 1, sm: 1, md: 2, lg: 2, xl: 3, xxl: 4 }}
          locale={{ emptyText: <Empty description="暂无维度数据" image={Empty.PRESENTED_IMAGE_SIMPLE} /> }}
          renderItem={(item) => (
            <List.Item>
              <Card
                actions={[
                  <Button icon={<EyeOutlined />} key="detail" onClick={() => setActiveDimension(item)} type="link">
                    查看详情
                  </Button>,
                ]}
                className="profile-dimension-list-card"
                size="small"
                variant="outlined"
              >
                <div className="profile-dimension-card-head">
                  <strong>{item.name}</strong>
                  <Tag color={item.displayOnly ? 'default' : 'success'}>
                    {item.displayOnly ? '展示补齐' : '接口数据'}
                  </Tag>
                </div>
                <p>{item.value}</p>
                <Progress percent={percentValue(item.confidence)} size="small" strokeColor="#315bff" />
              </Card>
            </List.Item>
          )}
        />
      </AppCard>

      <Drawer
        extra={
          <Button onClick={() => navigate('/ai-learning-center/learning-path')} type="primary">
            用于路径规划
          </Button>
        }
        open={Boolean(activeDimension)}
        size={520}
        title="画像维度详情"
        onClose={() => setActiveDimension(null)}
      >
        {activeDimension ? (
          <Descriptions
            column={1}
            items={[
              { key: 'name', label: '维度名称', children: activeDimension.name },
              { key: 'code', label: '维度编码', children: activeDimension.code },
              { key: 'value', label: '维度内容', children: activeDimension.value },
              { key: 'confidence', label: '可信度', children: `${percentValue(activeDimension.confidence)}%` },
              { key: 'source', label: '来源', children: sourceText(activeDimension.source) },
              { key: 'displayOnly', label: '数据类型', children: activeDimension.displayOnly ? '展示补齐' : '接口数据' },
            ]}
          />
        ) : null}
      </Drawer>
    </PageContainer>
  )
}

export default ProfileDimensionsPage
