package com.esportsclub.service;

import com.esportsclub.dao.MatchDAO;
import com.esportsclub.dao.TeamDAO;
import com.esportsclub.dao.TournamentDAO;
import com.esportsclub.dao.UserDAO;
import com.esportsclub.model.Match;
import com.esportsclub.model.Team;
import com.esportsclub.model.Tournament;
import com.esportsclub.model.User;
import com.esportsclub.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReportManager {

    private Connection connection;
    private TeamDAO teamDAO;
    private UserDAO userDAO;
    private TournamentDAO tournamentDAO;
    private MatchDAO matchDAO;

    public ReportManager() {
        this.connection = DBConnection.getInstance().getConnection();
        this.teamDAO = new TeamDAO();
        this.userDAO = new UserDAO();
        this.tournamentDAO = new TournamentDAO();
        this.matchDAO = new MatchDAO();
    }

    // En çok oyuncusu olan takımı getir
    public Team getMostPopularTeam() {
        String sql = "SELECT team_id, COUNT(*) as member_count " +
                "FROM team_members GROUP BY team_id " +
                "ORDER BY member_count DESC LIMIT 1";
        try {
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery(sql);
            if (rs.next()) {
                int teamId = rs.getInt("team_id");
                return teamDAO.getById(teamId);
            }
        } catch (SQLException e) {
            System.out.println("Hata: " + e.getMessage());
        }
        return null;
    }

    // En çok kazanan takımı getir
    public Team getMostWinningTeam() {
        String sql = "SELECT winner_id, COUNT(*) as win_count " +
                "FROM matches WHERE winner_id != 0 " +
                "GROUP BY winner_id ORDER BY win_count DESC LIMIT 1";
        try {
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery(sql);
            if (rs.next()) {
                int teamId = rs.getInt("winner_id");
                return teamDAO.getById(teamId);
            }
        } catch (SQLException e) {
            System.out.println("Hata: " + e.getMessage());
        }
        return null;
    }

    // Tüm aktif kullanıcıları getir
    public List<User> getActiveUsers() {
        String sql = "SELECT * FROM users WHERE status='ACTIVE'";
        List<User> users = new ArrayList<>();
        try {
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                users.add(new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("email"),
                        rs.getString("role"),
                        rs.getString("status")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Hata: " + e.getMessage());
        }
        return users;
    }

    // Toplam maç sayısını getir
    public int getTotalMatchCount() {
        String sql = "SELECT COUNT(*) as total FROM matches";
        try {
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery(sql);
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            System.out.println("Hata: " + e.getMessage());
        }
        return 0;
    }

    // Bitmiş turnuvaları getir
    public List<Tournament> getFinishedTournaments() {
        return tournamentDAO.getByStatus("FINISHED");
    }

    // Bir takımın tüm maçlarını getir
    public List<Match> getMatchesByTeam(int teamId) {
        List<Match> matches = new ArrayList<>();
        String sql = "SELECT * FROM matches WHERE team1_id=? OR team2_id=?";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, teamId);
            ps.setInt(2, teamId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                matches.add(new Match(
                        rs.getInt("id"),
                        rs.getInt("tournament_id"),
                        rs.getInt("team1_id"),
                        rs.getInt("team2_id"),
                        rs.getInt("winner_id"),
                        rs.getInt("team1_score"),
                        rs.getInt("team2_score"),
                        rs.getString("match_date"),
                        rs.getString("status")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Hata: " + e.getMessage());
        }
        return matches;
    }
}