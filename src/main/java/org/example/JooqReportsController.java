package org.example;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class JooqReportsController {

    private final JooqReportingService reportingService;

    public JooqReportsController(JooqReportingService reportingService) {
        this.reportingService = reportingService;
    }

    @GetMapping("/reports")
    public String reportsPage(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null || !user.isAdmin()) {
            return "redirect:/";  // block access
        }

        model.addAttribute("topBooks", reportingService.topPurchasedBooks());
        model.addAttribute("topUsers", reportingService.topUsers());
        model.addAttribute("topInCart", reportingService.topInCartBooks());
        model.addAttribute("topGenres", reportingService.topGenresBySales());
        model.addAttribute("user", user);
        model.addAttribute("isAdmin", true);
        model.addAttribute("isLoggedIn", true);

        return "reports";
    }

}
