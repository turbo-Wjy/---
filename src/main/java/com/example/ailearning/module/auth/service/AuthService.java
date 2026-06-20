package com.example.ailearning.module.auth.service;

import com.example.ailearning.common.exception.BusinessException;
import com.example.ailearning.common.exception.ErrorCode;
import com.example.ailearning.common.security.CurrentUser;
import com.example.ailearning.common.security.CurrentUserHolder;
import com.example.ailearning.common.security.JwtTokenService;
import com.example.ailearning.module.auth.dto.LoginRequest;
import com.example.ailearning.module.auth.vo.LoginResponse;
import com.example.ailearning.module.user.entity.User;
import com.example.ailearning.module.user.service.UserService;
import com.example.ailearning.module.user.vo.UserVO;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthService {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenService jwtTokenService;

    public AuthService(UserService userService, PasswordEncoder passwordEncoder, JwtTokenService jwtTokenService) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenService = jwtTokenService;
    }

    public LoginResponse login(LoginRequest request) {
        User user = userService.findActiveByUsername(request.getUsername());
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "账号或密码错误");
        }
        userService.updateLastLoginAt(user.getId());

        String token = jwtTokenService.generateToken(user.getId(), user.getUsername());
        UserVO userVO = userService.getUserVO(user.getId());
        List<String> permissions = userService.getPermissionCodes(user.getId());
        List<String> menus = userService.getMenuCodes(user.getId());

        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setMustChangePassword(user.getMustChangePassword());
        response.setUser(userVO);
        response.setPermissions(permissions);
        response.setMenus(menus);
        return response;
    }

    public void forceChangePassword(String newPassword) {
        CurrentUser currentUser = CurrentUserHolder.getRequired();
        userService.changePassword(currentUser.getUserId(), newPassword, false);
    }

    public UserVO currentUser() {
        CurrentUser currentUser = CurrentUserHolder.getRequired();
        return userService.getUserVO(currentUser.getUserId());
    }

    public List<String> currentMenus() {
        CurrentUser currentUser = CurrentUserHolder.getRequired();
        return userService.getMenuCodes(currentUser.getUserId());
    }

    public List<String> currentPermissions() {
        CurrentUser currentUser = CurrentUserHolder.getRequired();
        return userService.getPermissionCodes(currentUser.getUserId());
    }
}
