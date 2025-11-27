package org.example;

import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final BookRepository bookRepository;
    private final PurchaseHistoryService purchaseHistoryService;

    public CartService(CartRepository cartRepository, BookRepository bookRepository, PurchaseHistoryService purchaseHistoryService) {
        this.cartRepository = cartRepository;
        this.bookRepository = bookRepository;
        this.purchaseHistoryService = purchaseHistoryService;
    }

    public Cart getOrCreateCart(User user) {
        return cartRepository.findByUser(user)
                .orElseGet(() -> {
                    Cart cart = new Cart(user);
                    return cartRepository.save(cart);
                });
    }

    public Cart getCart(User user) {
        return getOrCreateCart(user);
    }

    public void addBookToCart(User user, Long bookId) {
        Cart cart = getOrCreateCart(user);
        BookInfo book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("Book not found: " + bookId));
        cart.addBook(book);
        cartRepository.save(cart);
    }

    public void removeBookFromCart(User user, Long bookId) {
        Cart cart = getOrCreateCart(user);
        Optional<BookInfo> bookOpt = cart.getBooks().stream()
                .filter(b -> b.getId().equals(bookId))
                .findFirst();

        bookOpt.ifPresent(book -> {
            cart.removeBook(book);
            cartRepository.save(cart);
        });
    }

    public void checkout(User user) {
        Cart cart = getOrCreateCart(user);

        if (cart.getBooks().isEmpty()) {
            return; // nothing to do
        }

        // Get or create purchase history for this user
        PurchaseHistory history = purchaseHistoryService.getOrCreateHistory(user);

        // Move all books from cart → history
        history.getBooks().addAll(cart.getBooks());
        cart.getBooks().clear();

        // Persist
        purchaseHistoryService.save(history);
        cartRepository.save(cart);
    }

    public double getCartTotal(User user) {
        Cart cart = getOrCreateCart(user);
        return cart.getBooks().stream()
                .map(BookInfo::getBookPrice)
                .filter(Objects::nonNull)
                .mapToDouble(Double::doubleValue)
                .sum();
    }



}
