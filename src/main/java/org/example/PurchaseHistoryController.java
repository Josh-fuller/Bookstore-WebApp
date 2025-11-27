package org.example;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class PurchaseHistoryController {

    private final PurchaseHistoryService purchaseHistoryService;

    public PurchaseHistoryController(PurchaseHistoryService purchaseHistoryService) {
        this.purchaseHistoryService = purchaseHistoryService;
    }

    @GetMapping("/purchase-history")
    public String viewPurchaseHistory(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null || !user.isCustomer()) {
            return "redirect:/login";
        }

        PurchaseHistory history = purchaseHistoryService.getOrCreateHistory(user);

        model.addAttribute("user", user);
        model.addAttribute("isLoggedIn", true);
        model.addAttribute("isAdmin", user.isAdmin());

        // reuse "books" like cart/inventory
        model.addAttribute("books", history.getBooks());

        return "purchase-history"; // matches purchase-history.html
    }

    @PostMapping("/purchase-history/remove/{id}")
    public String removeFromHistory(@PathVariable("id") Long bookId,
                                    HttpSession session) {

        User user = (User) session.getAttribute("user");
        if (user == null || !user.isCustomer()) {
            return "redirect:/login";
        }

        purchaseHistoryService.removeBookFromHistory(user, bookId);
        return "redirect:/purchase-history";
    }
}
