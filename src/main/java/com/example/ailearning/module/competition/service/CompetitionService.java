package com.example.ailearning.module.competition.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.ailearning.common.security.CurrentUserHolder;
import com.example.ailearning.module.competition.dto.CompetitionRequest;
import com.example.ailearning.module.competition.dto.CompetitionResultRequest;
import com.example.ailearning.module.competition.entity.Competition;
import com.example.ailearning.module.competition.entity.CompetitionResult;
import com.example.ailearning.module.competition.mapper.CompetitionMapper;
import com.example.ailearning.module.competition.mapper.CompetitionResultMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CompetitionService {
    private final CompetitionMapper competitionMapper;
    private final CompetitionResultMapper resultMapper;

    public CompetitionService(CompetitionMapper competitionMapper, CompetitionResultMapper resultMapper) {
        this.competitionMapper = competitionMapper;
        this.resultMapper = resultMapper;
    }

    public List<Competition> list(String status) {
        LambdaQueryWrapper<Competition> wrapper = new LambdaQueryWrapper<Competition>()
                .isNull(Competition::getDeletedAt)
                .orderByDesc(Competition::getCreatedAt);
        if (status != null && !status.isBlank()) {
            wrapper.eq(Competition::getStatus, status);
        }
        return competitionMapper.selectList(wrapper);
    }

    @Transactional(rollbackFor = Exception.class)
    public Competition create(CompetitionRequest request) {
        Long userId = CurrentUserHolder.getRequired().getUserId();
        Competition competition = new Competition();
        competition.setTitle(request.getTitle());
        competition.setLevel(request.getLevel());
        competition.setStartTime(request.getStartTime());
        competition.setEndTime(request.getEndTime());
        competition.setLocation(request.getLocation());
        competition.setRequirements(request.getRequirements());
        competition.setOfficialUrl(request.getOfficialUrl());
        competition.setPublishedBy(userId);
        competition.setStatus("published");
        competition.setCreatedBy(userId);
        competitionMapper.insert(competition);
        return competition;
    }

    public List<CompetitionResult> listResults(String reviewStatus) {
        LambdaQueryWrapper<CompetitionResult> wrapper = new LambdaQueryWrapper<CompetitionResult>()
                .isNull(CompetitionResult::getDeletedAt)
                .orderByDesc(CompetitionResult::getCreatedAt);
        if (reviewStatus != null && !reviewStatus.isBlank()) {
            wrapper.eq(CompetitionResult::getReviewStatus, reviewStatus);
        }
        return resultMapper.selectList(wrapper);
    }

    @Transactional(rollbackFor = Exception.class)
    public CompetitionResult submitResult(CompetitionResultRequest request) {
        CompetitionResult result = new CompetitionResult();
        result.setCompetitionId(request.getCompetitionId());
        result.setStudentId(request.getStudentId());
        result.setCoachTeacherId(request.getCoachTeacherId());
        result.setAwardName(request.getAwardName());
        result.setProofFileUrl(request.getProofFileUrl());
        result.setReviewStatus("pending");
        result.setSubmittedAt(LocalDateTime.now());
        result.setStatus("pending");
        result.setCreatedBy(CurrentUserHolder.getRequired().getUserId());
        resultMapper.insert(result);
        return result;
    }
}
