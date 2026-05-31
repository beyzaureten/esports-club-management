package com.esportsclub.esports_management.controller;

import com.esportsclub.esports_management.model.User;
import com.esportsclub.esports_management.repository.UserRepository;
import com.esportsclub.esports_management.service.PasswordService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class ChangePasswordController {

    private final UserRepository userRepository;
    private final PasswordService passwordService;

    public ChangePasswordController(UserRepository userRepository,
                                    PasswordService passwordService) {
        this.userRepository = userRepository;
        this.passwordService = passwordService;
    }

    @GetMapping("/change-password")
    public String changePage(HttpSession session, Model model) {
        User loggedUser = (User) session.getAttribute("loggedUser");
        if (loggedUser == null) return "redirect:/login";

        // Geçici şifresi yoksa buraya gelememeli
        if (!loggedUser.isTempPassword()) return "redirect:/dashboard";

        model.addAttribute("loggedUser", loggedUser);
        return "change-password";
    }

    @PostMapping("/change-password")
    public String handleChange(@RequestParam String newPassword,
                               @RequestParam String confirmPassword,
                               HttpSession session,
                               Model model) {
        User loggedUser = (User) session.getAttribute("loggedUser");
        if (loggedUser == null) return "redirect:/login";

        User dbUser = userRepository.findById(loggedUser.getId()).orElse(null);
        if (dbUser == null) return "redirect:/login";

        if (newPassword.length() < 6) {
            model.addAttribute("error", "Password must be at least 6 characters.");
            model.addAttribute("loggedUser", loggedUser);
            return "change-password";
        }

        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("error", "Passwords do not match.");
            model.addAttribute("loggedUser", loggedUser);
            return "change-password";
        }

        // Eski şifre hash'i session'da varsa onunla karşılaştır
        String originalHash = (String) session.getAttribute("originalPasswordHash");
        if (originalHash != null && passwordService.checkPassword(newPassword, originalHash)) {
            model.addAttribute("error", "New password must be different from your previous password.");
            model.addAttribute("loggedUser", loggedUser);
            return "change-password";
        }

        dbUser.setPassword(passwordService.hashPassword(newPassword));
        dbUser.setTempPassword(false);
        userRepository.save(dbUser);
        session.setAttribute("loggedUser", dbUser);
        session.removeAttribute("originalPasswordHash");

        return "redirect:/dashboard";
    }
}