# 核心业务时序图设计

## 1. 设计说明

本文档只绘制第一版系统演示和业务闭环中最关键的时序图，不覆盖普通 CRUD。

时序图重点说明：

- 用户、前端、后端服务、数据库、AI 智能体、审核人之间的调用关系。
- 哪些步骤需要写入操作日志、审核记录、导出记录。
- 哪些节点需要加密、脱敏、权限校验。
- 哪些流程支持打回重做。

系统统一约定：

- 所有接口统一前缀为 `/api/v1`。
- 写操作统一记录 `operation_logs`。
- 审核操作统一记录 `review_records`。
- 导出操作统一记录 `export_records`。
- 敏感字段默认加密存储、脱敏返回。

## 2. 学生登录与首次改密

```mermaid
sequenceDiagram
  autonumber
  actor Student as 学生
  participant Web as 前端
  participant Auth as 认证服务
  participant UserSvc as 用户服务
  participant DB as 数据库
  participant Log as 日志服务

  Student->>Web: 输入学号和初始密码
  Web->>Auth: POST /api/v1/auth/login
  Auth->>DB: 查询 users.username
  DB-->>Auth: 返回账号、password_hash、must_change_password
  Auth->>Auth: 校验 BCrypt/Argon2 密码哈希
  alt 密码错误或账号停用
    Auth-->>Web: 返回登录失败
    Auth->>Log: 记录失败登录日志
    Web-->>Student: 提示账号或密码错误
  else 登录成功且需要改密
    Auth-->>Web: 返回临时 Token 和 mustChangePassword=true
    Web-->>Student: 跳转首次改密页
    Student->>Web: 输入新密码
    Web->>Auth: POST /api/v1/auth/force-change-password
    Auth->>UserSvc: 更新 password_hash
    UserSvc->>DB: 写入新密码哈希，must_change_password=0
    UserSvc->>Log: 记录改密操作，不记录明文密码
    Auth-->>Web: 返回正式 Token
    Web-->>Student: 进入首页工作台
  else 登录成功且无需改密
    Auth->>Log: 记录成功登录日志
    Auth-->>Web: 返回 Token、用户信息、角色权限
    Web-->>Student: 进入首页工作台
  end
```

## 3. 专业负责人导入学生账号

```mermaid
sequenceDiagram
  autonumber
  actor Leader as 专业负责人
  participant Web as 前端
  participant ImportSvc as 学生导入服务
  participant UserSvc as 用户服务
  participant DB as 数据库
  participant Log as 日志服务

  Leader->>Web: 上传教务系统 Excel
  Web->>ImportSvc: POST /api/v1/students/import/preview
  ImportSvc->>ImportSvc: 解析 Excel，校验学号、姓名、专业、班级
  ImportSvc->>DB: 查询 majors、classes、students、users
  DB-->>ImportSvc: 返回匹配结果和重复数据
  ImportSvc-->>Web: 返回预览结果、错误行、可导入数量
  Web-->>Leader: 展示导入预览

  Leader->>Web: 确认导入
  Web->>ImportSvc: POST /api/v1/students/import/confirm
  ImportSvc->>DB: 开启事务
  loop 每一条合法学生数据
    ImportSvc->>UserSvc: 创建学生登录账号
    UserSvc->>UserSvc: 生成默认密码哈希，不保存明文密码
    UserSvc->>DB: 写入 users，must_change_password=1
    ImportSvc->>DB: 写入 students
    ImportSvc->>DB: 写入 user_roles 绑定 student 角色
  end
  ImportSvc->>DB: 提交事务
  ImportSvc->>Log: 记录导入批次、导入人、成功数、失败数
  ImportSvc-->>Web: 返回导入结果
  Web-->>Leader: 显示导入完成
```

## 4. 对话式学习画像构建

