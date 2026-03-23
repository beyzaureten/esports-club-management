package com.esportsclub.dao;

import com.esportsclub.model.Game;
import com.esportsclub.model.Team;
import com.esportsclub.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReportDAO {

    private Connection connection;

    public ReportDAO() {
        this.connection = DBConnection.getInstance().getConnection();
    }

    // Get team with most members
    public Team getMostPopularTeam() {
        String sql = "SELECT t.*, COUNT(tm.id) as member_count " +
                "FROM teams t LEFT JOIN team_members tm ON t.id = tm.team_id " +
                "GROUP BY t.id ORDER BY member_count DESC LIMIT 1";
        try {
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery(sql);
            if (rs.next()) {
                return new Team(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getInt("game_id"),
                        rs.getInt("max_capacity"),
                        rs.getString("status")
                );
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return null;
    }

    // Get most played game
    public Game getMostPlayedGame() {
        String sql = "SELECT g.*, COUNT(t.id) as team_count " +
                "FROM games g LEFT JOIN teams t ON g.id = t.game_id " +
                "GROUP BY g.id ORDER BY team_count DESC LIMIT 1";
        try {
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery(sql);
            if (rs.next()) {
                return new Game(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("genre"),
                        rs.getString("mode")
                );
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return null;
    }

    // Get win counts for all teams
    public List<String> getTeamWinCounts() {
        List<String> results = new ArrayList<>();
        String sql = "SELECT t.name, COUNT(m.winner_id) as wins " +
                "FROM teams t LEFT JOIN matches m ON t.id = m.winner_id " +
                "GROUP BY t.id ORDER BY wins DESC";
        try {
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                results.add(rs.getString("name") + " - Wins: " + rs.getInt("wins"));
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return results;
    }

    // Get total number of users
    public int getTotalUserCount() {
        String sql = "SELECT COUNT(*) as total FROM users";
        try {
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery(sql);
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return 0;
    }

    // Get total number of tournaments
    public int getTotalTournamentCount() {
        String sql = "SELECT COUNT(*) as total FROM tournaments";
        try {
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery(sql);
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return 0;
    }

    // Get total number of matches
    public int getTotalMatchCount() {
        String sql = "SELECT COUNT(*) as total FROM matches";
        try {
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery(sql);
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return 0;
    }
}