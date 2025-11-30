package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void registerUser() {
        User user = userService.registerUser("testuser", "password123", "test@example.com", "CUSTOMER");

        assertNotNull(user);
        assertEquals("testuser", user.getUsername());
        assertEquals("test@example.com", user.getEmail());
        assertEquals("CUSTOMER", user.getRole());
        assertNotEquals("password123", user.getPassword());
    }

    @Test
    void registerUserWithDuplicateUsername() {
        userService.registerUser("testuser", "password123", "test@example.com", "CUSTOMER");

        User duplicate = userService.registerUser("testuser", "password456", "another@example.com", "CUSTOMER");

        assertNull(duplicate);
    }

    @Test
    void registerUserWithDuplicateEmail() {
        userService.registerUser("testuser", "password123", "test@example.com", "CUSTOMER");

        User duplicate = userService.registerUser("anotheruser", "password456", "test@example.com", "CUSTOMER");

        assertNull(duplicate);
    }

    @Test
    void authenticateUser() {
        userService.registerUser("testuser", "password123", "test@example.com", "CUSTOMER");

        User authenticated = userService.authenticateUser("testuser", "password123");

        assertNotNull(authenticated);
        assertEquals("testuser", authenticated.getUsername());
    }

    @Test
    void authenticateUserWithWrongPassword() {
        userService.registerUser("testuser", "password123", "test@example.com", "CUSTOMER");

        User authenticated = userService.authenticateUser("testuser", "wrongpassword");

        assertNull(authenticated);
    }

    @Test
    void authenticateNonExistentUser() {
        User authenticated = userService.authenticateUser("nonexistent", "password123");

        assertNull(authenticated);
    }

    @Test
    void findByUsername() {
        userService.registerUser("testuser", "password123", "test@example.com", "CUSTOMER");

        var foundUser = userService.findByUsername("testuser");

        assertTrue(foundUser.isPresent());
        assertEquals("testuser", foundUser.get().getUsername());
    }

    @Test
    void findByUsernameNotFound() {
        var foundUser = userService.findByUsername("nonexistent");

        assertFalse(foundUser.isPresent());
    }

    @Test
    void usernameExists() {
        userService.registerUser("testuser", "password123", "test@example.com", "CUSTOMER");

        assertTrue(userService.usernameExists("testuser"));
        assertFalse(userService.usernameExists("nonexistent"));
    }

    @Test
    void emailExists() {
        userService.registerUser("testuser", "password123", "test@example.com", "CUSTOMER");

        assertTrue(userService.emailExists("test@example.com"));
        assertFalse(userService.emailExists("nonexistent@example.com"));
    }

    @Test
    void registerAdminUser() {
        User admin = userService.registerUser("adminuser", "password123", "admin@example.com", "ADMIN");

        assertNotNull(admin);
        assertEquals("ADMIN", admin.getRole());
        assertTrue(admin.isAdmin());
    }
}
