package org.example;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.awt.print.Book;
import java.util.Optional;
@RestController
@RequestMapping("/api/inventory")

public class BookInventoryController {

    private final BookInventoryRepository bookInventoryRepository;
    private final BookRepository bookRepository;

    @Autowired
    public BookInventoryController(BookInventoryRepository bookInventoryRepositoryRepository,BookRepository bookRepository) {
        this.bookInventoryRepository = bookInventoryRepositoryRepository;
        this.bookRepository = bookRepository;
    }

    /**
     *
     * @return
     */
    @PostMapping // tag creates or updates data
    public BookInventory createBookInventory() {
        BookInventory book = new BookInventory();
        return bookInventoryRepository.save(book);
    }

    /**
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}") // tag reads data
    public ResponseEntity<BookInventory> getBookInventory(@PathVariable Long id) {
        return bookInventoryRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     *
     * @param id
     * @param bookId
     * @return
     */
    @DeleteMapping("/{id}/buddies/{buddyId}")
    public ResponseEntity<BookInventory> removeBook(@PathVariable Long id,
                                                   @PathVariable Long bookId) {
        Optional<BookInventory> bookOpt = bookInventoryRepository.findById(id);
        Optional<BookInfo> buddyOpt = bookRepository.findById(bookId); //get address and buddy by id.
        //Use optional if not in database

        if (bookOpt.isEmpty() || buddyOpt.isEmpty()) {
            return ResponseEntity.notFound().build(); // if either book is empty or buddy DNE error
        }

        BookInventory bookInventory = bookOpt.get();
        BookInfo book = buddyOpt.get();

        bookInventory.removeBook(book);
        bookInventoryRepository.save(bookInventory);
        bookRepository.delete(book);

        return ResponseEntity.ok(bookInventory);
    }

    /**
     * for adding buddies
     * @param id
     * @param bookInfo
     * @return
     */
    @PostMapping("/{id}/buddies")
    public ResponseEntity<BookInventory> addBook(@PathVariable Long id,
                                         @RequestBody BookInfo bookInfo) {
        Optional<BookInventory> bookOpt = bookInventoryRepository.findById(id);
        if (bookOpt.isEmpty()) return ResponseEntity.notFound().build();

        BookInventory book = bookOpt.get();
        book.addBook(bookInfo);
        bookRepository.save(bookInfo);
        bookInventoryRepository.save(book);

        return ResponseEntity.ok(book);
    }


}
