package com.esportsclub.service;

import com.esportsclub.dao.UserDAO;
import com.esportsclub.model.User;
import com.esportsclub.util.Logger;

import java.util.List;

public class UserService {

    private UserDAO userDAO;
    private Logger logger;

    public UserService() {
        this.userDAO = new UserDAO();
        this.logger = Logger.getInstance();
    }

    // Register new user
    public boolean register(String username, String password, String email) {
        try {
            if (username == null || username.trim().isEmpty()) {
                logger.warning("Registration failed: Username is empty");
                return false;
            }
            if (password == null || password.length() < 6) {
                logger.warning("Registration failed: Password too short for user - " + username);
                return false;
            }
            if (email == null || !email.contains("@")) {
                logger.warning("Registration failed: Invalid email for user - " + username);
                return false;
            }
            User user = new User(0, username, password, email, "MEMBER", "ACTIVE");
            userDAO.insert(user);
            logger.info("New user registered: " + username);
            return true;
        } catch (Exception e) {
            logger.error("Unexpected error during registration: " + e.getMessage());
            return false;
        }
    }

    // Login
    public User login(String username, String password) {
        try {
            if (username == null || password == null) {
                logger.warning("Login failed: Username or password is null");
                return null;
            }
            User user = userDAO.login(username, password);
            if (user == null) {
                logger.warning("Login failed: Invalid credentials for - " + username);
            } else {
                logger.info("User logged in: " + username);
            }
            return user;
        } catch (Exception e) {
            logger.error("Unexpected error during login: " + e.getMessage());
            return null;
        }
    }

    // Check if user is admin
    public boolean isAdmin(User user) {
        return user != null && user.getRole().equals("ADMIN");
    }

    // Get all users
    public List<User> getAllUsers() {
        try {
            return userDAO.getAll();
        } catch (Exception e) {
            logger.error("Error fetching all users: " + e.getMessage());
            return null;
        }
    }

    // Get user by id
    public User getUserById(int id) {
        try {
            return userDAO.getById(id);
        } catch (Exception e) {
            logger.error("Error fetching user by ID " + id + ": " + e.getMessage());
            return null;
        }
    }

    // Update user
    public boolean updateUser(User user) {
        try {
            if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
                logger.warning("Update failed: Username is empty");
                return false;
            }
            userDAO.update(user);
            logger.info("User updated: " + user.getUsername());
            return true;
        } catch (Exception e) {
            logger.error("Error updating user: " + e.getMessage());
            return false;
        }
    }

    // Delete user
    public void deleteUser(int id) {
        try {
            userDAO.delete(id);
            logger.info("User deleted. ID: " + id);
        } catch (Exception e) {
            logger.error("Error deleting user ID " + id + ": " + e.getMessage());
        }
    }

    // Activate or deactivate user
    public void setUserStatus(int userId, String status) {
        try {
            User user = userDAO.getById(userId);
            if (user != null) {
                user.setStatus(status);
                userDAO.update(user);
                logger.info("User status updated to " + status + " for user ID: " + userId);
            } else {
                logger.warning("User not found for status update. ID: " + userId);
            }
        } catch (Exception e) {
            logger.error("Error updating user status: " + e.getMessage());
        }
    }

    // Change user role
    public void setUserRole(int userId, String role) {
        try {
            User user = userDAO.getById(userId);
            if (user != null) {
                user.setRole(role);
                userDAO.update(user);
                logger.info("User role updated to " + role + " for user ID: " + userId);
            } else {
                logger.warning("User not found for role update. ID: " + userId);
            }
        } catch (Exception e) {
            logger.error("Error updating user role: " + e.getMessage());
        }
    }
}