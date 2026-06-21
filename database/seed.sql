USE ai_learning_platform;

-- =========================================================
-- 初始化数据说明
-- 1. 本脚本只写入非敏感配置。
-- 2. 不创建默认管理员账号，不写入明文密码、手机号、邮箱、身份证号。
-- 3. 默认管理员账号由部署脚本或后端初始化命令安全创建。
-- =========================================================

-- =========================================================
-- 1. 默认角色
-- =========================================================

INSERT INTO roles (code, name, data_scope, is_core, description, status)
VALUES
  ('admin', '系统管理员', 'platform', 1, '负责账号、权限、菜单、AI智能体配置、模型参数和系统日志。', 'active'),
  ('major_leader', '专业负责人', 'major', 1, '负责学生账号导入、岗位审核、证书标准导入、画像和分类统计导出。', 'active'),
  ('teacher', '教师', 'assigned_or_course', 1, '统一承载任课老师、小组负责教师、带队老师职责，通过职责标签和模块权限区分。', 'active'),
  ('competition_admin', '竞赛管理员', 'college_competition', 1, '负责竞赛发布、竞赛成果审核和荣誉展示。', 'active'),
  ('enterprise_mentor', '企业导师', 'enterprise', 1, '负责岗位发布、企业侧简历审核、提交或推荐简历。', 'active'),
  ('student', '学生', 'self', 1, '构建画像、生成资源、学习答题、上传成果、生成简历、岗位投递。', 'active'),
  ('data_viewer', '数据查看者', 'college_or_major_readonly', 0, '只读查看统计、画像分析和学习效果。', 'active')
ON DUPLICATE KEY UPDATE
  name = VALUES(name),
  data_scope = VALUES(data_scope),
  is_core = VALUES(is_core),
  description = VALUES(description),
  status = VALUES(status),
  updated_at = CURRENT_TIMESTAMP;

-- =========================================================
-- 2. 默认权限
-- =========================================================

