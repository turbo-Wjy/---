import { BellOutlined, DownOutlined, MessageOutlined, SearchOutlined } from '@ant-design/icons'
import { Avatar, Badge, Button, Dropdown, Input, type MenuProps } from 'antd'
import { useNavigate } from 'react-router-dom'
import { topbarActions, topbarSearchPlaceholder } from '../../mocks/dashboard'
import { useAuth } from '../../stores/auth'

const AppTopbar = () => {
  const auth = useAuth()
  const navigate = useNavigate()

  const dropdownItems: MenuProps['items'] = [
    { key: 'username', label: auth.user?.username || '当前用户', disabled: true },
    { type: 'divider' },
    { key: 'logout', label: '退出登录' },
  ]

  const handleLogout = async () => {
    await auth.logout()
    navigate('/login', { replace: true })
  }

  return (
    <div className="app-topbar">
      <Input
        aria-label="全局搜索"
        className="topbar-search"
        placeholder={topbarSearchPlaceholder}
        prefix={<SearchOutlined />}
      />
      <div className="header-actions">
        <Badge count={topbarActions.notificationCount} size="small">
          <Button
            aria-label="消息提醒"
            icon={<BellOutlined />}
            onClick={() => navigate('/dashboard/reminders')}
            shape="circle"
          />
        </Badge>
        <Button aria-label="站内消息" icon={<MessageOutlined />} onClick={() => navigate('/ai-learning-center/tutor')} shape="circle" />
        <Dropdown
          menu={{
            items: dropdownItems,
            onClick: ({ key }) => {
              if (key === 'logout') void handleLogout()
            },
          }}
          trigger={['click']}
        >
          <button className="user-trigger user-trigger-plain" type="button">
            <Avatar size={34}>李</Avatar>
            <DownOutlined />
          </button>
        </Dropdown>
      </div>
    </div>
  )
}

export default AppTopbar
