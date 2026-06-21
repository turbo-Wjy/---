import { ArrowRightOutlined } from '@ant-design/icons'
import { Button } from 'antd'
import { useNavigate } from 'react-router-dom'
import heroBanner from '../../assets/dashboard-hero-web.png'
import type { DashboardOverview } from '../../types/dashboard'
import { dashboardRoutes } from '../../utils/dashboardDrilldown'

interface DashboardHeroProps {
  overview: DashboardOverview
  loading?: boolean
}

const getTaskCount = (overview: DashboardOverview) =>
  overview.dashboardType === 'staff' ? overview.pendingReviews.length : overview.todayTasks.length

const DashboardHero = ({ overview, loading }: DashboardHeroProps) => {
  const navigate = useNavigate()
  const targetPath = overview.dashboardType === 'staff' ? dashboardRoutes.reviews : dashboardRoutes.courseOnline

  return (
    <section className="dashboard-hero">
      <img alt="" aria-hidden="true" className="hero-background" src={heroBanner} />
      <div className="hero-content">
        <h2>{loading ? '正在整理今日工作台' : `你好，${overview.greetingName || '同学'}`}</h2>
        <p>
          {overview.dashboardType === 'staff'
            ? `当前有 ${getTaskCount(overview)} 项事项需要处理，优先完成审核和指导任务。`
            : `今天有 ${getTaskCount(overview)} 项学习待办，继续推进你的学习路径。`}
        </p>
        <Button
          icon={<ArrowRightOutlined />}
          iconPlacement="end"
          onClick={() => navigate(targetPath)}
          shape="round"
          size="middle"
        >
          {overview.dashboardType === 'staff' ? '处理待办' : '继续学习'}
        </Button>
      </div>
    </section>
  )
}

export default DashboardHero
