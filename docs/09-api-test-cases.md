# 后端接口测试用例

## 1. 测试目标

本文档用于验证融合版接口设计的第一版核心闭环，重点覆盖：

- 新增权限种子是否正确授权。
- 岗位能力模型、岗位能力点、融合关系是否可维护。
- 学生能查看个人融合图谱并生成资源包。
- 教师能审核和发布资源包。
- 教师能审核项目交付物。
- 统计导出能记录脱敏导出日志。

接口统一前缀：

`/api/v1`

统一响应：

```json
{
  "code": 0,
  "message": "success",
  "data": {},
  "traceId": "uuid"
}
```

## 2. 测试账号与角色

| 角色 | 建议账号 | 数据范围 | 说明 |
| --- | --- | --- | --- |
| 系统管理员 | `admin_demo` | 平台全部 | 校验全部权限 |
| 专业负责人 | `major_ai_demo` | 人工智能技术应用专业 | 管理岗位能力、融合关系、统计导出 |
| 教师 | `teacher_group_demo` | 负责学生小组 | 审核资源包、证书成果、项目交付物 |
| 学生 | `20240001` | 本人 | 查看图谱、生成资源包 |
| 数据查看者 | `data_viewer_demo` | 只读 | 查看统计和图谱，不允许写操作 |

说明：演示账号的 `password_hash` 是占位哈希，真实接口测试前需要通过系统重置密码。

## 3. 权限种子验证

### TC-PERM-001 新增权限点已初始化

| 项目 | 内容 |
| --- | --- |
| 前置条件 | 已执行 `database/seed.sql` |
| 验证 SQL | 查询新增权限数量 |
| 期望结果 | 以下权限均存在且 `status = active` |

权限清单：

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

### TC-PERM-002 角色授权正确

| 角色 | 必须拥有的新增权限 |
| --- | --- |
| `admin` | 全部新增权限 |
| `major_leader` | `fusion.relation.manage`、`fusion.graph.view.assigned`、`job_role.manage.major`、`job_capability.manage.major`、`profile.view.major` |
| `teacher` | `profile.view.assigned`、`fusion.graph.view.assigned`、`resource_package.review.assigned`、`resource_package.publish.teacher`、`certificate_result.review_group`、`teacher_dashboard.view.assigned`、`project_deliverable.review.assigned` |
| `student` | `profile.session.create.self`、`profile.confirm.self`、`profile.view.self`、`fusion.graph.view.self`、`resource_package.generate.self` |
| `data_viewer` | `profile.view.major`、`fusion.graph.view.assigned`、`statistics.view_readonly` |

## 4. 岗位能力模型接口

### TC-JOB-001 专业负责人创建岗位能力模型

| 项目 | 内容 |
| --- | --- |
| 角色 | 专业负责人 |
| 权限 | `job_role.manage.major` |
| 接口 | `POST /api/v1/job-roles` |
| 请求体 | `{ "roleCode": "AI_APP_DEV_ASSISTANT", "roleName": "AI应用开发助理", "majorId": 1 }` |
| 期望结果 | 创建成功，返回岗位模型 ID |
| 数据校验 | `job_roles` 新增或更新对应记录 |

### TC-JOB-002 专业负责人维护岗位能力点

| 项目 | 内容 |
| --- | --- |
| 角色 | 专业负责人 |
| 权限 | `job_capability.manage.major` |
| 接口 | `POST /api/v1/job-roles/{id}/capabilities` |
| 请求体 | `{ "capabilityCode": "ML_MODEL_EVALUATION", "capabilityName": "机器学习模型评估", "weight": 0.95 }` |
| 期望结果 | 创建成功，返回能力点 ID |
| 数据校验 | `job_capabilities` 新增对应能力点 |

### TC-JOB-003 学生只读查看岗位能力模型

| 项目 | 内容 |
| --- | --- |
| 角色 | 学生 |
| 接口 | `GET /api/v1/job-roles` |
| 期望结果 | 返回岗位能力模型列表 |
| 反向校验 | 学生调用 `POST /api/v1/job-roles` 应返回无权限 |

## 5. 融合关系与融合图谱接口

### TC-FUSION-001 专业负责人新增融合关系

| 项目 | 内容 |
| --- | --- |
| 角色 | 专业负责人 |
| 权限 | `fusion.relation.manage` |
| 接口 | `POST /api/v1/fusion-relations` |
| 请求体 | `{ "sourceType": "job_capability", "sourceId": 1, "targetType": "course_knowledge_point", "targetId": 1, "relationType": "supports", "weight": 0.85 }` |
| 期望结果 | 创建成功 |
| 数据校验 | `fusion_relations` 写入唯一关系 |