```mermaid
sequenceDiagram
  autonumber
  actor Student as 学生
  participant Web as 前端
  participant ProfileSvc as 画像服务
  participant Agent as 画像构建智能体
  participant DB as 数据库
  participant Log as 日志服务

  Student->>Web: 进入学习画像
  Web->>ProfileSvc: POST /api/v1/profile-sessions
  ProfileSvc->>DB: 创建 profile_sessions
  ProfileSvc-->>Web: 返回 sessionId

  loop 多轮自然语言对话
    Student->>Web: 输入学习目标、基础、偏好、短板
    Web->>ProfileSvc: POST /api/v1/profile-sessions/{id}/messages
    ProfileSvc->>DB: 加密保存 profile_session_messages
    ProfileSvc->>Agent: 结合上下文生成追问或总结
    Agent-->>ProfileSvc: 返回回复和抽取线索
    ProfileSvc->>DB: 加密保存智能体回复
    ProfileSvc-->>Web: 返回对话回复
  end

  Student->>Web: 点击生成画像草稿
  Web->>ProfileSvc: POST /api/v1/profile-sessions/{id}/extract
  ProfileSvc->>Agent: 抽取画像维度和置信度
  Agent-->>ProfileSvc: 返回画像草稿
  ProfileSvc->>DB: 更新 profile_sessions.draft_profile_json
  ProfileSvc-->>Web: 返回画像草稿

  Student->>Web: 确认画像
  Web->>ProfileSvc: POST /api/v1/learning-profiles/me/confirm
  ProfileSvc->>DB: 写入 student_profiles
  ProfileSvc->>DB: 写入 profile_dimension_values
  ProfileSvc->>DB: 写入 profile_update_logs
  ProfileSvc->>Log: 记录画像确认操作
  ProfileSvc-->>Web: 返回当前画像
  Web-->>Student: 展示动态学习画像
```

## 5. 多智能体资源包生成

```mermaid
sequenceDiagram
  autonumber
  actor Student as 学生
  participant Web as 前端
  participant AISvc as AI资源服务
  participant ProfileSvc as 画像服务
  participant FusionSvc as 融合图谱服务
  participant Agents as 多智能体编排器
  participant DB as 数据库
  participant Log as 日志服务

  Student->>Web: 选择目标岗位、课程、知识短板和资源类型
  Web->>AISvc: POST /api/v1/ai-generation-tasks
  AISvc->>ProfileSvc: 读取学生画像
  ProfileSvc->>DB: 查询 student_profiles 和 profile_dimension_values
  ProfileSvc-->>AISvc: 返回画像摘要
  AISvc->>FusionSvc: 读取岗课赛证融合关系
  FusionSvc->>DB: 查询 fusion_relations、job_capabilities、knowledge_points
  FusionSvc-->>AISvc: 返回融合上下文
  AISvc->>DB: 创建 ai_generation_tasks，状态 queued
  AISvc-->>Web: 返回 taskId
  Web-->>Student: 显示生成中

  AISvc->>Agents: 分发资源包生成任务
  par 文档生成
    Agents->>Agents: 文档生成智能体生成讲解文档
  and PPT生成
    Agents->>Agents: PPT生成智能体生成课件大纲
  and 题库生成
    Agents->>Agents: 题库智能体生成练习题
  and 思维导图生成
    Agents->>Agents: 思维导图智能体生成结构
  and 实操案例生成
    Agents->>Agents: 实操案例智能体生成案例
  end
  Agents-->>AISvc: 返回多个资源结果
  AISvc->>DB: 写入 ai_generated_resources
  AISvc->>DB: 写入 resource_packages
  AISvc->>DB: 写入 resource_package_items
  AISvc->>DB: 更新 ai_generation_tasks 为 finished
  AISvc->>Log: 记录 AI 生成操作和资源类型
  Web->>AISvc: GET /api/v1/ai-generation-tasks/{id}
  AISvc-->>Web: 返回已完成和 packageId
  Web-->>Student: 展示资源包
```

## 6. 学习路径生成与资源推送

```mermaid
sequenceDiagram
  autonumber
  actor Student as 学生
  participant Web as 前端
  participant PathSvc as 学习路径服务
  participant ProfileSvc as 画像服务
  participant FusionSvc as 融合图谱服务
  participant RecommendSvc as 推荐服务
  participant Agent as 学习路径规划智能体
  participant DB as 数据库

  Student->>Web: 选择目标岗位和学习周期
  Web->>PathSvc: POST /api/v1/learning-paths/generate
  PathSvc->>ProfileSvc: 获取学生画像和短板
  ProfileSvc->>DB: 查询画像、能力得分、错题、学习记录
  ProfileSvc-->>PathSvc: 返回画像和短板
  PathSvc->>FusionSvc: 获取目标岗位相关课程、竞赛、证书
  FusionSvc->>DB: 查询 fusion_relations
  FusionSvc-->>PathSvc: 返回融合关系
  PathSvc->>Agent: 生成阶段目标、步骤顺序和资源需求
  Agent-->>PathSvc: 返回学习路径草案
  PathSvc->>DB: 写入 learning_paths
  PathSvc->>DB: 写入 learning_path_steps
  PathSvc->>RecommendSvc: 生成资源推荐
  RecommendSvc->>DB: 写入 resource_recommendations
  PathSvc-->>Web: 返回学习路径

  Student->>Web: 接受路径
  Web->>PathSvc: POST /api/v1/learning-paths/{id}/accept
  PathSvc->>DB: 更新 learning_paths.path_status=accepted
  Web-->>Student: 首页工作台展示今日任务和推荐资源
```

