package org.example;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

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
                .orElseGet(() -> inventoryRepository.save(new BookInventory()));

        // ✅ Load all books from DB and sort alphabetically
        List<BookInfo> allBooks = bookRepository.findAll().stream()
                .sorted(Comparator.comparing(BookInfo::getBookTitle, String.CASE_INSENSITIVE_ORDER))
                .toList();

        User user = (User) session.getAttribute("user");
        model.addAttribute("user", user);
        model.addAttribute("isLoggedIn", user != null);
        model.addAttribute("isAdmin", user != null && user.isAdmin());

        model.addAttribute("inventory", inventory);
        model.addAttribute("books", allBooks);
        model.addAttribute("newBook", new BookInfo());
        model.addAttribute("genres", getDistinctGenres());

        model.addAttribute("title", "");
        model.addAttribute("minPrice", null);
        model.addAttribute("maxPrice", null);
        model.addAttribute("genre", "");

        System.out.println("Loaded " + allBooks.size() + " books (sorted by title).");
        return "inventory";
    }

    @PostMapping("/addBook")
    public String addBook(@ModelAttribute("newBook") BookInfo book, HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null || !user.isAdmin()) {
            return "redirect:/login";
        }

        BookInventory inventory = inventoryRepository.findAll().stream()
                .findFirst().orElseGet(() -> inventoryRepository.save(new BookInventory()));

        // *experimental*: auto-generate cover URL based on ISBN
        if (book.getBookISBN() != null && !book.getBookISBN().isEmpty()) {
            // remove hyphens and spaces before generating cover URL
            String isbn = book.getBookISBN().trim().replaceAll("-", "").replaceAll("\\s+", "");
            book.setBookCoverURL("https://covers.openlibrary.org/b/isbn/" + isbn + "-L.jpg");
        }

        bookRepository.save(book);
        inventory.addBook(book);
        inventoryRepository.save(inventory);
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

    // *experimental*
    @GetMapping("/search")
    public String searchBooks(@RequestParam(required = false) String title,
                              @RequestParam(required = false) Double minPrice,
                              @RequestParam(required = false) Double maxPrice,
                              @RequestParam(required = false) String genre, HttpSession session,
                              Model model) {

        List<BookInfo> allBooks = bookRepository.findAll();

        boolean noFilters =
                (title == null || title.isBlank()) &&
                        (genre == null || genre.isBlank()) &&
                        (minPrice == null) &&
                        (maxPrice == null);

        // If no filters, just show everything
        List<BookInfo> filtered;
        if (noFilters) {
            filtered = allBooks;
        } else {
            filtered = allBooks.stream()
                    .filter(b -> title == null || title.isBlank() ||
                            (b.getBookTitle() != null &&
                                    b.getBookTitle().toLowerCase().contains(title.toLowerCase())))
                    .filter(b -> genre == null || genre.isBlank() ||
                            (b.getBookGenre() != null &&
                                    b.getBookGenre().toLowerCase().contains(genre.toLowerCase())))
                    .filter(b -> (minPrice == null || (b.getBookPrice() != null && b.getBookPrice() >= minPrice)) &&
                            (maxPrice == null || (b.getBookPrice() != null && b.getBookPrice() <= maxPrice)))
                    .toList();
        }

        BookInventory inventory = inventoryRepository.findAll().stream()
                .findFirst()
                .orElse(null);

        // Alphabetical order for search results
        User user = (User) session.getAttribute("user");
        boolean loggedIn = (user != null);
        boolean admin = loggedIn && user.isAdmin();

        model.addAttribute("user", user);
        model.addAttribute("isLoggedIn", loggedIn);
        model.addAttribute("isAdmin", admin);

        filtered = new ArrayList<>(filtered).stream().sorted(Comparator.comparing(BookInfo::getBookTitle, String.CASE_INSENSITIVE_ORDER)).toList();
        model.addAttribute("inventory", inventory);
        model.addAttribute("books", filtered);
        model.addAttribute("genres", getDistinctGenres());
        model.addAttribute("newBook", new BookInfo());

        model.addAttribute("title", title);
        model.addAttribute("minPrice", minPrice);
        model.addAttribute("maxPrice", maxPrice);
        model.addAttribute("genre", genre);


        // ✅ Debugging
        System.out.println("Search Request -> title=" + title + ", genre=" + genre +
                ", minPrice=" + minPrice + ", maxPrice=" + maxPrice);
        System.out.println("Results found: " + filtered.size());

        return "inventory";
    }




    // *experimental*
    private List<String> getDistinctGenres() {
        return bookRepository.findAll().stream()
                .map(BookInfo::getBookGenre)
                .filter(g -> g != null && !g.isBlank()) // ✅ filter out null or empty
                .flatMap(g -> Arrays.stream(g.split(",")))
                .map(String::trim)
                .filter(g -> !g.isEmpty())
                .distinct()
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .toList();
    }



}