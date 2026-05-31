package com.esportsclub.esports_management.controller;

import com.esportsclub.esports_management.model.Match;
import com.esportsclub.esports_management.model.Team;
import com.esportsclub.esports_management.model.TeamRequest;
import com.esportsclub.esports_management.model.Tournament;
import com.esportsclub.esports_management.model.User;
import com.esportsclub.esports_management.repository.UserRepository;
import com.esportsclub.esports_management.service.MatchService;
import com.esportsclub.esports_management.service.TeamRequestService;
import com.esportsclub.esports_management.service.TeamService;
import com.esportsclub.esports_management.service.TournamentService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class ProfileController {

    private final UserRepository userRepository;
    private final TeamService teamService;
    private final MatchService matchService;
    private final TournamentService tournamentService;
    private final TeamRequestService teamRequestService;

    public ProfileController(UserRepository userRepository,
                             TeamService teamService,
                             MatchService matchService,
                             TournamentService tournamentService,
                             TeamRequestService teamRequestService) {
        this.userRepository = userRepository;
        this.teamService = teamService;
        this.matchService = matchService;
        this.tournamentService = tournamentService;
        this.teamRequestService = teamRequestService;
    }

    @GetMapping("/profile")
    public String profile(HttpSession session, Model model) {
        User loggedUser = (User) session.getAttribute("loggedUser");
        if (loggedUser == null) return "redirect:/login";

        List<Team> allTeams = teamService.getAllTeams();
        List<Match> myMatches = List.of();
        List<Tournament> myTournaments = List.of();
        Map<Integer, String> teamNames = new HashMap<>();

        for (Team team : allTeams) {
            teamNames.put(team.getId(), team.getName());
        }

        String teamName = loggedUser.getTeamName();

        if (teamName != null && !teamName.trim().isEmpty()) {
            String cleanTeamName = teamName.trim();
            Team myTeam = allTeams.stream()
                    .filter(team -> team.getName() != null)
                    .filter(team -> team.getName().equalsIgnoreCase(cleanTeamName))
                    .findFirst().orElse(null);

            if (myTeam != null) {
                int teamId = myTeam.getId();
                myMatches = matchService.getAllMatches().stream()
                        .filter(match -> match.getTeam1Id() == teamId || match.getTeam2Id() == teamId)
                        .collect(Collectors.toList());

                List<Integer> tournamentIds = myMatches.stream()
                        .map(Match::getTournamentId).distinct().collect(Collectors.toList());

                myTournaments = tournamentService.getAllTournaments().stream()
                        .filter(tournament -> tournamentIds.contains(tournament.getId()))
                        .collect(Collectors.toList());
            }
        }

        boolean hasPendingRequest = teamRequestService.hasPendingRequest(loggedUser.getId());
        List<TeamRequest> myRequests = teamRequestService.getRequestsByUser(loggedUser.getId());

        boolean isMember = "MEMBER".equalsIgnoreCase(loggedUser.getRole());
        boolean isCoach = "COACH".equalsIgnoreCase(loggedUser.getRole());

        List<User> coachTeamPlayers = List.of();
        boolean coachHasTeam = false;

        if (isCoach && teamName != null && !teamName.trim().isEmpty()) {
            coachHasTeam = true;
            coachTeamPlayers = userRepository.findByTeamNameAndRole(teamName.trim(), "MEMBER");
        }

        model.addAttribute("loggedUser", loggedUser);
        model.addAttribute("allTeams", allTeams.stream()
                .filter(t -> "ACTIVE".equalsIgnoreCase(t.getStatus()))
                .collect(Collectors.toList()));
        model.addAttribute("teamNames", teamNames);
        model.addAttribute("myMatches", myMatches);
        model.addAttribute("myTournaments", myTournaments);
        model.addAttribute("hasPendingRequest", hasPendingRequest);
        model.addAttribute("myRequests", myRequests);
        model.addAttribute("totalMatches", myMatches.size());
        model.addAttribute("totalTournaments", myTournaments.size());
        model.addAttribute("isMember", isMember);
        model.addAttribute("isCoach", isCoach);
        model.addAttribute("coachHasTeam", coachHasTeam);
        model.addAttribute("coachTeamName", teamName);
        model.addAttribute("coachTeamPlayers", coachTeamPlayers);

        return "profile";
    }

    @PostMapping("/profile/request-team")
    public String requestTeam(@RequestParam String teamName, HttpSession session) {
        User loggedUser = (User) session.getAttribute("loggedUser");
        if (loggedUser == null) return "redirect:/login";
        if (!"MEMBER".equalsIgnoreCase(loggedUser.getRole())) return "redirect:/profile?error=notallowed";
        if (teamRequestService.hasPendingRequest(loggedUser.getId())) return "redirect:/profile?error=pending";
        if (teamName == null || teamName.trim().isEmpty()) return "redirect:/profile?error=noteam";

        // INACTIVE takım kontrolü
        boolean isInactive = teamService.getAllTeams().stream()
                .filter(t -> t.getName().equalsIgnoreCase(teamName.trim()))
                .findFirst()
                .map(t -> "INACTIVE".equalsIgnoreCase(t.getStatus()))
                .orElse(true);

        if (isInactive) return "redirect:/profile?error=inactiveteam";

        TeamRequest request = new TeamRequest();
        request.setUserId(loggedUser.getId());
        request.setTeamName(teamName.trim());
        request.setStatus("PENDING");
        request.setRequestedAt(LocalDateTime.now());
        teamRequestService.saveRequest(request);

        return "redirect:/profile?success=requested";
    }

    @PostMapping("/profile/remove-member")
    public String removeMember(@RequestParam int userId, HttpSession session) {
        User loggedUser = (User) session.getAttribute("loggedUser");
        if (loggedUser == null) return "redirect:/login";
        if (!"COACH".equalsIgnoreCase(loggedUser.getRole())) return "redirect:/profile";

        String coachTeam = loggedUser.getTeamName();
        if (coachTeam == null || coachTeam.trim().isEmpty()) return "redirect:/profile";

        userRepository.findById(userId).ifPresent(member -> {
            if (coachTeam.equalsIgnoreCase(member.getTeamName()) &&
                    "MEMBER".equalsIgnoreCase(member.getRole())) {
                member.setTeamName(null);
                userRepository.save(member);
            }
        });

        return "redirect:/profile";
    }
}