-- =========================================================
-- AI 岗课赛证学习平台：融合关系测试数据种子
-- Version: 002
-- Target: MySQL 5.7+ / 8
-- Purpose: seed job role, capabilities, knowledge points,
--          competition tasks, certificate units, assessment points,
--          fusion relations, capability scores and one resource package.
-- =========================================================

USE ai_learning_platform;

SET NAMES utf8mb4;

SET @major_ai = (SELECT id FROM majors WHERE code = 'AI_TECH_APP' LIMIT 1);
SET @course_ai = (SELECT id FROM courses WHERE course_code = 'AI-ML-001' LIMIT 1);
SET @competition = (SELECT id FROM competitions WHERE title = '全国大学生AI算法挑战赛' LIMIT 1);
SET @certificate = (SELECT id FROM certificates WHERE major_id = @major_ai AND certificate_name = '人工智能训练师（中级）' LIMIT 1);
SET @student_demo = (SELECT id FROM students WHERE student_no = '20240001' LIMIT 1);
SET @profile_demo = (SELECT id FROM student_profiles WHERE student_id = @student_demo ORDER BY profile_version DESC LIMIT 1);
SET @ai_task_demo = (SELECT id FROM ai_generation_tasks WHERE student_id = @student_demo ORDER BY id DESC LIMIT 1);
SET @ai_resource_demo = (SELECT id FROM ai_generated_resources WHERE task_id = @ai_task_demo ORDER BY id DESC LIMIT 1);

-- =========================================================
-- 1. 岗位能力模型：AI 应用开发助理
-- =========================================================

INSERT INTO job_roles (
  major_id, role_code, role_name, description, typical_tasks, ability_tags, sort_order, status
) VALUES (
  @major_ai,
  'AI_APP_DEV_ASSISTANT',
  'AI应用开发助理',
  '面向人工智能技术应用专业，承担AI应用原型开发、模型调用、数据处理和基础评估等任务。',
  JSON_ARRAY('AI应用原型开发', '大模型API调用', '数据预处理', '模型效果评估', '知识库问答搭建'),
  JSON_ARRAY('Python', 'API调用', '机器学习', 'RAG', '模型评估'),
  1,
  'active'
) ON DUPLICATE KEY UPDATE
  major_id = VALUES(major_id),
  role_name = VALUES(role_name),
  description = VALUES(description),
  typical_tasks = VALUES(typical_tasks),
  ability_tags = VALUES(ability_tags),
  status = VALUES(status),
  updated_at = CURRENT_TIMESTAMP;

SET @job_role_ai = (SELECT id FROM job_roles WHERE role_code = 'AI_APP_DEV_ASSISTANT' LIMIT 1);

INSERT INTO job_capabilities (
  job_role_id, parent_id, capability_code, capability_name, description, level, weight, sort_order, status
) VALUES
  (@job_role_ai, NULL, 'PYTHON_DATA_PROCESSING', 'Python数据处理', '能够使用Python完成数据读取、清洗、分析和基础可视化。', 'basic', 0.90, 1, 'active'),
  (@job_role_ai, NULL, 'ML_MODEL_EVALUATION', '机器学习模型评估', '能够理解分类模型评估指标，并根据指标判断模型效果。', 'intermediate', 0.95, 2, 'active'),
  (@job_role_ai, NULL, 'LLM_API_PROMPT', '大模型API调用与提示词设计', '能够调用大模型API，并编写清晰的任务提示词。', 'intermediate', 0.90, 3, 'active'),
  (@job_role_ai, NULL, 'RAG_KNOWLEDGE_BASE', 'RAG知识库应用基础', '能够理解知识库检索增强生成的基本流程。', 'advanced', 0.80, 4, 'active')
ON DUPLICATE KEY UPDATE
  capability_name = VALUES(capability_name),
  description = VALUES(description),
  level = VALUES(level),
  weight = VALUES(weight),
  sort_order = VALUES(sort_order),
  status = VALUES(status),
  updated_at = CURRENT_TIMESTAMP;

