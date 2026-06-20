package com.example.ailearning.module.permission.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.ailearning.common.constant.DeleteConstants;
import com.example.ailearning.common.exception.BusinessException;
import com.example.ailearning.common.exception.ErrorCode;
import com.example.ailearning.common.pagination.PageQuery;
import com.example.ailearning.common.pagination.PageResult;
import com.example.ailearning.module.permission.dto.PermissionRequest;
import com.example.ailearning.module.permission.entity.Permission;
import com.example.ailearning.module.permission.mapper.PermissionMapper;
import com.example.ailearning.module.permission.vo.PermissionVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PermissionService {
    private final PermissionMapper permissionMapper;

    public PermissionService(PermissionMapper permissionMapper) {
        this.permissionMapper = permissionMapper;
    }

    public PageResult<PermissionVO> page(PageQuery query) {
        Page<Permission> page = new Page<>(query.getPage(), query.getPageSize());
        LambdaQueryWrapper<Permission> wrapper = new LambdaQueryWrapper<Permission>()
                .isNull(Permission::getDeletedAt)
                .orderByAsc(Permission::getModule)
                .orderByAsc(Permission::getCode);
        if (query.getKeyword() != null && !query.getKeyword().isBlank()) {
            wrapper.and(w -> w.like(Permission::getCode, query.getKeyword()).or().like(Permission::getName, query.getKeyword()));
        }
        if (query.getStatus() != null && !query.getStatus().isBlank()) {
            wrapper.eq(Permission::getStatus, query.getStatus());
        }
        Page<Permission> result = permissionMapper.selectPage(page, wrapper);
        List<PermissionVO> items = result.getRecords().stream().map(this::toVO).toList();
        return new PageResult<>(items, result.getCurrent(), result.getSize(), result.getTotal());
    }

    public PermissionVO get(Long id) {
        return toVO(getEntity(id));
    }

    @Transactional(rollbackFor = Exception.class)
    public PermissionVO create(PermissionRequest request) {
        Permission permission = new Permission();
        fill(permission, request);
        permissionMapper.insert(permission);
        return toVO(permission);
    }

    @Transactional(rollbackFor = Exception.class)
    public PermissionVO update(Long id, PermissionRequest request) {
        Permission permission = getEntity(id);
        fill(permission, request);
        permissionMapper.updateById(permission);
        return toVO(permission);
    }

    @Transactional(rollbackFor = Exception.class)
    public void softDelete(Long id) {
        Permission permission = getEntity(id);
        permission.setDeletedAt(DeleteConstants.now());
        permission.setStatus("deleted");
        permissionMapper.updateById(permission);
    }

    private Permission getEntity(Long id) {
        Permission permission = permissionMapper.selectById(id);
        if (permission == null || permission.getDeletedAt() != null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "权限不存在");
        }
        return permission;
    }

    private void fill(Permission permission, PermissionRequest request) {
        permission.setCode(request.getCode());
        permission.setName(request.getName());
        permission.setModule(request.getModule());
        permission.setPermissionType(request.getPermissionType());
        permission.setDescription(request.getDescription());
        permission.setStatus(request.getStatus());
    }

    private PermissionVO toVO(Permission permission) {
        PermissionVO vo = new PermissionVO();
        vo.setId(permission.getId());
        vo.setCode(permission.getCode());
        vo.setName(permission.getName());
        vo.setModule(permission.getModule());
        vo.setPermissionType(permission.getPermissionType());
        vo.setDescription(permission.getDescription());
        vo.setStatus(permission.getStatus());
        return vo;
    }
}
