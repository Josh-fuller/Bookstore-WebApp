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
class PurchaseHistoryControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private PurchaseHistoryRepository purchaseHistoryRepository;

    private User customer;
    private User admin;
    private BookInfo book;

    @BeforeEach
    void setUp() {
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
    void viewPurchaseHistoryAsCustomer() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", customer);

        mockMvc.perform(get("/purchase-history").session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("purchase-history"))
                .andExpect(model().attributeExists("books"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attributeExists("isLoggedIn"))
                .andExpect(model().attributeExists("isAdmin"));
    }

    @Test
    void viewPurchaseHistoryNotLoggedIn() throws Exception {
        mockMvc.perform(get("/purchase-history"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    void viewPurchaseHistoryAsAdmin() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", admin);

        mockMvc.perform(get("/purchase-history").session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    void removeFromHistoryAsCustomer() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", customer);

        mockMvc.perform(post("/purchase-history/remove/{id}", book.getId()).session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/purchase-history"));
    }

    @Test
    void removeFromHistoryNotLoggedIn() throws Exception {
        mockMvc.perform(post("/purchase-history/remove/{id}", book.getId()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    void removeFromHistoryAsAdmin() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", admin);

        mockMvc.perform(post("/purchase-history/remove/{id}", book.getId()).session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }
}
