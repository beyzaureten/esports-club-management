package com.esportsclub.esports_management;

import com.esportsclub.esports_management.service.PasswordService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PasswordServiceTest {

    private PasswordService passwordService;

    @BeforeEach
    void setUp() {
        passwordService = new PasswordService();
    }

    @Test
    void TC_P01_hashIsDeterministic() {
        String hash1 = passwordService.hashPassword("test123");
        String hash2 = passwordService.hashPassword("test123");
        assertEquals(hash1, hash2, "Same password should produce identical hashes");
    }

    @Test
    void TC_P02_correctPasswordCheck() {
        String stored = passwordService.hashPassword("test123");
        assertTrue(passwordService.checkPassword("test123", stored),
                "Correct password should return true");
    }

    @Test
    void TC_P03_wrongPasswordCheck() {
        String stored = passwordService.hashPassword("test123");
        assertFalse(passwordService.checkPassword("wrong", stored),
                "Wrong password should return false");
    }

    @Test
    void TC_P04_hashLengthIs64() {
        String hash = passwordService.hashPassword("admin123");
        assertEquals(64, hash.length(), "SHA-256 hash should be 64 characters long");
    }
}