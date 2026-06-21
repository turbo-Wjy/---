# 融合版后端接口设计说明书

## 1. 设计目标

本接口设计将基础管理、岗课赛证融合关系和 AI 智能学习能力统一到一套后端 API 中。第一版重点服务软件杯核心演示：

学生登录 → 对话构建画像 → 选择目标岗位能力 → 生成岗课赛证融合图谱 → 生成学习路径 → 多智能体生成资源包 → 学习 / 答题 / 项目实训 → 学习效果评估 → 教师审核 → 成长报告导出。

真实企业招聘、AI 简历和岗位投递保留为二期扩展，不作为第一版主线。

## 2. 分层结构

| 层级 | 作用 | 典型模块 |
| --- | --- | --- |
| 基础管理层 | 管理系统基础数据和组织权限 | 用户、角色、学生、教师、专业、班级、课程、竞赛、证书、项目 |
| 融合能力层 | 建立岗课赛证之间的能力映射 | 岗位能力、课程知识点、竞赛任务、证书能力单元、融合关系 |
| 智能学习层 | 支撑个性化学习闭环 | 画像会话、学习路径、资源包、智能辅导、学习评估、教师审核、统计导出 |

## 3. 统一接口规范

### 3.1 基础规范

- 接口前缀：`/api/v1`
- 鉴权方式：`Authorization: Bearer <token>`
- 删除策略：软删除，更新 `deleted_at`
- 写操作：记录 `operation_logs`
- 审核操作：记录 `review_records`
- 导出操作：记录 `export_records`
- 敏感信息：默认脱敏返回

### 3.2 统一响应结构

```json
{
  "code": 0,
  "message": "success",
  "data": {},
  "traceId": "uuid"
}
```

### 3.3 分页结构

```json
{
  "items": [],
  "page": 1,
  "pageSize": 20,
  "total": 100
}
```

### 3.4 基础 CRUD 规则

核心资源默认提供：

| 方法 | 路径 | 说明 |
| --- | --- | --- |
| GET | `/{resources}` | 列表 |
| GET | `/{resources}/{id}` | 详情 |
| POST | `/{resources}` | 新增 |
| PUT | `/{resources}/{id}` | 修改 |
| DELETE | `/{resources}/{id}` | 软删除 |

管理端列表默认支持：`page`、`pageSize`、`keyword`、`status`。

## 4. 基础管理层接口

### 4.1 认证与当前用户

| 方法 | 路径 | 权限 | 说明 |
| --- | --- | --- | --- |
| POST | `/auth/login` | public | 登录 |
| POST | `/auth/logout` | login | 退出 |
| POST | `/auth/change-password` | login | 修改密码 |
| POST | `/auth/force-change-password` | login | 首次登录强制改密 |
| GET | `/auth/me` | login | 当前用户信息 |
| GET | `/auth/me/menus` | login | 当前用户菜单 |
| GET | `/auth/me/permissions` | login | 当前用户权限 |

### 4.2 用户、角色、权限

| 方法 | 路径 | 权限 | 说明 |
| --- | --- | --- | --- |
| GET | `/users` | `account.manage` | 用户列表 |
| GET | `/users/{id}` | `account.manage` | 用户详情 |
| POST | `/users` | `account.manage` | 新增用户 |
| POST | `/users/bootstrap-admin` | public / bootstrap only | 初始化可登录管理员，仅无可用管理员时开放 |
| PUT | `/users/{id}` | `account.manage` | 修改用户 |
| DELETE | `/users/{id}` | `account.manage` | 停用用户 |
| POST | `/users/{id}/reset-password` | `account.manage` | 重置密码 |
| PUT | `/users/{id}/roles` | `role.manage` | 绑定角色 |
| GET | `/roles` | `role.manage` | 角色列表 |
| POST | `/roles` | `role.manage` | 新增角色 |
| PUT | `/roles/{id}` | `role.manage` | 修改角色 |
| DELETE | `/roles/{id}` | `role.manage` | 删除角色 |
| GET | `/permissions` | `permission.manage` | 权限列表 |
| PUT | `/roles/{id}/permissions` | `permission.manage` | 角色授权 |

### 4.3 学院、专业、班级

