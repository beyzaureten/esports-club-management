package com.esportsclub.dao;

import com.esportsclub.model.Match;
import com.esportsclub.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MatchDAO {

    private Connection connection;

    public MatchDAO() {
        this.connection = DBConnection.getInstance().getConnection();
    }

    // Add new match
    public void insert(Match match) {
        String sql = "INSERT INTO matches (tournament_id, team1_id, team2_id, winner_id, team1_score, team2_score, match_date, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, match.getTournamentId());
            ps.setInt(2, match.getTeam1Id());
            ps.setInt(3, match.getTeam2Id());
            ps.setInt(4, match.getWinnerId());
            ps.setInt(5, match.getTeam1Score());
            ps.setInt(6, match.getTeam2Score());
            ps.setString(7, match.getMatchDate());
            ps.setString(8, match.getStatus());
            ps.executeUpdate();
            System.out.println("Match added.");
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // Update match
    public void update(Match match) {
        String sql = "UPDATE matches SET winner_id=?, team1_score=?, team2_score=?, status=? WHERE id=?";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, match.getWinnerId());
            ps.setInt(2, match.getTeam1Score());
            ps.setInt(3, match.getTeam2Score());
            ps.setString(4, match.getStatus());
            ps.setInt(5, match.getId());
            ps.executeUpdate();
            System.out.println("Match updated. ID: " + match.getId());
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // Delete match
    public void delete(int id) {
        String sql = "DELETE FROM matches WHERE id=?";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, id);
            ps.executeUpdate();
            System.out.println("Match deleted. ID: " + id);
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // Get match by id
    public Match getById(int id) {
        String sql = "SELECT * FROM matches WHERE id=?";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapResultSet(rs);
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return null;
    }

    // Get all matches
    public List<Match> getAll() {
        List<Match> matches = new ArrayList<>();
        String sql = "SELECT * FROM matches";
        try {
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                matches.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return matches;
    }

    // Get matches by tournament
    public List<Match> getByTournamentId(int tournamentId) {
        List<Match> matches = new ArrayList<>();
        String sql = "SELECT * FROM matches WHERE tournament_id=?";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, tournamentId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                matches.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return matches;
    }

    // Map ResultSet to Match object
    private Match mapResultSet(ResultSet rs) throws SQLException {
        return new Match(
                rs.getInt("id"),
                rs.getInt("tournament_id"),
                rs.getInt("team1_id"),
                rs.getInt("team2_id"),
                rs.getInt("winner_id"),
                rs.getInt("team1_score"),
                rs.getInt("team2_score"),
                rs.getString("match_date"),
                rs.getString("status")
        );
    }
}