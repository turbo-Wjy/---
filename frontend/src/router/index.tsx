import { Navigate, Outlet, createBrowserRouter, useLocation } from 'react-router-dom'
import { allMenuPages, defaultAuthedPath } from '../constants/menu'
import MainLayout from '../layouts/MainLayout'
import { useAuth } from '../stores/auth'
import ChangePasswordView from '../views/ChangePasswordView'
import ForbiddenView from '../views/errors/ForbiddenView'
import NotFoundView from '../views/errors/NotFoundView'
import LoginView from '../views/LoginView'
import PlaceholderView from '../views/PlaceholderView'
import DashboardPage from '../views/dashboard/DashboardPage'
import DashboardRemindersPage from '../views/dashboard/DashboardRemindersPage'
import DashboardResourcesPage from '../views/dashboard/DashboardResourcesPage'
import DashboardReviewsPage from '../views/dashboard/DashboardReviewsPage'
import CourseGraphPage from '../views/courseLearning/CourseGraphPage'
import FusionGrowthPathPage from '../views/fusionGraph/FusionGrowthPathPage'
import FusionMapPage from '../views/fusionGraph/FusionMapPage'
import FusionWeakPointsPage from '../views/fusionGraph/FusionWeakPointsPage'
import ProfileChatPage from '../views/learningProfile/ProfileChatPage'
import ProfileDimensionsPage from '../views/learningProfile/ProfileDimensionsPage'
import ProfileDynamicPage from '../views/learningProfile/ProfileDynamicPage'
import ProfileLogsPage from '../views/learningProfile/ProfileLogsPage'

const dashboardPageMap: Record<string, React.ReactNode> = {
  '/dashboard/today': <DashboardPage />,
  '/dashboard/reminders': <DashboardRemindersPage />,
  '/dashboard/resources': <DashboardResourcesPage />,
  '/dashboard/reviews': <DashboardReviewsPage />,
}

const businessPageMap: Record<string, React.ReactNode> = {
  ...dashboardPageMap,
  '/learning-profile/chat': <ProfileChatPage />,
  '/learning-profile/dynamic': <ProfileDynamicPage />,
  '/learning-profile/dimensions': <ProfileDimensionsPage />,
  '/learning-profile/logs': <ProfileLogsPage />,
  '/course-learning/graph': <CourseGraphPage />,
  '/fusion-graph/map': <FusionMapPage />,
  '/fusion-graph/growth-path': <FusionGrowthPathPage />,
  '/fusion-graph/weak-points': <FusionWeakPointsPage />,
}

const PublicOnly = () => {
  const auth = useAuth()
  return auth.isAuthed ? <Navigate to={defaultAuthedPath} replace /> : <Outlet />
}

const RequireAuth = () => {
  const auth = useAuth()
  const location = useLocation()

  if (!auth.isAuthed) {
    return <Navigate to={`/login?redirect=${encodeURIComponent(location.pathname + location.search)}`} replace />
  }
  if (auth.user?.mustChangePassword && location.pathname !== '/change-password') {
    return <Navigate to="/change-password" replace />
  }
  return <Outlet />
}

const RequirePageAccess = ({ pagePath }: { pagePath: string }) => {
  const auth = useAuth()
  const page = allMenuPages.find((item) => item.path === pagePath)

  if (!page) {
    return <NotFoundView />
  }
  if (!auth.hasMenu(page.groupMenuCode, page.requiredAnyPermissions)) {
    return <Navigate to="/403" replace />
  }
  if (businessPageMap[page.path]) {
    return businessPageMap[page.path]
  }
  return <PlaceholderView />
}

const pageRoutes = allMenuPages.map((page) => ({
  path: page.path.replace(/^\//, ''),
  element: <RequirePageAccess pagePath={page.path} />,
}))

export const router = createBrowserRouter([
  {
    element: <PublicOnly />,
    children: [
      {
        path: '/login',
        element: <LoginView />,
      },
    ],
  },
  {
    element: <RequireAuth />,
    children: [
      {
        path: '/change-password',
        element: <ChangePasswordView />,
      },
      {
        path: '/',
        element: <MainLayout />,
        children: [
          {
            index: true,
            element: <Navigate to={defaultAuthedPath} replace />,
          },
          ...pageRoutes,
          {
            path: '403',
            element: <ForbiddenView />,
          },
        ],
      },
    ],
  },
  {
    path: '*',
    element: <NotFoundView />,
  },
])
