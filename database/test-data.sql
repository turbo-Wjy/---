USE ai_learning_platform;

-- =========================================================
-- 测试数据说明
-- 1. 本脚本用于开发/演示环境，不用于生产环境。
-- 2. 不写入真实手机号、邮箱、身份证号、简历正文、画像详情。
-- 3. password_hash 为演示占位哈希，登录前应通过系统重置密码。
-- 4. 脚本尽量保持可重复执行。
-- =========================================================

SET @demo_password_hash = '$2b$12$demo.hash.placeholder.replace.before.login';

-- =========================================================
-- 1. 学院、专业、班级
-- =========================================================

INSERT INTO colleges (code, name, status)
VALUES ('AI_VOCATIONAL_COLLEGE', 'AI职教学院', 'active')
ON DUPLICATE KEY UPDATE name = VALUES(name), status = VALUES(status), updated_at = CURRENT_TIMESTAMP;

SET @college_id = (SELECT id FROM colleges WHERE code = 'AI_VOCATIONAL_COLLEGE');

INSERT INTO majors (college_id, code, name, status)
VALUES
  (@college_id, 'AI_TECH_APP', '人工智能技术应用', 'active'),
  (@college_id, 'INTELLIGENT_ROBOT_TECH', '智能机器人技术', 'active'),
  (@college_id, 'INFO_SECURITY_TECH_APP', '信息安全技术应用', 'active')
ON DUPLICATE KEY UPDATE college_id = VALUES(college_id), name = VALUES(name), status = VALUES(status), updated_at = CURRENT_TIMESTAMP;

SET @major_ai = (SELECT id FROM majors WHERE code = 'AI_TECH_APP');
SET @major_robot = (SELECT id FROM majors WHERE code = 'INTELLIGENT_ROBOT_TECH');
SET @major_security = (SELECT id FROM majors WHERE code = 'INFO_SECURITY_TECH_APP');

INSERT INTO classes (major_id, grade, name, status)
SELECT @major_ai, '2024', '智能24301', 'active'
WHERE NOT EXISTS (SELECT 1 FROM classes WHERE major_id = @major_ai AND grade = '2024' AND name = '智能24301');

INSERT INTO classes (major_id, grade, name, status)
SELECT @major_ai, '2024', '智能24302', 'active'
WHERE NOT EXISTS (SELECT 1 FROM classes WHERE major_id = @major_ai AND grade = '2024' AND name = '智能24302');

INSERT INTO classes (major_id, grade, name, status)
SELECT @major_ai, '2025', '智能25301', 'active'
WHERE NOT EXISTS (SELECT 1 FROM classes WHERE major_id = @major_ai AND grade = '2025' AND name = '智能25301');

INSERT INTO classes (major_id, grade, name, status)
SELECT @major_ai, '2025', '智能25302', 'active'
WHERE NOT EXISTS (SELECT 1 FROM classes WHERE major_id = @major_ai AND grade = '2025' AND name = '智能25302');

INSERT INTO classes (major_id, grade, name, status)
SELECT @major_ai, '2025', '智能25303', 'active'
WHERE NOT EXISTS (SELECT 1 FROM classes WHERE major_id = @major_ai AND grade = '2025' AND name = '智能25303');

INSERT INTO classes (major_id, grade, name, status)
SELECT @major_robot, '2025', '机器人25301', 'active'
WHERE NOT EXISTS (SELECT 1 FROM classes WHERE major_id = @major_robot AND grade = '2025' AND name = '机器人25301');

INSERT INTO classes (major_id, grade, name, status)
SELECT @major_robot, '2025', '机器人25302', 'active'
WHERE NOT EXISTS (SELECT 1 FROM classes WHERE major_id = @major_robot AND grade = '2025' AND name = '机器人25302');

INSERT INTO classes (major_id, grade, name, status)
SELECT @major_security, '2025', '信安25301', 'active'
WHERE NOT EXISTS (SELECT 1 FROM classes WHERE major_id = @major_security AND grade = '2025' AND name = '信安25301');

INSERT INTO classes (major_id, grade, name, status)
SELECT @major_security, '2025', '信安25302', 'active'
WHERE NOT EXISTS (SELECT 1 FROM classes WHERE major_id = @major_security AND grade = '2025' AND name = '信安25302');

SET @class_ai_24 = (SELECT id FROM classes WHERE major_id = @major_ai AND name = '智能24301' LIMIT 1);
SET @class_ai_25 = (SELECT id FROM classes WHERE major_id = @major_ai AND name = '智能25301' LIMIT 1);
SET @class_robot_25 = (SELECT id FROM classes WHERE major_id = @major_robot AND name = '机器人25301' LIMIT 1);
SET @class_security_25 = (SELECT id FROM classes WHERE major_id = @major_security AND name = '信安25301' LIMIT 1);

