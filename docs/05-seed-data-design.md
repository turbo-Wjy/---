# 初始化数据与权限种子设计

## 1. 设计目标

本设计用于说明系统首次部署时需要初始化的非敏感配置数据。对应 SQL 文件为：

- `database/seed.sql`

执行顺序：

1. 执行 `database/schema.sql` 创建数据库和表结构。
2. 执行 `database/seed.sql` 初始化角色、权限、角色授权和 AI 智能体。
3. 通过部署脚本或后端初始化命令创建默认管理员账号。

`seed.sql` 不写入明文密码、手机号、邮箱、身份证号等敏感信息。

## 2. 初始化范围

| 类型 | 是否写入 seed.sql | 说明 |
| --- | --- | --- |
| 默认角色 | 是 | 写入 `roles` |
| 默认权限 | 是 | 写入 `permissions` |
| 角色授权 | 是 | 写入 `role_permissions` |
| AI 智能体 | 是 | 写入 `ai_agents` |
| 教师职责标签 | 否 | 当前表为教师绑定表，不是全局字典表 |
| 画像维度 | 否 | 第一版作为后端系统常量维护 |
| 状态枚举 | 否 | 第一版作为后端系统常量维护 |
| 导出类型 | 否 | 第一版作为后端系统常量维护 |
| 默认管理员账号 | 否 | 由部署流程安全创建 |

## 3. 默认角色

| 编码 | 名称 | 数据范围 | 是否核心 |
| --- | --- | --- | --- |
| `admin` | 系统管理员 | `platform` | 是 |
| `major_leader` | 专业负责人 | `major` | 是 |
| `teacher` | 教师 | `assigned_or_course` | 是 |
| `competition_admin` | 竞赛管理员 | `college_competition` | 是 |
| `enterprise_mentor` | 企业导师 | `enterprise` | 是 |
| `student` | 学生 | `self` | 是 |
| `data_viewer` | 数据查看者 | `college_or_major_readonly` | 否 |

## 4. 默认权限

### 4.1 菜单权限

菜单作为 `permissions.permission_type = 'menu'` 管理。

| 权限编码 | 名称 | 模块 |
| --- | --- | --- |
| `menu.dashboard` | 首页工作台 | `dashboard` |
| `menu.learning_profile` | 学习画像 | `learning_profile` |
| `menu.ai_learning_center` | AI学习中心 | `ai_learning_center` |
| `menu.course_learning` | 课程学习 | `course_learning` |
| `menu.job_ability` | 岗位能力 | `job_ability` |
| `menu.competition_growth` | 竞赛成长 | `competition_growth` |
| `menu.certificate_standard` | 证书达标 | `certificate_standard` |
| `menu.project_training` | 项目实训 | `project_training` |
| `menu.fusion_graph` | 融合图谱 | `fusion_graph` |
| `menu.statistics_analysis` | 统计分析 | `statistics_analysis` |

### 4.2 核心操作权限

| 权限编码 | 名称 | 模块 |
| --- | --- | --- |
| `student.import_major` | 本专业学生导入 | `student` |
| `student_profile.view_major` | 本专业学生画像查看 | `learning_profile` |
| `student_profile.view_assigned` | 负责学生画像查看 | `learning_profile` |
| `job_post.review_major` | 专业负责人岗位审核 | `job_ability` |
| `certificate_standard.import_major` | 本专业证书标准导入 | `certificate_standard` |
| `course_resource.upload` | 课程资料上传 | `course_learning` |
| `course_knowledge_point.manage` | 课程知识点维护 | `course_learning` |
| `resume.review_group` | 小组负责教师简历审核 | `job_ability` |
| `competition.publish` | 竞赛发布 | `competition_growth` |
| `competition_result.review` | 竞赛成果审核 | `competition_growth` |
| `competition_result.upload_coached` | 带队竞赛成果上传 | `competition_growth` |
| `job_post.create` | 企业岗位发布 | `job_ability` |
| `resume.review_enterprise` | 企业侧简历审核 | `job_ability` |
| `resume.recommend_company` | 简历提交或推荐企业 | `job_ability` |
| `learning_profile.chat_build` | 对话式画像构建 | `learning_profile` |
| `ai_resource.generate_self` | AI资源生成 | `ai_learning_center` |
| `learning_path.view_self` | 学习路径查看 | `ai_learning_center` |
| `resource_recommendation.view_self` | 资源精准推送查看 | `ai_learning_center` |
| `ai_tutor.chat_self` | 智能辅导 | `ai_learning_center` |
| `learning_effect.view_self` | 学习效果评估查看 | `ai_learning_center` |
| `resume.generate_ai` | AI简历生成 | `job_ability` |
| `job_application.submit` | 岗位投递 | `job_ability` |
| `certificate_result.upload_self` | 学生证书成果上传 | `certificate_standard` |
| `certificate_result.review_group` | 小组负责教师证书成果审核 | `certificate_standard` |
| `fusion.relation.manage` | 岗课赛证融合关系维护 | `fusion_graph` |
| `fusion.graph.view.self` | 个人融合图谱查看 | `fusion_graph` |
| `fusion.graph.view.assigned` | 负责学生融合图谱查看 | `fusion_graph` |
| `job_role.manage.major` | 本专业岗位能力模型维护 | `job_ability` |
| `job_capability.manage.major` | 本专业岗位能力点维护 | `job_ability` |
| `profile.session.create.self` | 个人画像会话创建 | `learning_profile` |
| `profile.confirm.self` | 个人画像确认 | `learning_profile` |
| `profile.view.self` | 个人画像查看 | `learning_profile` |
| `profile.view.assigned` | 负责学生画像查看 | `learning_profile` |
| `profile.view.major` | 本专业学生画像查看 | `learning_profile` |
| `resource_package.generate.self` | 个人资源包生成 | `ai_learning_center` |
| `resource_package.review.assigned` | 负责学生资源包审核 | `ai_learning_center` |
| `resource_package.publish.teacher` | 教师发布资源包 | `ai_learning_center` |
| `teacher_dashboard.view.assigned` | 教师工作台查看 | `teacher_dashboard` |
| `project_deliverable.review.assigned` | 项目交付物审核 | `project_training` |
| `statistics.view_readonly` | 统计只读查看 | `statistics_analysis` |
| `statistics.export_major` | 本专业统计导出 | `statistics_analysis` |

