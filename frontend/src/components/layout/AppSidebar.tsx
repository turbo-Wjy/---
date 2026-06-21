import {
  ApartmentOutlined,
  BarChartOutlined,
  BookOutlined,
  HomeFilled,
  ProjectOutlined,
  ReadOutlined,
  RobotOutlined,
  SafetyCertificateOutlined,
  SettingOutlined,
  TrophyOutlined,
  UserOutlined,
} from '@ant-design/icons'
import { Layout, Menu, type MenuProps } from 'antd'
import type { ReactNode } from 'react'
import { useMemo } from 'react'
import { useLocation, useNavigate } from 'react-router-dom'
import robotImage from '../../assets/robot-sidebar-transparent-web.png'
import { menuGroups } from '../../constants/menu'
import { sidebarAssistant } from '../../mocks/dashboard'
import { useAuth } from '../../stores/auth'
import type { MenuGroupConfig } from '../../types/menu'

const { Sider } = Layout

const iconMap: Record<string, ReactNode> = {
  dashboard: <HomeFilled />,
  'learning-profile': <UserOutlined />,
  'ai-learning-center': <RobotOutlined />,
  'course-learning': <ReadOutlined />,
  'job-ability': <ApartmentOutlined />,
  'competition-growth': <TrophyOutlined />,
  'certificate-standard': <SafetyCertificateOutlined />,
  'project-training': <ProjectOutlined />,
  'fusion-graph': <BookOutlined />,
  'statistics-analysis': <BarChartOutlined />,
  'system-management': <SettingOutlined />,
}

const getGroupIcon = (group: MenuGroupConfig) => iconMap[group.key] || <BookOutlined />

interface AppSidebarProps {
  collapsed: boolean
  onCollapse: (collapsed: boolean) => void
}

const AppSidebar = ({ collapsed, onCollapse }: AppSidebarProps) => {
  const auth = useAuth()
  const location = useLocation()
  const navigate = useNavigate()

  const visibleMenus = useMemo(
    () =>
      menuGroups
        .filter((group) => auth.hasMenu(group.menuCode, group.requiredAnyPermissions))
        .map((group) => ({
          ...group,
          children: group.menuCode
            ? group.children
            : group.children.filter((child) => auth.hasAnyPermission(child.requiredAnyPermissions)),
        }))
        .filter((group) => group.children.length > 0),
    [auth],
  )

  const menuItems = useMemo<MenuProps['items']>(
    () =>
      visibleMenus.map((group) => ({
        key: group.key,
        icon: getGroupIcon(group),
        label: group.title,
        title: group.title,
        children: group.children.map((child) => ({
          key: child.path,
          label: child.title,
          title: child.title,
        })),
      })),
    [visibleMenus],
  )

  return (
    <Sider
      className={collapsed ? 'app-sidebar app-sidebar-collapsed' : 'app-sidebar'}
      collapsed={collapsed}
      collapsedWidth={82}
      collapsible
      onCollapse={onCollapse}
      trigger={null}
      width={248}
      theme="dark"
    >
      <div className="brand brand-dark">
        <div className="brand-mark">AI</div>
        <div className="brand-copy">
          <div className="brand-title">AI职教智学工坊</div>
        </div>
      </div>

      <Menu
        className="side-menu side-menu-dark"
        defaultOpenKeys={visibleMenus.map((group) => group.key)}
        inlineCollapsed={collapsed}
        items={menuItems}
        mode="inline"
        onClick={({ key }) => navigate(key)}
        selectedKeys={[location.pathname]}
        theme="dark"
      />

      <div className="sidebar-assistant">
        {collapsed ? (
          <button aria-label={sidebarAssistant.title} className="assistant-entry assistant-entry-compact" type="button">
            <RobotOutlined />
          </button>
        ) : (
          <>
            <div className="sidebar-robot-frame">
              <img alt="AI助手小智" src={robotImage} />
            </div>
            <button className="assistant-entry" type="button">
              <span>
                <strong>{sidebarAssistant.title}</strong>
                <small>{sidebarAssistant.description}</small>
              </span>
              <span aria-hidden="true">›</span>
            </button>
          </>
        )}
      </div>
    </Sider>
  )
}

export default AppSidebar
