package com.garage.utils;

import com.garage.security.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class SecurityUtils {

    private SecurityUtils() {
    }

    public static Authentication getAuthentication() {

        return SecurityContextHolder
                .getContext()
                .getAuthentication();
    }

    public static boolean isAuthenticated() {

        Authentication authentication =
                getAuthentication();

        return authentication != null
                && authentication.isAuthenticated()
                && !"anonymousUser".equals(
                authentication.getPrincipal()
        );
    }

    public static String getCurrentUsername() {

        if (!isAuthenticated()) {
            return null;
        }

        return getAuthentication().getName();
    }

    public static CustomUserDetails getCurrentUser() {

        if (!isAuthenticated()) {
            return null;
        }

        Object principal =
                getAuthentication().getPrincipal();

        if (principal instanceof CustomUserDetails userDetails) {
            return userDetails;
        }

        return null;
    }

    public static boolean hasRole(String role) {

        if (!isAuthenticated()) {
            return false;
        }

        return getAuthentication()
                .getAuthorities()
                .stream()
                .anyMatch(
                        authority ->
                                authority.getAuthority()
                                        .equals("ROLE_" + role)
                );
    }
}