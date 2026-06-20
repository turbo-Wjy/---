package com.example.ailearning.module.base.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.ailearning.common.constant.DeleteConstants;
import com.example.ailearning.common.exception.BusinessException;
import com.example.ailearning.common.exception.ErrorCode;
import com.example.ailearning.common.pagination.PageQuery;
import com.example.ailearning.common.pagination.PageResult;
import com.example.ailearning.module.base.dto.MajorRequest;
import com.example.ailearning.module.base.entity.Major;
import com.example.ailearning.module.base.mapper.MajorMapper;
import com.example.ailearning.module.base.vo.MajorVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MajorService {
    private final MajorMapper majorMapper;

    public MajorService(MajorMapper majorMapper) {
        this.majorMapper = majorMapper;
    }

    public PageResult<MajorVO> page(PageQuery query) {
        Page<Major> page = new Page<>(query.getPage(), query.getPageSize());
        LambdaQueryWrapper<Major> wrapper = new LambdaQueryWrapper<Major>()
                .isNull(Major::getDeletedAt)
                .orderByAsc(Major::getCode);
        if (query.getKeyword() != null && !query.getKeyword().isBlank()) {
            wrapper.and(w -> w.like(Major::getCode, query.getKeyword()).or().like(Major::getName, query.getKeyword()));
        }
        if (query.getStatus() != null && !query.getStatus().isBlank()) {
            wrapper.eq(Major::getStatus, query.getStatus());
        }
        Page<Major> result = majorMapper.selectPage(page, wrapper);
        List<MajorVO> items = result.getRecords().stream().map(this::toVO).toList();
        return new PageResult<>(items, result.getCurrent(), result.getSize(), result.getTotal());
    }

    public MajorVO get(Long id) {
        return toVO(getEntity(id));
    }

    @Transactional(rollbackFor = Exception.class)
    public MajorVO create(MajorRequest request) {
        Major major = new Major();
        fill(major, request);
        majorMapper.insert(major);
        return toVO(major);
    }

    @Transactional(rollbackFor = Exception.class)
    public MajorVO update(Long id, MajorRequest request) {
        Major major = getEntity(id);
        fill(major, request);
        majorMapper.updateById(major);
        return toVO(major);
    }

    @Transactional(rollbackFor = Exception.class)
    public void softDelete(Long id) {
        Major major = getEntity(id);
        major.setDeletedAt(DeleteConstants.now());
        major.setStatus("deleted");
        majorMapper.updateById(major);
    }

    private Major getEntity(Long id) {
        Major major = majorMapper.selectById(id);
        if (major == null || major.getDeletedAt() != null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "专业不存在");
        }
        return major;
    }

    private void fill(Major major, MajorRequest request) {
        major.setCollegeId(request.getCollegeId());
        major.setCode(request.getCode());
        major.setName(request.getName());
        major.setStatus(request.getStatus());
    }

    private MajorVO toVO(Major major) {
        MajorVO vo = new MajorVO();
        vo.setId(major.getId());
        vo.setCollegeId(major.getCollegeId());
        vo.setCode(major.getCode());
        vo.setName(major.getName());
        vo.setStatus(major.getStatus());
        return vo;
    }
}
