package com.example.ailearning.module.teacher.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.ailearning.common.constant.DeleteConstants;
import com.example.ailearning.common.exception.BusinessException;
import com.example.ailearning.common.exception.ErrorCode;
import com.example.ailearning.common.pagination.PageQuery;
import com.example.ailearning.common.pagination.PageResult;
import com.example.ailearning.module.teacher.dto.TeacherStudentGroupRequest;
import com.example.ailearning.module.teacher.entity.TeacherStudentGroup;
import com.example.ailearning.module.teacher.mapper.TeacherStudentGroupMapper;
import com.example.ailearning.module.teacher.vo.TeacherStudentGroupVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TeacherStudentGroupService {
    private final TeacherStudentGroupMapper mapper;

    public TeacherStudentGroupService(TeacherStudentGroupMapper mapper) {
        this.mapper = mapper;
    }

    public PageResult<TeacherStudentGroupVO> page(PageQuery query) {
        Page<TeacherStudentGroup> page = new Page<>(query.getPage(), query.getPageSize());
        LambdaQueryWrapper<TeacherStudentGroup> wrapper = new LambdaQueryWrapper<TeacherStudentGroup>()
                .isNull(TeacherStudentGroup::getDeletedAt)
                .orderByAsc(TeacherStudentGroup::getGroupName);
        if (query.getKeyword() != null && !query.getKeyword().isBlank()) {
            wrapper.like(TeacherStudentGroup::getGroupName, query.getKeyword());
        }
        if (query.getStatus() != null && !query.getStatus().isBlank()) {
            wrapper.eq(TeacherStudentGroup::getStatus, query.getStatus());
        }
        Page<TeacherStudentGroup> result = mapper.selectPage(page, wrapper);
        List<TeacherStudentGroupVO> items = result.getRecords().stream().map(this::toVO).toList();
        return new PageResult<>(items, result.getCurrent(), result.getSize(), result.getTotal());
    }

    public TeacherStudentGroupVO get(Long id) {
        return toVO(getEntity(id));
    }

    @Transactional(rollbackFor = Exception.class)
    public TeacherStudentGroupVO create(TeacherStudentGroupRequest request) {
        TeacherStudentGroup group = new TeacherStudentGroup();
        fill(group, request);
        mapper.insert(group);
        return toVO(group);
    }

    @Transactional(rollbackFor = Exception.class)
    public TeacherStudentGroupVO update(Long id, TeacherStudentGroupRequest request) {
        TeacherStudentGroup group = getEntity(id);
        fill(group, request);
        mapper.updateById(group);
        return toVO(group);
    }

    @Transactional(rollbackFor = Exception.class)
    public void softDelete(Long id) {
        TeacherStudentGroup group = getEntity(id);
        group.setDeletedAt(DeleteConstants.now());
        group.setStatus("deleted");
        mapper.updateById(group);
    }

    private TeacherStudentGroup getEntity(Long id) {
        TeacherStudentGroup group = mapper.selectById(id);
        if (group == null || group.getDeletedAt() != null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "教师学生分组不存在");
        }
        return group;
    }

    private void fill(TeacherStudentGroup group, TeacherStudentGroupRequest request) {
        group.setTeacherId(request.getTeacherId());
        group.setStudentId(request.getStudentId());
        group.setGroupName(request.getGroupName());
        group.setBindType(request.getBindType());
        group.setStatus(request.getStatus());
    }

    private TeacherStudentGroupVO toVO(TeacherStudentGroup group) {
        TeacherStudentGroupVO vo = new TeacherStudentGroupVO();
        vo.setId(group.getId());
        vo.setTeacherId(group.getTeacherId());
        vo.setStudentId(group.getStudentId());
        vo.setGroupName(group.getGroupName());
        vo.setBindType(group.getBindType());
        vo.setStatus(group.getStatus());
        return vo;
    }
}
