package com.esportsclub.esports_management.service;

import com.esportsclub.esports_management.model.Match;
import com.esportsclub.esports_management.model.Team;
import com.esportsclub.esports_management.model.Tournament;
import com.esportsclub.esports_management.repository.MatchRepository;
import com.esportsclub.esports_management.repository.TeamRepository;
import com.esportsclub.esports_management.repository.TournamentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class MatchService {

    private final MatchRepository matchRepository;
    private final TournamentRepository tournamentRepository;
    private final TeamRepository teamRepository;

    public MatchService(MatchRepository matchRepository,
                        TournamentRepository tournamentRepository,
                        TeamRepository teamRepository) {
        this.matchRepository = matchRepository;
        this.tournamentRepository = tournamentRepository;
        this.teamRepository = teamRepository;
    }

    public List<Match> getAllMatches() {
        return matchRepository.findAll();
    }

    public Optional<Match> getMatchById(int id) {
        return matchRepository.findById(id);
    }

    public List<Match> getMatchesByTournament(int tournamentId) {
        return matchRepository.findByTournamentId(tournamentId);
    }

    public String saveMatchWithValidation(Match match) {
        String error = validateAndPrepareMatch(match);
        if (error != null) return error;
        matchRepository.save(match);
        return null;
    }

    public String updateMatchInfo(Match updatedMatch) {
        Optional<Match> existingOpt = matchRepository.findById(updatedMatch.getId());
        if (existingOpt.isEmpty()) return "Match not found.";

        Match existingMatch = existingOpt.get();
        existingMatch.setTournamentId(updatedMatch.getTournamentId());
        existingMatch.setTeam1Id(updatedMatch.getTeam1Id());
        existingMatch.setTeam2Id(updatedMatch.getTeam2Id());
        existingMatch.setMatchDate(updatedMatch.getMatchDate());
        existingMatch.setStatus(updatedMatch.getStatus());

        String error = validateAndPrepareMatch(existingMatch);
        if (error != null) return error;

        matchRepository.save(existingMatch);
        return null;
    }

    public void saveMatch(Match match) {
        matchRepository.save(match);
    }

    public void deleteMatch(int id) {
        matchRepository.deleteById(id);
    }

    public void updatePastMatchesAutomatically() {
        LocalDate today = LocalDate.now();
        List<Match> matches = matchRepository.findAll();
        for (Match match : matches) {
            // Sadece PENDING maçları kontrol et, ONGOING'e dokunma
            if (match.getMatchDate() != null
                    && match.getMatchDate().isBefore(today)
                    && "PENDING".equalsIgnoreCase(match.getStatus())) {
                match.setStatus("FINISHED");
                matchRepository.save(match);
            }
        }
    }

    private String validateAndPrepareMatch(Match match) {
        if (match.getTournamentId() == null) {
            return "Please select a tournament.";
        }

        if (match.getTeam1Id() == null || match.getTeam2Id() == null) {
            return "Please select both teams.";
        }

        if (match.getTeam1Id().equals(match.getTeam2Id())) {
            return "Team 1 and Team 2 cannot be the same.";
        }

        if (match.getMatchDate() == null) {
            return "Please select a match date.";
        }

        // INACTIVE takım kontrolü
        Optional<Team> team1Opt = teamRepository.findById(match.getTeam1Id());
        Optional<Team> team2Opt = teamRepository.findById(match.getTeam2Id());

        if (team1Opt.isPresent() && "INACTIVE".equalsIgnoreCase(team1Opt.get().getStatus())) {
            return "Team 1 (" + team1Opt.get().getName() + ") is inactive and cannot participate in matches.";
        }

        if (team2Opt.isPresent() && "INACTIVE".equalsIgnoreCase(team2Opt.get().getStatus())) {
            return "Team 2 (" + team2Opt.get().getName() + ") is inactive and cannot participate in matches.";
        }

        Optional<Tournament> tournamentOpt = tournamentRepository.findById(match.getTournamentId());
        if (tournamentOpt.isEmpty()) {
            return "Selected tournament could not be found.";
        }

        Tournament tournament = tournamentOpt.get();

        // FINISHED turnuvaya yeni maç eklenemez
        if ("FINISHED".equalsIgnoreCase(tournament.getStatus())) {
            return "Cannot add a match to a finished tournament.";
        }

        if (tournament.getStartDate() != null
                && match.getMatchDate().isBefore(tournament.getStartDate())) {
            return "Match date cannot be before the tournament start date.";
        }

        if (tournament.getEndDate() != null
                && match.getMatchDate().isAfter(tournament.getEndDate())) {
            return "Match date cannot be after the tournament end date.";
        }

        if (match.getStatus() == null || match.getStatus().isBlank()) {
            match.setStatus("PENDING");
        }

        if (match.getTeam1Score() == null) match.setTeam1Score(0);
        if (match.getTeam2Score() == null) match.setTeam2Score(0);

        return null;
    }
}