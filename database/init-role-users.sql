USE ai_learning_platform;

-- =========================================================
-- 按角色初始化演示用户
-- 1. 本脚本用于开发/演示环境，不用于生产环境。
-- 2. 不写入真实手机号、邮箱、身份证号。
-- 3. password_hash 为演示占位哈希，正式登录前应通过系统重置密码。
-- 4. 脚本可重复执行。
-- =========================================================

SET @demo_password_hash = '$2b$12$demo.hash.placeholder.replace.before.login';
SET @college_id = (SELECT id FROM colleges WHERE code = 'AI_VOCATIONAL_COLLEGE');
SET @major_ai = (SELECT id FROM majors WHERE code = 'AI_TECH_APP');
SET @major_robot = (SELECT id FROM majors WHERE code = 'INTELLIGENT_ROBOT_TECH');
SET @major_security = (SELECT id FROM majors WHERE code = 'INFO_SECURITY_TECH_APP');

-- =========================================================
-- 1. 登录用户
-- =========================================================

INSERT INTO users (username, password_hash, real_name, account_status, must_change_password)
VALUES
  ('sys_admin_001', @demo_password_hash, '系统管理员001', 'active', 1),
  ('major_ai_001', @demo_password_hash, '人工智能专业负责人001', 'active', 1),
  ('major_robot_001', @demo_password_hash, '智能机器人专业负责人001', 'active', 1),
  ('major_security_001', @demo_password_hash, '信息安全专业负责人001', 'active', 1),
  ('teacher_ai_course_001', @demo_password_hash, '人工智能任课教师001', 'active', 1),
  ('teacher_ai_group_001', @demo_password_hash, '人工智能小组教师001', 'active', 1),
  ('teacher_robot_course_001', @demo_password_hash, '机器人任课教师001', 'active', 1),
  ('teacher_security_course_001', @demo_password_hash, '信安任课教师001', 'active', 1),
  ('teacher_competition_coach_001', @demo_password_hash, '竞赛带队教师001', 'active', 1),
  ('competition_admin_001', @demo_password_hash, '竞赛管理员001', 'active', 1),
  ('enterprise_mentor_001', @demo_password_hash, '企业导师001', 'active', 1),
  ('data_viewer_001', @demo_password_hash, '数据查看者001', 'active', 1),
  ('20242430101', @demo_password_hash, '智能24301学生001', 'active', 1),
  ('20242430201', @demo_password_hash, '智能24302学生001', 'active', 1),
  ('20252530101', @demo_password_hash, '智能25301学生001', 'active', 1),
  ('20252530201', @demo_password_hash, '智能25302学生001', 'active', 1),
  ('20252530301', @demo_password_hash, '智能25303学生001', 'active', 1),
  ('20252540101', @demo_password_hash, '机器人25301学生001', 'active', 1),
  ('20252540201', @demo_password_hash, '机器人25302学生001', 'active', 1),
  ('20252550101', @demo_password_hash, '信安25301学生001', 'active', 1),
  ('20252550201', @demo_password_hash, '信安25302学生001', 'active', 1)
ON DUPLICATE KEY UPDATE
  real_name = VALUES(real_name),
  account_status = VALUES(account_status),
  must_change_password = VALUES(must_change_password),
  updated_at = CURRENT_TIMESTAMP;

-- =========================================================
-- 2. 用户角色绑定
-- =========================================================

INSERT IGNORE INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u JOIN roles r ON r.code = 'admin'
WHERE u.username = 'sys_admin_001';

INSERT IGNORE INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u JOIN roles r ON r.code = 'major_leader'
WHERE u.username IN ('major_ai_001', 'major_robot_001', 'major_security_001');

INSERT IGNORE INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u JOIN roles r ON r.code = 'teacher'
WHERE u.username IN (
  'teacher_ai_course_001',
  'teacher_ai_group_001',
  'teacher_robot_course_001',
  'teacher_security_course_001',
  'teacher_competition_coach_001'
);

INSERT IGNORE INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u JOIN roles r ON r.code = 'competition_admin'
WHERE u.username = 'competition_admin_001';

INSERT IGNORE INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u JOIN roles r ON r.code = 'enterprise_mentor'
WHERE u.username = 'enterprise_mentor_001';

