import type { ReactNode } from 'react'

type StatusTone = 'blue' | 'purple' | 'green' | 'orange' | 'red' | 'gray'

interface StatusTagProps {
  children: ReactNode
  tone?: StatusTone
}

const StatusTag = ({ children, tone = 'blue' }: StatusTagProps) => (
  <span className={`status-tag status-tag-${tone}`}>{children}</span>
)

export default StatusTag
