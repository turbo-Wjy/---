import { Card } from 'antd'
import type { ComponentType } from 'react'
import ProgressBar from './ProgressBar'

interface MetricCardProps {
  title: string
  value: number | string
  unit?: string
  percent?: number
  color: string
  icon: ComponentType
}

const MetricCard = ({ title, value, unit, percent, color, icon: Icon }: MetricCardProps) => (
  <Card className="metric-card" size="small" variant="outlined">
    <div className="metric-card-top">
      <span className="metric-icon" style={{ color }}>
        <Icon />
      </span>
      <div>
        <span className="metric-title">{title}</span>
        <strong>
          {value}
          {unit ? <small>{unit}</small> : null}
        </strong>
      </div>
    </div>
    <ProgressBar color={color} percent={percent ?? (Number(value) || 0)} />
  </Card>
)

export default MetricCard