INSERT IGNORE INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u JOIN roles r ON r.code = 'data_viewer'
WHERE u.username = 'data_viewer_001';

INSERT IGNORE INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u JOIN roles r ON r.code = 'student'
WHERE u.username IN (
  '20242430101',
  '20242430201',
  '20252530101',
  '20252530201',
  '20252530301',
  '20252540101',
  '20252540201',
  '20252550101',
  '20252550201'
);

-- =========================================================
-- 3. 教师扩展信息与职责标签
-- =========================================================

INSERT INTO teachers (user_id, teacher_no, college_id, title, phone_encrypted, phone_iv, phone_hash, email_encrypted, email_iv, email_hash, status)
SELECT u.id, 'T-AI-C-001', @college_id, '讲师', 'demo_cipher_t_ai_c_001_phone', 'demo_iv_t_ai_c_001_phone', SHA2('demo_t_ai_c_001_phone', 256), 'demo_cipher_t_ai_c_001_email', 'demo_iv_t_ai_c_001_email', SHA2('demo_t_ai_c_001_email', 256), 'active'
FROM users u WHERE u.username = 'teacher_ai_course_001'
ON DUPLICATE KEY UPDATE title = VALUES(title), updated_at = CURRENT_TIMESTAMP;

INSERT INTO teachers (user_id, teacher_no, college_id, title, phone_encrypted, phone_iv, phone_hash, email_encrypted, email_iv, email_hash, status)
SELECT u.id, 'T-AI-G-001', @college_id, '副教授', 'demo_cipher_t_ai_g_001_phone', 'demo_iv_t_ai_g_001_phone', SHA2('demo_t_ai_g_001_phone', 256), 'demo_cipher_t_ai_g_001_email', 'demo_iv_t_ai_g_001_email', SHA2('demo_t_ai_g_001_email', 256), 'active'
FROM users u WHERE u.username = 'teacher_ai_group_001'
ON DUPLICATE KEY UPDATE title = VALUES(title), updated_at = CURRENT_TIMESTAMP;

INSERT INTO teachers (user_id, teacher_no, college_id, title, phone_encrypted, phone_iv, phone_hash, email_encrypted, email_iv, email_hash, status)
SELECT u.id, 'T-RB-C-001', @college_id, '讲师', 'demo_cipher_t_rb_c_001_phone', 'demo_iv_t_rb_c_001_phone', SHA2('demo_t_rb_c_001_phone', 256), 'demo_cipher_t_rb_c_001_email', 'demo_iv_t_rb_c_001_email', SHA2('demo_t_rb_c_001_email', 256), 'active'
FROM users u WHERE u.username = 'teacher_robot_course_001'
ON DUPLICATE KEY UPDATE title = VALUES(title), updated_at = CURRENT_TIMESTAMP;

INSERT INTO teachers (user_id, teacher_no, college_id, title, phone_encrypted, phone_iv, phone_hash, email_encrypted, email_iv, email_hash, status)
SELECT u.id, 'T-SEC-C-001', @college_id, '讲师', 'demo_cipher_t_sec_c_001_phone', 'demo_iv_t_sec_c_001_phone', SHA2('demo_t_sec_c_001_phone', 256), 'demo_cipher_t_sec_c_001_email', 'demo_iv_t_sec_c_001_email', SHA2('demo_t_sec_c_001_email', 256), 'active'
FROM users u WHERE u.username = 'teacher_security_course_001'
ON DUPLICATE KEY UPDATE title = VALUES(title), updated_at = CURRENT_TIMESTAMP;

INSERT INTO teachers (user_id, teacher_no, college_id, title, phone_encrypted, phone_iv, phone_hash, email_encrypted, email_iv, email_hash, status)
SELECT u.id, 'T-COACH-001', @college_id, '讲师', 'demo_cipher_t_coach_001_phone', 'demo_iv_t_coach_001_phone', SHA2('demo_t_coach_001_phone', 256), 'demo_cipher_t_coach_001_email', 'demo_iv_t_coach_001_email', SHA2('demo_t_coach_001_email', 256), 'active'
FROM users u WHERE u.username = 'teacher_competition_coach_001'
ON DUPLICATE KEY UPDATE title = VALUES(title), updated_at = CURRENT_TIMESTAMP;