SET @cap_python = (SELECT id FROM job_capabilities WHERE job_role_id = @job_role_ai AND capability_code = 'PYTHON_DATA_PROCESSING' LIMIT 1);
SET @cap_eval = (SELECT id FROM job_capabilities WHERE job_role_id = @job_role_ai AND capability_code = 'ML_MODEL_EVALUATION' LIMIT 1);
SET @cap_prompt = (SELECT id FROM job_capabilities WHERE job_role_id = @job_role_ai AND capability_code = 'LLM_API_PROMPT' LIMIT 1);
SET @cap_rag = (SELECT id FROM job_capabilities WHERE job_role_id = @job_role_ai AND capability_code = 'RAG_KNOWLEDGE_BASE' LIMIT 1);

-- =========================================================
-- 2. 课程知识点补充
-- =========================================================

INSERT INTO course_knowledge_points (course_id, parent_id, name, description, difficulty_level, sort_order, status)
SELECT @course_ai, NULL, 'Python数据处理', '使用Python完成数据读取、清洗、统计分析和简单可视化。', 'basic', 3, 'active'
WHERE @course_ai IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM course_knowledge_points WHERE course_id = @course_ai AND name = 'Python数据处理');

INSERT INTO course_knowledge_points (course_id, parent_id, name, description, difficulty_level, sort_order, status)
SELECT @course_ai, NULL, '大模型API调用', '理解API Key、请求参数、模型返回结果和错误处理。', 'medium', 4, 'active'
WHERE @course_ai IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM course_knowledge_points WHERE course_id = @course_ai AND name = '大模型API调用');

INSERT INTO course_knowledge_points (course_id, parent_id, name, description, difficulty_level, sort_order, status)
SELECT @course_ai, NULL, '提示词工程基础', '围绕角色、任务、约束、输出格式设计提示词。', 'medium', 5, 'active'
WHERE @course_ai IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM course_knowledge_points WHERE course_id = @course_ai AND name = '提示词工程基础');

INSERT INTO course_knowledge_points (course_id, parent_id, name, description, difficulty_level, sort_order, status)
SELECT @course_ai, NULL, 'RAG知识库基础', '理解文档切分、向量化、检索和生成回答的基本流程。', 'hard', 6, 'active'
WHERE @course_ai IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM course_knowledge_points WHERE course_id = @course_ai AND name = 'RAG知识库基础');

SET @kp_supervised = (SELECT id FROM course_knowledge_points WHERE course_id = @course_ai AND name = '监督学习' LIMIT 1);
SET @kp_eval = (SELECT id FROM course_knowledge_points WHERE course_id = @course_ai AND name = '模型评估' LIMIT 1);
SET @kp_python = (SELECT id FROM course_knowledge_points WHERE course_id = @course_ai AND name = 'Python数据处理' LIMIT 1);
SET @kp_api = (SELECT id FROM course_knowledge_points WHERE course_id = @course_ai AND name = '大模型API调用' LIMIT 1);
SET @kp_prompt = (SELECT id FROM course_knowledge_points WHERE course_id = @course_ai AND name = '提示词工程基础' LIMIT 1);
SET @kp_rag = (SELECT id FROM course_knowledge_points WHERE course_id = @course_ai AND name = 'RAG知识库基础' LIMIT 1);

INSERT IGNORE INTO knowledge_point_relations (
  source_knowledge_point_id, target_knowledge_point_id, relation_type, weight, description, status
)
SELECT @kp_python, @kp_supervised, 'prerequisite', 0.80, 'Python数据处理是监督学习实验的前置基础。', 'active'
FROM DUAL
WHERE @kp_python IS NOT NULL AND @kp_supervised IS NOT NULL;

INSERT IGNORE INTO knowledge_point_relations (
  source_knowledge_point_id, target_knowledge_point_id, relation_type, weight, description, status
)
SELECT @kp_supervised, @kp_eval, 'supports', 0.90, '监督学习模型训练后需要通过模型评估判断效果。', 'active'
FROM DUAL
WHERE @kp_supervised IS NOT NULL AND @kp_eval IS NOT NULL;

INSERT IGNORE INTO knowledge_point_relations (
  source_knowledge_point_id, target_knowledge_point_id, relation_type, weight, description, status
)
SELECT @kp_api, @kp_prompt, 'supports', 0.85, '大模型API调用需要配合提示词设计完成具体任务。', 'active'
FROM DUAL
WHERE @kp_api IS NOT NULL AND @kp_prompt IS NOT NULL;

INSERT IGNORE INTO knowledge_point_relations (
  source_knowledge_point_id, target_knowledge_point_id, relation_type, weight, description, status
)
SELECT @kp_prompt, @kp_rag, 'supports', 0.75, '提示词工程基础支撑RAG回答质量优化。', 'active'
FROM DUAL
WHERE @kp_prompt IS NOT NULL AND @kp_rag IS NOT NULL;

