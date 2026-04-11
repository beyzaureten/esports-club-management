package com.esportsclub.service;

import com.esportsclub.dao.TeamDAO;
import com.esportsclub.dao.TeamMemberDAO;
import com.esportsclub.model.Team;

import java.util.List;

public class TeamService {

    private TeamDAO teamDAO;

    public TeamService() {
        this.teamDAO = new TeamDAO();
    }

    public boolean addTeam(Team team) {
        if (team.getName() == null || team.getName().trim().isEmpty()) {
            System.out.println("Hata: Takım adı boş olamaz!");
            return false;
        }
        if (team.getMaxCapacity() <= 0) {
            System.out.println("Hata: Kapasite 0'dan büyük olmalı!");
            return false;
        }
        teamDAO.insert(team);
        return true;
    }

    public boolean updateTeam(Team team) {
        if (team.getName() == null || team.getName().trim().isEmpty()) {
            System.out.println("Hata: Takım adı boş olamaz!");
            return false;
        }
        teamDAO.update(team);
        return true;
    }

    public void deleteTeam(int id) {
        teamDAO.delete(id);
    }

    public Team getTeamById(int id) {
        return teamDAO.getById(id);
    }

    public List<Team> getAllTeams() {
        return teamDAO.getAll();
    }

    public List<Team> getTeamsByGame(int gameId) {
        return teamDAO.getByGameId(gameId);
    }

    public boolean isTeamFull(int teamId) {
        Team team = teamDAO.getById(teamId);
        if (team == null) {
            System.out.println("Hata: Takım bulunamadı!");
            return true;
        }
        return false;
    }

    public void setTeamStatus(int teamId, String status) {
        Team team = teamDAO.getById(teamId);
        if (team != null) {
            team.setStatus(status);
            teamDAO.update(team);
            System.out.println("Takım durumu güncellendi: " + status);
        }
    }
}