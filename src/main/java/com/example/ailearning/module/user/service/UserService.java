package com.example.ailearning.module.user.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.ailearning.common.constant.StatusConstants;
import com.example.ailearning.common.exception.BusinessException;
import com.example.ailearning.common.exception.ErrorCode;
import com.example.ailearning.common.pagination.PageResult;
import com.example.ailearning.common.security.CurrentUser;
import com.example.ailearning.module.user.dto.UserPageQuery;
import com.example.ailearning.module.user.entity.User;
import com.example.ailearning.module.user.mapper.UserMapper;
import com.example.ailearning.module.user.vo.UserVO;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserService {
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
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
    public void resetPassword(Long userId, String newPassword) {
        User user = findActiveById(userId);
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        user.setMustChangePassword(true);
        user.setUpdatedAt(LocalDateTime.now());
        userMapper.updateById(user);
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