INSERT INTO permissions (code, name, module, permission_type, description, status)
VALUES
  ('menu.dashboard', '首页工作台', 'dashboard', 'menu', '访问首页工作台菜单', 'active'),
  ('menu.learning_profile', '学习画像', 'learning_profile', 'menu', '访问学习画像菜单', 'active'),
  ('menu.ai_learning_center', 'AI学习中心', 'ai_learning_center', 'menu', '访问AI学习中心菜单', 'active'),
  ('menu.course_learning', '课程学习', 'course_learning', 'menu', '访问课程学习菜单', 'active'),
  ('menu.job_ability', '岗位能力', 'job_ability', 'menu', '访问岗位能力菜单', 'active'),
  ('menu.competition_growth', '竞赛成长', 'competition_growth', 'menu', '访问竞赛成长菜单', 'active'),
  ('menu.certificate_standard', '证书达标', 'certificate_standard', 'menu', '访问证书达标菜单', 'active'),
  ('menu.project_training', '项目实训', 'project_training', 'menu', '访问项目实训菜单', 'active'),
  ('menu.fusion_graph', '融合图谱', 'fusion_graph', 'menu', '访问融合图谱菜单', 'active'),
  ('menu.statistics_analysis', '统计分析', 'statistics_analysis', 'menu', '访问统计分析菜单', 'active'),

  ('account.manage', '账号管理', 'system', 'action', '管理用户账号、启停和重置密码', 'active'),
  ('role.manage', '角色管理', 'system', 'action', '管理角色和用户角色绑定', 'active'),
  ('permission.manage', '权限管理', 'system', 'action', '管理权限和角色授权', 'active'),
  ('base_data.manage', '基础数据管理', 'base_data', 'action', '管理学院、专业、班级等基础数据', 'active'),
  ('student.import_major', '本专业学生导入', 'student', 'action', '专业负责人导入本专业学生Excel并生成账号', 'active'),
  ('student_profile.view_major', '本专业学生画像查看', 'learning_profile', 'action', '查看本专业学生画像', 'active'),
  ('student_profile.view_assigned', '负责学生画像查看', 'learning_profile', 'action', '教师查看负责学生画像', 'active'),
  ('job_post.review_major', '专业负责人岗位审核', 'job_ability', 'action', '专业负责人审核企业导师发布的岗位', 'active'),
  ('certificate_standard.import_major', '本专业证书标准导入', 'certificate_standard', 'action', '专业负责人导入本专业证书标准', 'active'),
  ('certificate_standard.manage_major', '本专业证书标准维护', 'certificate_standard', 'action', '专业负责人维护本专业证书标准', 'active'),
  ('course_resource.upload', '课程资料上传', 'course_learning', 'action', '任课老师上传课程资料', 'active'),
  ('course_knowledge_point.manage', '课程知识点维护', 'course_learning', 'action', '任课老师维护课程知识点', 'active'),
  ('resume.review_group', '小组负责教师简历审核', 'job_ability', 'action', '小组负责教师审核学生简历', 'active'),
  ('competition.publish', '竞赛发布', 'competition_growth', 'action', '竞赛管理员发布竞赛信息', 'active'),
  ('competition_result.review', '竞赛成果审核', 'competition_growth', 'action', '竞赛管理员审核竞赛成果和荣誉', 'active'),
  ('competition_result.upload_coached', '带队竞赛成果上传', 'competition_growth', 'action', '带队老师上传竞赛成果和荣誉材料', 'active'),
  ('job_post.create', '企业岗位发布', 'job_ability', 'action', '企业导师发布岗位', 'active'),
  ('resume.review_enterprise', '企业侧简历审核', 'job_ability', 'action', '企业导师审核学生投递简历', 'active'),
  ('resume.recommend_company', '简历提交或推荐企业', 'job_ability', 'action', '企业导师提交或推荐简历给企业', 'active'),
  ('learning_profile.chat_build', '对话式画像构建', 'learning_profile', 'action', '学生通过自然语言对话构建画像', 'active'),
  ('ai_resource.generate_self', 'AI资源生成', 'ai_learning_center', 'action', '学生调用多智能体生成学习资源', 'active'),
  ('learning_path.view_self', '学习路径查看', 'ai_learning_center', 'action', '学生查看个性化学习路径', 'active'),
  ('learning_path.view.self', '学习路径查看', 'ai_learning_center', 'action', '学生查看个性化学习路径', 'active'),
  ('resource_recommendation.view_self', '资源精准推送查看', 'ai_learning_center', 'action', '学生查看系统推荐资源', 'active'),
  ('resource_recommendation.view.self', '资源精准推送查看', 'ai_learning_center', 'action', '学生查看系统推荐资源', 'active'),
  ('ai_tutor.chat_self', '智能辅导', 'ai_learning_center', 'action', '学生使用智能辅导问答', 'active'),
  ('learning_effect.generate.self', '学习效果评估生成', 'ai_learning_center', 'action', '学生基于学习行为、答题和路径进度生成个人学习效果评估', 'active'),
  ('learning_effect.generate_self', '学习效果评估生成', 'ai_learning_center', 'action', '学生基于学习行为、答题和路径进度生成个人学习效果评估', 'active'),
  ('learning_effect.view_self', '学习效果评估查看', 'ai_learning_center', 'action', '学生查看个人学习效果评估', 'active'),
  ('learning_effect.view.self', '学习效果评估查看', 'ai_learning_center', 'action', '学生查看个人学习效果评估', 'active'),
  ('learning_record.create_self', '个人学习记录创建', 'course_learning', 'action', '学生浏览、下载、学习课程资源时记录学习行为', 'active'),
  ('quiz.practice', '答题练习', 'course_learning', 'action', '学生提交课程或证书练习题作答记录', 'active'),
  ('resume.generate_ai', 'AI简历生成', 'job_ability', 'action', '学生基于画像和成果生成AI简历', 'active'),
  ('job_application.submit', '岗位投递', 'job_ability', 'action', '学生提交岗位投递', 'active'),
  ('certificate_result.upload_self', '学生证书成果上传', 'certificate_standard', 'action', '学生上传个人证书成果', 'active'),
  ('certificate_result.review_group', '小组负责教师证书成果审核', 'certificate_standard', 'action', '小组负责教师审核学生证书成果', 'active'),
  ('fusion.relation.manage', '岗课赛证融合关系维护', 'fusion_graph', 'action', '维护岗位能力、课程知识点、竞赛任务、证书考核点之间的融合关系', 'active'),
  ('fusion.graph.view.self', '个人融合图谱查看', 'fusion_graph', 'action', '学生查看自己的岗课赛证融合图谱', 'active'),
  ('fusion.graph.view.assigned', '负责学生融合图谱查看', 'fusion_graph', 'action', '教师或专业负责人查看负责范围内学生融合图谱', 'active'),
  ('job_role.manage.major', '本专业岗位能力模型维护', 'job_ability', 'action', '专业负责人维护本专业岗位能力模型', 'active'),
  ('job_capability.manage.major', '本专业岗位能力点维护', 'job_ability', 'action', '专业负责人维护本专业岗位能力点', 'active'),
  ('profile.session.create.self', '个人画像会话创建', 'learning_profile', 'action', '学生创建并使用对话式画像构建会话', 'active'),
  ('profile.confirm.self', '个人画像确认', 'learning_profile', 'action', '学生确认画像草稿并生成正式画像', 'active'),
  ('profile.view.self', '个人画像查看', 'learning_profile', 'action', '学生查看自己的学习画像', 'active'),
  ('profile.view.assigned', '负责学生画像查看', 'learning_profile', 'action', '教师查看负责学生的学习画像', 'active'),
  ('profile.view.major', '本专业学生画像查看', 'learning_profile', 'action', '专业负责人查看本专业学生学习画像', 'active'),
  ('resource_package.generate.self', '个人资源包生成', 'ai_learning_center', 'action', '学生基于画像、目标岗位和融合关系生成多智能体资源包', 'active'),
  ('resource_package.review.assigned', '负责学生资源包审核', 'ai_learning_center', 'action', '教师审核负责学生提交的资源包', 'active'),
  ('resource_package.publish.teacher', '教师发布资源包', 'ai_learning_center', 'action', '教师发布审核通过的资源包', 'active'),
  ('teacher_dashboard.view.assigned', '教师工作台查看', 'teacher_dashboard', 'action', '教师查看负责学生、班级短板、待审核事项和学习报告', 'active'),
  ('project.manage.teacher', '教师项目实训管理', 'project_training', 'action', '教师创建项目、维护项目交付物和实训过程', 'active'),
  ('project_deliverable.review.assigned', '项目交付物审核', 'project_training', 'action', '教师审核负责学生或项目组提交的项目交付物', 'active'),
  ('statistics.view_readonly', '统计只读查看', 'statistics_analysis', 'action', '只读查看统计分析', 'active'),
  ('statistics.export_major', '本专业统计导出', 'statistics_analysis', 'action', '专业负责人导出本专业画像与分类统计', 'active')
