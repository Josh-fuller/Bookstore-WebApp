package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.mock.web.MockHttpSession;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class CartControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private PurchaseHistoryRepository purchaseHistoryRepository;

    private User customer;
    private User admin;
    private BookInfo book;

    @BeforeEach
    void setUp() {
        cartRepository.deleteAll();
        purchaseHistoryRepository.deleteAll();
        bookRepository.deleteAll();
        userRepository.deleteAll();

        customer = new User("customer", "password", "customer@example.com", "CUSTOMER");
        admin = new User("admin", "password", "admin@example.com", "ADMIN");
        userRepository.save(customer);
        userRepository.save(admin);

        book = new BookInfo("Test Book", "Fiction", 19.99, "ISBN123", "Author", "Publisher", "Desc", "URL");
        bookRepository.save(book);
    }

    @Test
    void addToCartAsCustomer() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", customer);

        mockMvc.perform(post("/cart/add/{id}", book.getId()).session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }

    @Test
    void addToCartAsAdmin() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", admin);

        mockMvc.perform(post("/cart/add/{id}", book.getId()).session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    void addToCartNotLoggedIn() throws Exception {
        mockMvc.perform(post("/cart/add/{id}", book.getId()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    void removeFromCart() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", customer);

        mockMvc.perform(post("/cart/remove/{id}", book.getId()).session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/cart"));
    }

    @Test
    void removeFromCartNotLoggedIn() throws Exception {
        mockMvc.perform(post("/cart/remove/{id}", book.getId()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    void viewCartAsCustomer() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", customer);

        mockMvc.perform(get("/cart").session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("cart"))
                .andExpect(model().attributeExists("books"))
                .andExpect(model().attributeExists("totalPrice"));
    }

    @Test
    void viewCartNotLoggedIn() throws Exception {
        mockMvc.perform(get("/cart"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    void viewCartAsAdmin() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", admin);

        mockMvc.perform(get("/cart").session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    void checkoutAsCustomer() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", customer);

        mockMvc.perform(post("/cart/checkout").session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/purchase-history"));
    }

    @Test
    void checkoutNotLoggedIn() throws Exception {
        mockMvc.perform(post("/cart/checkout"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    void checkoutAsAdmin() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", admin);

        mockMvc.perform(post("/cart/checkout").session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }
}
