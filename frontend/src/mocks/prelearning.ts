import type { Course, CourseGraph, KnowledgePoint } from '../types/course'
import type { FusionGraph, FusionNode, FusionRelation, JobCapability, JobRole } from '../types/fusion'

export const mockCourses: Course[] = [
  {
    id: -101,
    courseCode: 'AI-WEB-01',
    courseName: 'AI 应用前端开发',
    credit: 3,
    semester: '2025-2026-2',
    status: 'active',
    displayOnly: true,
  },
  {
    id: -102,
    courseCode: 'DATA-STRUCT',
    courseName: '数据结构与算法基础',
    credit: 4,
    semester: '2025-2026-2',
    status: 'active',
    displayOnly: true,
  },
  {
    id: -103,
    courseCode: 'PROJECT-TRAIN',
    courseName: '岗课赛证项目实训',
    credit: 2,
    semester: '2025-2026-2',
    status: 'active',
    displayOnly: true,
  },
]

export const mockKnowledgePoints: KnowledgePoint[] = [
  {
    id: -1,
    courseId: -101,
    name: '组件化页面结构',
    description: '理解页面容器、业务卡片、详情抽屉和列表组件的职责拆分。',
    difficultyLevel: 'basic',
    sortOrder: 1,
    status: 'active',
    displayOnly: true,
  },
  {
    id: -2,
    courseId: -101,
    name: '接口数据适配',
    description: '掌握真实接口、展示补齐、错误兜底和业务动作之间的边界。',
    difficultyLevel: 'medium',
    sortOrder: 2,
    status: 'active',
    displayOnly: true,
  },
  {
    id: -3,
    courseId: -101,
    name: '权限与下钻',
    description: '按角色权限展示入口，并确保每个按钮都能跳转到可解释的业务目标。',
    difficultyLevel: 'medium',
    sortOrder: 3,
    status: 'active',
    displayOnly: true,
  },
  {
    id: -4,
    courseId: -101,
    name: '构建与验收',
    description: '使用类型检查、组件 lint 和构建结果验证前端页面质量。',
    difficultyLevel: 'advanced',
    sortOrder: 4,
    status: 'active',
    displayOnly: true,
  },
]

export const mockCourseGraph: CourseGraph = {
  nodes: mockKnowledgePoints,
  edges: [
    {
      id: -11,
      sourceKnowledgePointId: -1,
      targetKnowledgePointId: -2,
      relationType: 'prerequisite',
      weight: 0.86,
      description: '组件职责拆清楚后，才能稳定接入真实接口。',
      status: 'active',
      displayOnly: true,
    },
    {
      id: -12,
      sourceKnowledgePointId: -2,
      targetKnowledgePointId: -3,
      relationType: 'supports',
      weight: 0.8,
      description: '数据适配结果会影响权限入口和按钮下钻。',
      status: 'active',
      displayOnly: true,
    },
    {
      id: -13,
      sourceKnowledgePointId: -3,
      targetKnowledgePointId: -4,
      relationType: 'supports',
      weight: 0.78,
      description: '下钻链路完整后再做页面验收。',
      status: 'active',
      displayOnly: true,
    },
  ],
  displayOnly: true,
}

export const mockJobRoles: JobRole[] = [
  {
    id: -201,
    roleCode: 'ai_frontend_engineer',
    roleName: 'AI 应用前端开发工程师',
    description: '负责 AI 学习平台页面、组件、接口联调和数据可视化体验。',
    typicalTasks: '业务页面搭建、权限路由、接口适配、图谱展示、构建验收',
    abilityTags: 'React,AntD,TypeScript,接口联调,可视化',
    status: 'active',
    displayOnly: true,
  },
  {
    id: -202,
    roleCode: 'data_application_assistant',
    roleName: '数据应用开发助理',
    description: '围绕课程学习数据、画像数据和统计结果完成数据应用功能。',
    typicalTasks: '数据表格、指标看板、统计导出、接口查询',
    abilityTags: 'SQL,Java,数据分析,统计展示',
    status: 'active',
    displayOnly: true,
  },
]

export const mockJobCapabilities: JobCapability[] = [
  {
    id: -301,
    jobRoleId: -201,
    capabilityCode: 'component_modeling',
    capabilityName: '组件建模能力',
    description: '能把页面拆成容器、列表、图谱、详情和操作反馈组件。',
    level: 'core',
    weight: 0.9,
    status: 'active',
    displayOnly: true,
  },
  {
    id: -302,
    jobRoleId: -201,
    capabilityCode: 'api_integration',
    capabilityName: '接口联调能力',
    description: '能区分真实接口、展示补齐和业务提交动作。',
    level: 'core',
    weight: 0.88,
    status: 'active',
    displayOnly: true,
  },
  {
    id: -303,
    jobRoleId: -201,
    capabilityCode: 'visual_validation',
    capabilityName: '页面验收能力',
    description: '能通过类型检查、组件规范和构建结果验证页面质量。',
    level: 'advanced',
    weight: 0.74,
    status: 'active',
    displayOnly: true,
  },
]

