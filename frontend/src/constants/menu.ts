import type { MenuGroupConfig, MenuPageConfig } from '../types/menu'

const anyProfileView = [
  'profile.view.self',
  'profile.view.assigned',
  'profile.view.major',
  'student_profile.view_major',
  'student_profile.view_assigned',
  'statistics.view_readonly',
]

const anyFusionView = ['fusion.graph.view.self', 'fusion.graph.view.assigned', 'statistics.view_readonly']

const page = (
  key: string,
  title: string,
  path: string,
  description: string,
  requiredAnyPermissions?: string[],
  statusItems?: string[],
): MenuPageConfig => ({
  key,
  title,
  path,
  description,
  requiredAnyPermissions,
  statusItems,
})

export const menuGroups: MenuGroupConfig[] = [
  {
    key: 'dashboard',
    title: '首页工作台',
    menuCode: 'menu.dashboard',
    description: '汇总当前用户的任务、提醒、资源和审核入口。',
    children: [
      page('dashboard-today', '今日概览', '/dashboard/today', '轻量展示关键指标、优先事项和学习路径进度。'),
      page('dashboard-reminders', '学习提醒', '/dashboard/reminders', '集中查看学习路径、课程进度和证书达标提醒。'),
      page('dashboard-resources', '推荐资源', '/dashboard/resources', '查看画像、岗位能力和学习路径推荐资源。'),
      page('dashboard-reviews', '待办审核', '/dashboard/reviews', '教师、专业负责人、竞赛管理员和企业导师的审核待办入口。'),
    ],
  },
  {
    key: 'learning-profile',
    title: '学习画像',
    menuCode: 'menu.learning_profile',
    description: '围绕学生画像构建、确认、查看、分析和更新追溯。',
    children: [
      page(
        'profile-chat',
        '对话式画像构建',
        '/learning-profile/chat',
        '通过自然语言对话收集学习基础、目标、偏好和短板。',
        ['profile.session.create.self', 'learning_profile.chat_build'],
      ),
      page('profile-dynamic', '动态画像', '/learning-profile/dynamic', '展示个人或负责范围内学生的最新学习画像。', anyProfileView),
      page('profile-dimensions', '画像维度分析', '/learning-profile/dimensions', '按知识基础、学习目标、认知风格、知识短板等维度分析画像。', anyProfileView),
      page('profile-logs', '画像更新记录', '/learning-profile/logs', '追溯画像确认、行为更新和证据来源。', anyProfileView),
    ],
  },
  {
    key: 'ai-learning-center',
    title: 'AI学习中心',
    menuCode: 'menu.ai_learning_center',
    description: '资源生成、学习路径、精准推荐、智能辅导和学习评估。',
    children: [
      page(
        'ai-resource-generation',
        '多智能体资源生成',
        '/ai-learning-center/resource-generation',
        '基于画像、目标岗位和知识短板生成学习资源包。',
        ['resource_package.generate.self', 'ai_resource.generate_self'],
      ),
      page('ai-learning-path', '学习路径规划', '/ai-learning-center/learning-path', '生成、查看和调整个性化学习路径。', [
        'learning_path.view_self',
        'learning_path.view.self',
      ]),
      page('ai-recommendations', '资源精准推送', '/ai-learning-center/recommendations', '查看系统为当前学习阶段推荐的资源。', [
        'resource_recommendation.view_self',
        'resource_recommendation.view.self',
      ]),
      page('ai-tutor', '智能辅导', '/ai-learning-center/tutor', '进入智能问答和学习辅导入口。', ['ai_tutor.chat_self']),
      page('ai-evaluation', '学习效果评估', '/ai-learning-center/evaluation', '查看或生成个人学习效果评估。', [
        'learning_effect.view_self',
        'learning_effect.view.self',
        'learning_effect.generate.self',
        'learning_effect.generate_self',
      ]),
    ],
  },
  {
    key: 'course-learning',
    title: '课程学习',
    menuCode: 'menu.course_learning',
    description: '课程图谱、资料、在线学习、练习和学习记录。',
    children: [
      page('course-graph', '课程图谱', '/course-learning/graph', '查看课程知识点和知识点关系图谱。'),
      page('course-resources', '课程资料', '/course-learning/resources', '查看、上传或维护课程资料。', [
        'course_resource.upload',
        'learning_record.create_self',
      ]),
      page('course-online', '在线学习', '/course-learning/online', '浏览课程内容并形成学习行为记录。', ['learning_record.create_self']),
      page('course-quiz', '答题练习', '/course-learning/quiz', '提交课程或证书练习题作答记录。', ['quiz.practice']),
      page('course-records', '学习记录', '/course-learning/records', '查看个人或负责范围内学生的学习记录。', [
        'learning_record.create_self',
        'learning_record.view_assigned',
      ]),
    ],
  },
  {
    key: 'job-ability',
    title: '岗位能力',
    menuCode: 'menu.job_ability',
    description: '岗位能力模型、岗位广场、简历生成、审核和投递记录。',
    children: [
      page('job-square', '职位广场', '/job-ability/square', '查看岗位能力模型、真实岗位和岗位审核状态。', [
        'job_role.manage.major',
        'job_post.review_major',
        'job_post.create',
        'job_application.submit',
      ]),
      page('job-resume', '我的简历', '/job-ability/resume', '查看学生简历或进入教师、企业侧简历审核列表。', [
        'resume.generate_ai',
        'resume.review_group',
        'resume.review_enterprise',
      ]),
      page('job-resume-ai', 'AI简历生成', '/job-ability/ai-resume', '基于画像和岗课赛证成果生成 AI 简历。', ['resume.generate_ai']),
      page('job-applications', '投递记录', '/job-ability/applications', '查看岗位投递、教师审核和企业审核流转。', [
        'job_application.submit',
        'resume.review_group',
        'resume.review_enterprise',
        'resume.recommend_company',
      ]),
      page('job-match', '岗位匹配度', '/job-ability/match', '展示岗位能力点与个人画像、课程、竞赛、证书的匹配情况。', [
        'fusion.graph.view.self',
        'fusion.graph.view.assigned',
        'job_capability.manage.major',
      ]),
    ],
  },
  {
    key: 'competition-growth',
    title: '竞赛成长',
    menuCode: 'menu.competition_growth',
    description: '竞赛发布、任务、成果上传、审核和荣誉展示。',
    children: [
      page('competition-tasks', '竞赛任务', '/competition-growth/tasks', '发布或查看竞赛任务、参赛要求和训练安排。', [
        'competition.publish',
        'competition_result.upload_coached',
      ]),
      page('competition-square', '竞赛广场', '/competition-growth/square', '浏览竞赛信息、报名方式和官方链接。'),
      page('competition-results', '我的竞赛成果', '/competition-growth/results', '学生或带队教师维护竞赛成果和荣誉材料。', [
        'competition_result.upload_coached',
      ]),
      page('competition-honor', '荣誉展示', '/competition-growth/honor', '展示审核通过的竞赛成果和荣誉。', [
        'competition.publish',
        'competition_result.review',
      ]),
    ],
  },
  {
    key: 'certificate-standard',
    title: '证书达标',
    menuCode: 'menu.certificate_standard',
    description: '证书标准、考证资料、题库和证书成果。',
    children: [
      page('certificate-standards', '证书标准', '/certificate-standard/standards', '查看、导入或维护本专业证书标准。', [
        'certificate_standard.import_major',
        'certificate_standard.manage_major',
        'certificate_result.upload_self',
      ]),
      page('certificate-materials', '考证资料', '/certificate-standard/materials', '查看证书学习资料和达标说明。'),
      page('certificate-quiz', '证书题库', '/certificate-standard/quiz', '进入证书专项练习和题库。', ['quiz.practice']),
      page('certificate-results', '我的证书成果', '/certificate-standard/results', '上传、查看或审核证书成果。', [
        'certificate_result.upload_self',
        'certificate_result.review_group',
      ]),
    ],
  },
  {
    key: 'project-training',
    title: '项目实训',
    menuCode: 'menu.project_training',
    description: '实训项目、实操案例、项目材料和项目评价。',
    children: [
      page('project-list', '实训项目', '/project-training/projects', '查看或创建项目实训任务。', [
        'project.manage.teacher',
      ]),
      page('project-cases', '实操案例', '/project-training/cases', '查看实践项目案例和代码材料。'),
      page('project-materials', '项目材料', '/project-training/materials', '维护项目材料、交付物和过程资料。', [
        'project.manage.teacher',
      ]),
      page('project-evaluation', '项目评价', '/project-training/evaluation', '查看或审核项目交付物评价。', [
        'project_deliverable.review.assigned',
      ]),
    ],
  },
  {
    key: 'fusion-graph',
    title: '融合图谱',
    menuCode: 'menu.fusion_graph',
    description: '串联岗位能力、课程知识点、竞赛任务和证书能力。',
    children: [
      page('fusion-map', '岗课赛证关联图谱', '/fusion-graph/map', '查看或维护岗课赛证关联图谱。', [
        ...anyFusionView,
        'fusion.relation.manage',
      ]),
      page('fusion-growth-path', '能力成长路径', '/fusion-graph/growth-path', '查看面向目标岗位的能力成长路径。', anyFusionView),
      page('fusion-weak-points', '知识短板定位', '/fusion-graph/weak-points', '定位个人或负责学生的知识短板。', anyFusionView),
      page('fusion-recommended-path', '推荐学习路径', '/fusion-graph/recommended-path', '基于图谱和画像展示推荐学习路径。', anyFusionView),
    ],
  },
  {
    key: 'statistics-analysis',
    title: '统计分析',
    menuCode: 'menu.statistics_analysis',
    description: '画像统计、专业分类、岗课赛证达成和学习效果统计。',
    children: [
      page('statistics-profile-export', '学生画像导出', '/statistics-analysis/profile-export', '创建和下载学生画像导出任务。', [
        'statistics.export_major',
        'statistics.view_readonly',
      ]),
      page('statistics-major', '专业分类统计', '/statistics-analysis/major', '按专业、班级和年级查看分类统计。', [
        'statistics.export_major',
        'statistics.view_readonly',
      ]),
      page('statistics-fusion', '岗课赛证达成统计', '/statistics-analysis/fusion', '汇总岗位、课程、竞赛、证书和项目实训达成情况。', [
        'statistics.export_major',
        'statistics.view_readonly',
      ]),
      page('statistics-learning-effect', '学习效果统计', '/statistics-analysis/learning-effect', '查看学习路径、练习、评估和成长报告统计。', [
        'statistics.export_major',
        'statistics.view_readonly',
      ]),
    ],
  },
  {
    key: 'system-management',
    title: '系统管理',
    description: '系统管理员专用的账号、角色、权限和基础数据入口。',
    requiredAnyPermissions: ['account.manage', 'role.manage', 'permission.manage', 'base_data.manage'],
    children: [
      page('system-users', '用户管理', '/system/users', '管理用户账号、账号启停和重置密码。', ['account.manage']),
      page('system-roles', '角色管理', '/system/roles', '管理角色和用户角色绑定。', ['role.manage']),
      page('system-permissions', '权限管理', '/system/permissions', '管理权限点和角色授权。', ['permission.manage']),
      page('system-base-data', '基础数据', '/system/base-data', '维护学院、专业、班级、教师和课程等基础数据。', [
        'base_data.manage',
      ]),
    ],
  },
]

export const allMenuPages = menuGroups.flatMap((group) =>
  group.children.map((child) => ({
    ...child,
    groupKey: group.key,
    groupTitle: group.title,
    groupMenuCode: group.menuCode,
  })),
)

export const defaultAuthedPath = '/dashboard/today'