### TC-FUSION-002 学生查看个人融合图谱

| 项目 | 内容 |
| --- | --- |
| 角色 | 学生 |
| 权限 | `fusion.graph.view.self` |
| 接口 | `GET /api/v1/fusion-graph/me` |
| 期望结果 | 返回 `nodes`、`edges`、`weakPoints`、`recommendedPath` |
| 数据来源 | `job_capabilities`、`course_knowledge_points`、`competition_tasks`、`certificate_assessment_points`、`fusion_relations`、`student_capability_scores` |

### TC-FUSION-003 教师查看负责学生融合图谱

| 项目 | 内容 |
| --- | --- |
| 角色 | 教师 |
| 权限 | `fusion.graph.view.assigned` |
| 接口 | `GET /api/v1/fusion-graph/students/{studentId}` |
| 期望结果 | 如果学生在教师负责范围内，返回图谱 |
| 反向校验 | 查询非负责学生应返回无数据范围权限 |

## 6. 画像会话接口

### TC-PROFILE-001 学生创建画像会话

| 项目 | 内容 |
| --- | --- |
| 角色 | 学生 |
| 权限 | `profile.session.create.self` |
| 接口 | `POST /api/v1/profile-sessions` |
| 期望结果 | 返回 `sessionId` |
| 数据校验 | `profile_sessions` 新增记录 |

### TC-PROFILE-002 学生发送画像对话消息

| 项目 | 内容 |
| --- | --- |
| 角色 | 学生 |
| 权限 | `profile.session.create.self` |
| 接口 | `POST /api/v1/profile-sessions/{id}/messages` |
| 请求体 | `{ "message": "我想提升机器学习模型评估能力，目标岗位是AI应用开发助理。" }` |
| 期望结果 | 返回智能体回复 |
| 安全校验 | `profile_session_messages.message_content_encrypted` 存密文，不存明文 |

### TC-PROFILE-003 学生确认画像

| 项目 | 内容 |
| --- | --- |
| 角色 | 学生 |
| 权限 | `profile.confirm.self` |
| 接口 | `POST /api/v1/learning-profiles/me/confirm` |
| 期望结果 | 生成或更新正式画像 |
| 数据校验 | 写入 `student_profiles`、`profile_dimension_values`、`profile_update_logs` |

## 7. 多智能体资源包接口

### TC-RP-001 学生创建资源包生成任务

| 项目 | 内容 |
| --- | --- |
| 角色 | 学生 |
| 权限 | `resource_package.generate.self` |
| 接口 | `POST /api/v1/ai-generation-tasks` |
| 请求体 | `{ "targetJobRoleId": 1, "courseId": 1, "resourceTypes": ["handout", "ppt", "quiz", "mindmap"], "difficulty": "intermediate", "scenario": "competition_training" }` |
| 期望结果 | 返回 `taskId` |
| 数据校验 | `ai_generation_tasks` 创建任务，完成后生成 `resource_packages` 和 `resource_package_items` |

### TC-RP-002 学生提交资源包审核

| 项目 | 内容 |
| --- | --- |
| 角色 | 学生 |
| 权限 | `resource_package.generate.self` |
| 接口 | `POST /api/v1/resource-packages/{id}/submit-review` |
| 期望结果 | 资源包状态变为 `pending_review` |
| 数据校验 | `resource_packages.review_status = pending_review` |

### TC-RP-003 教师审核资源包打回

| 项目 | 内容 |
| --- | --- |
| 角色 | 教师 |
| 权限 | `resource_package.review.assigned` |
| 接口 | `POST /api/v1/resource-packages/{id}/review` |
| 请求体 | `{ "result": "rejected", "comment": "题库难度分层不够，请重新生成。" }` |
| 期望结果 | 资源包状态变为 `rejected` |
| 数据校验 | 写入 `review_records` |

### TC-RP-004 教师审核通过并发布资源包

| 项目 | 内容 |
| --- | --- |
| 角色 | 教师 |
| 权限 | `resource_package.review.assigned`、`resource_package.publish.teacher` |
| 接口 | `POST /api/v1/resource-packages/{id}/review`、`POST /api/v1/resource-packages/{id}/publish` |
| 期望结果 | 审核通过后资源包发布 |
| 数据校验 | `resource_packages.review_status = approved`，`status = published`，写入 `review_records` 和 `operation_logs` |

