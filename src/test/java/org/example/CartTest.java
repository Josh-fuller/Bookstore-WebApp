package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ContextConfiguration;

import static org.junit.jupiter.api.Assertions.*;

@ContextConfiguration(classes = Cart.class)
class CartTest {

    private Cart cart;
    private User user;
    private BookInfo book1;
    private BookInfo book2;

    @BeforeEach
    void setUp() {
        user = new User("testuser", "password", "test@example.com", "CUSTOMER");
        cart = new Cart(user);

        book1 = new BookInfo("Book One", "Fiction", 10.99, "ISBN1", "Author1", "Publisher1", "Desc1", "URL1");
        book2 = new BookInfo("Book Two", "Fiction", 15.99, "ISBN2", "Author2", "Publisher2", "Desc2", "URL2");
    }

    @Test
    void testCartCreation() {
        assertNull(cart.getId());
        assertEquals(user, cart.getUser());
        assertTrue(cart.getBooks().isEmpty());
    }

    @Test
    void testEmptyCartCreation() {
        Cart emptyCart = new Cart();

        assertNull(emptyCart.getId());
        assertNull(emptyCart.getUser());
        assertTrue(emptyCart.getBooks().isEmpty());
    }

    @Test
    void testAddBook() {
        cart.addBook(book1);

        assertEquals(1, cart.getBooks().size());
        assertTrue(cart.getBooks().contains(book1));
    }

    @Test
    void testAddMultipleBooks() {
        cart.addBook(book1);
        cart.addBook(book2);

        assertEquals(2, cart.getBooks().size());
        assertTrue(cart.getBooks().contains(book1));
        assertTrue(cart.getBooks().contains(book2));
    }

    @Test
    void testRemoveBook() {
        cart.addBook(book1);
        cart.addBook(book2);

        cart.removeBook(book1);

        assertEquals(1, cart.getBooks().size());
        assertFalse(cart.getBooks().contains(book1));
        assertTrue(cart.getBooks().contains(book2));
    }

    @Test
    void testSetUser() {
        Cart newCart = new Cart();
        User newUser = new User("newuser", "pass", "new@example.com", "CUSTOMER");

        newCart.setUser(newUser);

        assertEquals(newUser, newCart.getUser());
    }

    @Test
    void testSetBooks() {
        Cart newCart = new Cart(user);
        java.util.Set<BookInfo> bookSet = new java.util.LinkedHashSet<>();
        bookSet.add(book1);
        bookSet.add(book2);

        newCart.setBooks(bookSet);

        assertEquals(2, newCart.getBooks().size());
        assertTrue(newCart.getBooks().contains(book1));
        assertTrue(newCart.getBooks().contains(book2));
    }
}
