import { ArrowRightOutlined, ExclamationCircleOutlined, ThunderboltOutlined } from '@ant-design/icons'
import { Button, Card, Descriptions, Drawer, Empty, Select, Space, Statistic, Tabs, Tag } from 'antd'
import { useEffect, useMemo, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { getJobRolesApi, getMyFusionGraphApi } from '../../api/fusion'
import AppCard from '../../components/ui/AppCard'
import PageContainer from '../../components/ui/PageContainer'
import SectionHeader from '../../components/ui/SectionHeader'
import { WeaknessHeatmapChart, WeaknessRankingChart } from '../../components/visualization/prelearningVisuals'
import { withDisplayFusionGraph, withDisplayJobRoles } from '../../mocks/prelearning'
import type { FusionGraph, FusionNode, JobRole } from '../../types/fusion'
import { masteryColor, masteryText, nodeTypeText, percentValue } from '../../utils/prelearningDisplay'

const FusionWeakPointsPage = () => {
  const navigate = useNavigate()
  const [roles, setRoles] = useState<JobRole[]>([])
  const [selectedRoleId, setSelectedRoleId] = useState<number | undefined>()
  const [graph, setGraph] = useState<FusionGraph | null>(null)
  const [activeNode, setActiveNode] = useState<FusionNode | null>(null)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    let active = true
    const load = async () => {
      setLoading(true)
      const [rolesResult, graphResult] = await Promise.allSettled([getJobRolesApi(), getMyFusionGraphApi()])
      if (!active) return
      setRoles(rolesResult.status === 'fulfilled' ? rolesResult.value.items || [] : [])
      setGraph(graphResult.status === 'fulfilled' ? graphResult.value : null)
      setLoading(false)
    }
    load()
    return () => {
      active = false
    }
  }, [])

  const displayRoles = useMemo(() => withDisplayJobRoles(roles), [roles])
  const displayGraph = useMemo(() => withDisplayFusionGraph(graph), [graph])
  const weakPoints = displayGraph.weakPoints
  const averageScore = weakPoints.length
    ? Math.round(weakPoints.reduce((sum, item) => sum + percentValue(item.score), 0) / weakPoints.length)
    : 0

  const handleRoleChange = async (roleId: number) => {
    setSelectedRoleId(roleId)
    if (roleId < 0) {
      setGraph(null)
      return
    }
    setLoading(true)
    try {
      setGraph(await getMyFusionGraphApi(roleId))
    } catch {
      setGraph(null)
    } finally {
      setLoading(false)
    }
  }

  return (
    <PageContainer
      description="从融合图谱中定位当前画像与目标岗位之间的知识短板，并引导进入资源生成和路径规划。"
      extra={
        <Space wrap>
          <Select
            allowClear
            className="prelearning-select"
            onChange={handleRoleChange}
            options={displayRoles.map((role) => ({ label: role.roleName, value: role.id }))}
            placeholder="选择目标岗位"
            value={selectedRoleId}
          />
          <Button onClick={() => navigate('/fusion-graph/map')}>查看完整图谱</Button>
          <Button icon={<ArrowRightOutlined />} onClick={() => navigate('/ai-learning-center/resource-generation')} type="primary">
            生成补弱资源
          </Button>
        </Space>
      }
      title="知识短板定位"
    >
      <div className="profile-stat-grid">
        <Card loading={loading} variant="outlined">
          <Statistic prefix={<ExclamationCircleOutlined />} title="薄弱节点" value={weakPoints.length} />
        </Card>
        <Card loading={loading} variant="outlined">
          <Statistic title="平均得分" suffix="%" value={averageScore} />
        </Card>
        <Card loading={loading} variant="outlined">
          <Statistic title="课程相关" value={weakPoints.filter((item) => item.nodeType === 'course_knowledge_point').length} />
        </Card>
        <Card loading={loading} variant="outlined">
          <Statistic title="能力相关" value={weakPoints.filter((item) => item.nodeType === 'job_capability').length} />
        </Card>
      </div>

      <div className="visual-page-grid">
        <AppCard
          className="visual-main-card"
          loading={loading}
          title={
            <SectionHeader
              icon={<ExclamationCircleOutlined />}
              title="短板热力定位"
              description="颜色越深，说明该节点在理解、练习或应用阶段越需要优先补强。"
            />
          }
        >
          <WeaknessHeatmapChart loading={loading} weakPoints={weakPoints} />
        </AppCard>

        <AppCard className="visual-side-card" loading={loading} title={<SectionHeader icon={<ThunderboltOutlined />} title="补弱建议" />}>
          <WeaknessRankingChart height={280} loading={loading} weakPoints={weakPoints} />
          <div className="visual-side-actions">
            <Button block onClick={() => navigate('/ai-learning-center/resource-generation')} type="primary">
              生成补弱资源包
            </Button>
            <Button block onClick={() => navigate('/ai-learning-center/learning-path')}>调整学习路径</Button>
            <Button block onClick={() => navigate('/course-learning/quiz')}>进入答题练习</Button>
          </div>
        </AppCard>
      </div>

      <AppCard title={<SectionHeader title="短板处理台" />}>
        <Tabs
          items={[
            {
              key: 'detail',
              label: '短板详情',
              children: (
                weakPoints.length ? (
                  <div className="visual-list-stack">
                    {weakPoints.map((item) => (
                      <div className="visual-list-item" key={item.nodeKey || item.label}>
                        <div>
                          <span className="detail-list-title">
                            {item.label}
                            <Tag>{nodeTypeText(item.nodeType)}</Tag>
                            <Tag color={masteryColor(item.masteryStatus)}>{masteryText(item.masteryStatus)}</Tag>
                          </span>
                          <div className="detail-list-description">
                            <span>{item.description || '暂无说明'}</span>
                            <span>得分 {percentValue(item.score)}%</span>
                          </div>
                        </div>
                        <Space>
                          <Button onClick={() => setActiveNode(item)} type="link">
                            查看详情
                          </Button>
                          <Button onClick={() => navigate('/ai-learning-center/resource-generation')} type="primary">
                            生成资源
                          </Button>
                        </Space>
                      </div>
                    ))}
                  </div>
                ) : (
                  <Empty description="暂无知识短板" image={Empty.PRESENTED_IMAGE_SIMPLE} />
                )
              ),
            },
            {
              key: 'resources',
              label: '关联资源',
              children: (
                <div className="visual-action-grid">
                  <button onClick={() => navigate('/course-learning/resources')} type="button">
                    <strong>课程资料</strong>
                    <span>查看和短板知识点相关的课程资料。</span>
                  </button>
                  <button onClick={() => navigate('/ai-learning-center/recommendations')} type="button">
                    <strong>推荐资源</strong>
                    <span>进入资源精准推送列表。</span>
                  </button>
                  <button onClick={() => navigate('/project-training/cases')} type="button">
                    <strong>实操案例</strong>
                    <span>用案例任务巩固薄弱能力。</span>
                  </button>
                </div>
              ),
            },
            {
              key: 'practice',
              label: '练习记录',
              children: (
                <div className="visual-action-grid">
                  <button onClick={() => navigate('/course-learning/quiz')} type="button">
                    <strong>答题练习</strong>
                    <span>通过练习确认短板是否改善。</span>
                  </button>
                  <button onClick={() => navigate('/course-learning/records')} type="button">
                    <strong>学习记录</strong>
                    <span>查看学习行为是否回流到画像。</span>
                  </button>
                  <button onClick={() => navigate('/ai-learning-center/evaluation')} type="button">
                    <strong>效果评估</strong>
                    <span>查看阶段学习效果和改进建议。</span>
                  </button>
                </div>
              ),
            },
          ]}
        />
      </AppCard>

      <Drawer
        extra={
          <Button onClick={() => navigate('/ai-learning-center/resource-generation')} type="primary">
            生成补弱资源
          </Button>
        }
        open={Boolean(activeNode)}
        size={520}
        title="短板详情"
        onClose={() => setActiveNode(null)}
      >
        {activeNode ? (
          <Descriptions
            column={1}
            items={[
              { key: 'label', label: '短板节点', children: activeNode.label },
              { key: 'type', label: '节点类型', children: nodeTypeText(activeNode.nodeType) },
              { key: 'description', label: '说明', children: activeNode.description || '-' },
              { key: 'score', label: '掌握得分', children: `${percentValue(activeNode.score)}%` },
              {
                key: 'mastery',
                label: '掌握状态',
                children: <Tag color={masteryColor(activeNode.masteryStatus)}>{masteryText(activeNode.masteryStatus)}</Tag>,
              },
            ]}
          />
        ) : null}
      </Drawer>
    </PageContainer>
  )
}

export default FusionWeakPointsPage
