package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ContextConfiguration(classes = BookstoreApplication.class)
@ActiveProfiles("test")
class UserRepositoryIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    private User user1;
    private User user2;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        user1 = new User("testuser1", "password1", "test1@example.com", "CUSTOMER");
        user2 = new User("testuser2", "password2", "test2@example.com", "ADMIN");
    }

    @Test
    void findByUsername() {
        entityManager.persist(user1);
        entityManager.flush();

        Optional<User> found = userRepository.findByUsername("testuser1");

        assertTrue(found.isPresent());
        assertEquals("testuser1", found.get().getUsername());
    }

    @Test
    void findByUsernameNotFound() {
        Optional<User> found = userRepository.findByUsername("nonexistent");

        assertFalse(found.isPresent());
    }

    @Test
    void findByEmail() {
        entityManager.persist(user1);
        entityManager.flush();

        Optional<User> found = userRepository.findByEmail("test1@example.com");

        assertTrue(found.isPresent());
        assertEquals("test1@example.com", found.get().getEmail());
    }

    @Test
    void findByEmailNotFound() {
        Optional<User> found = userRepository.findByEmail("nonexistent@example.com");

        assertFalse(found.isPresent());
    }

    @Test
    void existsByUsername() {
        entityManager.persist(user1);
        entityManager.flush();

        assertTrue(userRepository.existsByUsername("testuser1"));
        assertFalse(userRepository.existsByUsername("nonexistent"));
    }

    @Test
    void existsByEmail() {
        entityManager.persist(user1);
        entityManager.flush();

        assertTrue(userRepository.existsByEmail("test1@example.com"));
        assertFalse(userRepository.existsByEmail("nonexistent@example.com"));
    }

    @Test
    void saveUser() {
        User savedUser = userRepository.save(user1);

        assertNotNull(savedUser.getId());
        assertEquals("testuser1", savedUser.getUsername());
    }

    @Test
    void findMultipleUsers() {
        entityManager.persist(user1);
        entityManager.persist(user2);
        entityManager.flush();

        var users = userRepository.findAll();

        assertEquals(2, users.size());
    }
}