| 方法 | 路径 | 权限 | 说明 |
| --- | --- | --- | --- |
| GET | `/colleges` | login | 学院列表 |
| POST | `/colleges` | `base_data.manage` | 新增学院 |
| PUT | `/colleges/{id}` | `base_data.manage` | 修改学院 |
| DELETE | `/colleges/{id}` | `base_data.manage` | 删除学院 |
| GET | `/majors` | login | 专业列表 |
| POST | `/majors` | `base_data.manage` | 新增专业 |
| PUT | `/majors/{id}` | `base_data.manage` | 修改专业 |
| DELETE | `/majors/{id}` | `base_data.manage` | 删除专业 |
| GET | `/classes` | login | 班级列表 |
| POST | `/classes` | `base_data.manage` | 新增班级 |
| PUT | `/classes/{id}` | `base_data.manage` | 修改班级 |
| DELETE | `/classes/{id}` | `base_data.manage` | 删除班级 |

### 4.4 学生与导入

| 方法 | 路径 | 权限 | 说明 |
| --- | --- | --- | --- |
| GET | `/students` | `student.view.major` / `student.view.assigned` | 学生列表 |
| GET | `/students/{id}` | `student.view.major` / `student.view.assigned` | 学生详情 |
| POST | `/students` | `student.import.major` | 新增学生 |
| PUT | `/students/{id}` | `student.import.major` | 修改学生基础信息 |
| DELETE | `/students/{id}` | `student.import.major` | 停用学生账号 |
| POST | `/students/import/preview` | `student.import.major` | 上传 Excel 并预校验 |
| POST | `/students/import/confirm` | `student.import.major` | 确认导入并生成账号 |
| GET | `/students/import/{batchId}` | `student.import.major` | 查看导入结果 |

### 4.5 教师与分组

| 方法 | 路径 | 权限 | 说明 |
| --- | --- | --- | --- |
| GET | `/teachers` | `base_data.manage` | 教师列表 |
| POST | `/teachers` | `base_data.manage` | 新增教师 |
| PUT | `/teachers/{id}` | `base_data.manage` | 修改教师 |
| DELETE | `/teachers/{id}` | `base_data.manage` | 删除教师 |
| PUT | `/teachers/{id}/duty-tags` | `base_data.manage` | 设置教师职责标签 |
| GET | `/teacher-student-groups` | `student.view.assigned` | 学生分组列表 |
| POST | `/teacher-student-groups` | `base_data.manage` | 创建学生分组 |
| PUT | `/teacher-student-groups/{id}` | `base_data.manage` | 修改学生分组 |
| DELETE | `/teacher-student-groups/{id}` | `base_data.manage` | 删除学生分组 |

### 4.6 基础课程、竞赛、证书、项目

| 方法 | 路径 | 权限 | 说明 |
| --- | --- | --- | --- |
| GET/POST/PUT/DELETE | `/courses` | login / `base_data.manage` | 课程 CRUD |
| GET/POST/PUT/DELETE | `/course-resources` | login / `course_resource.upload` | 课程资料 CRUD |
| GET/POST/PUT/DELETE | `/competitions` | login / `competition.publish` | 竞赛基础信息 CRUD |
| GET/POST/PUT/DELETE | `/certificates` | login / `certificate_standard.manage_major` | 证书标准 CRUD |
| GET/POST/PUT/DELETE | `/projects` | login / `project.manage.teacher` | 项目基础信息 CRUD |

## 5. 融合能力层接口

### 5.1 岗位能力模型

| 方法 | 路径 | 权限 | 说明 |
| --- | --- | --- | --- |
| GET | `/job-roles` | login | 岗位能力模型列表 |
| GET | `/job-roles/{id}` | login | 岗位能力模型详情 |
| POST | `/job-roles` | `job_role.manage.major` | 新增岗位能力模型 |
| PUT | `/job-roles/{id}` | `job_role.manage.major` | 修改岗位能力模型 |
| DELETE | `/job-roles/{id}` | `job_role.manage.major` | 删除岗位能力模型 |
| GET | `/job-roles/{id}/capabilities` | login | 岗位能力点列表 |
| POST | `/job-roles/{id}/capabilities` | `job_capability.manage.major` | 新增岗位能力点 |
| PUT | `/job-capabilities/{id}` | `job_capability.manage.major` | 修改岗位能力点 |
| DELETE | `/job-capabilities/{id}` | `job_capability.manage.major` | 删除岗位能力点 |

### 5.2 课程知识图谱

