package org.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/inventories")
public class BookInventoryController {

    private final BookInventoryRepository bookInventoryRepository;
    private final BookRepository bookRepository;

    @Autowired
    public BookInventoryController(BookInventoryRepository bookInventoryRepository,
                                   BookRepository bookRepository) {
        this.bookInventoryRepository = bookInventoryRepository;
        this.bookRepository = bookRepository;

    }

    /**
     * Creates a new book inventory.
     *
     * @return created BookInventory object
     */
    @PostMapping
    public BookInventory createInventory() {
        BookInventory inventory = new BookInventory();
        return bookInventoryRepository.save(inventory);
    }

    /**
     * Retrieves a specific book inventory by ID.
     *
     * @param id inventory ID
     * @return BookInventory object or 404 if not found
     */
    @GetMapping("/{id}")
    public ResponseEntity<BookInventory> getBookInventory(@PathVariable Long id) {
        return bookInventoryRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());

    }

    /**
     * Lists all inventories.
     *
     * @return list of BookInventory objects
     */
    @GetMapping
    public List<BookInventory> getAllInventories() {
        return bookInventoryRepository.findAll();
    }

    /**
     * Deletes an inventory by ID.
     *
     * @param id inventory ID
     * @return 204 if deleted or 404 if not found
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInventory(@PathVariable Long id) {
        if (!bookInventoryRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        bookInventoryRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Adds a book to a specific inventory.
     *
     * @param id inventory ID
     * @param newBook book to add
     * @return updated inventory or 404 if not found
     */
    @PostMapping("/{id}/books")
    public ResponseEntity<BookInventory> addBookToInventory(@PathVariable Long id,
                                                            @RequestBody BookInfo newBook) {
        Optional<BookInventory> inventoryOpt = bookInventoryRepository.findById(id);
        if (inventoryOpt.isEmpty()) return ResponseEntity.notFound().build();

        BookInventory inventory = inventoryOpt.get();
        bookRepository.save(newBook);
        inventory.addBook(newBook);
        bookInventoryRepository.save(inventory);

        return ResponseEntity.ok(inventory);
    }

    /**
     * Removes a book from a specific inventory.
     *
     * @param inventoryId inventory ID
     * @param bookId book ID
     * @return updated inventory or 404 if not found
     */
    @DeleteMapping("/{inventoryId}/books/{bookId}")
    public ResponseEntity<BookInventory> removeBookFromInventory(@PathVariable Long inventoryId,
                                                                 @PathVariable Long bookId) {
        Optional<BookInventory> inventoryOpt = bookInventoryRepository.findById(inventoryId);
        Optional<BookInfo> bookOpt = bookRepository.findById(bookId);

        if (inventoryOpt.isEmpty() || bookOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        BookInventory inventory = inventoryOpt.get();
        BookInfo book = bookOpt.get();

        inventory.removeBook(book);
        bookInventoryRepository.save(inventory);

        // Optional: also remove the book from the main book table
        bookRepository.delete(book);

        return ResponseEntity.ok(inventory);
    }

    /**
     * Adds a new book to the main BookInfo table (not tied to any inventory).
     *
     * @param book book to save
     * @return created BookInfo object
     */
    @PostMapping("/books")
    public BookInfo addBookToLibrary(@RequestBody BookInfo book) {
        return bookRepository.save(book);
    }

    /**
     * Retrieves all books in the system.
     *
     * @return list of BookInfo objects
     */
    @GetMapping("/books")
    public List<BookInfo> getAllBooks() {
        return bookRepository.findAll();
    }

    /**
     * Retrieves a single book by its ID.
     *
     * @param bookId book ID
     * @return BookInfo object or 404 if not found
     */
    @GetMapping("/books/{bookId}")
    public ResponseEntity<BookInfo> getBookById(@PathVariable Long bookId) {
        return bookRepository.findById(bookId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Deletes a book from the BookInfo table.
     *
     * @param bookId book ID
     * @return 204 if deleted or 404 if not found
     */
    @DeleteMapping("/books/{bookId}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long bookId) {
        if (!bookRepository.existsById(bookId)) {
            return ResponseEntity.notFound().build();
        }
        bookRepository.deleteById(bookId);
        return ResponseEntity.noContent().build();
    }
}
