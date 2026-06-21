import { ArrowRightOutlined, FlagOutlined, PartitionOutlined, RiseOutlined } from '@ant-design/icons'
import { Button, Card, Descriptions, Drawer, Empty, Progress, Select, Space, Statistic, Tabs, Tag } from 'antd'
import { useEffect, useMemo, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { getJobRolesApi, getMyFusionGraphApi } from '../../api/fusion'
import AppCard from '../../components/ui/AppCard'
import PageContainer from '../../components/ui/PageContainer'
import SectionHeader from '../../components/ui/SectionHeader'
import { AbilityRadarChart, GrowthPathGraph, GrowthTrendChart } from '../../components/visualization/prelearningVisuals'
import { withDisplayFusionGraph, withDisplayJobRoles } from '../../mocks/prelearning'
import type { FusionGraph, FusionNode, JobRole } from '../../types/fusion'
import { masteryColor, masteryText, nodeTypeText, percentValue } from '../../utils/prelearningDisplay'

const FusionGrowthPathPage = () => {
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
  const pathItems = displayGraph.recommendedPath
  const pathProgress = pathItems.length
    ? Math.round(
        displayGraph.nodes.reduce((sum, item) => sum + percentValue(item.score), 0) / Math.max(displayGraph.nodes.length, 1),
      )
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
      description="把岗位能力、课程知识点和项目实践串成阶段成长路径，为后续学习路径规划提供输入。"
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
          <Button onClick={() => navigate('/fusion-graph/weak-points')}>查看短板</Button>
          <Button icon={<ArrowRightOutlined />} onClick={() => navigate('/ai-learning-center/learning-path')} type="primary">
            生成学习路径
          </Button>
        </Space>
      }
      title="能力成长路径"
    >
      <div className="profile-stat-grid">
        <Card loading={loading} variant="outlined">
          <Statistic prefix={<FlagOutlined />} title="路径阶段" value={pathItems.length} />
        </Card>
        <Card loading={loading} variant="outlined">
          <Statistic title="成长进度" suffix="%" value={pathProgress} />
        </Card>
        <Card loading={loading} variant="outlined">
          <Statistic prefix={<PartitionOutlined />} title="关联节点" value={displayGraph.nodes.length} />
        </Card>
        <Card loading={loading} variant="outlined">
          <Statistic title="薄弱节点" value={displayGraph.weakPoints.length} />
        </Card>
      </div>

      <div className="visual-page-grid">
        <AppCard
          className="visual-main-card"
          loading={loading}
          title={
            <SectionHeader
              icon={<RiseOutlined />}
              title="阶段成长路径图"
              description="从目标岗位能力出发，串联课程、知识点和项目成果。"
            />
          }
        >
          <GrowthPathGraph graph={displayGraph} loading={loading} onNodeClick={setActiveNode} />
        </AppCard>

        <AppCard className="visual-side-card" loading={loading} title={<SectionHeader title="路径完成度" />}>
          <Progress percent={pathProgress} strokeColor="#315bff" type="dashboard" />
          <div className="visual-step-list">
            {pathItems.map((item, index) => (
              <button key={`${item}-${index}`} onClick={() => navigate('/ai-learning-center/learning-path')} type="button">
                <span>{index + 1}</span>
                <strong>{item}</strong>
              </button>
            ))}
          </div>
          <div className="visual-side-actions">
            <Button block onClick={() => navigate('/ai-learning-center/learning-path')} type="primary">
              生成正式学习路径
            </Button>
            <Button block onClick={() => navigate('/project-training/projects')}>进入项目实训</Button>
          </div>
        </AppCard>
      </div>

      <AppCard title={<SectionHeader title="成长路径分析" />}>
        <Tabs
          items={[
            {
              key: 'stages',
              label: '阶段详情',
              children: (
                displayGraph.nodes.length ? (
                  <div className="visual-card-grid">
                    {displayGraph.nodes.map((item) => (
                      <Card className="profile-dimension-list-card" key={item.nodeKey || item.label} size="small" variant="outlined">
                        <div className="profile-dimension-card-head">
                          <strong>{item.label}</strong>
                          <Tag>{nodeTypeText(item.nodeType)}</Tag>
                        </div>
                        <p>{item.description || '暂无说明'}</p>
                        <Progress percent={percentValue(item.score)} size="small" />
                        <div className="visual-status-row">
                          <span style={{ background: masteryColor(item.masteryStatus) === 'warning' ? '#ff8a3d' : '#315bff' }} />
                          {masteryText(item.masteryStatus)}
                        </div>
                      </Card>
                    ))}
                  </div>
                ) : (
                  <Empty description="暂无关联节点" image={Empty.PRESENTED_IMAGE_SIMPLE} />
                )
              ),
            },
            {
              key: 'charts',
              label: '能力趋势',
              children: (
                <div className="visual-chart-grid">
                  <GrowthTrendChart loading={loading} nodes={displayGraph.nodes} />
                  <AbilityRadarChart loading={loading} nodes={displayGraph.nodes} />
                </div>
              ),
            },
            {
              key: 'evidence',
              label: '成果证据',
              children: (
                <div className="visual-action-grid">
                  <button onClick={() => navigate('/project-training/materials')} type="button">
                    <strong>项目材料</strong>
                    <span>补充项目过程材料和交付物证据。</span>
                  </button>
                  <button onClick={() => navigate('/certificate-standard/results')} type="button">
                    <strong>证书成果</strong>
                    <span>上传或查看证书达标成果。</span>
                  </button>
                  <button onClick={() => navigate('/competition-growth/results')} type="button">
                    <strong>竞赛成果</strong>
                    <span>沉淀竞赛训练和获奖材料。</span>
                  </button>
                </div>
              ),
            },
          ]}
        />
      </AppCard>

      <Drawer
        extra={
          <Space>
            <Button onClick={() => navigate('/course-learning/resources')}>关联资源</Button>
            <Button onClick={() => navigate('/ai-learning-center/learning-path')} type="primary">
              继续提升
            </Button>
          </Space>
        }
        open={Boolean(activeNode)}
        size={520}
        title="阶段节点详情"
        onClose={() => setActiveNode(null)}
      >
        {activeNode ? (
          <Descriptions
            column={1}
            items={[
              { key: 'label', label: '节点名称', children: activeNode.label },
              { key: 'type', label: '节点类型', children: nodeTypeText(activeNode.nodeType) },
              { key: 'description', label: '说明', children: activeNode.description || '-' },
              { key: 'score', label: '完成度', children: `${percentValue(activeNode.score)}%` },
              {
                key: 'mastery',
                label: '状态',
                children: <Tag color={masteryColor(activeNode.masteryStatus)}>{masteryText(activeNode.masteryStatus)}</Tag>,
              },
            ]}
          />
        ) : null}
      </Drawer>
    </PageContainer>
  )
}

export default FusionGrowthPathPage
