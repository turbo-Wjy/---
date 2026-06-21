import { MenuFoldOutlined, MenuUnfoldOutlined } from '@ant-design/icons'
import { Breadcrumb, Button, Layout } from 'antd'
import { useMemo, useState } from 'react'
import { Outlet, useLocation } from 'react-router-dom'
import AppSidebar from '../components/layout/AppSidebar'
import AppTopbar from '../components/layout/AppTopbar'
import { allMenuPages } from '../constants/menu'

const { Header, Content } = Layout

const MainLayout = () => {
  const location = useLocation()
  const [sidebarCollapsed, setSidebarCollapsed] = useState(false)

  const activePage = useMemo(
    () => allMenuPages.find((page) => page.path === location.pathname),
    [location.pathname],
  )

  const breadcrumbItems = activePage
    ? [{ title: activePage.groupTitle }, { title: activePage.title }]
    : [{ title: location.pathname === '/403' ? '无权限' : '工作台' }]

  return (
    <Layout className="app-layout">
      <AppSidebar collapsed={sidebarCollapsed} onCollapse={setSidebarCollapsed} />

      <Layout className="app-content-layout">
        <Header className="app-header">
          <div className="header-breadcrumb-group">
            <Button
              aria-label={sidebarCollapsed ? '展开导航菜单' : '收起导航菜单'}
              className="sidebar-collapse-trigger"
              icon={sidebarCollapsed ? <MenuUnfoldOutlined /> : <MenuFoldOutlined />}
              onClick={() => setSidebarCollapsed((current) => !current)}
              shape="circle"
            />
            <Breadcrumb items={breadcrumbItems} />
          </div>
          <AppTopbar />
        </Header>

        <Content className="app-main">
          <Outlet />
        </Content>
      </Layout>
    </Layout>
  )
}

export default MainLayout
