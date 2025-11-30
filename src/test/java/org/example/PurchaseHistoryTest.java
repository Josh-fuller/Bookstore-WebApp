package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ContextConfiguration;

import static org.junit.jupiter.api.Assertions.*;

@ContextConfiguration(classes = PurchaseHistory.class)
class PurchaseHistoryTest {

    private PurchaseHistory history;
    private User user;
    private BookInfo book1;
    private BookInfo book2;

    @BeforeEach
    void setUp() {
        user = new User("testuser", "password", "test@example.com", "CUSTOMER");
        history = new PurchaseHistory(user);

        book1 = new BookInfo("Book One", "Fiction", 10.99, "ISBN1", "Author1", "Publisher1", "Desc1", "URL1");
        book2 = new BookInfo("Book Two", "Fiction", 15.99, "ISBN2", "Author2", "Publisher2", "Desc2", "URL2");
    }

    @Test
    void testPurchaseHistoryCreation() {
        assertNull(history.getId());
        assertEquals(user, history.getUser());
        assertTrue(history.getBooks().isEmpty());
    }

    @Test
    void testEmptyPurchaseHistoryCreation() {
        PurchaseHistory emptyHistory = new PurchaseHistory();

        assertNull(emptyHistory.getId());
        assertNull(emptyHistory.getUser());
        assertTrue(emptyHistory.getBooks().isEmpty());
    }

    @Test
    void testAddBook() {
        history.addBook(book1);

        assertEquals(1, history.getBooks().size());
        assertTrue(history.getBooks().contains(book1));
    }

    @Test
    void testAddMultipleBooks() {
        history.addBook(book1);
        history.addBook(book2);

        assertEquals(2, history.getBooks().size());
        assertTrue(history.getBooks().contains(book1));
        assertTrue(history.getBooks().contains(book2));
    }

    @Test
    void testRemoveBook() {
        history.addBook(book1);
        history.addBook(book2);

        history.removeBook(book1);

        assertEquals(1, history.getBooks().size());
        assertFalse(history.getBooks().contains(book1));
        assertTrue(history.getBooks().contains(book2));
    }

    @Test
    void testSetUser() {
        PurchaseHistory newHistory = new PurchaseHistory();
        User newUser = new User("newuser", "pass", "new@example.com", "CUSTOMER");

        newHistory.setUser(newUser);

        assertEquals(newUser, newHistory.getUser());
    }

    @Test
    void testSetBooks() {
        PurchaseHistory newHistory = new PurchaseHistory(user);
        java.util.Set<BookInfo> bookSet = new java.util.LinkedHashSet<>();
        bookSet.add(book1);
        bookSet.add(book2);

        newHistory.setBooks(bookSet);

        assertEquals(2, newHistory.getBooks().size());
        assertTrue(newHistory.getBooks().contains(book1));
        assertTrue(newHistory.getBooks().contains(book2));
    }
}
