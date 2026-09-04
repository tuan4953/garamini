package com.garage.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException, ServletException {

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        boolean isAdmin = authorities.stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ADMIN"));

        boolean isTechnician = authorities.stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_TECHNICIAN") || a.getAuthority().equals("TECHNICIAN"));

        if (isAdmin) {
            response.sendRedirect("/admin/invoices"); // Hoặc trang dashboard Admin của bạn
        } else if (isTechnician) {
            response.sendRedirect("/technician/home");
        } else {
            response.sendRedirect("/");
        }
    }
}