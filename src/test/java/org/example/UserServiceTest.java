package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private PurchaseHistoryRepository purchaseHistoryRepository;

    @BeforeEach
    void setUp() {
        cartRepository.deleteAll();
        purchaseHistoryRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void registerUser_success() {
        User user = userService.registerUser("testuser", "password123", "test@example.com", "CUSTOMER");
        
        assertNotNull(user);
        assertEquals("testuser", user.getUsername());
        assertEquals("test@example.com", user.getEmail());
        assertEquals("CUSTOMER", user.getRole());
        assertNotEquals("password123", user.getPassword());
    }

    @Test
    void registerUser_duplicateUsername() {
        userService.registerUser("testuser", "password123", "test@example.com", "CUSTOMER");
        User duplicate = userService.registerUser("testuser", "password456", "different@example.com", "CUSTOMER");
        
        assertNull(duplicate);
    }

    @Test
    void registerUser_duplicateEmail() {
        userService.registerUser("testuser", "password123", "test@example.com", "CUSTOMER");
        User duplicate = userService.registerUser("different", "password123", "test@example.com", "CUSTOMER");
        
        assertNull(duplicate);
    }

    @Test
    void authenticateUser_validCredentials() {
        userService.registerUser("testuser", "password123", "test@example.com", "CUSTOMER");
        
        User authenticated = userService.authenticateUser("testuser", "password123");
        
        assertNotNull(authenticated);
        assertEquals("testuser", authenticated.getUsername());
    }

    @Test
    void authenticateUser_invalidPassword() {
        userService.registerUser("testuser", "password123", "test@example.com", "CUSTOMER");
        
        User authenticated = userService.authenticateUser("testuser", "wrongpassword");
        
        assertNull(authenticated);
    }

    @Test
    void authenticateUser_nonexistentUser() {
        User authenticated = userService.authenticateUser("nonexistent", "password123");
        
        assertNull(authenticated);
    }

    @Test
    void findByUsername_exists() {
        userService.registerUser("testuser", "password123", "test@example.com", "CUSTOMER");
        
        Optional<User> found = userService.findByUsername("testuser");
        
        assertTrue(found.isPresent());
        assertEquals("testuser", found.get().getUsername());
    }

    @Test
    void findByUsername_notExists() {
        Optional<User> found = userService.findByUsername("nonexistent");
        
        assertFalse(found.isPresent());
    }

    @Test
    void usernameExists_true() {
        userService.registerUser("testuser", "password123", "test@example.com", "CUSTOMER");
        
        assertTrue(userService.usernameExists("testuser"));
    }

    @Test
    void usernameExists_false() {
        assertFalse(userService.usernameExists("nonexistent"));
    }

    @Test
    void emailExists_true() {
        userService.registerUser("testuser", "password123", "test@example.com", "CUSTOMER");
        
        assertTrue(userService.emailExists("test@example.com"));
    }

    @Test
    void emailExists_false() {
        assertFalse(userService.emailExists("nonexistent@example.com"));
    }

    @Test
    void passwordIsHashed() {
        User user = userService.registerUser("testuser", "password123", "test@example.com", "CUSTOMER");
        
        assertNotEquals("password123", user.getPassword());
        assertTrue(user.getPassword().length() > 20);
    }

    @Test
    void registerAdmin() {
        User admin = userService.registerUser("adminuser", "password123", "admin@example.com", "ADMIN");
        
        assertNotNull(admin);
        assertEquals("ADMIN", admin.getRole());
        assertTrue(admin.isAdmin());
        assertFalse(admin.isCustomer());
    }

    @Test
    void registerCustomer() {
        User customer = userService.registerUser("customeruser", "password123", "customer@example.com", "CUSTOMER");
        
        assertNotNull(customer);
        assertEquals("CUSTOMER", customer.getRole());
        assertTrue(customer.isCustomer());
        assertFalse(customer.isAdmin());
    }
}
