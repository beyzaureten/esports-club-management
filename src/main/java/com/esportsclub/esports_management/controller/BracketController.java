package com.esportsclub.esports_management.controller;

import com.esportsclub.esports_management.model.Match;
import com.esportsclub.esports_management.model.Team;
import com.esportsclub.esports_management.model.Tournament;
import com.esportsclub.esports_management.service.MatchService;
import com.esportsclub.esports_management.service.TeamService;
import com.esportsclub.esports_management.service.TournamentService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.esportsclub.esports_management.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Controller
public class BracketController {

    private final TournamentService tournamentService;
    private final MatchService matchService;
    private final TeamService teamService;

    public BracketController(TournamentService tournamentService,
                             MatchService matchService,
                             TeamService teamService) {
        this.tournamentService = tournamentService;
        this.matchService = matchService;
        this.teamService = teamService;
    }

    @GetMapping("/tournaments/bracket/{id}")
    public String bracket(@PathVariable int id, Model model, HttpSession session) {
        User loggedUser = (User) session.getAttribute("loggedUser");
        if (loggedUser == null) return "redirect:/login";

        Optional<Tournament> tournamentOpt = tournamentService.getById(id);
        if (tournamentOpt.isEmpty()) return "redirect:/tournaments";

        Tournament tournament = tournamentOpt.get();
        List<Match> matches = matchService.getMatchesByTournament(id);
        List<Team> allTeams = teamService.getAllTeams();

        Map<Integer, String> teamNames = allTeams.stream()
                .collect(Collectors.toMap(Team::getId, Team::getName));

        // Maçları tarihe göre sırala
        matches.sort(Comparator.comparing(
                m -> m.getMatchDate() != null ? m.getMatchDate() : java.time.LocalDate.MIN
        ));

        // Round sayısını hesapla: kaç maç varsa 2'nin kuvvetine göre round belirle
        // 1 maç = Final
        // 2 maç = Semifinal + Final
        // 3-4 maç = Quarterfinal + Semifinal + Final
        // 5-8 maç = Round of 16 + Quarterfinal + Semifinal + Final
        List<List<Match>> rounds = new ArrayList<>();

        if (matches.isEmpty()) {
            model.addAttribute("tournament", tournament);
            model.addAttribute("rounds", rounds);
            model.addAttribute("roundNames", new ArrayList<>());
            model.addAttribute("teamNames", teamNames);
            model.addAttribute("loggedUser", session.getAttribute("loggedUser"));
            return "bracket";
        }

        // Toplam maç sayısına göre kaç round olduğunu hesapla
        int totalMatches = matches.size();
        int totalRounds = (int) Math.ceil(Math.log(totalMatches + 1) / Math.log(2)) + 1;
        if (totalMatches == 1) totalRounds = 1;

        // Maçları round'lara dağıt:
        // Son round'da 1 maç (Final), öncesinde 2 (Semi), öncesinde 4 (Quarter) vs.
        int remaining = totalMatches;
        List<Integer> matchesInRound = new ArrayList<>();

        // Sondan başa round başına maç sayısını belirle
        int roundMatches = 1;
        while (remaining > 0) {
            int take = Math.min(roundMatches, remaining);
            matchesInRound.add(0, take);
            remaining -= take;
            roundMatches *= 2;
        }

        int index = 0;
        for (int count : matchesInRound) {
            rounds.add(new ArrayList<>(matches.subList(index, index + count)));
            index += count;
        }

        // Round isimlerini belirle
        List<String> roundNames = new ArrayList<>();
        int size = rounds.size();
        for (int i = 0; i < size; i++) {
            int fromEnd = size - i;
            if (fromEnd == 1) roundNames.add("Final");
            else if (fromEnd == 2) roundNames.add("Semifinal");
            else if (fromEnd == 3) roundNames.add("Quarterfinal");
            else if (fromEnd == 4) roundNames.add("Round of 16");
            else if (fromEnd == 5) roundNames.add("Round of 32");
            else roundNames.add("Round " + (i + 1));
        }

        model.addAttribute("tournament", tournament);
        model.addAttribute("rounds", rounds);
        model.addAttribute("roundNames", roundNames);
        model.addAttribute("teamNames", teamNames);
        model.addAttribute("loggedUser", session.getAttribute("loggedUser"));
        return "bracket";
    }
}