export const mockFusionNodes: FusionNode[] = [
  {
    nodeKey: 'job:-201',
    nodeType: 'job_role',
    nodeId: -201,
    label: 'AI 应用前端开发工程师',
    description: '目标岗位',
    score: 0.72,
    masteryStatus: 'in_progress',
    displayOnly: true,
  },
  {
    nodeKey: 'capability:-301',
    nodeType: 'job_capability',
    nodeId: -301,
    label: '组件建模能力',
    description: '岗位核心能力',
    score: 0.78,
    masteryStatus: 'in_progress',
    displayOnly: true,
  },
  {
    nodeKey: 'course:-101',
    nodeType: 'course',
    nodeId: -101,
    label: 'AI 应用前端开发',
    description: '支撑课程',
    score: 0.68,
    masteryStatus: 'weak',
    displayOnly: true,
  },
  {
    nodeKey: 'knowledge:-2',
    nodeType: 'course_knowledge_point',
    nodeId: -2,
    label: '接口数据适配',
    description: '薄弱知识点',
    score: 0.58,
    masteryStatus: 'weak',
    displayOnly: true,
  },
  {
    nodeKey: 'project:-103',
    nodeType: 'project',
    nodeId: -103,
    label: '岗课赛证项目实训',
    description: '成果沉淀任务',
    score: 0.64,
    masteryStatus: 'in_progress',
    displayOnly: true,
  },
]

export const mockFusionGraph: FusionGraph = {
  nodes: mockFusionNodes,
  edges: [
    {
      sourceKey: 'job:-201',
      targetKey: 'capability:-301',
      sourceType: 'job_role',
      sourceId: -201,
      targetType: 'job_capability',
      targetId: -301,
      relationType: 'requires',
      weight: 0.92,
      description: '岗位要求具备组件建模能力。',
      displayOnly: true,
    },
    {
      sourceKey: 'capability:-301',
      targetKey: 'course:-101',
      sourceType: 'job_capability',
      sourceId: -301,
      targetType: 'course',
      targetId: -101,
      relationType: 'supported_by',
      weight: 0.84,
      description: '课程支撑岗位核心能力。',
      displayOnly: true,
    },
    {
      sourceKey: 'course:-101',
      targetKey: 'knowledge:-2',
      sourceType: 'course',
      sourceId: -101,
      targetType: 'course_knowledge_point',
      targetId: -2,
      relationType: 'contains',
      weight: 0.78,
      description: '接口数据适配是课程中的关键知识点。',
      displayOnly: true,
    },
    {
      sourceKey: 'knowledge:-2',
      targetKey: 'project:-103',
      sourceType: 'course_knowledge_point',
      sourceId: -2,
      targetType: 'project',
      targetId: -103,
      relationType: 'practice_with',
      weight: 0.76,
      description: '通过项目实训巩固接口适配。',
      displayOnly: true,
    },
  ],
  weakPoints: mockFusionNodes.filter((item) => item.masteryStatus === 'weak'),
  recommendedPath: ['组件建模能力', 'AI 应用前端开发', '接口数据适配', '岗课赛证项目实训'],
  displayOnly: true,
}

export const mockFusionRelations: FusionRelation[] = mockFusionGraph.edges.map((edge, index) => ({
  id: -401 - index,
  sourceType: edge.sourceType,
  sourceId: edge.sourceId,
  targetType: edge.targetType,
  targetId: edge.targetId,
  relationType: edge.relationType,
  weight: edge.weight,
  description: edge.description,
  evidence: '展示补齐关系，用于页面结构预览。',
  status: 'active',
  displayOnly: true,
}))

export const withDisplayCourses = (courses?: Course[]) => {
  if (!courses?.length) return mockCourses
  return courses
}

export const withDisplayCourseGraph = (graph?: CourseGraph | null): CourseGraph => {
  if (!graph) return mockCourseGraph
  return {
    nodes: graph.nodes?.length ? graph.nodes : mockCourseGraph.nodes,
    edges: graph.edges?.length ? graph.edges : mockCourseGraph.edges,
    displayOnly: graph.displayOnly,
  }
}

export const withDisplayJobRoles = (roles?: JobRole[]) => {
  if (!roles?.length) return mockJobRoles
  return roles
}

export const withDisplayJobCapabilities = (capabilities?: JobCapability[]) => {
  if (!capabilities?.length) return mockJobCapabilities
  return capabilities
}

export const withDisplayFusionGraph = (graph?: FusionGraph | null): FusionGraph => {
  if (!graph) return mockFusionGraph
  const nodes = graph.nodes?.length ? graph.nodes : mockFusionGraph.nodes
  return {
    nodes,
    edges: graph.edges?.length ? graph.edges : mockFusionGraph.edges,
    weakPoints: graph.weakPoints?.length ? graph.weakPoints : nodes.filter((item) => item.masteryStatus === 'weak'),
    recommendedPath: graph.recommendedPath?.length ? graph.recommendedPath : mockFusionGraph.recommendedPath,
    displayOnly: graph.displayOnly,
  }
}

export const withDisplayFusionRelations = (relations?: FusionRelation[]) => {
  if (!relations?.length) return mockFusionRelations
  return relations
}