## 7. 学习记录与学习效果评估

```mermaid
sequenceDiagram
  autonumber
  actor Student as 学生
  participant Web as 前端
  participant LearnSvc as 学习记录服务
  participant EvalSvc as 学习评估服务
  participant Agent as 学习评估智能体
  participant ProfileSvc as 画像服务
  participant DB as 数据库

  Student->>Web: 浏览资料、下载资源、观看视频或答题
  Web->>LearnSvc: POST /api/v1/learning-records
  LearnSvc->>DB: 写入 learning_records

  alt 学生提交答题
    Web->>LearnSvc: POST /api/v1/quiz-attempts
    LearnSvc->>DB: 写入 quiz_attempts
    alt 答错
      LearnSvc->>DB: 写入 wrong_questions
    end
  end

  Web->>EvalSvc: POST /api/v1/learning-evaluations/generate
  EvalSvc->>DB: 汇总学习时长、下载、答题、错题、项目提交
  EvalSvc->>Agent: 生成学习效果评估
  Agent-->>EvalSvc: 返回评估报告、薄弱知识点、调整建议
  EvalSvc->>DB: 写入 learning_evaluations
  EvalSvc->>DB: 更新 student_capability_scores
  EvalSvc->>ProfileSvc: 触发画像更新
  ProfileSvc->>DB: 写入 profile_update_logs
  EvalSvc-->>Web: 返回学习效果报告
  Web-->>Student: 展示学习评估和改进建议
```

## 8. 岗课赛证融合图谱生成

```mermaid
sequenceDiagram
  autonumber
  actor User as 学生或教师
  participant Web as 前端
  participant FusionSvc as 融合图谱服务
  participant ProfileSvc as 画像服务
  participant DB as 数据库
  participant Graph as 图谱构建器

  User->>Web: 打开融合图谱
  Web->>FusionSvc: GET /api/v1/fusion-graph/me
  FusionSvc->>ProfileSvc: 获取当前学生画像和目标岗位
  ProfileSvc->>DB: 查询 student_profiles、profile_dimension_values
  ProfileSvc-->>FusionSvc: 返回画像和目标岗位
  FusionSvc->>DB: 查询 job_roles、job_capabilities
  FusionSvc->>DB: 查询 course_knowledge_points、competition_tasks、certificate_assessment_points
  FusionSvc->>DB: 查询 fusion_relations
  FusionSvc->>DB: 查询 student_capability_scores
  FusionSvc->>Graph: 组装节点、边、权重、掌握状态
  Graph-->>FusionSvc: 返回图谱结构
  FusionSvc-->>Web: 返回 nodes、edges、weakPoints、recommendedPath
  Web-->>User: 展示岗课赛证关联图谱
```

## 9. 竞赛成果上传与审核

```mermaid
sequenceDiagram
  autonumber
  actor Coach as 带队老师
  actor Admin as 竞赛管理员
  participant Web as 前端
  participant CompSvc as 竞赛服务
  participant ReviewSvc as 审核服务
  participant ProfileSvc as 画像服务
  participant DB as 数据库
  participant Log as 日志服务

  Coach->>Web: 上传竞赛成果和获奖证明
  Web->>CompSvc: POST /api/v1/competition-results
  CompSvc->>DB: 写入 competition_results，review_status=pending
  CompSvc->>Log: 记录成果提交操作
  CompSvc-->>Web: 返回提交成功

  Admin->>Web: 查看待审核竞赛成果
  Web->>CompSvc: GET /api/v1/teacher-dashboard/pending-reviews
  CompSvc-->>Web: 返回竞赛成果待审列表

  Admin->>Web: 审核通过或打回
  Web->>ReviewSvc: POST /api/v1/competition-results/{id}/review
  ReviewSvc->>DB: 写入 review_records
  alt 打回
    ReviewSvc->>DB: 更新 competition_results.review_status=rejected
    ReviewSvc-->>Web: 返回打回意见
    Web-->>Coach: 提示补充材料后重新提交
  else 通过
    ReviewSvc->>DB: 更新 competition_results.review_status=approved
    ReviewSvc->>ProfileSvc: 竞赛成果进入学生画像
    ProfileSvc->>DB: 写入 profile_update_logs
    ReviewSvc-->>Web: 返回审核通过
    Web-->>Admin: 成果进入荣誉展示
  end
```

