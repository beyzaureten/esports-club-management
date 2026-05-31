package com.esportsclub.esports_management.controller;

import com.esportsclub.esports_management.model.Match;
import com.esportsclub.esports_management.model.Team;
import com.esportsclub.esports_management.model.TeamRequest;
import com.esportsclub.esports_management.model.User;
import com.esportsclub.esports_management.model.Tournament; // 🎯 HATAYI ÇÖZEN KRİTİK IMPORT SATIRI!
import com.esportsclub.esports_management.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.*;
import java.util.stream.Collectors;

@Controller
public class DashboardController {

    private final MatchService matchService;
    private final UserService userService;
    private final TournamentService tournamentService;
    private final TeamService teamService;
    private final TeamRequestService teamRequestService;

    public DashboardController(MatchService matchService, UserService userService,
                               TournamentService tournamentService, TeamService teamService,
                               TeamRequestService teamRequestService) {
        this.matchService = matchService;
        this.userService = userService;
        this.tournamentService = tournamentService;
        this.teamService = teamService;
        this.teamRequestService = teamRequestService;
    }

    @GetMapping({"/dashboard", "/"})
    public String dashboard(Model model, HttpSession session) {
        User loggedUser = (User) session.getAttribute("loggedUser");
        if (loggedUser == null) return "redirect:/login";

        List<Match> allMatches = matchService.getAllMatches();
        List<Team> allTeams = teamService.getAllTeams();

        Map<Integer, String> teamNames = allTeams.stream()
                .collect(Collectors.toMap(Team::getId, Team::getName, (v1, v2) -> v1));

        // 🎯 ROL BAZLI BİLDİRİM VE SAYAÇ MOTORU
        List<TeamRequest> filteredRequestsForNotif = new ArrayList<>();
        long pendingCount = 0;

        List<TeamRequest> allRequests = teamRequestService.getAllRequests();
        List<TeamRequest> pendingRequests = teamRequestService.getPendingRequests();

        if ("ADMIN".equals(loggedUser.getRole())) {
            // Admin her şeyi görür ve tüm bekleyen isteklerin sayısını sayar
            pendingCount = pendingRequests.size();
            filteredRequestsForNotif = allRequests;
        } else if ("COACH".equals(loggedUser.getRole())) {
            String coachTeam = loggedUser.getTeamName();
            if (coachTeam != null && !coachTeam.isEmpty()) {
                // Koç sadece kendi takımına gelen bekleyenlerin sayısını görür
                pendingCount = pendingRequests.stream()
                        .filter(r -> coachTeam.equals(r.getTeamName()))
                        .count();
                // Koç kendi takımına gelen tüm isteklerin geçmişini listede görür
                filteredRequestsForNotif = allRequests.stream()
                        .filter(r -> coachTeam.equals(r.getTeamName()))
                        .collect(Collectors.toList());
            }
        } else if ("MEMBER".equals(loggedUser.getRole())) {
            // Member sadece kendi bekleyen isteklerinin sayısını görür
            pendingCount = pendingRequests.stream()
                    .filter(r -> r.getUserId() == loggedUser.getId())
                    .count();
            // Member kendi isteklerinin onay/red dahil tüm durum bildirimlerini görür
            filteredRequestsForNotif = allRequests.stream()
                    .filter(r -> r.getUserId() == loggedUser.getId())
                    .collect(Collectors.toList());
        }

        // 📨 BİLDİRİMLERİ İNSANİ VE ANLAŞILIR METİNLERE DÖNÜŞTÜRME
        List<Map<String, String>> notifications = new ArrayList<>();

        for (TeamRequest req : filteredRequestsForNotif) {
            Map<String, String> n = new HashMap<>();
            String usernameDisplay = (req.getUsername() != null) ? req.getUsername() : "User ID: " + req.getUserId();

            if ("PENDING".equals(req.getStatus())) {
                if ("MEMBER".equals(loggedUser.getRole())) {
                    n.put("message", "⏳ Your request to join Team [" + req.getTeamName() + "] is PENDING.");
                } else {
                    n.put("message", "📨 " + usernameDisplay + " applied to join Team [" + req.getTeamName() + "].");
                }
                n.put("time", "Team Requests");
                notifications.add(n);
            } else if ("APPROVED".equals(req.getStatus())) {
                if ("MEMBER".equals(loggedUser.getRole())) {
                    n.put("message", "✅ Your request to join Team [" + req.getTeamName() + "] was APPROVED by " + (req.getReviewedBy() != null ? req.getReviewedBy() : "Admin") + "!");
                } else {
                    n.put("message", "🟢 Request of " + usernameDisplay + " for Team [" + req.getTeamName() + "] was APPROVED.");
                }
                n.put("time", "Approved");
                notifications.add(n);
            } else if ("REJECTED".equals(req.getStatus())) {
                if ("MEMBER".equals(loggedUser.getRole())) {
                    n.put("message", "❌ Your request to join Team [" + req.getTeamName() + "] was REJECTED.");
                } else {
                    n.put("message", "🔴 Request of " + usernameDisplay + " for Team [" + req.getTeamName() + "] was REJECTED.");
                }
                n.put("time", "Rejected");
                notifications.add(n);
            }
        }

        // Biten Maç Bildirimleri (Orijinal mantık korundu)
        allMatches.stream()
                .filter(m -> "FINISHED".equals(m.getStatus()))
                .limit(3)
                .forEach(m -> {
                    Map<String, String> n = new HashMap<>();
                    n.put("message", "⚔️ Match finished: "
                            + teamNames.getOrDefault(m.getTeam1Id(), "Team " + m.getTeam1Id())
                            + " " + m.getTeam1Score() + " - " + m.getTeam2Score() + " "
                            + teamNames.getOrDefault(m.getTeam2Id(), "Team " + m.getTeam2Id()));
                    n.put("time", m.getMatchDate() != null ? m.getMatchDate().toString() : "");
                    notifications.add(n);
                });

        // ONGOING turnuva bildirimleri
        tournamentService.getAllTournaments().stream()
                .filter(t -> "ONGOING".equals(t.getStatus()))
                .limit(2)
                .forEach(t -> {
                    Map<String, String> n = new HashMap<>();
                    n.put("message", "🏆 Tournament ongoing: " + t.getName());
                    n.put("time", "Tournament Alert");
                    notifications.add(n);
                });

        model.addAttribute("notifications", notifications);
        model.addAttribute("pendingRequestCount", pendingCount);

        model.addAttribute("totalMatches", allMatches.size());
        model.addAttribute("totalUsers", userService.getAllUsers().size());
        model.addAttribute("totalTournaments", tournamentService.getAllTournaments().size());
        model.addAttribute("totalTeams", allTeams.size());

        // ONGOING önce, sonra UPCOMING, sonra FINISHED
        List<Tournament> recentTournaments = tournamentService.getAllTournaments().stream()
                .sorted(Comparator.comparingInt(t -> {
                    if ("ONGOING".equals(t.getStatus())) return 0;
                    if ("UPCOMING".equals(t.getStatus())) return 1;
                    return 2;
                }))
                .limit(5)
                .collect(Collectors.toList());

        // En son maçlar önce
        List<Match> recentMatches = allMatches.stream()
                .sorted(Comparator.comparing(
                        m -> m.getMatchDate() != null ? m.getMatchDate() : java.time.LocalDate.MIN,
                        Comparator.reverseOrder()))
                .limit(5)
                .collect(Collectors.toList());

        model.addAttribute("recentTournaments", recentTournaments);
        model.addAttribute("recentMatches", recentMatches);
        model.addAttribute("teamNames", teamNames);
        model.addAttribute("loggedUser", loggedUser);

        return "dashboard";
    }
}