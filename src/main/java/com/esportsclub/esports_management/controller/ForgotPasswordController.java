package com.esportsclub.esports_management.controller;

import com.esportsclub.esports_management.model.User;
import com.esportsclub.esports_management.repository.UserRepository;
import com.esportsclub.esports_management.service.PasswordService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;

@Controller
public class ForgotPasswordController {

    private final UserRepository userRepository;
    private final PasswordService passwordService;

    public ForgotPasswordController(UserRepository userRepository,
                                    PasswordService passwordService) {
        this.userRepository = userRepository;
        this.passwordService = passwordService;
    }

    @GetMapping("/forgot-password")
    public String forgotPage() {
        return "forgot-password";
    }

    @PostMapping("/forgot-password")
    public String handleForgot(@RequestParam String email, Model model, HttpSession session) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();

            // INACTIVE kullanıcıya geçici şifre verme
            if (!"ACTIVE".equalsIgnoreCase(user.getStatus())) {
                model.addAttribute("error",
                        "This account is inactive. Please contact an administrator.");
                return "forgot-password";
            }

            // Eski şifre hash'ini session'a kaydet
            session.setAttribute("originalPasswordHash", user.getPassword());

            String tempPass = "Temp" + (int)(Math.random() * 9000 + 1000);
            String hashed = passwordService.hashPassword(tempPass);

            userRepository.updatePasswordAndTempFlag(user.getId(), hashed, true);

            model.addAttribute("success", true);
            model.addAttribute("username", user.getUsername());
            model.addAttribute("tempPassword", tempPass);
        } else {
            model.addAttribute("error", "This email address is not registered in the system.");
        }
        return "forgot-password";
    }
}