| 方法 | 路径 | 权限 | 说明 |
| --- | --- | --- | --- |
| GET | `/courses/{id}/knowledge-points` | login | 课程知识点列表 |
| POST | `/courses/{id}/knowledge-points` | `course_knowledge_point.manage` | 新增知识点 |
| PUT | `/knowledge-points/{id}` | `course_knowledge_point.manage` | 修改知识点 |
| DELETE | `/knowledge-points/{id}` | `course_knowledge_point.manage` | 删除知识点 |
| GET | `/knowledge-points/{id}/relations` | login | 知识点关系 |
| POST | `/knowledge-point-relations` | `course_knowledge_point.manage` | 新增知识点关系 |
| DELETE | `/knowledge-point-relations/{id}` | `course_knowledge_point.manage` | 删除知识点关系 |
| GET | `/courses/{id}/graph` | login | 课程知识图谱 |
| GET | `/courses/{id}/progress/me` | `learning_record.create_self` | 我的课程进度 |

### 5.3 竞赛任务与证书能力

| 方法 | 路径 | 权限 | 说明 |
| --- | --- | --- | --- |
| GET/POST/PUT/DELETE | `/competition-tasks` | login / `competition.publish` | 竞赛任务点 CRUD |
| GET/POST/PUT/DELETE | `/certificates/{id}/units` | login / `certificate_standard.manage_major` | 证书能力单元 CRUD |
| GET/POST/PUT/DELETE | `/certificate-units/{id}/assessment-points` | login / `certificate_standard.manage_major` | 证书考核点 CRUD |
| GET | `/certificates/{id}/readiness/me` | login | 我的证书达标分析 |
| POST | `/certificates/{id}/practice-attempts` | `quiz.practice` | 证书专项练习提交 |

### 5.4 岗课赛证融合关系

| 方法 | 路径 | 权限 | 说明 |
| --- | --- | --- | --- |
| GET | `/fusion-relations` | login | 查询融合关系 |
| POST | `/fusion-relations` | `fusion.relation.manage` | 新增融合关系 |
| PUT | `/fusion-relations/{id}` | `fusion.relation.manage` | 修改融合关系 |
| DELETE | `/fusion-relations/{id}` | `fusion.relation.manage` | 删除融合关系 |
| GET | `/fusion-graph/me` | `fusion.graph.view.self` | 我的融合图谱 |
| GET | `/fusion-graph/students/{studentId}` | `fusion.graph.view.assigned` | 指定学生融合图谱 |
| GET | `/fusion-graph/jobs/{jobRoleId}` | login | 某岗位关联课程、竞赛、证书 |
| GET | `/fusion-graph/courses/{courseId}` | login | 某课程关联岗位、竞赛、证书 |

请求体：

```json
{
  "sourceType": "job_capability",
  "sourceId": 1,
  "targetType": "course_knowledge_point",
  "targetId": 12,
  "relationType": "supports",
  "weight": 0.85,
  "description": "Python基础支撑AI应用开发助理岗位能力"
}
```

## 6. 智能学习层接口

### 6.1 对话式学习画像

| 方法 | 路径 | 权限 | 说明 |
| --- | --- | --- | --- |
| POST | `/profile-sessions` | `profile.session.create.self` | 创建画像对话会话 |
| POST | `/profile-sessions/{id}/messages` | `profile.session.create.self` | 发送画像对话消息 |
| GET | `/profile-sessions/{id}` | `profile.session.create.self` | 查看画像会话 |
| POST | `/profile-sessions/{id}/extract` | `profile.session.create.self` | 抽取画像草稿 |
| POST | `/learning-profiles/me/confirm` | `profile.confirm.self` | 学生确认画像 |
| GET | `/learning-profiles/me` | `profile.view.self` | 我的画像 |
| GET | `/learning-profiles/me/versions` | `profile.view.self` | 画像版本 |
| GET | `/learning-profiles/me/evidence` | `profile.view.self` | 画像依据 |
| GET | `/learning-profiles/students/{studentId}` | `profile.view.assigned` / `profile.view.major` | 指定学生画像 |

画像返回应包含维度、置信度、来源、更新时间和确认状态。

### 6.2 多智能体资源包

