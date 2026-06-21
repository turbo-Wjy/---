package com.example.ailearning.module.user.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.ailearning.common.constant.StatusConstants;
import com.example.ailearning.common.exception.BusinessException;
import com.example.ailearning.common.exception.ErrorCode;
import com.example.ailearning.common.pagination.PageResult;
import com.example.ailearning.common.security.CurrentUser;
import com.example.ailearning.module.role.entity.Role;
import com.example.ailearning.module.role.mapper.RoleMapper;
import com.example.ailearning.module.user.dto.UserCreateRequest;
import com.example.ailearning.module.user.dto.UserPageQuery;
import com.example.ailearning.module.user.entity.User;
import com.example.ailearning.module.user.entity.UserRole;
import com.example.ailearning.module.user.mapper.UserMapper;
import com.example.ailearning.module.user.mapper.UserRoleMapper;
import com.example.ailearning.module.user.vo.UserVO;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserService {
    private final UserMapper userMapper;
    private final UserRoleMapper userRoleMapper;
    private final RoleMapper roleMapper;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserMapper userMapper, UserRoleMapper userRoleMapper, RoleMapper roleMapper, PasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
        this.userRoleMapper = userRoleMapper;
        this.roleMapper = roleMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public User findActiveByUsername(String username) {
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, username)
                .isNull(User::getDeletedAt)
                .last("LIMIT 1"));
        if (user == null || !StatusConstants.ACTIVE.equals(user.getAccountStatus())) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "账号不存在或已停用");
        }
        return user;
    }

    public User findActiveById(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null || user.getDeletedAt() != null || !StatusConstants.ACTIVE.equals(user.getAccountStatus())) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "账号不存在或已停用");
        }
        return user;
    }

    public CurrentUser loadCurrentUser(Long userId) {
        User user = findActiveById(userId);
        List<String> roles = userMapper.selectRoleCodesByUserId(userId);
        List<String> permissions = userMapper.selectPermissionCodesByUserId(userId);
        return new CurrentUser(user.getId(), user.getUsername(), user.getRealName(), roles, permissions);
    }

    public UserVO getUserVO(Long userId) {
        User user = findActiveById(userId);
        return toVO(user, true);
    }

    public List<String> getMenuCodes(Long userId) {
        return userMapper.selectMenuCodesByUserId(userId);
    }

    public List<String> getPermissionCodes(Long userId) {
        return userMapper.selectPermissionCodesByUserId(userId);
    }

    public PageResult<UserVO> pageUsers(UserPageQuery query) {
        Page<User> page = new Page<>(query.getPage(), query.getPageSize());
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<User>()
                .isNull(User::getDeletedAt)
                .orderByDesc(User::getCreatedAt);
        if (query.getKeyword() != null && !query.getKeyword().isBlank()) {
            wrapper.and(w -> w.like(User::getUsername, query.getKeyword()).or().like(User::getRealName, query.getKeyword()));
        }
        if (query.getStatus() != null && !query.getStatus().isBlank()) {
            wrapper.eq(User::getAccountStatus, query.getStatus());
        }
        Page<User> result = userMapper.selectPage(page, wrapper);
        List<UserVO> items = result.getRecords().stream().map(user -> toVO(user, false)).toList();
        return new PageResult<>(items, result.getCurrent(), result.getSize(), result.getTotal());
    }

    @Transactional(rollbackFor = Exception.class)
    public UserVO createUser(UserCreateRequest request) {
        if (userMapper.exists(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, request.getUsername())
                .isNull(User::getDeletedAt))) {
            throw new BusinessException(ErrorCode.CONFLICT, "登录账号已存在");
        }
        if (request.getRoleCodes() == null || request.getRoleCodes().isEmpty()) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "至少需要绑定一个角色");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setRealName(request.getRealName());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setAccountStatus(normalizeStatus(request.getAccountStatus()));
        user.setMustChangePassword(request.getMustChangePassword() == null || request.getMustChangePassword());
        userMapper.insert(user);

        bindRoles(user.getId(), request.getRoleCodes());
        return getUserVO(user.getId());
    }

    @Transactional(rollbackFor = Exception.class)
    public UserVO bootstrapAdmin(UserCreateRequest request) {
        Role adminRole = roleMapper.selectOne(new LambdaQueryWrapper<Role>()
                .eq(Role::getCode, "admin")
                .isNull(Role::getDeletedAt)
                .last("LIMIT 1"));
        if (adminRole == null || !StatusConstants.ACTIVE.equals(adminRole.getStatus())) {
            throw new BusinessException(ErrorCode.CONFLICT, "管理员角色不存在，请先执行 seed.sql");
        }

        List<UserRole> adminBindings = userRoleMapper.selectList(new LambdaQueryWrapper<UserRole>()
                .eq(UserRole::getRoleId, adminRole.getId()));
        boolean hasUsableAdmin = adminBindings.stream()
                .map(binding -> userMapper.selectById(binding.getUserId()))
                .anyMatch(this::isUsableAdminAccount);
        if (hasUsableAdmin) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "系统已有可登录管理员，初始化管理员接口已关闭");
        }

        User existing = userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, request.getUsername())
                .isNull(User::getDeletedAt)
                .last("LIMIT 1"));
        if (existing != null) {
            existing.setRealName(request.getRealName());
            existing.setPasswordHash(passwordEncoder.encode(request.getPassword()));
            existing.setAccountStatus(StatusConstants.ACTIVE);
            existing.setMustChangePassword(request.getMustChangePassword() != null && request.getMustChangePassword());
            existing.setUpdatedAt(LocalDateTime.now());
            userMapper.updateById(existing);
            bindRoles(existing.getId(), List.of("admin"));
            return getUserVO(existing.getId());
        }

        request.setRoleCodes(List.of("admin"));
        request.setAccountStatus(StatusConstants.ACTIVE);
        if (request.getMustChangePassword() == null) {
            request.setMustChangePassword(false);
        }
        return createUser(request);
    }

    @Transactional(rollbackFor = Exception.class)
    public void resetPassword(Long userId, String newPassword) {
        User user = findActiveById(userId);
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        user.setMustChangePassword(true);
        user.setUpdatedAt(LocalDateTime.now());
        userMapper.updateById(user);
    }

    private void bindRoles(Long userId, List<String> roleCodes) {
        for (String roleCode : roleCodes.stream().distinct().toList()) {
            Role role = roleMapper.selectOne(new LambdaQueryWrapper<Role>()
                    .eq(Role::getCode, roleCode)
                    .isNull(Role::getDeletedAt)
                    .last("LIMIT 1"));
            if (role == null || !StatusConstants.ACTIVE.equals(role.getStatus())) {
                throw new BusinessException(ErrorCode.PARAM_ERROR, "角色不存在或已停用：" + roleCode);
            }
            if (userRoleMapper.exists(new LambdaQueryWrapper<UserRole>()
                    .eq(UserRole::getUserId, userId)
                    .eq(UserRole::getRoleId, role.getId()))) {
                continue;
            }
            UserRole userRole = new UserRole();
            userRole.setUserId(userId);
            userRole.setRoleId(role.getId());
            userRoleMapper.insert(userRole);
        }
    }

    private String normalizeStatus(String status) {
        if (status == null || status.isBlank()) {
            return StatusConstants.ACTIVE;
        }
        if (!StatusConstants.ACTIVE.equals(status) && !"disabled".equals(status)) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "账号状态只支持 active 或 disabled");
        }
        return status;
    }

    private boolean isUsableAdminAccount(User user) {
        return user != null
                && user.getDeletedAt() == null
                && StatusConstants.ACTIVE.equals(user.getAccountStatus())
                && user.getPasswordHash() != null
                && !user.getPasswordHash().contains("demo.hash.placeholder");
    }

    @Transactional(rollbackFor = Exception.class)
    public void changePassword(Long userId, String newPassword, boolean mustChangePassword) {
        User user = findActiveById(userId);
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        user.setMustChangePassword(mustChangePassword);
        user.setUpdatedAt(LocalDateTime.now());
        userMapper.updateById(user);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateLastLoginAt(Long userId) {
        User user = new User();
        user.setId(userId);
        user.setLastLoginAt(LocalDateTime.now());
        userMapper.updateById(user);
    }

    private UserVO toVO(User user, boolean includePermissions) {
        UserVO vo = new UserVO();
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setRealName(user.getRealName());
        vo.setAccountStatus(user.getAccountStatus());
        vo.setMustChangePassword(user.getMustChangePassword());
        vo.setLastLoginAt(user.getLastLoginAt());
        vo.setRoleCodes(userMapper.selectRoleCodesByUserId(user.getId()));
        if (includePermissions) {
            vo.setPermissionCodes(userMapper.selectPermissionCodesByUserId(user.getId()));
        }
        return vo;
    }
}
