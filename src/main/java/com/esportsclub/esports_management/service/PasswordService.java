package com.esportsclub.esports_management.service;

import org.springframework.stereotype.Service;
import java.security.MessageDigest;

@Service
public class PasswordService {

    public String hashPassword(String plainPassword) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedPassword = md.digest(plainPassword.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashedPassword) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("Password hashing failed.", e);
        }
    }

    public boolean checkPassword(String plainPassword, String storedPassword) {
        try {
            String hashed = hashPassword(plainPassword);
            return hashed.equals(storedPassword);
        } catch (Exception e) {
            return false;
        }
    }
}