package org.example;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;

@Controller
@RequestMapping("/bookInventories")
/**
 * for rendering the thymeleaf templates
 * located in resoucres
 */
public class BookInventoryView {
    private final BookInventoryRepository bookInventoryRepository;
    private final BookRepository bookRepository;

    public BookInventoryView(BookInventoryRepository bookInventoryRepositoryRepository,BookRepository bookRepository) {
        this.bookInventoryRepository = bookInventoryRepositoryRepository;
        this.bookRepository = bookRepository;
    }

    /**
     *
     * retur
     * @param id
     * @param model
     * @return
     */
    @GetMapping("/{id}")
    public String viewBookInventory(@PathVariable Long id, Model model) {
        BookInventory bookInventory = bookInventoryRepository.findById(id).orElse(null);
        model.addAttribute("bookInventory", bookInventory); // model is a container for data for sending between controller and view
        model.addAttribute("book", new BookInfo());
        return "bookInventory";
        // look addressbook.html in templates
    }

    /**
     * lists all addresses and searches for the html page
     * @param model
     * @return
     */
    @GetMapping
    public String listBookInventory(Model model) {
        model.addAttribute("bookInventory", bookInventoryRepository.findAll());
        return "bookInventories";
        // look addressbook(s).html in templates
    }

    /**
     * function to create/add books to bookInventory
     * @param id
     * @param newBook
     * @return
     */
    @PostMapping("/{id}/addBuddy")
    public String addBook(@PathVariable Long id,
                           @ModelAttribute BookInfo newBook) {
        BookInventory bookInventory = bookInventoryRepository.findById(id).orElse(null);
        if (bookInventory != null) {
            bookInventory.addBook(newBook);
            bookRepository.save(newBook);
            bookInventoryRepository.save(bookInventory);
        }
        // redirect back to the same address book view
        return "redirect:/bookInventories/" + id;
    }

    /**
     * method to render addressBook page
     * @param id
     * @param model
     * @return
     */
    @GetMapping("/addressbook/{id}")
    public String getBookInventory(@PathVariable Long id, Model model) {
        BookInventory bookInventory = bookInventoryRepository.findById(id).orElse(null);
        model.addAttribute("bookInventory", bookInventory);


        model.addAttribute("buddy", new BookInfo()); //empty obj needed

        return "bookInventory"; //template
    }

    /**
     * Displays details for a single book within an inventory.
     * Example: /bookInventories/1/book/2
     */
    @GetMapping("/{inventoryId}/book/{bookId}")
    public String viewBookDetails(@PathVariable Long inventoryId,
                                  @PathVariable Long bookId,
                                  Model model) {
        BookInventory inventory = bookInventoryRepository.findById(inventoryId).orElse(null);
        BookInfo book = bookRepository.findById(bookId).orElse(null);

        model.addAttribute("bookInventory", inventory);
        model.addAttribute("book", book);
        return "bookInventory"; // templates/bookInventory.html
    }


}