| 方法 | 路径 | 权限 | 说明 |
| --- | --- | --- | --- |
| POST | `/ai-generation-tasks` | `resource_package.generate.self` | 创建多智能体生成任务 |
| GET | `/ai-generation-tasks/{id}` | login | 查询任务状态 |
| POST | `/ai-generation-tasks/{id}/cancel` | `resource_package.generate.self` | 取消任务 |
| POST | `/ai-generation-tasks/{id}/retry` | `resource_package.generate.self` | 重试任务 |
| GET | `/ai-generation-tasks/{id}/logs` | login | 任务日志 |
| GET | `/resource-packages` | login | 资源包列表 |
| GET | `/resource-packages/{id}` | login | 资源包详情 |
| POST | `/resource-packages/{id}/submit-review` | `resource_package.generate.self` | 提交资源包审核 |
| POST | `/resource-packages/{id}/review` | `resource_package.review.assigned` | 教师审核资源包 |
| POST | `/resource-packages/{id}/publish` | `resource_package.publish.teacher` | 发布资源包 |
| GET | `/resources/{id}` | login | 单个资源详情 |
| GET | `/resources/{id}/preview` | login | 资源预览 |
| GET | `/resources/{id}/download` | login | 资源下载 |
| DELETE | `/resources/{id}` | `resource_package.generate.self` | 删除个人资源 |

资源包生成请求：

```json
{
  "targetJobRoleId": 1,
  "courseId": 3,
  "competitionId": 2,
  "certificateId": 5,
  "knowledgePointIds": [11, 12, 13],
  "weakPointIds": [21, 22],
  "resourceTypes": ["handout", "ppt", "quiz", "code_case", "mindmap"],
  "difficulty": "intermediate",
  "scenario": "competition_training"
}
```

### 6.3 学习路径、辅导与评估

| 方法 | 路径 | 权限 | 说明 |
| --- | --- | --- | --- |
| POST | `/learning-paths/generate` | `resource_package.generate.self` | 生成学习路径 |
| GET | `/learning-paths/me` | `learning_path.view.self` | 我的学习路径 |
| GET | `/learning-paths/{id}` | login | 学习路径详情 |
| POST | `/learning-paths/{id}/accept` | `learning_path.view.self` | 接受路径 |
| POST | `/learning-paths/{id}/adjust` | `learning_path.view.self` | 调整路径 |
| GET | `/learning-paths/{id}/resources` | login | 路径关联资源 |
| POST | `/learning-path-steps/{id}/complete` | `learning_record.create_self` | 完成路径步骤 |
| POST | `/ai-tutor/chat` | `ai_tutor.chat_self` | 智能辅导 |
| POST | `/learning-evaluations/generate` | `learning_effect.generate.self` | 生成学习效果评估 |
| GET | `/learning-evaluations/me` | `learning_effect.view_self` | 我的评估报告 |

学习路径生成请求：

```json
{
  "targetJobRoleId": 1,
  "competitionId": 2,
  "certificateId": 3,
  "learningGoal": "完成软件杯参赛作品",
  "durationWeeks": 6,
  "preferredResourceTypes": ["ppt", "code_case", "quiz"]
}
```

## 7. 学习过程与教师工作台

### 7.1 学习记录与练习

| 方法 | 路径 | 权限 | 说明 |
| --- | --- | --- | --- |
| POST | `/learning-records` | `learning_record.create_self` | 记录学习行为 |
| GET | `/learning-records/me` | login | 我的学习记录 |
| POST | `/quiz-attempts` | `quiz.practice` | 提交答题 |
| GET | `/wrong-questions/me` | `quiz.practice` | 我的错题 |

### 7.2 教师工作台

| 方法 | 路径 | 权限 | 说明 |
| --- | --- | --- | --- |
| GET | `/teacher-dashboard/overview` | `teacher_dashboard.view.assigned` | 教师工作台概览 |
| GET | `/teacher-dashboard/pending-reviews` | `teacher_dashboard.view.assigned` | 待审核事项 |
| GET | `/teacher-dashboard/classes/{classId}/students` | `teacher_dashboard.view.assigned` | 班级学生 |
| GET | `/teacher-dashboard/classes/{classId}/learning-profiles` | `teacher_dashboard.view.assigned` | 班级画像 |
| GET | `/teacher-dashboard/classes/{classId}/weak-points` | `teacher_dashboard.view.assigned` | 班级知识短板 |
| POST | `/teacher-tasks` | `teacher_dashboard.view.assigned` | 创建学习任务 |
| GET | `/teacher-tasks` | `teacher_dashboard.view.assigned` | 学习任务列表 |
| POST | `/teacher-tasks/{id}/publish` | `teacher_dashboard.view.assigned` | 发布学习任务 |
| GET | `/teacher-reports/class-learning-effect` | `teacher_dashboard.view.assigned` | 班级学习效果报告 |