-- =========================================================
-- 2. 演示用户与角色绑定
-- =========================================================

INSERT INTO users (username, password_hash, real_name, account_status, must_change_password)
VALUES
  ('admin_demo', @demo_password_hash, '演示系统管理员', 'active', 1),
  ('major_ai_demo', @demo_password_hash, '人工智能专业负责人', 'active', 1),
  ('teacher_course_demo', @demo_password_hash, '演示任课老师', 'active', 1),
  ('teacher_group_demo', @demo_password_hash, '演示小组负责教师', 'active', 1),
  ('teacher_coach_demo', @demo_password_hash, '演示带队老师', 'active', 1),
  ('competition_admin_demo', @demo_password_hash, '演示竞赛管理员', 'active', 1),
  ('enterprise_mentor_demo', @demo_password_hash, '演示企业导师', 'active', 1),
  ('data_viewer_demo', @demo_password_hash, '演示数据查看者', 'active', 1),
  ('20240001', @demo_password_hash, '李同学', 'active', 1),
  ('20250001', @demo_password_hash, '王同学', 'active', 1),
  ('20250002', @demo_password_hash, '张同学', 'active', 1),
  ('20250003', @demo_password_hash, '赵同学', 'active', 1)
ON DUPLICATE KEY UPDATE real_name = VALUES(real_name), account_status = VALUES(account_status), updated_at = CURRENT_TIMESTAMP;

INSERT IGNORE INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u JOIN roles r ON r.code = 'admin' WHERE u.username = 'admin_demo';
INSERT IGNORE INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u JOIN roles r ON r.code = 'major_leader' WHERE u.username = 'major_ai_demo';
INSERT IGNORE INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u JOIN roles r ON r.code = 'teacher' WHERE u.username IN ('teacher_course_demo', 'teacher_group_demo', 'teacher_coach_demo');
INSERT IGNORE INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u JOIN roles r ON r.code = 'competition_admin' WHERE u.username = 'competition_admin_demo';
INSERT IGNORE INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u JOIN roles r ON r.code = 'enterprise_mentor' WHERE u.username = 'enterprise_mentor_demo';
INSERT IGNORE INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u JOIN roles r ON r.code = 'data_viewer' WHERE u.username = 'data_viewer_demo';
INSERT IGNORE INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u JOIN roles r ON r.code = 'student' WHERE u.username IN ('20240001', '20250001', '20250002', '20250003');

-- =========================================================
-- 3. 教师、学生、企业导师
-- =========================================================

INSERT INTO teachers (user_id, teacher_no, college_id, title, phone_encrypted, phone_iv, phone_hash, email_encrypted, email_iv, email_hash, status)
SELECT u.id, 'T20230001', @college_id, '讲师', 'demo_cipher_teacher_course_phone', 'demo_iv_teacher_course_phone', SHA2('demo_teacher_course_phone', 256), 'demo_cipher_teacher_course_email', 'demo_iv_teacher_course_email', SHA2('demo_teacher_course_email', 256), 'active'
FROM users u WHERE u.username = 'teacher_course_demo'
ON DUPLICATE KEY UPDATE title = VALUES(title), updated_at = CURRENT_TIMESTAMP;

INSERT INTO teachers (user_id, teacher_no, college_id, title, phone_encrypted, phone_iv, phone_hash, email_encrypted, email_iv, email_hash, status)
SELECT u.id, 'T20230002', @college_id, '副教授', 'demo_cipher_teacher_group_phone', 'demo_iv_teacher_group_phone', SHA2('demo_teacher_group_phone', 256), 'demo_cipher_teacher_group_email', 'demo_iv_teacher_group_email', SHA2('demo_teacher_group_email', 256), 'active'
FROM users u WHERE u.username = 'teacher_group_demo'
ON DUPLICATE KEY UPDATE title = VALUES(title), updated_at = CURRENT_TIMESTAMP;

INSERT INTO teachers (user_id, teacher_no, college_id, title, phone_encrypted, phone_iv, phone_hash, email_encrypted, email_iv, email_hash, status)
SELECT u.id, 'T20230003', @college_id, '讲师', 'demo_cipher_teacher_coach_phone', 'demo_iv_teacher_coach_phone', SHA2('demo_teacher_coach_phone', 256), 'demo_cipher_teacher_coach_email', 'demo_iv_teacher_coach_email', SHA2('demo_teacher_coach_email', 256), 'active'
FROM users u WHERE u.username = 'teacher_coach_demo'
ON DUPLICATE KEY UPDATE title = VALUES(title), updated_at = CURRENT_TIMESTAMP;

