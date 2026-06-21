import { LockOutlined, UserOutlined } from '@ant-design/icons'
import { Button, Card, Form, Input, message } from 'antd'
import { useState } from 'react'
import { useLocation, useNavigate } from 'react-router-dom'
import { defaultAuthedPath } from '../constants/menu'
import { useAuth } from '../stores/auth'
import type { LoginRequest } from '../types/auth'

const LoginView = () => {
  const auth = useAuth()
  const navigate = useNavigate()
  const location = useLocation()
  const [submitting, setSubmitting] = useState(false)

  const submit = async (values: LoginRequest) => {
    setSubmitting(true)
    try {
      const response = await auth.login(values)
      message.success('登录成功')
      if (response.mustChangePassword) {
        navigate('/change-password', { replace: true })
        return
      }
      const params = new URLSearchParams(location.search)
      const redirect = params.get('redirect') || defaultAuthedPath
      navigate(redirect, { replace: true })
    } catch {
      // The HTTP interceptor already surfaces the concrete error message.
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <main className="login-page">
      <section className="login-panel">
        <div className="login-copy">
          <div className="brand-row">
            <div className="brand-mark brand-mark-large">AI</div>
            <div>
              <h1>AI岗课赛证学习平台</h1>
              <p>学习画像、多智能体资源、融合图谱和成长评价的一体化工作台。</p>
            </div>
          </div>
          <div className="feature-grid">
            <div>
              <strong>角色菜单</strong>
              <span>登录后自动按权限展示两级导航。</span>
            </div>
            <div>
              <strong>业务骨架</strong>
              <span>先完成页面占位，后续逐步接入接口。</span>
            </div>
          </div>
        </div>

        <Card className="login-card" title={<LoginHeader />} variant="outlined">
          <Form<LoginRequest> layout="vertical" onFinish={submit} requiredMark={false}>
            <Form.Item label="用户名" name="username" rules={[{ required: true, message: '请输入用户名' }]}>
              <Input autoComplete="username" placeholder="请输入用户名" prefix={<UserOutlined />} size="large" />
            </Form.Item>
            <Form.Item label="密码" name="password" rules={[{ required: true, message: '请输入密码' }]}>
              <Input.Password
                autoComplete="current-password"
                placeholder="请输入密码"
                prefix={<LockOutlined />}
                size="large"
              />
            </Form.Item>
            <Button block htmlType="submit" loading={submitting} size="large" type="primary">
              登录
            </Button>
          </Form>
        </Card>
      </section>
    </main>
  )
}

const LoginHeader = () => (
  <div>
    <h2>账号登录</h2>
    <p>请输入平台账号和密码</p>
  </div>
)

export default LoginView
