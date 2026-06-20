package com.example.ailearning.module.audit.service;

import com.example.ailearning.common.security.CurrentUser;
import com.example.ailearning.common.security.CurrentUserHolder;
import com.example.ailearning.module.audit.entity.OperationLog;
import com.example.ailearning.module.audit.entity.ReviewRecord;
import com.example.ailearning.module.audit.mapper.OperationLogMapper;
import com.example.ailearning.module.audit.mapper.ReviewRecordMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuditService {
    private final ReviewRecordMapper reviewRecordMapper;
    private final OperationLogMapper operationLogMapper;

    public AuditService(ReviewRecordMapper reviewRecordMapper, OperationLogMapper operationLogMapper) {
        this.reviewRecordMapper = reviewRecordMapper;
        this.operationLogMapper = operationLogMapper;
    }

    public void review(String targetType, Long targetId, String reviewNode, String result, String comment) {
        CurrentUser currentUser = CurrentUserHolder.getRequired();
        ReviewRecord record = new ReviewRecord();
        record.setTargetType(targetType);
        record.setTargetId(targetId);
        record.setReviewNode(reviewNode);
        record.setReviewerUserId(currentUser.getUserId());
        record.setReviewResult(result);
        record.setReviewComment(comment);
        record.setReviewedAt(LocalDateTime.now());
        record.setStatus("active");
        record.setCreatedBy(currentUser.getUserId());
        reviewRecordMapper.insert(record);
    }

    public void operation(String module, String action, String targetType, Long targetId, String result, String remark) {
        CurrentUser currentUser = CurrentUserHolder.getRequired();
        OperationLog log = new OperationLog();
        log.setOperatorId(currentUser.getUserId());
        log.setOperatorRole(String.join(",", currentUser.getRoleCodes()));
        log.setModule(module);
        log.setAction(action);
        log.setTargetType(targetType);
        log.setTargetId(targetId);
        log.setResult(result);
        log.setRemark(remark);
        operationLogMapper.insert(log);
    }
}
