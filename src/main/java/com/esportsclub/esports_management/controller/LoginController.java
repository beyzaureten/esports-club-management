package com.esportsclub.esports_management.controller;

import com.esportsclub.esports_management.model.User;
import com.esportsclub.esports_management.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;

@Controller
public class LoginController {

    private final UserService userService;

    public LoginController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        @RequestParam(required = false, defaultValue = "") String selectedRole,
                        HttpSession session,
                        Model model) {

        // Hata mesajını belirle (INACTIVE kontrolü dahil)
        String error = userService.getLoginError(username, password);
        if (error != null) {
            model.addAttribute("error", error);
            return "login";
        }

        Optional<User> user = userService.login(username, password);
        if (user.isPresent()) {
            // Seçilen rol ile DB'deki rol eşleşmiyor mu?
            if (!selectedRole.isEmpty() && !selectedRole.equalsIgnoreCase(user.get().getRole())) {
                model.addAttribute("error",
                        "Incorrect role selected. Your account role is " + user.get().getRole() + ".");
                return "login";
            }
            session.setAttribute("loggedUser", user.get());
            if (user.get().isTempPassword()) return "redirect:/change-password";
            return "redirect:/dashboard";
        }

        model.addAttribute("error", "Invalid username or password.");
        return "login";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}