### 7.3 竞赛与项目过程管理

| 方法 | 路径 | 权限 | 说明 |
| --- | --- | --- | --- |
| GET/POST/PUT/DELETE | `/competition-teams` | login / `competition.publish` | 竞赛团队 CRUD |
| GET/POST/DELETE | `/competition-teams/{id}/members` | `competition.publish` | 团队成员 |
| GET/POST/PATCH | `/competition-teams/{id}/milestones` | `competition.publish` | 竞赛里程碑 |
| GET/POST/PUT/DELETE | `/competition-deliverables` | login / `competition.publish` | 竞赛交付物 |
| GET/POST/PUT/DELETE | `/project-tasks` | login / `project.manage.teacher` | 项目任务 |
| GET/POST/PATCH | `/project-milestones` | login / `project.manage.teacher` | 项目里程碑 |
| GET/POST/PUT/DELETE | `/project-deliverables` | login / `project.manage.teacher` | 项目交付物 |
| POST | `/project-deliverables/{id}/review` | `project_deliverable.review.assigned` | 项目交付物审核 |
| GET | `/projects/{id}/progress` | login | 项目进度 |

## 8. 统计导出

| 方法 | 路径 | 权限 | 说明 |
| --- | --- | --- | --- |
| GET | `/statistics/profile` | `statistics.view_readonly` / `statistics.export_major` | 画像统计 |
| GET | `/statistics/fusion` | `statistics.view_readonly` / `statistics.export_major` | 岗课赛证融合统计 |
| GET | `/statistics/learning-effect` | `statistics.view_readonly` / `statistics.export_major` | 学习效果统计 |
| POST | `/exports` | `statistics.export_major` | 创建导出任务 |
| GET | `/exports` | `statistics.export_major` | 导出记录 |
| GET | `/exports/{id}/download` | `statistics.export_major` | 下载导出文件 |

导出请求：

```json
{
  "exportType": "student_profile_summary",
  "exportScope": "major",
  "majorId": 1,
  "isDesensitized": true
}
```

## 9. 二期就业扩展接口

以下接口保留为二期，不阻塞第一版核心演示：

| 方法 | 路径 | 说明 |
| --- | --- | --- |
| GET/POST/PUT/DELETE | `/job-posts` | 企业真实招聘岗位 |
| POST | `/resumes/generate` | AI 简历生成 |
| GET/POST | `/job-applications` | 岗位投递 |
| POST | `/job-applications/{id}/teacher-review` | 教师审核简历 |
| POST | `/job-applications/{id}/enterprise-review` | 企业导师审核 |
| POST | `/job-applications/{id}/recommend` | 企业导师推荐 |

## 10. 权限调整

权限码逐步统一为：

```text
模块.动作.范围
```

新增或修正：

- `fusion.relation.manage`
- `fusion.graph.view.self`
- `fusion.graph.view.assigned`
- `job_role.manage.major`
- `job_capability.manage.major`
- `profile.session.create.self`
- `profile.confirm.self`
- `profile.view.self`
- `profile.view.assigned`
- `profile.view.major`
- `resource_package.generate.self`
- `resource_package.review.assigned`
- `resource_package.publish.teacher`
- `certificate_result.review_group`
- `teacher_dashboard.view.assigned`
- `project_deliverable.review.assigned`

`certificate_result.review_group` 替换原先不合理的 `resume.review_group`。

## 11. 验收要点

- 每个基础模块都有 CRUD。
- 融合图谱可以串起：岗位能力 → 课程知识点 → 竞赛任务 → 证书能力单元。
- 资源包生成读取画像、目标岗位、知识短板和融合关系。
- 画像支持会话、抽取、确认、版本、依据追溯。
- 学习路径支持生成、接受、调整、步骤完成。
- 教师端可查看负责学生、班级短板、待审核事项和学习报告。
- 证书成果审核不再复用简历审核权限。
- 二期就业接口不影响第一版主线演示。

## 12. 后续影响

为完整支撑本接口设计，后续数据库需要补充：

- 岗位能力模型表
- 岗位能力点表
- 融合关系表
- 知识点关系表
- 竞赛任务表
- 证书能力单元表
- 证书考核点表
- 画像会话表
- 资源包表
- 教师任务表
- 竞赛团队 / 里程碑 / 交付物表
- 项目任务 / 里程碑 / 交付物表
