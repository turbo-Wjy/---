import { Button, Result } from 'antd'
import { useNavigate } from 'react-router-dom'
import { defaultAuthedPath } from '../../constants/menu'

const NotFoundView = () => {
  const navigate = useNavigate()

  return (
    <Result
      extra={
        <Button type="primary" onClick={() => navigate(defaultAuthedPath, { replace: true })}>
          返回首页
        </Button>
      }
      status="404"
      subTitle="页面不存在或地址输入有误。"
      title="404"
    />
  )
}

export default NotFoundView