-- =========================================================
-- 3. 竞赛任务点
-- =========================================================

INSERT INTO competition_tasks (
  competition_id, task_code, task_title, task_description, related_capability_tags, difficulty, sort_order, status
)
SELECT @competition, 'AI_COMP_DATA_PREP', '赛题数据理解与预处理', '分析赛题数据字段，完成缺失值处理、特征理解和训练数据准备。', JSON_ARRAY('Python', '数据处理'), 'medium', 1, 'active'
WHERE @competition IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM competition_tasks WHERE competition_id = @competition AND task_code = 'AI_COMP_DATA_PREP');

INSERT INTO competition_tasks (
  competition_id, task_code, task_title, task_description, related_capability_tags, difficulty, sort_order, status
)
SELECT @competition, 'AI_COMP_MODEL_EVAL', '模型训练与效果评估', '完成基础模型训练，使用准确率、召回率、F1等指标评估模型效果。', JSON_ARRAY('机器学习', '模型评估'), 'medium', 2, 'active'
WHERE @competition IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM competition_tasks WHERE competition_id = @competition AND task_code = 'AI_COMP_MODEL_EVAL');

INSERT INTO competition_tasks (
  competition_id, task_code, task_title, task_description, related_capability_tags, difficulty, sort_order, status
)
SELECT @competition, 'AI_COMP_AI_ASSISTANT', 'AI学习助手原型设计', '围绕学生学习需求，设计一个调用大模型API的学习助手原型。', JSON_ARRAY('API调用', '提示词工程', 'AI应用'), 'hard', 3, 'active'
WHERE @competition IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM competition_tasks WHERE competition_id = @competition AND task_code = 'AI_COMP_AI_ASSISTANT');

SET @task_data = (SELECT id FROM competition_tasks WHERE competition_id = @competition AND task_code = 'AI_COMP_DATA_PREP' LIMIT 1);
SET @task_eval = (SELECT id FROM competition_tasks WHERE competition_id = @competition AND task_code = 'AI_COMP_MODEL_EVAL' LIMIT 1);
SET @task_assistant = (SELECT id FROM competition_tasks WHERE competition_id = @competition AND task_code = 'AI_COMP_AI_ASSISTANT' LIMIT 1);

-- =========================================================
-- 4. 证书能力单元与考核点
-- =========================================================

INSERT INTO certificate_units (
  certificate_id, unit_code, unit_name, description, weight, sort_order, status
)
SELECT @certificate, 'AI_TRAINER_DATA_UNIT', '数据处理与标注能力', '掌握数据清洗、标注规范、质量检查等基础能力。', 0.30, 1, 'active'
WHERE @certificate IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM certificate_units WHERE certificate_id = @certificate AND unit_code = 'AI_TRAINER_DATA_UNIT');

INSERT INTO certificate_units (
  certificate_id, unit_code, unit_name, description, weight, sort_order, status
)
SELECT @certificate, 'AI_TRAINER_MODEL_UNIT', '模型训练与评估能力', '掌握基础机器学习模型训练和效果评估方法。', 0.40, 2, 'active'
WHERE @certificate IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM certificate_units WHERE certificate_id = @certificate AND unit_code = 'AI_TRAINER_MODEL_UNIT');

INSERT INTO certificate_units (
  certificate_id, unit_code, unit_name, description, weight, sort_order, status
)
SELECT @certificate, 'AI_TRAINER_APP_UNIT', 'AI应用与工具使用能力', '掌握AI工具、大模型API和智能体应用的基础使用方法。', 0.30, 3, 'active'
WHERE @certificate IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM certificate_units WHERE certificate_id = @certificate AND unit_code = 'AI_TRAINER_APP_UNIT');

SET @unit_data = (SELECT id FROM certificate_units WHERE certificate_id = @certificate AND unit_code = 'AI_TRAINER_DATA_UNIT' LIMIT 1);
SET @unit_model = (SELECT id FROM certificate_units WHERE certificate_id = @certificate AND unit_code = 'AI_TRAINER_MODEL_UNIT' LIMIT 1);
SET @unit_app = (SELECT id FROM certificate_units WHERE certificate_id = @certificate AND unit_code = 'AI_TRAINER_APP_UNIT' LIMIT 1);

