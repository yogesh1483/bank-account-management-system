package com.bank.management.app.security;

import lombok.NoArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

@NoArgsConstructor
public class SecurityUtils {
    public static String getCurrentUsername() {

        Authentication auth =
                SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated())
            return null;

        return auth.getName();
    }


    public static String getCurrentUserRole() {

        Authentication auth =
                SecurityContextHolder.getContext().getAuthentication();

        return auth.getAuthorities()
                .stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElse(null);
    }


    public static boolean isAdmin() {

        return "ROLE_ADMIN".equals(getCurrentUserRole());
    }


    public static boolean isCustomer() {

        return "ROLE_CUSTOMER".equals(getCurrentUserRole());
    }
}
