package com.esportsclub.service;

import com.esportsclub.dao.TournamentDAO;
import com.esportsclub.dao.TournamentTeamDAO;
import com.esportsclub.model.Tournament;
import com.esportsclub.model.TournamentTeam;
import com.esportsclub.util.Logger;

import java.util.List;

public class TournamentService {

    private TournamentDAO tournamentDAO;
    private TournamentTeamDAO tournamentTeamDAO;
    private Logger logger;

    public TournamentService() {
        this.tournamentDAO = new TournamentDAO();
        this.tournamentTeamDAO = new TournamentTeamDAO();
        this.logger = Logger.getInstance();
    }

    // Create new tournament
    public boolean createTournament(Tournament tournament) {
        try {
            if (tournament.getName() == null || tournament.getName().trim().isEmpty()) {
                logger.warning("Tournament creation failed: Name is empty");
                return false;
            }
            if (tournament.getMaxTeams() <= 0) {
                logger.warning("Tournament creation failed: Invalid max teams - " + tournament.getMaxTeams());
                return false;
            }
            if (tournament.getStartDate() == null || tournament.getEndDate() == null) {
                logger.warning("Tournament creation failed: Dates are null");
                return false;
            }
            tournamentDAO.insert(tournament);
            logger.info("Tournament created: " + tournament.getName());
            return true;
        } catch (Exception e) {
            logger.error("Unexpected error creating tournament: " + e.getMessage());
            return false;
        }
    }

    // Update tournament
    public boolean updateTournament(Tournament tournament) {
        try {
            if (tournament.getName() == null || tournament.getName().trim().isEmpty()) {
                logger.warning("Tournament update failed: Name is empty");
                return false;
            }
            tournamentDAO.update(tournament);
            logger.info("Tournament updated: " + tournament.getName());
            return true;
        } catch (Exception e) {
            logger.error("Unexpected error updating tournament: " + e.getMessage());
            return false;
        }
    }

    // Delete tournament
    public void deleteTournament(int id) {
        try {
            tournamentDAO.delete(id);
            logger.info("Tournament deleted. ID: " + id);
        } catch (Exception e) {
            logger.error("Error deleting tournament ID " + id + ": " + e.getMessage());
        }
    }

    // Get tournament by id
    public Tournament getTournamentById(int id) {
        try {
            return tournamentDAO.getById(id);
        } catch (Exception e) {
            logger.error("Error fetching tournament by ID " + id + ": " + e.getMessage());
            return null;
        }
    }

    // Get all tournaments
    public List<Tournament> getAllTournaments() {
        try {
            return tournamentDAO.getAll();
        } catch (Exception e) {
            logger.error("Error fetching all tournaments: " + e.getMessage());
            return null;
        }
    }

    // Get tournaments by status
    public List<Tournament> getTournamentsByStatus(String status) {
        try {
            return tournamentDAO.getByStatus(status);
        } catch (Exception e) {
            logger.error("Error fetching tournaments by status " + status + ": " + e.getMessage());
            return null;
        }
    }

    // Add team to tournament
    public boolean addTeamToTournament(int tournamentId, int teamId, String date) {
        try {
            Tournament tournament = tournamentDAO.getById(tournamentId);
            if (tournament == null) {
                logger.warning("Add team failed: Tournament not found. ID: " + tournamentId);
                return false;
            }
            int currentTeamCount = tournamentTeamDAO.countByTournamentId(tournamentId);
            if (currentTeamCount >= tournament.getMaxTeams()) {
                logger.warning("Add team failed: Tournament is full. ID: " + tournamentId);
                return false;
            }
            TournamentTeam tt = new TournamentTeam(0, tournamentId, teamId, date);
            tournamentTeamDAO.insert(tt);
            logger.info("Team " + teamId + " added to tournament " + tournamentId);
            return true;
        } catch (Exception e) {
            logger.error("Unexpected error adding team to tournament: " + e.getMessage());
            return false;
        }
    }

    // Remove team from tournament
    public void removeTeamFromTournament(int id) {
        try {
            tournamentTeamDAO.delete(id);
            logger.info("Team removed from tournament. Record ID: " + id);
        } catch (Exception e) {
            logger.error("Error removing team from tournament: " + e.getMessage());
        }
    }

    // Get all teams in a tournament
    public List<TournamentTeam> getTeamsInTournament(int tournamentId) {
        try {
            return tournamentTeamDAO.getByTournamentId(tournamentId);
        } catch (Exception e) {
            logger.error("Error fetching teams in tournament " + tournamentId + ": " + e.getMessage());
            return null;
        }
    }

    // Update tournament status
    public void setTournamentStatus(int tournamentId, String status) {
        try {
            Tournament tournament = tournamentDAO.getById(tournamentId);
            if (tournament != null) {
                tournament.setStatus(status);
                tournamentDAO.update(tournament);
                logger.info("Tournament status updated to " + status + " for ID: " + tournamentId);
            } else {
                logger.warning("Tournament not found for status update. ID: " + tournamentId);
            }
        } catch (Exception e) {
            logger.error("Error updating tournament status: " + e.getMessage());
        }
    }
}