SET @teacher_course = (SELECT t.id FROM teachers t JOIN users u ON u.id = t.user_id WHERE u.username = 'teacher_course_demo');
SET @teacher_group = (SELECT t.id FROM teachers t JOIN users u ON u.id = t.user_id WHERE u.username = 'teacher_group_demo');
SET @teacher_coach = (SELECT t.id FROM teachers t JOIN users u ON u.id = t.user_id WHERE u.username = 'teacher_coach_demo');

INSERT IGNORE INTO teacher_duty_tags (teacher_id, tag_code, tag_name)
VALUES
  (@teacher_course, 'course_teacher', '任课老师'),
  (@teacher_group, 'group_teacher', '小组负责教师'),
  (@teacher_coach, 'competition_coach', '带队老师');

INSERT INTO students (user_id, student_no, college_id, major_id, class_id, grade, gender, enrollment_status, phone_encrypted, phone_iv, phone_hash, email_encrypted, email_iv, email_hash, status)
SELECT u.id, '20240001', @college_id, @major_ai, @class_ai_24, '2024', '男', 'studying', 'demo_cipher_student_20240001_phone', 'demo_iv_student_20240001_phone', SHA2('demo_student_20240001_phone', 256), 'demo_cipher_student_20240001_email', 'demo_iv_student_20240001_email', SHA2('demo_student_20240001_email', 256), 'active'
FROM users u WHERE u.username = '20240001'
ON DUPLICATE KEY UPDATE major_id = VALUES(major_id), class_id = VALUES(class_id), enrollment_status = VALUES(enrollment_status), updated_at = CURRENT_TIMESTAMP;

INSERT INTO students (user_id, student_no, college_id, major_id, class_id, grade, gender, enrollment_status, phone_encrypted, phone_iv, phone_hash, email_encrypted, email_iv, email_hash, status)
SELECT u.id, '20250001', @college_id, @major_ai, @class_ai_25, '2025', '女', 'studying', 'demo_cipher_student_20250001_phone', 'demo_iv_student_20250001_phone', SHA2('demo_student_20250001_phone', 256), 'demo_cipher_student_20250001_email', 'demo_iv_student_20250001_email', SHA2('demo_student_20250001_email', 256), 'active'
FROM users u WHERE u.username = '20250001'
ON DUPLICATE KEY UPDATE major_id = VALUES(major_id), class_id = VALUES(class_id), enrollment_status = VALUES(enrollment_status), updated_at = CURRENT_TIMESTAMP;

INSERT INTO students (user_id, student_no, college_id, major_id, class_id, grade, gender, enrollment_status, phone_encrypted, phone_iv, phone_hash, email_encrypted, email_iv, email_hash, status)
SELECT u.id, '20250002', @college_id, @major_robot, @class_robot_25, '2025', '男', 'studying', 'demo_cipher_student_20250002_phone', 'demo_iv_student_20250002_phone', SHA2('demo_student_20250002_phone', 256), 'demo_cipher_student_20250002_email', 'demo_iv_student_20250002_email', SHA2('demo_student_20250002_email', 256), 'active'
FROM users u WHERE u.username = '20250002'
ON DUPLICATE KEY UPDATE major_id = VALUES(major_id), class_id = VALUES(class_id), enrollment_status = VALUES(enrollment_status), updated_at = CURRENT_TIMESTAMP;

INSERT INTO students (user_id, student_no, college_id, major_id, class_id, grade, gender, enrollment_status, phone_encrypted, phone_iv, phone_hash, email_encrypted, email_iv, email_hash, status)
SELECT u.id, '20250003', @college_id, @major_security, @class_security_25, '2025', '女', 'studying', 'demo_cipher_student_20250003_phone', 'demo_iv_student_20250003_phone', SHA2('demo_student_20250003_phone', 256), 'demo_cipher_student_20250003_email', 'demo_iv_student_20250003_email', SHA2('demo_student_20250003_email', 256), 'active'
FROM users u WHERE u.username = '20250003'
ON DUPLICATE KEY UPDATE major_id = VALUES(major_id), class_id = VALUES(class_id), enrollment_status = VALUES(enrollment_status), updated_at = CURRENT_TIMESTAMP;

SET @student_1 = (SELECT id FROM students WHERE student_no = '20240001');
SET @student_2 = (SELECT id FROM students WHERE student_no = '20250001');
SET @student_3 = (SELECT id FROM students WHERE student_no = '20250002');
SET @student_4 = (SELECT id FROM students WHERE student_no = '20250003');

