CREATE DATABASE IF NOT EXISTS ai_learning_platform
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_unicode_ci;

USE ai_learning_platform;

-- =========================================================
-- 1. 用户权限
-- =========================================================

CREATE TABLE users (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  username VARCHAR(64) NOT NULL COMMENT '登录用户名，学生默认使用学号',
  password_hash VARCHAR(255) NOT NULL COMMENT '密码哈希，使用BCrypt或Argon2',
  real_name VARCHAR(64) NOT NULL COMMENT '真实姓名',
  phone_encrypted VARCHAR(512) NULL COMMENT '手机号密文',
  phone_iv VARCHAR(128) NULL COMMENT '手机号加密IV',
  phone_hash CHAR(64) NULL COMMENT '手机号hash，用于精确检索',
  email_encrypted VARCHAR(512) NULL COMMENT '邮箱密文',
  email_iv VARCHAR(128) NULL COMMENT '邮箱加密IV',
  email_hash CHAR(64) NULL COMMENT '邮箱hash，用于精确检索',
  account_status VARCHAR(32) NOT NULL DEFAULT 'active' COMMENT '账号状态',
  must_change_password TINYINT(1) NOT NULL DEFAULT 1 COMMENT '是否首次登录强制改密',
  last_login_at DATETIME NULL COMMENT '最后登录时间',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted_at DATETIME NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_users_username (username),
  KEY idx_users_phone_hash (phone_hash),
  KEY idx_users_email_hash (email_hash),
  KEY idx_users_account_status (account_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='登录账号表';

CREATE TABLE roles (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  code VARCHAR(64) NOT NULL COMMENT '角色编码',
  name VARCHAR(64) NOT NULL COMMENT '角色名称',
  data_scope VARCHAR(64) NOT NULL COMMENT '数据范围',
  is_core TINYINT(1) NOT NULL DEFAULT 1 COMMENT '是否核心角色',
  description VARCHAR(512) NULL COMMENT '角色说明',
  status VARCHAR(32) NOT NULL DEFAULT 'active',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted_at DATETIME NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_roles_code (code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色表';

CREATE TABLE permissions (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '权限ID',
  code VARCHAR(128) NOT NULL COMMENT '权限编码',
  name VARCHAR(128) NOT NULL COMMENT '权限名称',
  module VARCHAR(64) NOT NULL COMMENT '所属模块',
  permission_type VARCHAR(32) NOT NULL COMMENT '权限类型：menu/button/data/action',
  description VARCHAR(512) NULL COMMENT '权限说明',
  status VARCHAR(32) NOT NULL DEFAULT 'active',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted_at DATETIME NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_permissions_code (code),
  KEY idx_permissions_module (module)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='权限表';

CREATE TABLE user_roles (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  user_id BIGINT UNSIGNED NOT NULL COMMENT '用户ID',
  role_id BIGINT UNSIGNED NOT NULL COMMENT '角色ID',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY uk_user_roles_user_role (user_id, role_id),
  KEY idx_user_roles_role_id (role_id),
  CONSTRAINT fk_user_roles_users_user_id FOREIGN KEY (user_id) REFERENCES users(id),
  CONSTRAINT fk_user_roles_roles_role_id FOREIGN KEY (role_id) REFERENCES roles(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户角色绑定表';

CREATE TABLE role_permissions (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  role_id BIGINT UNSIGNED NOT NULL COMMENT '角色ID',
  permission_id BIGINT UNSIGNED NOT NULL COMMENT '权限ID',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY uk_role_permissions_role_permission (role_id, permission_id),
  KEY idx_role_permissions_permission_id (permission_id),
  CONSTRAINT fk_role_permissions_roles_role_id FOREIGN KEY (role_id) REFERENCES roles(id),
  CONSTRAINT fk_role_permissions_permissions_permission_id FOREIGN KEY (permission_id) REFERENCES permissions(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色权限绑定表';

-- =========================================================
-- 2. 基础数据与用户扩展
-- =========================================================

CREATE TABLE colleges (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '学院ID',
  code VARCHAR(64) NOT NULL COMMENT '学院编码',
  name VARCHAR(128) NOT NULL COMMENT '学院名称',
  status VARCHAR(32) NOT NULL DEFAULT 'active',
  created_by BIGINT UNSIGNED NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted_at DATETIME NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_colleges_code (code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='学院表';

CREATE TABLE majors (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '专业ID',
  college_id BIGINT UNSIGNED NOT NULL COMMENT '所属学院ID',
  code VARCHAR(64) NOT NULL COMMENT '专业编码',
  name VARCHAR(128) NOT NULL COMMENT '专业名称',
  status VARCHAR(32) NOT NULL DEFAULT 'active',
  created_by BIGINT UNSIGNED NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted_at DATETIME NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_majors_code (code),
  KEY idx_majors_college_id (college_id),
  CONSTRAINT fk_majors_colleges_college_id FOREIGN KEY (college_id) REFERENCES colleges(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='专业表';

CREATE TABLE classes (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '班级ID',
  major_id BIGINT UNSIGNED NOT NULL COMMENT '所属专业ID',
  grade VARCHAR(16) NOT NULL COMMENT '年级',
  name VARCHAR(128) NOT NULL COMMENT '班级名称',
  status VARCHAR(32) NOT NULL DEFAULT 'active',
  created_by BIGINT UNSIGNED NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted_at DATETIME NULL,
  PRIMARY KEY (id),
  KEY idx_classes_major_id (major_id),
  KEY idx_classes_grade (grade),
  CONSTRAINT fk_classes_majors_major_id FOREIGN KEY (major_id) REFERENCES majors(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='班级表';

CREATE TABLE students (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '学生ID',
  user_id BIGINT UNSIGNED NOT NULL COMMENT '关联用户ID',
  student_no VARCHAR(64) NOT NULL COMMENT '学号',
  college_id BIGINT UNSIGNED NOT NULL COMMENT '学院ID',
  major_id BIGINT UNSIGNED NOT NULL COMMENT '专业ID',
  class_id BIGINT UNSIGNED NOT NULL COMMENT '班级ID',
  grade VARCHAR(16) NOT NULL COMMENT '年级',
  gender VARCHAR(16) NULL COMMENT '性别',
  enrollment_status VARCHAR(32) NOT NULL DEFAULT 'studying' COMMENT '学籍状态',
  phone_encrypted VARCHAR(512) NULL COMMENT '学生手机号密文',
  phone_iv VARCHAR(128) NULL COMMENT '学生手机号IV',
  phone_hash CHAR(64) NULL COMMENT '学生手机号hash',
  email_encrypted VARCHAR(512) NULL COMMENT '学生邮箱密文',
  email_iv VARCHAR(128) NULL COMMENT '学生邮箱IV',
  email_hash CHAR(64) NULL COMMENT '学生邮箱hash',
  id_card_no_encrypted VARCHAR(512) NULL COMMENT '身份证号密文',
  id_card_no_iv VARCHAR(128) NULL COMMENT '身份证号IV',
  id_card_no_hash CHAR(64) NULL COMMENT '身份证号hash',
  status VARCHAR(32) NOT NULL DEFAULT 'active',
  created_by BIGINT UNSIGNED NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted_at DATETIME NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_students_user_id (user_id),
  UNIQUE KEY uk_students_student_no (student_no),
  KEY idx_students_college_id (college_id),
  KEY idx_students_major_id (major_id),
  KEY idx_students_class_id (class_id),
  KEY idx_students_phone_hash (phone_hash),
  KEY idx_students_email_hash (email_hash),
  KEY idx_students_id_card_no_hash (id_card_no_hash),
  CONSTRAINT fk_students_users_user_id FOREIGN KEY (user_id) REFERENCES users(id),
  CONSTRAINT fk_students_colleges_college_id FOREIGN KEY (college_id) REFERENCES colleges(id),
  CONSTRAINT fk_students_majors_major_id FOREIGN KEY (major_id) REFERENCES majors(id),
  CONSTRAINT fk_students_classes_class_id FOREIGN KEY (class_id) REFERENCES classes(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='学生扩展信息表';

CREATE TABLE teachers (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '教师ID',
  user_id BIGINT UNSIGNED NOT NULL COMMENT '关联用户ID',
  teacher_no VARCHAR(64) NOT NULL COMMENT '教师工号',
  college_id BIGINT UNSIGNED NOT NULL COMMENT '学院ID',
  title VARCHAR(64) NULL COMMENT '职称',
  phone_encrypted VARCHAR(512) NULL COMMENT '教师手机号密文',
  phone_iv VARCHAR(128) NULL COMMENT '教师手机号IV',
  phone_hash CHAR(64) NULL COMMENT '教师手机号hash',
  email_encrypted VARCHAR(512) NULL COMMENT '教师邮箱密文',
  email_iv VARCHAR(128) NULL COMMENT '教师邮箱IV',
  email_hash CHAR(64) NULL COMMENT '教师邮箱hash',
  status VARCHAR(32) NOT NULL DEFAULT 'active',
  created_by BIGINT UNSIGNED NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted_at DATETIME NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_teachers_user_id (user_id),
  UNIQUE KEY uk_teachers_teacher_no (teacher_no),
  KEY idx_teachers_college_id (college_id),
  KEY idx_teachers_phone_hash (phone_hash),
  KEY idx_teachers_email_hash (email_hash),
  CONSTRAINT fk_teachers_users_user_id FOREIGN KEY (user_id) REFERENCES users(id),
  CONSTRAINT fk_teachers_colleges_college_id FOREIGN KEY (college_id) REFERENCES colleges(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='教师扩展信息表';

CREATE TABLE teacher_duty_tags (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  teacher_id BIGINT UNSIGNED NOT NULL COMMENT '教师ID',
  tag_code VARCHAR(64) NOT NULL COMMENT '职责标签编码',
  tag_name VARCHAR(64) NOT NULL COMMENT '职责标签名称',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY uk_teacher_duty_tags_teacher_tag (teacher_id, tag_code),
  KEY idx_teacher_duty_tags_tag_code (tag_code),
  CONSTRAINT fk_teacher_duty_tags_teachers_teacher_id FOREIGN KEY (teacher_id) REFERENCES teachers(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='教师职责标签表';

CREATE TABLE teacher_student_groups (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  teacher_id BIGINT UNSIGNED NOT NULL COMMENT '小组负责教师ID',
  student_id BIGINT UNSIGNED NOT NULL COMMENT '学生ID',
  group_name VARCHAR(128) NOT NULL COMMENT '分组名称',
  bind_type VARCHAR(32) NOT NULL DEFAULT 'custom_group' COMMENT '绑定类型',
  status VARCHAR(32) NOT NULL DEFAULT 'active',
  created_by BIGINT UNSIGNED NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted_at DATETIME NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_teacher_student_groups_teacher_student (teacher_id, student_id),
  KEY idx_teacher_student_groups_student_id (student_id),
  KEY idx_teacher_student_groups_group_name (group_name),
  CONSTRAINT fk_teacher_student_groups_teachers_teacher_id FOREIGN KEY (teacher_id) REFERENCES teachers(id),
  CONSTRAINT fk_teacher_student_groups_students_student_id FOREIGN KEY (student_id) REFERENCES students(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='教师学生分组表';

CREATE TABLE enterprises (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '企业ID',
  name VARCHAR(128) NOT NULL COMMENT '企业名称',
  industry VARCHAR(128) NULL COMMENT '所属行业',
  contact_name VARCHAR(64) NULL COMMENT '联系人姓名',
  contact_phone_encrypted VARCHAR(512) NULL COMMENT '联系人电话密文',
  contact_phone_iv VARCHAR(128) NULL COMMENT '联系人电话IV',
  contact_phone_hash CHAR(64) NULL COMMENT '联系人电话hash',
  status VARCHAR(32) NOT NULL DEFAULT 'active',
  created_by BIGINT UNSIGNED NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted_at DATETIME NULL,
  PRIMARY KEY (id),
  KEY idx_enterprises_name (name),
  KEY idx_enterprises_contact_phone_hash (contact_phone_hash)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='合作企业表';

CREATE TABLE enterprise_mentors (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '企业导师ID',
  user_id BIGINT UNSIGNED NOT NULL COMMENT '关联用户ID',
  enterprise_id BIGINT UNSIGNED NOT NULL COMMENT '企业ID',
  position VARCHAR(128) NULL COMMENT '企业职位',
  contact_phone_encrypted VARCHAR(512) NULL COMMENT '联系电话密文',
  contact_phone_iv VARCHAR(128) NULL COMMENT '联系电话IV',
  contact_phone_hash CHAR(64) NULL COMMENT '联系电话hash',
  email_encrypted VARCHAR(512) NULL COMMENT '邮箱密文',
  email_iv VARCHAR(128) NULL COMMENT '邮箱IV',
  email_hash CHAR(64) NULL COMMENT '邮箱hash',
  status VARCHAR(32) NOT NULL DEFAULT 'active',
  created_by BIGINT UNSIGNED NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted_at DATETIME NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_enterprise_mentors_user_id (user_id),
  KEY idx_enterprise_mentors_enterprise_id (enterprise_id),
  KEY idx_enterprise_mentors_phone_hash (contact_phone_hash),
  KEY idx_enterprise_mentors_email_hash (email_hash),
  CONSTRAINT fk_enterprise_mentors_users_user_id FOREIGN KEY (user_id) REFERENCES users(id),
  CONSTRAINT fk_enterprise_mentors_enterprises_enterprise_id FOREIGN KEY (enterprise_id) REFERENCES enterprises(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='企业导师表';

-- =========================================================
-- 3. AI智能体与课程基础
-- =========================================================

CREATE TABLE ai_agents (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '智能体ID',
  code VARCHAR(64) NOT NULL COMMENT '智能体编码',
  name VARCHAR(128) NOT NULL COMMENT '智能体名称',
  agent_type VARCHAR(64) NOT NULL COMMENT '智能体类型',
  model_name VARCHAR(128) NULL COMMENT '模型名称',
  config_json JSON NULL COMMENT '智能体配置',
  enabled TINYINT(1) NOT NULL DEFAULT 1 COMMENT '是否启用',
  status VARCHAR(32) NOT NULL DEFAULT 'active',
  created_by BIGINT UNSIGNED NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted_at DATETIME NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_ai_agents_code (code),
  KEY idx_ai_agents_agent_type (agent_type),
  KEY idx_ai_agents_enabled (enabled)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI智能体配置表';

CREATE TABLE courses (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '课程ID',
  course_code VARCHAR(64) NOT NULL COMMENT '课程编码',
  course_name VARCHAR(128) NOT NULL COMMENT '课程名称',
  major_id BIGINT UNSIGNED NOT NULL COMMENT '所属专业ID',
  credit DECIMAL(4,1) NULL COMMENT '学分',
  semester VARCHAR(32) NULL COMMENT '学期',
  status VARCHAR(32) NOT NULL DEFAULT 'active',
  created_by BIGINT UNSIGNED NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted_at DATETIME NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_courses_course_code (course_code),
  KEY idx_courses_major_id (major_id),
  CONSTRAINT fk_courses_majors_major_id FOREIGN KEY (major_id) REFERENCES majors(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='课程表';

CREATE TABLE course_knowledge_points (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '知识点ID',
  course_id BIGINT UNSIGNED NOT NULL COMMENT '课程ID',
  parent_id BIGINT UNSIGNED NULL COMMENT '父级知识点ID',
  name VARCHAR(128) NOT NULL COMMENT '知识点名称',
  description TEXT NULL COMMENT '知识点描述',
  difficulty_level VARCHAR(32) NULL COMMENT '难度等级',
  sort_order INT NOT NULL DEFAULT 0 COMMENT '排序',
  status VARCHAR(32) NOT NULL DEFAULT 'active',
  created_by BIGINT UNSIGNED NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted_at DATETIME NULL,
  PRIMARY KEY (id),
  KEY idx_course_knowledge_points_course_id (course_id),
  KEY idx_course_knowledge_points_parent_id (parent_id),
  CONSTRAINT fk_course_knowledge_points_courses_course_id FOREIGN KEY (course_id) REFERENCES courses(id),
  CONSTRAINT fk_course_knowledge_points_self_parent_id FOREIGN KEY (parent_id) REFERENCES course_knowledge_points(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='课程知识点表';

-- =========================================================
-- 4. 学生画像
-- =========================================================

CREATE TABLE profile_conversations (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  student_id BIGINT UNSIGNED NOT NULL COMMENT '学生ID',
  agent_id BIGINT UNSIGNED NULL COMMENT '画像构建智能体ID',
  message_role VARCHAR(32) NOT NULL COMMENT '消息角色：student/assistant/system',
  message_content_encrypted MEDIUMTEXT NOT NULL COMMENT '对话内容密文',
  message_content_iv VARCHAR(128) NOT NULL COMMENT '对话内容IV',
  extracted_features_encrypted MEDIUMTEXT NULL COMMENT '抽取特征密文',
  extracted_features_iv VARCHAR(128) NULL COMMENT '抽取特征IV',
  status VARCHAR(32) NOT NULL DEFAULT 'active',
  created_by BIGINT UNSIGNED NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted_at DATETIME NULL,
  PRIMARY KEY (id),
  KEY idx_profile_conversations_student_id (student_id),
  KEY idx_profile_conversations_agent_id (agent_id),
  CONSTRAINT fk_profile_conversations_students_student_id FOREIGN KEY (student_id) REFERENCES students(id),
  CONSTRAINT fk_profile_conversations_ai_agents_agent_id FOREIGN KEY (agent_id) REFERENCES ai_agents(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='画像对话记录表';

CREATE TABLE student_profiles (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  student_id BIGINT UNSIGNED NOT NULL COMMENT '学生ID',
  profile_version INT NOT NULL DEFAULT 1 COMMENT '画像版本',
  profile_summary_encrypted MEDIUMTEXT NULL COMMENT '画像摘要密文',
  profile_summary_iv VARCHAR(128) NULL COMMENT '画像摘要IV',
  completeness_score DECIMAL(5,2) NOT NULL DEFAULT 0.00 COMMENT '画像完整度',
  last_generated_at DATETIME NULL COMMENT '最后生成时间',
  status VARCHAR(32) NOT NULL DEFAULT 'active',
  created_by BIGINT UNSIGNED NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted_at DATETIME NULL,
  PRIMARY KEY (id),
  KEY idx_student_profiles_student_id (student_id),
  KEY idx_student_profiles_status (status),
  CONSTRAINT fk_student_profiles_students_student_id FOREIGN KEY (student_id) REFERENCES students(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='学生画像主表';

CREATE TABLE profile_dimension_values (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  profile_id BIGINT UNSIGNED NOT NULL COMMENT '画像ID',
  dimension_code VARCHAR(64) NOT NULL COMMENT '画像维度编码',
  dimension_name VARCHAR(128) NOT NULL COMMENT '画像维度名称',
  dimension_value_encrypted MEDIUMTEXT NULL COMMENT '画像维度值密文',
  dimension_value_iv VARCHAR(128) NULL COMMENT '画像维度值IV',
  confidence_score DECIMAL(5,2) NULL COMMENT '置信度',
  source_type VARCHAR(64) NULL COMMENT '来源类型',
  status VARCHAR(32) NOT NULL DEFAULT 'active',
  created_by BIGINT UNSIGNED NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted_at DATETIME NULL,
  PRIMARY KEY (id),
  KEY idx_profile_dimension_values_profile_id (profile_id),
  KEY idx_profile_dimension_values_dimension_code (dimension_code),
  CONSTRAINT fk_profile_dimension_values_student_profiles_profile_id FOREIGN KEY (profile_id) REFERENCES student_profiles(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='画像维度值表';

CREATE TABLE profile_update_logs (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  student_id BIGINT UNSIGNED NOT NULL COMMENT '学生ID',
  source_type VARCHAR(64) NOT NULL COMMENT '更新来源类型',
  source_id BIGINT UNSIGNED NULL COMMENT '更新来源ID',
  before_snapshot JSON NULL COMMENT '更新前快照',
  after_snapshot JSON NULL COMMENT '更新后快照',
  updated_reason VARCHAR(512) NULL COMMENT '更新原因',
  status VARCHAR(32) NOT NULL DEFAULT 'active',
  created_by BIGINT UNSIGNED NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted_at DATETIME NULL,
  PRIMARY KEY (id),
  KEY idx_profile_update_logs_student_id (student_id),
  KEY idx_profile_update_logs_source (source_type, source_id),
  CONSTRAINT fk_profile_update_logs_students_student_id FOREIGN KEY (student_id) REFERENCES students(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='画像更新日志表';

-- =========================================================
-- 5. AI学习中心
-- =========================================================

CREATE TABLE ai_generation_tasks (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  student_id BIGINT UNSIGNED NOT NULL COMMENT '发起学生ID',
  agent_id BIGINT UNSIGNED NOT NULL COMMENT '主执行智能体ID',
  task_type VARCHAR(64) NOT NULL COMMENT '任务类型',
  prompt TEXT NOT NULL COMMENT '生成提示词，避免写入敏感明文',
  context_snapshot JSON NULL COMMENT '上下文快照',
  task_status VARCHAR(32) NOT NULL DEFAULT 'queued' COMMENT '任务状态',
  error_message TEXT NULL COMMENT '失败原因',
  started_at DATETIME NULL COMMENT '开始时间',
  finished_at DATETIME NULL COMMENT '完成时间',
  status VARCHAR(32) NOT NULL DEFAULT 'active',
  created_by BIGINT UNSIGNED NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted_at DATETIME NULL,
  PRIMARY KEY (id),
  KEY idx_ai_generation_tasks_student_id (student_id),
  KEY idx_ai_generation_tasks_agent_id (agent_id),
  KEY idx_ai_generation_tasks_task_status (task_status),
  CONSTRAINT fk_ai_generation_tasks_students_student_id FOREIGN KEY (student_id) REFERENCES students(id),
  CONSTRAINT fk_ai_generation_tasks_ai_agents_agent_id FOREIGN KEY (agent_id) REFERENCES ai_agents(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI资源生成任务表';

CREATE TABLE ai_generated_resources (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  task_id BIGINT UNSIGNED NOT NULL COMMENT '生成任务ID',
  resource_type VARCHAR(64) NOT NULL COMMENT '资源类型',
  title VARCHAR(255) NOT NULL COMMENT '资源标题',
  content_url VARCHAR(512) NULL COMMENT '资源文件URL',
  content_text MEDIUMTEXT NULL COMMENT '生成内容文本',
  metadata_json JSON NULL COMMENT '资源元数据',
  status VARCHAR(32) NOT NULL DEFAULT 'active',
  created_by BIGINT UNSIGNED NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted_at DATETIME NULL,
  PRIMARY KEY (id),
  KEY idx_ai_generated_resources_task_id (task_id),
  KEY idx_ai_generated_resources_resource_type (resource_type),
  CONSTRAINT fk_ai_generated_resources_ai_generation_tasks_task_id FOREIGN KEY (task_id) REFERENCES ai_generation_tasks(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI生成资源表';

CREATE TABLE learning_paths (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  student_id BIGINT UNSIGNED NOT NULL COMMENT '学生ID',
  title VARCHAR(255) NOT NULL COMMENT '学习路径标题',
  goal TEXT NULL COMMENT '学习目标',
  generated_by_agent_id BIGINT UNSIGNED NULL COMMENT '生成智能体ID',
  path_status VARCHAR(32) NOT NULL DEFAULT 'active' COMMENT '路径状态',
  status VARCHAR(32) NOT NULL DEFAULT 'active',
  created_by BIGINT UNSIGNED NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted_at DATETIME NULL,
  PRIMARY KEY (id),
  KEY idx_learning_paths_student_id (student_id),
  KEY idx_learning_paths_generated_by_agent_id (generated_by_agent_id),
  CONSTRAINT fk_learning_paths_students_student_id FOREIGN KEY (student_id) REFERENCES students(id),
  CONSTRAINT fk_learning_paths_ai_agents_generated_by_agent_id FOREIGN KEY (generated_by_agent_id) REFERENCES ai_agents(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='学习路径表';

CREATE TABLE learning_path_steps (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  path_id BIGINT UNSIGNED NOT NULL COMMENT '学习路径ID',
  step_order INT NOT NULL COMMENT '步骤顺序',
  title VARCHAR(255) NOT NULL COMMENT '步骤标题',
  resource_id BIGINT UNSIGNED NULL COMMENT '关联资源ID',
  expected_duration INT NULL COMMENT '预计学习时长，分钟',
  completion_status VARCHAR(32) NOT NULL DEFAULT 'not_started' COMMENT '完成状态',
  status VARCHAR(32) NOT NULL DEFAULT 'active',
  created_by BIGINT UNSIGNED NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted_at DATETIME NULL,
  PRIMARY KEY (id),
  KEY idx_learning_path_steps_path_id (path_id),
  KEY idx_learning_path_steps_resource_id (resource_id),
  CONSTRAINT fk_learning_path_steps_learning_paths_path_id FOREIGN KEY (path_id) REFERENCES learning_paths(id),
  CONSTRAINT fk_learning_path_steps_ai_generated_resources_resource_id FOREIGN KEY (resource_id) REFERENCES ai_generated_resources(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='学习路径步骤表';

CREATE TABLE resource_recommendations (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  student_id BIGINT UNSIGNED NOT NULL COMMENT '学生ID',
  resource_id BIGINT UNSIGNED NOT NULL COMMENT '推荐资源ID',
  recommend_reason VARCHAR(512) NULL COMMENT '推荐原因',
  source_profile_id BIGINT UNSIGNED NULL COMMENT '来源画像ID',
  view_status VARCHAR(32) NOT NULL DEFAULT 'unread' COMMENT '查看状态',
  status VARCHAR(32) NOT NULL DEFAULT 'active',
  created_by BIGINT UNSIGNED NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted_at DATETIME NULL,
  PRIMARY KEY (id),
  KEY idx_resource_recommendations_student_id (student_id),
  KEY idx_resource_recommendations_resource_id (resource_id),
  KEY idx_resource_recommendations_source_profile_id (source_profile_id),
  CONSTRAINT fk_resource_recommendations_students_student_id FOREIGN KEY (student_id) REFERENCES students(id),
  CONSTRAINT fk_resource_recommendations_ai_generated_resources_resource_id FOREIGN KEY (resource_id) REFERENCES ai_generated_resources(id),
  CONSTRAINT fk_resource_recommendations_student_profiles_source_profile_id FOREIGN KEY (source_profile_id) REFERENCES student_profiles(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='资源推荐记录表';

CREATE TABLE ai_tutoring_sessions (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  student_id BIGINT UNSIGNED NOT NULL COMMENT '学生ID',
  knowledge_point_id BIGINT UNSIGNED NULL COMMENT '关联知识点ID',
  question_encrypted MEDIUMTEXT NOT NULL COMMENT '学生问题密文',
  question_iv VARCHAR(128) NOT NULL COMMENT '学生问题IV',
  answer_text_encrypted MEDIUMTEXT NULL COMMENT '回答文本密文',
  answer_text_iv VARCHAR(128) NULL COMMENT '回答文本IV',
  answer_assets_json JSON NULL COMMENT '图解、脚本、推荐资源等资产',
  feedback_score DECIMAL(5,2) NULL COMMENT '反馈评分',
  status VARCHAR(32) NOT NULL DEFAULT 'active',
  created_by BIGINT UNSIGNED NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted_at DATETIME NULL,
  PRIMARY KEY (id),
  KEY idx_ai_tutoring_sessions_student_id (student_id),
  KEY idx_ai_tutoring_sessions_knowledge_point_id (knowledge_point_id),
  CONSTRAINT fk_ai_tutoring_sessions_students_student_id FOREIGN KEY (student_id) REFERENCES students(id),
  CONSTRAINT fk_ai_tutor_sessions_kp_id FOREIGN KEY (knowledge_point_id) REFERENCES course_knowledge_points(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='智能辅导会话表';

CREATE TABLE learning_evaluations (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  student_id BIGINT UNSIGNED NOT NULL COMMENT '学生ID',
  source_type VARCHAR(64) NOT NULL COMMENT '评估来源类型',
  source_id BIGINT UNSIGNED NULL COMMENT '评估来源ID',
  evaluation_summary TEXT NULL COMMENT '评估摘要，导出时默认脱敏',
  score_json JSON NULL COMMENT '评分明细',
  suggestion_json JSON NULL COMMENT '改进建议',
  status VARCHAR(32) NOT NULL DEFAULT 'active',
  created_by BIGINT UNSIGNED NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted_at DATETIME NULL,
  PRIMARY KEY (id),
  KEY idx_learning_evaluations_student_id (student_id),
  KEY idx_learning_evaluations_source (source_type, source_id),
  CONSTRAINT fk_learning_evaluations_students_student_id FOREIGN KEY (student_id) REFERENCES students(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='学习效果评估表';

-- =========================================================
-- 6. 课程学习
-- =========================================================

CREATE TABLE course_resources (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  course_id BIGINT UNSIGNED NOT NULL COMMENT '课程ID',
  knowledge_point_id BIGINT UNSIGNED NULL COMMENT '知识点ID',
  uploaded_by_teacher_id BIGINT UNSIGNED NOT NULL COMMENT '上传教师ID',
  resource_type VARCHAR(64) NOT NULL COMMENT '资源类型',
  title VARCHAR(255) NOT NULL COMMENT '资源标题',
  file_url VARCHAR(512) NULL COMMENT '文件URL',
  file_name VARCHAR(255) NULL COMMENT '文件名',
  file_type VARCHAR(64) NULL COMMENT '文件类型',
  file_size BIGINT UNSIGNED NULL COMMENT '文件大小',
  status VARCHAR(32) NOT NULL DEFAULT 'active',
  created_by BIGINT UNSIGNED NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted_at DATETIME NULL,
  PRIMARY KEY (id),
  KEY idx_course_resources_course_id (course_id),
  KEY idx_course_resources_knowledge_point_id (knowledge_point_id),
  KEY idx_course_resources_uploaded_by_teacher_id (uploaded_by_teacher_id),
  KEY idx_course_resources_resource_type (resource_type),
  CONSTRAINT fk_course_resources_courses_course_id FOREIGN KEY (course_id) REFERENCES courses(id),
  CONSTRAINT fk_course_resources_course_knowledge_points_knowledge_point_id FOREIGN KEY (knowledge_point_id) REFERENCES course_knowledge_points(id),
  CONSTRAINT fk_course_resources_teachers_uploaded_by_teacher_id FOREIGN KEY (uploaded_by_teacher_id) REFERENCES teachers(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='课程资料表';

CREATE TABLE learning_records (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  student_id BIGINT UNSIGNED NOT NULL COMMENT '学生ID',
  course_id BIGINT UNSIGNED NOT NULL COMMENT '课程ID',
  resource_id BIGINT UNSIGNED NULL COMMENT '课程资源ID',
  action_type VARCHAR(32) NOT NULL COMMENT '学习行为类型',
  duration_seconds INT NOT NULL DEFAULT 0 COMMENT '学习时长，秒',
  completed TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否完成',
  status VARCHAR(32) NOT NULL DEFAULT 'active',
  created_by BIGINT UNSIGNED NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted_at DATETIME NULL,
  PRIMARY KEY (id),
  KEY idx_learning_records_student_id (student_id),
  KEY idx_learning_records_course_id (course_id),
  KEY idx_learning_records_resource_id (resource_id),
  KEY idx_learning_records_action_type (action_type),
  CONSTRAINT fk_learning_records_students_student_id FOREIGN KEY (student_id) REFERENCES students(id),
  CONSTRAINT fk_learning_records_courses_course_id FOREIGN KEY (course_id) REFERENCES courses(id),
  CONSTRAINT fk_learning_records_course_resources_resource_id FOREIGN KEY (resource_id) REFERENCES course_resources(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='学习行为记录表';

CREATE TABLE quiz_attempts (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  student_id BIGINT UNSIGNED NOT NULL COMMENT '学生ID',
  course_id BIGINT UNSIGNED NOT NULL COMMENT '课程ID',
  knowledge_point_id BIGINT UNSIGNED NULL COMMENT '知识点ID',
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
  KEY idx_quiz_attempts_student_id (student_id),
  KEY idx_quiz_attempts_course_id (course_id),
  KEY idx_quiz_attempts_knowledge_point_id (knowledge_point_id),
  CONSTRAINT fk_quiz_attempts_students_student_id FOREIGN KEY (student_id) REFERENCES students(id),
  CONSTRAINT fk_quiz_attempts_courses_course_id FOREIGN KEY (course_id) REFERENCES courses(id),
  CONSTRAINT fk_quiz_attempts_course_knowledge_points_knowledge_point_id FOREIGN KEY (knowledge_point_id) REFERENCES course_knowledge_points(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='答题记录表';

CREATE TABLE wrong_questions (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  student_id BIGINT UNSIGNED NOT NULL COMMENT '学生ID',
  quiz_attempt_id BIGINT UNSIGNED NOT NULL COMMENT '答题记录ID',
  knowledge_point_id BIGINT UNSIGNED NULL COMMENT '知识点ID',
  wrong_reason VARCHAR(512) NULL COMMENT '错误原因',
  review_status VARCHAR(32) NOT NULL DEFAULT 'unreviewed' COMMENT '复习状态',
  status VARCHAR(32) NOT NULL DEFAULT 'active',
  created_by BIGINT UNSIGNED NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted_at DATETIME NULL,
  PRIMARY KEY (id),
  KEY idx_wrong_questions_student_id (student_id),
  KEY idx_wrong_questions_quiz_attempt_id (quiz_attempt_id),
  KEY idx_wrong_questions_knowledge_point_id (knowledge_point_id),
  CONSTRAINT fk_wrong_questions_students_student_id FOREIGN KEY (student_id) REFERENCES students(id),
  CONSTRAINT fk_wrong_questions_quiz_attempts_quiz_attempt_id FOREIGN KEY (quiz_attempt_id) REFERENCES quiz_attempts(id),
  CONSTRAINT fk_wrong_questions_course_knowledge_points_knowledge_point_id FOREIGN KEY (knowledge_point_id) REFERENCES course_knowledge_points(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='错题记录表';

-- =========================================================
-- 7. 岗位、简历与投递
-- =========================================================

CREATE TABLE job_posts (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  enterprise_id BIGINT UNSIGNED NOT NULL COMMENT '企业ID',
  mentor_id BIGINT UNSIGNED NOT NULL COMMENT '企业导师ID',
  major_id BIGINT UNSIGNED NOT NULL COMMENT '适用专业ID',
  title VARCHAR(255) NOT NULL COMMENT '岗位名称',
  requirements TEXT NULL COMMENT '岗位要求',
  salary_range VARCHAR(64) NULL COMMENT '薪资范围',
  location VARCHAR(128) NULL COMMENT '工作地点',
  ability_tags JSON NULL COMMENT '能力标签',
  review_status VARCHAR(32) NOT NULL DEFAULT 'draft' COMMENT '岗位审核状态',
  submitted_at DATETIME NULL COMMENT '提交审核时间',
  approved_at DATETIME NULL COMMENT '审核通过时间',
  status VARCHAR(32) NOT NULL DEFAULT 'draft',
  created_by BIGINT UNSIGNED NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted_at DATETIME NULL,
  PRIMARY KEY (id),
  KEY idx_job_posts_enterprise_id (enterprise_id),
  KEY idx_job_posts_mentor_id (mentor_id),
  KEY idx_job_posts_major_id (major_id),
  KEY idx_job_posts_review_status (review_status),
  CONSTRAINT fk_job_posts_enterprises_enterprise_id FOREIGN KEY (enterprise_id) REFERENCES enterprises(id),
  CONSTRAINT fk_job_posts_enterprise_mentors_mentor_id FOREIGN KEY (mentor_id) REFERENCES enterprise_mentors(id),
  CONSTRAINT fk_job_posts_majors_major_id FOREIGN KEY (major_id) REFERENCES majors(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='岗位表';

CREATE TABLE resumes (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  student_id BIGINT UNSIGNED NOT NULL COMMENT '学生ID',
  target_job_id BIGINT UNSIGNED NULL COMMENT '目标岗位ID',
  generated_by_task_id BIGINT UNSIGNED NULL COMMENT 'AI生成任务ID',
  resume_content_encrypted MEDIUMTEXT NULL COMMENT '简历正文密文',
  resume_content_iv VARCHAR(128) NULL COMMENT '简历正文IV',
  resume_summary TEXT NULL COMMENT '简历摘要，导出默认使用摘要',
  student_confirmed TINYINT(1) NOT NULL DEFAULT 0 COMMENT '学生是否确认',
  confirmed_at DATETIME NULL COMMENT '学生确认时间',
  status VARCHAR(32) NOT NULL DEFAULT 'draft',
  created_by BIGINT UNSIGNED NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted_at DATETIME NULL,
  PRIMARY KEY (id),
  KEY idx_resumes_student_id (student_id),
  KEY idx_resumes_target_job_id (target_job_id),
  KEY idx_resumes_generated_by_task_id (generated_by_task_id),
  CONSTRAINT fk_resumes_students_student_id FOREIGN KEY (student_id) REFERENCES students(id),
  CONSTRAINT fk_resumes_job_posts_target_job_id FOREIGN KEY (target_job_id) REFERENCES job_posts(id),
  CONSTRAINT fk_resumes_ai_generation_tasks_generated_by_task_id FOREIGN KEY (generated_by_task_id) REFERENCES ai_generation_tasks(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='简历表';

CREATE TABLE job_applications (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  job_id BIGINT UNSIGNED NOT NULL COMMENT '岗位ID',
  resume_id BIGINT UNSIGNED NOT NULL COMMENT '简历ID',
  student_id BIGINT UNSIGNED NOT NULL COMMENT '学生ID',
  application_status VARCHAR(32) NOT NULL DEFAULT 'pending_teacher_review' COMMENT '投递状态',
  submitted_at DATETIME NULL COMMENT '提交时间',
  enterprise_feedback TEXT NULL COMMENT '企业反馈',
  status VARCHAR(32) NOT NULL DEFAULT 'pending',
  created_by BIGINT UNSIGNED NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted_at DATETIME NULL,
  PRIMARY KEY (id),
  KEY idx_job_applications_job_id (job_id),
  KEY idx_job_applications_resume_id (resume_id),
  KEY idx_job_applications_student_id (student_id),
  KEY idx_job_applications_application_status (application_status),
  CONSTRAINT fk_job_applications_job_posts_job_id FOREIGN KEY (job_id) REFERENCES job_posts(id),
  CONSTRAINT fk_job_applications_resumes_resume_id FOREIGN KEY (resume_id) REFERENCES resumes(id),
  CONSTRAINT fk_job_applications_students_student_id FOREIGN KEY (student_id) REFERENCES students(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='岗位投递表';

-- =========================================================
-- 8. 竞赛、证书、项目实训
-- =========================================================

CREATE TABLE competitions (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  title VARCHAR(255) NOT NULL COMMENT '竞赛名称',
  level VARCHAR(64) NULL COMMENT '竞赛级别',
  start_time DATETIME NULL COMMENT '开始时间',
  end_time DATETIME NULL COMMENT '结束时间',
  location VARCHAR(255) NULL COMMENT '地点',
  requirements TEXT NULL COMMENT '参赛要求',
  official_url VARCHAR(512) NULL COMMENT '官方链接',
  published_by BIGINT UNSIGNED NOT NULL COMMENT '发布人用户ID',
  status VARCHAR(32) NOT NULL DEFAULT 'published',
  created_by BIGINT UNSIGNED NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted_at DATETIME NULL,
  PRIMARY KEY (id),
  KEY idx_competitions_published_by (published_by),
  KEY idx_competitions_status (status),
  CONSTRAINT fk_competitions_users_published_by FOREIGN KEY (published_by) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='竞赛信息表';

CREATE TABLE competition_results (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  competition_id BIGINT UNSIGNED NOT NULL COMMENT '竞赛ID',
  student_id BIGINT UNSIGNED NOT NULL COMMENT '获奖学生ID',
  coach_teacher_id BIGINT UNSIGNED NULL COMMENT '带队老师ID',
  award_name VARCHAR(255) NOT NULL COMMENT '奖项名称',
  proof_file_url VARCHAR(512) NULL COMMENT '证明材料URL',
  review_status VARCHAR(32) NOT NULL DEFAULT 'pending' COMMENT '审核状态',
  submitted_at DATETIME NULL COMMENT '提交审核时间',
  approved_at DATETIME NULL COMMENT '审核通过时间',
  status VARCHAR(32) NOT NULL DEFAULT 'pending',
  created_by BIGINT UNSIGNED NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted_at DATETIME NULL,
  PRIMARY KEY (id),
  KEY idx_competition_results_competition_id (competition_id),
  KEY idx_competition_results_student_id (student_id),
  KEY idx_competition_results_coach_teacher_id (coach_teacher_id),
  KEY idx_competition_results_review_status (review_status),
  CONSTRAINT fk_competition_results_competitions_competition_id FOREIGN KEY (competition_id) REFERENCES competitions(id),
  CONSTRAINT fk_competition_results_students_student_id FOREIGN KEY (student_id) REFERENCES students(id),
  CONSTRAINT fk_competition_results_teachers_coach_teacher_id FOREIGN KEY (coach_teacher_id) REFERENCES teachers(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='竞赛成果表';

CREATE TABLE certificates (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  major_id BIGINT UNSIGNED NOT NULL COMMENT '专业ID',
  certificate_name VARCHAR(255) NOT NULL COMMENT '证书名称',
  requirement_level VARCHAR(64) NULL COMMENT '要求等级',
  graduation_required TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否毕业必需',
  resource_url VARCHAR(512) NULL COMMENT '考证资料URL',
  imported_by BIGINT UNSIGNED NOT NULL COMMENT '导入人用户ID',
  status VARCHAR(32) NOT NULL DEFAULT 'active',
  created_by BIGINT UNSIGNED NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted_at DATETIME NULL,
  PRIMARY KEY (id),
  KEY idx_certificates_major_id (major_id),
  KEY idx_certificates_imported_by (imported_by),
  CONSTRAINT fk_certificates_majors_major_id FOREIGN KEY (major_id) REFERENCES majors(id),
  CONSTRAINT fk_certificates_users_imported_by FOREIGN KEY (imported_by) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='证书标准表';

CREATE TABLE certificate_results (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  certificate_id BIGINT UNSIGNED NOT NULL COMMENT '证书标准ID',
  student_id BIGINT UNSIGNED NOT NULL COMMENT '学生ID',
  certificate_no VARCHAR(128) NULL COMMENT '证书编号',
  issued_at DATE NULL COMMENT '发证日期',
  proof_file_url VARCHAR(512) NULL COMMENT '证明材料URL',
  review_status VARCHAR(32) NOT NULL DEFAULT 'pending' COMMENT '审核状态',
  submitted_at DATETIME NULL COMMENT '提交审核时间',
  approved_at DATETIME NULL COMMENT '审核通过时间',
  status VARCHAR(32) NOT NULL DEFAULT 'pending',
  created_by BIGINT UNSIGNED NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted_at DATETIME NULL,
  PRIMARY KEY (id),
  KEY idx_certificate_results_certificate_id (certificate_id),
  KEY idx_certificate_results_student_id (student_id),
  KEY idx_certificate_results_review_status (review_status),
  CONSTRAINT fk_certificate_results_certificates_certificate_id FOREIGN KEY (certificate_id) REFERENCES certificates(id),
  CONSTRAINT fk_certificate_results_students_student_id FOREIGN KEY (student_id) REFERENCES students(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='证书成果表';

CREATE TABLE projects (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  course_id BIGINT UNSIGNED NULL COMMENT '关联课程ID',
  title VARCHAR(255) NOT NULL COMMENT '项目标题',
  description TEXT NULL COMMENT '项目描述',
  difficulty_level VARCHAR(32) NULL COMMENT '难度等级',
  ability_tags JSON NULL COMMENT '能力标签',
  status VARCHAR(32) NOT NULL DEFAULT 'active',
  created_by BIGINT UNSIGNED NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted_at DATETIME NULL,
  PRIMARY KEY (id),
  KEY idx_projects_course_id (course_id),
  CONSTRAINT fk_projects_courses_course_id FOREIGN KEY (course_id) REFERENCES courses(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='项目实训表';

CREATE TABLE project_materials (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  project_id BIGINT UNSIGNED NOT NULL COMMENT '项目ID',
  material_type VARCHAR(64) NOT NULL COMMENT '材料类型',
  title VARCHAR(255) NOT NULL COMMENT '材料标题',
  file_url VARCHAR(512) NULL COMMENT '文件URL',
  content_text MEDIUMTEXT NULL COMMENT '文本内容',
  status VARCHAR(32) NOT NULL DEFAULT 'active',
  created_by BIGINT UNSIGNED NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted_at DATETIME NULL,
  PRIMARY KEY (id),
  KEY idx_project_materials_project_id (project_id),
  KEY idx_project_materials_material_type (material_type),
  CONSTRAINT fk_project_materials_projects_project_id FOREIGN KEY (project_id) REFERENCES projects(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='项目材料表';

CREATE TABLE project_submissions (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  project_id BIGINT UNSIGNED NOT NULL COMMENT '项目ID',
  student_id BIGINT UNSIGNED NOT NULL COMMENT '学生ID',
  submission_url VARCHAR(512) NULL COMMENT '提交材料URL',
  score DECIMAL(5,2) NULL COMMENT '项目得分',
  teacher_comment TEXT NULL COMMENT '教师评价',
  status VARCHAR(32) NOT NULL DEFAULT 'submitted',
  created_by BIGINT UNSIGNED NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted_at DATETIME NULL,
  PRIMARY KEY (id),
  KEY idx_project_submissions_project_id (project_id),
  KEY idx_project_submissions_student_id (student_id),
  CONSTRAINT fk_project_submissions_projects_project_id FOREIGN KEY (project_id) REFERENCES projects(id),
  CONSTRAINT fk_project_submissions_students_student_id FOREIGN KEY (student_id) REFERENCES students(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='项目提交表';

-- =========================================================
-- 9. 审核、导出、操作日志
-- =========================================================

CREATE TABLE review_records (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  target_type VARCHAR(64) NOT NULL COMMENT '审核对象类型',
  target_id BIGINT UNSIGNED NOT NULL COMMENT '审核对象ID',
  review_node VARCHAR(64) NOT NULL COMMENT '审核节点',
  reviewer_user_id BIGINT UNSIGNED NOT NULL COMMENT '审核人用户ID',
  review_result VARCHAR(32) NOT NULL COMMENT '审核结果：approved/rejected',
  review_comment TEXT NULL COMMENT '审核意见，不得包含认证敏感信息',
  reviewed_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '审核时间',
  status VARCHAR(32) NOT NULL DEFAULT 'active',
  created_by BIGINT UNSIGNED NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted_at DATETIME NULL,
  PRIMARY KEY (id),
  KEY idx_review_records_target (target_type, target_id),
  KEY idx_review_records_reviewer_user_id (reviewer_user_id),
  KEY idx_review_records_review_result (review_result),
  CONSTRAINT fk_review_records_users_reviewer_user_id FOREIGN KEY (reviewer_user_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='统一审核记录表';

CREATE TABLE export_records (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  export_type VARCHAR(64) NOT NULL COMMENT '导出类型',
  export_scope VARCHAR(64) NOT NULL COMMENT '导出范围',
  major_id BIGINT UNSIGNED NULL COMMENT '专业ID',
  exported_by BIGINT UNSIGNED NOT NULL COMMENT '导出人用户ID',
  file_url VARCHAR(512) NULL COMMENT '导出文件URL',
  is_desensitized TINYINT(1) NOT NULL DEFAULT 1 COMMENT '是否脱敏',
  export_status VARCHAR(32) NOT NULL DEFAULT 'queued' COMMENT '导出状态',
  status VARCHAR(32) NOT NULL DEFAULT 'active',
  created_by BIGINT UNSIGNED NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted_at DATETIME NULL,
  PRIMARY KEY (id),
  KEY idx_export_records_exported_by (exported_by),
  KEY idx_export_records_major_id (major_id),
  KEY idx_export_records_export_type (export_type),
  KEY idx_export_records_export_status (export_status),
  CONSTRAINT fk_export_records_users_exported_by FOREIGN KEY (exported_by) REFERENCES users(id),
  CONSTRAINT fk_export_records_majors_major_id FOREIGN KEY (major_id) REFERENCES majors(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='导出记录表';

CREATE TABLE operation_logs (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  operator_id BIGINT UNSIGNED NULL COMMENT '操作人用户ID',
  operator_role VARCHAR(64) NULL COMMENT '操作时角色',
  module VARCHAR(64) NOT NULL COMMENT '操作模块',
  action VARCHAR(64) NOT NULL COMMENT '操作动作',
  target_type VARCHAR(64) NULL COMMENT '操作对象类型',
  target_id BIGINT UNSIGNED NULL COMMENT '操作对象ID',
  result VARCHAR(32) NOT NULL COMMENT '操作结果',
  ip_address VARCHAR(64) NULL COMMENT 'IP地址',
  remark VARCHAR(1024) NULL COMMENT '脱敏备注，不记录明文密码和完整隐私信息',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  KEY idx_operation_logs_operator_id (operator_id),
  KEY idx_operation_logs_module (module),
  KEY idx_operation_logs_created_at (created_at),
  KEY idx_operation_logs_target (target_type, target_id),
  CONSTRAINT fk_operation_logs_users_operator_id FOREIGN KEY (operator_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='操作日志表';
