package com.esportsclub.dao;

import com.esportsclub.model.User;
import com.esportsclub.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    private Connection connection;

    public UserDAO() {
        this.connection = DBConnection.getInstance().getConnection();
    }

    // Yeni kullanıcı ekle
    public void insert(User user) {
        String sql = "INSERT INTO users (username, password, email, role, status) VALUES (?, ?, ?, ?, ?)";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getEmail());
            ps.setString(4, user.getRole());
            ps.setString(5, user.getStatus());
            ps.executeUpdate();
            System.out.println("Kullanıcı eklendi: " + user.getUsername());
        } catch (SQLException e) {
            System.out.println("Hata: " + e.getMessage());
        }
    }

    // Kullanıcı güncelle
    public void update(User user) {
        String sql = "UPDATE users SET username=?, password=?, email=?, role=?, status=? WHERE id=?";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getEmail());
            ps.setString(4, user.getRole());
            ps.setString(5, user.getStatus());
            ps.setInt(6, user.getId());
            ps.executeUpdate();
            System.out.println("Kullanıcı güncellendi: " + user.getUsername());
        } catch (SQLException e) {
            System.out.println("Hata: " + e.getMessage());
        }
    }

    // Kullanıcı sil
    public void delete(int id) {
        String sql = "DELETE FROM users WHERE id=?";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, id);
            ps.executeUpdate();
            System.out.println("Kullanıcı silindi. ID: " + id);
        } catch (SQLException e) {
            System.out.println("Hata: " + e.getMessage());
        }
    }

    // ID ile kullanıcı getir
    public User getById(int id) {
        String sql = "SELECT * FROM users WHERE id=?";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapResultSet(rs);
            }
        } catch (SQLException e) {
            System.out.println("Hata: " + e.getMessage());
        }
        return null;
    }

    // Tüm kullanıcıları getir
    public List<User> getAll() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users";
        try {
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                users.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            System.out.println("Hata: " + e.getMessage());
        }
        return users;
    }

    // Kullanıcı adı ve şifre ile giriş kontrolü
    public User login(String username, String password) {
        String sql = "SELECT * FROM users WHERE username=? AND password=?";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapResultSet(rs);
            }
        } catch (SQLException e) {
            System.out.println("Hata: " + e.getMessage());
        }
        return null;
    }

    // ResultSet'i User nesnesine çevir
    private User mapResultSet(ResultSet rs) throws SQLException {
        return new User(
                rs.getInt("id"),
                rs.getString("username"),
                rs.getString("password"),
                rs.getString("email"),
                rs.getString("role"),
                rs.getString("status")
        );
    }
}