INSERT IGNORE INTO teacher_student_groups (teacher_id, student_id, group_name, bind_type, status)
VALUES
  (@teacher_group, @student_1, 'AI学习成长一组', 'custom_group', 'active'),
  (@teacher_group, @student_2, 'AI学习成长一组', 'custom_group', 'active'),
  (@teacher_group, @student_3, '智能技术成长一组', 'custom_group', 'active'),
  (@teacher_group, @student_4, '信息安全成长一组', 'custom_group', 'active');

INSERT INTO enterprises (name, industry, contact_name, contact_phone_encrypted, contact_phone_iv, contact_phone_hash, status)
SELECT '星火智能科技有限公司', '人工智能', '企业联系人', 'demo_cipher_enterprise_phone', 'demo_iv_enterprise_phone', SHA2('demo_enterprise_phone', 256), 'active'
WHERE NOT EXISTS (SELECT 1 FROM enterprises WHERE name = '星火智能科技有限公司');

SET @enterprise_id = (SELECT id FROM enterprises WHERE name = '星火智能科技有限公司' LIMIT 1);

INSERT INTO enterprise_mentors (user_id, enterprise_id, position, contact_phone_encrypted, contact_phone_iv, contact_phone_hash, email_encrypted, email_iv, email_hash, status)
SELECT u.id, @enterprise_id, 'AI算法工程师导师', 'demo_cipher_enterprise_mentor_phone', 'demo_iv_enterprise_mentor_phone', SHA2('demo_enterprise_mentor_phone', 256), 'demo_cipher_enterprise_mentor_email', 'demo_iv_enterprise_mentor_email', SHA2('demo_enterprise_mentor_email', 256), 'active'
FROM users u WHERE u.username = 'enterprise_mentor_demo'
ON DUPLICATE KEY UPDATE enterprise_id = VALUES(enterprise_id), position = VALUES(position), updated_at = CURRENT_TIMESTAMP;

SET @enterprise_mentor = (SELECT em.id FROM enterprise_mentors em JOIN users u ON u.id = em.user_id WHERE u.username = 'enterprise_mentor_demo');

-- =========================================================
-- 4. 课程、知识点、课程资源、学习记录
-- =========================================================

INSERT INTO courses (course_code, course_name, major_id, credit, semester, status)
VALUES
  ('AI-ML-001', '机器学习基础', @major_ai, 4.0, '2024-2025-2', 'active'),
  ('ROBOT-CTRL-001', '机器人控制基础', @major_robot, 4.0, '2025-2026-1', 'active'),
  ('SEC-NET-001', '网络安全基础', @major_security, 3.5, '2025-2026-1', 'active')
ON DUPLICATE KEY UPDATE course_name = VALUES(course_name), major_id = VALUES(major_id), updated_at = CURRENT_TIMESTAMP;

SET @course_ai = (SELECT id FROM courses WHERE course_code = 'AI-ML-001');
SET @course_robot = (SELECT id FROM courses WHERE course_code = 'ROBOT-CTRL-001');
SET @course_security = (SELECT id FROM courses WHERE course_code = 'SEC-NET-001');

INSERT INTO course_knowledge_points (course_id, parent_id, name, description, difficulty_level, sort_order, status)
SELECT @course_ai, NULL, '监督学习', '分类与回归的基本思想', 'medium', 1, 'active'
WHERE NOT EXISTS (SELECT 1 FROM course_knowledge_points WHERE course_id = @course_ai AND name = '监督学习');

INSERT INTO course_knowledge_points (course_id, parent_id, name, description, difficulty_level, sort_order, status)
SELECT @course_ai, NULL, '模型评估', '准确率、召回率、F1等指标', 'medium', 2, 'active'
WHERE NOT EXISTS (SELECT 1 FROM course_knowledge_points WHERE course_id = @course_ai AND name = '模型评估');

SET @kp_supervised = (SELECT id FROM course_knowledge_points WHERE course_id = @course_ai AND name = '监督学习' LIMIT 1);
SET @kp_eval = (SELECT id FROM course_knowledge_points WHERE course_id = @course_ai AND name = '模型评估' LIMIT 1);

INSERT INTO course_resources (course_id, knowledge_point_id, uploaded_by_teacher_id, resource_type, title, file_url, file_name, file_type, file_size, status)
SELECT @course_ai, @kp_supervised, @teacher_course, 'ppt', '机器学习基础-监督学习PPT', '/demo/course/ai-ml-supervised.pptx', 'ai-ml-supervised.pptx', 'pptx', 2048000, 'active'
WHERE NOT EXISTS (SELECT 1 FROM course_resources WHERE course_id = @course_ai AND title = '机器学习基础-监督学习PPT');

