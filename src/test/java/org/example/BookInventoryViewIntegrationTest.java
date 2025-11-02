package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class BookInventoryViewIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BookInventoryRepository bookInventoryRepository;

    @Autowired
    private BookRepository bookRepository;

    private BookInventory inventory;
    private BookInfo book;

    @BeforeEach
    void setUp() {
        bookInventoryRepository.deleteAll();
        bookRepository.deleteAll();

        inventory = new BookInventory();
        bookInventoryRepository.save(inventory);

        book = new BookInfo("The Hunger Games", "Fantasy", 19.99,
                "9780439023481", "Suzanne Collins",
                "Scholastic", "description",
                "https://m.media-amazon.com/images/S/compressed.photo.goodreads.com/books/1586722975i/2767052.jpg");
        bookRepository.save(book);
    }

    @Test
    void whenViewBookInventory_thenReturnInventoryPage() throws Exception {
        mockMvc.perform(get("/bookInventories/{id}", inventory.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("bookInventory"))
                .andExpect(model().attributeExists("bookInventory"))
                .andExpect(model().attributeExists("book"));
    }

    @Test
    void whenViewNonExistingBookInventory_thenRedirectToList() throws Exception {
        mockMvc.perform(get("/bookInventories/{id}", 999L))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/bookInventories"));
    }

    @Test
    void whenListBookInventories_thenReturnInventoriesPage() throws Exception {
        mockMvc.perform(get("/bookInventories"))
                .andExpect(status().isOk())
                .andExpect(view().name("bookInventories"))
                .andExpect(model().attributeExists("bookInventories"));
    }

    @Test
    void whenAddBookToInventory_thenRedirectToInventory() throws Exception {
        mockMvc.perform(post("/bookInventories/{id}/addBook", inventory.getId())
                        .param("bookTitle", "The Hunger Games")
                        .param("bookGenres", "Fantasy")
                        .param("bookPrice", "19.99")
                        .param("bookISBN", "9780439023481")
                        .param("bookAuthor", "Suzanne Collins")
                        .param("bookPublisher", "Scholastic")
                        .param("bookDescription", "description")
                        .param("bookCoverURL", "https://m.media-amazon.com/images/S/compressed.photo.goodreads.com/books/1586722975i/2767052.jpg"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/bookInventories/" + inventory.getId()));
    }

    @Test
    void whenRemoveBookFromInventory_thenRedirectToInventory() throws Exception {
        inventory.addBook(book);
        bookInventoryRepository.save(inventory);

        
        mockMvc.perform(get("/bookInventories/{inventoryId}/removeBook/{bookId}",
                        inventory.getId(), book.getId()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/bookInventories/" + inventory.getId()));
    }
}