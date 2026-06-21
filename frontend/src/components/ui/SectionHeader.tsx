import type { ReactNode } from 'react'

interface SectionHeaderProps {
  icon?: ReactNode
  title: string
  description?: string
  action?: ReactNode
}

const SectionHeader = ({ icon, title, description, action }: SectionHeaderProps) => (
  <div className="section-header">
    <div className="section-header-main">
      {icon ? <span className="section-header-icon">{icon}</span> : null}
      <div>
        <h2>{title}</h2>
        {description ? <p>{description}</p> : null}
      </div>
    </div>
    {action ? <div className="section-header-action">{action}</div> : null}
  </div>
)

export default SectionHeader
