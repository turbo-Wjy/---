import { Card, type CardProps } from 'antd'
import type { ReactNode } from 'react'

interface AppCardProps extends Omit<CardProps, 'title'> {
  title?: ReactNode
}

const AppCard = ({ className, title, children, ...props }: AppCardProps) => (
  <Card className={className} title={title} variant="outlined" {...props}>
    {children}
  </Card>
)

export default AppCard
