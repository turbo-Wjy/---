package com.example.ailearning.module.base.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.ailearning.common.constant.DeleteConstants;
import com.example.ailearning.common.exception.BusinessException;
import com.example.ailearning.common.exception.ErrorCode;
import com.example.ailearning.common.pagination.PageQuery;
import com.example.ailearning.common.pagination.PageResult;
import com.example.ailearning.module.base.dto.SchoolClassRequest;
import com.example.ailearning.module.base.entity.SchoolClass;
import com.example.ailearning.module.base.mapper.SchoolClassMapper;
import com.example.ailearning.module.base.vo.SchoolClassVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SchoolClassService {
    private final SchoolClassMapper schoolClassMapper;

    public SchoolClassService(SchoolClassMapper schoolClassMapper) {
        this.schoolClassMapper = schoolClassMapper;
    }

    public PageResult<SchoolClassVO> page(PageQuery query) {
        Page<SchoolClass> page = new Page<>(query.getPage(), query.getPageSize());
        LambdaQueryWrapper<SchoolClass> wrapper = new LambdaQueryWrapper<SchoolClass>()
                .isNull(SchoolClass::getDeletedAt)
                .orderByDesc(SchoolClass::getGrade)
                .orderByAsc(SchoolClass::getName);
        if (query.getKeyword() != null && !query.getKeyword().isBlank()) {
            wrapper.like(SchoolClass::getName, query.getKeyword());
        }
        if (query.getStatus() != null && !query.getStatus().isBlank()) {
            wrapper.eq(SchoolClass::getStatus, query.getStatus());
        }
        Page<SchoolClass> result = schoolClassMapper.selectPage(page, wrapper);
        List<SchoolClassVO> items = result.getRecords().stream().map(this::toVO).toList();
        return new PageResult<>(items, result.getCurrent(), result.getSize(), result.getTotal());
    }

    public SchoolClassVO get(Long id) {
        return toVO(getEntity(id));
    }

    @Transactional(rollbackFor = Exception.class)
    public SchoolClassVO create(SchoolClassRequest request) {
        SchoolClass schoolClass = new SchoolClass();
        fill(schoolClass, request);
        schoolClassMapper.insert(schoolClass);
        return toVO(schoolClass);
    }

    @Transactional(rollbackFor = Exception.class)
    public SchoolClassVO update(Long id, SchoolClassRequest request) {
        SchoolClass schoolClass = getEntity(id);
        fill(schoolClass, request);
        schoolClassMapper.updateById(schoolClass);
        return toVO(schoolClass);
    }

    @Transactional(rollbackFor = Exception.class)
    public void softDelete(Long id) {
        SchoolClass schoolClass = getEntity(id);
        schoolClass.setDeletedAt(DeleteConstants.now());
        schoolClass.setStatus("deleted");
        schoolClassMapper.updateById(schoolClass);
    }

    private SchoolClass getEntity(Long id) {
        SchoolClass schoolClass = schoolClassMapper.selectById(id);
        if (schoolClass == null || schoolClass.getDeletedAt() != null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "班级不存在");
        }
        return schoolClass;
    }

    private void fill(SchoolClass schoolClass, SchoolClassRequest request) {
        schoolClass.setMajorId(request.getMajorId());
        schoolClass.setGrade(request.getGrade());
        schoolClass.setName(request.getName());
        schoolClass.setStatus(request.getStatus());
    }

    private SchoolClassVO toVO(SchoolClass schoolClass) {
        SchoolClassVO vo = new SchoolClassVO();
        vo.setId(schoolClass.getId());
        vo.setMajorId(schoolClass.getMajorId());
        vo.setGrade(schoolClass.getGrade());
        vo.setName(schoolClass.getName());
        vo.setStatus(schoolClass.getStatus());
        return vo;
    }
}
