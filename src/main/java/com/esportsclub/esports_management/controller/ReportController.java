package com.esportsclub.esports_management.controller;

import com.esportsclub.esports_management.model.Match;
import com.esportsclub.esports_management.model.Team;
import com.esportsclub.esports_management.model.Tournament;
import com.esportsclub.esports_management.model.User;
import com.esportsclub.esports_management.model.Game;
import com.esportsclub.esports_management.service.*;
import com.esportsclub.esports_management.strategy.ScoringStrategy;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.*;
import java.util.stream.Collectors;

@Controller
public class ReportController {

    private final MatchService matchService;
    private final TeamService teamService;
    private final TournamentService tournamentService;
    private final UserService userService;
    private final GameService gameService;
    private final ScoringStrategy pointsStrategy;
    private final ScoringStrategy winRateStrategy;

    public ReportController(MatchService matchService,
                            TeamService teamService,
                            TournamentService tournamentService,
                            UserService userService,
                            GameService gameService,
                            @Qualifier("points") ScoringStrategy pointsStrategy,
                            @Qualifier("winrate") ScoringStrategy winRateStrategy) {
        this.matchService = matchService;
        this.teamService = teamService;
        this.tournamentService = tournamentService;
        this.userService = userService;
        this.gameService = gameService;
        this.pointsStrategy = pointsStrategy;
        this.winRateStrategy = winRateStrategy;
    }