SET @teacher_ai_course = (SELECT t.id FROM teachers t JOIN users u ON u.id = t.user_id WHERE u.username = 'teacher_ai_course_001');
SET @teacher_ai_group = (SELECT t.id FROM teachers t JOIN users u ON u.id = t.user_id WHERE u.username = 'teacher_ai_group_001');
SET @teacher_robot_course = (SELECT t.id FROM teachers t JOIN users u ON u.id = t.user_id WHERE u.username = 'teacher_robot_course_001');
SET @teacher_security_course = (SELECT t.id FROM teachers t JOIN users u ON u.id = t.user_id WHERE u.username = 'teacher_security_course_001');
SET @teacher_coach = (SELECT t.id FROM teachers t JOIN users u ON u.id = t.user_id WHERE u.username = 'teacher_competition_coach_001');

INSERT IGNORE INTO teacher_duty_tags (teacher_id, tag_code, tag_name)
VALUES
  (@teacher_ai_course, 'course_teacher', '任课老师'),
  (@teacher_ai_group, 'group_teacher', '小组负责教师'),
  (@teacher_robot_course, 'course_teacher', '任课老师'),
  (@teacher_security_course, 'course_teacher', '任课老师'),
  (@teacher_coach, 'competition_coach', '带队老师');

-- =========================================================
-- 4. 企业导师扩展信息
-- =========================================================

INSERT INTO enterprises (name, industry, contact_name, contact_phone_encrypted, contact_phone_iv, contact_phone_hash, status)
SELECT '智云产业学院合作企业', '人工智能与智能制造', '演示企业联系人', 'demo_cipher_enterprise_role_users_phone', 'demo_iv_enterprise_role_users_phone', SHA2('demo_enterprise_role_users_phone', 256), 'active'
WHERE NOT EXISTS (SELECT 1 FROM enterprises WHERE name = '智云产业学院合作企业');

SET @enterprise_id = (SELECT id FROM enterprises WHERE name = '智云产业学院合作企业' LIMIT 1);

INSERT INTO enterprise_mentors (user_id, enterprise_id, position, contact_phone_encrypted, contact_phone_iv, contact_phone_hash, email_encrypted, email_iv, email_hash, status)
SELECT u.id, @enterprise_id, '企业实践导师', 'demo_cipher_enterprise_mentor_001_phone', 'demo_iv_enterprise_mentor_001_phone', SHA2('demo_enterprise_mentor_001_phone', 256), 'demo_cipher_enterprise_mentor_001_email', 'demo_iv_enterprise_mentor_001_email', SHA2('demo_enterprise_mentor_001_email', 256), 'active'
FROM users u WHERE u.username = 'enterprise_mentor_001'
ON DUPLICATE KEY UPDATE enterprise_id = VALUES(enterprise_id), position = VALUES(position), updated_at = CURRENT_TIMESTAMP;

-- =========================================================
-- 5. 学生扩展信息
-- =========================================================

SET @class_zn_24301 = (SELECT id FROM classes WHERE name = '智能24301' AND major_id = @major_ai LIMIT 1);
SET @class_zn_24302 = (SELECT id FROM classes WHERE name = '智能24302' AND major_id = @major_ai LIMIT 1);
SET @class_zn_25301 = (SELECT id FROM classes WHERE name = '智能25301' AND major_id = @major_ai LIMIT 1);
SET @class_zn_25302 = (SELECT id FROM classes WHERE name = '智能25302' AND major_id = @major_ai LIMIT 1);
SET @class_zn_25303 = (SELECT id FROM classes WHERE name = '智能25303' AND major_id = @major_ai LIMIT 1);
SET @class_robot_25301 = (SELECT id FROM classes WHERE name = '机器人25301' AND major_id = @major_robot LIMIT 1);
SET @class_robot_25302 = (SELECT id FROM classes WHERE name = '机器人25302' AND major_id = @major_robot LIMIT 1);
SET @class_sec_25301 = (SELECT id FROM classes WHERE name = '信安25301' AND major_id = @major_security LIMIT 1);
SET @class_sec_25302 = (SELECT id FROM classes WHERE name = '信安25302' AND major_id = @major_security LIMIT 1);