## 5. 角色授权

| 角色 | 授权原则 |
| --- | --- |
| `admin` | 拥有全部权限 |
| `major_leader` | 学生导入、岗位审核、证书标准导入、岗位能力模型、融合关系、本专业画像查看、统计导出 |
| `teacher` | 课程资料上传、知识点维护、负责学生画像和融合图谱查看、资源包审核发布、证书成果审核、项目交付物审核、带队成果上传 |
| `competition_admin` | 竞赛发布、竞赛成果审核、竞赛菜单 |
| `enterprise_mentor` | 岗位发布、企业侧简历审核、企业推荐 |
| `student` | 画像会话、画像确认、个人画像和融合图谱查看、资源包生成、智能辅导、学习路径、AI简历、岗位投递、证书成果上传 |
| `data_viewer` | 统计、画像和融合图谱只读查看 |

## 6. AI 智能体初始化

写入 `ai_agents`，第一版统一：

- `model_name = 'default_llm'`
- `config_json = {}`
- `enabled = 1`

| 编码 | 名称 |
| --- | --- |
| `profile_builder_agent` | 画像构建智能体 |
| `resource_designer_agent` | 资源设计智能体 |
| `document_generator_agent` | 文档生成智能体 |
| `ppt_generator_agent` | PPT生成智能体 |
| `question_bank_agent` | 题库生成智能体 |
| `mind_map_agent` | 思维导图智能体 |
| `video_animation_script_agent` | 视频/动画脚本智能体 |
| `practice_case_agent` | 实操案例智能体 |
| `learning_path_agent` | 学习路径规划智能体 |
| `resource_recommendation_agent` | 资源推荐智能体 |
| `ai_tutor_agent` | 智能辅导智能体 |
| `learning_evaluation_agent` | 学习评估智能体 |
| `resume_generator_agent` | 简历生成智能体 |
| `statistics_analysis_agent` | 统计分析智能体 |

## 7. 系统常量

### 7.1 教师职责标签

当前 `teacher_duty_tags` 是教师与职责标签绑定表，因此不在 seed 中写入。后端分配教师职责时使用以下常量：

| 编码 | 名称 |
| --- | --- |
| `course_teacher` | 任课老师 |
| `group_teacher` | 小组负责教师 |
| `competition_coach` | 带队老师 |

### 7.2 画像维度

- `knowledge_foundation`
- `learning_goal`
- `cognitive_style`
- `knowledge_gap`
- `error_prone_points`
- `resource_preference`
- `practice_ability`
- `learning_progress`

### 7.3 导出类型

- `student_basic_info`
- `student_profile_summary`
- `profile_dimension_statistics`
- `job_ability_statistics`
- `course_learning_progress`
- `learning_effect_statistics`
- `competition_award_statistics`
- `certificate_completion_statistics`
- `project_training_statistics`
- `resume_application_statistics`

## 8. 安全要求

- `database/seed.sql` 不写入默认管理员账号。
- `database/seed.sql` 不写入明文密码。
- `database/seed.sql` 不写入手机号、邮箱、身份证号等真实敏感信息。
- 默认管理员账号由部署流程创建：
  - 用户名从环境变量读取。
  - 密码由部署时输入。
  - 应用层生成 BCrypt 或 Argon2 哈希后写入 `users.password_hash`。
  - `must_change_password = 1`。
- `database/seed.sql` 使用 `ON DUPLICATE KEY UPDATE`，允许重复执行。

## 9. 验收要点

- `roles` 初始化 7 个默认角色。
- `permissions` 初始化 10 个菜单权限和核心操作权限。
- `role_permissions` 完成各角色授权。
- `ai_agents` 初始化 14 个默认智能体。
- 重复执行 `database/seed.sql` 不产生重复角色、权限或智能体。
- `database/seed.sql` 不包含明文密码和真实敏感信息。
