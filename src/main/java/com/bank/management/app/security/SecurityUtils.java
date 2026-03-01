package com.bank.management.app.security;

import lombok.NoArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

@NoArgsConstructor
public class SecurityUtils {
    /**
     * Returns the username of the currently logged-in user
     *
     * @return username
     * @throws AccessDeniedException if user is not authenticated
     */

    public static String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("User is not authenticated");
        }

        return authentication.getName();
    }

    // 🎭 ROLE
    public static String getCurrentUserRole() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) return null;

        return auth.getAuthorities().stream().map(GrantedAuthority::getAuthority).findFirst().orElse(null);
    }

}
