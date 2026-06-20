package com.example.ailearning.module.audit.service;

import com.example.ailearning.common.security.CurrentUser;
import com.example.ailearning.module.audit.dto.ReviewRequest;
import com.example.ailearning.module.audit.mapper.ReviewRecordMapper;
import com.example.ailearning.module.competition.entity.CompetitionResult;
import com.example.ailearning.module.competition.mapper.CompetitionResultMapper;
import com.example.ailearning.module.project.mapper.ProjectDeliverableMapper;
import com.example.ailearning.module.certificate.mapper.CertificateResultMapper;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AchievementReviewServiceTest {

    @Test
    void reviewCompetitionResultUpdatesStatusAndWritesReviewRecord() {
        CompetitionResultMapper competitionResultMapper = mock(CompetitionResultMapper.class);
        CompetitionResult result = new CompetitionResult();
        result.setId(3L);
        result.setReviewStatus("pending");
        when(competitionResultMapper.selectById(3L)).thenReturn(result);
        AchievementReviewService service = new AchievementReviewService(
                competitionResultMapper,
                mock(CertificateResultMapper.class),
                mock(ProjectDeliverableMapper.class),
                new AuditService(mock(ReviewRecordMapper.class), null)
        );
        setUser();

        ReviewRequest request = new ReviewRequest();
        request.setResult("approved");
        request.setComment("材料真实有效");
        service.reviewCompetitionResult(3L, request);

        assertThat(result.getReviewStatus()).isEqualTo("approved");
        verify(competitionResultMapper).updateById(result);
        SecurityContextHolder.clearContext();
    }

    private void setUser() {
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(
                new CurrentUser(7L, "teacher01", "教师一", List.of("competition_admin"), List.of("competition.publish")),
                null
        ));
    }
}
