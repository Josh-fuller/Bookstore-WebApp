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
class CartRepositoryIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CartRepository cartRepository;

    private User user1;
    private User user2;
    private Cart cart1;
    private Cart cart2;

    @BeforeEach
    void setUp() {
        cartRepository.deleteAll();

        user1 = new User("testuser1", "password1", "test1@example.com", "CUSTOMER");
        user2 = new User("testuser2", "password2", "test2@example.com", "CUSTOMER");
        entityManager.persist(user1);
        entityManager.persist(user2);

        cart1 = new Cart(user1);
        cart2 = new Cart(user2);
    }

    @Test
    void findByUser() {
        entityManager.persist(cart1);
        entityManager.flush();

        Optional<Cart> found = cartRepository.findByUser(user1);

        assertTrue(found.isPresent());
        assertEquals(user1, found.get().getUser());
    }

    @Test
    void findByUserNotFound() {
        User newUser = new User("newuser", "pass", "new@example.com", "CUSTOMER");
        entityManager.persist(newUser);
        entityManager.flush();

        Optional<Cart> found = cartRepository.findByUser(newUser);

        assertFalse(found.isPresent());
    }

    @Test
    void saveCart() {
        Cart savedCart = cartRepository.save(cart1);

        assertNotNull(savedCart.getId());
        assertEquals(user1, savedCart.getUser());
    }

    @Test
    void findMultipleCarts() {
        entityManager.persist(cart1);
        entityManager.persist(cart2);
        entityManager.flush();

        var carts = cartRepository.findAll();

        assertEquals(2, carts.size());
    }

    @Test
    void deleteCart() {
        entityManager.persist(cart1);
        entityManager.flush();

        cartRepository.delete(cart1);

        Optional<Cart> found = cartRepository.findByUser(user1);
        assertFalse(found.isPresent());
    }
}
