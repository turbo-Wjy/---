package com.example.ailearning.module.course.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.ailearning.common.security.CurrentUserHolder;
import com.example.ailearning.module.course.dto.LearningRecordRequest;
import com.example.ailearning.module.course.entity.CourseKnowledgePoint;
import com.example.ailearning.module.course.entity.LearningRecord;
import com.example.ailearning.module.course.mapper.CourseKnowledgePointMapper;
import com.example.ailearning.module.course.mapper.LearningRecordMapper;
import com.example.ailearning.module.course.vo.CourseProgressVO;
import com.example.ailearning.module.course.vo.LearningRecordVO;
import com.example.ailearning.module.student.service.StudentContextService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class LearningRecordService {
    private final CourseService courseService;
    private final StudentContextService studentContextService;
    private final LearningRecordMapper learningRecordMapper;
    private final CourseKnowledgePointMapper knowledgePointMapper;

    public LearningRecordService(
            CourseService courseService,
            StudentContextService studentContextService,
            LearningRecordMapper learningRecordMapper,
            CourseKnowledgePointMapper knowledgePointMapper
    ) {
        this.courseService = courseService;
        this.studentContextService = studentContextService;
        this.learningRecordMapper = learningRecordMapper;
        this.knowledgePointMapper = knowledgePointMapper;
    }

    @Transactional(rollbackFor = Exception.class)
    public LearningRecordVO create(LearningRecordRequest request) {
        courseService.getEntity(request.getCourseId());
        Long studentId = studentContextService.resolveStudentId(request.getStudentId());
        LearningRecord record = new LearningRecord();
        record.setStudentId(studentId);
        record.setCourseId(request.getCourseId());
        record.setResourceId(request.getResourceId());
        record.setActionType(request.getActionType());
        record.setDurationSeconds(request.getDurationSeconds());
        record.setCompleted(Boolean.TRUE.equals(request.getCompleted()));
        record.setStatus("active");
        record.setCreatedBy(CurrentUserHolder.getRequired().getUserId());
        learningRecordMapper.insert(record);
        return toVO(record);
    }

    public List<LearningRecordVO> myRecords(Long courseId) {
        Long studentId = studentContextService.currentStudentIdRequired();
        LambdaQueryWrapper<LearningRecord> wrapper = new LambdaQueryWrapper<LearningRecord>()
                .eq(LearningRecord::getStudentId, studentId)
                .isNull(LearningRecord::getDeletedAt)
                .orderByDesc(LearningRecord::getCreatedAt);
        if (courseId != null) {
            wrapper.eq(LearningRecord::getCourseId, courseId);
        }
        return learningRecordMapper.selectList(wrapper).stream().map(this::toVO).toList();
    }

    public CourseProgressVO myCourseProgress(Long courseId) {
        courseService.getEntity(courseId);
        Long studentId = studentContextService.currentStudentIdRequired();
        long totalKnowledgePoints = knowledgePointMapper.selectCount(new LambdaQueryWrapper<CourseKnowledgePoint>()
                .eq(CourseKnowledgePoint::getCourseId, courseId)
                .isNull(CourseKnowledgePoint::getDeletedAt));
        List<LearningRecord> records = learningRecordMapper.selectList(new LambdaQueryWrapper<LearningRecord>()
                .eq(LearningRecord::getStudentId, studentId)
                .eq(LearningRecord::getCourseId, courseId)
                .isNull(LearningRecord::getDeletedAt));
        CourseProgressVO vo = new CourseProgressVO();
        vo.setCourseId(courseId);
        vo.setStudentId(studentId);
        vo.setTotalKnowledgePoints(totalKnowledgePoints);
        vo.setCompletedLearningRecords(records.stream().filter(r -> Boolean.TRUE.equals(r.getCompleted())).count());
        vo.setTotalDurationSeconds(records.stream()
                .map(LearningRecord::getDurationSeconds)
                .filter(v -> v != null)
                .mapToLong(Integer::longValue)
                .sum());
        return vo;
    }

    private LearningRecordVO toVO(LearningRecord record) {
        LearningRecordVO vo = new LearningRecordVO();
        vo.setId(record.getId());
        vo.setStudentId(record.getStudentId());
        vo.setCourseId(record.getCourseId());
        vo.setResourceId(record.getResourceId());
        vo.setActionType(record.getActionType());
        vo.setDurationSeconds(record.getDurationSeconds());
        vo.setCompleted(record.getCompleted());
        return vo;
    }
}