INSERT INTO students (user_id, student_no, college_id, major_id, class_id, grade, gender, enrollment_status, phone_encrypted, phone_iv, phone_hash, email_encrypted, email_iv, email_hash, status)
SELECT u.id, '20242430101', @college_id, @major_ai, @class_zn_24301, '2024', '男', 'studying', 'demo_cipher_s_20242430101_phone', 'demo_iv_s_20242430101_phone', SHA2('demo_s_20242430101_phone', 256), 'demo_cipher_s_20242430101_email', 'demo_iv_s_20242430101_email', SHA2('demo_s_20242430101_email', 256), 'active'
FROM users u WHERE u.username = '20242430101'
ON DUPLICATE KEY UPDATE major_id = VALUES(major_id), class_id = VALUES(class_id), grade = VALUES(grade), updated_at = CURRENT_TIMESTAMP;

INSERT INTO students (user_id, student_no, college_id, major_id, class_id, grade, gender, enrollment_status, phone_encrypted, phone_iv, phone_hash, email_encrypted, email_iv, email_hash, status)
SELECT u.id, '20242430201', @college_id, @major_ai, @class_zn_24302, '2024', '女', 'studying', 'demo_cipher_s_20242430201_phone', 'demo_iv_s_20242430201_phone', SHA2('demo_s_20242430201_phone', 256), 'demo_cipher_s_20242430201_email', 'demo_iv_s_20242430201_email', SHA2('demo_s_20242430201_email', 256), 'active'
FROM users u WHERE u.username = '20242430201'
ON DUPLICATE KEY UPDATE major_id = VALUES(major_id), class_id = VALUES(class_id), grade = VALUES(grade), updated_at = CURRENT_TIMESTAMP;

INSERT INTO students (user_id, student_no, college_id, major_id, class_id, grade, gender, enrollment_status, phone_encrypted, phone_iv, phone_hash, email_encrypted, email_iv, email_hash, status)
SELECT u.id, '20252530101', @college_id, @major_ai, @class_zn_25301, '2025', '男', 'studying', 'demo_cipher_s_20252530101_phone', 'demo_iv_s_20252530101_phone', SHA2('demo_s_20252530101_phone', 256), 'demo_cipher_s_20252530101_email', 'demo_iv_s_20252530101_email', SHA2('demo_s_20252530101_email', 256), 'active'
FROM users u WHERE u.username = '20252530101'
ON DUPLICATE KEY UPDATE major_id = VALUES(major_id), class_id = VALUES(class_id), grade = VALUES(grade), updated_at = CURRENT_TIMESTAMP;

INSERT INTO students (user_id, student_no, college_id, major_id, class_id, grade, gender, enrollment_status, phone_encrypted, phone_iv, phone_hash, email_encrypted, email_iv, email_hash, status)
SELECT u.id, '20252530201', @college_id, @major_ai, @class_zn_25302, '2025', '女', 'studying', 'demo_cipher_s_20252530201_phone', 'demo_iv_s_20252530201_phone', SHA2('demo_s_20252530201_phone', 256), 'demo_cipher_s_20252530201_email', 'demo_iv_s_20252530201_email', SHA2('demo_s_20252530201_email', 256), 'active'
FROM users u WHERE u.username = '20252530201'
ON DUPLICATE KEY UPDATE major_id = VALUES(major_id), class_id = VALUES(class_id), grade = VALUES(grade), updated_at = CURRENT_TIMESTAMP;

INSERT INTO students (user_id, student_no, college_id, major_id, class_id, grade, gender, enrollment_status, phone_encrypted, phone_iv, phone_hash, email_encrypted, email_iv, email_hash, status)
SELECT u.id, '20252530301', @college_id, @major_ai, @class_zn_25303, '2025', '男', 'studying', 'demo_cipher_s_20252530301_phone', 'demo_iv_s_20252530301_phone', SHA2('demo_s_20252530301_phone', 256), 'demo_cipher_s_20252530301_email', 'demo_iv_s_20252530301_email', SHA2('demo_s_20252530301_email', 256), 'active'
FROM users u WHERE u.username = '20252530301'
ON DUPLICATE KEY UPDATE major_id = VALUES(major_id), class_id = VALUES(class_id), grade = VALUES(grade), updated_at = CURRENT_TIMESTAMP;

