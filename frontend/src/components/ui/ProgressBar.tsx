import { Progress } from 'antd'

interface ProgressBarProps {
  percent: number
  color: string
}

const ProgressBar = ({ percent, color }: ProgressBarProps) => (
  <Progress
    percent={percent}
    showInfo={false}
    size={['100%', 8]}
    strokeColor={color}
    railColor="#edf2fb"
  />
)

export default ProgressBar
