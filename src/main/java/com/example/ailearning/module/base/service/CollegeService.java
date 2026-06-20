package com.example.ailearning.module.base.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.ailearning.common.constant.DeleteConstants;
import com.example.ailearning.common.exception.BusinessException;
import com.example.ailearning.common.exception.ErrorCode;
import com.example.ailearning.common.pagination.PageQuery;
import com.example.ailearning.common.pagination.PageResult;
import com.example.ailearning.module.base.dto.CollegeRequest;
import com.example.ailearning.module.base.entity.College;
import com.example.ailearning.module.base.mapper.CollegeMapper;
import com.example.ailearning.module.base.vo.CollegeVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CollegeService {
    private final CollegeMapper collegeMapper;

    public CollegeService(CollegeMapper collegeMapper) {
        this.collegeMapper = collegeMapper;
    }

    public PageResult<CollegeVO> page(PageQuery query) {
        Page<College> page = new Page<>(query.getPage(), query.getPageSize());
        LambdaQueryWrapper<College> wrapper = new LambdaQueryWrapper<College>()
                .isNull(College::getDeletedAt)
                .orderByAsc(College::getCode);
        if (query.getKeyword() != null && !query.getKeyword().isBlank()) {
            wrapper.and(w -> w.like(College::getCode, query.getKeyword()).or().like(College::getName, query.getKeyword()));
        }
        if (query.getStatus() != null && !query.getStatus().isBlank()) {
            wrapper.eq(College::getStatus, query.getStatus());
        }
        Page<College> result = collegeMapper.selectPage(page, wrapper);
        List<CollegeVO> items = result.getRecords().stream().map(this::toVO).toList();
        return new PageResult<>(items, result.getCurrent(), result.getSize(), result.getTotal());
    }

    public CollegeVO get(Long id) {
        return toVO(getEntity(id));
    }

    @Transactional(rollbackFor = Exception.class)
    public CollegeVO create(CollegeRequest request) {
        College college = new College();
        fill(college, request);
        collegeMapper.insert(college);
        return toVO(college);
    }

    @Transactional(rollbackFor = Exception.class)
    public CollegeVO update(Long id, CollegeRequest request) {
        College college = getEntity(id);
        fill(college, request);
        collegeMapper.updateById(college);
        return toVO(college);
    }

    @Transactional(rollbackFor = Exception.class)
    public void softDelete(Long id) {
        College college = getEntity(id);
        college.setDeletedAt(DeleteConstants.now());
        college.setStatus("deleted");
        collegeMapper.updateById(college);
    }

    private College getEntity(Long id) {
        College college = collegeMapper.selectById(id);
        if (college == null || college.getDeletedAt() != null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "学院不存在");
        }
        return college;
    }

    private void fill(College college, CollegeRequest request) {
        college.setCode(request.getCode());
        college.setName(request.getName());
        college.setStatus(request.getStatus());
    }

    private CollegeVO toVO(College college) {
        CollegeVO vo = new CollegeVO();
        vo.setId(college.getId());
        vo.setCode(college.getCode());
        vo.setName(college.getName());
        vo.setStatus(college.getStatus());
        return vo;
    }
}
