import { ArrowRightOutlined, BookOutlined, BranchesOutlined, NodeIndexOutlined, PieChartOutlined } from '@ant-design/icons'
import { Button, Card, Descriptions, Drawer, Empty, Progress, Select, Space, Statistic, Table, Tabs, Tag } from 'antd'
import type { TableColumnsType } from 'antd'
import { useEffect, useMemo, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { getCourseGraphApi, getCoursesApi } from '../../api/course'
import AppCard from '../../components/ui/AppCard'
import PageContainer from '../../components/ui/PageContainer'
import SectionHeader from '../../components/ui/SectionHeader'
import { CourseKnowledgeGraph, DifficultyDistributionChart } from '../../components/visualization/prelearningVisuals'
import { withDisplayCourseGraph, withDisplayCourses } from '../../mocks/prelearning'
import type { Course, CourseGraph, KnowledgePoint, KnowledgePointRelation } from '../../types/course'
import { difficultyColor, difficultyText, percentValue, pointLabel, relationText } from '../../utils/prelearningDisplay'

const CourseGraphPage = () => {
  const navigate = useNavigate()
  const [courses, setCourses] = useState<Course[]>([])
  const [selectedCourseId, setSelectedCourseId] = useState<number | undefined>()
  const [graph, setGraph] = useState<CourseGraph | null>(null)
  const [activePoint, setActivePoint] = useState<KnowledgePoint | null>(null)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    let active = true
    const loadCourses = async () => {
      setLoading(true)
      try {
        const result = await getCoursesApi()
        if (!active) return
        const nextCourses = result.items || []
        setCourses(nextCourses)
        setSelectedCourseId(nextCourses[0]?.id)
      } catch {
        if (!active) return
        setCourses([])
        setSelectedCourseId(undefined)
      } finally {
        if (active) setLoading(false)
      }
    }
    loadCourses()
    return () => {
      active = false
    }
  }, [])

  const displayCourses = useMemo(() => withDisplayCourses(courses), [courses])
  const selectedCourse = useMemo(
    () => displayCourses.find((item) => item.id === selectedCourseId) || displayCourses[0],
    [displayCourses, selectedCourseId],
  )

  useEffect(() => {
    let active = true
    const loadGraph = async () => {
      if (!selectedCourse?.id || selectedCourse.id < 0) {
        setGraph(null)
        return
      }
      setLoading(true)
      try {
        const data = await getCourseGraphApi(selectedCourse.id)
        if (active) setGraph(data)
      } catch {
        if (active) setGraph(null)
      } finally {
        if (active) setLoading(false)
      }
    }
    loadGraph()
    return () => {
      active = false
    }
  }, [selectedCourse?.id])

  const displayGraph = useMemo(() => withDisplayCourseGraph(graph), [graph])
  const pointMap = useMemo(
    () => new Map(displayGraph.nodes.map((item) => [item.id, pointLabel(item)])),
    [displayGraph.nodes],
  )

  const relationColumns: TableColumnsType<KnowledgePointRelation> = [
    {
      title: '来源知识点',
      dataIndex: 'sourceKnowledgePointId',
      render: (value: number) => pointMap.get(value) || value || '-',
    },
    {
      title: '目标知识点',
      dataIndex: 'targetKnowledgePointId',
      render: (value: number) => pointMap.get(value) || value || '-',
    },
    {
      title: '关系',
      dataIndex: 'relationType',
      width: 120,
      render: (value: string) => <Tag color="blue">{relationText(value)}</Tag>,
    },
    {
      title: '权重',
      dataIndex: 'weight',
      width: 120,
      render: (value: number) => <Progress percent={percentValue(value)} size="small" />,
    },
    {
      title: '说明',
      dataIndex: 'description',
      ellipsis: true,
    },
  ]

  return (
    <PageContainer
      description="查看课程知识点、知识点关系和课程对后续 AI 学习路径的支撑。"
      extra={
        <Space wrap>
          <Select
            className="prelearning-select"
            onChange={setSelectedCourseId}
            options={displayCourses.map((item) => ({ label: item.courseName, value: item.id }))}
            placeholder="选择课程"
            value={selectedCourse?.id}
          />
          <Button onClick={() => navigate('/fusion-graph/map')}>查看融合图谱</Button>
          <Button icon={<ArrowRightOutlined />} onClick={() => navigate('/ai-learning-center/learning-path')} type="primary">
            规划学习路径
          </Button>
        </Space>
      }
      tags={selectedCourse?.displayOnly ? <Tag>展示补齐</Tag> : <Tag color="success">接口数据</Tag>}
      title="课程图谱"
    >
      <div className="profile-stat-grid">
        <Card loading={loading} variant="outlined">
          <Statistic prefix={<BookOutlined />} title="课程数量" value={displayCourses.length} />
        </Card>
        <Card loading={loading} variant="outlined">
          <Statistic prefix={<NodeIndexOutlined />} title="知识点" value={displayGraph.nodes.length} />
        </Card>
        <Card loading={loading} variant="outlined">
          <Statistic prefix={<BranchesOutlined />} title="知识关系" value={displayGraph.edges.length} />
        </Card>
        <Card loading={loading} variant="outlined">
          <Statistic title="课程学分" value={selectedCourse?.credit || 0} />
        </Card>
      </div>

      <div className="visual-page-grid">
        <AppCard
          className="visual-main-card"
          title={
            <SectionHeader
              icon={<NodeIndexOutlined />}
              title="课程知识点网络"
              description="点击知识点查看说明，并下钻到学习或资源生成。"
            />
          }
        >
          <CourseKnowledgeGraph graph={displayGraph} loading={loading} onNodeClick={setActivePoint} />
        </AppCard>

        <AppCard className="visual-side-card" loading={loading} title={<SectionHeader title="课程信息" />}>
          <Descriptions
            column={1}
            items={[
              { key: 'name', label: '课程名称', children: selectedCourse?.courseName || '-' },
              { key: 'code', label: '课程编码', children: selectedCourse?.courseCode || '-' },
              { key: 'semester', label: '学期', children: selectedCourse?.semester || '-' },
              { key: 'status', label: '状态', children: selectedCourse?.status || '-' },
            ]}
          />
          <div className="visual-side-actions">
            <Button block onClick={() => navigate('/course-learning/online')} type="primary">
              开始学习
            </Button>
            <Button block onClick={() => navigate('/course-learning/resources')}>查看课程资料</Button>
            <Button block onClick={() => navigate('/course-learning/quiz')}>进入答题练习</Button>
          </div>
        </AppCard>
      </div>

      <AppCard title={<SectionHeader icon={<PieChartOutlined />} title="课程图谱明细" />}>
        <Tabs
          items={[
            {
              key: 'relations',
              label: '关系明细',
              children: (
                <Table
                  columns={relationColumns}
                  dataSource={displayGraph.edges}
                  locale={{ emptyText: <Empty description="暂无知识点关系" image={Empty.PRESENTED_IMAGE_SIMPLE} /> }}
                  pagination={false}
                  rowKey={(record) => String(record.id || `${record.sourceKnowledgePointId}-${record.targetKnowledgePointId}`)}
                  scroll={{ x: 860 }}
                  size="small"
                />
              ),
            },
            {
              key: 'difficulty',
              label: '难度分布',
              children: <DifficultyDistributionChart loading={loading} points={displayGraph.nodes} />,
            },
            {
              key: 'learning',
              label: '学习入口',
              children: (
                <div className="visual-action-grid">
                  <button onClick={() => navigate('/course-learning/resources')} type="button">
                    <strong>课程资料</strong>
                    <span>查看讲义、视频、案例和课程附件。</span>
                  </button>
                  <button onClick={() => navigate('/course-learning/online')} type="button">
                    <strong>在线学习</strong>
                    <span>进入课程内容并形成学习记录。</span>
                  </button>
                  <button onClick={() => navigate('/ai-learning-center/learning-path')} type="button">
                    <strong>规划路径</strong>
                    <span>把课程知识点接入个性化学习路径。</span>
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
            <Button onClick={() => navigate('/course-learning/online')}>开始学习</Button>
            <Button onClick={() => navigate('/ai-learning-center/resource-generation')} type="primary">
              生成学习资源
            </Button>
          </Space>
        }
        open={Boolean(activePoint)}
        size={520}
        title="知识点详情"
        onClose={() => setActivePoint(null)}
      >
        {activePoint ? (
          <Descriptions
            column={1}
            items={[
              { key: 'name', label: '知识点', children: activePoint.name },
              { key: 'description', label: '说明', children: activePoint.description || '-' },
              {
                key: 'difficulty',
                label: '难度',
                children: <Tag color={difficultyColor(activePoint.difficultyLevel)}>{difficultyText(activePoint.difficultyLevel)}</Tag>,
              },
              { key: 'status', label: '状态', children: activePoint.status || '-' },
            ]}
          />
        ) : null}
      </Drawer>
    </PageContainer>
  )
}

export default CourseGraphPage
