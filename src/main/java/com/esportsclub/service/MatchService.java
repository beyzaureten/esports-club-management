package com.esportsclub.service;

import com.esportsclub.dao.MatchDAO;
import com.esportsclub.model.Match;

import java.util.List;

public class MatchService {

    private MatchDAO matchDAO;

    public MatchService() {
        this.matchDAO = new MatchDAO();
    }

    // Create new match
    public boolean createMatch(Match match) {
        if (match.getTournamentId() <= 0) {
            System.out.println("Error: Tournament ID is invalid!");
            return false;
        }
        if (match.getTeam1Id() == match.getTeam2Id()) {
            System.out.println("Error: A team cannot play against itself!");
            return false;
        }
        if (match.getMatchDate() == null || match.getMatchDate().trim().isEmpty()) {
            System.out.println("Error: Match date cannot be empty!");
            return false;
        }
        matchDAO.insert(match);
        return true;
    }

    // Enter match result
    public boolean enterResult(int matchId, int winnerId, int team1Score, int team2Score) {
        Match match = matchDAO.getById(matchId);
        if (match == null) {
            System.out.println("Error: Match not found!");
            return false;
        }
        if (team1Score < 0 || team2Score < 0) {
            System.out.println("Error: Scores cannot be negative!");
            return false;
        }
        if (winnerId != match.getTeam1Id() && winnerId != match.getTeam2Id()) {
            System.out.println("Error: Winner must be one of the two teams!");
            return false;
        }

        match.setWinnerId(winnerId);
        match.setTeam1Score(team1Score);
        match.setTeam2Score(team2Score);
        match.setStatus("FINISHED");
        matchDAO.update(match);
        return true;
    }

    // Get match by id
    public Match getMatchById(int id) {
        return matchDAO.getById(id);
    }

    // Get all matches
    public List<Match> getAllMatches() {
        return matchDAO.getAll();
    }

    // Get matches by tournament
    public List<Match> getMatchesByTournament(int tournamentId) {
        return matchDAO.getByTournamentId(tournamentId);
    }

    // Delete match
    public void deleteMatch(int id) {
        matchDAO.delete(id);
    }

    // Update match status
    public void setMatchStatus(int matchId, String status) {
        Match match = matchDAO.getById(matchId);
        if (match != null) {
            match.setStatus(status);
            matchDAO.update(match);
            System.out.println("Match status updated: " + status);
        }
    }

    // Check if match is finished
    public boolean isMatchFinished(int matchId) {
        Match match = matchDAO.getById(matchId);
        if (match == null) return false;
        return match.getStatus().equals("FINISHED");
    }
}   