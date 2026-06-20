-- =========================================================
-- AI 岗课赛证学习平台：融合接口数据库升级脚本
-- Version: 001
-- Target: MySQL 8
-- Purpose: support job roles, fusion relations, resource packages,
--          profile sessions, competition/certificate/project process data.
-- =========================================================

USE ai_learning_platform;

SET NAMES utf8mb4;

-- =========================================================
-- 1. 岗位能力模型
-- =========================================================

CREATE TABLE IF NOT EXISTS job_roles (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '岗位能力模型ID',
  major_id BIGINT UNSIGNED NULL COMMENT '适用专业ID',
  role_code VARCHAR(64) NOT NULL COMMENT '岗位模型编码',
  role_name VARCHAR(128) NOT NULL COMMENT '岗位模型名称',
  description TEXT NULL COMMENT '岗位说明',
  typical_tasks JSON NULL COMMENT '典型工作任务',
  ability_tags JSON NULL COMMENT '能力标签',
  sort_order INT NOT NULL DEFAULT 0 COMMENT '排序',
  status VARCHAR(32) NOT NULL DEFAULT 'active',
  created_by BIGINT UNSIGNED NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted_at DATETIME NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_job_roles_role_code (role_code),
  KEY idx_job_roles_major_id (major_id),
  KEY idx_job_roles_status (status),
  CONSTRAINT fk_job_roles_majors_major_id FOREIGN KEY (major_id) REFERENCES majors(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='岗位能力模型表';

CREATE TABLE IF NOT EXISTS job_capabilities (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '岗位能力点ID',
  job_role_id BIGINT UNSIGNED NOT NULL COMMENT '岗位能力模型ID',
  parent_id BIGINT UNSIGNED NULL COMMENT '父级能力点ID',
  capability_code VARCHAR(64) NOT NULL COMMENT '能力点编码',
  capability_name VARCHAR(128) NOT NULL COMMENT '能力点名称',
  description TEXT NULL COMMENT '能力说明',
  level VARCHAR(32) NULL COMMENT '能力等级：basic/intermediate/advanced',
  weight DECIMAL(5,2) NOT NULL DEFAULT 1.00 COMMENT '能力权重',
  sort_order INT NOT NULL DEFAULT 0 COMMENT '排序',
  status VARCHAR(32) NOT NULL DEFAULT 'active',
  created_by BIGINT UNSIGNED NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted_at DATETIME NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_job_capabilities_role_code (job_role_id, capability_code),
  KEY idx_job_capabilities_job_role_id (job_role_id),
  KEY idx_job_capabilities_parent_id (parent_id),
  KEY idx_job_capabilities_level (level),
  CONSTRAINT fk_job_capabilities_job_roles_job_role_id FOREIGN KEY (job_role_id) REFERENCES job_roles(id),
  CONSTRAINT fk_job_capabilities_self_parent_id FOREIGN KEY (parent_id) REFERENCES job_capabilities(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='岗位能力点表';

-- =========================================================
-- 2. 课程知识点关系与岗课赛证融合关系
-- =========================================================

CREATE TABLE IF NOT EXISTS knowledge_point_relations (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '知识点关系ID',
  source_knowledge_point_id BIGINT UNSIGNED NOT NULL COMMENT '源知识点ID',
  target_knowledge_point_id BIGINT UNSIGNED NOT NULL COMMENT '目标知识点ID',
  relation_type VARCHAR(32) NOT NULL COMMENT '关系类型：prerequisite/supports/recommends',
  weight DECIMAL(5,2) NOT NULL DEFAULT 1.00 COMMENT '关系权重',
  description VARCHAR(512) NULL COMMENT '关系说明',
  status VARCHAR(32) NOT NULL DEFAULT 'active',
  created_by BIGINT UNSIGNED NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted_at DATETIME NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_kp_relations_unique (source_knowledge_point_id, target_knowledge_point_id, relation_type),
  KEY idx_kp_relations_source (source_knowledge_point_id),
  KEY idx_kp_relations_target (target_knowledge_point_id),
  CONSTRAINT fk_kp_relations_kp_source FOREIGN KEY (source_knowledge_point_id) REFERENCES course_knowledge_points(id),
  CONSTRAINT fk_kp_relations_kp_target FOREIGN KEY (target_knowledge_point_id) REFERENCES course_knowledge_points(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='课程知识点关系表';

CREATE TABLE IF NOT EXISTS fusion_relations (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '融合关系ID',
  source_type VARCHAR(64) NOT NULL COMMENT '源对象类型',
  source_id BIGINT UNSIGNED NOT NULL COMMENT '源对象ID',
  target_type VARCHAR(64) NOT NULL COMMENT '目标对象类型',
  target_id BIGINT UNSIGNED NOT NULL COMMENT '目标对象ID',
  relation_type VARCHAR(32) NOT NULL COMMENT '关系类型：supports/requires/improves/assesses/recommends',
  weight DECIMAL(5,2) NOT NULL DEFAULT 1.00 COMMENT '关系权重',
  description VARCHAR(512) NULL COMMENT '关系说明',
  evidence_json JSON NULL COMMENT '关系依据',
  status VARCHAR(32) NOT NULL DEFAULT 'active',
  created_by BIGINT UNSIGNED NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted_at DATETIME NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_fusion_relations_unique (source_type, source_id, target_type, target_id, relation_type),
  KEY idx_fusion_relations_source (source_type, source_id),
  KEY idx_fusion_relations_target (target_type, target_id),
  KEY idx_fusion_relations_relation_type (relation_type),
  KEY idx_fusion_relations_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='岗课赛证融合关系表';

CREATE TABLE IF NOT EXISTS student_capability_scores (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '学生能力得分ID',
  student_id BIGINT UNSIGNED NOT NULL COMMENT '学生ID',
  target_type VARCHAR(64) NOT NULL COMMENT '能力对象类型',
  target_id BIGINT UNSIGNED NOT NULL COMMENT '能力对象ID',
  score DECIMAL(5,2) NOT NULL DEFAULT 0.00 COMMENT '掌握度得分',
  mastery_status VARCHAR(32) NOT NULL DEFAULT 'unknown' COMMENT '掌握状态',
  source_type VARCHAR(64) NULL COMMENT '来源类型',
  source_id BIGINT UNSIGNED NULL COMMENT '来源ID',
  evidence_json JSON NULL COMMENT '评估依据',
  evaluated_at DATETIME NULL COMMENT '评估时间',
  status VARCHAR(32) NOT NULL DEFAULT 'active',
  created_by BIGINT UNSIGNED NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted_at DATETIME NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_student_capability_scores_target (student_id, target_type, target_id),
  KEY idx_student_capability_scores_student_id (student_id),
  KEY idx_student_capability_scores_target (target_type, target_id),
  KEY idx_student_capability_scores_mastery_status (mastery_status),
  CONSTRAINT fk_student_capability_scores_students_student_id FOREIGN KEY (student_id) REFERENCES students(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='学生能力掌握度表';

-- =========================================================
-- 3. 对话式画像会话
-- =========================================================

CREATE TABLE IF NOT EXISTS profile_sessions (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '画像会话ID',
  student_id BIGINT UNSIGNED NOT NULL COMMENT '学生ID',
  agent_id BIGINT UNSIGNED NULL COMMENT '画像构建智能体ID',
  session_title VARCHAR(255) NULL COMMENT '会话标题',
  draft_profile_json JSON NULL COMMENT '画像草稿，不保存敏感明文',
  extracted_dimensions_json JSON NULL COMMENT '抽取维度摘要',
  confidence_score DECIMAL(5,2) NULL COMMENT '整体置信度',
  confirm_status VARCHAR(32) NOT NULL DEFAULT 'draft' COMMENT '确认状态：draft/extracted/confirmed',
  confirmed_profile_id BIGINT UNSIGNED NULL COMMENT '确认后画像ID',
  confirmed_at DATETIME NULL COMMENT '确认时间',
  status VARCHAR(32) NOT NULL DEFAULT 'active',
  created_by BIGINT UNSIGNED NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted_at DATETIME NULL,
  PRIMARY KEY (id),
  KEY idx_profile_sessions_student_id (student_id),
  KEY idx_profile_sessions_agent_id (agent_id),
  KEY idx_profile_sessions_confirm_status (confirm_status),
  KEY idx_profile_sessions_confirmed_profile_id (confirmed_profile_id),
  CONSTRAINT fk_profile_sessions_students_student_id FOREIGN KEY (student_id) REFERENCES students(id),
  CONSTRAINT fk_profile_sessions_ai_agents_agent_id FOREIGN KEY (agent_id) REFERENCES ai_agents(id),
  CONSTRAINT fk_profile_sessions_student_profiles_confirmed_profile_id FOREIGN KEY (confirmed_profile_id) REFERENCES student_profiles(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='画像构建会话表';

CREATE TABLE IF NOT EXISTS profile_session_messages (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '画像会话消息ID',
  session_id BIGINT UNSIGNED NOT NULL COMMENT '画像会话ID',
  message_role VARCHAR(32) NOT NULL COMMENT '消息角色：student/assistant/system',
  message_content_encrypted MEDIUMTEXT NOT NULL COMMENT '消息内容密文',
  message_content_iv VARCHAR(128) NOT NULL COMMENT '消息内容IV',
  extracted_features_encrypted MEDIUMTEXT NULL COMMENT '抽取特征密文',
  extracted_features_iv VARCHAR(128) NULL COMMENT '抽取特征IV',
  token_usage JSON NULL COMMENT '模型调用消耗',
  status VARCHAR(32) NOT NULL DEFAULT 'active',
  created_by BIGINT UNSIGNED NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted_at DATETIME NULL,
  PRIMARY KEY (id),
  KEY idx_profile_session_messages_session_id (session_id),
  KEY idx_profile_session_messages_role (message_role),
  CONSTRAINT fk_profile_session_messages_profile_sessions_session_id FOREIGN KEY (session_id) REFERENCES profile_sessions(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='画像构建会话消息表';

-- =========================================================
-- 4. 多智能体资源包
-- =========================================================

CREATE TABLE IF NOT EXISTS resource_packages (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '资源包ID',
  student_id BIGINT UNSIGNED NOT NULL COMMENT '发起学生ID',
  task_id BIGINT UNSIGNED NULL COMMENT 'AI生成任务ID',
  profile_id BIGINT UNSIGNED NULL COMMENT '使用的画像ID',
  target_job_role_id BIGINT UNSIGNED NULL COMMENT '目标岗位模型ID',
  course_id BIGINT UNSIGNED NULL COMMENT '关联课程ID',
  competition_id BIGINT UNSIGNED NULL COMMENT '关联竞赛ID',
  certificate_id BIGINT UNSIGNED NULL COMMENT '关联证书ID',
  package_title VARCHAR(255) NOT NULL COMMENT '资源包标题',
  generation_context JSON NULL COMMENT '生成上下文摘要',
  resource_types JSON NULL COMMENT '资源类型列表',
  difficulty VARCHAR(32) NULL COMMENT '难度',
  scenario VARCHAR(64) NULL COMMENT '使用场景',
  review_status VARCHAR(32) NOT NULL DEFAULT 'generated' COMMENT '审核状态',
  published_at DATETIME NULL COMMENT '发布时间',
  status VARCHAR(32) NOT NULL DEFAULT 'generated',
  created_by BIGINT UNSIGNED NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted_at DATETIME NULL,
  PRIMARY KEY (id),
  KEY idx_resource_packages_student_id (student_id),
  KEY idx_resource_packages_task_id (task_id),
  KEY idx_resource_packages_profile_id (profile_id),
  KEY idx_resource_packages_target_job_role_id (target_job_role_id),
  KEY idx_resource_packages_course_id (course_id),
  KEY idx_resource_packages_review_status (review_status),
  CONSTRAINT fk_resource_packages_students_student_id FOREIGN KEY (student_id) REFERENCES students(id),
  CONSTRAINT fk_resource_packages_ai_generation_tasks_task_id FOREIGN KEY (task_id) REFERENCES ai_generation_tasks(id),
  CONSTRAINT fk_resource_packages_student_profiles_profile_id FOREIGN KEY (profile_id) REFERENCES student_profiles(id),
  CONSTRAINT fk_resource_packages_job_roles_target_job_role_id FOREIGN KEY (target_job_role_id) REFERENCES job_roles(id),
  CONSTRAINT fk_resource_packages_courses_course_id FOREIGN KEY (course_id) REFERENCES courses(id),
  CONSTRAINT fk_resource_packages_competitions_competition_id FOREIGN KEY (competition_id) REFERENCES competitions(id),
  CONSTRAINT fk_resource_packages_certificates_certificate_id FOREIGN KEY (certificate_id) REFERENCES certificates(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='多智能体资源包表';

CREATE TABLE IF NOT EXISTS resource_package_items (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '资源包明细ID',
  package_id BIGINT UNSIGNED NOT NULL COMMENT '资源包ID',
  resource_id BIGINT UNSIGNED NOT NULL COMMENT 'AI生成资源ID',
  item_order INT NOT NULL DEFAULT 0 COMMENT '排序',
  status VARCHAR(32) NOT NULL DEFAULT 'active',
  created_by BIGINT UNSIGNED NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted_at DATETIME NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_resource_package_items_unique (package_id, resource_id),
  KEY idx_resource_package_items_package_id (package_id),
  KEY idx_resource_package_items_resource_id (resource_id),
  CONSTRAINT fk_resource_package_items_resource_packages_package_id FOREIGN KEY (package_id) REFERENCES resource_packages(id),
  CONSTRAINT fk_resource_package_items_ai_generated_resources_resource_id FOREIGN KEY (resource_id) REFERENCES ai_generated_resources(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='资源包明细表';

CREATE TABLE IF NOT EXISTS ai_generation_task_logs (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'AI任务日志ID',
  task_id BIGINT UNSIGNED NOT NULL COMMENT 'AI生成任务ID',
  agent_id BIGINT UNSIGNED NULL COMMENT '执行智能体ID',
  log_level VARCHAR(32) NOT NULL DEFAULT 'info' COMMENT '日志级别',
  log_message VARCHAR(1024) NOT NULL COMMENT '日志消息，不记录敏感明文',
  payload_json JSON NULL COMMENT '结构化日志摘要',
  status VARCHAR(32) NOT NULL DEFAULT 'active',
  created_by BIGINT UNSIGNED NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted_at DATETIME NULL,
  PRIMARY KEY (id),
  KEY idx_ai_generation_task_logs_task_id (task_id),
  KEY idx_ai_generation_task_logs_agent_id (agent_id),
  KEY idx_ai_generation_task_logs_level (log_level),
  CONSTRAINT fk_ai_generation_task_logs_ai_generation_tasks_task_id FOREIGN KEY (task_id) REFERENCES ai_generation_tasks(id),
  CONSTRAINT fk_ai_generation_task_logs_ai_agents_agent_id FOREIGN KEY (agent_id) REFERENCES ai_agents(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI生成任务日志表';

-- =========================================================
-- 5. 竞赛任务、团队、里程碑、交付物
-- =========================================================

CREATE TABLE IF NOT EXISTS competition_tasks (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '竞赛任务ID',
  competition_id BIGINT UNSIGNED NOT NULL COMMENT '竞赛ID',
  task_code VARCHAR(64) NULL COMMENT '任务编码',
  task_title VARCHAR(255) NOT NULL COMMENT '任务标题',
  task_description TEXT NULL COMMENT '任务说明',
  related_capability_tags JSON NULL COMMENT '关联能力标签',
  difficulty VARCHAR(32) NULL COMMENT '难度',
  sort_order INT NOT NULL DEFAULT 0 COMMENT '排序',
  status VARCHAR(32) NOT NULL DEFAULT 'active',
  created_by BIGINT UNSIGNED NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted_at DATETIME NULL,
  PRIMARY KEY (id),
  KEY idx_competition_tasks_competition_id (competition_id),
  KEY idx_competition_tasks_status (status),
  CONSTRAINT fk_competition_tasks_competitions_competition_id FOREIGN KEY (competition_id) REFERENCES competitions(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='竞赛任务点表';

CREATE TABLE IF NOT EXISTS competition_teams (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '竞赛团队ID',
  competition_id BIGINT UNSIGNED NOT NULL COMMENT '竞赛ID',
  team_name VARCHAR(128) NOT NULL COMMENT '团队名称',
  coach_teacher_id BIGINT UNSIGNED NULL COMMENT '带队老师ID',
  team_status VARCHAR(32) NOT NULL DEFAULT 'preparing' COMMENT '团队状态',
  status VARCHAR(32) NOT NULL DEFAULT 'active',
  created_by BIGINT UNSIGNED NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted_at DATETIME NULL,
  PRIMARY KEY (id),
  KEY idx_competition_teams_competition_id (competition_id),
  KEY idx_competition_teams_coach_teacher_id (coach_teacher_id),
  CONSTRAINT fk_competition_teams_competitions_competition_id FOREIGN KEY (competition_id) REFERENCES competitions(id),
  CONSTRAINT fk_competition_teams_teachers_coach_teacher_id FOREIGN KEY (coach_teacher_id) REFERENCES teachers(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='竞赛团队表';

CREATE TABLE IF NOT EXISTS competition_team_members (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '竞赛团队成员ID',
  team_id BIGINT UNSIGNED NOT NULL COMMENT '团队ID',
  student_id BIGINT UNSIGNED NOT NULL COMMENT '学生ID',
  member_role VARCHAR(64) NULL COMMENT '团队角色',
  joined_at DATETIME NULL COMMENT '加入时间',
  status VARCHAR(32) NOT NULL DEFAULT 'active',
  created_by BIGINT UNSIGNED NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted_at DATETIME NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_competition_team_members_unique (team_id, student_id),
  KEY idx_competition_team_members_team_id (team_id),
  KEY idx_competition_team_members_student_id (student_id),
  CONSTRAINT fk_competition_team_members_competition_teams_team_id FOREIGN KEY (team_id) REFERENCES competition_teams(id),
  CONSTRAINT fk_competition_team_members_students_student_id FOREIGN KEY (student_id) REFERENCES students(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='竞赛团队成员表';

CREATE TABLE IF NOT EXISTS competition_milestones (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '竞赛里程碑ID',
  team_id BIGINT UNSIGNED NOT NULL COMMENT '竞赛团队ID',
  milestone_title VARCHAR(255) NOT NULL COMMENT '里程碑标题',
  description TEXT NULL COMMENT '说明',
  due_at DATETIME NULL COMMENT '截止时间',
  completion_status VARCHAR(32) NOT NULL DEFAULT 'not_started' COMMENT '完成状态',
  completed_at DATETIME NULL COMMENT '完成时间',
  status VARCHAR(32) NOT NULL DEFAULT 'active',
  created_by BIGINT UNSIGNED NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted_at DATETIME NULL,
  PRIMARY KEY (id),
  KEY idx_competition_milestones_team_id (team_id),
  KEY idx_competition_milestones_completion_status (completion_status),
  CONSTRAINT fk_competition_milestones_competition_teams_team_id FOREIGN KEY (team_id) REFERENCES competition_teams(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='竞赛里程碑表';

CREATE TABLE IF NOT EXISTS competition_deliverables (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '竞赛交付物ID',
  team_id BIGINT UNSIGNED NOT NULL COMMENT '竞赛团队ID',
  milestone_id BIGINT UNSIGNED NULL COMMENT '里程碑ID',
  title VARCHAR(255) NOT NULL COMMENT '交付物标题',
  deliverable_type VARCHAR(64) NOT NULL COMMENT '交付物类型',
  file_url VARCHAR(512) NULL COMMENT '文件URL',
  content_text MEDIUMTEXT NULL COMMENT '文本内容',
  submitted_by BIGINT UNSIGNED NOT NULL COMMENT '提交人用户ID',
  submitted_at DATETIME NULL COMMENT '提交时间',
  review_status VARCHAR(32) NOT NULL DEFAULT 'pending' COMMENT '审核状态',
  status VARCHAR(32) NOT NULL DEFAULT 'submitted',
  created_by BIGINT UNSIGNED NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted_at DATETIME NULL,
  PRIMARY KEY (id),
  KEY idx_competition_deliverables_team_id (team_id),
  KEY idx_competition_deliverables_milestone_id (milestone_id),
  KEY idx_competition_deliverables_submitted_by (submitted_by),
  KEY idx_competition_deliverables_review_status (review_status),
  CONSTRAINT fk_competition_deliverables_competition_teams_team_id FOREIGN KEY (team_id) REFERENCES competition_teams(id),
  CONSTRAINT fk_competition_deliverables_competition_milestones_milestone_id FOREIGN KEY (milestone_id) REFERENCES competition_milestones(id),
  CONSTRAINT fk_competition_deliverables_users_submitted_by FOREIGN KEY (submitted_by) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='竞赛交付物表';

-- =========================================================
-- 6. 证书能力单元、考核点、专项练习
-- =========================================================

CREATE TABLE IF NOT EXISTS certificate_units (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '证书能力单元ID',
  certificate_id BIGINT UNSIGNED NOT NULL COMMENT '证书ID',
  unit_code VARCHAR(64) NULL COMMENT '能力单元编码',
  unit_name VARCHAR(128) NOT NULL COMMENT '能力单元名称',
  description TEXT NULL COMMENT '能力单元说明',
  weight DECIMAL(5,2) NOT NULL DEFAULT 1.00 COMMENT '权重',
  sort_order INT NOT NULL DEFAULT 0 COMMENT '排序',
  status VARCHAR(32) NOT NULL DEFAULT 'active',
  created_by BIGINT UNSIGNED NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted_at DATETIME NULL,
  PRIMARY KEY (id),
  KEY idx_certificate_units_certificate_id (certificate_id),
  CONSTRAINT fk_certificate_units_certificates_certificate_id FOREIGN KEY (certificate_id) REFERENCES certificates(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='证书能力单元表';

CREATE TABLE IF NOT EXISTS certificate_assessment_points (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '证书考核点ID',
  unit_id BIGINT UNSIGNED NOT NULL COMMENT '证书能力单元ID',
  point_code VARCHAR(64) NULL COMMENT '考核点编码',
  point_name VARCHAR(128) NOT NULL COMMENT '考核点名称',
  description TEXT NULL COMMENT '考核点说明',
  difficulty VARCHAR(32) NULL COMMENT '难度',
  score_weight DECIMAL(5,2) NOT NULL DEFAULT 1.00 COMMENT '分值权重',
  sort_order INT NOT NULL DEFAULT 0 COMMENT '排序',
  status VARCHAR(32) NOT NULL DEFAULT 'active',
  created_by BIGINT UNSIGNED NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted_at DATETIME NULL,
  PRIMARY KEY (id),
  KEY idx_certificate_assessment_points_unit_id (unit_id),
  KEY idx_certificate_assessment_points_difficulty (difficulty),
  CONSTRAINT fk_certificate_assessment_points_certificate_units_unit_id FOREIGN KEY (unit_id) REFERENCES certificate_units(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='证书考核点表';

CREATE TABLE IF NOT EXISTS certificate_practice_attempts (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '证书专项练习ID',
  certificate_id BIGINT UNSIGNED NOT NULL COMMENT '证书ID',
  assessment_point_id BIGINT UNSIGNED NULL COMMENT '考核点ID',
  student_id BIGINT UNSIGNED NOT NULL COMMENT '学生ID',
  question_snapshot JSON NOT NULL COMMENT '题目快照',
  answer TEXT NULL COMMENT '学生答案',
  is_correct TINYINT(1) NULL COMMENT '是否正确',
  score DECIMAL(5,2) NULL COMMENT '得分',
  status VARCHAR(32) NOT NULL DEFAULT 'active',
  created_by BIGINT UNSIGNED NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted_at DATETIME NULL,
  PRIMARY KEY (id),
  KEY idx_certificate_practice_attempts_certificate_id (certificate_id),
  KEY idx_certificate_practice_attempts_assessment_point_id (assessment_point_id),
  KEY idx_certificate_practice_attempts_student_id (student_id),
  CONSTRAINT fk_certificate_practice_attempts_certificates_certificate_id FOREIGN KEY (certificate_id) REFERENCES certificates(id),
  CONSTRAINT fk_cert_practice_attempts_assessment_point_id FOREIGN KEY (assessment_point_id) REFERENCES certificate_assessment_points(id),
  CONSTRAINT fk_certificate_practice_attempts_students_student_id FOREIGN KEY (student_id) REFERENCES students(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='证书专项练习记录表';

-- =========================================================
-- 7. 教师任务
-- =========================================================

CREATE TABLE IF NOT EXISTS teacher_tasks (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '教师任务ID',
  teacher_id BIGINT UNSIGNED NOT NULL COMMENT '发布教师ID',
  task_title VARCHAR(255) NOT NULL COMMENT '任务标题',
  task_type VARCHAR(64) NOT NULL COMMENT '任务类型：course/competition/certificate/project',
  task_description TEXT NULL COMMENT '任务说明',
  due_at DATETIME NULL COMMENT '截止时间',
  publish_status VARCHAR(32) NOT NULL DEFAULT 'draft' COMMENT '发布状态',
  published_at DATETIME NULL COMMENT '发布时间',
  status VARCHAR(32) NOT NULL DEFAULT 'draft',
  created_by BIGINT UNSIGNED NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted_at DATETIME NULL,
  PRIMARY KEY (id),
  KEY idx_teacher_tasks_teacher_id (teacher_id),
  KEY idx_teacher_tasks_task_type (task_type),
  KEY idx_teacher_tasks_publish_status (publish_status),
  CONSTRAINT fk_teacher_tasks_teachers_teacher_id FOREIGN KEY (teacher_id) REFERENCES teachers(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='教师任务表';

CREATE TABLE IF NOT EXISTS teacher_task_targets (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '教师任务对象ID',
  task_id BIGINT UNSIGNED NOT NULL COMMENT '教师任务ID',
  target_type VARCHAR(64) NOT NULL COMMENT '对象类型：student/class/major/group/project',
  target_id BIGINT UNSIGNED NOT NULL COMMENT '对象ID',
  completion_status VARCHAR(32) NOT NULL DEFAULT 'not_started' COMMENT '完成状态',
  completed_at DATETIME NULL COMMENT '完成时间',
  status VARCHAR(32) NOT NULL DEFAULT 'active',
  created_by BIGINT UNSIGNED NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted_at DATETIME NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_teacher_task_targets_unique (task_id, target_type, target_id),
  KEY idx_teacher_task_targets_task_id (task_id),
  KEY idx_teacher_task_targets_target (target_type, target_id),
  KEY idx_teacher_task_targets_completion_status (completion_status),
  CONSTRAINT fk_teacher_task_targets_teacher_tasks_task_id FOREIGN KEY (task_id) REFERENCES teacher_tasks(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='教师任务下发对象表';

-- =========================================================
-- 8. 项目成员、任务、里程碑、交付物
-- =========================================================

CREATE TABLE IF NOT EXISTS project_members (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '项目成员ID',
  project_id BIGINT UNSIGNED NOT NULL COMMENT '项目ID',
  student_id BIGINT UNSIGNED NOT NULL COMMENT '学生ID',
  member_role VARCHAR(64) NULL COMMENT '项目角色',
  joined_at DATETIME NULL COMMENT '加入时间',
  status VARCHAR(32) NOT NULL DEFAULT 'active',
  created_by BIGINT UNSIGNED NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted_at DATETIME NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_project_members_unique (project_id, student_id),
  KEY idx_project_members_project_id (project_id),
  KEY idx_project_members_student_id (student_id),
  CONSTRAINT fk_project_members_projects_project_id FOREIGN KEY (project_id) REFERENCES projects(id),
  CONSTRAINT fk_project_members_students_student_id FOREIGN KEY (student_id) REFERENCES students(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='项目成员表';

CREATE TABLE IF NOT EXISTS project_tasks (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '项目任务ID',
  project_id BIGINT UNSIGNED NOT NULL COMMENT '项目ID',
  parent_id BIGINT UNSIGNED NULL COMMENT '父级任务ID',
  assigned_student_id BIGINT UNSIGNED NULL COMMENT '指派学生ID',
  task_title VARCHAR(255) NOT NULL COMMENT '任务标题',
  task_description TEXT NULL COMMENT '任务说明',
  priority VARCHAR(32) NULL COMMENT '优先级',
  due_at DATETIME NULL COMMENT '截止时间',
  completion_status VARCHAR(32) NOT NULL DEFAULT 'not_started' COMMENT '完成状态',
  completed_at DATETIME NULL COMMENT '完成时间',
  status VARCHAR(32) NOT NULL DEFAULT 'active',
  created_by BIGINT UNSIGNED NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted_at DATETIME NULL,
  PRIMARY KEY (id),
  KEY idx_project_tasks_project_id (project_id),
  KEY idx_project_tasks_parent_id (parent_id),
  KEY idx_project_tasks_assigned_student_id (assigned_student_id),
  KEY idx_project_tasks_completion_status (completion_status),
  CONSTRAINT fk_project_tasks_projects_project_id FOREIGN KEY (project_id) REFERENCES projects(id),
  CONSTRAINT fk_project_tasks_self_parent_id FOREIGN KEY (parent_id) REFERENCES project_tasks(id),
  CONSTRAINT fk_project_tasks_students_assigned_student_id FOREIGN KEY (assigned_student_id) REFERENCES students(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='项目任务表';

CREATE TABLE IF NOT EXISTS project_milestones (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '项目里程碑ID',
  project_id BIGINT UNSIGNED NOT NULL COMMENT '项目ID',
  milestone_title VARCHAR(255) NOT NULL COMMENT '里程碑标题',
  description TEXT NULL COMMENT '说明',
  due_at DATETIME NULL COMMENT '截止时间',
  completion_status VARCHAR(32) NOT NULL DEFAULT 'not_started' COMMENT '完成状态',
  completed_at DATETIME NULL COMMENT '完成时间',
  status VARCHAR(32) NOT NULL DEFAULT 'active',
  created_by BIGINT UNSIGNED NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted_at DATETIME NULL,
  PRIMARY KEY (id),
  KEY idx_project_milestones_project_id (project_id),
  KEY idx_project_milestones_completion_status (completion_status),
  CONSTRAINT fk_project_milestones_projects_project_id FOREIGN KEY (project_id) REFERENCES projects(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='项目里程碑表';

CREATE TABLE IF NOT EXISTS project_deliverables (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '项目交付物ID',
  project_id BIGINT UNSIGNED NOT NULL COMMENT '项目ID',
  task_id BIGINT UNSIGNED NULL COMMENT '项目任务ID',
  milestone_id BIGINT UNSIGNED NULL COMMENT '项目里程碑ID',
  student_id BIGINT UNSIGNED NULL COMMENT '提交学生ID',
  title VARCHAR(255) NOT NULL COMMENT '交付物标题',
  deliverable_type VARCHAR(64) NOT NULL COMMENT '交付物类型',
  file_url VARCHAR(512) NULL COMMENT '文件URL',
  content_text MEDIUMTEXT NULL COMMENT '文本内容',
  submitted_at DATETIME NULL COMMENT '提交时间',
  review_status VARCHAR(32) NOT NULL DEFAULT 'pending' COMMENT '审核状态',
  score DECIMAL(5,2) NULL COMMENT '评分',
  teacher_comment TEXT NULL COMMENT '教师评价',
  status VARCHAR(32) NOT NULL DEFAULT 'submitted',
  created_by BIGINT UNSIGNED NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted_at DATETIME NULL,
  PRIMARY KEY (id),
  KEY idx_project_deliverables_project_id (project_id),
  KEY idx_project_deliverables_task_id (task_id),
  KEY idx_project_deliverables_milestone_id (milestone_id),
  KEY idx_project_deliverables_student_id (student_id),
  KEY idx_project_deliverables_review_status (review_status),
  CONSTRAINT fk_project_deliverables_projects_project_id FOREIGN KEY (project_id) REFERENCES projects(id),
  CONSTRAINT fk_project_deliverables_project_tasks_task_id FOREIGN KEY (task_id) REFERENCES project_tasks(id),
  CONSTRAINT fk_project_deliverables_project_milestones_milestone_id FOREIGN KEY (milestone_id) REFERENCES project_milestones(id),
  CONSTRAINT fk_project_deliverables_students_student_id FOREIGN KEY (student_id) REFERENCES students(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='项目交付物表';

-- =========================================================
-- 9. 升级完成提示
-- =========================================================

SELECT '001_fusion_api_upgrade completed' AS migration_result;