SET @course_resource = (SELECT id FROM course_resources WHERE course_id = @course_ai AND title = '机器学习基础-监督学习PPT' LIMIT 1);

INSERT INTO learning_records (student_id, course_id, resource_id, action_type, duration_seconds, completed, status)
SELECT @student_1, @course_ai, @course_resource, 'study', 1800, 1, 'active'
WHERE NOT EXISTS (SELECT 1 FROM learning_records WHERE student_id = @student_1 AND resource_id = @course_resource AND action_type = 'study');

INSERT INTO quiz_attempts (student_id, course_id, knowledge_point_id, question_snapshot, answer, is_correct, score, status)
SELECT @student_1, @course_ai, @kp_eval, JSON_OBJECT('question', 'demo_encrypted_question_reference', 'type', 'single_choice'), 'demo_answer_A', 0, 60.00, 'active'
WHERE NOT EXISTS (SELECT 1 FROM quiz_attempts WHERE student_id = @student_1 AND course_id = @course_ai AND knowledge_point_id = @kp_eval);

SET @quiz_attempt = (SELECT id FROM quiz_attempts WHERE student_id = @student_1 AND course_id = @course_ai AND knowledge_point_id = @kp_eval LIMIT 1);

INSERT INTO wrong_questions (student_id, quiz_attempt_id, knowledge_point_id, wrong_reason, review_status, status)
SELECT @student_1, @quiz_attempt, @kp_eval, '混淆准确率与召回率', 'unreviewed', 'active'
WHERE NOT EXISTS (SELECT 1 FROM wrong_questions WHERE quiz_attempt_id = @quiz_attempt);

-- =========================================================
-- 5. 学习画像、AI资源、路径、辅导、评估
-- =========================================================

SET @profile_agent = (SELECT id FROM ai_agents WHERE code = 'profile_builder_agent');
SET @resource_agent = (SELECT id FROM ai_agents WHERE code = 'document_generator_agent');
SET @path_agent = (SELECT id FROM ai_agents WHERE code = 'learning_path_agent');
SET @tutor_agent = (SELECT id FROM ai_agents WHERE code = 'ai_tutor_agent');

INSERT INTO profile_conversations (student_id, agent_id, message_role, message_content_encrypted, message_content_iv, extracted_features_encrypted, extracted_features_iv, status)
SELECT @student_1, @profile_agent, 'student', 'demo_cipher_profile_message_20240001', 'demo_iv_profile_message_20240001', 'demo_cipher_profile_features_20240001', 'demo_iv_profile_features_20240001', 'active'
WHERE NOT EXISTS (SELECT 1 FROM profile_conversations WHERE student_id = @student_1 AND message_content_iv = 'demo_iv_profile_message_20240001');

INSERT INTO student_profiles (student_id, profile_version, profile_summary_encrypted, profile_summary_iv, completeness_score, last_generated_at, status)
SELECT @student_1, 1, 'demo_cipher_profile_summary_20240001', 'demo_iv_profile_summary_20240001', 82.50, NOW(), 'active'
WHERE NOT EXISTS (SELECT 1 FROM student_profiles WHERE student_id = @student_1 AND profile_version = 1);

SET @profile_1 = (SELECT id FROM student_profiles WHERE student_id = @student_1 AND profile_version = 1 LIMIT 1);

INSERT INTO profile_dimension_values (profile_id, dimension_code, dimension_name, dimension_value_encrypted, dimension_value_iv, confidence_score, source_type, status)
SELECT @profile_1, 'knowledge_foundation', '知识基础', 'demo_cipher_dimension_knowledge_foundation', 'demo_iv_dimension_knowledge_foundation', 86.00, 'conversation', 'active'
WHERE NOT EXISTS (SELECT 1 FROM profile_dimension_values WHERE profile_id = @profile_1 AND dimension_code = 'knowledge_foundation');

INSERT INTO profile_dimension_values (profile_id, dimension_code, dimension_name, dimension_value_encrypted, dimension_value_iv, confidence_score, source_type, status)
SELECT @profile_1, 'resource_preference', '资源偏好', 'demo_cipher_dimension_resource_preference', 'demo_iv_dimension_resource_preference', 78.00, 'learning_record', 'active'
WHERE NOT EXISTS (SELECT 1 FROM profile_dimension_values WHERE profile_id = @profile_1 AND dimension_code = 'resource_preference');

