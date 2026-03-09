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

    // Yeni takım ekle
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

    // Takım güncelle
    public boolean updateTeam(Team team) {
        if (team.getName() == null || team.getName().trim().isEmpty()) {
            System.out.println("Hata: Takım adı boş olamaz!");
            return false;
        }
        teamDAO.update(team);
        return true;
    }

    // Takım sil
    public void deleteTeam(int id) {
        teamDAO.delete(id);
    }

    // ID ile takım getir
    public Team getTeamById(int id) {
        return teamDAO.getById(id);
    }

    // Tüm takımları getir
    public List<Team> getAllTeams() {
        return teamDAO.getAll();
    }

    // Oyuna göre takımları getir
    public List<Team> getTeamsByGame(int gameId) {
        return teamDAO.getByGameId(gameId);
    }

    // Takım kapasitesi dolu mu kontrolü
    public boolean isTeamFull(int teamId) {
        Team team = teamDAO.getById(teamId);
        if (team == null) {
            System.out.println("Hata: Takım bulunamadı!");
            return true;
        }
        // TeamMemberDAO hazır olunca burası güncellenecek
        // Şimdilik false döndürüyoruz
        return false;
    }

    // Takımı aktif/pasif yap
    public void setTeamStatus(int teamId, String status) {
        Team team = teamDAO.getById(teamId);
        if (team != null) {
            team.setStatus(status);
            teamDAO.update(team);
            System.out.println("Takım durumu güncellendi: " + status);
        }
    }
}