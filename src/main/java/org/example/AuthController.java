package org.example;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;
    @Autowired
    private BookRepository bookRepository;

    @GetMapping("/login")
    public String showLoginPage(Model model, HttpSession session) {
        if (session.getAttribute("user") != null) {
            return "redirect:/";
        }
        return "login";
    }

    @GetMapping("/register")
    public String showRegisterPage(Model model, HttpSession session) {
        if (session.getAttribute("user") != null) {
            return "redirect:/";
        }
        return "register";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password, HttpSession session, Model model) {
        
        User user = userService.authenticateUser(username, password);

        if (user != null) {
            session.setAttribute("user", user);
            session.setAttribute("username", user.getUsername());
            session.setAttribute("role", user.getRole());
            return "redirect:/";
        } else {
            model.addAttribute("error", "Invalid username or password");
            return "login";
        }
    }

    @PostMapping("/register")
    public String register(@RequestParam String username,
                          @RequestParam String password,
                          @RequestParam String confirmPassword,
                          @RequestParam String email,
                          @RequestParam(defaultValue = "CUSTOMER") String role,
                          Model model) {
        
        //validate password match
        if (!password.equals(confirmPassword)) {
            model.addAttribute("error", "Passwords do not match");
            return "register";
        }
        //validate password lenght
        if (password.length() < 6) {
            model.addAttribute("error", "Password must be at least 6 characters long");
            return "register";
        }
        //check if the user already exist
        if (userService.usernameExists(username)) {
            model.addAttribute("error", "Username already exists");
            return "register";
        }
        //check if the email already exist
        if (userService.emailExists(email)) {
            model.addAttribute("error", "Email already exists");
            return "register";
        }

        //register the user
        User user = userService.registerUser(username, password, email, role);

        if (user != null) {
            model.addAttribute("success", "Registration successful! Please login");
            return "login";
        } else {
            model.addAttribute("error", "Registration failed. Please try again");
            return "register";
        }
    }


    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }

    @PostMapping("/cart/add/{id}")
    public String addToCart(@PathVariable Long id, HttpSession session) {

        // 1) Get the logged-in user from session
        User sessionUser = (User) session.getAttribute("user");
        if (sessionUser == null) {
            return "redirect:/login";
        }

        // 2) Reload a MANAGED copy from DB
        User user = userService.findById(sessionUser.getId());
        if (user == null) {
            // session is stale
            session.invalidate();
            return "redirect:/login";
        }

        // 3) Fetch the book (managed)
        BookInfo book = bookRepository.findById(id).orElse(null);
        if (book == null) {
            return "redirect:/";
        }

        // 4) Avoid duplicates so if already purchased or in cart, do nothing
        if (user.getPurchasedBooks() != null &&
                user.getPurchasedBooks().getBooks().contains(book)) {
            return "redirect:/";
        }

        if (user.getInCart() != null &&
                user.getInCart().getBooks().contains(book)) {
            return "redirect:/";
        }

        // 5) Add to cart
        user.getInCart().addBook(book);

        // 6) Save managed user
        userService.saveUser(user);

        // 7) Put the fresh managed user back into session
        session.setAttribute("user", user);

        return "redirect:/";
    }


    @PostMapping("/cart/remove/{bookId}")
    public String removeFromCart(@PathVariable Long bookId, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/login";

        BookInfo book = bookRepository.findById(bookId).orElse(null);
        if (book != null) {
            user.getInCart().removeBook(book);
            userService.saveUser(user);
        }

        // refresh session
        session.setAttribute("user", userService.findById(user.getId()));

        return "redirect:/";
    }

    @PostMapping("/cart/checkout")
    public String checkout(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/login";

        // 1. Make a temporary copy of cart books
        List<BookInfo> temp = new ArrayList<>(user.getInCart().getBooks());

        // 2. Add each book to purchasedBooks
        for (BookInfo book : temp) {
            user.getPurchasedBooks().addBook(book);
        }

        // 3. Remove each book from cart individually
        for (BookInfo book : temp) {
            user.getInCart().removeBook(book);
        }
        userService.saveUser(user);

        // 5. Refresh session user
        session.setAttribute("user", userService.findById(user.getId()));

        return "redirect:/";
    }
}
