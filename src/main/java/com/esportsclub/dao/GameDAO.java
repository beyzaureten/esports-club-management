package com.esportsclub.dao;

import com.esportsclub.model.Game;
import com.esportsclub.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GameDAO {

    private Connection connection;

    public GameDAO() {
        this.connection = DBConnection.getInstance().getConnection();
    }

    public void insert(Game game) {
        String sql = "INSERT INTO games (name, genre, mode) VALUES (?, ?, ?)";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, game.getName());
            ps.setString(2, game.getGenre());
            ps.setString(3, game.getMode());
            ps.executeUpdate();
            System.out.println("Game added: " + game.getName());
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public void update(Game game) {
        String sql = "UPDATE games SET name=?, genre=?, mode=? WHERE id=?";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, game.getName());
            ps.setString(2, game.getGenre());
            ps.setString(3, game.getMode());
            ps.setInt(4, game.getId());
            ps.executeUpdate();
            System.out.println("Game updated: " + game.getName());
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public void delete(int id) {
        String sql = "DELETE FROM games WHERE id=?";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, id);
            ps.executeUpdate();
            System.out.println("Game deleted. ID: " + id);
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public Game getById(int id) {
        String sql = "SELECT * FROM games WHERE id=?";
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

    public List<Game> getAll() {
        List<Game> games = new ArrayList<>();
        String sql = "SELECT * FROM games";
        try {
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                games.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return games;
    }

    private Game mapResultSet(ResultSet rs) throws SQLException {
        return new Game(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("genre"),
                rs.getString("mode")
        );
    }
}