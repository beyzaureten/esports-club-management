package com.esportsclub.service;

import com.esportsclub.dao.MatchDAO;
import com.esportsclub.model.Match;
import com.esportsclub.util.Logger;

import java.util.List;

public class MatchService {

    private MatchDAO matchDAO;
    private Logger logger;

    public MatchService() {
        this.matchDAO = new MatchDAO();
        this.logger = Logger.getInstance();
    }

    // Create new match
    public boolean createMatch(Match match) {
        try {
            if (match.getTournamentId() <= 0) {
                logger.warning("Match creation failed: Invalid tournament ID - " + match.getTournamentId());
                return false;
            }
            if (match.getTeam1Id() == match.getTeam2Id()) {
                logger.warning("Match creation failed: A team cannot play against itself");
                return false;
            }
            if (match.getMatchDate() == null || match.getMatchDate().trim().isEmpty()) {
                logger.warning("Match creation failed: Match date is empty");
                return false;
            }
            matchDAO.insert(match);
            logger.info("Match created between team " + match.getTeam1Id() + " and team " + match.getTeam2Id());
            return true;
        } catch (Exception e) {
            logger.error("Unexpected error creating match: " + e.getMessage());
            return false;
        }
    }

    // Enter match result
    public boolean enterResult(int matchId, int winnerId, int team1Score, int team2Score) {
        try {
            Match match = matchDAO.getById(matchId);
            if (match == null) {
                logger.warning("Enter result failed: Match not found. ID: " + matchId);
                return false;
            }
            if (team1Score < 0 || team2Score < 0) {
                logger.warning("Enter result failed: Scores cannot be negative");
                return false;
            }
            if (winnerId != match.getTeam1Id() && winnerId != match.getTeam2Id()) {
                logger.warning("Enter result failed: Winner ID " + winnerId + " is not part of match " + matchId);
                return false;
            }
            match.setWinnerId(winnerId);
            match.setTeam1Score(team1Score);
            match.setTeam2Score(team2Score);
            match.setStatus("FINISHED");
            matchDAO.update(match);
            logger.info("Result entered for match " + matchId + ". Winner: team " + winnerId);
            return true;
        } catch (Exception e) {
            logger.error("Unexpected error entering result: " + e.getMessage());
            return false;
        }
    }

    // Get match by id
    public Match getMatchById(int id) {
        try {
            return matchDAO.getById(id);
        } catch (Exception e) {
            logger.error("Error fetching match by ID " + id + ": " + e.getMessage());
            return null;
        }
    }

    // Get all matches
    public List<Match> getAllMatches() {
        try {
            return matchDAO.getAll();
        } catch (Exception e) {
            logger.error("Error fetching all matches: " + e.getMessage());
            return null;
        }
    }

    // Get matches by tournament
    public List<Match> getMatchesByTournament(int tournamentId) {
        try {
            return matchDAO.getByTournamentId(tournamentId);
        } catch (Exception e) {
            logger.error("Error fetching matches for tournament " + tournamentId + ": " + e.getMessage());
            return null;
        }
    }

    // Delete match
    public void deleteMatch(int id) {
        try {
            matchDAO.delete(id);
            logger.info("Match deleted. ID: " + id);
        } catch (Exception e) {
            logger.error("Error deleting match ID " + id + ": " + e.getMessage());
        }
    }

    // Update match status
    public void setMatchStatus(int matchId, String status) {
        try {
            Match match = matchDAO.getById(matchId);
            if (match != null) {
                match.setStatus(status);
                matchDAO.update(match);
                logger.info("Match status updated to " + status + " for ID: " + matchId);
            } else {
                logger.warning("Match not found for status update. ID: " + matchId);
            }
        } catch (Exception e) {
            logger.error("Error updating match status: " + e.getMessage());
        }
    }

    // Check if match is finished
    public boolean isMatchFinished(int matchId) {
        try {
            Match match = matchDAO.getById(matchId);
            if (match == null) {
                logger.warning("Match not found. ID: " + matchId);
                return false;
            }
            return match.getStatus().equals("FINISHED");
        } catch (Exception e) {
            logger.error("Error checking match status. ID: " + matchId + ": " + e.getMessage());
            return false;
        }
    }
}