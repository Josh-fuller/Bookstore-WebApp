package org.example;

import org.junit.jupiter.api.Test;
import org.springframework.test.context.ContextConfiguration;

import static org.junit.jupiter.api.Assertions.*;

@ContextConfiguration(classes = User.class)
class UserTest {

    @Test
    void testUserCreation() {
        User user = new User("testuser", "password123", "test@example.com", "CUSTOMER");

        assertNull(user.getId());
        assertEquals("testuser", user.getUsername());
        assertEquals("password123", user.getPassword());
        assertEquals("test@example.com", user.getEmail());
        assertEquals("CUSTOMER", user.getRole());
    }

    @Test
    void testUserSetters() {
        User user = new User();

        user.setUsername("newuser");
        user.setPassword("newpass");
        user.setEmail("new@example.com");
        user.setRole("ADMIN");

        assertNull(user.getId());
        assertEquals("newuser", user.getUsername());
        assertEquals("newpass", user.getPassword());
        assertEquals("new@example.com", user.getEmail());
        assertEquals("ADMIN", user.getRole());
    }

    @Test
    void testIsAdmin() {
        User adminUser = new User("admin", "pass", "admin@example.com", "ADMIN");
        User customerUser = new User("customer", "pass", "customer@example.com", "CUSTOMER");

        assertTrue(adminUser.isAdmin());
        assertFalse(customerUser.isAdmin());
    }

    @Test
    void testIsCustomer() {
        User adminUser = new User("admin", "pass", "admin@example.com", "ADMIN");
        User customerUser = new User("customer", "pass", "customer@example.com", "CUSTOMER");

        assertFalse(adminUser.isCustomer());
        assertTrue(customerUser.isCustomer());
    }

    @Test
    void testIsAdminCaseInsensitive() {
        User user = new User("user", "pass", "user@example.com", "admin");

        assertTrue(user.isAdmin());
    }

    @Test
    void testIsCustomerCaseInsensitive() {
        User user = new User("user", "pass", "user@example.com", "customer");

        assertTrue(user.isCustomer());
    }
}
