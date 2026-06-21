import { Button, Space, Tag } from 'antd'
import type { ReactNode } from 'react'
import { useMemo } from 'react'
import { useLocation, useNavigate } from 'react-router-dom'
import { allMenuPages, defaultAuthedPath } from '../../constants/menu'

interface PageContainerProps {
  title?: string
  description?: string
  tags?: ReactNode
  extra?: ReactNode
  children: ReactNode
}

const PageContainer = ({ title, description, tags, extra, children }: PageContainerProps) => {
  const location = useLocation()
  const navigate = useNavigate()
  const page = useMemo(() => allMenuPages.find((item) => item.path === location.pathname), [location.pathname])

  return (
    <section className="page-shell standard-page">
      <div className="page-heading page-panel">
        <div>
          <Space size={8} wrap>
            <Tag>{page?.groupTitle || '业务模块'}</Tag>
            {tags}
          </Space>
          <h1>{title || page?.title || '业务页面'}</h1>
          <p>{description || page?.description || '页面建设中。'}</p>
        </div>
        <div className="page-heading-extra">
          {extra || (
            <Button onClick={() => navigate(defaultAuthedPath)} type="primary">
              返回今日概览
            </Button>
          )}
        </div>
      </div>
      {children}
    </section>
  )
}

export default PageContainer
