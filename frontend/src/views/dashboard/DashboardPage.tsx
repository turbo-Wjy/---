import { useEffect, useState } from 'react'
import { getDashboardOverviewApi } from '../../api/dashboard'
import AiAssistantPanel from '../../components/dashboard/AiAssistantPanel'
import DashboardHero from '../../components/dashboard/DashboardHero'
import LearningPath from '../../components/dashboard/LearningPath'
import MetricGrid from '../../components/dashboard/MetricGrid'
import RecommendPanel from '../../components/dashboard/RecommendPanel'
import ReminderPanel from '../../components/dashboard/ReminderPanel'
import TodayTasks from '../../components/dashboard/TodayTasks'
import type { DashboardOverview } from '../../types/dashboard'
import { emptyDashboardOverview } from '../../utils/dashboardDisplay'

const itemIdentity = (item: { itemType?: string; targetType?: string; targetId?: number; id?: number }) =>
  [item.targetType || item.itemType || '', item.targetId || item.id || ''].join(':')

const DashboardPage = () => {
  const [overview, setOverview] = useState<DashboardOverview>(emptyDashboardOverview)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    let active = true

    getDashboardOverviewApi()
      .then((data) => {
        if (active) {
          setOverview({
            ...emptyDashboardOverview,
            ...data,
            metrics: data.metrics || [],
            todayTasks: data.todayTasks || [],
            learningReminders: data.learningReminders || [],
            recommendedResources: data.recommendedResources || [],
            pendingReviews: data.pendingReviews || [],
          })
        }
      })
      .catch(() => {
        if (active) {
          setOverview(emptyDashboardOverview)
        }
      })
      .finally(() => {
        if (active) {
          setLoading(false)
        }
      })

    return () => {
      active = false
    }
  }, [])

  const actionItemKeys = new Set(
    (overview.dashboardType === 'staff' ? overview.pendingReviews : overview.todayTasks).map(itemIdentity),
  )
  const visibleReminders = overview.learningReminders.filter((item) => !actionItemKeys.has(itemIdentity(item)))

  return (
    <section className="dashboard-page">
      <div className="dashboard-grid">
        <main className="dashboard-main">
          <DashboardHero loading={loading} overview={overview} />
          <MetricGrid metrics={overview.metrics} />
          <div className="dashboard-two-col">
            <TodayTasks loading={loading} overview={overview} />
            <LearningPath loading={loading} progress={overview.learningPathProgress} />
          </div>
        </main>
        <aside className="dashboard-aside">
          <ReminderPanel loading={loading} reminders={visibleReminders} />
          <RecommendPanel loading={loading} resources={overview.recommendedResources} />
          <AiAssistantPanel />
        </aside>
      </div>
    </section>
  )
}

export default DashboardPage
