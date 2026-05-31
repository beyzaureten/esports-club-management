package com.esportsclub.esports_management.controller;

import com.esportsclub.esports_management.model.Match;
import com.esportsclub.esports_management.model.User;
import com.esportsclub.esports_management.service.MatchService;
import com.esportsclub.esports_management.service.TeamService;
import com.esportsclub.esports_management.service.TournamentService;
import jakarta.servlet.http.HttpSession;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/matches")
public class MatchController {

    private final MatchService matchService;
    private final TeamService teamService;
    private final TournamentService tournamentService;

    public MatchController(MatchService matchService,
                           TeamService teamService,
                           TournamentService tournamentService) {
        this.matchService = matchService;
        this.teamService = teamService;
        this.tournamentService = tournamentService;
    }

    @GetMapping
    public String list(Model model, HttpSession session) {
        User loggedUser = (User) session.getAttribute("loggedUser");

        if (loggedUser == null) {
            return "redirect:/login";
        }

        matchService.updatePastMatchesAutomatically();

        Map<Integer, String> teamNames = new HashMap<>();
        teamService.getAllTeams().forEach(team -> teamNames.put(team.getId(), team.getName()));

        Map<Integer, String> tournamentNames = new HashMap<>();
        tournamentService.getAllTournaments().forEach(tournament -> tournamentNames.put(tournament.getId(), tournament.getName()));

        model.addAttribute("matches", matchService.getAllMatches());
        model.addAttribute("teams", teamService.getAllTeams());
        model.addAttribute("tournaments", tournamentService.getAllTournaments());
        model.addAttribute("teamMap", teamNames);
        model.addAttribute("tournamentMap", tournamentNames);
        model.addAttribute("loggedUser", loggedUser);
        model.addAttribute("match", new Match());

        return "matches";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute Match match,
                       HttpSession session,
                       RedirectAttributes redirectAttributes) {

        User loggedUser = (User) session.getAttribute("loggedUser");

        if (loggedUser == null || !"ADMIN".equals(loggedUser.getRole())) {
            return "redirect:/login";
        }

        String error = matchService.saveMatchWithValidation(match);

        if (error != null) {
            redirectAttributes.addFlashAttribute("error", error);
            return "redirect:/matches";
        }

        return "redirect:/matches";
    }

    @PostMapping("/update-info")
    public String updateInfo(@RequestParam int id,
                             @RequestParam int tournamentId,
                             @RequestParam int team1Id,
                             @RequestParam int team2Id,
                             @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate matchDate,
                             @RequestParam String status,
                             HttpSession session,
                             RedirectAttributes redirectAttributes) {

        User loggedUser = (User) session.getAttribute("loggedUser");

        if (loggedUser == null || !"ADMIN".equals(loggedUser.getRole())) {
            return "redirect:/login";
        }

        Match updatedMatch = new Match();
        updatedMatch.setId(id);
        updatedMatch.setTournamentId(tournamentId);
        updatedMatch.setTeam1Id(team1Id);
        updatedMatch.setTeam2Id(team2Id);
        updatedMatch.setMatchDate(matchDate);
        updatedMatch.setStatus(status);

        String error = matchService.updateMatchInfo(updatedMatch);

        if (error != null) {
            redirectAttributes.addFlashAttribute("error", error);
            return "redirect:/matches";
        }

        return "redirect:/matches";
    }

    @PostMapping("/update-score")
    public String updateScore(@RequestParam int id,
                              @RequestParam int team1Score,
                              @RequestParam int team2Score,
                              HttpSession session,
                              RedirectAttributes redirectAttributes) {

        User loggedUser = (User) session.getAttribute("loggedUser");

        if (loggedUser == null || !"ADMIN".equals(loggedUser.getRole())) {
            return "redirect:/login";
        }

        if (team1Score < 0 || team2Score < 0) {
            redirectAttributes.addFlashAttribute("error", "Scores cannot be negative.");
            return "redirect:/matches";
        }

        matchService.getMatchById(id).ifPresent(match -> {
            match.setTeam1Score(team1Score);
            match.setTeam2Score(team2Score);

            if (team1Score > team2Score) {
                match.setWinnerId(match.getTeam1Id());
            } else if (team2Score > team1Score) {
                match.setWinnerId(match.getTeam2Id());
            } else {
                match.setWinnerId(null);
            }

            match.setStatus("FINISHED");
            matchService.saveMatch(match);
        });

        return "redirect:/matches";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable int id, HttpSession session) {
        User loggedUser = (User) session.getAttribute("loggedUser");

        if (loggedUser != null && "ADMIN".equals(loggedUser.getRole())) {
            matchService.deleteMatch(id);
        }

        return "redirect:/matches";
    }

    @PostMapping("/finish/{id}")
    public String finishMatch(@PathVariable int id, HttpSession session) {
        User loggedUser = (User) session.getAttribute("loggedUser");

        if (loggedUser == null || !"ADMIN".equals(loggedUser.getRole())) {
            return "redirect:/login";
        }

        matchService.getMatchById(id).ifPresent(match -> {
            match.setStatus("FINISHED");
            matchService.saveMatch(match);
        });

        return "redirect:/matches";
    }
}