INSERT INTO profile_update_logs (student_id, source_type, source_id, before_snapshot, after_snapshot, updated_reason, status)
SELECT @student_1, 'learning_record', @course_resource, JSON_OBJECT('completeness', 70), JSON_OBJECT('completeness', 82.5), '完成机器学习课程资料学习后更新画像', 'active'
WHERE NOT EXISTS (SELECT 1 FROM profile_update_logs WHERE student_id = @student_1 AND source_type = 'learning_record' AND source_id = @course_resource);

INSERT INTO ai_generation_tasks (student_id, agent_id, task_type, prompt, context_snapshot, task_status, started_at, finished_at, status)
SELECT @student_1, @resource_agent, 'document', 'demo_prompt_generate_ml_summary_without_sensitive_text', JSON_OBJECT('course', 'AI-ML-001', 'profile_id', @profile_1), 'succeeded', NOW(), NOW(), 'active'
WHERE NOT EXISTS (SELECT 1 FROM ai_generation_tasks WHERE student_id = @student_1 AND task_type = 'document');

SET @ai_task = (SELECT id FROM ai_generation_tasks WHERE student_id = @student_1 AND task_type = 'document' LIMIT 1);

INSERT INTO ai_generated_resources (task_id, resource_type, title, content_url, content_text, metadata_json, status)
SELECT @ai_task, 'document', '监督学习个性化讲解文档', '/demo/ai-resource/supervised-learning-summary.md', 'demo_generated_resource_summary', JSON_OBJECT('source', 'test-data'), 'active'
WHERE NOT EXISTS (SELECT 1 FROM ai_generated_resources WHERE task_id = @ai_task AND resource_type = 'document');

SET @ai_resource = (SELECT id FROM ai_generated_resources WHERE task_id = @ai_task AND resource_type = 'document' LIMIT 1);

INSERT INTO learning_paths (student_id, title, goal, generated_by_agent_id, path_status, status)
SELECT @student_1, '机器学习基础两周提升路径', '补齐监督学习与模型评估知识短板', @path_agent, 'active', 'active'
WHERE NOT EXISTS (SELECT 1 FROM learning_paths WHERE student_id = @student_1 AND title = '机器学习基础两周提升路径');

SET @learning_path = (SELECT id FROM learning_paths WHERE student_id = @student_1 AND title = '机器学习基础两周提升路径' LIMIT 1);

INSERT INTO learning_path_steps (path_id, step_order, title, resource_id, expected_duration, completion_status, status)
SELECT @learning_path, 1, '学习监督学习讲解文档', @ai_resource, 45, 'completed', 'active'
WHERE NOT EXISTS (SELECT 1 FROM learning_path_steps WHERE path_id = @learning_path AND step_order = 1);

INSERT INTO resource_recommendations (student_id, resource_id, recommend_reason, source_profile_id, view_status, status)
SELECT @student_1, @ai_resource, '根据画像中的知识短板推荐监督学习讲解文档', @profile_1, 'read', 'active'
WHERE NOT EXISTS (SELECT 1 FROM resource_recommendations WHERE student_id = @student_1 AND resource_id = @ai_resource);

INSERT INTO ai_tutoring_sessions (student_id, knowledge_point_id, question_encrypted, question_iv, answer_text_encrypted, answer_text_iv, answer_assets_json, feedback_score, status)
SELECT @student_1, @kp_eval, 'demo_cipher_tutor_question', 'demo_iv_tutor_question', 'demo_cipher_tutor_answer', 'demo_iv_tutor_answer', JSON_OBJECT('diagram', '/demo/assets/evaluation-metrics.png'), 4.50, 'active'
WHERE NOT EXISTS (SELECT 1 FROM ai_tutoring_sessions WHERE student_id = @student_1 AND question_iv = 'demo_iv_tutor_question');

INSERT INTO learning_evaluations (student_id, source_type, source_id, evaluation_summary, score_json, suggestion_json, status)
SELECT @student_1, 'quiz_attempt', @quiz_attempt, '演示评估摘要：模型评估指标仍需强化', JSON_OBJECT('overall', 76, 'quiz', 60), JSON_OBJECT('next', '复习准确率、召回率和F1'), 'active'
WHERE NOT EXISTS (SELECT 1 FROM learning_evaluations WHERE student_id = @student_1 AND source_type = 'quiz_attempt' AND source_id = @quiz_attempt);

-- =========================================================
-- 6. 岗位、AI简历、投递与审核
-- =========================================================

