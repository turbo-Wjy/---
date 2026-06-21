import { ArrowRightOutlined, BranchesOutlined, NodeIndexOutlined, PartitionOutlined, RadarChartOutlined } from '@ant-design/icons'
import { Button, Card, Descriptions, Drawer, Empty, Progress, Select, Space, Statistic, Table, Tabs, Tag } from 'antd'
import type { TableColumnsType } from 'antd'
import { useEffect, useMemo, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { getFusionRelationsApi, getJobRolesApi, getMyFusionGraphApi } from '../../api/fusion'
import AppCard from '../../components/ui/AppCard'
import PageContainer from '../../components/ui/PageContainer'
import SectionHeader from '../../components/ui/SectionHeader'
import { AbilityRadarChart, FusionRelationGraph, NodeTypeDonutChart } from '../../components/visualization/prelearningVisuals'
import {
  withDisplayFusionGraph,
  withDisplayFusionRelations,
  withDisplayJobRoles,
} from '../../mocks/prelearning'
import type { FusionGraph, FusionNode, FusionRelation, JobRole } from '../../types/fusion'
import {
  masteryColor,
  masteryText,
  nodeLabel,
  nodeTypeText,
  percentValue,
  relationText,
} from '../../utils/prelearningDisplay'

const FusionMapPage = () => {
  const navigate = useNavigate()
  const [roles, setRoles] = useState<JobRole[]>([])
  const [selectedRoleId, setSelectedRoleId] = useState<number | undefined>()
  const [graph, setGraph] = useState<FusionGraph | null>(null)
  const [relations, setRelations] = useState<FusionRelation[]>([])
  const [activeNode, setActiveNode] = useState<FusionNode | null>(null)
  const [activeEdge, setActiveEdge] = useState<FusionRelation | null>(null)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    let active = true
    const loadBase = async () => {
      setLoading(true)
      const [rolesResult, graphResult, relationsResult] = await Promise.allSettled([
        getJobRolesApi(),
        getMyFusionGraphApi(),
        getFusionRelationsApi(),
      ])
      if (!active) return
      setRoles(rolesResult.status === 'fulfilled' ? rolesResult.value.items || [] : [])
      setGraph(graphResult.status === 'fulfilled' ? graphResult.value : null)
      setRelations(relationsResult.status === 'fulfilled' ? relationsResult.value.items || [] : [])
      setLoading(false)
    }
    loadBase()
    return () => {
      active = false
    }
  }, [])

  const displayRoles = useMemo(() => withDisplayJobRoles(roles), [roles])
  const displayGraph = useMemo(() => withDisplayFusionGraph(graph), [graph])
  const displayRelations = useMemo(() => withDisplayFusionRelations(relations), [relations])
  const nodeMap = useMemo(() => new Map(displayGraph.nodes.map((node) => [node.nodeKey, nodeLabel(node)])), [displayGraph.nodes])

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

  const columns: TableColumnsType<FusionRelation> = [
    { title: '来源类型', dataIndex: 'sourceType', render: (value: string) => nodeTypeText(value) },
    { title: '目标类型', dataIndex: 'targetType', render: (value: string) => nodeTypeText(value) },
    { title: '关系', dataIndex: 'relationType', render: (value: string) => <Tag color="blue">{relationText(value)}</Tag> },
    { title: '权重', dataIndex: 'weight', render: (value: number) => <Progress percent={percentValue(value)} size="small" /> },
    { title: '说明', dataIndex: 'description', ellipsis: true },
  ]

  return (
    <PageContainer
      description="串联岗位、能力、课程、知识点、证书、竞赛和项目，给资源生成与学习路径提供结构化依据。"
      extra={
        <Space wrap>
          <Select
            allowClear
            className="prelearning-select"
            onChange={handleRoleChange}
            options={displayRoles.map((role) => ({ label: role.roleName, value: role.id }))}
            placeholder="按目标岗位查看"
            value={selectedRoleId}
          />
          <Button onClick={() => navigate('/fusion-graph/weak-points')}>查看短板</Button>
          <Button icon={<ArrowRightOutlined />} onClick={() => navigate('/ai-learning-center/resource-generation')} type="primary">
            生成资源
          </Button>
        </Space>
      }
      tags={displayGraph.displayOnly ? <Tag>展示补齐</Tag> : <Tag color="success">接口数据</Tag>}
      title="岗课赛证关联图谱"
    >
      <div className="profile-stat-grid">
        <Card loading={loading} variant="outlined">
          <Statistic prefix={<NodeIndexOutlined />} title="图谱节点" value={displayGraph.nodes.length} />
        </Card>
        <Card loading={loading} variant="outlined">
          <Statistic prefix={<BranchesOutlined />} title="图谱关系" value={displayGraph.edges.length} />
        </Card>
        <Card loading={loading} variant="outlined">
          <Statistic title="薄弱节点" value={displayGraph.weakPoints.length} />
        </Card>
        <Card loading={loading} variant="outlined">
          <Statistic prefix={<PartitionOutlined />} title="关系库" value={displayRelations.length} />
        </Card>
      </div>

      <div className="visual-page-grid">
        <AppCard
          className="visual-main-card"
          title={
            <SectionHeader
              icon={<PartitionOutlined />}
              title="岗课赛证关联网络"
              description="点击节点进入详情，点击连线查看关系说明和证据。"
            />
          }
        >
          <FusionRelationGraph
            graph={displayGraph}
            loading={loading}
            onEdgeClick={(edge) =>
              setActiveEdge({
                description: edge.description,
                evidence: edge.displayOnly ? '展示补齐关系，用于页面结构预览。' : undefined,
                relationType: edge.relationType,
                sourceId: edge.sourceId,
                sourceType: edge.sourceType,
                targetId: edge.targetId,
                targetType: edge.targetType,
                weight: edge.weight,
              })
            }
            onNodeClick={setActiveNode}
          />
        </AppCard>

        <AppCard className="visual-side-card" loading={loading} title={<SectionHeader title="推荐路径预览" />}>
          <div className="visual-step-list">
            {displayGraph.recommendedPath.map((item, index) => (
              <button key={`${item}-${index}`} onClick={() => navigate('/fusion-graph/growth-path')} type="button">
                <span>{index + 1}</span>
                <strong>{item}</strong>
              </button>
            ))}
          </div>
          <div className="visual-side-actions">
            <Button block onClick={() => navigate('/fusion-graph/growth-path')} type="primary">
              查看成长路径
            </Button>
            <Button block onClick={() => navigate('/fusion-graph/weak-points')}>定位知识短板</Button>
            <Button block onClick={() => navigate('/ai-learning-center/resource-generation')}>生成资源</Button>
          </div>
        </AppCard>
      </div>

      <AppCard title={<SectionHeader icon={<RadarChartOutlined />} title="图谱分析" />}>
        <Tabs
          items={[
            {
              key: 'relations',
              label: '关系明细',
              children: (
                <Table
                  columns={columns}
                  dataSource={displayRelations}
                  locale={{ emptyText: <Empty description="暂无融合关系" image={Empty.PRESENTED_IMAGE_SIMPLE} /> }}
                  pagination={false}
                  rowKey={(record) => String(record.id || `${record.sourceType}-${record.sourceId}-${record.targetType}-${record.targetId}`)}
                  scroll={{ x: 920 }}
                  size="small"
                />
              ),
            },
            {
              key: 'ability',
              label: '能力证据',
              children: (
                <div className="visual-chart-grid">
                  <AbilityRadarChart loading={loading} nodes={displayGraph.nodes} />
                  <NodeTypeDonutChart loading={loading} nodes={displayGraph.nodes} />
                </div>
              ),
            },
            {
              key: 'actions',
              label: '维护入口',
              children: (
                <div className="visual-action-grid">
                  <button onClick={() => navigate('/course-learning/graph')} type="button">
                    <strong>课程图谱</strong>
                    <span>查看课程与知识点对能力的支撑。</span>
                  </button>
                  <button onClick={() => navigate('/certificate-standard/standards')} type="button">
                    <strong>证书标准</strong>
                    <span>维护证书达标要求和能力映射。</span>
                  </button>
                  <button onClick={() => navigate('/project-training/projects')} type="button">
                    <strong>项目实训</strong>
                    <span>进入项目任务沉淀实践证据。</span>
                  </button>
                </div>
              ),
            },
          ]}
        />
      </AppCard>

      <Drawer
        extra={
          <Button onClick={() => navigate('/ai-learning-center/learning-path')} type="primary">
            用于路径规划
          </Button>
        }
        open={Boolean(activeNode)}
        size={520}
        title="图谱节点详情"
        onClose={() => setActiveNode(null)}
      >
        {activeNode ? (
          <Descriptions
            column={1}
            items={[
              { key: 'label', label: '节点名称', children: activeNode.label },
              { key: 'type', label: '节点类型', children: nodeTypeText(activeNode.nodeType) },
              { key: 'description', label: '说明', children: activeNode.description || '-' },
              { key: 'score', label: '掌握得分', children: `${percentValue(activeNode.score)}%` },
              {
                key: 'mastery',
                label: '掌握状态',
                children: <Tag color={masteryColor(activeNode.masteryStatus)}>{masteryText(activeNode.masteryStatus)}</Tag>,
              },
              { key: 'relationLabel', label: '图谱标签', children: nodeMap.get(activeNode.nodeKey) || activeNode.label },
            ]}
          />
        ) : null}
      </Drawer>

      <Drawer
        extra={
          <Button onClick={() => navigate('/ai-learning-center/learning-path')} type="primary">
            用于路径规划
          </Button>
        }
        open={Boolean(activeEdge)}
        size={520}
        title="关系详情"
        onClose={() => setActiveEdge(null)}
      >
        {activeEdge ? (
          <Descriptions
            column={1}
            items={[
              { key: 'source', label: '来源类型', children: nodeTypeText(activeEdge.sourceType) },
              { key: 'target', label: '目标类型', children: nodeTypeText(activeEdge.targetType) },
              { key: 'relation', label: '关系类型', children: <Tag color="blue">{relationText(activeEdge.relationType)}</Tag> },
              { key: 'weight', label: '关系权重', children: `${percentValue(activeEdge.weight)}%` },
              { key: 'description', label: '说明', children: activeEdge.description || '-' },
              { key: 'evidence', label: '证据', children: activeEdge.evidence || '-' },
            ]}
          />
        ) : null}
      </Drawer>
    </PageContainer>
  )
}

export default FusionMapPage
