package com.esportsclub.esports_management.controller;

import com.esportsclub.esports_management.model.Team;
import com.esportsclub.esports_management.model.TeamRequest;
import com.esportsclub.esports_management.model.User;
import com.esportsclub.esports_management.repository.UserRepository;
import com.esportsclub.esports_management.service.TeamRequestService;
import com.esportsclub.esports_management.service.TeamService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/team-requests")
public class TeamRequestController {

    private final TeamRequestService teamRequestService;
    private final UserRepository userRepository;
    private final TeamService teamService;

    public TeamRequestController(TeamRequestService teamRequestService,
                                 UserRepository userRepository,
                                 TeamService teamService) {
        this.teamRequestService = teamRequestService;
        this.userRepository = userRepository;
        this.teamService = teamService;
    }

    @GetMapping
    public String list(HttpSession session, Model model) {
        User loggedUser = (User) session.getAttribute("loggedUser");
        if (loggedUser == null) return "redirect:/login";

        List<TeamRequest> pendingRequests;
        List<TeamRequest> allRequests;

        if ("ADMIN".equals(loggedUser.getRole())) {
            pendingRequests = teamRequestService.getPendingRequests();
            allRequests = teamRequestService.getAllRequests();
        } else if ("COACH".equals(loggedUser.getRole())) {
            String coachTeam = loggedUser.getTeamName();
            if (coachTeam == null || coachTeam.isEmpty()) {
                model.addAttribute("pendingRequests", List.of());
                model.addAttribute("allRequests", List.of());
                model.addAttribute("loggedUser", loggedUser);
                model.addAttribute("noTeamWarning", true);
                return "team-requests";
            }
            pendingRequests = teamRequestService.getPendingRequests().stream()
                    .filter(r -> coachTeam.equals(r.getTeamName()))
                    .collect(Collectors.toList());
            allRequests = teamRequestService.getAllRequests().stream()
                    .filter(r -> coachTeam.equals(r.getTeamName()))
                    .collect(Collectors.toList());
        } else if ("MEMBER".equals(loggedUser.getRole())) {
            pendingRequests = teamRequestService.getPendingRequests().stream()
                    .filter(r -> r.getUserId() == loggedUser.getId())
                    .collect(Collectors.toList());
            allRequests = teamRequestService.getAllRequests().stream()
                    .filter(r -> r.getUserId() == loggedUser.getId())
                    .collect(Collectors.toList());
        } else {
            return "redirect:/access-denied";
        }

        // Sadece ACTIVE takımları göster
        List<Team> activeTeams = teamService.getAllTeams().stream()
                .filter(t -> "ACTIVE".equalsIgnoreCase(t.getStatus()))
                .collect(Collectors.toList());

        model.addAttribute("pendingRequests", pendingRequests);
        model.addAttribute("allRequests", allRequests);
        model.addAttribute("availableTeams", activeTeams);
        model.addAttribute("loggedUser", loggedUser);
        return "team-requests";
    }

    @PostMapping("/submit")
    public String submitRequest(@RequestParam String teamName, HttpSession session) {
        User loggedUser = (User) session.getAttribute("loggedUser");
        if (loggedUser == null) return "redirect:/login";

        if (!"MEMBER".equalsIgnoreCase(loggedUser.getRole())) {
            return "redirect:/team-requests?error=notallowed";
        }

        if (teamRequestService.hasPendingRequest(loggedUser.getId())) {
            return "redirect:/team-requests?error=pending";
        }

        // INACTIVE takım kontrolü
        Optional<Team> teamOpt = teamService.getAllTeams().stream()
                .filter(t -> t.getName().equalsIgnoreCase(teamName.trim()))
                .findFirst();

        if (teamOpt.isEmpty() || "INACTIVE".equalsIgnoreCase(teamOpt.get().getStatus())) {
            return "redirect:/team-requests?error=inactiveteam";
        }

        TeamRequest request = new TeamRequest();
        request.setUserId(loggedUser.getId());
        request.setUsername(loggedUser.getUsername());
        request.setTeamName(teamName);
        request.setStatus("PENDING");
        request.setRequestedAt(LocalDateTime.now());

        teamRequestService.saveRequest(request);
        return "redirect:/team-requests?success=submitted";
    }

    @PostMapping("/approve/{id}")
    public String approve(@PathVariable int id, HttpSession session) {
        User loggedUser = (User) session.getAttribute("loggedUser");
        if (loggedUser == null) return "redirect:/login";

        TeamRequest request = teamRequestService.getById(id);
        if (request == null || !"PENDING".equals(request.getStatus())) {
            return "redirect:/team-requests";
        }

        if ("COACH".equals(loggedUser.getRole())) {
            if (!request.getTeamName().equals(loggedUser.getTeamName())) {
                return "redirect:/access-denied";
            }
        }

        request.setStatus("APPROVED");
        request.setReviewedAt(LocalDateTime.now());
        request.setReviewedBy(loggedUser.getUsername());
        teamRequestService.saveRequest(request);

        userRepository.findById(request.getUserId()).ifPresent(user -> {
            user.setTeamName(request.getTeamName());
            userRepository.save(user);
        });

        return "redirect:/team-requests";
    }

    @PostMapping("/reject/{id}")
    public String reject(@PathVariable int id, HttpSession session) {
        User loggedUser = (User) session.getAttribute("loggedUser");
        if (loggedUser == null) return "redirect:/login";

        TeamRequest request = teamRequestService.getById(id);
        if (request == null || !"PENDING".equals(request.getStatus())) {
            return "redirect:/team-requests";
        }

        if ("COACH".equals(loggedUser.getRole())) {
            if (!request.getTeamName().equals(loggedUser.getTeamName())) {
                return "redirect:/access-denied";
            }
        }

        request.setStatus("REJECTED");
        request.setReviewedAt(LocalDateTime.now());
        request.setReviewedBy(loggedUser.getUsername());
        teamRequestService.saveRequest(request);

        return "redirect:/team-requests";
    }
}