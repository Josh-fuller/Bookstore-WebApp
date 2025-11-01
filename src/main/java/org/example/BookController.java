package org.example;

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

    @GetMapping("/")
    public String showInventory(Model model) {
        BookInventory inventory = inventoryRepository.findAll().stream()
                .findFirst()
                .orElse(new BookInventory());

        model.addAttribute("inventory", inventory);
        model.addAttribute("books", inventory.getBooks());
        model.addAttribute("newBook", new BookInfo()); // <- Add this
        return "inventory";
    }

    @PostMapping("/addBook")
    public String addBook(@ModelAttribute("newBook") BookInfo book) {
        BookInventory inventory = inventoryRepository.findAll().stream()
                .findFirst()
                .orElse(null);

        if (inventory != null && book.getBookName() != null && !book.getBookName().isEmpty()) {
            inventory.addBook(book);
            inventoryRepository.save(inventory);
        }

        return "redirect:/";
    }

    @PostMapping("/removeBook/{id}")
    public String removeBook(@PathVariable Long id) {
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
            }
        }

        return "redirect:/";
    }
}