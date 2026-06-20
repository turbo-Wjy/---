package com.example.ailearning.module.statistics.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.ailearning.common.security.CurrentUser;
import com.example.ailearning.module.audit.service.AuditService;
import com.example.ailearning.module.course.mapper.LearningRecordMapper;
import com.example.ailearning.module.course.mapper.QuizAttemptMapper;
import com.example.ailearning.module.course.mapper.WrongQuestionMapper;
import com.example.ailearning.module.evaluation.mapper.LearningEvaluationMapper;
import com.example.ailearning.module.fusion.mapper.FusionRelationMapper;
import com.example.ailearning.module.fusion.mapper.StudentCapabilityScoreMapper;
import com.example.ailearning.module.profile.mapper.StudentProfileMapper;
import com.example.ailearning.module.statistics.dto.ExportRequest;
import com.example.ailearning.module.statistics.entity.ExportRecord;
import com.example.ailearning.module.statistics.mapper.ExportRecordMapper;
import com.example.ailearning.module.statistics.vo.ExportRecordVO;
import com.example.ailearning.module.statistics.vo.StatisticsOverviewVO;
import com.example.ailearning.module.student.mapper.StudentMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class StatisticsServiceTest {

    @Test
    void profileStatisticsReturnsStudentAndProfileMetrics() {
        StudentMapper studentMapper = mock(StudentMapper.class);
        StudentProfileMapper profileMapper = mock(StudentProfileMapper.class);
        StatisticsService service = service(studentMapper, profileMapper, mock(ExportRecordMapper.class));

        when(studentMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(10L);
        when(profileMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(6L);

        StatisticsOverviewVO result = service.profileStatistics(null);

        assertThat(result.getStatisticsType()).isEqualTo("profile");
        assertThat(result.getMetrics()).extracting("code").contains("student_count", "profiled_student_count", "profile_completion_rate");
    }

    @Test
    void createExportDefaultsToDesensitizedQueuedRecord() {
        ExportRecordMapper exportRecordMapper = mock(ExportRecordMapper.class);
        StatisticsService service = service(mock(StudentMapper.class), mock(StudentProfileMapper.class), exportRecordMapper);
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(
                new CurrentUser(7L, "teacher01", "教师一", List.of("major_leader"), List.of("statistics.export_major")),
                null
        ));

        ExportRequest request = new ExportRequest();
        request.setExportType("student_profile_summary");
        request.setExportScope("major");
        request.setMajorId(3L);

        ExportRecordVO result = service.createExport(request);

        ArgumentCaptor<ExportRecord> captor = ArgumentCaptor.forClass(ExportRecord.class);
        verify(exportRecordMapper).insert(captor.capture());
        assertThat(captor.getValue().getDesensitized()).isTrue();
        assertThat(captor.getValue().getExportStatus()).isEqualTo("queued");
        assertThat(result.getExportType()).isEqualTo("student_profile_summary");
        SecurityContextHolder.clearContext();
    }

    private StatisticsService service(StudentMapper studentMapper, StudentProfileMapper profileMapper, ExportRecordMapper exportRecordMapper) {
        return new StatisticsService(
                studentMapper,
                profileMapper,
                mock(StudentCapabilityScoreMapper.class),
                mock(FusionRelationMapper.class),
                mock(LearningRecordMapper.class),
                mock(QuizAttemptMapper.class),
                mock(WrongQuestionMapper.class),
                mock(LearningEvaluationMapper.class),
                exportRecordMapper,
                mock(AuditService.class)
        );
    }
}
