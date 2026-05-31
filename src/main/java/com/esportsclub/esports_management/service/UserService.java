package com.esportsclub.esports_management.service;

import com.esportsclub.esports_management.event.MemberRegisteredEvent;
import com.esportsclub.esports_management.model.User;
import com.esportsclub.esports_management.repository.UserRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordService passwordService;
    private final ApplicationEventPublisher eventPublisher;

    public UserService(UserRepository userRepository,
                       PasswordService passwordService,
                       ApplicationEventPublisher eventPublisher) {
        this.userRepository = userRepository;
        this.passwordService = passwordService;
        this.eventPublisher = eventPublisher;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> login(String username, String password) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) return Optional.empty();
        User user = userOpt.get();
        // Case-sensitive kullanıcı adı kontrolü
        if (!user.getUsername().equals(username)) return Optional.empty();
        if (!"ACTIVE".equalsIgnoreCase(user.getStatus())) return Optional.empty();
        if (passwordService.checkPassword(password, user.getPassword())) return Optional.of(user);
        return Optional.empty();
    }

    public String getLoginError(String username, String password) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) return "Invalid username or password.";
        User user = userOpt.get();
        // Case-sensitive kullanıcı adı kontrolü
        if (!user.getUsername().equals(username)) return "Invalid username or password.";
        if (!"ACTIVE".equalsIgnoreCase(user.getStatus()))
            return "Your account is inactive. Please contact an administrator.";
        if (!passwordService.checkPassword(password, user.getPassword()))
            return "Invalid username or password.";
        return null;
    }

    public void saveUser(User user) {
        user.setPassword(passwordService.hashPassword(user.getPassword()));
        if (user.getStatus() == null) user.setStatus("ACTIVE");
        userRepository.save(user);
        eventPublisher.publishEvent(new MemberRegisteredEvent(this, user));
    }

    // Şifre hashleme olmadan güncelleme için kullanılır.
    public void updateUser(User user) {
        userRepository.save(user);
    }

    public List<User> getUsersByRole(String role) {
        return userRepository.findByRole(role);
    }

    public List<User> getUsersByTeamName(String teamName) {
        return userRepository.findByTeamName(teamName);
    }

    public List<User> getMembersByTeamName(String teamName) {
        return userRepository.findByTeamNameAndRole(teamName, "MEMBER");
    }

    public void deleteUser(int id) {
        userRepository.deleteById(id);
    }
}