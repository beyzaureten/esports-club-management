package com.esportsclub.esports_management.controller;

import com.esportsclub.esports_management.factory.UserFactory;
import com.esportsclub.esports_management.model.User;
import com.esportsclub.esports_management.service.PasswordService;
import com.esportsclub.esports_management.service.TeamService;
import com.esportsclub.esports_management.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final TeamService teamService;
    private final PasswordService passwordService;
    private final UserFactory userFactory;

    public UserController(UserService userService, TeamService teamService,
                          PasswordService passwordService, UserFactory userFactory) {
        this.userService = userService;
        this.teamService = teamService;
        this.passwordService = passwordService;
        this.userFactory = userFactory;
    }

    @GetMapping
    public String list(Model model, HttpSession session) {
        User loggedUser = (User) session.getAttribute("loggedUser");
        if (loggedUser == null || !"ADMIN".equals(loggedUser.getRole())) return "redirect:/dashboard";
        model.addAttribute("users", userService.getAllUsers());
        model.addAttribute("newUser", new User());
        model.addAttribute("teams", teamService.getAllTeams());
        model.addAttribute("loggedUser", loggedUser);
        return "users";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute("newUser") User formUser,
                       HttpSession session,
                       RedirectAttributes redirectAttributes) {
        User loggedUser = (User) session.getAttribute("loggedUser");
        if (loggedUser == null || !"ADMIN".equals(loggedUser.getRole())) return "redirect:/login";

        if (formUser.getUsername() == null || formUser.getUsername().trim().isEmpty() ||
                formUser.getEmail() == null || formUser.getEmail().trim().isEmpty() ||
                formUser.getPassword() == null || formUser.getPassword().trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "All fields are required!");
            return "redirect:/users";
        }

        if (formUser.getPassword().trim().length() < 6) {
            redirectAttributes.addFlashAttribute("error", "Password must be at least 6 characters.");
            return "redirect:/users";
        }

        if (userService.findByUsername(formUser.getUsername().trim()).isPresent()) {
            redirectAttributes.addFlashAttribute("error", "This username is already taken. Please choose another.");
            return "redirect:/users";
        }

        if (userService.findByEmail(formUser.getEmail().trim()).isPresent()) {
            redirectAttributes.addFlashAttribute("error", "This email address is already registered.");
            return "redirect:/users";
        }

        String role = (formUser.getRole() == null || formUser.getRole().isEmpty()) ? "MEMBER" : formUser.getRole();

        User newUser = userFactory.createUser(
                formUser.getUsername().trim(),
                formUser.getEmail().trim(),
                formUser.getPassword().trim(),
                role
        );

        if (!"ADMIN".equalsIgnoreCase(role)) {
            newUser.setTeamName(formUser.getTeamName());
        }

        if (formUser.getStatus() != null && !formUser.getStatus().isEmpty()) {
            newUser.setStatus(formUser.getStatus());
        }

        userService.saveUser(newUser);
        redirectAttributes.addFlashAttribute("success", "User created successfully.");
        return "redirect:/users";
    }

    @PostMapping("/update")
    public String update(@ModelAttribute User user,
                         HttpSession session,
                         RedirectAttributes redirectAttributes) {
        User loggedUser = (User) session.getAttribute("loggedUser");
        if (loggedUser == null || !"ADMIN".equals(loggedUser.getRole())) return "redirect:/login";

        if (user.getRole() == null || user.getRole().isEmpty()) user.setRole("MEMBER");
        if (user.getStatus() == null || user.getStatus().isEmpty()) user.setStatus("ACTIVE");

        if ("ADMIN".equals(user.getRole())) {
            user.setTeamName(null);
        }

        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            userService.getAllUsers().stream()
                    .filter(u -> u.getId() == user.getId())
                    .findFirst()
                    .ifPresent(existing -> user.setPassword(existing.getPassword()));
        } else {
            if (user.getPassword().trim().length() < 6) {
                redirectAttributes.addFlashAttribute("error", "Password must be at least 6 characters.");
                return "redirect:/users";
            }
            user.setPassword(passwordService.hashPassword(user.getPassword()));
        }

        userService.updateUser(user);
        return "redirect:/users";
    }

    @PostMapping("/update-role/{id}")
    public String updateRole(@PathVariable int id, @RequestParam String role, HttpSession session) {
        User loggedUser = (User) session.getAttribute("loggedUser");
        if (loggedUser == null || !"ADMIN".equals(loggedUser.getRole())) return "redirect:/login";
        userService.getAllUsers().stream()
                .filter(u -> u.getId() == id).findFirst()
                .ifPresent(u -> { u.setRole(role); userService.updateUser(u); });
        return "redirect:/users";
    }

    @PostMapping("/update-team/{id}")
    public String updateTeam(@PathVariable int id, @RequestParam String teamName, HttpSession session) {
        User loggedUser = (User) session.getAttribute("loggedUser");
        if (loggedUser == null || !"ADMIN".equals(loggedUser.getRole())) return "redirect:/login";
        userService.getAllUsers().stream()
                .filter(u -> u.getId() == id).findFirst()
                .ifPresent(u -> {
                    u.setTeamName(teamName == null || teamName.isEmpty() ? null : teamName);
                    userService.updateUser(u);
                });
        return "redirect:/users";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable int id, HttpSession session) {
        User loggedUser = (User) session.getAttribute("loggedUser");
        if (loggedUser != null && "ADMIN".equals(loggedUser.getRole())) {
            userService.deleteUser(id);
        }
        return "redirect:/users";
    }
}