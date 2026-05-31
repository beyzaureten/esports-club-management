package com.esportsclub.esports_management.config;

import com.esportsclub.esports_management.model.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {

        HttpSession session = request.getSession();
        User loggedUser = (User) session.getAttribute("loggedUser");
        String path = request.getRequestURI();

        // Herkese açık sayfalar
        if (path.equals("/login") ||
                path.equals("/register") ||
                path.equals("/forgot-password") ||
                path.equals("/access-denied") ||
                path.equals("/change-password") ||
                path.startsWith("/style") ||
                path.startsWith("/css") ||
                path.startsWith("/js") ||
                path.startsWith("/images")) {
            return true;
        }

        // Giriş yapılmamışsa login'e yönlendir
        if (loggedUser == null) {
            response.sendRedirect("/login");
            return false;
        }

        String role = loggedUser.getRole();

        // ADMIN her şeye erişebilir
        if ("ADMIN".equals(role)) {
            return true;
        }

        // COACH erişemeyeceği sayfalar
        if ("COACH".equals(role)) {
            if (path.startsWith("/users")) {
                response.sendRedirect("/access-denied");
                return false;
            }
            return true;
        }

        // MEMBER erişebileceği sayfalar
        if ("MEMBER".equals(role)) {
            if (path.startsWith("/dashboard") ||
                    path.startsWith("/reports") ||
                    path.startsWith("/profile") ||
                    path.startsWith("/team-requests") ||
                    path.startsWith("/logout")) {
                return true;
            }
            response.sendRedirect("/access-denied");
            return false;
        }

        return true;
    }
}