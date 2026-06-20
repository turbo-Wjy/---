package com.example.ailearning.common.security;

import java.util.Collections;
import java.util.List;

public class CurrentUser {
    private Long userId;
    private String username;
    private String realName;
    private List<String> roleCodes = Collections.emptyList();
    private List<String> permissionCodes = Collections.emptyList();

    public CurrentUser() {
    }

    public CurrentUser(Long userId, String username, String realName, List<String> roleCodes, List<String> permissionCodes) {
        this.userId = userId;
        this.username = username;
        this.realName = realName;
        this.roleCodes = roleCodes;
        this.permissionCodes = permissionCodes;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public List<String> getRoleCodes() {
        return roleCodes;
    }

    public void setRoleCodes(List<String> roleCodes) {
        this.roleCodes = roleCodes;
    }

    public List<String> getPermissionCodes() {
        return permissionCodes;
    }

    public void setPermissionCodes(List<String> permissionCodes) {
        this.permissionCodes = permissionCodes;
    }
}
