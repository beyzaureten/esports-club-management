package com.esportsclub.service;

import com.esportsclub.dao.TeamMemberDAO;
import com.esportsclub.dao.TeamDAO;
import com.esportsclub.model.Team;

import java.util.List;

public class TeamService {

    private TeamDAO teamDAO;

    public TeamService() {
        this.teamDAO = new TeamDAO();
    }

    // Add new team
    public boolean addTeam(Team team) {
        if (team.getName() == null || team.getName().trim().isEmpty()) {
            System.out.println("Error: Team name cannot be empty!");
            return false;
        }
        if (team.getMaxCapacity() <= 0) {
            System.out.println("Error: Capacity must be greater than 0!");
            return false;
        }
        teamDAO.insert(team);
        return true;
    }

    // Update team
    public boolean updateTeam(Team team) {
        if (team.getName() == null || team.getName().trim().isEmpty()) {
            System.out.println("Error: Team name cannot be empty!");
            return false;
        }
        teamDAO.update(team);
        return true;
    }

    // Delete team
    public void deleteTeam(int id) {
        teamDAO.delete(id);
    }

    // Get team by id
    public Team getTeamById(int id) {
        return teamDAO.getById(id);
    }

    // Get all teams
    public List<Team> getAllTeams() {
        return teamDAO.getAll();
    }

    // Get teams by game
    public List<Team> getTeamsByGame(int gameId) {
        return teamDAO.getByGameId(gameId);
    }

    // Check if team is full
    public boolean isTeamFull(int teamId) {
        Team team = teamDAO.getById(teamId);
        if (team == null) {
            System.out.println("Error: Team not found!");
            return true;
        }
        TeamMemberDAO teamMemberDAO = new TeamMemberDAO();
        int currentCount = teamMemberDAO.countByTeamId(teamId);
        return currentCount >= team.getMaxCapacity();
    }

    // Set team status
    public void setTeamStatus(int teamId, String status) {
        Team team = teamDAO.getById(teamId);
        if (team != null) {
            team.setStatus(status);
            teamDAO.update(team);
            System.out.println("Team status updated: " + status);
        }
    }
}