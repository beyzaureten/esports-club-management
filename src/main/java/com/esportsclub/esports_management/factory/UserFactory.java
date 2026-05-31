package com.esportsclub.esports_management.factory;

import com.esportsclub.esports_management.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserFactory {

    public User createUser(String username, String email, String password, String role) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(password);
        user.setRole(role);
        user.setStatus("ACTIVE");
        user.setTempPassword(false);

        // ADMIN'e takim atanamaz
        if ("ADMIN".equalsIgnoreCase(role)) {
            user.setTeamName(null);
        }

        return user;
    }

    public User createMember(String username, String email, String password) {
        return createUser(username, email, password, "MEMBER");
    }

    public User createCoach(String username, String email, String password) {
        return createUser(username, email, password, "COACH");
    }

    public User createAdmin(String username, String email, String password) {
        return createUser(username, email, password, "ADMIN");
    }
}