INSERT INTO certificate_assessment_points (
  unit_id, point_code, point_name, description, difficulty, score_weight, sort_order, status
)
SELECT @unit_data, 'DATA_PROCESSING_BASIC', '数据预处理基础', '能够完成数据清洗、格式转换和基本统计分析。', 'basic', 0.30, 1, 'active'
WHERE @unit_data IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM certificate_assessment_points WHERE unit_id = @unit_data AND point_code = 'DATA_PROCESSING_BASIC');

INSERT INTO certificate_assessment_points (
  unit_id, point_code, point_name, description, difficulty, score_weight, sort_order, status
)
SELECT @unit_model, 'MODEL_EVALUATION_BASIC', '模型评估指标理解', '能够解释准确率、召回率、F1等常见评估指标。', 'medium', 0.40, 1, 'active'
WHERE @unit_model IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM certificate_assessment_points WHERE unit_id = @unit_model AND point_code = 'MODEL_EVALUATION_BASIC');

INSERT INTO certificate_assessment_points (
  unit_id, point_code, point_name, description, difficulty, score_weight, sort_order, status
)
SELECT @unit_app, 'LLM_API_USAGE_BASIC', '大模型API基础使用', '能够完成大模型API调用、参数配置和结果解析。', 'medium', 0.30, 1, 'active'
WHERE @unit_app IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM certificate_assessment_points WHERE unit_id = @unit_app AND point_code = 'LLM_API_USAGE_BASIC');

SET @point_data = (SELECT id FROM certificate_assessment_points WHERE unit_id = @unit_data AND point_code = 'DATA_PROCESSING_BASIC' LIMIT 1);
SET @point_eval = (SELECT id FROM certificate_assessment_points WHERE unit_id = @unit_model AND point_code = 'MODEL_EVALUATION_BASIC' LIMIT 1);
SET @point_api = (SELECT id FROM certificate_assessment_points WHERE unit_id = @unit_app AND point_code = 'LLM_API_USAGE_BASIC' LIMIT 1);

-- =========================================================
-- 5. 岗课赛证融合关系
-- =========================================================

INSERT IGNORE INTO fusion_relations (
  source_type, source_id, target_type, target_id, relation_type, weight, description, evidence_json, status
)
SELECT 'job_capability', @cap_python, 'course_knowledge_point', @kp_python, 'supports', 0.92, 'Python数据处理知识点支撑AI应用开发助理的数据处理能力。', JSON_OBJECT('source', 'seed', 'scenario', 'job-course'), 'active'
WHERE @cap_python IS NOT NULL AND @kp_python IS NOT NULL;

INSERT IGNORE INTO fusion_relations (
  source_type, source_id, target_type, target_id, relation_type, weight, description, evidence_json, status
)
SELECT 'job_capability', @cap_eval, 'course_knowledge_point', @kp_eval, 'supports', 0.95, '模型评估知识点支撑岗位中的模型效果判断能力。', JSON_OBJECT('source', 'seed', 'scenario', 'job-course'), 'active'
WHERE @cap_eval IS NOT NULL AND @kp_eval IS NOT NULL;

INSERT IGNORE INTO fusion_relations (
  source_type, source_id, target_type, target_id, relation_type, weight, description, evidence_json, status
)
SELECT 'job_capability', @cap_prompt, 'course_knowledge_point', @kp_api, 'supports', 0.88, '大模型API调用知识点支撑岗位中的AI应用集成能力。', JSON_OBJECT('source', 'seed', 'scenario', 'job-course'), 'active'
WHERE @cap_prompt IS NOT NULL AND @kp_api IS NOT NULL;

INSERT IGNORE INTO fusion_relations (
  source_type, source_id, target_type, target_id, relation_type, weight, description, evidence_json, status
)
SELECT 'job_capability', @cap_prompt, 'course_knowledge_point', @kp_prompt, 'supports', 0.90, '提示词工程基础支撑岗位中的智能交互设计能力。', JSON_OBJECT('source', 'seed', 'scenario', 'job-course'), 'active'
WHERE @cap_prompt IS NOT NULL AND @kp_prompt IS NOT NULL;

INSERT IGNORE INTO fusion_relations (
  source_type, source_id, target_type, target_id, relation_type, weight, description, evidence_json, status
)
SELECT 'job_capability', @cap_rag, 'course_knowledge_point', @kp_rag, 'supports', 0.82, 'RAG知识库基础支撑岗位中的知识库问答搭建能力。', JSON_OBJECT('source', 'seed', 'scenario', 'job-course'), 'active'
WHERE @cap_rag IS NOT NULL AND @kp_rag IS NOT NULL;

