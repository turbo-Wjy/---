import { ArrowRightOutlined } from '@ant-design/icons'
import { Button, Progress, Steps } from 'antd'
import { useNavigate } from 'react-router-dom'
import type { LearningPathProgress } from '../../types/dashboard'
import { dashboardRoutes } from '../../utils/dashboardDrilldown'
import AppCard from '../ui/AppCard'
import SectionHeader from '../ui/SectionHeader'

interface LearningPathProps {
  progress?: LearningPathProgress | null
  loading?: boolean
}

const stepTitles = ['基础入门', '核心技能', '项目实战', '岗位提升', '能力认证']

const LearningPath = ({ progress, loading }: LearningPathProps) => {
  const navigate = useNavigate()
  const completed = progress?.completedSteps ?? 0
  const total = progress?.totalSteps || stepTitles.length
  const percent = progress?.progressPercent ?? 0
  const current = Math.min(Math.max(completed, 0), stepTitles.length - 1)

  return (
    <AppCard
      className="learning-path-card"
      loading={loading}
      title={
        <SectionHeader
          action={
            <Button
              icon={<ArrowRightOutlined />}
              iconPlacement="end"
              onClick={() => navigate(dashboardRoutes.learningPath)}
              type="link"
            >
              查看路径
            </Button>
          }
          description={progress?.pathTitle || '根据画像和岗位目标生成的学习路径'}
          title="学习路径进度"
        />
      }
    >
      <div className="path-summary">
        <div>
          <strong>{percent}%</strong>
          <span>
            已完成 {completed}/{total} 个阶段
          </span>
        </div>
        <Progress percent={percent} showInfo={false} strokeColor="#315bff" />
      </div>
      <Steps
        className="learning-path-steps"
        current={current}
        items={stepTitles.map((title, index) => ({
          title,
          description: index < completed ? '已完成' : index === completed ? '进行中' : '未开始',
          status: index < completed ? 'finish' : index === completed ? 'process' : 'wait',
        }))}
        responsive={false}
        size="small"
      />
    </AppCard>
  )
}

export default LearningPath