## 10. 证书成果上传与审核

```mermaid
sequenceDiagram
  autonumber
  actor Student as 学生
  actor Teacher as 小组负责教师
  participant Web as 前端
  participant CertSvc as 证书服务
  participant ReviewSvc as 审核服务
  participant ProfileSvc as 画像服务
  participant DB as 数据库

  Student->>Web: 上传证书编号和证明材料
  Web->>CertSvc: POST /api/v1/certificate-results
  CertSvc->>DB: 写入 certificate_results，review_status=pending
  CertSvc-->>Web: 返回提交成功

  Teacher->>Web: 查看待审核证书成果
  Web->>CertSvc: GET /api/v1/teacher-dashboard/pending-reviews
  CertSvc-->>Web: 返回证书待审列表

  Teacher->>Web: 审核证书成果
  Web->>ReviewSvc: POST /api/v1/certificate-results/{id}/review
  ReviewSvc->>DB: 写入 review_records，权限 certificate_result.review_group
  alt 打回
    ReviewSvc->>DB: 更新 certificate_results.review_status=rejected
    ReviewSvc-->>Web: 返回打回意见
    Web-->>Student: 提醒补充证明材料
  else 通过
    ReviewSvc->>DB: 更新 certificate_results.review_status=approved
    ReviewSvc->>DB: 更新 student_capability_scores
    ReviewSvc->>ProfileSvc: 证书成果进入学生画像
    ProfileSvc->>DB: 写入 profile_update_logs
    ReviewSvc-->>Web: 返回审核通过
    Web-->>Student: 展示证书达标状态
  end
```

## 11. 教师审核资源包

```mermaid
sequenceDiagram
  autonumber
  actor Student as 学生
  actor Teacher as 教师
  participant Web as 前端
  participant ResourceSvc as 资源包服务
  participant ReviewSvc as 审核服务
  participant DB as 数据库
  participant Log as 日志服务

  Student->>Web: 提交 AI 资源包审核
  Web->>ResourceSvc: POST /api/v1/resource-packages/{id}/submit-review
  ResourceSvc->>DB: 更新 resource_packages.review_status=pending_review
  ResourceSvc->>Log: 记录提交审核操作
  ResourceSvc-->>Web: 返回提交成功

  Teacher->>Web: 查看待审核资源包
  Web->>ResourceSvc: GET /api/v1/teacher-dashboard/pending-reviews
  ResourceSvc->>DB: 查询 resource_packages 和 resource_package_items
  ResourceSvc-->>Web: 返回待审核资源包

  Teacher->>Web: 审核资源包
  Web->>ReviewSvc: POST /api/v1/resource-packages/{id}/review
  ReviewSvc->>DB: 写入 review_records
  alt 打回
    ReviewSvc->>DB: 更新 resource_packages.review_status=rejected
    ReviewSvc-->>Web: 返回修改意见
    Web-->>Student: 提醒修改或重新生成
  else 通过
    ReviewSvc->>DB: 更新 resource_packages.review_status=approved
    Teacher->>Web: 发布资源包
    Web->>ResourceSvc: POST /api/v1/resource-packages/{id}/publish
    ResourceSvc->>DB: 更新 resource_packages.status=published
    ResourceSvc->>Log: 记录资源包发布操作
    ResourceSvc-->>Web: 返回发布成功
  end
```

## 12. 统计导出与脱敏记录

```mermaid
sequenceDiagram
  autonumber
  actor Leader as 专业负责人
  participant Web as 前端
  participant StatSvc as 统计服务
  participant ExportSvc as 导出服务
  participant DataMask as 脱敏组件
  participant DB as 数据库
  participant Log as 日志服务

  Leader->>Web: 选择专业、导出类型和是否脱敏
  Web->>ExportSvc: POST /api/v1/exports
  ExportSvc->>ExportSvc: 校验 statistics.export_major 权限和 major 数据范围
  ExportSvc->>DB: 写入 export_records，export_status=queued
  ExportSvc-->>Web: 返回 exportId

  ExportSvc->>StatSvc: 汇总画像、课程、岗位能力、竞赛、证书、项目数据
  StatSvc->>DB: 查询 students、student_profiles、learning_records
  StatSvc->>DB: 查询 student_capability_scores、competition_results、certificate_results
  DB-->>StatSvc: 返回统计数据
  StatSvc-->>ExportSvc: 返回导出数据集

  alt 默认脱敏导出
    ExportSvc->>DataMask: 手机号、邮箱、身份证号、画像详情、简历内容脱敏
    DataMask-->>ExportSvc: 返回脱敏数据
  else 完整导出
    ExportSvc->>ExportSvc: 校验完整导出权限
  end

  ExportSvc->>ExportSvc: 生成 Excel 文件
  ExportSvc->>DB: 更新 export_records 文件地址和完成状态
  ExportSvc->>Log: 写入 operation_logs，记录导出人、范围、是否脱敏
  Web->>ExportSvc: GET /api/v1/exports/{id}/download
  ExportSvc-->>Web: 返回下载地址
  Web-->>Leader: 下载统计表
```

