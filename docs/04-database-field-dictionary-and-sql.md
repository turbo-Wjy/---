# 数据库字段字典与建表 SQL 设计

## 1. 设计目标

本设计承接 `docs/03-database-er-core-tables.md`，将 ER 图和核心表清单细化为字段字典与 MySQL 8 建表 SQL。对应 SQL 文件为：

- `database/schema.sql`

数据库覆盖以下主线：

- 用户权限与基础数据
- 学生画像
- AI 学习中心
- 课程学习
- 岗位、简历、投递
- 竞赛、证书、项目实训
- 审核、导出、操作日志

## 2. 统一字段规范

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| `id` | `BIGINT UNSIGNED` | 自增主键 |
| `status` | `VARCHAR(32)` | 通用业务状态 |
| `created_by` | `BIGINT UNSIGNED` | 创建人用户 ID，可为空 |
| `created_at` | `DATETIME` | 创建时间 |
| `updated_at` | `DATETIME` | 更新时间 |
| `deleted_at` | `DATETIME` | 软删除时间 |

约定：

- 字符集统一使用 `utf8mb4`。
- 排序规则统一使用 `utf8mb4_unicode_ci`。
- 状态字段使用 `VARCHAR(32)`，不使用 MySQL `ENUM`。
- AI 上下文、模型配置、评分明细、元数据使用 `JSON`。
- 文件本体不入库，只保存 URL、文件名、类型、大小。
- 业务查询默认加 `deleted_at IS NULL`。

## 3. 敏感信息保护规范

### 3.1 密码

| 字段 | 处理方式 |
| --- | --- |
| `users.password_hash` | 使用 BCrypt 或 Argon2 哈希 |

要求：

- 不保存明文密码。
- 不保存明文初始密码。
- 初始密码只在生成时展示一次，或通过重置流程重新生成。
- `users.must_change_password` 用于控制首次登录强制改密。

### 3.2 加密字段

隐私字段采用应用层 AES-256-GCM 加密后入库。数据库只保存密文、IV / nonce、hash 检索值。

| 原始信息 | 入库字段 |
| --- | --- |
| 手机号 | `phone_encrypted`、`phone_iv`、`phone_hash` |
| 邮箱 | `email_encrypted`、`email_iv`、`email_hash` |
| 身份证号 | `id_card_no_encrypted`、`id_card_no_iv`、`id_card_no_hash` |
| 企业联系人电话 | `contact_phone_encrypted`、`contact_phone_iv`、`contact_phone_hash` |
| 简历正文 | `resume_content_encrypted`、`resume_content_iv` |
| 画像对话内容 | `message_content_encrypted`、`message_content_iv` |
| 画像摘要 | `profile_summary_encrypted`、`profile_summary_iv` |
| 画像维度值 | `dimension_value_encrypted`、`dimension_value_iv` |
| 智能辅导问题 | `question_encrypted`、`question_iv` |
| 智能辅导回答 | `answer_text_encrypted`、`answer_text_iv` |

搜索约定：

- 需要精确搜索的隐私字段使用 `xxx_hash`。
- 不对密文字段做模糊查询。
- hash 由应用层使用统一规范生成。

### 3.3 导出与日志脱敏

导出默认脱敏：

- 手机号：`138****5678`
- 邮箱：`ab***@example.com`
- 身份证号：仅保留前后少量字符
- 简历内容、画像详情默认导出摘要

日志要求：

- `operation_logs` 不记录明文密码、完整手机号、完整邮箱、完整身份证号。
- AI 提问、画像内容、简历内容如需留痕，只记录摘要、hash 或对象 ID。
- 审核意见可以记录原文，但不得包含认证信息。

## 4. 字段字典

### 4.1 用户权限

| 表名 | 核心字段 |
| --- | --- |
| `users` | `username`、`password_hash`、`real_name`、`phone_encrypted`、`phone_iv`、`phone_hash`、`email_encrypted`、`email_iv`、`email_hash`、`account_status`、`must_change_password`、`last_login_at` |
| `roles` | `code`、`name`、`data_scope`、`is_core`、`description` |
| `permissions` | `code`、`name`、`module`、`permission_type`、`description` |
| `user_roles` | `user_id`、`role_id` |
| `role_permissions` | `role_id`、`permission_id` |

