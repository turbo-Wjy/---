import type { LearningProfile, ProfileDimension, ProfileSession } from '../types/profile'

const now = new Date().toISOString()

export const profileDimensionTemplates: ProfileDimension[] = [
  {
    code: 'knowledge_base',
    name: '知识基础',
    value: '已掌握 Java 基础语法、数据库基础和前端页面开发流程，需要继续加强工程化协作。',
    confidence: 0.86,
    source: 'profile_session',
  },
  {
    code: 'learning_goal',
    name: '学习目标',
    value: '阶段目标是完成岗课赛证项目作品，形成可展示的课程成果、项目材料和简历素材。',
    confidence: 0.82,
    source: 'profile_session',
  },
  {
    code: 'cognitive_style',
    name: '认知风格',
    value: '偏好任务驱动和案例拆解，适合通过项目实训、代码案例和错题复盘推进学习。',
    confidence: 0.78,
    source: 'learning_record',
  },
  {
    code: 'weak_points',
    name: '知识短板',
    value: '数据结构、接口联调、组件状态拆分和测试验证仍需要专项强化。',
    confidence: 0.74,
    source: 'evaluation',
  },
  {
    code: 'mistake_patterns',
    name: '易错点',
    value: '容易忽略边界状态、权限范围和接口失败兜底，需要在开发前明确验收场景。',
    confidence: 0.72,
    source: 'quiz',
  },
  {
    code: 'resource_preference',
    name: '资源偏好',
    value: '更适合短讲义、流程图、练习题和实操案例组合，不宜一次性给过长材料。',
    confidence: 0.8,
    source: 'resource_feedback',
  },
  {
    code: 'practice_ability',
    name: '实践能力',
    value: '能独立完成页面搭建和接口调用，复杂模块需要任务拆解和阶段性验收。',
    confidence: 0.77,
    source: 'project',
  },
  {
    code: 'learning_progress',
    name: '学习进度',
    value: '当前处于画像完善和学习路径启动阶段，建议优先完成课程图谱和推荐资源学习。',
    confidence: 0.84,
    source: 'dashboard',
  },
]

export const mockLearningProfile: LearningProfile = {
  id: 0,
  studentId: 0,
  profileVersion: 1,
  profileSummary:
    '该学生具备一定编程和课程项目基础，适合围绕目标岗位建立阶段化学习路径。近期应重点补齐接口联调、数据结构、项目材料沉淀和简历证据链。',
  completenessScore: 82,
  lastGeneratedAt: now,
  dimensions: profileDimensionTemplates.map((item) => ({ ...item, displayOnly: true })),
  displayOnly: true,
}

export const mockProfileSession: ProfileSession = {
  sessionTitle: '学习画像构建预览',
  confirmStatus: 'extracted',
  confidenceScore: 0.82,
  draftProfile: mockLearningProfile.profileSummary,
  dimensions: mockLearningProfile.dimensions,
  messages: [
    {
      role: 'assistant',
      content: '我会通过几个问题了解你的学习基础、目标岗位、资源偏好和当前短板。',
      createdAt: now,
      displayOnly: true,
    },
    {
      role: 'student',
      content: '我想提升项目实战能力，也希望后面能生成一份更完整的 AI 简历。',
      createdAt: now,
      displayOnly: true,
    },
  ],
  createdAt: now,
  displayOnly: true,
}

export const mockProfileVersions: LearningProfile[] = [
  mockLearningProfile,
  {
    ...mockLearningProfile,
    id: -1,
    profileVersion: 0,
    completenessScore: 58,
    lastGeneratedAt: new Date(Date.now() - 1000 * 60 * 60 * 24 * 8).toISOString(),
    profileSummary: '初始画像已记录基础学习目标和资源偏好，后续需要通过学习行为继续完善。',
  },
]

export const mockProfileEvidence: ProfileSession[] = [
  mockProfileSession,
  {
    ...mockProfileSession,
    sessionTitle: '课程学习偏好补充',
    confirmStatus: 'confirmed',
    confidenceScore: 0.76,
    createdAt: new Date(Date.now() - 1000 * 60 * 60 * 24 * 3).toISOString(),
  },
]

export const fillProfileDimensions = (dimensions?: ProfileDimension[]) => {
  const realDimensions = dimensions || []
  const realCodes = new Set(realDimensions.map((item) => item.code))
  const fillers = profileDimensionTemplates
    .filter((item) => !realCodes.has(item.code))
    .map((item) => ({ ...item, displayOnly: true }))
  return [...realDimensions, ...fillers].slice(0, profileDimensionTemplates.length)
}

export const withDisplayProfile = (profile?: LearningProfile | null): LearningProfile => {
  if (!profile) return mockLearningProfile
  return {
    ...mockLearningProfile,
    ...profile,
    dimensions: fillProfileDimensions(profile.dimensions),
    displayOnly: profile.displayOnly,
  }
}

export const withDisplaySession = (session?: ProfileSession | null): ProfileSession => {
  if (!session) return mockProfileSession
  return {
    ...mockProfileSession,
    ...session,
    dimensions: fillProfileDimensions(session.dimensions),
    messages: session.messages?.length ? session.messages : mockProfileSession.messages,
    displayOnly: session.displayOnly,
  }
}

export const withDisplayVersions = (versions?: LearningProfile[]) => {
  if (!versions?.length) return mockProfileVersions
  return versions.map(withDisplayProfile)
}

export const withDisplayEvidence = (sessions?: ProfileSession[]) => {
  if (!sessions?.length) return mockProfileEvidence
  return sessions.map(withDisplaySession)
}
