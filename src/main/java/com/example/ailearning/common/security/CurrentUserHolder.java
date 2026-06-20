package com.example.ailearning.common.security;

import com.example.ailearning.common.exception.BusinessException;
import com.example.ailearning.common.exception.ErrorCode;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class CurrentUserHolder {
    private CurrentUserHolder() {
    }

    public static CurrentUser getRequired() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof CurrentUser currentUser)) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }
        return currentUser;
    }
}
