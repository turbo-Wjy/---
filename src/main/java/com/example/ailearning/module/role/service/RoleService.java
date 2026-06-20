package com.example.ailearning.module.role.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.ailearning.common.constant.DeleteConstants;
import com.example.ailearning.common.exception.BusinessException;
import com.example.ailearning.common.exception.ErrorCode;
import com.example.ailearning.common.pagination.PageQuery;
import com.example.ailearning.common.pagination.PageResult;
import com.example.ailearning.module.role.dto.RolePermissionRequest;
import com.example.ailearning.module.role.dto.RoleRequest;
import com.example.ailearning.module.role.entity.Role;
import com.example.ailearning.module.role.entity.RolePermission;
import com.example.ailearning.module.role.mapper.RoleMapper;
import com.example.ailearning.module.role.mapper.RolePermissionMapper;
import com.example.ailearning.module.role.vo.RoleVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RoleService {
    private final RoleMapper roleMapper;
    private final RolePermissionMapper rolePermissionMapper;

    public RoleService(RoleMapper roleMapper, RolePermissionMapper rolePermissionMapper) {
        this.roleMapper = roleMapper;
        this.rolePermissionMapper = rolePermissionMapper;
    }

    public PageResult<RoleVO> page(PageQuery query) {
        Page<Role> page = new Page<>(query.getPage(), query.getPageSize());
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<Role>()
                .isNull(Role::getDeletedAt)
                .orderByDesc(Role::getCore)
                .orderByAsc(Role::getCode);
        if (query.getKeyword() != null && !query.getKeyword().isBlank()) {
            wrapper.and(w -> w.like(Role::getCode, query.getKeyword()).or().like(Role::getName, query.getKeyword()));
        }
        if (query.getStatus() != null && !query.getStatus().isBlank()) {
            wrapper.eq(Role::getStatus, query.getStatus());
        }
        Page<Role> result = roleMapper.selectPage(page, wrapper);
        List<RoleVO> items = result.getRecords().stream().map(this::toVO).toList();
        return new PageResult<>(items, result.getCurrent(), result.getSize(), result.getTotal());
    }

    public RoleVO get(Long id) {
        return toVO(getEntity(id));
    }

    @Transactional(rollbackFor = Exception.class)
    public RoleVO create(RoleRequest request) {
        Role role = new Role();
        fill(role, request);
        roleMapper.insert(role);
        return toVO(role);
    }

    @Transactional(rollbackFor = Exception.class)
    public RoleVO update(Long id, RoleRequest request) {
        Role role = getEntity(id);
        fill(role, request);
        roleMapper.updateById(role);
        return toVO(role);
    }

    @Transactional(rollbackFor = Exception.class)
    public void softDelete(Long id) {
        Role role = getEntity(id);
        if (Boolean.TRUE.equals(role.getCore())) {
            throw new BusinessException(ErrorCode.CONFLICT, "核心角色不允许删除");
        }
        role.setDeletedAt(DeleteConstants.now());
        role.setStatus("deleted");
        roleMapper.updateById(role);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updatePermissions(Long roleId, RolePermissionRequest request) {
        getEntity(roleId);
        rolePermissionMapper.delete(new LambdaQueryWrapper<RolePermission>().eq(RolePermission::getRoleId, roleId));
        for (Long permissionId : request.getPermissionIds()) {
            RolePermission rolePermission = new RolePermission();
            rolePermission.setRoleId(roleId);
            rolePermission.setPermissionId(permissionId);
            rolePermissionMapper.insert(rolePermission);
        }
    }

    private Role getEntity(Long id) {
        Role role = roleMapper.selectById(id);
        if (role == null || role.getDeletedAt() != null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "角色不存在");
        }
        return role;
    }

    private void fill(Role role, RoleRequest request) {
        role.setCode(request.getCode());
        role.setName(request.getName());
        role.setDataScope(request.getDataScope());
        role.setCore(request.getCore());
        role.setDescription(request.getDescription());
        role.setStatus(request.getStatus());
    }

    private RoleVO toVO(Role role) {
        RoleVO vo = new RoleVO();
        vo.setId(role.getId());
        vo.setCode(role.getCode());
        vo.setName(role.getName());
        vo.setDataScope(role.getDataScope());
        vo.setCore(role.getCore());
        vo.setDescription(role.getDescription());
        vo.setStatus(role.getStatus());
        return vo;
    }
}
