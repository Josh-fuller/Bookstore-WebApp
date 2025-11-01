package org.example;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;

@Controller
@RequestMapping("/bookInventories")
/**
 * For rendering the Thymeleaf templates located in resources/templates.
 */
public class BookInventoryView {

    private final BookInventoryRepository bookInventoryRepository;
    private final BookRepository bookRepository;

    public BookInventoryView(BookInventoryRepository bookInventoryRepository, BookRepository bookRepository) {
        this.bookInventoryRepository = bookInventoryRepository;
        this.bookRepository = bookRepository;
    }

    /**
     * Displays a single inventory and its books.
     *
     * @param id inventory ID
     * @param model data passed to the template
     * @return bookInventory.html view
     */
    @GetMapping("/{id}")
    public String viewBookInventory(@PathVariable Long id, Model model) {
        BookInventory bookInventory = bookInventoryRepository.findById(id).orElse(null);

        if (bookInventory == null) {
            return "redirect:/bookInventories"; // prevents NullPointer errors
        }

        bookInventory.getBooks().sort(Comparator.comparing(BookInfo::getBookTitle, String.CASE_INSENSITIVE_ORDER));

        model.addAttribute("bookInventory", bookInventory);
        model.addAttribute("book", new BookInfo());
        return "bookInventory";
    }


    /**
     * Lists all book inventories.
     *
     * @param model data passed to the template
     * @return bookInventories.html view
     */
    @GetMapping
    public String listBookInventories(Model model) {
        model.addAttribute("bookInventories", bookInventoryRepository.findAll());
        return "bookInventories";
    }

    /**
     * Adds a book to the specified inventory.
     *
     * @param id inventory ID
     * @param newBook new book details from form
     * @return redirect to same inventory page
     */
    @PostMapping("/{id}/addBook")
    public String addBookToInventory(@PathVariable Long id, @ModelAttribute BookInfo newBook) {
        BookInventory bookInventory = bookInventoryRepository.findById(id).orElse(null);

        if (bookInventory != null) {
            bookRepository.save(newBook);
            bookInventory.addBook(newBook);
            bookInventoryRepository.save(bookInventory);
        }

        return "redirect:/bookInventories/" + id;
    }

    /**
     * Removes a book from an inventory.
     *
     * @param inventoryId inventory ID
     * @param bookId book ID
     * @return redirect to the inventory page
     */
    @GetMapping("/{inventoryId}/removeBook/{bookId}")
    public String removeBookFromInventory(@PathVariable Long inventoryId, @PathVariable Long bookId) {
        BookInventory inventory = bookInventoryRepository.findById(inventoryId).orElse(null);
        BookInfo book = bookRepository.findById(bookId).orElse(null);

        if (inventory != null && book != null) {
            inventory.removeBook(book);
            bookInventoryRepository.save(inventory);
            bookRepository.delete(book);
        }

        return "redirect:/bookInventories/" + inventoryId;
    }

    /**
     * Displays details for a single book within an inventory.
     *
     * @param inventoryId inventory ID
     * @param bookId book ID
     * @param model data passed to the template
     * @return bookInventory.html view
     */
    @GetMapping("/{inventoryId}/book/{bookId}")
    public String viewBookDetails(@PathVariable Long inventoryId,
                                  @PathVariable Long bookId,
                                  Model model) {
        BookInventory inventory = bookInventoryRepository.findById(inventoryId).orElse(null);
        BookInfo book = bookRepository.findById(bookId).orElse(null);

        model.addAttribute("bookInventory", inventory);
        model.addAttribute("book", book);
        return "bookInventory";
    }
}
