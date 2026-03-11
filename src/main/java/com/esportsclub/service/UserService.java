package com.esportsclub.service;

import com.esportsclub.dao.UserDAO;
import com.esportsclub.model.User;

import java.util.List;

public class UserService {

    private UserDAO userDAO;

    public UserService() {
        this.userDAO = new UserDAO();
    }

    // Register new user
    public boolean register(String username, String password, String email) {
        if (username == null || username.trim().isEmpty()) {
            System.out.println("Error: Username cannot be empty!");
            return false;
        }
        if (password == null || password.length() < 6) {
            System.out.println("Error: Password must be at least 6 characters!");
            return false;
        }
        if (email == null || !email.contains("@")) {
            System.out.println("Error: Invalid email address!");
            return false;
        }

        User user = new User(0, username, password, email, "MEMBER", "ACTIVE");
        userDAO.insert(user);
        return true;
    }

    // Login
    public User login(String username, String password) {
        if (username == null || password == null) {
            System.out.println("Error: Username or password cannot be empty!");
            return null;
        }
        User user = userDAO.login(username, password);
        if (user == null) {
            System.out.println("Error: Invalid username or password!");
        }
        return user;
    }

    // Check if user is admin
    public boolean isAdmin(User user) {
        return user != null && user.getRole().equals("ADMIN");
    }

    // Get all users
    public List<User> getAllUsers() {
        return userDAO.getAll();
    }

    // Get user by id
    public User getUserById(int id) {
        return userDAO.getById(id);
    }

    // Update user
    public boolean updateUser(User user) {
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            System.out.println("Error: Username cannot be empty!");
            return false;
        }
        userDAO.update(user);
        return true;
    }

    // Delete user
    public void deleteUser(int id) {
        userDAO.delete(id);
    }

    // Activate or deactivate user
    public void setUserStatus(int userId, String status) {
        User user = userDAO.getById(userId);
        if (user != null) {
            user.setStatus(status);
            userDAO.update(user);
            System.out.println("User status updated: " + status);
        }
    }

    // Change user role
    public void setUserRole(int userId, String role) {
        User user = userDAO.getById(userId);
        if (user != null) {
            user.setRole(role);
            userDAO.update(user);
            System.out.println("User role updated: " + role);
        }
    }
}