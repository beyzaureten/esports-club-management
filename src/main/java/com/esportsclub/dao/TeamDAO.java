package com.esportsclub.dao;

import com.esportsclub.model.Team;
import com.esportsclub.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TeamDAO {

    private Connection connection;

    public TeamDAO() {
        this.connection = DBConnection.getInstance().getConnection();
    }

    public void insert(Team team) {
        String sql = "INSERT INTO teams (name, game_id, max_capacity, status) VALUES (?, ?, ?, ?)";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, team.getName());
            ps.setInt(2, team.getGameId());
            ps.setInt(3, team.getMaxCapacity());
            ps.setString(4, team.getStatus());
            ps.executeUpdate();
            System.out.println("Team added: " + team.getName());
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public void update(Team team) {
        String sql = "UPDATE teams SET name=?, game_id=?, max_capacity=?, status=? WHERE id=?";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, team.getName());
            ps.setInt(2, team.getGameId());
            ps.setInt(3, team.getMaxCapacity());
            ps.setString(4, team.getStatus());
            ps.setInt(5, team.getId());
            ps.executeUpdate();
            System.out.println("Team is updated: " + team.getName());
        } catch (SQLException e) {
            System.out.println("Hata: " + e.getMessage());
        }
    }

    public void delete(int id) {
        String sql = "DELETE FROM teams WHERE id=?";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, id);
            ps.executeUpdate();
            System.out.println("Team is deleted. ID: " + id);
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public Team getById(int id) {
        String sql = "SELECT * FROM teams WHERE id=?";
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

    public List<Team> getAll() {
        List<Team> teams = new ArrayList<>();
        String sql = "SELECT * FROM teams";
        try {
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                teams.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return teams;
    }

    public List<Team> getByGameId(int gameId) {
        List<Team> teams = new ArrayList<>();
        String sql = "SELECT * FROM teams WHERE game_id=?";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, gameId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                teams.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return teams;
    }

    private Team mapResultSet(ResultSet rs) throws SQLException {
        return new Team(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getInt("game_id"),
                rs.getInt("max_capacity"),
                rs.getString("status")
        );
    }
}