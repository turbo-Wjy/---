import { CheckCircleOutlined, ClockCircleOutlined, LinkOutlined, SettingOutlined } from '@ant-design/icons'
import { Button, Card, Empty, Steps, Tag } from 'antd'
import { useMemo } from 'react'
import { useLocation, useNavigate } from 'react-router-dom'
import PageContainer from '../components/ui/PageContainer'
import { allMenuPages, defaultAuthedPath } from '../constants/menu'
import { useAuth } from '../stores/auth'

const PlaceholderView = () => {
  const auth = useAuth()
  const location = useLocation()
  const navigate = useNavigate()
  const page = useMemo(() => allMenuPages.find((item) => item.path === location.pathname), [location.pathname])
  const permissionTags = page?.requiredAnyPermissions || []
  const siblingPages = allMenuPages.filter((item) => item.groupKey === page?.groupKey && item.path !== page?.path)
  const nextPage = siblingPages[0]

  return (
    <PageContainer
      description={page?.description || '页面建设中。'}
      extra={
        <Button icon={<SettingOutlined />} onClick={() => navigate(nextPage?.path || defaultAuthedPath)} type="primary">
          {nextPage ? `进入${nextPage.title}` : '返回今日概览'}
        </Button>
      }
      title={page?.title || '页面占位'}
    >

      <div className="content-grid">
        <Card
          className="page-panel"
          title={
            <span className="card-title">
              <ClockCircleOutlined />
              当前状态
            </span>
          }
          variant="outlined"
        >
          <Empty description="业务数据接口将在后续版本接入">
            <Button onClick={() => navigate(defaultAuthedPath)} type="primary" variant="outlined">
              返回今日概览
            </Button>
          </Empty>
        </Card>

        <Card
          className="page-panel"
          title={
            <span className="card-title">
              <LinkOutlined />
              权限入口
            </span>
          }
          variant="outlined"
        >
          {permissionTags.length ? (
            <div className="permission-list">
              {permissionTags.map((permission) => (
                <div className="permission-row" key={permission}>
                  <span>{permission}</span>
                  <Tag color={auth.hasPermission(permission) ? 'success' : 'default'}>
                    {auth.hasPermission(permission) ? '已授权' : '未授权'}
                  </Tag>
                </div>
              ))}
            </div>
          ) : (
            <Empty description="该页面只需要所属一级菜单权限" />
          )}
        </Card>
      </div>

      <Card
        className="page-panel"
        title={
          <span className="card-title">
            <CheckCircleOutlined />
            后续接入建议
          </span>
        }
        variant="outlined"
      >
        <Steps
          current={1}
          items={[{ title: '页面入口' }, { title: '接口联调' }, { title: '表格表单' }, { title: '审核流转' }]}
          type="navigation"
        />
      </Card>
    </PageContainer>
  )
}

export default PlaceholderView
