import {
  ArrowRightOutlined,
  CheckCircleOutlined,
  MessageOutlined,
  PlusOutlined,
  SendOutlined,
  SyncOutlined,
} from '@ant-design/icons'
import { Button, Card, Empty, Form, Input, List, Progress, Space, Tag, message } from 'antd'
import { useEffect, useMemo, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import {
  addProfileSessionMessageApi,
  confirmLearningProfileApi,
  createProfileSessionApi,
  extractProfileSessionApi,
  getMyLearningProfileApi,
  getMyProfileEvidenceApi,
  getProfileSessionApi,
} from '../../api/profile'
import AppCard from '../../components/ui/AppCard'
import PageContainer from '../../components/ui/PageContainer'
import SectionHeader from '../../components/ui/SectionHeader'
import { withDisplayProfile, withDisplaySession } from '../../mocks/profile'
import type { LearningProfile, ProfileSession } from '../../types/profile'
import {
  confirmStatusColor,
  confirmStatusText,
  formatDateTime,
  percentValue,
  sessionTitle,
} from '../../utils/profileDisplay'

interface MessageFormValues {
  content: string
}

const ProfileChatPage = () => {
  const navigate = useNavigate()
  const [form] = Form.useForm<MessageFormValues>()
  const [profile, setProfile] = useState<LearningProfile | null>(null)
  const [session, setSession] = useState<ProfileSession | null>(null)
  const [loading, setLoading] = useState(true)
  const [submitting, setSubmitting] = useState(false)

  useEffect(() => {
    let active = true
    const load = async () => {
      setLoading(true)
      const [profileResult, evidenceResult] = await Promise.allSettled([
        getMyLearningProfileApi(),
        getMyProfileEvidenceApi(),
      ])
      if (!active) return

      if (profileResult.status === 'fulfilled') {
        setProfile(profileResult.value)
      }

      const latestSession =
        evidenceResult.status === 'fulfilled' ? evidenceResult.value.find((item) => Boolean(item.id)) : undefined
      if (latestSession?.id) {
        try {
          const detail = await getProfileSessionApi(latestSession.id)
          if (active) setSession(detail)
        } catch {
          if (active) setSession(latestSession)
        }
      }
      if (active) setLoading(false)
    }
    load()
    return () => {
      active = false
    }
  }, [])

  const displayProfile = useMemo(() => withDisplayProfile(profile), [profile])
  const displaySession = useMemo(() => withDisplaySession(session), [session])
  const dimensions = displaySession.dimensions || displayProfile.dimensions || []

  const ensureSession = async () => {
    if (session?.id) return session
    const created = await createProfileSessionApi({ sessionTitle: '学习画像构建' })
    setSession(created)
    return created
  }

  const handleCreateSession = async () => {
    setSubmitting(true)
    try {
      const created = await createProfileSessionApi({ sessionTitle: '学习画像构建' })
      setSession(created)
      message.success('已创建画像对话')
    } catch {
      message.error('画像对话暂时无法创建')
    } finally {
      setSubmitting(false)
    }
  }

  const handleSendMessage = async (values: MessageFormValues) => {
    setSubmitting(true)
    try {
      const current = await ensureSession()
      if (!current.id) return
      const nextSession = await addProfileSessionMessageApi(current.id, { content: values.content })
      setSession(nextSession)
      form.resetFields()
    } catch {
      message.error('消息发送失败，请稍后再试')
    } finally {
      setSubmitting(false)
    }
  }

  const handleExtract = async () => {
    setSubmitting(true)
    try {
      const current = await ensureSession()
      if (!current.id) return
      const extracted = await extractProfileSessionApi(current.id)
      setSession(extracted)
      message.success('已抽取画像草稿')
    } catch {
      message.error('画像草稿抽取失败')
    } finally {
      setSubmitting(false)
    }
  }

  const handleConfirm = async () => {
    if (!session?.id) {
      message.warning('请先创建并抽取画像草稿')
      return
    }
    setSubmitting(true)
    try {
      const confirmed = await confirmLearningProfileApi({ sessionId: session.id })
      setProfile(confirmed)
      const refreshed = await getProfileSessionApi(session.id)
      setSession(refreshed)
      message.success('画像已确认')
    } catch {
      message.error('画像确认失败，请先确认已抽取草稿')
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <PageContainer
      description="通过自然语言对话收集学习基础、目标、偏好和短板，抽取后由学生确认生成正式画像。"
      extra={
        <Space wrap>
          <Button icon={<ArrowRightOutlined />} onClick={() => navigate('/learning-profile/dynamic')}>
            查看动态画像
          </Button>
          <Button icon={<PlusOutlined />} loading={submitting} onClick={handleCreateSession} type="primary">
            新建画像对话
          </Button>
        </Space>
      }
      tags={<Tag color={confirmStatusColor(displaySession.confirmStatus)}>{confirmStatusText(displaySession.confirmStatus)}</Tag>}
      title="对话式画像构建"
    >
      <div className="profile-chat-grid">
        <AppCard
          className="profile-chat-card"
          loading={loading}
          title={<SectionHeader icon={<MessageOutlined />} title={sessionTitle(displaySession)} />}
        >
          <List
            className="profile-message-list"
            dataSource={displaySession.messages || []}
            locale={{ emptyText: <Empty description="暂无对话消息" image={Empty.PRESENTED_IMAGE_SIMPLE} /> }}
            renderItem={(item) => (
              <List.Item className={item.role === 'student' ? 'profile-message profile-message-self' : 'profile-message'}>
                <div>
                  <strong>{item.role === 'student' ? '我' : '画像构建智能体'}</strong>
                  <p>{item.content}</p>
                  <span>{formatDateTime(item.createdAt)}</span>
                </div>
              </List.Item>
            )}
          />

          <Form form={form} layout="vertical" onFinish={handleSendMessage}>
            <Form.Item name="content" rules={[{ required: true, message: '请输入要补充的画像信息' }]}>
              <Input.TextArea
                autoSize={{ minRows: 3, maxRows: 5 }}
                placeholder="描述你的学习基础、目标岗位、想提升的能力或当前遇到的困难..."
              />
            </Form.Item>
            <div className="profile-action-row">
              <Space wrap>
                <Button icon={<SyncOutlined />} loading={submitting} onClick={handleExtract}>
                  抽取画像草稿
                </Button>
                <Button icon={<CheckCircleOutlined />} loading={submitting} onClick={handleConfirm} type="primary">
                  确认画像
                </Button>
              </Space>
              <Button htmlType="submit" icon={<SendOutlined />} loading={submitting} type="primary">
                发送
              </Button>
            </div>
          </Form>
        </AppCard>

        <div className="profile-side-stack">
          <Card loading={loading} title="画像完整度" variant="outlined">
            <Progress percent={percentValue(displayProfile.completenessScore)} strokeColor="#315bff" />
            <p className="profile-muted">最近生成：{formatDateTime(displayProfile.lastGeneratedAt)}</p>
          </Card>
          <Card loading={loading} title="抽取维度预览" variant="outlined">
            <div className="profile-dimension-preview">
              {dimensions.slice(0, 5).map((item) => (
                <div key={item.code}>
                  <span>{item.name}</span>
                  <Progress percent={percentValue(item.confidence)} size="small" strokeColor="#315bff" />
                </div>
              ))}
            </div>
          </Card>
        </div>
      </div>
    </PageContainer>
  )
}

export default ProfileChatPage