INSERT IGNORE INTO fusion_relations (
  source_type, source_id, target_type, target_id, relation_type, weight, description, evidence_json, status
)
SELECT 'job_capability', @cap_python, 'competition_task', @task_data, 'improves', 0.86, '赛题数据理解与预处理任务可提升Python数据处理能力。', JSON_OBJECT('source', 'seed', 'scenario', 'job-competition'), 'active'
WHERE @cap_python IS NOT NULL AND @task_data IS NOT NULL;

INSERT IGNORE INTO fusion_relations (
  source_type, source_id, target_type, target_id, relation_type, weight, description, evidence_json, status
)
SELECT 'job_capability', @cap_eval, 'competition_task', @task_eval, 'improves', 0.90, '模型训练与效果评估任务可提升模型评估能力。', JSON_OBJECT('source', 'seed', 'scenario', 'job-competition'), 'active'
WHERE @cap_eval IS NOT NULL AND @task_eval IS NOT NULL;

INSERT IGNORE INTO fusion_relations (
  source_type, source_id, target_type, target_id, relation_type, weight, description, evidence_json, status
)
SELECT 'job_capability', @cap_prompt, 'competition_task', @task_assistant, 'improves', 0.84, 'AI学习助手原型任务可提升API调用与提示词设计能力。', JSON_OBJECT('source', 'seed', 'scenario', 'job-competition'), 'active'
WHERE @cap_prompt IS NOT NULL AND @task_assistant IS NOT NULL;

INSERT IGNORE INTO fusion_relations (
  source_type, source_id, target_type, target_id, relation_type, weight, description, evidence_json, status
)
SELECT 'job_capability', @cap_python, 'certificate_assessment_point', @point_data, 'assesses', 0.82, '证书考核点数据预处理基础可评价Python数据处理能力。', JSON_OBJECT('source', 'seed', 'scenario', 'job-certificate'), 'active'
WHERE @cap_python IS NOT NULL AND @point_data IS NOT NULL;

INSERT IGNORE INTO fusion_relations (
  source_type, source_id, target_type, target_id, relation_type, weight, description, evidence_json, status
)
SELECT 'job_capability', @cap_eval, 'certificate_assessment_point', @point_eval, 'assesses', 0.88, '证书考核点模型评估指标理解可评价模型评估能力。', JSON_OBJECT('source', 'seed', 'scenario', 'job-certificate'), 'active'
WHERE @cap_eval IS NOT NULL AND @point_eval IS NOT NULL;

INSERT IGNORE INTO fusion_relations (
  source_type, source_id, target_type, target_id, relation_type, weight, description, evidence_json, status
)
SELECT 'job_capability', @cap_prompt, 'certificate_assessment_point', @point_api, 'assesses', 0.80, '证书考核点大模型API基础使用可评价API调用能力。', JSON_OBJECT('source', 'seed', 'scenario', 'job-certificate'), 'active'
WHERE @cap_prompt IS NOT NULL AND @point_api IS NOT NULL;

INSERT IGNORE INTO fusion_relations (
  source_type, source_id, target_type, target_id, relation_type, weight, description, evidence_json, status
)
SELECT 'course_knowledge_point', @kp_eval, 'certificate_assessment_point', @point_eval, 'supports', 0.90, '课程中的模型评估知识点支撑证书中的模型评估考核点。', JSON_OBJECT('source', 'seed', 'scenario', 'course-certificate'), 'active'
WHERE @kp_eval IS NOT NULL AND @point_eval IS NOT NULL;

INSERT IGNORE INTO fusion_relations (
  source_type, source_id, target_type, target_id, relation_type, weight, description, evidence_json, status
)
SELECT 'competition_task', @task_eval, 'certificate_assessment_point', @point_eval, 'supports', 0.78, '竞赛模型评估任务可反向支撑证书模型评估达标。', JSON_OBJECT('source', 'seed', 'scenario', 'competition-certificate'), 'active'
WHERE @task_eval IS NOT NULL AND @point_eval IS NOT NULL;

-- =========================================================
-- 6. 学生能力得分样例
-- =========================================================

