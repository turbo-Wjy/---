import { Button, Result } from 'antd'
import { useNavigate } from 'react-router-dom'
import { defaultAuthedPath } from '../../constants/menu'

const ForbiddenView = () => {
  const navigate = useNavigate()

  return (
    <Result
      extra={
        <Button type="primary" onClick={() => navigate(defaultAuthedPath, { replace: true })}>
          返回首页
        </Button>
      }
      status="403"
      subTitle="当前账号没有访问该页面的权限。"
      title="403"
    />
  )
}

export default ForbiddenView
