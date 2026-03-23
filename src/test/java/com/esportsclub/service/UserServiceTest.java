package com.esportsclub.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import com.esportsclub.model.User;

public class UserServiceTest {

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService();
    }

    @Test
    void testRegisterWithEmptyUsername() {
        boolean result = userService.register("", "password123", "test@test.com");
        assertFalse(result, "Registration should fail with empty username");
    }

    @Test
    void testRegisterWithShortPassword() {
        boolean result = userService.register("testuser", "123", "test@test.com");
        assertFalse(result, "Registration should fail with short password");
    }

    @Test
    void testRegisterWithInvalidEmail() {
        boolean result = userService.register("testuser", "password123", "invalidemail");
        assertFalse(result, "Registration should fail with invalid email");
    }

    @Test
    void testLoginWithNullValues() {
        User user = userService.login(null, null);
        assertNull(user, "Login should return null with null values");
    }

    @Test
    void testIsAdminWithNullUser() {
        boolean result = userService.isAdmin(null);
        assertFalse(result, "isAdmin should return false for null user");
    }

    @Test
    void testIsAdminWithMemberRole() {
        User user = new User(1, "testuser", "pass123", "test@test.com", "MEMBER", "ACTIVE");
        boolean result = userService.isAdmin(user);
        assertFalse(result, "isAdmin should return false for MEMBER role");
    }

    @Test
    void testIsAdminWithAdminRole() {
        User user = new User(1, "admin", "pass123", "admin@test.com", "ADMIN", "ACTIVE");
        boolean result = userService.isAdmin(user);
        assertTrue(result, "isAdmin should return true for ADMIN role");
    }
}