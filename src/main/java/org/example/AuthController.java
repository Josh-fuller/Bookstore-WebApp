package org.example;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private RecommendationService recommendationService;

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

    @GetMapping("/recommendations")
    public String showRecommendations(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/login";
        if (user.isAdmin()) return "redirect:/";

        List<BookInfo> recommendedBooks = recommendationService.getRecommendations(user, 10);

        model.addAttribute("user", user);
        model.addAttribute("isLoggedIn", true);
        model.addAttribute("isAdmin", false);
        model.addAttribute("recommendedBooks", recommendedBooks);

        return "recommendations";
    }
}
