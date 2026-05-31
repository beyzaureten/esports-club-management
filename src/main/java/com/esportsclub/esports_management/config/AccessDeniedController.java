package com.esportsclub.esports_management.controller;

import com.esportsclub.esports_management.model.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AccessDeniedController {

    @GetMapping("/access-denied")
    public String accessDenied(HttpSession session, Model model) {
        User loggedUser = (User) session.getAttribute("loggedUser");
        model.addAttribute("loggedUser", loggedUser);
        return "access-denied";
    }
}