INSERT INTO job_posts (enterprise_id, mentor_id, major_id, title, requirements, salary_range, location, ability_tags, review_status, submitted_at, approved_at, status)
SELECT @enterprise_id, @enterprise_mentor, @major_ai, 'AI数据标注与模型评估实习生', '掌握机器学习基础，了解模型评估指标。', '3k-5k/月', '杭州', JSON_ARRAY('机器学习', '数据分析', '模型评估'), 'published', NOW(), NOW(), 'published'
WHERE NOT EXISTS (SELECT 1 FROM job_posts WHERE enterprise_id = @enterprise_id AND title = 'AI数据标注与模型评估实习生');

SET @job_post = (SELECT id FROM job_posts WHERE enterprise_id = @enterprise_id AND title = 'AI数据标注与模型评估实习生' LIMIT 1);

INSERT INTO resumes (student_id, target_job_id, generated_by_task_id, resume_content_encrypted, resume_content_iv, resume_summary, student_confirmed, confirmed_at, status)
SELECT @student_1, @job_post, @ai_task, 'demo_cipher_resume_content_20240001', 'demo_iv_resume_content_20240001', '演示简历摘要：具备机器学习基础、课程学习与竞赛经历。', 1, NOW(), 'approved'
WHERE NOT EXISTS (SELECT 1 FROM resumes WHERE student_id = @student_1 AND target_job_id = @job_post);

SET @resume = (SELECT id FROM resumes WHERE student_id = @student_1 AND target_job_id = @job_post LIMIT 1);

INSERT INTO job_applications (job_id, resume_id, student_id, application_status, submitted_at, enterprise_feedback, status)
SELECT @job_post, @resume, @student_1, 'recommended', NOW(), '演示反馈：进入企业推荐池。', 'approved'
WHERE NOT EXISTS (SELECT 1 FROM job_applications WHERE job_id = @job_post AND student_id = @student_1);

SET @job_application = (SELECT id FROM job_applications WHERE job_id = @job_post AND student_id = @student_1 LIMIT 1);
SET @teacher_group_user = (SELECT user_id FROM teachers WHERE id = @teacher_group);
SET @enterprise_mentor_user = (SELECT user_id FROM enterprise_mentors WHERE id = @enterprise_mentor);
SET @major_leader_user = (SELECT id FROM users WHERE username = 'major_ai_demo');

INSERT INTO review_records (target_type, target_id, review_node, reviewer_user_id, review_result, review_comment, reviewed_at, status)
SELECT 'job_post', @job_post, 'major_review', @major_leader_user, 'approved', '岗位信息符合本专业学生实习方向。', NOW(), 'active'
WHERE NOT EXISTS (SELECT 1 FROM review_records WHERE target_type = 'job_post' AND target_id = @job_post AND review_node = 'major_review');

INSERT INTO review_records (target_type, target_id, review_node, reviewer_user_id, review_result, review_comment, reviewed_at, status)
SELECT 'job_application', @job_application, 'teacher_group_review', @teacher_group_user, 'approved', '简历材料完整，可以投递。', NOW(), 'active'
WHERE NOT EXISTS (SELECT 1 FROM review_records WHERE target_type = 'job_application' AND target_id = @job_application AND review_node = 'teacher_group_review');

INSERT INTO review_records (target_type, target_id, review_node, reviewer_user_id, review_result, review_comment, reviewed_at, status)
SELECT 'job_application', @job_application, 'enterprise_review', @enterprise_mentor_user, 'approved', '符合岗位初筛要求。', NOW(), 'active'
WHERE NOT EXISTS (SELECT 1 FROM review_records WHERE target_type = 'job_application' AND target_id = @job_application AND review_node = 'enterprise_review');

-- =========================================================
-- 7. 竞赛、证书、项目实训
-- =========================================================

SET @competition_admin_user = (SELECT id FROM users WHERE username = 'competition_admin_demo');

INSERT INTO competitions (title, level, start_time, end_time, location, requirements, official_url, published_by, status)
SELECT '全国大学生AI算法挑战赛', 'national', '2026-07-01 09:00:00', '2026-07-03 18:00:00', '线上', '具备Python和机器学习基础。', 'https://example.edu/ai-competition', @competition_admin_user, 'published'
WHERE NOT EXISTS (SELECT 1 FROM competitions WHERE title = '全国大学生AI算法挑战赛');

SET @competition = (SELECT id FROM competitions WHERE title = '全国大学生AI算法挑战赛' LIMIT 1);

INSERT INTO competition_results (competition_id, student_id, coach_teacher_id, award_name, proof_file_url, review_status, submitted_at, approved_at, status)
SELECT @competition, @student_1, @teacher_coach, '校赛一等奖', '/demo/proofs/competition-20240001.pdf', 'approved', NOW(), NOW(), 'approved'
WHERE NOT EXISTS (SELECT 1 FROM competition_results WHERE competition_id = @competition AND student_id = @student_1);

