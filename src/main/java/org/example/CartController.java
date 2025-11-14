package org.example;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping("/cart/add/{id}")
    public String addToCart(@PathVariable("id") Long bookId,
                            HttpSession session) {

        User user = (User) session.getAttribute("user");
        if (user == null || !user.isCustomer()) {
            // Only logged-in customers can add to cart
            return "redirect:/login";
        }

        cartService.addBookToCart(user, bookId);
        return "redirect:/";
    }

    @PostMapping("/cart/remove/{id}")
    public String removeFromCart(@PathVariable("id") Long bookId,
                                 HttpSession session) {

        User user = (User) session.getAttribute("user");
        if (user == null || !user.isCustomer()) {
            return "redirect:/login";
        }

        cartService.removeBookFromCart(user, bookId);
        return "redirect:/cart";
    }

    @GetMapping("/cart")
    public String viewCart(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null || !user.isCustomer()) {
            return "redirect:/login";
        }

        Cart cart = cartService.getCart(user);

        boolean loggedIn = true;
        boolean admin = user.isAdmin();

        model.addAttribute("user", user);
        model.addAttribute("isLoggedIn", loggedIn);
        model.addAttribute("isAdmin", admin);

        // Reuse the same "books" collection style as inventory
        model.addAttribute("books", cart.getBooks());
        model.addAttribute("cartId", cart.getId());

        // Cart page doesn’t need genres/search for now, but we coud add them later
        return "cart";
    }
}
