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
class BookControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BookInventoryRepository bookInventoryRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserRepository userRepository;

    private BookInventory inventory;
    private BookInfo book;
    private User user;

    @BeforeEach
    void setUp() {
        bookInventoryRepository.deleteAll();
        bookRepository.deleteAll();
        userRepository.deleteAll();

        user = new User("testuser", "password123", "test@example.com", "ADMIN");
        userRepository.save(user);

        inventory = new BookInventory();
        bookInventoryRepository.save(inventory);

        book = new BookInfo("The Hunger Games", "Fantasy", 19.99,
                "9780439023481", "Suzanne Collins",
                "Scholastic", "description",
                "https://m.media-amazon.com/images/S/compressed.photo.goodreads.com/books/1586722975i/2767052.jpg");
        bookRepository.save(book);
    }

    @Test
    void whenViewHomePage_thenReturnInventoryPage() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("inventory"))
                .andExpect(model().attributeExists("inventory"))
                .andExpect(model().attributeExists("books"))
                .andExpect(model().attributeExists("newBook"));
    }

    @Test
    void whenAddBook_thenRedirectToHome() throws Exception {
        MockHttpSession adminSession = new MockHttpSession();
        adminSession.setAttribute("user", user);
        adminSession.setAttribute("username", user.getUsername());
        adminSession.setAttribute("role", user.getRole());

        mockMvc.perform(post("/addBook")
                        .session(adminSession)
                        .param("bookTitle", "Catching Fire")
                        .param("bookGenres", "Fantasy")
                        .param("bookPrice", "19.99")
                        .param("bookISBN", "9780439023498")
                        .param("bookAuthor", "Suzanne Collins")
                        .param("bookPublisher", "Scholastic")
                        .param("bookDescription", "Second book in the series")
                        .param("bookCoverURL", "https://m.media-amazon.com/images/S/compressed.photo.goodreads.com/books/1586722941i/6148028.jpg"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }

    @Test
    void whenRemoveBook_thenRedirectToHome() throws Exception {
        MockHttpSession adminSession = new MockHttpSession();
        adminSession.setAttribute("user", user);
        adminSession.setAttribute("username", user.getUsername());
        adminSession.setAttribute("role", user.getRole());

        inventory.addBook(book);
        bookInventoryRepository.save(inventory);

        mockMvc.perform(post("/removeBook/{id}", book.getId()).session(adminSession))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }
}