## 8. 教师工作台与项目交付物

### TC-TEACHER-001 教师查看工作台待审核事项

| 项目 | 内容 |
| --- | --- |
| 角色 | 教师 |
| 权限 | `teacher_dashboard.view.assigned` |
| 接口 | `GET /api/v1/teacher-dashboard/pending-reviews` |
| 期望结果 | 返回资源包、证书成果、项目交付物等待审列表 |

### TC-PROJECT-001 教师审核项目交付物

| 项目 | 内容 |
| --- | --- |
| 角色 | 教师 |
| 权限 | `project_deliverable.review.assigned` |
| 接口 | `POST /api/v1/project-deliverables/{id}/review` |
| 请求体 | `{ "result": "approved", "score": 88, "comment": "项目结构完整，模型评估部分还可加强。" }` |
| 期望结果 | 项目交付物审核通过 |
| 数据校验 | `project_deliverables.review_status = approved`，写入 `review_records` |

## 9. 统计导出与脱敏

### TC-EXPORT-001 专业负责人导出本专业画像汇总

| 项目 | 内容 |
| --- | --- |
| 角色 | 专业负责人 |
| 权限 | `statistics.export_major` |
| 接口 | `POST /api/v1/exports` |
| 请求体 | `{ "exportType": "student_profile_summary", "exportScope": "major", "majorId": 1, "isDesensitized": true }` |
| 期望结果 | 创建导出任务 |
| 数据校验 | `export_records.is_desensitized = 1`，写入 `operation_logs` |

### TC-EXPORT-002 导出敏感字段默认脱敏

| 项目 | 内容 |
| --- | --- |
| 角色 | 专业负责人 |
| 接口 | `GET /api/v1/exports/{id}/download` |
| 期望结果 | 手机号、邮箱、身份证号、画像详情、简历内容默认脱敏 |
| 反向校验 | 无完整导出权限时，不允许导出明文敏感字段 |

## 10. 权限反向测试

| 用例 | 角色 | 接口 | 期望结果 |
| --- | --- | --- | --- |
| TC-AUTH-001 | 学生 | `POST /api/v1/fusion-relations` | 无权限 |
| TC-AUTH-002 | 学生 | `POST /api/v1/resource-packages/{id}/review` | 无权限 |
| TC-AUTH-003 | 企业导师 | `GET /api/v1/learning-profiles/students/{studentId}` | 无权限或无数据范围 |
| TC-AUTH-004 | 数据查看者 | `POST /api/v1/job-roles` | 无权限 |
| TC-AUTH-005 | 教师 | `POST /api/v1/exports` | 无权限，除非额外授予导出权限 |

## 11. 数据闭环验收

| 闭环 | 验收点 |
| --- | --- |
| 岗位能力闭环 | `job_roles` → `job_capabilities` 可维护 |
| 融合图谱闭环 | `job_capabilities` → `fusion_relations` → 课程、竞赛、证书节点可查询 |
| 资源包闭环 | `ai_generation_tasks` → `resource_packages` → `resource_package_items` → `review_records` |
| 学习画像闭环 | `profile_sessions` → `student_profiles` → `profile_update_logs` |
| 学习评估闭环 | `learning_records` / `quiz_attempts` → `learning_evaluations` → `student_capability_scores` |
| 项目过程闭环 | `project_tasks` → `project_deliverables` → `review_records` |
| 统计导出闭环 | 业务统计 → `export_records` → `operation_logs` |

## 12. SQL 快速校验

执行 `database/seed.sql` 后，可用以下思路校验：

```sql
SELECT code, name, module
FROM permissions
WHERE code IN (
  'fusion.relation.manage',
  'job_role.manage.major',
  'job_capability.manage.major',
  'resource_package.generate.self',
  'resource_package.review.assigned',
  'resource_package.publish.teacher',
  'project_deliverable.review.assigned'
)
ORDER BY code;

SELECT r.code AS role_code, p.code AS permission_code
FROM role_permissions rp
JOIN roles r ON r.id = rp.role_id
JOIN permissions p ON p.id = rp.permission_id
WHERE p.code IN (
  'fusion.relation.manage',
  'job_role.manage.major',
  'resource_package.generate.self',
  'resource_package.review.assigned',
  'project_deliverable.review.assigned'
)
ORDER BY r.code, p.code;
```
