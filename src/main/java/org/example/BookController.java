package org.example;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class BookController {

    @Autowired
    private BookInventoryRepository inventoryRepository;

    @Autowired
    private BookRepository bookRepository;

    @GetMapping("/")
    public String showInventory(Model model, HttpSession session) {
        BookInventory inventory = inventoryRepository.findAll().stream()
                .findFirst()
                .orElse(null);

        if (inventory == null) {
            inventory = new BookInventory();
            inventoryRepository.save(inventory);
        }

        User user = (User) session.getAttribute("user");
        model.addAttribute("user", user);
        model.addAttribute("isLoggedIn", user != null);
        model.addAttribute("isAdmin", user != null && user.isAdmin());

        model.addAttribute("inventory", inventory);
        model.addAttribute("books", inventory.getBooks());
        model.addAttribute("newBook", new BookInfo());
        return "inventory";
    }

    @PostMapping("/addBook")
    public String addBook(@ModelAttribute("newBook") BookInfo book, HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null || !user.isAdmin()) {
            return "redirect:/login";
        }

        BookInventory inventory = inventoryRepository.findAll().stream()
                .findFirst()
                .orElse(null);

        if (inventory == null) {
            inventory = new BookInventory();
            inventoryRepository.save(inventory);
        }

        if (inventory != null && book.getBookTitle() != null && !book.getBookTitle().isEmpty()) {
            bookRepository.save(book);
            inventory.addBook(book);
            inventoryRepository.save(inventory);
        }

        return "redirect:/";
    }

    @PostMapping("/removeBook/{id}")
    public String removeBook(@PathVariable Long id, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null || !user.isAdmin()) {
            return "redirect:/login";
        }

        BookInventory inventory = inventoryRepository.findAll().stream()
                .findFirst()
                .orElse(null);

        if (inventory != null) {
            BookInfo bookToRemove = inventory.getBooks().stream()
                    .filter(b -> id.equals(b.getId()))
                    .findFirst()
                    .orElse(null);

            if (bookToRemove != null) {
                inventory.removeBook(bookToRemove);
                inventoryRepository.save(inventory);
                bookRepository.deleteById(id);
            }
        }

        return "redirect:/";
    }
}
