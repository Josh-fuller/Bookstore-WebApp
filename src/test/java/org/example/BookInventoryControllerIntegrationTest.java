package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = BookstoreApplication.class)
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class BookInventoryControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BookInventoryRepository bookInventoryRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private BookInventory inventory;
    private BookInfo book;

    @BeforeEach
    void setUp() {
        bookInventoryRepository.deleteAll();
        bookRepository.deleteAll();

        inventory = new BookInventory();
        bookInventoryRepository.save(inventory);
        
        String description1 = "Winning means fame and fortune. Losing means certain death. The Hunger Games have begun. In the ruins of a place once known as North America lies the nation of Panem, a shining Capitol surrounded by twelve outlying districts. The Capitol is harsh and cruel and keeps the districts in line by forcing them all to send one boy and one girl between the ages of twelve and eighteen to participate in the annual Hunger Games, a fight to the death on live TV. Sixteen-year-old Katniss Everdeen regards it as a death sentence when she steps forward to take her sister's place in the Games. But Katniss has been close to dead before-and survival, for her, is second nature. Without really meaning to, she becomes a contender. But if she is to win, she will have to start making choices that weigh survival against humanity and life against love.";
        book = new BookInfo("The Hunger Games", "Fantasy", 19.99,
                "9780439023481", "Suzanne Collins",
                "Scholastic", description1,
                "https://m.media-amazon.com/images/S/compressed.photo.goodreads.com/books/1586722975i/2767052.jpg");
        bookRepository.save(book);
    }

    @Test
    void createInventory() throws Exception {
        
        mockMvc.perform(post("/api/inventories")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    void getExistingInventory() throws Exception {
        
        mockMvc.perform(get("/api/inventories/{id}", inventory.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(inventory.getId()));
    }

    @Test
    void getNonExistingInventory() throws Exception {
        
        mockMvc.perform(get("/api/inventories/{id}", 999L))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllInventories_thenReturnInventories() throws Exception {
        
        mockMvc.perform(get("/api/inventories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void addBookToInventory() throws Exception {

        BookInfo newBook = new BookInfo("Catching Fire", "Fantasy", 19.99,
                "9780439023498", "Suzanne Collins",
                "Scholastic", "",
                "https://m.media-amazon.com/images/S/compressed.photo.goodreads.com/books/1586722941i/6148028.jpg");

        
        mockMvc.perform(post("/api/inventories/{id}/books", inventory.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newBook)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.books.length()").value(1));
    }

    @Test
    void addBookToNonExistingInventory() throws Exception {

        BookInfo newBook = new BookInfo("Catching Fire", "Fantasy", 19.99,
                "9780439023498", "Suzanne Collins",
                "Scholastic", "",
                "https://m.media-amazon.com/images/S/compressed.photo.goodreads.com/books/1586722941i/6148028.jpg");

        
        mockMvc.perform(post("/api/inventories/{id}/books", 999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newBook)))
                .andExpect(status().isNotFound());
    }

    @Test
    void removeBookFromInventory() throws Exception {
        
        inventory.addBook(book);
        bookInventoryRepository.save(inventory);

        mockMvc.perform(delete("/api/inventories/{inventoryId}/books/{bookId}",
                        inventory.getId(), book.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.books.length()").value(0));
    }

    @Test
    void addBookToLibrary() throws Exception {
        BookInfo newBook = new BookInfo("Catching Fire", "Fantasy", 19.99,
                "9780439023498", "Suzanne Collins",
                "Scholastic", "",
                "https://m.media-amazon.com/images/S/compressed.photo.goodreads.com/books/1586722941i/6148028.jpg");

        
        mockMvc.perform(post("/api/inventories/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newBook)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookTitle").value("Catching Fire"));
    }

    @Test
    void deleteExistingBook() throws Exception {
        mockMvc.perform(delete("/api/inventories/books/{bookId}", book.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteNonExistingBook() throws Exception {
        mockMvc.perform(delete("/api/inventories/books/{bookId}", 999L))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteInventory() throws Exception {
        mockMvc.perform(delete("/api/inventories/{id}", inventory.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteNonExistingInventory() throws Exception {
        mockMvc.perform(delete("/api/inventories/{id}", 999L))
                .andExpect(status().isNotFound());
    }

    @Test
    void getBookById() throws Exception {
        mockMvc.perform(get("/api/inventories/books/{bookId}", book.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(book.getId()))
                .andExpect(jsonPath("$.bookTitle").value("The Hunger Games"));
    }

    @Test
    void getNonExistingBook() throws Exception {
        mockMvc.perform(get("/api/inventories/books/{bookId}", 999L))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllBooks() throws Exception {
        mockMvc.perform(get("/api/inventories/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
}
