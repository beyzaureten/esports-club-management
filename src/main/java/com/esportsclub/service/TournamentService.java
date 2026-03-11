package com.esportsclub.service;

import com.esportsclub.dao.TournamentDAO;
import com.esportsclub.dao.TournamentTeamDAO;
import com.esportsclub.model.Tournament;
import com.esportsclub.model.TournamentTeam;

import java.util.List;

public class TournamentService {

    private TournamentDAO tournamentDAO;
    private TournamentTeamDAO tournamentTeamDAO;

    public TournamentService() {
        this.tournamentDAO = new TournamentDAO();
        this.tournamentTeamDAO = new TournamentTeamDAO();
    }

    // Create new tournament
    public boolean createTournament(Tournament tournament) {
        if (tournament.getName() == null || tournament.getName().trim().isEmpty()) {
            System.out.println("Error: Tournament name cannot be empty!");
            return false;
        }
        if (tournament.getMaxTeams() <= 0) {
            System.out.println("Error: Max teams must be greater than 0!");
            return false;
        }
        if (tournament.getStartDate() == null || tournament.getEndDate() == null) {
            System.out.println("Error: Start and end dates cannot be empty!");
            return false;
        }
        tournamentDAO.insert(tournament);
        return true;
    }

    // Update tournament
    public boolean updateTournament(Tournament tournament) {
        if (tournament.getName() == null || tournament.getName().trim().isEmpty()) {
            System.out.println("Error: Tournament name cannot be empty!");
            return false;
        }
        tournamentDAO.update(tournament);
        return true;
    }

    // Delete tournament
    public void deleteTournament(int id) {
        tournamentDAO.delete(id);
    }

    // Get tournament by id
    public Tournament getTournamentById(int id) {
        return tournamentDAO.getById(id);
    }

    // Get all tournaments
    public List<Tournament> getAllTournaments() {
        return tournamentDAO.getAll();
    }

    // Get tournaments by status
    public List<Tournament> getTournamentsByStatus(String status) {
        return tournamentDAO.getByStatus(status);
    }

    // Add team to tournament
    public boolean addTeamToTournament(int tournamentId, int teamId, String date) {
        Tournament tournament = tournamentDAO.getById(tournamentId);
        if (tournament == null) {
            System.out.println("Error: Tournament not found!");
            return false;
        }

        // Check if tournament is full
        int currentTeamCount = tournamentTeamDAO.countByTournamentId(tournamentId);
        if (currentTeamCount >= tournament.getMaxTeams()) {
            System.out.println("Error: Tournament is full!");
            return false;
        }

        TournamentTeam tt = new TournamentTeam(0, tournamentId, teamId, date);
        tournamentTeamDAO.insert(tt);
        return true;
    }

    // Remove team from tournament
    public void removeTeamFromTournament(int id) {
        tournamentTeamDAO.delete(id);
    }

    // Get all teams in a tournament
    public List<TournamentTeam> getTeamsInTournament(int tournamentId) {
        return tournamentTeamDAO.getByTournamentId(tournamentId);
    }

    // Update tournament status
    public void setTournamentStatus(int tournamentId, String status) {
        Tournament tournament = tournamentDAO.getById(tournamentId);
        if (tournament != null) {
            tournament.setStatus(status);
            tournamentDAO.update(tournament);
            System.out.println("Tournament status updated: " + status);
        }
    }
}