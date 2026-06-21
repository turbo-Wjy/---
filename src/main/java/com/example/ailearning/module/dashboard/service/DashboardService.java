package com.example.ailearning.module.dashboard.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.ailearning.common.security.CurrentUser;
import com.example.ailearning.common.security.CurrentUserHolder;
import com.example.ailearning.module.ai.entity.AiGeneratedResource;
import com.example.ailearning.module.ai.mapper.AiGeneratedResourceMapper;
import com.example.ailearning.module.dashboard.vo.ClassStudentOverviewVO;
import com.example.ailearning.module.dashboard.vo.ClassWeakPointVO;
import com.example.ailearning.module.dashboard.vo.DashboardItemVO;
import com.example.ailearning.module.dashboard.vo.DashboardMetricVO;
import com.example.ailearning.module.dashboard.vo.DashboardOverviewVO;
import com.example.ailearning.module.dashboard.vo.LearningPathProgressVO;
import com.example.ailearning.module.certificate.entity.CertificateResult;
import com.example.ailearning.module.certificate.mapper.CertificateResultMapper;
import com.example.ailearning.module.competition.entity.CompetitionResult;
import com.example.ailearning.module.competition.mapper.CompetitionResultMapper;
import com.example.ailearning.module.fusion.entity.StudentCapabilityScore;
import com.example.ailearning.module.fusion.mapper.StudentCapabilityScoreMapper;
import com.example.ailearning.module.learning.entity.LearningPath;
import com.example.ailearning.module.learning.entity.LearningPathStep;
import com.example.ailearning.module.learning.entity.ResourceRecommendation;
import com.example.ailearning.module.learning.mapper.LearningPathMapper;
import com.example.ailearning.module.learning.mapper.LearningPathStepMapper;
import com.example.ailearning.module.learning.mapper.ResourceRecommendationMapper;
import com.example.ailearning.module.learning.vo.ResourceRecommendationVO;
import com.example.ailearning.module.profile.entity.StudentProfile;
import com.example.ailearning.module.profile.mapper.StudentProfileMapper;
import com.example.ailearning.module.profile.service.ProfileService;
import com.example.ailearning.module.profile.vo.LearningProfileVO;
import com.example.ailearning.module.project.entity.ProjectDeliverable;
import com.example.ailearning.module.project.mapper.ProjectDeliverableMapper;
import com.example.ailearning.module.resource.entity.ResourcePackage;
import com.example.ailearning.module.resource.mapper.ResourcePackageMapper;
import com.example.ailearning.module.resource.vo.AiGeneratedResourceVO;
import com.example.ailearning.module.student.entity.Student;
import com.example.ailearning.module.student.mapper.StudentMapper;
import com.example.ailearning.module.student.service.StudentContextService;
import com.example.ailearning.module.teacher.entity.Teacher;
import com.example.ailearning.module.teacher.entity.TeacherStudentGroup;
import com.example.ailearning.module.teacher.mapper.TeacherMapper;
import com.example.ailearning.module.teacher.mapper.TeacherStudentGroupMapper;
import com.example.ailearning.module.user.entity.User;
import com.example.ailearning.module.user.mapper.UserMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class DashboardService {
    private final StudentContextService studentContextService;
    private final StudentMapper studentMapper;
    private final TeacherMapper teacherMapper;
    private final TeacherStudentGroupMapper teacherStudentGroupMapper;
    private final StudentProfileMapper profileMapper;
    private final ProfileService profileService;
    private final LearningPathMapper pathMapper;
    private final LearningPathStepMapper stepMapper;
    private final ResourceRecommendationMapper recommendationMapper;
    private final AiGeneratedResourceMapper resourceMapper;
    private final ResourcePackageMapper packageMapper;
    private final StudentCapabilityScoreMapper capabilityScoreMapper;
    private final UserMapper userMapper;
    private final CompetitionResultMapper competitionResultMapper;
    private final CertificateResultMapper certificateResultMapper;
    private final ProjectDeliverableMapper projectDeliverableMapper;

    public DashboardService(
            StudentContextService studentContextService,
            StudentMapper studentMapper,
            TeacherMapper teacherMapper,
            TeacherStudentGroupMapper teacherStudentGroupMapper,
            StudentProfileMapper profileMapper,
            ProfileService profileService,
            LearningPathMapper pathMapper,
            LearningPathStepMapper stepMapper,
            ResourceRecommendationMapper recommendationMapper,
            AiGeneratedResourceMapper resourceMapper,
            ResourcePackageMapper packageMapper,
            StudentCapabilityScoreMapper capabilityScoreMapper,
            UserMapper userMapper,
            CompetitionResultMapper competitionResultMapper,
            CertificateResultMapper certificateResultMapper,
            ProjectDeliverableMapper projectDeliverableMapper
    ) {
        this.studentContextService = studentContextService;
        this.studentMapper = studentMapper;
        this.teacherMapper = teacherMapper;
        this.teacherStudentGroupMapper = teacherStudentGroupMapper;
        this.profileMapper = profileMapper;
        this.profileService = profileService;
        this.pathMapper = pathMapper;
        this.stepMapper = stepMapper;
        this.recommendationMapper = recommendationMapper;
        this.resourceMapper = resourceMapper;
        this.packageMapper = packageMapper;
        this.capabilityScoreMapper = capabilityScoreMapper;
        this.userMapper = userMapper;
        this.competitionResultMapper = competitionResultMapper;
        this.certificateResultMapper = certificateResultMapper;
        this.projectDeliverableMapper = projectDeliverableMapper;
    }

    public DashboardOverviewVO overview() {
        CurrentUser currentUser = CurrentUserHolder.getRequired();
        Student student = studentContextService.currentStudent();
        if (student != null) {
            return studentOverview(currentUser, student);
        }
        return staffOverview(currentUser);
    }

    public DashboardOverviewVO teacherOverview() {
        CurrentUser currentUser = CurrentUserHolder.getRequired();
        DashboardOverviewVO overview = staffOverview(currentUser);
        overview.setDashboardType("teacher");
        return overview;
    }

    public List<DashboardItemVO> teacherPendingReviews() {
        return pendingReviewsForCurrentStaff();
    }

    public List<ClassStudentOverviewVO> classStudents(Long classId) {
        return visibleStudentsInClass(classId).stream().map(this::classStudentVO).toList();
    }

    public List<LearningProfileVO> classLearningProfiles(Long classId) {
        return visibleStudentsInClass(classId).stream()
                .map(Student::getId)
                .map(this::safeStudentProfile)
                .filter(profile -> profile != null && profile.getId() != null)
                .toList();
    }

    public List<ClassWeakPointVO> classWeakPoints(Long classId) {
        List<Student> students = visibleStudentsInClass(classId);
        if (students.isEmpty()) {
            return List.of();
        }
        List<Long> studentIds = students.stream().map(Student::getId).toList();
        List<StudentCapabilityScore> scores = capabilityScoreMapper.selectList(new LambdaQueryWrapper<StudentCapabilityScore>()
                .in(StudentCapabilityScore::getStudentId, studentIds)
                .isNull(StudentCapabilityScore::getDeletedAt));
        Map<String, List<StudentCapabilityScore>> grouped = scores.stream()
                .filter(this::isWeakCapabilityScore)
                .collect(Collectors.groupingBy(
                        score -> score.getTargetType() + "#" + score.getTargetId(),
                        LinkedHashMap::new,
                        Collectors.toList()
                ));
        return grouped.values().stream()
                .map(list -> weakPointVO(list, students.size()))
                .sorted(Comparator.comparing(ClassWeakPointVO::getAverageScore, Comparator.nullsLast(Comparator.naturalOrder())))
                .limit(10)
                .toList();
    }

    private List<Student> visibleStudentsInClass(Long classId) {
        List<Student> classStudents = studentMapper.selectList(new LambdaQueryWrapper<Student>()
                .eq(Student::getClassId, classId)
                .isNull(Student::getDeletedAt)
                .orderByAsc(Student::getStudentNo)
                .orderByAsc(Student::getId));
        CurrentUser currentUser = CurrentUserHolder.getRequired();
        if (canViewAllStudents(currentUser)) {
            return classStudents;
        }
        Teacher teacher = currentTeacher(currentUser);
        if (teacher == null || classStudents.isEmpty()) {
            return List.of();
        }
        Set<Long> assignedStudentIds = teacherStudentGroupMapper.selectList(new LambdaQueryWrapper<TeacherStudentGroup>()
                        .eq(TeacherStudentGroup::getTeacherId, teacher.getId())
                        .isNull(TeacherStudentGroup::getDeletedAt))
                .stream()
                .map(TeacherStudentGroup::getStudentId)
                .collect(Collectors.toSet());
        return classStudents.stream()
                .filter(student -> assignedStudentIds.contains(student.getId()))
                .toList();
    }

    private boolean canViewAllStudents(CurrentUser currentUser) {
        return currentUser.getRoleCodes().stream()
                .anyMatch(role -> role.equals("admin") || role.equals("major_leader") || role.equals("data_viewer"));
    }

    private Teacher currentTeacher(CurrentUser currentUser) {
        return teacherMapper.selectOne(new LambdaQueryWrapper<Teacher>()
                .eq(Teacher::getUserId, currentUser.getUserId())
                .isNull(Teacher::getDeletedAt)
                .last("LIMIT 1"));
    }

    private ClassStudentOverviewVO classStudentVO(Student student) {
        StudentProfile profile = latestProfileOrNull(student.getId());
        LearningPath latestPath = latestPath(student.getId());
        List<LearningPathStep> steps = latestPath == null ? List.of() : steps(latestPath.getId());

        ClassStudentOverviewVO vo = new ClassStudentOverviewVO();
        vo.setStudentId(student.getId());
        vo.setStudentNo(student.getStudentNo());
        vo.setRealName(realName(student.getUserId()));
        vo.setClassId(student.getClassId());
        vo.setGrade(student.getGrade());
        vo.setEnrollmentStatus(student.getEnrollmentStatus());
        vo.setProfileVersion(profile == null ? null : profile.getProfileVersion());
        vo.setProfileCompletenessScore(profile == null ? null : profile.getCompletenessScore());
        vo.setLastProfileGeneratedAt(profile == null ? null : profile.getLastGeneratedAt());
        vo.setLearningPathProgressPercent(progressPercent(steps));
        vo.setWeakPointCount(weakPointCount(student.getId()));
        return vo;
    }

    private LearningProfileVO safeStudentProfile(Long studentId) {
        try {
            return profileService.studentProfile(studentId);
        } catch (RuntimeException exception) {
            return null;
        }
    }

    private String realName(Long userId) {
        if (userId == null) {
            return null;
        }
        User user = userMapper.selectById(userId);
        return user == null ? null : user.getRealName();
    }

    private StudentProfile latestProfileOrNull(Long studentId) {
        return profileMapper.selectOne(new LambdaQueryWrapper<StudentProfile>()
                .eq(StudentProfile::getStudentId, studentId)
                .isNull(StudentProfile::getDeletedAt)
                .orderByDesc(StudentProfile::getProfileVersion)
                .last("LIMIT 1"));
    }

    private long weakPointCount(Long studentId) {
        return capabilityScoreMapper.selectList(new LambdaQueryWrapper<StudentCapabilityScore>()
                        .eq(StudentCapabilityScore::getStudentId, studentId)
                        .isNull(StudentCapabilityScore::getDeletedAt))
                .stream()
                .filter(this::isWeakCapabilityScore)
                .count();
    }

    private boolean isWeakCapabilityScore(StudentCapabilityScore score) {
        if (score.getScore() != null && score.getScore().compareTo(BigDecimal.valueOf(60)) < 0) {
            return true;
        }
        return "weak".equals(score.getMasteryStatus()) || "not_mastered".equals(score.getMasteryStatus());
    }

    private ClassWeakPointVO weakPointVO(List<StudentCapabilityScore> scores, int totalStudentCount) {
        StudentCapabilityScore first = scores.get(0);
        BigDecimal average = scores.stream()
                .map(StudentCapabilityScore::getScore)
                .filter(score -> score != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        long scoredCount = scores.stream().filter(score -> score.getScore() != null).count();
        if (scoredCount > 0) {
            average = average.divide(BigDecimal.valueOf(scoredCount), 2, RoundingMode.HALF_UP);
        }

        ClassWeakPointVO vo = new ClassWeakPointVO();
        vo.setTargetType(first.getTargetType());
        vo.setTargetId(first.getTargetId());
        vo.setLabel(first.getTargetType() + "#" + first.getTargetId());
        vo.setAverageScore(scoredCount == 0 ? null : average);
        vo.setAffectedStudentCount(scores.stream().map(StudentCapabilityScore::getStudentId).distinct().count());
        vo.setTotalStudentCount((long) totalStudentCount);
        return vo;
    }

    private DashboardOverviewVO studentOverview(CurrentUser currentUser, Student student) {
        LearningPath latestPath = latestPath(student.getId());
        List<LearningPathStep> steps = latestPath == null ? List.of() : steps(latestPath.getId());
        List<ResourceRecommendationVO> recommendations = recommendations(student.getId());
        List<DashboardItemVO> todayTasks = steps.stream()
                .filter(step -> !"completed".equals(step.getCompletionStatus()))
                .limit(5)
                .map(this::taskItem)
                .toList();

        DashboardOverviewVO overview = new DashboardOverviewVO();
        overview.setDashboardType("student");
        overview.setGreetingName(currentUser.getRealName());
        overview.setMetrics(List.of(
                new DashboardMetricVO("path_progress", "学习路径进度", progressPercent(steps), "%"),
                new DashboardMetricVO("today_tasks", "今日待办", todayTasks.size(), "项"),
                new DashboardMetricVO("recommended_resources", "推荐资源", recommendations.size(), "个"),
                new DashboardMetricVO("profile_versions", "画像版本", profileCount(student.getId()), "版")
        ));
        overview.setTodayTasks(todayTasks);
        overview.setLearningReminders(studentReminders(latestPath, steps, recommendations));
        overview.setRecommendedResources(recommendations);
        overview.setPendingReviews(pendingReviewsForStudent(student.getId()));
        overview.setLearningPathProgress(pathProgress(latestPath, steps));
        return overview;
    }

    private DashboardOverviewVO staffOverview(CurrentUser currentUser) {
        List<DashboardItemVO> pendingReviews = pendingReviewsForCurrentStaff();
        long activeStudents = studentMapper.selectCount(new LambdaQueryWrapper<Student>().isNull(Student::getDeletedAt).eq(Student::getStatus, "active"));
        long activePaths = pathMapper.selectCount(new LambdaQueryWrapper<LearningPath>().isNull(LearningPath::getDeletedAt).eq(LearningPath::getStatus, "active"));
        long activeRecommendations = recommendationMapper.selectCount(new LambdaQueryWrapper<ResourceRecommendation>().isNull(ResourceRecommendation::getDeletedAt).eq(ResourceRecommendation::getStatus, "active"));

        DashboardOverviewVO overview = new DashboardOverviewVO();
        overview.setDashboardType("staff");
        overview.setGreetingName(currentUser.getRealName());
        overview.setMetrics(List.of(
                new DashboardMetricVO("active_students", "活跃学生", activeStudents, "人"),
                new DashboardMetricVO("active_paths", "学习路径", activePaths, "条"),
                new DashboardMetricVO("recommendations", "资源推荐", activeRecommendations, "条"),
                new DashboardMetricVO("pending_reviews", "待审核事项", pendingReviews.size(), "项")
        ));
        overview.setTodayTasks(List.of());
        overview.setLearningReminders(staffReminders(pendingReviews));
        overview.setRecommendedResources(List.of());
        overview.setPendingReviews(pendingReviews);
        overview.setLearningPathProgress(null);
        return overview;
    }

    private List<DashboardItemVO> pendingReviewsForCurrentStaff() {
        CurrentUser currentUser = CurrentUserHolder.getRequired();
        List<DashboardItemVO> items = new ArrayList<>();
        if (currentUser.getRoleCodes().stream().anyMatch(role -> role.equals("admin") || role.equals("major_leader") || role.equals("data_viewer"))) {
            items.addAll(packageMapper.selectList(new LambdaQueryWrapper<ResourcePackage>()
                            .eq(ResourcePackage::getReviewStatus, "pending_review")
                            .isNull(ResourcePackage::getDeletedAt)
                            .orderByDesc(ResourcePackage::getCreatedAt))
                    .stream().map(this::reviewItem).toList());
            items.addAll(globalAchievementReviews());
            return items.stream().limit(20).toList();
        }
        Teacher teacher = teacherMapper.selectOne(new LambdaQueryWrapper<Teacher>()
                .eq(Teacher::getUserId, currentUser.getUserId())
                .isNull(Teacher::getDeletedAt)
                .last("LIMIT 1"));
        if (teacher == null) {
            return List.of();
        }
        List<Long> studentIds = teacherStudentGroupMapper.selectList(new LambdaQueryWrapper<TeacherStudentGroup>()
                        .eq(TeacherStudentGroup::getTeacherId, teacher.getId())
                        .isNull(TeacherStudentGroup::getDeletedAt))
                .stream().map(TeacherStudentGroup::getStudentId).toList();
        if (studentIds.isEmpty()) {
            return List.of();
        }
        items.addAll(packageMapper.selectList(new LambdaQueryWrapper<ResourcePackage>()
                        .in(ResourcePackage::getStudentId, studentIds)
                        .eq(ResourcePackage::getReviewStatus, "pending_review")
                        .isNull(ResourcePackage::getDeletedAt)
                        .orderByDesc(ResourcePackage::getCreatedAt))
                .stream().map(this::reviewItem).toList());
        items.addAll(assignedAchievementReviews(studentIds));
        return items.stream().limit(20).toList();
    }

    private List<DashboardItemVO> globalAchievementReviews() {
        List<DashboardItemVO> items = new ArrayList<>();
        items.addAll(competitionResultMapper.selectList(new LambdaQueryWrapper<CompetitionResult>()
                        .eq(CompetitionResult::getReviewStatus, "pending")
                        .isNull(CompetitionResult::getDeletedAt)
                        .orderByDesc(CompetitionResult::getCreatedAt))
                .stream().map(this::competitionResultItem).toList());
        items.addAll(certificateResultMapper.selectList(new LambdaQueryWrapper<CertificateResult>()
                        .eq(CertificateResult::getReviewStatus, "pending")
                        .isNull(CertificateResult::getDeletedAt)
                        .orderByDesc(CertificateResult::getCreatedAt))
                .stream().map(this::certificateResultItem).toList());
        items.addAll(projectDeliverableMapper.selectList(new LambdaQueryWrapper<ProjectDeliverable>()
                        .eq(ProjectDeliverable::getReviewStatus, "pending")
                        .isNull(ProjectDeliverable::getDeletedAt)
                        .orderByDesc(ProjectDeliverable::getCreatedAt))
                .stream().map(this::projectDeliverableItem).toList());
        return items;
    }

    private List<DashboardItemVO> assignedAchievementReviews(List<Long> studentIds) {
        List<DashboardItemVO> items = new ArrayList<>();
        items.addAll(competitionResultMapper.selectList(new LambdaQueryWrapper<CompetitionResult>()
                        .in(CompetitionResult::getStudentId, studentIds)
                        .eq(CompetitionResult::getReviewStatus, "pending")
                        .isNull(CompetitionResult::getDeletedAt)
                        .orderByDesc(CompetitionResult::getCreatedAt))
                .stream().map(this::competitionResultItem).toList());
        items.addAll(certificateResultMapper.selectList(new LambdaQueryWrapper<CertificateResult>()
                        .in(CertificateResult::getStudentId, studentIds)
                        .eq(CertificateResult::getReviewStatus, "pending")
                        .isNull(CertificateResult::getDeletedAt)
                        .orderByDesc(CertificateResult::getCreatedAt))
                .stream().map(this::certificateResultItem).toList());
        items.addAll(projectDeliverableMapper.selectList(new LambdaQueryWrapper<ProjectDeliverable>()
                        .in(ProjectDeliverable::getStudentId, studentIds)
                        .eq(ProjectDeliverable::getReviewStatus, "pending")
                        .isNull(ProjectDeliverable::getDeletedAt)
                        .orderByDesc(ProjectDeliverable::getCreatedAt))
                .stream().map(this::projectDeliverableItem).toList());
        return items;
    }

    private List<DashboardItemVO> pendingReviewsForStudent(Long studentId) {
        return packageMapper.selectList(new LambdaQueryWrapper<ResourcePackage>()
                        .eq(ResourcePackage::getStudentId, studentId)
                        .in(ResourcePackage::getReviewStatus, List.of("generated", "pending_review", "rejected"))
                        .isNull(ResourcePackage::getDeletedAt)
                        .orderByDesc(ResourcePackage::getCreatedAt))
                .stream().limit(5).map(pkg -> {
                    DashboardItemVO item = reviewItem(pkg);
                    item.setItemType("resource_package_status");
                    item.setTitle("资源包状态：" + pkg.getPackageTitle());
                    return item;
                }).toList();
    }

    private List<ResourceRecommendationVO> recommendations(Long studentId) {
        return recommendationMapper.selectList(new LambdaQueryWrapper<ResourceRecommendation>()
                        .eq(ResourceRecommendation::getStudentId, studentId)
                        .isNull(ResourceRecommendation::getDeletedAt)
                        .orderByDesc(ResourceRecommendation::getCreatedAt))
                .stream().limit(6).map(this::recommendationVO).toList();
    }

    private List<DashboardItemVO> studentReminders(LearningPath latestPath, List<LearningPathStep> steps, List<ResourceRecommendationVO> recommendations) {
        List<DashboardItemVO> reminders = new ArrayList<>();
        if (latestPath == null) {
            reminders.add(item("learning_path", "生成个性化学习路径", "建议先基于学习画像和目标岗位生成学习路径。", "important", "learning_path", null));
        } else if (!"accepted".equals(latestPath.getPathStatus())) {
            reminders.add(item("learning_path", "确认学习路径", "当前学习路径待确认，确认后会进入今日任务。", "important", "learning_path", latestPath.getId()));
        }
        long unfinished = steps.stream().filter(step -> !"completed".equals(step.getCompletionStatus())).count();
        if (unfinished > 0) {
            reminders.add(item("learning_task", "还有未完成学习步骤", "当前路径还有 " + unfinished + " 个步骤待完成。", "normal", "learning_path", latestPath.getId()));
        }
        if (!recommendations.isEmpty()) {
            reminders.add(item("resource_recommendation", "查看推荐资源", "系统已根据画像和资源包为你推荐学习资料。", "normal", "resource_recommendation", recommendations.get(0).getId()));
        }
        return reminders;
    }

    private List<DashboardItemVO> staffReminders(List<DashboardItemVO> pendingReviews) {
        if (pendingReviews.isEmpty()) {
            return List.of(item("teacher_dashboard", "暂无待审核事项", "当前没有新的资源包审核任务。", "normal", "resource_package", null));
        }
        return List.of(item("teacher_dashboard", "处理待审核资源包", "当前有 " + pendingReviews.size() + " 个资源包待审核。", "important", "resource_package", pendingReviews.get(0).getTargetId()));
    }

    private LearningPath latestPath(Long studentId) {
        return pathMapper.selectOne(new LambdaQueryWrapper<LearningPath>()
                .eq(LearningPath::getStudentId, studentId)
                .isNull(LearningPath::getDeletedAt)
                .orderByDesc(LearningPath::getCreatedAt)
                .last("LIMIT 1"));
    }

    private List<LearningPathStep> steps(Long pathId) {
        return stepMapper.selectList(new LambdaQueryWrapper<LearningPathStep>()
                .eq(LearningPathStep::getPathId, pathId)
                .isNull(LearningPathStep::getDeletedAt)
                .orderByAsc(LearningPathStep::getStepOrder)
                .orderByAsc(LearningPathStep::getId));
    }

    private LearningPathProgressVO pathProgress(LearningPath path, List<LearningPathStep> steps) {
        if (path == null) {
            return null;
        }
        LearningPathProgressVO progress = new LearningPathProgressVO();
        progress.setPathId(path.getId());
        progress.setPathTitle(path.getTitle());
        progress.setPathStatus(path.getPathStatus());
        progress.setTotalSteps(steps.size());
        progress.setCompletedSteps(steps.stream().filter(step -> "completed".equals(step.getCompletionStatus())).count());
        progress.setProgressPercent(progressPercent(steps));
        return progress;
    }

    private int progressPercent(List<LearningPathStep> steps) {
        if (steps.isEmpty()) {
            return 0;
        }
        long completed = steps.stream().filter(step -> "completed".equals(step.getCompletionStatus())).count();
        return (int) Math.round(completed * 100.0 / steps.size());
    }

    private long profileCount(Long studentId) {
        return profileMapper.selectCount(new LambdaQueryWrapper<StudentProfile>()
                .eq(StudentProfile::getStudentId, studentId)
                .isNull(StudentProfile::getDeletedAt));
    }

    private DashboardItemVO taskItem(LearningPathStep step) {
        DashboardItemVO item = new DashboardItemVO();
        item.setId(step.getId());
        item.setItemType("learning_path_step");
        item.setTitle(step.getTitle());
        item.setDescription(step.getResourceId() == null ? "路径任务" : "关联学习资源 ID：" + step.getResourceId());
        item.setStatus(step.getCompletionStatus());
        item.setPriority("normal");
        item.setTargetType("learning_path_step");
        item.setTargetId(step.getId());
        item.setCreatedAt(step.getCreatedAt());
        return item;
    }

    private DashboardItemVO reviewItem(ResourcePackage resourcePackage) {
        DashboardItemVO item = new DashboardItemVO();
        item.setId(resourcePackage.getId());
        item.setItemType("resource_package_review");
        item.setTitle("资源包审核：" + resourcePackage.getPackageTitle());
        item.setDescription("学生 ID：" + resourcePackage.getStudentId());
        item.setStatus(resourcePackage.getReviewStatus());
        item.setPriority("pending_review".equals(resourcePackage.getReviewStatus()) ? "important" : "normal");
        item.setTargetType("resource_package");
        item.setTargetId(resourcePackage.getId());
        item.setCreatedAt(resourcePackage.getCreatedAt());
        return item;
    }

    private DashboardItemVO competitionResultItem(CompetitionResult result) {
        DashboardItemVO item = item("competition_result_review", "竞赛成果审核：" + result.getAwardName(), "学生 ID：" + result.getStudentId(), "important", "competition_result", result.getId());
        item.setId(result.getId());
        item.setStatus(result.getReviewStatus());
        item.setCreatedAt(result.getCreatedAt());
        return item;
    }

    private DashboardItemVO certificateResultItem(CertificateResult result) {
        DashboardItemVO item = item("certificate_result_review", "证书成果审核：" + result.getCertificateNo(), "学生 ID：" + result.getStudentId(), "important", "certificate_result", result.getId());
        item.setId(result.getId());
        item.setStatus(result.getReviewStatus());
        item.setCreatedAt(result.getCreatedAt());
        return item;
    }

    private DashboardItemVO projectDeliverableItem(ProjectDeliverable deliverable) {
        DashboardItemVO item = item("project_deliverable_review", "项目交付物审核：" + deliverable.getTitle(), "学生 ID：" + deliverable.getStudentId(), "important", "project_deliverable", deliverable.getId());
        item.setId(deliverable.getId());
        item.setStatus(deliverable.getReviewStatus());
        item.setCreatedAt(deliverable.getCreatedAt());
        return item;
    }

    private DashboardItemVO item(String type, String title, String description, String priority, String targetType, Long targetId) {
        DashboardItemVO item = new DashboardItemVO();
        item.setItemType(type);
        item.setTitle(title);
        item.setDescription(description);
        item.setPriority(priority);
        item.setTargetType(targetType);
        item.setTargetId(targetId);
        item.setStatus("active");
        return item;
    }

    private ResourceRecommendationVO recommendationVO(ResourceRecommendation recommendation) {
        ResourceRecommendationVO vo = new ResourceRecommendationVO();
        vo.setId(recommendation.getId());
        vo.setStudentId(recommendation.getStudentId());
        vo.setResourceId(recommendation.getResourceId());
        vo.setRecommendReason(recommendation.getRecommendReason());
        vo.setSourceProfileId(recommendation.getSourceProfileId());
        vo.setViewStatus(recommendation.getViewStatus());
        vo.setStatus(recommendation.getStatus());
        vo.setCreatedAt(recommendation.getCreatedAt());
        AiGeneratedResource resource = resourceMapper.selectById(recommendation.getResourceId());
        if (resource != null && resource.getDeletedAt() == null) {
            vo.setResource(resourceVO(resource));
        }
        return vo;
    }

    private AiGeneratedResourceVO resourceVO(AiGeneratedResource resource) {
        AiGeneratedResourceVO vo = new AiGeneratedResourceVO();
        vo.setId(resource.getId());
        vo.setTaskId(resource.getTaskId());
        vo.setResourceType(resource.getResourceType());
        vo.setTitle(resource.getTitle());
        vo.setContentUrl(resource.getContentUrl());
        vo.setContentText(resource.getContentText());
        vo.setMetadata(resource.getMetadataJson());
        vo.setStatus(resource.getStatus());
        return vo;
    }
}