INSERT INTO students (user_id, student_no, college_id, major_id, class_id, grade, gender, enrollment_status, phone_encrypted, phone_iv, phone_hash, email_encrypted, email_iv, email_hash, status)
SELECT u.id, '20252540101', @college_id, @major_robot, @class_robot_25301, '2025', '男', 'studying', 'demo_cipher_s_20252540101_phone', 'demo_iv_s_20252540101_phone', SHA2('demo_s_20252540101_phone', 256), 'demo_cipher_s_20252540101_email', 'demo_iv_s_20252540101_email', SHA2('demo_s_20252540101_email', 256), 'active'
FROM users u WHERE u.username = '20252540101'
ON DUPLICATE KEY UPDATE major_id = VALUES(major_id), class_id = VALUES(class_id), grade = VALUES(grade), updated_at = CURRENT_TIMESTAMP;

INSERT INTO students (user_id, student_no, college_id, major_id, class_id, grade, gender, enrollment_status, phone_encrypted, phone_iv, phone_hash, email_encrypted, email_iv, email_hash, status)
SELECT u.id, '20252540201', @college_id, @major_robot, @class_robot_25302, '2025', '女', 'studying', 'demo_cipher_s_20252540201_phone', 'demo_iv_s_20252540201_phone', SHA2('demo_s_20252540201_phone', 256), 'demo_cipher_s_20252540201_email', 'demo_iv_s_20252540201_email', SHA2('demo_s_20252540201_email', 256), 'active'
FROM users u WHERE u.username = '20252540201'
ON DUPLICATE KEY UPDATE major_id = VALUES(major_id), class_id = VALUES(class_id), grade = VALUES(grade), updated_at = CURRENT_TIMESTAMP;

INSERT INTO students (user_id, student_no, college_id, major_id, class_id, grade, gender, enrollment_status, phone_encrypted, phone_iv, phone_hash, email_encrypted, email_iv, email_hash, status)
SELECT u.id, '20252550101', @college_id, @major_security, @class_sec_25301, '2025', '男', 'studying', 'demo_cipher_s_20252550101_phone', 'demo_iv_s_20252550101_phone', SHA2('demo_s_20252550101_phone', 256), 'demo_cipher_s_20252550101_email', 'demo_iv_s_20252550101_email', SHA2('demo_s_20252550101_email', 256), 'active'
FROM users u WHERE u.username = '20252550101'
ON DUPLICATE KEY UPDATE major_id = VALUES(major_id), class_id = VALUES(class_id), grade = VALUES(grade), updated_at = CURRENT_TIMESTAMP;

INSERT INTO students (user_id, student_no, college_id, major_id, class_id, grade, gender, enrollment_status, phone_encrypted, phone_iv, phone_hash, email_encrypted, email_iv, email_hash, status)
SELECT u.id, '20252550201', @college_id, @major_security, @class_sec_25302, '2025', '女', 'studying', 'demo_cipher_s_20252550201_phone', 'demo_iv_s_20252550201_phone', SHA2('demo_s_20252550201_phone', 256), 'demo_cipher_s_20252550201_email', 'demo_iv_s_20252550201_email', SHA2('demo_s_20252550201_email', 256), 'active'
FROM users u WHERE u.username = '20252550201'
ON DUPLICATE KEY UPDATE major_id = VALUES(major_id), class_id = VALUES(class_id), grade = VALUES(grade), updated_at = CURRENT_TIMESTAMP;

-- =========================================================
-- 6. 小组教师绑定学生
-- =========================================================

INSERT IGNORE INTO teacher_student_groups (teacher_id, student_id, group_name, bind_type, status)
SELECT @teacher_ai_group, s.id, '角色初始化演示一组', 'custom_group', 'active'
FROM students s
WHERE s.student_no IN (
  '20242430101',
  '20242430201',
  '20252530101',
  '20252530201',
  '20252530301',
  '20252540101',
  '20252540201',
  '20252550101',
  '20252550201'
);

