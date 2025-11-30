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
class CartServiceTest {

    @Autowired
    private CartService cartService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private PurchaseHistoryRepository purchaseHistoryRepository;

    private User user;
    private BookInfo book1;
    private BookInfo book2;

    @BeforeEach
    void setUp() {
        cartRepository.deleteAll();
        purchaseHistoryRepository.deleteAll();
        bookRepository.deleteAll();
        userRepository.deleteAll();

        user = new User("testuser", "password123", "test@example.com", "CUSTOMER");
        userRepository.save(user);

        book1 = new BookInfo("Book One", "Fiction", 10.99,
                "1111111111", "Author One", "Publisher One",
                "Description One", "http://example.com/cover1.jpg");
        book2 = new BookInfo("Book Two", "Fiction", 15.99,
                "2222222222", "Author Two", "Publisher Two",
                "Description Two", "http://example.com/cover2.jpg");
        
        bookRepository.save(book1);
        bookRepository.save(book2);
    }

    @Test
    void getOrCreateCart_createsNewCart() {
        Cart cart = cartService.getOrCreateCart(user);
        
        assertNotNull(cart);
        assertEquals(user, cart.getUser());
        assertTrue(cart.getBooks().isEmpty());
    }

    @Test
    void getOrCreateCart_returnsExistingCart() {
        Cart firstCart = cartService.getOrCreateCart(user);
        Cart secondCart = cartService.getOrCreateCart(user);
        
        assertEquals(firstCart.getId(), secondCart.getId());
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
    void getCartTotal() {
        cartService.addBookToCart(user, book1.getId());
        cartService.addBookToCart(user, book2.getId());
        
        double total = cartService.getCartTotal(user);
        assertEquals(26.98, total, 0.01);
    }

    @Test
    void checkout_movesItemsToPurchaseHistory() {
        cartService.addBookToCart(user, book1.getId());
        cartService.addBookToCart(user, book2.getId());
        
        cartService.checkout(user);
        
        Cart cart = cartService.getCart(user);
        assertTrue(cart.getBooks().isEmpty());
    }

    @Test
    void checkout_withEmptyCart() {
        cartService.checkout(user);
        
        Cart cart = cartService.getCart(user);
        assertTrue(cart.getBooks().isEmpty());
    }
}