INSERT IGNORE INTO student_capability_scores (
  student_id, target_type, target_id, score, mastery_status, source_type, source_id, evidence_json, evaluated_at, status
)
SELECT @student_demo, 'job_capability', @cap_python, 76.00, 'qualified', 'learning_record', NULL, JSON_OBJECT('source', 'seed', 'reason', '已完成监督学习资料学习'), NOW(), 'active'
WHERE @student_demo IS NOT NULL AND @cap_python IS NOT NULL;

INSERT IGNORE INTO student_capability_scores (
  student_id, target_type, target_id, score, mastery_status, source_type, source_id, evidence_json, evaluated_at, status
)
SELECT @student_demo, 'job_capability', @cap_eval, 62.00, 'developing', 'quiz_attempt', NULL, JSON_OBJECT('source', 'seed', 'reason', '模型评估题目仍有错题'), NOW(), 'active'
WHERE @student_demo IS NOT NULL AND @cap_eval IS NOT NULL;

INSERT IGNORE INTO student_capability_scores (
  student_id, target_type, target_id, score, mastery_status, source_type, source_id, evidence_json, evaluated_at, status
)
SELECT @student_demo, 'job_capability', @cap_prompt, 45.00, 'weak', 'profile', @profile_demo, JSON_OBJECT('source', 'seed', 'reason', 'API调用与提示词经验不足'), NOW(), 'active'
WHERE @student_demo IS NOT NULL AND @cap_prompt IS NOT NULL;

INSERT IGNORE INTO student_capability_scores (
  student_id, target_type, target_id, score, mastery_status, source_type, source_id, evidence_json, evaluated_at, status
)
SELECT @student_demo, 'course_knowledge_point', @kp_eval, 66.00, 'developing', 'quiz_attempt', NULL, JSON_OBJECT('source', 'seed', 'reason', 'F1指标理解需要加强'), NOW(), 'active'
WHERE @student_demo IS NOT NULL AND @kp_eval IS NOT NULL;

-- =========================================================
-- 7. 资源包样例
-- =========================================================

INSERT INTO resource_packages (
  student_id, task_id, profile_id, target_job_role_id, course_id, competition_id, certificate_id,
  package_title, generation_context, resource_types, difficulty, scenario, review_status, status
)
SELECT
  @student_demo,
  @ai_task_demo,
  @profile_demo,
  @job_role_ai,
  @course_ai,
  @competition,
  @certificate,
  'AI应用开发助理成长资源包',
  JSON_OBJECT(
    'targetJobRole', 'AI应用开发助理',
    'weakPoints', JSON_ARRAY('大模型API调用', '提示词工程基础', '模型评估'),
    'fusionRelationCount', (SELECT COUNT(*) FROM fusion_relations WHERE source_type = 'job_capability')
  ),
  JSON_ARRAY('handout', 'ppt', 'quiz', 'mindmap', 'practice_case'),
  'intermediate',
  'competition_training',
  'published',
  'published'
WHERE @student_demo IS NOT NULL
  AND NOT EXISTS (
    SELECT 1 FROM resource_packages
    WHERE student_id = @student_demo AND package_title = 'AI应用开发助理成长资源包'
  );

SET @resource_package = (
  SELECT id FROM resource_packages
  WHERE student_id = @student_demo AND package_title = 'AI应用开发助理成长资源包'
  LIMIT 1
);

INSERT INTO resource_package_items (package_id, resource_id, item_order, status)
SELECT @resource_package, @ai_resource_demo, 1, 'active'
WHERE @resource_package IS NOT NULL
  AND @ai_resource_demo IS NOT NULL
  AND NOT EXISTS (
    SELECT 1 FROM resource_package_items
    WHERE package_id = @resource_package AND resource_id = @ai_resource_demo
  );

SELECT
  '002_fusion_sample_seed completed' AS seed_result,
  (SELECT COUNT(*) FROM job_roles WHERE role_code = 'AI_APP_DEV_ASSISTANT') AS job_role_count,
  (SELECT COUNT(*) FROM job_capabilities WHERE job_role_id = @job_role_ai) AS capability_count,
  (SELECT COUNT(*) FROM competition_tasks WHERE competition_id = @competition) AS competition_task_count,
  (SELECT COUNT(*) FROM certificate_units WHERE certificate_id = @certificate) AS certificate_unit_count,
  (SELECT COUNT(*) FROM fusion_relations) AS fusion_relation_count;