ON DUPLICATE KEY UPDATE
  name = VALUES(name),
  module = VALUES(module),
  permission_type = VALUES(permission_type),
  description = VALUES(description),
  status = VALUES(status),
  updated_at = CURRENT_TIMESTAMP;

-- =========================================================
-- 3. 角色授权
-- =========================================================

INSERT IGNORE INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
JOIN permissions p
WHERE r.code = 'admin';

INSERT IGNORE INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
JOIN permissions p ON p.code IN (
  'menu.dashboard',
  'menu.learning_profile',
  'menu.job_ability',
  'menu.fusion_graph',
  'menu.certificate_standard',
  'menu.statistics_analysis',
  'student.import_major',
  'student_profile.view_major',
  'profile.view.major',
  'fusion.relation.manage',
  'fusion.graph.view.assigned',
  'job_role.manage.major',
  'job_capability.manage.major',
  'job_post.review_major',
  'certificate_standard.import_major',
  'certificate_standard.manage_major',
  'statistics.export_major'
)
WHERE r.code = 'major_leader';

INSERT IGNORE INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
JOIN permissions p ON p.code IN (
  'menu.dashboard',
  'menu.learning_profile',
  'menu.course_learning',
  'menu.job_ability',
  'menu.competition_growth',
  'menu.project_training',
  'menu.fusion_graph',
  'student_profile.view_assigned',
  'profile.view.assigned',
  'fusion.graph.view.assigned',
  'course_resource.upload',
  'course_knowledge_point.manage',
  'resume.review_group',
  'certificate_result.review_group',
  'competition_result.upload_coached',
  'resource_package.review.assigned',
  'resource_package.publish.teacher',
  'teacher_dashboard.view.assigned',
  'project.manage.teacher',
  'project_deliverable.review.assigned'
)
WHERE r.code = 'teacher';

