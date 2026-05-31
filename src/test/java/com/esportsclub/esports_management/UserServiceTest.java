package com.esportsclub.esports_management;

import com.esportsclub.esports_management.event.MemberRegisteredEvent;
import com.esportsclub.esports_management.model.User;
import com.esportsclub.esports_management.repository.UserRepository;
import com.esportsclub.esports_management.service.PasswordService;
import com.esportsclub.esports_management.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordService passwordService;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1);
        testUser.setUsername("testuser");
        testUser.setPassword("plaintext123");
        testUser.setEmail("test@esports.com");
        testUser.setRole("MEMBER");
        testUser.setStatus("ACTIVE");
    }

    @Test
    void TC_U01_passwordIsHashedOnSave() {
        when(passwordService.hashPassword("plaintext123")).thenReturn("hashedvalue");
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        userService.saveUser(testUser);

        assertEquals("hashedvalue", testUser.getPassword(),
                "Password should be hashed before saving");
    }

    @Test
    void TC_U02_eventIsPublishedOnSave() {
        when(passwordService.hashPassword(any())).thenReturn("hashed");
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        userService.saveUser(testUser);

        verify(eventPublisher, times(1)).publishEvent(any(MemberRegisteredEvent.class));
    }

    @Test
    void TC_U03_statusDefaultsToActiveOnSave() {
        testUser.setStatus(null);
        when(passwordService.hashPassword(any())).thenReturn("hashed");
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        userService.saveUser(testUser);

        assertEquals("ACTIVE", testUser.getStatus(),
                "Status should default to ACTIVE when null");
    }

    @Test
    void TC_U04_loginSucceedsWithCorrectCredentials() {
        testUser.setPassword("hashed123");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(passwordService.checkPassword("plaintext123", "hashed123")).thenReturn(true);

        Optional<User> result = userService.login("testuser", "plaintext123");

        assertTrue(result.isPresent(), "Login should succeed with correct credentials");
    }

    @Test
    void TC_U05_loginFailsWithWrongPassword() {
        testUser.setPassword("hashed123");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(passwordService.checkPassword("wrongpass", "hashed123")).thenReturn(false);

        Optional<User> result = userService.login("testuser", "wrongpass");

        assertFalse(result.isPresent(), "Login should fail with wrong password");
    }

    @Test
    void TC_U06_loginFailsWithNonExistentUser() {
        when(userRepository.findByUsername("nobody")).thenReturn(Optional.empty());

        Optional<User> result = userService.login("nobody", "anypass");

        assertFalse(result.isPresent(), "Login should fail for non-existent user");
    }
}