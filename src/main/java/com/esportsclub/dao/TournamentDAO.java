package com.esportsclub.dao;

import com.esportsclub.model.Tournament;
import com.esportsclub.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TournamentDAO {

    private Connection connection;

    public TournamentDAO() {
        this.connection = DBConnection.getInstance().getConnection();
    }

    public void insert(Tournament tournament) {
        String sql = "INSERT INTO tournaments (name, game_id, max_teams, start_date, end_date, status) VALUES (?, ?, ?, ?, ?, ?)";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, tournament.getName());
            ps.setInt(2, tournament.getGameId());
            ps.setInt(3, tournament.getMaxTeams());
            ps.setString(4, tournament.getStartDate());
            ps.setString(5, tournament.getEndDate());
            ps.setString(6, tournament.getStatus());
            ps.executeUpdate();
            System.out.println("Tournament added: " + tournament.getName());
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public void update(Tournament tournament) {
        String sql = "UPDATE tournaments SET name=?, game_id=?, max_teams=?, start_date=?, end_date=?, status=? WHERE id=?";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, tournament.getName());
            ps.setInt(2, tournament.getGameId());
            ps.setInt(3, tournament.getMaxTeams());
            ps.setString(4, tournament.getStartDate());
            ps.setString(5, tournament.getEndDate());
            ps.setString(6, tournament.getStatus());
            ps.setInt(7, tournament.getId());
            ps.executeUpdate();
            System.out.println("Tournament is updated: " + tournament.getName());
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public void delete(int id) {
        String sql = "DELETE FROM tournaments WHERE id=?";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, id);
            ps.executeUpdate();
            System.out.println("Tournament is deleted. ID: " + id);
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public Tournament getById(int id) {
        String sql = "SELECT * FROM tournaments WHERE id=?";
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

    public List<Tournament> getAll() {
        List<Tournament> tournaments = new ArrayList<>();
        String sql = "SELECT * FROM tournaments";
        try {
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                tournaments.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return tournaments;
    }

    public List<Tournament> getByStatus(String status) {
        List<Tournament> tournaments = new ArrayList<>();
        String sql = "SELECT * FROM tournaments WHERE status=?";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, status);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                tournaments.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return tournaments;
    }

    private Tournament mapResultSet(ResultSet rs) throws SQLException {
        return new Tournament(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getInt("game_id"),
                rs.getInt("max_teams"),
                rs.getString("start_date"),
                rs.getString("end_date"),
                rs.getString("status")
        );
    }
}