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
class CartServiceIntegrationTest {

    @Autowired
    private CartService cartService;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PurchaseHistoryRepository purchaseHistoryRepository;

    private User user;
    private BookInfo book1;
    private BookInfo book2;

    @BeforeEach
    void setUp() {
        cartRepository.deleteAll();
        bookRepository.deleteAll();
        userRepository.deleteAll();
        purchaseHistoryRepository.deleteAll();

        user = new User("testuser", "password", "test@example.com", "CUSTOMER");
        userRepository.save(user);

        book1 = new BookInfo("Book One", "Fiction", 10.99, "ISBN1", "Author1", "Publisher1", "Desc1", "URL1");
        book2 = new BookInfo("Book Two", "Fiction", 15.99, "ISBN2", "Author2", "Publisher2", "Desc2", "URL2");
        bookRepository.save(book1);
        bookRepository.save(book2);
    }

    @Test
    void getOrCreateCart() {
        Cart cart = cartService.getOrCreateCart(user);

        assertNotNull(cart);
        assertEquals(user, cart.getUser());
        assertTrue(cart.getBooks().isEmpty());
    }

    @Test
    void getOrCreateCartReturnsExistingCart() {
        Cart cart1 = cartService.getOrCreateCart(user);
        Cart cart2 = cartService.getOrCreateCart(user);

        assertEquals(cart1.getId(), cart2.getId());
    }

    @Test
    void getCart() {
        Cart cart = cartService.getCart(user);

        assertNotNull(cart);
        assertEquals(user, cart.getUser());
    }

    @Test
    void addBookToCart() {
        cartService.addBookToCart(user, book1.getId());

        Cart cart = cartService.getCart(user);
        assertEquals(1, cart.getBooks().size());
        assertTrue(cart.getBooks().contains(book1));
    }

    @Test
    void addMultipleBooksToCart() {
        cartService.addBookToCart(user, book1.getId());
        cartService.addBookToCart(user, book2.getId());

        Cart cart = cartService.getCart(user);
        assertEquals(2, cart.getBooks().size());
        assertTrue(cart.getBooks().contains(book1));
        assertTrue(cart.getBooks().contains(book2));
    }

    @Test
    void removeBookFromCart() {
        cartService.addBookToCart(user, book1.getId());
        cartService.addBookToCart(user, book2.getId());

        cartService.removeBookFromCart(user, book1.getId());

        Cart cart = cartService.getCart(user);
        assertEquals(1, cart.getBooks().size());
        assertFalse(cart.getBooks().contains(book1));
        assertTrue(cart.getBooks().contains(book2));
    }

    @Test
    void removeNonExistentBookFromCart() {
        cartService.addBookToCart(user, book1.getId());

        cartService.removeBookFromCart(user, 999L);

        Cart cart = cartService.getCart(user);
        assertEquals(1, cart.getBooks().size());
    }

    @Test
    void checkout() {
        cartService.addBookToCart(user, book1.getId());
        cartService.addBookToCart(user, book2.getId());

        cartService.checkout(user);

        Cart cart = cartService.getCart(user);
        assertTrue(cart.getBooks().isEmpty());

        var historyOpt = purchaseHistoryRepository.findByUser(user);
        assertTrue(historyOpt.isPresent());
        assertEquals(2, historyOpt.get().getBooks().size());
    }

    @Test
    void checkoutEmptyCart() {
        cartService.checkout(user);

        Cart cart = cartService.getCart(user);
        assertTrue(cart.getBooks().isEmpty());
    }

    @Test
    void getCartTotal() {
        cartService.addBookToCart(user, book1.getId());
        cartService.addBookToCart(user, book2.getId());

        double total = cartService.getCartTotal(user);

        assertEquals(26.98, total, 0.01);
    }

    @Test
    void getCartTotalEmptyCart() {
        double total = cartService.getCartTotal(user);

        assertEquals(0.0, total, 0.01);
    }

    @Test
    void addBookToCartThrowsExceptionForNonExistentBook() {
        assertThrows(IllegalArgumentException.class, () -> {
            cartService.addBookToCart(user, 999L);
        });
    }
}