## 13. 二期扩展：AI 简历生成与岗位投递

```mermaid
sequenceDiagram
  autonumber
  actor Student as 学生
  actor Teacher as 小组负责教师
  actor Mentor as 企业导师
  participant Web as 前端
  participant ResumeSvc as 简历服务
  participant JobSvc as 岗位服务
  participant ReviewSvc as 审核服务
  participant Agent as 简历生成智能体
  participant DB as 数据库

  Student->>Web: 选择目标岗位并生成 AI 简历
  Web->>ResumeSvc: POST /api/v1/resumes/generate
  ResumeSvc->>DB: 读取学生画像、课程、竞赛、证书、项目数据
  ResumeSvc->>Agent: 生成简历初稿
  Agent-->>ResumeSvc: 返回简历内容
  ResumeSvc->>DB: 加密写入 resumes.resume_content_encrypted
  ResumeSvc-->>Web: 返回简历草稿

  Student->>Web: 编辑并确认简历
  Web->>ResumeSvc: PUT /api/v1/resumes/{id}
  ResumeSvc->>DB: 更新 resumes，student_confirmed=1

  Student->>Web: 投递岗位
  Web->>JobSvc: POST /api/v1/job-applications
  JobSvc->>DB: 写入 job_applications，application_status=pending_teacher_review

  Teacher->>Web: 教师审核简历投递
  Web->>ReviewSvc: POST /api/v1/job-applications/{id}/teacher-review
  ReviewSvc->>DB: 写入 review_records
  alt 教师打回
    ReviewSvc->>DB: 更新 application_status=rejected_by_teacher
    ReviewSvc-->>Web: 返回打回意见
    Web-->>Student: 修改简历后重提
  else 教师通过
    ReviewSvc->>DB: 更新 application_status=pending_enterprise_review
  end

  Mentor->>Web: 企业导师审核简历
  Web->>ReviewSvc: POST /api/v1/job-applications/{id}/enterprise-review
  ReviewSvc->>DB: 写入 review_records
  alt 企业打回
    ReviewSvc->>DB: 更新 application_status=rejected_by_enterprise
    ReviewSvc-->>Web: 返回企业意见
  else 企业通过
    ReviewSvc->>DB: 更新 application_status=approved_by_enterprise
    Mentor->>Web: 推荐给企业
    Web->>JobSvc: POST /api/v1/job-applications/{id}/recommend
    JobSvc->>DB: 更新 application_status=recommended
  end
```

## 14. 时序图覆盖关系

| 序号 | 时序图 | 覆盖模块 |
| --- | --- | --- |
| 1 | 学生登录与首次改密 | 认证、用户、密码哈希、登录日志 |
| 2 | 专业负责人导入学生账号 | 学生导入、账号生成、角色绑定 |
| 3 | 对话式学习画像构建 | 画像会话、智能体、画像确认 |
| 4 | 多智能体资源包生成 | AI 任务、资源包、融合上下文 |
| 5 | 学习路径生成与资源推送 | 学习路径、资源推荐、画像短板 |
| 6 | 学习记录与学习效果评估 | 学习记录、答题、错题、评估 |
| 7 | 岗课赛证融合图谱生成 | 岗位能力、课程、竞赛、证书、融合关系 |
| 8 | 竞赛成果上传与审核 | 竞赛成果、审核、荣誉展示 |
| 9 | 证书成果上传与审核 | 证书成果、审核、达标统计 |
| 10 | 教师审核资源包 | 资源包审核、发布 |
| 11 | 统计导出与脱敏记录 | 统计、导出、脱敏、日志 |
| 12 | AI 简历生成与岗位投递 | 二期就业扩展 |