### 4.2 基础数据

| 表名 | 核心字段 |
| --- | --- |
| `colleges` | `code`、`name`、`status` |
| `majors` | `college_id`、`code`、`name`、`status` |
| `classes` | `major_id`、`grade`、`name`、`status` |
| `students` | `user_id`、`student_no`、`college_id`、`major_id`、`class_id`、`grade`、`enrollment_status`、`phone_encrypted`、`phone_hash`、`email_encrypted`、`email_hash`、`id_card_no_encrypted`、`id_card_no_hash` |
| `teachers` | `user_id`、`teacher_no`、`college_id`、`title`、`phone_encrypted`、`phone_hash`、`email_encrypted`、`email_hash` |
| `teacher_duty_tags` | `teacher_id`、`tag_code`、`tag_name` |
| `teacher_student_groups` | `teacher_id`、`student_id`、`group_name`、`bind_type` |
| `enterprises` | `name`、`industry`、`contact_name`、`contact_phone_encrypted`、`contact_phone_hash`、`status` |
| `enterprise_mentors` | `user_id`、`enterprise_id`、`position`、`contact_phone_encrypted`、`contact_phone_hash`、`email_encrypted`、`email_hash` |

### 4.3 学生画像

| 表名 | 核心字段 |
| --- | --- |
| `profile_conversations` | `student_id`、`agent_id`、`message_role`、`message_content_encrypted`、`message_content_iv`、`extracted_features_encrypted`、`extracted_features_iv` |
| `student_profiles` | `student_id`、`profile_version`、`profile_summary_encrypted`、`profile_summary_iv`、`completeness_score`、`last_generated_at` |
| `profile_dimension_values` | `profile_id`、`dimension_code`、`dimension_name`、`dimension_value_encrypted`、`dimension_value_iv`、`confidence_score`、`source_type` |
| `profile_update_logs` | `student_id`、`source_type`、`source_id`、`before_snapshot`、`after_snapshot`、`updated_reason` |

画像维度第一版：

- `knowledge_foundation`
- `learning_goal`
- `cognitive_style`
- `knowledge_gap`
- `error_prone_points`
- `resource_preference`
- `practice_ability`
- `learning_progress`

### 4.4 AI 学习中心

| 表名 | 核心字段 |
| --- | --- |
| `ai_agents` | `code`、`name`、`agent_type`、`model_name`、`config_json`、`enabled` |
| `ai_generation_tasks` | `student_id`、`agent_id`、`task_type`、`prompt`、`context_snapshot`、`task_status` |
| `ai_generated_resources` | `task_id`、`resource_type`、`title`、`content_url`、`content_text`、`metadata_json` |
| `learning_paths` | `student_id`、`title`、`goal`、`generated_by_agent_id`、`path_status` |
| `learning_path_steps` | `path_id`、`step_order`、`title`、`resource_id`、`expected_duration`、`completion_status` |
| `resource_recommendations` | `student_id`、`resource_id`、`recommend_reason`、`source_profile_id`、`view_status` |
| `ai_tutoring_sessions` | `student_id`、`knowledge_point_id`、`question_encrypted`、`question_iv`、`answer_text_encrypted`、`answer_text_iv`、`answer_assets_json`、`feedback_score` |
| `learning_evaluations` | `student_id`、`source_type`、`source_id`、`evaluation_summary`、`score_json`、`suggestion_json` |

### 4.5 课程学习

| 表名 | 核心字段 |
| --- | --- |
| `courses` | `course_code`、`course_name`、`major_id`、`credit`、`semester` |
| `course_knowledge_points` | `course_id`、`parent_id`、`name`、`description`、`difficulty_level` |
| `course_resources` | `course_id`、`knowledge_point_id`、`uploaded_by_teacher_id`、`resource_type`、`title`、`file_url`、`file_name`、`file_type`、`file_size` |
| `learning_records` | `student_id`、`course_id`、`resource_id`、`action_type`、`duration_seconds`、`completed` |
| `quiz_attempts` | `student_id`、`course_id`、`knowledge_point_id`、`question_snapshot`、`answer`、`is_correct`、`score` |
| `wrong_questions` | `student_id`、`quiz_attempt_id`、`knowledge_point_id`、`wrong_reason`、`review_status` |

