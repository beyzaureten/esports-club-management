package com.esportsclub.esports_management.controller;

import com.esportsclub.esports_management.factory.UserFactory;
import com.esportsclub.esports_management.model.User;
import com.esportsclub.esports_management.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class RegisterController {

    private final UserService userService;
    private final UserFactory userFactory;

    public RegisterController(UserService userService, UserFactory userFactory) {
        this.userService = userService;
        this.userFactory = userFactory;
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute("user") User formUser, Model model) {
        if (formUser.getUsername() == null || formUser.getUsername().trim().isEmpty() ||
                formUser.getEmail() == null || formUser.getEmail().trim().isEmpty() ||
                formUser.getPassword() == null || formUser.getPassword().trim().isEmpty()) {
            model.addAttribute("error", "All fields are required.");
            return "register";
        }

        // Şifre minimum uzunluk kontrolü
        if (formUser.getPassword().trim().length() < 6) {
            model.addAttribute("error", "Password must be at least 6 characters.");
            return "register";
        }

        // Username benzersizlik kontrolü
        if (userService.findByUsername(formUser.getUsername().trim()).isPresent()) {
            model.addAttribute("error", "This username is already taken. Please choose another.");
            return "register";
        }

        // Email benzersizlik kontrolü
        if (userService.findByEmail(formUser.getEmail().trim()).isPresent()) {
            model.addAttribute("error", "This email address is already registered.");
            return "register";
        }

        User newUser = userFactory.createMember(
                formUser.getUsername().trim(),
                formUser.getEmail().trim(),
                formUser.getPassword().trim()
        );

        userService.saveUser(newUser);
        return "redirect:/login";
    }
}