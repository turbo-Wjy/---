package com.example.ailearning.module.competition.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.ailearning.common.constant.DeleteConstants;
import com.example.ailearning.common.exception.BusinessException;
import com.example.ailearning.common.exception.ErrorCode;
import com.example.ailearning.common.pagination.PageQuery;
import com.example.ailearning.common.pagination.PageResult;
import com.example.ailearning.common.security.CurrentUserHolder;
import com.example.ailearning.module.audit.service.AuditService;
import com.example.ailearning.module.competition.dto.CompetitionRequest;
import com.example.ailearning.module.competition.entity.Competition;
import com.example.ailearning.module.competition.mapper.CompetitionMapper;
import com.example.ailearning.module.competition.vo.CompetitionVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CompetitionService {
    private final CompetitionMapper competitionMapper;
    private final AuditService auditService;

    public CompetitionService(CompetitionMapper competitionMapper, AuditService auditService) {
        this.competitionMapper = competitionMapper;
        this.auditService = auditService;
    }

    public PageResult<CompetitionVO> page(PageQuery query, String level) {
        Page<Competition> page = new Page<>(query.getPage(), query.getPageSize());
        LambdaQueryWrapper<Competition> wrapper = new LambdaQueryWrapper<Competition>()
                .isNull(Competition::getDeletedAt)
                .orderByDesc(Competition::getCreatedAt);
        if (query.getKeyword() != null && !query.getKeyword().isBlank()) {
            wrapper.like(Competition::getTitle, query.getKeyword());
        }
        if (query.getStatus() != null && !query.getStatus().isBlank()) {
            wrapper.eq(Competition::getStatus, query.getStatus());
        }
        if (level != null && !level.isBlank()) {
            wrapper.eq(Competition::getLevel, level);
        }
        Page<Competition> result = competitionMapper.selectPage(page, wrapper);
        return new PageResult<>(result.getRecords().stream().map(this::toVO).toList(), result.getCurrent(), result.getSize(), result.getTotal());
    }

    public CompetitionVO get(Long id) {
        return toVO(getEntity(id));
    }

    @Transactional(rollbackFor = Exception.class)
    public CompetitionVO create(CompetitionRequest request) {
        Competition competition = new Competition();
        fill(competition, request);
        competition.setPublishedBy(CurrentUserHolder.getRequired().getUserId());
        competition.setCreatedBy(CurrentUserHolder.getRequired().getUserId());
        competitionMapper.insert(competition);
        auditService.operation("competition_growth", "create_competition", "competition", competition.getId(), "success", "发布竞赛信息");
        return toVO(competition);
    }

    @Transactional(rollbackFor = Exception.class)
    public CompetitionVO update(Long id, CompetitionRequest request) {
        Competition competition = getEntity(id);
        fill(competition, request);
        competitionMapper.updateById(competition);
        auditService.operation("competition_growth", "update_competition", "competition", competition.getId(), "success", "更新竞赛信息");
        return toVO(competition);
    }

    @Transactional(rollbackFor = Exception.class)
    public void softDelete(Long id) {
        Competition competition = getEntity(id);
        competition.setDeletedAt(DeleteConstants.now());
        competition.setStatus("deleted");
        competitionMapper.updateById(competition);
        auditService.operation("competition_growth", "delete_competition", "competition", competition.getId(), "success", "删除竞赛信息");
    }

    public Competition getEntity(Long id) {
        Competition competition = competitionMapper.selectById(id);
        if (competition == null || competition.getDeletedAt() != null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "竞赛不存在");
        }
        return competition;
    }

    private void fill(Competition competition, CompetitionRequest request) {
        competition.setTitle(request.getTitle());
        competition.setLevel(request.getLevel());
        competition.setStartTime(request.getStartTime());
        competition.setEndTime(request.getEndTime());
        competition.setLocation(request.getLocation());
        competition.setRequirements(request.getRequirements());
        competition.setOfficialUrl(request.getOfficialUrl());
        competition.setStatus(request.getStatus() == null || request.getStatus().isBlank() ? "published" : request.getStatus());
    }

    public CompetitionVO toVO(Competition competition) {
        CompetitionVO vo = new CompetitionVO();
        vo.setId(competition.getId());
        vo.setTitle(competition.getTitle());
        vo.setLevel(competition.getLevel());
        vo.setStartTime(competition.getStartTime());
        vo.setEndTime(competition.getEndTime());
        vo.setLocation(competition.getLocation());
        vo.setRequirements(competition.getRequirements());
        vo.setOfficialUrl(competition.getOfficialUrl());
        vo.setPublishedBy(competition.getPublishedBy());
        vo.setStatus(competition.getStatus());
        vo.setCreatedAt(competition.getCreatedAt());
        return vo;
    }
}
