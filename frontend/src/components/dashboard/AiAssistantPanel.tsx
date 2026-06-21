import { RobotOutlined } from '@ant-design/icons'
import { Button } from 'antd'
import { useNavigate } from 'react-router-dom'
import robotImage from '../../assets/robot-standing-web.png'
import { dashboardRoutes } from '../../utils/dashboardDrilldown'
import AppCard from '../ui/AppCard'
import SectionHeader from '../ui/SectionHeader'

const AiAssistantPanel = () => {
  const navigate = useNavigate()

  return (
    <AppCard className="ai-panel" title={<SectionHeader icon={<RobotOutlined />} title="AI助手" />}>
      <div>
        <h3>有问题？问小智吧！</h3>
        <p>学习咨询、资源推荐、问题解答</p>
        <Button onClick={() => navigate(dashboardRoutes.aiTutor)} type="primary">
          去咨询
        </Button>
      </div>
      <span className="ai-panel-robot">
        <img alt="AI助手小智" src={robotImage} />
      </span>
    </AppCard>
  )
}

export default AiAssistantPanel
