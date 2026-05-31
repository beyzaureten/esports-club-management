package com.esportsclub.esports_management.controller;

import com.esportsclub.esports_management.model.Game;
import com.esportsclub.esports_management.model.Match;
import com.esportsclub.esports_management.model.Team;
import com.esportsclub.esports_management.model.Tournament;
import com.esportsclub.esports_management.model.User;
import com.esportsclub.esports_management.service.GameService;
import com.esportsclub.esports_management.service.MatchService;
import com.esportsclub.esports_management.service.TeamService;
import com.esportsclub.esports_management.service.TournamentService;
import com.esportsclub.esports_management.strategy.ScoringStrategy;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/tournaments")
public class TournamentController {

    private final TournamentService tournamentService;
    private final TeamService teamService;
    private final MatchService matchService;
    private final GameService gameService;
    private final ScoringStrategy pointsStrategy;
    private final ScoringStrategy winRateStrategy;

    public TournamentController(TournamentService tournamentService,
                                TeamService teamService,
                                MatchService matchService,
                                GameService gameService,
                                @Qualifier("points") ScoringStrategy pointsStrategy,
                                @Qualifier("winrate") ScoringStrategy winRateStrategy) {
        this.tournamentService = tournamentService;
        this.teamService = teamService;
        this.matchService = matchService;
        this.gameService = gameService;
        this.pointsStrategy = pointsStrategy;
        this.winRateStrategy = winRateStrategy;
    }

    @GetMapping
    public String list(Model model, HttpSession session) {
        User loggedUser = (User) session.getAttribute("loggedUser");
        if (loggedUser == null) return "redirect:/login";

        String strategy = (String) session.getAttribute("strategy");
        if (strategy == null || strategy.isBlank()) strategy = "points";

        List<Team> allTeams = teamService.getAllTeams();
        List<Match> allMatches = matchService.getAllMatches();

        Map<Integer, Long> winCounts = new HashMap<>();
        Map<Integer, Long> matchCounts = new HashMap<>();
        Map<Integer, Integer> teamPoints = new HashMap<>();

        for (Team team : allTeams) {
            teamPoints.put(team.getId(), 0);
            winCounts.put(team.getId(), 0L);
            matchCounts.put(team.getId(), 0L);
        }

        for (Match match : allMatches) {
            if (match == null) continue;
            String status = match.getStatus();
            if (status == null || !"FINISHED".equalsIgnoreCase(status)) continue;

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

        Map<Integer, String> gameMap = gameService.getAllGames().stream()
                .collect(Collectors.toMap(Game::getId, Game::getName, (v1, v2) -> v1));

        model.addAttribute("tournaments", tournamentService.getAllTournaments());
        model.addAttribute("games", gameService.getAllGames());
        model.addAttribute("gameMap", gameMap);
        model.addAttribute("rankedTeams", rankedTeams);
        model.addAttribute("winCounts", winCounts);
        model.addAttribute("matchCounts", matchCounts);
        model.addAttribute("teamPoints", teamPoints);
        model.addAttribute("tournament", new Tournament());
        model.addAttribute("selectedStrategy", strategy);
        model.addAttribute("loggedUser", loggedUser);
        model.addAttribute("observerMessage", session.getAttribute("observerMessage"));
        session.removeAttribute("observerMessage");

        return "tournaments";
    }

    @PostMapping("/strategy")
    public String changeStrategy(@RequestParam String strategy, HttpSession session) {
        session.setAttribute("strategy", strategy);
        return "redirect:/tournaments";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute("tournament") Tournament tournament, HttpSession session, Model model) {
        User loggedUser = (User) session.getAttribute("loggedUser");
        if (loggedUser == null || !"ADMIN".equalsIgnoreCase(loggedUser.getRole())) return "redirect:/tournaments";

        if (tournament.getStatus() == null || tournament.getStatus().isBlank()) tournament.setStatus("UPCOMING");

        String error = tournamentService.saveTournament(tournament);
        if (error != null) {
            model.addAttribute("tournaments", tournamentService.getAllTournaments());
            model.addAttribute("games", gameService.getAllGames());
            model.addAttribute("tournament", tournament);
            model.addAttribute("error", error);
            model.addAttribute("loggedUser", loggedUser);
            return "tournaments";
        }
        return "redirect:/tournaments";
    }

    @PostMapping("/update")
    public String update(@ModelAttribute Tournament tournament, HttpSession session, RedirectAttributes redirectAttributes) {
        User loggedUser = (User) session.getAttribute("loggedUser");
        if (loggedUser == null || !"ADMIN".equalsIgnoreCase(loggedUser.getRole())) return "redirect:/tournaments";
        if (tournament.getStatus() == null || tournament.getStatus().isBlank()) tournament.setStatus("UPCOMING");
        String error = tournamentService.saveTournament(tournament);
        if (error != null) {
            redirectAttributes.addFlashAttribute("error", error);
        }
        return "redirect:/tournaments";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable int id, HttpSession session) {
        User loggedUser = (User) session.getAttribute("loggedUser");
        if (loggedUser != null && "ADMIN".equalsIgnoreCase(loggedUser.getRole())) {
            tournamentService.deleteTournament(id);
        }
        return "redirect:/tournaments";
    }
}