    @GetMapping("/reports")
    public String reports(Model model, HttpSession session) {
        User loggedUser = (User) session.getAttribute("loggedUser");
        if (loggedUser == null) return "redirect:/login";

        String strategy = (String) session.getAttribute("strategy");
        if (strategy == null || strategy.isBlank()) strategy = "points";

        List<Match> allMatches = matchService.getAllMatches();
        List<Team> allTeams = teamService.getAllTeams();
        List<Tournament> allTournaments = tournamentService.getAllTournaments();
        List<User> allUsers = userService.getAllUsers();

        // Geçici winner hesapla
        for (Match m : allMatches) {
            if ("FINISHED".equalsIgnoreCase(m.getStatus()) && m.getWinnerId() == null) {
                if (m.getTeam1Score() != null && m.getTeam2Score() != null) {
                    if (m.getTeam1Score() > m.getTeam2Score()) m.setWinnerId(m.getTeam1Id());
                    else if (m.getTeam2Score() > m.getTeam1Score()) m.setWinnerId(m.getTeam2Id());
                }
            }
        }

        // Win counts ve match counts
        Map<Integer, Long> winCounts = new HashMap<>();
        Map<Integer, Long> matchCounts = new HashMap<>();
        Map<Integer, Integer> teamPoints = new HashMap<>();

        for (Team team : allTeams) {
            winCounts.put(team.getId(), 0L);
            matchCounts.put(team.getId(), 0L);
            teamPoints.put(team.getId(), 0);
        }

        for (Match match : allMatches) {
            if (match == null) continue;
            if (!"FINISHED".equalsIgnoreCase(match.getStatus())) continue;

            Integer team1Id = match.getTeam1Id();
            Integer team2Id = match.getTeam2Id();
            Integer winnerId = match.getWinnerId();
            int team1Score = match.getTeam1Score() != null ? match.getTeam1Score() : 0;
            int team2Score = match.getTeam2Score() != null ? match.getTeam2Score() : 0;

            if (team1Id != null) {
                matchCounts.put(team1Id, matchCounts.getOrDefault(team1Id, 0L) + 1);
                teamPoints.put(team1Id, teamPoints.getOrDefault(team1Id, 0) + team1Score);
            }
            if (team2Id != null) {
                matchCounts.put(team2Id, matchCounts.getOrDefault(team2Id, 0L) + 1);
                teamPoints.put(team2Id, teamPoints.getOrDefault(team2Id, 0) + team2Score);
            }
            if (winnerId != null && winnerId > 0) {
                winCounts.put(winnerId, winCounts.getOrDefault(winnerId, 0L) + 1);
                teamPoints.put(winnerId, teamPoints.getOrDefault(winnerId, 0) + 3);
            }
        }

        // Strateji bazlı sıralama
        List<Team> rankedTeams;
        if ("winrate".equalsIgnoreCase(strategy)) {
            rankedTeams = allTeams.stream()
                    .sorted((a, b) -> {
                        long wA = winCounts.getOrDefault(a.getId(), 0L);
                        long wB = winCounts.getOrDefault(b.getId(), 0L);
                        long mA = matchCounts.getOrDefault(a.getId(), 0L);
                        long mB = matchCounts.getOrDefault(b.getId(), 0L);
                        double rA = mA > 0 ? (double) wA / mA : 0.0;
                        double rB = mB > 0 ? (double) wB / mB : 0.0;
                        return Double.compare(rB, rA);
                    }).collect(Collectors.toList());
        } else {
            rankedTeams = allTeams.stream()
                    .sorted(Comparator.comparingInt((Team t) -> teamPoints.getOrDefault(t.getId(), 0)).reversed())
                    .collect(Collectors.toList());
        }

        // MVP
        String mvpTeamName = "No data yet";
        int mvpWins = 0;
        if (winCounts.values().stream().anyMatch(v -> v > 0)) {
            int mvpTeamId = Collections.max(winCounts.entrySet(), Map.Entry.comparingByValue()).getKey();
            mvpWins = winCounts.get(mvpTeamId).intValue();
            mvpTeamName = allTeams.stream()
                    .filter(t -> t.getId() == mvpTeamId)
                    .findFirst()
                    .map(Team::getName)
                    .orElse("Team #" + mvpTeamId);
        }

        // Most Active
        String mostActiveTeam = "No data yet";
        if (matchCounts.values().stream().anyMatch(v -> v > 0)) {
            int activeTeamId = Collections.max(matchCounts.entrySet(), Map.Entry.comparingByValue()).getKey();
            mostActiveTeam = allTeams.stream()
                    .filter(t -> t.getId() == activeTeamId)
                    .findFirst()
                    .map(Team::getName)
                    .orElse("Team #" + activeTeamId);
        }

        List<Tournament> finishedTournaments = allTournaments.stream()
                .filter(t -> t.getStatus() != null && "FINISHED".equalsIgnoreCase(t.getStatus()))
                .collect(Collectors.toList());

        List<User> activeUsers = allUsers.stream()
                .filter(u -> u.getStatus() != null && "ACTIVE".equalsIgnoreCase(u.getStatus()))
                .collect(Collectors.toList());

        Map<Integer, String> teamNames = allTeams.stream()
                .collect(Collectors.toMap(Team::getId, Team::getName, (v1, v2) -> v1));

        Map<Integer, String> tournamentNames = allTournaments.stream()
                .collect(Collectors.toMap(Tournament::getId, Tournament::getName, (v1, v2) -> v1));

        Map<Integer, String> gameMap = gameService.getAllGames().stream()
                .collect(Collectors.toMap(Game::getId, Game::getName, (v1, v2) -> v1));

        model.addAttribute("totalMatches", allMatches.size());
        model.addAttribute("totalUsers", allUsers.size());
        model.addAttribute("totalTournaments", allTournaments.size());
        model.addAttribute("totalTeams", allTeams.size());
        model.addAttribute("activeUsers", activeUsers);
        model.addAttribute("finishedTournaments", finishedTournaments);
        model.addAttribute("allMatches", allMatches);
        model.addAttribute("teamNames", teamNames);
        model.addAttribute("tournamentNames", tournamentNames);
        model.addAttribute("gameMap", gameMap);
        model.addAttribute("rankedTeams", rankedTeams);
        model.addAttribute("winCounts", winCounts);
        model.addAttribute("matchCounts", matchCounts);
        model.addAttribute("teamPoints", teamPoints);
        model.addAttribute("mvpTeamName", mvpTeamName);
        model.addAttribute("mvpWins", mvpWins);
        model.addAttribute("mostActiveTeam", mostActiveTeam);
        model.addAttribute("selectedStrategy", strategy);
        model.addAttribute("loggedUser", loggedUser);

        return "reports";
    }

    @PostMapping("/reports/strategy")
    public String changeStrategy(@RequestParam String strategy, HttpSession session) {
        session.setAttribute("strategy", strategy);
        return "redirect:/reports";
    }
}