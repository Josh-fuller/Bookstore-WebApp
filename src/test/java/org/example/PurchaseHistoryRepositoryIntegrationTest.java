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
class PurchaseHistoryRepositoryIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private PurchaseHistoryRepository purchaseHistoryRepository;

    private User user1;
    private User user2;
    private PurchaseHistory history1;
    private PurchaseHistory history2;

    @BeforeEach
    void setUp() {
        purchaseHistoryRepository.deleteAll();

        user1 = new User("testuser1", "password1", "test1@example.com", "CUSTOMER");
        user2 = new User("testuser2", "password2", "test2@example.com", "CUSTOMER");
        entityManager.persist(user1);
        entityManager.persist(user2);

        history1 = new PurchaseHistory(user1);
        history2 = new PurchaseHistory(user2);
    }

    @Test
    void findByUser() {
        entityManager.persist(history1);
        entityManager.flush();

        Optional<PurchaseHistory> found = purchaseHistoryRepository.findByUser(user1);

        assertTrue(found.isPresent());
        assertEquals(user1, found.get().getUser());
    }

    @Test
    void findByUserNotFound() {
        User newUser = new User("newuser", "pass", "new@example.com", "CUSTOMER");
        entityManager.persist(newUser);
        entityManager.flush();

        Optional<PurchaseHistory> found = purchaseHistoryRepository.findByUser(newUser);

        assertFalse(found.isPresent());
    }

    @Test
    void savePurchaseHistory() {
        PurchaseHistory savedHistory = purchaseHistoryRepository.save(history1);

        assertNotNull(savedHistory.getId());
        assertEquals(user1, savedHistory.getUser());
    }

    @Test
    void findMultipleHistories() {
        entityManager.persist(history1);
        entityManager.persist(history2);
        entityManager.flush();

        var histories = purchaseHistoryRepository.findAll();

        assertEquals(2, histories.size());
    }

    @Test
    void deletePurchaseHistory() {
        entityManager.persist(history1);
        entityManager.flush();

        purchaseHistoryRepository.delete(history1);

        Optional<PurchaseHistory> found = purchaseHistoryRepository.findByUser(user1);
        assertFalse(found.isPresent());
    }
}
