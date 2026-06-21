import { LockOutlined } from '@ant-design/icons'
import { Button, Card, Form, Input, message } from 'antd'
import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { defaultAuthedPath } from '../constants/menu'
import { useAuth } from '../stores/auth'

interface ChangePasswordForm {
  newPassword: string
  confirmPassword: string
}

const ChangePasswordView = () => {
  const auth = useAuth()
  const navigate = useNavigate()
  const [submitting, setSubmitting] = useState(false)

  const submit = async (values: ChangePasswordForm) => {
    setSubmitting(true)
    try {
      await auth.changePassword(values.newPassword)
      message.success('密码修改成功')
      navigate(defaultAuthedPath, { replace: true })
    } catch {
      // The HTTP interceptor already surfaces the concrete error message.
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <main className="login-page">
      <Card className="password-card" title={<PasswordHeader />} variant="outlined">
        <Form<ChangePasswordForm> layout="vertical" onFinish={submit} requiredMark={false}>
          <Form.Item
            label="新密码"
            name="newPassword"
            rules={[
              { required: true, message: '请输入新密码' },
              { min: 8, message: '密码至少 8 位' },
            ]}
          >
            <Input.Password placeholder="请输入新密码" prefix={<LockOutlined />} size="large" />
          </Form.Item>
          <Form.Item
            dependencies={['newPassword']}
            label="确认密码"
            name="confirmPassword"
            rules={[
              { required: true, message: '请再次输入新密码' },
              ({ getFieldValue }) => ({
                validator(_, value) {
                  if (!value || getFieldValue('newPassword') === value) {
                    return Promise.resolve()
                  }
                  return Promise.reject(new Error('两次输入的密码不一致'))
                },
              }),
            ]}
          >
            <Input.Password placeholder="请再次输入新密码" prefix={<LockOutlined />} size="large" />
          </Form.Item>
          <Button block htmlType="submit" loading={submitting} size="large" type="primary">
            确认修改
          </Button>
        </Form>
      </Card>
    </main>
  )
}

const PasswordHeader = () => (
  <div>
    <h2>首次修改密码</h2>
    <p>为保障账号安全，请先设置新的登录密码。</p>
  </div>
)

export default ChangePasswordView
