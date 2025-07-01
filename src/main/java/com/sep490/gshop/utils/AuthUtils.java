package com.sep490.gshop.utils;

import com.sep490.gshop.config.handler.AppException;
import com.sep490.gshop.config.security.services.UserDetailsImpl;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.UUID;

public class AuthUtils {
    private AuthUtils() {
    }
    public static UserDetailsImpl getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getPrincipal() != "anonymousUser") {
            return (UserDetailsImpl) authentication.getPrincipal();
        } else {
            throw new AppException(401,"Bạn cần đăng nhập để sử dụng dịch vụ");
        }
    }

    public static UUID getCurrentUserId() {
        UserDetailsImpl user = getCurrentUser();
        return user.getId();
    }

    public static String getCurrentUsername() {
        UserDetailsImpl user = getCurrentUser();
        return user.getUsername();
    }
}
