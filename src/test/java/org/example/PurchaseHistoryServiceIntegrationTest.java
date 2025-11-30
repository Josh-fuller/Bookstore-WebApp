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
class PurchaseHistoryServiceIntegrationTest {

    @Autowired
    private PurchaseHistoryService purchaseHistoryService;

    @Autowired
    private PurchaseHistoryRepository purchaseHistoryRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserRepository userRepository;

    private User user;
    private BookInfo book1;
    private BookInfo book2;

    @BeforeEach
    void setUp() {
        purchaseHistoryRepository.deleteAll();
        bookRepository.deleteAll();
        userRepository.deleteAll();

        user = new User("testuser", "password", "test@example.com", "CUSTOMER");
        userRepository.save(user);

        book1 = new BookInfo("Book One", "Fiction", 10.99, "ISBN1", "Author1", "Publisher1", "Desc1", "URL1");
        book2 = new BookInfo("Book Two", "Fiction", 15.99, "ISBN2", "Author2", "Publisher2", "Desc2", "URL2");
        bookRepository.save(book1);
        bookRepository.save(book2);
    }

    @Test
    void getOrCreateHistory() {
        PurchaseHistory history = purchaseHistoryService.getOrCreateHistory(user);

        assertNotNull(history);
        assertEquals(user, history.getUser());
        assertTrue(history.getBooks().isEmpty());
    }

    @Test
    void getOrCreateHistoryReturnsExistingHistory() {
        PurchaseHistory history1 = purchaseHistoryService.getOrCreateHistory(user);
        PurchaseHistory history2 = purchaseHistoryService.getOrCreateHistory(user);

        assertEquals(history1.getId(), history2.getId());
    }

    @Test
    void addBookToHistory() {
        purchaseHistoryService.addBookToHistory(user, book1.getId());

        PurchaseHistory history = purchaseHistoryService.getOrCreateHistory(user);
        assertEquals(1, history.getBooks().size());
        assertTrue(history.getBooks().contains(book1));
    }

    @Test
    void addMultipleBooksToHistory() {
        purchaseHistoryService.addBookToHistory(user, book1.getId());
        purchaseHistoryService.addBookToHistory(user, book2.getId());

        PurchaseHistory history = purchaseHistoryService.getOrCreateHistory(user);
        assertEquals(2, history.getBooks().size());
        assertTrue(history.getBooks().contains(book1));
        assertTrue(history.getBooks().contains(book2));
    }

    @Test
    void removeBookFromHistory() {
        purchaseHistoryService.addBookToHistory(user, book1.getId());
        purchaseHistoryService.addBookToHistory(user, book2.getId());

        purchaseHistoryService.removeBookFromHistory(user, book1.getId());

        PurchaseHistory history = purchaseHistoryService.getOrCreateHistory(user);
        assertEquals(1, history.getBooks().size());
        assertFalse(history.getBooks().contains(book1));
        assertTrue(history.getBooks().contains(book2));
    }

    @Test
    void removeNonExistentBookFromHistory() {
        purchaseHistoryService.addBookToHistory(user, book1.getId());

        purchaseHistoryService.removeBookFromHistory(user, 999L);

        PurchaseHistory history = purchaseHistoryService.getOrCreateHistory(user);
        assertEquals(1, history.getBooks().size());
    }

    @Test
    void saveHistory() {
        PurchaseHistory history = purchaseHistoryService.getOrCreateHistory(user);
        history.addBook(book1);

        purchaseHistoryService.save(history);

        var savedHistory = purchaseHistoryRepository.findByUser(user);
        assertTrue(savedHistory.isPresent());
        assertEquals(1, savedHistory.get().getBooks().size());
    }

    @Test
    void addBookToHistoryThrowsExceptionForNonExistentBook() {
        assertThrows(IllegalArgumentException.class, () -> {
            purchaseHistoryService.addBookToHistory(user, 999L);
        });
    }
}