SET @competition_result = (SELECT id FROM competition_results WHERE competition_id = @competition AND student_id = @student_1 LIMIT 1);

INSERT INTO review_records (target_type, target_id, review_node, reviewer_user_id, review_result, review_comment, reviewed_at, status)
SELECT 'competition_result', @competition_result, 'competition_admin_review', @competition_admin_user, 'approved', '证明材料完整，准予展示。', NOW(), 'active'
WHERE NOT EXISTS (SELECT 1 FROM review_records WHERE target_type = 'competition_result' AND target_id = @competition_result);

INSERT INTO certificates (major_id, certificate_name, requirement_level, graduation_required, resource_url, imported_by, status)
SELECT @major_ai, '人工智能训练师（中级）', '中级', 1, '/demo/certificates/ai-trainer-middle.md', @major_leader_user, 'active'
WHERE NOT EXISTS (SELECT 1 FROM certificates WHERE major_id = @major_ai AND certificate_name = '人工智能训练师（中级）');

SET @certificate = (SELECT id FROM certificates WHERE major_id = @major_ai AND certificate_name = '人工智能训练师（中级）' LIMIT 1);

INSERT INTO certificate_results (certificate_id, student_id, certificate_no, issued_at, proof_file_url, review_status, submitted_at, approved_at, status)
SELECT @certificate, @student_1, 'DEMO-CERT-20240001', '2026-06-01', '/demo/proofs/certificate-20240001.pdf', 'approved', NOW(), NOW(), 'approved'
WHERE NOT EXISTS (SELECT 1 FROM certificate_results WHERE certificate_id = @certificate AND student_id = @student_1);

SET @certificate_result = (SELECT id FROM certificate_results WHERE certificate_id = @certificate AND student_id = @student_1 LIMIT 1);

INSERT INTO review_records (target_type, target_id, review_node, reviewer_user_id, review_result, review_comment, reviewed_at, status)
SELECT 'certificate_result', @certificate_result, 'teacher_group_review', @teacher_group_user, 'approved', '证书材料完整。', NOW(), 'active'
WHERE NOT EXISTS (SELECT 1 FROM review_records WHERE target_type = 'certificate_result' AND target_id = @certificate_result);

INSERT INTO projects (course_id, title, description, difficulty_level, ability_tags, status)
SELECT @course_ai, '鸢尾花分类模型实训', '基于经典数据集完成分类模型训练与评估。', 'medium', JSON_ARRAY('Python', '机器学习', '模型评估'), 'active'
WHERE NOT EXISTS (SELECT 1 FROM projects WHERE course_id = @course_ai AND title = '鸢尾花分类模型实训');

SET @project = (SELECT id FROM projects WHERE course_id = @course_ai AND title = '鸢尾花分类模型实训' LIMIT 1);

INSERT INTO project_materials (project_id, material_type, title, file_url, content_text, status)
SELECT @project, 'practice_case', '鸢尾花分类实操指导书', '/demo/projects/iris-classification-guide.md', 'demo_project_material_content', 'active'
WHERE NOT EXISTS (SELECT 1 FROM project_materials WHERE project_id = @project AND title = '鸢尾花分类实操指导书');

INSERT INTO project_submissions (project_id, student_id, submission_url, score, teacher_comment, status)
SELECT @project, @student_1, '/demo/submissions/iris-20240001.zip', 88.00, '模型训练流程完整，评估指标解释仍可加强。', 'submitted'
WHERE NOT EXISTS (SELECT 1 FROM project_submissions WHERE project_id = @project AND student_id = @student_1);

-- =========================================================
-- 8. 导出记录与操作日志
-- =========================================================

INSERT INTO export_records (export_type, export_scope, major_id, exported_by, file_url, is_desensitized, export_status, status)
SELECT 'student_profile_summary', 'major', @major_ai, @major_leader_user, '/demo/exports/profile-summary-ai.xlsx', 1, 'succeeded', 'active'
WHERE NOT EXISTS (SELECT 1 FROM export_records WHERE export_type = 'student_profile_summary' AND major_id = @major_ai AND exported_by = @major_leader_user);

INSERT INTO operation_logs (operator_id, operator_role, module, action, target_type, target_id, result, ip_address, remark)
SELECT @major_leader_user, 'major_leader', 'test_data', 'initialize_demo_data', 'major', @major_ai, 'success', '127.0.0.1', '初始化演示数据，备注不包含敏感明文。'
WHERE NOT EXISTS (SELECT 1 FROM operation_logs WHERE module = 'test_data' AND action = 'initialize_demo_data' AND target_id = @major_ai);