INSERT IGNORE INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
JOIN permissions p ON p.code IN (
  'menu.dashboard',
  'menu.competition_growth',
  'competition.publish',
  'competition_result.review'
)
WHERE r.code = 'competition_admin';

INSERT IGNORE INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
JOIN permissions p ON p.code IN (
  'menu.dashboard',
  'menu.job_ability',
  'job_post.create',
  'resume.review_enterprise',
  'resume.recommend_company'
)
WHERE r.code = 'enterprise_mentor';

INSERT IGNORE INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
JOIN permissions p ON p.code IN (
  'menu.dashboard',
  'menu.learning_profile',
  'menu.ai_learning_center',
  'menu.course_learning',
  'menu.job_ability',
  'menu.competition_growth',
  'menu.certificate_standard',
  'menu.project_training',
  'menu.fusion_graph',
  'learning_profile.chat_build',
  'profile.session.create.self',
  'profile.confirm.self',
  'profile.view.self',
  'fusion.graph.view.self',
  'ai_resource.generate_self',
  'resource_package.generate.self',
  'learning_path.view_self',
  'learning_path.view.self',
  'resource_recommendation.view_self',
  'resource_recommendation.view.self',
  'ai_tutor.chat_self',
  'learning_effect.generate.self',
  'learning_effect.generate_self',
  'learning_effect.view_self',
  'learning_effect.view.self',
  'learning_record.create_self',
  'quiz.practice',
  'resume.generate_ai',
  'job_application.submit',
  'certificate_result.upload_self'
)
WHERE r.code = 'student';

INSERT IGNORE INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
JOIN permissions p ON p.code IN (
  'menu.dashboard',
  'menu.learning_profile',
  'menu.fusion_graph',
  'menu.statistics_analysis',
  'profile.view.major',
  'fusion.graph.view.assigned',
  'statistics.view_readonly'
)
WHERE r.code = 'data_viewer';

-- =========================================================
-- 4. AI智能体初始化
-- =========================================================

INSERT INTO ai_agents (code, name, agent_type, model_name, config_json, enabled, status)
VALUES
  ('profile_builder_agent', '画像构建智能体', 'profile', 'default_llm', JSON_OBJECT(), 1, 'active'),
  ('resource_designer_agent', '资源设计智能体', 'resource_design', 'default_llm', JSON_OBJECT(), 1, 'active'),
  ('document_generator_agent', '文档生成智能体', 'resource_generation', 'default_llm', JSON_OBJECT(), 1, 'active'),
  ('ppt_generator_agent', 'PPT生成智能体', 'resource_generation', 'default_llm', JSON_OBJECT(), 1, 'active'),
  ('question_bank_agent', '题库生成智能体', 'resource_generation', 'default_llm', JSON_OBJECT(), 1, 'active'),
  ('mind_map_agent', '思维导图智能体', 'resource_generation', 'default_llm', JSON_OBJECT(), 1, 'active'),
  ('video_animation_script_agent', '视频/动画脚本智能体', 'resource_generation', 'default_llm', JSON_OBJECT(), 1, 'active'),
  ('practice_case_agent', '实操案例智能体', 'resource_generation', 'default_llm', JSON_OBJECT(), 1, 'active'),
  ('learning_path_agent', '学习路径规划智能体', 'learning_path', 'default_llm', JSON_OBJECT(), 1, 'active'),
  ('resource_recommendation_agent', '资源推荐智能体', 'recommendation', 'default_llm', JSON_OBJECT(), 1, 'active'),
  ('ai_tutor_agent', '智能辅导智能体', 'tutoring', 'default_llm', JSON_OBJECT(), 1, 'active'),
  ('learning_evaluation_agent', '学习评估智能体', 'evaluation', 'default_llm', JSON_OBJECT(), 1, 'active'),
  ('resume_generator_agent', '简历生成智能体', 'resume', 'default_llm', JSON_OBJECT(), 1, 'active'),
  ('statistics_analysis_agent', '统计分析智能体', 'statistics', 'default_llm', JSON_OBJECT(), 1, 'active')
ON DUPLICATE KEY UPDATE
  name = VALUES(name),
  agent_type = VALUES(agent_type),
  model_name = VALUES(model_name),
  config_json = VALUES(config_json),
  enabled = VALUES(enabled),
  status = VALUES(status),
  updated_at = CURRENT_TIMESTAMP;