### 4.6 岗位、简历与投递

| 表名 | 核心字段 |
| --- | --- |
| `job_posts` | `enterprise_id`、`mentor_id`、`major_id`、`title`、`requirements`、`salary_range`、`location`、`ability_tags`、`review_status` |
| `resumes` | `student_id`、`target_job_id`、`generated_by_task_id`、`resume_content_encrypted`、`resume_content_iv`、`resume_summary`、`student_confirmed`、`confirmed_at` |
| `job_applications` | `job_id`、`resume_id`、`student_id`、`application_status`、`submitted_at`、`enterprise_feedback` |

### 4.7 竞赛、证书、项目实训

| 表名 | 核心字段 |
| --- | --- |
| `competitions` | `title`、`level`、`start_time`、`end_time`、`location`、`requirements`、`official_url`、`published_by` |
| `competition_results` | `competition_id`、`student_id`、`coach_teacher_id`、`award_name`、`proof_file_url`、`review_status` |
| `certificates` | `major_id`、`certificate_name`、`requirement_level`、`graduation_required`、`resource_url`、`imported_by` |
| `certificate_results` | `certificate_id`、`student_id`、`certificate_no`、`issued_at`、`proof_file_url`、`review_status` |
| `projects` | `course_id`、`title`、`description`、`difficulty_level`、`ability_tags` |
| `project_materials` | `project_id`、`material_type`、`title`、`file_url`、`content_text` |
| `project_submissions` | `project_id`、`student_id`、`submission_url`、`score`、`teacher_comment` |

### 4.8 审核、导出、日志

| 表名 | 核心字段 |
| --- | --- |
| `review_records` | `target_type`、`target_id`、`review_node`、`reviewer_user_id`、`review_result`、`review_comment`、`reviewed_at` |
| `export_records` | `export_type`、`export_scope`、`major_id`、`exported_by`、`file_url`、`is_desensitized`、`export_status` |
| `operation_logs` | `operator_id`、`operator_role`、`module`、`action`、`target_type`、`target_id`、`result`、`ip_address`、`remark` |

## 5. 状态与枚举

### 5.1 通用状态

- `draft`
- `pending`
- `approved`
- `rejected`
- `published`
- `archived`

### 5.2 AI 任务状态

- `queued`
- `running`
- `succeeded`
- `failed`
- `cancelled`

### 5.3 资源类型

- `document`
- `ppt`
- `mind_map`
- `quiz`
- `reading`
- `video_script`
- `practice_case`
- `project_material`

### 5.4 学习行为类型

- `view`
- `download`
- `study`
- `quiz`
- `complete_task`
- `feedback`

### 5.5 审核对象类型

- `job_post`
- `resume`
- `job_application`
- `competition_result`
- `certificate_result`

### 5.6 导出类型

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

## 6. 索引与约束

唯一约束：

- `users.username`
- `roles.code`
- `permissions.code`
- `students.student_no`
- `teachers.teacher_no`
- `ai_agents.code`
- `courses.course_code`

敏感字段检索索引：

- `users.phone_hash`
- `users.email_hash`
- `students.phone_hash`
- `students.email_hash`
- `students.id_card_no_hash`
- `teachers.phone_hash`
- `teachers.email_hash`
- `enterprise_mentors.contact_phone_hash`

常用查询索引：

- 学生：`major_id`、`class_id`、`student_no`
- 画像：`student_id`、`profile_id`、`dimension_code`
- AI 任务：`student_id`、`agent_id`、`task_status`
- 学习记录：`student_id`、`course_id`、`resource_id`
- 岗位：`mentor_id`、`major_id`、`review_status`
- 投递：`student_id`、`job_id`、`application_status`
- 审核：`target_type,target_id`、`reviewer_user_id`
- 日志：`operator_id`、`module`、`created_at`
- 导出：`exported_by`、`major_id`、`export_type`

## 7. 建表 SQL

完整 SQL 见：

- `database/schema.sql`

执行顺序已经按外键依赖排列。

